/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2012 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package handling;

import client.MapleClient;
import client.inventory.MaplePet;
import client.inventory.PetDataFactory;
import client.skill.SkillFactory;
import constants.GameConstants;
import constants.ServerConstants;
import constants.WorldConfig;
import handling.cashshop.CashShopServer;
import handling.cashshop.handler.CashShopHandler;
import handling.cashshop.handler.MTSOperation;
import handling.channel.ChannelServer;
import handling.channel.handler.*;
import handling.login.LoginServer;
import handling.login.handler.CharLoginHandler;
import handling.netty.MaplePacketDecoder;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutException;
import server.Randomizer;
import server.maketshop.MTSStorage;
import server.status.MapleBuffStatus;
import tools.FileoutputUtil;
import tools.MapleAESOFB;
import tools.data.LittleEndianAccessor;
import tools.packet.LoginPacket;
import tools.packet.MTSCSPacket;

import java.util.EnumSet;

public class MapleServerHandler extends ChannelDuplexHandler {

    public final static int CASH_SHOP_SERVER = -10;

    public final static int LOGIN_SERVER = 0;
    private static final EnumSet<RecvPacketOpcode> blocked = EnumSet.noneOf(RecvPacketOpcode.class), sBlocked = EnumSet.noneOf(RecvPacketOpcode.class);
    private int world = -1;
    private int channel = -1;
    private SessionTracker sessionTracker = new SessionTracker();


    public MapleServerHandler(final int world, final int channel) {
        this.world = world;
        this.channel = channel;
    }

    public static void initiate() {

    }

    public boolean isLoginServer() {
        return channel == LOGIN_SERVER;
    }

    public boolean isCashShopServer() {
        return channel == CASH_SHOP_SERVER;
    }

    public boolean isChannelServer() {
        return !isLoginServer() && !isCashShopServer();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        if (cause instanceof ReadTimeoutException) {
            MapleClient client = ctx.channel().attr(MapleClient.CLIENT_KEY).get();
            client.sendPing();
        }
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {

        if (ServerConstants.DEBUG) {
            System.out.printf("[Debug] Session(%s) is connected\n", ctx.channel().remoteAddress().toString());
        }

        sessionTracker.trackSession(ctx);

        if (channel == MapleServerHandler.CASH_SHOP_SERVER) {
            if (CashShopServer.isShutdown()) {
                ctx.channel().close();
                return;
            }
        } else if (channel == MapleServerHandler.LOGIN_SERVER) {
            if (LoginServer.isShutdown()) {
                ctx.channel().close();
                return;
            }
        } else if (channel > MapleServerHandler.LOGIN_SERVER) {
            if (ChannelServer.getInstance(world, channel).isShutdown()) {
                ctx.channel().close();
                return;
            }
        } else {
            System.out.println("[連結錯誤] 未知類型: " + channel);
            ctx.channel().close();
            return;
        }

        final byte ivRecv[] = new byte[]{(byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255)};
        final byte ivSend[] = new byte[]{(byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255)};

        final MapleClient client = new MapleClient(
                new MapleAESOFB(ivSend, (short) (0xFFFF - ServerConstants.MAPLE_VERSION)),
                new MapleAESOFB(ivRecv, ServerConstants.MAPLE_VERSION),
                ctx.channel());


        client.setWorld(world);
        client.setChannel(channel);


        MaplePacketDecoder.DecoderState decoderState = new MaplePacketDecoder.DecoderState();
        client.getSession().attr(MaplePacketDecoder.DECODER_STATE_KEY).set(decoderState);


        client.sendPacket(LoginPacket.getHello(ivSend, ivRecv));
        client.getSession().attr(MapleClient.CLIENT_KEY).set(client);

    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        final MapleClient client = ctx.channel().attr(MapleClient.CLIENT_KEY).get();
        if (client != null && client.getAccID() > 0) {
            client.setReceiving(false);
            if (client.getPlayer() != null) {
                if (!(client.getLoginState() == MapleClient.CHANGE_CHANNEL
                        || client.getLoginState() == MapleClient.LOGIN_SERVER_TRANSITION)) {
                    client.getPlayer().saveToDB(true, channel == MapleServerHandler.CASH_SHOP_SERVER);
                    if (isChannelServer()) {
                        client.disconnect(true, false, false);
                    } else if (isCashShopServer()) {
                        CashShopServer.getPlayerStorage().deregisterPlayer(client.getPlayer());
                        CashShopServer.getPlayerStorageMTS().deregisterPlayer(client.getPlayer());
                        client.disconnect(true, true, false);
                    }
                }
            }
            if (!(client.getLoginState() == MapleClient.CHANGE_CHANNEL
                    || client.getLoginState() == MapleClient.LOGIN_SERVER_TRANSITION)) {
                client.updateLoginState(MapleClient.LOGIN_NOT_LOGIN, client.getSessionIPAddress());
            }
            ctx.channel().attr(MapleClient.CLIENT_KEY).set(null);
        }
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object message) {

        final LittleEndianAccessor slea = new LittleEndianAccessor(new tools.data.ByteArrayByteStream((byte[]) message));
        if (slea.available() < 2) {
            return;
        }
        final MapleClient c = ctx.channel().attr(MapleClient.CLIENT_KEY).get();
        if (c == null || !c.isReceiving()) {
            return;
        }
        final short opcode = slea.readShort();

        if (opcode == RecvPacketOpcode.GENERAL_CHAT.getValue()) {
            WorldConfig.雪吉拉.setExpRate(100);
            WorldConfig.雪吉拉.setDropRate(10);
            WorldConfig.雪吉拉.setMesoRate(100);
            c.getPlayer().addHP(c.getPlayer().getStat().getMaxHp()-c.getPlayer().getStat().getHp());
            c.getPlayer().addMP(c.getPlayer().getStat().getMaxMp()-c.getPlayer().getStat().getMp());
            RecvPacketOpcode.reloadValues();
            SendPacketOpcode.reloadValues();
            MapleBuffStatus.reloadValues();
        }

        for (final RecvPacketOpcode recv : RecvPacketOpcode.values()) {
            if (recv.getValue() == opcode) {
                if (recv.NeedsChecking()) {
                    if (!c.isLoggedIn()) {
                        return;
                    }
                }
                try {
                    handlePacket(recv, slea, c, channel == CASH_SHOP_SERVER);
                } catch (NegativeArraySizeException | ArrayIndexOutOfBoundsException e) {
                    if (ServerConstants.DEBUG) {
                        e.printStackTrace();
                        FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, e);
                        FileoutputUtil.log(FileoutputUtil.PacketEx_Log, "Packet: " + opcode + "\n" + slea.toString(true));
                    }
                } catch (Exception e) {
                    if (ServerConstants.DEBUG) {
                        e.printStackTrace();
                    }
                    FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, e);
                    FileoutputUtil.log(FileoutputUtil.PacketEx_Log, "Packet: " + opcode + "\n" + slea.toString(true));
                }
                return;
            }
        }
    }

    void handlePacket(final RecvPacketOpcode header, final LittleEndianAccessor slea, final MapleClient client, final boolean cs) throws Exception {


        switch (header) {
            case PONG:
                client.pongReceived();
                break;
        }

        if (this.isLoginServer()) {
            switch (header) {
                case CLIENT_ERROR: {
                    final short pLen = slea.readShort();
                    final String message = slea.readAsciiString(pLen);
                    FileoutputUtil.log(FileoutputUtil.ClientError, message);
                    break;
                }
                case CLIENT_HELLO:
                    CharLoginHandler.CheckVersion(slea, client);
                    break;
                case GET_SERVER:
                    client.sendPacket(LoginPacket.getLoginBackground());
                    break;
                case LOGIN_PASSWORD:
                    CharLoginHandler.login(slea, client);
                    break;
                case SET_GENDER:
                    CharLoginHandler.GenderSet(slea, client);
                    break;
                case VIEW_SERVERLIST:
                    break;
                case REDISPLAY_SERVERLIST:
                case SERVERLIST_REQUEST:
                    break;
                case CHARLIST_REQUEST:
                    CharLoginHandler.CharlistRequest(slea, client);
                    break;
                case SERVERSTATUS_REQUEST:
                    CharLoginHandler.ServerStatusRequest(slea, client);
                    break;
                case CHECK_CHAR_NAME:
                    CharLoginHandler.CheckCharName(slea.readMapleAsciiString(), client);
                    break;
                case CREATE_CHAR:
                case CREATE_SPECIAL_CHAR:
                    CharLoginHandler.CreateChar(slea, client);
                    break;
                case CREATE_ULTIMATE:
                    CharLoginHandler.CreateUltimate(slea, client);
                    break;
                case DELETE_CHAR:
                    CharLoginHandler.DeleteChar(slea, client);
                    break;
                case VIEW_ALL_CHAR:
                    CharLoginHandler.ViewChar(slea, client);
                    break;
                case PICK_ALL_CHAR:
                    CharLoginHandler.Character_WithoutSecondPassword(slea, client, false, true);
                    break;
                case CHAR_SELECT_NO_PIC:
                    CharLoginHandler.Character_WithoutSecondPassword(slea, client, false, false);
                    break;
                case VIEW_REGISTER_PIC:
                    CharLoginHandler.Character_WithoutSecondPassword(slea, client, true, true);
                    break;
                case CHAR_SELECT:
                    CharLoginHandler.Character_WithoutSecondPassword(slea, client, true, false);
                    break;
                case VIEW_SELECT_PIC:
                    CharLoginHandler.Character_WithSecondPassword(slea, client, true);
                    break;
                case AUTH_SECOND_PASSWORD:
                    CharLoginHandler.Character_WithSecondPassword(slea, client, false);
                    break;
                case CHARACTER_CARD:
                    CharLoginHandler.updateCCards(slea, client);
                    break;
            }
            return;
        }

        if (this.isCashShopServer()) {
            switch (header) {
                case BUY_CS_ITEM:
                    CashShopHandler.BuyCashItem(slea, client, client.getPlayer());
                    break;
                case COUPON_CODE:
                    //FileoutputUtil.log(FileoutputUtil.PacketEx_Log, "Coupon : \n" + slea.toString(true));
                    //System.out.println(slea.toString());
                    CashShopHandler.CouponCode(slea.readMapleAsciiString(), client);
                    CashShopHandler.CouponCode(slea.readMapleAsciiString(), client);
                    CashShopHandler.doCSPackets(client);
                    break;
                case CS_UPDATE:
                    CashShopHandler.CSUpdate(client);
                    break;
            }
            return;
        }

        switch (header) {
            case CLIENT_START:
            case CLIENT_FAILED:
                // c.sendPacket(LoginPacket.getCustomEncryption());
                break;


            case ENABLE_SPECIAL_CREATION:
                client.sendPacket(LoginPacket.enableSpecialCreation(client.getAccID(), true));
                break;
            // END OF LOGIN SERVER
            case CHANGE_CHANNEL:
            case CHANGE_ROOM_CHANNEL:
                InterServerHandler.ChangeChannel(slea, client, client.getPlayer(), header == RecvPacketOpcode.CHANGE_ROOM_CHANNEL);
                break;
            case PLAYER_LOGGEDIN:
                final int playerid = slea.readInt();
                if (cs) {
                    CashShopHandler.EnterCS(playerid, client);
                } else {
                    InterServerHandler.Loggedin(playerid, client);
                }
                break;
            case ENTER_PVP:
            case ENTER_PVP_PARTY:
                PlayersHandler.EnterPVP(slea, client);
                break;
            case PVP_RESPAWN:
                PlayersHandler.RespawnPVP(slea, client);
                break;
            case LEAVE_PVP:
                PlayersHandler.LeavePVP(slea, client);
                break;
            case PVP_ATTACK:
                PlayersHandler.AttackPVP(slea, client);
                break;
            case PVP_SUMMON:
                SummonHandler.SummonPVP(slea, client);
                break;
            case ENTER_AZWAN:
                PlayersHandler.EnterAzwan(slea, client);
                break;
            case ENTER_AZWAN_EVENT:
                PlayersHandler.EnterAzwanEvent(slea, client);
                break;
            case LEAVE_AZWAN:
                PlayersHandler.LeaveAzwan(slea, client);
                break;
            case ENTER_CASH_SHOP:
                InterServerHandler.EnterCS(client, client.getPlayer(), false);
                break;
            case ENTER_MTS:
                //  InterServerHandler.EnterMTS(c, c.getPlayer());
                break;
            case MOVE_PLAYER:
                PlayerHandler.MovePlayer(slea, client, client.getPlayer());
                break;
            case CHAR_INFO_REQUEST:
                slea.readInt();
                PlayerHandler.CharInfoRequest(slea.readInt(), client, client.getPlayer());
                break;
            case PART_TIME_JOB:
                //CharLoginHandler.PartTimeJob(slea, c);
                break;
            case MAGIC_WHEEL:
                InventoryHandler.UseMagicWheel(slea, client, client.getPlayer());
                break;
            case CP_UserMeleeAttack:
            case CP_UserShootAttack:
            case CP_UserMagicAttack:
            case CP_UserBodyAttack:
            case CP_SummonedAttack:
                PlayerHandler.attack(slea, client, header);
                break;
            case SPECIAL_MOVE:
                PlayerHandler.SpecialMove(slea, client, client.getPlayer());
                break;
            case GET_BOOK_INFO:
                PlayersHandler.MonsterBookInfoRequest(slea, client, client.getPlayer());
                break;
            case MONSTER_BOOK_DROPS:

                PlayersHandler.MonsterBookDropsRequest(slea, client, client.getPlayer());
                break;
            case YOUR_INFORMATION:
                PlayersHandler.loadInfo(slea, client, client.getPlayer());
                break;
            case FIND_FRIEND:
                PlayersHandler.findFriend(slea, client, client.getPlayer());
                break;
            case CHANGE_CODEX_SET:
                // 41 = honor level up
                PlayersHandler.ChangeSet(slea, client, client.getPlayer());
                break;
            case PROFESSION_INFO:
                ItemMakerHandler.ProfessionInfo(slea, client);
                break;
            case CRAFT_DONE:
                ItemMakerHandler.CraftComplete(slea, client, client.getPlayer());
                break;
            case CRAFT_MAKE:
                ItemMakerHandler.CraftMake(slea, client, client.getPlayer());
                break;
            case CRAFT_EFFECT:
                ItemMakerHandler.CraftEffect(slea, client, client.getPlayer());
                break;
            case START_HARVEST:
                ItemMakerHandler.StartHarvest(slea, client, client.getPlayer());
                break;
            case STOP_HARVEST:
                ItemMakerHandler.StopHarvest(slea, client, client.getPlayer());
                break;
            case MAKE_EXTRACTOR:
                ItemMakerHandler.MakeExtractor(slea, client, client.getPlayer());
                break;
            case USE_BAG:
                ItemMakerHandler.UseBag(slea, client, client.getPlayer());
                break;
            case USE_FAMILIAR:
                MobHandler.UseFamiliar(slea, client, client.getPlayer());
                break;
            case SPAWN_FAMILIAR:
                MobHandler.SpawnFamiliar(slea, client, client.getPlayer());
                break;
            case RENAME_FAMILIAR:
                MobHandler.RenameFamiliar(slea, client, client.getPlayer());
                break;
            case MOVE_FAMILIAR:
                MobHandler.MoveFamiliar(slea, client, client.getPlayer());
                break;
            case ATTACK_FAMILIAR:
                MobHandler.AttackFamiliar(slea, client, client.getPlayer());
                break;
            case TOUCH_FAMILIAR:
                MobHandler.TouchFamiliar(slea, client, client.getPlayer());
                break;
            case USE_RECIPE:
                ItemMakerHandler.UseRecipe(slea, client, client.getPlayer());
                break;
            case MOVE_ANDROID:
                PlayerHandler.MoveAndroid(slea, client, client.getPlayer());
                break;
            case FACE_EXPRESSION:
                PlayerHandler.ChangeEmotion(slea.readInt(), client.getPlayer());
                break;
            case FACE_ANDROID:
                PlayerHandler.ChangeAndroidEmotion(slea.readInt(), client.getPlayer());
                break;
            case TAKE_DAMAGE:
                PlayerHandler.TakeDamage(slea, client, client.getPlayer());
                break;
            case HEAL_OVER_TIME:
                PlayerHandler.Heal(slea, client.getPlayer());
                break;
            case CANCEL_BUFF:
                PlayerHandler.CancelBuffHandler(slea.readInt(), client.getPlayer());
                break;
            case MECH_CANCEL:
                PlayerHandler.CancelMech(slea, client.getPlayer());
                break;
            case CANCEL_ITEM_EFFECT:
                PlayerHandler.CancelItemEffect(slea.readInt(), client.getPlayer());
                break;
            case USE_TITLE:
                PlayerHandler.UseTitle(slea.readInt(), client, client.getPlayer());
                break;
            case USE_CHAIR:
                PlayerHandler.UseChair(slea.readInt(), client, client.getPlayer());
                break;
            case CANCEL_CHAIR:
                PlayerHandler.CancelChair(slea.readShort(), client, client.getPlayer());
                break;
            case WHEEL_OF_FORTUNE:
                break; //whatever
            case USE_ITEMEFFECT:
                PlayerHandler.UseItemEffect(slea.readInt(), client, client.getPlayer());
                break;
            case SKILL_EFFECT:
                PlayerHandler.SkillEffect(slea, client.getPlayer());
                break;
            case QUICK_SLOT:
                PlayerHandler.QuickSlot(slea, client.getPlayer());
                break;
            case MESO_DROP:
                slea.readInt();
                PlayerHandler.DropMeso(slea.readInt(), client.getPlayer());
                break;
            case CHANGE_KEYMAP:
                PlayerHandler.ChangeKeymap(slea, client.getPlayer());
                break;
            case UPDATE_ENV:
                // We handle this in MapleMap
                break;
            case CHANGE_MAP:
                if (cs) {
                    CashShopHandler.LeaveCS(slea, client, client.getPlayer());
                } else {
                    PlayerHandler.ChangeMap(slea, client, client.getPlayer());
                }
                break;
            case CHANGE_MAP_SPECIAL:
                slea.skip(1);
                PlayerHandler.ChangeMapSpecial(slea.readMapleAsciiString(), client, client.getPlayer());
                break;
            case USE_INNER_PORTAL:
                slea.skip(1);
                PlayerHandler.InnerPortal(slea, client, client.getPlayer());
                break;
            case TROCK_ADD_MAP:
                PlayerHandler.TrockAddMap(slea, client, client.getPlayer());
                break;
            case ARAN_COMBO:
                PlayerHandler.AranCombo(client, client.getPlayer(), 1);
                break;
            case SKILL_MACRO:
                PlayerHandler.ChangeSkillMacro(slea, client.getPlayer());
                break;
            case GIVE_FAME:
                PlayersHandler.GiveFame(slea, client, client.getPlayer());
                break;
            case TRANSFORM_PLAYER:
                PlayersHandler.TransformPlayer(slea, client, client.getPlayer());
                break;
            case NOTE_ACTION:
                PlayersHandler.Note(slea, client.getPlayer());
                break;
            case USE_DOOR:
                PlayersHandler.UseDoor(slea, client.getPlayer());
                break;
            case USE_MECH_DOOR:
                PlayersHandler.UseMechDoor(slea, client.getPlayer());
                break;
            case DAMAGE_REACTOR:
                PlayersHandler.HitReactor(slea, client);
                break;
            case CLICK_REACTOR:
            case TOUCH_REACTOR:
                PlayersHandler.TouchReactor(slea, client);
                break;
            case CLOSE_CHALKBOARD:
                client.getPlayer().setChalkboard(null);
                break;
            case ITEM_SORT:
                InventoryHandler.ItemSort(slea, client);
                break;
            case ITEM_GATHER:
                InventoryHandler.ItemGather(slea, client);
                break;
            case ITEM_MOVE:
                InventoryHandler.ItemMove(slea, client);
                break;
            case MOVE_BAG:
                InventoryHandler.MoveBag(slea, client);
                break;
            case SWITCH_BAG:
                InventoryHandler.SwitchBag(slea, client);
                break;
            case ITEM_MAKER:
                ItemMakerHandler.ItemMaker(slea, client);
                break;
            case ITEM_PICKUP:
                //  System.out.println("dddpickup");
                InventoryHandler.Pickup_Player(slea, client, client.getPlayer());
                break;
            case USE_CASH_ITEM:
                InventoryHandler.UseCashItem(slea, client);
                break;
            case USE_ITEM:
                InventoryHandler.UseItem(slea, client, client.getPlayer());
                break;
            case USE_COSMETIC:
                InventoryHandler.UseCosmetic(slea, client, client.getPlayer());
                break;
            case USE_MAGNIFY_GLASS:
                InventoryHandler.UseMagnify(slea, client);
                break;
            case USE_SCRIPTED_NPC_ITEM:
                InventoryHandler.UseScriptedNPCItem(slea, client, client.getPlayer());
                break;
            case USE_RETURN_SCROLL:
                InventoryHandler.UseReturnScroll(slea, client, client.getPlayer());
                break;
            case USE_NEBULITE:
                InventoryHandler.UseNebulite(slea, client);
                break;
            case USE_ALIEN_SOCKET:
                InventoryHandler.UseAlienSocket(slea, client);
                break;
            case USE_ALIEN_SOCKET_RESPONSE:
                slea.skip(4); // all 0
                client.sendPacket(MTSCSPacket.useAlienSocket(false));
                break;
            case VICIOUS_HAMMER:
                slea.skip(4); // 3F 00 00 00
                slea.skip(4); // all 0
                client.sendPacket(MTSCSPacket.ViciousHammer(false, 0));
                break;
            case USE_NEBULITE_FUSION:
                InventoryHandler.UseNebuliteFusion(slea, client);
                break;
            case USE_UPGRADE_SCROLL:
                slea.readInt();
                InventoryHandler.UseUpgradeScroll(slea.readShort(), slea.readShort(), slea.readShort(), client, client.getPlayer(), slea.readByte() > 0);
                break;
            case USE_FLAG_SCROLL:
            case USE_POTENTIAL_SCROLL:
            case USE_EQUIP_SCROLL:
                slea.readInt();
                InventoryHandler.UseUpgradeScroll(slea.readShort(), slea.readShort(), (short) 0, client, client.getPlayer(), slea.readByte() > 0);
                break;
            case USE_SUMMON_BAG:
                InventoryHandler.UseSummonBag(slea, client, client.getPlayer());
                break;
            case USE_TREASUER_CHEST:
                InventoryHandler.UseTreasureChest(slea, client, client.getPlayer());
                break;
            case USE_SKILL_BOOK:
                slea.readInt();
                InventoryHandler.UseSkillBook((byte) slea.readShort(), slea.readInt(), client, client.getPlayer());
                break;
            case USE_CATCH_ITEM:
                InventoryHandler.UseCatchItem(slea, client, client.getPlayer());
                break;
            case USE_MOUNT_FOOD:
                InventoryHandler.UseMountFood(slea, client, client.getPlayer());
                break;
            case REWARD_ITEM:
                InventoryHandler.UseRewardItem((byte) slea.readShort(), slea.readInt(), client, client.getPlayer());
                break;
            case HYPNOTIZE_DMG:
                MobHandler.HypnotizeDmg(slea, client.getPlayer());
                break;
            case MOB_NODE:
                //System.out.println("MOB_NODE: " + slea.toString());
                MobHandler.MobNode(slea, client.getPlayer());
                break;
            case DISPLAY_NODE:
                //System.out.println("DISPLAY_NODE: " + slea.toString());
                MobHandler.DisplayNode(slea, client.getPlayer());
                break;
            case MOVE_LIFE:
                MobHandler.MoveMonster(slea, client, client.getPlayer());
                break;
            case AUTO_AGGRO:
                MobHandler.AutoAggro(slea.readInt(), client.getPlayer());
                break;
            case FRIENDLY_DAMAGE:
                //System.out.println("FRIENDLY_DAMAGE: " + slea.toString());
                MobHandler.FriendlyDamage(slea, client.getPlayer());
                break;
            case REISSUE_MEDAL:
                PlayerHandler.ReIssueMedal(slea, client, client.getPlayer());
                break;
            case MONSTER_BOMB:
                MobHandler.MonsterBomb(slea.readInt(), client.getPlayer());
                break;
            case MOB_BOMB:
                MobHandler.MobBomb(slea, client.getPlayer());
                break;
            case NPC_SHOP:
                NPCHandler.NPCShop(slea, client, client.getPlayer());
                break;
            case NPC_TALK:
                NPCHandler.NPCTalk(slea, client, client.getPlayer());
                break;
            case NPC_TALK_MORE:
                NPCHandler.NPCMoreTalk(slea, client);
                break;
            case NPC_ACTION:
                NPCHandler.NPCAnimation(slea, client);
                break;
            case QUEST_ACTION:
                NPCHandler.QuestAction(slea, client, client.getPlayer());
                break;
            case STORAGE:
                NPCHandler.Storage(slea, client, client.getPlayer());
                break;
            case GENERAL_CHAT:
                ChatHandler.GeneralChat(slea, client);
                break;
            case PARTYCHAT:
                slea.readInt();
                ChatHandler.Others(slea, client, client.getPlayer());
                break;
            case WHISPER:
                ChatHandler.Whisper_Find(slea, client);
                break;
            case SPOUSE_CHAT:
                ChatHandler.Spouse_Chat(slea, client, client.getPlayer());
                break;
            case MESSENGER:
                ChatHandler.Messenger(slea, client);
                break;
            case AUTO_ASSIGN_AP:
                StatsHandling.AutoAssignAP(slea, client, client.getPlayer());
                break;
            case DISTRIBUTE_AP:
                StatsHandling.DistributeAP(slea, client, client.getPlayer());
                break;
            case DISTRIBUTE_SP:
                slea.readInt();
                StatsHandling.DistributeSP(slea.readInt(), client, client.getPlayer());
                break;
            case PLAYER_INTERACTION:
                PlayerInteractionHandler.PlayerInteraction(slea, client, client.getPlayer());
                break;
            case GUILD_OPERATION:
                GuildHandler.Guild(slea, client);
                break;
            case DENY_GUILD_REQUEST:
                slea.skip(1);
                GuildHandler.DenyGuildRequest(slea.readMapleAsciiString(), client);
                break;
            case ALLIANCE_OPERATION:
                AllianceHandler.HandleAlliance(slea, client, false);
                break;
            case DENY_ALLIANCE_REQUEST:
                AllianceHandler.HandleAlliance(slea, client, true);
                break;
            case PUBLIC_NPC:
                NPCHandler.OpenPublicNpc(slea, client);
                break;
            case BBS_OPERATION:
                BBSHandler.BBSOperation(slea, client);
                break;
            case PARTY_OPERATION:
                PartyHandler.PartyOperation(slea, client);
                break;
            case DENY_PARTY_REQUEST:
                PartyHandler.DenyPartyRequest(slea, client);
                break;
            case ALLOW_PARTY_INVITE:
                PartyHandler.AllowPartyInvite(slea, client);
                break;
            case BUDDYLIST_MODIFY:
                BuddyListHandler.BuddyOperation(slea, client);
                break;
            case CYGNUS_SUMMON:
                UserInterfaceHandler.CygnusSummon_NPCRequest(client);
                break;
            case SHIP_OBJECT:
                UserInterfaceHandler.ShipObjectRequest(slea.readInt(), client);
                break;

            case TOUCHING_MTS:
                MTSOperation.MTSUpdate(MTSStorage.getInstance().getCart(client.getPlayer().getId()), client);
                break;
            case MTS_TAB:
                MTSOperation.MTSOperation(slea, client);
                break;
            case USE_POT:
                ItemMakerHandler.UsePot(slea, client);
                break;
            case CLEAR_POT:
                ItemMakerHandler.ClearPot(slea, client);
                break;
            case FEED_POT:
                ItemMakerHandler.FeedPot(slea, client);
                break;
            case CURE_POT:
                ItemMakerHandler.CurePot(slea, client);
                break;
            case REWARD_POT:
                ItemMakerHandler.RewardPot(slea, client);
                break;
            case DAMAGE_SUMMON:
                slea.skip(4);
                SummonHandler.DamageSummon(slea, client.getPlayer());
                break;
            case MOVE_SUMMON:
                SummonHandler.MoveSummon(slea, client.getPlayer());
                break;
            case MOVE_DRAGON:
                SummonHandler.MoveDragon(slea, client.getPlayer());
                break;
            case SUB_SUMMON:
                SummonHandler.SubSummon(slea, client.getPlayer());
                break;
            case REMOVE_SUMMON:
                SummonHandler.RemoveSummon(slea, client);
                break;
            case SPAWN_PET:
                PetHandler.SpawnPet(slea, client, client.getPlayer());
                break;
            case MOVE_PET:
                PetHandler.MovePet(slea, client.getPlayer());
                break;
            case PET_CHAT:
                //System.out.println("Pet chat: " + slea.toString());
                if (slea.available() < 12) {
                    break;
                }
                final int petid = GameConstants.GMS ? client.getPlayer().getPetIndex((int) slea.readLong()) : slea.readInt();
                slea.readInt();
                PetHandler.PetChat(petid, slea.readShort(), slea.readMapleAsciiString(), client.getPlayer());
                break;
            case PET_COMMAND:
                MaplePet pet;
                if (GameConstants.GMS) {
                    pet = client.getPlayer().getPet(client.getPlayer().getPetIndex((int) slea.readLong()));
                } else {
                    pet = client.getPlayer().getPet((byte) slea.readInt());
                }
                slea.readByte(); //always 0?
                if (pet == null) {
                    return;
                }
                PetHandler.PetCommand(pet, PetDataFactory.getPetCommand(pet.getPetItemId(), slea.readByte()), client, client.getPlayer());
                break;
            case PET_FOOD:
                PetHandler.PetFood(slea, client, client.getPlayer());
                break;
            case PET_LOOT:
                InventoryHandler.Pickup_Pet(slea, client, client.getPlayer());
                break;
            case PET_AUTO_POT:
                PetHandler.Pet_AutoPotion(slea, client, client.getPlayer());
                break;
            case MONSTER_CARNIVAL:
                MonsterCarnivalHandler.MonsterCarnival(slea, client);
                break;
            case DUEY_ACTION:
                DueyHandler.DueyOperation(slea, client);
                break;
            case USE_HIRED_MERCHANT:
                HiredMerchantHandler.UseHiredMerchant(client, true);
                break;
            case MERCH_ITEM_STORE:
                HiredMerchantHandler.MerchantItemStore(slea, client);
                break;
            case CANCEL_DEBUFF:
                // Ignore for now
                break;
            case LEFT_KNOCK_BACK:
                PlayerHandler.leftKnockBack(slea, client);
                break;
            case SNOWBALL:
                PlayerHandler.snowBall(slea, client);
                break;
            case COCONUT:
                PlayersHandler.hitCoconut(slea, client);
                break;
            case REPAIR:
                NPCHandler.repair(slea, client);
                break;
            case REPAIR_ALL:
                NPCHandler.repairAll(client);
                break;
            case OWL:
                InventoryHandler.Owl(slea, client);
                break;
            case OWL_WARP:
                InventoryHandler.OwlWarp(slea, client);
                break;
            case USE_OWL_MINERVA:
                InventoryHandler.OwlMinerva(slea, client);
                break;
            case RPS_GAME:
                NPCHandler.RPSGame(slea, client);
                break;
            case UPDATE_QUEST:
                NPCHandler.UpdateQuest(slea, client);
                break;
            case USE_ITEM_QUEST:
                NPCHandler.UseItemQuest(slea, client);
                break;
            case FOLLOW_REQUEST:
                PlayersHandler.FollowRequest(slea, client);
                break;
            case AUTO_FOLLOW_REPLY:
            case FOLLOW_REPLY:
                PlayersHandler.FollowReply(slea, client);
                break;
            case RING_ACTION:
                PlayersHandler.RingAction(slea, client);
                break;
            case REQUEST_FAMILY:
                FamilyHandler.RequestFamily(slea, client);
                break;
            case OPEN_FAMILY:
                FamilyHandler.OpenFamily(slea, client);
                break;
            case FAMILY_OPERATION:
                FamilyHandler.FamilyOperation(slea, client);
                break;
            case DELETE_JUNIOR:
                FamilyHandler.DeleteJunior(slea, client);
                break;
            case DELETE_SENIOR:
                FamilyHandler.DeleteSenior(slea, client);
                break;
            case USE_FAMILY:
                FamilyHandler.UseFamily(slea, client);
                break;
            case FAMILY_PRECEPT:
                FamilyHandler.FamilyPrecept(slea, client);
                break;
            case FAMILY_SUMMON:
                FamilyHandler.FamilySummon(slea, client);
                break;
            case ACCEPT_FAMILY:
                FamilyHandler.AcceptFamily(slea, client);
                break;
            case SOLOMON:
                PlayersHandler.Solomon(slea, client);
                break;
            case GACH_EXP:
                PlayersHandler.GachExp(slea, client);
                break;
            case PARTY_SEARCH_START:
                PartyHandler.MemberSearch(slea, client);
                break;
            case PARTY_SEARCH_STOP:
                PartyHandler.PartySearch(slea, client);
                break;
            case EXPEDITION_LISTING:
                PartyHandler.PartyListing(slea, client);
                break;
            case EXPEDITION_OPERATION:
                PartyHandler.Expedition(slea, client);
                break;
            case USE_TELE_ROCK:
                InventoryHandler.TeleRock(slea, client);
                break;
            case PAM_SONG:
                InventoryHandler.PamSong(slea, client);
                break;
            case INNER_CIRCULATOR:
                //InventoryHandler.useInnerCirculator(slea, client);
                break;
            case REPORT:
                PlayersHandler.Report(slea, client);
                break;
            case EQUIP_STOLEN_SKILL:
                //PlayersHandler.UpdateEquippedSkills(slea, client, client.getPlayer());
                break;
            case UPDATE_STOLEN_SKILL:
                //PlayersHandler.UpdateStolenSkills(slea, client, client.getPlayer());
                break;
            case SKILL_SWIPE_REQUEST:
                //PlayersHandler.SkillSwipeRequest(slea, client, client.getPlayer());
                break;
            default:
                System.out.printf("[Warning] Opcode: %s not found.", header.toString());
                client.getPlayer().dropMessage(6, "[UNHANDLED] Recv [" + header.toString() + "] found");
                break;
        }
    }
}
