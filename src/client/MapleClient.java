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
 MERCHANTABILITY or FITESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package client;

import client.buddy.BuddyList;
import constants.GameConstants;
import constants.ServerConstants;
import constants.ServerConstants.PlayerGMRank;
import database.DatabaseConnection;
import database.DatabaseException;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.login.handler.LoginResponse;
import handling.world.*;
import handling.world.family.MapleFamilyCharacter;
import handling.world.guild.MapleGuildCharacter;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import server.CharacterCardFactory;
import server.Timer.PingTimer;
import server.maps.MapleMap;
import server.quest.MapleQuest;
import server.shops.IMaplePlayerShop;
import tools.*;
import tools.packet.LoginPacket;
import tools.types.Pair;

import javax.script.ScriptEngine;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MapleClient implements Serializable {

    public static final byte LOGIN_NOT_LOGIN = 0,
            LOGIN_SERVER_TRANSITION = 1,
            LOGIN_LOGGED = 2,
            CHANGE_CHANNEL = 3;
    public static final int DEFAULT_CHAR_SLOT = 6;
    public static final AttributeKey<MapleClient> CLIENT_KEY = AttributeKey.valueOf("Client");
    private static final long serialVersionUID = 9179541993413738569L;
    private final static Lock login_mutex = new ReentrantLock(true);
    private final transient Lock mutex = new ReentrantLock(true);
    private final transient Lock npc_mutex = new ReentrantLock();
    private transient MapleAESOFB send, receive;
    private transient Channel session;
    private MapleCharacter player;
    private MapleClient ClinetS;
    private String email = "";

    public long lastsmegaa;
    public long lastsmegacomparee;
    public long lastsack;
    public long lastsackcompare;

    public transient short loginAttempt = 0;

    private int channel = 1, accountId = -1, world = -1, birthday;
    private Map<Integer, Pair<Short, Short>> charInfo = new LinkedHashMap<>();
    private int charslots = DEFAULT_CHAR_SLOT;
    private boolean loggedIn = false, serverTransition = false;
    private transient Calendar tempban = null;
    private String accountName;
    private transient long lastPong = 0, lastPing = 0;
    private boolean monitored = false, receiving = true, isClientServer = false;
    private int gmLevel;
    private byte greason = 1, gender = -1;
    private transient List<Integer> allowedChar = new LinkedList<>();
    private Set<String> macs = new HashSet<>();
    private transient Map<String, ScriptEngine> engines = new HashMap<>();
    private transient ScheduledFuture<?> idleTask = null;
    private transient String secondPassword, salt2, tempIP = ""; // To be used only on login
    private long lastNpcClick = 0;
    private boolean tos = false;

    public MapleClient(MapleAESOFB send, MapleAESOFB receive, Channel session) {
        this.send = send;
        this.receive = receive;
        this.session = session;
    }

    public static final void banMacs(String[] macs) {
        Connection con = DatabaseConnection.getConnection();
        try {
            List<String> filtered = new LinkedList<String>();
            PreparedStatement ps = con.prepareStatement("SELECT filter FROM macfilters");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                filtered.add(rs.getString("filter"));
            }
            rs.close();
            ps.close();

            ps = con.prepareStatement("INSERT INTO macbans (mac) VALUES (?)");
            for (String mac : macs) {
                boolean matched = false;
                for (String filter : filtered) {
                    if (mac.matches(filter)) {
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    ps.setString(1, mac);
                    try {
                        ps.executeUpdate();
                    } catch (SQLException e) {
                        // can fail because of UNIQUE key, we dont care
                    }
                }
            }
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error banning MACs" + e);
        }
    }

    public static boolean checkHash(String hash, String type, String password) {
        try {
            MessageDigest digester = MessageDigest.getInstance(type);
            digester.update(password.getBytes("UTF-8"), 0, password.length());
            return HexTool.toString(digester.digest()).replace(" ", "").toLowerCase().equals(hash);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException("Encoding the string failed", e);
        }
    }

    public static byte unban(String charname) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT accountid from characters where name = ?");
            ps.setString(1, charname);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            final int accid = rs.getInt(1);
            rs.close();
            ps.close();

            ps = con.prepareStatement("UPDATE accounts SET banned = 0, banreason = '' WHERE id = ?");
            ps.setInt(1, accid);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error while unbanning" + e);
            return -2;
        }
        return 0;
    }

    public static byte unbanaccs(String accname) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT id from accounts where name = ?");
            ps.setString(1, accname);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            final int accid = rs.getInt(1);
            rs.close();
            ps.close();

            ps = con.prepareStatement("UPDATE accounts SET banned = 0, points = 0, vpoints = 0, nxprepaid = 0, mPoints = 0, nxcredit = 0, banreason = '' WHERE id = ?");
            ps.setInt(1, accid);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error while unbanning" + e);
            return -2;
        }
        return 0;
    }

    public static String getLogMessage(final MapleClient cfor, final String message) {
        return getLogMessage(cfor, message, new Object[0]);
    }

    public static String getLogMessage(final MapleCharacter cfor, final String message) {
        return getLogMessage(cfor == null ? null : cfor.getClient(), message);
    }

    public static String getLogMessage(final MapleCharacter cfor, final String message, final Object... parms) {
        return getLogMessage(cfor == null ? null : cfor.getClient(), message, parms);
    }

    public static String getLogMessage(final MapleClient cfor, final String message, final Object... parms) {
        final StringBuilder builder = new StringBuilder();
        if (cfor != null) {
            if (cfor.getPlayer() != null) {
                builder.append("<");
                builder.append(MapleCharacterUtil.makeMapleReadable(cfor.getPlayer().getName()));
                builder.append(" (cid: ");
                builder.append(cfor.getPlayer().getId());
                builder.append(")> ");
            }
            if (cfor.getAccountName() != null) {
                builder.append("(Account: ");
                builder.append(cfor.getAccountName());
                builder.append(") ");
            }
        }
        builder.append(message);
        int start;
        for (final Object parm : parms) {
            start = builder.indexOf("{}");
            builder.replace(start, start + 2, parm.toString());
        }
        return builder.toString();
    }

    public static int findAccIdForCharacterName(final String charName) {
        try {
            Connection con = DatabaseConnection.getConnection();
            int ret;
            try (PreparedStatement ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ?")) {
                ps.setString(1, charName);
                try (ResultSet rs = ps.executeQuery()) {
                    ret = -1;
                    if (rs.next()) {
                        ret = rs.getInt("accountid");
                    }
                }
            }

            return ret;
        } catch (final SQLException e) {
            System.err.println("findAccIdForCharacterName SQL error");
        }
        return -1;
    }

    public static byte unbanIPMacs(String charname) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT accountid from characters where name = ?");
            ps.setString(1, charname);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            final int accid = rs.getInt(1);
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
            ps.setInt(1, accid);
            rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            String sessionIP = rs.getString("sessionIP");
            String macs = rs.getString("macs");
            rs.close();
            ps.close();
            byte ret = 0;
            if (sessionIP != null) {
                try (PreparedStatement psa = con.prepareStatement("DELETE FROM ipbans WHERE ip like ?")) {
                    psa.setString(1, sessionIP);
                    psa.execute();
                }
                ret++;
            }
            if (macs != null) {
                String[] macz = macs.split(", ");
                for (String mac : macz) {
                    if (!mac.equals("")) {
                        try (PreparedStatement psa = con.prepareStatement("DELETE FROM macbans WHERE mac = ?")) {
                            psa.setString(1, mac);
                            psa.execute();
                        }
                    }
                }
                ret++;
            }
            return ret;
        } catch (SQLException e) {
            System.err.println("Error while unbanning" + e);
            return -2;
        }
    }

    public static byte unbanbyAccId(String accountName) {
        try {
            Connection con = DatabaseConnection.getConnection();

            PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE name = ?");
            ps.setString(1, accountName);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            String sessionIP = rs.getString("sessionIP");
            String macs = rs.getString("macs");
            rs.close();
            ps.close();
            byte ret = 0;
            if (sessionIP != null) {
                try (PreparedStatement psa = con.prepareStatement("DELETE FROM ipbans WHERE ip like ?")) {
                    psa.setString(1, sessionIP);
                    psa.execute();
                }
                ret++;
            }
            if (macs != null) {
                String[] macz = macs.split(", ");
                for (String mac : macz) {
                    if (!mac.equals("")) {
                        try (PreparedStatement psa = con.prepareStatement("DELETE FROM macbans WHERE mac = ?")) {
                            psa.setString(1, mac);
                            psa.execute();
                        }
                    }
                }
                ret++;
            }
            return ret;
        } catch (SQLException e) {
            System.err.println("Error while unbanning" + e);
            return -2;
        }
    }

    public static byte unHellban(String charname) {
        try {
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT accountid from characters where name = ?");
            ps.setString(1, charname);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            final int accid = rs.getInt(1);
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
            ps.setInt(1, accid);
            rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            final String sessionIP = rs.getString("sessionIP");
            final String email = rs.getString("email");
            rs.close();
            ps.close();
            ps = con.prepareStatement("UPDATE accounts SET banned = 0, banreason = '' WHERE email = ?" + (sessionIP == null ? "" : " OR sessionIP = ?"));
            ps.setString(1, email);
            if (sessionIP != null) {
                ps.setString(2, sessionIP);
            }
            ps.execute();
            ps.close();
            return 0;
        } catch (SQLException e) {
            System.err.println("Error while unbanning" + e);
            return -2;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void sendPacket(byte[] packet) {
        session.writeAndFlush(packet);
    }

    public final MapleAESOFB getReceiveCrypto() {
        return receive;
    }

    public final MapleAESOFB getSendCrypto() {
        return send;
    }

    public final Channel getSession() {
        return session;
    }

    public final Lock getLock() {
        return mutex;
    }

    public final Lock getNPCLock() {
        return npc_mutex;
    }

    public MapleCharacter getPlayer() {
        return player;
    }

    public void setPlayer(MapleCharacter player) {
        this.player = player;
    }

    public final String getLiteralIP() {
        String ip = session.remoteAddress().toString();
        ip = ip.split(":")[0];
        ip = ip.replaceAll("/", "");
        return ip;
    }

    public void createdChar(final int id) {
        allowedChar.add(id);
    }

    public final boolean login_Auth(final int id) {
        return allowedChar.contains(id);
    }

    public final List<MapleCharacter> loadCharacters(final int serverId) { // TODO make this less costly zZz
        final List<MapleCharacter> chars = new LinkedList<>();

        for (final CharNameAndId cni : loadCharactersInternal(serverId)) {
            MapleCharacter chr = MapleCharacter.loadCharFromDB(cni.id, this, false);
            chars.add(chr);
            this.charInfo.put(chr.getId(), new Pair<>(chr.getLevel(), chr.getJob()));
            if (!login_Auth(chr.getId())) {
                allowedChar.add(chr.getId());
            }
        }
        return chars;
    }

    public Pair<Byte, Long> getPartTimeJob(int cid) {
        Pair<Byte, Long> data = null;

        Connection con = DatabaseConnection.getConnection();
        try {
            try (PreparedStatement ps = con.prepareStatement("SELECT `partTime_id`, `partTime_start` FROM `characters` WHERE `id` = ? AND `accountid` = ? LIMIT 1")) {
                ps.setInt(1, cid);
                ps.setInt(2, this.accountId);
                ResultSet rs = ps.executeQuery();
                try {
                    if (rs.next()) {
                        data = new Pair(rs.getByte("partTime_id"), rs.getLong("partTime_start"));
                    }
                } catch (SQLException e) {
                    System.out.println(e.toString());
                }
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return data;
    }

    public boolean canMakePartTimeJob() {
        if (this.allowedChar.isEmpty()) {
            return false;
        }
        boolean ret = false;
        Connection con = DatabaseConnection.getConnection();
        try {
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM `characters` WHERE `accountid` = ? AND `partTime_id` > 0 LIMIT 1")) {
                ps.setInt(1, this.accountId);
                ResultSet rs = ps.executeQuery();
                try {
                    if (rs.next()) {
                        ret = rs.getInt(1) < 3;
                    }
                } catch (SQLException e) {
                    System.out.println(e.toString());
                }
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return ret;
    }

    public boolean updatePartTimeJob(int cid, byte type, long time) {
        boolean ret = false;
        Connection con = DatabaseConnection.getConnection();
        try {
            try (PreparedStatement ps = con.prepareStatement("UPDATE `characters` SET `partTime_id` = ?, `partTime_start` = ? WHERE `id` = ? AND `accountid` = ?")) {
                try {
                    ps.setByte(1, type);
                    ps.setLong(2, time);
                    ps.setInt(3, cid);
                    ps.setInt(4, this.accountId);
                    if (ps.executeUpdate() > 0) {
                        ret = true;
                    }
                } catch (SQLException e) {
                    System.out.println(e.toString());
                } finally {
                    if (ps != null) {

                        ps.close();

                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return ret;
    }

    public final void updateCharacterCards(Map<Integer, Integer> cids) {
        if (this.charInfo.isEmpty()) {
            return;
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM `character_cards` WHERE `accountid` = ?")) {
                ps.setInt(1, this.accountId);
                ps.executeUpdate();
            }
            try (PreparedStatement psu = con.prepareStatement("INSERT INTO `character_cards` (accountid, worldid, characterid, position) VALUES (?, ?, ?, ?)")) {
                for (Map.Entry ii : cids.entrySet()) {
                    Pair info = (Pair) this.charInfo.get(ii.getValue());
                    if ((info == null) || (((Integer) ii.getValue()) == 0) || (!CharacterCardFactory.getInstance().canHaveCard(((Short) info.getLeft()), ((Short) info.getRight())))) {
                        continue;
                    }
                    psu.setInt(1, this.accountId);
                    psu.setInt(2, this.world);
                    psu.setInt(3, ((Integer) ii.getValue()));
                    psu.setInt(4, ((Integer) ii.getKey()));
                    psu.executeUpdate();
                }
            }
        } catch (SQLException sqlE) {
            System.out.println(new StringBuilder().append("Failed to update character cards. Reason: ").append(sqlE.toString()).toString());
        }
    }

    public boolean canMakeCharacter(int serverId) {
        return loadCharactersSize(serverId) < 15;
    }

    public List<String> loadCharacterNames(int serverId) {
        List<String> chars = new LinkedList<>();
        for (CharNameAndId cni : loadCharactersInternal(serverId)) {
            chars.add(cni.name);
        }
        return chars;
    }

    public List<Integer> loadCharacterIds(int serverId) {
        List<Integer> chars = new LinkedList<>();
        for (CharNameAndId cni : loadCharactersInternal(serverId)) {
            chars.add(cni.id);
        }
        return chars;
    }

    private List<CharNameAndId> loadCharactersInternal(int serverId) {
        List<CharNameAndId> chars = new LinkedList<>();
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT id, name, gm FROM characters WHERE accountid = ? AND world = ?")) {
                ps.setInt(1, accountId);
                ps.setInt(2, serverId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        chars.add(new CharNameAndId(rs.getString("name"), rs.getInt("id")));
                        LoginServer.getLoginAuth(rs.getInt("id"));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("error loading characters internal");
        }
        return chars;
    }

    private int loadCharactersSize(int serverId) {
        int chars = 0;
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT count(*) FROM characters WHERE accountid = ? AND world = ?")) {
                ps.setInt(1, accountId);
                ps.setInt(2, serverId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        chars = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("error loading characters internal");
        }
        return chars;
    }

    public boolean isLoggedIn() {
        return loggedIn && accountId > 0;
    }

    private Calendar getTempBanCalendar(ResultSet rs) throws SQLException {
        Calendar lTempban = Calendar.getInstance();
        if (rs.getTimestamp("tempban") == null) { // basically if timestamp in db is 0000-00-00
            lTempban.setTimeInMillis(0);
            return lTempban;
        }
        Calendar today = Calendar.getInstance();
        lTempban.setTimeInMillis(rs.getTimestamp("tempban").getTime());
        if (today.getTimeInMillis() < lTempban.getTimeInMillis()) {
            return lTempban;
        }
        lTempban.setTimeInMillis(0);
        return lTempban;
    }

    public Calendar getTempBanCalendar() {
        return tempban;
    }

    public byte getBanReason() {
        return greason;
    }

    public boolean hasBannedIP() {
        boolean ret = false;
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM ipbans WHERE ? LIKE CONCAT(ip, '%')")) {
                ps.setString(1, getSessionIPAddress());
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        ret = true;
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error checking ip bans" + ex);
        }
        return ret;
    }

    public boolean hasBannedMac() {
        if (macs.isEmpty()) {
            return false;
        }
        boolean ret = false;
        int i;
        try {
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM macbans WHERE mac IN (");
            for (i = 0; i < macs.size(); i++) {
                sql.append("?");
                if (i != macs.size() - 1) {
                    sql.append(", ");
                }
            }
            sql.append(")");
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql.toString())) {
                i = 0;
                for (String mac : macs) {
                    i++;
                    ps.setString(i, mac);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        ret = true;
                    }
                }
            }
        } catch (Exception e) {
        }
        return ret;
    }

    private void loadMacsIfNescessary() throws SQLException {
        if (macs.isEmpty()) {
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("SELECT macs FROM accounts WHERE id = ?")) {
                ps.setInt(1, accountId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        for (String mac : rs.getString("macs").split(", ")) {
                            if (!mac.equals("")) {
                                macs.add(mac);
                            }
                        }
                    }
                }
            }
        }
    }

    public void banMacs() {
        Connection con = DatabaseConnection.getConnection();
        try {
            loadMacsIfNescessary();
            List<String> filtered = new LinkedList<>();
            try (PreparedStatement ps = con.prepareStatement("SELECT filter FROM macfilters"); ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    filtered.add(rs.getString("filter"));
                }
            }
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO macbans (mac) VALUES (?)")) {
                for (String mac : macs) {
                    boolean matched = false;
                    for (String filter : filtered) {
                        if (mac.matches(filter)) {
                            matched = true;
                            break;
                        }
                    }
                    if (!matched) {
                        ps.setString(1, mac);
                        ps.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
        }
    }

    public int finishLogin() {
        login_mutex.lock();
        try {
            final byte state = getLoginState();
            if (state > MapleClient.LOGIN_NOT_LOGIN) { // already loggedin
                loggedIn = false;
                return 7;
            }
            updateLoginState(MapleClient.LOGIN_LOGGED, getSessionIPAddress());
        } finally {
            login_mutex.unlock();
        }
        return 0;
    }


    public LoginResponse login(String account, String password) {
        Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement("INSERT INTO `loginlog` VALUES(?,?,?,?,CURRENT_TIMESTAMP,?)")) {
            ps.setString(1, account);
            ps.setString(2, password);
            ps.setString(3, account);
            ps.setString(4, getSessionIPAddress());
            ps.setString(5, getMacs().toString());
            ps.execute();
        } catch (Exception ignored) {
        }

        if (hasBannedIP()) {
            return LoginResponse.IP_NOT_ALLOWED;
        } else if (hasBannedMac()) {
            return LoginResponse.ACCOUNT_BLOCKED;
        }

        int db_banned = 0;
        String db_passwordHash = "";
        String db_passwordSalt = "";
        String db_SessionIP = "";
        String db_macs = "";
        long lastLogin = 0;

        try (PreparedStatement ps = con.prepareStatement("SELECT id, banned, password, salt, macs, 2ndpassword,lastlogin,  gm, greason, tempban, gender, SessionIP FROM accounts WHERE name = ?")) {
            ps.setString(1, account);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    db_banned = rs.getInt("banned");
                    db_passwordHash = rs.getString("password");
                    db_passwordSalt = rs.getString("salt");
                    db_SessionIP = rs.getString("SessionIP");
                    db_macs = rs.getString("macs");
                    accountId = rs.getInt("id");
                    secondPassword = rs.getString("2ndpassword");
                    gmLevel = rs.getInt("gm");
                    //bannedReason = rs.getByte("greason");
                    tempban = getTempBanCalendar(rs);
                    gender = rs.getByte("gender");
                    Timestamp ll = rs.getTimestamp("lastlogin");
                    if (ll != null) {
                        lastLogin = ll.getTime();
                    }
                    ps.close();
                } else {
                    return LoginResponse.NOT_REGISTERED;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            FileoutputUtil.log("Login_Exception.txt", "Account : " + account + " login raise some exception !" + e.getMessage());
            return LoginResponse.SYSTEM_ERROR;

        }

        boolean updatePasswordHash = false;
        if (!checkLoginPassword(password, db_passwordHash, db_passwordSalt)) {
            if (password.equals(db_passwordHash)) {
                updatePasswordHash = true;
            } else {
                loggedIn = false;
                return LoginResponse.WRONG_PASSWORD;
            }
        }

        if (db_banned > 0 && !isGM()) {
            return LoginResponse.ACCOUNT_BLOCKED;
        }

        boolean is_p = false;
        for(MapleClient cl : World.pending_clients){
            if(cl.getAccountName().equals(account) && cl.getSessionIPAddress().equalsIgnoreCase(getSessionIPAddress())){
                is_p = true;
                setClinetS(cl);
            }
        }

        if(!is_p && (!ServerConstants.DEBUG))
            return LoginResponse.SYSTEM_ERROR;

        int loginState = getLoginState();
        if (loginState > 0) {
            if (loginState == MapleClient.CHANGE_CHANNEL
                    || loginState == MapleClient.LOGIN_SERVER_TRANSITION) {
                if (lastLogin + 60000 > System.currentTimeMillis()) {
                    return LoginResponse.IN_TRANSMISSION;
                } else {
                    return LoginResponse.ALREADY_LOGGED_IN;
                }
            } else {
                return LoginResponse.ALREADY_LOGGED_IN;
            }
        }

        if (isGM()) {
            updateSaltedPasswordHash(password);
        } else if (updatePasswordHash) {
            updatePasswordHash(password);
        }
        return LoginResponse.LOGIN_SUCCESS;
    }

    public final boolean isSetSecondPassword() {
        return !(this.gender == 10 || this.secondPassword == null || this.secondPassword.isEmpty());
    }

    public boolean check2ndPassword(String secondPassword) {
        boolean allow = false;
        // Check if the passwords are correct here. :B
        if (checkHash(this.secondPassword, "SHA-1", secondPassword)) {
            allow = true;
        }
        return allow;
    }

    public int getAccountIdByLogin(String name) {
        Connection con = DatabaseConnection.getConnection();
        int accid = 0;
        try {
            PreparedStatement ps = con.prepareStatement("SELECT id FROM accounts WHERE name = ? LIMIT 1");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                accid = rs.getInt("id");
            }
            ps.close();
            rs.close();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return accid;
    }

    public boolean CheckSecondPassword(String in) {
        return true;
    }

    private void unban() {
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET banned = 0, banreason = '' WHERE id = ?")) {
                ps.setInt(1, accountId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error while unbanning" + e);
        }
    }

    private boolean updateSaltedPasswordHash(String password) {
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement pss = con.prepareStatement("UPDATE `accounts` SET `password` = ?, `salt` = ? WHERE id = ?")) {
            final String newSalt = CryptoTool.makeSalt();
            pss.setString(1, CryptoTool.makeSaltedSha512Hash(password, newSalt));
            pss.setString(2, newSalt);
            pss.setInt(3, accountId);
            pss.executeUpdate();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    private boolean updatePasswordHash(String password) {
        Connection con = DatabaseConnection.getConnection();
        try (PreparedStatement pss = con.prepareStatement("UPDATE `accounts` SET `password` = ? WHERE id = ?")) {
            pss.setString(1, CryptoTool.hexSha1(password));
            pss.setInt(2, accountId);
            pss.executeUpdate();
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    private boolean checkLoginPassword(String password, String hash, String salt) {
        if (CryptoLegacy.isLegacyPassword(hash)
                && CryptoLegacy.checkPassword(password, hash)) {
            return true;
        }
        if (salt == null
                && CryptoTool.checkSha1Hash(hash, password)) {
            return true;
        }
        return CryptoTool.checkSaltedSha512Hash(hash, password, salt);
    }

    public void updateMacs(String macData) {
        macs.addAll(Arrays.asList(macData.split(", ")));
        StringBuilder newMacData = new StringBuilder();
        Iterator<String> iter = macs.iterator();
        while (iter.hasNext()) {
            newMacData.append(iter.next());
            if (iter.hasNext()) {
                newMacData.append(", ");
            }
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET macs = ? WHERE id = ?")) {
                ps.setString(1, newMacData.toString());
                ps.setInt(2, accountId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error saving MACs" + e);
        }
    }

    public int getAccID() {
        return this.accountId;
    }

    public void setAccID(int id) {
        this.accountId = id;
    }

    public final void updateLoginState(final int newstate, final String SessionID) { // TODO hide?
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET loggedin = ?, SessionIP = ?, lastlogin = CURRENT_TIMESTAMP() WHERE id = ?")) {
                ps.setInt(1, newstate);
                ps.setString(2, SessionID);
                ps.setInt(3, getAccID());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("error updating login state" + e);
        }
        if (newstate == MapleClient.LOGIN_NOT_LOGIN) {
            loggedIn = false;
            serverTransition = false;
        } else {
            serverTransition = (newstate == MapleClient.LOGIN_SERVER_TRANSITION || newstate == MapleClient.CHANGE_CHANNEL);
            loggedIn = !serverTransition;
        }
    }

    public final void updateSecondPassword() {
        try {
            final Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE `accounts` SET `2ndpassword` = ?, `salt2` = null WHERE id = ?")) {
                ps.setString(1, CryptoTool.hexSha1(this.secondPassword));
                ps.setInt(2, accountId);
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            System.err.println("error updating login state" + e);
        }
    }

    public final void changeSecondPassword() {
        try {
            final Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE `accounts` SET `2ndpassword` = ?, `salt2` = null WHERE id = ?")) {
                ps.setString(1, "13337");
                ps.setInt(2, accountId);
                ps.executeUpdate();
                ps.close();
            }
        } catch (SQLException e) {
            System.err.println("error updating login state" + e);
        }
    }

    public final byte getLoginState() { // TODO hide?
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps;
            ps = con.prepareStatement("SELECT loggedin, lastlogin, banned, `birthday` + 0 AS `bday` FROM accounts WHERE id = ?");
            ps.setInt(1, getAccID());
            byte state;
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || rs.getInt("banned") > 0) {
                    ps.close();
                    rs.close();
                    session.close();
                    throw new DatabaseException("Account doesn't exist or is banned");
                }
                birthday = rs.getInt("bday");
                state = rs.getByte("loggedin");
                if (state == MapleClient.LOGIN_SERVER_TRANSITION || state == MapleClient.CHANGE_CHANNEL) {
                    if (rs.getTimestamp("lastlogin").getTime() + 20000 < System.currentTimeMillis()) { // connecting to chanserver timeout
                        state = MapleClient.LOGIN_NOT_LOGIN;
                        updateLoginState(state, getSessionIPAddress());
                    }
                }
            }
            ps.close();
            if (state == MapleClient.LOGIN_LOGGED) {
                loggedIn = true;
            } else {
                loggedIn = false;
            }
            return state;
        } catch (SQLException e) {
            loggedIn = false;
            throw new DatabaseException("error getting login state", e);
        }
    }

    public final boolean checkBirthDate(final int date) {
        return birthday == date;
    }

    public final void removalTask(boolean shutdown) {
        try {
            player.cancelAllBuffs_();
            player.silenClearAllDiseaseBuffs();
            if (player.getMarriageId() > 0) {
                final MapleQuestStatus stat1 = player.getQuestNoAdd(MapleQuest.getInstance(160001));
                final MapleQuestStatus stat2 = player.getQuestNoAdd(MapleQuest.getInstance(160002));
                if (stat1 != null && stat1.getCustomData() != null && (stat1.getCustomData().equals("2_") || stat1.getCustomData().equals("2"))) {
                    //dc in process of marriage
                    if (stat2 != null && stat2.getCustomData() != null) {
                        stat2.setCustomData("0");
                    }
                    stat1.setCustomData("3");
                }
            }
            if (player.getMapId() == GameConstants.JAIL) {
                final MapleQuestStatus stat1 = player.getQuestNAdd(MapleQuest.getInstance(GameConstants.JAIL_TIME));
                final MapleQuestStatus stat2 = player.getQuestNAdd(MapleQuest.getInstance(GameConstants.JAIL_QUEST));
                if (stat1.getCustomData() == null) {
                    stat1.setCustomData(String.valueOf(System.currentTimeMillis()));
                } else if (stat2.getCustomData() == null) {
                    stat2.setCustomData("0"); //seconds of jail
                } else { //previous seconds - elapsed seconds
                    int seconds = Integer.parseInt(stat2.getCustomData()) - (int) ((System.currentTimeMillis() - Long.parseLong(stat1.getCustomData())) / 1000);
                    if (seconds < 0) {
                        seconds = 0;
                    }
                    stat2.setCustomData(String.valueOf(seconds));
                }
            }
            player.changeRemoval(true);
            if (player.getEventInstance() != null) {
                player.getEventInstance().playerDisconnected(player, player.getId());
            }
            final IMaplePlayerShop shop = player.getPlayerShop();
            if (shop != null) {
                shop.removeVisitor(player);
                if (shop.isOwner(player)) {
                    if (shop.getShopType() == 1 && shop.isAvailable() && !shutdown) {
                        shop.setOpen(true);
                    } else {
                        shop.closeShop(true, !shutdown);
                    }
                }
            }
            player.setMessenger(null);
            if (player.getMap() != null) {
                if (shutdown || (getChannelServer() != null && getChannelServer().isShutdown())) {
                    int questID = -1;
                    switch (player.getMapId()) {
                        case 240060200: //HT
                            questID = 160100;
                            break;
                        case 240060201: //ChaosHT
                            questID = 160103;
                            break;
                        case 280030000: //Zakum
                            questID = 160101;
                            break;
                        case 280030001: //ChaosZakum
                            questID = 160102;
                            break;
                        case 270050100: //PB
                            questID = 160101;
                            break;
                        case 105100300: //Balrog
                        case 105100400: //Balrog
                            questID = 160106;
                            break;
                        case 211070000: //VonLeon
                        case 211070100: //VonLeon
                        case 211070101: //VonLeon
                        case 211070110: //VonLeon
                            questID = 160107;
                            break;
                        case 551030200: //scartar
                            questID = 160108;
                            break;
                        case 262031300: // hilla
                            questID = 160110;
                            break;
                        case 272030400:
                            questID = 160111;
                            break;
                        case 271040100: //cygnus
                            questID = 160109;
                            break;
                    }
                    if (questID > 0) {
                        player.getQuestNAdd(MapleQuest.getInstance(questID)).setCustomData("0"); //reset the time.
                    }
                } else if (player.isAlive()) {
                    switch (player.getMapId()) {
                        case 541010100: //latanica
                        case 541020800: //krexel
                        case 220080001: //pap
                            player.getMap().addDisconnected(player.getId());
                            break;
                    }
                }
                player.getMap().removePlayer(player);
            }
        } catch (final Throwable e) {
            FileoutputUtil.outputFileError(FileoutputUtil.Acc_Stuck, e);
        }
    }

    public final void disconnect(boolean RemoveInChannelServer, boolean fromCS) {
        disconnect(RemoveInChannelServer, fromCS, false);
    }

    public final void disconnect(final boolean RemoveInChannelServer, final boolean fromCS, final boolean shutdown) {
        if (player != null) {
            if (player.getMaster() > 0) {
                player.getMster().dropMessage(5, "Due to your Apprentice disconnecting, your Apprentice has been reset.");
                player.getMster().setApprentice(0);
                player.setMaster(0);
            }
            if (player.getApprentice() > 0) {
                player.getApp().dropMessage(5, "Due to your Master disconnecting, your Master has been reset.");
                player.getApp().setMaster(0);
                player.setApprentice(0);
            }
            MapleMap map = player.getMap();
            final MapleParty party = player.getParty();
            final boolean clone = player.isClone();
            final String namez = player.getName();
            final int idz = player.getId(), messengerid = player.getMessenger() == null ? 0 : player.getMessenger().getId(), gid = player.getGuildId(), fid = player.getFamilyId();
            final BuddyList bl = player.getBuddylist();
            final MaplePartyCharacter chrp = new MaplePartyCharacter(player);
            final MapleMessengerCharacter chrm = new MapleMessengerCharacter(player);
            final MapleGuildCharacter chrg = player.getMGC();
            final MapleFamilyCharacter chrf = player.getMFC();

            removalTask(shutdown);
            LoginServer.getLoginAuth(player.getId());
            player.saveToDB(true, fromCS);
            if (shutdown) {
                player = null;
                receiving = false;
                return;
            }

            if (!fromCS) {
                final ChannelServer ch = ChannelServer.getInstance(world, map == null ? channel : map.getChannel());
                final int chz = World.Find.findChannel(idz);
                if (chz < -1) {
                    disconnect(RemoveInChannelServer, true);//u lie
                    return;
                }
                try {
                    if (chz == -1 || ch == null || clone || ch.isShutdown()) {
                        player = null;
                        return;//no idea
                    }
                    if (messengerid > 0) {
                        World.Messenger.leaveMessenger(messengerid, chrm);
                    }
                    if (party != null) {
                        chrp.setOnline(false);
                        World.Party.updateParty(party.getId(), PartyOperation.LOG_ONOFF, chrp);
                        if (map != null && party.getLeader().getId() == idz) {
                            MaplePartyCharacter lchr = null;
                            for (MaplePartyCharacter pchr : party.getMembers()) {
                                if (pchr != null && map.getCharacterById(pchr.getId()) != null && (lchr == null || lchr.getLevel() < pchr.getLevel())) {
                                    lchr = pchr;
                                }
                            }
                            if (lchr != null) {
                                World.Party.updateParty(party.getId(), PartyOperation.CHANGE_LEADER_DC, lchr);
                            }
                        }
                    }
                    if (bl != null) {
                        if (!serverTransition) {
                            World.Buddy.loggedOff(namez, idz, channel, bl.getBuddyIds());
                        } else { // Change channel
                            World.Buddy.loggedOn(namez, idz, channel, bl.getBuddyIds());
                        }
                    }
                    if (gid > 0 && chrg != null) {
                        World.Guild.setGuildMemberOnline(chrg, false, -1);
                    }
                    if (fid > 0 && chrf != null) {
                        World.Family.setFamilyMemberOnline(chrf, false, -1);
                    }
                } catch (final Exception e) {
                    FileoutputUtil.outputFileError(FileoutputUtil.Acc_Stuck, e);
                    System.err.println(getLogMessage(this, "ERROR") + e);
                } finally {
                    if (RemoveInChannelServer && ch != null) {
                        ch.getPlayerStorage().removePlayer(idz);
                        World world = LoginServer.getInstance().getWorld(getWorld());
                        world.getPlayerStorage().removePlayer(player.getId());
                    }
                    //    player.getClient().getSession().close();
                    player = null;

                }
            } else {
                final int ch = World.Find.findChannel(idz);
                if (ch > 0) {
                    disconnect(RemoveInChannelServer, false);//u lie
                    return;
                }
                try {
                    if (party != null) {
                        chrp.setOnline(false);
                        World.Party.updateParty(party.getId(), PartyOperation.LOG_ONOFF, chrp);
                    }
                    if (!serverTransition) {
                        World.Buddy.loggedOff(namez, idz, channel, bl.getBuddyIds());
                    } else { // Change channel
                        World.Buddy.loggedOn(namez, idz, channel, bl.getBuddyIds());
                    }
                    if (gid > 0 && chrg != null) {
                        World.Guild.setGuildMemberOnline(chrg, false, -1);
                    }
                    if (fid > 0 && chrf != null) {
                        World.Family.setFamilyMemberOnline(chrf, false, -1);
                    }
                    if (player != null) {
                        player.setMessenger(null);
                    }
                } catch (final Exception e) {
                    FileoutputUtil.outputFileError(FileoutputUtil.Acc_Stuck, e);
                    System.err.println(getLogMessage(this, "ERROR") + e);
                } finally {
                    if (RemoveInChannelServer && ch > 0) {
                        CashShopServer.getPlayerStorage().removePlayer(idz);
                    }
                    player = null;
                }
            }
        }
        if (!serverTransition && isLoggedIn()) {
            updateLoginState(MapleClient.LOGIN_NOT_LOGIN, getSessionIPAddress());
        }
        engines.clear();
    }

    public final String getSessionIPAddress() {
        if (session != null && session.remoteAddress() != null) {
            return session.remoteAddress().toString().split(":")[0];
        } else {
            return getLastIPAddress();
        }
    }

    public final String getLastIPAddress() {
        String sessionIP = null;
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT SessionIP FROM accounts WHERE id = ?")) {
                ps.setInt(1, this.accountId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        sessionIP = rs.getString("SessionIP");
                    }
                }
            }
        } catch (final SQLException e) {
            System.err.println("Failed in checking IP address for client.");
        }
        return sessionIP == null ? "" : sessionIP;
    }

    public final boolean CheckIPAddress() {
        boolean canlogin = false;
        final String sessionIP = getLastIPAddress();
        if (!sessionIP.isEmpty()) { // Probably a login proced skipper?
            canlogin = getSessionIPAddress().equals(sessionIP.split(":")[0]);
        }
        return canlogin;
    }

    public final void DebugMessage(final StringBuilder sb) {
        sb.append("IP:");
        sb.append(getSession().remoteAddress());
        sb.append(" 連接狀態:");
        sb.append(getSession().isActive());
        sb.append(" ClientKeySet:");
        sb.append(getSession().attr(MapleClient.CLIENT_KEY).get() != null);
        sb.append(" 是否已登入:");
        sb.append(isLoggedIn());
        sb.append(" 角色上線:");
        sb.append(getPlayer() != null);
    }

    public final int getChannel() {
        return channel;
    }

    public final void setChannel(final int channel) {
        this.channel = channel;
    }

    public final ChannelServer getChannelServer() {
        return LoginServer.getInstance().getChannel(world, channel);
    }

    public World getWorldServer() {
        return LoginServer.getWorld(world);
    }

    public final int deleteCharacter(final int cid) {
        try {
            final Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT guildid, guildrank, familyid, name FROM characters WHERE id = ? AND accountid = ?")) {
                ps.setInt(1, cid);
                ps.setInt(2, accountId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        rs.close();
                        ps.close();
                        return 9;
                    }
                    if (rs.getInt("guildid") > 0) { // is in a guild when deleted
                        if (rs.getInt("guildrank") == 1) { //cant delete when leader
                            rs.close();
                            ps.close();
                            return 22;
                        }
                        World.Guild.deleteGuildCharacter(rs.getInt("guildid"), cid);
                    }
                    if (rs.getInt("familyid") > 0 && World.Family.getFamily(rs.getInt("familyid")) != null) {
                        World.Family.getFamily(rs.getInt("familyid")).leaveFamily(cid);
                    }
                }
            }

            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM characters WHERE id = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM hiredmerch WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM mts_cart WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM mts_items WHERE characterid = ?", cid);
            //MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM cheatlog WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM mountdata WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM inventoryitems WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM famelog WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM famelog WHERE characterid_to = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM dueypackages WHERE RecieverId = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM wishlist WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM buddies WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM buddies WHERE buddyid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM keymap WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM trocklocations WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM regrocklocations WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM hyperrocklocations WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM savedlocations WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM skills WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM familiars WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM mountdata WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM skillmacros WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM trocklocations WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM queststatus WHERE characterid = ?", cid);
            //   MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM inventoryslot WHERE characterid = ?", cid);
            MapleCharacter.deleteWhereCharacterId(con, "DELETE FROM extendedslots WHERE characterid = ?", cid);
            return 0;
        } catch (Exception e) {
            FileoutputUtil.outputFileError(FileoutputUtil.PacketEx_Log, e);
        }
        return 10;
    }

    public final byte getGender() {
        return gender;
    }

    public final void setGender(final byte gender) {
        this.gender = gender;
    }

    public final String getSecondPassword() {
        return "00000000"; // fake pic 4 dayz
    }

    public final void setSecondPassword(final String secondPassword) {
        this.secondPassword = secondPassword;
    }

    public final String getAccountName() {
        return accountName;
    }

    public final void setAccountName(final String accountName) {
        this.accountName = accountName;
    }

    public final int getWorld() {
        return world;
    }

    public final void setWorld(final int world) {
        this.world = world;
    }

    public MapleClient getClinetS() {
        return ClinetS;
    }

    public void setClinetS(MapleClient clinetS) {
        ClinetS = clinetS;
    }

    public final int getLatency() {
        return (int) (lastPong - lastPing);
    }

    public final long getLastPong() {
        return lastPong;
    }

    public final long getLastPing() {
        return lastPing;
    }

    public final void pongReceived() {
        lastPong = System.currentTimeMillis();
    }

    public final void sendPing() {
        lastPing = System.currentTimeMillis();
        session.write(LoginPacket.getPing());

        PingTimer.getInstance().schedule(() -> {
            try {
                if (getLatency() < 0) {
                    disconnect(true, false);
                    if (getSession().isActive()) {
                        getSession().close();
                    }
                }
            } catch (final NullPointerException e) {
                // client already gone
            }
        }, 60000); // note: idletime gets added to this too
    }

    public Set<String> getMacs() {
        return Collections.unmodifiableSet(macs);
    }

    public boolean isIntern() {
        return gmLevel >= PlayerGMRank.INTERN.getLevel();
    }

    public boolean isGM() {
        return gmLevel >= PlayerGMRank.GM.getLevel();
    }

    public boolean isSuperGM() {
        return gmLevel >= PlayerGMRank.SUPER_GM.getLevel();
    }

    public boolean isAdmin() {
        return gmLevel >= PlayerGMRank.ADMIN.getLevel();
    }

    public boolean isClientServer() {
        return isClientServer;
    }

    public void setClientServer(boolean clientServer) {
        isClientServer = clientServer;
    }

    public int getGmLevel() {
        return gmLevel;
    }

    public final void setGmLevel(PlayerGMRank rank) {
        this.gmLevel = rank.getLevel();
    }


    public final void setScriptEngine(final String name, final ScriptEngine e) {
        engines.put(name, e);
    }

    public final ScriptEngine getScriptEngine(final String name) {
        return engines.get(name);
    }

    public final void removeScriptEngine(final String name) {
        engines.remove(name);
    }

    public final ScheduledFuture<?> getIdleTask() {
        return idleTask;
    }

    public final void setIdleTask(final ScheduledFuture<?> idleTask) {
        this.idleTask = idleTask;
    }

    public boolean isMonitored() {
        return monitored;
    }

    public void setMonitored(boolean m) {
        this.monitored = m;
    }

    public boolean hasAcceptedToS() {
        return tos;
    }

    public void acceptedToS() {
        tos = true;
    }

    public int getCharacterSlots() {
        if (isGM()) {
            return 15;
        }
        if (charslots != DEFAULT_CHAR_SLOT) {
            return charslots; //save a sql
        }
        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM character_slots WHERE accountid = ? AND worldid = ?")) {
                ps.setInt(1, accountId);
                ps.setInt(2, world);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        charslots = rs.getInt("charslots");
                    } else {
                        try (PreparedStatement psu = con.prepareStatement("INSERT INTO character_slots (accountid, worldid, charslots) VALUES (?, ?, ?)")) {
                            psu.setInt(1, accountId);
                            psu.setInt(2, world);
                            psu.setInt(3, charslots);
                            psu.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException ignored) {
        }
        return charslots;
    }

    public boolean gainCharacterSlot() {
        if (getCharacterSlots() >= 15) {
            return false;
        }
        charslots++;

        try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE character_slots SET charslots = ? WHERE worldid = ? AND accountid = ?")) {
                ps.setInt(1, charslots);
                ps.setInt(2, world);
                ps.setInt(3, accountId);
                ps.executeUpdate();
            }
        } catch (SQLException sqlE) {
            return false;
        }
        return true;
    }

    public final void updateGender() {

        final Connection con = DatabaseConnection.getConnection();

        try (PreparedStatement ps = con.prepareStatement("UPDATE `accounts` SET `gender` = ? WHERE id = ?")) {
            ps.setInt(1, gender);
            ps.setInt(2, accountId);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("error updating gender" + e);
        }
    }

    public final void update2ndPassword() {

        try {
            MessageDigest digester = MessageDigest.getInstance("SHA-1");
            digester.update(secondPassword.getBytes("UTF-8"), 0, secondPassword.length());
            String hash = HexTool.toString(digester.digest()).replace(" ", "").toLowerCase();
            final Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE `accounts` SET `2ndpassword` = ? WHERE id = ?")) {
                ps.setString(1, hash);
                ps.setInt(2, accountId);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ignored) {
        }
    }

    public boolean isReceiving() {
        return receiving;
    }

    public void setReceiving(boolean m) {
        this.receiving = m;
    }

    public boolean canClickNPC() {
        return lastNpcClick + 500 < System.currentTimeMillis();
    }

    public void setClickedNPC() {
        lastNpcClick = System.currentTimeMillis();
    }

    public void removeClickedNPC() {
        lastNpcClick = 0;
    }

    public final Timestamp getCreated() { // TODO hide?
        Connection con = DatabaseConnection.getConnection();
        try {
            PreparedStatement ps;
            ps = con.prepareStatement("SELECT createdat FROM accounts WHERE id = ?");
            ps.setInt(1, getAccID());
            Timestamp ret;
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    rs.close();
                    ps.close();
                    return null;
                }
                ret = rs.getTimestamp("createdat");
            }
            ps.close();
            return ret;
        } catch (SQLException e) {
            throw new DatabaseException("error getting create", e);
        }
    }

    public String getTempIP() {
        return tempIP;
    }

    public void setTempIP(String s) {
        this.tempIP = s;
    }

    public boolean isLocalhost() {
        return ServerConstants.ONLY_LOCALHOST;
    }

    protected static final class CharNameAndId {

        public final String name;
        public final int id;

        public CharNameAndId(final String name, final int id) {
            super();
            this.name = name;
            this.id = id;
        }
    }
}
