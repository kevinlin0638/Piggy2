/*
 * This file is part of the OdinMS MapleStory Private Server
 * Copyright (C) 2012 Patrick Huy and Matthias Butz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package tools.packet;

import client.*;
import client.inventory.Equip.ScrollResult;
import client.inventory.*;
import client.skill.SkillMacro;
import constants.GameConstants;
import constants.ServerConstants;
import constants.SkillConstants;
import extensions.temporary.DirectionType;
import handling.SendPacketOpcode;
import handling.channel.handler.AttackInfo;
import handling.world.World;
import handling.world.guild.MapleGuild;
import scripting.NPCTalkType;
import server.MapleDueyActions;
import server.MapleShop;
import server.MapleTrade;
import server.Randomizer;
import server.life.MapleNPC;
import server.maps.*;
import server.maps.MapleNodes.MaplePlatform;
import server.movement.ILifeMovementFragment;
import server.quest.MapleQuest;
import server.worldevents.MapleSnowball.MapleSnowballs;
import tools.HexTool;
import tools.data.MaplePacketLittleEndianWriter;
import tools.types.AttackPair;
import tools.types.Pair;
import tools.types.Triple;

import java.awt.*;
import java.net.InetAddress;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

public class CField {

    public static byte[] getPacketFromHexString(final String hex) {
        return HexTool.getByteArrayFromHexString(hex);

    }

    public static byte[] getServerIP(MapleClient c, InetAddress inetAddr, int port, int clientId) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SERVER_IP.getValue());
        mplew.writeShort(0);
        byte[] address = inetAddr.getAddress();
        mplew.write(address);
        mplew.writeShort(port);
        mplew.writeInt(clientId);
        mplew.writeZeroBytes(5);
        return mplew.getPacket();
    }

    public static byte[] getChannelChange(MapleClient c, InetAddress inetAddr, int port) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CHANGE_CHANNEL.getValue());
        mplew.write(1);
        byte[] addr = inetAddr.getAddress();
        mplew.write(addr);
        mplew.writeShort(port);
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] gainCardStack(int oid, int runningId, int color, int skillid, int damage, int times) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GAIN_FORCE.getValue());
        mplew.write(0);
        mplew.writeInt(oid);
        mplew.writeInt(1);
        mplew.writeInt(damage);
        mplew.writeInt(skillid);
        for (int i = 0; i < times; i++) {
            mplew.write(1);
            mplew.writeInt(damage == 0 ? runningId + i : runningId);
            mplew.writeInt(color);
            mplew.writeInt(Randomizer.rand(15, 29));
            mplew.writeInt(Randomizer.rand(7, 11));
            mplew.writeInt(Randomizer.rand(0, 9));
        }
        mplew.write(0);
        return mplew.getPacket();
    }

    public static byte[] updateCardStack(int total) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_CARD_DECK.getValue());
        mplew.write(total);
        return mplew.getPacket();
    }

    public static byte[] getNPCTalk(int npc, byte msgType, String talk, String endBytes) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
        mplew.write(4);
        mplew.writeInt(npc);
        mplew.write(msgType);
        mplew.write(0);
        mplew.writeMapleAsciiString(talk);
        mplew.write(HexTool.getByteArrayFromHexString(endBytes));
        return mplew.getPacket();
    }

    public static byte[] playPortalSound() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(10);

        return mplew.getPacket();
    }

    public static byte[] getPVPType(final int type, List<Pair<Integer, String>> players1, final int team, boolean enabled, final int lvl) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_TYPE.getValue());
        mplew.write(type); //type is really a byte
        mplew.write(lvl); //0 = rookie, 1 = gladiator, etc
        mplew.write(enabled ? 1 : 0); //no idea
        mplew.write(0);
        if (type > 0) {
            mplew.write(team); //the team of the player
            mplew.writeInt(players1.size());
            for (Pair<Integer, String> pl : players1) {
                mplew.writeInt(pl.left);
                mplew.writeMapleAsciiString(pl.right);
                mplew.writeShort(2660); //?
            }
        }

        return mplew.getPacket();
    }

    public static byte[] getPVPTransform(int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_TRANSFORM.getValue());
        mplew.write(type); //2?

        return mplew.getPacket();
    }

    public static byte[] getPVPDetails(List<Pair<Integer, Integer>> players) { // cid, team
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_DETAILS.getValue());
        mplew.write(1); // 0 = guild, 1 = team, 2 = nothing,
        mplew.write(0); // ???
        mplew.writeInt(players.size());
        for (Pair<Integer, Integer> pl : players) {
            mplew.writeInt(pl.left);
            mplew.write(pl.right); // team (0 =none, 1 = red, 2 = blue)
        }

        return mplew.getPacket();
    }

    public static byte[] enablePVP(final boolean enabled) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_ENABLED.getValue());
        mplew.write(enabled ? 1 : 2);

        return mplew.getPacket();
    }

    public static byte[] getPVPScore(int score, boolean kill) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_SCORE.getValue());
        mplew.writeInt(score);
        mplew.write(kill ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] getPVPResult(List<Pair<Integer, MapleCharacter>> flags, int exp, int winningTeam, int playerTeam) { //Flag_R_1 to 0, etc
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_RESULT.getValue());
        mplew.writeInt(flags.size());
        for (Pair<Integer, MapleCharacter> f : flags) {
            mplew.writeInt(f.right.getId());
            mplew.writeMapleAsciiString(f.right.getName());
            mplew.writeInt(f.left);
            mplew.writeShort(f.right.getTeam() + 1); //??? 1 = bold
            mplew.writeInt(0); //??? delay 600
            mplew.writeInt(0); //???
        }
        mplew.writeZeroBytes(24); //BONUS: ___
        mplew.writeInt(exp);
        mplew.write(0);
        mplew.writeShort(100); //exp modifier
        mplew.writeInt(0); //delay 600
        mplew.writeInt(0); //???
        mplew.write(winningTeam); //losing team?
        mplew.write(playerTeam); //player's team

        return mplew.getPacket();
    }

    public static byte[] getPVPTeam(List<Pair<Integer, String>> players) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_TEAM.getValue());
        mplew.writeInt(players.size());
        for (Pair<Integer, String> pl : players) {
            mplew.writeInt(pl.left);
            mplew.writeMapleAsciiString(pl.right);
            mplew.writeShort(2660); //?
        }

        return mplew.getPacket();
    }

    public static byte[] getPVPScoreboard(List<Pair<Integer, MapleCharacter>> flags, int type) { //Flag_R_1 to 0, etc
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_SCOREBOARD.getValue());
        mplew.writeShort(flags.size());
        for (Pair<Integer, MapleCharacter> f : flags) {
            mplew.writeInt(f.right.getId());
            mplew.writeMapleAsciiString(f.right.getName());
            mplew.writeInt(f.left);
            mplew.write(type == 0 ? 0 : (f.right.getTeam() + 1));
        }

        return mplew.getPacket();
    }

    public static byte[] getPVPPoints(int p1, int p2) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_POINTS.getValue());
        mplew.writeInt(p1);
        mplew.writeInt(p2);

        return mplew.getPacket();
    }

    public static byte[] getPVPKilled(String lastWords) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_KILLED.getValue());
        mplew.writeMapleAsciiString(lastWords); //____ defeated ____.

        return mplew.getPacket();
    }

    public static byte[] getPVPMode(final int mode) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_MODE.getValue());
        mplew.write(mode); //11 = starting, 0 = started, 4 = ended??? 8 = blue team win???

        return mplew.getPacket();
    }

    public static byte[] getPVPIceHPBar(int hp, int maxHp) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_ICEKNIGHT.getValue());
        mplew.writeInt(hp);
        mplew.writeInt(maxHp);

        return mplew.getPacket();
    }

    public static byte[] getCaptureFlags(MapleMap map) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CAPTURE_FLAGS.getValue());
        mplew.writeRect(map.getArea(0));
        mplew.writeInt(map.getGuardians().get(0).left.x);
        mplew.writeInt(map.getGuardians().get(0).left.y);
        mplew.writeRect(map.getArea(1));
        mplew.writeInt(map.getGuardians().get(1).left.x);
        mplew.writeInt(map.getGuardians().get(1).left.y);

        return mplew.getPacket();
    }

    public static byte[] getCapturePosition(MapleMap map) { //position of flags if they are still at base
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        final Point p1 = map.getPointOfItem(2910000);
        final Point p2 = map.getPointOfItem(2910001);
        mplew.writeShort(SendPacketOpcode.CAPTURE_POSITION.getValue());
        mplew.write(p1 == null ? 0 : 1);
        if (p1 != null) {
            mplew.writeInt(p1.x);
            mplew.writeInt(p1.y);
        }
        mplew.write(p2 == null ? 0 : 1);
        if (p2 != null) {
            mplew.writeInt(p2.x);
            mplew.writeInt(p2.y);
        }

        return mplew.getPacket();
    }

    public static byte[] getMacros(SkillMacro[] macros) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SKILL_MACRO.getValue());
        int count = 0;
        for (int i = 0; i < 5; i++) {
            if (macros[i] != null) {
                count++;
            }
        }
        mplew.write(count); // number of macros
        for (int i = 0; i < 5; i++) {
            SkillMacro macro = macros[i];
            if (macro != null) {
                mplew.writeMapleAsciiString(macro.getName());
                mplew.write(macro.getShout());
                mplew.writeInt(macro.getSkill1());
                mplew.writeInt(macro.getSkill2());
                mplew.writeInt(macro.getSkill3());
            }
        }

        return mplew.getPacket();
    }

    public static byte[] getCharInfo(MapleCharacter chr) {
        return setField(chr, true, null, 0);
    }

    public static byte[] getWarpToMap(MapleMap to, int spawnPoint, MapleCharacter chr) {
        return setField(chr, false, to, spawnPoint);
    }

    public static byte[] spawnFlags(List<Pair<String, Integer>> flags) { //Flag_R_1 to 0, etc
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LOGIN_WELCOME.getValue());
        mplew.write(flags == null ? 0 : flags.size());
        if (flags != null) {
            for (Pair<String, Integer> f : flags) {
                mplew.writeMapleAsciiString(f.left);
                mplew.write(f.right);
            }
        }

        return mplew.getPacket();
    }

    /*public static byte[] resetCapture() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CAPTURE_RESET.getValue());

        return mplew.getPacket();
    }*/
    public static byte[] setField(MapleCharacter chr, boolean CharInfo, MapleMap to, int spawnPoint) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.WARP_TO_MAP.getValue());
        mplew.writeInt(chr.getClient().getChannel() - 1);
        mplew.write(0);
        mplew.writeInt(0);
        mplew.write(CharInfo ? 1 : 2);
        mplew.writeInt(0);
        mplew.write(CharInfo ? 1 : 0);
        int v104 = 0;
        mplew.writeShort(v104); // size :: v104
        if (v104 != 0) {
            mplew.writeMapleAsciiString("");
            for (int i = 0; i < v104; i++) {
                mplew.writeMapleAsciiString("");
            }
        }
        if (CharInfo) {
            chr.CRand().connectData(mplew); // [Int][Int][Int]
            PacketHelper.addCharacterInfo(mplew, chr);
            mplew.writeInt(0);
            for (int i = 0; i < 3; i++) {
                mplew.writeInt(0);
            }
        } else {
            mplew.write(0);
            mplew.writeInt(to.getId());
            mplew.write(spawnPoint);
            mplew.writeInt(chr.getStat().getHp());
            boolean v12 = false;
            mplew.write(v12 ? 1 : 0);
            if (v12) {
                mplew.writeInt(0);
                mplew.writeInt(0);
            }
        }
        mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        mplew.writeInt(100);
        mplew.writeBoolean(false);
        mplew.write(0);
        mplew.write(GameConstants.isSeparatedSp(chr.getJob()) ? 1 : 0); // v109
        return mplew.getPacket();
    }

    /**
     * Possible values for <code>type</code>:<br>
     * 1: You cannot move that channel. Please try again later.<br>
     * 2: You cannot go into the cash shop. Please try again later.<br>
     * 3: The Item-Trading shop is currently unavailable, please try again
     * later.<br>
     * 4: You cannot go into the trade shop, due to the limitation of user
     * count.<br>
     * 5: You do not meet the minimum level requirement to access the Trade
     * Shop.<br>
     */
    public static byte[] serverBlocked(int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVER_BLOCKED.getValue());
        mplew.write(type);

        return mplew.getPacket();
    }

    //9 = cannot join due to party, 1 = cannot join at this time, sry
    public static byte[] pvpBlocked(int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_ENABLED.getValue());
        mplew.write(type);

        return mplew.getPacket();
    }

    public static byte[] showEquipEffect() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_EQUIP_EFFECT.getValue());

        return mplew.getPacket();
    }

    public static byte[] showEquipEffect(int team) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_EQUIP_EFFECT.getValue());
        mplew.writeShort(team);

        return mplew.getPacket();
    }

    public static byte[] multiChat(String name, String chattext, int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MULTICHAT.getValue());
        mplew.write(mode); //  0 buddychat; 1 partychat; 2 guildchat
        mplew.writeMapleAsciiString(name);
        mplew.writeMapleAsciiString(chattext);

        return mplew.getPacket();
    }

    public static byte[] getFindReplyWithCS(String target, final boolean buddy) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(2);
        mplew.writeInt(-1);

        return mplew.getPacket();
    }

    public static byte[] getFindReplyWithMTS(String target, final boolean buddy) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(0);
        mplew.writeInt(-1);

        return mplew.getPacket();
    }

    public static byte[] getWhisper(String sender, int channel, String text) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(0x12);
        mplew.writeMapleAsciiString(sender);
        mplew.writeShort(channel - 1);
        mplew.writeMapleAsciiString(text);

        return mplew.getPacket();
    }

    public static byte[] getWhisperReply(String target, byte reply) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(0x0A); // whisper?
        mplew.writeMapleAsciiString(target);
        mplew.write(reply);//  0x0 = cannot find char, 0x1 = success

        return mplew.getPacket();
    }

    public static byte[] getFindReplyWithMap(String target, int mapid, final boolean buddy) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(1);
        mplew.writeInt(mapid);
        mplew.writeZeroBytes(8); // ?? official doesn't send zeros here but whatever

        return mplew.getPacket();
    }

    public static byte[] getFindReply(String target, int channel, final boolean buddy) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(buddy ? 72 : 9);
        mplew.writeMapleAsciiString(target);
        mplew.write(3);
        mplew.writeInt(channel - 1);

        return mplew.getPacket();
    }

    public static byte[] MapEff(final String path) {
        return environmentChange(path, 3);
    }

    public static byte[] MapNameDisplay(final int mapid) {
        return environmentChange("maplemap/enter/" + mapid, 3);
    }

    public static byte[] Aran_Start() {
        return environmentChange("Aran/balloon", 4);
    }

    public static byte[] musicChange(String song) {
        return environmentChange(song, 6);
    }

    public static byte[] showEffect(String effect) {
        return environmentChange(effect, 3);
    }

    public static byte[] playSound(String sound) {
        return environmentChange(sound, 4);
    }

    public static byte[] environmentChange(String env, int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOSS_ENV.getValue());
        mplew.write(mode);
        mplew.writeMapleAsciiString(env);

        return mplew.getPacket();
    }

    public static byte[] trembleEffect(int type, int delay) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOSS_ENV.getValue());
        mplew.write(1);
        mplew.write(type);
        mplew.writeInt(delay);

        return mplew.getPacket();
    }

    public static byte[] environmentMove(String env, int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_ENV.getValue());
        mplew.writeMapleAsciiString(env);
        mplew.writeInt(mode);

        return mplew.getPacket();
    }

    public static byte[] getUpdateEnvironment(final MapleMap map) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_ENV.getValue());
        mplew.writeInt(map.getEnvironment().size());
        for (Entry<String, Integer> mp : map.getEnvironment().entrySet()) {
            mplew.writeMapleAsciiString(mp.getKey());
            mplew.writeInt(mp.getValue());
        }

        return mplew.getPacket();
    }

    public static byte[] startMapEffect(String msg, int itemid, boolean active) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MAP_EFFECT.getValue());
        mplew.write(active ? 0 : 1);

        mplew.writeInt(itemid);
        if (active) {
            mplew.writeMapleAsciiString(msg);
        }
        return mplew.getPacket();
    }

    public static byte[] removeMapEffect() {
        return startMapEffect(null, 0, false);
    }

    public static byte[] GameMaster_Func(int value) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GM_EFFECT.getValue());
        mplew.write(value);
        mplew.writeZeroBytes(17);

        return mplew.getPacket();
    }

    public static byte[] showOXQuiz(int questionSet, int questionId, boolean askQuestion) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.OX_QUIZ.getValue());
        mplew.write(askQuestion ? 1 : 0);
        mplew.write(questionSet);
        mplew.writeShort(questionId);

        return mplew.getPacket();
    }

    public static byte[] showEventInstructions() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GMEVENT_INSTRUCTIONS.getValue());
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] getPVPClock(int type, int time) { // time in seconds
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
        mplew.write(3);
        mplew.write(type);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] getClock(int time) { // time in seconds
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
        mplew.write(2); // clock type. if you send 3 here you have to send another byte (which does not matter at all) before the timestamp
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] getClockTime(int hour, int min, int sec) { // Current Time
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CLOCK.getValue());
        mplew.write(1); //Clock-Type
        mplew.write(hour);
        mplew.write(min);
        mplew.write(sec);

        return mplew.getPacket();
    }

    public static byte[] boatPacket(int effect, int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOAT_MOVE.getValue());
        mplew.write(effect); // 8 = start, 10 = move, 12 = end
        mplew.write(mode);
        // Effect 8: 2 = ship go
        // Effect 10: 4 = appears, 5 = disappears
        // Effect 12: 6 = ship arrives

        return mplew.getPacket();
    }

    public static byte[] setBoatState(int effect) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOAT_STATE.getValue());
        mplew.write(effect); // 0/1/6: arrives | 3/4/2/5: go
        mplew.write(1);  // we put 1 here

        return mplew.getPacket();
    }

    public static byte[] stopClock() {
        return getPacketFromHexString(Integer.toHexString(SendPacketOpcode.STOP_CLOCK.getValue()) + " 00");
    }

    public static byte[] showAriantScoreBoard() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ARIANT_SCOREBOARD.getValue());
        return mplew.getPacket();
    }

    public static byte[] sendPyramidUpdate(final int amount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PYRAMID_UPDATE.getValue());
        mplew.writeInt(amount); //1-132 ?

        return mplew.getPacket();
    }

    public static byte[] sendPyramidResult(final byte rank, final int amount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PYRAMID_RESULT.getValue());
        mplew.write(rank);
        mplew.writeInt(amount); //1-132 ?

        return mplew.getPacket();
    }

    public static byte[] quickSlot(String skil) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.QUICK_SLOT.getValue());
        mplew.write(skil == null ? 0 : 1); // sending 0 here = request handler for quick slots
        if (skil != null) {
            final String[] slots = skil.split(",");
            for (int i = 0; i < 8; i++) {
                mplew.writeInt(Integer.parseInt(slots[i]));
            }
        }

        return mplew.getPacket();
    }

    public static byte[] getMovingPlatforms(final MapleMap map) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_PLATFORM.getValue());
        mplew.writeInt(map.getPlatforms().size());
        for (MaplePlatform mp : map.getPlatforms()) {
            mplew.writeMapleAsciiString(mp.name);
            mplew.writeInt(mp.start);
            mplew.writeInt(mp.SN.size());
            for (int x = 0; x < mp.SN.size(); x++) {
                mplew.writeInt(mp.SN.get(x));
            }
            mplew.writeInt(mp.speed);
            mplew.writeInt(mp.x1);
            mplew.writeInt(mp.x2);
            mplew.writeInt(mp.y1);
            mplew.writeInt(mp.y2);
            mplew.writeInt(mp.x1);//?
            mplew.writeInt(mp.y1);
            mplew.writeShort(mp.r);
        }

        return mplew.getPacket();
    }

    public static byte[] sendPyramidKills(final int amount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PYRAMID_KILL_COUNT.getValue());
        mplew.writeInt(amount);

        return mplew.getPacket();
    }

    public static byte[] getGMBoard(int uniqueNumber, String path) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GM_BOARD.getValue()); // 2A 00 01 00 00 00
        mplew.writeShort(uniqueNumber);
        mplew.writeShort(Randomizer.nextInt(Short.MAX_VALUE)); //these two shorts actually one int
        mplew.writeMapleAsciiString(path);
        return mplew.getPacket();
    }

    public static byte[] getMapleAdminMsg() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MAPLE_ADMIN_MSG.getValue()); // 2A 00 01 00 00 00
        mplew.writeMapleAsciiString("Test");
        mplew.writeInt(0);
        mplew.writeInt(0);
        return mplew.getPacket();
    }

    public static byte[] sendPVPMaps() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_INFO.getValue());
        mplew.write(3); //max amount of players
        for (int i = 0; i < 20; i++) {
            mplew.writeInt(10); //how many peoples in each map
        }
        mplew.writeZeroBytes(124);
        mplew.writeShort(150); ////PVP 1.5 EVENT!
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] gainForce(int oid, int count, int color) { // Check
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GAIN_FORCE.getValue());
        mplew.write(1); // 0 = remote user?
        mplew.writeInt(oid);
        byte newcheck = 0;
        mplew.writeInt(newcheck); //direction
        if (newcheck > 0) {
            mplew.writeInt(0); //direction
            mplew.writeInt(0); //direction
        }
        mplew.write(0);
        mplew.writeInt(4); // size, for each below
        mplew.writeInt(count); //count
        mplew.writeInt(color); //color, 1-10 for demon, 1-2 for phantom
        mplew.writeInt(0); //direction
        mplew.writeInt(0); //direction

        return mplew.getPacket();
    }

    public static byte[] achievementRatio(int amount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ACHIEVEMENT_RATIO.getValue());
        mplew.writeInt(amount);

        return mplew.getPacket();
    }

    public static byte[] getPublicNPCInfo() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
//[6/1/2012 9:04:20 AM] (55) <Inbound> == E5 00 07 00 00 B4 65 8A 00 00 00 00 00 1E 00 00 00 8B 00 4D 6F 76 65 20 74 6F 20 74 68 65 20 42 61 74 74 6C 65 20 4D 6F 64 65 20 7A 6F 6E 65 20 23 63 42 61 74 74 6C 65 20 53 71 75 61 72 65 23 2C 20 77 68 65 72 65 20 79 6F 75 20 63 61 6E 20 66 69 67 68 74 20 61 67 61 69 6E 73 74 20 6F 74 68 65 72 20 75 73 65 72 73 2E 0A 23 63 4C 76 2E 20 33 30 20 6F 72 20 61 62 6F 76 65 20 63 61 6E 20 70 61 72 74 69 63 69 70 61 74 65 20 69 6E 20 42 61 74 74 6C 65 20 53 71 75 61 72 65 2E 00 00 66 7B 89 00 02 00 00 00 0A 00 00 00 43 00 55 73 65 20 74 68 65 20 23 63 44 69 6D 65 6E 73 69 6F 6E 61 6C 20 4D 69 72 72 6F 72 23 20 74 6F 20 6D 6F 76 65 20 74 6F 20 61 20 76 61 72 69 65 74 79 20 6F 66 20 70 61 72 74 79 20 71 75 65 73 74 73 2E 00 00 9B 69 8A 00 01 00 00 00 14 00 00 00 A9 00 4D 6F 76 65 20 74 6F 20 74 68 65 20 70 61 72 74 79 20 7A 6F 6E 65 20 0A 23 63 4D 6F 6E 73 74 65 72 20 50 61 72 6B 23 2C 20 77 68 65 72 65 20 79 6F 75 20 63 61 6E 20 66 69 67 68 74 20 61 67 61 69 6E 73 74 20 73 74 72 6F 6E 67 20 6D 6F 6E 73 74 65 72 73 20 77 69 74 68 20 79 6F 75 72 20 70 61 72 74 79 20 6D 65 6D 62 65 72 73 2E 0A 23 63 4F 6E 6C 79 20 4C 76 2E 20 32 30 20 6F 72 20 61 62 6F 76 65 20 63 61 6E 20 70 61 72 74 69 63 69 70 61 74 65 20 69 6E 20 74 68 65 20 4D 6F 6E 73 74 65 72 20 50 61 72 6B 2E 00 00 96 54 89 00 05 00 00 00 00 00 00 00 49 00 4D 6F 76 65 20 74 6F 20 74 68 65 20 63 6C 6F 73 65 73 74 20 23 63 49 6E 74 65 72 63 6F 6E 74 69 6E 65 6E 74 61 6C 20 53 74 61 74 69 6F 6E 23 20 74 6F 20 79 6F 75 72 20 63 75 72 72 65 6E 74 20 6C 6F 63 61 74 69 6F 6E 2E 00 00 97 54 89 00 03 00 00 00 00 00 00 00 47 00 4D 6F 76 65 20 74 6F 20 74 68 65 20 23 63 46 72 65 65 20 4D 61 72 6B 65 74 23 2C 20 77 68 65 72 65 20 79 6F 75 20 63 61 6E 20 74 72 61 64 65 20 69 74 65 6D 73 20 77 69 74 68 20 6F 74 68 65 72 20 75 73 65 72 73 2E 00 00 98 54 89 00 04 00 00 00 1E 00 00 00 5D 00 4D 6F 76 65 20 74 6F 20 23 63 41 72 64 65 6E 74 6D 69 6C 6C 23 2C 20 74 68 65 20 74 6F 77 6E 20 6F 66 20 50 72 6F 66 65 73 73 69 6F 6E 73 2E 0A 23 63 4F 6E 6C 79 20 4C 76 2E 20 33 30 20 6F 72 20 61 62 6F 76 65 20 63 61 6E 20 6D 6F 76 65 20 74 6F 20 41 72 64 65 6E 74 6D 69 6C 6C 00 00 99 54 89 00 06 00 00 00 00 00 00 00 30 00 54 61 6B 65 20 74 68 65 20 23 63 54 61 78 69 23 20 74 6F 20 6D 6F 76 65 20 74 6F 20 6D 61 6A 6F 72 20 61 72 65 61 73 20 71 75 69 63 6B 6C 79 2E
        mplew.writeShort(SendPacketOpcode.PUBLIC_NPC.getValue());
        mplew.write(GameConstants.publicNpcIds.length);
        for (int i = 0; i < GameConstants.publicNpcIds.length; i++) {
            mplew.writeMapleAsciiString(""); //npc name?
            mplew.writeInt(GameConstants.publicNpcIds[i]);
            mplew.writeInt(i); // type
            mplew.writeInt(0); //level needed
            mplew.writeMapleAsciiString(GameConstants.publicNpcs[i]); // Description upon hovering
        }
        // 0: Ariant guy
        // 1: Monster Park Taxi
        // 2: Dimensional Mirror
        // 3: Free Market
        // 4: Ardentmill
        // 5: Boat
        // 6: Taxi
        // 7: Alcaster
        // 8: Purple color?

        return mplew.getPacket();
    }

    public static byte[] spawnPlayerMapobject(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_PLAYER.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(chr.getLevel());
        mplew.writeMapleAsciiString(chr.getName());

        MapleQuestStatus ultExplorer = chr.getQuestNoAdd(MapleQuest.getInstance(111111));
        if ((ultExplorer != null) && (ultExplorer.getCustomData() != null)) {
            mplew.writeMapleAsciiString(ultExplorer.getCustomData());
        } else {
            mplew.writeMapleAsciiString("");
        }

        final MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
        if (gs != null) {
            mplew.writeMapleAsciiString(gs.getName());
            mplew.writeShort(gs.getLogoBG());
            mplew.write(gs.getLogoBGColor());
            mplew.writeShort(gs.getLogo());
            mplew.write(gs.getLogoColor());
        } else {
            mplew.writeMapleAsciiString("");
            mplew.writeShort(0);
            mplew.write(0);
            mplew.writeShort(0);
            mplew.write(0);
        }

        PacketHelper.addSpawnPlayerBuffStatus(mplew, chr);

        mplew.writeShort(chr.getJob()); // m_nJobCode
        mplew.writeShort(0); // m_nSubJobCode
        PacketHelper.addCharLook(mplew, chr, true, chr.getClient());

        mplew.writeInt(0); // ????
        mplew.writeInt(0); // ????

        // sub_8EE492
        int v1 = 0;
        int v2 = 0;
        int v3 = 0;
        mplew.writeInt(v1);
        mplew.writeInt(v2);
        mplew.writeInt(v3);
        for (int i = 0; i < v3; i++) {
            mplew.writeInt(0);
        }
        // end

        mplew.writeInt(Math.min(250, chr.getInventory(MapleInventoryType.CASH).countById(5110000))); //max is like 100. but w/e
        mplew.writeInt(0);
        /**
         * 黃金雞特效 (42900000) *
         */
        mplew.writeInt(chr.getItemEffect());
        mplew.writeInt(0);
        /**
         * BlinkMonkeyEffect *
         */
        mplew.writeInt(0);
        /**
         * ActiveNickName *
         */
        mplew.writeInt(0);
        mplew.writeMapleAsciiString("");
        mplew.writeMapleAsciiString("");

        mplew.writeShort(0xFFFF);
        mplew.writeShort(0xFFFF);
        mplew.write(0);
        mplew.writeInt(GameConstants.getInventoryType(chr.getChair()) == MapleInventoryType.SETUP ? chr.getChair() : 0);
        mplew.writeInt(0); //new v143
        mplew.writePos(chr.getPosition());
        mplew.write(chr.getStance());
        mplew.writeShort(chr.getFH());
//
//        mplew.writeInt(Math.min(250, chr.getInventory(MapleInventoryType.CASH).countById(5110000)));
//        mplew.writeInt(chr.getItemEffect());
//        mplew.writeInt(0);
//        final MapleQuestStatus stat = chr.getQuestNoAdd(MapleQuest.getInstance(GameConstants.ITEM_TITLE));
//        mplew.writeInt(stat != null && stat.getCustomData() != null ? Integer.valueOf(stat.getCustomData()) : 0);
//        mplew.writeInt(chr.hasEquipped(1202089) || chr.hasEquipped(1202090) || chr.hasEquipped(1202091) ? 1 : 0); // this is a possible value for our totems. this is the ID for the statEffect from SetItemInfo in Effect.wz
//        mplew.writeInt(chr.hasEquipped(1202089) || chr.hasEquipped(1202090) || chr.hasEquipped(1202091) ? 1 : 0); // this is a possible value for our totems. this is the ID for the statEffect from SetItemInfo in Effect.wz
//        mplew.writeInt(GameConstants.getInventoryType(chr.getChair()) == MapleInventoryType.SETUP ? chr.getChair() : 0);
//        mplew.writeInt(chr.hasEquipped(1202089) || chr.hasEquipped(1202090) || chr.hasEquipped(1202091) ? 1 : 0); // this is a possible value for our totems. this is the ID for the statEffect from SetItemInfo in Effect.wz
//        mplew.writePos(chr.getTruePosition());
//        mplew.write(chr.getStance());

        // Pets
        for (int i = 0; i < 3; i++) { // 寵物
            MaplePet pet = chr.getSummonedPet(i);
            mplew.writeBool(pet != null);
            if (pet == null) {
                break;
            }
            mplew.writeInt(i);
            PetPacket.addPetInfo(mplew, chr, pet, false);
        }

//        mplew.write(0); // pets can smd :)
//        if (!chr.isHidden() && chr.getActivePets() > 0) {
//            chr.getPets().stream().filter(MaplePet::getSummoned).forEach(pet -> {
//                addPetInfo(mplew, pet, false);
//            });
//        }
        mplew.writeInt(chr.getMount().getLevel()); // mount lvl
        mplew.writeInt(chr.getMount().getExp()); // exp
        mplew.writeInt(chr.getMount().getFatigue()); // tiredness
        PacketHelper.addAnnounceBox(mplew, chr);

        boolean hashChalkBoard = chr.getChalkboard() != null && chr.getChalkboard().length() > 0;
        mplew.writeBool(hashChalkBoard);
        if (hashChalkBoard) {
            mplew.writeMapleAsciiString(chr.getChalkboard());
        }

        Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> rings = chr.getRings(false);
        addRingInfo(mplew, rings.getLeft());
        addRingInfo(mplew, rings.getMid());
        addMRingInfo(mplew, rings.getRight(), chr);

        int v65 = 0;
        mplew.write(v65);
        for (int o = 0; o < v65; o++) {
            mplew.writeInt(0);
        }

        int unk_mask = 0;
        mplew.write(chr.getStat().Berserk ? 1 : 0); //unk_mask
        if ((unk_mask & 1) != 0) {
        }
        if ((unk_mask & 2) != 0) {
        }
        if ((unk_mask & 8) != 0) {
            mplew.writeInt(0);
        }
        if ((unk_mask & 10) != 0) {
            mplew.writeInt(0);
        }
        if ((unk_mask & 20) != 0) {
            mplew.writeInt(0);
        }

        mplew.writeInt(chr.getMount().getItemId());//骑宠id
        return mplew.getPacket();
    }

    public static void addPetInfo(final MaplePacketLittleEndianWriter mplew, MaplePet pet, boolean showpet) {
        mplew.write(1);
        if (showpet) {
            mplew.write(0); //1?
        }
        mplew.writeInt(pet.getPetItemId());
        mplew.writeMapleAsciiString(pet.getName());
        mplew.writeLong(pet.getUniqueId());
        mplew.writeShort(pet.getPos().x);
        mplew.writeShort(pet.getPos().y - 20);
        mplew.write(pet.getStance());
        mplew.writeInt(pet.getFh());
    }

    public static byte[] removePlayerFromMap(int cid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REMOVE_PLAYER_FROM_MAP.getValue());
        mplew.writeInt(cid);

        return mplew.getPacket();
    }

    public static byte[] getChatText(int cidfrom, String text, boolean whiteBG, int show) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CHATTEXT.getValue());
        mplew.writeInt(cidfrom);
        mplew.write(whiteBG ? 1 : 0);
        mplew.writeMapleAsciiString(text);
        mplew.write(show);
        return mplew.getPacket();
    }

    public static byte[] getScrollEffect(int chr, ScrollResult scrollSuccess, boolean legendarySpirit, boolean whiteScroll, int scrollid, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_SCROLL_EFFECT.getValue());
        mplew.writeInt(chr);
        mplew.write(scrollSuccess == ScrollResult.CURSE ? 2 : (scrollSuccess == ScrollResult.SUCCESS ? 1 : 0)); // 0 = fail, 1 = success, 2 = boom, 3 = cannot be used on this item
        mplew.write(legendarySpirit ? 1 : 0);
        mplew.writeInt(scrollid); // scroll id // The perfect innocence scroll lights up, and its mysteries energies transfers to..<item id>
        mplew.writeInt(itemid); // item id
        mplew.writeInt(0); // some kind of id, definitely not white scroll
        mplew.write(0);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] showMagnifyingEffect(final int chr, final short pos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_MAGNIFYING_EFFECT.getValue());
        mplew.writeInt(chr);
        mplew.writeShort(pos);

        return mplew.getPacket();
    }

    public static byte[] showPotentialReset(final boolean fireworks, final int chr, final boolean success, final int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(fireworks ? SendPacketOpcode.SHOW_FIREWORKS_EFFECT.getValue() : SendPacketOpcode.SHOW_POTENTIAL_RESET.getValue());
        mplew.writeInt(chr);
        mplew.write(success ? 1 : 0);
        mplew.writeInt(itemid); // fireworks, Item/Cash/0506.img/%08d/statEffect/default

        return mplew.getPacket();
    }

    public static byte[] showNebuliteEffect(final int chr, final boolean success) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_NEBULITE_EFFECT.getValue());
        mplew.writeInt(chr);
        mplew.write(success ? 1 : 0);
        mplew.writeMapleAsciiString(success ? "Successfully mounted Nebulite." : "Failed to mount Nebulite.");

        return mplew.getPacket();
    }

    public static byte[] useNebuliteFusion(int cid, int itemId, boolean success) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_FUSION_EFFECT.getValue());
        mplew.writeInt(cid);
        mplew.write(success ? 1 : 0);
        mplew.writeInt(itemId);

        return mplew.getPacket();
    }

    public static byte[] pvpAttack(int cid, int playerLevel, int skill, int skillLevel, int speed, int mastery, int projectile, int attackCount, int chargeTime, int stance, int direction, int range, int linkSkill, int linkSkillLevel, boolean movementSkill, boolean pushTarget, boolean pullTarget, List<AttackPair> attack) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_ATTACK.getValue());
        mplew.writeInt(cid);
        mplew.write(playerLevel);
        mplew.writeInt(skill);
        mplew.write(skillLevel);
        mplew.writeInt(linkSkill != skill ? linkSkill : 0);
        mplew.write(linkSkillLevel != skillLevel ? linkSkillLevel : 0);
        mplew.write(direction);
        mplew.write(movementSkill ? 1 : 0);
        mplew.write(pushTarget ? 1 : 0);
        mplew.write(pullTarget ? 1 : 0); //afaik only chains of hell does chains
        mplew.write(0); //direction
        mplew.writeShort(stance); //display
        mplew.write(speed);
        mplew.write(mastery);
        mplew.writeInt(projectile);
        mplew.writeInt(chargeTime);
        mplew.writeInt(range);
        mplew.writeShort(attack.size());
        mplew.write(0);
        mplew.writeInt(0);
        mplew.write(attackCount);
        mplew.write(0); //idk: probably does something like immobilize target

        for (AttackPair p : attack) {
            mplew.writeInt(p.objectId);
            mplew.writeInt(0);
            mplew.writePos(p.point);
            mplew.write(0);
            mplew.writeInt(0);
            for (Pair<Integer, Boolean> atk : p.attack) {
                mplew.writeInt(atk.left);
                mplew.writeInt(0);
                mplew.write(atk.right ? 1 : 0);
                mplew.writeShort(0); //1 = no hit
            }
        }
        return mplew.getPacket();
    }

    public static byte[] getPVPMist(int cid, int mistSkill, int mistLevel, int damage) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_MIST.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(mistSkill);
        mplew.write(mistLevel);
        mplew.writeInt(damage);
        mplew.write(8); //skill delay
        mplew.writeInt(1000);

        return mplew.getPacket();
    }

    public static byte[] pvpCool(int cid, List<Integer> attack) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_COOL.getValue());
        mplew.writeInt(cid);
        mplew.write(attack.size());
        for (int b : attack) {
            mplew.writeInt(b);
        }
        return mplew.getPacket();
    }

    public static byte[] teslaTriangle(int cid, int sum1, int sum2, int sum3) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.TESLA_TRIANGLE.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(sum1);
        mplew.writeInt(sum2);
        mplew.writeInt(sum3);

        return mplew.getPacket();
    }

    public static byte[] followEffect(int initiator, int replier, Point toMap) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FOLLOW_EFFECT.getValue());
        mplew.writeInt(initiator);
        mplew.writeInt(replier);
        mplew.writeLong(0);
        if (replier == 0) { //cancel
            mplew.write(toMap == null ? 0 : 1); //1 -> x (int) y (int) to change map
            if (toMap != null) {
                mplew.writeInt(toMap.x);
                mplew.writeInt(toMap.y);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] showPQReward(int cid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_PQ_REWARD.getValue());
        mplew.writeInt(cid);
        for (int i = 0; i < 6; i++) { // 6 loops
            mplew.write(0);
        }

        return mplew.getPacket();
    }

    public static byte[] craftMake(int cid, int something, int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CRAFT_EFFECT.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(something);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] craftFinished(int cid, int craftID, int ranking, int itemId, int quantity, int exp) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CRAFT_COMPLETE.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(craftID);
        mplew.writeInt(ranking);
        mplew.writeInt(itemId);
        mplew.writeInt(quantity);
        mplew.writeInt(exp);

        return mplew.getPacket();
    }

    public static byte[] harvestResult(int cid, boolean success) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.HARVESTED.getValue());
        mplew.writeInt(cid);
        mplew.write(success ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] playerDamaged(int cid, int dmg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PLAYER_DAMAGED.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(dmg);

        return mplew.getPacket();
    }

    public static byte[] showPyramidEffect(final int chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.NETT_PYRAMID.getValue());
        mplew.writeInt(chr);
        mplew.write(1); // size, for each 2 int
        mplew.writeInt(0);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] pamsSongEffect(int cid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PAMS_SONG.getValue());
        mplew.writeInt(cid);

        return mplew.getPacket();
    }

    public static byte[] spawnDragon(MapleDragon d) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DRAGON_SPAWN.getValue());
        mplew.writeInt(d.getOwner());
        mplew.writeInt(d.getPosition().x);
        mplew.writeInt(d.getPosition().y);
        mplew.write(d.getStance()); //stance?
        mplew.writeShort(0);
        mplew.writeShort(d.getJobId());

        return mplew.getPacket();
    }

    public static byte[] removeDragon(int chrid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DRAGON_REMOVE.getValue());
        mplew.writeInt(chrid);

        return mplew.getPacket();
    }

    public static byte[] moveDragon(MapleDragon d, Point startPos, List<ILifeMovementFragment> moves) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DRAGON_MOVE.getValue());
        mplew.writeInt(d.getOwner());
        mplew.writePos(startPos);
        mplew.writeInt(0);
        PacketHelper.serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static byte[] spawnAndroid(MapleCharacter cid, MapleAndroid android) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ANDROID_SPAWN.getValue());
        mplew.writeInt(cid.getId());
        mplew.write(android.getItemId() == 1662006 ? 5 : (android.getItemId() - 1661999)); //type of android, 1-4, 5 = princessoid
        mplew.writePos(android.getPos());
        mplew.write(android.getStance());
        mplew.writeShort(0); // FH, fix the dropping of andriods upon changing map
        mplew.writeShort(android.getSkin()); // andriod skin
        mplew.writeShort(android.getHair() - 30000);
        mplew.writeShort(android.getFace() - 20000);
        mplew.writeMapleAsciiString(android.getName());
        for (short i = -1200; i > -1207; i--) {
            final Item item = cid.getInventory(MapleInventoryType.EQUIPPED).getItem(i);
            mplew.writeInt(item != null ? item.getItemId() : 0);
        }

        return mplew.getPacket();
    }

    public static byte[] moveAndroid(int cid, Point pos, List<ILifeMovementFragment> res) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ANDROID_MOVE.getValue());
        mplew.writeInt(cid);
        mplew.writePos(pos);
        mplew.writeInt(Integer.MAX_VALUE); //time left in milliseconds? this appears to go down...slowly 1377440900
        PacketHelper.serializeMovementList(mplew, res);

        return mplew.getPacket();
    }

    public static byte[] showAndroidEmotion(int cid, int animation) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ANDROID_EMOTION.getValue());
        mplew.writeInt(cid);
        mplew.write(0);
        mplew.write(animation); //1234567 = default smiles, 8 = throwing up, 11 = kiss, 14 = googly eyes, 17 = wink...

        return mplew.getPacket();
    }

    public static byte[] updateAndroidLook(boolean itemOnly, MapleCharacter cid, MapleAndroid android) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ANDROID_UPDATE.getValue());
        mplew.writeInt(cid.getId());
        mplew.write(itemOnly ? 1 : 0);
        if (itemOnly) {
            for (short i = -1200; i > -1207; i--) {
                final Item item = cid.getInventory(MapleInventoryType.EQUIPPED).getItem(i);
                mplew.writeInt(item != null ? item.getItemId() : 0); // cash item
            }
        } else {
            mplew.writeShort(android.getSkin()); // skin
            mplew.writeShort(android.getHair() - 30000);
            mplew.writeShort(android.getFace() - 20000);
            mplew.writeMapleAsciiString(android.getName());
        }

        return mplew.getPacket();
    }

    public static byte[] deactivateAndroid(int cid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ANDROID_DEACTIVATED.getValue());
        mplew.writeInt(cid);

        return mplew.getPacket();
    }

    public static byte[] removeFamiliar(int cid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_FAMILIAR.getValue());
        mplew.writeInt(cid);
        mplew.writeShort(0);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] spawnFamiliar(MonsterFamiliar mf, boolean spawn) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_FAMILIAR.getValue());
        mplew.writeInt(mf.getCharacterId());
        mplew.writeShort(spawn ? 1 : 0);
        mplew.write(0);
        if (spawn) {
            mplew.writeInt(mf.getFamiliar());
            mplew.writeInt(mf.getFatigue());
            mplew.writeInt(mf.getVitality() * 300); //max fatigue
            mplew.writeMapleAsciiString(mf.getName());
            mplew.writePos(mf.getTruePosition());
            mplew.write(mf.getStance());
            mplew.writeShort(mf.getFh());
        }

        return mplew.getPacket();
    }

    public static byte[] moveFamiliar(int cid, Point startPos, List<ILifeMovementFragment> moves) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_FAMILIAR.getValue());
        mplew.writeInt(cid);
        mplew.write(0); // familiar id or something..
        mplew.writePos(startPos);
        mplew.writeInt(0);
        PacketHelper.serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static byte[] touchFamiliar(int cid, byte unk, int objectid, int type, int delay, int damage) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.TOUCH_FAMILIAR.getValue());
        mplew.writeInt(cid);
        mplew.write(0); // familiar id?
        mplew.write(unk);
        mplew.writeInt(objectid);
        mplew.writeInt(type);
        mplew.writeInt(delay);
        mplew.writeInt(damage);

        return mplew.getPacket();
    }

    public static byte[] familiarAttack(int cid, byte unk, List<Triple<Integer, Integer, List<Integer>>> attackPair) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ATTACK_FAMILIAR.getValue());
        mplew.writeInt(cid);
        mplew.write(0);// familiar id?
        mplew.write(unk);
        mplew.write(attackPair.size());
        for (Triple<Integer, Integer, List<Integer>> s : attackPair) {
            mplew.writeInt(s.left);
            mplew.write(s.mid);
            mplew.write(s.right.size());
            for (int damage : s.right) {
                mplew.writeInt(damage);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] renameFamiliar(MonsterFamiliar mf) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.RENAME_FAMILIAR.getValue());
        mplew.writeInt(mf.getCharacterId());
        mplew.write(0);// familiar id?
        mplew.writeInt(mf.getFamiliar());
        mplew.writeMapleAsciiString(mf.getName());

        return mplew.getPacket();
    }

    public static byte[] updateFamiliar(MonsterFamiliar mf) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_FAMILIAR.getValue());
        mplew.writeInt(mf.getCharacterId());
        mplew.writeInt(mf.getFamiliar());
        mplew.writeInt(mf.getFatigue());
        mplew.writeLong(PacketHelper.getTime(mf.getVitality() >= 3 ? System.currentTimeMillis() : -2));

        return mplew.getPacket();
    }

    public static byte[] movePlayer(int cid, List<ILifeMovementFragment> moves, Point startPos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_PLAYER.getValue());
        mplew.writeInt(cid);
        mplew.writePos(startPos);
        mplew.writeInt(0);
        PacketHelper.serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static byte[] closeRangeAttack(MapleCharacter chr, int skilllevel, int itemId, AttackInfo attackInfo) {
        return addAttackInfo(SendPacketOpcode.LP_UserMeleeAttack, chr, skilllevel, itemId, attackInfo);
    }

    public static byte[] rangedAttack(MapleCharacter chr, int skilllevel, int itemId, AttackInfo attackInfo) {
        return addAttackInfo(SendPacketOpcode.LP_UserShootAttack, chr, skilllevel, itemId, attackInfo);
    }

    public static byte[] magicAttack(MapleCharacter chr, int skilllevel, int itemId, AttackInfo attackInfo) {
        return addAttackInfo(SendPacketOpcode.LP_UserMagicAttack, chr, skilllevel, itemId, attackInfo);
    }

    public static byte[] passiveAttack(MapleCharacter chr, int skilllevel, int itemId, AttackInfo attackInfo) {
        return addAttackInfo(SendPacketOpcode.LP_UserBodyAttack, chr, skilllevel, itemId, attackInfo);
    }

    public static byte[] addAttackInfo(SendPacketOpcode op, MapleCharacter chr, int skilllevel, int itemId, AttackInfo attackInfo) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(op.getValue());
        mplew.writeInt(chr.getId());
        addAttackBody(mplew, op, chr, skilllevel, itemId, attackInfo);
        return mplew.getPacket();
    }

    public static void addAttackBody(MaplePacketLittleEndianWriter mplew, SendPacketOpcode op, MapleCharacter chr, int skilllevel, int itemId, AttackInfo attackInfo) {
        mplew.write(attackInfo.tbyte);
        mplew.write(chr.getLevel());
        skilllevel = attackInfo.skill > 0 ? skilllevel : 0;
        mplew.write(skilllevel);
        if (skilllevel > 0) {
            mplew.writeInt(attackInfo.skill);
        }

        if (attackInfo.skill == 3211006) {
            boolean v6 = false;
            if (v6) {
                int v7 = 0;
                mplew.writeInt(v7);

            }
        }

        mplew.write(attackInfo.direction);
        mplew.writeShort(attackInfo.display);
        int nAction = attackInfo.display & 0x7FFF;
//        if (nAction < 0x19D) {
            mplew.write(attackInfo.speed);
            mplew.write(chr.getStat().passive_mastery());
            mplew.writeInt(itemId > 0 ? itemId : attackInfo.charge);
            attackInfo.allDamage.stream().forEach(oned -> {
                mplew.writeInt(oned.objectId);
                if (oned.objectId > 0) {
                    mplew.write(7);
                    if (attackInfo.skill == 4211006) {
                        mplew.write(oned.attack.size());
                        for (Pair eachd : oned.attack) {
                            mplew.writeInt((Integer) eachd.left);
                        }
                    } else {
                        for (Pair<Integer, Boolean> eachd : oned.attack) {
                            //mplew.write(!eachd.right ? 0 : 1);
                            if (eachd.right) {
                                mplew.writeInt(eachd.left + 0x80000000);
                            } else {
                                mplew.writeInt(eachd.left);
                            }
                        }
                    }
                }
            });

            if (nAction >= 0x23) {//v145 [遠攻座標]
                mplew.writePos(attackInfo.position);
            }

            if (SkillConstants.isKeyDownSkillWithPos(attackInfo.skill)) {
                mplew.writePos(attackInfo.position);
            } else if (attackInfo.skill == 33101007) {
                mplew.writeInt(0);
            }

//        }
    }

    public static byte[] skillEffect(MapleCharacter from, int skillId, byte level, short display, byte unk) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SKILL_EFFECT.getValue());
        mplew.writeInt(from.getId());
        mplew.writeInt(skillId);
        mplew.write(level);
        mplew.writeShort(display);
        mplew.write(unk);

        return mplew.getPacket();
    }

    public static byte[] skillCancel(MapleCharacter from, int skillId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CANCEL_SKILL_EFFECT.getValue());
        mplew.writeInt(from.getId());
        mplew.writeInt(skillId);

        return mplew.getPacket();
    }

    public static byte[] damagePlayer(int cid, int type, int damage, int monsteridfrom, byte direction,
            int skillid, int pDMG, boolean pPhysical, int pID, byte pType, Point pPos, byte offset, int offset_d, int fake) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DAMAGE_PLAYER.getValue());
        mplew.writeInt(cid);
        mplew.write(type);
        mplew.writeInt(damage);
        mplew.write(0);
        if (type >= -1) {
            mplew.writeInt(monsteridfrom);
            mplew.write(direction);
            mplew.writeInt(skillid);
            mplew.writeInt(pDMG);
            mplew.write(0); // ?
            if (pDMG > 0) {
                mplew.write(pPhysical ? 1 : 0);
                mplew.writeInt(pID);
                mplew.write(pType);
                mplew.writePos(pPos);
            }
            mplew.write(offset);
            if (offset == 1) {
                mplew.writeInt(offset_d);
            }
        }
        mplew.writeInt(damage);
        if (damage <= 0 || fake > 0) { // supposed to be -1
            mplew.writeInt(fake);
        }
        mplew.writeZeroBytes(30);
        return mplew.getPacket();
    }

    public static byte[] facialExpression(MapleCharacter from, int expression) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FACIAL_EXPRESSION.getValue());
        mplew.writeInt(from.getId());
        mplew.writeInt(expression);
        mplew.writeInt(-1); //itemid of expression use
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] itemEffect(int characterid, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_EFFECT.getValue());

        mplew.writeInt(characterid);
        mplew.writeInt(itemid);

        //mplew.writeInt(0); // v83
        return mplew.getPacket();
    }

    public static byte[] showTitle(int characterid, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_TITLE.getValue());
        mplew.writeInt(characterid);
        mplew.writeInt(itemid);

        return mplew.getPacket();
    }

    public static byte[] showChair(int characterid, int itemid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_CHAIR.getValue());
        mplew.writeInt(characterid);
        mplew.writeInt(itemid);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] updateCharLook(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_CHAR_LOOK.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(1);
        PacketHelper.addCharLook(mplew, chr, false, chr.getClient());
        Triple<List<MapleRing>, List<MapleRing>, List<MapleRing>> rings = chr.getRings(false);
        addRingInfo(mplew, rings.getLeft());
        addRingInfo(mplew, rings.getMid());
        addMRingInfo(mplew, rings.getRight(), chr);
        mplew.writeInt(0); // -> charid to follow (4)
        return mplew.getPacket();
    }

    public static byte[] updatePartyMemberHP(int cid, int curhp, int maxhp) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_PARTYMEMBER_HP.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(curhp);
        mplew.writeInt(maxhp);

        return mplew.getPacket();
    }

    public static byte[] loadGuildName(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.LOAD_GUILD_NAME.getValue());
        mplew.writeInt(chr.getId());
        if (chr.getGuildId() <= 0) {
            mplew.writeShort(0);
        } else {
            final MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
            if (gs != null) {
                mplew.writeMapleAsciiString(gs.getName());
            } else {
                mplew.writeShort(0);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] loadGuildIcon(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LOAD_GUILD_ICON.getValue());
        mplew.writeInt(chr.getId());
        if (chr.getGuildId() <= 0) {
            mplew.writeZeroBytes(6);
        } else {
            final MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
            if (gs != null) {
                mplew.writeShort(gs.getLogoBG());
                mplew.write(gs.getLogoBGColor());
                mplew.writeShort(gs.getLogo());
                mplew.write(gs.getLogoColor());
            } else {
                mplew.writeZeroBytes(6);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] changeTeam(int cid, int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LOAD_TEAM.getValue());
        mplew.writeInt(cid);
        mplew.write(type); //2?

        return mplew.getPacket();
    }

    public static byte[] showHarvesting(int cid, int tool) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_HARVEST.getValue());
        mplew.writeInt(cid);
        if (tool > 0) {
            mplew.writeInt(1); // update time
            mplew.writeInt(tool);
        } else {
            mplew.writeInt(0);
        }
        mplew.writeZeroBytes(30);

        return mplew.getPacket();
    }

    public static byte[] getPVPHPBar(int cid, int hp, int maxHp) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_HP.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(hp);
        mplew.writeInt(maxHp);

        return mplew.getPacket();
    }

    public static byte[] cancelChair(int id) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CANCEL_CHAIR.getValue());
        if (id == -1) {
            mplew.write(0);
        } else {
            mplew.write(1);
            mplew.writeShort(id);
        }

        return mplew.getPacket();
    }

    public static byte[] instantMapWarp(final byte portal) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CURRENT_MAP_WARP.getValue());
        mplew.write(0); // tick
        mplew.write(portal);

        return mplew.getPacket();
    }

    public static byte[] updateQuestInfo(MapleCharacter c, int quest, int npc, byte progress) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_QUEST_INFO.getValue());
        mplew.write(progress);
        mplew.writeShort(quest);
        mplew.writeInt(npc);
        mplew.writeInt(0);

        return mplew.getPacket();
    }

    public static byte[] updateQuestFinish(int quest, int npc, int nextquest) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_QUEST_INFO.getValue());
        mplew.write(10);
        mplew.writeShort(quest);
        mplew.writeInt(npc);
        mplew.writeInt(nextquest);

        return mplew.getPacket();
    }

    public static byte[] sendHint(String hint, int width, int height) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PLAYER_HINT.getValue());
        mplew.writeMapleAsciiString(hint);
        mplew.writeShort(width < 1 ? Math.max(hint.length() * 10, 40) : width);
        mplew.writeShort(Math.max(height, 5));
        mplew.write(1); // if this is 0, 2 more int

        return mplew.getPacket();
    }

    public static byte[] testCombo(int value) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ARAN_COMBO.getValue());
        mplew.writeInt(value);

        return mplew.getPacket();
    }

    public static byte[] rechargeCombo(int value) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ARAN_COMBO_RECHARGE.getValue());
        mplew.writeInt(value);

        return mplew.getPacket();
    }

    public static byte[] getFollowMessage(final String msg) {
        return getGameMessage(msg, true);
    }

    public static byte[] getGameMessage(String msg, boolean white) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        // 0: Normal Chat
        // 1: Whisper
        // 2: Party
        // 3: Buddy
        // 4: Guild
        // 5: Alliance
        // 6: Spouse [Dark Red]
        // 7: Grey
        // 8: Yellow
        // 9: Light Yellow
        // 10: Blue
        // 11: White
        // 12: Red
        // 13: Light Blue
        mplew.writeShort(SendPacketOpcode.GAME_MESSAGE.getValue());
        mplew.writeShort(white ? 11 : 6);
        mplew.writeMapleAsciiString(msg);
        return mplew.getPacket();
    }

    // @Eric
    public static byte[] getGamePatches() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.GAME_PATCHES.getValue());
        return mplew.getPacket();
    }

    public static byte[] getBuffZoneEffect(final int itemId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BUFF_ZONE_EFFECT.getValue());
        mplew.writeInt(itemId); // Get from the "spec" in wz

        return mplew.getPacket();
    }

    public static byte[] getTimeBombAttack() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.TIME_BOMB_ATTACK.getValue());
        mplew.writeInt(0); // ??
        mplew.writeInt(0); // ??
        mplew.writeInt(0); // ?
        mplew.writeInt(10); // Distance thrown away
        mplew.writeInt(6); // Damage received

        return mplew.getPacket();
    }

    public static byte[] moveFollow(Point otherStart, Point myStart, Point otherEnd, List<ILifeMovementFragment> moves) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FOLLOW_MOVE.getValue());
        mplew.writePos(otherStart);
        mplew.writePos(myStart);
        PacketHelper.serializeMovementList(mplew, moves);
        mplew.write(0x11); //what? could relate to movePlayer
        for (int i = 0; i < 8; i++) {
            mplew.write(0); //?? sometimes 0x44 sometimes 0x88 sometimes 0x4.. etc.. buffstat or what
        }
        mplew.write(0); //?
        mplew.writePos(otherEnd);
        mplew.writePos(otherStart);
        mplew.writeLong(0);
        return mplew.getPacket();
    }

    public static byte[] getFollowMsg(int opcode) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FOLLOW_MSG.getValue());
        mplew.writeLong(opcode); //5 = canceled request.

        return mplew.getPacket();
    }

    public static byte[] registerFamiliar(MonsterFamiliar mf) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REGISTER_FAMILIAR.getValue());
        mplew.writeLong(mf.getId());
        mf.writeRegisterPacket(mplew, false);
        mplew.writeShort(mf.getVitality() >= 3 ? 1 : 0);

        return mplew.getPacket();
    }

    // @Eric
    public static byte[] resetMiniMap(boolean reset) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.RESET_MINIMAP.getValue());

        mplew.write(reset ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] createUltimate(int amount) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CREATE_ULTIMATE.getValue());
        mplew.writeInt(amount);

        return mplew.getPacket();
    }

    public static byte[] harvestMessage(int oid, int msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.HARVEST_MESSAGE.getValue());
        mplew.writeInt(oid);
        mplew.writeInt(msg);

        return mplew.getPacket();
    }

    public static byte[] openBag(int index, int itemId, boolean firstTime) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.OPEN_BAG.getValue());
        mplew.writeInt(index);
        mplew.writeInt(itemId);
        mplew.writeShort(firstTime ? 1 : 0); //this might actually be 2 bytes

        return mplew.getPacket();
    }

    public static byte[] dragonBlink(int portalId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DRAGON_BLINK.getValue());
        mplew.write(portalId);

        return mplew.getPacket();
    }

    public static byte[] getPVPIceGage(int score) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PVP_ICEGAGE.getValue());
        mplew.writeInt(score);

        return mplew.getPacket();
    }

    public static byte[] skillCooldown(int sid, int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.COOLDOWN.getValue());
        mplew.writeInt(sid);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] dropItemFromMapObject(MapleMapItem drop, Point dropfrom, Point dropto, byte mod) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DROP_ITEM_FROM_MAPOBJECT.getValue());
        mplew.write(mod); // 1 animation, 2 no animation, 3 spawn disappearing item [Fade], 4 spawn disappearing item
        mplew.writeInt(drop.getObjectId()); // item owner id
        mplew.write(drop.getMeso() > 0 ? 1 : 0); // 1 mesos, 0 item, 2 and above all item meso bag,
        mplew.writeInt(drop.getItemId()); // drop object ID
        mplew.writeInt(drop.getOwner()); // owner charid
        mplew.write(drop.getDropType()); // 0 = timeout for non-owner, 1 = timeout for non-owner's party, 2 = FFA, 3 = explosive/FFA
        mplew.writePos(dropto);
        mplew.writeInt(0);
        if (mod != 2) {
            mplew.writePos(dropfrom);
            mplew.writeShort(0);
        }
        if (drop.getMeso() == 0) {
            PacketHelper.addExpirationTime(mplew, drop.getItem().getExpiration());
        }
        mplew.writeShort(drop.isPlayerDrop() ? 0 : 1); // pet EQP pickup

        return mplew.getPacket();
    }

    public static byte[] explodeDrop(int oid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REMOVE_ITEM_FROM_MAP.getValue());
        mplew.write(4); // 4 = Explode
        mplew.writeInt(oid);
        mplew.writeShort(655);
        mplew.writeZeroBytes(50);
        return mplew.getPacket();
    }

    public static byte[] removeItemFromMap(int oid, int animation, int cid) {
        return removeItemFromMap(oid, animation, cid, 0);
    }

    public static byte[] removeItemFromMap(int oid, int animation, int cid, int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REMOVE_ITEM_FROM_MAP.getValue());
        mplew.write(animation); // 0 = Expire, 1 = without animation, 2 = pickup, 4 = explode, 5 = pet pickup
        mplew.writeInt(oid);
        if (animation >= 2) {
            mplew.writeInt(cid);
            if (animation == 5) { // allow pet pickup?
                mplew.writeInt(slot);
            }
        }
        mplew.writeZeroBytes(50);
        return mplew.getPacket();
    }

    public static byte[] spawnMist(final MapleMist mist) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_MIST.getValue());
        mplew.writeInt(mist.getObjectId());
        mplew.writeInt(mist.isMobMist() ? 0 : mist.isPoisonMist()); //2 = invincible, so put 1 for recovery aura
        mplew.writeInt(mist.getOwnerId());
        if (mist.getMobSkill() == null) {
            mplew.writeInt(mist.getSourceSkill().getId());
        } else {
            mplew.writeInt(mist.getMobSkill().getSkillId());
        }
        mplew.write(mist.getSkillLevel());
        mplew.writeShort(mist.getSkillDelay());
        mplew.writeRect(mist.getBox());
        mplew.writeLong(0);
        mplew.writeInt(0); //dunno
        mplew.writeZeroBytes(50);
        return mplew.getPacket();
    }

    public static byte[] removeMist(final int oid, final boolean eruption) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REMOVE_MIST.getValue());
        mplew.writeInt(oid);
        mplew.write(eruption ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] spawnDoor(final int oid, final Point pos, final boolean animation) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_DOOR.getValue());
        mplew.write(animation ? 0 : 1);
        mplew.writeInt(oid);
        mplew.writePos(pos);

        return mplew.getPacket();
    }

    public static byte[] removeDoor(int oid, boolean animation) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.REMOVE_DOOR.getValue());
        mplew.write(animation ? 0 : 1);
        mplew.writeInt(oid);

        return mplew.getPacket();
    }

    public static byte[] spawnMechDoor(MechDoor md, boolean animated) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MECH_DOOR_SPAWN.getValue());
        mplew.write(animated ? 0 : 1);
        mplew.writeInt(md.getOwnerId());
        mplew.writePos(md.getTruePosition());
        mplew.write(md.getId());
        mplew.writeInt(md.getPartyId());
        return mplew.getPacket();
    }

    public static byte[] removeMechDoor(MechDoor md, boolean animated) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MECH_DOOR_REMOVE.getValue());
        mplew.write(animated ? 0 : 1);
        mplew.writeInt(md.getOwnerId());
        mplew.write(md.getId());

        return mplew.getPacket();
    }

    public static byte[] triggerReactor(MapleReactor reactor, int stance) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REACTOR_HIT.getValue());
        mplew.writeInt(reactor.getObjectId());
        mplew.write(reactor.getState());
        mplew.writePos(reactor.getTruePosition());
        mplew.writeInt(stance);
        return mplew.getPacket();
    }

    public static byte[] spawnReactor(MapleReactor reactor) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REACTOR_SPAWN.getValue());
        mplew.writeInt(reactor.getObjectId());
        mplew.writeInt(reactor.getReactorId());
        mplew.write(reactor.getState());
        mplew.writePos(reactor.getTruePosition());
        mplew.write(reactor.getFacingDirection()); // stance
        mplew.writeMapleAsciiString(reactor.getName());

        return mplew.getPacket();
    }

    public static byte[] destroyReactor(MapleReactor reactor) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REACTOR_DESTROY.getValue());
        mplew.writeInt(reactor.getObjectId());
        mplew.write(reactor.getState());
        mplew.writePos(reactor.getPosition());

        return mplew.getPacket();
    }

    public static byte[] makeExtractor(int cid, String cname, Point pos, int timeLeft, int itemId, int fee) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_EXTRACTOR.getValue());
        mplew.writeInt(cid);
        mplew.writeMapleAsciiString(cname);
        mplew.writeInt(pos.x);
        mplew.writeInt(pos.y);
        mplew.writeShort(timeLeft); //fh or time left, dunno
        mplew.writeInt(itemId); //3049000, 3049001...
        mplew.writeInt(fee);

        return mplew.getPacket();
    }

    public static byte[] removeExtractor(int cid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.REMOVE_EXTRACTOR.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(1); //probably 1 = animation, 2 = make something?

        return mplew.getPacket();
    }

    public static byte[] rollSnowball(int type, MapleSnowballs ball1, MapleSnowballs ball2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ROLL_SNOWBALL.getValue());
        mplew.write(type); // 0 = normal, 1 = rolls from start to end, 2 = down disappear, 3 = up disappear, 4 = move
        mplew.writeInt(ball1 == null ? 0 : (ball1.getSnowmanHP() / 75));
        mplew.writeInt(ball2 == null ? 0 : (ball2.getSnowmanHP() / 75));
        mplew.writeShort(ball1 == null ? 0 : ball1.getPosition());
        mplew.write(0);
        mplew.writeShort(ball2 == null ? 0 : ball2.getPosition());
        mplew.writeZeroBytes(11);

        return mplew.getPacket();
    }

    public static byte[] enterSnowBall() {
        return rollSnowball(0, null, null);
    }

    public static byte[] hitSnowBall(int team, int damage, int distance, int delay) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.HIT_SNOWBALL.getValue());
        mplew.write(team);// 0 is down, 1 is up
        mplew.writeShort(damage);
        mplew.write(distance);
        mplew.write(delay);

        return mplew.getPacket();
    }

    public static byte[] snowballMessage(int team, int message) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SNOWBALL_MESSAGE.getValue());
        mplew.write(team);// 0 is down, 1 is up
        mplew.writeInt(message);

        return mplew.getPacket();
    }

    public static byte[] leftKnockBack() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LEFT_KNOCK_BACK.getValue());

        return mplew.getPacket();
    }

    public static byte[] hitCoconut(boolean spawn, int id, int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.HIT_COCONUT.getValue());
        mplew.writeInt(spawn ? 0x8000 : id);
        mplew.write(spawn ? 0 : type); // What action to do for the coconut.

        return mplew.getPacket();
    }

    public static byte[] coconutScore(int[] coconutscore) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.COCONUT_SCORE.getValue());
        mplew.writeShort(coconutscore[0]);
        mplew.writeShort(coconutscore[1]);

        return mplew.getPacket();
    }

    public static byte[] updateAriantScore(List<MapleCharacter> players) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ARIANT_SCORE_UPDATE.getValue());
        mplew.write(players.isEmpty() ? 0 : 1);
        if (!players.isEmpty()) {
            for (MapleCharacter i : players) {
                mplew.writeMapleAsciiString(i.getName());
                mplew.writeInt(i.getAriantScore()); // points
            }
        }
        return mplew.getPacket();
    }

    public static byte[] updateAriantScore(String name, int score, boolean empty) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ARIANT_SCORE_UPDATE.getValue());
        mplew.write(empty ? 0 : 1);
        if (!empty) {
            mplew.writeMapleAsciiString(name);
            mplew.writeInt(score); // points
        }
        return mplew.getPacket();
    }

    public static byte[] sheepRanchInfo(byte wolf, byte sheep) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHEEP_RANCH_INFO.getValue());
        mplew.write(wolf);
        mplew.write(sheep);

        return mplew.getPacket();
    }

    public static byte[] sheepRanchClothes(int cid, byte clothes) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHEEP_RANCH_CLOTHES.getValue());
        mplew.writeInt(cid);
        mplew.write(clothes); // 0 = sheep, 1 = wolf, 2 = Spectator (wolf without wool)

        return mplew.getPacket();
    }

    public static byte[] updateWitchTowerKeys(int keys) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WITCH_TOWER.getValue());
        mplew.write(keys);

        return mplew.getPacket();
    }

    public static byte[] showChaosZakumShrine(boolean spawned, int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHAOS_ZAKUM_SHRINE.getValue());
        mplew.write(spawned ? 1 : 0);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] showChaosHorntailShrine(boolean spawned, int time) {
        return showHorntailShrine(spawned, time);
    }

    public static byte[] showHorntailShrine(boolean spawned, int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.HORNTAIL_SHRINE.getValue());
        mplew.write(spawned ? 1 : 0);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] getRPSMode(byte mode, int mesos, int selection, int answer) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.RPS_GAME.getValue());
        mplew.write(mode);
        switch (mode) {
            case 6: { //not enough mesos
                if (mesos != -1) {
                    mplew.writeInt(mesos);
                }
                break;
            }
            case 8: { //open (npc)
                mplew.writeInt(9000019);
                break;
            }
            case 11: { //selection vs answer
                mplew.write(selection);
                mplew.write(answer); // FF = lose, or if selection = answer then lose ???
                break;
            }
        }
        return mplew.getPacket();
    }

    public static byte[] messengerInvite(String from, int messengerid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(3);
        mplew.writeMapleAsciiString(from);
        mplew.write(0);
        mplew.writeInt(messengerid);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] addMessengerPlayer(String from, MapleCharacter chr, int position, int channel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(0);
        mplew.write(position);
        PacketHelper.addCharLook(mplew, chr, true, chr.getClient());
        mplew.writeMapleAsciiString(from);
        mplew.write(channel);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] removeMessengerPlayer(int position) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(2);
        mplew.write(position);

        return mplew.getPacket();
    }

    public static byte[] updateMessengerPlayer(String from, MapleCharacter chr, int position, int channel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(7);
        mplew.write(position);
        PacketHelper.addCharLook(mplew, chr, true, chr.getClient());
        mplew.writeMapleAsciiString(from); // not needed
        mplew.writeShort(channel); // not needed

        return mplew.getPacket();
    }

    public static byte[] joinMessenger(int position) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(1);
        mplew.write(position);

        return mplew.getPacket();
    }

    public static byte[] messengerChat(final String charname, final String text) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(6);
        mplew.writeMapleAsciiString(charname);
        mplew.writeMapleAsciiString(text);

        return mplew.getPacket();
    }

    public static byte[] messengerNote(String text, int mode, int mode2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MESSENGER.getValue());
        mplew.write(mode);
        mplew.writeMapleAsciiString(text);
        mplew.write(mode2);

        return mplew.getPacket();
    }

    public static byte[] removeItemFromDuey(boolean remove, int Package) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DUEY.getValue());
        mplew.write(0x18);
        mplew.writeInt(Package);
        mplew.write(remove ? 3 : 4);

        return mplew.getPacket();
    }

    public static byte[] sendDuey(byte operation, List<MapleDueyActions> packages) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DUEY.getValue());
        mplew.write(operation);

        switch (operation) {
            case 9: { // Request 13 Digit AS
                mplew.write(1);
                // 0xFF = error
                break;
            }
            case 10: { // Open duey
                mplew.write(0);
                mplew.write(packages.size());

                for (MapleDueyActions dp : packages) {
                    mplew.writeInt(dp.getPackageId());
                    mplew.writeAsciiString(dp.getSender(), 13);
                    mplew.writeInt(dp.getMesos());
                    mplew.writeLong(PacketHelper.getTime(dp.getSentTime()));
                    mplew.writeZeroBytes(205);

                    if (dp.getItem() != null) {
                        mplew.write(1);
                        PacketHelper.GW_ItemSlotBase_Decode(mplew, dp.getItem());
                    } else {
                        mplew.write(0);
                    }
                    //System.out.println("Package has been sent in packet: " + dp.getPackageId());
                }
                mplew.write(0);
                break;
            }
        }
        return mplew.getPacket();
    }

    public static byte[] getKeymap(MapleKeyLayout layout) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.KEYMAP.getValue());

        layout.writeData(mplew);

        return mplew.getPacket();
    }

    public static byte[] petAutoHP(int itemId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PET_AUTO_HP.getValue());
        mplew.writeInt(itemId);

        return mplew.getPacket();
    }

    public static byte[] petAutoMP(int itemId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PET_AUTO_MP.getValue());
        mplew.writeInt(itemId);

        return mplew.getPacket();
    }

    public static void addRingInfo(MaplePacketLittleEndianWriter mplew, List<MapleRing> rings) {
        mplew.write(rings.size() > 0 ? 1 : 0);
        if (rings.size() > 0) {
            mplew.writeInt(rings.size());
            for (MapleRing ring : rings) {
                mplew.writeLong(ring.getRingId());
                mplew.writeLong(ring.getPartnerRingId());
                mplew.writeInt(ring.getItemId());
            }
        }
    }

    public static void addMRingInfo(MaplePacketLittleEndianWriter mplew, List<MapleRing> rings, MapleCharacter chr) {
        mplew.writeBool(rings.size() > 0);
        if (rings.size() > 0) {
            mplew.writeInt(rings.size());
            for (MapleRing ring : rings) {
                mplew.writeInt(chr.getId());
                mplew.writeInt(ring.getPartnerChrId());
                mplew.writeInt(ring.getRingId());
            }
        }
    }

    public static byte[] getBuffBar(long millis) { //You can use the buff again _ seconds later. + bar above head
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BUFF_BAR.getValue());
        mplew.writeLong(millis);

        return mplew.getPacket();
    }

    public static byte[] getBoosterFamiliar(int cid, int familiar, int id) { //item IDs
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOOSTER_FAMILIAR.getValue());
        mplew.writeInt(cid);
        mplew.writeInt(familiar);
        mplew.writeLong(id);
        mplew.write(0);

        return mplew.getPacket();
    }

    public static class EffectPacket {
        // 0: LevelUP
        // 8: RESIST
        // 11: Job Change
        // 12: Quest Completion
        // 14: Shine Effect
        // 16: Monster Book
        // 18: Item Level UP
        // 20: EXP Card
        // 29: You have revived on the current map through the statEffect of the Spirit Stone.
        // 35: GRADE UP (PVP)
        // 36: Character Blinks
        // 42: Gained an Upgrade Potion for playing an hour
        // 43: Monster Book Set Complete
        // 44: Familiar Escape!

        public static byte[] showForeignEffect(int effect) {
            return showForeignEffect(-1, effect);
        }

        public static byte[] showForeignEffect(int cid, int effect) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            if (cid == -1) {
                mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
                mplew.writeInt(cid);
            }
            mplew.write(effect); // 0 = Level up, 8 = job change

            return mplew.getPacket();
        }

        public static byte[] showItemLevelupEffect() {
            return showForeignEffect(18);
        }

        public static byte[] showForeignItemLevelupEffect(int cid) {
            return showForeignEffect(cid, 18);
        }

        public static byte[] showOwnDiceEffect(int skillid, int effectid, int effectid2, int level) {
            return showDiceEffect(-1, skillid, effectid, effectid2, level);
        }

        public static byte[] showDiceEffect(int cid, int skillid, int effectid, int effectid2, int level) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            if (cid == -1) {
                mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
                mplew.writeInt(cid);
            }
            mplew.write(3);
            mplew.writeInt(effectid);
            mplew.writeInt(effectid2);
            mplew.writeInt(skillid);
            mplew.write(level);
            mplew.write(0);

            return mplew.getPacket();
        }

        public static byte[] useCharm(byte charmsleft, byte daysleft, boolean safetyCharm) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            // The EXP did not drop after using the Safety Charm once(0 days/0 times left)
            // The EXP did not drop after using <name> item.
            mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
            mplew.write(8);
            mplew.write(safetyCharm ? 1 : 0);
            mplew.write(charmsleft); // Times
            mplew.write(daysleft); // Days
            if (!safetyCharm) {
                mplew.writeInt(0); // Item Id
            }

            return mplew.getPacket();
        }

        public static byte[] Mulung_DojoUp2() {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
            mplew.write(10);

            return mplew.getPacket();
        }

        public static byte[] showOwnHpHealed(final int amount) {
            return showHpHealed(-1, amount);
        }

        public static byte[] showHpHealed(final int cid, final int amount) {
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            if (cid == -1) {
                mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
                mplew.writeInt(cid);
            }
            mplew.write(30); // 30/31, same, 13 heal also, but 1 byte(not int)
            mplew.writeInt(amount);

            return mplew.getPacket();
        }

        public static byte[] showRewardItemAnimation(int itemId, String effect) {
            return showRewardItemAnimation(itemId, effect, -1);
        }

        public static byte[] showRewardItemAnimation(int itemId, String effect, int from_playerid) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            if (from_playerid == -1) {
                mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
                mplew.writeInt(from_playerid);
            }
            mplew.write(17);
            mplew.writeInt(itemId);
            mplew.write(effect != null && effect.length() > 0 ? 1 : 0);
            if (effect != null && effect.length() > 0) {
                mplew.writeMapleAsciiString(effect);
            }

            return mplew.getPacket();
        }

        public static byte[] showCashItemEffect(int itemId) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
            mplew.write(23);
            mplew.writeInt(itemId); // Item/Cash/0528.img/%08d/statEffect

            return mplew.getPacket();
        }

        public static byte[] ItemMaker_Success() {
            return ItemMaker_Success_3rdParty(-1);
        }

        public static byte[] ItemMaker_Success_3rdParty(final int from_playerid) {
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            if (from_playerid == -1) {
                mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
                mplew.writeInt(from_playerid);
            }
            mplew.write(19);
            mplew.writeInt(0/*pass ? 0 : 1*/); // 0 = pass, 1 = fail

            return mplew.getPacket();
        }

        public static byte[] useWheel(byte charmsleft) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
            mplew.write(24);
            mplew.write(charmsleft);

            return mplew.getPacket();
        }

        public static byte[] showOwnBuffEffect(int skillid, int effectid, int playerLevel, int skillLevel) {
            return showBuffeffect(-1, skillid, effectid, playerLevel, skillLevel, (byte) 3);
        }

        public static byte[] showOwnBuffEffect(int skillid, int effectid, int playerLevel, int skillLevel, byte direction) {
            return showBuffeffect(-1, skillid, effectid, playerLevel, skillLevel, direction);
        }

        public static byte[] showBuffeffect(int cid, int skillid, int effectid, int playerLevel, int skillLevel) {
            return showBuffeffect(cid, skillid, effectid, playerLevel, skillLevel, (byte) 3);
        }

        public static byte[] showBuffeffect(int cid, int skillid, int effectid, int playerLevel, int skillLevel, byte direction) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            if (cid == -1) {
                mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
                mplew.writeInt(cid);
            }
            mplew.write(effectid); // 7, 2, 1, 13
            mplew.writeInt(skillid);
            mplew.write(playerLevel - 1); //player level
            if (effectid == 2 && skillid == 31111003) {
                mplew.writeInt(0); // idk
            }
            mplew.write(skillLevel); //skill level
            if (direction != (byte) 3 || skillid == 1320006 || skillid == 30001062 || skillid == 30001061) {
                mplew.write(direction);
                // 0: Monster successfully captured.
                // 1: Capture failed. Monster HP too high.
                // 2: Monster cannot be captured.
            }
            if (skillid == 30001062) { // Call Of hunter
                mplew.writeInt(0); // position of monster spawned
            }
            mplew.writeZeroBytes(10); // just incase

            return mplew.getPacket();
        }

        public static byte[] ShowWZEffect(final String data) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
            mplew.write(22); // 25/26/28/15 also same structure
            mplew.writeMapleAsciiString(data);

            return mplew.getPacket();
        }

        public static byte[] showOwnCraftingEffect(String effect, int time, int mode) {
            return showCraftingEffect(-1, effect, time, mode);
        }

        public static byte[] showCraftingEffect(int cid, String effect, int time, int mode) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            if (cid == -1) {
                mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
                mplew.writeInt(cid);
            }
            mplew.write(32);
            mplew.writeMapleAsciiString(effect);
            mplew.write(0);// idk
            mplew.writeInt(time);
            mplew.writeInt(mode);
            if (mode == 2) {
                mplew.writeInt(0); // idk
            }

            return mplew.getPacket();
        }

        public static byte[] AranTutInstructionalBalloon(final String data) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
            mplew.write(0x19);
            mplew.writeMapleAsciiString(data);
            mplew.writeInt(1);

            return mplew.getPacket();
        }

        public static byte[] showOwnPetLevelUp(final byte index) {
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
            mplew.write(6);
            mplew.write(0);
            mplew.write(index); // Pet Index

            return mplew.getPacket();
        }

        public static byte[] showOwnChampionEffect() {
            return showChampionEffect(-1);
        }

        public static byte[] showChampionEffect(final int from_playerid) {
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            if (from_playerid == -1) {
                mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
                mplew.writeInt(from_playerid);
            }
            mplew.write(34);
            mplew.writeInt(30000); //seconds

            return mplew.getPacket();
        }
    }

    public static class UIPacket {

        public static byte[] getDirectionStatus(boolean enable) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.DIRECTION_STATUS.getValue());
            mplew.write(enable ? 1 : 0);
            return mplew.getPacket();
        }

        public static byte[] openUI(final int type) {
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);
            mplew.writeShort(SendPacketOpcode.OPEN_UI.getValue());
            mplew.write(type);
            return mplew.getPacket();
        }

        public static byte[] startAzwan(int npc) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(10);

            mplew.writeShort(SendPacketOpcode.OPEN_UI_OPTION.getValue());
            mplew.writeInt(70);
            mplew.writeInt(npc);

            return mplew.getPacket();
        }

        //Useless, just opens the ui. Not able to pass through any args for it to populate data.
        public static byte[] AzwanRewards(int npc) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(10);

            mplew.writeShort(SendPacketOpcode.OPEN_UI_OPTION.getValue());
            mplew.writeInt(70);
            mplew.writeInt(npc);

            return mplew.getPacket();
        }

        public static byte[] sendRepairWindow(final int npc) {
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(10);

            // 1st int: 3(Skill), 7(Maple User), 21(Party Search), 33(Repair)
            // 0: Buddy, 1: Party, 2: Expedition, 3: Guild, 4: Alliance, 5: Blacklist or npc id
            mplew.writeShort(SendPacketOpcode.OPEN_UI_OPTION.getValue());
            mplew.writeInt(33);
            mplew.writeInt(npc);

            return mplew.getPacket();
        }

        // 1 Enable 0: Disable
        public static byte[] lockUI(boolean enable) {
            return UIPacket.lockUI(enable ? 1 : 0, enable ? 1 : 0);
        }

        public static byte[] lockUI(int enable, int enable2) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.LP_SetInGameDirectionMode.getValue());
            mplew.write(enable > 0 ? 1 : 0);
            if (enable > 0) {
                mplew.writeShort(enable2);
            }
            mplew.write(enable < 0 ? 1 : 0);

            return mplew.getPacket();
        }

        public static byte[] lockKey(boolean enable) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.LP_SetDirectionMode.getValue());
            mplew.write(enable ? 1 : 0);
            mplew.writeInt(0);

            return mplew.getPacket();
        }

        public static byte[] IntroEnableUI(int wtf) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.LP_SetInGameDirectionMode.getValue());
            mplew.write(wtf > 0 ? 1 : 0);
            if (wtf > 0) {
                mplew.writeShort(wtf);
            }

            return mplew.getPacket();
        }

        public static byte[] disableOthers(boolean enable) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.LP_SetStandAloneMode.getValue());
            mplew.write(enable ? 1 : 0);
            return mplew.getPacket();
        }

        public static byte[] summonHelper(boolean summon) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SUMMON_HINT.getValue());
            mplew.write(summon ? 1 : 0);

            return mplew.getPacket();
        }

        public static byte[] summonMessage(int type) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SUMMON_HINT_MSG.getValue());
            mplew.write(1);
            mplew.writeInt(type);
            mplew.writeInt(7000); // probably the delay

            return mplew.getPacket();
        }

        public static byte[] summonMessage(String message) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SUMMON_HINT_MSG.getValue());
            mplew.write(0);
            mplew.writeMapleAsciiString(message);
            mplew.writeInt(200); // IDK
            mplew.writeShort(0);
            mplew.writeInt(10000); // Probably delay

            return mplew.getPacket();
        }

        public static byte[] getDirectionInfo(DirectionType type, int value) {
            return getDirectionInfo(type, value, 0);
        }

        public static byte[] getDirectionInfo(DirectionType type, int value, int value2) {
            return getDirectionEffect(type, null, new int[]{value, value2, 0, 0, 0, 0, 0, 0});
        }

        public static byte[] getDirectionInfo(String data, int value, int x, int y, int a, int b) {
            return getDirectionEffect(DirectionType.EFFECT, data, new int[]{value, x, y, a, b, 0, 0, 0});
        }

        public static byte[] getDirectionEffect(String data, int value, int x, int y) {
            return getDirectionEffect(data, value, x, y, 0);
        }

        public static byte[] getDirectionEffect(String data, int value, int x, int y, int npc) {
            //[02]  [02 00 31 31] [84 03 00 00] [00 00 00 00] [88 FF FF FF] [01] [00 00 00 00] [01] [29 C2 1D 00] [00] [00]
            //[mod] [data       ] [value      ] [value2     ] [value3     ] [a1] [a3         ] [a2] [npc        ] [  ] [a4]
            return getDirectionEffect(DirectionType.EFFECT, data, new int[]{value, x, y, 1, 1, 0, 0, npc});
        }

        public static byte[] getDirectionInfoNew(byte x, int value) {
            return getDirectionInfoNew(x, value, 0, 0);
        }

        public static byte[] getDirectionInfoNew(byte x, int value, int a, int b) {
            //[mod] [data] [value] [value2] [value3] [a1] ....
            return getDirectionEffect(DirectionType.UNK5, null, new int[]{x, value, a, b, 0, 0, 0, 0});
        }

        //int value, int value2, int value3, int a1, int a2, int a3, int a4, int npc
        public static byte[] getDirectionEffect(DirectionType type, String data, int[] value) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.LP_UserInGameDirectionEvent.getValue());
            mplew.write(type.getValue());
            switch (type) {
                case UNK:
                    mplew.writeInt(value[0]);
                    if (value[0] <= 413) {
                        mplew.writeInt(value[1]);
                    }
                    break;
                case EXEC_TIME:
                    mplew.writeInt(value[0]);
                    break;
                case EFFECT:
                    mplew.writeMapleAsciiString(data);
                    mplew.writeInt(value[0]);
                    mplew.writeInt(value[1]);
                    mplew.writeInt(value[2]);
                    mplew.write(value[3]);
                    if (value[3] > 0) {
                        mplew.writeInt(value[5]);
                    }
                    mplew.write(value[4]);
                    if (value[4] > 0) {
                        mplew.writeInt(value[6]);
                        mplew.write(value[6] > 0 ? 0 : 1); // 暫時解決
                        mplew.write(value[7]);
                    }
                    break;
                case ACTION:
                    mplew.writeInt(value[0]);
                    break;
                case UNK4:
                    mplew.writeInt(value[0]);
                    break;
                case UNK5:
                    mplew.writeMapleAsciiString(data);
                    mplew.writeInt(value[0]);
                    mplew.writeInt(value[1]);
                    mplew.writeInt(value[2]);
                    break;
                default:
                    System.out.println("CField.getDirectionInfo() is Unknow mod :: [" + type + "]");
                    break;
            }

            return mplew.getPacket();
        }

//        public static byte[] getDirectionInfo(int type, int value) {
//            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
//
//            mplew.writeShort(SendPacketOpcode.LP_UserInGameDirectionEvent.getValue());
//            mplew.write(type);
//            mplew.writeLong(value);
//
//            return mplew.getPacket();
//        }
//
//        public static byte[] getDirectionInfo(String data, int value, int x, int y, int pro) {
//            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
//
//            mplew.writeShort(SendPacketOpcode.LP_UserInGameDirectionEvent.getValue());
//            mplew.write(2);
//            mplew.writeMapleAsciiString(data);
//            mplew.writeInt(value);
//            mplew.writeInt(x);
//            mplew.writeInt(y);
//            mplew.writeShort(pro);
//            mplew.writeInt(0); //only if pro is > 0
//
//            return mplew.getPacket();
//        }
        public static byte[] reissueMedal(int itemId, int type) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.REISSUE_MEDAL.getValue());
            mplew.write(type);
            mplew.writeInt(itemId);
            // 0: A medal has been re-issued to you: %s
            // 1: You do not have enough mesos.
            // 2: You have insufficient slots \r\nin your Inventory Equip tab.
            // 3: You already have this medal.
            // 4: This medal cannot be re-issued.

            return mplew.getPacket();
        }

        public static byte[] playMovie(final String data, boolean show) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.PLAY_MOVIE.getValue());
            mplew.writeMapleAsciiString(data);
            mplew.write(show ? 1 : 0);

            return mplew.getPacket();
        }
    }

    public static class SummonPacket {

        public static byte[] spawnSummon(MapleSummon summon, boolean animated) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SPAWN_SUMMON.getValue());
            mplew.writeInt(summon.getOwnerId());
            mplew.writeInt(summon.getObjectId());
            mplew.writeInt(summon.getSkill());
            mplew.write(summon.getOwnerLevel() - 1);
            mplew.write(summon.getSkillLevel());
            mplew.writePos(summon.getPosition());
            mplew.write(summon.getSkill() == 32111006 || summon.getSkill() == 33101005 ? 5 : 4); //reaper = 5?
            if (summon.getSkill() == 35121003 && summon.getOwner().getMap() != null) {
                mplew.writeShort(summon.getOwner().getMap().getFootholds().findBelow(summon.getPosition()).getId());
            } else {
                mplew.writeShort(0);
            }
            mplew.write(summon.getMovementType().getValue());
            mplew.write(summon.getSummonType()); // 0 = Summon can't attack - but puppets don't attack with 1 either ^.-
            mplew.write(animated ? 1 : 0);
            mplew.write(1); //no idea
            final MapleCharacter chr = summon.getOwner();
            mplew.write(summon.getSkill() == 4341006 && chr != null ? 1 : 0); //mirror target
            if (summon.getSkill() == 4341006 && chr != null) {
                PacketHelper.addCharLook(mplew, chr, true, summon.getOwner().getClient());
            }
            if (summon.getSkill() == 35111002) {
                mplew.write(0);
            }

            return mplew.getPacket();
        }

        public static byte[] removeSummon(int ownerId, int objId) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.REMOVE_SUMMON.getValue());
            mplew.writeInt(ownerId);
            mplew.writeInt(objId);
            mplew.write(10); // message type

            return mplew.getPacket();
        }

        public static byte[] removeSummon(MapleSummon summon, boolean animated) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.REMOVE_SUMMON.getValue());
            mplew.writeInt(summon.getOwnerId());
            mplew.writeInt(summon.getObjectId());
            if (animated) {
                switch (summon.getSkill()) {
                    case 35121003:
                        mplew.write(10);
                        break;
                    case 35111001:
                    case 35111010:
                    case 35111009:
                    case 35111002:
                    case 35111005:
                    case 35111011:
                    case 35121009:
                    case 35121010:
                    case 35121011:
                    case 33101008:
                        mplew.write(5);
                        break;
                    default:
                        mplew.write(4);
                        break;
                }
            } else {
                mplew.write(1);
            }

            return mplew.getPacket();
        }

        public static byte[] moveSummon(int cid, int oid, Point startPos, List<ILifeMovementFragment> moves) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.MOVE_SUMMON.getValue());
            mplew.writeInt(cid);
            mplew.writeInt(oid);
            mplew.writePos(startPos);
            mplew.writeInt(0);
            PacketHelper.serializeMovementList(mplew, moves);

            return mplew.getPacket();
        }

        public static byte[] summonAttack(final int cid, final int summonSkillId, final byte animation, final List<Pair<Integer, Integer>> allDamage, final int level, final boolean darkFlare) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SUMMON_ATTACK.getValue());
            mplew.writeInt(cid);
            mplew.writeInt(summonSkillId);
            mplew.write(level - 1); //? guess
            mplew.write(animation);
            mplew.write(allDamage.size());
            for (final Pair<Integer, Integer> attackEntry : allDamage) {
                mplew.writeInt(attackEntry.left); // oid
                mplew.write(7); // who knows
                mplew.writeInt(attackEntry.right); // damage
            }
            mplew.write(darkFlare ? 1 : 0);

            return mplew.getPacket();
        }

        public static byte[] pvpSummonAttack(int cid, int playerLevel, int oid, int animation, Point pos, List<AttackPair> attack) {
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.PVP_SUMMON.getValue());
            mplew.writeInt(cid);
            mplew.writeInt(oid);
            mplew.write(playerLevel);
            mplew.write(animation);
            mplew.writePos(pos);
            mplew.writeInt(0); //<-- delay
            mplew.write(attack.size());
            for (AttackPair p : attack) {
                mplew.writeInt(p.objectId);
                mplew.writePos(p.point);
                mplew.write(p.attack.size());
                mplew.write(0); // who knows
                for (Pair<Integer, Boolean> atk : p.attack) {
                    mplew.writeInt(atk.left);
                }
            }

            return mplew.getPacket();
        }

        public static byte[] summonSkill(int cid, int summonSkillId, int newStance) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SUMMON_SKILL.getValue());
            mplew.writeInt(cid);
            mplew.writeInt(summonSkillId);
            mplew.write(newStance);

            return mplew.getPacket();
        }

        public static byte[] damageSummon(int cid, int summonSkillId, int damage, int unkByte, int monsterIdFrom) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.DAMAGE_SUMMON.getValue());
            mplew.writeInt(cid);
            mplew.writeInt(summonSkillId);
            mplew.write(unkByte);
            mplew.writeInt(damage);
            mplew.writeInt(monsterIdFrom);
            mplew.write(0);

            return mplew.getPacket();
        }
    }

    public static class NPCTalkPacket {

        public static byte[] spawnNPC(MapleNPC life, boolean show) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SPAWN_NPC.getValue());
            mplew.writeInt(life.getObjectId());
            mplew.writeInt(life.getId());
            mplew.writeShort(life.getPosition().x);
            mplew.writeShort(life.getCy());
            mplew.write(life.getF() == 1 ? 0 : 1);
            mplew.writeShort(life.getFh());
            mplew.writeShort(life.getRx0());
            mplew.writeShort(life.getRx1());
            mplew.write(show ? 1 : 0);
            return mplew.getPacket();
        }

        public static byte[] removeNPC(final int objectid) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.REMOVE_NPC.getValue());
            mplew.writeInt(objectid);

            return mplew.getPacket();
        }

        public static byte[] removeNPCController(final int objectid) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SPAWN_NPC_REQUEST_CONTROLLER.getValue());
            mplew.write(0);
            mplew.writeInt(objectid);

            return mplew.getPacket();
        }

        public static byte[] spawnNPCRequestController(MapleNPC life, boolean MiniMap) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SPAWN_NPC_REQUEST_CONTROLLER.getValue());
            mplew.write(1);
            mplew.writeInt(life.getObjectId());
            mplew.writeInt(life.getId());

            mplew.writeShort(life.getPosition().x);
            mplew.writeShort(life.getCy());
            mplew.write(life.getF() == 1 ? 0 : 1);
            mplew.writeShort(life.getFh());
            mplew.writeShort(life.getRx0());
            mplew.writeShort(life.getRx1());
            mplew.write(MiniMap ? 1 : 0);

            return mplew.getPacket();
        }

        public static byte[] setNPCScriptable(List<Pair<Integer, String>> npcs) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.NPC_SCRIPTABLE.getValue());
            mplew.write(npcs.size());
            for (Pair<Integer, String> s : npcs) {
                mplew.writeInt(s.left);
                mplew.writeMapleAsciiString(s.right);
                mplew.writeInt(0); // start time
                mplew.writeInt(Integer.MAX_VALUE); // end time
            }
            return mplew.getPacket();
        }

        public static byte[] getNPCTalk(int npc, NPCTalkType msgType, String talk, String endBytes, byte type) {
            return getNPCTalk(npc, msgType, talk, endBytes, type, npc);
        }

        public static byte[] getNPCTalk(int npc, NPCTalkType msgType, String talk, String endBytes, byte type, int diffNPC) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.write(msgType.getType());
            mplew.write(type); // mask; 1 = no ESC, 2 = playerspeaks, 4 = diff NPC 8 = something, ty KDMS
            if ((type & 0x4) != 0) {
                mplew.writeInt(diffNPC);
            }
            mplew.writeMapleAsciiString(talk);
            mplew.write(HexTool.getByteArrayFromHexString(endBytes));

            return mplew.getPacket();
        }

        public static byte[] getEvanTutorial(String data) {
            if (ServerConstants.DEBUG) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(8);
            mplew.writeInt(0);
            mplew.write(0); // Boolean
            mplew.write(NPCTalkType.IMAGE.getType());
            mplew.write(1);
            mplew.write(0);
            mplew.write(1);
            mplew.writeMapleAsciiString(data);
            return mplew.getPacket();
        }

        public static byte[] getMapSelection(final int npcid, final String sel) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(4);
            mplew.writeInt(npcid);
            mplew.writeShort(GameConstants.GMS ? 0x11 : 0x10);
            mplew.writeInt(npcid == 2083006 ? 1 : 0); //neo city
            mplew.writeInt(npcid == 9010022 ? 1 : 0); //dimensional
            mplew.writeMapleAsciiString(sel);

            return mplew.getPacket();
        }

        // 0 = Where would you like to go? (4 options)
        // 1 = Where do you want to go? (3 options)
        // 2 = Korean words here (4 options)
        // 3 = Where would you like to go? (4 options)
        // 4 = Select recovery or buff. (5 options)
        public static byte[] getBuffSelection(final int npcid, final String sel) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(4);
            mplew.writeInt(npcid);
            mplew.writeShort(17);
            mplew.writeInt(4);
            mplew.writeInt(0);
            mplew.writeMapleAsciiString(sel);
            return mplew.getPacket();
        }

        public static byte[] getNPCTalkStyle(int npc, String talk, int... args) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.writeShort(9);
            mplew.writeMapleAsciiString(talk);
            mplew.write(args.length);
            for (int i = 0; i < args.length; i++) {
                mplew.writeInt(args[i]);
            }
            return mplew.getPacket();
        }

        public static byte[] getAndroidTalkStyle(int npc, String talk, int... args) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.writeShort(10);
            mplew.writeMapleAsciiString(talk);
            mplew.write(args.length);
            for (int i = 0; i < args.length; i++) {
                mplew.writeInt(args[i]);
            }
            return mplew.getPacket();
        }

        public static byte[] getNPCTalkNum(int npc, String talk, int def, int min, int max) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.writeShort(4);
            mplew.writeMapleAsciiString(talk);
            mplew.writeInt(def);
            mplew.writeInt(min);
            mplew.writeInt(max);
            mplew.writeInt(0);
            return mplew.getPacket();
        }

        // 0 - sendOk
        // 1 - http://prntscr.com/4k4w2w Evan Picture/Image Window
        // 2 - sendYesNo
        // 3 - sendGetText
        // 4 - sendGetNumber
        // 5 - sendNext
        // 6 - http://prntscr.com/4k512x Speed quiz (answer question)
        // 7 - http://prntscr.com/4k51r0 Speed quiz (name monster)
        // 8 - http://prntscr.com/4k4wzg Maple Idol Timer (Question: \n Hint : \n Answer: input)
        // 9 - sendStyle but DC's.. wtf?
        // 10 - askAndroid (requires extra arguments else does nothing)
        // 11 - DC
        // 12-14 - Unknown
        // 15 - sendAcceptDecline
        // 16 - Ok button with a weird square and looks like outputted text input or something? idfk
        // 17 - Map Selection
        // 18+ - Unknown
        public static byte[] getNPCTalkText(int npc, String talk) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.NPC_TALK.getValue());
            mplew.write(4);
            mplew.writeInt(npc);
            mplew.writeShort(3);
            mplew.writeMapleAsciiString(talk);
            mplew.writeInt(0); // when 0x11, window types differ.
            mplew.writeInt(0);
            return mplew.getPacket();
        }

        public static byte[] getNPCShop(int sid, MapleShop shop, MapleClient c) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.OPEN_NPC_SHOP.getValue());
            mplew.writeInt(0);
            mplew.writeInt(sid);
            PacketHelper.addShopInfo(mplew, shop, c);
            return mplew.getPacket();
        }

        public static byte[] confirmShopTransaction(byte code, MapleShop shop, MapleClient c, int indexBought) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.CONFIRM_SHOP_TRANSACTION.getValue());
            mplew.write(code); // 8 = sell, 0 = buy, 0x20 = due to an error
            if (code == 4) {
                mplew.writeInt(0);
                mplew.writeInt(shop.getNpcId()); //oops
                PacketHelper.addShopInfo(mplew, shop, c);
            } else {
                mplew.write(indexBought >= 0 ? 1 : 0);
                if (indexBought >= 0) {
                    mplew.writeInt(indexBought);
                }
            }

            return mplew.getPacket();
        }

        public static byte[] getStorage(int npcId, byte slots, Collection<Item> items, int meso) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
            mplew.write(0x16);
            mplew.writeInt(npcId);
            mplew.write(slots);
            mplew.writeShort(0x7E);
            mplew.writeShort(0);
            mplew.writeInt(0);
            mplew.writeInt(meso);
            mplew.writeShort(0);
            mplew.write((byte) items.size());
            for (Item item : items) {
                PacketHelper.GW_ItemSlotBase_Decode(mplew, item);
            }
            mplew.writeShort(0);
            mplew.write(0);

            return mplew.getPacket();
        }

        public static byte[] getStorageFull() {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
            mplew.write(0x11);

            return mplew.getPacket();
        }

        public static byte[] mesoStorage(byte slots, int meso) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
            mplew.write(0x13);
            mplew.write(slots);
            mplew.writeShort(2);
            mplew.writeShort(0);
            mplew.writeInt(0);
            mplew.writeInt(meso);

            return mplew.getPacket();
        }

        public static byte[] arrangeStorage(byte slots, Collection<Item> items, boolean changed) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
            mplew.write(0x0F);
            mplew.write(slots);
            mplew.write(0x7C); //4 | 8 | 10 | 20 | 40
            mplew.writeZeroBytes(10);
            mplew.write(items.size());
            for (Item item : items) {
                PacketHelper.GW_ItemSlotBase_Decode(mplew, item);
            }
            mplew.write(0);
            return mplew.getPacket();
        }

        public static byte[] storeStorage(byte slots, MapleInventoryType type, Collection<Item> items) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
            mplew.write(0x0D);
            mplew.write(slots);
            mplew.writeShort(type.getBitfieldEncoding());
            mplew.writeShort(0);
            mplew.writeInt(0);
            mplew.write(items.size());
            for (Item item : items) {
                PacketHelper.GW_ItemSlotBase_Decode(mplew, item);
            }
            return mplew.getPacket();
        }

        public static byte[] takeOutStorage(byte slots, MapleInventoryType type, Collection<Item> items) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.OPEN_STORAGE.getValue());
            mplew.write(0x9);
            mplew.write(slots);
            mplew.writeShort(type.getBitfieldEncoding());
            mplew.writeShort(0);
            mplew.writeInt(0);
            mplew.write(items.size());
            for (Item item : items) {
                PacketHelper.GW_ItemSlotBase_Decode(mplew, item);
            }
            return mplew.getPacket();
        }
    }

    public static class InteractionPacket {

        public static byte[] getTradeInvite(MapleCharacter c) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
            mplew.write(GameConstants.GMS ? 11 : 2);
            mplew.write(3);
            mplew.writeMapleAsciiString(c.getName());
            mplew.writeInt(0); // Trade ID

            return mplew.getPacket();
        }

        public static byte[] getTradeMesoSet(byte number, int meso) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
            mplew.write(GameConstants.GMS ? 1 : 0xF);
            mplew.write(number);
            mplew.writeInt(meso);

            return mplew.getPacket();
        }

        public static byte[] getTradeItemAdd(byte number, Item item) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
            mplew.write(GameConstants.GMS ? 0 : 0xE);
            mplew.write(number);
            mplew.write(item.getPosition()); // Only in inv and not equipped //PacketHelper.addItemPosition(mplew, item, true, false);
            PacketHelper.GW_ItemSlotBase_Decode(mplew, item);
            return mplew.getPacket();
        }

        public static byte[] getTradeStart(MapleClient c, MapleTrade trade, byte number) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
            mplew.write(GameConstants.GMS ? 10 : 5);
            mplew.write(3);
            mplew.write(2);
            mplew.write(number);

            if (number == 1) {
                mplew.write(0);
                PacketHelper.addCharLook(mplew, trade.getPartner().getChr(), false, c);
                mplew.writeMapleAsciiString(trade.getPartner().getChr().getName());
                mplew.writeShort(trade.getPartner().getChr().getJob());
            }
            mplew.write(number);
            PacketHelper.addCharLook(mplew, c.getPlayer(), false, c);
            mplew.writeMapleAsciiString(c.getPlayer().getName());
            mplew.writeShort(c.getPlayer().getJob());
            mplew.write(0xFF);

            return mplew.getPacket();
        }

        public static byte[] getTradeConfirmation() {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
            mplew.write(GameConstants.GMS ? 2 : 0x10); //or 7? what

            return mplew.getPacket();
        }

        public static byte[] TradeMessage(final byte UserSlot, final byte message) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
            mplew.write(GameConstants.GMS ? 18 : 0xA);
            mplew.write(UserSlot);
            mplew.write(message);
            //0x02 = cancelled
            //0x07 = success [tax is automated]
            //0x08 = unsuccessful
            //0x09 = "You cannot make the trade because there are some items which you cannot carry more than one."
            //0x0A = "You cannot make the trade because the other person's on a different map."

            return mplew.getPacket();
        }

        public static byte[] getTradeCancel(final byte UserSlot, final int unsuccessful) { //0 = canceled 1 = invent space 2 = pickuprestricted
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
            mplew.write(GameConstants.GMS ? 18 : 0xA);
            mplew.write(UserSlot);
            mplew.write(unsuccessful == 0 ? (GameConstants.GMS ? 7 : 2) : (unsuccessful == 1 ? 8 : 9));

            return mplew.getPacket();
        }
    }
}
