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
package scripting;

import client.*;
import client.inventory.*;
import client.skill.Skill;
import client.skill.SkillEntry;
import client.skill.SkillFactory;
import com.mysql.cj.api.mysqla.result.Resultset;
import constants.GameConstants;
import constants.ItemConstants;
import constants.Occupations;
import constants.ServerConstants;
import database.DatabaseConnection;
import ecpay.payment.integration.PaymentAIO;
import extensions.temporary.DirectionType;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import handling.channel.handler.HiredMerchantHandler;
import handling.channel.handler.InventoryHandler;
import handling.channel.handler.PlayersHandler;
import handling.login.LoginInformationProvider;
import handling.login.LoginServer;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.World;
import handling.world.exped.ExpeditionType;
import handling.world.guild.MapleGuild;
import handling.world.guild.MapleGuildAlliance;
import jdk.nashorn.internal.objects.NativeArray;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.*;
import server.Timer.WorldTimer;
import server.life.*;
import server.maps.*;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.KoreanDateUtil;
import tools.StringUtil;
import tools.data.MaplePacketLittleEndianWriter;
import tools.packet.CField;
import tools.packet.CField.NPCTalkPacket;
import tools.packet.CField.UIPacket;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.GuildPacket;
import tools.packet.CWvsContext.InfoPacket;
import tools.types.Pair;
import tools.types.Triple;

import javax.script.Invocable;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class NPCConversationManager extends AbstractPlayerInteraction {

    public boolean pendingDisposal = false;
    public long MRushAmount = 700000000000L;
    public long Mesos = getPlayer().getMeso();
    private String getText;
    private final ScriptType type; // -1 = NPC, 0 = start quest, 1 = end quest
    private NPCTalkType lastMsg = null;
    private Invocable iv;

    public NPCConversationManager(MapleClient c, int npc, int questid, String npcscript, ScriptType type, Invocable iv) {
        super(c, npc, questid, npcscript);
        this.type = type;
        this.iv = iv;

    }

    public static void dispose(MapleClient c) {
        c.sendPacket(CWvsContext.enableActions());
        NPCScriptManager.getInstance().getCM(c).dispose();
    }

    public Invocable getIv() {
        return iv;
    }

    public int getNpc() {
        return id;
    }

    public int getQuest() {
        return id2;
    }

    public String getDevNews() throws SQLException {
        StringBuilder ret = new StringBuilder();
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT title, msg, date FROM dev_news ORDER BY id desc LIMIT 5");
        ResultSet rs = ps.executeQuery();
        try {
            while (rs.next()) {
                ret.append("\r\n#e").append(rs.getString("title")).append(" - (").append(rs.getString("date")).append(")#n\r\n").append(rs.getString("msg")).append("\r\n");
            }
        } catch (SQLException ex) {
            Logger.getLogger(NPCConversationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        ps.close();
        rs.close();
        return ret.toString();
    }

    public ScriptType getType() {
        return type;
    }

    public void updateAndroid(boolean itemonly) {
        CField.updateAndroidLook(itemonly, c.getPlayer(), c.getPlayer().getAndroid());
    }

    public void safeDispose() {
        pendingDisposal = true;
    }

    public void dispose() {
        NPCScriptManager.getInstance().dispose(c);
    }

    public void sendNext(String text) {
        sendNext(text, id);
    }

    public void sendNext(String text, int id) {
        if (text.contains("#L")) { //sendNext will dc otherwise!
            sendSimple(text);
            return;
        }
        lastMsg = NPCTalkType.NEXT_PREV;
        c.getSession().writeAndFlush(NPCTalkPacket.getNPCTalk(id, lastMsg, text, "00 01", (byte) 0));
    }


    public void sendPlayerToNpc(String text) {
        sendNextS(text, (byte) 3, id);
    }

    public void sendNextNoESC(String text) {
        sendNextS(text, (byte) 1, id);
    }

    public void sendNextNoESC(String text, int id) {
        sendNextS(text, (byte) 1, id);
    }

    public void sendNextS(String text, byte type) {
        sendNextS(text, type, id);
    }

    public void sendNextS(String text, byte type, int npcid) {
        if (text.contains("#L")) { // will dc otherwise!
            sendSimpleS(text, type);
            return;
        }
        lastMsg = NPCTalkType.NEXT_PREV;
        c.getSession().writeAndFlush(NPCTalkPacket.getNPCTalk(npcid, lastMsg, text, "00 01", type, npcid));
    }

    public void sendPrev(String text) {
        sendPrev(text, id);
    }

    public void sendPrev(String text, int id) {
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        lastMsg = NPCTalkType.NEXT_PREV;
        c.getSession().writeAndFlush(CField.NPCTalkPacket.getNPCTalk(id, lastMsg, text, "01 00", (byte) 0));
    }

    public void sendPrevS(String text, byte type) {
        sendPrevS(text, type, id);
    }

    public void sendPrevS(String text, byte type, int idd) {
        if (text.contains("#L")) { // will dc otherwise!
            sendSimpleS(text, type);
            return;
        }
        lastMsg = NPCTalkType.NEXT_PREV;
        c.getSession().writeAndFlush(NPCTalkPacket.getNPCTalk(id, lastMsg, text, "01 00", type, idd));
    }

    public void sendNextPrev(String text) {
        sendNextPrev(text, id);
    }

    public void sendNextPrev(String text, int id) {
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        lastMsg = NPCTalkType.NEXT_PREV;
        c.getSession().writeAndFlush(NPCTalkPacket.getNPCTalk(id, lastMsg, text, "01 01", (byte) 0));
    }

    public void PlayerToNpc(String text) {
        sendNextPrevS(text, (byte) 3);
    }

    public void sendNextPrevS(String text) {
        sendNextPrevS(text, (byte) 3);
    }

    public void sendNextPrevS(String text, byte type) {
        sendNextPrevS(text, type, id);
    }

    public void sendNextPrevS(String text, byte type, int npcid) {
        if (text.contains("#L")) { // will dc otherwise!
            sendSimpleS(text, type);
            return;
        }
        lastMsg = NPCTalkType.NEXT_PREV;
        c.getSession().writeAndFlush(CField.NPCTalkPacket.getNPCTalk(npcid, lastMsg, text, "01 01", type, npcid));
    }

    public void sendOk(String text) {
        sendOk(text, id);
    }

    public void sendOk(String text, int id) {
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        lastMsg = NPCTalkType.NEXT_PREV;
        c.getSession().writeAndFlush(NPCTalkPacket.getNPCTalk(id, lastMsg, text, "00 00", (byte) 0));
    }

    public void sendOkS(String text, byte type) {
        sendOkS(text, type, id);
    }

    public void sendOkS(String text, byte type, int idd) {
        if (text.contains("#L")) { // will dc otherwise!
            sendSimpleS(text, type);
            return;
        }
        lastMsg = NPCTalkType.NEXT_PREV;
        c.getSession().writeAndFlush(NPCTalkPacket.getNPCTalk(id, lastMsg, text, "00 00", type, idd));
    }

    public void sendYesNo(String text) {
        sendYesNo(text, id);
    }

    public void sendYesNo(String text, int id) {
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        lastMsg = NPCTalkType.YES_NO;
        c.getSession().writeAndFlush(NPCTalkPacket.getNPCTalk(id, lastMsg, text, "", (byte) 0));
    }

    public void sendYesNoS(String text, byte type) {
        sendYesNoS(text, type, id);
    }

    public void sendYesNoS(String text, byte type, int idd) {
        if (text.contains("#L")) { // will dc otherwise!
            sendSimpleS(text, type);
            return;
        }
        lastMsg = NPCTalkType.YES_NO;
        c.getSession().writeAndFlush(NPCTalkPacket.getNPCTalk(id, lastMsg, text, "", type, idd));
    }

    public void sendAcceptDecline(String text) {
        askAcceptDecline(text);
    }

    public void sendAcceptDeclineNoESC(String text) {
        askAcceptDeclineNoESC(text);
    }

    public void askAcceptDecline(String text) {
        askAcceptDecline(text, id);
    }

    public void askAcceptDecline(String text, int id) {
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        lastMsg = NPCTalkType.ACCEPT_DECLINE;
        c.getSession().writeAndFlush(NPCTalkPacket.getNPCTalk(id, lastMsg, text, "", (byte) 0));
    }

    public void askAcceptDeclineNoESC(String text) {
        askAcceptDeclineNoESC(text, id);
    }

    public void askAcceptDeclineNoESC(String text, int id) {
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        lastMsg = NPCTalkType.ACCEPT_DECLINE;
        c.getSession().writeAndFlush(NPCTalkPacket.getNPCTalk(id, lastMsg, text, "", (byte) 1));
    }

    public void askAvatar(String text, int... args) {
        c.getSession().writeAndFlush(NPCTalkPacket.getNPCTalkStyle(id, text, args));
        lastMsg = NPCTalkType.AVATAR;
    }

    public void sendSimple(String text) {
        sendSimple(text, id);
    }

    public void sendSimple(String text, int id) {
        if (!text.contains("#L")) { //sendSimple will dc otherwise!
            sendNext(text);
            return;
        }
        lastMsg = NPCTalkType.SELECTION;
        c.getSession().writeAndFlush(NPCTalkPacket.getNPCTalk(id, lastMsg, text, "", (byte) 0));
    }

    public void sendSimpleS(String text, byte type) {
        sendSimpleS(text, type, id);
    }

    public void sendSimpleS(String text, byte type, int idd) {
        if (!text.contains("#L")) { //sendSimple will dc otherwise!
            sendNextS(text, type);
            return;
        }
        lastMsg = NPCTalkType.SELECTION;
        c.getSession().writeAndFlush(NPCTalkPacket.getNPCTalk(id, NPCTalkType.SELECTION, text, "", type, idd));
    }

    public void sendStyle(String text, int styles[]) {
        c.getSession().writeAndFlush(NPCTalkPacket.getNPCTalkStyle(id, text, styles));
        lastMsg = NPCTalkType.ANDROID;
    }

    public void askAndroid(String text, int... args) {
        c.getSession().writeAndFlush(CField.NPCTalkPacket.getAndroidTalkStyle(id, text, args));
        lastMsg = NPCTalkType.PET;
    }

    public void sendGetNumber(String text, int def, int min, int max) {
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        c.getSession().writeAndFlush(NPCTalkPacket.getNPCTalkNum(id, text, def, min, max));
        lastMsg = NPCTalkType.INPUT_NUMBER;
    }

    public void sendGetText(String text) {
        sendGetText(text, id);
    }

    public void sendGetText(String text, int id) {
        if (text.contains("#L")) { // will dc otherwise!
            sendSimple(text);
            return;
        }
        c.getSession().writeAndFlush(NPCTalkPacket.getNPCTalkText(id, text));
        lastMsg = NPCTalkType.INPUT_TEXT;
    }

    public void setGetText(String text) {
        this.getText = text;
    }

    public String getText() {
        return getText;
    }

    public void setHair(int hair) {
        getPlayer().setHair(hair);
        getPlayer().updateSingleStat(MapleStat.HAIR, hair);
        getPlayer().equipChanged();
    }

    @Override
    public final MapleCharacter getChar() {
        return getPlayer();
    }

    public void setFace(int face) {
        getPlayer().setFace(face);
        getPlayer().updateSingleStat(MapleStat.FACE, face);
        getPlayer().equipChanged();
    }

    public void setSkin(int color) {
        getPlayer().setSkinColor((byte) color);
        getPlayer().updateSingleStat(MapleStat.SKIN, color);
        getPlayer().equipChanged();
    }

    public String getPvPRanks() throws SQLException {
        StringBuilder ret = new StringBuilder();
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT name, pvpKills FROM characters WHERE gm < 3 ORDER BY pvpKills desc LIMIT 10");
        ps.executeQuery();
        ResultSet rs = ps.executeQuery();
        try {
            while (rs.next()) {
                ret.append("\r\n").append("#b").append("Name(IGN) : ").append(rs.getString("name")).append("#k#r").append("        |      Kills : ").append(rs.getInt("pvpKills")).append("#k");
            }
            rs.close();
            ps.close();
        } catch (Exception ex) {
            Logger.getLogger(NPCConversationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret.toString();
    }

    public String getFameRanks() throws SQLException {
        StringBuilder ret = new StringBuilder();
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT name, fame FROM characters WHERE gm < 3 ORDER BY fame desc LIMIT 10");
        ps.executeQuery();
        ResultSet rs = ps.executeQuery();
        try {
            while (rs.next()) {
                ret.append("\r\n").append("#b").append(rs.getString("name")).append(" : ").append(rs.getInt("fame")).append("#k");
            }
            rs.close();
            ps.close();
        } catch (Exception ex) {
            Logger.getLogger(NPCConversationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret.toString();
    }

    public String getJQRanks() throws SQLException {
        StringBuilder ret = new StringBuilder();
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT name, jqlevel, jqexp FROM characters WHERE gm < 3 ORDER BY jqlevel desc, jqexp DESC LIMIT 10");
        ps.executeQuery();
        ResultSet rs = ps.executeQuery();
        try {
            while (rs.next()) {
                ret.append("\r\n").append("#d").append(rs.getString("name")).append("#k - #bJQ Level:#k #rLv. ").append(rs.getInt("jqlevel")).append("#k #bJQ Exp:#k #r").append(rs.getInt("jqexp")).append("#k");
            }
            rs.close();
            ps.close();
        } catch (Exception ex) {
            Logger.getLogger(NPCConversationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret.toString();
    }

    public void ProDonatorItem(byte slot, int str, int dex, int int_, int luk) {
        MapleInventory equip = getPlayer().getInventory(MapleInventoryType.EQUIP);
        Equip eu = (Equip) equip.getItem(slot); // get slot determine eq
        short hand = eu.getHands(); // HANDS
        byte level = eu.getLevel(); // LEVEL
        eu.setStr((short) str); // STR
        eu.setDex((short) dex); // DEX
        eu.setInt((short) int_); // INT
        eu.setLuk((short) luk); //LUK
        eu.setUpgradeSlots((byte) 0); // Feel free to change
        eu.setHands(hand);
        eu.setLevel(level);
        getPlayer().getInventory(MapleInventoryType.EQUIP).addFromDB(eu);
    }

    public int setAndroid(int args) {
        if (args < 30000) {
            c.getPlayer().getAndroid().setFace(args);
            c.getPlayer().getAndroid().saveToDb();
        } else {
            c.getPlayer().getAndroid().setHair(args);
            c.getPlayer().getAndroid().saveToDb();
        }
        c.getPlayer().equipChanged();

        return 1;
    }

    public int getAndroidStat(final String type) {
        if (type.equals("HAIR")) {
            return c.getPlayer().getAndroid().getHair();
        } else if (type.equals("FACE")) {
            return c.getPlayer().getAndroid().getFace();
        } else if (type.equals("GENDER")) {
            int itemid = c.getPlayer().getAndroid().getItemId();
            if (itemid == 1662000 || itemid == 1662002) {
                return 0;
            } else {
                return 1;
            }
        }
        return -1;
    }

    public void gainJQExp(int gain) {
        getPlayer().gainJQExp(gain);
    }

    public void MakeGMItem(byte slot, MapleCharacter player) {
        MapleInventory equip = player.getInventory(MapleInventoryType.EQUIP);
        Equip eu = (Equip) equip.getItem(slot);
        int item = equip.getItem(slot).getItemId();
        short hand = eu.getHands();
        byte level = eu.getLevel();
        Equip nItem = new Equip(item, slot, (byte) 0);
        nItem.setStr((short) 32767); // STR
        nItem.setDex((short) 32767); // DEX
        nItem.setInt((short) 32767); // INT
        nItem.setLuk((short) 32767); //LUK
        nItem.setUpgradeSlots((byte) 0);
        nItem.setHands(hand);
        nItem.setLevel(level);
        player.getInventory(MapleInventoryType.EQUIP).removeItem(slot);
        player.getInventory(MapleInventoryType.EQUIP).addFromDB(nItem);
    }

    public void giveBuff(int buff) {
        SkillFactory.getSkill(buff).getEffect(SkillFactory.getSkill(buff).getMaxLevel()).applyTo(getPlayer());
    }

    public void MakeNoobPot(byte slot, MapleCharacter player) {
        int randst = (int) (100.0 * Math.random()) + 21;
        MapleInventory equip = player.getInventory(MapleInventoryType.EQUIP);
        Equip eu = (Equip) equip.getItem(slot);
        int item = equip.getItem(slot).getItemId();
        // MapleJob job = eu.();
        short hand = eu.getHands();
        byte level = eu.getLevel();
        Equip nItem = new Equip(item, equip.getNextFreeSlot(), (byte) 0);
        nItem.setStr((short) randst); // STR
        nItem.setDex((short) randst); // DEX
        nItem.setInt((short) randst); // INT
        nItem.setLuk((short) randst); //LUK
        nItem.setWatk((short) randst); // WA
        nItem.setUpgradeSlots((byte) 0); // SLOT
        //nItem.setJob(job);
        nItem.setHands(hand);
        nItem.setLevel(level);
        player.getInventory(MapleInventoryType.EQUIP).addFromDB(nItem);
    }

    public void MakeProPot(byte slot, MapleCharacter player) {
        int randst = (int) (1000.0 * Math.random()) + 21;
        int randwa = (int) (500.0 * Math.random()) + 21;
        MapleInventory equip = player.getInventory(MapleInventoryType.EQUIP);
        Equip eu = (Equip) equip.getItem(slot);
        int item = equip.getItem(slot).getItemId();
        // MapleJob job = eu.();
        short hand = eu.getHands();
        byte level = eu.getLevel();
        Equip nItem = new Equip(item, equip.getNextFreeSlot(), (byte) 0);
        nItem.setStr((short) randst); // STR
        nItem.setDex((short) randst); // DEX
        nItem.setInt((short) randst); // INT
        nItem.setLuk((short) randst); //LUK
        nItem.setWatk((short) randwa); // WA
        nItem.setUpgradeSlots((byte) 0); // SLOT
        //nItem.setJob(job);
        nItem.setHands(hand);
        nItem.setLevel(level);
        player.getInventory(MapleInventoryType.EQUIP).addFromDB(nItem);
    }

    public void MakeAdvPot(byte slot, MapleCharacter player) {
        int randst = (int) (5000.0 * Math.random()) + 21;
        int randwa = (int) (2000.0 * Math.random()) + 21;
        MapleInventory equip = player.getInventory(MapleInventoryType.EQUIP);
        Equip eu = (Equip) equip.getItem(slot);
        int item = equip.getItem(slot).getItemId();
        // MapleJob job = eu.();
        short hand = eu.getHands();
        byte level = eu.getLevel();
        Equip nItem = new Equip(item, equip.getNextFreeSlot(), (byte) 0);
        nItem.setStr((short) randst); // STR
        nItem.setDex((short) randst); // DEX
        nItem.setInt((short) randst); // INT
        nItem.setLuk((short) randst); //LUK
        nItem.setWatk((short) randwa); // WA
        nItem.setUpgradeSlots((byte) 0); // SLOT
        //nItem.setJob(job);
        nItem.setHands(hand);
        nItem.setLevel(level);
        player.getInventory(MapleInventoryType.EQUIP).addFromDB(nItem);
    }

    public boolean nameIsLegal(String text) {
        String[] illegalChars = {" ", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "-", "+", "=", ";", ":", "\"", "\\", "/", "//", ",", ".", "<", ">", "?", "{", "}", "[", "]", "|", " ", "Owner", "Admin", "GameMaster", "GM", "Fuck", "Bitch", "Pussy", "friend", "EricIs", "Shit", "Dick", " Vagina", "Penis", "Clit", "Faggot", "Gay", "Gayboi", "Asian", "DeathStar", "God"};
        for (String illegalChar : illegalChars) {
            if (text.contains(illegalChar)) {
                return false;
            }
        }
        return true;
    }

    public boolean nameIsLegalDonor(String dtext) {
        String[] illegalChars = {" ", "Owner", "Admin", "GameMaster", "GM", "Fuck", "Bitch", "Pussy", "friend", "EricIs", "Shit", "Dick", " Vagina", "Penis", "Clit", "Faggot", "Gay", "Gayboi", "Asian", "DeathStar", "God"};
        for (int i = 0; i < illegalChars.length; i++)
            if (dtext.contains(illegalChars[i]))
                return false;
        return true;
    }

    public String sendGreen(String text) {
        char[] hi = text.toLowerCase().toCharArray();
        int itemId;
        String newString = "";
        for (Character hello : hi) {
            itemId = Character.getNumericValue(hello) + 3991016;
            newString += itemId != 3991015 ? "#v" + itemId + "#" : " ";
        }
        return newString;
    }

    public int getRandom(int start, int end) {
        return (int) Math.floor(Math.random() * end + start);
    }

    public String getInsult() {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("http://www.randominsults.net/").openConnection();
            StringBuilder sb = new StringBuilder();
            con.connect();
            InputStream input = con.getInputStream();
            byte[] buf = new byte[2048];
            int read;
            while ((read = input.read(buf)) > 0) {
                sb.append(new String(buf, 0, read));
            }
            final String find = "<strong><i>";
            int firstPost = sb.indexOf(find);
            StringBuilder send = new StringBuilder();
            for (int i = firstPost + find.length(); i < sb.length(); i++) {
                char ch = sb.charAt(i);
                if (sb.charAt(i) == '<' && sb.charAt(i + 1) == '/' && sb.charAt(i + 2) == 'i')
                    break;
                send.append(ch);
            }
            String sendTxt = send.toString();
            sendTxt = sendTxt.replaceAll("\\<.*?>", "");
            sendTxt = fixHTML(sendTxt);
            return sendTxt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error Occured!";
    }

    public String fixHTML(String in) {
        in = in.replaceAll(Pattern.quote("&quot;"), "\"");
        in = in.replaceAll(Pattern.quote("&amp;"), "&");

        return in;
    }

    public void playMusic(String music) {
        getPlayer().getMap().broadcastMessage(CField.musicChange(music));
    }

    public String getChallenges() throws SQLException {
        StringBuilder ret = new StringBuilder();
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT title, completed, date FROM challenges ORDER BY id desc LIMIT 5");
        ResultSet rs = ps.executeQuery();
        try {
            while (rs.next()) {
                ret.append("\r\n#e").append(rs.getString("title")).append(" - (").append(rs.getString("date")).append(")#n\r\n").append(rs.getString("completed")).append("\r\n");
            }
        } catch (SQLException ex) {
            Logger.getLogger(NPCConversationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        ps.close();
        rs.close();
        return ret.toString();
    }

    public void openNpc2(int id) {
        dispose();
        NPCScriptManager.getInstance().start(getClient(), id);
    }

    public void changeKeyBinding(int key, byte type, int action) {
        getPlayer().changeKeybinding(key, type, action);
        getPlayer().sendKeymap();
    }

    public String getNews() throws SQLException {
        StringBuilder ret = new StringBuilder();
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT title, message, date FROM trollnews ORDER BY newsid desc LIMIT 5");
        ResultSet rs = ps.executeQuery();
        try {
            while (rs.next()) {
                ret.append("\r\n#e").append(rs.getString("title")).append(" - (").append(rs.getString("date")).append(")#n\r\n").append(rs.getString("message")).append("\r\n");
            }
        } catch (SQLException ex) {
            Logger.getLogger(NPCConversationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        ps.close();
        rs.close();
        return ret.toString();
    }

    public String getDigits() throws SQLException {
        StringBuilder ret = new StringBuilder();
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT mesos FROM mrush");
        ResultSet rs = ps.executeQuery();
        try {
            while (rs.next()) {
                ret.append("#e#b").append(rs.getLong("mesos")).append("#k#n");
            }
        } catch (SQLException ex) {
            Logger.getLogger(NPCConversationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        ps.close();
        rs.close();
        return ret.toString();
    }

    public long getMMesos() throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT mesos FROM mrush");
        ResultSet rs = ps.executeQuery();
        try {
            while (rs.next()) {
                MRushAmount = rs.getLong("mesos");
            }
        } catch (SQLException ex) {
            Logger.getLogger(NPCConversationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        ps.close();
        rs.close();
        return MRushAmount;
    }

    public long getPMesos() throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT meso FROM characters");
        ResultSet rs = ps.executeQuery();
        try {
            while (rs.next()) {
                Mesos = rs.getLong("meso");
            }
        } catch (SQLException ex) {
            Logger.getLogger(NPCConversationManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        ps.close();
        rs.close();
        return Mesos;
    }

    public void resetMonsterRush() throws SQLException {
        PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE mrush SET mesos = 700000000000");
        ps.executeUpdate();
        ps.close();
    }

    public void deduct50M() {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE mrush SET mesos = ?");
            //ResultSet rs = ps.executeQuery();
            if (getMMesos() <= 50000000) {
                ps.setLong(1, 0); // set total to 0
            } else {
                ps.setLong(1, getMMesos() - 50000000); //see if it will subtract or do anything at all. :)
            }
            ps.executeUpdate();
            ps.close();
            //rs.close();
        } catch (SQLException e) {
            Logger.getLogger(NPCConversationManager.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void deduct100M() {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE mrush SET mesos = ?");
            if (getMMesos() <= 50000000) {
                ps.setLong(1, 0); // set total to 0
            } else {
                ps.setLong(1, getMMesos() - 100000000);
            }
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            Logger.getLogger(NPCConversationManager.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void deduct200M() {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE mrush SET mesos = ?");
            if (getMMesos() <= 50000000) {
                ps.setLong(1, 0); // set total to 0
            } else {
                ps.setLong(1, getMMesos() - 200000000);
            }
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            Logger.getLogger(NPCConversationManager.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void deduct400M() {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE mrush SET mesos = ?");
            if (getMMesos() <= 50000000) {
                ps.setLong(1, 0); // set total to 0
            } else {
                ps.setLong(1, getMMesos() - 400000000);
            }
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            Logger.getLogger(NPCConversationManager.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void deduct800M() {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE mrush SET mesos = ?");
            if (getMMesos() <= 50000000) {
                ps.setLong(1, 0); // set total to 0
            } else {
                ps.setLong(1, getMMesos() - 800000000);
            }
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            Logger.getLogger(NPCConversationManager.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void deduct1_5B() {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE mrush SET mesos = ?");
            if (getMMesos() <= 50000000) {
                ps.setLong(1, 0); // set total to 0
            } else {
                ps.setLong(1, getMMesos() - 1500000000);
            }
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            Logger.getLogger(NPCConversationManager.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void deduct69() {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE mrush SET mesos = ?");
            if (getMMesos() <= 69) {
                ps.setLong(1, 0); // set total to 0
            } else {
                ps.setLong(1, getMMesos() - 69);
            }
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            Logger.getLogger(NPCConversationManager.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void deductAll() {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE mrush SET mesos = ?");
            ps.setLong(1, getMMesos() - getMeso());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            Logger.getLogger(NPCConversationManager.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void startMonsterRush() { // don't use my sloppy code until i re-build this entire system #EricsOldCodeIsLul
        for (int l = 1; l <= LoginServer.getWorlds().size(); l++) { //ChannelServer instance [l]
            //ChannelServer.getInstance(l).MonsterRush = true;
            //MapleMonsterStats newStats = new MapleMonsterStats();
            //newStats.setHp(2000000000);
            //newStats.setLevel((short)195);
            //newStats.setExp(/*8589934*/200000000); //EXP = 250x, 8589934 * 250 = MAX value rounded up, 140less then Integer.MAX_VALUE. EXP equal from leveling 1 to 200 per mob LOL
            MapleMap map = c.getChannelServer().getMapFactory().getMap(100000000);
            MapleMap map1 = c.getChannelServer().getMapFactory().getMap(103000000);
            MapleMap map2 = c.getChannelServer().getMapFactory().getMap(680000000);
            MapleMap map3 = c.getChannelServer().getMapFactory().getMap(220000000);
            MapleMap map4 = c.getChannelServer().getMapFactory().getMap(200000000);
            MapleMap map5 = c.getChannelServer().getMapFactory().getMap(240000000);
            for (int i = 0; i < 25; i++) { //Monster Spawning Instance [i]
                MapleMonster npcmob = MapleLifeFactory.getMonster(9400203);
                //npcmob.setOverrideStats(newStats);
                npcmob.setHp(npcmob.getMobMaxHp());
                npcmob.setMp(npcmob.getMobMaxMp());
                map.spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(9400203), new Point(72, 274));
                map1.spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(9400203), new Point(72, 274));
                map2.spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(9400203), new Point(72, 274));
                map3.spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(9400203), new Point(72, 274));
                map4.spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(9400203), new Point(72, 274));
                map5.spawnMonsterOnGroudBelow(MapleLifeFactory.getMonster(9400203), new Point(72, 274));
            }
        }
    }

    public int getJQMap() {
        return World.getEventMap();
    }

    public int getEventMap() {
        /**
         * @param: <Returning> the reason we're returning the map ID is so
         * @param: <Returning> that it equals the map, otherwise we put 0 
         * @param: <Returning> so that it says wtf are you doing here instead of loading.
         */
        if (getPlayer().getMapId() == 690000067 && World.getEventMap() == 690000066) {
            return 690000067; // Forest of Patience
        } else if (getPlayer().getMapId() == 280020001 && World.getEventMap() == 280020000) {
            return 280020001; // Zakum
        } else if (getPlayer().getMapId() == 109040004 && World.getEventMap() == 109040000) {
            return 109040004; // Fitness
        } else if (getPlayer().getMapId() == 910130102 && World.getEventMap() == 910130100) {
            return 910130102; // Forest of Endurance
        } else if (getPlayer().getMapId() == 910530001 && World.getEventMap() == 910530000) {
            return 910530001; // Forest of Tenacity
        } else if (getPlayer().getMapId() == World.getEventMap()) {
            return World.getEventMap();
        } else {
            return 0; // invalid - will return unavailable | wrong map
        }
    }

    public void setEventMap(int id) {
        World.setEventMap(id);
        World.setJQChannel(-1); // could use 0 but whatever? o.O this will never be used unless a GM has hosted
    }

    public boolean getEventMapWarp(MapleCharacter player) {
        /**
         * @param: <Returning> We reach through sets of maps and return true if
         * @param: <Returning> the player is in the designated area, we are using this
         * @param: <Returning> to check all players in the world on multi-staged maps, else return false
         */
        if (player.getMapId() >= 690000066 && player.getMapId() <= 690000067) {
            return true; // Forest of Patience
        } else if (player.getMapId() >= 280020000 && player.getMapId() <= 280020001) {
            return true; // Zakum
        } else if (player.getMapId() >= 109040000 && player.getMapId() <= 109040004) {
            return true; // Fitness
        } else if (player.getMapId() >= 910130100 && player.getMapId() <= 910130102) {
            return true; // Forest of Endurance
        } else if (player.getMapId() >= 910530000 && player.getMapId() <= 910530001) {
            return true; // Forest of Tenacity
        } else if (player.getMapId() == World.getEventMap()) {
            return true;
        } else {
            return false; // invalid - will return unavailable | wrong map
        }
    }

    public int getEventMapByGM() {
        return c.getChannelServer().eventMap;
    }

    public int getMapleEvent() {
        // if (getChannelServer().getEvent() > 0) {
        //   if (getChannelServer().getEvent() == 109080000 || getChannelServer().getEvent() == 109080010) {
        //       warp(getChannelServer().getEvent(), 0);
        //   } else {
        //       warp(getChannelServer().getEvent(), "join00");
        //   }
        return -1;//getChannelServer().getEventMap();
        // }
    }

    public void changeOccupationById(int occ) {
        getPlayer().changeOccupation(Occupations.getById(occ));
    }

    public boolean hasOccupation() {
        return (getPlayer().retrieveOccupation().getId() % 100 == 0);
    }

    public void serverNotice(String msg) {
        World.Broadcast.broadcastMessage(c.getWorld(), CWvsContext.broadcastMsg(6, "[Notice] " + msg));
    }

    public void gainCurrency(short amount) {
        MapleInventoryManipulator.addById(c, ServerConstants.Currency, (short) amount, "AutoJQ Reward");
    }

    public void makeCustomPet(int petid) {
        if (petid >= 5000000 && petid <= 5000500) {
            MapleInventoryManipulator.addById(c, petid, (short) 1, "", MaplePet.createPet(petid, MapleItemInformationProvider.getInstance().getName(petid), 1, 0, 100, MapleInventoryIdentifier.getInstance(), 0, (short) 0), 20000, "");
        } else {
            getPlayer().dropMessage(1, "The item you just received is not a pet, please report this.");
            System.out.println("ERROR: makeCustomPet AT NPCConversationManager :: Unable to create pet of id " + petid);
        }
    }

    public int setRandomAvatar(int ticket, int... args_all) {
        if (!haveItem(ticket)) {
            return -1;
        }
        gainItem(ticket, (short) -1);

        int args = args_all[Randomizer.nextInt(args_all.length)];
        if (args < 100) {
            c.getPlayer().setSkinColor((byte) args);
            c.getPlayer().updateSingleStat(MapleStat.SKIN, args);
        } else if (args < 30000) {
            c.getPlayer().setFace(args);
            c.getPlayer().updateSingleStat(MapleStat.FACE, args);
        } else {
            c.getPlayer().setHair(args);
            c.getPlayer().updateSingleStat(MapleStat.HAIR, args);
        }
        c.getPlayer().equipChanged();

        return 1;
    }

    public int setAvatar(int ticket, int args) {
        if (!haveItem(ticket)) {
            return -1;
        }
        gainItem(ticket, (short) -1);

        if (args < 100) {
            c.getPlayer().setSkinColor((byte) args);
            c.getPlayer().updateSingleStat(MapleStat.SKIN, args);
        } else if (args < 30000) {
            c.getPlayer().setFace(args);
            c.getPlayer().updateSingleStat(MapleStat.FACE, args);
        } else {
            c.getPlayer().setHair(args);
            c.getPlayer().updateSingleStat(MapleStat.HAIR, args);
        }
        c.getPlayer().equipChanged();

        return 1;
    }

    // For use of cm.haveMeso(100000); (Was this previously used in sources? I could've swarn..)
    public boolean haveMeso(int meso) {
        return getPlayer().getMeso() >= meso;
    }

    public void gainReborns(int reborns) {
        getPlayer().setReborns(reborns + getPlayer().getReborns());
    }

    public void reloadChar() {
        getPlayer().getClient().sendPacket(CField.getCharInfo(getPlayer()));
        getPlayer().getMap().removePlayer(getPlayer());
        getPlayer().getMap().addPlayer(getPlayer());
    }

    public MapleCharacter getCharByName(String name) {
        return c.getChannelServer().getPlayerStorage().getCharacterByName(name);
    }

    public void sendStorage() {
        c.getPlayer().setConversation(4);
        c.getPlayer().getStorage().sendStorage(c, id);
    }

    public void openShop(int id) {
        MapleShopFactory.getInstance().getShop(id).sendShop(c);
    }

    public void openShopNPC(int id) {
        MapleShopFactory.getInstance().getShop(id).sendShop(c, this.id);
    }

    /*
     * 随机抽奖
     * 参数 道具的ID
     * 参数 道具的数量
     */
    public int gainGachaponItem(int id, int quantity) {
        return gainGachaponItem(id, quantity, getPlayer().getMap().getStreetName() + " - " + getPlayer().getMap().getMapName());
    }

    /*
     * 随机抽奖
     * 参数 道具的ID
     * 参数 道具的数量
     * 参数 獲得装备的日志
     */
    public int gainGachaponItem(int id, int quantity, String msg) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        try {
            if (!ii.itemExists(id)) {
                return -1;
            }
            Item item = MapleInventoryManipulator.addbyId_Gachapon(getClient(), id, (short) quantity, "從 " + msg + " 中獲得時間: " + KoreanDateUtil.getCurrentDate());
            if (item == null) {
                return -1;
            }
            byte rareness = GameConstants.gachaponRareItem(item.getItemId());
            if (rareness == 1 || rareness == 2 || rareness == 3) {
                World.Broadcast.broadcastMessage(c.getWorld(), CWvsContext.getGachaponMega(getPlayer().getName(), " : 從" + msg + "中獲得{" + ii.getName(item.getItemId()) + "}！大家一起恭喜他（她）吧！！！！", item, rareness, msg));
            }
            return item.getItemId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /*
     * NPC给玩家道具带公告
     * 参数 道具的ID
     * 参数 道具的数量
     * 参数 獲得装备的日志
     * 参数 公告喇叭的类型[1-3]
     */
    public int gainGachaponItem(int id, int quantity, String msg, int rareness) {
        return gainGachaponItem(id, quantity, msg, rareness, false, 0);
    }

    /*
     * NPC给玩家道具带公告
     * 参数 道具的ID
     * 参数 道具的数量
     * 参数 獲得装备的日志
     * 参数 公告喇叭的类型[1-3]
     * 参数 道具的使用时间
     */
    public int gainGachaponItem(int id, int quantity, String msg, int rareness, long period) {
        return gainGachaponItem(id, quantity, msg, rareness, false, period);
    }

    /*
     * NPC给玩家道具带公告
     * 参数 道具的ID
     * 参数 道具的数量
     * 参数 獲得装备的日志
     * 参数 公告喇叭的类型[1-3]
     * 参数 是否NPC購買
     * 参数 道具的使用时间
     */
    public int gainGachaponItem(int id, int quantity, String msg, int rareness, boolean buy) {
        return gainGachaponItem(id, quantity, msg, rareness, buy, 0);
    }

    /*
     * NPC给玩家道具带公告
     * 参数 道具的ID
     * 参数 道具的数量
     * 参数 獲得装备的日志
     * 参数 公告喇叭的类型[1-3]
     * 参数 是否NPC購買
     * 参数 道具的使用时间
     */
    public int gainGachaponItem(int id, int quantity, String msg, int rareness, boolean buy, long period) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        try {
            if (!ii.itemExists(id)) {
                return -1;
            }
            Item item = MapleInventoryManipulator.addbyId_Gachapon(getClient(), id, (short) quantity, "從 " + msg + " 中" + (buy ? "購買" : "獲得") + "时间: " + KoreanDateUtil.getCurrentDate(), period);
            if (item == null) {
                return -1;
            }
            if (rareness == 1 || rareness == 2 || rareness == 3) {
                //World.Broadcast.broadcastSmega(c.getWorld(), CWvsContext.itemMegaphone("從" + msg + "中" + (buy ? "購買" : "獲得") + "{" + ii.getName(item.getItemId()) + "}！大家一起恭喜他（她）吧！！！！", true, c.getChannel(), item));
                World.Broadcast.broadcastMessage(c.getWorld(), CWvsContext.getGachaponMega(getPlayer().getName(), " : 從" + msg + "中" + (buy ? "購買" : "獲得") + "{" + ii.getName(item.getItemId()) + "}！大家一起恭喜他（她）吧！！！！", item, (byte) rareness, msg));
            }
            return item.getItemId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int useNebuliteGachapon() {
        try {
            if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 1
                    || c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 1
                    || c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < 1
                    || c.getPlayer().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < 1
                    || c.getPlayer().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < 1) {
                return -1;
            }
            int grade; // Default D
            final int chance = Randomizer.nextInt(100); // cannot gacha S, only from alien cube.
            if (chance < 1) { // Grade A
                grade = 3;
            } else if (chance < 5) { // Grade B
                grade = 2;
            } else if (chance < 35) { // Grade C
                grade = 1;
            } else { // grade == 0
                grade = Randomizer.nextInt(100) < 25 ? 5 : 0; // 25% again to get premium ticket piece				
            }
            int newId = 0;
            if (grade == 5) {
                newId = 4420000;
            } else {
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final List<StructItemOption> pots = new LinkedList<>(ii.getAllSocketInfo(grade).values());
                while (newId == 0) {
                    StructItemOption pot = pots.get(Randomizer.nextInt(pots.size()));
                    if (pot != null) {
                        newId = pot.opID;
                    }
                }
            }
            final Item item = MapleInventoryManipulator.addbyId_Gachapon(c, newId, (short) 1);
            if (item == null) {
                return -1;
            }
            if (grade >= 2 && grade != 5) {
                World.Broadcast.broadcastMessage(c.getWorld(), CWvsContext.getGachaponMega(c.getPlayer().getName(), " : got a(n)", item, (byte) 0, "Maple World"));
            }
            c.sendPacket(InfoPacket.getShowItemGain(newId, (short) 1, true));
            gainItem(2430748, (short) 1);
            gainItemSilent(5220094, (short) -1);
            return item.getItemId();
        } catch (Exception e) {
            System.out.println("[Error] Failed to use Nebulite Gachapon. " + e);
        }
        return -1;
    }

    public void sendSimple(String text, String... selections) {
        if (selections.length > 0) // Adding this even if selections length is 0 will do anything, but whatever.
            text += "#b\r\n";

        for (int i = 0; i < selections.length; i++) {
            text += "#L" + i + "#" + selections[i] + "#l\r\n";
        }
        sendSimple(text, id);
    }

    public void changeJob(int job) {
        c.getPlayer().changeJob(job);
    }

    public void startQuest(int idd) {
        MapleQuest.getInstance(idd).start(getPlayer(), id);
    }

    public void completeQuest(int idd) {
        MapleQuest.getInstance(idd).complete(getPlayer(), id);
    }

    public void forfeitQuest(int idd) {
        MapleQuest.getInstance(idd).forfeit(getPlayer());
    }

    public void forceStartQuest() {
        MapleQuest.getInstance(id2).forceStart(getPlayer(), getNpc(), null);
    }

    @Override
    public void forceStartQuest(int idd) {
        MapleQuest.getInstance(idd).forceStart(getPlayer(), getNpc(), null);
    }

    public void forceStartQuest(String customData) {
        MapleQuest.getInstance(id2).forceStart(getPlayer(), getNpc(), customData);
    }

    public void forceCompleteQuest() {
        MapleQuest.getInstance(id2).forceComplete(getPlayer(), getNpc());
    }

    @Override
    public void forceCompleteQuest(final int idd) {
        MapleQuest.getInstance(idd).forceComplete(getPlayer(), getNpc());
    }

    public String getQuestCustomData() {
        return c.getPlayer().getQuestNAdd(MapleQuest.getInstance(id2)).getCustomData();
    }

    public void setQuestCustomData(String customData) {
        getPlayer().getQuestNAdd(MapleQuest.getInstance(id2)).setCustomData(customData);
    }

    public String getQuestCustomData(int qid) {
        return c.getPlayer().getQuestNAdd(MapleQuest.getInstance(qid)).getCustomData();
    }

    public void setQuestCustomData(int qid, String customData) {
        getPlayer().getQuestNAdd(MapleQuest.getInstance(qid)).setCustomData(customData);
    }

    public int getMeso() {
        return getPlayer().getMeso();
    }

    public void gainAp(final int amount) {
        c.getPlayer().gainAp((short) amount);
    }

    public void expandInventory(byte type, int amt) {
        c.getPlayer().expandInventory(type, amt);
    }

    public final void clearSkills() {
        final Map<Skill, SkillEntry> skills = new HashMap<>(getPlayer().getSkills());
        final Map<Skill, SkillEntry> newList = new HashMap<>();
        for (Entry<Skill, SkillEntry> skill : skills.entrySet()) {
            newList.put(skill.getKey(), new SkillEntry((byte) 0, (byte) 0, -1));
        }
        getPlayer().changeSkillsLevel(newList);
        newList.clear();
        skills.clear();
    }

    public boolean hasSkill(int skillid) {
        Skill theSkill = SkillFactory.getSkill(skillid);
        if (theSkill != null) {
            return c.getPlayer().getSkillLevel(theSkill) > 0;
        }
        return false;
    }

    public void showEffect(boolean broadcast, String effect) {
        if (broadcast) {
            c.getPlayer().getMap().broadcastMessage(CField.showEffect(effect));
        } else {
            c.sendPacket(CField.showEffect(effect));
        }
    }

    public void exceTime(int time) {
        getDirectionEffect(DirectionType.EXEC_TIME.getValue(), null, new int[]{time});
    }

    public void playerWaite() {
        getDirectionEffect(DirectionType.ACTION.getValue(), null, new int[]{0});
    }

    public void playerMoveLeft() {
        getDirectionEffect(DirectionType.ACTION.getValue(), null, new int[]{1});
    }

    public void playerMoveRight() {
        getDirectionEffect(DirectionType.ACTION.getValue(), null, new int[]{2});
    }

    public void playerJump() {
        getDirectionEffect(DirectionType.ACTION.getValue(), null, new int[]{3});
    }

    public void playerMoveDown() {
        getDirectionEffect(DirectionType.ACTION.getValue(), null, new int[]{4});
    }


    public void getDirectionEffect(int mod, String data, int[] values) {
        DirectionType type = DirectionType.getType(mod);
        c.getSession().writeAndFlush(UIPacket.getDirectionEffect(type, data, values));
        if (lastMsg != null) {
            return;
        }
        DirectionType dt = DirectionType.getType(mod);
        switch (dt) {
            case EXEC_TIME:
            case ACTION:
            case UNK4:
            case UNK5:
                lastMsg = NPCTalkType.DIRECTION_SCRIPT_ACTION;
                break;
        }
    }

    public void playSound(boolean broadcast, String sound) {
        if (broadcast) {
            c.getPlayer().getMap().broadcastMessage(CField.playSound(sound));
        } else {
            c.sendPacket(CField.playSound(sound));
        }
    }

    public void environmentChange(boolean broadcast, String env) {
        if (broadcast) {
            c.getPlayer().getMap().broadcastMessage(CField.environmentChange(env, 2));
        } else {
            c.sendPacket(CField.environmentChange(env, 2));
        }
    }

    public void updateBuddyCapacity(int capacity) {
        c.getPlayer().setBuddyCapacity((byte) capacity);
    }

    public int getBuddyCapacity() {
        return c.getPlayer().getBuddyCapacity();
    }

    public int partyMembersInMap() {
        int inMap = 0;
        if (getPlayer().getParty() == null) {
            return inMap;
        }
        for (MapleCharacter char2 : getPlayer().getMap().getCharactersThreadsafe()) {
            if (char2.getParty() != null && char2.getParty().getId() == getPlayer().getParty().getId()) {
                inMap++;
            }
        }
        return inMap;
    }

    public List<MapleCharacter> getPartyMembers() {
        if (getPlayer().getParty() == null) {
            return null;
        }
        List<MapleCharacter> chars = new LinkedList<>(); // creates an empty array full of shit..
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            for (ChannelServer channel : LoginServer.getInstance().getWorld(c.getWorld()).getChannels()) {
                MapleCharacter ch = channel.getPlayerStorage().getCharacterById(chr.getId());
                if (ch != null) { // double check <3
                    chars.add(ch);
                }
            }
        }
        return chars;
    }

    public void warpPartyWithExp(int mapId, int exp) {
        if (getPlayer().getParty() == null) {
            warp(mapId, 0);
            gainExp(exp);
            return;
        }
        MapleMap target = getMap(mapId);
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
            if ((curChar.getEventInstance() == null && getPlayer().getEventInstance() == null) || curChar.getEventInstance() == getPlayer().getEventInstance()) {
                curChar.changeMap(target, target.getPortal(0));
                curChar.gainExp(exp, true, false, true);
            }
        }
    }

    public void warpPartyWithExpMeso(int mapId, int exp, int meso) {
        if (getPlayer().getParty() == null) {
            warp(mapId, 0);
            gainExp(exp);
            gainMeso(meso);
            return;
        }
        MapleMap target = getMap(mapId);
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
            if ((curChar.getEventInstance() == null && getPlayer().getEventInstance() == null) || curChar.getEventInstance() == getPlayer().getEventInstance()) {
                curChar.changeMap(target, target.getPortal(0));
                curChar.gainExp(exp, true, false, true);
                curChar.gainMeso(meso, true);
            }
        }
    }

    public void openGate() { // for MV's Lair
        if (getPlayer().getParty() == null) {
            return;
        }
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
            if ((curChar.getEventInstance() == null && getPlayer().getEventInstance() == null) || curChar.getEventInstance() == getPlayer().getEventInstance()) {
                curChar.gate[4] = true;
            }
        }
    }

    public void closeGate() { // for MV's Lair
        if (getPlayer().getParty() == null) {
            return;
        }
        for (MaplePartyCharacter chr : getPlayer().getParty().getMembers()) {
            MapleCharacter curChar = c.getChannelServer().getPlayerStorage().getCharacterByName(chr.getName());
            if ((curChar.getEventInstance() == null && getPlayer().getEventInstance() == null) || curChar.getEventInstance() == getPlayer().getEventInstance()) {
                curChar.gate[4] = false;
            }
        }
    }

    public MapleSquad getSquad(String type) {
        return c.getChannelServer().getMapleSquad(type);
    }

    public int getSquadAvailability(String type) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return -1;
        }
        return squad.getStatus();
    }

    public boolean registerSquad(String type, int minutes, String startText) {
        if (c.getChannelServer().getMapleSquad(type) == null) {
            final MapleSquad squad = new MapleSquad(c.getWorld(), c.getChannel(), type, c.getPlayer(), minutes * 60 * 1000, startText);
            final boolean ret = c.getChannelServer().addMapleSquad(squad, type);
            if (ret) {
                final MapleMap map = c.getPlayer().getMap();

                map.broadcastMessage(CField.getClock(minutes * 60));
                map.broadcastMessage(CWvsContext.broadcastMsg(6, c.getPlayer().getName() + startText));
            } else {
                squad.clear();
            }
            return ret;
        }
        return false;
    }

    public boolean getSquadList(String type, byte type_) {
        try {
            final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
            if (squad == null) {
                return false;
            }
            if (type_ == 0 || type_ == 3) { // Normal viewing
                sendNext(squad.getSquadMemberString(type_));
            } else if (type_ == 1) { // Squad Leader banning, Check out banned participant
                sendSimple(squad.getSquadMemberString(type_));
            } else if (type_ == 2) {
                if (squad.getBannedMemberSize() > 0) {
                    sendSimple(squad.getSquadMemberString(type_));
                } else {
                    sendNext(squad.getSquadMemberString(type_));
                }
            }
            return true;
        } catch (NullPointerException ex) {
            FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, ex);
            return false;
        }
    }

    public byte isSquadLeader(String type) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return -1;
        } else {
            if (squad.getLeader() != null && squad.getLeader().getId() == c.getPlayer().getId()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public boolean reAdd(String eim, String squad) {
        EventInstanceManager eimz = getDisconnected(eim);
        MapleSquad squadz = getSquad(squad);
        if (eimz != null && squadz != null) {
            squadz.reAddMember(getPlayer());
            eimz.registerPlayer(getPlayer());
            return true;
        }
        return false;
    }

    public void banMember(String type, int pos) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.banMember(pos);
        }
    }

    public void acceptMember(String type, int pos) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad != null) {
            squad.acceptMember(pos);
        }
    }

    public int addMember(String type, boolean join) {
        try {
            final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
            if (squad != null) {
                return squad.addMember(c.getPlayer(), join);
            }
            return -1;
        } catch (NullPointerException ex) {
            FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, ex);
            return -1;
        }
    }

    public byte isSquadMember(String type) {
        final MapleSquad squad = c.getChannelServer().getMapleSquad(type);
        if (squad == null) {
            return -1;
        } else {
            if (squad.getMembers().contains(c.getPlayer().getName())) {
                return 1;
            } else if (squad.isBanned(c.getPlayer())) {
                return 2;
            } else {
                return 0;
            }
        }
    }

    public void resetReactors() {
        getPlayer().getMap().resetReactors();
    }

    public void genericGuildMessage(int code) {
        c.sendPacket(GuildPacket.genericGuildMessage((byte) code));
    }

    public void disbandGuild() {
        final int gid = c.getPlayer().getGuildId();
        if (gid <= 0 || c.getPlayer().getGuildRank() != 1) {
            return;
        }
        World.Guild.disbandGuild(gid);
    }

    public void increaseGuildCapacity(boolean trueMax) {
        if (c.getPlayer().getMeso() < 500000 && !trueMax) {
            c.sendPacket(CWvsContext.broadcastMsg(1, "You do not have enough mesos."));
            return;
        }
        final int gid = c.getPlayer().getGuildId();
        if (gid <= 0) {
            return;
        }
        if (World.Guild.increaseGuildCapacity(gid, trueMax)) {
            if (!trueMax) {
                c.getPlayer().gainMeso(-500000, true, true);
            } else {
                gainGP(-25000);
            }
            sendNext("Your guild capacity has been raised...");
        } else if (!trueMax) {
            sendNext("Please check if your guild capacity is full. (Limit: 100)");
        } else {
            sendNext("Please check if your guild capacity is full, if you have the GP needed or if subtracting GP would decrease a guild level. (Limit: 200)");
        }
    }

    public void displayGuildRanks() {
        c.sendPacket(GuildPacket.showGuildRanks(id, MapleGuildRanking.getInstance().getRank()));
    }

    public boolean removePlayerFromInstance() {
        if (c.getPlayer().getEventInstance() != null) {
            c.getPlayer().getEventInstance().removePlayer(c.getPlayer());
            return true;
        }
        return false;
    }

    public boolean isPlayerInstance() {
        if (c.getPlayer().getEventInstance() != null) {
            return true;
        }
        return false;
    }

    public void changeStat(byte slot, int type, int amount) {
        Equip sel = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(slot);
        switch (type) {
            case 0:
                sel.setStr((short) amount);
                break;
            case 1:
                sel.setDex((short) amount);
                break;
            case 2:
                sel.setInt((short) amount);
                break;
            case 3:
                sel.setLuk((short) amount);
                break;
            case 4:
                sel.setHp((short) amount);
                break;
            case 5:
                sel.setMp((short) amount);
                break;
            case 6:
                sel.setWatk((short) amount);
                break;
            case 7:
                sel.setMatk((short) amount);
                break;
            case 8:
                sel.setWdef((short) amount);
                break;
            case 9:
                sel.setMdef((short) amount);
                break;
            case 10:
                sel.setAcc((short) amount);
                break;
            case 11:
                sel.setAvoid((short) amount);
                break;
            case 12:
                sel.setHands((short) amount);
                break;
            case 13:
                sel.setSpeed((short) amount);
                break;
            case 14:
                sel.setJump((short) amount);
                break;
            case 15:
                sel.setUpgradeSlots((byte) amount);
                break;
            case 16:
                sel.setViciousHammer((byte) amount);
                break;
            case 17:
                sel.setLevel((byte) amount);
                break;
            case 18:
                sel.setEnhance((byte) amount);
                break;
            case 19:
                sel.setPotential1(amount);
                break;
            case 20:
                sel.setPotential2(amount);
                break;
            case 21:
                sel.setPotential3(amount);
                break;
            case 22:
                sel.setPotential4(amount);
                break;
            case 23:
                sel.setPotential5(amount);
                break;
            case 24:
                sel.setOwner(getText());
                break;
            default:
                break;
        }
        c.getPlayer().equipChanged();
        c.getPlayer().fakeRelog();
    }

    public void openDuey() {
        c.getPlayer().setConversation(2);
        c.sendPacket(CField.sendDuey((byte) 9, null));
    }

    public void openMerchantItemStore() {
        c.getPlayer().setConversation(3);
        HiredMerchantHandler.displayMerch(c);
        //c.sendPacket(PlayerShopPacket.merchItemStore((byte) 0x22));
        //c.getPlayer().dropMessage(5, "Please enter ANY 13 characters.");
    }

    public void sendPVPWindow() {
        c.sendPacket(UIPacket.openUI(50));
        c.sendPacket(CField.sendPVPMaps());
    }

    public void sendDojoRanks() {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT `name`, `time` FROM dojo_ranks ORDER BY `time` ASC LIMIT 50");
            ResultSet rs = ps.executeQuery();
            c.sendPacket(CWvsContext.getMulungRanks(rs));
            ps.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println("Failed to load Mu Lung Ranking. " + e);
        }
    }

    public void setDojoMode(int mode) {
        getPlayer().setDojoMode(getPlayer().getDojoMode(mode));
    }

    public void findParty() {
        c.sendPacket(UIPacket.openUI(21));
    }

    public void sendAzwanWindow() {
        c.sendPacket(UIPacket.openUI(0x46));
    }

    public void sendRepairWindow() {
        c.sendPacket(UIPacket.sendRepairWindow(id));
    }

    public void sendProfessionWindow() {
        c.sendPacket(UIPacket.openUI(42));
    }

    public final int getDojoPoints() {
        return dojo_getPts();
    }

    public final int getDojoRecord() {
        return c.getPlayer().getIntNoRecord(GameConstants.DOJO_RECORD);
    }

    public void setDojoRecord(final boolean reset) {
        if (reset) {
            c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.DOJO_RECORD)).setCustomData("0");
            c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.DOJO)).setCustomData("0");
        } else {
            c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.DOJO_RECORD)).setCustomData(String.valueOf(c.getPlayer().getIntRecord(GameConstants.DOJO_RECORD) + 1));
        }
    }

    public void takeDojoPoints(final int data) {
        if (data < 1) {
            c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.DOJO_RECORD)).setCustomData("0");
            c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.DOJO)).setCustomData("0");
        } else {
            final int dojo = c.getPlayer().getIntRecord(GameConstants.DOJO) - data;
            c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.DOJO)).setCustomData(String.valueOf(dojo));
        }
    }

    public void addDojoPoints(final int data) {
        final int dojo = c.getPlayer().getIntRecord(GameConstants.DOJO) + data;
        c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.DOJO)).setCustomData(String.valueOf(dojo));
        // c.getPlayer().getClient().sendPacket(CWvsContext.Mulung_Pts(data, dojo));
    }

    public boolean start_DojoAgent(final boolean dojo, final boolean party, final int level) {
        if (dojo) {
            return Event_DojoAgent.warpStartDojo(c.getPlayer(), party, level);
        }
        return Event_DojoAgent.warpStartAgent(c.getPlayer(), party);
    }

    public final void resetItem(Item item, int type) {
        c.getPlayer().forceReAddItem(item ,MapleInventoryType.getByType((byte) type));
    }

    public int getTotalDonate() {
        return c.getPlayer().getTotalDonate();
    }

    public boolean start_PyramidSubway(final int pyramid) {
        if (pyramid >= 0) {
            return Event_PyramidSubway.warpStartPyramid(c.getPlayer(), pyramid);
        }
        return Event_PyramidSubway.warpStartSubway(c.getPlayer());
    }

    public boolean bonus_PyramidSubway(final int pyramid) {
        if (pyramid >= 0) {
            return Event_PyramidSubway.warpBonusPyramid(c.getPlayer(), pyramid);
        }
        return Event_PyramidSubway.warpBonusSubway(c.getPlayer());
    }

    public final short getKegs() {
        return c.getChannelServer().getFireWorks().getKegsPercentage();
    }

    public void giveKegs(final int kegs) {
        c.getChannelServer().getFireWorks().giveKegs(c.getPlayer(), kegs);
    }

    public final short getSunshines() {
        return c.getChannelServer().getFireWorks().getSunsPercentage();
    }

    public void addSunshines(final int kegs) {
        c.getChannelServer().getFireWorks().giveSuns(c.getPlayer(), kegs);
    }

    public final short getDecorations() {
        return c.getChannelServer().getFireWorks().getDecsPercentage();
    }

    public void addDecorations(final int kegs) {
        try {
            c.getChannelServer().getFireWorks().giveDecs(c.getPlayer(), kegs);
        } catch (Exception e) {
        }
    }

    public final MapleCarnivalParty getCarnivalParty() {
        return c.getPlayer().getCarnivalParty();
    }

    public final MapleCarnivalChallenge getNextCarnivalRequest() {
        return c.getPlayer().getNextCarnivalRequest();
    }

    public final MapleCarnivalChallenge getCarnivalChallenge(MapleCharacter chr) {
        return new MapleCarnivalChallenge(chr);
    }

    public void maxStats() {
        Map<MapleStat, Integer> statup = new EnumMap<>(MapleStat.class);
        c.getPlayer().getStat().str = (short) 32767;
        c.getPlayer().getStat().dex = (short) 32767;
        c.getPlayer().getStat().int_ = (short) 32767;
        c.getPlayer().getStat().luk = (short) 32767;

        int overrDemon = GameConstants.isDemon(c.getPlayer().getJob()) ? GameConstants.getMPByJob(c.getPlayer().getJob()) : 99999;
        c.getPlayer().getStat().maxhp = 99999;
        c.getPlayer().getStat().maxmp = overrDemon;
        c.getPlayer().getStat().setHp(99999, c.getPlayer());
        c.getPlayer().getStat().setMp(overrDemon, c.getPlayer());

        statup.put(MapleStat.STR, Integer.valueOf(32767));
        statup.put(MapleStat.DEX, Integer.valueOf(32767));
        statup.put(MapleStat.LUK, Integer.valueOf(32767));
        statup.put(MapleStat.INT, Integer.valueOf(32767));
        statup.put(MapleStat.HP, Integer.valueOf(99999));
        statup.put(MapleStat.MAX_HP, Integer.valueOf(99999));
        statup.put(MapleStat.MP, Integer.valueOf(overrDemon));
        statup.put(MapleStat.MAX_MP, Integer.valueOf(overrDemon));
        c.getPlayer().getStat().recalcLocalStats(c.getPlayer());
        c.sendPacket(CWvsContext.updatePlayerStats(statup, c.getPlayer()));
    }

    public Triple<String, Map<Integer, String>, Long> getSpeedRun(String typ) {
        final ExpeditionType fefea = ExpeditionType.valueOf(typ);
        if (SpeedRunner.getSpeedRunData(fefea) != null) {
            return SpeedRunner.getSpeedRunData(fefea);
        }
        return new Triple<String, Map<Integer, String>, Long>("", new HashMap<Integer, String>(), 0L);
    }

    public boolean getSR(Triple<String, Map<Integer, String>, Long> ma, int sel) {
        if (ma.mid.get(sel) == null || ma.mid.get(sel).length() <= 0) {
            dispose();
            return false;
        }
        sendOk(ma.mid.get(sel));
        return true;
    }

    public Equip getEquip(int itemid) {
        return (Equip) MapleItemInformationProvider.getInstance().getEquipById(itemid);
    }

    public void setExpiration(Object statsSel, long expire) {
        if (statsSel instanceof Equip) {
            ((Equip) statsSel).setExpiration(System.currentTimeMillis() + (expire * 24 * 60 * 60 * 1000));
        }
    }

    public void setLock(Object statsSel) {
        if (statsSel instanceof Equip) {
            Equip eq = (Equip) statsSel;
            if (eq.getExpiration() == -1) {
                eq.setFlag((byte) (eq.getFlag() | ItemFlag.LOCK.getValue()));
            } else {
                eq.setFlag((byte) (eq.getFlag() | ItemFlag.UNTRADEABLE.getValue()));
            }
        }
    }

    public boolean addFromDrop(Object statsSel) {
        if (statsSel instanceof Item) {
            final Item it = (Item) statsSel;
            return MapleInventoryManipulator.checkSpace(getClient(), it.getItemId(), it.getQuantity(), it.getOwner()) && MapleInventoryManipulator.addFromDrop(getClient(), it, false);
        }
        return false;
    }

    public boolean replaceItem(int slot, int invType, Object statsSel, int offset, String type) {
        return replaceItem(slot, invType, statsSel, offset, type, false);
    }

    public boolean replaceItem(int slot, int invType, Object statsSel, int offset, String type, boolean takeSlot) {
        MapleInventoryType inv = MapleInventoryType.getByType((byte) invType);
        if (inv == null) {
            return false;
        }
        Item item = getPlayer().getInventory(inv).getItem((byte) slot);
        if (item == null || statsSel instanceof Item) {
            item = (Item) statsSel;
        }
        if (offset > 0) {
            if (inv != MapleInventoryType.EQUIP) {
                return false;
            }
            Equip eq = (Equip) item;
            if (takeSlot) {
                if (eq.getUpgradeSlots() < 1) {
                    return false;
                } else {
                    eq.setUpgradeSlots((byte) (eq.getUpgradeSlots() - 1));
                }
                if (eq.getExpiration() == -1) {
                    eq.setFlag((byte) (eq.getFlag() | ItemFlag.LOCK.getValue()));
                } else {
                    eq.setFlag((byte) (eq.getFlag() | ItemFlag.UNTRADEABLE.getValue()));
                }
            }
            if (type.equalsIgnoreCase("Slots")) {
                eq.setUpgradeSlots((byte) (eq.getUpgradeSlots() + offset));
                eq.setViciousHammer((byte) (eq.getViciousHammer() + offset));
            } else if (type.equalsIgnoreCase("Level")) {
                eq.setLevel((byte) (eq.getLevel() + offset));
            } else if (type.equalsIgnoreCase("Hammer")) {
                eq.setViciousHammer((byte) (eq.getViciousHammer() + offset));
            } else if (type.equalsIgnoreCase("STR")) {
                eq.setStr((short) (eq.getStr() + offset));
            } else if (type.equalsIgnoreCase("DEX")) {
                eq.setDex((short) (eq.getDex() + offset));
            } else if (type.equalsIgnoreCase("INT")) {
                eq.setInt((short) (eq.getInt() + offset));
            } else if (type.equalsIgnoreCase("LUK")) {
                eq.setLuk((short) (eq.getLuk() + offset));
            } else if (type.equalsIgnoreCase("HP")) {
                eq.setHp((short) (eq.getHp() + offset));
            } else if (type.equalsIgnoreCase("MP")) {
                eq.setMp((short) (eq.getMp() + offset));
            } else if (type.equalsIgnoreCase("WATK")) {
                eq.setWatk((short) (eq.getWatk() + offset));
            } else if (type.equalsIgnoreCase("MATK")) {
                eq.setMatk((short) (eq.getMatk() + offset));
            } else if (type.equalsIgnoreCase("WDEF")) {
                eq.setWdef((short) (eq.getWdef() + offset));
            } else if (type.equalsIgnoreCase("MDEF")) {
                eq.setMdef((short) (eq.getMdef() + offset));
            } else if (type.equalsIgnoreCase("ACC")) {
                eq.setAcc((short) (eq.getAcc() + offset));
            } else if (type.equalsIgnoreCase("Avoid")) {
                eq.setAvoid((short) (eq.getAvoid() + offset));
            } else if (type.equalsIgnoreCase("Hands")) {
                eq.setHands((short) (eq.getHands() + offset));
            } else if (type.equalsIgnoreCase("Speed")) {
                eq.setSpeed((short) (eq.getSpeed() + offset));
            } else if (type.equalsIgnoreCase("Jump")) {
                eq.setJump((short) (eq.getJump() + offset));
            } else if (type.equalsIgnoreCase("ItemEXP")) {
                eq.setItemEXP(eq.getItemEXP() + offset);
            } else if (type.equalsIgnoreCase("Expiration")) {
                eq.setExpiration((long) (eq.getExpiration() + offset));
            } else if (type.equalsIgnoreCase("Flag")) {
                eq.setFlag((byte) (eq.getFlag() + offset));
            }
            item = eq.copy();
        }
        MapleInventoryManipulator.removeFromSlot(getClient(), inv, (short) slot, item.getQuantity(), false);
        return MapleInventoryManipulator.addFromDrop(getClient(), item, false);
    }

    public boolean replaceItem(int slot, int invType, Object statsSel, int upgradeSlots) {
        return replaceItem(slot, invType, statsSel, upgradeSlots, "Slots");
    }

    public boolean isCash(final int itemId) {
        return MapleItemInformationProvider.getInstance().isCash(itemId);
    }

    public void clearDrops() {
        MapleMap map = c.getPlayer().getMap();
        double range = Double.POSITIVE_INFINITY;
        java.util.List<MapleMapObject> items = map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.ITEM));
        for (MapleMapObject itemmo : items) {
            map.removeMapObject(itemmo);
            map.broadcastMessage(CField.removeItemFromMap(itemmo.getObjectId(), 0, c.getPlayer().getId()));
        }
    }

    public int getTotalStat(final int itemId) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        return ii.getTotalStat((Equip) ii.getEquipById(itemId));
    }

    public int getReqLevel(final int itemId) {
        return MapleItemInformationProvider.getInstance().getReqLevel(itemId);
    }

    public MapleStatEffect getEffect(int buff) {
        return MapleItemInformationProvider.getInstance().getItemEffect(buff);
    }

    public void buffGuild(final int buff, final int duration, final String msg) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (ii.getItemEffect(buff) != null && getPlayer().getGuildId() > 0) {
            final MapleStatEffect mse = ii.getItemEffect(buff);
            for (ChannelServer channelServ : LoginServer.getWorld(c.getWorld()).getChannels()) {
                channelServ.getPlayerStorage().getAllCharacters().stream().filter(chr -> chr.getGuildId() == getPlayer().getGuildId()).forEach(chr -> {
                    mse.applyTo(chr, chr, true, null, duration);
                    chr.dropMessage(5, "Your guild has gotten a " + msg + " buff.");
                });
            }
        }
    }

    public boolean createAlliance(String alliancename) {
        MapleParty pt = c.getPlayer().getParty();
        MapleCharacter otherChar = c.getChannelServer().getPlayerStorage().getCharacterById(pt.getMemberByIndex(1).getId());
        if (otherChar == null || otherChar.getId() == c.getPlayer().getId()) {
            return false;
        }
        try {
            return World.Alliance.createAlliance(alliancename, c.getPlayer().getId(), otherChar.getId(), c.getPlayer().getGuildId(), otherChar.getGuildId());
        } catch (Exception re) {
            return false;
        }
    }

    public boolean addCapacityToAlliance() {
        try {
            final MapleGuild gs = World.Guild.getGuild(c.getPlayer().getGuildId());
            if (gs != null && c.getPlayer().getGuildRank() == 1 && c.getPlayer().getAllianceRank() == 1) {
                if (World.Alliance.getAllianceLeader(gs.getAllianceId()) == c.getPlayer().getId() && World.Alliance.changeAllianceCapacity(gs.getAllianceId())) {
                    gainMeso(-MapleGuildAlliance.CHANGE_CAPACITY_COST);
                    return true;
                }
            }
        } catch (Exception re) {
        }
        return false;
    }

    public boolean disbandAlliance() {
        try {
            final MapleGuild gs = World.Guild.getGuild(c.getPlayer().getGuildId());
            if (gs != null && c.getPlayer().getGuildRank() == 1 && c.getPlayer().getAllianceRank() == 1) {
                if (World.Alliance.getAllianceLeader(gs.getAllianceId()) == c.getPlayer().getId() && World.Alliance.disbandAlliance(gs.getAllianceId())) {
                    return true;
                }
            }
        } catch (Exception re) {
        }
        return false;
    }

    public NPCTalkType getLastMsg() {
        return lastMsg;
    }

    public final void setLastMsg(final NPCTalkType last) {
        this.lastMsg = last;
    }

    public final void maxAllSkills() {
        HashMap<Skill, SkillEntry> sa = new HashMap<>();
        for (Skill skil : SkillFactory.getAllSkills()) {
            if (GameConstants.isApplicableSkill(skil.getId()) && skil.getId() < 90000000) { //no db/additionals/resistance skills
                sa.put(skil, new SkillEntry((byte) skil.getMaxLevel(), (byte) skil.getMaxLevel(), SkillFactory.getDefaultSExpiry(skil)));
            }
        }
        getPlayer().changeSkillsLevel(sa);
    }

    public final void maxSkillsByJob() {
        HashMap<Skill, SkillEntry> sa = new HashMap<>();
        for (Skill skil : SkillFactory.getAllSkills()) {
            if (GameConstants.isApplicableSkill(skil.getId()) && skil.canBeLearnedBy(getPlayer().getJob())) { //no db/additionals/resistance skills
                sa.put(skil, new SkillEntry((byte) skil.getMaxLevel(), (byte) skil.getMaxLevel(), SkillFactory.getDefaultSExpiry(skil)));
            }
        }
        getPlayer().changeSkillsLevel(sa);
    }

    public final void resetStats(int str, int dex, int z, int luk) {
        c.getPlayer().resetStats(str, dex, z, luk);
    }

    public final boolean dropItem(int slot, int invType, int quantity) {
        MapleInventoryType inv = MapleInventoryType.getByType((byte) invType);
        if (inv == null) {
            return false;
        }
        return MapleInventoryManipulator.drop(c, inv, (short) slot, (short) quantity, true);
    }

    public final List<Integer> getAllPotentialInfo() {
        List<Integer> list = new ArrayList<>(MapleItemInformationProvider.getInstance().getAllPotentialInfo().keySet());
        Collections.sort(list);
        return list;
    }

    public final List<Integer> getAllPotentialInfoSearch(String content) {
        List<Integer> list = new ArrayList<>();
        for (Entry<Integer, List<StructItemOption>> i : MapleItemInformationProvider.getInstance().getAllPotentialInfo().entrySet()) {
            for (StructItemOption ii : i.getValue()) {
                if (ii.toString().contains(content)) {
                    list.add(i.getKey());
                }
            }
        }
        Collections.sort(list);
        return list;
    }
    public boolean doEnhance(int itemid, short dst,short scrollslot){
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Item scroll = getPlayer().getInventory(MapleInventoryType.USE).getItem(scrollslot);
        Equip equip = (Equip) getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(dst);

        return InventoryHandler.UseUpgradeScroll(scrollslot, dst, (short) 0, c, c.getPlayer(), 0, true);

    }

    public void doCube(int itemid, byte dst,int CubeID){
        byte src = (byte) 127;
        boolean insight = src == 127;
        Item magnify = getPlayer().getInventory(MapleInventoryType.USE).getItem(src);
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Equip equip = (Equip) getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(dst);
        Item cube = getPlayer().getInventory(MapleInventoryType.CASH).findById(CubeID);
        equip.renewPotential(CubeID - 5062000);

        c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(false, c.getPlayer().getId(), true, equip.getItemId()));
        c.getSession().writeAndFlush(CWvsContext.InventoryPacket.scrolledItem(cube, MapleInventoryType.EQUIP, equip, false, true, false));
        c.getPlayer().forceReAddItem_NoUpdate(equip, MapleInventoryType.EQUIP);

        int reqLevel = ii.getReqLevel(equip.getItemId()) / 10;
        final int n3 = (reqLevel >= 20) ? 19 : reqLevel;
        if ((equip.getState() < 17 && equip.getState() > 0) || (equip.getState() < 17 && equip.getState() > 0)) {
            final boolean isPotAdd = equip.getState() < 17 && equip.getState() > 0;
            if (insight) {
                final int meso = 5000;
                if (getPlayer().getMeso() < meso) {
                    getPlayer().dropMessage(5, "您沒有足夠的金幣。");
                    getClient().sendPacket(CWvsContext.enableActions());
                    return;
                }
                getPlayer().gainMeso(-meso, false);
            }
            final Equip nEquip = InventoryHandler.UseMagnify((byte) equip.getPosition(), c);
            if(nEquip == null)
                return;
            getPlayer().getTrait(MapleTrait.MapleTraitType.insight).addExp((insight ? 10 : ((magnify.getItemId() + 2) - 2460000)) * 2, getPlayer());
            getPlayer().getMap().broadcastMessage(CField.showMagnifyingEffect(getPlayer().getId(), equip.getPosition()));
            if (!insight) {
                MapleInventoryManipulator.removeFromSlot(getClient(), MapleInventoryType.USE, magnify.getPosition(), (short) 1, false);
            }
            getPlayer().forceUpdateItem(equip, true);
            if (dst < 0) { //当 dst 小于 就是鉴定装备中的装备 需要重新计算角色的属性
                getPlayer().equipChanged();
            }
            getClient().sendPacket(CWvsContext.enableActions());
        } else {
            getClient().sendPacket(CWvsContext.InventoryPacket.getInventoryFull());
        }
    }

    public byte getEquipPotState(int itemid) {
        if(itemid < 100)
            return 0;
        Equip equip = (Equip) getPlayer().getInventory(MapleInventoryType.EQUIP).findById(itemid);
        return equip.getState();
    }

    public void openWeb(String url){
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(20);
        mplew.write(url.getBytes());
        c.getClinetS().sendPacket(mplew.getPacket());
    }

    public int getPotID(int itemID, int Pot_position){
        Equip equip = (Equip) getPlayer().getInventory(MapleInventoryType.EQUIP).findById(itemID);
        return equip.getPotential(Pot_position);
    }


    public final String getPotentialInfo(final int id) {
        final List<StructItemOption> potInfo = MapleItemInformationProvider.getInstance().getPotentialInfo(id);
        final StringBuilder builder = new StringBuilder("#b#ePOTENTIAL INFO FOR ID: ");
        builder.append(id);
        builder.append("#n#k\r\n\r\n");
        int minLevel = 1, maxLevel = 10;
        for (StructItemOption item : potInfo) {
            builder.append("#eLevels ");
            builder.append(minLevel);
            builder.append("~");
            builder.append(maxLevel);
            builder.append(": #n");
            builder.append(item.toString());
            minLevel += 10;
            maxLevel += 10;
            builder.append("\r\n");
        }
        return builder.toString();
    }

    public final void sendRPS() {
        c.sendPacket(CField.getRPSMode((byte) 8, -1, -1, -1));
    }

    public final void setQuestRecord(Object ch, final int questid, final String data) {
        ((MapleCharacter) ch).getQuestNAdd(MapleQuest.getInstance(questid)).setCustomData(data);
    }

    public void warpMap(int id) {
        for (MapleCharacter chr : getMap().getCharacters()) {
            chr.changeMap(id);
        }
    }

    public void warpMapAutoJQers(int id) {
        for (MapleCharacter jqers : World.getAllCharacters()) {
            if (getEventMapWarp(jqers)) { // this is so we catch all the characters within the jq maps (even staged)*
                jqers.changeMap(id);
            }
        }
    }

    public final void doWeddingEffect(final Object ch) {
        final MapleCharacter chr = (MapleCharacter) ch;
        final MapleCharacter player = getPlayer();
        getMap().broadcastMessage(CWvsContext.yellowChat(player.getName() + ", do you take " + chr.getName() + " as your wife and promise to stay beside her through all downtimes, crashes, and lags?"));
        WorldTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (chr == null || player == null) {
                    warpMap(680000500, 0);
                } else {
                    chr.getMap().broadcastMessage(CWvsContext.yellowChat(chr.getName() + ", do you take " + player.getName() + " as your husband and promise to stay beside him through all downtimes, crashes, and lags?"));
                }
            }
        }, 10000);
        WorldTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (chr == null || player == null) {
                    if (player != null) {
                        setQuestRecord(player, 160001, "3");
                        setQuestRecord(player, 160002, "0");
                    } else if (chr != null) {
                        setQuestRecord(chr, 160001, "3");
                        setQuestRecord(chr, 160002, "0");
                    }
                    warpMap(680000500, 0);
                } else {
                    setQuestRecord(player, 160001, "2");
                    setQuestRecord(chr, 160001, "2");
                    sendNPCText(player.getName() + " and " + chr.getName() + ", I wish you two all the best on your " + chr.getClient().getWorldServer().getWorldName() + " journey together!", 9201002);
                    chr.getMap().startExtendedMapEffect("You may now kiss the bride, " + player.getName() + "!", 5120006);
                    if (chr.getGuildId() > 0) {
                        World.Guild.guildPacket(chr.getGuildId(), CWvsContext.sendMarriage(false, chr.getName()));
                    }
                    if (chr.getFamilyId() > 0) {
                        World.Family.familyPacket(chr.getFamilyId(), CWvsContext.sendMarriage(true, chr.getName()), chr.getId());
                    }
                    if (player.getGuildId() > 0) {
                        World.Guild.guildPacket(player.getGuildId(), CWvsContext.sendMarriage(false, player.getName()));
                    }
                    if (player.getFamilyId() > 0) {
                        World.Family.familyPacket(player.getFamilyId(), CWvsContext.sendMarriage(true, chr.getName()), player.getId());
                    }
                }
            }
        }, 20000); //10 sec 10 sec

    }

    public void putKey(int key, int type, int action) {
        getPlayer().changeKeybinding(key, (byte) type, action);
        getClient().sendPacket(CField.getKeymap(getPlayer().getKeyLayout()));
    }

    public void logDonator(String log, int previous_points) {
        final StringBuilder logg = new StringBuilder();
        logg.append(MapleCharacterUtil.makeMapleReadable(getPlayer().getName()));
        logg.append(" [CID: ").append(getPlayer().getId()).append("] ");
        logg.append(" [Account: ").append(MapleCharacterUtil.makeMapleReadable(getClient().getAccountName())).append("] ");
        logg.append(log);
        logg.append(" [Previous: ").append(previous_points).append("] [Now: ").append(getPlayer().getPoints()).append("]");

        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO donorlog VALUES(DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                ps.setString(1, MapleCharacterUtil.makeMapleReadable(getClient().getAccountName()));
                ps.setInt(2, getClient().getAccID());
                ps.setString(3, MapleCharacterUtil.makeMapleReadable(getPlayer().getName()));
                ps.setInt(4, getPlayer().getId());
                ps.setString(5, log);
                ps.setString(6, FileoutputUtil.CurrentReadable_Time());
                ps.setInt(7, previous_points);
                ps.setInt(8, getPlayer().getPoints());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
        }
        FileoutputUtil.log(FileoutputUtil.Donator_Log, logg.toString());
    }

    public void doRing(final String name, final int itemid) {
        PlayersHandler.DoRing(getClient(), name, itemid);
    }

    public int getNaturalStats(final int itemid, final String it) {
        Map<String, Integer> eqStats = MapleItemInformationProvider.getInstance().getEquipStats(itemid);
        if (eqStats != null && eqStats.containsKey(it)) {
            return eqStats.get(it);
        }
        return 0;
    }

    public boolean isEligibleName(String t) {
        return MapleCharacterUtil.canCreateChar(t, getPlayer().isGM()) && (!LoginInformationProvider.getInstance().isForbiddenName(t) || getPlayer().isGM());
    }

    public String checkDrop(int mobId) {
        final List<MonsterDropEntry> ranks = MapleMonsterInformationProvider.getInstance().retrieveDrop(mobId);
        if (ranks != null && ranks.size() > 0) {
            int num = 0, itemId, ch;
            MonsterDropEntry de;
            StringBuilder name = new StringBuilder();
            for (int i = 0; i < ranks.size(); i++) {
                de = ranks.get(i);
                if (de.chance > 0 && (de.questid <= 0 || (de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0))) {
                    itemId = de.itemId;
                    if (num == 0) {
                        name.append("Drops for #o").append(mobId).append("#\r\n");
                        name.append("--------------------------------------\r\n");
                    }
                    String namez = "#z" + itemId + "#";
                    if (itemId == 0) { //meso
                        itemId = 4031041; //display sack of cash
                        namez = (de.Minimum * getClient().getWorldServer().getMesoRate()) + " to " + (de.Maximum * getClient().getWorldServer().getMesoRate()) + " meso";
                    }
                    ch = de.chance * getClient().getWorldServer().getDropRate();
                    name.append(num + 1).append(") #v").append(itemId).append("#").append(namez).append(" - ").append(Integer.valueOf(ch >= 999999 ? 1000000 : ch).doubleValue() / 10000.0).append("% chance. ").append(de.questid > 0 && MapleQuest.getInstance(de.questid).getName().length() > 0 ? ("Requires quest " + MapleQuest.getInstance(de.questid).getName() + " to be started.") : "").append("\r\n");
                    num++;
                }
            }
            if (name.length() > 0) {
                return name.toString();
            }

        }
        return "No drops was returned.";
    }


    public void handleDivorce() {
        if (getPlayer().getMarriageId() <= 0) {
            sendNext("Please make sure you have a marriage.");
            return;
        }
        final int chz = World.Find.findChannel(getPlayer().getMarriageId());
        final int wlz = World.Find.findWorld(getPlayer().getMarriageId());
        if (chz == -1) {
            //sql queries
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("UPDATE queststatus SET customData = ? WHERE characterid = ? AND (quest = ? OR quest = ?)");
                ps.setString(1, "0");
                ps.setInt(2, getPlayer().getMarriageId());
                ps.setInt(3, 160001);
                ps.setInt(4, 160002);
                ps.executeUpdate();
                ps.close();

                ps = con.prepareStatement("UPDATE characters SET marriageid = ? WHERE id = ?");
                ps.setInt(1, 0);
                ps.setInt(2, getPlayer().getMarriageId());
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                outputFileError(e);
                return;
            }
            setQuestRecord(getPlayer(), 160001, "0");
            setQuestRecord(getPlayer(), 160002, "0");
            getPlayer().setMarriageId(0);
            sendNext("You have been successfully divorced...");
            return;
        } else if (chz < -1) {
            sendNext("Please make sure your partner is logged on.");
            return;
        }
        MapleCharacter cPlayer = ChannelServer.getInstance(wlz, chz).getPlayerStorage().getCharacterById(getPlayer().getMarriageId());
        if (cPlayer != null) {
            cPlayer.dropMessage(1, "Your partner has divorced you.");
            cPlayer.setMarriageId(0);
            setQuestRecord(cPlayer, 160001, "0");
            setQuestRecord(getPlayer(), 160001, "0");
            setQuestRecord(cPlayer, 160002, "0");
            setQuestRecord(getPlayer(), 160002, "0");
            getPlayer().setMarriageId(0);
            sendNext("You have been successfully divorced...");
        } else {
            sendNext("An error occurred...");
        }
    }

    public String getReadableMillis(long startMillis, long endMillis) {
        return StringUtil.getReadableMillis(startMillis, endMillis);
    }

    public void sendUltimateExplorer() {
        getClient().sendPacket(CWvsContext.ultimateExplorer());
    }


    public void addPendantSlot(int days) {
        c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT)).setCustomData(String.valueOf(System.currentTimeMillis() + ((long) days * 24 * 60 * 60 * 1000)));
        c.sendPacket(CWvsContext.pendantSlot(Long.parseLong(c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT)).getCustomData()) < System.currentTimeMillis()));
    }

    public Triple<Integer, Integer, Integer> getCompensation() {
        Triple<Integer, Integer, Integer> ret = null;
        try {
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT * FROM compensationlog_confirmed WHERE chrname LIKE ?")) {
                ps.setString(1, getPlayer().getName());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ret = new Triple<>(rs.getInt("value"), rs.getInt("taken"), rs.getInt("donor"));
                    }
                }
            }
            return ret;
        } catch (SQLException e) {
            FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, e);
            return ret;
        }
    }

    public boolean deleteCompensation(int taken) {
        try {
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE compensationlog_confirmed SET taken = ? WHERE chrname LIKE ?")) {
                ps.setInt(1, taken);
                ps.setString(2, getPlayer().getName());
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, e);
            return false;
        }
    }

    public void gainAPS(int gain) {
        getPlayer().gainAPS(gain);
    }

    public void hideNpc(int npcid) {
        for (MapleMapObject npcs : c.getPlayer().getMap().getAllNPCsThreadsafe()) {
            MapleNPC npc = (MapleNPC) npcs;
            if (npc.getId() == npcid) {
                c.sendPacket(NPCTalkPacket.removeNPCController(npc.getObjectId()));
                c.sendPacket(NPCTalkPacket.removeNPC(npc.getObjectId()));
            }
        }
    }
    public ArrayList<Equip> getbangbang(int charid) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        ArrayList<Equip> all = new ArrayList<>();
        Equip eq = null;
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps;
            ps = con.prepareStatement("SELECT * FROM equipgrave WHERE characterid = ? ORDER BY equipgraveid desc");
            ps.setInt(1, charid);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                eq = (Equip) ii.getEquipById(rs.getInt("itemid"));
                eq.setUpgradeSlots(rs.getByte("upgradeslots"));
                eq.setLevel(rs.getByte("level"));

                eq.setStr(rs.getShort("str"));
                eq.setDex(rs.getShort("dex"));
                eq.setInt(rs.getShort("int"));
                eq.setLuk(rs.getShort("luk"));
                eq.setHp(rs.getShort("hp"));
                eq.setMp(rs.getShort("mp"));
                eq.setWatk(rs.getShort("watk"));
                eq.setMatk(rs.getShort("matk"));
                eq.setWdef(rs.getShort("wdef"));
                eq.setMdef(rs.getShort("mdef"));

                eq.setAcc(rs.getShort("acc"));
                eq.setAvoid(rs.getShort("avoid"));
                eq.setHands(rs.getShort("hands"));
                eq.setSpeed(rs.getShort("speed"));
                eq.setJump(rs.getShort("jump"));

                eq.setViciousHammer(rs.getByte("ViciousHammer"));
                eq.setItemEXP(rs.getInt("itemEXP"));
                eq.setDurability(rs.getInt("durability"));

                eq.setEnhance(rs.getByte("enhance"));
                eq.setPotential1(rs.getShort("potential1"));
                eq.setPotential2(rs.getShort("potential2"));
                eq.setPotential3(rs.getShort("potential3"));
                eq.setPotential4(rs.getShort("potential4"));
                eq.setPotential5(rs.getShort("potential5"));


                eq.setOwner(rs.getString("owner"));
                eq.setGMLog(String.valueOf(rs.getInt("equipgraveid")));
                eq.setFlag(rs.getByte("flag"));
                eq.setExpiration(rs.getLong("expiredate"));
                eq.setGiftFrom(rs.getString("sender"));

                eq.setExtraScroll(rs.getInt("extrascroll"));
                eq.setAddi_str((short) rs.getInt("addi_str"));
                eq.setAddi_dex((short) rs.getInt("addi_dex"));
                eq.setAddi_int((short) rs.getInt("addi_int"));
                eq.setAddi_luk((short) rs.getInt("addi_luk"));
                eq.setAddi_watk((short) rs.getInt("addi_watk"));
                eq.setAddi_matk((short) rs.getInt("addi_matk"));
                eq.setBreak_dmg(rs.getInt("break_dmg"));
            }
            all.add(eq);
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error getting character default" + e);
        }
        return all;
    }

    public int getItemQuantity(int itemid) {
        return c.getPlayer().getItemQuantity(itemid, false);
    }


    public String getPotentialString(int potId) {
        String potInfo = getPotentialInfo(potId);
        return potInfo;
    }

    public Connection getConnection() {
        return DatabaseConnection.getConnection();
    }

    public void DeleteBangEquip(int eid) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps;
            ps = con.prepareStatement("DELETE FROM equipgrave WHERE equipgraveid = ?");
            ps.setInt(1, eid);
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error getting character default" + e);
        }
    }

    public List<Integer> getSMob(String name) {
        MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File(System.getProperty("net.sf.odinms.wzpath") + "/" + "String.wz"));
        List<Integer> retMobs = new ArrayList<Integer>();
        MapleData data = null;
        data = dataProvider.getData("Mob.img");
        List<Pair<Integer, String>> mobPairList = new LinkedList<Pair<Integer, String>>();
        for (MapleData mobIdData : data.getChildren()) {

            mobPairList.add(new Pair<Integer, String>(Integer.parseInt(mobIdData.getName()), MapleDataTool.getString(mobIdData.getChildByPath("name"), "NO-NAME")));
        }
        for (Pair<Integer, String> mobPair : mobPairList) {
            if (mobPair.getRight().toLowerCase().contains(name.toLowerCase())) {
                if(MapleLifeFactory.getMonster(mobPair.getLeft()) != null)
                    retMobs.add(mobPair.getLeft());
            }
            if(retMobs.size() >= 100)
                break;
        }
        return retMobs;
    }

    public List<Integer> getSItem(String name) {
        List<Integer> retItems = new ArrayList<Integer>();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        for (ItemInformation itemPair : MapleItemInformationProvider.getInstance().getAllItems()) {
            if (itemPair.name.toLowerCase().contains(name.toLowerCase())) {
                if(ii.itemExists(itemPair.itemId) && !ii.isCash(itemPair.itemId))
                    if(itemPair.itemId < 1800000 && itemPair.itemId > 1300000) {
                        if (GameConstants.isWeapon(itemPair.itemId))
                            retItems.add(itemPair.itemId);
                    }else {
                        retItems.add(itemPair.itemId);
                    }
            }
            if(retItems.size() >= 100)
                break;
        }
        return retItems;
    }

    public List<Integer> getSItemCash(String name) {
        List<Integer> retItems = new ArrayList<Integer>();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        for (ItemInformation itemPair : MapleItemInformationProvider.getInstance().getAllItems()) {
            if (itemPair.name.toLowerCase().contains(name.toLowerCase())) {
                if(ii.itemExists(itemPair.itemId) && !ii.isCash(itemPair.itemId))
                    retItems.add(itemPair.itemId);
            }
            if(retItems.size() >= 100)
                break;
        }
        return retItems;
    }

    public List<Integer> getDropers(String name) {
        final MapleMonsterInformationProvider mi = MapleMonsterInformationProvider.getInstance();
        return mi.retrieveDroper(Integer.parseInt(name));
    }

    public List<Integer> getDrops(String name) {
        List<Integer> retItems = new ArrayList<Integer>();
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final MapleMonsterInformationProvider mi = MapleMonsterInformationProvider.getInstance();
        final List<MonsterDropEntry> dropEntry = mi.retrieveDrop(Integer.parseInt(name));
        for(MonsterDropEntry dr : dropEntry){
            if(dr.itemId != 0 && ii.itemExists(dr.itemId) && !ii.isCash(dr.itemId))
                retItems.add(dr.itemId);
        }
        return retItems;
    }

    public boolean isEnhanceItem(short slot){
        Equip equip = (Equip) getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(slot);
        return equip.getUpgradeSlots() == 0;

    }

    public Pair<Integer, Integer> getEnhRate(short slot, short scroll){
        return null;
    }


    public ArrayList<Pair<String, Integer>> getEnhDes(short slot){
        Equip equip = (Equip) getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(slot);
        boolean isW = false;
        if(GameConstants.isWeapon(equip.getItemId())){
            isW = true;
        }
        ArrayList<Pair<String, Integer>> al = new ArrayList<Pair<String, Integer>>();
        switch (equip.getEnhance()) {
            case 0:
                if (isW) {
                    al.add(new Pair<>("物攻魔攻", 5));
                } else {
                    al.add(new Pair<>("全能力", 5));
                }
                break;
            case 1:
                if (isW) {
                    al.add(new Pair<>("閃避 精準", 15));
                } else {
                    al.add(new Pair<>("閃避 精準", 7));
                }
                break;
            case 2:
                if (isW) {
                    al.add(new Pair<>("跳躍 速度", 5));
                } else {
                    al.add(new Pair<>("跳躍 速度", 8));
                }
                break;
            case 3:
                if (isW) {
                    al.add(new Pair<>("物防 魔防", 25));
                } else {
                    al.add(new Pair<>("物防 魔防", 40));
                }
                break;
            case 4:
                if (isW) {
                    al.add(new Pair<>("閃避 精準", 15));
                } else {
                    al.add(new Pair<>("閃避 精準", 7));
                }
                break;
            case 5:
                if (isW) {
                    al.add(new Pair<>("跳躍 速度", 5));
                } else {
                    al.add(new Pair<>("跳躍 速度", 8));
                }
                break;
            case 6:
                if (isW) {
                    al.add(new Pair<>("物防 魔防", 25));
                } else {
                    al.add(new Pair<>("物防 魔防", 40));
                }
                break;
            case 7:
                if (isW) {
                    al.add(new Pair<>("物攻魔攻", 5));
                } else {
                    al.add(new Pair<>("全能力", 5));
                }
                break;
            case 8:
                if (isW) {
                    al.add(new Pair<>("物攻魔攻", 6));
                } else {
                    al.add(new Pair<>("全能力", 6));
                }
                break;
            case 9:
                if (isW) {
                    al.add(new Pair<>("物攻魔攻", 7));
                } else {
                    al.add(new Pair<>("全能力", 10));
                }
                break;
            case 10:
                if (isW) {
                    al.add(new Pair<>("物攻魔攻", 8));
                } else {
                    al.add(new Pair<>("全能力", 13));
                }
                break;
            case 11:
                if (isW) {
                    al.add(new Pair<>("物攻魔攻", 9));
                } else {
                    al.add(new Pair<>("全能力", 15));
                }
                break;
            case 12:
                if (isW) {
                    al.add(new Pair<>("物攻魔攻", 10));
                    al.add(new Pair<>("全能力", 10));
                } else {
                    al.add(new Pair<>("全能力", 18));
                }
                break;
            case 13:
                if (isW) {
                    al.add(new Pair<>("物攻魔攻", 15));
                    al.add(new Pair<>("全能力", 10));
                } else {
                    al.add(new Pair<>("全能力", 20));
                }
                break;
            case 14:
                if (isW) {
                    al.add(new Pair<>("物攻魔攻", 20));
                    al.add(new Pair<>("全能力", 11));
                } else {
                    al.add(new Pair<>("全能力", 25));
                }
                break;
            case 15:
                if (isW) {
                    al.add(new Pair<>("物攻魔攻", 21));
                    al.add(new Pair<>("全能力", 12));
                } else {
                    al.add(new Pair<>("全能力", 30));
                }
                break;
            case 16:
                if (isW) {
                    al.add(new Pair<>("物攻魔攻", 22));
                    al.add(new Pair<>("全能力", 13));
                } else {
                    al.add(new Pair<>("物攻魔攻", 3));
                    al.add(new Pair<>("全能力", 30));
                }
                break;
            case 17:
                if (isW) {
                    al.add(new Pair<>("物攻魔攻", 23));
                    al.add(new Pair<>("全能力", 14));
                } else {
                    al.add(new Pair<>("物攻魔攻", 3));
                    al.add(new Pair<>("全能力", 32));
                }
                break;
            case 18:
                if (isW) {
                    al.add(new Pair<>("物攻魔攻", 24));
                    al.add(new Pair<>("全能力", 15));
                } else {
                    al.add(new Pair<>("物攻魔攻", 5));
                    al.add(new Pair<>("全能力", 33));
                }
                break;
            case 19:
                if (isW) {
                    al.add(new Pair<>("物攻魔攻", 25));
                    al.add(new Pair<>("全能力", 20));
                } else {
                    al.add(new Pair<>("物攻魔攻", 5));
                    al.add(new Pair<>("全能力", 34));
                }
                break;
            case 20:
                if (isW) {
                    al.add(new Pair<>("物攻魔攻", 30));
                    al.add(new Pair<>("全能力", 21));
                } else {
                    al.add(new Pair<>("物攻魔攻", 10));
                    al.add(new Pair<>("全能力", 40));
                }
                break;
            case 21:
                if (isW) {
                    al.add(new Pair<>("物攻魔攻", 31));
                    al.add(new Pair<>("全能力", 22));
                } else {
                    al.add(new Pair<>("物攻魔攻", 11));
                    al.add(new Pair<>("全能力", 41));
                }
                break;
            case 22:
                if (isW) {
                    al.add(new Pair<>("物攻魔攻", 32));
                    al.add(new Pair<>("全能力", 23));
                } else {
                    al.add(new Pair<>("物攻魔攻", 12));
                    al.add(new Pair<>("全能力", 42));
                }
                break;
            case 23:
                if (isW) {
                    al.add(new Pair<>("物攻魔攻", 33));
                    al.add(new Pair<>("全能力", 24));
                } else {
                    al.add(new Pair<>("物攻魔攻", 13));
                    al.add(new Pair<>("全能力", 43));
                }
                break;
            case 24:
                if (isW) {
                    al.add(new Pair<>("物攻魔攻", 34));
                    al.add(new Pair<>("全能力", 25));
                } else {
                    al.add(new Pair<>("物攻魔攻", 14));
                    al.add(new Pair<>("全能力", 44));
                }
                break;
            case 25:
                if (isW) {
                    al.add(new Pair<>("物攻魔攻", 50));
                    al.add(new Pair<>("全能力", 30));
                } else {
                    al.add(new Pair<>("物攻魔攻", 20));
                    al.add(new Pair<>("全能力", 50));
                }
                break;
            case 26:
                if (isW) {
                    al.add(new Pair<>("物攻魔攻", 60));
                    al.add(new Pair<>("全能力", 40));
                } else {
                    al.add(new Pair<>("物攻魔攻", 25));
                    al.add(new Pair<>("全能力", 60));
                }
                break;
            case 27:
                if (isW) {
                    al.add(new Pair<>("物攻魔攻", 70));
                    al.add(new Pair<>("全能力", 50));
                } else {
                    al.add(new Pair<>("物攻魔攻", 30));
                    al.add(new Pair<>("全能力", 70));
                }
                break;
            case 28:
                if (isW) {
                    al.add(new Pair<>("物攻魔攻", 80));
                    al.add(new Pair<>("全能力", 60));
                } else {
                    al.add(new Pair<>("物攻魔攻", 35));
                    al.add(new Pair<>("全能力", 80));
                }
                break;
            case 29:
                if (isW) {
                    al.add(new Pair<>("物攻魔攻", 90));
                    al.add(new Pair<>("全能力", 70));
                } else {
                    al.add(new Pair<>("物攻魔攻", 40));
                    al.add(new Pair<>("全能力", 90));
                }
                break;
                default:
                    al.add(new Pair<>("錯誤", 0));
        }
        return al;
    }


    public boolean isPotItem(short ID){
        Equip equip = (Equip) getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(ID);
        return equip.getPotential1() > 0;
    }

    public boolean itemExit(int id) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        return ii.itemExists(id);
    }

    public MapleItemInformationProvider getItemInfo() {
        return MapleItemInformationProvider.getInstance();
    }

    public String getScript() {
        return this.script;
    }

    public int getMaxEnhance(Equip eqp) {
        return  MapleItemInformationProvider.getInstance().getSlots(eqp.getItemId());
    }

    public boolean doScissor(Item item, int typep) {
        if (item != null) {
            short flag = item.getFlag();
            if ((ItemFlag.KARMA_USE.check(flag) || ItemFlag.KARMA_EQ.check(flag)) && flag != 24) {
                return false;
            }

            if(ItemFlag.LOCK.check(flag) && item.getItemId() == 1112127){
                return false;
            }
            MapleInventoryType type = MapleInventoryType.getByType((byte) typep);
            if (type == MapleInventoryType.EQUIP) {
                flag = (byte) ItemFlag.KARMA_EQ.getValue();
                item.setFlag(flag);
                c.getPlayer().forceReAddItem_Flag(item, type);
            } else {
                flag = (byte) ItemFlag.KARMA_USE.getValue();
                item.setFlag(flag);
                c.getPlayer().fakeRelog();
            }

        }
        return true;
    }

    public String getPayBill(int amount){
        Date time = Calendar.getInstance().getTime();
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(time);
        PaymentAIO mp = new PaymentAIO();
        String as = amount >= 1000?String.valueOf(amount / 100):String.valueOf(amount);
        String ss = timeStamp+as;
        String url = "";
        int key = 0;
        try (Connection con = DatabaseConnection.getConnection()) {
            boolean isC = true;
            while(isC){
                Random r = new Random();
                String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
                StringBuilder s = new StringBuilder();
                for (int i = 0; i < 6; i++) {
                    s.append(alphabet.charAt(r.nextInt(alphabet.length())));
                }
                try (PreparedStatement ps = con.prepareStatement("SELECT url from paybill_bills where url = ?")){
                    ps.setString(1, s.toString());
                    ResultSet rs = ps.executeQuery();
                    if(rs.next()){
                        continue;
                    }
                    url = s.toString();
                    isC = false;
                }
            }

            try (PreparedStatement ps = con.prepareStatement("INSERT INTO paybill_bills (BillID, money, account, accountID, characterID, Date,isSent,TradeNo, url) VALUES (DEFAULT,?, ?, ?, ?, CURRENT_TIMESTAMP,?,?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, amount);
                ps.setString(2, getPlayer().getClient().getAccountName());
                ps.setInt(3, getPlayer().getAccountID());
                ps.setInt(4, getPlayer().getId());
                ps.setInt(5, -1);
                ps.setString(6, ss);
                ps.setString(7, url);
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (!rs.next()) {
                    throw new RuntimeException("[saveItems] 保存道具失败.");
                }else{
                    key = rs.getInt(1);
                }
            }

        } catch (SQLException ex) {//130.211.243.179
            ex.printStackTrace();
        }

        String poststr = mp.getCustomBill(amount, Integer.toString(getPlayer().getAccountID()), key,time, ss);
        try {
            FileWriter fw = new FileWriter(new File("C:/Bills/" + url + ".html "));
            fw.write(poststr);
            fw.close();
        }catch (IOException e){
            System.out.println(e);
        }
        return url;
    }
    public String getDateStr(){
        return new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
    }


    public boolean addWithPara(int itemId, short str, short dex, short intt, short luk, short watk, short matk, long expire, short slot) {
        Equip eq = (Equip) MapleItemInformationProvider.getInstance().getEquipById(itemId);
        eq.setStr((short) (eq.getStr() + str));
        eq.setDex((short) (eq.getDex() + dex));
        eq.setInt((short) (eq.getInt() + intt));
        eq.setLuk((short) (eq.getLuk() + luk));
        eq.setWatk((short) (eq.getWatk() + watk));
        eq.setMatk((short) (eq.getMatk() + matk));
        eq.setUpgradeSlots((byte) (eq.getUpgradeSlots() + slot));

        eq.setAddi_str(str);
        eq.setAddi_dex(dex);
        eq.setAddi_int(intt);
        eq.setAddi_luk(luk);
        eq.setAddi_watk(watk);
        eq.setAddi_matk(matk);
        eq.setExtraScroll(slot);

        if(expire > 0 && expire < 1000L)
            eq.setExpiration(System.currentTimeMillis() + expire * 24 * 60 * 60 * 1000);
        else if(expire > 1000L)
            eq.setExpiration(System.currentTimeMillis() + expire);

        return MapleInventoryManipulator.addFromDrop(getClient(), eq, false);
    }

    public boolean addWithPara(int itemId, long expire) {
        Equip eq = (Equip) MapleItemInformationProvider.getInstance().getEquipById(itemId);

        if(expire > 0 && expire < 1000L)
            eq.setExpiration(System.currentTimeMillis() + expire * 24 * 60 * 60 * 1000);
        else if(expire > 1000L)
            eq.setExpiration(System.currentTimeMillis() + expire);

        return MapleInventoryManipulator.addFromDrop(getClient(), eq, false);
    }

    public boolean addWithPara(int itemId, long expire, boolean lock) {
        Equip eq = (Equip) MapleItemInformationProvider.getInstance().getEquipById(itemId);

        if(expire > 0 && expire < 1000L)
            eq.setExpiration(System.currentTimeMillis() + expire * 24 * 60 * 60 * 1000);
        else if(expire > 1000L)
            eq.setExpiration(System.currentTimeMillis() + expire);

        if(lock) {
            short flag = eq.getFlag();
            flag |= ItemFlag.LOCK.getValue();
            eq.setFlag(flag);
        }

        return MapleInventoryManipulator.addFromDrop(getClient(), eq, false);
    }

    public boolean addWithPara(int itemId, int quality, long expire, boolean lock, boolean tradable) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

        Item eq = ii.getEquipById(itemId);
        if(!GameConstants.isEquip(itemId)) {
            final Item ret = new Item(itemId, (byte) 0, (short) quality, (byte) 0, -1);
            if(expire > 0 && expire < 1000L)
                ret.setExpiration(System.currentTimeMillis() + expire * 24 * 60 * 60 * 1000);
            else if(expire > 1000L)
                ret.setExpiration(System.currentTimeMillis() + expire);

            if(lock) {
                short flag = ret.getFlag();
                flag |= ItemFlag.LOCK.getValue();
                ret.setFlag(flag);
            }

            if(!tradable){
                short flag = ret.getFlag();
                flag |= ItemFlag.UNTRADEABLE.getValue();
                ret.setFlag(flag);
            }

            return MapleInventoryManipulator.addFromDrop(getClient(), ret, false);
        }else {
            final Item ret = (Item) eq;
            if(expire > 0 && expire < 1000L)
                ret.setExpiration(System.currentTimeMillis() + expire * 24 * 60 * 60 * 1000);
            else if(expire > 1000L)
                ret.setExpiration(System.currentTimeMillis() + expire);

            if(lock) {
                short flag = ret.getFlag();
                flag |= ItemFlag.LOCK.getValue();
                ret.setFlag(flag);
            }

            if(!tradable){
                short flag = ret.getFlag();
                flag |= ItemFlag.UNTRADEABLE.getValue();
                ret.setFlag(flag);
            }

            return MapleInventoryManipulator.addFromDrop(getClient(), ret, false);
        }
    }

    public boolean ExistItem(final int item){

        if (!MapleItemInformationProvider.getInstance().itemExists(item))
            return false;
        else
            return true;
    }

}
