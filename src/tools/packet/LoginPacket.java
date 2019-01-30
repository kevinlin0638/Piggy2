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
package tools.packet;

import client.MapleCharacter;
import client.MapleClient;
import constants.ServerConstants;
import handling.SendPacketOpcode;
import handling.channel.ChannelServer;
import tools.HexTool;
import tools.KoreanDateUtil;
import tools.data.MaplePacketLittleEndianWriter;
import tools.types.Pair;

import java.util.LinkedList;
import java.util.List;

public class LoginPacket {


    public static byte[] getHello(final byte[] sendIv, final byte[] recvIv) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(15 + ServerConstants.MAPLE_PATCH.length());
        mplew.writeShort(0x0D + ServerConstants.MAPLE_PATCH.length()); // length of the packet
        mplew.writeShort(ServerConstants.MAPLE_VERSION);
        mplew.writeMapleAsciiString(ServerConstants.MAPLE_PATCH);
        mplew.write(recvIv);
        mplew.write(sendIv);
        mplew.write(ServerConstants.MAPLE_LOCALE);
        return mplew.getPacket();
    }

    public static byte[] showMapleStory() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(2);
        mplew.writeShort(SendPacketOpcode.SHOW_MAPLESTORY.getValue());
        return mplew.getPacket();
    }

    public static byte[] getLoginBackground() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LOGIN_AUTH.getValue());
        //UI.wz/MapLogin.img ... MapLogin2.img
        String[] bg = {"MapLogin", "MapLogin0", "MapLogin1", "MapLogin2"};
        mplew.writeMapleAsciiString(bg[(int) (Math.random() * bg.length)]);
        mplew.writeInt(KoreanDateUtil.getCurrentDate());
        mplew.write(1);

        return mplew.getPacket();
    }

    public static final byte[] getGenderChanged(final MapleClient client) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GENDER_SET.getValue());
        mplew.writeMapleAsciiString(client.getAccountName());

        return mplew.getPacket();
    }

    public static final byte[] getGenderNeeded(final MapleClient client) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHOOSE_GENDER.getValue());
        mplew.writeMapleAsciiString(client.getAccountName());

        return mplew.getPacket();
    }


    public static byte[] getPing() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(2);
        mplew.writeShort(SendPacketOpcode.PING.getValue());
        return mplew.getPacket();
    }

    public static byte[] getAuthSuccessRequest(final MapleClient client) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.write(0); // status

        mplew.writeInt(client.getAccID());
        mplew.write(client.getGender());
        mplew.writeBool(client.isGM());
        // GM flag , SuperGM(1<<4)  GM(1<<5)
        mplew.write(client.isGM() ? 0x10 : 0x00);
        mplew.writeInt(0);
        mplew.writeMapleAsciiString(client.getAccountName());
        mplew.writeInt(1);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0); // 1 = 帳號禁止說話
        mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));// 禁止說話期限
        mplew.write(0);
        mplew.writeLong(0x64);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static byte[] getLoginFailed(final int reason) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);

        // -1/6/8/9 : Trouble logging in
        // 2/3 : Id deleted or blocked
        // 4: Incorrect password
        // 5: Not a registered ID
        // 7: Logged in    
        // 10: Too many requests
        // 11: 20 years older can use
        // 13: Unable to log on as master at IP
        // 14/15: Redirect to nexon + buttons    
        // 16/21: Verify account
        // 17: Selected the wrong gateway
        // 25: Logging in outside service region
        // 23: License agreement
        // 27: Download full client
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.write(reason);
        mplew.write(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] sendToS() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);
        mplew.writeShort(SendPacketOpcode.SEND_EULA.getValue());
        mplew.write(23);
        //mplew.write(0);
        //mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] getPermBan(final byte reason) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(16);

        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.writeShort(2); // Account is banned
        mplew.writeInt(0);
        mplew.writeShort(reason);
        mplew.write(HexTool.getByteArrayFromHexString("01 01 01 01 00"));

        return mplew.getPacket();
    }

    public static byte[] getTempBan(final long timestampTill, final byte reason) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(17);

        // 99 : You have been blocked for typing in an invalid password or pincode 5 times.
        // 199 : You have been blocked for typing in an invalid password or pincode 10 times.
        // 299 : You have been blocked for typing in an invalid password or pincode more than 10 times.			
        mplew.writeShort(SendPacketOpcode.LOGIN_STATUS.getValue());
        mplew.write(2);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.write(reason);
        mplew.writeLong(timestampTill); // Tempban date is handled as a 64-bit long, number of 100NS intervals since 1/1/1601.

        return mplew.getPacket();
    }

    public static byte[] deleteCharResponse(final int cid, final int state) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DELETE_CHAR_RESPONSE.getValue());
        mplew.writeInt(cid);
        mplew.write(state);

        return mplew.getPacket();
    }

    public static byte[] secondPwError(final byte mode) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);

        /*
         * 14 - Invalid password
         * 15 - Second password is incorrect
         */
        mplew.writeShort(SendPacketOpcode.SECONDPW_ERROR.getValue());
        mplew.write(0/*mode*/);

        return mplew.getPacket();
    }

    public static byte[] enableReport() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
        mplew.writeShort(SendPacketOpcode.REPORT_STATUS.getValue());
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] enableRecommended() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ENABLE_RECOMMENDED.getValue());
        mplew.writeInt(0); //worldID with most characters
        return mplew.getPacket();
    }

    public static byte[] selectWorld(int world) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ENABLE_RECOMMENDED.getValue());
        mplew.writeInt(world); //worldID with most characters
        return mplew.getPacket();
    }

    public static byte[] sendRecommended(int world, String message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SEND_RECOMMENDED.getValue());
        mplew.write(message != null ? 1 : 0); //amount of messages
        if (message != null) {
            mplew.writeInt(world);
            mplew.writeMapleAsciiString(message);
        }
        return mplew.getPacket();
    }

    public static byte[] getServerList(final int serverId, String serverName, int flag, String eventMessage, List<ChannelServer> channelLoad) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SERVERLIST.getValue());
        mplew.write(serverId);
        mplew.write(0);
        mplew.writeMapleAsciiString(serverName);
        mplew.write(flag);
        mplew.writeMapleAsciiString(eventMessage);
        mplew.writeShort(0x64);
        mplew.writeShort(0x64);
        mplew.write(channelLoad.size());
        for (ChannelServer ch : channelLoad) {
            mplew.writeMapleAsciiString(serverName + "-" + ch.getChannel());
            mplew.writeInt((ch.getConnectedClients() * 1200) / ServerConstants.CHANNEL_LOAD);
            mplew.write(serverId);
            mplew.writeShort(ch.getChannel() - 1);
        }
        mplew.writeShort(ServerConstants.getBalloons().size());
        for (ServerConstants.MapleLoginBalloon balloon : ServerConstants.getBalloons()) {
            mplew.writeShort(balloon.nX);
            mplew.writeShort(balloon.nY);
            mplew.writeMapleAsciiString(balloon.sMessage);
        }
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] getEndOfServerList() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVERLIST.getValue());
        mplew.write(0xFF);
        mplew.write(0xFF);

        return mplew.getPacket();
    }

    public static byte[] sendEULA() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SEND_EULA.getValue());
        mplew.write(1);
        mplew.write(1);
        return mplew.getPacket();
    }

    public static byte[] getLoginWelcome() {
        List<Pair<String, Integer>> flags = new LinkedList<>();
        flags.add(new Pair<>("20120808", 0));
        flags.add(new Pair<>("20120815", 0));

        //flags.add(new Pair<>("20120111", 0));
        //flags.add(new Pair<>("returnLegend2", 0));
        return CField.spawnFlags(flags);
    }

    public static byte[] getServerStatus(final int status) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        /*	 
         * 0 - Normal
         * 1 - Highly populated
         * 2 - Full
        */
        mplew.writeShort(SendPacketOpcode.SERVERSTATUS.getValue());
        mplew.writeShort(status);

        return mplew.getPacket();
    }

    public static byte[] getChannelSelected() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHANNEL_SELECTED.getValue());
        mplew.writeZeroBytes(3);

        return mplew.getPacket();
    }

    public static byte[] getCharacterList(final boolean need2ndPassword, final List<MapleCharacter> chars, int charSlots) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHARLIST.getValue());
        mplew.write(0);
        mplew.writeInt(1000000);
        mplew.write(chars.size());
        chars.stream().forEach((chr) -> addCharEntry(mplew, chr, !chr.isGM() && chr.getLevel() >= 30, false));
        mplew.write(need2ndPassword ? 0 : 3); // second pw request
        mplew.write(0);
        mplew.writeInt(charSlots);
        mplew.writeInt(0);
        mplew.writeInt(-1);
        mplew.writeReversedLong(PacketHelper.getTime(System.currentTimeMillis()));
        return mplew.getPacket();
    }

    public static byte[] addNewCharEntry(final MapleCharacter chr, final boolean worked) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ADD_NEW_CHAR_ENTRY.getValue());
        mplew.write(worked ? 0 : 1);
        addCharEntry(mplew, chr, false, false);

        return mplew.getPacket();
    }

    public static byte[] charNameResponse(final String charname, final boolean nameUsed) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHAR_NAME_RESPONSE.getValue());
        mplew.writeMapleAsciiString(charname);
        mplew.write(nameUsed ? 1 : 0);

        return mplew.getPacket();
    }

    private static void addCharEntry(final MaplePacketLittleEndianWriter mplew, final MapleCharacter chr, boolean ranking, boolean viewAll) {
        PacketHelper.addCharStats(mplew, chr);
        PacketHelper.addCharLook(mplew, chr, true, chr.getClient());
        if (!viewAll) {
            mplew.write(0);
        }
        mplew.write(ranking ? 1 : 0);
        if (ranking) {
            mplew.writeInt(chr.getRank());
            mplew.writeInt(chr.getRankMove());
            mplew.writeInt(chr.getJobRank());
            mplew.writeInt(chr.getJobRankMove());
        }
    }

    public static byte[] showAllCharacter(int chars) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ALL_CHARLIST.getValue());
        mplew.write(1); //bIsChar
        mplew.writeInt(chars);
        mplew.writeInt(chars + (3 - chars % 3)); //rowsize
        return mplew.getPacket();
    }

    public static byte[] showAllCharacterInfo(int worldid, List<MapleCharacter> chars, String pic) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ALL_CHARLIST.getValue());
        mplew.write(chars.isEmpty() ? 5 : 0); //5 = cannot find any
        mplew.write(worldid);
        mplew.write(chars.size());
        for (MapleCharacter chr : chars) {
            addCharEntry(mplew, chr, true, true);
        }
        mplew.write(pic == null ? 0 : (pic.equals("") ? 2 : 1)); //writing 2 here disables PIC		
        return mplew.getPacket();
    }

    public static byte[] enableSpecialCreation(int accid, boolean enable) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPECIAL_CREATION.getValue());
        mplew.writeInt(accid);
        mplew.write(enable ? 0 : 1);
        mplew.write(0); // amount of legends created

        return mplew.getPacket();
    }

    /*     */
    public static byte[] partTimeJobRequest(int cid, int mode, int jobType, long time, boolean finish, boolean bonus) {
/* 466 */
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
/*     */ 
/* 468 */
        mplew.writeShort(SendPacketOpcode.PART_TIME_JOB.getValue());
/* 469 */
        mplew.writeInt(cid);
/* 470 */
        mplew.write(mode);
/*     */ 
/* 475 */
        mplew.write(jobType);
/* 476 */
        mplew.writeReversedLong(PacketHelper.getTime(time));
/* 477 */
        mplew.writeInt(finish ? 1 : 0);
/* 478 */
        mplew.write(bonus ? 1 : 0);
/*     */ 
/* 482 */
        return mplew.getPacket();
/*     */
    }
}
