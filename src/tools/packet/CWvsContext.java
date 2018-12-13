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
import client.MapleTrait.MapleTraitType;
import client.buddy.BuddyListEntry;
import client.inventory.*;
import client.inventory.MapleImp.ImpFlag;
import client.skill.Skill;
import client.skill.SkillEntry;
import server.status.MapleBuffStatus;
import constants.GameConstants;
import constants.ServerConstants;
import handling.SendPacketOpcode;
import handling.channel.MapleGuildRanking.GuildRankingInfo;
import handling.channel.handler.InventoryHandler;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.World;
import handling.world.exped.MapleExpedition;
import handling.world.exped.PartySearch;
import handling.world.exped.PartySearchType;
import handling.world.family.MapleFamily;
import handling.world.family.MapleFamilyBuff;
import handling.world.family.MapleFamilyCharacter;
import handling.world.guild.*;
import handling.world.guild.MapleBBSThread.MapleBBSReply;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.StructFamiliar;
import server.life.PlayerNPC;
import server.shops.HiredMerchant;
import server.shops.MaplePlayerShopItem;
import tools.StringUtil;
import tools.data.MaplePacketLittleEndianWriter;
import tools.types.Pair;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * @author AlphaEta
 */
public class CWvsContext {

    public static byte[] updateEquippedSkill(int skillId, int overlapId) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.EQUIP_STOLEN_SKILL.getValue());
        mplew.write(1);
        mplew.write((overlapId > 0) ? 1 : 0);
        mplew.writeInt(skillId);
        if (overlapId > 0) {
            mplew.writeInt(overlapId);
        }

        return mplew.getPacket();
    }

    /*      */
    public static byte[] MagicWheelAction(int mode) {
        /* 7414 */
        return MagicWheelAction(mode, null, null, 0);
        /*      */
    }

    /*      */
 /*      */
    public static byte[] MagicWheelAction(int mode, String dataKey, List<Integer> ids, int position) {
        /* 7418 */
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        /*      */
 /* 7420 */
        mplew.writeShort(SendPacketOpcode.MAGIC_WHEEL.getValue());
        /* 7421 */
        mplew.write(mode);
        /* 7422 */
        if (mode == 3) {
            /* 7423 */
            mplew.write(ids.size());
            /* 7424 */
            for (Integer i : ids) {
                /* 7425 */
                mplew.writeInt(i.intValue());
                /*      */
            }
            /* 7427 */
            mplew.writeMapleAsciiString(dataKey);
            /* 7428 */
            mplew.write(position);
            /*      */
        }
        /*      */
 /* 7438 */
        return mplew.getPacket();
        /*      */
    }

    public static byte[] professionInfo(String skil, int level1, int level2, int chance) {
        /* 3965 */
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        /* 3967 */
        mplew.writeShort(SendPacketOpcode.PROFESSION_INFO.getValue());
        /* 3968 */
        mplew.writeMapleAsciiString(skil);
        /* 3969 */
        mplew.writeInt(level1);
        /* 3970 */
        mplew.writeInt(level2);
        /* 3971 */
        mplew.write(1);
        /* 3972 */
        mplew.writeInt((skil.startsWith("9200")) || (skil.startsWith("9201")) ? 100 : chance);

        /* 3974 */
        return mplew.getPacket();
    }

    public static byte[] updateAzwanFame(int level, int fame, boolean levelup) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.AZWAN_FAME.getValue());
        mplew.writeInt(level);
        mplew.writeInt(fame);
        mplew.write(levelup ? 1 : 0);

        return mplew.getPacket();
    }

    /*      */
    public static byte[] updateStolenSkills(int mode) {
        /* 2725 */
        return updateStolenSkills(mode, 0, 0, 0);
        /*      */
    }

    /*      */
 /*      */
    public static byte[] updateStolenSkills(int mode, int skillid, int skilllevel, int slot) {
        /* 2729 */
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        /*      */
 /* 2731 */
        mplew.writeShort(SendPacketOpcode.UPDATE_STOLEN_SKILLS.getValue());
        /* 2732 */
        mplew.write(1);
        /* 2733 */
        mplew.write(mode);
        /*      */
 /* 2739 */
        switch (mode) {
            /*      */
            case 0:
                /* 2741 */
                mplew.writeInt(GameConstants.getPhantomBook(skillid));
                /* 2742 */
                mplew.writeInt(slot);
                /* 2743 */
                mplew.writeInt(skillid);
                /* 2744 */
                mplew.writeInt(skilllevel);
                /* 2745 */
                mplew.writeInt(0);
                /* 2746 */
                break;
            /*      */
            case 3:
                /* 2748 */
                mplew.writeInt(GameConstants.getPhantomBook(skillid));
                /* 2749 */
                mplew.writeInt(slot);
                /* 2750 */
                break;
            /*      */
            case 4:
            /*      */
            case 5:
                /* 2753 */
                mplew.writeInt(0);
                break;
            /*      */
            case 1:
            /*      */
            case 2:
                break;
            /*      */
        }
        /* 2757 */
        return mplew.getPacket();
        /*      */
    }

    /*      */
 /*      */
    public static byte[] showTargetSkills(int cid, int job, List<Integer> available) {
        /* 2761 */
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        /*      */
 /* 2763 */
        mplew.writeShort(SendPacketOpcode.SKILL_SWIPE_WINDOW.getValue());
        /* 2764 */
        mplew.write(1);
        /* 2765 */
        mplew.writeInt(cid);
        /* 2766 */
        mplew.writeInt(4);
        /* 2767 */
        mplew.writeInt(job);
        /* 2768 */
        mplew.writeInt(available.size());
        /* 2769 */
        for (Integer i : available) {
            /* 2770 */
            mplew.writeInt(i.intValue());
            /*      */
        }
        /*      */
 /* 2773 */
        return mplew.getPacket();
        /*      */
    }

    public static byte[] toSpouse(String sender, String text, int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SPOUSE_CHAT.getValue());
        mplew.write(type);
        if (type == 4) {
            mplew.write(1);
        } else {
            mplew.writeMapleAsciiString(sender);
            mplew.write(5);
        }
        mplew.writeMapleAsciiString(text);
        return mplew.getPacket();
    }

    public static byte[] multiChat(String name, String chattext, int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MULTICHAT.getValue());
        mplew.write(mode);
        mplew.writeMapleAsciiString(name);
        mplew.writeMapleAsciiString(chattext);
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

    public static byte[] getBuddy(String name, String chattext) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
        mplew.writeMapleAsciiString(name);
        mplew.writeMapleAsciiString(chattext);
        return mplew.getPacket();
    }

    public static byte[] getWhisper(String sender, int channel, String text) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.WHISPER.getValue());
        mplew.write(0x12);
        mplew.writeMapleAsciiString(sender);
        mplew.writeShort(channel - 1); // I guess this is the channel
        mplew.writeMapleAsciiString(text);
        return mplew.getPacket();
    }

    // @Eric
    public static byte[] OnChangeHour(int hour, int min) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.CHANGE_HOUR.getValue());

        mplew.writeShort(hour); // not sure if it's hour and min
        mplew.writeShort(min); // or what it is tbh, it's just short, short

        return mplew.getPacket();
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="BuffPacket">

    public static byte[] enableActions() {
        return updatePlayerStats(new EnumMap<MapleStat, Integer>(MapleStat.class), true, null);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="InfoPacket">

    public static byte[] updatePlayerStats(final Map<MapleStat, Integer> stats, final MapleCharacter chr) {
        return updatePlayerStats(stats, false, chr);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="GuildPacket">

    public static byte[] showWeirdEffect(String effect, int itemId) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
        mplew.write(0x20);
        mplew.writeMapleAsciiString(effect);
        mplew.write(1);
        mplew.writeInt(0);//weird high number is it will keep showing it lol
        mplew.writeInt(2);
        mplew.writeInt(itemId);
        return mplew.getPacket();
    }

    public static byte[] updateAreaInfo(int area, String info) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
        mplew.write(0x0C);
        mplew.writeShort(area);//infoNumber
        mplew.writeMapleAsciiString(info);
        return mplew.getPacket();
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="PartyPacket">
    public static byte[] showWeirdEffect(int chrId, String effect, int itemId) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(chrId);
        mplew.write(0x20);
        mplew.writeMapleAsciiString(effect);
        mplew.write(1);
        mplew.writeInt(0);//weird high number is it will keep showing it lol
        mplew.writeInt(2);//this makes it read the itemId
        mplew.writeInt(itemId);
        return mplew.getPacket();
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="ExpeditionPacket">

    public static byte[] updatePlayerStats(final Map<MapleStat, Integer> mystats, final boolean itemReaction, final MapleCharacter chr) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_STATS.getValue());
        mplew.write(itemReaction ? 1 : 0);
        long updateMask = 0;
        for (MapleStat statUpdate : mystats.keySet()) {
            updateMask |= statUpdate.getValue();
        }
        mplew.writeLong(updateMask);

        Map<MapleStat, Integer> sorted = mystats.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> oldVal, LinkedHashMap::new));

        for (final Entry<MapleStat, Integer> statUpdate : sorted.entrySet()) {
            switch (statUpdate.getKey()) {
                case SKIN:
                case LEVEL:
                case FATIGUE:
                case BATTLE_RANK:
                case ICE_GAGE: // not sure..
                case GENDER:
                    mplew.write(statUpdate.getValue().byteValue());
                    break;
                case BATTLE_EXP:
                case BATTLE_POINTS:
                case EXP:
                case HP:
                case MAX_HP:
                case MP:
                case MAX_MP:
                case FACE:
                case HAIR:
                case VIRTUE:
                case TRAIT_CHARISMA:
                case TRAIT_CRAFT:
                case TRAIT_SENSE:
                case TRAIT_WILL:
                case TRAIT_INSIGHT:
                case TRAIT_CHARM:
                case MESO:
                    mplew.writeInt(statUpdate.getValue());
                    break;
                case JOB:
                case STR:
                case DEX:
                case INT:
                case LUK:
                case AVAILABLE_AP:
                    mplew.writeShort(statUpdate.getValue().shortValue());
                    break;
                case AVAILABLE_SP:
                    if (GameConstants.isEvan(chr.getJob()) || GameConstants.isResist(chr.getJob()) || GameConstants.isMercedes(chr.getJob()) || GameConstants.isJett(chr.getJob()) || GameConstants.isPhantom(chr.getJob()) || GameConstants.isMihile(chr.getJob())) {
                        mplew.write(chr.getRemainingSpSize());
                        for (int i = 0; i < chr.getRemainingSps().length; i++) {
                            if (chr.getRemainingSp(i) > 0) {
                                mplew.write(i + 1);
                                mplew.write(chr.getRemainingSp(i));
                            }
                        }
                    } else {
                        mplew.writeShort(chr.getRemainingSp());
                    }
                    break;

                case TRAIT_LIMIT:
                    mplew.writeInt(statUpdate.getValue()); //actually 6 shorts.
                    mplew.writeInt(statUpdate.getValue());
                    mplew.writeInt(statUpdate.getValue());
                    break;
                case PET:
                    mplew.writeLong(statUpdate.getValue()); //uniqueID of 3 pets
                    mplew.writeLong(statUpdate.getValue());
                    mplew.writeLong(statUpdate.getValue());
                    break;
            }
        }
        mplew.write(0); // SetSecondaryStatChangedPoint [byte]
        mplew.write(0); // SetBattleRecoveryInfo [int][int]
        return mplew.getPacket();
    }

    public static byte[] temporaryStats_Aran() { // used for mercedes tutorial also
        final Map<MapleStat.Temp, Integer> stats = new EnumMap<>(MapleStat.Temp.class);

        stats.put(MapleStat.Temp.STR, 999);
        stats.put(MapleStat.Temp.DEX, 999);
        stats.put(MapleStat.Temp.INT, 999);
        stats.put(MapleStat.Temp.LUK, 999);
        stats.put(MapleStat.Temp.WATK, 255);
        stats.put(MapleStat.Temp.ACC, 999);
        stats.put(MapleStat.Temp.AVOID, 999);
        stats.put(MapleStat.Temp.SPEED, 140);
        stats.put(MapleStat.Temp.JUMP, 120);

        return temporaryStats(stats);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="FamilyPacket">

    public static byte[] temporaryStats_Balrog(final MapleCharacter chr) {
        final Map<MapleStat.Temp, Integer> stats = new EnumMap<>(MapleStat.Temp.class);

        int offset = 1 + (chr.getLevel() - 90) / 20; //every 20 levels above 90, +1
        stats.put(MapleStat.Temp.STR, chr.getStat().getTotalStr() / offset);
        stats.put(MapleStat.Temp.DEX, chr.getStat().getTotalDex() / offset);
        stats.put(MapleStat.Temp.INT, chr.getStat().getTotalInt() / offset);
        stats.put(MapleStat.Temp.LUK, chr.getStat().getTotalLuk() / offset);
        stats.put(MapleStat.Temp.WATK, (int) chr.getStat().getTotalWatk() / offset);
        stats.put(MapleStat.Temp.MATK, chr.getStat().getTotalMagic() / offset);

        return temporaryStats(stats);
    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="AlliancePacket">

    public static byte[] temporaryStats(final Map<MapleStat.Temp, Integer> mystats) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.TEMP_STATS.getValue());
        int updateMask = 0;
        for (MapleStat.Temp statupdate : mystats.keySet()) {
            updateMask |= statupdate.getValue();
        }
        mplew.writeInt(updateMask);
        for (final Entry<MapleStat.Temp, Integer> statupdate : mystats.entrySet()) {
            switch (statupdate.getKey()) {
                case SPEED:
                case JUMP:
                case UNKNOWN:
                    mplew.write(statupdate.getValue().byteValue());
                    break;
                default:
                    mplew.writeShort(statupdate.getValue().shortValue());
                    break;
            }
        }

        return mplew.getPacket();
    }
    //</editor-fold>

    public static byte[] temporaryStats_Reset() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.TEMP_STATS_RESET.getValue());

        return mplew.getPacket();
    }

    public static byte[] updateSkills(final Map<Skill, SkillEntry> update, boolean hyper) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(7 + (update.size() * 20));

        mplew.writeShort(SendPacketOpcode.UPDATE_SKILLS.getValue());
        mplew.write(1);
        mplew.write(0); // A skill has been activated / deactivated?
        mplew.writeShort(update.size());
        for (final Entry<Skill, SkillEntry> z : update.entrySet()) {
            mplew.writeInt(z.getKey().getId());
            mplew.writeInt(z.getValue().skillLevel);
            mplew.writeInt(z.getValue().masterlevel);
            PacketHelper.addExpirationTime(mplew, z.getValue().expiration);
        }
        mplew.write(/*hyper ? 0x0C : */4);
        return mplew.getPacket();
    }

    public static byte[] giveFameErrorResponse(final int op) {
        return OnFameResult(op, null, true, 0);
    }

    public static byte[] OnFameResult(final int op, final String charname, final boolean raise, final int newFame) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        // 1: The user name is incorrectly entered.
        // 2: Users under level l5 are unable to toggle with fame.
        // 3: You can't raise or drop a level anymore for today.
        // 4: You can't raise or drop a level of fame of that character anymore for this month.
        // default: The level of fame has neither been raise or dropped due to an unexpected error.
        mplew.writeShort(SendPacketOpcode.FAME_RESPONSE.getValue());
        mplew.write(op);
        if (op == 0 || op == 5) { // Give / Receive Fame
            mplew.writeMapleAsciiString(charname == null ? "" : charname);
            mplew.write(raise ? 1 : 0); // 1 raise, 0 drop
            if (op == 0) { // Give
                mplew.writeInt(newFame);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] BombLieDetector(final boolean error, final int mapid, final int channel) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOMB_LIE_DETECTOR.getValue());
        mplew.write(error ? 2 : 1);
        mplew.writeInt(mapid);
        mplew.writeInt(channel); // 255 for all channels

        return mplew.getPacket();
    }

    public static byte[] report(final int mode) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        // 3: You have been reported by a user.
        // 65: Please try again later.
        // 66: Please re-check the character name, then try again.
        // 67: You do not have enough mesos to report.
        // 68: Unable to connect to the server.
        // 69: You have exceeded the number of reports available.
        // 71: You may only report from 0 to 0. -> Based on last packet.
        // 72: Unable to report due to previously being cited for a false report.
        mplew.writeShort(SendPacketOpcode.REPORT_RESPONSE.getValue());
        mplew.write(mode);
        // case 0:
        // v6 = (unsigned __int8)CInPacket::Decode1(v3);
        // v7 = CInPacket::Decode4(v3);

        if (mode == 2) {//mode might be 0 now // You have successfully registered.
            mplew.write(0); // 0 or 1 only
            mplew.writeInt(1); // ?
        }

        return mplew.getPacket();
    }

    public static byte[] OnSetClaimSvrAvailableTime(final int from, final int to) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(4);

        // You may only report from 8 to 10.
        mplew.writeShort(SendPacketOpcode.REPORT_TIME.getValue());
        mplew.write(from);
        mplew.write(to);

        return mplew.getPacket();
    }

    public static byte[] OnClaimSvrStatusChanged(final boolean enable) { // Enable Report
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);

        mplew.writeShort(SendPacketOpcode.REPORT_STATUS.getValue());
        mplew.write(enable ? 1 : 0);

        return mplew.getPacket();
    }

    // LieDetector || LieDetectorResponse:
    // 0 - The user cannot be found.
    // 1 - You cannot use it on a user that isn't in the middle of attack.
    // 2 - This user has already been tested before.
    // 3 - This user is currently going through the Lie Detector Test.
    // 4 - byte, long(?)
    // 5 - byte, long(?)
    // 6 - byte, byte, int, byte
    public static byte[] OnAntiMacroResultNone() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.ANTI_MACRO_RESULT.getValue());
        mplew.write(7);
        mplew.writeInt(200);
        mplew.writeInt(200);
        return mplew.getPacket();
    }

    // correct structure, but invalid pointer..
    public static byte[] sendLieDetector(final byte[] captcha) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.LIE_DETECTOR.getValue());
        mplew.write(7); // 1 = not attacking, 2 = tested, 3 = going through
        mplew.write(2); // 2 give invalid pointer (suppose to be admin macro)
        mplew.write(1); // the time >0 is always 1 minute
        mplew.write(0);
        if (captcha == null) {
            mplew.writeInt(0);
            return mplew.getPacket();
        }
        mplew.writeInt(captcha.length);
        mplew.write(captcha);

        return mplew.getPacket();
    }

    public static byte[] getBytesFromFile(final File file) throws IOException {
        return new byte[(int) file.length()];
    }

    // correct structure, but seems to always give an invalid pointer..
    public static byte[] OnAntiMacroResult() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.LIE_DETECTOR.getValue());
        mplew.write(7); // byte (mode)
        mplew.write(0); // byte (anything but 2 would work but i'll use 1, i think 2 might be admin macro and 1 is the item)
        mplew.write(1);
        mplew.write(0);

        try {
            byte[] captcha = getBytesFromFile(new File("captcha.jpg"));
            if (captcha == null) {
                mplew.writeInt(0);
                return mplew.getPacket();
            }
            mplew.writeInt(captcha.length); // maybe..or maybe i'm tired
            mplew.write(captcha);
            System.out.println(captcha.length);
        } catch (IOException ignored) {
        }
        return mplew.getPacket();
    }

    public static byte[] LieDetectorResponse(final byte msg) {
        return LieDetectorResponse(msg, (byte) 0);
    }

    public static byte[] LieDetectorResponse(final byte msg, final byte msg2) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.LIE_DETECTOR.getValue());
        mplew.write(msg); // 1 = not attacking, 2 = tested, 3 = going through
        mplew.write(msg2);

        return mplew.getPacket();
    }

    public static byte[] updateMount(MapleCharacter chr, boolean levelup) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_MOUNT.getValue());
        mplew.writeInt(chr.getId());
        mplew.writeInt(chr.getMount().getLevel());
        mplew.writeInt(chr.getMount().getExp());
        mplew.writeInt(chr.getMount().getFatigue());
        mplew.write(levelup ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] getShowQuestCompletion(int id) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_QUEST_COMPLETION.getValue());
        mplew.writeShort(id);

        return mplew.getPacket();
    }

    public static byte[] useSkillBook(MapleCharacter chr, int skillid, int maxlevel, boolean canuse, boolean success) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.USE_SKILL_BOOK.getValue());
        mplew.write(0); //?
        mplew.writeInt(chr.getId());
        mplew.write(1);
        mplew.writeInt(skillid);
        mplew.writeInt(maxlevel);
        mplew.write(canuse ? 1 : 0);
        mplew.write(success ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] useAPSPReset(boolean spReset, int cid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(spReset ? SendPacketOpcode.SP_RESET.getValue() : SendPacketOpcode.AP_RESET.getValue());
        mplew.write(1); // update tick
        mplew.writeInt(cid);
        mplew.write(1); // 0 = fail

        return mplew.getPacket();
    }

    public static byte[] expandCharacterSlots(final int mode) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        // -1: Your characters slots have already been expanded.
        // 0: Failed to expand character slots.
        // 1: You've increased your number of character slots.
        mplew.writeShort(SendPacketOpcode.EXPAND_CHARACTER_SLOTS.getValue());
        // mplew.write(0); // @Eric, v117 IDA includes an extra byte here like AP/SP Resetting.
        mplew.writeInt(mode);
        mplew.write(0); // idk, a boolean

        return mplew.getPacket();
    }

    public static byte[] finishedGather(int type) {
        return gatherSortItem(true, type);
    }

    public static byte[] finishedSort(int type) {
        return gatherSortItem(false, type);
    }

    public static byte[] gatherSortItem(boolean gather, int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(gather ? SendPacketOpcode.FINISH_GATHER.getValue() : SendPacketOpcode.FINISH_SORT.getValue());
        mplew.write(1);
        mplew.write(type);

        return mplew.getPacket();
    }

    public static byte[] updateGender(MapleCharacter chr) { // Send this upon entering cs
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_GENDER.getValue());
        mplew.write(chr.getGender());

        return mplew.getPacket();
    }

    public static byte[] charInfo(final MapleCharacter chr, final boolean isSelf) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CHAR_INFO.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(chr.getLevel());
        mplew.writeShort(chr.getSubcategory());
        mplew.write(chr.getStat().pvpRank);
        mplew.writeInt(chr.getFame());
        mplew.write(chr.getMarriageId() > 0 ? 1 : 0);
        List<Integer> professions = chr.getProfessions();
        mplew.write(professions.size()); // checks for > 0
        // if > 0 we write short
        professions.forEach(mplew::writeShort);
        if (chr.getGuildId() <= 0) {
            mplew.writeMapleAsciiString("-");
            mplew.writeMapleAsciiString("");
        } else {
            final MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
            if (gs != null) {
                mplew.writeMapleAsciiString(gs.getName());
                if (gs.getAllianceId() > 0) {
                    final MapleGuildAlliance allianceName = World.Alliance.getAlliance(gs.getAllianceId());
                    if (allianceName != null) {
                        mplew.writeMapleAsciiString(allianceName.getName());
                    } else {
                        mplew.writeMapleAsciiString("");
                    }
                } else {
                    mplew.writeMapleAsciiString("");
                }
            } else {
                mplew.writeMapleAsciiString("-");
                mplew.writeMapleAsciiString("");
            }
        }
        mplew.write(isSelf ? -1 : 0);
        mplew.write(0);

        // TODO: 血型那些
        mplew.writeMapleAsciiString("測試");
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        mplew.write(0);
        mplew.write(chr.getSummonedPets().isEmpty() ? 0 : 1);
//        mplew.writeMapleAsciiString(chr.getCharMessage()); // 角色訊息
//        mplew.write(chr.getExpression());// 表情
//        mplew.write(chr.getConstellation());// 星座
//        mplew.write(chr.getBlood());// 血型
//        mplew.write(chr.getMonth());// 月
//        mplew.write(chr.getDay());// 日

        chr.getPets().stream().filter(pet -> pet.getSummoned()).forEach(pet -> {
            mplew.write(pet.getSummonedValue());
            mplew.writeInt(chr.getPetIndex(pet));
            mplew.writeInt(pet.getPetItemId()); // petid
            mplew.writeMapleAsciiString(pet.getName());
            mplew.write(pet.getLevel()); // pet level
            mplew.writeShort(pet.getCloseness()); // pet closeness
            mplew.write(pet.getFullness()); // pet fullness
            mplew.writeShort(0); // pet flags
            Item inv = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) (pet.getSummonedValue() == 2 ? -130 : pet.getSummonedValue() == 1 ? -114 : -138));
            mplew.writeInt(inv == null ? 0 : inv.getItemId());
        });
        mplew.write(0);

        if (chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18) != null && chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -19) != null) {
            final MapleMount mount = chr.getMount();
            mplew.write(1);
            mplew.writeInt(mount.getLevel());
            mplew.writeInt(mount.getExp());
            mplew.writeInt(mount.getFatigue());
        } else {
            mplew.write(0);
        }

        final int wishListSize = chr.getWishlistSize();
        mplew.write(wishListSize);
        if (wishListSize > 0) {
            final int[] wishList = chr.getWishlist();
            for (int x = 0; x < wishListSize; x++) {
                mplew.writeInt(wishList[x]);
            }
        }

        Item medal = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -46);
        mplew.writeInt(medal == null ? 0 : medal.getItemId());
        List<Pair<Integer, Long>> medalQuests = chr.getCompletedMedals();
        mplew.writeShort(medalQuests.size());
        for (Pair<Integer, Long> x : medalQuests) {
            mplew.writeShort(x.left);
            mplew.writeLong(x.right); // Gain Filetime
        }

        for (MapleTraitType t : MapleTraitType.values()) {
            mplew.write(chr.getTrait(t).getLevel());
        }
        return mplew.getPacket();
    }

    public static byte[] getMonsterBookInfo(MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOOK_INFO.getValue());
        mplew.writeInt(chr.getId());
        mplew.writeInt(chr.getLevel());
        chr.getMonsterBook().writeCharInfoPacket(mplew);

        return mplew.getPacket();
    }

    public static byte[] spawnPortal(final int townId, final int targetId, final int skillId, final Point pos) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_PORTAL.getValue());
        mplew.writeInt(townId);
        mplew.writeInt(targetId);
        if (townId != 999999999 && targetId != 999999999) {
            mplew.writeInt(skillId);
            mplew.writePos(pos);
        }

        return mplew.getPacket();
    }

    public static byte[] mechPortal(Point pos) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MECH_PORTAL.getValue());
        mplew.writePos(pos);

        return mplew.getPacket();
    }

    public static byte[] echoMegaphone(String name, String message) { // RAWR, removed by nexon
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ECHO_MESSAGE.getValue());
        mplew.write(0); //1 = Your echo message has been successfully sent

        // this should only be when 1?
        // if ( a2 ) {
        // CInPacket::DecodeBuffer((volatile LONG *)(a2 - 12));
        mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));

        mplew.writeMapleAsciiString(name); //name
        mplew.writeMapleAsciiString(message); //message

        return mplew.getPacket();
    }

    public static byte[] showQuestMsg(final String msg) {
        return broadcastMsg(5, msg);
    }

    public static byte[] Mulung_Pts(int recv, int total) {
        return showQuestMsg("You have received " + recv + " training points. Your total training score is now " + total + ".");
    }

    public static byte[] broadcastMessage(String message) {
        return broadcastMessage(4, 0, message, false);
    }

    public static byte[] broadcastMsg(int type, String message) {
        return broadcastMessage(type, 0, message, false);
    }

    public static byte[] broadcastMsg(int type, int channel, String message) {
        return broadcastMessage(type, channel, message, false);
    }

    public static byte[] broadcastMsg(int type, int channel, String message, boolean smegaEar) {
        return broadcastMessage(type, channel, message, smegaEar);
    }

    public static byte[] getPopupMsg(String message) {
        return broadcastMessage(1, 0, message, false);
    }

    private static byte[] broadcastMessage(int type, int channel, String message, boolean megaEar) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
        mplew.write(type);
        if (type == 4) {
            mplew.write(1);
        }
        if (type != 13 && type != 14) {
            mplew.writeMapleAsciiString(message);
        }
        switch (type) {
            case 3:
            case 17:
            case 18:
            case 15:
            case 16:
                mplew.write(channel - 1);
                mplew.write(megaEar ? 1 : 0);
                break;
            case 9: // Like Item Megaphone (Without Item)
                mplew.write(channel - 1);
                break;
            case 12: // Weather Effect
                mplew.writeInt(channel); // item id
                break;
            case 6:
            case 11:
            case 20:
                mplew.writeInt(channel >= 1000000 && channel < 6000000 ? channel : 0); // Item Id
                //E.G. All new EXP coupon {Ruby EXP Coupon} is now available in the Cash Shop!
                break;
            case 24:
                mplew.writeShort(0); // ?
                break;
        }

        return mplew.getPacket();
    }

    public static byte[] getGachaponMega(final String name, final String message, final Item item, final byte rareness, final String gacha) {
        return getGachaponMega(name, message, item, rareness, false, gacha);
    }

    public static byte[] getGachaponMega(final String name, final String message, final Item item, final byte rareness, final boolean dragon, final String gacha) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
        mplew.write(13); // 15, 16 = twin dragon egg
        mplew.writeMapleAsciiString(name + message);
        if (!dragon) {
            mplew.writeInt(0);
            mplew.writeInt(item.getItemId());
        }
        mplew.writeMapleAsciiString(gacha);
        PacketHelper.GW_ItemSlotBase_Decode(mplew, item);
        return mplew.getPacket();
    }

    public static byte[] getAniMsg(final int questID, final int time) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
        mplew.write(23);
        mplew.writeShort(questID);
        mplew.writeInt(time);

        return mplew.getPacket();
    }

    public static byte[] tripleSmega(List<String> message, boolean ear, int channel) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
        mplew.write(10);
        if (message.get(0) != null) {
            mplew.writeMapleAsciiString(message.get(0));
        }
        mplew.write(message.size());
        for (int i = 1; i < message.size(); i++) {
            if (message.get(i) != null) {
                mplew.writeMapleAsciiString(message.get(i));
            }
        }
        mplew.write(channel - 1);
        mplew.write(ear ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] itemMegaphone(String msg, boolean whisper, int channel, Item item) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SERVERMESSAGE.getValue());
        mplew.write(8);
        mplew.writeMapleAsciiString(msg);
        mplew.write(channel - 1);
        mplew.write(whisper ? 1 : 0);
        PacketHelper.addItemPosition(mplew, item, true, false);
        if (item != null) {
            PacketHelper.GW_ItemSlotBase_Decode(mplew, item);
        }

        return mplew.getPacket();
    }

    public static byte[] getPeanutResult(int itemId, short quantity, int itemId2, short quantity2, int ourItem) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PIGMI_REWARD.getValue());
        mplew.writeInt(itemId);
        mplew.writeShort(quantity);
        mplew.writeInt(ourItem);
        mplew.writeInt(itemId2);
        mplew.writeInt(quantity2);

        // if ( CInPacket::Decode1(v1) ) {
        // v2 = CInPacket::Decode1(a2);
        return mplew.getPacket();
    }

    public static byte[] getOwlOpen() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.OWL_OF_MINERVA.getValue());
        mplew.write(9);
        mplew.write(GameConstants.owlItems.length);
        for (int i : GameConstants.owlItems) {
            mplew.writeInt(i); //these are the most searched items. too lazy to actually make
        }

        return mplew.getPacket();
    }

    public static byte[] getOwlSearched(final int itemSearch, final List<HiredMerchant> hms) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.OWL_OF_MINERVA.getValue());
        mplew.write(8);
        mplew.writeInt(0);
        mplew.writeInt(itemSearch);
        int size = 0;

        for (HiredMerchant hm : hms) {
            size += hm.searchItem(itemSearch).size();
        }
        mplew.writeInt(size);
        for (HiredMerchant hm : hms) {
            final List<MaplePlayerShopItem> items = hm.searchItem(itemSearch);
            for (MaplePlayerShopItem item : items) {
                mplew.writeMapleAsciiString(hm.getOwnerName());
                mplew.writeInt(hm.getMap().getId());
                mplew.writeMapleAsciiString(hm.getDescription());
                mplew.writeInt(item.item.getQuantity()); //I THINK.
                mplew.writeInt(item.bundles); //I THINK.
                mplew.writeInt(item.price);
                switch (InventoryHandler.OWL_ID) {
                    case 0:
                        mplew.writeInt(hm.getOwnerId()); //store ID
                        break;
                    case 1:
                        mplew.writeInt(hm.getStoreId());
                        break;
                    default:
                        mplew.writeInt(hm.getObjectId());
                        break;
                }
                mplew.write(hm.getFreeSlot() == -1 ? 1 : 0);
                mplew.write(GameConstants.getInventoryType(itemSearch).getType()); //position?
                if (GameConstants.getInventoryType(itemSearch) == MapleInventoryType.EQUIP) {
                    PacketHelper.GW_ItemSlotBase_Decode(mplew, item.item);
                }
            }
        }

        return mplew.getPacket();
    }

    public static byte[] getOwlMessage(final int msg) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);

        // 0: Success
        // 1: The room is already closed.
        // 2: You can't enter the room due to full capacity.
        // 3: Other requests are being fulfilled this minute.
        // 4: You can't do it while you're dead.
        // 7: You are not allowed to trade other items at this point.
        // 17: You may not enter this store.
        // 18: The owner of the store is currently undergoing store maintenance. Please try again in a bit.
        // 23: This can only be used inside the Free Market.
        // default: This character is unable to do it.
        mplew.writeShort(SendPacketOpcode.OWL_RESULT.getValue());
        mplew.write(msg);

        return mplew.getPacket();
    }

    public static byte[] sendEngagementRequest(String name, int cid) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ENGAGE_REQUEST.getValue());
        mplew.write(0); //mode, 0 = engage, 1 = cancel, 2 = answer.. etc
        mplew.writeMapleAsciiString(name); // name
        mplew.writeInt(cid); // playerid

        return mplew.getPacket();
    }

    public static byte[] sendEngagement(final byte msg, final int item, final MapleCharacter male, final MapleCharacter female) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        // 11: You are now engaged.
        // 12: You are now married!
        // 13: Your engagement has been broken.
        // 14: You are no longer married.
        // 16: Congratulations!\r\nYour reservation was successfully made!
        // 18: You have entered the wrong character name.
        // 19: Your partner has to be in the same map.
        // 20: Your ETC slot is full.\r\nPlease remove some items.
        // 21: Your partner's ETC slots are full.
        // 22: You cannot be engaged to the same gender.
        // 23: You are already engaged.
        // 25: You are already married.
        // 24: She is already engaged.
        // 26: This person is already married.
        // 27: You're already in middle or proposing a person.
        // 28: She is currently being asked by another suitor.
        // 29: Unfortunately, the man who proposed to you has withdrawn his request for an engagement.
        // 30: She has politely declined your engagement request.
        // 31: The reservation has been canceled. Please try again later.
        // 32: You can't break the engagement after making reservations.
        // 34: This invitation is not valid.
        // 36: POPUP
        mplew.writeShort(SendPacketOpcode.ENGAGE_RESULT.getValue());
        mplew.write(msg); // 1103 custom quest
        if (msg == 11 || msg == 12) { // engage = 11, married = 12
            mplew.writeInt(0); // ringid or uniqueid
            mplew.writeInt(male.getId());
            mplew.writeInt(female.getId());
            mplew.writeShort(1); //always
            mplew.writeInt(item);
            mplew.writeInt(item); // wtf?repeat?
            mplew.writeAsciiString(male.getName(), 13);
            mplew.writeAsciiString(female.getName(), 13);
        } else if (msg == 15) { // Open Wedding invitation card
            mplew.writeAsciiString("Male", 13);
            mplew.writeAsciiString("Female", 13);
            mplew.writeShort(0); // type (Cathedral = 2, Vegas = other)
        }

        return mplew.getPacket();
    }

    public static byte[] sendWeddingGive() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WEDDING_GIFT.getValue());
        mplew.write(9);
        mplew.write(0); // item size, for each, additempos and additeminfo

        return mplew.getPacket();
    }

    public static byte[] sendWeddingReceive() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WEDDING_GIFT.getValue());
        mplew.write(10);
        mplew.writeLong(-1); // ?
        mplew.writeInt(0); // ?
        mplew.write(0);  // item size, for each, additempos and additeminfo

        return mplew.getPacket();
    }

    public static byte[] giveWeddingItem() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WEDDING_GIFT.getValue());
        mplew.write(11); // 12: You cannot give more than one present for each wishlist. 13/14: Failed to send the gift.
        mplew.write(0); // for each : String
        mplew.writeLong(0); // could this be time?
        mplew.write(0); // size: For each: additeminfo (without pos)

        return mplew.getPacket();
    }

    public static byte[] receiveWeddingItem() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.WEDDING_GIFT.getValue());
        mplew.write(15); // 16: idk.. 17: Item could not be retrieved\r\nbecause there was an item that\r\ncould only be acquired once.
        mplew.writeLong(0); // could this be time?
        mplew.write(0); // size: For each: additeminfo (without pos)

        return mplew.getPacket();
    }

    public static byte[] sendCashPetFood(final boolean success, final byte index) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3 + (success ? 1 : 0));

        mplew.writeShort(SendPacketOpcode.USE_CASH_PET_FOOD.getValue());
        mplew.write(success ? 0 : 1);
        if (success) {
            mplew.write(index);
        }

        return mplew.getPacket();
    }

    public static byte[] yellowChat(String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.YELLOW_CHAT.getValue());
        mplew.write(-1); //could be something like mob displaying message.
        mplew.writeMapleAsciiString(msg);

        return mplew.getPacket();
    }

    public static byte[] shopDiscount(int percent) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOP_DISCOUNT.getValue());
        mplew.write(percent);

        return mplew.getPacket();
    }

    public static byte[] catchMob(int mobid, int itemid, byte success) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CATCH_MOB.getValue());
        mplew.write(success);
        mplew.writeInt(itemid);
        mplew.writeInt(mobid);

        return mplew.getPacket();
    }

    public static byte[] spawnPlayerNPC(PlayerNPC npc, MapleClient c) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PLAYER_NPC.getValue());
        mplew.write(1); // Size
        mplew.writeInt(npc.getId());
        mplew.writeMapleAsciiString(npc.getName());
        PacketHelper.addCharLook(mplew, npc, true, c); // remove npc.getPet(i), npc.getF()?

        return mplew.getPacket();
    }

    public static byte[] disabledNPC(final List<Integer> ids) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3 + (ids.size() * 4));

        mplew.writeShort(SendPacketOpcode.DISABLE_NPC.getValue());
        mplew.write(ids.size());
        for (final Integer i : ids) {
            mplew.writeInt(i);
        }

        return mplew.getPacket();
    }

    public static byte[] getCard(int itemid, int level) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.GET_CARD.getValue());
        mplew.write(itemid > 0 ? 1 : 0);
        if (itemid > 0) {
            mplew.writeInt(itemid);
            mplew.writeInt(level);
        }
        return mplew.getPacket();
    }

    public static byte[] changeCardSet(int set) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CARD_SET.getValue());
        mplew.writeInt(set);

        return mplew.getPacket();
    }

    public static byte[] upgradeBook(Item book, MapleCharacter chr) { //slot -55
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.BOOK_STATS.getValue());
        mplew.writeInt(book.getPosition());
        PacketHelper.GW_ItemSlotBase_Decode(mplew, book, chr);

        return mplew.getPacket();
    }

    public static byte[] getCardDrops(int cardid, final List<Integer> drops) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CARD_DROPS.getValue());
        mplew.writeInt(cardid);
        mplew.writeShort(drops == null ? 0 : drops.size());
        if (drops != null) {
            for (final Integer de : drops) {
                mplew.writeInt(de);
            }
        }

        return mplew.getPacket();
    }

    public static byte[] getFamiliarInfo(MapleCharacter chr) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FAMILIAR_INFO.getValue());
        mplew.writeInt(chr.getFamiliars().size()); //size
        for (MonsterFamiliar mf : chr.getFamiliars().values()) {
            mf.writeRegisterPacket(mplew, true);
        }
        List<Pair<Integer, Long>> size = new ArrayList<>();
        for (Item i : chr.getInventory(MapleInventoryType.USE).list()) {
            if (i.getItemId() / 10000 == 287) { //expensif
                StructFamiliar f = MapleItemInformationProvider.getInstance().getFamiliarByItem(i.getItemId());
                if (f != null) {
                    size.add(new Pair<>(f.familiar, i.getInventoryId()));
                }
            }
        }
        mplew.writeInt(size.size());
        for (Pair<Integer, Long> s : size) {
            mplew.writeInt(chr.getId());
            mplew.writeInt(s.left);
            mplew.writeLong(s.right);
            mplew.write(0); //activated or not, troll
        }
        size.clear();

        return mplew.getPacket();
    }

    // @Eric
    public static byte[] OnWebBoardUpdate() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.WEB_BOARD_UPDATE.getValue());

        mplew.write(0);
        mplew.writeMapleAsciiString("http://www.google.com/"); // not sure what string gets written

        return mplew.getPacket();
    }

    public static byte[] MulungEnergy(int energy) {
        return sendPyramidEnergy("energy", String.valueOf(energy));
    }

    public static byte[] sendPyramidEnergy(final String type, final String amount) {
        return sendString(1, type, amount); // energy, massacre_hit, massacre_miss, massacre_cool, massacre_skill, PRaid_Team, balloon_Team, redTeam, blueTeam, kill_count
    }

    public static byte[] sendGhostPoint(final String type, final String amount) {
        return sendString(2, type, amount); //PRaid_Point (0-1500???)
    }

    public static byte[] sendGhostStatus(final String type, final String amount) {
        return sendString(3, type, amount); //Red_Stage(1-5), Blue_Stage, blueTeamDamage, redTeamDamage, Bamboo_Used
    }

    public static byte[] sendString(final int type, final String object, final String amount) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        switch (type) {
            case 1:
                mplew.writeShort(SendPacketOpcode.SESSION_VALUE.getValue());
                break;
            case 2:
                mplew.writeShort(SendPacketOpcode.PARTY_VALUE.getValue());
                break;
            case 3:
                mplew.writeShort(SendPacketOpcode.MAP_VALUE.getValue());
                break;
        }
        mplew.writeMapleAsciiString(object);
        mplew.writeMapleAsciiString(amount);

        return mplew.getPacket();
    }

    public static byte[] fairyPendantMessage(final int termStart, final int incExpR) { // bonusExp, <= 0x3D
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(14);

        mplew.writeShort(SendPacketOpcode.EXP_BONUS.getValue());
        mplew.writeInt(0x11); // 0x11 = pendant, 0x31 = evan medal
        mplew.writeInt(0/*termStart*/); // hour = 0 upon equipping
        mplew.writeInt(incExpR);

        return mplew.getPacket();
    }

    public static byte[] potionDiscountMessage(final int type, final int potionDiscR) { // PotionDiscount, <= 0x3D
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(10);

        mplew.writeShort(SendPacketOpcode.POTION_BONUS.getValue());
        mplew.writeInt(type); // 0x11 = pendant, 0x31 = evan medal
        mplew.writeInt(potionDiscR);

        return mplew.getPacket();
    }

    public static byte[] sendLevelup(boolean family, int level, String name) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LEVEL_UPDATE.getValue());
        mplew.write(family ? 1 : 2);
        mplew.writeInt(level);
        mplew.writeMapleAsciiString(name);

        return mplew.getPacket();
    }

    public static byte[] sendMarriage(boolean family, String name) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MARRIAGE_UPDATE.getValue());
        mplew.write(family ? 1 : 0);
        mplew.writeMapleAsciiString(name);

        return mplew.getPacket();
    }

    public static byte[] sendJobup(boolean family, int jobid, String name) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.JOB_UPDATE.getValue());
        mplew.write(family ? 1 : 0);
        mplew.writeInt(jobid); //or is this a short
        mplew.writeMapleAsciiString((!family ? "> " : "") + name);

        return mplew.getPacket();
    }

    // Does nothing, either I'm blind or this structure opened the GM_STORY_BOARD
    public static byte[] sendClassUp(String name) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CLASS_UPDATE.getValue());
        mplew.writeMapleAsciiString(name);

        return mplew.getPacket();
    }

    // Does nothing
    public static byte[] sendConsulUp(String name) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CONSULT_UPDATE.getValue());
        mplew.writeMapleAsciiString(name);

        return mplew.getPacket();
    }

    public static byte[] getAvatarMega(final MapleCharacter chr, final int channel, final int itemId, final List<String> text, final boolean ear) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.AVATAR_MEGA.getValue());
        mplew.writeInt(itemId);
        mplew.writeMapleAsciiString(chr.getName());
        for (final String i : text) {
            mplew.writeMapleAsciiString(i);
        }
        mplew.writeInt(channel - 1); // channel
        mplew.write(ear ? 1 : 0);
        PacketHelper.addCharLook(mplew, chr, true, chr.getClient());

        return mplew.getPacket();
    }

    public static byte[] GMPoliceMessage(final boolean dc) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(3);

        mplew.writeShort(SendPacketOpcode.GM_POLICE.getValue());
        mplew.write(dc ? 10 : 0);

        return mplew.getPacket();
    }

    public static byte[] pendantSlot(boolean p) { //slot -59
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PENDANT_SLOT.getValue());
        mplew.write(p ? 1 : 0);

        return mplew.getPacket();
    }

    public static byte[] followRequest(int chrid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FOLLOW_REQUEST.getValue());
        mplew.writeInt(chrid);

        return mplew.getPacket();
    }

    public static byte[] getTopMsg(String msg) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.TOP_MSG.getValue());
        mplew.writeMapleAsciiString(msg);

        return mplew.getPacket();
    }

    public static byte[] showMidMsg(String s, int l) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MID_MSG.getValue());
        mplew.write(l); // boolean
        mplew.writeMapleAsciiString(s);
        mplew.write(s.length() > 0 ? 0 : 1); //boolean remove?

        return mplew.getPacket();
    }

    public static byte[] getMidMsg(String msg, boolean keep, int index) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MID_MSG.getValue());
        mplew.write(index); //where the message should appear on the screen
        mplew.writeMapleAsciiString(msg);
        mplew.write(keep ? 0 : 1);

        return mplew.getPacket();
    }

    public static byte[] clearMidMsg() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.CLEAR_MID_MSG.getValue());

        return mplew.getPacket();
    }

    public static byte[] updateJaguar(MapleCharacter from) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_JAGUAR.getValue());
        PacketHelper.addJaguarInfo(mplew, from);

        return mplew.getPacket();
    }

    public static byte[] loadInformation(int location, int what, int birthday, int found) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.YOUR_INFORMATION.getValue());
        mplew.write(2);
        mplew.writeInt(location);
        mplew.writeInt(birthday);
        mplew.writeInt(what);
        mplew.writeInt(found);
        return mplew.getPacket();
    }

    public static byte[] saveInformation(final boolean fail) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.YOUR_INFORMATION.getValue());
        mplew.write(4); // save mode
        mplew.write(fail ? 0 : 1);

        return mplew.getPacket();
    }

    public static byte[] myInfoResult(int result) { // this is send upon opening the friend finder
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FIND_FRIEND.getValue());
        mplew.write(6);
        mplew.writeInt(1); // ?
        mplew.writeInt(result); // ?

        return mplew.getPacket();
    }

    public static byte[] findFriendResult(final List<MapleCharacter> friends) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FIND_FRIEND.getValue());
        mplew.write(8);
        mplew.writeShort(friends.size());
        for (MapleCharacter mc : friends) {
            mplew.writeInt(mc.getId());
            mplew.writeMapleAsciiString(mc.getName());
            mplew.write(mc.getLevel());
            mplew.writeShort(mc.getJob());
            mplew.writeInt(mc.getTODO());
            mplew.writeInt(mc.getFOUND());
        }
        return mplew.getPacket();
    }

    public static byte[] friendFinderError() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FIND_FRIEND.getValue());
        mplew.write(9);
        mplew.write(12); // Please try again later.

        return mplew.getPacket();
    }

    public static byte[] friendCharacterInfo(final MapleCharacter chr) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.FIND_FRIEND.getValue());
        mplew.write(11);
        mplew.writeInt(chr.getId());
        PacketHelper.addCharLook(mplew, chr, true, chr.getClient());

        return mplew.getPacket();
    }

    public static byte[] showBackgroundEffect(String eff, int value) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.VISITOR.getValue());
        mplew.writeMapleAsciiString(eff); //"Visitor"
        mplew.write(value);

        return mplew.getPacket();
    }

    public static byte[] sendPinkBeanChoco() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PINKBEAN_CHOCO.getValue());
        mplew.writeInt(0);
        mplew.write(1); // 1 = open, 0 = update
        mplew.write(0); // close = 1
        mplew.write(0); // boolean, 1 = all full
        mplew.writeInt(0); // flags
        // 0x1: First chocolate, 3994200
        // 0x2, 0x4, 0x8, 0x10, 0x20, 0x40, 0x80, 0x100

        return mplew.getPacket();
    }

    public static byte[] changeChannelMsg(final int channel, final String msg) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(8 + msg.length());

        mplew.writeShort(SendPacketOpcode.AUTO_CC_MSG.getValue());
        mplew.writeInt(channel);
        mplew.writeMapleAsciiString(msg);

        return mplew.getPacket();
    }

    public static byte[] pamSongUI() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PAM_SONG.getValue());

        return mplew.getPacket();
    }

    public static byte[] ultimateExplorer() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ULTIMATE_EXPLORER.getValue());

        return mplew.getPacket();
    }

    public static byte[] updateImpTime() {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_IMP_TIME.getValue());
        mplew.writeInt(0); // imp id?
        mplew.writeLong(0); // dwHighDateTime or low, same as getcharinfo

        return mplew.getPacket();
    }

    public static byte[] updateImp(MapleImp imp, int mask, int index, boolean login) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.ITEM_POT.getValue());
        mplew.write(login ? 0 : 1); //0 = unchanged, 1 = isChanged
        mplew.writeInt(index + 1);
        mplew.writeInt(mask);
        if ((mask & ImpFlag.SUMMONED.getValue()) != 0) {
            final Pair<Integer, Integer> i = MapleItemInformationProvider.getInstance().getPot(imp.getItemId());
            if (i == null) {
                return CWvsContext.enableActions();
            }
            mplew.writeInt(i.left);
            mplew.write(imp.getLevel()); //probably type
        }
        if ((mask & ImpFlag.SUMMONED.getValue()) != 0 || (mask & ImpFlag.STATE.getValue()) != 0) {
            mplew.write(imp.getState());
        }
        if ((mask & ImpFlag.SUMMONED.getValue()) != 0 || (mask & ImpFlag.FULLNESS.getValue()) != 0) {
            mplew.writeInt(imp.getFullness());
        }
        if ((mask & ImpFlag.SUMMONED.getValue()) != 0 || (mask & ImpFlag.CLOSENESS.getValue()) != 0) {
            mplew.writeInt(imp.getCloseness());
        }
        if ((mask & ImpFlag.SUMMONED.getValue()) != 0 || (mask & ImpFlag.CLOSENESS_LEFT.getValue()) != 0) {
            mplew.writeInt(1); //how much closeness is available to get right now
        }
        if ((mask & ImpFlag.SUMMONED.getValue()) != 0 || (mask & ImpFlag.MINUTES_LEFT.getValue()) != 0) {
            mplew.writeInt(0); //how much mins till next closeness
        }
        if ((mask & ImpFlag.SUMMONED.getValue()) != 0 || (mask & ImpFlag.LEVEL.getValue()) != 0) {
            mplew.write(1); //k idk
        }
        if ((mask & ImpFlag.SUMMONED.getValue()) != 0 || (mask & ImpFlag.FULLNESS_2.getValue()) != 0) {
            mplew.writeInt(imp.getFullness()); //idk
        }
        if ((mask & ImpFlag.SUMMONED.getValue()) != 0 || (mask & ImpFlag.UPDATE_TIME.getValue()) != 0) {
            mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }
        if ((mask & ImpFlag.SUMMONED.getValue()) != 0 || (mask & ImpFlag.CREATE_TIME.getValue()) != 0) {
            mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }
        if ((mask & ImpFlag.SUMMONED.getValue()) != 0 || (mask & ImpFlag.AWAKE_TIME.getValue()) != 0) {
            mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }
        if ((mask & ImpFlag.SUMMONED.getValue()) != 0 || (mask & ImpFlag.SLEEP_TIME.getValue()) != 0) {
            mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }
        if ((mask & ImpFlag.SUMMONED.getValue()) != 0 || (mask & ImpFlag.MAX_CLOSENESS.getValue()) != 0) {
            mplew.writeInt(100); //max closeness available to be gotten
        }
        if ((mask & ImpFlag.SUMMONED.getValue()) != 0 || (mask & ImpFlag.MAX_DELAY.getValue()) != 0) {
            mplew.writeInt(1000); //idk, 1260?
        }
        if ((mask & ImpFlag.SUMMONED.getValue()) != 0 || (mask & ImpFlag.MAX_FULLNESS.getValue()) != 0) {
            mplew.writeInt(1000);
        }
        if ((mask & ImpFlag.SUMMONED.getValue()) != 0 || (mask & ImpFlag.MAX_ALIVE.getValue()) != 0) {
            mplew.writeInt(1); //k ive no idea
        }
        if ((mask & ImpFlag.SUMMONED.getValue()) != 0 || (mask & ImpFlag.MAX_MINUTES.getValue()) != 0) {
            mplew.writeInt(10); //max minutes?
        }
        mplew.write(0); //or 1 then lifeID of affected pot, OR IS THIS 0x80000?'
        mplew.writeZeroBytes(100);

        return mplew.getPacket();
    }

    public static byte[] getMulungRanks(ResultSet rs) throws SQLException {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.MULUNG_DOJO_RANKING.getValue());
        if (!rs.last()) {
            mplew.writeInt(1);
            mplew.writeShort(1);
            mplew.writeMapleAsciiString("");
            mplew.writeLong(0);
            return mplew.getPacket();
        }
        mplew.writeInt(rs.getRow()); // size
        rs.beforeFirst();
        int rank = 1;
        while (rs.next()) {
            mplew.writeShort(rank);
            mplew.writeMapleAsciiString(rs.getString("name"));
            mplew.writeLong(rs.getInt("time"));
            rank++;
        }
        return mplew.getPacket();
    }

    public static byte[] getMulungMessage(final boolean dc, final String msg) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MULUNG_MESSAGE.getValue());
        mplew.write(dc ? 1 : 0); // close client
        // when dc (boolean byte) == 1
        /*
            *((_DWORD *)v5 - 3) = CInPacket::Decode4(v3);
            *((_DWORD *)v5 - 2) = CInPacket::Decode4(v3);
            *((_DWORD *)v5 - 1) = CInPacket::Decode4(v3);
            v6 = CInPacket::Decode4(v3);
            for ( i = 0; i < v6; *(_DWORD *)v16 = CInPacket::Decode4(v3)
            v8 = CInPacket::Decode4(v3);
            if ( v8 > 0 ) {
                *(_DWORD *)(v10 + *(_DWORD *)v9) = CInPacket::Decode4(v3);
                v11 = CInPacket::DecodeStr(v3, (int)&v16);
                v13 = CInPacket::DecodeStr(v3, (int)&v17);
         */
        mplew.writeMapleAsciiString(msg);

        return mplew.getPacket();
    }

    public byte[] OnAntiMacroResult(byte type, String name) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.LIE_DETECTOR.getValue());
        byte second = 0;
        mplew.write(type); // first byte switches, type.
        if (type == 4) {
            mplew.write(second); // second byte
            mplew.writeMapleAsciiString(name);
        } else if (type == 7 || type == 9) {
            mplew.write(type == 7 ? 4 : 0); // looks like a fail/success
        } else if (type == 6) {
            byte unk = 127;
            mplew.write(4);
            mplew.write(1);
            mplew.writeInt(unk ^ 2); // this is probably a size or something
            mplew.write((unk ^ 2) * 2); // this is also a size or maybe a name or something no clue
        } else if (type == 8 || type == 10) {
            mplew.write(second); // this should be 0 always i'm pretty sure
            mplew.writeMapleAsciiString(name);
        } else if (type == 5) {
            // CWvsContext::SaveAntiMacroScreenShot(v6);
            mplew.write(1);
            mplew.writeMapleAsciiString(name);
        }
        return mplew.getPacket();
    }

    public static class InventoryPacket {

        public static byte[] modifyInventory(boolean updateTick, final ModifyInventory mod) {
            return modifyInventory(updateTick, Collections.singletonList(mod));
        }

        public static byte[] modifyInventory(boolean updateTick, final List<ModifyInventory> mods) {
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
            mplew.write(updateTick ? 1 : 0);
            mplew.write(mods.size());
            mplew.write(0); // Unk
            int addMovement = -1;
            for (ModifyInventory mod : mods) {
                mplew.write(mod.getMode());
                mplew.write(mod.getInventoryType());
                mplew.writeShort(mod.getPosition());
                switch (mod.getMode()) {
                    case ModifyInventory.Types.ADD: {//add item
                        PacketHelper.GW_ItemSlotBase_Decode(mplew, mod.getItem());
                        break;
                    }
                    case ModifyInventory.Types.UPDATE: {//update quantity
                        mplew.writeShort(mod.getQuantity());
                        break;
                    }
                    case ModifyInventory.Types.MOVE: {//move
                        mplew.writeShort(mod.getNewPosition());
                        if (mod.getPosition() < 0 || mod.getNewPosition() < 0) {
                            addMovement = mod.getPosition() < 0 ? 1 : 2;
                        }
                        break;
                    }
                    case ModifyInventory.Types.REMOVE: {//remove
                        if (mod.getPosition() < 0) {
                            addMovement = 2;
                        }
                        break;
                    }

                    case 4: {
                        mplew.writeLong(0);
                        break;
                    }

                    case ModifyInventory.Types.MOVE_TO_BAG: {
                        mplew.writeInt(mod.getNewPosition());
                        addMovement = 0;
                        break;
                    }
                    case ModifyInventory.Types.UPDATE_IN_BAG: {
                        boolean v1 = false;
                        int v2 = 0;
                        if (v1) {
                            mplew.writeShort(v2);
                        }
                        if (v1 || v2 > 0) {
                            if (mod.getPosition() < 0) {
                                addMovement = 2; // 不知?
                            }
                        }
                    }
                    case ModifyInventory.Types.REMOVE_IN_BAG: {
                        if (mod.getPosition() < 0) {
                            addMovement = 2; // 不知?
                        }
                    }
                    case ModifyInventory.Types.MOVE_IN_BAG: {
                        mplew.writeShort(mod.getNewPosition());
                        addMovement = 0;
                    }
                    case ModifyInventory.Types.ADD_IN_BAG: {
                        PacketHelper.GW_ItemSlotBase_Decode(mplew, mod.getItem());
                    }
                    case 10: {
                        if (mod.getPosition() < 0) {
                            addMovement = 2; // 不知?
                        }
                    }
                    default: {
                        if (mod.getPosition() < 0) {
                            addMovement = 2; // 不知?
                        }
                    }
                }
                mod.clear();
            }
            if (addMovement > -1) {
                mplew.write(addMovement);
            }
            return mplew.getPacket();
        }

        public static byte[] updateInventoryFull() {
            return modifyInventory(false, new ArrayList<>());
        }

        public static byte[] getInventoryFull() {
            return modifyInventory(true, new ArrayList<>());
        }

        public static byte[] addInventorySlot(Item item) {
            return addInventorySlot(item, false);
        }

        public static byte[] addInventorySlot(Item item, boolean fromDrop) {
            return modifyInventory(fromDrop, new ModifyInventory(!GameConstants.isInBag(item.getPosition(), GameConstants.getInventoryType(item.getItemId()).getType()) ? ModifyInventory.Types.ADD : ModifyInventory.Types.ADD_IN_BAG, item));
        }

        public static byte[] updateInventorySlot(Item item, boolean fromDrop) {
            return modifyInventory(fromDrop, new ModifyInventory(!GameConstants.isInBag(item.getPosition(), GameConstants.getInventoryType(item.getItemId()).getType()) ? ModifyInventory.Types.UPDATE : ModifyInventory.Types.UPDATE_IN_BAG, item));
        }

        public static byte[] clearInventoryItem(final MapleInventoryType type, final short slot, final boolean fromDrop) {
            if (ServerConstants.DEBUG) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
            mplew.write(fromDrop ? 1 : 0);
            mplew.write(1);
            mplew.write(0);
            mplew.write(slot > 100 && type == MapleInventoryType.ETC ? ModifyInventory.Types.REMOVE_IN_BAG : ModifyInventory.Types.REMOVE); //bag
            mplew.write(type.getType());
            mplew.writeShort(slot);
            return mplew.getPacket();
        }

        public static byte[] updateSpecialItemUse(final Item item, final byte invType, final MapleCharacter chr) {
            return updateSpecialItemUse(item, invType, item.getPosition(), false, chr);
        }

        public static byte[] updateSpecialItemUse(final Item item, final byte invType, final short pos, final boolean theShort, final MapleCharacter chr) {
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
            mplew.write(0);
            mplew.write(2); // always 2
            mplew.write(0);

            //clears the slot and puts item in same slot in one packet
            mplew.write(GameConstants.isInBag(pos, invType) ? ModifyInventory.Types.REMOVE_IN_BAG : ModifyInventory.Types.REMOVE); // quantity > 0 (?)
            mplew.write(invType); // Inventory type
            mplew.writeShort(pos); // item slot

            mplew.write(0);
            mplew.write(invType);
            if (item.getType() == 1 || theShort) {
                mplew.writeShort(pos);
            } else {
                mplew.write(pos);
            }
            PacketHelper.GW_ItemSlotBase_Decode(mplew, item, chr);
            if (pos < 0) {
                mplew.write(2);
            }

            return mplew.getPacket();
        }

        public static byte[] updateSpecialItemUse_(final Item item, final byte invType, final MapleCharacter chr) {
            return updateSpecialItemUse_(item, invType, item.getPosition(), chr);
        }

        public static byte[] updateSpecialItemUse_(final Item item, final byte invType, final short pos, final MapleCharacter chr) {
            if (ServerConstants.DEBUG) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
            mplew.write(0); // could be from drop
            mplew.write(1);
            mplew.write(0);

            mplew.write(0); // quantity > 0 (?)
            mplew.write(invType); // Inventory type
            if (item.getType() == 1) {
                mplew.writeShort(pos);
            } else {
                mplew.write(pos);
            }
            PacketHelper.GW_ItemSlotBase_Decode(mplew, item, chr);
            if (pos < 0) {
                mplew.write(1);
            }

            return mplew.getPacket();
        }

        public static byte[] updateEquippedItem(MapleCharacter chr, Equip eq, short pos) {
            if (ServerConstants.DEBUG) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
            mplew.write(0);
            mplew.write(1);
            mplew.write(0);

            mplew.write(0);
            mplew.write(1);
            mplew.writeShort(pos);
            PacketHelper.GW_ItemSlotBase_Decode(mplew, eq, chr);

            return mplew.getPacket();
        }

        public static byte[] scrolledItem(Item scroll, MapleInventoryType inv, Item item, boolean destroyed, boolean potential, boolean equipped) {
            if (ServerConstants.DEBUG) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
            mplew.write(1);
            mplew.write(destroyed ? 2 : 3);
            mplew.write(0);

            mplew.write(scroll.getQuantity() > 0 ? 1 : 3);
            mplew.write(GameConstants.getInventoryType(scroll.getItemId()).getType());
            mplew.writeShort(scroll.getPosition());
            if (scroll.getQuantity() > 0) {
                mplew.writeShort(scroll.getQuantity());
            }

            mplew.write(3);
            mplew.write(inv.getType());
            mplew.writeShort(item.getPosition());
            if (!destroyed) {
                mplew.write(0);
                mplew.write(inv.getType());
                mplew.writeShort(item.getPosition());
                PacketHelper.GW_ItemSlotBase_Decode(mplew, item);
            }
            if (!potential) {
                mplew.write(1);
            }
            if (equipped) {
                mplew.write(8);
            }

            return mplew.getPacket();
        }

        public static byte[] moveAndUpgradeItem(MapleInventoryType type, Item item, short oldpos, short newpos, MapleCharacter chr) {
            if (ServerConstants.DEBUG) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
            mplew.write(1);
            mplew.write(3);
            mplew.write(0);

            mplew.write(GameConstants.isInBag(newpos, type.getType()) ? ModifyInventory.Types.REMOVE_IN_BAG : ModifyInventory.Types.REMOVE); //bag
            mplew.write(type.getType());
            mplew.writeShort(oldpos);

            mplew.write(0);
            mplew.write(1);
            mplew.writeShort(oldpos);
            PacketHelper.GW_ItemSlotBase_Decode(mplew, item, chr);

            mplew.write(2);
            mplew.write(type.getType());
            mplew.writeShort(oldpos);
            mplew.writeShort(newpos);
            mplew.write(0);

            return mplew.getPacket();
        }

        public static byte[] dropInventoryItem(MapleInventoryType type, short src) {
            if (ServerConstants.DEBUG) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
            mplew.write(1);
            mplew.write(1);
            mplew.write(0);

            mplew.write(3);
            mplew.write(type.getType());
            mplew.writeShort(src);
            if (src < 0) {
                mplew.write(1);
            }

            return mplew.getPacket();
        }

        public static byte[] dropInventoryItemUpdate(MapleInventoryType type, Item item) {
            if (ServerConstants.DEBUG) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[0]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
            mplew.write(1);
            mplew.write(1);
            mplew.write(0);

            mplew.write(1);
            mplew.write(type.getType());
            mplew.writeShort(item.getPosition());
            mplew.writeShort(item.getQuantity());

            return mplew.getPacket();
        }

        public static byte[] getSlotUpdate(byte invType, byte newSlots) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.INVENTORY_GROW.getValue());
            mplew.write(invType);
            mplew.write(newSlots);

            return mplew.getPacket();
        }

        public static byte[] getShowInventoryFull() {
            return InfoPacket.getShowInventoryStatus(255);
        }

        public static byte[] showItemUnavailable() {
            return InfoPacket.getShowInventoryStatus(254);
        }
    }

    public static class BuffPacket {

        public static final MapleBuffStatus[] checkList1 = new MapleBuffStatus[]{
            MapleBuffStatus.STR, MapleBuffStatus.INT, MapleBuffStatus.DEX, MapleBuffStatus.LUK
        };

        /*      */
        public static byte[] giveDice(int buffid, int skillid, int duration, Map<MapleBuffStatus, Integer> statups) /*      */ {
            /*  398 */
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            /*      */
 /*  400 */
            mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
            /*  401 */
            PacketHelper.writeBuffMask(mplew, statups);
            /*  402 */
            mplew.writeShort(Math.max(buffid / 100, Math.max(buffid / 10, buffid % 10)));
            /*  403 */
            mplew.writeInt(skillid);
            /*  404 */
            mplew.writeInt(duration);
            /*  405 */
            mplew.writeShort(0);
            /*  406 */
            mplew.write(0);
            /*  407 */
            mplew.writeInt(GameConstants.getDiceStat(buffid, 3));
            /*  408 */
            mplew.writeInt(GameConstants.getDiceStat(buffid, 3));
            /*  409 */
            mplew.writeInt(GameConstants.getDiceStat(buffid, 4));
            /*  410 */
            mplew.writeZeroBytes(20);
            /*  411 */
            mplew.writeInt(GameConstants.getDiceStat(buffid, 2));
            /*  412 */
            mplew.writeZeroBytes(12);
            /*  413 */
            mplew.writeInt(GameConstants.getDiceStat(buffid, 5));
            /*  414 */
            mplew.writeZeroBytes(16);
            /*  415 */
            mplew.writeInt(GameConstants.getDiceStat(buffid, 6));
            /*  416 */
            mplew.writeZeroBytes(16);
            /*  417 */
            mplew.writeShort(1000);
            /*  418 */
            mplew.write(0);
            /*      */
 /*  420 */
            return mplew.getPacket();
            /*      */
        }

        public static byte[] giveHoming(int skillid, int mobid, int x) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
            PacketHelper.writeSingleMask(mplew, MapleBuffStatus.HOMING_BEACON);
            mplew.writeShort(0);
            mplew.write(0);
            mplew.writeInt(1);
            mplew.writeLong(skillid);
            mplew.write(0);
            mplew.writeLong(mobid);
            mplew.writeShort(0);
            mplew.writeShort(0);
            mplew.write(0);
            mplew.write(0);//v112
            return mplew.getPacket();
        }

        public static byte[] giveMount(int buffid, int skillid, Map<MapleBuffStatus, Integer> statups) {
            return showMonsterRiding(-1, statups, buffid, skillid);
        }

        public static byte[] showMonsterRiding(int cid, Map<MapleBuffStatus, Integer> statups, int buffid, int skillId) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            if (cid == -1) {
                mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
                mplew.writeInt(cid);
            }
            PacketHelper.writeBuffMask(mplew, statups);
            mplew.writeShort(0);
            mplew.write(0);
            mplew.writeInt(buffid);
            mplew.writeInt(skillId);
            mplew.writeInt(0);
            mplew.writeShort(0);
            mplew.write(1);
            mplew.write(4);
            mplew.write(0);
            return mplew.getPacket();
        }

        public static byte[] givePirate(Map<MapleBuffStatus, Integer> statups, int duration, int skillid) {
            final boolean infusion = skillid == 5121009 || skillid == 15111005;
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
            PacketHelper.writeBuffMask(mplew, statups);
            mplew.writeShort(0);
            mplew.write(0);
            for (Map.Entry<MapleBuffStatus, Integer> stat : statups.entrySet()) {
                mplew.writeInt(stat.getValue());
                mplew.writeLong(skillid);
                mplew.writeZeroBytes(infusion ? 6 : 1);
                mplew.writeShort(duration);
            }
            mplew.writeShort(0);
            mplew.writeShort(0);
            mplew.write(1);
            mplew.write(1);
            return mplew.getPacket();
        }

        public static byte[] giveForeignPirate(Map<MapleBuffStatus, Integer> statups, int duration, int cid, int skillid) {
            final boolean infusion = skillid == 5121009 || skillid == 15111005;
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
            mplew.writeInt(cid);
            PacketHelper.writeBuffMask(mplew, statups);
            mplew.writeShort(0);
            mplew.write(0);
            for (Integer stat : statups.values()) {
                mplew.writeInt(stat.intValue());
                mplew.writeLong(skillid);
                mplew.writeZeroBytes(infusion ? 6 : 1);
                mplew.writeShort(duration);//duration... seconds
            }
            mplew.writeShort(0);
            mplew.writeShort(0);
            mplew.write(1);
            mplew.write(1);
            return mplew.getPacket();
        }

        public static byte[] giveArcane(Map<Integer, Integer> statups, int duration) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
            PacketHelper.writeSingleMask(mplew, MapleBuffStatus.ARCANE_AIM);

            mplew.writeShort(0);
            mplew.write(0);
            mplew.writeInt(statups.size());
            for (Entry<Integer, Integer> stat : statups.entrySet()) {
                mplew.writeInt(stat.getKey());
                mplew.writeLong(stat.getValue());
                mplew.writeInt(duration);
            }
            mplew.writeShort(0);
            mplew.writeShort(0);
            mplew.write(1);
            mplew.write(1);
            mplew.write(0);//v112
            return mplew.getPacket();
        }

        public static byte[] giveEnergyChargeTest(int bar, int bufflength) {
            return giveEnergyChargeTest(-1, bar, bufflength);
        }

        public static byte[] giveEnergyChargeTest(int cid, int bar, int bufflength) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            if (cid == -1) {
                mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
            } else {
                mplew.writeShort(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
                mplew.writeInt(cid);
            }
            PacketHelper.writeSingleMask(mplew, MapleBuffStatus.ENERGY_CHARGE);
            mplew.writeShort(0);
            mplew.write(0);
            mplew.writeInt(Math.min(bar, 10000));
            mplew.writeLong(0L);
            mplew.write(0);
            mplew.writeInt(bar >= 10000 ? bufflength : 0);
            mplew.writeLong(0);
            mplew.writeLong(0);
            mplew.writeLong(0);
            mplew.writeZeroBytes(60);
            return mplew.getPacket();
        }

        public static byte[] giveBuff(int buffid, int bufflength, Map<MapleBuffStatus, Integer> statups, MapleStatEffect effect) {
            if (ServerConstants.DEBUG) {
                System.out.println("調用位置: " + new java.lang.Throwable().getStackTrace()[1]);
            }
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
            PacketHelper.writeBuffMask(mplew, statups);

            boolean stacked = false;
            boolean isAura = false;
            for (Map.Entry stat : statups.entrySet()) {
                if ((stat.getKey() == MapleBuffStatus.YELLOW_AURA) || (stat.getKey() == MapleBuffStatus.BLUE_AURA) || (stat.getKey() == MapleBuffStatus.DARK_AURA)) {
                    isAura = true;
                }
                if (((MapleBuffStatus) stat.getKey()).canStack()) {
                    if (!stacked) {
                        mplew.writeZeroBytes(3);
                        stacked = true;
                    }
                    mplew.writeInt(1);
                    mplew.writeInt(buffid);
                    mplew.writeLong(((Integer) stat.getValue()).longValue());
                } else {
                    if (statups.containsKey(MapleBuffStatus.BODY_BOOST) /*|| statups.containsKey(MapleBuffStatus.JUDGMENT_DRAW)*/) {
                        mplew.writeInt(0);
                    }
                    if (stat.getKey() == MapleBuffStatus.SPIRIT_SURGE) {
                        mplew.writeInt((Integer) stat.getValue());
                    } else {
                        mplew.writeShort((Integer) stat.getValue());
                    }
                    mplew.writeInt(buffid);
                }
                mplew.writeInt(bufflength);
            }
            if (!isAura) {
                mplew.writeShort(0);
                if ((effect != null) && (effect.isDivineShield())) {
                    mplew.writeInt(effect.getEnhancedWatk());
                } else if ((effect != null) && (effect.getCharColor() > 0)) {
                    mplew.writeInt(effect.getCharColor());
                } else if ((effect != null) && (effect.isInflation())) {
                    mplew.writeInt(effect.getInflation());
                }
            }

            mplew.writeShort(1000);
            mplew.writeShort(0);
            mplew.write(0);
            mplew.write(0);
            mplew.write((effect != null) && (effect.isShadow()) ? 1 : 3);
            if (isAura) {
                mplew.write(0);
            }

            return mplew.getPacket();
        }

        private static boolean isMovementAffectingStat(Map<MapleBuffStatus, Integer> statups) {
            return (statups.containsKey(MapleBuffStatus.JUMP)) || (statups.containsKey(MapleBuffStatus.SPEED)) || (statups.containsKey(MapleBuffStatus.MORPH)) || (statups.containsKey(MapleBuffStatus.GHOST_MORPH)) || (statups.containsKey(MapleBuffStatus.MAPLE_WARRIOR)) || (statups.containsKey(MapleBuffStatus.MONSTER_RIDING)) || (statups.containsKey(MapleBuffStatus.DASH_SPEED)) || (statups.containsKey(MapleBuffStatus.DASH_JUMP)) || (statups.containsKey(MapleBuffStatus.SOARING)) || (statups.containsKey(MapleBuffStatus.YELLOW_AURA)) || (statups.containsKey(MapleBuffStatus.SNATCH)) || (statups.containsKey(MapleBuffStatus.ANGEL_SPEED)) || (statups.containsKey(MapleBuffStatus.ANGEL_JUMP)) || (statups.containsKey(MapleBuffStatus.ENERGY_CHARGE)) || (statups.containsKey(MapleBuffStatus.MECH_CHANGE));
        }

        public static byte[] giveDebuff(MapleBuffStatus statups, int x, int skillid, int level, int duration) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.GIVE_BUFF.getValue());
            PacketHelper.writeSingleMask(mplew, statups);
            mplew.writeShort(x);
            mplew.writeShort(skillid);
            mplew.writeShort(level);
            mplew.writeInt(duration);
            mplew.writeShort(0);
            mplew.write(0);
            mplew.writeShort(0);
            mplew.writeShort(0);
            mplew.write(1);
            mplew.write(1);
            mplew.writeZeroBytes(60);
            return mplew.getPacket();
        }

        public static byte[] cancelBuff(List<MapleBuffStatus> statups) /*      */ {
            /*  678 */
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            /*      */
 /*  680 */
            mplew.writeShort(SendPacketOpcode.CANCEL_BUFF.getValue());
            /*  681 */
            PacketHelper.writeMask(mplew, statups);
            /*  682 */
            for (MapleBuffStatus z : statups) {
                /*  683 */
                if (z.canStack()) {
                    /*  684 */
                    mplew.writeInt(0);
                    /*      */
                }
                /*      */
            }
            /*  687 */
            mplew.write(3);
            /*  688 */
            mplew.write(1);
            /*      */
 /*  690 */
            return mplew.getPacket();
            /*      */
        }

        public static byte[] cancelDebuff(MapleBuffStatus mask) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.CANCEL_BUFF.getValue());
            PacketHelper.writeSingleMask(mplew, mask);
            mplew.write(3);
            mplew.write(1);
            return mplew.getPacket();
        }

        public static byte[] cancelHoming() {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.CANCEL_BUFF.getValue());
            PacketHelper.writeSingleMask(mplew, MapleBuffStatus.HOMING_BEACON);
            return mplew.getPacket();
        }

        public static byte[] giveForeignBuff(int cid, Map<MapleBuffStatus, Integer> statups, MapleStatEffect effect) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
            mplew.writeInt(cid);
            PacketHelper.writeBuffMask(mplew, statups);
            for (Map.Entry statup : statups.entrySet()) {
                if ((statup.getKey() == MapleBuffStatus.SHADOWPARTNER) || (statup.getKey() == MapleBuffStatus.MECH_CHANGE) || (statup.getKey() == MapleBuffStatus.DARK_AURA) || (statup.getKey() == MapleBuffStatus.YELLOW_AURA) || (statup.getKey() == MapleBuffStatus.BLUE_AURA) || statup.getKey() == MapleBuffStatus.GIANT_POTION || (statup.getKey() == MapleBuffStatus.SPIRIT_LINK) || (statup.getKey() == MapleBuffStatus.PYRAMID_PQ) || (statup.getKey() == MapleBuffStatus.WK_CHARGE) || (statup.getKey() == MapleBuffStatus.SPIRIT_SURGE) || (statup.getKey() == MapleBuffStatus.MORPH) || (statup.getKey() == MapleBuffStatus.DARK_METAMORPHOSIS)) {
                    mplew.writeShort(((Integer) statup.getValue()).shortValue());
                    mplew.writeInt(effect.isSkill() ? effect.getSourceId() : -effect.getSourceId());
                } else if (statup.getKey() == MapleBuffStatus.FAMILIAR_SHADOW) {
                    mplew.writeInt((Integer) statup.getValue());
                    mplew.writeInt(effect.getCharColor());
                } else {
                    mplew.writeShort(((Integer) statup.getValue()).shortValue());
                }
            }
            mplew.writeShort(0);
            mplew.writeShort(0);
            mplew.write(1);
            mplew.write(1);
            mplew.writeZeroBytes(60);
            return mplew.getPacket();
        }

        public static byte[] giveForeignDebuff(int cid, MapleBuffStatus statups, int skillid, int level, int x) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.GIVE_FOREIGN_BUFF.getValue());
            mplew.writeInt(cid);
            PacketHelper.writeSingleMask(mplew, statups);
            if (skillid == 125) {
                mplew.writeShort(0);
                mplew.write(0);
            }
            mplew.writeShort(x);
            mplew.writeShort(skillid);
            mplew.writeShort(level);
            mplew.writeShort(0);
            mplew.writeShort(0);
            mplew.write(1);
            mplew.write(1);
            mplew.writeZeroBytes(30);
            return mplew.getPacket();
        }

        public static byte[] cancelForeignBuff(int cid, List<MapleBuffStatus> statups) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.CANCEL_FOREIGN_BUFF.getValue());
            mplew.writeInt(cid);
            PacketHelper.writeMask(mplew, statups);
            mplew.write(3);
            mplew.write(1);
            return mplew.getPacket();
        }

        public static byte[] cancelForeignDebuff(int cid, MapleBuffStatus mask) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.CANCEL_FOREIGN_BUFF.getValue());
            mplew.writeInt(cid);
            PacketHelper.writeSingleMask(mplew, mask);
            mplew.write(3);
            mplew.write(1);
            return mplew.getPacket();
        }
    }

    public static class InfoPacket {

        public static byte[] showMesoGain(final int gain, final boolean inChat) {
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            if (!inChat) {
                mplew.write(0);
                mplew.write(1);
                mplew.write(0); // A portion was not found after falling on the ground
                mplew.writeInt(gain);
                mplew.writeShort(0); // Internet Cafe Meso Bonus
            } else {
                mplew.write(6);
                mplew.writeInt(gain);
                mplew.writeInt(-1);
            }

            return mplew.getPacket();
        }

        public static byte[] getShowInventoryStatus(int mode) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            mplew.write(0);
            mplew.write(mode);
            mplew.writeInt(0);
            mplew.writeInt(0);

            return mplew.getPacket();
        }

        public static byte[] getShowItemGain(int itemId, short quantity) {
            return getShowItemGain(itemId, quantity, false);
        }

        public static byte[] getShowItemGain(int itemId, short quantity, boolean inChat) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            if (inChat) {
                mplew.writeShort(SendPacketOpcode.SHOW_ITEM_GAIN_INCHAT.getValue());
                mplew.write(5);
                mplew.write(1); // item count // if this is 0, then after quantity extra 1 string
                mplew.writeInt(itemId);
                mplew.writeInt(quantity);
            } else {
                mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
                mplew.writeShort(0);
                mplew.writeInt(itemId);
                mplew.writeInt(quantity);
            }

            return mplew.getPacket();
        }

        public static byte[] updateQuest(final MapleQuestStatus quest) {
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            mplew.write(1);
            mplew.writeShort(quest.getQuest().getId());
            mplew.write(quest.getStatus());
            switch (quest.getStatus()) {
                case 0:
                    mplew.write(0);
                    break;
                case 1:
                    mplew.writeMapleAsciiString(quest.getCustomData() != null ? quest.getCustomData() : "");
                    break;
                case 2:
                    mplew.writeLong(PacketHelper.getTime(System.currentTimeMillis()));
                    break;
            }

            return mplew.getPacket();
        }

        public static byte[] updateQuestMobKills(final MapleQuestStatus status) {
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            mplew.write(1);
            mplew.writeShort(status.getQuest().getId());
            mplew.write(1);
            final StringBuilder sb = new StringBuilder();
            for (final int kills : status.getMobKills().values()) {
                sb.append(StringUtil.getLeftPaddedStr(String.valueOf(kills), '0', 3));
            }
            mplew.writeMapleAsciiString(sb.toString());
            mplew.writeLong(0);

            return mplew.getPacket();
        }

        public static byte[] itemExpired(int itemid) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            mplew.write(2);
            mplew.writeInt(itemid);

            return mplew.getPacket();
        }

        public static byte[] GainEXP_Monster(final int gain, final boolean white, final int partyinc, final int Class_Bonus_EXP, final int Equipment_Bonus_EXP, final int Premium_Bonus_EXP) {
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            /*  894 */
            mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            /*  895 */
            mplew.write(3);
            /*  896 */
            mplew.write(white ? 1 : 0);
            /*  897 */
            mplew.writeInt(gain);
            /*  898 */
            mplew.write(0);
            /*  899 */
            mplew.writeInt(0);
            /*  900 */
            mplew.write(0);
            /*  901 */
            mplew.write(0);
            /*  902 */
            mplew.writeInt(0);
            /*  903 */
            mplew.write(0);
            /*  904 */
            mplew.writeInt(partyinc);
            /*      */
 /*  906 */
            mplew.writeInt(Equipment_Bonus_EXP);
            /*  907 */
            mplew.writeInt(0);
            /*  908 */
            mplew.writeInt(0);
            /*  909 */
            mplew.write(0);
            /*  910 */
            mplew.writeInt(0);
            /*  911 */
            mplew.writeInt(0);
            /*  912 */
            mplew.writeInt(0);
            /*      */
 /*  915 */
            mplew.writeInt(0);
            /*  916 */
            mplew.writeInt(0);
            /*  917 */
            mplew.writeInt(0);
            /*  918 */
            mplew.writeInt(0);
            /*  919 */
            mplew.writeInt(Premium_Bonus_EXP);
            /*  920 */
            mplew.writeInt(0);
            /*      */

            return mplew.getPacket();
        }

        public static byte[] GainEXP_Others(final int gain, final boolean inChat, final boolean white) {
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            mplew.write(3); // 3 = exp, 4 = fame, 5 = mesos, 6 = guildpoints
            mplew.write(white ? 1 : 0);
            mplew.writeInt(gain);
            mplew.write(inChat ? 1 : 0);
            mplew.writeZeroBytes(64);
            if (inChat) {
                mplew.writeZeroBytes(5);
            }

            return mplew.getPacket();
        }

        public static byte[] getSPMsg(byte sp, short job) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            mplew.write(4);
            mplew.writeShort(job);
            mplew.write(sp);

            return mplew.getPacket();
        }

        public static byte[] getShowFameGain(final int gain) {
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            mplew.write(5);
            mplew.writeInt(gain);

            return mplew.getPacket();
        }

        public static byte[] getGPMsg(int itemid) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            mplew.write(7);
            mplew.writeInt(itemid);

            return mplew.getPacket();
        }

        public static byte[] getGPContribution(int itemid) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            mplew.write(8);
            mplew.writeInt(itemid);

            return mplew.getPacket();
        }

        public static byte[] getStatusMsg(int itemid) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            mplew.write(9);
            mplew.writeInt(itemid);

            return mplew.getPacket();
        }

        public static byte[] updateInfoQuest(final int quest, final String data) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            mplew.write(12);
            mplew.writeShort(quest);
            mplew.writeMapleAsciiString(data);

            return mplew.getPacket();
        }

        public static byte[] showItemReplaceMessage(final List<String> message) {
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            mplew.write(14);
            mplew.write(message.size());
            for (final String x : message) {
                mplew.writeMapleAsciiString(x);
            }

            return mplew.getPacket();
        }

        public static byte[] showTraitGain(MapleTraitType trait, int amount) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            mplew.write(16);
            mplew.writeLong(trait.getStat().getValue());
            mplew.writeInt(amount);

            return mplew.getPacket();
        }

        public static byte[] showTraitMaxed(MapleTraitType trait) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            mplew.write(17);
            mplew.writeLong(trait.getStat().getValue());

            return mplew.getPacket();
        }

        public static byte[] getBPMsg(int amount) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            mplew.write(21);
            mplew.writeInt(amount); // Battle Points
            mplew.writeInt(0); // Battle EXP

            return mplew.getPacket();
        }

        public static byte[] showExpireMessage(final byte type, final List<Integer> item) {
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(4 + (item.size() * 4));

            // normal = 10, seal = 13, skill = 15;
            mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            mplew.write(type);
            mplew.write(item.size());
            item.forEach(mplew::writeInt);
            return mplew.getPacket();
        }

        public static byte[] showStatusMessage(final int mode, final String info, final String data) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            // 19: The Android is not powered. Please insert a Mechanical Heart.
            // 20: You recovered some fatigue by resting.
            mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            mplew.write(mode);
            if (mode == 22) {
                mplew.writeMapleAsciiString(info); //name got Shield.
                mplew.writeMapleAsciiString(data); //Shield applied to name.
            }

            return mplew.getPacket();
        }

        public static byte[] showReturnStone(final int act) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            // 0: You can't use that item during a divorce.
            // 1: The location of your spouse is unknown.
            // 2: Your spouse is in an area where Return Stones cannot be used.
            // 3: Return Stones cannot be used here.
            mplew.writeShort(SendPacketOpcode.SHOW_STATUS_INFO.getValue());
            mplew.write(23);
            mplew.write(act);

            return mplew.getPacket();
        }
    }

    public static class GuildPacket {

        public static byte[] guildInvite(int gid, String charName, int levelFrom, int jobFrom) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
            mplew.write(5);
            mplew.writeInt(gid);
            mplew.writeMapleAsciiString(charName);
            mplew.writeInt(levelFrom);
            mplew.writeInt(jobFrom);
            mplew.writeInt(0);
            return mplew.getPacket();
        }

        public static byte[] showGuildInfo(MapleCharacter c) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
            mplew.write(0x1A);
            if (c == null || c.getMGC() == null) { //show empty guild (used for leaving, expelled)
                mplew.write(0);
                return mplew.getPacket();
            }
            MapleGuild g = World.Guild.getGuild(c.getGuildId());
            if (g == null) { //failed to read from DB - don't show a guild
                mplew.write(0);
                return mplew.getPacket();
            }
            mplew.write(1); //bInGuild
            getGuildInfo(mplew, g);
            return mplew.getPacket();
        }

        public static void getGuildInfo(MaplePacketLittleEndianWriter mplew, MapleGuild guild) {
            mplew.writeInt(guild.getId());
            mplew.writeMapleAsciiString(guild.getName());
            for (int i = 1; i <= 5; i++) {
                mplew.writeMapleAsciiString(guild.getRankTitle(i));
            }
            guild.addMemberData(mplew);
            mplew.writeInt(guild.getCapacity());
            mplew.writeShort(guild.getLogoBG());
            mplew.write(guild.getLogoBGColor());
            mplew.writeShort(guild.getLogo());
            mplew.write(guild.getLogoColor());
            mplew.writeMapleAsciiString(guild.getNotice());
            mplew.writeInt(guild.getGP());
            mplew.writeInt(guild.getGP());
            mplew.writeInt(guild.getAllianceId() > 0 ? guild.getAllianceId() : 0);
            mplew.write(guild.getLevel());
            mplew.writeShort(0); // guild rank
            mplew.writeShort(guild.getSkills().size());
            for (MapleGuildSkill i : guild.getSkills()) {
                mplew.writeInt(i.skillID);
                mplew.writeShort(i.level);
                mplew.writeLong(PacketHelper.getTime(i.timestamp));
                mplew.writeMapleAsciiString(i.purchaser);
                mplew.writeMapleAsciiString(i.activator);
            }
        }

        public static byte[] newGuildInfo(final MapleCharacter c) {
            // Congratulation~  %s guild has been registered to our guild office...  I wish the best luck for you guys~
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
            mplew.write(0xA1);
            if (c == null || c.getMGC() == null) { // Show empty guild (used for leaving, expelled)
                return genericGuildMessage((byte) 37);
            }
            MapleGuild g = World.Guild.getGuild(c.getGuildId());
            if (g == null) {
                return genericGuildMessage((byte) 37);
            }
            getGuildInfo(mplew, g); // All empty data

            return mplew.getPacket();
        }

        public static byte[] newGuildMember(MapleGuildCharacter mgc) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
            mplew.write(45);
            mplew.writeInt(mgc.getGuildId());
            mplew.writeInt(mgc.getId());
            mplew.writeAsciiString(mgc.getName(), 13);
            mplew.writeInt(mgc.getJobId());
            mplew.writeInt(mgc.getLevel());
            mplew.writeInt(mgc.getGuildRank()); //should be always 5 but whatevs
            mplew.writeInt(mgc.isOnline() ? 1 : 0); //should always be 1 too
            mplew.writeInt(mgc.getAllianceRank()); //? could be guild signature, but doesn't seem to matter
            mplew.writeInt(mgc.getGuildContribution()); //should always 3

            return mplew.getPacket();
        }

        public static byte[] memberLeft(MapleGuildCharacter mgc, boolean bExpelled) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
            mplew.write(bExpelled ? 53 : 50);
            mplew.writeInt(mgc.getGuildId());
            mplew.writeInt(mgc.getId());
            mplew.writeMapleAsciiString(mgc.getName());

            return mplew.getPacket();
        }

        public static byte[] guildDisband(int gid) {
            // The guild has been disbanded..  Please come back to me when you want to make a guild, again..
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
            mplew.write(56);
            mplew.writeInt(gid);
            mplew.write(1);

            return mplew.getPacket();
        }

        public static byte[] guildCapacityChange(int gid, int capacity) {
            // Congratulation~  The number of guld members has now increased to %d ...  Please come back to me whenever you need to..
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
            mplew.write(64);
            mplew.writeInt(gid);
            mplew.write(capacity);

            return mplew.getPacket();
        }

        public static byte[] guildContribution(int gid, int cid, int c) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
            mplew.write(72);
            mplew.writeInt(gid);
            mplew.writeInt(cid);
            mplew.writeInt(c);

            return mplew.getPacket();
        }

        public static byte[] changeRank(MapleGuildCharacter mgc) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
            mplew.write(70);
            mplew.writeInt(mgc.getGuildId());
            mplew.writeInt(mgc.getId());
            mplew.write(mgc.getGuildRank());

            return mplew.getPacket();
        }

        public static byte[] rankTitleChange(int gid, String[] ranks) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
            mplew.write(68);
            mplew.writeInt(gid);
            for (String r : ranks) {
                mplew.writeMapleAsciiString(r);
            }

            return mplew.getPacket();
        }

        public static byte[] guildEmblemChange(int gid, short bg, byte bgcolor, short logo, byte logocolor) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
            mplew.write(73);
            mplew.writeInt(gid);
            mplew.writeShort(bg);
            mplew.write(bgcolor);
            mplew.writeShort(logo);
            mplew.write(logocolor);

            return mplew.getPacket();
        }

        public static byte[] updateGP(int gid, int GP, int glevel) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
            mplew.write(79);
            mplew.writeInt(gid);
            mplew.writeInt(GP);
            mplew.writeInt(glevel);

            return mplew.getPacket();
        }

        public static byte[] guildNotice(int gid, String notice) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
            mplew.write(75);
            mplew.writeInt(gid);
            mplew.writeMapleAsciiString(notice);

            return mplew.getPacket();
        }

        public static byte[] guildMemberLevelJobUpdate(MapleGuildCharacter mgc) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
            mplew.write(66);
            mplew.writeInt(mgc.getGuildId());
            mplew.writeInt(mgc.getId());
            mplew.writeInt(mgc.getLevel());
            mplew.writeInt(mgc.getJobId());

            return mplew.getPacket();
        }

        public static byte[] guildMemberOnline(int gid, int cid, boolean bOnline) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
            mplew.write(67);
            mplew.writeInt(gid);
            mplew.writeInt(cid);
            mplew.write(bOnline ? 1 : 0);

            return mplew.getPacket();
        }

        public static byte[] showGuildRanks(int npcid, List<GuildRankingInfo> all) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
            mplew.write(0x50);
            mplew.writeInt(npcid);
            if (all.isEmpty()) { // not sure what happens if we have no guilds but i think we dc from loading null info
                mplew.writeInt(0);
                return mplew.getPacket();
            }
            mplew.writeInt(all.size());
            int rank = 1;
            for (GuildRankingInfo info : all) {
                mplew.writeShort(rank); // sniffed this, this shouldn't be 0 because it's the rank. weird how in v83 it did this automagically.
                mplew.writeMapleAsciiString(info.getName());
                mplew.writeInt(info.getGP());
                mplew.writeInt(info.getLogo());
                mplew.writeInt(info.getLogoColor());
                mplew.writeInt(info.getLogoBg());
                mplew.writeInt(info.getLogoBgColor());
                rank++;
            }
            return mplew.getPacket();
        }

        public static byte[] guildSkillPurchased(int gid, int sid, int level, long expiration, String purchase, String activate) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
            mplew.write(85);
            mplew.writeInt(gid);
            mplew.writeInt(sid);
            mplew.writeShort(level);
            mplew.writeLong(PacketHelper.getTime(expiration));
            mplew.writeMapleAsciiString(purchase);
            mplew.writeMapleAsciiString(activate);

            return mplew.getPacket();
        }

        public static byte[] guildLeaderChanged(int gid, int oldLeader, int newLeader, int allianceId) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
            mplew.write(89);
            mplew.writeInt(gid);
            mplew.writeInt(oldLeader);
            mplew.writeInt(newLeader);
            mplew.write(1); //new rank lol
            mplew.writeInt(allianceId);

            return mplew.getPacket();
        }

        public static byte[] denyGuildInvitation(String charname) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
            mplew.write(61);
            mplew.writeMapleAsciiString(charname);

            return mplew.getPacket();
        }

        public static byte[] genericGuildMessage(byte code) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
            mplew.write(code);
            if (code == 87) {
                mplew.writeInt(0);
            }
            if (code == 3 || code == 59 || code == 60 || code == 61 || code == 84 || code == 87) {
                mplew.writeMapleAsciiString("");
            }
            // 1: Request for Guild Name
            // 18: Set Emblem
            // 3: Would you like to create %s Guild?
            // 34: The name is already in use... Please try other ones....
            // 39: Already joined the guild.
            // 41: You cannot make a guild, due to the limitation of minimum level requirement.
            // 42: Somebody has disagreed to form a guild...  Please come back to me when you meet with the right people...  You can only make a guild after getting agreement from all...
            // 46: Already joined the guild.
            // 47: The guild, you are trying to join, has already reached the max number of users.
            // 48: The character cannot be found in the current channel.
            // 37/44/58: The problem has happened during the process of forming the guild... Plese try again later...
            // 51: You are not in the guild.
            // 54: You are not in the guild.
            // 59: %s is currently not accepting guild invite message.
            // 60: '%s' is taking care of another invitation.
            // 61: %s has denied your guild invitation.
            // 62: Admin cannot make a guild.
            // 86: Extending Guild Skill failed.
            // 81: There are less than 6 members remaining, so the quest cannot continue. Your Guild Quest will end in 5 seconds.
            // 82: The user that registered has disconnected, so the quest cannot continue. Your Guild Quest will end in 5 seconds.
            // default: The guild request has not been accepted, due to unknown reason.
            // 84/87: idk

            return mplew.getPacket();
        }

        public static byte[] BBSThreadList(final List<MapleBBSThread> bbs, int start) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.BBS_OPERATION.getValue());
            mplew.write(6);
            if (bbs == null) {
                mplew.write(0);
                mplew.writeLong(0);
                return mplew.getPacket();
            }
            int threadCount = bbs.size();
            MapleBBSThread notice = null;
            for (MapleBBSThread b : bbs) {
                if (b.isNotice()) { //notice
                    notice = b;
                    break;
                }
            }
            mplew.write(notice == null ? 0 : 1);
            if (notice != null) { //has a notice
                addThread(mplew, notice);
            }
            if (threadCount < start) { //seek to the thread before where we start
                start = 0; //uh, we're trying to start at a place past possible
            }
            mplew.writeInt(threadCount); //each page has 10 threads, start = page # in packet but not here
            final int pages = Math.min(10, threadCount - start);
            mplew.writeInt(pages);
            for (int i = 0; i < pages; i++) {
                addThread(mplew, bbs.get(start + i)); //because 0 = notice
            }

            return mplew.getPacket();
        }

        private static void addThread(MaplePacketLittleEndianWriter mplew, MapleBBSThread rs) {
            mplew.writeInt(rs.localthreadID);
            mplew.writeInt(rs.ownerID);
            mplew.writeMapleAsciiString(rs.name);
            mplew.writeLong(PacketHelper.getKoreanTimestamp(rs.timestamp));
            mplew.writeInt(rs.icon);
            mplew.writeInt(rs.getReplyCount());
        }

        public static byte[] showThread(MapleBBSThread thread) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.BBS_OPERATION.getValue());
            mplew.write(7);
            mplew.writeInt(thread.localthreadID);
            mplew.writeInt(thread.ownerID);
            mplew.writeLong(PacketHelper.getKoreanTimestamp(thread.timestamp));
            mplew.writeMapleAsciiString(thread.name);
            mplew.writeMapleAsciiString(thread.text);
            mplew.writeInt(thread.icon);
            mplew.writeInt(thread.getReplyCount());
            for (MapleBBSReply reply : thread.replies.values()) {
                mplew.writeInt(reply.replyid);
                mplew.writeInt(reply.ownerID);
                mplew.writeLong(PacketHelper.getKoreanTimestamp(reply.timestamp));
                mplew.writeMapleAsciiString(reply.content);
            }

            return mplew.getPacket();
        }
    }

    public static class PartyPacket {

        public static byte[] partyCreated(int partyid) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
            mplew.write(12);
            mplew.writeInt(partyid);
            mplew.writeInt(999999999);
            mplew.writeInt(999999999);
            mplew.writeInt(0);
            mplew.writeShort(0);
            mplew.writeShort(0);
            mplew.write(0);
            mplew.write(1);

            return mplew.getPacket();
        }

        public static byte[] partyInvite(MapleCharacter from) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
            mplew.write(4);
            mplew.writeInt(from.getParty() == null ? 0 : from.getParty().getId());
            mplew.writeMapleAsciiString(from.getName());
            mplew.writeInt(from.getLevel());
            mplew.writeInt(from.getJob());
            mplew.writeInt(0);
            mplew.write(0); // IDA is int, string, int, int, int, byte (NOT int, string, int, int, byte, int)
            return mplew.getPacket();
        }

        public static byte[] partyRequestInvite(MapleCharacter from) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
            mplew.write(7);
            mplew.writeInt(from.getId());
            mplew.writeMapleAsciiString(from.getName());
            mplew.writeInt(from.getLevel());
            mplew.writeInt(from.getJob());
            mplew.writeInt(0); // no clue what this int is for
            return mplew.getPacket();
        }

        public static byte[] partyStatusMessage(int message, String charname) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            // 13: Already have joined a party.
            // 14: A beginner can't create a party.
            // 17: You have yet to join a party.
            // 20: You have joined the party.
            // 21: Already have joined a party.
            // 22: The party you're trying to join is already in full capacity.
            // 26: You have invited '%s' to your party.
            // 33: Cannot kick another user in this map
            // 36: This can only be given to a party member within the vicinity.
            // 37: Unable to hand over the leadership post; No party member is currently within the vicinity of the party leader.
            // 38: You may only change with the party member that's on the same channel.
            // 40: As a GM, you're forbidden from creating a party.
            // 45: The party leader has isChanged the party join request acceptance setting.
            // 46: Party settings could not be isChanged. Please try again later.
            // 51: Cannot be done in the current map.
            // 52: You've requested to join %s's party.
            // default: Your request for a party didn't work due to an unexpected error.
            mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
            mplew.write(message);
            if (message == 26 || message == 52) {
                mplew.writeMapleAsciiString(charname);
            } else if (message == 45) {
                mplew.write(0); // some mode?..
            }

            return mplew.getPacket();
        }

        public static void addPartyStatus(int forchannel, MapleParty party, MaplePacketLittleEndianWriter lew, boolean leaving) {
            addPartyStatus(forchannel, party, lew, leaving, false);
        }

        // 202 + 24 + 120
        public static void addPartyStatus(int forchannel, MapleParty party, MaplePacketLittleEndianWriter lew, boolean leaving, boolean exped) {
            List<MaplePartyCharacter> partymembers;
            if (party == null) {
                partymembers = new ArrayList<>();
            } else {
                partymembers = new ArrayList<>(party.getMembers());
            }
            while (partymembers.size() < 6) {
                partymembers.add(new MaplePartyCharacter());
            }
            for (MaplePartyCharacter partychar : partymembers) {
                lew.writeInt(partychar.getId()); // 24
            }
            for (MaplePartyCharacter partychar : partymembers) {
                lew.writeAsciiString(partychar.getName(), 13); // 78
            }
            for (MaplePartyCharacter partychar : partymembers) {
                lew.writeInt(partychar.getJobId()); // 24
            }
            for (MaplePartyCharacter partychar : partymembers) {
                lew.writeInt(0); // 24
            }
            for (MaplePartyCharacter partychar : partymembers) {
                lew.writeInt(partychar.getLevel()); // 24
            }
            for (MaplePartyCharacter partychar : partymembers) {
                lew.writeInt(partychar.isOnline() ? (partychar.getChannel() - 1) : -2); //24
            }
            for (MaplePartyCharacter partychar : partymembers) {
                lew.writeInt(0); //24
            }
            lew.writeInt(party == null ? 0 : party.getLeader().getId());
            if (exped) {
                return;
            }
            for (MaplePartyCharacter partychar : partymembers) {
                lew.writeInt(partychar.getChannel() == forchannel ? partychar.getMapid() : 0);  // 24
            }
            for (MaplePartyCharacter partychar : partymembers) { // 120
                if (partychar.getChannel() == forchannel && !leaving) {
                    lew.writeInt(partychar.getDoorTown());
                    lew.writeInt(partychar.getDoorTarget());
                    lew.writeInt(partychar.getDoorSkill());
                    lew.writeInt(partychar.getDoorPosition().x);
                    lew.writeInt(partychar.getDoorPosition().y);
                } else {
                    lew.writeInt(leaving ? 999999999 : 0);
                    lew.writeLong(leaving ? 999999999 : 0);
                    lew.writeLong(leaving ? -1 : 0);
                }
            }
            lew.write(1);
        }

        public static byte[] updateParty(int forChannel, MapleParty party, PartyOperation op, MaplePartyCharacter target) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
            switch (op) {
                case DISBAND:
                case EXPEL:
                case LEAVE:
                    mplew.write(16);
                    mplew.writeInt(party.getId());
                    mplew.writeInt(target.getId());
                    mplew.write(op == PartyOperation.DISBAND ? 0 : 1);
                    if (op != PartyOperation.DISBAND) {
                        mplew.write(op == PartyOperation.EXPEL ? 1 : 0);
                        mplew.writeMapleAsciiString(target.getName());
                        addPartyStatus(forChannel, party, mplew, op == PartyOperation.LEAVE);
                    }
                    break;
                case JOIN:
                    mplew.write(19);
                    mplew.writeInt(party.getId());
                    mplew.writeMapleAsciiString(target.getName());
                    addPartyStatus(forChannel, party, mplew, false);
                    break;
                case SILENT_UPDATE:
                case LOG_ONOFF:
                    mplew.write(11);
                    mplew.writeInt(party.getId());
                    addPartyStatus(forChannel, party, mplew, op == PartyOperation.LOG_ONOFF);
                    break;
                case CHANGE_LEADER:
                case CHANGE_LEADER_DC:
                    mplew.write(35);
                    mplew.writeInt(target.getId());
                    mplew.write(op == PartyOperation.CHANGE_LEADER_DC ? 1 : 0);
                    break;
            }

            return mplew.getPacket();
        }

        public static byte[] partyPortal(int townId, int targetId, int skillId, Point position, boolean animation) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
            mplew.write(61);
            mplew.write(animation ? 0 : 1);
            mplew.writeInt(townId);
            mplew.writeInt(targetId);
            mplew.writeInt(skillId);
            mplew.writePos(position);

            return mplew.getPacket();
        }

        //no clue for below atm
        public static byte[] getPartyListing(final PartySearchType pst) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
            mplew.write(GameConstants.GMS ? 0x93 : 0x4D);
            mplew.writeInt(pst.id);
            final List<PartySearch> parties = World.Party.searchParty(pst);
            mplew.writeInt(parties.size());
            for (PartySearch party : parties) {
                mplew.writeInt(0); //ive no clue,either E8 72 94 00 or D8 72 94 00
                mplew.writeInt(2); //again, no clue, seems to remain constant?
                if (pst.exped) {
                    MapleExpedition me = World.Party.getExped(party.getId());
                    mplew.writeInt(me.getType().maxMembers);
                    mplew.writeInt(party.getId());
                    mplew.writeAsciiString(party.getName(), 48);
                    for (int i = 0; i < 5; i++) { //all parties in the exped other than the leader
                        if (i < me.getParties().size()) {
                            MapleParty part = World.Party.getParty(me.getParties().get(i));
                            if (part != null) {
                                addPartyStatus(-1, part, mplew, false, true);
                            } else {
                                mplew.writeZeroBytes(202); //length of the addPartyStatus.
                            }
                        } else {
                            mplew.writeZeroBytes(202); //length of the addPartyStatus.
                        }
                    }
                } else {
                    mplew.writeInt(0);
                    mplew.writeInt(party.getId());
                    mplew.writeAsciiString(party.getName(), 48);
                    addPartyStatus(-1, World.Party.getParty(party.getId()), mplew, false, true); //if exped, send 0, if not then skip
                }

                mplew.writeShort(0); //wonder if this goes here or at bottom
            }

            return mplew.getPacket();
        }

        public static byte[] partyListingAdded(final PartySearch ps) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.PARTY_OPERATION.getValue());
            mplew.write(93);
            mplew.writeInt(ps.getType().id);
            mplew.writeInt(0); //ive no clue,either 48 DB 60 00 or 18 DB 60 00
            mplew.writeInt(1);
            if (ps.getType().exped) {
                MapleExpedition me = World.Party.getExped(ps.getId());
                mplew.writeInt(me.getType().maxMembers);
                mplew.writeInt(ps.getId());
                mplew.writeAsciiString(ps.getName(), 48);
                for (int i = 0; i < 5; i++) { //all parties in the exped other than the leader
                    if (i < me.getParties().size()) {
                        MapleParty party = World.Party.getParty(me.getParties().get(i));
                        if (party != null) {
                            addPartyStatus(-1, party, mplew, false, true);
                        } else {
                            mplew.writeZeroBytes(202); //length of the addPartyStatus.
                        }
                    } else {
                        mplew.writeZeroBytes(202); //length of the addPartyStatus.
                    }
                }
            } else {
                mplew.writeInt(0); //doesn't matter
                mplew.writeInt(ps.getId());
                mplew.writeAsciiString(ps.getName(), 48);
                addPartyStatus(-1, World.Party.getParty(ps.getId()), mplew, false, true); //if exped, send 0, if not then skip
            }
            mplew.writeShort(0); //wonder if this goes here or at bottom

            return mplew.getPacket();
        }

        public static byte[] showMemberSearch(List<MapleCharacter> chr) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.MEMBER_SEARCH.getValue());
            mplew.write(chr.size()); // > 0 (do)
            for (MapleCharacter c : chr) {
                mplew.writeInt(c.getId());
                mplew.writeMapleAsciiString(c.getName());
                mplew.writeShort(c.getJob());
                mplew.writeShort(c.getLevel()); // this is short, but isn't level? wtf is this?
                mplew.write(c.getLevel()); // this is byte, and is used for level for some fucked up reason
            }
            return mplew.getPacket();
        }

        public static byte[] showPartySearch(List<MapleParty> chr) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.PARTY_SEARCH.getValue());
            mplew.write(chr.size());
            for (MapleParty c : chr) {
                mplew.writeInt(c.getId());
                mplew.writeMapleAsciiString(c.getLeader().getName());
                mplew.write(c.getLeader().getLevel());
                mplew.write(c.getLeader().isOnline() ? 1 : 0);
                mplew.write(c.getMembers().size());
                for (MaplePartyCharacter ch : c.getMembers()) {
                    mplew.writeInt(ch.getId());
                    mplew.writeMapleAsciiString(ch.getName());
                    mplew.writeShort(ch.getJobId());
                    mplew.writeShort(ch.getLevel());
                    mplew.write(ch.getLevel()); // what in the actual fuck nexon
                    mplew.write(ch.isOnline() ? 1 : 0); // required users
                }
            }
            return mplew.getPacket();
        }
    }

    public static class ExpeditionPacket {

        public static byte[] expeditionStatus(final MapleExpedition me, boolean created) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.EXPEDITION_OPERATION.getValue());
            mplew.write(created ? 74 : 76); // 72(silent), 74(A new expedition has been created), 76("You have joined the expedition)
            mplew.writeInt(me.getType().exped);
            mplew.writeInt(0); //eh?
            for (int i = 0; i < 5; i++) { //all parties in the exped other than the leader
                if (i < me.getParties().size()) {
                    MapleParty party = World.Party.getParty(me.getParties().get(i));
                    if (party != null) {
                        PartyPacket.addPartyStatus(-1, party, mplew, false, true);
                    } else {
                        mplew.writeZeroBytes(202); //length of the addPartyStatus.
                    }
                } else {
                    mplew.writeZeroBytes(202); //length of the addPartyStatus.
                }
            }
            //mplew.writeShort(0); //wonder if this goes here or at bottom

            return mplew.getPacket();
        }

        public static byte[] expeditionError(final int errcode, final String name) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            // 0 : '<Name>' could not be found in the current server.
            // 1 : Admins can only invite other admins.
            // 2 : '<Name>' is already in a party.
            // 3 : '<Name>' does not meet the level requirement for the expedition.
            // 4 : '<Name>' is currently not accepting any expedition invites.
            // 5 : '<Name>' is taking care of another invitation.
            // 6 : You have already invited '<Name>' to the expedition.
            // 7 : '<Name>' has been invited to the expedition.
            mplew.writeShort(SendPacketOpcode.EXPEDITION_OPERATION.getValue());
            mplew.write(88);
            mplew.writeInt(errcode);
            mplew.writeMapleAsciiString(name);

            return mplew.getPacket();
        }

        public static byte[] expeditionMessage(final int code) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            // 73 : Silent remove
            // 77 : You have joined the expedition.
            // 80 : You have left the expedition.
            // 82 : You have been kicked out of the expedition.
            // 83 : The Expedition has been disbanded.
            // 89 : You cannot create a Cygnus Expedition because you have reached the limit on Cygnus Clear Points.\r\nCygnus can be defeated up to 3 times a week, and the Clear
            // 90 : You cannot invite this character to a Cygnus Expedition because he has reached the limit on Cygnus Clear Points. Cygnus can be defeated up to 3 times a week, and the Clear Points reset on Wednesdays at midnight.
            // 91 : You cannot join the Cygnus Expedition because you have reached the limit on Cygnus Clear Points.\r\nCygnus can be defeated up to 3 times a week, and the Clear Points reset on Wednesdays at midnight.
            mplew.writeShort(SendPacketOpcode.EXPEDITION_OPERATION.getValue());
            mplew.write(code);

            return mplew.getPacket();
        }

        public static byte[] expeditionJoined(final String name) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.EXPEDITION_OPERATION.getValue());
            mplew.write(75); // 75 : '<Name>' has joined the expedition.
            mplew.writeMapleAsciiString(name);

            return mplew.getPacket();
        }

        public static byte[] expeditionLeft(final String name) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.EXPEDITION_OPERATION.getValue());
            mplew.write(79); // 79 : '<Name>' has left the expedition.
            mplew.writeMapleAsciiString(name);
            // 81 : '<Name>' has been kicked out of the expedition.

            return mplew.getPacket();
        }

        public static byte[] expeditionLeaderChanged(final int newLeader) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.EXPEDITION_OPERATION.getValue());
            mplew.write(84);
            mplew.writeInt(newLeader);

            return mplew.getPacket();
        }

        public static byte[] expeditionUpdate(final int partyIndex, final MapleParty party) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.EXPEDITION_OPERATION.getValue());
            mplew.write(85);
            mplew.writeInt(0); //lol?
            mplew.writeInt(partyIndex);
            if (party == null) {
                mplew.writeZeroBytes(202); //length of the addPartyStatus.
            } else {
                PartyPacket.addPartyStatus(-1, party, mplew, false, true);
            }
            return mplew.getPacket();
        }

        public static byte[] expeditionInvite(MapleCharacter from, int exped) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.EXPEDITION_OPERATION.getValue());
            mplew.write(87);
            mplew.writeInt(from.getLevel());
            mplew.writeInt(from.getJob());
            mplew.writeMapleAsciiString(from.getName());
            mplew.writeInt(exped);

            return mplew.getPacket();
        }
    }

    public static class BuddylistPacket {

        public static byte[] updateBuddylist(Collection<BuddyListEntry> buddylist) {
            return updateBuddylist(buddylist, 7);
        }

        public static byte[] updateBuddylist(Collection<BuddyListEntry> buddylist, int deleted) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
            mplew.write(deleted); // 7, 10, 18, // 8 = update, diff structure
            mplew.write(buddylist.size());
            for (BuddyListEntry buddy : buddylist) {
                mplew.writeInt(buddy.getCharacterId());
                mplew.writeAsciiString(buddy.getName(), 13);
                mplew.write(buddy.isVisible() ? 0 : 1);
                mplew.writeInt(buddy.getChannel() == -1 ? -1 : (buddy.getChannel() - 1));
                mplew.writeAsciiString(buddy.getGroup(), 17);
            }
            for (int x = 0; x < buddylist.size(); x++) {
                mplew.writeInt(0);
            }

            return mplew.getPacket();
        }

        public static byte[] updateBuddyChannel(int characterid, int channel) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
            mplew.write(20);
            mplew.writeInt(characterid);
            mplew.write(0);
            mplew.writeInt(channel);

            return mplew.getPacket();
        }

        public static byte[] requestBuddylistAdd(int cidFrom, String nameFrom, int levelFrom, int jobFrom) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
            mplew.write(9);
            mplew.writeInt(cidFrom);
            mplew.writeMapleAsciiString(nameFrom);
            mplew.writeInt(levelFrom);
            mplew.writeInt(jobFrom);
            mplew.writeInt(0);
            mplew.writeInt(cidFrom);
            mplew.writeAsciiString(nameFrom, 13);
            mplew.write(1);
            mplew.writeInt(0);
            mplew.writeAsciiString("ETC", 17);
            mplew.write(0);

            return mplew.getPacket();
        }

        public static byte[] updateBuddyCapacity(int capacity) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
            mplew.write(21);
            mplew.write(capacity);

            return mplew.getPacket();
        }

        public static byte[] buddylistMessage(byte message) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            // 11: Your buddy list is full.
            // 12: The user's buddy list is full
            // 13: That character is already registered as your buddy.
            // 14: Gamemaster is not available as a buddy.
            // 15: That character is not registered.
            // 23: You've already made the Friend Request. Please try again later.
            mplew.writeShort(SendPacketOpcode.BUDDYLIST.getValue());
            mplew.write(message);

            return mplew.getPacket();
        }
    }

    public static class FamilyPacket {

        public static byte[] getFamilyData() {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.FAMILY.getValue());
            MapleFamilyBuff[] entries = MapleFamilyBuff.values();
            mplew.writeInt(entries.length); // Number of worldevents

            for (MapleFamilyBuff entry : entries) {
                mplew.write(entry.type);
                mplew.writeInt(entry.rep);
                mplew.writeInt(1); //i think it always has to be this
                mplew.writeMapleAsciiString(entry.name);
                mplew.writeMapleAsciiString(entry.desc);
            }
            return mplew.getPacket();
        }

        public static byte[] getFamilyInfo(MapleCharacter chr) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.OPEN_FAMILY.getValue());
            mplew.writeInt(chr.getCurrentRep()); //rep
            mplew.writeInt(chr.getTotalRep()); // total rep
            mplew.writeInt(chr.getTotalRep()); //rep recorded today
            mplew.writeShort(chr.getNoJuniors());
            mplew.writeShort(2);
            mplew.writeShort(chr.getNoJuniors());
            MapleFamily family = World.Family.getFamily(chr.getFamilyId());
            if (family != null) {
                mplew.writeInt(family.getLeaderId()); //??? 9D 60 03 00
                mplew.writeMapleAsciiString(family.getLeaderName());
                mplew.writeMapleAsciiString(family.getNotice()); //message?
            } else {
                mplew.writeLong(0);
            }
            List<Integer> b = chr.usedBuffs();
            mplew.writeInt(b.size());
            for (int ii : b) {
                mplew.writeInt(ii); //buffid
                mplew.writeInt(1); //times used, but if its 0 it still records!
            }

            return mplew.getPacket();
        }

        public static void addFamilyCharInfo(MapleFamilyCharacter ldr, MaplePacketLittleEndianWriter mplew) {
            mplew.writeInt(ldr.getId());
            mplew.writeInt(ldr.getSeniorId());
            mplew.writeShort(ldr.getJobId());
            mplew.writeShort(0);
            mplew.write(ldr.getLevel());
            mplew.write(ldr.isOnline() ? 1 : 0);
            mplew.writeInt(ldr.getCurrentRep());
            mplew.writeInt(ldr.getTotalRep());
            mplew.writeInt(ldr.getTotalRep()); //recorded rep to senior
            mplew.writeInt(ldr.getTotalRep()); //then recorded rep to sensen
            mplew.writeInt(Math.max(ldr.getChannel(), 0)); //channel
            mplew.writeInt(0); // time online in seconds
            mplew.writeMapleAsciiString(ldr.getName());
        }

        public static byte[] getFamilyPedigree(MapleCharacter chr) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.SEND_PEDIGREE.getValue());
            mplew.writeInt(chr.getId());
            MapleFamily family = World.Family.getFamily(chr.getFamilyId());
            int descendants = 2, gens = 0, generations = 0;
            if (family == null) {
                mplew.writeInt(2);
                addFamilyCharInfo(new MapleFamilyCharacter(chr, 0, 0, 0, 0), mplew); //leader
            } else {
                mplew.writeInt(family.getMFC(chr.getId()).getPedigree().size() + 1); //+ 1 for leader, but we don't want leader seeing all msgs
                addFamilyCharInfo(family.getMFC(family.getLeaderId()), mplew);

                if (chr.getSeniorId() > 0) {
                    MapleFamilyCharacter senior = family.getMFC(chr.getSeniorId());
                    if (senior != null) {
                        if (senior.getSeniorId() > 0) {
                            addFamilyCharInfo(family.getMFC(senior.getSeniorId()), mplew);
                        }
                        addFamilyCharInfo(senior, mplew);
                    }
                }
            }
            addFamilyCharInfo(chr.getMFC() == null ? new MapleFamilyCharacter(chr, 0, 0, 0, 0) : chr.getMFC(), mplew);
            if (family != null) {
                if (chr.getSeniorId() > 0) {
                    MapleFamilyCharacter senior = family.getMFC(chr.getSeniorId());
                    if (senior != null) {
                        if (senior.getJunior1() > 0 && senior.getJunior1() != chr.getId()) {
                            addFamilyCharInfo(family.getMFC(senior.getJunior1()), mplew);
                        } else if (senior.getJunior2() > 0 && senior.getJunior2() != chr.getId()) {
                            addFamilyCharInfo(family.getMFC(senior.getJunior2()), mplew);
                        }
                    }
                }
                if (chr.getJunior1() > 0) {
                    MapleFamilyCharacter junior = family.getMFC(chr.getJunior1());
                    if (junior != null) {
                        addFamilyCharInfo(junior, mplew);
                    }
                }
                if (chr.getJunior2() > 0) {
                    MapleFamilyCharacter junior = family.getMFC(chr.getJunior2());
                    if (junior != null) {
                        addFamilyCharInfo(junior, mplew);
                    }
                }
                if (chr.getJunior1() > 0) {
                    MapleFamilyCharacter junior = family.getMFC(chr.getJunior1());
                    if (junior != null) {
                        if (junior.getJunior1() > 0 && family.getMFC(junior.getJunior1()) != null) {
                            gens++;
                            addFamilyCharInfo(family.getMFC(junior.getJunior1()), mplew);
                        }
                        if (junior.getJunior2() > 0 && family.getMFC(junior.getJunior2()) != null) {
                            gens++;
                            addFamilyCharInfo(family.getMFC(junior.getJunior2()), mplew);
                        }
                    }
                }
                if (chr.getJunior2() > 0) {
                    MapleFamilyCharacter junior = family.getMFC(chr.getJunior2());
                    if (junior != null) {
                        if (junior.getJunior1() > 0 && family.getMFC(junior.getJunior1()) != null) {
                            gens++;
                            addFamilyCharInfo(family.getMFC(junior.getJunior1()), mplew);
                        }
                        if (junior.getJunior2() > 0 && family.getMFC(junior.getJunior2()) != null) {
                            gens++;
                            addFamilyCharInfo(family.getMFC(junior.getJunior2()), mplew);
                        }
                    }
                }
                generations = family.getMemberSize();
            }
            mplew.writeLong(2 + gens); //no clue
            mplew.writeInt(gens); //2?
            mplew.writeInt(-1);
            mplew.writeInt(generations);
            if (family != null) {
                if (chr.getJunior1() > 0) {
                    MapleFamilyCharacter junior = family.getMFC(chr.getJunior1());
                    if (junior != null) {
                        if (junior.getJunior1() > 0 && family.getMFC(junior.getJunior1()) != null) {
                            mplew.writeInt(junior.getJunior1());
                            mplew.writeInt(family.getMFC(junior.getJunior1()).getDescendants());
                        }
                        if (junior.getJunior2() > 0 && family.getMFC(junior.getJunior2()) != null) {
                            mplew.writeInt(junior.getJunior2());
                            mplew.writeInt(family.getMFC(junior.getJunior2()).getDescendants());
                        }
                    }
                }
                if (chr.getJunior2() > 0) {
                    MapleFamilyCharacter junior = family.getMFC(chr.getJunior2());
                    if (junior != null) {
                        if (junior.getJunior1() > 0 && family.getMFC(junior.getJunior1()) != null) {
                            mplew.writeInt(junior.getJunior1());
                            mplew.writeInt(family.getMFC(junior.getJunior1()).getDescendants());
                        }
                        if (junior.getJunior2() > 0 && family.getMFC(junior.getJunior2()) != null) {
                            mplew.writeInt(junior.getJunior2());
                            mplew.writeInt(family.getMFC(junior.getJunior2()).getDescendants());
                        }
                    }
                }
            }

            List<Integer> b = chr.usedBuffs();
            mplew.writeInt(b.size());
            for (int ii : b) {
                mplew.writeInt(ii); //buffid
                mplew.writeInt(1); //times used, but if 0 it still records!
            }
            mplew.writeShort(2);
            mplew.writeZeroBytes(100);
            return mplew.getPacket();
        }

        public static byte[] getFamilyMsg(byte type, int meso) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            // 1: You have severed ties with %s.\r\nYour family relationship has ended.
            // 64: You cannot add this character as a Junior.
            // 65: The name you entered is incorrect or he/she is currently not logged in.
            // 66: You belong to the same family.
            // 67: You do not belong to the same family.
            // 69: The character you wish to add as\r\na Junior must be in the same map.
            // 70: This character is already a Junior of another character.
            // 71: The Junior you wish to add\r\nmust be at a lower rank.
            // 72: The gap between you and your\r\njunior must be within 20 levels.
            // 73: Another character has requested to add this character.\r\nPlease try again later.
            // 74: Another character has requested a summon.\r\nPlease try again later.
            // 75: The summons has failed. Your current location or state does not allow a summons.
            // 76: The family cannot extend more than 1000 generations from above and below.
            // 77: The Junior you wish to add\r\nmust be over Level 10.
            // 78: You cannot add a Junior \r\nthat has requested to change worlds.
            // 79: You cannot add a Junior \r\nsince you've requested to change worlds.
            // 80: Separation is not possible due to insufficient Mesos.\r\nYou will need %d Mesos to\r\nseparate with a Senior.
            // 81: Separation is not possible due to insufficient Mesos.\r\nYou will need %d Mesos to\r\nseparate with a Junior.
            // 82: The Entitlement does not apply because your level does not match the corresponding area.
            mplew.writeShort(SendPacketOpcode.FAMILY_MESSAGE.getValue());
            mplew.writeInt(type);
            mplew.writeInt(meso); // used on type 80/81

            return mplew.getPacket();
        }

        public static byte[] sendFamilyInvite(int cid, int otherLevel, int otherJob, String inviter) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.FAMILY_INVITE.getValue());
            mplew.writeInt(cid); //the inviter
            mplew.writeInt(otherLevel);
            mplew.writeInt(otherJob);
            mplew.writeInt(0);
            mplew.writeMapleAsciiString(inviter);

            return mplew.getPacket();
        }

        public static byte[] sendFamilyJoinResponse(boolean accepted, String added) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.FAMILY_JUNIOR.getValue());
            mplew.write(accepted ? 1 : 0);
            mplew.writeMapleAsciiString(added);

            return mplew.getPacket();
        }

        public static byte[] getSeniorMessage(String name) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.SENIOR_MESSAGE.getValue());
            mplew.writeMapleAsciiString(name);

            return mplew.getPacket();
        }

        public static byte[] changeRep(int r, String name) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.REP_INCREASE.getValue());
            mplew.writeInt(r);
            mplew.writeMapleAsciiString(name);

            return mplew.getPacket();
        }

        public static byte[] familyLoggedIn(boolean online, String name) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.FAMILY_LOGGEDIN.getValue());
            mplew.write(online ? 1 : 0);
            mplew.writeMapleAsciiString(name);

            return mplew.getPacket();
        }

        public static byte[] familyBuff(int type, int buffnr, int amount, int time) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(SendPacketOpcode.FAMILY_BUFF.getValue());
            mplew.write(type);
            if (type >= 2 && type <= 4) {
                mplew.writeInt(buffnr);
                //first int = exp, second int = drop
                mplew.writeInt(type == 3 ? 0 : amount);
                mplew.writeInt(type == 2 ? 0 : amount);
                mplew.write(0); // add = 0, minus = 1
                mplew.writeInt(time);
            }
            return mplew.getPacket();
        }

        public static byte[] cancelFamilyBuff() {
            return familyBuff(0, 0, 0, 0);
        }

        public static byte[] familySummonRequest(String name, String mapname) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.FAMILY_USE_REQUEST.getValue());
            mplew.writeMapleAsciiString(name);
            mplew.writeMapleAsciiString(mapname);

            return mplew.getPacket();
        }
    }

    public static class AlliancePacket {

        public static byte[] getAllianceInfo(MapleGuildAlliance alliance) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
            mplew.write(12);
            mplew.write(alliance == null ? 0 : 1); //in an alliance
            if (alliance != null) {
                addAllianceInfo(mplew, alliance);
            }

            return mplew.getPacket();
        }

        private static void addAllianceInfo(MaplePacketLittleEndianWriter mplew, MapleGuildAlliance alliance) {
            mplew.writeInt(alliance.getId());
            mplew.writeMapleAsciiString(alliance.getName());
            for (int i = 1; i <= 5; i++) {
                mplew.writeMapleAsciiString(alliance.getRank(i));
            }
            mplew.write(alliance.getNoGuilds());
            for (int i = 0; i < alliance.getNoGuilds(); i++) {
                mplew.writeInt(alliance.getGuildId(i));
            }
            mplew.writeInt(alliance.getCapacity()); // ????
            mplew.writeMapleAsciiString(alliance.getNotice());
        }

        public static byte[] getGuildAlliance(MapleGuildAlliance alliance) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
            mplew.write(13);
            if (alliance == null) {
                mplew.writeInt(0);
                return mplew.getPacket();
            }
            final int noGuilds = alliance.getNoGuilds();
            MapleGuild[] g = new MapleGuild[noGuilds];
            for (int i = 0; i < alliance.getNoGuilds(); i++) {
                g[i] = World.Guild.getGuild(alliance.getGuildId(i));
                if (g[i] == null) {
                    return CWvsContext.enableActions();
                }
            }
            mplew.writeInt(noGuilds);
            for (MapleGuild gg : g) {
                GuildPacket.getGuildInfo(mplew, gg);
            }
            return mplew.getPacket();
        }

        public static byte[] allianceMemberOnline(int alliance, int gid, int id, boolean online) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
            mplew.write(14);
            mplew.writeInt(alliance);
            mplew.writeInt(gid);
            mplew.writeInt(id);
            mplew.write(online ? 1 : 0);

            return mplew.getPacket();
        }

        public static byte[] removeGuildFromAlliance(MapleGuildAlliance alliance, MapleGuild expelledGuild, boolean expelled) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
            mplew.write(16);
            addAllianceInfo(mplew, alliance);
            GuildPacket.getGuildInfo(mplew, expelledGuild);
            mplew.write(expelled ? 1 : 0); //1 = expelled, 0 = left

            return mplew.getPacket();
        }

        public static byte[] addGuildToAlliance(MapleGuildAlliance alliance, MapleGuild newGuild) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
            mplew.write(18);
            addAllianceInfo(mplew, alliance);
            mplew.writeInt(newGuild.getId());
            GuildPacket.getGuildInfo(mplew, newGuild);
            mplew.write(0); // not here

            return mplew.getPacket();
        }

        public static byte[] sendAllianceInvite(String allianceName, MapleCharacter inviter) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
            mplew.write(3);
            mplew.writeInt(inviter.getGuildId());
            mplew.writeMapleAsciiString(inviter.getName());
            mplew.writeMapleAsciiString(allianceName);

            return mplew.getPacket();
        }

        public static byte[] getAllianceUpdate(MapleGuildAlliance alliance) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
            mplew.write(23);
            addAllianceInfo(mplew, alliance);

            return mplew.getPacket();
        }

        public static byte[] createGuildAlliance(MapleGuildAlliance alliance) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
            mplew.write(15);
            addAllianceInfo(mplew, alliance);
            final int noGuilds = alliance.getNoGuilds();
            MapleGuild[] g = new MapleGuild[noGuilds];
            for (int i = 0; i < alliance.getNoGuilds(); i++) {
                g[i] = World.Guild.getGuild(alliance.getGuildId(i));
                if (g[i] == null) {
                    return CWvsContext.enableActions();
                }
            }
            for (MapleGuild gg : g) {
                GuildPacket.getGuildInfo(mplew, gg);
            }
            return mplew.getPacket();
        }

        public static byte[] updateAlliance(MapleGuildCharacter mgc, int allianceid) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
            mplew.write(24);
            mplew.writeInt(allianceid);
            mplew.writeInt(mgc.getGuildId());
            mplew.writeInt(mgc.getId());
            mplew.writeInt(mgc.getLevel());
            mplew.writeInt(mgc.getJobId());

            return mplew.getPacket();
        }

        public static byte[] updateAllianceLeader(int allianceid, int newLeader, int oldLeader) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
            mplew.write(25);
            mplew.writeInt(allianceid);
            mplew.writeInt(oldLeader);
            mplew.writeInt(newLeader);

            return mplew.getPacket();
        }

        public static byte[] allianceRankChange(int aid, String[] ranks) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.GUILD_OPERATION.getValue());
            mplew.write(26);
            mplew.writeInt(aid);
            for (String r : ranks) {
                mplew.writeMapleAsciiString(r); // x5
            }

            return mplew.getPacket();
        }

        public static byte[] updateAllianceRank(MapleGuildCharacter mgc) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
            mplew.write(27);
            mplew.writeInt(mgc.getId());
            mplew.write(mgc.getAllianceRank());

            return mplew.getPacket();
        }

        public static byte[] changeAllianceNotice(int allianceid, String notice) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
            mplew.write(28);
            mplew.writeInt(allianceid);
            mplew.writeMapleAsciiString(notice);

            return mplew.getPacket();
        }

        public static byte[] disbandAlliance(int alliance) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
            mplew.write(29);
            mplew.writeInt(alliance);

            return mplew.getPacket();
        }

        public static byte[] changeAlliance(MapleGuildAlliance alliance, final boolean in) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
            mplew.write(1);
            mplew.write(in ? 1 : 0);
            mplew.writeInt(in ? alliance.getId() : 0);
            final int noGuilds = alliance.getNoGuilds();
            MapleGuild[] g = new MapleGuild[noGuilds];
            for (int i = 0; i < noGuilds; i++) {
                g[i] = World.Guild.getGuild(alliance.getGuildId(i));
                if (g[i] == null) {
                    return CWvsContext.enableActions();
                }
            }
            mplew.write(noGuilds);
            for (int i = 0; i < noGuilds; i++) {
                mplew.writeInt(g[i].getId());
                //must be world
                Collection<MapleGuildCharacter> members = g[i].getMembers();
                mplew.writeInt(members.size());
                for (MapleGuildCharacter mgc : members) {
                    mplew.writeInt(mgc.getId());
                    mplew.write(in ? mgc.getAllianceRank() : 0);
                }
            }

            return mplew.getPacket();
        }

        public static byte[] changeAllianceLeader(int allianceid, int newLeader, int oldLeader) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
            mplew.write(2);
            mplew.writeInt(allianceid);
            mplew.writeInt(oldLeader);
            mplew.writeInt(newLeader);

            return mplew.getPacket();
        }

        public static byte[] changeGuildInAlliance(MapleGuildAlliance alliance, MapleGuild guild, final boolean add) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
            mplew.write(4);
            mplew.writeInt(add ? alliance.getId() : 0);
            mplew.writeInt(guild.getId());
            Collection<MapleGuildCharacter> members = guild.getMembers();
            mplew.writeInt(members.size());
            for (MapleGuildCharacter mgc : members) {
                mplew.writeInt(mgc.getId());
                mplew.write(add ? mgc.getAllianceRank() : 0);
            }

            return mplew.getPacket();
        }

        public static byte[] changeAllianceRank(int allianceid, MapleGuildCharacter player) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

            mplew.writeShort(SendPacketOpcode.ALLIANCE_OPERATION.getValue());
            mplew.write(5);
            mplew.writeInt(allianceid);
            mplew.writeInt(player.getId());
            mplew.writeInt(player.getAllianceRank());

            return mplew.getPacket();
        }
    }
}
