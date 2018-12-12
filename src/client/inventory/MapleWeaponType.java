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
package client.inventory;

public enum MapleWeaponType {

    沒有武器(1.43f, 20),
    弓(1.2f, 15),
    弩(1.35f, 15),
    拳套(1.75f, 15),
    手杖(1.3f, 15),
    短劍(1.3f, 20),
    單手斧(1.2f, 20),
    單手劍(1.2f, 20),
    單手棍(1.2f, 20),
    雙手斧(1.32f, 20),
    雙手劍(1.32f, 20),
    雙手棍(1.32f, 20),
    槍(1.49f, 20),
    矛(1.49f, 20),
    長杖(1.0f, 25),
    短杖(1.0f, 25),
    指虎(1.7f, 20),
    火槍(1.5f, 15),
    加農炮(1.35f, 15),
    雙弩槍(2.0f, 15), //beyond op
    MAGIC_ARROW(2.0f, 15),
    雙刀(1.3f, 20),
    未知(0.0f, 0),
    ;


    private final float damageMultiplier;
    private final int baseMastery;

    MapleWeaponType(final float maxDamageMultiplier, int baseMastery) {
        this.damageMultiplier = maxDamageMultiplier;
        this.baseMastery = baseMastery;
    }

    public final float getMaxDamageMultiplier() {
        return damageMultiplier;
    }

    public final int getBaseMastery() {
        return baseMastery;
    }
};
