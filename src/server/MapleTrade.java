package server;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import client.messages.CommandProcessor;
import constants.GameConstants;
import constants.ServerConstants;
import tools.packet.CField.InteractionPacket;
import tools.packet.CWvsContext;
import tools.packet.PlayerShopPacket;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MapleTrade {

    private final List<Item> items = new LinkedList<>();
    private final WeakReference<MapleCharacter> chr;
    private final byte tradingslot;
    private MapleTrade partner = null;
    private List<Item> exchangeItems;
    private int meso = 0, exchangeMeso = 0;
    private boolean locked = false, inTrade = false;

    public MapleTrade(final byte tradingslot, final MapleCharacter chr) {
        this.tradingslot = tradingslot;
        this.chr = new WeakReference<>(chr);
    }

    private static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM HH:mm:ss z");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static void completeTrade(final MapleCharacter c) {
        final MapleTrade local = c.getTrade();
        final MapleTrade partner = local.getPartner();

        if (partner == null || local.locked) {
            return;
        }
        local.locked = true; // Locking the trade
        partner.getChr().getClient().sendPacket(InteractionPacket.getTradeConfirmation());

        partner.exchangeItems = new LinkedList<>(local.items); // Copy this to partner's trade since it's alreadt accepted
        partner.exchangeMeso = local.meso; // Copy this to partner's trade since it's alreadt accepted

        if (partner.isLocked()) { // Both locked
            int lz = local.check(), lz2 = partner.check();
            if (lz == 0 && lz2 == 0) {
                local.CompleteTrade();
                partner.CompleteTrade();
            } else {
                // NOTE : IF accepted = other party but inventory is full, the item is lost.
                partner.cancel(partner.getChr().getClient(), partner.getChr(), lz == 0 ? lz2 : lz);
                local.cancel(c.getClient(), c, lz == 0 ? lz2 : lz);
            }
            partner.getChr().setTrade(null);
            c.setTrade(null);
        }
    }

    public static void cancelTrade(final MapleTrade Localtrade, final MapleClient c, final MapleCharacter chr) {
        Localtrade.cancel(c, chr);

        final MapleTrade partner = Localtrade.getPartner();
        if (partner != null && partner.getChr() != null) {
            partner.cancel(partner.getChr().getClient(), partner.getChr());
            partner.getChr().setTrade(null);
        }
        chr.setTrade(null);
    }

    public static void startTrade(final MapleCharacter c) {
        if (c.getTrade() == null) {
            c.setTrade(new MapleTrade((byte) 0, c));
            c.getClient().sendPacket(InteractionPacket.getTradeStart(c.getClient(), c.getTrade(), (byte) 0));
        } else {
            c.getClient().sendPacket(CWvsContext.broadcastMsg(5, "You are already in a trade"));
        }
    }

    public static void inviteTrade(final MapleCharacter c1, final MapleCharacter c2) {
        if (c1 == null || c1.getTrade() == null) {
            return;
        }
        if (c2 != null && c2.getTrade() == null) {
            c2.setTrade(new MapleTrade((byte) 1, c2));
            c2.getTrade().setPartner(c1.getTrade());
            c1.getTrade().setPartner(c2.getTrade());
            c2.getClient().sendPacket(InteractionPacket.getTradeInvite(c1));
        } else {
            c1.getClient().sendPacket(CWvsContext.broadcastMsg(5, "The other player is already trading with someone else."));
            cancelTrade(c1.getTrade(), c1.getClient(), c1);
        }
    }

    public static void visitTrade(final MapleCharacter c1, final MapleCharacter c2) {
        if (c2 != null && c1.getTrade() != null && c1.getTrade().getPartner() == c2.getTrade() && c2.getTrade() != null && c2.getTrade().getPartner() == c1.getTrade()) {
            // We don't need to check for map here as the user is found via MapleMap.getCharacterById()
            c1.getTrade().inTrade = true;
            c2.getClient().sendPacket(PlayerShopPacket.shopVisitorAdd(c1, 1));
            c1.getClient().sendPacket(InteractionPacket.getTradeStart(c1.getClient(), c1.getTrade(), (byte) 1));
            c1.dropMessage(-2, "系統 : 請使用 @help 來觀看相關指令");
            c2.dropMessage(-2, "系統 : 請使用 @help 來觀看相關指令");
        } else {
            c1.getClient().sendPacket(CWvsContext.broadcastMsg(5, "The other player has already closed the trade"));
        }
    }

    public static void declineTrade(final MapleCharacter c) {
        final MapleTrade trade = c.getTrade();
        if (trade != null) {
            if (trade.getPartner() != null) {
                MapleCharacter other = trade.getPartner().getChr();
                if (other != null && other.getTrade() != null) {
                    other.getTrade().cancel(other.getClient(), other);
                    other.setTrade(null);
                    other.dropMessage(5, c.getName() + " has declined your trade request");
                }
            }
            trade.cancel(c.getClient(), c);
            c.setTrade(null);
        }
    }

    public final void CompleteTrade() {
        if (exchangeItems != null) { // just to be on the safe side...
            List<Item> itemz = new LinkedList<>(exchangeItems);
            for (final Item item : itemz) {
                short flag = item.getFlag();

                if (ItemFlag.KARMA_EQ.check(flag)) {
                    item.setFlag((short) (flag - ItemFlag.KARMA_EQ.getValue()));
                } else if (ItemFlag.KARMA_USE.check(flag)) {
                    item.setFlag((short) (flag - ItemFlag.KARMA_USE.getValue()));
                }
                MapleInventoryManipulator.addFromDrop(chr.get().getClient(), item, false);
            }
            exchangeItems.clear();
        }
        if (exchangeMeso > 0) {
            chr.get().gainMeso(exchangeMeso - GameConstants.getTaxAmount(exchangeMeso), false, false);
        }
        exchangeMeso = 0;


        chr.get().getClient().sendPacket(InteractionPacket.TradeMessage(tradingslot, (byte) 0x07));
    }

    public final void cancel(final MapleClient c, final MapleCharacter chr) {
        cancel(c, chr, 0);
    }

    public final void cancel(final MapleClient c, final MapleCharacter chr, final int unsuccessful) {
        if (items != null) { // just to be on the safe side...
            List<Item> itemz = new LinkedList<>(items);
            for (final Item item : itemz) {
                MapleInventoryManipulator.addFromDrop(c, item, false);
            }
            items.clear();
        }
        if (meso > 0) {
            chr.gainMeso(meso, false, false);
        }
        meso = 0;


        c.sendPacket(InteractionPacket.getTradeCancel(tradingslot, unsuccessful));
    }

    public final boolean isLocked() {
        return locked;
    }

    public final void setMeso(final int meso) {
        if (locked || partner == null || meso <= 0 || this.meso + meso <= 0) {
            return;
        }
        if (chr.get().getMeso() >= meso) {
            chr.get().gainMeso(-meso, false, false);
            this.meso += meso;
            chr.get().getClient().sendPacket(InteractionPacket.getTradeMesoSet((byte) 0, this.meso));
            if (partner != null) {
                partner.getChr().getClient().sendPacket(InteractionPacket.getTradeMesoSet((byte) 1, this.meso));
            }
        }
    }

    public final void addItem(final Item item) {
        if (locked || partner == null) {
            return;
        }
        items.add(item);
        chr.get().getClient().sendPacket(InteractionPacket.getTradeItemAdd((byte) 0, item));
        if (partner != null) {
            partner.getChr().getClient().sendPacket(InteractionPacket.getTradeItemAdd((byte) 1, item));
        }

    }

    public final void chat(String message) {
        MapleCharacter player = chr.get();
        if(player != null){
            message = WordFilter.illegalArrayCheck(message, chr.get());
            if (player.getMap().getId() == GameConstants.JAIL) {
                player.dropMessage(5, "You're in jail, herp derp.");
                player.getClient().sendPacket(CWvsContext.enableActions());
            } else if (player.isMuted() || (player.getMap().getMuted())) {
                player.dropMessage(5, player.isMuted() ? "You are Muted, therefore you are unable to talk. " : "The map is Muted, therefore you are unable to talk.");
                player.getClient().sendPacket(CWvsContext.enableActions());
            }
        }
        if (!CommandProcessor.processCommand(chr.get().getClient(), message, ServerConstants.CommandType.TRADE)) {
            chr.get().dropMessage(-2, chr.get().getName() + " : " + message);
            if (partner != null) {
                partner.getChr().getClient().sendPacket(PlayerShopPacket.shopChat(chr.get().getName() + " : " + message, 1));
            }
        }
    }

    public final void chatAuto(String message) {
        message = WordFilter.illegalArrayCheck(message, chr.get());
        if (chr.get().getMap().getId() == GameConstants.JAIL) {
            chr.get().dropMessage(5, "You're in jail, herp derp.");
            chr.get().getClient().sendPacket(CWvsContext.enableActions());
            return;
        }
        if (chr.get().isMuted() || (chr.get().getMap().getMuted())) {
            chr.get().dropMessage(5, chr.get().isMuted() ? "You are Muted, therefore you are unable to talk. " : "The map is Muted, therefore you are unable to talk.");
            chr.get().getClient().sendPacket(CWvsContext.enableActions());
            return;
        }
        chr.get().dropMessage(-2, message);
        if (partner != null) {
            partner.getChr().getClient().sendPacket(PlayerShopPacket.shopChat(message, 1));
        }
    }

    public final MapleTrade getPartner() {
        return partner;
    }

    public final void setPartner(final MapleTrade partner) {
        if (locked) {
            return;
        }
        this.partner = partner;
    }

    public final MapleCharacter getChr() {
        return chr.get();
    }

    public final int getNextTargetSlot() {
        if (items.size() >= 9) {
            return -1;
        }
        int ret = 1; //first slot
        for (Item item : items) {
            if (item.getPosition() == ret) {
                ret++;
            }
        }
        return ret;
    }

    public boolean inTrade() {
        return inTrade;
    }

    public final boolean setItems(final MapleClient c, final Item item, byte targetSlot, final int quantity) {
        int target = getNextTargetSlot();
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (partner == null || target == -1 || GameConstants.isPet(item.getItemId()) || isLocked() || (GameConstants.getInventoryType(item.getItemId()) == MapleInventoryType.EQUIP && quantity != 1)) {
            return false;
        }
        final short flag = item.getFlag();
        if (ItemFlag.UNTRADEABLE.check(flag) || ItemFlag.LOCK.check(flag)) {
            c.sendPacket(CWvsContext.enableActions());
            return false;
        }
        if (ii.isAccountShared(item.getItemId())) {
            if (!(ItemFlag.KARMA_EQ.check(flag) || ItemFlag.KARMA_USE.check(flag))) {
                c.sendPacket(CWvsContext.enableActions());
                return false;
            }
        }
        Item tradeItem = item.copy();
        if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
            tradeItem.setQuantity(item.getQuantity());
            MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(item.getItemId()), item.getPosition(), item.getQuantity(), true);
            c.getPlayer().saveToDB(false, false);
        } else {
            tradeItem.setQuantity((short) quantity);
            MapleInventoryManipulator.removeFromSlot(c, GameConstants.getInventoryType(item.getItemId()), item.getPosition(), (short) quantity, true);
            c.getPlayer().saveToDB(false, false);
        }
        if (targetSlot < 0) {
            targetSlot = (byte) target;
        } else {
            for (Item itemz : items) {
                if (itemz.getPosition() == targetSlot) {
                    targetSlot = (byte) target;
                    break;
                }
            }
        }
        tradeItem.setPosition(targetSlot);
        addItem(tradeItem);
        c.getPlayer().saveToDB(false, false);
        return true;
    }

    private int check() { //0 = fine, 1 = invent space not, 2 = pickupRestricted
        if (chr.get().getMeso() + exchangeMeso < 0) {
            return 1;
        }

        if (exchangeItems != null) {
            // final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            byte eq = 0, use = 0, setup = 0, etc = 0, cash = 0;
            for (final Item item : exchangeItems) {
                switch (GameConstants.getInventoryType(item.getItemId())) {
                    case EQUIP:
                        eq++;
                        break;
                    case USE:
                        use++;
                        break;
                    case SETUP:
                        setup++;
                        break;
                    case ETC:
                        etc++;
                        break;
                    case CASH: // Not allowed, probably hacking
                        cash++;
                        break;
                }
                // if (ii.isPickupRestricted(item.getItemId()) && chr.get().haveItem(item.getItemId(), 1, true, true)) {
                //      return 2;
                // }
            }
            if (chr.get().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < eq || chr.get().getInventory(MapleInventoryType.USE).getNumFreeSlot() < use || chr.get().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < setup || chr.get().getInventory(MapleInventoryType.ETC).getNumFreeSlot() < etc || chr.get().getInventory(MapleInventoryType.CASH).getNumFreeSlot() < cash) {
                return 1;
            }
        }
        return 0;
    }
    public void ShowTradeInfo(){
        if (getPartner().items.toArray().length == 0){
            getChr().dropMessage(-2, "系統提示 : 對方尚未提供任何道具");
            return;
        }

        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        getChr().dropMessage(-2, "系統提示 : 以下為對方交易之道具:");
        for(Item i : getPartner().items){
            getChr().dropMessage(-2, ii.getName(i.getItemId()) + " x " + i.getQuantity());
        }
    }
}
