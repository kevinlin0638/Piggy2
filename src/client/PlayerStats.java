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
package client;

import client.MapleTrait.MapleTraitType;
import client.inventory.*;
import client.skill.Skill;
import client.skill.SkillEntry;
import client.skill.SkillFactory;
import server.status.MapleBuffStatus;
import constants.GameConstants;
import handling.world.World;
import handling.world.guild.MapleGuild;
import handling.world.guild.MapleGuildSkill;
import server.*;
import server.StructSetItem.SetItem;
import server.life.Element;
import tools.data.MaplePacketLittleEndianWriter;
import tools.packet.CField.EffectPacket;
import tools.packet.CWvsContext.InventoryPacket;
import tools.types.Pair;
import tools.types.Triple;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

public class PlayerStats implements Serializable {
    public static int[] pvpSkills = {1000007, 2000007, 3000006, 4000010, 5000006, 5010004, 11000006, 12000006, 13000005, 14000006, 15000005, 21000005, 22000002, 23000004, 31000005, 32000012, 33000004, 35000005};
    private static int[] allJobs = {0, 10000, 10000000, 20000000, 20010000, 20020000, 30000000, 30010000};
    public short str, dex, luk, int_;
    public int hp, maxhp, mp, maxmp;
    public transient boolean equippedWelcomeBackRing, hasClone, hasPartyBonus, Berserk, canFish, canFishVIP;
    public transient double expBuff, dropBuff, mesoBuff, cashBuff, mesoGuard, mesoGuardMeso, expMod, pickupRange;
    public transient double dam_r, bossdam_r;
    public transient int recoverHP, recoverMP, mpconReduce, mpconPercent, incMesoProp, reduceCooltime, DAMreflect, DAMreflect_rate, ignoreDAMr, ignoreDAMr_rate, ignoreDAM, ignoreDAM_rate, mpRestore,
            hpRecover, hpRecoverProp, hpRecoverPercent, mpRecover, mpRecoverProp, RecoveryUP, BuffUP, RecoveryUP_Skill, BuffUP_Skill,
            incAllskill, combatOrders, ignoreTargetDEF, defRange, BuffUP_Summon, evaR, dodgeChance, speed, jump, harvestingTool,
            equipmentBonusExp, dropMod, cashMod, levelBonus, ASR, TER, pickRate, decreaseDebuff, equippedFairy, equippedSummon,
            percent_hp, percent_mp, percent_str, percent_dex, percent_int, percent_luk, percent_acc, percent_atk, percent_matk, percent_wdef, percent_mdef,
            pvpDamage, hpRecoverTime = 0, mpRecoverTime = 0, dot, dotTime, questBonus, pvpRank, pvpExp, wdef, mdef, trueMastery;
    public transient int def, element_ice, element_fire, element_light, element_psn;
    private Map<Integer, Integer> setHandling = new HashMap<>(), skillsIncrement = new HashMap<>(), damageIncrease = new HashMap<>();
    private EnumMap<Element, Integer> elemBoosts = new EnumMap<>(Element.class);
    private List<Equip> durabilityHandling = new ArrayList<>(), equipLevelHandling = new ArrayList<>();
    private List<Triple<Integer, String, Integer>> psdSkills = new ArrayList<>();
    private transient float shouldHealHP, shouldHealMP;
    private transient short passive_sharpeye_min_percent, passive_sharpeye_percent, passive_sharpeye_rate;
    private transient byte passive_mastery;
    private transient int localstr, localdex, localluk, localint_, localmaxhp, localmaxmp;
    private transient int magic, hands, accuracy;
    private transient long watk;
    private transient float localmaxbasedamage, localmaxbasepvpdamage, localmaxbasepvpdamageL;

    public static int getSkillByJob(int skillID, int job) {
        if (GameConstants.isKOC(job)) {
            return skillID + 10000000;
        } else if (GameConstants.isAran(job)) {
            return skillID + 20000000;
        } else if (GameConstants.isEvan(job)) {
            return skillID + 20010000;
        } else if (GameConstants.isMercedes(job)) {
            return skillID + 20020000;
        } else if (GameConstants.isDemon(job)) {
            return skillID + 30010000;
        } else if (GameConstants.isResist(job)) {
            return skillID + 30000000;
        }
        return skillID;
    }

    // TODO: all psd skills (Passive)
    public void init(MapleCharacter chra) {
        recalcLocalStats(chra);
    }

    public short getStr() {
        return str;
    }

    public short getDex() {
        return dex;
    }

    public short getLuk() {
        return luk;
    }

    public short getInt() {
        return int_;
    }

    public void setStr(short str, MapleCharacter chra) {
        this.str = str;
        recalcLocalStats(chra);
    }

    public void setDex(short dex, MapleCharacter chra) {
        this.dex = dex;
        recalcLocalStats(chra);
    }

    public void setLuk(short luk, MapleCharacter chra) {
        this.luk = luk;
        recalcLocalStats(chra);
    }

    public void setInt(short int_, MapleCharacter chra) {
        this.int_ = int_;
        recalcLocalStats(chra);
    }

    public boolean setHp(int newhp, MapleCharacter chra) {
        return setHp(newhp, false, chra);
    }

    public boolean setHp(int newhp, boolean silent, MapleCharacter chra) {
        int oldHp = hp;
        int thp = newhp;
        if (thp < 0) {
            thp = 0;
        }
        if (thp > localmaxhp) {
            thp = localmaxhp;
        }
        this.hp = thp;

        if (chra != null) {
            if (!silent) {
                chra.checkBerserk();
                chra.updatePartyMemberHP();
            }
            if (oldHp > hp && !chra.isAlive()) {
                chra.playerDead();
            }
        }
        return hp != oldHp;
    }

    public boolean setMp(int newmp, MapleCharacter chra) {
        int oldMp = mp;
        int tmp = newmp;
        if (tmp < 0) {
            tmp = 0;
        }
        if (tmp > localmaxmp) {
            tmp = localmaxmp;
        }
        this.mp = tmp;
        return mp != oldMp;
    }

    public void setInfo(int maxhp, int maxmp, int hp, int mp) {
        this.maxhp = maxhp;
        this.maxmp = maxmp;
        this.hp = hp;
        this.mp = mp;
    }

    public void setMaxHp(int hp, MapleCharacter chra) {
        this.maxhp = hp;
        recalcLocalStats(chra);
    }

    public void setMaxMp(int mp, MapleCharacter chra) {
        this.maxmp = mp;
        recalcLocalStats(chra);
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxhp;
    }

    public int getMp() {
        return mp;
    }

    public int getMaxMp() {
        return maxmp;
    }

    public int getTotalDex() {
        return localdex;
    }

    public int getTotalInt() {
        return localint_;
    }

    public int getTotalStr() {
        return localstr;
    }

    public int getTotalLuk() {
        return localluk;
    }

    public int getTotalMagic() {
        return magic;
    }

    public int getSpeed() {
        return speed;
    }

    public int getJump() {
        return jump;
    }

    public long getTotalWatk() {
        return watk;
    }

    public int getCurrentMaxHp() {
        return localmaxhp;

    }

    public int getCurrentMaxMp(int job) {
        if (GameConstants.isDemon(job)) {
            return GameConstants.getMPByJob(job);
        }
        return localmaxmp;

    }

    public int getHands() {
        return hands;
    }

    public float getCurrentMaxBaseDamage() {
        return localmaxbasedamage;
    }

    public float getCurrentMaxBasePVPDamage() {
        return localmaxbasepvpdamage;
    }

    public float getCurrentMaxBasePVPDamageL() {
        return localmaxbasepvpdamageL;
    }

    public void recalcLocalStats(MapleCharacter chra) {
        recalcLocalStats(false, chra);
    }

    public void recalcLocalStats(boolean first_login, MapleCharacter chra) {

        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int oldmaxhp = localmaxhp;
        int localmaxhp_ = getMaxHp();
        int localmaxmp_ = getMaxMp();
        accuracy = 0;
        wdef = 0;
        mdef = 0;
        localdex = getDex();
        localint_ = getInt();
        localstr = getStr();
        localluk = getLuk();
        speed = 100;
        jump = 100;
        pickupRange = 0.0;
        decreaseDebuff = 0;
        ASR = 0;
        TER = 0;
        dot = 0;
        questBonus = 1;
        dotTime = 0;
        trueMastery = 0;
        percent_wdef = 0;
        percent_mdef = 0;
        percent_hp = 0;
        percent_mp = 0;
        percent_str = 0;
        percent_dex = 0;
        percent_int = 0;
        percent_luk = 0;
        percent_acc = 0;
        percent_atk = 0;
        percent_matk = 0;
        passive_sharpeye_rate = 5;
        passive_sharpeye_min_percent = 20;
        passive_sharpeye_percent = 50;
        magic = 0;
        watk = 0;
        if (chra.getJob() == 500 || (chra.getJob() >= 520 && chra.getJob() <= 522)) {
            watk = 20; //bullet
        } else if (chra.getJob() == 400 || (chra.getJob() >= 410 && chra.getJob() <= 412) || (chra.getJob() >= 1400 && chra.getJob() <= 1412)) {
            watk = 30; //stars
        }
        StructItemOption soc;
        dodgeChance = 0;
        pvpDamage = 0;
        mesoGuard = 50.0;
        mesoGuardMeso = 0.0;
        dam_r = 100.0;
        bossdam_r = 100.0;
        expBuff = 100.0;
        cashBuff = 100.0;
        dropBuff = 100.0;
        mesoBuff = 100.0;
        recoverHP = 0;
        recoverMP = 0;
        mpconReduce = 0;
        mpconPercent = 100;
        incMesoProp = 0;
        reduceCooltime = 0;
        DAMreflect = 0;
        DAMreflect_rate = 0;
        ignoreDAMr = 0;
        ignoreDAMr_rate = 0;
        ignoreDAM = 0;
        ignoreDAM_rate = 0;
        ignoreTargetDEF = 0;
        hpRecover = 0;
        hpRecoverProp = 0;
        hpRecoverPercent = 0;
        mpRecover = 0;
        mpRecoverProp = 0;
        mpRestore = 0;
        pickRate = 0;
        equippedWelcomeBackRing = false;
        equippedFairy = 0;
        equippedSummon = 0;
        hasClone = false;
        hasPartyBonus = false;
        Berserk = false;
        canFish = GameConstants.GMS;
        canFishVIP = false;
        equipmentBonusExp = 0;
        RecoveryUP = 0;
        BuffUP = 0;
        RecoveryUP_Skill = 0;
        BuffUP_Skill = 0;
        BuffUP_Summon = 0;
        dropMod = 1;
        expMod = 1.0;
        cashMod = 1;
        levelBonus = 0;
        incAllskill = 0;
        combatOrders = 0;
        defRange = 0;
        durabilityHandling.clear();
        equipLevelHandling.clear();
        skillsIncrement.clear();
        damageIncrease.clear();
        setHandling.clear();
        harvestingTool = 0;
        element_fire = 100;
        element_ice = 100;
        element_light = 100;
        element_psn = 100;
        def = 100;
        for (MapleTraitType t : MapleTraitType.values()) {
            chra.getTrait(t).clearLocalExp();
        }
        Map<Skill, SkillEntry> sData = new HashMap<>();
        for (Item item1 : chra.getInventory(MapleInventoryType.EQUIPPED).newList()) {
            Equip equip = (Equip) item1;
            //   if (GameConstants.getAngelicSkill(equip.getItemId()) > 0) {
            //     equippedSummon = PlayerStats.getSkillByJob(GameConstants.getAngelicSkill(equip.getItemId()), chra.getJob());
            //}
            if (equip.getPosition() == -11) {
                if (GameConstants.isMagicWeapon(equip.getItemId())) {
                    Map<String, Integer> eqstat = MapleItemInformationProvider.getInstance().getEquipStats(equip.getItemId());

                    if (eqstat != null) {
                        if (eqstat.containsKey("incRMAF")) {
                            element_fire = eqstat.get("incRMAF");
                        }
                        if (eqstat.containsKey("incRMAI")) {
                            element_ice = eqstat.get("incRMAI");
                        }
                        if (eqstat.containsKey("incRMAL")) {
                            element_light = eqstat.get("incRMAL");
                        }
                        if (eqstat.containsKey("incRMAS")) {
                            element_psn = eqstat.get("incRMAS");
                        }
                        if (eqstat.containsKey("elemDefault")) {
                            def = eqstat.get("elemDefault");
                        }
                    }
                }
            }
            if (equip.getItemId() / 10000 == 166 && equip.getAndroid() != null && chra.getAndroid() == null) {
                chra.setAndroid(equip.getAndroid());
            }
            //if (equip.getItemId() / 1000 == 1099) {
            //    equippedForce += equip.getMp();
            //}
            chra.getTrait(MapleTraitType.craft).addLocalExp(equip.getHands());
            accuracy += equip.getAcc();
            localmaxhp_ += equip.getHp();
            localmaxmp_ += equip.getMp();
            localdex += equip.getDex();
            localint_ += equip.getInt();
            localstr += equip.getStr();
            localluk += equip.getLuk();
            magic += equip.getMatk();
            watk += equip.getWatk();
            wdef += equip.getWdef();
            mdef += equip.getMdef();
            speed += equip.getSpeed();
            jump += equip.getJump();
            pvpDamage += equip.getPVPDamage();
            switch (equip.getItemId()) {
                case 1112127:
                    equippedWelcomeBackRing = true;
                    break;
                case 1122017:
                    equippedFairy = 10;
                    break;
                case 1122158:
                    equippedFairy = 5;
                    break;
                case 1112585:
                    equippedSummon = 1085;
                    break;
                case 1112586:
                    equippedSummon = 1087;
                    break;
                case 1112594:
                case 1112663:
                    equippedSummon = 1179;
                    break;
                default:
                    for (int eb_bonus : GameConstants.Equipments_Bonus) {
                        if (equip.getItemId() == eb_bonus) {
                            equipmentBonusExp += GameConstants.Equipment_Bonus_EXP(eb_bonus);
                            break;
                        }
                    }
                    break;
            } //slow, poison, darkness, seal, freeze
            Integer set = ii.getSetItemID(equip.getItemId());
            if (set != null && set > 0) {
                int value = 1;
                if (setHandling.containsKey(set)) {
                    value += setHandling.get(set).intValue();
                }
                setHandling.put(set, value); //id of Set, number of items to go with the set
            }
            if (equip.getIncSkill() > 0 && ii.getEquipSkills(equip.getItemId()) != null) {
                for (int zzz : ii.getEquipSkills(equip.getItemId())) {
                    Skill skil = SkillFactory.getSkill(zzz);
                    if (skil != null && skil.canBeLearnedBy(chra.getJob())) { //dont go over masterlevel :D
                        int value = 1;
                        if (skillsIncrement.get(skil.getId()) != null) {
                            value += skillsIncrement.get(skil.getId());
                        }
                        skillsIncrement.put(skil.getId(), value);
                    }
                }

            }
            EnumMap<EquipAdditions, Pair<Integer, Integer>> additions = ii.getEquipAdditions(equip.getItemId());
            if (additions != null) {
                for (Entry<EquipAdditions, Pair<Integer, Integer>> add : additions.entrySet()) {
                    switch (add.getKey()) {
                        case elemboost:
                            int value = add.getValue().right;
                            Element key = Element.getFromId(add.getValue().left);
                            if (elemBoosts.get(key) != null) {
                                value += elemBoosts.get(key);
                            }
                            elemBoosts.put(key, value);
                            break;
                        case mobcategory: //skip the category, thinkings too expensive to have yet another Map<Integer, Integer> for damage calculations
                            dam_r *= (add.getValue().right + 100.0) / 100.0;
                            bossdam_r += (add.getValue().right + 100.0) / 100.0;
                            break;
                        case critical:
                            passive_sharpeye_rate += add.getValue().left;
                            passive_sharpeye_min_percent += add.getValue().right;
                            passive_sharpeye_percent += add.getValue().right; //???CONFIRM - not sure if this is max or minCritDmg
                            break;
                        case boss:
                            bossdam_r *= (add.getValue().right + 100.0) / 100.0;
                            break;
                        case mobdie:
                            if (add.getValue().left > 0) {
                                hpRecover += add.getValue().left; //no indication of prop, so i made myself
                                hpRecoverProp += 5;
                            }
                            if (add.getValue().right > 0) {
                                mpRecover += add.getValue().right; //no indication of prop, so i made myself
                                mpRecoverProp += 5;
                            }
                            break;
                        case skill: //now, i'm a bit iffy on this one
                            if (first_login) {
                                sData.put(SkillFactory.getSkill(add.getValue().left), new SkillEntry((byte) (int) add.getValue().right, (byte) 0, -1));
                            }
                            break;
                        case hpmpchange:
                            recoverHP += add.getValue().left;
                            recoverMP += add.getValue().right;
                            break;
                    }
                }
            }
            if (equip.getState() >= 17) {
                int[] potentials = {equip.getPotential1(), equip.getPotential2(), equip.getPotential3(), equip.getPotential4(), equip.getPotential5()};
                for (int i : potentials) {
                    if (i > 0) {
                        soc = ii.getPotentialInfo(i).get(ii.getReqLevel(equip.getItemId()) / 10);
                        if (soc != null) {
                            localmaxhp_ += soc.get("incMHP");
                            localmaxmp_ += soc.get("incMMP");
                            handleItemOption(soc, chra, first_login, sData);
                        }
                    }
                }
            }
            if (equip.getSocketState() > 15) {
                int[] sockets = {equip.getSocket1(), equip.getSocket2(), equip.getSocket3()};
                for (int i : sockets) {
                    if (i > 0) {
                        soc = ii.getSocketInfo(i);
                        if (soc != null) {
                            localmaxhp_ += soc.get("incMHP");
                            localmaxmp_ += soc.get("incMMP");
                            handleItemOption(soc, chra, first_login, sData);
                        }
                    }
                }
            }
            if (equip.getDurability() > 0) {
                durabilityHandling.add((Equip) equip);
            }
            if (GameConstants.getMaxLevel(equip.getItemId()) > 0 && (GameConstants.getStatFromWeapon(equip.getItemId()) == null ? (equip.getEquipLevel() <= GameConstants.getMaxLevel(equip.getItemId())) : (equip.getEquipLevel() < GameConstants.getMaxLevel(equip.getItemId())))) {
                equipLevelHandling.add((Equip) equip);
            }
        }
        Iterator<Entry<Integer, Integer>> iter = setHandling.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Integer, Integer> entry = iter.next();
            StructSetItem set = ii.getSetItem(entry.getKey());
            if (set != null) {
                Map<Integer, SetItem> itemz = set.getItems();
                for (Entry<Integer, SetItem> ent : itemz.entrySet()) {
                    if (ent.getKey() <= entry.getValue()) {
                        SetItem se = ent.getValue();
                        localstr += se.incSTR + se.incAllStat;
                        localdex += se.incDEX + se.incAllStat;
                        localint_ += se.incINT + se.incAllStat;
                        localluk += se.incLUK + se.incAllStat;
                        watk += se.incPAD;
                        magic += se.incMAD;
                        speed += se.incSpeed;
                        accuracy += se.incACC;
                        localmaxhp_ += se.incMHP;
                        localmaxmp_ += se.incMMP;
                        percent_hp += se.incMHPr;
                        percent_mp += se.incMMPr;
                        wdef += se.incPDD;
                        mdef += se.incMDD;
                        if (se.option1 > 0 && se.option1Level > 0) {
                            soc = ii.getPotentialInfo(se.option1).get(se.option1Level);
                            if (soc != null) {
                                localmaxhp_ += soc.get("incMHP");
                                localmaxmp_ += soc.get("incMMP");
                                handleItemOption(soc, chra, first_login, sData);
                            }
                        }
                        if (se.option2 > 0 && se.option2Level > 0) {
                            soc = ii.getPotentialInfo(se.option2).get(se.option2Level);
                            if (soc != null) {
                                localmaxhp_ += soc.get("incMHP");
                                localmaxmp_ += soc.get("incMMP");
                                handleItemOption(soc, chra, first_login, sData);
                            }
                        }
                    }
                }
            }
        }
        handleProfessionTool(chra);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        for (Item item : chra.getInventory(MapleInventoryType.CASH).newList()) {
            if (item.getItemId() / 100000 == 52) {
                if (expMod < 3 && (item.getItemId() == 5211060 || item.getItemId() == 5211050 || item.getItemId() == 5211051 || item.getItemId() == 5211052 || item.getItemId() == 5211053 || item.getItemId() == 5211054)) {
                    expMod = 3.0;//overwrite
                } else if (expMod < 2 && (item.getItemId() == 5210000 || item.getItemId() == 5210001 || item.getItemId() == 5210002 || item.getItemId() == 5210003 || item.getItemId() == 5210004 || item.getItemId() == 5210005 || item.getItemId() == 5211061 || item.getItemId() == 5211000 || item.getItemId() == 5211001 || item.getItemId() == 5211002 || item.getItemId() == 5211003 || item.getItemId() == 5211046 || item.getItemId() == 5211047 || item.getItemId() == 5211048 || item.getItemId() == 5211049)) {
                    expMod = 2.0;
                } else if (expMod < 1.5 && (item.getItemId() == 5211077 || item.getItemId() == 5211078 || item.getItemId() == 5211079 || item.getItemId() == 5211068)) {
                    expMod = 1.5;
                } else if (expMod < 1.2 && (item.getItemId() == 5211071 || item.getItemId() == 5211072 || item.getItemId() == 5211073 || item.getItemId() == 5211074 || item.getItemId() == 5211075 || item.getItemId() == 5211076 || item.getItemId() == 5211067)) {
                    expMod = 1.2;
                }
            } else if (dropMod == 1 && item.getItemId() / 10000 == 536) {
                if (item.getItemId() == 5360000 || item.getItemId() == 5360009 || item.getItemId() == 5360010 || item.getItemId() == 5360011 || item.getItemId() == 5360012 || item.getItemId() == 5360013 || item.getItemId() == 5360014 || item.getItemId() == 5360017 || item.getItemId() == 5360050 || item.getItemId() == 5360053 || item.getItemId() == 5360042 || item.getItemId() == 5360052) {
                    dropMod = 2;
                }
            } else if (item.getItemId() == 5650000) {
                hasPartyBonus = true;
            } else if (item.getItemId() == 5710000) {
                questBonus = 2;
            } else if (item.getItemId() == 5340000) {
                canFish = true;
            } else if (item.getItemId() == 5340001) {
                canFish = true;
                canFishVIP = true;
            }
        }
        for (Item item : chra.getInventory(MapleInventoryType.ETC).list()) { //omfg;
            switch (item.getItemId()) {
                case 4030003:
                    pickupRange = Double.POSITIVE_INFINITY;
                    break;
                case 4030004:
                    break;
                case 4030005:
                    cashMod = 2;
                    break;
            }
        }
        if (first_login && chra.getLevel() >= 30) { //yeah
            if (chra.isGM()) { //!job lol
                for (int i = 0; i < allJobs.length; i++) {
                    sData.put(SkillFactory.getSkill(1085 + allJobs[i]), new SkillEntry((byte) 1, (byte) 0, -1));
                    sData.put(SkillFactory.getSkill(1087 + allJobs[i]), new SkillEntry((byte) 1, (byte) 0, -1));
                    sData.put(SkillFactory.getSkill(1179 + allJobs[i]), new SkillEntry((byte) 1, (byte) 0, -1));
                }
            } else {
                sData.put(SkillFactory.getSkill(getSkillByJob(1085, chra.getJob())), new SkillEntry((byte) 1, (byte) 0, -1));
                sData.put(SkillFactory.getSkill(getSkillByJob(1087, chra.getJob())), new SkillEntry((byte) 1, (byte) 0, -1));
                sData.put(SkillFactory.getSkill(getSkillByJob(1179, chra.getJob())), new SkillEntry((byte) 1, (byte) 0, -1));
            }
        }

        for (Pair ix : chra.getCharacterCard().getCardEffects()) {
            MapleStatEffect e = SkillFactory.getSkill(((Integer) ix.getLeft()).intValue()).getEffect(((Integer) ix.getRight()).intValue());
            this.percent_wdef += e.getWDEFRate();
            this.watk += chra.getLevel();
            this.percent_hp += e.getPercentHP();
            this.percent_mp += e.getPercentMP();
            this.magic += chra.getLevel();
            this.RecoveryUP += 132;
            this.percent_acc += 11;
            this.passive_sharpeye_rate = (short) (this.passive_sharpeye_rate + e.getCr());
            this.jump += e.getPassiveJump();
            this.speed += e.getPassiveSpeed();
            this.dodgeChance += 13;
            this.BuffUP_Summon += 10;
            this.ASR += e.getASRRate();
            this.BuffUP_Skill += 30;
            this.incMesoProp += 10;
            this.passive_sharpeye_percent = (short) (this.passive_sharpeye_percent + e.getCriticalMax());
            this.ignoreTargetDEF += e.getIgnoreMob();
            this.localstr += e.getStrX();
            this.localdex += e.getDexX();
            this.localint_ += e.getIntX();
            this.localluk += e.getLukX();
            this.watk += e.getAttackX();
            this.magic += e.getMagicX();
            this.bossdam_r += e.getBossDamage();
        }
        if (equippedSummon > 0) {
            equippedSummon = getSkillByJob(equippedSummon, chra.getJob());
        }
        //dam_r += (chra.getJob() >= 430 && chra.getJob() <= 434 ? 70 : 0); //leniency on upper stab
        this.localstr += Math.floor((localstr * percent_str) / 100.0f);
        this.localdex += Math.floor((localdex * percent_dex) / 100.0f);
        this.localint_ += Math.floor((localint_ * percent_int) / 100.0f);
        this.localluk += Math.floor((localluk * percent_luk) / 100.0f);

        if (localint_ > localdex) {
            accuracy += localint_ + Math.floor(localluk * 1.2);
        } else {
            accuracy += localluk + Math.floor(localdex * 1.2);
        }
        this.wdef += Math.floor((localstr * 1.2) + ((localdex + localluk) * 0.5) + (localint_ * 0.4));
        this.mdef += Math.floor((localstr * 0.4) + ((localdex + localluk) * 0.5) + (localint_ * 1.2));
        this.accuracy += Math.floor((accuracy * percent_acc) / 100.0f);
        Skill bx;
        int bof;
        MapleStatEffect eff = chra.getStatForBuff(MapleBuffStatus.MONSTER_RIDING);
        if (eff != null && eff.getSourceId() == 33001001) { //jaguar
            passive_sharpeye_rate += eff.getW();
            percent_hp += eff.getZ();
        }
        Integer buff = chra.getBuffedValue(MapleBuffStatus.DICE_ROLL);
        if (buff != null) {
            percent_wdef += GameConstants.getDiceStat(buff.intValue(), 2);
            percent_mdef += GameConstants.getDiceStat(buff.intValue(), 2);
            percent_hp += GameConstants.getDiceStat(buff.intValue(), 3);
            percent_mp += GameConstants.getDiceStat(buff.intValue(), 3);
            passive_sharpeye_rate += GameConstants.getDiceStat(buff.intValue(), 4);
            dam_r *= (GameConstants.getDiceStat(buff.intValue(), 5) + 100.0) / 100.0;
            bossdam_r *= (GameConstants.getDiceStat(buff.intValue(), 5) + 100.0) / 100.0;
            expBuff *= (GameConstants.getDiceStat(buff.intValue(), 6) + 100.0) / 100.0;
        }
//        buff = chra.getBuffedValue(MapleBuffStatus.HP_BOOST_PERCENT);
//        if (buff != null) {
//            percent_hp += buff.intValue();
//        }
//        buff = chra.getBuffedValue(MapleBuffStatus.MP_BOOST_PERCENT);
//        if (buff != null) {
//            percent_mp += buff.intValue();
//        }
//        buff = chra.getBuffedValue(MapleBuffStatus.DEFENCE_BOOST_R);
//        if (buff != null) {
//            percent_wdef += buff.intValue();
//            percent_mdef += buff.intValue();
//        }
        buff = chra.getBuffedValue(MapleBuffStatus.ABNORMAL_STATUS_R);
        if (buff != null) {
            ASR += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStatus.ELEMENTAL_STATUS_R);
        if (buff != null) {
            TER += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStatus.INFINITY);
        if (buff != null) {
            percent_matk += buff - 1;
        }
        buff = chra.getBuffedValue(MapleBuffStatus.ONYX_SHROUD);
        if (buff != null) {
            dodgeChance += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStatus.PVP_DAMAGE);
        if (buff != null) {
            pvpDamage += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStatus.PVP_ATTACK);
        if (buff != null) {
            pvpDamage += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStatus.FELINE_BERSERK);
        if (buff != null) {
            percent_hp += buff;
        }
        eff = chra.getStatForBuff(MapleBuffStatus.BLUE_AURA);
        if (eff != null) {
            percent_wdef += eff.getZ() + eff.getY();
            percent_mdef += eff.getZ() + eff.getY();

        }
        buff = chra.getBuffedValue(MapleBuffStatus.CONVERSION);
        if (buff != null) {
            percent_hp += buff;
        } else {
            buff = chra.getBuffedValue(MapleBuffStatus.MAXHP);
            if (buff != null) {
                percent_hp += buff;
            }
        }
        buff = chra.getBuffedValue(MapleBuffStatus.MAXMP);
        if (buff != null) {
            percent_mp += buff;
        }
        buff = chra.getBuffedValue(MapleBuffStatus.MP_BUFF);
        if (buff != null) {
            percent_mp += buff;
        }
        buff = chra.getBuffedSkill_X(MapleBuffStatus.BUFF_MASTERY);
        if (buff != null) {
            BuffUP_Skill += buff;
        }
        if (GameConstants.isKOC(chra.getJob())) {
            bx = SkillFactory.getSkill(2000006);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0 && bx != null) {
                eff = bx.getEffect(bof);
                percent_hp += eff.getX();
                percent_mp += eff.getX();
            }
        }
        switch (chra.getJob()) {
            case 200:
            case 210:
            case 211:
            case 212:
            case 220:
            case 221:
            case 222:
            case 230:
            case 231:
            case 232: {
                bx = SkillFactory.getSkill(2000006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_mp += bx.getEffect(bof).getPercentMP();
                }
                break;
            }
            case 1200:
            case 1210:
            case 1211:
            case 1212: {
                bx = SkillFactory.getSkill(12000005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_mp += bx.getEffect(bof).getPercentMP();
                }
                bx = SkillFactory.getSkill(12110000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    TER += bx.getEffect(bof).getX();
                }
                break;
            }
            case 1100:
            case 1110:
            case 1111:
            case 1112: {
                bx = SkillFactory.getSkill(11000005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_hp += bx.getEffect(bof).getPercentHP();
                }
                break;
            }
            case 2400:
            case 2410:
            case 2411:
            case 2412: { // Phantom
                // Blanc Carte - 24100003
                // Cane Mastery - 24100004
                // Luck of Phantom Thief - 24111002
                // 24111003- uses monlight statEffect, but is Misfortune Protection
                // 24110004 - Flash and Flee -> active
                // 24111005 - Moonlight
                // 24111006 - Phantom Charge
                // 24111008- Breeze Carte, (hidden), linked to phantom charge
                // 24121000 - Ultimate Drive
                // 24120002 - Noir Carte
                // 24121003 - Twilight
                // 24121004 - Pray of Aria
                // 24121005 - Tempest of Card
                // 24120006 - Cane Expert
                // 24121007 - Soul Steal
                // 24121008 - Maple Warrior
                // 24121009 - Hero's will
                // 24121010 - Some linked skill (Twilight)
                bx = SkillFactory.getSkill(24001002); // Swift Phantom
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    speed += eff.getPassiveSpeed();
                    jump += eff.getPassiveJump();
                }
                bx = SkillFactory.getSkill(24000003); // Quick Evasion
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dodgeChance += eff.getX();
                }
                bx = SkillFactory.getSkill(24100006); //Luck Monopoly
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    localluk += eff.getLukX();
                }
                bx = SkillFactory.getSkill(24110007); // Acute Sense
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    passive_sharpeye_rate += eff.getCr();
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                }
                bx = SkillFactory.getSkill(24111002); //Luck of Phantom Thief
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    localluk += eff.getLukX();
                }
                break;
            }
            case 501:
            case 530:
            case 531:
            case 532:
                defRange = 200;
                bx = SkillFactory.getSkill(5010003);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    watk += bx.getEffect(bof).getAttackX();
                }
                bx = SkillFactory.getSkill(5300008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    localstr += eff.getStrX();
                    localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(5311001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    damageIncrease.put(5301001, (int) bx.getEffect(bof).getDAMRate());
                }
                bx = SkillFactory.getSkill(5310007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    percent_hp += eff.getHpR();
                    ASR += eff.getASRRate();
                    percent_wdef += eff.getWDEFRate();
                }
                bx = SkillFactory.getSkill(5310006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    watk += bx.getEffect(bof).getAttackX();
                }
                bx = SkillFactory.getSkill(5321009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r *= (eff.getDAMRate() + 100.0) / 100.0;
                    bossdam_r *= (eff.getDAMRate() + 100.0) / 100.0;
                    ignoreTargetDEF += eff.getIgnoreMob();
                }
                break;
            case 3001:
            case 3100:
            case 3110:
            case 3111:
            case 3112:
                mpRecoverProp = 100;
                bx = SkillFactory.getSkill(31000003);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_hp += bx.getEffect(bof).getHpR();
                }
                bx = SkillFactory.getSkill(31100007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    damageIncrease.put(31000004, (int) eff.getDAMRate());
                    damageIncrease.put(31001006, (int) eff.getDAMRate());
                    damageIncrease.put(31001007, (int) eff.getDAMRate());
                    damageIncrease.put(31001008, (int) eff.getDAMRate());
                }
                bx = SkillFactory.getSkill(31100005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    localstr += eff.getStrX();
                    localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(31100010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    damageIncrease.put(31000004, (int) eff.getX());
                    damageIncrease.put(31001006, (int) eff.getX());
                    damageIncrease.put(31001007, (int) eff.getX());
                    damageIncrease.put(31001008, (int) eff.getX());
                }
                bx = SkillFactory.getSkill(31110007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r *= (eff.getDAMRate() + 100.0) / 100.0;
                    bossdam_r *= (eff.getDAMRate() + 100.0) / 100.0;
                }
                bx = SkillFactory.getSkill(31110008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dodgeChance += eff.getX();
                    // HACK: shouldn't be here
                    hpRecoverPercent += eff.getY();
                    hpRecoverProp += eff.getX();
                    //mpRecover += eff.getY(); // handle in takeDamage
                    //mpRecoverProp += eff.getX();
                }
                bx = SkillFactory.getSkill(31110009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    mpRecover += 1;
                    mpRecoverProp += eff.getProb();
                }
                bx = SkillFactory.getSkill(31110006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r *= (eff.getX() + 100.0) / 100.0;
                    bossdam_r *= (eff.getX() + 100.0) / 100.0;
                    passive_sharpeye_rate += eff.getY();
                }
                bx = SkillFactory.getSkill(31121006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    ignoreTargetDEF += bx.getEffect(bof).getIgnoreMob();
                }
                bx = SkillFactory.getSkill(31120011);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    damageIncrease.put(31000004, (int) eff.getX());
                    damageIncrease.put(31001006, (int) eff.getX());
                    damageIncrease.put(31001007, (int) eff.getX());
                    damageIncrease.put(31001008, (int) eff.getX());
                }
                bx = SkillFactory.getSkill(31120008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    watk += eff.getAttackX();
                    trueMastery += eff.getMastery();
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                }
                bx = SkillFactory.getSkill(31120009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_wdef += bx.getEffect(bof).getT();
                }
                bx = SkillFactory.getSkill(30010112);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    bossdam_r += eff.getBossDamage();
                    mpRecover += eff.getX();
                    mpRecoverProp += eff.getBossDamage(); //yes
                }
                bx = SkillFactory.getSkill(30010185);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    chra.getTrait(MapleTraitType.will).addLocalExp(GameConstants.getTraitExpNeededForLevel(eff.getY()));
                    chra.getTrait(MapleTraitType.charisma).addLocalExp(GameConstants.getTraitExpNeededForLevel(eff.getZ()));
                }
                bx = SkillFactory.getSkill(30010111);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    hpRecoverPercent += eff.getX();
                    hpRecoverProp += eff.getProb(); //yes
                }
                break;
            case 510:
            case 511:
            case 512: {
                bx = SkillFactory.getSkill(5100009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_hp += bx.getEffect(bof).getPercentHP();
                }
                break;
            }
            case 1510:
            case 1511:
            case 1512: {
                bx = SkillFactory.getSkill(15100007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_hp += bx.getEffect(bof).getPercentHP();
                }
                break;
            }
            case 400:
            case 410:
            case 411:
            case 412: {
                defRange = 200;
                bx = SkillFactory.getSkill(4000001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    defRange += bx.getEffect(bof).getRange();
                }
                bx = SkillFactory.getSkill(4100006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    percent_hp += eff.getPercentHP();
                    ASR += eff.getASRRate();
                    TER += eff.getTERRate();
                }
                break;
            }
            case 420:
            case 421:
            case 422: {
                bx = SkillFactory.getSkill(4200006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    percent_hp += eff.getPercentHP();
                    ASR += eff.getASRRate();
                    TER += eff.getTERRate();
                }
                bx = SkillFactory.getSkill(4210000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    percent_wdef += eff.getX();
                    percent_mdef += eff.getX();
                }
                break;
            }
            case 431:
            case 432:
            case 433:
            case 434: {
                bx = SkillFactory.getSkill(4310004);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    percent_hp += eff.getPercentHP();
                    ASR += eff.getASRRate();
                    TER += eff.getTERRate();
                }
                bx = SkillFactory.getSkill(4341006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    percent_wdef += eff.getWDEFRate();
                    percent_mdef += eff.getMDEFRate();
                }
                break;
            }
            case 100:
            case 110:
            case 111:
            case 112:
            case 120:
            case 121:
            case 122:
            case 130:
            case 131:
            case 132: {
                bx = SkillFactory.getSkill(1000006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_hp += bx.getEffect(bof).getPercentHP();
                }
                bx = SkillFactory.getSkill(1210001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    percent_wdef += eff.getX();
                    percent_mdef += eff.getX();
                }
                bx = SkillFactory.getSkill(1220005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_wdef += bx.getEffect(bof).getT();
                }
                bx = SkillFactory.getSkill(1220010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    trueMastery += bx.getEffect(bof).getMastery();
                }
                bx = SkillFactory.getSkill(1310000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    TER += bx.getEffect(bof).getX();
                }
                break;
            }
            case 322: { // Crossbowman
                bx = SkillFactory.getSkill(3220004);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    watk += eff.getX();
                    trueMastery += eff.getMastery();
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                }
                bx = SkillFactory.getSkill(3220009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    percent_hp += eff.getPercentHP();
                    ignoreTargetDEF += eff.getIgnoreMob();
                }
                bx = SkillFactory.getSkill(3220005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && chra.getBuffedValue(MapleBuffStatus.SPIRIT_LINK) != null) {
                    eff = bx.getEffect(bof);
                    percent_hp += eff.getX();
                    dam_r *= (eff.getDamage() + 100.0) / 100.0;
                    bossdam_r *= (eff.getDamage() + 100.0) / 100.0;
                }
                break;
            }
            case 312: { // Bowmaster
                bx = SkillFactory.getSkill(3120005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    watk += bx.getEffect(bof).getX();
                }
                bx = SkillFactory.getSkill(3120011);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    percent_hp += eff.getPercentHP();
                    ignoreTargetDEF += eff.getIgnoreMob();
                }
                bx = SkillFactory.getSkill(3120006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0 && chra.getBuffedValue(MapleBuffStatus.SPIRIT_LINK) != null) {
                    eff = bx.getEffect(bof);
                    percent_hp += eff.getX();
                    dam_r *= (eff.getDamage() + 100.0) / 100.0;
                    bossdam_r *= (eff.getDamage() + 100.0) / 100.0;
                }
                break;
            }
            case 3510:
            case 3511:
            case 3512:
                defRange = 200;
                bx = SkillFactory.getSkill(35100000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    watk += bx.getEffect(bof).getAttackX();
                }
                bx = SkillFactory.getSkill(35120000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    trueMastery += bx.getEffect(bof).getMastery();
                }
                break;
            case 3211:
            case 3212:
                bx = SkillFactory.getSkill(32110000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    ASR += bx.getEffect(bof).getASRRate();
                    TER += bx.getEffect(bof).getTERRate();
                }
                bx = SkillFactory.getSkill(32110001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r *= (eff.getDAMRate() + 100.0) / 100.0;
                    bossdam_r *= (eff.getDAMRate() + 100.0) / 100.0;
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                }
                bx = SkillFactory.getSkill(32120000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    magic += bx.getEffect(bof).getMagicX();
                }
                bx = SkillFactory.getSkill(32120001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    dodgeChance += bx.getEffect(bof).getER();
                }
                bx = SkillFactory.getSkill(32120009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    percent_hp += bx.getEffect(bof).getPercentHP();
                }
                break;
            case 3300:
            case 3310:
            case 3311:
            case 3312:
                defRange = 200;
                bx = SkillFactory.getSkill(33120000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    watk += eff.getX();
                    trueMastery += eff.getMastery();
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                }
                bx = SkillFactory.getSkill(33110000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r *= (eff.getDamage() + 100.0) / 100.0;
                    bossdam_r *= (eff.getDamage() + 100.0) / 100.0;
                }
                bx = SkillFactory.getSkill(33120010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    ignoreTargetDEF += eff.getIgnoreMob();
                    dodgeChance += eff.getER();
                }
                bx = SkillFactory.getSkill(32110001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r *= (eff.getDAMRate() + 100.0) / 100.0;
                    bossdam_r *= (eff.getDAMRate() + 100.0) / 100.0;
                }
                break;
            case 2200:
            case 2210:
            case 2211:
            case 2212:
            case 2213:
            case 2214:
            case 2215:
            case 2216:
            case 2217:
            case 2218: {
                magic += chra.getTotalSkillLevel(SkillFactory.getSkill(22000000));
                bx = SkillFactory.getSkill(22150000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    mpconPercent += eff.getX() - 100;
                    dam_r *= eff.getY() / 100.0;
                    bossdam_r *= eff.getY() / 100.0;
                }
                bx = SkillFactory.getSkill(22160000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r *= (eff.getDamage() + 100.0) / 100.0;
                    bossdam_r *= (eff.getDamage() + 100.0) / 100.0;
                }
                bx = SkillFactory.getSkill(22170001); // magic mastery, this is an invisible skill
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    magic += eff.getX();
                    trueMastery += eff.getMastery();
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                }
                break;
            }
            case 2112: {
                bx = SkillFactory.getSkill(21120001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    watk += eff.getX();
                    trueMastery += eff.getMastery();
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                }
                break;
            }
        }
        this.watk += Math.floor((watk * percent_atk) / 100.0f);
        this.magic += Math.floor((magic * percent_matk) / 100.0f); //or should this go before
        this.localint_ += Math.floor((localint_ * percent_matk) / 100.0f); //overpowered..
        bx = SkillFactory.getSkill(80000000);
        bof = chra.getSkillLevel(bx);
        if (bof > 0) {
            eff = bx.getEffect(bof);
            localstr += eff.getStrX();
            localdex += eff.getDexX();
            localint_ += eff.getIntX();
            localluk += eff.getLukX();
            percent_hp += eff.getHpR();
            percent_mp += eff.getMpR();
        }
        bx = SkillFactory.getSkill(80000001);
        bof = chra.getSkillLevel(bx);
        if (bof > 0) {
            eff = bx.getEffect(bof);
            bossdam_r += eff.getBossDamage();
        }
        bx = SkillFactory.getSkill(80001040);
        bof = chra.getSkillLevel(bx);
        if (bof > 0) {
            expBuff *= (bx.getEffect(bof).getEXPRate() + 100.0) / 100.0;
        }
        if (GameConstants.isAdventurer(chra.getJob())) {
            bx = SkillFactory.getSkill(74);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                levelBonus += bx.getEffect(bof).getX();
            }

            bx = SkillFactory.getSkill(80);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                levelBonus += bx.getEffect(bof).getX();
            }

            bx = SkillFactory.getSkill(10074);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                levelBonus += bx.getEffect(bof).getX();
            }

            bx = SkillFactory.getSkill(10080);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                levelBonus += bx.getEffect(bof).getX();
            }

            bx = SkillFactory.getSkill(110);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
                localint_ += eff.getIntX();
                localluk += eff.getLukX();
                percent_hp += eff.getHpR();
                percent_mp += eff.getMpR();
            }

            bx = SkillFactory.getSkill(10110);
            bof = chra.getSkillLevel(bx);
            if (bof > 0) {
                eff = bx.getEffect(bof);
                localstr += eff.getStrX();
                localdex += eff.getDexX();
                localint_ += eff.getIntX();
                localluk += eff.getLukX();
                percent_hp += eff.getHpR();
                percent_mp += eff.getMpR();
            }
        }

        if (chra.getGuildId() > 0) {
            MapleGuild g = World.Guild.getGuild(chra.getGuildId());
            if (g != null && g.getSkills().size() > 0) {
                long now = System.currentTimeMillis();
                for (MapleGuildSkill gs : g.getSkills()) {
                    if (gs.timestamp > now && gs.activator.length() > 0) {
                        MapleStatEffect e = SkillFactory.getSkill(gs.skillID).getEffect(gs.level);
                        passive_sharpeye_rate += e.getCr();
                        watk += e.getAttackX();
                        magic += e.getMagicX();
                        expBuff *= (e.getEXPRate() + 100.0) / 100.0;
                        dodgeChance += e.getER();
                        percent_wdef += e.getWDEFRate();
                        percent_mdef += e.getMDEFRate();
                    }
                }
            }
        }

        localmaxhp_ += Math.floor((percent_hp * localmaxhp_) / 100.0f);
        localmaxmp_ += Math.floor((percent_mp * localmaxmp_) / 100.0f);
        wdef += Math.min(30000, Math.floor((wdef * percent_wdef) / 100.0f));
        mdef += Math.min(30000, Math.floor((wdef * percent_mdef) / 100.0f));
        //magic = Math.min(magic, 1999); //buffs can make it higher
        buff = chra.getBuffedValue(MapleBuffStatus.STR);
        if (buff != null) {
            localstr += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStatus.DEX);
        if (buff != null) {
            localdex += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStatus.INT);
        if (buff != null) {
            localint_ += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStatus.LUK);
        if (buff != null) {
            localluk += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStatus.ANGEL_STAT);
        if (buff != null) {
            localstr += buff.intValue();
            localdex += buff.intValue();
            localint_ += buff.intValue();
            localluk += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStatus.ENHANCED_MAXHP);
        if (buff != null) {
            localmaxhp_ += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStatus.ENHANCED_MAXMP);
        if (buff != null) {
            localmaxmp_ += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStatus.ENHANCED_WDEF);
        if (buff != null) {
            wdef += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStatus.ENHANCED_MDEF);
        if (buff != null) {
            mdef += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStatus.WDEF);
        if (buff != null) {
            wdef += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStatus.WDEF);
        if (buff != null) {
            mdef += buff.intValue();
        }

        buff = chra.getBuffedValue(MapleBuffStatus.HP_BOOST);
        if (buff != null) {
            localmaxhp_ += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStatus.MP_BOOST);
        if (buff != null) {
            localmaxmp_ += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStatus.MAPLE_WARRIOR);
        if (buff != null) {
            double d = buff.doubleValue() / 100.0;
            localstr += d * str; //base only
            localdex += d * dex;
            localluk += d * luk;
            localint_ += d * int_;
        }
        buff = chra.getBuffedValue(MapleBuffStatus.ECHO_OF_HERO);
        if (buff != null) {
            double d = buff.doubleValue() / 100.0;
            watk += (int) (watk * d);
            magic += (int) (magic * d);
        }
        buff = chra.getBuffedValue(MapleBuffStatus.ARAN_COMBO);
        if (buff != null) {
            watk += buff.intValue() / 10;
        }
        buff = chra.getBuffedValue(MapleBuffStatus.MESOGUARD);
        if (buff != null) {
            mesoGuardMeso += buff.doubleValue();
        }
        bx = SkillFactory.getSkill(GameConstants.getBOF_ForJob(chra.getJob()));
        bof = chra.getSkillLevel(bx);
        if (bof > 0) {
            eff = bx.getEffect(bof);
            watk += eff.getX();
            magic += eff.getY();
            accuracy += eff.getX();
        }

        bx = SkillFactory.getSkill(GameConstants.getEmpress_ForJob(chra.getJob()));
        bof = chra.getSkillLevel(bx);
        if (bof > 0) {
            eff = bx.getEffect(bof);
            watk += eff.getX();
            magic += eff.getY();
            accuracy += eff.getZ();
        }

        buff = chra.getBuffedValue(MapleBuffStatus.EXPRATE);
        if (buff != null) {
            expBuff *= buff.doubleValue() / 100.0;
        }
        buff = chra.getBuffedValue(MapleBuffStatus.DROP_RATE);
        if (buff != null) {
            dropBuff *= buff.doubleValue() / 100.0;
        }
        buff = chra.getBuffedValue(MapleBuffStatus.ACASH_RATE);
        if (buff != null) {
            cashBuff *= buff.doubleValue() / 100.0;
        }
        buff = chra.getBuffedValue(MapleBuffStatus.MESO_RATE);
        if (buff != null) {
            mesoBuff *= buff.doubleValue() / 100.0;
        }
        buff = chra.getBuffedValue(MapleBuffStatus.MESOUP);
        if (buff != null) {
            mesoBuff *= buff.doubleValue() / 100.0;
        }
        buff = chra.getBuffedValue(MapleBuffStatus.ACC);
        if (buff != null) {
            accuracy += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStatus.ANGEL_ACC);
        if (buff != null) {
            accuracy += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStatus.ANGEL_ATK);
        if (buff != null) {
            watk += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStatus.ANGEL_MATK);
        if (buff != null) {
            magic += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStatus.WATK);
        if (buff != null) {
            watk += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStatus.SPIRIT_SURGE);
        if (buff != null) {
            passive_sharpeye_rate += buff.intValue();
            dam_r *= (buff.intValue() + 100.0) / 100.0;
            bossdam_r *= (buff.intValue() + 100.0) / 100.0;
        }
        buff = chra.getBuffedValue(MapleBuffStatus.ENHANCED_WATK);
        if (buff != null) {
            watk += buff.intValue();
        }
        eff = chra.getStatForBuff(MapleBuffStatus.ENERGY_CHARGE);
        if (eff != null) {
            watk += eff.getWatk();
            accuracy += eff.getAcc();
        }
        buff = chra.getBuffedValue(MapleBuffStatus.MATK);
        if (buff != null) {
            magic += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStatus.SPEED);
        if (buff != null) {
            speed += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStatus.JUMP);
        if (buff != null) {
            jump += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStatus.DASH_SPEED);
        if (buff != null) {
            speed += buff.intValue();
        }
        buff = chra.getBuffedValue(MapleBuffStatus.DASH_JUMP);
        if (buff != null) {
            jump += buff.intValue();
        }
        eff = chra.getStatForBuff(MapleBuffStatus.HIDDEN_POTENTIAL);
        if (eff != null) {
            passive_sharpeye_rate = 100; //INTENSE
            ASR = 100; //INTENSE

            wdef += eff.getX();
            mdef += eff.getX();
            watk += eff.getX();
            magic += eff.getX();
        }
        buff = chra.getBuffedValue(MapleBuffStatus.DAMAGE_BUFF);
        if (buff != null) {
            dam_r *= (buff.doubleValue() + 100.0) / 100.0;
            bossdam_r *= (buff.doubleValue() + 100.0) / 100.0;
        }
        buff = chra.getBuffedSkill_Y(MapleBuffStatus.FINAL_CUT);
        if (buff != null) {
            dam_r *= buff.doubleValue() / 100.0;
            bossdam_r *= buff.doubleValue() / 100.0;
        }
        buff = chra.getBuffedSkill_Y(MapleBuffStatus.OWL_SPIRIT);
        if (buff != null) {
            dam_r *= buff.doubleValue() / 100.0;
            bossdam_r *= buff.doubleValue() / 100.0;
        }
        buff = chra.getBuffedSkill_X(MapleBuffStatus.BERSERK_FURY);
        if (buff != null) {
            dam_r *= buff.doubleValue() / 100.0;
            bossdam_r *= buff.doubleValue() / 100.0;
        }
        eff = chra.getStatForBuff(MapleBuffStatus.BLESS);
        if (eff != null) {
            watk += eff.getX();
            magic += eff.getY();
            accuracy += eff.getV();
        }
        buff = chra.getBuffedSkill_X(MapleBuffStatus.CONCENTRATE);
        if (buff != null) {
            mpconReduce += buff.intValue();
        }
        eff = chra.getStatForBuff(MapleBuffStatus.HOLY_SHIELD);
        if (eff != null) {
            watk += eff.getX();
            magic += eff.getY();
            accuracy += eff.getV();
            mpconReduce += eff.getMPConReduce();
        }
        eff = chra.getStatForBuff(MapleBuffStatus.MAGIC_RESISTANCE);
        if (eff != null) {
            ASR += eff.getX();
        }

        eff = chra.getStatForBuff(MapleBuffStatus.COMBO);
        buff = chra.getBuffedValue(MapleBuffStatus.COMBO);
        if (eff != null && buff != null) {
            dam_r *= ((100.0 + ((eff.getV() + eff.getDAMRate()) * (buff.intValue() - 1))) / 100.0);
            bossdam_r *= ((100.0 + ((eff.getV() + eff.getDAMRate()) * (buff.intValue() - 1))) / 100.0);
        }
        eff = chra.getStatForBuff(MapleBuffStatus.SUMMON);
        if (eff != null) {
            if (eff.getSourceId() == 35121010) { //amp
                dam_r *= (eff.getX() + 100.0) / 100.0;
                bossdam_r *= (eff.getX() + 100.0) / 100.0;
            }
        }
        eff = chra.getStatForBuff(MapleBuffStatus.DARK_AURA);
        if (eff != null) {
            dam_r *= (eff.getX() + 100.0) / 100.0;
            bossdam_r *= (eff.getX() + 100.0) / 100.0;
        }
        eff = chra.getStatForBuff(MapleBuffStatus.BODY_BOOST);
        if (eff != null) {
            dam_r *= (eff.getV() + 100.0) / 100.0;
            bossdam_r *= (eff.getV() + 100.0) / 100.0;
        }
        eff = chra.getStatForBuff(MapleBuffStatus.BEHOLDER);
        if (eff != null) {
            trueMastery += eff.getMastery();
        }
        eff = chra.getStatForBuff(MapleBuffStatus.MECH_CHANGE);
        if (eff != null) {
            passive_sharpeye_rate += eff.getCr();
        }
        eff = chra.getStatForBuff(MapleBuffStatus.PYRAMID_PQ);
        if (eff != null && eff.getBerserk() > 0) {
            dam_r *= eff.getBerserk() / 100.0;
            bossdam_r *= eff.getBerserk() / 100.0;
        }
        eff = chra.getStatForBuff(MapleBuffStatus.WK_CHARGE);
        if (eff != null) {
            dam_r *= eff.getDamage() / 100.0;
            bossdam_r *= eff.getDamage() / 100.0;
        }
        eff = chra.getStatForBuff(MapleBuffStatus.PICKPOCKET);
        if (eff != null) {
            pickRate = eff.getProb();
        }
        eff = chra.getStatForBuff(MapleBuffStatus.PIRATES_REVENGE);
        if (eff != null) {
            dam_r *= (eff.getDAMRate() + 100.0) / 100.0;
            bossdam_r *= (eff.getDAMRate() + 100.0) / 100.0;
        }
        eff = chra.getStatForBuff(MapleBuffStatus.LIGHTNING_CHARGE);
        if (eff != null) {
            dam_r *= eff.getDamage() / 100.0;
            bossdam_r *= eff.getDamage() / 100.0;
        }
        eff = chra.getStatForBuff(MapleBuffStatus.WIND_WALK);
        if (eff != null) {
            dam_r *= eff.getDamage() / 100.0;
            bossdam_r *= eff.getDamage() / 100.0;
        }
        eff = chra.getStatForBuff(MapleBuffStatus.DIVINE_SHIELD);
        if (eff != null) {
            watk += eff.getEnhancedWatk();
        }
        buff = chra.getBuffedSkill_Y(MapleBuffStatus.DARK_SIGHT);
        if (buff != null) {
            dam_r *= (buff.intValue() + 100.0) / 100.0;
            bossdam_r *= (buff.intValue() + 100.0) / 100.0;
        }
//        buff = chra.getBuffedSkill_X(MapleBuffStatus.ENRAGE);
//        if (buff != null) {
//            dam_r *= (buff.intValue() + 100.0) / 100.0;
//            bossdam_r *= (buff.intValue() + 100.0) / 100.0;
//        }
        buff = chra.getBuffedSkill_X(MapleBuffStatus.COMBAT_ORDERS);
        if (buff != null) {
            combatOrders += buff.intValue();
        }
        eff = chra.getStatForBuff(MapleBuffStatus.SHARP_EYES);
        if (eff != null) {
            passive_sharpeye_rate += eff.getX();
            passive_sharpeye_percent += eff.getCriticalMax();
        }
        buff = chra.getBuffedValue(MapleBuffStatus.CRITICAL_RATE_BUFF);
        if (buff != null) {
            passive_sharpeye_rate += buff.intValue();
        }
        if (speed > 140) {
            speed = 140;
        }
        if (jump > 123) {
            jump = 123;
        }
        buff = chra.getBuffedValue(MapleBuffStatus.MONSTER_RIDING);
        if (buff != null) {
            jump = 120;
            switch (buff.intValue()) {
                case 1:
                    speed = 150;
                    break;
                case 2:
                    speed = 170;
                    break;
                case 3:
                    speed = 180;
                    break;
                default:
                    speed = 200; //lol
                    break;
            }

        }
        hands = this.localdex + this.localint_ + this.localluk;
        calculateFame(chra);
        ignoreTargetDEF += chra.getTrait(MapleTraitType.charisma).getLevel() / 10;
        pvpDamage += chra.getTrait(MapleTraitType.charisma).getLevel() / 10;


        localmaxhp_ += (chra.getTrait(MapleTraitType.will).getLevel() / 5) * (percent_hp + 100);// 性向系統血量百分比後處理
//        localmaxhp_ += Math.floor((percent_hp * localmaxhp_) / 100.0f);

        ASR += chra.getTrait(MapleTraitType.will).getLevel() / 5;

        accuracy += chra.getTrait(MapleTraitType.insight).getLevel() * 15 / 10;

        localmaxhp = Math.min(99999, Math.abs(Math.max(-99999, localmaxhp_)));
        localmaxmp = Math.min(99999, Math.abs(Math.max(-99999, localmaxmp_)));

        if (chra.getEventInstance() != null && chra.getEventInstance().getName().startsWith("PVP")) { //hack
            localmaxhp = Math.min(40000, localmaxhp * 3); //approximate.
            localmaxmp = Math.min(20000, localmaxmp * 2);
            //not sure on 20000 cap
            for (int i : pvpSkills) {
                Skill skil = SkillFactory.getSkill(i);
                if (skil != null && skil.canBeLearnedBy(chra.getJob())) {
                    sData.put(skil, new SkillEntry((byte) 1, (byte) 0, -1));
                    eff = skil.getEffect(1);
                    switch ((i / 1000000) % 10) {
                        case 1:
                            if (eff.getX() > 0) {
                                pvpDamage += (wdef / eff.getX());
                            }
                            break;
                        case 3:
                            hpRecoverProp += eff.getProb();
                            hpRecover += eff.getX();
                            mpRecoverProp += eff.getProb();
                            mpRecover += eff.getX();
                            break;
                        case 5:
                            passive_sharpeye_rate += eff.getProb();
                            passive_sharpeye_percent = 100;
                            break;
                    }
                    break;
                }
            }
            eff = chra.getStatForBuff(MapleBuffStatus.MORPH);
            if (eff != null && eff.getSourceId() % 10000 == 1105) { //ice knight
                localmaxhp = 99999;
                localmaxmp = 99999;
            }
        }
        chra.changeSkillLevel_Skip(sData, false);
        if (GameConstants.isDemon(chra.getJob())) {
            localmaxmp = GameConstants.getMPByJob(chra.getJob());
        }
        //damage increase
        switch (chra.getJob()) {
            case 210:
            case 211:
            case 212: { // IL
                bx = SkillFactory.getSkill(2100007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    localint_ += bx.getEffect(bof).getIntX();
                }
                bx = SkillFactory.getSkill(2110000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dotTime += eff.getX();
                    dot += eff.getZ();
                }
                bx = SkillFactory.getSkill(2110001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    mpconPercent += eff.getX() - 100;
                    dam_r *= eff.getY() / 100.0;
                    bossdam_r *= eff.getY() / 100.0;
                }
                bx = SkillFactory.getSkill(2121003);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    damageIncrease.put(2111003, (int) eff.getX());
                }
                bx = SkillFactory.getSkill(2120009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    magic += eff.getMagicX();
                    BuffUP_Skill += eff.getX();
                }
                bx = SkillFactory.getSkill(2121005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    TER += bx.getEffect(bof).getTERRate();
                }
                bx = SkillFactory.getSkill(2121009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    magic += bx.getEffect(bof).getMagicX();
                }
                bx = SkillFactory.getSkill(2120010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r *= (eff.getX() * eff.getY() + 100.0) / 100.0;
                    bossdam_r *= (eff.getX() * eff.getY() + 100.0) / 100.0;
                    ignoreTargetDEF += eff.getIgnoreMob();
                }
                break;
            }
            case 220:
            case 221:
            case 222: { // IL
                bx = SkillFactory.getSkill(2200007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    localint_ += bx.getEffect(bof).getIntX();
                }
                bx = SkillFactory.getSkill(2210000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    dot += bx.getEffect(bof).getZ();
                }
                bx = SkillFactory.getSkill(2210001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    mpconPercent += eff.getX() - 100;
                    dam_r *= eff.getY() / 100.0;
                    bossdam_r *= eff.getY() / 100.0;
                }
                bx = SkillFactory.getSkill(2220009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    magic += eff.getMagicX();
                    BuffUP_Skill += eff.getX();
                }
                bx = SkillFactory.getSkill(2221005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    TER += bx.getEffect(bof).getTERRate();
                }
                bx = SkillFactory.getSkill(2221009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    magic += bx.getEffect(bof).getMagicX();
                }
                bx = SkillFactory.getSkill(2220010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r *= (eff.getX() * eff.getY() + 100.0) / 100.0;
                    bossdam_r *= (eff.getX() * eff.getY() + 100.0) / 100.0;
                    ignoreTargetDEF += eff.getIgnoreMob();
                }
                break;
            }
            case 1211:
            case 1212: { // flame
                bx = SkillFactory.getSkill(12110001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    mpconPercent += eff.getX() - 100;
                    dam_r *= eff.getY() / 100.0;
                    bossdam_r *= eff.getY() / 100.0;
                }

                bx = SkillFactory.getSkill(12111004);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    TER += bx.getEffect(bof).getTERRate();
                }
                break;
            }
            case 230:
            case 231:
            case 232: { // Bishop
                bx = SkillFactory.getSkill(2300007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    localint_ += bx.getEffect(bof).getIntX();
                }
                bx = SkillFactory.getSkill(2310008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    passive_sharpeye_rate += bx.getEffect(bof).getCr();
                }
                bx = SkillFactory.getSkill(2320010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    magic += eff.getMagicX();
                    BuffUP_Skill += eff.getX();
                }
                bx = SkillFactory.getSkill(2321010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    magic += bx.getEffect(bof).getMagicX();
                }
                bx = SkillFactory.getSkill(2320005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    ASR += bx.getEffect(bof).getASRRate();
                }
                bx = SkillFactory.getSkill(2320011);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r *= (eff.getX() * eff.getY() + 100.0) / 100.0;
                    bossdam_r *= (eff.getX() * eff.getY() + 100.0) / 100.0;
                    ignoreTargetDEF += eff.getIgnoreMob();
                }
                break;
            }
            case 2001:
            case 2300:
            case 2310:
            case 2311:
            case 2312:
                defRange = 200;
                bx = SkillFactory.getSkill(20021110);
                bof = chra.getSkillLevel(bx);
                if (bof > 0) {
                    expBuff *= (bx.getEffect(bof).getEXPRate() + 100.0) / 100.0;
                }
                bx = SkillFactory.getSkill(20020112);
                bof = chra.getSkillLevel(bx);
                if (bof > 0) {
                    chra.getTrait(MapleTraitType.charm).addLocalExp(GameConstants.getTraitExpNeededForLevel(30));
                }
                bx = SkillFactory.getSkill(23000001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    dodgeChance += bx.getEffect(bof).getER();
                }
                bx = SkillFactory.getSkill(23100008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    localstr += eff.getStrX();
                    localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(23110004);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    dodgeChance += bx.getEffect(bof).getProb();
                }
                bx = SkillFactory.getSkill(23110004);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    damageIncrease.put(23101001, (int) bx.getEffect(bof).getDAMRate());
                }
                bx = SkillFactory.getSkill(23121004);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    dodgeChance += bx.getEffect(bof).getProb();
                }
                bx = SkillFactory.getSkill(23120009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    watk += bx.getEffect(bof).getX();
                }
                bx = SkillFactory.getSkill(23120010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    ignoreTargetDEF += bx.getEffect(bof).getX(); //or should we do 100?
                }
                bx = SkillFactory.getSkill(23120011);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    damageIncrease.put(23101001, (int) bx.getEffect(bof).getDAMRate());
                }
                bx = SkillFactory.getSkill(23120012);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    watk += bx.getEffect(bof).getAttackX();
                }
                break;
            case 1300:
            case 1310:
            case 1311:
            case 1312:
                defRange = 200;
                bx = SkillFactory.getSkill(13000001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    defRange += bx.getEffect(bof).getRange();
                }
                bx = SkillFactory.getSkill(13110008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    dodgeChance += bx.getEffect(bof).getER();
                }
                bx = SkillFactory.getSkill(13110003);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    trueMastery += eff.getMastery();
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                }
                break;
            case 300:
            case 310:
            case 311:
            case 312:
                defRange = 200;
                bx = SkillFactory.getSkill(3000002);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    defRange += bx.getEffect(bof).getRange();
                }
                bx = SkillFactory.getSkill(3100006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    damageIncrease.put(3001004, eff.getX());
                    damageIncrease.put(3001005, eff.getY());
                    localstr += eff.getStrX();
                    localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(3110007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    dodgeChance += bx.getEffect(bof).getER();
                }
                bx = SkillFactory.getSkill(3120005);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    trueMastery += eff.getMastery();
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                }
                break;
            case 320:
            case 321:
            case 322:
                defRange = 200;
                bx = SkillFactory.getSkill(3000002);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    defRange += bx.getEffect(bof).getRange();
                }
                bx = SkillFactory.getSkill(3200006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    damageIncrease.put(3001004, eff.getX());
                    damageIncrease.put(3001005, eff.getY());
                    localstr += eff.getStrX();
                    localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(3220010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    damageIncrease.put(3211006, bx.getEffect(bof).getDamage() - 150);
                }
                bx = SkillFactory.getSkill(3210007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    dodgeChance += bx.getEffect(bof).getER();
                }
                break;

            case 422:
                bx = SkillFactory.getSkill(4221007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) { //Savage Blow, Steal, and Assaulter
                    eff = bx.getEffect(bof);
                    damageIncrease.put(4201005, (int) eff.getDAMRate());
                    damageIncrease.put(4201004, (int) eff.getDAMRate());
                    damageIncrease.put(4211002, (int) eff.getDAMRate());
                }
                bx = SkillFactory.getSkill(4220009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    mesoBuff *= (eff.getMesoRate() + 100.0) / 100.0;
                    pickRate += eff.getU();
                    mesoGuard -= eff.getV();
                    mesoGuardMeso -= eff.getW();
                    damageIncrease.put(4211006, eff.getX());
                }
                break;
            case 433:
            case 434:
                bx = SkillFactory.getSkill(4330007);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    hpRecoverProp += eff.getProb();
                    hpRecoverPercent += eff.getX();
                }
                bx = SkillFactory.getSkill(4341002);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) { //Fatal Blow, Slash Storm, Tornado Spin, Bloody Storm, Upper Stab, and Flying Assaulter
                    eff = bx.getEffect(bof);
                    damageIncrease.put(4311002, (int) eff.getDAMRate());
                    damageIncrease.put(4311003, (int) eff.getDAMRate());
                    damageIncrease.put(4321000, (int) eff.getDAMRate());
                    damageIncrease.put(4321001, (int) eff.getDAMRate());
                    damageIncrease.put(4331000, (int) eff.getDAMRate());
                    damageIncrease.put(4331004, (int) eff.getDAMRate());
                    damageIncrease.put(4331005, (int) eff.getDAMRate());
                }
                bx = SkillFactory.getSkill(4341006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    dodgeChance += bx.getEffect(bof).getER();
                }
                break;
            case 2110:
            case 2111:
            case 2112: { // Aran
                bx = SkillFactory.getSkill(21101006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r *= (eff.getDAMRate() + 100.0) / 100.0;
                    bossdam_r *= (eff.getDAMRate() + 100.0) / 100.0;
                }
                bx = SkillFactory.getSkill(21110002);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    damageIncrease.put(21000004, bx.getEffect(bof).getW());
                }
                bx = SkillFactory.getSkill(21111010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    ignoreTargetDEF += bx.getEffect(bof).getIgnoreMob();
                }
                bx = SkillFactory.getSkill(21120002);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    damageIncrease.put(21100007, bx.getEffect(bof).getZ());
                }
                bx = SkillFactory.getSkill(21120011);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    damageIncrease.put(21100002, (int) eff.getDAMRate());
                    damageIncrease.put(21110003, (int) eff.getDAMRate());
                }
                break;
            }
            case 3511:
            case 3512:
                bx = SkillFactory.getSkill(35110014);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) { //ME-07 Drillhands, Atomic Hammer
                    eff = bx.getEffect(bof);
                    damageIncrease.put(35001003, (int) eff.getDAMRate());
                    damageIncrease.put(35101003, (int) eff.getDAMRate());
                }
                bx = SkillFactory.getSkill(35121006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) { //Satellite
                    eff = bx.getEffect(bof);
                    damageIncrease.put(35111001, (int) eff.getDAMRate());
                    damageIncrease.put(35111009, (int) eff.getDAMRate());
                    damageIncrease.put(35111010, (int) eff.getDAMRate());
                }
                bx = SkillFactory.getSkill(35120001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) { //Satellite
                    eff = bx.getEffect(bof);
                    damageIncrease.put(35111005, eff.getX());
                    damageIncrease.put(35111011, eff.getX());
                    damageIncrease.put(35121009, eff.getX());
                    damageIncrease.put(35121010, eff.getX());
                    damageIncrease.put(35121011, eff.getX());
                    BuffUP_Summon += eff.getY();
                }
                break;
            case 110:
            case 111:
            case 112:
                bx = SkillFactory.getSkill(1100009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    damageIncrease.put(1001004, eff.getX());
                    damageIncrease.put(1001005, eff.getY());
                    localstr += eff.getStrX();
                    localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(1110009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r *= eff.getDamage() / 100.0;
                    bossdam_r *= eff.getDamage() / 100.0;
                }
                bx = SkillFactory.getSkill(1120012);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    ignoreTargetDEF += bx.getEffect(bof).getIgnoreMob();
                }
                bx = SkillFactory.getSkill(1120013);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    watk += eff.getAttackX();
                    damageIncrease.put(1100002, (int) eff.getDamage());
                }
                break;
            case 120:
            case 121:
            case 122:
                bx = SkillFactory.getSkill(1200009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    damageIncrease.put(1001004, eff.getX());
                    damageIncrease.put(1001005, eff.getY());
                    localstr += eff.getStrX();
                    localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(1220006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    ASR += bx.getEffect(bof).getASRRate();
                    TER += bx.getEffect(bof).getTERRate();
                }
                break;
            case 511:
            case 512:
                bx = SkillFactory.getSkill(5110008);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) { //Backspin Blow, Double Uppercut, and Corkscrew Blow
                    eff = bx.getEffect(bof);
                    damageIncrease.put(5101002, eff.getX());
                    damageIncrease.put(5101003, eff.getY());
                    damageIncrease.put(5101004, eff.getZ());
                }
                break;
            case 520:
            case 521:
            case 522:
                defRange = 200;
                bx = SkillFactory.getSkill(5220001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) { //Flamethrower and Ice Splitter
                    eff = bx.getEffect(bof);
                    damageIncrease.put(5211004, (int) eff.getDamage());
                    damageIncrease.put(5211005, (int) eff.getDamage());
                }
                break;
            case 130:
            case 131:
            case 132:
                bx = SkillFactory.getSkill(1300009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    damageIncrease.put(1001004, eff.getX());
                    damageIncrease.put(1001005, eff.getY());
                    localstr += eff.getStrX();
                    localdex += eff.getDexX();
                }
                bx = SkillFactory.getSkill(1310009);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    passive_sharpeye_rate += eff.getCr();
                    passive_sharpeye_min_percent += eff.getCriticalMin();
                    hpRecoverProp += eff.getProb();
                    hpRecoverPercent += eff.getX();
                }
                bx = SkillFactory.getSkill(1320006);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    dam_r *= (eff.getDamage() + 100.0) / 100.0;
                    bossdam_r *= (eff.getDamage() + 100.0) / 100.0;
                }
                break;
            case 411:
            case 412:
                bx = SkillFactory.getSkill(4110000);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    RecoveryUP += eff.getX() - 100;
                    BuffUP += eff.getY() - 100;
                }
                bx = SkillFactory.getSkill(4120010);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) { //Lucky Seven, Drain, Avenger, Shadow Meso
                    eff = bx.getEffect(bof);
                    damageIncrease.put(4001344, (int) eff.getDAMRate());
                    damageIncrease.put(4101005, (int) eff.getDAMRate());
                    damageIncrease.put(4111004, (int) eff.getDAMRate());
                    damageIncrease.put(4111005, (int) eff.getDAMRate());
                }
                break;
            case 1400:
            case 1410:
            case 1411:
            case 1412:
                defRange = 200;
                bx = SkillFactory.getSkill(14110003);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    eff = bx.getEffect(bof);
                    RecoveryUP += eff.getX() - 100;
                    BuffUP += eff.getY() - 100;
                }
                bx = SkillFactory.getSkill(14000001);
                bof = chra.getTotalSkillLevel(bx);
                if (bof > 0) {
                    defRange += bx.getEffect(bof).getRange();
                }
                break;
        }
        if (GameConstants.isResist(chra.getJob())) {
            bx = SkillFactory.getSkill(30000002);
            bof = chra.getTotalSkillLevel(bx);
            if (bof > 0) {
                RecoveryUP += bx.getEffect(bof).getX() - 100;
            }
        }
        // CalcPassive_SharpEye(chra);
        CalcPassive_Mastery(chra);
        recalcPVPRank(chra);
        if (first_login) {
            chra.silentEnforceMaxHpMp();
            relocHeal(chra);
        } else {
            chra.enforceMaxHpMp();
        }

        calculateMaxBaseDamage(Math.max(magic, watk), pvpDamage, chra);
        trueMastery = Math.min(100, trueMastery);
        passive_sharpeye_min_percent = (short) Math.min(passive_sharpeye_min_percent, passive_sharpeye_percent);
        if (oldmaxhp != 0 && oldmaxhp != localmaxhp) {
            chra.updatePartyMemberHP();
        }

        //[CUSTOM]: Inneral Abilities:
        //Apply formulas from InnerSkillValueHolder.java here;
        //PROOF OF CONCEPT. SEEMS TO WORK JUST FINE INGAME.
//        for (InnerSkillValueHolder ISVH : chra.getInnerSkills()) {
//            int x = ISVH.getSkillLevel();
//            switch (ISVH.getSkillId()) {
//                case 70000000:
//                    localstr += MapleStatEffect.parseEval("x", x);
//                    break; //strFX = x
//                case 70000001:
//                    localdex += MapleStatEffect.parseEval("x", x);
//                    break;
//                case 70000002:
//                    localint_ += MapleStatEffect.parseEval("x", x);
//                    break;
//                case 70000003:
//                    localluk += MapleStatEffect.parseEval("x", x);
//                    break;
//                case 70000004:
//                    accuracy += MapleStatEffect.parseEval("(10 * x)", x);
//                    break; //accX = 10 * x
//                case 70000005:
//                    evaR += MapleStatEffect.parseEval("(10 * x)", x);
//                    break;
//                case 70000006:
//                    wdef += MapleStatEffect.parseEval("(10 * x)", x);
//                    break;
//                case 70000007:
//                    mdef += MapleStatEffect.parseEval("(10 * x)", x);
//                    break;
//                case 70000008:
//                    localmaxhp += MapleStatEffect.parseEval("(15 * x)", x);
//                    break;//mhpX = x * 15
//                case 70000009:
//                    localmaxmp += MapleStatEffect.parseEval("(15 * x)", x);
//                    ;
//                    break;
//                case 70000010:
//                    jump += MapleStatEffect.parseEval("2*u(x/3)", x);
//                    break; //psdJump = 2 * u(x / 3)
//                case 70000011:
//                    speed += MapleStatEffect.parseEval("2*u(x/3)", x);
//                    break;
//                case 70000012:
//                    watk += MapleStatEffect.parseEval("3*u(x/3)", x);
//                    break; //padX = 3 * u(x / 3)
//                case 70000013:
//                    magic += MapleStatEffect.parseEval("3*u(x/3)", x);
//                    break;
//                case 70000014:
//                    passive_sharpeye_rate += MapleStatEffect.parseEval("x", x);
//                    break;
//                case 70000015: { //lukFX = x	strFX = x	dexFX = x	intFX = x
//                    localstr += MapleStatEffect.parseEval("x", x);
//                    localdex += MapleStatEffect.parseEval("x", x);
//                    localint_ += MapleStatEffect.parseEval("x", x);
//                    localluk += MapleStatEffect.parseEval("x", x);
//                }
//                break;
//                case 70000016:
//                    break; //actionSpeed ??= -1 //this is attack speed, isn't it?  client-sided
//                case 70000017:
//                    mdef += (wdef * MapleStatEffect.parseEval("u(x / 4)", x) / 100);
//                    break;// pdd2mdd = u (x / 4)
//                case 70000018:
//                    wdef += (mdef * MapleStatEffect.parseEval("u(x / 4)", x) / 100);
//                    break;
//                case 70000019:
//                    localmaxmp += (accuracy * MapleStatEffect.parseEval("(5 * u(x / 4))", x) / 100);
//                    break;//acc2mp = 5 * u (x / 4)
//                case 70000020:
//                    localmaxhp += (evaR * MapleStatEffect.parseEval("(5 * u(x / 4))", x) / 100);
//                    break;
//                case 70000021:
//                    localdex += (str * MapleStatEffect.parseEval("u(x / 4)", x) / 100);
//                    break; //str2dex = u (x / 4) (str is base, localstr is bonus included)
//                case 70000022:
//                    localstr += (dex * MapleStatEffect.parseEval("u(x / 4)", x) / 100);
//                    break;
//                case 70000023:
//                    localluk += (int_ * MapleStatEffect.parseEval("u(x / 4)", x) / 100);
//                    break;
//                case 70000024:
//                    localdex += (luk * MapleStatEffect.parseEval("u(x / 4)", x) / 100);
//                    break;
//                case 70000025: {
//                    int perLevelGain = MapleStatEffect.parseEval("(20 - (2 * d(x / 2)))", x);
//                    watk += d(chra.getLevel() / perLevelGain);
//                }
//                break; //lv2pad = 20-2 * d (x / 2)
//                case 70000026: {
//                    int perLevelGain = MapleStatEffect.parseEval("(20 - (2 * d(x / 2)))", x);
//                    magic += d(chra.getLevel() / perLevelGain);
//                }
//                break;
//                case 70000027:
//                    accuracy += (accuracy * (MapleStatEffect.parseEval("x", x) / 100));
//                    break;//accR = x
//                case 70000028:
//                    evaR += (evaR * (MapleStatEffect.parseEval("x", x) / 100));
//                    break;
//                case 70000029:
//                    wdef += (wdef * (MapleStatEffect.parseEval("x", x) / 100));
//                    break;
//                case 70000030:
//                    mdef += (mdef * (MapleStatEffect.parseEval("x", x) / 100));
//                    break;
//                case 70000031:
//                    localmaxhp += (localmaxhp * (MapleStatEffect.parseEval("x", x) / 100));
//                    break;
//                case 70000032:
//                    localmaxmp += (localmaxmp * (MapleStatEffect.parseEval("x", x) / 100));
//                    break;
//                case 70000033:
//                    accuracy += (accuracy * (MapleStatEffect.parseEval("u(x / 2)", x) / 100));
//                    break; //ar = u (x/2)
//                case 70000034:
//                    evaR += (evaR * (MapleStatEffect.parseEval("u(x / 2)", x) / 100));
//                    break;
//                case 70000035:
//                    break;//bossdam_r += MapleStatEffect.parseEval("x", x); break;//bdR = x //Clientsided?
//                case 70000036:
//                    break;//+% Damage to Norm Mobs//Clientsided?
//                case 70000037:
//                    break;//+% Damage to Towers//Clientsided?
//                case 70000038:
//                    break;//+% Chance to insta-kill in Azwan Supply//Clientsided?
//                case 70000039:
//                    break;//+% Damage when attacking abnormal ailment targets.//Clientsided?
//                case 70000040: { //Definitely needs to be looked into. "% of Wep Acc or Magic ACC (>) added to additional damage.
//                    watk += (accuracy * (MapleStatEffect.parseEval("x * 2 + u (x / 2)", x) / 100));//acc2dam = x * 2 + u (x / 2)
//                    magic += (accuracy * (MapleStatEffect.parseEval("x * 2 + u (x / 2)", x) / 100));
//                    //Assuming only one type of attack (magic or weapon) can be active at a time, just apply them to both;
//                }
//                break;
//                case 70000041: {
//                    watk += (wdef * (MapleStatEffect.parseEval("x * 2 + u (x / 2)", x) / 100));//pdd2dam = x * 2 + u (x / 2)
//                    magic += (wdef * (MapleStatEffect.parseEval("x * 2 + u (x / 2)", x) / 100));
//                }
//                break;
//                case 70000042: {
//                    watk += (wdef * (MapleStatEffect.parseEval("x * 2 + u (x / 2)", x) / 100));//pdd2dam = x * 2 + u (x / 2)
//                    magic += (mdef * (MapleStatEffect.parseEval("x * 2 + u (x / 2)", x) / 100));
//                }
//                break;
//                case 70000043:
//                    break; //When hit with magic attack, damage equal to % of WDEF is ignored. //Clientsided?
//                case 70000044:
//                    break; //When hit with physical attack, damage equal to % of MDEF is ignored. //Clientsided?
//                case 70000045: //Cooldown not applied at % Chance should be hooked elsewhere. //Clientsided? TEST.
//                case 70000046: //Increase Skill level of passive skills by # //Pretty sure Clientsided
//                case 70000047: //Number of enemies hit by multi target skills +1 //Clientsided.
//                case 70000048: //Buff skill duration +% Should be hooked elsewhere. //Clientsided? TEST.
//                case 70000049:
//                    dropBuff += (MapleStatEffect.parseEval("u(x / 2)", x));
//                    break; //dropR = u (x / 2)
//                case 70000050:
//                    mesoBuff = +(MapleStatEffect.parseEval("u(x / 2)", x));
//                    break;
//                case 70000051: {
//                    localstr += MapleStatEffect.parseEval("x", x);
//                    localdex += MapleStatEffect.parseEval("u(x / 2)", x);
//                }
//                break;
//                case 70000052: {
//                    localstr += MapleStatEffect.parseEval("x", x);
//                    localint_ += MapleStatEffect.parseEval("u(x / 2)", x);
//                }
//                break;
//                case 70000053: {
//                    localstr += MapleStatEffect.parseEval("x", x);
//                    localluk += MapleStatEffect.parseEval("u(x / 2)", x);
//                }
//                break;
//                case 70000054: {
//                    localdex += MapleStatEffect.parseEval("x", x);
//                    localint_ += MapleStatEffect.parseEval("u(x / 2)", x);
//                }
//                break;
//                case 70000055: {
//                    localdex += MapleStatEffect.parseEval("x", x);
//                    localluk += MapleStatEffect.parseEval("u(x / 2)", x);
//                }
//                break;
//                case 70000056: {
//                    localint_ += MapleStatEffect.parseEval("x", x);
//                    localluk += MapleStatEffect.parseEval("u(x / 2)", x);
//                }
//                break;
//                case 70000057: {
//                    localdex += MapleStatEffect.parseEval("x", x);
//                    localstr += MapleStatEffect.parseEval("u(x / 2)", x);
//                }
//                break;
//                case 70000058: {
//                    localint_ += MapleStatEffect.parseEval("x", x);
//                    localstr += MapleStatEffect.parseEval("u(x / 2)", x);
//                }
//                break;
//                case 70000059: {
//                    localluk += MapleStatEffect.parseEval("x", x);
//                    localstr += MapleStatEffect.parseEval("u(x / 2)", x);
//                }
//                break;
//                case 70000060: {
//                    localint_ += MapleStatEffect.parseEval("x", x);
//                    localdex += MapleStatEffect.parseEval("u(x / 2)", x);
//                }
//                break;
//                case 70000061: {
//                    localluk += MapleStatEffect.parseEval("x", x);
//                    localdex += MapleStatEffect.parseEval("u(x / 2)", x);
//                }
//                break;
//                case 70000062: {
//                    localluk += MapleStatEffect.parseEval("x", x);
//                    localint_ += MapleStatEffect.parseEval("u(x / 2)", x);
//                }
//                break;
//                default:
//                    break;
//            }
//        }
    }

    public List<Triple<Integer, String, Integer>> getPsdSkills() {
        return psdSkills;
    }

    public final int u(int variable) {
        return (int) Math.ceil(variable);
    }

    public final int d(int variable) {
        return (int) Math.floor(variable);
    }

    public boolean checkEquipLevels(MapleCharacter chr, int gain) {
        boolean changed = false;
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        List<Equip> all = new ArrayList<>(equipLevelHandling);
        for (Equip eq : all) {
            int lvlz = eq.getEquipLevel();
            eq.setItemEXP(eq.getItemEXP() + gain);

            if (eq.getEquipLevel() > lvlz) { //lvlup
                for (int i = eq.getEquipLevel() - lvlz; i > 0; i--) {
                    //now for the equipment increments...
                    Map<Integer, Map<String, Integer>> inc = ii.getEquipIncrements(eq.getItemId());
                    if (inc != null && inc.containsKey(lvlz + i)) { //flair = 1
                        eq = ii.levelUpEquip(eq, inc.get(lvlz + i));
                    }
                    //UGH, skillz
                    if (GameConstants.getStatFromWeapon(eq.getItemId()) == null && GameConstants.getMaxLevel(eq.getItemId()) < (lvlz + i) && Math.random() < 0.1 && eq.getIncSkill() <= 0 && ii.getEquipSkills(eq.getItemId()) != null) {
                        for (int zzz : ii.getEquipSkills(eq.getItemId())) {
                            Skill skil = SkillFactory.getSkill(zzz);
                            if (skil != null && skil.canBeLearnedBy(chr.getJob())) { //dont go over masterlevel :D
                                eq.setIncSkill(skil.getId());
                                chr.dropMessage(5, "Your skill has gained a levelup: " + skil.getName() + " +1");
                            }
                        }
                    }
                }
                changed = true;
            }
            chr.forceReAddItem(eq.copy(), MapleInventoryType.EQUIPPED);
        }
        if (changed) {
            chr.equipChanged();
            chr.getClient().sendPacket(EffectPacket.showItemLevelupEffect());
            chr.getMap().broadcastMessage(chr, EffectPacket.showForeignItemLevelupEffect(chr.getId()), false);
        }
        return changed;
    }

    public boolean checkEquipDurabilitys(MapleCharacter chr, int gain) {
        return checkEquipDurabilitys(chr, gain, false);
    }

    public boolean checkEquipDurabilitys(MapleCharacter chr, int gain, boolean aboveZero) {
        if (chr.inPVP()) {
            return true;
        }
        List<Equip> all = new ArrayList<>(durabilityHandling);
        for (Equip item : all) {
            if (item != null && ((item.getPosition() >= 0) == aboveZero)) {
                item.setDurability(item.getDurability() + gain);
                if (item.getDurability() < 0) { //shouldnt be less than 0
                    item.setDurability(0);
                }
            }
        }
        for (Equip eqq : all) {
            if (eqq != null && eqq.getDurability() == 0 && eqq.getPosition() < 0) { //> 0 went to negative
                if (chr.getInventory(MapleInventoryType.EQUIP).isFull()) {
                    chr.getClient().sendPacket(InventoryPacket.getInventoryFull());
                    chr.getClient().sendPacket(InventoryPacket.getShowInventoryFull());
                    return false;
                }
                durabilityHandling.remove(eqq);
                short pos = chr.getInventory(MapleInventoryType.EQUIP).getNextFreeSlot();
                MapleInventoryManipulator.unequip(chr.getClient(), eqq.getPosition(), pos);
            } else if (eqq != null) {
                chr.forceReAddItem(eqq.copy(), MapleInventoryType.EQUIPPED);
            }
        }
        return true;
    }

    public void handleProfessionTool(MapleCharacter chra) {
        if (chra.getProfessionLevel(92000000) > 0 || chra.getProfessionLevel(92010000) > 0) {
            Iterator<Item> itera = chra.getInventory(MapleInventoryType.EQUIP).newList().iterator();
            while (itera.hasNext()) { //goes to first harvesting tool and stops
                Equip equip = (Equip) itera.next();
                if (equip.getDurability() != 0 && (equip.getItemId() / 10000 == 150 && chra.getProfessionLevel(92000000) > 0) || (equip.getItemId() / 10000 == 151 && chra.getProfessionLevel(92010000) > 0)) {
                    if (equip.getDurability() > 0) {
                        durabilityHandling.add(equip);
                    }
                    harvestingTool = equip.getPosition();
                    break;
                }
            }
        }
    }

    private void CalcPassive_Mastery(MapleCharacter player) {
        if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11) == null) {
            passive_mastery = 0;
            return;
        }
        int skil;
        MapleWeaponType weaponType = GameConstants.getWeaponType(player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11).getItemId());
        boolean acc = true;
        switch (weaponType) {
            case 弓:
                skil = GameConstants.isKOC(player.getJob()) ? 13100000 : 3100000;
                break;
            case 拳套:
                skil = 4100000;
                break;
            case 手杖:
                skil = player.getTotalSkillLevel(24120006) > 0 ? 24120006 : 24100004;
                break;
            case 加農炮:
                skil = 5300005;
                break;
            case 雙刀:
            case 短劍:
                skil = player.getJob() >= 430 && player.getJob() <= 434 ? 4300000 : 4200000;
                break;
            case 弩:
                skil = GameConstants.isResist(player.getJob()) ? 33100000 : 3200000;
                break;
            case 單手斧:
            case 單手棍:
                skil = GameConstants.isResist(player.getJob()) ? 31100004 : (GameConstants.isKOC(player.getJob()) ? 11100000 : (player.getJob() > 112 ? 1200000 : 1100000)); //hero/pally
                break;
            case 雙手斧:
            case 單手劍:
            case 雙手劍:
            case 雙手棍:
                skil = GameConstants.isKOC(player.getJob()) ? 11100000 : (player.getJob() > 112 ? 1200000 : 1100000); //hero/pally
                break;
            case 槍:
                skil = GameConstants.isAran(player.getJob()) ? 21100000 : 1300000;
                break;
            case 矛:
                skil = 1300000;
                break;
            case 指虎:
                skil = GameConstants.isKOC(player.getJob()) ? 15100001 : 5100001;
                break;
            case 火槍:
                skil = GameConstants.isResist(player.getJob()) ? 35100000 : 5200000;
                break;
            case 雙弩槍:
                skil = 23100005;
                break;
            case 短杖:
            case 長杖:
                acc = false;
                skil = GameConstants.isResist(player.getJob()) ? 32100006 : (player.getJob() <= 212 ? 2100006 : (player.getJob() <= 222 ? 2200006 : (player.getJob() <= 232 ? 2300006 : (player.getJob() <= 2000 ? 12100007 : 22120002))));
                break;
            default:
                passive_mastery = 0;
                return;

        }
        if (player.getSkillLevel(skil) <= 0) {
            passive_mastery = 0;
            return;
        }
        MapleStatEffect eff = SkillFactory.getSkill(skil).getEffect(player.getTotalSkillLevel(skil));
        if (acc) {
            accuracy += eff.getX();
            if (skil == 35100000) {
                watk += eff.getX();
            }
        } else {
            magic += eff.getX();
        }
        passive_sharpeye_rate += eff.getCr();
        passive_mastery = (byte) eff.getMastery(); //after bb, simpler?
        trueMastery += eff.getMastery() + weaponType.getBaseMastery();
    }

    private void calculateFame(MapleCharacter player) {
        player.getTrait(MapleTraitType.charm).addLocalExp(player.getFame());
        for (MapleTraitType t : MapleTraitType.values()) {
            player.getTrait(t).recalcLevel();
        }
    }

    private void CalcPassive_SharpEye(MapleCharacter player) {
        Skill critSkill;
        int critlevel;
        if (GameConstants.isDemon(player.getJob())) {
            critSkill = SkillFactory.getSkill(30010022);
            critlevel = player.getTotalSkillLevel(critSkill);
            if (critlevel > 0) {
                passive_sharpeye_rate += critSkill.getEffect(critlevel).getProb();
                this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
            }
        } else if (GameConstants.isMercedes(player.getJob())) {
            critSkill = SkillFactory.getSkill(20020022);
            critlevel = player.getTotalSkillLevel(critSkill);
            if (critlevel > 0) {
                passive_sharpeye_rate += critSkill.getEffect(critlevel).getProb();
                this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
            }
        } else if (GameConstants.isResist(player.getJob())) {
            critSkill = SkillFactory.getSkill(30000022);
            critlevel = player.getTotalSkillLevel(critSkill);
            if (critlevel > 0) {
                passive_sharpeye_rate += critSkill.getEffect(critlevel).getProb();
                this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
            }
        }
        switch (player.getJob()) { // Apply passive Critical bonus
            case 410:
            case 411:
            case 412: { // Assasin/ Hermit / NL
                critSkill = SkillFactory.getSkill(4100001);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 2412: { // Phantom
                critSkill = SkillFactory.getSkill(24120006);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                    this.watk += critSkill.getEffect(critlevel).getAttackX();
                }
                break;
            }
            case 1410:
            case 1411:
            case 1412: { // Night Walker
                critSkill = SkillFactory.getSkill(14100001);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 3100:
            case 3110:
            case 3111:
            case 3112: {
                critSkill = SkillFactory.getSkill(31100006); //TODO LEGEND, not
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getCr());
                    this.watk += critSkill.getEffect(critlevel).getAttackX();
                }
                break;
            }
            case 2300:
            case 2310:
            case 2311:
            case 2312: {
                critSkill = SkillFactory.getSkill(23000003);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getCr());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 3210:
            case 3211:
            case 3212: {
                critSkill = SkillFactory.getSkill(32100006);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getCr());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 434: {
                critSkill = SkillFactory.getSkill(4340010);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 520:
            case 521:
            case 522: {
                critSkill = SkillFactory.getSkill(5900007);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getCr());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 1211:
            case 1212: {
                critSkill = SkillFactory.getSkill(12110000);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getCr());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 530:
            case 531:
            case 532: {
                critSkill = SkillFactory.getSkill(5300004);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getCr());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 510:
            case 511:
            case 512: { // Buccaner, Viper
                critSkill = SkillFactory.getSkill(5110000);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) critSkill.getEffect(critlevel).getProb();
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                Skill critSkill2 = SkillFactory.getSkill(5100008);
                int critlevel2 = player.getTotalSkillLevel(critSkill);
                if (critlevel2 > 0) {
                    this.passive_sharpeye_rate += (short) critSkill2.getEffect(critlevel2).getCr();
                    this.passive_sharpeye_min_percent += critSkill2.getEffect(critlevel2).getCriticalMin();
                }
                return;
            }
            case 1511:
            case 1512: {
                critSkill = SkillFactory.getSkill(15110000);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 2111:
            case 2112: {
                critSkill = SkillFactory.getSkill(21110000);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) ((critSkill.getEffect(critlevel).getX() * critSkill.getEffect(critlevel).getY()) + critSkill.getEffect(critlevel).getCr());
                }
                break;
            }
            case 300:
            case 310:
            case 311:
            case 312:
            case 320:
            case 321:
            case 322: { // Bowman
                critSkill = SkillFactory.getSkill(3000001);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 1300:
            case 1310:
            case 1311:
            case 1312: { // Bowman
                critSkill = SkillFactory.getSkill(13000000);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
            case 2214:
            case 2215:
            case 2216:
            case 2217:
            case 2218: { //Evan
                critSkill = SkillFactory.getSkill(22140000);
                critlevel = player.getTotalSkillLevel(critSkill);
                if (critlevel > 0) {
                    this.passive_sharpeye_rate += (short) (critSkill.getEffect(critlevel).getProb());
                    this.passive_sharpeye_min_percent += critSkill.getEffect(critlevel).getCriticalMin();
                }
                break;
            }
        }
    }

    public short passive_sharpeye_min_percent() {
        return passive_sharpeye_min_percent;
    }

    public short passive_sharpeye_percent() {
        return passive_sharpeye_percent;
    }

    public short passive_sharpeye_rate() {
        return passive_sharpeye_rate;
    }

    public byte passive_mastery() {
        return passive_mastery; //* 5 + 10 for mastery %
    }

    public void calculateMaxBaseDamage(long watk, int pvpDamage, MapleCharacter chra) {
        if (watk <= 0) {
            localmaxbasedamage = 1;
            localmaxbasepvpdamage = 1;
        } else if (watk >= Integer.MAX_VALUE) {
            localmaxbasedamage = Integer.MAX_VALUE - 1;
            localmaxbasepvpdamage = Integer.MAX_VALUE - 1;

        } else {
            Item weapon_item = chra.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -11);
            Item weapon_item2 = chra.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -10);
            int job = chra.getJob();
            MapleWeaponType weapon = weapon_item == null ? MapleWeaponType.沒有武器 : GameConstants.getWeaponType(weapon_item.getItemId());
            MapleWeaponType weapon2 = weapon_item2 == null ? MapleWeaponType.沒有武器 : GameConstants.getWeaponType(weapon_item2.getItemId());
            int mainstat, secondarystat, mainstatpvp, secondarystatpvp;
            boolean mage = (job >= 200 && job <= 232) || (job >= 1200 && job <= 1212) || (job >= 2200 && job <= 2218) || (job >= 3200 && job <= 3212);
            switch (weapon) {
                case 弓:
                case 弩:
                case 火槍:
                    mainstat = localdex;
                    secondarystat = localstr;
                    mainstatpvp = dex;
                    secondarystatpvp = str;
                    break;
                case 短劍:
                case 雙刀:
                case 拳套:
                case 手杖:
                    mainstat = localluk;
                    secondarystat = localdex + localstr;
                    mainstatpvp = luk;
                    secondarystatpvp = dex + str;
                    break;
                default:
                    if (mage) {
                        mainstat = localint_;
                        secondarystat = localluk;
                        mainstatpvp = int_;
                        secondarystatpvp = luk;
                    } else {
                        mainstat = localstr;
                        secondarystat = localdex;
                        mainstatpvp = str;
                        secondarystatpvp = dex;
                    }
                    break;
            }
            localmaxbasepvpdamage = weapon.getMaxDamageMultiplier() * (4 * mainstatpvp + secondarystatpvp) * (100.0f + (pvpDamage / 100.0f));
            localmaxbasepvpdamageL = weapon.getMaxDamageMultiplier() * (4 * mainstat + secondarystat) * (100.0f + (pvpDamage / 100.0f));
            if (weapon2 != MapleWeaponType.沒有武器 && weapon_item != null && weapon_item2 != null) {
                Equip we1 = (Equip) weapon_item;
                Equip we2 = (Equip) weapon_item2;
                localmaxbasedamage = weapon.getMaxDamageMultiplier() * (4 * mainstat + secondarystat) * ((watk - (mage ? we2.getMatk() : we2.getWatk())) / 100.0f);
                localmaxbasedamage += weapon2.getMaxDamageMultiplier() * (4 * mainstat + secondarystat) * ((watk - (mage ? we1.getMatk() : we1.getWatk())) / 100.0f);
            } else {
                localmaxbasedamage = weapon.getMaxDamageMultiplier() * (4 * mainstat + secondarystat) * (watk / 100.0f);
            }
        }
    }

    public float getHealHP() {
        return shouldHealHP;
    }

    public float getHealMP() {
        return shouldHealMP;
    }

    public void relocHeal(MapleCharacter chra) {
        int playerjob = chra.getJob();

        shouldHealHP = 10 + recoverHP; // Reset
        shouldHealMP = GameConstants.isDemon(chra.getJob()) ? 0 : (3 + mpRestore + recoverMP + (localint_ / 10)); // i think
        mpRecoverTime = 0;
        hpRecoverTime = 0;
        if (playerjob == 111 || playerjob == 112) {
            Skill effect = SkillFactory.getSkill(1110000); // Improving MP Recovery
            int lvl = chra.getSkillLevel(effect);
            if (lvl > 0) {
                MapleStatEffect eff = effect.getEffect(lvl);
                if (eff.getHp() > 0) {
                    shouldHealHP += eff.getHp();
                    hpRecoverTime = 4000;
                }
                shouldHealMP += eff.getMp();
                mpRecoverTime = 4000;
            }

        } else if (playerjob == 1111 || playerjob == 1112) {
            Skill effect = SkillFactory.getSkill(11110000); // Improving MP Recovery
            int lvl = chra.getSkillLevel(effect);
            if (lvl > 0) {
                shouldHealMP += effect.getEffect(lvl).getMp();
                mpRecoverTime = 4000;
            }
        } else if (GameConstants.isMercedes(playerjob)) {
            Skill effect = SkillFactory.getSkill(20020109); // Improving MP Recovery
            int lvl = chra.getSkillLevel(effect);
            if (lvl > 0) {
                shouldHealHP += (effect.getEffect(lvl).getX() * localmaxhp) / 100;
                hpRecoverTime = 4000;
                shouldHealMP += (effect.getEffect(lvl).getX() * localmaxmp) / 100;
                mpRecoverTime = 4000;
            }
        } else if (playerjob == 3111 || playerjob == 3112) {
            Skill effect = SkillFactory.getSkill(31110009); // Improving MP Recovery
            int lvl = chra.getSkillLevel(effect);
            if (lvl > 0) {
                shouldHealMP += effect.getEffect(lvl).getY();
                mpRecoverTime = 4000;
            }
        }
        if (chra.getChair() != 0) { // Is sitting on a chair.
            shouldHealHP += 99; // Until the values of Chair heal has been fixed,
            shouldHealMP += 99; // MP is different here, if chair data MP = 0, heal + 1.5
        } else if (chra.getMap() != null) { // Because Heal isn't multipled when there's a chair :)
            float recvRate = chra.getMap().getRecoveryRate();
            if (recvRate > 0) {
                shouldHealHP *= recvRate;
                shouldHealMP *= recvRate;
            }
        }
    }

    public void connectData(MaplePacketLittleEndianWriter mplew) {
        mplew.writeShort(str); // str
        mplew.writeShort(dex); // dex
        mplew.writeShort(int_); // int
        mplew.writeShort(luk); // luk
        mplew.writeInt(hp); // hp -- INT after bigbang
        mplew.writeInt(maxhp); // maxhp
        mplew.writeInt(mp); // mp
        mplew.writeInt(maxmp); // maxmp
    }

    public int getSkillIncrement(int skillID) {
        if (skillsIncrement.containsKey(skillID)) {
            return skillsIncrement.get(skillID);
        }
        return 0;
    }

    public int getElementBoost(Element key) {
        if (elemBoosts.containsKey(key)) {
            return elemBoosts.get(key);
        }
        return 0;
    }

    public int getDamageIncrease(int key) {
        if (damageIncrease.containsKey(key)) {
            return damageIncrease.get(key);
        }
        return 0;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void heal_noUpdate(MapleCharacter chra) {
        setHp(getCurrentMaxHp(), chra);
        setMp(getCurrentMaxMp(chra.getJob()), chra);
    }

    public void heal(MapleCharacter chra) {
        heal_noUpdate(chra);
        chra.updateSingleStat(MapleStat.HP, getCurrentMaxHp());
        chra.updateSingleStat(MapleStat.MP, getCurrentMaxMp(chra.getJob()));
    }

    public void handleItemOption(StructItemOption soc, MapleCharacter chra, boolean first_login, Map<Skill, SkillEntry> hmm) {
        localstr += soc.get("incSTR");
        localdex += soc.get("incDEX");
        localint_ += soc.get("incINT");
        localluk += soc.get("incLUK");
        accuracy += soc.get("incACC");
        // incEVA -> increase dodge
        speed += soc.get("incSpeed");
        jump += soc.get("incJump");
        watk += soc.get("incPAD");
        magic += soc.get("incMAD");
        wdef += soc.get("incPDD");
        mdef += soc.get("incMDD");
        percent_str += soc.get("incSTRr");
        percent_dex += soc.get("incDEXr");
        percent_int += soc.get("incINTr");
        percent_luk += soc.get("incLUKr");
        percent_hp += soc.get("incMHPr");
        percent_mp += soc.get("incMMPr");
        percent_acc += soc.get("incACCr");
        dodgeChance += soc.get("incEVAr");
        percent_atk += soc.get("incPADr");
        percent_matk += soc.get("incMADr");
        percent_wdef += soc.get("incPDDr");
        percent_mdef += soc.get("incMDDr");
        passive_sharpeye_rate += soc.get("incCr");
        bossdam_r *= (soc.get("incDAMr") + 100.0) / 100.0;
        if (soc.get("boss") <= 0) {
            dam_r *= (soc.get("incDAMr") + 100.0) / 100.0;
        }
        recoverHP += soc.get("RecoveryHP"); // This shouldn't be here, set 4 seconds.
        recoverMP += soc.get("RecoveryMP"); // This shouldn't be here, set 4 seconds.
        //if (soc.get("HP") > 0) { // Should be heal upon attacking
        //	hpRecover += soc.get("HP");
        //	hpRecoverProp += soc.get("prop");
        //}
        //if (soc.get("MP") > 0 && !GameConstants.isDemon(chra.getJob())) {
        //	mpRecover += soc.get("MP");
        //	mpRecoverProp += soc.get("prop");
        //}
        ignoreTargetDEF += soc.get("ignoreTargetDEF");
        if (soc.get("ignoreDAM") > 0) {
            ignoreDAM += soc.get("ignoreDAM");
            ignoreDAM_rate += soc.get("prop");
        }
        incAllskill += soc.get("incAllskill");
        if (soc.get("ignoreDAMr") > 0) {
            ignoreDAMr += soc.get("ignoreDAMr");
            ignoreDAMr_rate += soc.get("prop");
        }
        RecoveryUP += soc.get("RecoveryUP"); // only for hp items and skills
        passive_sharpeye_min_percent += soc.get("incCriticaldamageMin");
        passive_sharpeye_percent += soc.get("incCriticaldamageMax");
        TER += soc.get("incTerR"); // elemental resistance = avoid element damage from monster
        ASR += soc.get("incAsrR"); // abnormal status = disease
        if (soc.get("DAMreflect") > 0) {
            DAMreflect += soc.get("DAMreflect");
            DAMreflect_rate += soc.get("prop");
        }
        mpconReduce += soc.get("mpconReduce");
        reduceCooltime += soc.get("reduceCooltime"); // in seconds
        incMesoProp += soc.get("incMesoProp"); // mesos + %
        dropBuff *= (100 + soc.get("incRewardProp")) / 100.0; // extra drop rate for item
        if (first_login && soc.get("skillID") > 0) {
            hmm.put(SkillFactory.getSkill(getSkillByJob(soc.get("skillID"), chra.getJob())), new SkillEntry((byte) 1, (byte) 0, -1));
        }
        // TODO: Auto Steal potentials (modify handleSteal), potentials with invincible stuffs, abnormal status duration decrease,
        // poison, stun, etc (uses level field -> cast disease to mob/player), face?
    }

    public void recalcPVPRank(MapleCharacter chra) {
        this.pvpRank = 10;
        this.pvpExp = chra.getTotalBattleExp();
        for (int i = 0; i < 10; i++) {
            if (pvpExp > GameConstants.getPVPExpNeededForLevel(i + 1)) {
                pvpRank--;
                pvpExp -= GameConstants.getPVPExpNeededForLevel(i + 1);
            }
        }
    }

    public int getHPPercent() {
        return (int) Math.ceil((hp * 100.0) / localmaxhp);
    }
}
