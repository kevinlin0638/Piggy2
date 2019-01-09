package handling.cashshop.handler;

import client.MapleCharacter;
import client.MapleCharacterUtil;
import client.MapleClient;
import client.MapleQuestStatus;
import client.inventory.Item;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.MapleRing;
import constants.GameConstants;
import handling.cashshop.CashShopServer;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.CharacterTransfer;
import handling.world.World;
import server.cashshop.CashItemFactory;
import server.cashshop.CashItemInfo;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.quest.MapleQuest;
import tools.FileoutputUtil;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.MTSCSPacket;
import tools.types.Triple;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CashShopHandler {

    public static void LeaveCS(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        CashShopServer.getPlayerStorage().deregisterPendingPlayer(chr.getId());
        int world = World.Find.findWorld(chr.getId());
        c.updateLoginState(MapleClient.CHANGE_CHANNEL, c.getSessionIPAddress());
        try {
            World.ChannelChange_Data(new CharacterTransfer(chr), chr.getId(), world, c.getChannel());
            String socket[] = ChannelServer.getInstance(world, c.getChannel()).getIP().split(":");
            c.sendPacket(CField.getChannelChange(c, InetAddress.getByName(socket[0]) , Integer.parseInt(socket[1])));
        } catch (UnknownHostException ignored) {
        } finally {
            final String s = c.getSessionIPAddress();
            LoginServer.addIPAuth(s.substring(s.indexOf('/') + 1, s.length()));
            CashShopServer.getPlayerStorage().removePlayer(chr.getId());
            chr.saveToDB(false, true);
            c.setPlayer(null);
            c.setReceiving(false);
        }
    }

    public static void EnterCS(final int playerid, final MapleClient c) {
        CharacterTransfer transfer = CashShopServer.getPlayerStorage().getPendingCharacter(playerid);
        if (transfer == null) {
            c.getSession().close();
            System.out.println("Something fk with transfer");
            return;
        }
        MapleCharacter chr = MapleCharacter.ReconstructChr(transfer, c, false);
        c.setPlayer(chr);
        c.setAccID(chr.getAccountID());
        if (!c.CheckIPAddress()) { // Remote hack
            c.getSession().close();
            return;
        }
        final int state = c.getLoginState();
        boolean allowLogin = false;
        if (state == MapleClient.LOGIN_SERVER_TRANSITION || state == MapleClient.CHANGE_CHANNEL || state == MapleClient.LOGIN_NOT_LOGIN) {
            allowLogin = !World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()));
        }
        if (!allowLogin) {
            c.setPlayer(null);
            c.getSession().close();
            return;
        }
        c.updateLoginState(MapleClient.LOGIN_LOGGED, c.getSessionIPAddress());
        CashShopServer.getPlayerStorage().addPlayer(chr);
        c.sendPacket(MTSCSPacket.warpCS(c));
        c.sendPacket(MTSCSPacket.warpCSInfo(c));
        CSUpdate(c);
    }

    public static void CSUpdate(final MapleClient c) {
        c.sendPacket(MTSCSPacket.getCSGifts(c));
        doCSPackets(c);
        c.sendPacket(MTSCSPacket.sendWishList(c.getPlayer(), false));
    }

    public static void CouponCode(final String code, final MapleClient c) {
        if (code.length() <= 0) {
            return;
        }
        Triple<Boolean, Integer, Integer> info = null;
        try {
            info = MapleCharacterUtil.getNXCodeInfo(code);
        } catch (SQLException e) {
        }

        if (info != null && info.left) {
            int type = info.mid, item = info.right;
            try {
                MapleCharacterUtil.setNXCodeUsed(c.getPlayer().getName(), code);
            } catch (SQLException e) {
            }
            /*
             * Explanation of type!
             * Basically, this makes coupon codes do
             * different things!
             *
             * Type 1: A-Cash,
             * Type 2: Maple Points
             * Type 3: Item.. use SN
             * Type 4: Mesos
             */
            Map<Integer, Item> itemz = new HashMap<>();
            int maplePoints = 0, mesos = 0;
            switch (type) {
                case 1:
                case 2:
                    c.getPlayer().modifyCSPoints(type, item, false);
                    maplePoints = item;
                    break;
                case 3:
                    CashItemInfo itez = CashItemFactory.getInstance().getItem(item);
                    if (itez == null) {
                        c.sendPacket(MTSCSPacket.sendCSFail(0));
                        return;
                    }
                    byte slot = MapleInventoryManipulator.addId(c, itez.getId(), (short) 1, "", "Cash shop: coupon code" + " on " + FileoutputUtil.CurrentReadable_Date());
                    if (slot <= -1) {
                        c.sendPacket(MTSCSPacket.sendCSFail(0));
                        return;
                    } else {
                        itemz.put(item, c.getPlayer().getInventory(GameConstants.getInventoryType(item)).getItem(slot));
                    }
                    break;
                case 4:
                    c.getPlayer().gainMeso(item, false);
                    mesos = item;
                    break;
            }
            c.sendPacket(MTSCSPacket.showCouponRedeemedItem(itemz, mesos, maplePoints, c));
        } else {
            c.sendPacket(MTSCSPacket.sendCSFail(info == null ? 0xA7 : 0xA5)); //A1, 9F
        }
    }

    public static void BuyCashItem(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final int action = slea.readByte();
        if (action == 0) {
            slea.skip(2);
            CouponCode(slea.readMapleAsciiString(), c);
        } else if (action == 3) {
            slea.skip(1);
            final int toCharge = slea.readByte()+1;
            final int sn = slea.readInt();
            System.out.println(sn);
            final CashItemInfo item = CashItemFactory.getInstance().getItem(sn);

            if (item != null && chr.getCSPoints(toCharge) >= item.getPrice()) {
                if (!item.genderEquals(c.getPlayer().getGender())) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0xA6));
                    doCSPackets(c);
                    return;
                } else if (c.getPlayer().getCashInventory().getItemsSize() >= 100) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0xB1));
                    doCSPackets(c);
                    return;
                }
                for (int i : GameConstants.cashBlock) {
                    if (item.getId() == i) {
                        //   c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item.getWorldId()));
                        doCSPackets(c);
                        return;
                    }
                }
                chr.modifyCSPoints(toCharge, -item.getPrice(), false);
                Item itemz = chr.getCashInventory().toItem(item);
                if (itemz != null && itemz.getUniqueId() > 0 && itemz.getItemId() == item.getId() && itemz.getQuantity() == item.getCount()) {
                    chr.getCashInventory().addToInventory(itemz);
                    //c.sendPacket(MTSCSPacket.confirmToCSInventory(itemz, c.getAccID(), item.getSN()));
                    c.sendPacket(MTSCSPacket.showBoughtCSItem(itemz, item.getSN(), c.getAccID()));
                } else {
                    c.sendPacket(MTSCSPacket.sendCSFail(0));
                }
            } else {
                c.sendPacket(MTSCSPacket.sendCSFail(0));
            }
        } else if (action == 4 || action == 34) { //gift, package
            slea.readMapleAsciiString(); // pic
            final CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
            if (action == 4) {
                slea.skip(1);
            }
            String partnerName = slea.readMapleAsciiString();
            String msg = slea.readMapleAsciiString();
            if (item == null || c.getPlayer().getCSPoints(1) < item.getPrice() || msg.length() > 73 || msg.length() < 1) { //dont want packet editors gifting random stuff =P
                c.sendPacket(MTSCSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            Triple<Integer, Integer, Integer> info = MapleCharacterUtil.getInfoByName(partnerName, c.getPlayer().getWorld());
            if (info == null || info.getLeft().intValue() <= 0 || info.getLeft().intValue() == c.getPlayer().getId() || info.getMid().intValue() == c.getAccID()) {
                c.sendPacket(MTSCSPacket.sendCSFail(0xA2)); //9E v75
                doCSPackets(c);
                return;
            } else if (!item.genderEquals(info.getRight().intValue())) {
                c.sendPacket(MTSCSPacket.sendCSFail(0xA3));
                doCSPackets(c);
                return;
            } else {
                for (int i : GameConstants.cashBlock) {
                    if (item.getId() == i) {
                        //   c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item.getWorldId()));
                        doCSPackets(c);
                        return;
                    }
                }
                c.getPlayer().getCashInventory().gift(info.getLeft().intValue(), c.getPlayer().getName(), msg, item.getSN(), MapleInventoryIdentifier.getInstance());
                c.getPlayer().modifyCSPoints(1, -item.getPrice(), false);
                c.sendPacket(MTSCSPacket.sendGift(item.getPrice(), item.getId(), item.getCount(), partnerName, action == 34));
            }
        } else if (action == 5) { // Wishlist
            chr.clearWishlist();
            if (slea.available() < 40) {
                c.sendPacket(MTSCSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            int[] wishlist = new int[10];
            for (int i = 0; i < 10; i++) {
                wishlist[i] = slea.readInt();
            }
            chr.setWishlist(wishlist);
            c.sendPacket(MTSCSPacket.sendWishList(chr, true));

        } else if (action == 6) { // Increase inv
            slea.skip(1);
            final int toCharge = GameConstants.GMS ? slea.readInt() : 1;
            final boolean coupon = slea.readByte() > 0;
            if (coupon) {
                final MapleInventoryType type = getInventoryType(slea.readInt());
                if (chr.getCSPoints(toCharge) >= (GameConstants.GMS ? 6000 : 12000) && chr.getInventory(type).getSlotLimit() < 89) {
                    chr.modifyCSPoints(toCharge, (GameConstants.GMS ? -6000 : -12000), false);
                    chr.getInventory(type).addSlot((byte) 8);
                    chr.dropMessage(1, "Slots has been increased to " + chr.getInventory(type).getSlotLimit());
                } else {
                    c.sendPacket(MTSCSPacket.sendCSFail(0xA4));
                }
            } else {
                final MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());
                if (chr.getCSPoints(toCharge) >= (GameConstants.GMS ? 4000 : 8000) && chr.getInventory(type).getSlotLimit() < 93) {
                    chr.modifyCSPoints(toCharge, (GameConstants.GMS ? -4000 : -8000), false);
                    chr.getInventory(type).addSlot((byte) 4);
                    chr.dropMessage(1, "Slots has been increased to " + chr.getInventory(type).getSlotLimit());
                } else {
                    c.sendPacket(MTSCSPacket.sendCSFail(0xA4));
                }
            }

        } else if (action == 7) { // Increase slot space
            slea.skip(1);
            final int toCharge = GameConstants.GMS ? slea.readInt() : 1;
            final int coupon = slea.readByte() > 0 ? 2 : 1;
            if (chr.getCSPoints(toCharge) >= (GameConstants.GMS ? 4000 : 8000) * coupon && chr.getStorage().getSlots() < (49 - (4 * coupon))) {
                chr.modifyCSPoints(toCharge, (GameConstants.GMS ? -4000 : -8000) * coupon, false);
                chr.getStorage().increaseSlots((byte) (4 * coupon));
                chr.getStorage().saveToDB();
                chr.dropMessage(1, "Storage slots increased to: " + chr.getStorage().getSlots());
            } else {
                c.sendPacket(MTSCSPacket.sendCSFail(0xA4));
            }
        } else if (action == 8) { //...9 = pendant slot expansion
            slea.skip(1);
            final int toCharge = GameConstants.GMS ? slea.readInt() : 1;
            CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
            int slots = 15;
            if (item == null || c.getPlayer().getCSPoints(toCharge) < item.getPrice() || slots > 15 || item.getId() != 5430000) {
                c.sendPacket(MTSCSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            // if (c.gainCharacterSlot()) {
            //   c.getPlayer().modifyCSPoints(toCharge, -item.getPrice(), false);
            // chr.dropMessage(1, "Character slots increased to: " + (slots + 1));
            //} else {
            c.sendPacket(MTSCSPacket.sendCSFail(0));
            //}
        } else if (action == 9 || action == 10) { //...10 = pendant slot expansion
            //Data: 00 01 00 00 00 DC FE FD 02
            slea.readByte(); //Action is short?
            slea.readInt(); //always 1 - No Idea
            final int sn = slea.readInt();
            CashItemInfo item = CashItemFactory.getInstance().getItem(sn);
            if (item == null || c.getPlayer().getCSPoints(1) < item.getPrice() || item.getId() / 10000 != 555) {
                c.sendPacket(MTSCSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            }
            MapleQuestStatus marr = c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT));
            if (marr != null && marr.getCustomData() != null && Long.parseLong(marr.getCustomData()) >= System.currentTimeMillis()) {
                c.sendPacket(MTSCSPacket.sendCSFail(0));
            } else {
                c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.PENDANT_SLOT)).setCustomData(String.valueOf(System.currentTimeMillis() + ((long) item.getPeriod() * 24 * 60 * 60000)));
                c.getPlayer().modifyCSPoints(1, -item.getPrice(), false);
                chr.dropMessage(1, "Additional pendant slot gained.");
            }
        } else if (action == 14) { //get item from csinventory
            //uniqueid, 00 01 01 00, type->position(short)
            Item item = c.getPlayer().getCashInventory().findByCashId((int) slea.readLong());
            if (item != null && item.getQuantity() > 0 && MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                Item item_ = item.copy();
                short pos = MapleInventoryManipulator.addbyItem(c, item_, true);
                if (pos >= 0) {
                    if (item_.getPet() != null) {
                        item_.getPet().setInventoryPosition(pos);
                    }
                    c.getPlayer().getCashInventory().removeFromInventory(item);
                    c.sendPacket(MTSCSPacket.confirmFromCSInventory(item_, pos));
                } else {
                    c.sendPacket(MTSCSPacket.sendCSFail(0xB1));
                }
            } else {
                c.sendPacket(MTSCSPacket.sendCSFail(0xB1));
            }
        } else if (action == 15) { //put item in cash inventory
            int uniqueid = (int) slea.readLong();
            MapleInventoryType type = MapleInventoryType.getByType(slea.readByte());
            Item item = c.getPlayer().getInventory(type).findByUniqueId(uniqueid);
            if (item != null && item.getQuantity() > 0 && item.getUniqueId() > 0 && c.getPlayer().getCashInventory().getItemsSize() < 100) {
                Item item_ = item.copy();
                MapleInventoryManipulator.removeFromSlot(c, type, item.getPosition(), item.getQuantity(), false);
                item_.setPosition((byte) 0);
                c.getPlayer().getCashInventory().addToInventory(item_);
                //warning: this d/cs
                c.sendPacket(MTSCSPacket.confirmToCSInventory(item, c.getAccID(), CashItemFactory.getInstance().getItemSN(item.getItemId())));
            } else {
                c.sendPacket(MTSCSPacket.sendCSFail(0xB1));
            }
        } else if (action == 32 || action == 38) { //38 = friendship, 32 = crush
            //c.sendPacket(MTSCSPacket.sendCSFail(0));
            slea.readMapleAsciiString(); // as13
            final int toCharge = slea.readInt();
            final CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
            final String partnerName = slea.readMapleAsciiString();
            final String msg = slea.readMapleAsciiString();
            if (item == null || !GameConstants.isEffectRing(item.getId()) || c.getPlayer().getCSPoints(toCharge) < item.getPrice() || msg.length() > 73 || msg.length() < 1) {
                c.sendPacket(MTSCSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            } else if (!item.genderEquals(c.getPlayer().getGender())) {
                c.sendPacket(MTSCSPacket.sendCSFail(0xA6));
                doCSPackets(c);
                return;
            } else if (c.getPlayer().getCashInventory().getItemsSize() >= 100) {
                c.sendPacket(MTSCSPacket.sendCSFail(0xB1));
                doCSPackets(c);
                return;
            }
            for (int i : GameConstants.cashBlock) { //just incase hacker
                if (item.getId() == i) {
                    //   c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item.getWorldId()));
                    doCSPackets(c);
                    return;
                }
            }
            Triple<Integer, Integer, Integer> info = MapleCharacterUtil.getInfoByName(partnerName, c.getPlayer().getWorld());
            if (info == null || info.getLeft().intValue() <= 0 || info.getLeft().intValue() == c.getPlayer().getId()) {
                c.sendPacket(MTSCSPacket.sendCSFail(0xB4)); //9E v75
                doCSPackets(c);
                return;
            } else if (info.getMid().intValue() == c.getAccID()) {
                c.sendPacket(MTSCSPacket.sendCSFail(0xA3)); //9D v75
                doCSPackets(c);
                return;
            } else {
                if (info.getRight().intValue() == c.getPlayer().getGender() && action == 30) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0xA1)); //9B v75
                    doCSPackets(c);
                    return;
                }

                int err = MapleRing.createRing(item.getId(), c.getPlayer(), partnerName, msg, info.getLeft().intValue(), item.getSN());

                if (err != 1) {
                    c.sendPacket(MTSCSPacket.sendCSFail(0)); //9E v75
                    doCSPackets(c);
                    return;
                }
                c.getPlayer().modifyCSPoints(toCharge, -item.getPrice(), false);
            }
        } else if (action == 33) {
            slea.skip(1);
           // final int toCharge = slea.readByte();
            final CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
            List<Integer> ccc = null;
            if (item != null) {
                ccc = CashItemFactory.getInstance().getPackageItems(item.getId());
            }
            if (item == null || ccc == null /*|| c.getPlayer().getCSPoints(toCharge) < item.getPrice()*/) {
                c.sendPacket(MTSCSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            } else if (!item.genderEquals(c.getPlayer().getGender())) {
                c.sendPacket(MTSCSPacket.sendCSFail(0xA6));
                doCSPackets(c);
                return;
            } else if (c.getPlayer().getCashInventory().getItemsSize() >= (100 - ccc.size())) {
                c.sendPacket(MTSCSPacket.sendCSFail(0xB1));
                doCSPackets(c);
                return;
            }
            for (int iz : GameConstants.cashBlock) {
                if (item.getId() == iz) {
                    //    c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item.getWorldId()));
                    doCSPackets(c);
                    return;
                }
            }
            Map<Integer, Item> ccz = new HashMap<>();
            for (int i : ccc) {
                final CashItemInfo cii = CashItemFactory.getInstance().getSimpleItem(i);
                if (cii == null) {
                    continue;
                }
                Item itemz = c.getPlayer().getCashInventory().toItem(cii);
                if (itemz == null || itemz.getUniqueId() <= 0) {
                    continue;
                }
                for (int iz : GameConstants.cashBlock) {
                    if (itemz.getItemId() == iz) {
                        continue;
                    }
                }
                ccz.put(i, itemz);
                c.getPlayer().getCashInventory().addToInventory(itemz);
            }
            chr.modifyCSPoints(1, -item.getPrice(), false);
            c.sendPacket(MTSCSPacket.showBoughtCSPackage(ccz, c.getAccID()));

        } else if (action == 35) {
            final CashItemInfo item = CashItemFactory.getInstance().getItem(slea.readInt());
            if (item == null || !MapleItemInformationProvider.getInstance().isQuestItem(item.getId())) {
                c.sendPacket(MTSCSPacket.sendCSFail(0));
                doCSPackets(c);
                return;
            } else if (c.getPlayer().getMeso() < item.getPrice()) {
                c.sendPacket(MTSCSPacket.sendCSFail(0xB8));
                doCSPackets(c);
                return;
            } else if (c.getPlayer().getInventory(GameConstants.getInventoryType(item.getId())).getNextFreeSlot() < 0) {
                c.sendPacket(MTSCSPacket.sendCSFail(0xB1));
                doCSPackets(c);
                return;
            }
            for (int iz : GameConstants.cashBlock) {
                if (item.getId() == iz) {
                    //   c.getPlayer().dropMessage(1, GameConstants.getCashBlockedMsg(item.getWorldId()));
                    doCSPackets(c);
                    return;
                }
            }
            byte pos = MapleInventoryManipulator.addId(c, item.getId(), (short) item.getCount(), null, "Cash shop: quest item" + " on " + FileoutputUtil.CurrentReadable_Date());
            if (pos < 0) {
                c.sendPacket(MTSCSPacket.sendCSFail(0xB1));
                doCSPackets(c);
                return;
            }
            chr.gainMeso(-item.getPrice(), false);
            c.sendPacket(MTSCSPacket.showBoughtCSQuestItem(item.getPrice(), (short) item.getCount(), pos, item.getId()));
        } else if (action == 48) {
            c.sendPacket(MTSCSPacket.updatePurchaseRecord());
        } else if (action == 10) { // Open random box.
            c.getPlayer().dropMessage(1, "Sorry, this feature is not available.");

        } else if (action == 91) { // Open random box.
            final int uniqueid = (int) slea.readLong();

            //c.sendPacket(MTSCSPacket.sendRandomBox(uniqueid, new Item(1302000, (short) 1, (short) 1, (short) 0, 10), (short) 0));
        } else if(action == 46){
            //Unknow TODO
        } else {
            System.out.println("New Action: " + action + " Remaining: " + slea.toString());
            c.sendPacket(MTSCSPacket.sendCSFail(0));
        }
        doCSPackets(c);
    }

    private static MapleInventoryType getInventoryType(final int id) {
        switch (id) {
            case 50200093:
                return MapleInventoryType.EQUIP;
            case 50200094:
                return MapleInventoryType.USE;
            case 50200197:
                return MapleInventoryType.SETUP;
            case 50200095:
                return MapleInventoryType.ETC;
            default:
                return MapleInventoryType.UNDEFINED;
        }
    }

    public static void doCSPackets(MapleClient c) {
        c.sendPacket(MTSCSPacket.getCSInventory(c));
        c.sendPacket(MTSCSPacket.showNXMapleTokens(c.getPlayer()));
        c.sendPacket(MTSCSPacket.enableCSUse());
        c.getPlayer().getCashInventory().checkExpire(c);
    }
}
