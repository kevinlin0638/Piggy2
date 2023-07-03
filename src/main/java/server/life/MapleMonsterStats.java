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
package server.life;

import constants.GameConstants;
import tools.types.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

public class MapleMonsterStats {

    private byte cp, selfDestruction_action, tagColor, tagBgColor, rareItemDropLevel, HPDisplayType, summonType, PDRate, MDRate, category;
    private short level, charismaEXP;
    private long hp;
    private int id, exp, mp, removeAfter, buffToGive, fixedDamage, selfDestruction_hp, dropItemPeriod, point, eva, acc, PhysicalAttack, MagicAttack, speed, partyBonusR, pushed;
    private boolean boss, undead, ffaLoot, firstAttack, isExplosiveReward, mobile, fly, onlyNormalAttack, friendly, noDoom, invincible, partyBonusMob, changeable, escort;
    private String name, mobType;
    private EnumMap<Element, ElementalEffectiveness> resistance = new EnumMap<Element, ElementalEffectiveness>(Element.class);
    private List<Integer> revives = new ArrayList<Integer>();
    private List<Pair<Integer, Integer>> skills = new ArrayList<Pair<Integer, Integer>>();
    private List<MobAttackInfo> mai = new ArrayList<MobAttackInfo>();
    private BanishInfo banish;

    public MapleMonsterStats(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public long getHp() {
        return hp;
    }

    public void setHp(long hp) {
        this.hp = hp;//(hp * 3L / 2L);
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public short getLevel() {
        return level;
    }

    public void setLevel(short level) {
        this.level = level;
    }

    public short getCharismaEXP() {
        return charismaEXP;
    }

    public void setCharismaEXP(short leve) {
        this.charismaEXP = leve;
    }

    public byte getSelfD() {
        return selfDestruction_action;
    }

    public void setSelfD(byte selfDestruction_action) {
        this.selfDestruction_action = selfDestruction_action;
    }

    public void setSelfDHP(int selfDestruction_hp) {
        this.selfDestruction_hp = selfDestruction_hp;
    }

    public int getSelfDHp() {
        return selfDestruction_hp;
    }

    public int getFixedDamage() {
        return fixedDamage;
    }

    public void setFixedDamage(int damage) {
        this.fixedDamage = damage;
    }

    public int getPushed() {
        return pushed;
    }

    public void setPushed(int damage) {
        this.pushed = damage;
    }

    public int getPhysicalAttack() {
        return PhysicalAttack;
    }

    public void setPhysicalAttack(final int PhysicalAttack) {
        this.PhysicalAttack = PhysicalAttack;
    }

    public final int getMagicAttack() {
        return MagicAttack;
    }

    public final void setMagicAttack(final int MagicAttack) {
        this.MagicAttack = MagicAttack;
    }

    public final int getEva() {
        return eva;
    }

    public final void setEva(final int eva) {
        this.eva = eva;
    }

    public final int getAcc() {
        return acc;
    }

    public final void setAcc(final int acc) {
        this.acc = acc;
    }

    public final int getSpeed() {
        return speed;
    }

    public final void setSpeed(final int speed) {
        this.speed = speed;
    }

    public final int getPartyBonusRate() {
        return partyBonusR;
    }

    public final void setPartyBonusRate(final int speed) {
        this.partyBonusR = speed;
    }

    public void setOnlyNormalAttack(boolean onlyNormalAttack) {
        this.onlyNormalAttack = onlyNormalAttack;
    }

    public boolean getOnlyNoramlAttack() {
        return onlyNormalAttack;
    }

    public BanishInfo getBanishInfo() {
        return banish;
    }

    public void setBanishInfo(BanishInfo banish) {
        this.banish = banish;
    }

    public int getRemoveAfter() {
        return removeAfter;
    }

    public void setRemoveAfter(int removeAfter) {
        this.removeAfter = removeAfter;
    }

    public byte getrareItemDropLevel() {
        return rareItemDropLevel;
    }

    public void setrareItemDropLevel(byte rareItemDropLevel) {
        this.rareItemDropLevel = rareItemDropLevel;
    }

    public boolean isBoss() {
        return boss;
    }

    public void setBoss(boolean boss) {
        this.boss = boss;
    }

    public boolean isFfaLoot() {
        return ffaLoot;
    }

    public void setFfaLoot(boolean ffaLoot) {
        this.ffaLoot = ffaLoot;
    }

    public boolean isEscort() {
        return escort;
    }

    public void setEscort(boolean ffaL) {
        this.escort = ffaL;
    }

    public boolean isExplosiveReward() {
        return isExplosiveReward;
    }

    public void setExplosiveReward(boolean isExplosiveReward) {
        this.isExplosiveReward = isExplosiveReward;
    }

    public boolean getMobile() {
        return mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    public boolean getFly() {
        return fly;
    }

    public void setFly(boolean fly) {
        this.fly = fly;
    }

    public List<Integer> getRevives() {
        return revives;
    }

    public void setRevives(List<Integer> revives) {
        this.revives = revives;
    }

    public boolean getUndead() {
        return undead;
    }

    public void setUndead(boolean undead) {
        this.undead = undead;
    }

    public byte getSummonType() {
        return summonType;
    }

    public void setSummonType(byte selfDestruction) {
        this.summonType = selfDestruction;
    }

    public byte getCategory() {
        return category;
    }

    public void setCategory(byte selfDestruction) {
        this.category = selfDestruction;
    }

    public byte getPDRate() {
        return PDRate;
    }

    public void setPDRate(byte selfDestruction) {
        this.PDRate = selfDestruction;
    }

    public byte getMDRate() {
        return MDRate;
    }

    public void setMDRate(byte selfDestruction) {
        this.MDRate = selfDestruction;
    }

    public EnumMap<Element, ElementalEffectiveness> getElements() {
        return resistance;
    }

    public void setEffectiveness(Element e, ElementalEffectiveness ee) {
        resistance.put(e, ee);
    }

    public void removeEffectiveness(Element e) {
        resistance.remove(e);
    }

    public ElementalEffectiveness getEffectiveness(Element e) {
        ElementalEffectiveness elementalEffectiveness = resistance.get(e);
        if (elementalEffectiveness == null) {
            return ElementalEffectiveness.NORMAL;
        } else {
            return elementalEffectiveness;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return mobType;
    }

    public void setType(String mobt) {
        this.mobType = mobt;
    }

    public byte getTagColor() {
        return tagColor;
    }

    public void setTagColor(int tagColor) {
        this.tagColor = (byte) tagColor;
    }

    public byte getTagBgColor() {
        return tagBgColor;
    }

    public void setTagBgColor(int tagBgColor) {
        this.tagBgColor = (byte) tagBgColor;
    }

    public List<Pair<Integer, Integer>> getSkills() {
        return Collections.unmodifiableList(this.skills);
    }

    public void setSkills(List<Pair<Integer, Integer>> skill_) {
        for (Pair<Integer, Integer> skill : skill_) {
            skills.add(skill);
        }
    }

    public byte getNoSkills() {
        return (byte) skills.size();
    }

    public boolean hasSkill(int skillId, int level) {
        for (Pair<Integer, Integer> skill : skills) {
            if (skill.getLeft() == skillId && skill.getRight() == level) {
                return true;
            }
        }
        return false;
    }

    public boolean isFirstAttack() {
        return firstAttack;
    }

    public void setFirstAttack(boolean firstAttack) {
        this.firstAttack = firstAttack;
    }

    public byte getCP() {
        return cp;
    }

    public void setCP(byte cp) {
        this.cp = cp;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int cp) {
        this.point = cp;
    }

    public boolean isFriendly() {
        return friendly;
    }

    public void setFriendly(boolean friendly) {
        this.friendly = friendly;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public void setInvincible(boolean invin) {
        this.invincible = invin;
    }

    public void setChange(boolean invin) {
        this.changeable = invin;
    }

    public boolean isChangeable() {
        return changeable;
    }

    public boolean isPartyBonus() {
        return partyBonusMob;
    }

    public void setPartyBonus(boolean invin) {
        this.partyBonusMob = invin;
    }

    public boolean isNoDoom() {
        return noDoom;
    }

    public void setNoDoom(boolean doom) {
        this.noDoom = doom;
    }

    public int getBuffToGive() {
        return buffToGive;
    }

    public void setBuffToGive(int buff) {
        this.buffToGive = buff;
    }

    public byte getHPDisplayType() {
        return HPDisplayType;
    }

    public void setHPDisplayType(byte HPDisplayType) {
        this.HPDisplayType = HPDisplayType;
    }

    public int getDropItemPeriod() {
        return dropItemPeriod;
    }

    public void setDropItemPeriod(int d) {
        this.dropItemPeriod = d;
    }

    public void addMobAttack(MobAttackInfo ma) {
        this.mai.add(ma);
    }

    public MobAttackInfo getMobAttack(int attack) {
        if (attack >= this.mai.size() || attack < 0) {
            return null;
        }
        return this.mai.get(attack);
    }

    public List<MobAttackInfo> getMobAttacks() {
        return this.mai;
    }

    public int dropsMeso() {
        if (getRemoveAfter() != 0 || isInvincible() || getOnlyNoramlAttack() || getDropItemPeriod() > 0 || getCP() > 0 || getPoint() > 0 || getFixedDamage() > 0 || getSelfD() != -1 || getPDRate() <= 0 || getMDRate() <= 0) {
            return 0;
        }
        //final String mt = stats.getMobType();
        //if (mt != null && mt.length() > 0 && mt.charAt(0) == '7') {
        //    return 0; //bosses; magatia pq
        //}
        final int mobId = getId() / 100000;
        if (GameConstants.getPartyPlayHP(getId()) > 0 || mobId == 97 || mobId == 95 || mobId == 93 || mobId == 91 || mobId == 90) {
            return 0;
        }
        if (isExplosiveReward()) {
            return 7;
        }
        if (isBoss()) {
            return 2;
        }
        return 1;
    }
}
