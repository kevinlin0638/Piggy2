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
import client.inventory.Item;
import client.messages.commands.AdminCommand;
import constants.GameConstants;
import handling.SendPacketOpcode;
import server.MerchItemPackage;
import server.shops.AbstractPlayerStore.BoughtItem;
import server.shops.*;
import tools.data.MaplePacketLittleEndianWriter;
import tools.types.Pair;

import java.util.List;

public class PlayerShopPacket {

    public static byte[] sendTitleBox() {
        return sendTitleBox(7); // SendOpenShopRequest
    }

    public static byte[] sendTitleBox(final int mode) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(8);

        // 9: Please use this after retrieving items from Fredrick of Free Market.
        // 10: Another character is currently using the item. Please log on as a different character and close the store, or empty out the Store Bank.
        // 11: You are currently unable to open the store.
        // 15: Please retrieve your items from Fredrick.
        mplew.writeShort(SendPacketOpcode.SEND_TITLE_BOX.getValue());
        mplew.write(mode);
        if (mode == 8 || mode == 16) {
            // 8: Your store is currently open in Channel 2, Free Market 10. Please use this after closing the store.
            mplew.writeInt(0); // Room
            mplew.write(0); // (channel - 1)
            // If mode == 16 and channel -1 = -1/-2/-3 : Unable to use this due to the Remote Shop not being open.
            // The store is open at Channel 2. Would you like to change to that channel?			
        } else if (mode == 13) { // Minimap stuffs
            mplew.writeInt(0); // ?? object id?
        } else if (mode == 14) {
            //0: Renaming Failed - Can't find the Hired Merchant
            //1: Renaming Successful.
            mplew.write(0);
        } else if (mode == 18) { // Normal popup message
            mplew.write(1);
            mplew.writeMapleAsciiString("");
        }

        return mplew.getPacket();
    }

    public static byte[] requestShopPic(final int oid) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter(17);

        mplew.writeShort(SendPacketOpcode.SEND_TITLE_BOX.getValue());
        mplew.write(17);
        mplew.writeInt(oid);
        mplew.writeShort(0); // idk
        mplew.writeLong(0); // idk also

        return mplew.getPacket();
    }

    public static byte[] addCharBox(final MapleCharacter c, final int type) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_CHAR_BOX.getValue());
        mplew.writeInt(c.getId());
        PacketHelper.addAnnounceBox(mplew, c);

        return mplew.getPacket();
    }

    public static byte[] removeCharBox(final MapleCharacter c) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_CHAR_BOX.getValue());
        mplew.writeInt(c.getId());
        mplew.write(0);

        return mplew.getPacket();
    }

    public static byte[] sendPlayerShopBox(final MapleCharacter c) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_CHAR_BOX.getValue());
        mplew.writeInt(c.getId());
        PacketHelper.addAnnounceBox(mplew, c);

        return mplew.getPacket();
    }

    public static byte[] getHiredMerch(final MapleCharacter chr, final HiredMerchant merch, final boolean firstTime) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(10);
        mplew.write(5);
        mplew.write(6); // ?
        mplew.writeShort(merch.getVisitorSlot(chr));
        mplew.writeInt(merch.getItemId());
        mplew.writeMapleAsciiString("Hired Merchant");
        for (final Pair<Byte, MapleCharacter> storechr : merch.getVisitors()) {
            mplew.write(storechr.left);
            PacketHelper.addCharLook(mplew, storechr.right, false, chr.getClient());
            mplew.writeMapleAsciiString(storechr.right.getName());
            mplew.writeShort(storechr.right.getJob());
        }
        mplew.write(-1);
        mplew.writeShort(0);
        mplew.writeMapleAsciiString(merch.getOwnerName());
        if (merch.isOwner(chr)) {
            mplew.writeInt(merch.getTimeLeft());
            mplew.write(firstTime ? 1 : 0);
            mplew.write(merch.getBoughtItems().size());
            for (BoughtItem SoldItem : merch.getBoughtItems()) {
                mplew.writeInt(SoldItem.id);
                mplew.writeShort(SoldItem.quantity); // number of purchased
                mplew.writeInt(SoldItem.totalPrice); // total price
                mplew.writeMapleAsciiString(SoldItem.buyer); // name of the buyer
            }
            mplew.writeInt(merch.getMeso());
            mplew.writeInt(0);
        }
        mplew.writeMapleAsciiString(merch.getDescription());
        mplew.write(16); // size
        mplew.writeInt(merch.getMeso()); // meso
        mplew.write(merch.getItems().size());
        for (final MaplePlayerShopItem item : merch.getItems()) {
            mplew.writeShort(item.bundles);
            mplew.writeShort(item.item.getQuantity());
            mplew.writeInt(item.price);
            PacketHelper.GW_ItemSlotBase_Decode(mplew, item.item);
        }
        mplew.writeShort(0);

        return mplew.getPacket();
    }

    public static byte[] getPlayerStore(final MapleCharacter chr, final boolean firstTime) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        IMaplePlayerShop ips = chr.getPlayerShop();
        mplew.write(10);
        mplew.write(4);
        mplew.write(7);
        mplew.write(ips.isOwner(chr) ? 0 : 1);
        mplew.write(0);
        PacketHelper.addCharLook(mplew, ((MaplePlayerShop) ips).getMCOwner(), false, chr.getClient());
        mplew.writeMapleAsciiString(((MaplePlayerShop) ips).getMCOwner().getName());
        mplew.writeShort(((MaplePlayerShop) ips).getMCOwner().getJob());
        if (!firstTime) {
            for (final Pair<Byte, MapleCharacter> storechr : ips.getVisitors()) {
                mplew.write(storechr.left);
                PacketHelper.addCharLook(mplew, storechr.right, false, chr.getClient());
                mplew.writeMapleAsciiString(storechr.right.getName());
                mplew.writeShort(storechr.right.getJob());
            }
        } else {
            mplew.write(1);
            PacketHelper.addCharLook(mplew, ((MaplePlayerShop) ips).getMCOwner(), false, chr.getClient());
            mplew.writeMapleAsciiString(((MaplePlayerShop) ips).getMCOwner().getName());
            mplew.writeShort(((MaplePlayerShop) ips).getMCOwner().getJob());
        }
        mplew.write(0xFF);
        mplew.writeMapleAsciiString(ips.getDescription());
        mplew.write(0x10);
        mplew.write(ips.getItems().size());
        for (final MaplePlayerShopItem item : ips.getItems()) {
            mplew.writeShort(item.bundles);
            mplew.writeShort(item.item.getQuantity());
            mplew.writeInt(item.price);
            PacketHelper.GW_ItemSlotBase_Decode(mplew, item.item);
        }
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    public static byte[] shopChat(final String message, final int slot) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(14);
        mplew.write(15);
        mplew.write(slot);
        mplew.writeMapleAsciiString(message);

        return mplew.getPacket();
    }

    public static byte[] shopErrorMessage(final int error, final int type) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(GameConstants.GMS ? 18 : 0x0A);
        mplew.write(type);
        mplew.write(error);

        return mplew.getPacket();
    }

    public static byte[] spawnHiredMerchant(final HiredMerchant hm) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SPAWN_HIRED_MERCHANT.getValue());
        mplew.writeInt(hm.getOwnerId());
        mplew.writeInt(hm.getItemId());
        mplew.writePos(hm.getTruePosition());
        mplew.writeShort(0);
        mplew.writeMapleAsciiString(hm.getOwnerName());
        PacketHelper.addInteraction(mplew, hm);

        return mplew.getPacket();
    }

    public static byte[] destroyHiredMerchant(final int id) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.DESTROY_HIRED_MERCHANT.getValue());
        mplew.writeInt(id);

        return mplew.getPacket();
    }

    public static byte[] shopItemUpdate(final IMaplePlayerShop shop) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(46);
        if (shop.getShopType() == 1) {
            //mplew.writeInt(((MaplePlayerShop) shop).getMCOwner().getMeso());
            mplew.writeInt(0);
        }
        mplew.write(shop.getItems().size());
        for (final MaplePlayerShopItem item : shop.getItems()) {
            mplew.writeShort(item.bundles);
            mplew.writeShort(item.item.getQuantity());
            mplew.writeInt(item.price);
            PacketHelper.GW_ItemSlotBase_Decode(mplew, item.item);
        }
        mplew.writeShort(0);
        return mplew.getPacket();
    }

    public static byte[] shopVisitorAdd(final MapleCharacter chr, final int slot) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(9);
        mplew.write(slot);
        PacketHelper.addCharLook(mplew, chr, false, chr.getClient());
        mplew.writeMapleAsciiString(chr.getName());
        mplew.writeShort(chr.getJob());

        return mplew.getPacket();
    }


    public static byte[] shopVisitorLeave(final byte slot) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(18);
        if (slot > 0) {
            mplew.write(slot);
        }
        return mplew.getPacket();
    }

    public static byte[] Merchant_Buy_Error(final byte message) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        // 2 = You have not enough meso
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(44);
        mplew.write(message);

        return mplew.getPacket();
    }

    public static byte[] updateHiredMerchant(final HiredMerchant shop) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.UPDATE_HIRED_MERCHANT.getValue());
        mplew.writeInt(shop.getOwnerId());
        PacketHelper.addInteraction(mplew, shop);

        return mplew.getPacket();
    }

    public static byte[] merchItem_Message(final int op) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        //32: You have retrieved your items and mesos.
        //33: Unable to retrieve mesos and items due to\r\ntoo much money stored\r\nat the Store Bank.
        //34: Unable to retrieve mesos and items due to\r\none of the items\r\nthat can only be possessed one at a time.
        //35: Due to the lack of service fee, you were unable to \r\nretrieve mesos or items. 
        //36: Unable to retrieve mesos and items\r\ndue to full inventory.
        mplew.writeShort(SendPacketOpcode.MERCH_ITEM_MSG.getValue());
        mplew.write(op);

        return mplew.getPacket();
    }

    public static byte[] merchItemStore(final byte op, final int days, final int fees) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        // 40: This is currently unavailable.\r\nPlease try again later
        mplew.writeShort(SendPacketOpcode.MERCH_ITEM_STORE.getValue());
        mplew.write(op);
        switch (op) {
            case 39:
                mplew.writeInt(999999999); // ? 
                mplew.writeInt(999999999); // mapid
                mplew.write(0); // >= -2 channel
                // if cc -1 or map = 999,999,999 : I don't think you have any items or money to retrieve here. This is where you retrieve the items and mesos that you couldn't get from your Hired Merchant. You'll also need to see me as the character that opened the Personal Store.
                //Your Personal Store is open #bin Channel %s, Free Market %d#k.\r\nIf you need me, then please close your personal store first before seeing me.
                break;
            case 38:
                mplew.writeInt(days); // % tax or days, 1 day = 1%
                mplew.writeInt(fees); // feees
                break;
        }

        return mplew.getPacket();
    }

    public static byte[] merchItemStore_ItemData(final MerchItemPackage pack) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MERCH_ITEM_STORE.getValue());
        mplew.write(37);
        mplew.writeInt(9030000); // Fredrick
        mplew.write(16); // max items?
        mplew.writeLong(126); // ?
        mplew.writeInt(pack.getMeso());
        mplew.write(0);
        mplew.write(pack.getItems().size());
        for (final Item item : pack.getItems()) {
            PacketHelper.GW_ItemSlotBase_Decode(mplew, item);
        }
        mplew.writeZeroBytes(3);

        return mplew.getPacket();
    }

    // MINIGAME PACKETS -- UPDATED FOR MAPLE ASCENSION
    public static byte[] getMiniGame(MapleClient c, MapleMiniGame minigame) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(10); // 0x0A
        mplew.write(minigame.getGameType());
        mplew.write(minigame.getMaxSize());
        mplew.writeShort(minigame.getVisitorSlot(c.getPlayer()));
        PacketHelper.addCharLook(mplew, minigame.getMCOwner(), false, c);
        mplew.writeMapleAsciiString(minigame.getOwnerName());
        mplew.writeShort(minigame.getMCOwner().getJob());
        for (Pair<Byte, MapleCharacter> visitorz : minigame.getVisitors()) {
            mplew.write(visitorz.getLeft());
            PacketHelper.addCharLook(mplew, visitorz.getRight(), false, c);
            mplew.writeMapleAsciiString(visitorz.getRight().getName());
            mplew.writeShort(visitorz.getRight().getJob());
        }
        mplew.write(-1);
        mplew.write(0);
        addGameInfo(mplew, minigame.getMCOwner(), minigame);
        for (Pair<Byte, MapleCharacter> visitorz : minigame.getVisitors()) {
            mplew.write(visitorz.getLeft());
            addGameInfo(mplew, visitorz.getRight(), minigame);
        }
        mplew.write(-1);
        mplew.writeMapleAsciiString(minigame.getDescription());
        mplew.writeShort(minigame.getPieceType());

        return mplew.getPacket();
    }

    // REDO = 0x3C
    // SKIP = 0x45
    // START = 0x41
    // REQUEST TIE = 0x38
    // DENY TIE = 0x39
    // READY = 0x40
    // UNREADY = 0x41
    // EXPEL = 0x3C
    public static byte[] getMiniGameReady(boolean ready) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(ready ? 0x40 : 0x41);
        return mplew.getPacket();
    }

    public static byte[] getMiniGameExitAfter(boolean ready) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x3E);
        return mplew.getPacket();
    }

    public static byte[] getMiniGameStart(int loser) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x43);
        mplew.write(loser == 1 ? 0 : 1);
        return mplew.getPacket();
    }

    public static byte[] getMiniGameSkip(int slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x45);
        mplew.write(slot);
        return mplew.getPacket();
    }

    public static byte[] getMiniGameRequestTie() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x38);
        return mplew.getPacket();
    }

    public static byte[] getMiniGameDenyTie() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x39);
        return mplew.getPacket();
    }

    public static byte[] getMiniGameRequestRedo() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x3C);
        return mplew.getPacket();
    }

    public static byte[] getMiniGameDenyRedo() {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x3D);
        return mplew.getPacket();
    }

    public static byte[] getMiniGameFull() { // todo
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x10);
        mplew.write(0);
        mplew.write(2);
        System.out.println("getMiniGameFull{} called");
        return mplew.getPacket();
    }

    public static byte[] getMiniGameFullMem() { // todo
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x43);
        mplew.write(0);
        System.out.println("getMiniGameFullMem{} called");
        return mplew.getPacket();
    }

    public static byte[] getMiniGameMoveOmok(int move1, int move2, int move3) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x46);
        mplew.writeInt(move1);
        mplew.writeInt(move2);
        mplew.write(move3);
        return mplew.getPacket();
    }

    public static byte[] getMiniGameNewVisitor(MapleCharacter c, int slot, MapleMiniGame game) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x09); // could also be 0x04 but 0x09 works
        mplew.write(slot);
        PacketHelper.addCharLook(mplew, c, false, c.getClient());
        mplew.writeMapleAsciiString(c.getName());
        mplew.writeShort(c.getJob());
        addGameInfo(mplew, c, game);
        return mplew.getPacket();
    }

    public static void addGameInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr, MapleMiniGame game) {
        mplew.writeInt(game.getGameType());
        mplew.writeInt(game.getWins(chr));
        mplew.writeInt(game.getTies(chr));
        mplew.writeInt(game.getLosses(chr));
        mplew.writeInt(game.getScore(chr)); // points, what about Matchcards' score? Need to update that too :/
    }

    public static byte[] getMiniGameClose(byte number) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(GameConstants.GMS ? 18 : 0xA); // todo: check if this is correct
        mplew.write(1);
        mplew.write(number);
        return mplew.getPacket();
    }

    // MATCHCARD PACKETS -- UPDATED FOR MAPLE ASCENSION
    public static byte[] getMatchCardStart(MapleMiniGame game, int loser) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x43);
        mplew.write(loser == 1 ? 0 : 1);
        int times;
        if (game.getMatchesToWin() > 10) {
            times = 30;
        } else if (game.getMatchesToWin() > 6) {
            times = 20;
        } else {
            times = 12;
        }
        mplew.write(times);
        for (int i = 1; i <= times; i++) {
            mplew.writeInt(game.getCardId(i));
        }
        return mplew.getPacket();
    }

    public static byte[] getMatchCardSelect(int turn, int slot, int firstslot, int type) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x4A); // WAS: 0x49
        mplew.write(turn);
        // mplew.write(slot);
        if (turn == 1) {
            mplew.write(slot);
        } else if (turn == 0) {
            mplew.write(slot);
            mplew.write(firstslot);
            mplew.write(type);
        }
        return mplew.getPacket();
    }

    public static byte[] getMiniGameResult(MapleMiniGame game, int type, int x) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(0x44);
        mplew.write(type); //lose = 0, tie = 1, win = 2
        game.setPoints(x, type);
        if (type != 0) {
            game.setPoints(x == 1 ? 0 : 1, type == 2 ? 0 : 1);
        }
        if (type != 1) {
            if (type == 0) {
                mplew.write(x == 1 ? 0 : 1);
            } else {
                mplew.write(x);
            }
        }
        addGameInfo(mplew, game.getMCOwner(), game);
        for (Pair<Byte, MapleCharacter> visitorz : game.getVisitors()) {
            addGameInfo(mplew, visitorz.right, game);
        }

        return mplew.getPacket();
    }

    public static byte[] MerchantBlackListView(final List<String> blackList) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(37);
        mplew.writeShort(blackList.size());
        for (String visit : blackList) {
            mplew.writeMapleAsciiString(visit);
        }
        return mplew.getPacket();
    }

    public static byte[] MerchantVisitorView(List<Pair<String, Long>> visitor) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PLAYER_INTERACTION.getValue());
        mplew.write(36);
        mplew.writeShort(visitor.size());
        for (Pair<String, Long> visit : visitor) {
            mplew.writeMapleAsciiString(visit.getLeft());
            mplew.writeInt(visit.getRight().intValue()); /////for the lul
        }
        return mplew.getPacket();
    }
}
