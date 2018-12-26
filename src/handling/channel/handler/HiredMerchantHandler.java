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
import client.inventory.Item;
import client.inventory.ItemLoader;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ServerConstants;
import database.DatabaseConnection;
import handling.world.World;
import server.MapleInventoryManipulator;
import server.MerchItemPackage;
import tools.StringUtil;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;
import tools.packet.PlayerShopPacket;
import tools.types.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HiredMerchantHandler {

    public static boolean UseHiredMerchant(final MapleClient c, final boolean packet) {
        if (c.getPlayer().getMap() != null && c.getPlayer().getMap().allowPersonalShop()) {
            final byte state = checkExistance(c.getPlayer().getAccountID(), c.getPlayer().getId());

            switch (state) {
                case 1:
                    c.getPlayer().dropMessage(1, "請先去找富蘭德里領取道具.");
                    break;
                case 0:
                    boolean merch = World.hasMerchant(c.getPlayer().getAccountID(), c.getPlayer().getId());
                    if (!merch) {
                        if (c.getChannelServer().isShutdown()) {
                            c.getPlayer().dropMessage(1, "伺服器將要關閉.");
                            return false;
                        }
                        if (packet) {
                            c.sendPacket(PlayerShopPacket.sendTitleBox());
                        }
                        return true;
                    } else {
                        c.getPlayer().dropMessage(1, "請先關閉開張中的商店,重新再試.");
                    }
                    break;
                default:
                    c.getPlayer().dropMessage(1, "發生未知的錯誤.");
                    break;
            }
        } else {
            c.getSession().close();
        }
        return false;
    }

    private static byte checkExistance(final int accid, final int cid) {
        Connection con = DatabaseConnection.getConnection();
        try {
            try (PreparedStatement ps = con.prepareStatement("SELECT * from hiredmerch where accountid = ? OR characterid = ?")) {
                ps.setInt(1, accid);
                ps.setInt(2, cid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ps.close();
                        rs.close();
                        return 1;
                    }
                }
            }
            return 0;
        } catch (SQLException se) {
            return -1;
        }
    }

   /* public static void displayMerch(MapleClient c) {
        final int conv = c.getPlayer().getConversation();
        boolean merch = World.hasMerchant(c.getPlayer().getAccountID(), c.getPlayer().getWorldId());
        if (merch) {
            c.getPlayer().dropMessage(1, "Please close the existing store and try again.");
            c.getPlayer().setConversation(0);
        } else if (c.getChannelServer().isShutdown()) {
            c.getPlayer().dropMessage(1, "The world is going to shut down.");
            c.getPlayer().setConversation(0);
        } else if (conv == 3) { // Hired Merch
            final MerchItemPackage pack = loadItemFrom_Database(c.getPlayer().getAccountID());
            if (pack == null) {
                c.getPlayer().dropMessage(1, "You do not have any item(s) with Fredrick.");
                c.getPlayer().setConversation(0);
            } else if (pack.getItems().size() <= 0) { //error fix for complainers.
                if (!check(c.getPlayer(), pack)) {
                    c.sendPacket(PlayerShopPacket.merchItem_Message((byte) 33));
                    c.getPlayer().getClient().sendPacket(CWvsContext.enableActions());
                    return;
                }
                if (deletePackage(c.getPlayer().getAccountID(), pack.getPackageId(), c.getPlayer().getWorldId())) {
                    c.getPlayer().gainMeso(pack.getMeso(), false);
                    c.sendPacket(PlayerShopPacket.merchItem_Message((byte) 32));
                } else {
                    c.getPlayer().dropMessage(1, "An unknown error occured.");
                }
                c.getPlayer().setConversation(0);
            } else {
                c.sendPacket(PlayerShopPacket.merchItemStore_ItemData(pack));
            }
        }
    }*/


    public static void displayMerch(MapleClient c) {
        final int conv = c.getPlayer().getConversation();
        boolean merch = World.hasMerchant(c.getPlayer().getAccountID(), c.getPlayer().getId());
        if (merch) {
            c.getPlayer().dropMessage(1, "請關閉已開張的商店並重新嘗試.");
            c.getPlayer().setConversation(0);
        } else if (c.getChannelServer().isShutdown()) {
            c.getPlayer().dropMessage(1, "伺服器將要關閉.");
            c.getPlayer().setConversation(0);
        } else if (conv == 3) { // Hired Merch
            final MerchItemPackage pack = loadItemFrom_Database(c.getPlayer().getAccountID());

            if (pack == null) {
                c.getPlayer().dropMessage(1, "您沒有任何道具在我這邊保管.");
                c.getPlayer().setConversation(0);
            } else if (pack.getItems().size() <= 0) { //error fix for complainers.
                if (!check(c.getPlayer(), pack)) {
                    c.sendPacket(PlayerShopPacket.merchItem_Message((byte) 33));
                    c.getPlayer().getClient().sendPacket(CWvsContext.enableActions());
                    return;
                }
                if (deletePackage(c.getPlayer().getAccountID(), pack.getPackageId(), c.getPlayer().getId())) {
                    if (ServerConstants.MerchantsUseCurrency) {
                        c.getPlayer().gainCurrency(pack.getMeso(), false);
                    } else {
                        c.getPlayer().gainMeso(pack.getMeso(), false);
                    }
                    c.sendPacket(PlayerShopPacket.merchItem_Message((byte) 32));
                    c.getPlayer().dropMessage(1, "您獲得您的 " + (ServerConstants.MerchantsUseCurrency ? "楓幣" : "楓幣") + ".");
                } else {
                    c.getPlayer().dropMessage(1, "未知的錯誤.");
                }
                c.getPlayer().setConversation(0);
            } else {
                c.sendPacket(PlayerShopPacket.merchItemStore_ItemData(pack));
                MapleInventoryManipulator.checkSpace(c, conv, conv, null);
                for (final Item item : pack.getItems()) {
                    if (c.getPlayer().getInventory(GameConstants.getInventoryType(item.getItemId())).isFull()) {
                        c.getPlayer().dropMessage(1, "您的道具欄已滿, 請確認是否有足夠的空間.");
                        c.getPlayer().setConversation(0);
                        break;
                    }
                    MapleInventoryManipulator.addFromDrop(c, item, true);
                    deletePackage(c.getPlayer().getAccountID(), pack.getPackageId(), c.getPlayer().getId());
                    c.getPlayer().setConversation(0);
                }
            }
        }
        c.sendPacket(CWvsContext.enableActions());
    }


    public static void MerchantItemStore(final LittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer() == null) {
            return;
        }
        final byte operation = slea.readByte();
        if (operation == 27 || operation == 28) { // Request, Take out
            requestItems(c, operation == 27);
        } else if (operation == 30) { // Exit
            c.getPlayer().setConversation(0);
        }
    }

    private static void requestItems(final MapleClient c, final boolean request) {
        if (c.getPlayer().getConversation() != 3) {
            return;
        }
        boolean merch = World.hasMerchant(c.getPlayer().getAccountID(), c.getPlayer().getId());
        if (merch) {
            c.getPlayer().dropMessage(1, "請先關閉已開張的商店.");
            c.getPlayer().setConversation(0);
            return;
        }
        final MerchItemPackage pack = loadItemFrom_Database(c.getPlayer().getAccountID());
        if (pack == null) {
            c.getPlayer().dropMessage(1, "未知的錯誤.");
            return;
        } else if (c.getChannelServer().isShutdown()) {
            c.getPlayer().dropMessage(1, "伺服器即將關閉.");
            c.getPlayer().setConversation(0);
            return;
        }
        final int days = StringUtil.getDaysAmount(pack.getSavedTime(), System.currentTimeMillis()); // max 100%
        final double percentage = days / 100.0;
        final int fee = (int) Math.ceil(percentage * pack.getMeso()); // if no mesos = no tax
        if (request && days > 0 && percentage > 0 && pack.getMeso() > 0 && fee > 0) {
            c.sendPacket(PlayerShopPacket.merchItemStore((byte) 38, days, fee));
            return;
        }
        if (fee < 0) { // impossible
            c.sendPacket(PlayerShopPacket.merchItem_Message(33));
            return;
        }
        if (c.getPlayer().getMeso() < fee) {
            c.sendPacket(PlayerShopPacket.merchItem_Message(35));
            return;
        }
        if (!check(c.getPlayer(), pack)) {
            c.sendPacket(PlayerShopPacket.merchItem_Message(36));
            return;
        }
        if (deletePackage(c.getPlayer().getAccountID(), pack.getPackageId(), c.getPlayer().getId())) {
            if (fee > 0) {
                c.getPlayer().gainMeso(-fee, true);
            }
            c.getPlayer().gainMeso(pack.getMeso(), false);
            for (Item item : pack.getItems()) {
                MapleInventoryManipulator.addFromDrop(c, item, false);
            }
            c.sendPacket(PlayerShopPacket.merchItem_Message(32));
        } else {
            c.getPlayer().dropMessage(1, "未知的錯誤.");
        }
    }

    private static boolean check(final MapleCharacter chr, final MerchItemPackage pack) {
        if (chr.getMeso() + pack.getMeso() < 0) {
            return false;
        }
        byte eq = 0, use = 0, setup = 0, etc = 0, cash = 0;
        for (Item item : pack.getItems()) {
            final MapleInventoryType invtype = GameConstants.getInventoryType(item.getItemId());
            if (invtype == MapleInventoryType.EQUIP) {
                eq++;
            } else if (invtype == MapleInventoryType.USE) {
                use++;
            } else if (invtype == MapleInventoryType.SETUP) {
                setup++;
            } else if (invtype == MapleInventoryType.ETC) {
                etc++;
            } else if (invtype == MapleInventoryType.CASH) {
                cash++;
            }
//            if (MapleItemInformationProvider.getInstance().isPickupRestricted(item.getItemId()) && chr.haveItem(item.getItemId(), 1)) {
            //              return false;
            //        }
        }
        if (chr.getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < eq || chr.getInventory(MapleInventoryType.USE).getNumFreeSlot() < use || chr.getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < setup || chr.getInventory(MapleInventoryType.ETC).getNumFreeSlot() < etc || chr.getInventory(MapleInventoryType.CASH).getNumFreeSlot() < cash) {
            return false;
        }
        return true;
    }

    private static boolean deletePackage(final int accid, final int packageid, final int chrId) {
        final Connection con = DatabaseConnection.getConnection();

        try {
            try (PreparedStatement ps = con.prepareStatement("DELETE from hiredmerch where accountid = ? OR packageid = ? OR characterid = ?")) {
                ps.setInt(1, accid);
                ps.setInt(2, packageid);
                ps.setInt(3, chrId);
                ps.executeUpdate();
            }
            ItemLoader.HIRED_MERCHANT.saveItems(null, packageid);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    private static MerchItemPackage loadItemFrom_Database(final int accountid) {
        final Connection con = DatabaseConnection.getConnection();

        try {
            ResultSet rs;
            final int packageid;
            final MerchItemPackage pack;
            try (PreparedStatement ps = con.prepareStatement("SELECT * from hiredmerch where accountid = ?")) {
                ps.setInt(1, accountid);
                rs = ps.executeQuery();
                if (!rs.next()) {
                    ps.close();
                    rs.close();
                    return null;
                }
                packageid = rs.getInt("PackageId");
                pack = new MerchItemPackage();
                pack.setPackageId(packageid);
                pack.setMeso(rs.getInt("Mesos"));
                pack.setSavedTime(rs.getLong("time"));
            }
            rs.close();

            Map<Long, Pair<Item, MapleInventoryType>> items = ItemLoader.HIRED_MERCHANT.loadItems(false, packageid);
            if (items != null) {
                List<Item> iters = new ArrayList<>();
                for (Pair<Item, MapleInventoryType> z : items.values()) {
                    iters.add(z.left);
                }
                pack.setItems(iters);
            }


            return pack;
        } catch (SQLException e) {
            return null;
        }
    }
}
