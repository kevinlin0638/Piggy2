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
import client.inventory.ItemFlag;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.messages.CommandProcessor;
import constants.GameConstants;
import constants.ServerConstants;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleTrade;
import server.Randomizer;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.shops.*;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;
import tools.packet.PlayerShopPacket;

import java.util.Arrays;

public class PlayerInteractionHandler {

    public static int generatePieceType(MapleCharacter chr) {
        // to randomly generate the type of matchcard because i don't think it reads the byte of the matchcard layout anymore
        int type = Randomizer.rand(0, 2); //0=4x3,1=5x4,2=6x5
        if (type != chr.getMatchCardVal()) {
            chr.setMatchCardVal(type); // update it for the character each time.. (yes i know this is sloppy) :/
            return type;
        }
        return 2;
    }

    public static void PlayerInteraction(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        // System.out.println("player interaction.." + slea.toString());
        byte a = slea.readByte();
        final Interaction action = Interaction.getByAction(a);
        if (chr == null || action == null) {
            if(ServerConstants.DEBUG)
                System.out.println("null interaction: " + a);
            return;
        }
        c.getPlayer().setScrolledPosition((short) 0);
//        System.out.println("action: " + a);
        switch (action) {
            case CREATE: {
                if (chr.getPlayerShop() != null || c.getChannelServer().isShutdown() || chr.hasBlockedInventory()) {
                    c.sendPacket(CWvsContext.enableActions());
                    return;
                }
                final byte createType = slea.readByte();
                if ((!chr.getMap().getMapObjectsInRange(chr.getTruePosition(), 20000, Arrays.asList(MapleMapObjectType.SHOP, MapleMapObjectType.HIRED_MERCHANT)).isEmpty() || !chr.getMap().getPortalsInRange(chr.getTruePosition(), 20000).isEmpty()) && createType != 3) {
                    chr.dropMessage(1, "You may not establish a " + (createType == 1 || createType == 2 ? "minigame" : "store") + " here.");
                    c.sendPacket(CWvsContext.enableActions());
                    return;
                }
                if (createType == 3) {
                    MapleTrade.startTrade(chr);
                } else if (createType == 1 || createType == 2) { // omok and matchcards
                    final String desc = slea.readMapleAsciiString();
                    String pass = "";
                    if (slea.readByte() > 0) {
                        pass = slea.readMapleAsciiString();
                    }
                    byte slot = slea.readByte();
                    MapleInventory etc = c.getPlayer().getInventory(MapleInventoryType.ETC);
                    Item item = etc.getItem(slot);
                    int piece = createType == 1 ? (item.getItemId() == 4080010 ? 10 : item.getItemId() == 4080011 ? 11 : item.getItemId() % 10) : generatePieceType(chr);
                    final int itemId = item.getItemId();

                    MapleMiniGame game = new MapleMiniGame(chr, itemId, desc, pass, createType); //itemid
                    game.setPieceType(piece);
                    chr.setPlayerShop(game);
                    game.setAvailable(true);
                    game.setOpen(true);
                    game.send(c);
                    chr.getMap().addMapObject(game);
                    game.update();
                } else if (createType == 4 || createType == 5) {
                    // [06] // create
                    // [04] // createType
                    // [04 00] // amount of characters in "test" (ascii) and byte space
                    // [74 65 73 74 00] "test" in ascii and byte space
                    // [10 00 20] // ??
                    // [6E 4E 00] // ??
                    final String desc = slea.readMapleAsciiString();
                    if (slea.readByte() > 0) {
                        String pass = slea.readMapleAsciiString();
                    }
                    if (chr.getMap().allowPersonalShop()) {
                        Item shop = c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) slea.readShort());
                        if (shop == null || shop.getQuantity() <= 0 || shop.getItemId() != slea.readInt() || c.getPlayer().getMapId() < 910000001 || c.getPlayer().getMapId() > 910000022) {
                            return;
                        }
                        if (createType == 4) {
                            MaplePlayerShop mps = new MaplePlayerShop(chr, shop.getItemId(), desc);
                            chr.setPlayerShop(mps);
                            chr.getMap().addMapObject(mps);
                            c.sendPacket(PlayerShopPacket.getPlayerStore(chr, true));
                            c.sendPacket(PlayerShopPacket.shopVisitorLeave((byte) 1));
                        } else if (HiredMerchantHandler.UseHiredMerchant(chr.getClient(), false)) {
                            final HiredMerchant merch = new HiredMerchant(chr, shop.getItemId(), desc);
                            chr.setPlayerShop(merch);
                            chr.getMap().addMapObject(merch);
                            c.sendPacket(PlayerShopPacket.getHiredMerch(chr, merch, true));
                            c.getPlayer().dropMessage(-2, "注意 : 請先開啟商店後再雙擊新增點數裝備.");
                            c.getPlayer().dropMessage(-2, "幫助 : 使用方式 @pmerch 欄位 位置 組數 每組數量 價格 以下詳細說明.");
                            c.getPlayer().dropMessage(-2, "幫助 : 欄位 : 1-裝備欄 2-消耗欄 3-裝飾欄 4-其他欄 5-特殊欄");
                            c.getPlayer().dropMessage(-2, "幫助 : 位置 : 1~96");
                            c.getPlayer().dropMessage(-2, "幫助 : 組數 : 想要分成幾組販售");
                            c.getPlayer().dropMessage(-2, "幫助 : 每組數量 : 每一組中道具的數量");
                            c.getPlayer().dropMessage(-2, "幫助 : 價格 : 每一組的價格");
                        }
                    }
                }
                break;
            }
            case INVITE_TRADE: {
                if (chr.getMap() == null) {
                    return;
                }
                MapleCharacter chrr = chr.getMap().getCharacterById(slea.readInt());
                if (chrr == null || c.getChannelServer().isShutdown() || chrr.hasBlockedInventory()) {
                    c.sendPacket(CWvsContext.enableActions());
                    return;
                }
                MapleTrade.inviteTrade(chr, chrr);
                break;
            }
            case DENY_TRADE: {
                MapleTrade.declineTrade(chr);
                break;
            }
            case VISIT: { // re-code
                if (c.getChannelServer().isShutdown()) {
                    c.sendPacket(CWvsContext.enableActions());
                    return;
                }
                if (chr.getTrade() != null && chr.getTrade().getPartner() != null && !chr.getTrade().inTrade()) {
                    MapleTrade.visitTrade(chr, chr.getTrade().getPartner().getChr());
                } else if (chr.getMap() != null && chr.getTrade() == null) {
                    final int obid = slea.readInt();
                    MapleMapObject ob = chr.getMap().getMapObject(obid, MapleMapObjectType.HIRED_MERCHANT);
                    if (ob == null) {
                        ob = chr.getMap().getMapObject(obid, MapleMapObjectType.SHOP);
                    }

                    if (ob instanceof IMaplePlayerShop && chr.getPlayerShop() == null) {
                        final IMaplePlayerShop ips = (IMaplePlayerShop) ob;

                        if (ob instanceof HiredMerchant) {
                            final HiredMerchant merchant = (HiredMerchant) ips;
                            /*if (merchant.isOwner(chr) && merchant.isOpen() && merchant.isAvailable()) {
                                merchant.setOpen(false);
                                merchant.removeAllVisitors((byte) 16, (byte) 0);
                                chr.setPlayerShop(ips);
                                c.sendPacket(PlayerShopPacket.getHiredMerch(chr, merchant, false));
                            } else {*/
                            if (!merchant.isOpen() || !merchant.isAvailable()) {
                                chr.dropMessage(1, "店長正在維護商店中,請稍後再試.");
                            } else {
                                if (ips.getFreeSlot() == -1) {
                                    chr.dropMessage(1, "此商店的客人已滿, 請稍後再試.");
                                } else if (merchant.isInBlackList(chr.getName())) {
                                    chr.dropMessage(1, "您已被賣家加入黑名單.");
                                } else {
                                    if(merchant.getMCOwner() == chr) {
                                        merchant.setOpen(false);
                                    }
                                    chr.setPlayerShop(ips);
                                    merchant.addVisitor(chr);
                                    c.sendPacket(PlayerShopPacket.getHiredMerch(chr, merchant, false));
                                    if(merchant.getMCOwner() == chr) {
                                        chr.dropMessage(-2, "注意 : 請先開啟商店後再雙擊新增點數裝備.");
                                        chr.dropMessage(-2, "幫助 : 使用方式 @pmerch 欄位 位置 組數 每組數量 價格 以下詳細說明.");
                                        chr.dropMessage(-2, "幫助 : 欄位 : 1-裝備欄 2-消耗欄 3-裝飾欄 4-其他欄 5-特殊欄");
                                        chr.dropMessage(-2, "幫助 : 位置 : 1~96");
                                        chr.dropMessage(-2, "幫助 : 組數 : 想要分成幾組販售");
                                        chr.dropMessage(-2, "幫助 : 每組數量 : 每一組中道具的數量");
                                        chr.dropMessage(-2, "幫助 : 價格 : 每一組的價格");
                                    }
                                    merchant.SendMsg(c);
                                }
                            }
                            //}
                        } else {
                            if (ips instanceof MaplePlayerShop && ((MaplePlayerShop) ips).isBanned(chr.getName())) {
                                chr.dropMessage(1, "您已被賣家加入黑名單.");
                            } else {
                                if (ips.getFreeSlot() < 0 || ips.getVisitorSlot(chr) > -1 || !ips.isOpen() || !ips.isAvailable()) {
                                    c.sendPacket(PlayerShopPacket.getMiniGameFull());
                                } else {
                                    if (slea.available() > 0 && slea.readByte() > 0) { //a password has been entered
                                        String pass = slea.readMapleAsciiString();
                                        if (!pass.equals(ips.getPassword())) {
                                            c.getPlayer().dropMessage(1, "您輸入的密碼錯誤.");
                                            return;
                                        }
                                    } else if (ips.getPassword().length() > 0) {
                                        c.getPlayer().dropMessage(1, "您輸入的密碼錯誤.");
                                        return;
                                    }
                                    ips.addVisitor(chr);
                                    chr.setPlayerShop(ips);
                                    if (ips instanceof MapleMiniGame) {
                                        ((MapleMiniGame) ips).send(c);
                                    } else {
                                        c.sendPacket(PlayerShopPacket.getPlayerStore(chr, false));
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            }
            case HIRED_MERCHANT_MAINTENANCE: {
                if (c.getChannelServer().isShutdown() || chr.getMap() == null || chr.getTrade() != null) {
                    c.sendPacket(CWvsContext.enableActions());
                    return;
                }
                slea.skip(1); // 9?
                byte type = slea.readByte(); // 5?
                if (type != 5) {
                    c.sendPacket(CWvsContext.enableActions());
                    return;
                }
                final String password = slea.readMapleAsciiString();
                //if (!c.CheckSecondPassword(password) || password.length() < 6 || password.length() > 16) {
                //	chr.dropMessage(5, "Please enter a valid PIC.");
                //	c.sendPacket(CWvsContext.enableActions());
                //	return;
                //}
                final int obid = slea.readInt();
                MapleMapObject ob = chr.getMap().getMapObject(obid, MapleMapObjectType.HIRED_MERCHANT);
                if (ob == null || chr.getPlayerShop() != null) {
                    c.sendPacket(CWvsContext.enableActions());
                    return;
                }
                if (ob instanceof IMaplePlayerShop && ob instanceof HiredMerchant) {
                    final IMaplePlayerShop ips = (IMaplePlayerShop) ob;
                    final HiredMerchant merchant = (HiredMerchant) ips;
                    if (merchant.isOwner(chr) && merchant.isOpen() && merchant.isAvailable()) {
                        merchant.setOpen(false);
                        merchant.removeAllVisitors((byte) 16, (byte) 0);
                        chr.setPlayerShop(ips);
                        c.sendPacket(PlayerShopPacket.getHiredMerch(chr, merchant, false));
                    } else {
                        c.sendPacket(CWvsContext.enableActions());
                    }
                }
                break;
            }
            case CHAT: {
                slea.readInt();
                String message = slea.readMapleAsciiString();
                if (chr.getTrade() != null) {
                    chr.getTrade().chat(message);
                } else if (chr.getPlayerShop() != null) {
                    final IMaplePlayerShop ips = chr.getPlayerShop();

                    if (!CommandProcessor.processCommand(chr.getClient(), message, ServerConstants.CommandType.MERCH)) {
                        //chr.dropMessage(-2, chr.getName() + " : " + message);
                        ips.broadcastToVisitors(PlayerShopPacket.shopChat(chr.getName() + " : " + message, ips.getVisitorSlot(chr)));
                        if(ips.getShopType() == 1 ){ // Hired Merchant
                            ((HiredMerchant) ips).addMsg(chr.getName() + " : " + message , ips.getVisitorSlot(chr) );
                        }
                    }
                }
                break;
            }
            case EXIT: {
                if (chr.getTrade() != null) {
                    MapleTrade.cancelTrade(chr.getTrade(), chr.getClient(), chr);
                } else {
                    final IMaplePlayerShop ips = chr.getPlayerShop();
                    if (ips == null) { //should be null anyway for owners of hired merchants (maintenance_off)
                        return;
                    }
                    if (ips.isOwner(chr) && ips.getShopType() != 1) {
                        ips.closeShop(false, ips.isAvailable()); //how to return the items?
                    } else {
                        ips.removeVisitor(chr);
                    }
                    chr.setPlayerShop(null);
                }
                break;
            }
            case OPEN: {
                final IMaplePlayerShop shop = chr.getPlayerShop();
                if (shop != null && shop.isOwner(chr) && shop.getShopType() < 3 && !shop.isAvailable()) {
                    if (chr.getMap().allowPersonalShop()) {
                        if (c.getChannelServer().isShutdown()) {
                            chr.dropMessage(1, "伺服器即將關閉.");
                            c.sendPacket(CWvsContext.enableActions());
                            shop.closeShop(shop.getShopType() == 1, false);
                            return;
                        }
                        if (shop.getShopType() == 1 && HiredMerchantHandler.UseHiredMerchant(chr.getClient(), false)) {
                            final HiredMerchant merchant = (HiredMerchant) shop;
                            merchant.setStoreid(c.getChannelServer().addMerchant(merchant));
                            merchant.setOpen(true);
                            merchant.setAvailable(true);
                            chr.getMap().broadcastMessage(PlayerShopPacket.spawnHiredMerchant(merchant));
                            chr.setPlayerShop(null);
                            if(chr.getEventCount("每日開店獎勵") <= 0){
                                chr.setEventCount("每日開店獎勵");
                                chr.gainMeso(100000000, true);
                                chr.dropMessage(1, "獲得每日開店獎勵1億楓幣");
                            }
                        } else if (shop.getShopType() == 2) {
                            shop.setOpen(true);
                            shop.setAvailable(true);
                            shop.update();
                        }
                    } else {
                        c.getSession().close();
                    }
                }
                break;
            }
            case SET_ITEMS: {
                final MapleInventoryType ivType = MapleInventoryType.getByType(slea.readByte());
                final Item item = chr.getInventory(ivType).getItem((byte) slea.readShort());
                final short quantity = slea.readShort();
                final byte targetSlot = slea.readByte();
                if (chr.getTrade() != null && item != null) {
                    if ((quantity <= item.getQuantity() && quantity >= 0) || GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
                        chr.getTrade().setItems(c, item, targetSlot, quantity);
                    }
                }
                break;
            }
            case SET_MESO: {
                final MapleTrade trade = chr.getTrade();
                if (trade != null) {
                    trade.setMeso(slea.readInt());
                }
                break;
            }
            case PLAYER_SHOP_ADD_ITEM:
            case ADD_ITEM: {
                final MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());
                final byte slot = (byte) slea.readShort();
                final short bundles = slea.readShort(); // How many in a bundle
                final short perBundle = slea.readShort(); // Price per bundle
                if (!c.getPlayer().haveItem(c.getPlayer().getInventory(type).getItem(slot).getItemId(), (perBundle * bundles), false, true)) {
                    return;
                }
                final int price = slea.readInt();

                if (price <= 0 || bundles <= 0 || perBundle <= 0) {
                    return;
                }
                final IMaplePlayerShop shop = chr.getPlayerShop();

                if (shop == null || !shop.isOwner(chr) || shop instanceof MapleMiniGame) {
                    return;
                }
                final Item ivItem = chr.getInventory(type).getItem(slot);
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                if (ivItem != null) {
                    long check = bundles * perBundle;
                    if (check > 32767 || check <= 0) { //This is the better way to check.
                        return;
                    }
                    final short bundles_perbundle = (short) (bundles * perBundle);
                    if (ivItem.getQuantity() >= bundles_perbundle) {
                        final short flag = ivItem.getFlag();
                        if (ItemFlag.UNTRADEABLE.check(flag) || ItemFlag.LOCK.check(flag)) {
                            c.sendPacket(CWvsContext.enableActions());
                            return;
                        }
                        if (ii.isAccountShared(ivItem.getItemId())) {
                            if (!(ItemFlag.KARMA_EQ.check(flag) || ItemFlag.KARMA_USE.check(flag))) {
                                c.sendPacket(CWvsContext.enableActions());
                                return;
                            }
                        }
                        if (GameConstants.getLowestPrice(ivItem.getItemId()) > price) {
                            c.getPlayer().dropMessage(1, "The lowest you can sell this for is " + GameConstants.getLowestPrice(ivItem.getItemId()));
                            c.sendPacket(CWvsContext.enableActions());
                            return;
                        }
                        if (GameConstants.isThrowingStar(ivItem.getItemId()) || GameConstants.isBullet(ivItem.getItemId())) {
                            MapleInventoryManipulator.removeFromSlot(c, type, slot, ivItem.getQuantity(), true);
                            final Item sellItem = ivItem.copy();
                            shop.addItem(new MaplePlayerShopItem(sellItem, (short) 1, price));
                        } else {
                            MapleInventoryManipulator.removeFromSlot(c, type, slot, bundles_perbundle, true);
                            final Item sellItem = ivItem.copy();
                            sellItem.setQuantity(perBundle);
                            shop.addItem(new MaplePlayerShopItem(sellItem, bundles, price));
                        }
                        c.sendPacket(PlayerShopPacket.shopItemUpdate(shop));
                    }
                }
                break;
            }
            case CONFIRM_TRADE:
            case BUY_ITEM_PLAYER_SHOP:
            case BUY_ITEM_HIREDMERCHANT: { // fix Merchant buy
                if (chr.getTrade() != null) {
                    MapleTrade.completeTrade(chr);
                    break;
                }
                final int item = slea.readByte();
                final short quantity = slea.readShort();
                final IMaplePlayerShop shop = chr.getPlayerShop();

                if (shop == null || shop.isOwner(chr) || shop instanceof MapleMiniGame || item >= shop.getItems().size()) {
                    c.getPlayer().getClient().sendPacket(CWvsContext.enableActions());
                    return;
                }
                final MaplePlayerShopItem tobuy = shop.getItems().get(item);
                if (tobuy == null) {
                    c.getPlayer().getClient().sendPacket(CWvsContext.enableActions());
                    return;
                }
                long check = tobuy.bundles * quantity;
                long check2 = tobuy.price * quantity;
                long check3 = tobuy.item.getQuantity() * quantity;
                if (check <= 0 || check2 > 2147483647 || check2 <= 0 || check3 > 32767 || check3 < 0) {
                    c.getPlayer().dropMessage(1, "Can't buy! The shop owner probably has too much mesos."); //todo; gms-like
                    c.getPlayer().getClient().sendPacket(CWvsContext.enableActions());
                    return;
                }
                if (tobuy.bundles < quantity || (tobuy.bundles % quantity != 0 && GameConstants.isEquip(tobuy.item.getItemId())) || chr.getMeso() - (check2) < 0 || chr.getMeso() - (check2) > 2147483647 || shop.getMeso() + (check2) < 0 || shop.getMeso() + (check2) > 2147483647) {
                    c.getPlayer().dropMessage(1, "Can't buy! The shop owner probably has too much mesos.");
                    c.getPlayer().getClient().sendPacket(CWvsContext.enableActions());
                    return;
                }
                shop.buy(c, item, quantity);
                shop.broadcastToVisitors(PlayerShopPacket.shopItemUpdate(shop));
                break;
            }
            case PLAYER_SHOP_REMOVE_ITEM:
            case REMOVE_ITEM: {
                slea.skip(1);
                int slot = slea.readShort();
                final IMaplePlayerShop shop = chr.getPlayerShop();
                if (shop == null || !shop.isOwner(chr) || shop instanceof MapleMiniGame || shop.getItems().size() <= 0 || shop.getItems().size() <= slot || slot < 0) {
                    c.getPlayer().getClient().sendPacket(CWvsContext.enableActions());
                    return;
                }
                final MaplePlayerShopItem item = shop.getItems().get(slot);
                if (item != null) {
                    if (item.bundles > 0) {
                        Item item_get = item.item.copy();
                        long check = item.bundles * item.item.getQuantity();
                        if (check < 0 || check > 32767) {
                            c.getPlayer().getClient().sendPacket(CWvsContext.enableActions());
                            return;
                        }
                        item_get.setQuantity((short) check);
                        if (MapleInventoryManipulator.checkSpace(c, item_get.getItemId(), item_get.getQuantity(), item_get.getOwner())) {
                            MapleInventoryManipulator.addFromDrop(c, item_get, false);
                            item.bundles = 0;
                            shop.removeFromSlot(slot);
                        }
                    }
                }
                c.sendPacket(PlayerShopPacket.shopItemUpdate(shop));
                break;
            }
            case MAINTANCE_OFF: {
                final IMaplePlayerShop shop = chr.getPlayerShop();
                if (shop != null && shop instanceof HiredMerchant && shop.isOwner(chr) && shop.isAvailable()) {
                    shop.setOpen(true);
                    shop.removeAllVisitors(-1, -1);
                }
                break;
            }
            case MAINTANCE_ORGANISE: {
                final IMaplePlayerShop imps = chr.getPlayerShop();
                if (imps != null && imps.isOwner(chr) && !(imps instanceof MapleMiniGame)) {
                    for (int i = 0; i < imps.getItems().size(); i++) {
                        if (imps.getItems().get(i).bundles == 0) {
                            imps.getItems().remove(i);
                        }
                    }
                    if (chr.getMeso() + imps.getMeso() > 0 && chr.getMeso() + imps.getMeso() <= Integer.MAX_VALUE) {
                        chr.gainMeso(imps.getMeso(), false);
                        imps.setMeso(0);
                    }
                    c.sendPacket(PlayerShopPacket.shopItemUpdate(imps));
                }
                break;
            }
            case CLOSE_MERCHANT: { // the one-and-only todo >.>
                final IMaplePlayerShop merchant = chr.getPlayerShop();
                if (merchant != null && merchant.getShopType() == 1 && merchant.isOwner(chr) && (merchant.isAvailable() || !merchant.isOpen())) {
                    c.sendPacket(CWvsContext.broadcastMsg(1, "請找富蘭德里取回您的道具."));
                    if (merchant.isOwner(chr)) {
                        merchant.closeShop(true, true); //how to return the items?
                    } else {
                        merchant.removeVisitor(chr);
                    }

                    chr.setPlayerShop(null);
                    merchant.setOpen(true);
                    merchant.removeAllVisitors(22, 1);
                    merchant.setOpen(false);
                    c.sendPacket(CWvsContext.enableActions());
                }
                break;
            }
            case ADMIN_STORE_NAMECHANGE: { // Changing store name, only Admin
                // 01 00 00 00
                break;
            }
            case VIEW_MERCHANT_VISITOR: {
                final IMaplePlayerShop merchant = chr.getPlayerShop();
                if (merchant != null && merchant.getShopType() == 1 && merchant.isOwner(chr)) {
                    ((HiredMerchant) merchant).sendVisitor(c);
                }
                break;
            }
            case VIEW_MERCHANT_BLACKLIST: {
                final IMaplePlayerShop merchant = chr.getPlayerShop();
                if (merchant != null && merchant.getShopType() == 1 && merchant.isOwner(chr)) {
                    ((HiredMerchant) merchant).sendBlackList(c);
                }
                break;
            }
            case MERCHANT_BLACKLIST_ADD: {
                final IMaplePlayerShop merchant = chr.getPlayerShop();
                if (merchant != null && merchant.getShopType() == 1 && merchant.isOwner(chr)) {
                    ((HiredMerchant) merchant).addBlackList(slea.readMapleAsciiString());
                }
                break;
            }
            case MERCHANT_BLACKLIST_REMOVE: {
                final IMaplePlayerShop merchant = chr.getPlayerShop();
                if (merchant != null && merchant.getShopType() == 1 && merchant.isOwner(chr)) {
                    ((HiredMerchant) merchant).removeBlackList(slea.readMapleAsciiString());
                }
                break;
            }
            case PLAYER_SHOP_BAN: {
                byte a1 = slea.readByte();
                String a2 = slea.readMapleAsciiString();
                System.out.println("a1: " + a1 + " a2: " + a2);
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips.isOpen()) {
                    ips.removeFromSlot(a1);
                    ((MaplePlayerShop) ips).banPlayer(a2);
                }
                c.sendPacket(CWvsContext.enableActions());
                break;
            }
            case GIVE_UP: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        break;
                    }
                    game.broadcastToVisitors(PlayerShopPacket.getMiniGameResult(game, 0, game.getVisitorSlot(chr)));
                    game.nextLoser();
                    game.setOpen(true);
                    game.update();
                    game.checkExitAfterGame();
                }
                break;
            }
            case EXPEL: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    if (!((MapleMiniGame) ips).isOpen()) {
                        break;
                    }
                    ips.removeAllVisitors(3, 1); //no msg
                }
                break;
            }
            case READY:
            case UN_READY: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (!game.isOwner(chr) && game.isOpen()) {
                        game.setReady(game.getVisitorSlot(chr));
                        game.broadcastToVisitors(PlayerShopPacket.getMiniGameReady(game.isReady(game.getVisitorSlot(chr))));
                    }
                }
                break;
            }
            case START: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOwner(chr) && game.isOpen()) {
                        for (int i = 1; i < ips.getSize(); i++) {
                            if (!game.isReady(i)) {
                                return;
                            }
                        }
                        game.setGameType();
                        game.shuffleList();
                        if (game.getGameType() == 1) {
                            game.broadcastToVisitors(PlayerShopPacket.getMiniGameStart(game.getLoser()));
                        } else {
                            game.broadcastToVisitors(PlayerShopPacket.getMatchCardStart(game, game.getLoser()));
                        }
                        game.setOpen(false);
                        game.update();
                    }
                }
                break;
            }
            case REQUEST_TIE: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        break;
                    }
                    if (game.isOwner(chr)) {
                        game.broadcastToVisitors(PlayerShopPacket.getMiniGameRequestTie(), false);
                    } else {
                        game.getMCOwner().getClient().sendPacket(PlayerShopPacket.getMiniGameRequestTie());
                    }
                    game.setRequestedTie(game.getVisitorSlot(chr));
                }
                break;
            }
            case ANSWER_TIE: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        break;
                    }
                    if (game.getRequestedTie() > -1 && game.getRequestedTie() != game.getVisitorSlot(chr)) {
                        if (slea.readByte() > 0) {
                            game.broadcastToVisitors(PlayerShopPacket.getMiniGameResult(game, 1, game.getRequestedTie()));
                            game.nextLoser();
                            game.setOpen(true);
                            game.update();
                            game.checkExitAfterGame();
                        } else {
                            game.broadcastToVisitors(PlayerShopPacket.getMiniGameDenyTie());
                        }
                        game.setRequestedTie(-1);
                    }
                }
                break;
            }
            case SKIP: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        break;
                    }
                    //  if (game.getLoser() != ips.getVisitorSlot(chr)) {
                    //      ips.broadcastToVisitors(PlayerShopPacket.shopChat("Turn could not be skipped by " + chr.getName() + ". Loser: " + game.getLoser() + " Visitor: " + ips.getVisitorSlot(chr), ips.getVisitorSlot(chr)));
                    //      return;
                    //  }
                    ips.broadcastToVisitors(PlayerShopPacket.getMiniGameSkip(ips.getVisitorSlot(chr)));
                    game.nextLoser();
                }
                break;
            }
            case MOVE_OMOK: { // need to fix piece byte
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        break;
                    }
//                    if (game.getLoser() != game.getVisitorSlot(chr)) {
//                        game.broadcastToVisitors(PlayerShopPacket.shopChat("Omok could not be placed by " + chr.getName() + ". Loser: " + game.getLoser() + " Visitor: " + game.getVisitorSlot(chr), game.getVisitorSlot(chr)));
//                        return;
//                    }
                    game.setPiece(slea.readInt(), slea.readInt(), slea.readByte(), chr);
                }
                break;
            }
            case SELECT_CARD: { // need to fix piece byte
                final int turn = slea.readByte(); // 1st turn = 1; 2nd turn = 0
                final int slot = slea.readByte(); // slot
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        break;
                    }
                    //    if (game.getLoser() != game.getVisitorSlot(chr)) {
                    //        game.broadcastToVisitors(PlayerShopPacket.shopChat("Card could not be placed by " + chr.getName() + ". Loser: " + game.getLoser() + " Visitor: " + game.getVisitorSlot(chr), game.getVisitorSlot(chr)));
                    //        return;
                    //    }
                    //    if (slea.readByte() != game.getTurn()) {
                    //        game.broadcastToVisitors(PlayerShopPacket.shopChat("Omok could not be placed by " + chr.getName() + ". Loser: " + game.getLoser() + " Visitor: " + game.getVisitorSlot(chr) + " Turn: " + game.getTurn(), game.getVisitorSlot(chr)));
                    //        return;
                    //    }
                    final int firstslot = game.getFirstSlot();
                    if (turn == 1) {
                        game.setFirstSlot(slot);
                        game.broadcastToVisitors(PlayerShopPacket.getMatchCardSelect(turn, slot, firstslot, turn), !game.isOwner(c.getPlayer()));
                        game.setTurn(0); //2nd turn nao
                        return; // lolwut
                    } else if (game.getCardId(firstslot + 1) == game.getCardId(slot + 1)) {
                        if (game.isOwner(c.getPlayer())) {
                            game.broadcastToVisitors(PlayerShopPacket.getMatchCardSelect(turn, slot, firstslot, 2), true);
                            game.setPoints(game.getVisitorSlot(chr)); //correct.. so still same loser. diff turn tho
                        } else {
                            game.broadcastToVisitors(PlayerShopPacket.getMatchCardSelect(turn, slot, firstslot, 3), true);
//                          game.setVisitorPoints();
                            game.nextLoser();//wrong haha
                        }
                    } else {
                        game.broadcastToVisitors(PlayerShopPacket.getMatchCardSelect(turn, slot, firstslot, game.isOwner(c.getPlayer()) ? 0 : 1), true);
                    }
                    game.setTurn(1);
                    game.setFirstSlot(0);
                }
                break;
            }
            case EXIT_AFTER_GAME:
            case CANCEL_EXIT: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        break;
                    }
                    game.setExitAfter(chr);
                    game.broadcastToVisitors(PlayerShopPacket.getMiniGameExitAfter(game.isExitAfter(chr)));
                }
                break;
            }
            case REQUEST_REDO: {
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        break;
                    }
                    if (game.isOwner(chr)) {
                        game.broadcastToVisitors(PlayerShopPacket.getMiniGameRequestRedo(), false);
                    } else {
                        game.getMCOwner().getClient().sendPacket(PlayerShopPacket.getMiniGameRequestRedo());
                    }
                    // game.setRequestedTie(game.getVisitorSlot(chr));
                }
                break;
            }
            case ANSWER_REDO: { // TODO: code getMiniGameAnswerRedo()
                System.out.println("ANSWER_REDO interaction called.");
                final IMaplePlayerShop ips = chr.getPlayerShop();
                if (ips != null && ips instanceof MapleMiniGame) {
                    MapleMiniGame game = (MapleMiniGame) ips;
                    if (game.isOpen()) {
                        break;
                    }
                    if (game.getRequestedTie() > -1 && game.getRequestedTie() != game.getVisitorSlot(chr)) {
                        if (slea.readByte() > 0) {
                            game.broadcastToVisitors(PlayerShopPacket.getMiniGameResult(game, 1, game.getRequestedTie()));
                            game.nextLoser();
                            game.setOpen(true);
                            game.update();
                            game.checkExitAfterGame();
                        } else {
                            game.broadcastToVisitors(PlayerShopPacket.getMiniGameDenyRedo());
                        }
                        game.setRequestedTie(-1);
                    }
                }
                break;
            }
            default: {
                //System.out.println("Unhandled interaction action by " + chr.getName() + " : " + action + ", " + slea.toString());
                break;
            }
        }
    }

    private enum Interaction {

        CREATE(6),
        INVITE_TRADE(11),
        DENY_TRADE(12),
        VISIT(9),
        HIRED_MERCHANT_MAINTENANCE(999), // ?
        CHAT(14),
        EXIT(18),
        OPEN(16),
        SET_ITEMS(0),
        SET_MESO(1),
        CONFIRM_TRADE(2),
        PLAYER_SHOP_ADD_ITEM(49),
        PLAYER_SHOP_REMOVE_ITEM(50),
        PLAYER_SHOP_BAN(51),
        BUY_ITEM_PLAYER_SHOP(43), // (BUY_ITEM_STORE(22) // unknown: 53
        ADD_ITEM(21),
        BUY_ITEM_HIREDMERCHANT(22), // ? was 26
        REMOVE_ITEM(28),
        MAINTANCE_OFF(29), // ?
        MAINTANCE_ORGANISE(30), // ?
        CLOSE_MERCHANT(31), // fix this
        TAKE_MESOS(33), // ?
        ADMIN_STORE_NAMECHANGE(35), // ?
        VIEW_MERCHANT_VISITOR(36),
        VIEW_MERCHANT_BLACKLIST(37),
        MERCHANT_BLACKLIST_ADD(38),
        MERCHANT_BLACKLIST_REMOVE(39),
        REQUEST_TIE(56),
        ANSWER_TIE(57),
        GIVE_UP(58),
        REQUEST_REDO(60), // code this
        ANSWER_REDO(61), // code this
        EXIT_AFTER_GAME(62),
        CANCEL_EXIT(63),
        READY(64),
        UN_READY(65),
        EXPEL(66),
        START(67),
        SKIP(69),
        MOVE_OMOK(70),
        SELECT_CARD(74); // working (74), possibly 73
        public int action;

        private Interaction(int action) {
            this.action = action;
        }

        public static Interaction getByAction(int i) {
            for (Interaction s : Interaction.values()) {
                if (s.action == i) {
                    return s;
                }
            }
            return null;
        }
    }
}
