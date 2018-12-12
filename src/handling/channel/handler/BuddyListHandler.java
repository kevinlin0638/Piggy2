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

import client.*;
import client.buddy.BuddyList;
import client.buddy.BuddyList.BuddyAddResult;
import client.buddy.BuddyList.BuddyOperation;
import client.buddy.BuddyListEntry;
import client.buddy.CharacterNameAndId;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import handling.world.World;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext.BuddylistPacket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static client.buddy.BuddyList.BuddyOperation.ADDED;
import static client.buddy.BuddyList.BuddyOperation.DELETED;

public class BuddyListHandler {

    private static CharacterIdNameBuddyCapacity getCharacterIdAndNameFromDatabase(final String name, final String group) throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        CharacterIdNameBuddyCapacity ret;
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM characters WHERE name LIKE ?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                ret = null;
                if (rs.next()) {
                    ret = new CharacterIdNameBuddyCapacity(rs.getInt("id"), rs.getString("name"), group, rs.getInt("buddyCapacity"));
                }
            }
        }

        return ret;
    }

    public static void BuddyOperation(final LittleEndianAccessor slea, final MapleClient c) {
        final int mode = slea.readByte();
        final BuddyList buddylist = c.getPlayer().getBuddylist();

        if (mode == 1) { // add
            final String addName = slea.readMapleAsciiString();
            final String groupName = slea.readMapleAsciiString();
            final BuddyListEntry ble = buddylist.get(addName);

            if (addName.length() > 13 || groupName.length() > 16) {
                return;
            }
            if (ble != null && (ble.getGroup().equals(groupName) || !ble.isVisible())) {
                c.sendPacket(BuddylistPacket.buddylistMessage((byte) 11));
            } else if (ble != null && ble.isVisible()) {
                ble.setGroup(groupName);
                c.sendPacket(BuddylistPacket.updateBuddylist(buddylist.getBuddies(), 10));
            } else if (buddylist.isFull()) {
                c.sendPacket(BuddylistPacket.buddylistMessage((byte) 11));
            } else {
                try {
                    CharacterIdNameBuddyCapacity charWithId;
                    int channel = World.Find.findChannel(addName);
                    int world = World.Find.findWorld(addName);
                    MapleCharacter otherChar;
                    if (channel > 0) {
                        otherChar = ChannelServer.getInstance(world, channel).getPlayerStorage().getCharacterByName(addName);
                        if (otherChar == null) {
                            charWithId = getCharacterIdAndNameFromDatabase(addName, groupName);
                        } else {
                            charWithId = new CharacterIdNameBuddyCapacity(otherChar.getId(), otherChar.getName(), groupName, otherChar.getBuddylist().getCapacity());
                        }
                    } else {
                        charWithId = getCharacterIdAndNameFromDatabase(addName, groupName);
                    }

                    if (charWithId != null) {
                        BuddyAddResult buddyAddResult = null;
                        if (channel > 0) {
                            buddyAddResult = World.Buddy.requestBuddyAdd(addName, c.getChannel(), c.getPlayer().getId(), c.getPlayer().getName(), c.getPlayer().getLevel(), c.getPlayer().getJob());
                        } else {
                            Connection con = DatabaseConnection.getConnection();
                            PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) as buddyCount FROM buddies WHERE characterid = ? AND pending = 0");
                            ps.setInt(1, charWithId.getId());
                            ResultSet rs = ps.executeQuery();

                            if (!rs.next()) {
                                ps.close();
                                rs.close();
                                throw new RuntimeException("Result set expected");
                            } else {
                                int count = rs.getInt("buddyCount");
                                if (count >= charWithId.getBuddyCapacity()) {
                                    buddyAddResult = BuddyAddResult.BUDDYLIST_FULL;
                                }
                            }
                            rs.close();
                            ps.close();

                            ps = con.prepareStatement("SELECT pending FROM buddies WHERE characterid = ? AND buddyid = ?");
                            ps.setInt(1, charWithId.getId());
                            ps.setInt(2, c.getPlayer().getId());
                            rs = ps.executeQuery();
                            if (rs.next()) {
                                buddyAddResult = BuddyAddResult.ALREADY_ON_LIST;
                            }
                            rs.close();
                            ps.close();
                        }
                        if (buddyAddResult == BuddyAddResult.BUDDYLIST_FULL) {
                            c.sendPacket(BuddylistPacket.buddylistMessage((byte) 12));
                        } else {
                            int displayChannel = -1;
                            int otherCid = charWithId.getId();
                            if (buddyAddResult == BuddyAddResult.ALREADY_ON_LIST && channel > 0) {
                                displayChannel = channel;
                                notifyRemoteChannel(c, channel, otherCid, groupName, ADDED);
                            } else if (buddyAddResult != BuddyAddResult.ALREADY_ON_LIST) {
                                Connection con = DatabaseConnection.getConnection();
                                try (PreparedStatement ps = con.prepareStatement("INSERT INTO buddies (`characterid`, `buddyid`, `groupname`, `pending`) VALUES (?, ?, ?, 1)")) {
                                    ps.setInt(1, charWithId.getId());
                                    ps.setInt(2, c.getPlayer().getId());
                                    ps.setString(3, groupName);
                                    ps.executeUpdate();
                                }
                            }
                            buddylist.put(new BuddyListEntry(charWithId.getName(), otherCid, groupName, displayChannel, true));
                            c.sendPacket(BuddylistPacket.updateBuddylist(buddylist.getBuddies(), 10));
                        }
                    } else {
                        c.sendPacket(BuddylistPacket.buddylistMessage((byte) 15));
                    }
                } catch (SQLException e) {
                    System.err.println("SQL THROW" + e);
                }
            }
        } else if (mode == 2) { // accept buddy
            int otherCid = slea.readInt();
            final BuddyListEntry ble = buddylist.get(otherCid);
            if (!buddylist.isFull() && ble != null && !ble.isVisible()) {
                final int channel = World.Find.findChannel(otherCid);
                buddylist.put(new BuddyListEntry(ble.getName(), otherCid, "ETC", channel, true));
                c.sendPacket(BuddylistPacket.updateBuddylist(buddylist.getBuddies(), 10));
                notifyRemoteChannel(c, channel, otherCid, "ETC", ADDED);
            } else {
                c.sendPacket(BuddylistPacket.buddylistMessage((byte) 11));
            }
        } else if (mode == 3) { // delete
            final int otherCid = slea.readInt();
            final BuddyListEntry blz = buddylist.get(otherCid);
            if (blz != null && blz.isVisible()) {
                notifyRemoteChannel(c, World.Find.findChannel(otherCid), otherCid, blz.getGroup(), DELETED);
            }
            buddylist.remove(otherCid);
            c.sendPacket(BuddylistPacket.updateBuddylist(buddylist.getBuddies(), 18));
        }
    }

    private static void notifyRemoteChannel(final MapleClient c, final int remoteChannel, final int otherCid, final String group, final BuddyOperation operation) {
        final MapleCharacter player = c.getPlayer();

        if (remoteChannel > 0) {
            World.Buddy.buddyChanged(otherCid, player.getId(), player.getName(), c.getChannel(), operation, group);
        }
    }

    private static final class CharacterIdNameBuddyCapacity extends CharacterNameAndId {

        private int buddyCapacity;

        public CharacterIdNameBuddyCapacity(int id, String name, String group, int buddyCapacity) {
            super(id, name, group);
            this.buddyCapacity = buddyCapacity;
        }

        public int getBuddyCapacity() {
            return buddyCapacity;
        }
    }
}
