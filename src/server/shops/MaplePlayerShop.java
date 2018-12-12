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
package server.shops;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.ItemFlag;
import constants.ServerConstants;
import server.MapleInventoryManipulator;
import tools.packet.PlayerShopPacket;

import java.util.ArrayList;
import java.util.List;

public class MaplePlayerShop extends AbstractPlayerStore {

    private int boughtnumber = 0;
    private List<String> bannedList = new ArrayList<>();

    public MaplePlayerShop(MapleCharacter owner, int itemId, String desc) {
        super(owner, itemId, desc, "", (itemId >= 4080000 && itemId <= 4080011 ? 3 : itemId == 4080100 ? 3 : 6));
    }

    @Override
    public void buy(MapleClient c, int item, short quantity) {
        MaplePlayerShopItem pItem = items.get(item);
        if (pItem.bundles > 0) {
            Item newItem = pItem.item.copy();
            newItem.setQuantity((short) (quantity * newItem.getQuantity()));
            short flag = newItem.getFlag();

            if (ItemFlag.KARMA_EQ.check(flag)) {
                newItem.setFlag((short) (flag - ItemFlag.KARMA_EQ.getValue()));
            } else if (ItemFlag.KARMA_USE.check(flag)) {
                newItem.setFlag((short) (flag - ItemFlag.KARMA_USE.getValue()));
            }
            final int gainmeso = pItem.price * quantity;
            if ((ServerConstants.MerchantsUseCurrency ? c.getPlayer().getCurrency() : c.getPlayer().getMeso()) >= gainmeso) {
                if (ServerConstants.MerchantsUseCurrency) {
                    synchronized (c.getPlayer()) {
                        if (getMCOwner().getCurrency() + gainmeso > 0 && MapleInventoryManipulator.checkSpace(c, newItem.getItemId(), newItem.getQuantity(), newItem.getOwner()) && MapleInventoryManipulator.addFromDrop(c, newItem, false)) {
                            pItem.bundles -= quantity;
                            bought.add(new BoughtItem(newItem.getItemId(), quantity, gainmeso, c.getPlayer().getName()));
                            c.getPlayer().gainCurrency(-gainmeso, false);
                            getMCOwner().gainCurrency(gainmeso, false);
                            if (pItem.bundles < 1) {
                                if (++boughtnumber == items.size()) {
                                    getMCOwner().setPlayerShop(null);
                                    getMCOwner().getMap().broadcastMessage(PlayerShopPacket.removeCharBox(getMCOwner()));
                                    this.removeVisitors(false);
                                    getMCOwner().dropMessage(1, "The items are out of stock.");
                                }
                            }
                        } else {
                            c.getPlayer().dropMessage(1, "Your inventory is full.");
                        }
                    }
                } else {
                    synchronized (c.getPlayer()) {
                        if (getMCOwner().getMeso() + gainmeso > 0 && MapleInventoryManipulator.checkSpace(c, newItem.getItemId(), newItem.getQuantity(), newItem.getOwner()) && MapleInventoryManipulator.addFromDrop(c, newItem, false)) {
                            pItem.bundles -= quantity;
                            bought.add(new BoughtItem(newItem.getItemId(), quantity, gainmeso, c.getPlayer().getName()));
                            c.getPlayer().gainMeso(-gainmeso, false);
                            getMCOwner().gainMeso(gainmeso, false);
                            if (pItem.bundles < 1) {
                                if (++boughtnumber == items.size()) {
                                    getMCOwner().setPlayerShop(null);
                                    getMCOwner().getMap().broadcastMessage(PlayerShopPacket.removeCharBox(getMCOwner()));
                                    this.removeVisitors(false);
                                    getMCOwner().dropMessage(1, "The items are out of stock.");
                                }
                            }
                        } else {
                            c.getPlayer().dropMessage(1, "Your inventory is full.");
                        }
                    }
                }
            } else {
                c.getPlayer().dropMessage(1, "You do not have enough " + (ServerConstants.MerchantsUseCurrency ? "Munny" : "mesos") + " to complete the transaction.");
            }
        }
    }

    public void removeVisitors(boolean closedShop) {
        try {
            for (int i = 0; i < getMaxSize(); i++) {
                if (getVisitor(i) != null) {
                    getVisitor(i).dropMessage(1, closedShop ? "The shop is closed." : "The items are out of stock.");
                    removeVisitor(getVisitor(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getMCOwner() != null) {
            removeVisitor(getMCOwner());
        }
    }

    @Override
    public byte getShopType() {
        return IMaplePlayerShop.PLAYER_SHOP;
    }

    @Override
    public void closeShop(boolean saveItems, boolean remove) {
        MapleCharacter owner = getMCOwner();
        this.removeVisitors(true);
        getMap().removeMapObject(this);
        for (MaplePlayerShopItem itemsse : getItems()) {
            if (itemsse.bundles > 0) {
                Item newItem = itemsse.item.copy();
                newItem.setQuantity((short) (itemsse.bundles * newItem.getQuantity()));
                if (MapleInventoryManipulator.addFromDrop(owner.getClient(), newItem, false)) {
                    itemsse.bundles = 0;
                } else {
                    saveItems(); //O_o
                    break;
                }
            }
        }
        owner.setPlayerShop(null);
        getMCOwner().getMap().broadcastMessage(PlayerShopPacket.removeCharBox(getMCOwner()));
        // update();
        // getMCOwner().getClient().sendPacket(PlayerShopPacket.shopErrorMessage(3, 1));
    }

    public void send(MapleClient c) {
        if (getMCOwner() == null) {
            closeShop(false, false);
            return;
        }
        c.sendPacket(PlayerShopPacket.getPlayerStore(c.getPlayer(), true));
    }

    public void banPlayer(String name) {
        if (!bannedList.contains(name)) {
            bannedList.add(name);
        }
        for (int i = 0; i < getMaxSize(); i++) {
            MapleCharacter chr = getVisitor(i);
            if (chr.getName().equals(name)) {
                chr.getClient().sendPacket(PlayerShopPacket.shopErrorMessage(5, 1));
                chr.setPlayerShop(null);
                removeVisitor(chr);
            }
        }
    }

    public boolean isBanned(String name) {
        if (bannedList.contains(name)) {
            return true;
        }
        return false;
    }
}
