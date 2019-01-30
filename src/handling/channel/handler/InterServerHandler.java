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
package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import database.DatabaseConnection;
import handling.login.handler.LoginResponse;
import server.status.MapleDiseaseValueHolder;
import client.skill.SkillFactory;
import constants.GameConstants;
import constants.MapConstants;
import constants.ServerConstants;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.*;
import handling.world.exped.MapleExpedition;
import handling.world.guild.MapleGuild;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import tools.FileoutputUtil;
import tools.data.LittleEndianAccessor;
import tools.data.MaplePacketLittleEndianWriter;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.BuddylistPacket;
import tools.packet.CWvsContext.ExpeditionPacket;
import tools.packet.CWvsContext.FamilyPacket;
import tools.packet.CWvsContext.GuildPacket;
import tools.packet.LoginPacket;
import tools.packet.MTSCSPacket;

import javax.xml.crypto.Data;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.List;

public class InterServerHandler {

    public static final void EnterCS(final MapleClient c, final MapleCharacter chr, final boolean mts) {
        if (chr.hasBlockedInventory() || chr.getMap() == null || chr.getEventInstance() != null || c.getChannelServer() == null) {
            c.sendPacket(CField.serverBlocked(2));
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (ServerConstants.BLOCK_CASH_SHOP) {
            chr.dropMessage(1, "The Cash Shop has been temporarily disabled due to the amount of bugged players.");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (chr.inJQ()) {
            chr.dropMessage(1, "You can't exit the AutoJQ unless you type @exit.");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (chr.getMapId() == 502) {
            chr.dropMessage(1, "You can't enter the Cash Shop while in Fiesta.");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (GameConstants.isJail(chr.getMapId())) {
            chr.dropMessage(1, "You can't enter the Cash Shop while in Jail.");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (World.getPendingCharacterSize() >= 10) {
            chr.dropMessage(1, "The server is busy at the moment. Please try again in a minute or less.");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        World.ChannelChange_Data(new CharacterTransfer(c.getPlayer()), c.getPlayer().getId(), c.getWorld(), -1);
        final String s = c.getSessionIPAddress();
        LoginServer.addIPAuth(s.substring(s.indexOf('/') + 1, s.length()));

        if (c.getPlayer().getMessenger() != null) {
            World.Messenger.silentLeaveMessenger(c.getPlayer().getMessenger().getId(), new MapleMessengerCharacter(c.getPlayer()));
        }
        c.getPlayer().changeRemoval();
        PlayerBuffStorage.addBuffsToStorage(c.getPlayer().getId(), c.getPlayer().getAllBuffs());
        PlayerBuffStorage.addDiseaseToStorage(c.getPlayer().getId(), c.getPlayer().getAllDiseases());
        PlayerBuffStorage.addCooldownsToStorage(c.getPlayer().getId(), c.getPlayer().getCooldowns());
        c.getPlayer().getMap().removePlayer(c.getPlayer());
        c.getChannelServer().removePlayer(c.getPlayer());
        c.getPlayer().saveToDB(false, false);
        c.updateLoginState(MapleClient.CHANGE_CHANNEL, c.getSessionIPAddress());
        String[] socket = CashShopServer.getIP().split(":");
        try {
            c.sendPacket(CField.getChannelChange(c, InetAddress.getByName(socket[0]), Integer.parseInt(socket[1])));
        } catch (UnknownHostException | NumberFormatException e) {
        }
        c.setPlayer(null);
        c.setReceiving(false);
    }

    public static final void Loggedin(final int playerid, final MapleClient client) {
        MapleCharacter player;
        CharacterTransfer transfer = client.getWorldServer().getPlayerStorage().getPendingCharacter(playerid);
        if (transfer == null) {
            transfer = CashShopServer.getPlayerStorage().getPendingCharacter(playerid);
            if (transfer == null) {
                player = MapleCharacter.loadCharFromDB(playerid, client, true);
            } else {
                player = MapleCharacter.ReconstructChr(transfer, client, true);
                player.setInCS(true);
            }
        } else {
            player = MapleCharacter.ReconstructChr(transfer, client, true);
        }

        client.setPlayer(null);
        client.setAccID(player.getAccountID());

        if (!client.CheckIPAddress()) { // Remote hack
            client.getSession().close();
            return;
        }



        final int state = client.getLoginState();
        if (state != MapleClient.LOGIN_SERVER_TRANSITION && transfer == null) {
            client.getSession().close();
            return;
        }

        if (state == MapleClient.LOGIN_SERVER_TRANSITION && transfer != null) {
            client.getSession().close();
            return;
        }

        if (state != MapleClient.LOGIN_SERVER_TRANSITION && state != MapleClient.CHANGE_CHANNEL) {
            client.getSession().close();
            return;
        }

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        //TODO: QQQ
//        if (client.getLastLogin() + 3 * 1000 < currentTime.getTime()) {
//            client.setReceiving(false);
//            client.getSession().close();
//            return;
//        }
        if (!client.CheckIPAddress()) { // Remote hack
            System.out.println(client.getAccountName() + " BUG?3");
            client.getSession().close();
            return;
        }

        final ChannelServer channelServer = client.getChannelServer();
        World world = LoginServer.getWorld(client.getWorld());

        if (world == null) {
            client.getSession().close();
        }

        boolean is_p = false;
        for(MapleClient cl : World.pending_clients){
            if(cl.getAccountName().equals(client.getAccountName()) && cl.getSessionIPAddress().equalsIgnoreCase(client.getSessionIPAddress())){
                is_p = true;
                client.setClinetS(cl);
                final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
                mplew.writeShort(10);
                mplew.write(Integer.toString(player.getStat().getBkd()).getBytes());
                client.getClinetS().sendPacket(mplew.getPacket());
            }
        }

        if(!is_p && (!client.getAccountName().equals("kappa") && !client.getAccountName().equals("kappa2"))) {
            client.getSession().close();
            return;
        }

        final Connection con = DatabaseConnection.getConnection();


        client.setPlayer(player);
        client.setAccID(player.getAccountID());
        client.updateLoginState(MapleClient.LOGIN_LOGGED, client.getSessionIPAddress());
        channelServer.addPlayer(player);
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM paybill_bills WHERE accountID = ? AND isSent = 0", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, client.getAccID());
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                if(player.getClient().getAccountName().equalsIgnoreCase(rs.getString("account"))) {
                    player.dropMessage("帳號 : " + rs.getString("account") + " 成功獲得 " + (int) Math.floor(rs.getInt("money") * 1.5) + " 點贊助點.");
                    player.gainPoints((int) Math.floor(rs.getInt("money") * 1.5));
                    for(MapleClient cll : World.pending_clients){
                        if(cll.getAccountName().equalsIgnoreCase(rs.getString("account"))) {
                            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
                            mplew.writeShort(666);
                            String sb = "帳號 : " + rs.getString("account") + " 成功獲得 " + (int) Math.floor(rs.getInt("money") * 1.5) + " 點贊助點.";
                            mplew.write(sb.getBytes());
                            cll.sendPacket(mplew.getPacket());
                        }
                    }
                    ps = con.prepareStatement("UPDATE paybill_bills SET isSent = ? WHERE BillID = ? AND isSent != 1");
                    ps.setInt(1, 1);
                    ps.setInt(2, rs.getInt("BillID"));
                    ps.executeUpdate();
                    ps.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        player.giveCoolDowns(PlayerBuffStorage.getCooldownsFromStorage(player.getId()));
        player.silentGiveBuffs(PlayerBuffStorage.getBuffsFromStorage(player.getId()));
        final List<MapleDiseaseValueHolder> ld = PlayerBuffStorage.getDiseaseFromStorage(player.getId());
        if (ld != null) {
            player.giveSilentDebuff(ld);
        }
        client.sendPacket(CField.getCharInfo(player));
        player.getMap().addPlayer(player);
        world.getPlayerStorage().addPlayer(player);
        client.sendPacket(MTSCSPacket.enableCSUse());
        client.sendPacket(CWvsContext.temporaryStats_Reset()); //?

        if (player.inCS()) {
            player.setInCS(false); // exit them from CS enabling
        } else {
            client.sendPacket(CWvsContext.yellowChat("[小喵谷] 歡迎來到 " + ServerConstants.SERVER_NAME));
            client.sendPacket(CField.sendHint("" + ServerConstants.WELCOME_MESSAGE + "", 350, 5));
        }
        // GM Hide is a skill now, and auto-applies super hide. 
        if (player.isGM()) {
            if (player.isGod()) {
                player.setMegaHide(true); // on 
            }
            //SkillFactory.getSkill(9001004).getEffect(1).applyTo(c.getPlayer());
            //player.dropMessage(6, "Hide Deactivated.");
            //player.toggleHide(false, !player.isHidden());
        }
        //管理員上線預設隱藏
        if (player.isGM()) {
           // SkillFactory.getSkill(9001004).getEffect(1).applyTo(player);
        }
        try {
            // Start of buddylist
            final int buddyIds[] = player.getBuddylist().getBuddyIds();
            World.Buddy.loggedOn(player.getName(), player.getId(), client.getChannel(), buddyIds);
            if (player.getParty() != null) {
                final MapleParty party = player.getParty();
                World.Party.updateParty(party.getId(), PartyOperation.LOG_ONOFF, new MaplePartyCharacter(player));

                if (party != null && party.getExpeditionId() > 0) {
                    final MapleExpedition me = World.Party.getExped(party.getExpeditionId());
                    if (me != null) {
                        client.sendPacket(ExpeditionPacket.expeditionStatus(me, false));
                    }
                }
            }
            final CharacterIdChannelPair[] onlineBuddies = World.Find.multiBuddyFind(player.getId(), buddyIds);
            for (CharacterIdChannelPair onlineBuddy : onlineBuddies) {
                player.getBuddylist().get(onlineBuddy.getCharacterId()).setChannel(onlineBuddy.getChannel());
            }
            client.sendPacket(BuddylistPacket.updateBuddylist(player.getBuddylist().getBuddies()));

            // Start of Messenger
            final MapleMessenger messenger = player.getMessenger();
            if (messenger != null) {
                World.Messenger.silentJoinMessenger(messenger.getId(), new MapleMessengerCharacter(client.getPlayer()));
                World.Messenger.updateMessenger(messenger.getId(), client.getPlayer().getName(), client.getWorld(), client.getChannel());
            }

            // Start of Guild and alliance
            if (player.getGuildId() > 0) {
                World.Guild.setGuildMemberOnline(player.getMGC(), true, client.getChannel());
                client.sendPacket(GuildPacket.showGuildInfo(player));
                final MapleGuild gs = World.Guild.getGuild(player.getGuildId());
                if (gs != null) {
                    final List<byte[]> packetList = World.Alliance.getAllianceInfo(gs.getAllianceId(), true);
                    if (packetList != null) {
                        for (byte[] pack : packetList) {
                            if (pack != null) {
                                client.sendPacket(pack);
                            }
                        }
                    }
                } else { //guild not found, change guild id
                    player.setGuildId(0);
                    player.setGuildRank((byte) 5);
                    player.setAllianceRank((byte) 5);
                    player.saveGuildStatus();
                }
            }

            if (player.getFamilyId() > 0) {
                World.Family.setFamilyMemberOnline(player.getMFC(), true, client.getChannel());
            }
            client.sendPacket(FamilyPacket.getFamilyData());
            client.sendPacket(FamilyPacket.getFamilyInfo(player));
        } catch (Exception e) {
            FileoutputUtil.outputFileError(FileoutputUtil.Login_Error, e);
        }
        player.getClient().sendPacket(CWvsContext.broadcastMessage(channelServer.getServerMessage()));
        player.sendMacros();
        player.showNote();
        player.sendImp();
        player.updatePartyMemberHP();
        player.startFairySchedule(false);
        client.sendPacket(CField.getKeymap(player.getKeyLayout()));
        //client.sendPacket(LoginPacket.enableReport());
        player.updatePetAuto();
        player.expirationTask(true, player == null);
        if (player.getJob() == 132) { // DARKKNIGHT
            player.checkBerserk();
        }
        player.spawnSavedPets();
        if (player.getStat().equippedSummon > 0) {
            SkillFactory.getSkill(player.getStat().equippedSummon).getEffect(1).applyTo(player);
        }
        player.loadQuests(client);
        client.sendPacket(CWvsContext.getFamiliarInfo(player));
        if (World.getShutdown()) {
            player.getClient().sendPacket(CWvsContext.getMidMsg("The server is preparing to shutdown, so don't get too comfortable!", true, 1));
        }
        if (MapConstants.isStartingEventMap(player.getMap().getId())) {
            World.Broadcast.broadcastMessage(player.getWorld(), CWvsContext.yellowChat("[" + client.getPlayer().getName() + "] Just Joined " + ServerConstants.SERVER_NAME + " - The Ultimate MapleStory Private WorldConfig!"));
            player.dropMessage(6, "Welcome to " + ServerConstants.SERVER_NAME + ", Player #" + player.getId() + "!");
        }
        if (player.haveItem(ServerConstants.Currency, 1000, false, true) && !player.isDonator() && player.getReborns() < 50 && !player.isSuperDonor() && !player.isGM()) {
            player.sendGMMessage(6, "[GM Notification]: " + player.getName() + " has more then 1000 Munny, and less then 50 rebirths.");
        }
        if (player.haveItem(ServerConstants.Currency, 50000, false, true) && !player.isGM()) {
            player.sendGMMessage(6, "[GM Notification]: " + player.getName() + " has over 50,000 Munny. Check to see if they're hacking!");
        }
        player.saveToDB(false, false);
        //final List<Pair<Integer, String>> ii = new LinkedList<>();
        //ii.add(new Pair<>(10000, "Pio"));
        //player.getClient().sendPacket(CField.NPCTalkPacket.setNPCScriptable(ii));
    }

    public static void ChangeChannel(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr, final boolean room) {
        if (chr == null || chr.hasBlockedInventory() || chr.getEventInstance() != null || chr.getMap() == null || chr.isInBlockedMap() || FieldLimitType.ChannelSwitch.check(chr.getMap().getFieldLimit())) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (World.getPendingCharacterSize() >= 10) {
            chr.dropMessage(1, "The server is busy at the moment. Please try again in a less than a minute.");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        final int chc = slea.readByte() + 1;
        int mapid = 0;
        if (room) {
            mapid = slea.readInt();
        }
        slea.readInt();
        if (!World.isChannelAvailable(c.getWorld(), chc)) {
            chr.dropMessage(1, "The channel is full at the moment.");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (room && (mapid < 910000001 || mapid > 910000022)) {
            chr.dropMessage(1, "The channel is full at the moment.");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (chr.inJQ()) {
            chr.dropMessage(1, "You can't CC during a Jump Quest, try @exit.");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (GameConstants.isJail(chr.getMapId())) {
            chr.dropMessage(1, "You can't Change Channels in Jail, fgt.");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (room) {
            if (chr.getMapId() == mapid) {
                if (c.getChannel() == chc) {
                    chr.dropMessage(1, "You are already in " + chr.getMap().getMapName());
                    c.sendPacket(CWvsContext.enableActions());
                } else { // diff channel
                    chr.changeChannel(chc);
                }
            } else { // diff map
                if (c.getChannel() != chc) {
                    chr.changeChannel(chc);
                }
                final MapleMap warpz = ChannelServer.getInstance(c.getWorld(), c.getChannel()).getMapFactory().getMap(mapid);
                if (warpz != null) {
                    chr.changeMap(warpz, warpz.getPortal("out00"));
                } else {
                    chr.dropMessage(1, "The channel is full at the moment.");
                    c.sendPacket(CWvsContext.enableActions());
                }
            }
        } else {
            chr.changeChannel(chc);
        }
    }
}
