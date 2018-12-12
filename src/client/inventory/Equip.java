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

import constants.GameConstants;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.StructItemOption;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Equip extends Item implements Serializable {

    public static final int ARMOR_RATIO = 350000;
    public static final int WEAPON_RATIO = 700000;
    //charm: -1 = has not been initialized yet, 0 = already been worn, >0 = has teh charm exp
    private byte upgradeSlots = 0, level = 0, vicioushammer = 0, enhance = 0;
    private short str = 0, dex = 0, _int = 0, luk = 0, hp = 0, mp = 0, watk = 0, matk = 0, wdef = 0, mdef = 0, acc = 0, avoid = 0, hands = 0, speed = 0, jump = 0, charmExp = 0, pvpDamage = 0;
    private int itemEXP = 0, durability = -1, incSkill = -1, potential1 = 0, potential2 = 0, potential3 = 0, potential4 = 0, potential5 = 0, socket1 = -1, socket2 = -1, socket3 = -1;
    private MapleRing ring = null;
    private MapleAndroid android = null;
    // EUS (Eric's doing something? Whoa!)
    private int equipLevel, equipExp, equipMSIUpgrades;
    public Equip(int id, short position, byte flag) {
        super(id, position, (short) 1, flag);
        this.equipLevel = 1;
        this.equipExp = 0;
        this.equipMSIUpgrades = 0;
    }

    public Equip(int id, short position, int uniqueid, short flag) {
        super(id, position, (short) 1, flag, uniqueid);
        this.equipLevel = 1;
        this.equipExp = 0;
        this.equipMSIUpgrades = 0;
    }

    @Override
    public Item copy() {
        Equip ret = new Equip(getItemId(), getPosition(), getUniqueId(), getFlag());
        ret.str = str;
        ret.dex = dex;
        ret._int = _int;
        ret.luk = luk;
        ret.hp = hp;
        ret.mp = mp;
        ret.matk = matk;
        ret.mdef = mdef;
        ret.watk = watk;
        ret.wdef = wdef;
        ret.acc = acc;
        ret.avoid = avoid;
        ret.hands = hands;
        ret.speed = speed;
        ret.jump = jump;
        ret.enhance = enhance;
        ret.upgradeSlots = upgradeSlots;
        ret.level = level;
        ret.itemEXP = itemEXP;
        ret.durability = durability;
        ret.vicioushammer = vicioushammer;
        ret.potential1 = potential1;
        ret.potential2 = potential2;
        ret.potential3 = potential3;
        ret.potential4 = potential4;
        ret.potential5 = potential5;
        ret.socket1 = socket1;
        ret.socket2 = socket2;
        ret.socket3 = socket3;
        ret.charmExp = charmExp;
        ret.pvpDamage = pvpDamage;
        ret.incSkill = incSkill;
        ret.equipLevel = equipLevel;
        ret.equipExp = equipExp;
        ret.equipMSIUpgrades = equipMSIUpgrades;
        ret.setGiftFrom(getGiftFrom());
        ret.setOwner(getOwner());
        ret.setQuantity(getQuantity());
        ret.setExpiration(getExpiration());
        return ret;
    }

    @Override
    public byte getType() {
        return 1;
    }

    public byte getUpgradeSlots() {
        return upgradeSlots;
    }

    public void setUpgradeSlots(byte upgradeSlots) {
        this.upgradeSlots = upgradeSlots;
    }

    public short getStr() {
        return str;
    }

    public void setStr(short str) {
        if (str < 0) {
            str = 0;
        }
        this.str = str;
    }

    public short getDex() {
        return dex;
    }

    public void setDex(short dex) {
        if (dex < 0) {
            dex = 0;
        }
        this.dex = dex;
    }

    public short getInt() {
        return _int;
    }

    public void setInt(short _int) {
        if (_int < 0) {
            _int = 0;
        }
        this._int = _int;
    }

    public short getLuk() {
        return luk;
    }

    public void setLuk(short luk) {
        if (luk < 0) {
            luk = 0;
        }
        this.luk = luk;
    }

    public short getHp() {
        return hp;
    }

    public void setHp(short hp) {
        if (hp < 0) {
            hp = 0;
        }
        this.hp = hp;
    }

    public short getMp() {
        return mp;
    }

    public void setMp(short mp) {
        if (mp < 0) {
            mp = 0;
        }
        this.mp = mp;
    }

    public void makeMSI(String owner) {
        this.setOwner(owner);
        this.level = 20;
        this.str = 32767;
        this.dex = 32767;
        this._int = 32767;
        this.luk = 32767;
        this.watk = 32767;
        this.matk = 32767;
        this.wdef = 32767;
        this.mdef = 32767;
        this.speed = 32767;
        this.jump = 32767;
        this.acc = 32767;
        this.avoid = 32767;
        this.hp = 32767;
        this.mp = 32767;
        this.potential1 = 60001;
        this.potential2 = 60001;
        this.potential3 = 60001;
        this.potential4 = 60001;
        this.potential5 = 60001;
        this.socket1 = 3064221;
        this.socket2 = 3064221;
        this.socket3 = 3064221;
        this.pvpDamage = 32767;
        this.enhance = 20; // maximum of 127(byte), but the highest enhancement should be 15.
    }

    public void makeProItem(String owner, short stat, boolean potential) {
        this.setOwner(owner);
        this.level = 20;
        this.str = stat;
        this.dex = stat;
        this._int = stat;
        this.luk = stat;
        this.watk = stat;
        this.matk = stat;
        this.wdef = stat;
        this.mdef = stat;
        this.speed = stat;
        this.jump = stat;
        this.acc = stat;
        this.avoid = stat;
        this.hp = stat;
        this.mp = stat;
        if (potential) {
            this.potential1 = 60001;
            this.potential2 = 60001;
            this.potential3 = 60001;
            this.potential4 = 60001;
            this.potential5 = 60001;
        }
    }

    public void makeProItem(String owner, short str, short dex, short _int, short luk, short watk, short matk_, short wdef, short mdef_, short speed, short jump, short acc, short avoid, short hp, short mp, boolean potential) {
        this.setOwner(owner);
        this.level = 20;
        this.str = str;
        this.dex = dex;
        this._int = _int;
        this.luk = luk;
        this.watk = watk;
        this.matk = matk_;
        this.wdef = wdef;
        this.mdef = mdef_;
        this.speed = speed;
        this.jump = jump;
        this.acc = acc;
        this.avoid = avoid;
        this.hp = hp;
        this.mp = mp;
        if (potential) {
            this.potential1 = 60001;
            this.potential2 = 60001;
            this.potential3 = 60001;
            this.potential4 = 60001;
            this.potential5 = 60001;
        }
    }

    public short getWatk() {
        return watk;
    }

    public void setWatk(short watk) {
        if (watk < 0) {
            watk = 0;
        }
        this.watk = watk;
    }

    public short getMatk() {
        return matk;
    }

    public void setMatk(short matk) {
        if (matk < 0) {
            matk = 0;
        }
        this.matk = matk;
    }

    public short getWdef() {
        return wdef;
    }

    public void setWdef(short wdef) {
        if (wdef < 0) {
            wdef = 0;
        }
        this.wdef = wdef;
    }

    public short getMdef() {
        return mdef;
    }

    public void setMdef(short mdef) {
        if (mdef < 0) {
            mdef = 0;
        }
        this.mdef = mdef;
    }

    public short getAcc() {
        return acc;
    }

    public void setAcc(short acc) {
        if (acc < 0) {
            acc = 0;
        }
        this.acc = acc;
    }

    public short getAvoid() {
        return avoid;
    }

    public void setAvoid(short avoid) {
        if (avoid < 0) {
            avoid = 0;
        }
        this.avoid = avoid;
    }

    public short getHands() {
        return hands;
    }

    public void setHands(short hands) {
        if (hands < 0) {
            hands = 0;
        }
        this.hands = hands;
    }

    public short getSpeed() {
        return speed;
    }

    public void setSpeed(short speed) {
        if (speed < 0) {
            speed = 0;
        }
        this.speed = speed;
    }

    public short getJump() {
        return jump;
    }

    public void setJump(short jump) {
        if (jump < 0) {
            jump = 0;
        }
        this.jump = jump;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public byte getViciousHammer() {
        return vicioushammer;
    }

    public void setViciousHammer(byte ham) {
        vicioushammer = ham;
    }

    public int getItemEXP() {
        return itemEXP;
    }

    public void setItemEXP(int itemEXP) {
        if (itemEXP < 0) {
            itemEXP = 0;
        }
        this.itemEXP = itemEXP;
    }

    public int getEquipExp() {
        if (itemEXP <= 0) {
            return 0;
        }
        //aproximate value
        if (GameConstants.isWeapon(getItemId())) {
            return itemEXP / WEAPON_RATIO;
        } else {
            return itemEXP / ARMOR_RATIO;
        }
    }

    public void setEquipExp(int exp) {
        equipExp = exp;
    }

    public int getEquipExpForLevel() {
        if (getEquipExp() <= 0) {
            return 0;
        }
        int expz = getEquipExp();
        for (int i = getBaseLevel(); i <= GameConstants.getMaxLevel(getItemId()); i++) {
            if (expz >= GameConstants.getExpForLevel(i, getItemId())) {
                expz -= GameConstants.getExpForLevel(i, getItemId());
            } else { //for 0, dont continue;
                break;
            }
        }
        return expz;
    }

    public int getExpPercentage() {
        if (getEquipLevel() < getBaseLevel() || getEquipLevel() > GameConstants.getMaxLevel(getItemId()) || GameConstants.getExpForLevel(getEquipLevel(), getItemId()) <= 0) {
            return 0;
        }
        return getEquipExpForLevel() * 100 / GameConstants.getExpForLevel(getEquipLevel(), getItemId());
    }

    public int getEquipLevel() {
        if (GameConstants.getMaxLevel(getItemId()) <= 0) {
            return 0;
        } else if (getEquipExp() <= 0) {
            return getBaseLevel();
        }
        int levelz = getBaseLevel();
        int expz = getEquipExp();
        for (int i = levelz; (GameConstants.getStatFromWeapon(getItemId()) == null ? (i <= GameConstants.getMaxLevel(getItemId())) : (i < GameConstants.getMaxLevel(getItemId()))); i++) {
            if (expz >= GameConstants.getExpForLevel(i, getItemId())) {
                levelz++;
                expz -= GameConstants.getExpForLevel(i, getItemId());
            } else { //for 0, dont continue;
                break;
            }
        }
        return levelz;
    }

    public void setEquipLevel(int level) {
        equipLevel = level;
    }

    public int getBaseLevel() {
        return (GameConstants.getStatFromWeapon(getItemId()) == null ? 1 : 0);
    }

    @Override
    public void setQuantity(short quantity) {
        if (quantity < 0 || quantity > 1) {
            throw new RuntimeException("Setting the quantity to " + quantity + " on an equip (itemid: " + getItemId() + ")");
        }
        super.setQuantity(quantity);
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(final int dur) {
        this.durability = dur;
    }

    public byte getEnhance() {
        return enhance;
    }

    public void setEnhance(final byte en) {
        this.enhance = en;
    }

    public int getPotential1() {
        return potential1;
    }

    public void setPotential1(final int en) {
        this.potential1 = en;
    }

    public int getPotential2() {
        return potential2;
    }

    public void setPotential2(final int en) {
        this.potential2 = en;
    }

    public int getPotential3() {
        return potential3;
    }

    public void setPotential3(final int en) {
        this.potential3 = en;
    }

    public int getPotential4() {
        return potential4;
    }

    public void setPotential4(final int en) {
        this.potential4 = en;
    }

    public int getPotential5() {
        return potential5;
    }

    public void setPotential5(final int en) {
        this.potential5 = en;
    }

    public byte getState() {
        final int pots = potential1 + potential2 + potential3 + potential4 + potential5;
        if (potential1 >= 40000 || potential2 >= 40000 || potential3 >= 40000 || potential4 >= 40000 || potential5 >= 40000) {
            return 20; // legendary
        } else if (potential1 >= 30000 || potential2 >= 30000 || potential3 >= 30000 || potential4 >= 30000 || potential5 >= 30000) {
            return 19; // unique
        } else if (potential1 >= 20000 || potential2 >= 20000 || potential3 >= 20000 || potential4 >= 20000 || potential5 >= 20000) {
            return 18; // epic
        } else if (pots >= 1) {
            return 17; // rare
        } else if (pots < 0) {
            return 1; // hidden
        }
        return 0;
    }

    public void resetPotential_Fuse(boolean half, int potentialState) { //maker skill - equip first receive
        //0.0864% legendary, 1.44% chance unique, 12% chance epic, else rare
        potentialState = -potentialState;
        if (Randomizer.nextInt(100) < 12) {
            potentialState -= Randomizer.nextInt(100) < 12 ? (Randomizer.nextInt(100) < 6 ? 3 : 2) : 1;
        }
        if (potentialState < -20) {
            potentialState = -20;  //just in case
        }
        setPotential1(potentialState);
        setPotential2((Randomizer.nextInt(half ? 5 : 10) == 0 ? potentialState : 0)); //1/10 chance of 3 line
        setPotential3(0); //just set it theoretically
        setPotential4(0); //just set it theoretically
        setPotential5(0); //just set it theoretically
    }

    public void resetPotential() { //equip first one, scroll hidden on it
        //0.192% legendary, 2.4% chance unique, 20% chance epic, else rare
        final int rank = Randomizer.nextInt(100) < 20 ? (Randomizer.nextInt(100) < 12 ? (Randomizer.nextInt(100) < 8 ? -20 : -19) : -18) : -17;
        setPotential1(rank);
        setPotential2((Randomizer.nextInt(10) == 0 ? rank : 0)); // 1/10 chance of 3 line
        setPotential3(0); //just set it theoretically
        setPotential4(0); //just set it theoretically
        setPotential5(0); //just set it theoretically
    }

    public void renewPotential(int type) { // 0 = normal miracle cube, 1 = premium, 2 = epic pot scroll, 3 = super
        final int rank = type == 2 ? -18 : (Randomizer.nextInt(100) < 4 && getState() != (type == 3 ? 20 : 19) ? -(getState() + 1) : -(getState())); // 4 % chance to up 1 tier
        setPotential1(rank);
        if (getPotential3() > 0) {
            setPotential2(rank); // put back old 3rd line
        } else {
            switch (type) {
                case 1: // premium-> suppose to be 25%
                    setPotential2(Randomizer.nextInt(10) == 0 ? rank : 0); //1/10 chance of 3 line
                    break;
                case 2: // epic pot
                    setPotential2(Randomizer.nextInt(10) <= 1 ? rank : 0); //2/10 chance of 3 line
                    break;
                case 3: // super
                    setPotential2(Randomizer.nextInt(10) <= 2 ? rank : 0); //3/10 chance of 3 line
                    break;
                default:
                    setPotential2(0);
                    break;
            }
        }
        if (getPotential4() > 0) {
            setPotential3(rank); // put back old 4th line
        } else if (type == 3) { // super
            setPotential3(Randomizer.nextInt(100) <= 2 ? rank : 0); // 3/100 to get 4 lines
        } else { // premium cannot get 3 lines.
            setPotential3(0); //just set it theoretically
        }
        if (getPotential5() > 0) {
            setPotential4(rank); // put back old 5th line
        } else if (type == 3) { // super
            setPotential4(Randomizer.nextInt(100) <= 1 ? rank : 0); // 2/100 to get 5 lines
        } else {
            setPotential4(0); //just set it theoretically
        }
        setPotential5(0); //just set it theoretically
    }

    public int getIncSkill() {
        return incSkill;
    }

    public void setIncSkill(int inc) {
        this.incSkill = inc;
    }

    public short getCharmEXP() {
        return charmExp;
    }

    public void setCharmEXP(short s) {
        this.charmExp = s;
    }

    public short getPVPDamage() {
        return pvpDamage;
    }

    public void setPVPDamage(short p) {
        this.pvpDamage = p;
    }

    public MapleRing getRing() {
        if (!GameConstants.isEffectRing(getItemId()) || getUniqueId() <= 0) {
            return null;
        }
        if (ring == null) {
            ring = MapleRing.loadFromDb(getUniqueId(), getPosition() < 0);
        }
        return ring;
    }

    public void setRing(MapleRing ring) {
        this.ring = ring;
    }

    public MapleAndroid getAndroid() {
        if (getItemId() / 10000 != 166 || getUniqueId() <= 0) {
            return null;
        }
        if (android == null) {
            android = MapleAndroid.loadFromDb(getItemId(), getUniqueId());
        }
        return android;
    }

    public void setAndroid(MapleAndroid ring) {
        this.android = ring;
    }

    public short getSocketState() {
        int flag = 0;
        if (socket1 != -1 || socket2 != -1 || socket3 != -1) { // Got empty sockets show msg
            flag |= SocketFlag.DEFAULT.getValue();
        }
        if (socket1 != -1) {
            flag |= SocketFlag.SOCKET_BOX_1.getValue();
        }
        if (socket1 > 0) {
            flag |= SocketFlag.USED_SOCKET_1.getValue();
        }
        if (socket2 != -1) {
            flag |= SocketFlag.SOCKET_BOX_2.getValue();
        }
        if (socket2 > 0) {
            flag |= SocketFlag.USED_SOCKET_2.getValue();
        }
        if (socket3 != -1) {
            flag |= SocketFlag.SOCKET_BOX_3.getValue();
        }
        if (socket3 > 0) {
            flag |= SocketFlag.USED_SOCKET_3.getValue();
        }
        return (short) flag;
    }

    public int getSocket1() {
        return socket1;
    }

    public void setSocket1(int socket1) {
        this.socket1 = socket1;
    }

    public int getSocket2() {
        return socket2;
    }

    public void setSocket2(int socket2) {
        this.socket2 = socket2;
    }

    public int getSocket3() {
        return socket3;
    }

    public void setSocket3(int socket3) {
        this.socket3 = socket3;
    }

    // Equipment Upgrade System
    public int getEquipmentLevel() {
        return equipLevel;
    }

    public void levelUpEquip() { // won't need this for Exp because yeah.. logic lol
        equipLevel += 1;
    }

    public void addEquipLevel(int level) {
        equipLevel += level;
    }

    public int getEquipmentExp() {
        return equipExp;
    }

    public void gainEquipExp(int exp) {
        if (getEquipmentLevel() == 10) { // we don't want people to level up.. period after this.
            return;
        }
        if (equipExp + exp >= getEquipExpNeeded(getEquipmentLevel())) {
            levelUpEquip();
            equipExp = (getEquipmentLevel() == 10 ? 1 : 0); // we'll make it 1/1 after reaching 10.
            return;
        }
        equipExp += exp;
    }

    public int getEquipExpNeeded(int level) { // TODO: Calculations.
        switch (level) {
            case 1: // Level 1
            case 2: // Level 2
            case 3: // Level 3
            case 4: // Level 4
            case 5: // Level 5
            case 6: // Level 6
            case 7: // Level 7
            case 8: // Level 8
            case 9: // Level 9
                return 10;
            case 10: // Level 10 (Max Level)
                return 1; // when they're maxed, the exp system will read 1/1
            default:
                return 999;
        }
    }

    public int getEquipMSIUpgrades() {
        return equipMSIUpgrades;
    }

    public void setEquipMSIUpgrades(int upgrades) {
        equipMSIUpgrades = upgrades;
    }

    public void addEquipMSIUpgrade() {
        equipMSIUpgrades += 1;
    }

    //Doesn't change rank or lines, just reset existing ones.
    public void shufflePotential() {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        //Extra check: Level 200+ weapons hotfix
        final int potentialLevel = (ii.getReqLevel(getItemId()) / 10) >= 20 ? 19 : (ii.getReqLevel(getItemId()) / 10);
        final List<List<StructItemOption>> pots = new LinkedList<>(ii.getAllPotentialInfo().values());
        int lines = 1;

        if (getPotential2() != 0) {
            lines++;
        }
        if (getPotential3() != 0) {
            lines++;
        }
        if (getPotential4() != 0) {
            lines++;
        }
        if (getPotential5() != 0) {
            lines++;
        }

        for (int i = 0; i < lines; i++) {
            boolean rewarded = false;
            while (!rewarded) {
                StructItemOption pot = pots.get(Randomizer.nextInt(pots.size())).get(potentialLevel);
                if (pot != null && pot.reqLevel / 10 <= potentialLevel && GameConstants.optionTypeFits(pot.optionType, getItemId()) && GameConstants.potentialIDFits(pot.opID, getState(), i)) {
                    if (i == 0) {
                        setPotential1(pot.opID);
                    } else if (i == 1) {
                        setPotential2(pot.opID);
                    } else if (i == 2) {
                        setPotential3(pot.opID);
                    } else if (i == 3) {
                        setPotential4(pot.opID);
                    } else if (i == 4) {
                        setPotential5(pot.opID);
                    }
                    rewarded = true;
                }
            }
        }
    }

    public void shuffleSinglePotential(int line) {
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int potentialLevel = (ii.getReqLevel(getItemId()) / 10) >= 20 ? 19 : (ii.getReqLevel(getItemId()) / 10);
        final List<List<StructItemOption>> pots = new LinkedList<>(ii.getAllPotentialInfo().values());

        boolean rewarded = false;
        while (!rewarded) {
            StructItemOption pot = pots.get(Randomizer.nextInt(pots.size())).get(potentialLevel);
            if (pot != null && pot.reqLevel / 10 <= potentialLevel && GameConstants.optionTypeFits(pot.optionType, getItemId()) && GameConstants.potentialIDFits(pot.opID, getState(), line - 1)) {
                if (line == 1) {
                    setPotential1(pot.opID);
                } else if (line == 2) {
                    setPotential2(pot.opID);
                } else if (line == 3) {
                    setPotential3(pot.opID);
                } else if (line == 4) {
                    setPotential4(pot.opID);
                } else if (line == 5) {
                    setPotential5(pot.opID);
                }
                rewarded = true;
            }
        }
    }

    public static enum ScrollResult {
        SUCCESS, FAIL, CURSE
    }
}
