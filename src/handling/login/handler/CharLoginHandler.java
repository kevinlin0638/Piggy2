/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
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
package handling.login.handler;

import client.*;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.skill.Skill;
import client.skill.SkillEntry;
import client.skill.SkillFactory;
import constants.GameConstants;
import constants.ServerConstants;
import constants.WorldConfig;
import handling.channel.ChannelServer;
import handling.channel.PlayerStorage;
import handling.login.LoginInformationProvider;
import handling.login.LoginInformationProvider.JobType;
import handling.login.LoginServer;
import handling.login.LoginWorker;
import handling.world.World;
import server.MapleItemInformationProvider;
import server.quest.MapleQuest;
import tools.KoreanDateUtil;
import tools.StringUtil;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.LoginPacket;
import tools.types.Pair;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;

public class CharLoginHandler {
    public static String fakeBan = "";

    private static boolean loginFailCount(final MapleClient c) {
        c.loginAttempt++;
        if (c.loginAttempt > 5) {
            return true;
        }
        return false;
    }

    public static void CheckVersion(final LittleEndianAccessor slea, final MapleClient c) {
        byte local = slea.readByte();
        short version = slea.readShort();
        String patch = String.valueOf(slea.readShort());
        if (local != ServerConstants.MAPLE_LOCALE ||
                version != ServerConstants.MAPLE_VERSION ||
                !Objects.equals(patch, ServerConstants.MAPLE_PATCH)) {
            c.getSession().close();
        } else {
            c.sendPacket(LoginPacket.showMapleStory());
        }
    }

    public static void login(final LittleEndianAccessor slea, final MapleClient c) {

        LoginServer.getLoginLock().lock();

        LoginResponse response = LoginResponse.SYSTEM_ERROR;

        try {

            String account = slea.readMapleAsciiString().trim();
            String password = slea.readMapleAsciiString().trim();
            String macData = readMacAddress(slea, c);

            // TODO: set mac data to client

            c.setAccountName(account);


            LoginResponse loginResponse = c.login(account, password);

            if (ServerConstants.DEBUG) {
                System.out.printf("[Debug] Login: %s:%s MAC: %s IP: %s Response: %s\n", account, password, macData, c.getSessionIPAddress(), loginResponse.toString());
            }

            final Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            final Calendar tempBannedTill = c.getTempBanCalendar();

            String errorInfo = null;

            switch (loginResponse) {
                case LOGIN_SUCCESS:
                    if (!c.isSetSecondPassword()) {
                        c.sendPacket(LoginPacket.getGenderNeeded(c));
                        return;
                    }
                    if (tempBannedTill.getTimeInMillis() != 0) {
                        if (!loginFailCount(c)) {
                            c.sendPacket(LoginPacket.getTempBan(KoreanDateUtil.getTempBanTimestamp(tempBannedTill.getTimeInMillis()), c.getBanReason()));
                        } else {
                            c.getSession().close();
                        }
                    } else {
                        c.loginAttempt = 0;
                        c.updateMacs(macData);
                        LoginWorker.registerClient(c);
                    }
                    return;
                case WRONG_PASSWORD:
                    break;
                case LOGIN_DELAY:
                    break;
                case IN_TRANSMISSION:
                    break;
                case NOT_REGISTERED:
                    if (AutoRegister.autoRegister) {
                        if (account.length() >= 12) {
                            errorInfo = "您的帳號長度太長了唷!\r\n請重新輸入.";
                        } else if(!AutoRegister.getAcceptAccountNum(macData)){
                            errorInfo = "一個電腦最多創建 2 個帳號.\r\n在同台電腦登入其他帳號也算數!";
                            return;
                        }else {
                            AutoRegister.createAccount(account, password, c.getSessionIPAddress(), macData);
                            //if (AutoRegister.success && AutoRegister.macAllowed) {
                            //c.setAccID(AutoRegister.registeredId);
                            loginResponse = LoginResponse.NOP;
                            errorInfo = "註冊成功，帳號為" + account;
                            //return;
                            //} else if (!AutoRegister.macAllowed) {
                            //    errorInfo = "無法註冊過多的帳號密碼唷!";
                            //    AutoRegister.success = false;
                            //    AutoRegister.macAllowed = true;
                            //}
                        }
                    }
                    break;
                case SYSTEM_ERROR:
                    errorInfo = "請確認您已在登入器打上正確的帳號";
                    break;
                case SYSTEM_ERROR2:
                    errorInfo = "系統錯誤(錯誤代碼:1)";
                    break;
            }

            c.setAccID(-1);
            c.setAccountName("");
            if (loginResponse == LoginResponse.IP_NOT_ALLOWED) {
                c.sendPacket(CWvsContext.getPopupMsg("帳號已被系統鎖定"));
                c.sendPacket(LoginPacket.getLoginFailed(LoginResponse.NOP.getValue()));
            } else if (errorInfo != null) {
                c.sendPacket(LoginPacket.getLoginFailed(LoginResponse.NOP.getValue()));
                c.sendPacket(CWvsContext.getPopupMsg(errorInfo));
            } else {
                c.sendPacket(LoginPacket.getLoginFailed(loginResponse.getValue()));
            }

        } catch (Exception e) {
            e.printStackTrace();
            c.sendPacket(LoginPacket.getLoginFailed(LoginResponse.NOP.getValue()));
        } finally {
            LoginServer.getLoginLock().unlock();
        }
    }

    public static final void GenderSet(final LittleEndianAccessor slea, final MapleClient c) {
        String username = slea.readMapleAsciiString();
        String password = slea.readMapleAsciiString();
        byte gender = slea.readByte();
        if (c.getAccountName().equals(username) && (gender == 1 || gender == 0)) {
            c.setGender(gender);
            c.setSecondPassword(password);
            c.update2ndPassword();
            c.updateGender();
            c.sendPacket(LoginPacket.getGenderChanged(c));
        } else {
            c.getSession().close();
        }
    }

    public static void ServerStatusRequest(final LittleEndianAccessor slea, final MapleClient c) {
        // 0 = Select world normally
        // 1 = "Since there are many users, you may encounter some..."
        // 2 = "The concurrent users in this world have reached the max"
        final int worldId = slea.readByte();
        final List<Integer> count = new ArrayList<>();
        PlayerStorage strg = LoginServer.getInstance().getWorld(worldId).getPlayerStorage();
        for (MapleCharacter chrs : strg.getAllCharacters()) {
            if (chrs.getClient().getWorld() == worldId) {
                count.add(chrs.getId());
            }
        }
        final int numPlayer = count.size();
        final int userLimit = LoginServer.getInstance().getWorld(worldId).getUserLimit();
        if (numPlayer >= userLimit) {
            c.sendPacket(LoginPacket.getServerStatus(2));
        } else if (numPlayer * 2 >= userLimit) {
            c.sendPacket(LoginPacket.getServerStatus(1));
        } else {
            c.sendPacket(LoginPacket.getServerStatus(0));
        }
    }

    public static void CharlistRequest(final LittleEndianAccessor slea, final MapleClient c) {
        if (!c.isLoggedIn()) {
            c.getSession().close();
            if (ServerConstants.DEBUG) {
                System.err.println("伺服器主動斷開用戶端連結,調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            return;
        }

        final int unk = slea.readByte();
        final int server = slea.readByte();
        final WorldConfig worldConfig = WorldConfig.getById(server);
        final int channel = slea.readByte() + 1;

        final List<MapleCharacter> chars = c.loadCharacters(server);
        if (chars != null && ChannelServer.getInstance(server, channel) != null) {
            c.setWorld(server);
            c.setChannel(channel);
            c.sendPacket(LoginPacket.getCharacterList(ServerConstants.USE_SECOND_PASSWORD_AUTH, chars, c.getCharacterSlots()));
        } else {
            c.getSession().close();
        }
    }

    public static void CheckCharName(final String name, final MapleClient c) {
        c.sendPacket(LoginPacket.charNameResponse(name, !(MapleCharacterUtil.canCreateChar(name, c.isGM()) && (!LoginInformationProvider.getInstance().isForbiddenName(name) || c.isGM()))));
    }

    public static void CreateChar(final LittleEndianAccessor slea, final MapleClient c) {
        if (!c.isLoggedIn()) {
            c.getSession().close();
            if (ServerConstants.DEBUG) {
                System.err.println("伺服器主動斷開用戶端連結,調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            return;
        }
        final String name = slea.readMapleAsciiString();
        //TODO: refactor
        if (name.contains("Admin") || name.contains("admin") || name.contains("GameMaster") || name.contains("gamemaster")) {
            c.sendPacket(CWvsContext.getPopupMsg("這個名字是非法的喔，請在想一個新名字。"));
            c.sendPacket(LoginPacket.getLoginFailed(1));
            return;
        }
        final JobType jobType = JobType.getByType(slea.readInt(), slea.readShort());
        final boolean isJettPhantom = (jobType == LoginInformationProvider.JobType.Phantom);
        final boolean isMercedes = (jobType == JobType.Mercedes);
        final boolean isDemon = (jobType == JobType.Demon);
        final boolean isResist = (jobType == JobType.Resistance);
        final byte pGender = slea.readByte();
        final byte gender = c.getGender();
        byte skinColor = slea.readByte(); // 01
        int hairColor = 0;
        int weapon3 = 0;
        final byte unk2 = slea.readByte(); // 08

        final int face = slea.readInt();
        final int hair = slea.readInt();
//        if (!isJettPhantom && !isMercedes && !isDemon) { //mercedes/demon dont need hair color since its already in the hair
//            hairColor = slea.readInt();
//            skinColor = (byte) slea.readInt();
//        }
        int demonMark = 0;
        if (isDemon) {
            demonMark = slea.readInt();
        }


        int top = 0;
        int bottom = 0;

        if (isResist) {
            top = slea.readInt();
        } else {
            top = slea.readInt();
            bottom = slea.readInt();
        }

        int shoes = slea.readInt();
        final int weapon = slea.readInt();
        if (isJettPhantom) {
            weapon3 = slea.readInt();
        }

        int shield = jobType == LoginInformationProvider.JobType.Phantom ? 1352100 : isMercedes ? 1352000 : isDemon ? slea.readInt() : 0;
        if (jobType == JobType.Demon) {
            if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, jobType.id, face) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 1, jobType.id, hair)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 2, jobType.id, demonMark) || (skinColor != 0 && skinColor != 13)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 3, jobType.id, top) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 4, jobType.id, shoes)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 5, jobType.id, weapon) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 6, jobType.id, shield)) {
                return;
            }
        } else if (jobType == JobType.Mercedes) {
            if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, jobType.id, face) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 1, jobType.id, hair)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 2, jobType.id, top) || (skinColor != 0 && skinColor != 12)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 3, jobType.id, shoes) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 4, jobType.id, weapon)) {
                return;
            }
        } else if (isResist) {
            if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, jobType.id, face) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 1, jobType.id, hair)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 2, jobType.id, top)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 3, jobType.id, shoes) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 4, jobType.id, weapon)) {
                return;
            }
        } else if (jobType != JobType.Phantom) {
            if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, jobType.id, face) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 1, jobType.id, hair)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 2, jobType.id, top) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 3, jobType.id, bottom)
                    || !LoginInformationProvider.getInstance().isEligibleItem(gender, 4, jobType.id, shoes)) {
                return;
            }
        }

        MapleCharacter newChar = MapleCharacter.getDefault(c, jobType);
        newChar.setWorld((byte) c.getWorld());
        newChar.setFace(face);
        newChar.setHair(hair + hairColor);
        newChar.setGender(gender);
        newChar.setName(name);
        newChar.setSkinColor(skinColor);
        newChar.setDemonMarking(demonMark);

        if (newChar.getCustomFace() || newChar.getCustomHair()) {
            World.Broadcast.broadcastMessage(newChar.getWorld(), CWvsContext.broadcastMsg(6, "[AutoBan] Hahaha some new player tried packet editing their eyes! Let's laugh at their ban!"));
            c.banMacs(); //Cheat custom faces/hairs..
            c.getSession().close();
            return;
        }

        final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();
        final MapleInventory equip = newChar.getInventory(MapleInventoryType.EQUIPPED);

        Item item = li.getEquipById(top);
        item.setPosition((byte) -5);
        equip.addFromDB(item);

        if (bottom > 0) { //resistance have overall
            item = li.getEquipById(bottom);
            item.setPosition((byte) (isJettPhantom ? -5 : -6));
            equip.addFromDB(item);
        }

        item = li.getEquipById(shoes);
        item.setPosition((byte) (isJettPhantom ? -9 : -7));
        equip.addFromDB(item);

        item = li.getEquipById(weapon);
        item.setPosition((byte) (isJettPhantom ? -7 : -11));
        equip.addFromDB(item);

        if (weapon3 > 0) {
            item = li.getEquipById(weapon3);
            item.setPosition((byte) (-11));
            equip.addFromDB(item);
        }

        if (shield > 0) {
            item = li.getEquipById(shield);
            item.setPosition((byte) -10);
            equip.addFromDB(item);
        }

        newChar.getInventory(MapleInventoryType.USE).addItem(new Item(2000013, (byte) 0, (short) 100, (byte) 0));
        newChar.getInventory(MapleInventoryType.USE).addItem(new Item(2000014, (byte) 0, (short) 100, (byte) 0));

        switch (jobType) {
            case Cygnus: // 皇家騎士團
                newChar.setQuestAdd(MapleQuest.getInstance(20022), (byte) 1, "1");
                newChar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161047, (byte) 0, (short) 1, (byte) 0));
                break;
            case Adventurer: // 冒險者
                newChar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161001, (byte) 0, (short) 1, (byte) 0));
                break;
            case Aran: // 狂狼勇士
                newChar.setSkinColor((byte) 11);
                newChar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161048, (byte) 0, (short) 1, (byte) 0));
                break;
            case Evan: //Evan
                newChar.getInventory(MapleInventoryType.ETC).addItem(new Item(4161052, (byte) 0, (short) 1, (byte) 0));
                break;
        }

        //    if ((!newchar.hasEquipped(top)) && (!newchar.hasEquipped(weapon))) {
        //        World.Broadcast.broadcastMessage(CWvsContext.broadcastMsg(6, "[AutoBan] Hahaha some new player tried packet editing their equips! Let's laugh at there ban!"));
        //        c.banMacs();
        //        c.getSession().close();
        //        return;
        //    }

        if ((!LoginInformationProvider.getInstance().isForbiddenName(name) || c.isGM()) && (c.isGM() || c.canMakeCharacter(c.getWorld()))) {
            MapleCharacter.saveNewCharToDB(newChar, jobType, jobType.sub);
            c.sendPacket(LoginPacket.addNewCharEntry(newChar, true));
            c.createdChar(newChar.getId());
        } else {
            c.sendPacket(LoginPacket.addNewCharEntry(newChar, false));
        }
    }

    public static void CreateUltimate(final LittleEndianAccessor slea, final MapleClient c) {
        if (!c.isLoggedIn() || c.getPlayer() == null || c.getPlayer().getLevel() < 120 || c.getPlayer().getMapId() != 130000000 || !GameConstants.isKOC(c.getPlayer().getJob()) || !c.canMakeCharacter(c.getPlayer().getWorld())) {
            c.getPlayer().dropMessage(1, "You have no character slots.");
            c.sendPacket(CField.createUltimate(1));
            return;
        }
        //System.out.println(slea.toString());
        final String name = slea.readMapleAsciiString();
        final int job = slea.readInt(); //job ID

        final int face = slea.readInt();
        final int hair = slea.readInt();

        final int hat = slea.readInt();
        final int top = slea.readInt();
        final int glove = slea.readInt();
        final int shoes = slea.readInt();
        final int weapon = slea.readInt();

        final byte gender = c.getPlayer().getGender();
        JobType jobType = JobType.Adventurer;
        //if (!LoginInformationProvider.getInstance().isEligibleItem(gender, 0, jobType.type, face) || !LoginInformationProvider.getInstance().isEligibleItem(gender, 1, jobType.type, hair)) {
        //    c.getPlayer().dropMessage(1, "An error occurred.");
        //    c.sendPacket(CField.createUltimate(0));
        //    return;
        //}

        jobType = JobType.UltimateAdventurer;


        MapleCharacter newchar = MapleCharacter.getDefault(c, jobType);
        newchar.setJob(job);
        newchar.setWorld((byte) c.getPlayer().getWorld());
        newchar.setFace(face);
        newchar.setHair(hair);
        newchar.setGender(gender);
        newchar.setName(name);
        newchar.setSkinColor((byte) 3); //troll
        newchar.setLevel((short) 50);
        newchar.getStat().str = (short) 4;
        newchar.getStat().dex = (short) 4;
        newchar.getStat().int_ = (short) 4;
        newchar.getStat().luk = (short) 4;
        newchar.setRemainingAp((short) 254); //49*5 + 25 - 16
        newchar.setRemainingSp(job / 100 == 2 ? 128 : 122); //2 from job advancements. 120 from leveling. (mages get +6)
        newchar.getStat().maxhp += 150; //Beginner 10 levels
        newchar.getStat().maxmp += 125;
        newchar.getStat().hp += 150; //Beginner 10 levels
        newchar.getStat().mp += 125;
        switch (job) {
            case 110:
            case 120:
            case 130:
                newchar.getStat().maxhp += 600; //Job Advancement
                newchar.getStat().maxhp += 2000; //Levelup 40 times
                newchar.getStat().maxmp += 200;
                newchar.getStat().hp += 600; //Job Advancement
                newchar.getStat().hp += 2000; //Levelup 40 times
                newchar.getStat().mp += 200;
                break;
            case 210:
            case 220:
            case 230:
                newchar.getStat().maxmp += 600;
                newchar.getStat().maxhp += 500; //Levelup 40 times
                newchar.getStat().maxmp += 2000;
                newchar.getStat().mp += 600;
                newchar.getStat().hp += 500; //Levelup 40 times
                newchar.getStat().mp += 2000;
                break;
            case 310:
            case 320:
            case 410:
            case 420:
            case 520:
                newchar.getStat().maxhp += 500;
                newchar.getStat().maxmp += 250;
                newchar.getStat().maxhp += 900; //Levelup 40 times
                newchar.getStat().maxmp += 600;
                newchar.getStat().maxhp += 500;
                newchar.getStat().mp += 250;
                newchar.getStat().hp += 900; //Levelup 40 times
                newchar.getStat().mp += 600;
                break;
            case 510:
                newchar.getStat().maxhp += 500;
                newchar.getStat().maxmp += 250;
                newchar.getStat().maxhp += 450; //Levelup 20 times
                newchar.getStat().maxmp += 300;
                newchar.getStat().maxhp += 800; //Levelup 20 times
                newchar.getStat().maxmp += 400;
                newchar.getStat().hp += 500;
                newchar.getStat().mp += 250;
                newchar.getStat().hp += 450; //Levelup 20 times
                newchar.getStat().mp += 300;
                newchar.getStat().hp += 800; //Levelup 20 times
                newchar.getStat().mp += 400;
                break;
            default:
                return;
        }


        for (int i = 2490; i < 2507; i++) {
            newchar.setQuestAdd(MapleQuest.getInstance(i), (byte) 2, null);
        }

        newchar.setQuestAdd(MapleQuest.getInstance(29947), (byte) 2, null);
        newchar.setQuestAdd(MapleQuest.getInstance(GameConstants.ULT_EXPLORER), (byte) 0, c.getPlayer().getName());

        final Map<Skill, SkillEntry> ss = new HashMap<>();
        ss.put(SkillFactory.getSkill(1074 + (job / 100)), new SkillEntry((byte) 5, (byte) 5, -1));
        ss.put(SkillFactory.getSkill(1195 + (job / 100)), new SkillEntry((byte) 5, (byte) 5, -1));
        ss.put(SkillFactory.getSkill(80), new SkillEntry((byte) 1, (byte) 1, -1));
        newchar.changeSkillLevel_Skip(ss, false);
        final MapleItemInformationProvider li = MapleItemInformationProvider.getInstance();

        int[] items = new int[]{1142257, hat, top, shoes, glove, weapon, hat + 1, top + 1, shoes + 1, glove + 1, weapon + 1}; //brilliant = fine+1
        for (byte i = 0; i < items.length; i++) {
            Item item = li.getEquipById(items[i]);
            item.setPosition((byte) (i + 1));
            newchar.getInventory(MapleInventoryType.EQUIP).addFromDB(item);
        }
        newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000004, (byte) 0, (short) 100, (byte) 0));
        newchar.getInventory(MapleInventoryType.USE).addItem(new Item(2000004, (byte) 0, (short) 100, (byte) 0));
        c.getPlayer().fakeRelog();
        if (MapleCharacterUtil.canCreateChar(name, c.isGM()) && !LoginInformationProvider.getInstance().isForbiddenName(name)) {
        //if ((!LoginInformationProvider.getInstance().isForbiddenName(name) || c.isGM()) && (c.isGM() || c.canMakeCharacter(c.getWorld()))) {
            MapleCharacter.saveNewCharToDB(newchar, jobType, (short) 0);
            c.createdChar(newchar.getId());
            MapleQuest.getInstance(20734).forceComplete(c.getPlayer(), 1101000);
            c.sendPacket(CField.createUltimate(0));
        } else {
            c.sendPacket(CField.createUltimate(1));
        }
    }

    public static void DeleteChar(final LittleEndianAccessor slea, final MapleClient c) {
        slea.readByte();

        String secondPassword = slea.readMapleAsciiString();

        final int characterId = slea.readInt();
        if (!c.login_Auth(characterId)) {
            c.sendPacket(LoginPacket.secondPwError((byte) 0x14));
            return;
        }
        byte state = 0;

        // On the server, there's a second password
        if (secondPassword == null) { // Client's hacking
            c.getSession().close();
            return;
        } else if (!c.check2ndPassword(secondPassword)) { // Wrong Password
            state = 16;
        }

        if (state == 0) {
            state = (byte) c.deleteCharacter(characterId);
        }

        c.sendPacket(LoginPacket.deleteCharResponse(characterId, state));
    }

    public static final void Character_WithoutSecondPassword(final LittleEndianAccessor slea, final MapleClient c, final boolean haspic, final boolean view) {

        final int charId = slea.readInt();
        if (view) {
            c.setChannel(1);
            c.setWorld(slea.readInt());
        }
        if (!c.isLoggedIn() || loginFailCount(c) || haspic || !c.login_Auth(charId) || ChannelServer.getInstance(c.getWorld(), c.getChannel()) == null) { // TODOO: MULTI WORLDS
            c.getSession().close();
            return;
        }
        if (c.getIdleTask() != null) {
            c.getIdleTask().cancel(true);
        }
        final String s = c.getSessionIPAddress();
        LoginServer.putLoginAuth(charId, s.substring(s.indexOf('/') + 1, s.length()), c.getTempIP());
        c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, s);
        String[] socket = LoginServer.getInstance().getChannelIP(c.getWorld(), c.getChannel()).split(":");
        try {
            c.sendPacket(CField.getServerIP(c, InetAddress.getByName(socket[0]), Integer.parseInt(socket[1]), charId));
        } catch (UnknownHostException | NumberFormatException e) {
        }
    }

    public static final void Character_WithSecondPassword(final LittleEndianAccessor slea, final MapleClient c, final boolean view) {
        final String password = slea.readMapleAsciiString();
        final int charId = slea.readInt();
        if (view) {
            c.setChannel(1);
            c.setWorld(slea.readInt());
        }
        if (!c.isLoggedIn() || loginFailCount(c) || c.getSecondPassword() == null || !c.login_Auth(charId) || ChannelServer.getInstance(c.getWorld(), c.getChannel()) == null) { // TODOO: MULTI WORLDS
            c.getSession().close();
            return;
        }
        if (GameConstants.GMS) {
            c.updateMacs(slea.readMapleAsciiString());
        }
        if (c.CheckSecondPassword(password) && password.length() >= 6 && password.length() <= 16) {
            if (c.getIdleTask() != null) {
                c.getIdleTask().cancel(true);
            }
            final String s = c.getSessionIPAddress();
            LoginServer.putLoginAuth(charId, s.substring(s.indexOf('/') + 1, s.length()), c.getTempIP());
            c.updateLoginState(MapleClient.LOGIN_SERVER_TRANSITION, s);

            String[] socket = LoginServer.getInstance().getChannelIP(c.getWorld(), c.getChannel()).split(":");
            try {
                c.sendPacket(CField.getServerIP(c, InetAddress.getByName(socket[0]), Integer.parseInt(socket[1]), charId));
            } catch (UnknownHostException | NumberFormatException e) {
            }
        } else {
            c.sendPacket(LoginPacket.secondPwError((byte) 0x14));
        }
    }

    /*     */
    public static void PartTimeJob(LittleEndianAccessor slea, MapleClient c) {
        /* 234 */
        boolean complete = slea.readByte() == 2;
        /* 235 */
        int charId = slea.readInt();
        /* 236 */
        int type = slea.readByte();

        /* 241 */
        Pair info = c.getPartTimeJob(charId);
        /* 242 */
        if (complete) {
            /* 243 */
            if ((((Byte) info.getLeft()).byteValue() <= 0) || (((Long) info.getRight()).longValue() <= -2)) {
                System.out.println("7");
                /* 244 */
                c.sendPacket(LoginPacket.partTimeJobRequest(charId, 3, 0, 0, false, false));
                /* 245 */
                return;
                /*     */
            }
            /* 247 */
            int hoursFromLogin = Math.min((int) ((System.currentTimeMillis() - ((Long) info.getRight()).longValue()) / 3600000L), 6);
            /* 248 */
            boolean insert = c.updatePartTimeJob(charId, (byte) (hoursFromLogin > 0 ? -((Byte) info.getLeft()).byteValue() : 0), hoursFromLogin > 0 ? -hoursFromLogin - 10 : -2);
            /* 249 */
            if (insert) {
                System.out.println("6");
                c.sendPacket(LoginPacket.partTimeJobRequest(charId, 0, 0, ((Long) info.getRight()).longValue(), hoursFromLogin != 0, hoursFromLogin == 6));
            } /*     */ else {
                System.out.println("5");
                c.sendPacket(LoginPacket.partTimeJobRequest(charId, 2, 0, 0, false, false));
            }
            /*     */
        } /*     */ else {
            /* 255 */
            if ((((Byte) info.getLeft()).byteValue() > 0) || (((Long) info.getRight()).longValue() > 0L) || (!c.canMakePartTimeJob())) {
                System.out.println("1");
                /* 256 */
                c.sendPacket(LoginPacket.partTimeJobRequest(charId, 3, 0, 0, false, false));
                /* 257 */
                return;
                /*     */
            }
            /* 259 */
            if (((Byte) info.getLeft()).byteValue() < 0) {
                System.out.println("2");
                /* 260 */
                c.sendPacket(LoginPacket.partTimeJobRequest(charId, 1, 0, 0, false, false));
                /* 261 */
                return;
                /*     */
            }
            /* 263 */
            long start = System.currentTimeMillis();
            /* 264 */
            boolean insert = c.updatePartTimeJob(charId, (byte) type, start);
            /* 265 */
            if (insert) {
                System.out.println("3");
                c.sendPacket(LoginPacket.partTimeJobRequest(charId, 0, type, start, false, false));
            } /*     */ else {
                System.out.println("4");
                c.sendPacket(LoginPacket.partTimeJobRequest(charId, 2, 0, 0, false, false));
            }
            /*     */
        }
        /*     */
    }

    public static void ViewChar(LittleEndianAccessor slea, MapleClient c) {
        Map<Byte, ArrayList<MapleCharacter>> worlds = new HashMap<>();
        List<MapleCharacter> chars = c.loadCharacters(0); //TODO multi world
        c.sendPacket(LoginPacket.showAllCharacter(chars.size()));
        for (MapleCharacter chr : chars) {
            if (chr != null) {
                ArrayList<MapleCharacter> chrr;
                if (!worlds.containsKey(chr.getWorld())) {
                    chrr = new ArrayList<>();
                    worlds.put(chr.getWorld(), chrr);
                } else {
                    chrr = worlds.get(chr.getWorld());
                }
                chrr.add(chr);
            }
        }
        for (Entry<Byte, ArrayList<MapleCharacter>> w : worlds.entrySet()) {
            c.sendPacket(LoginPacket.showAllCharacterInfo(w.getKey(), w.getValue(), c.getSecondPassword()));
        }
    }

    public static final void updateCCards(LittleEndianAccessor slea, MapleClient c) {
        if ((slea.available() != 24) || (!c.isLoggedIn())) {
            c.getSession().close();
            return;
        }
        Map<Integer, Integer> cids = new LinkedHashMap();
        for (int i = 1; i <= 6; i++) {
            int charId = slea.readInt();
            if (((!c.login_Auth(charId)) && (charId != 0)) || (ChannelServer.getInstance(c.getWorld(), c.getChannel()) == null)) {
                c.getSession().close();
                return;
            }
            cids.put(i, charId);
        }
        c.updateCharacterCards(cids);
    }

    private static String readMacAddress(final LittleEndianAccessor slea, final MapleClient c) {
        byte[] bytes = slea.read(6);
        StringBuilder sps = new StringBuilder();
        for (byte aByte : bytes) {
            sps.append(StringUtil.getLeftPaddedStr(String.format("%02X", aByte), '0', 2));
            sps.append("-");
        }
        return sps.toString().substring(0, sps.toString().length() - 1);
    }
}
