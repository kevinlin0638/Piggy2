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

import client.*;
import client.MapleTrait.MapleTraitType;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.skill.Skill;
import client.skill.SkillFactory;
import server.status.MapleBuffStatus;
import server.status.MonsterStatus;
import server.status.MonsterStatusEffect;
import constants.GameConstants;
import constants.Occupations;
import constants.ServerConstants;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import scripting.EventInstanceManager;
import server.*;
import server.Timer;
import server.Timer.EtcTimer;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import tools.packet.CField;
import tools.packet.MobPacket;
import tools.types.ConcurrentEnumMap;
import tools.types.Pair;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MapleMonster extends AbstractLoadedMapleLife {

    private final Collection<AttackerEntry> attackers = new LinkedList<AttackerEntry>();
    private final ConcurrentEnumMap<MonsterStatus, MonsterStatusEffect> stati = new ConcurrentEnumMap<MonsterStatus, MonsterStatusEffect>(MonsterStatus.class);
    private final LinkedList<MonsterStatusEffect> poisons = new LinkedList<MonsterStatusEffect>();
    private final ReentrantReadWriteLock poisonsLock = new ReentrantReadWriteLock();
    public boolean monsterPet = false;
    private MapleMonsterStats stats;
    private ChangeableStats ostats = null;
    private long hp, nextKill = 0, lastDropTime = 0;
    private int mp;
    private byte carnivalTeam = -1;
    private MapleMap map;
    private WeakReference<MapleMonster> sponge = new WeakReference<MapleMonster>(null);
    private int linkoid = 0, lastNode = -1, highestDamageChar = 0, linkCID = 0; // Just a reference for monster EXP distribution after dead
    private WeakReference<MapleCharacter> controller = new WeakReference<MapleCharacter>(null);
    private boolean fake = false, dropsDisabled = false, controllerHasAggro = false;
    private EventInstanceManager eventInstance;
    private MonsterListener listener = null;
    private byte[] reflectpack = null, nodepack = null;
    private Map<Integer, Long> usedSkills;
    private int stolen = -1; //monster can only be stolen ONCE
    private boolean shouldDropItem = false, killed = false;
    private int randomNX = MapleCharacter.rand(10, 500); //player NX
    private int TrollNX = MapleCharacter.rand(1337, 9001); //Troll NX - UMad

    public MapleMonster(final int id, final MapleMonsterStats stats) {
        super(id);
        initWithStats(stats);
    }

    public MapleMonster(final MapleMonster monster) {
        super(monster);
        initWithStats(monster.stats);
    }

    private final void initWithStats(final MapleMonsterStats stats) {
        setStance(5);
        this.stats = stats;
        hp = stats.getHp();
        mp = stats.getMp();

        if (stats.getNoSkills() > 0) {
            usedSkills = new HashMap<Integer, Long>();
        }
    }

    public final ArrayList<AttackerEntry> getAttackers() {
        if (attackers == null || attackers.size() <= 0) {
            return new ArrayList<AttackerEntry>();
        }
        ArrayList<AttackerEntry> ret = new ArrayList<AttackerEntry>();
        for (AttackerEntry e : attackers) {
            if (e != null) {
                ret.add(e);
            }
        }
        return ret;
    }

    public boolean isMonsterPet() {
        return monsterPet;
    }


    public final MapleMonsterStats getStats() {
        return stats;
    }

    public final void disableDrops() {
        this.dropsDisabled = true;
    }

    public final boolean dropsDisabled() {
        return dropsDisabled;
    }

    public final long getHp() {
        return hp;
    }

    public final void setHp(long hp) {
        this.hp = hp;
    }

    public boolean isBoss() {
        return stats.isBoss();
    }

    public String getName() {
        return stats.getName();
    }

    public final ChangeableStats getChangedStats() {
        return ostats;
    }

    public final long getMobMaxHp() {
        if (ostats != null) {
            return ostats.hp;
        }
        return stats.getHp();
    }

    public final int getMobLevel() {
        if (ostats != null) {
            return ostats.level;
        }
        return stats.getLevel();
    }

    public final int getMp() {
        return mp;
    }

    public final void setMp(int mp) {
        if (mp < 0) {
            mp = 0;
        }
        this.mp = mp;
    }

    public final int getMobMaxMp() {
        if (ostats != null) {
            return ostats.mp;
        }
        return stats.getMp();
    }

    public final int getMobExp() {
        if (ostats != null) {
            return ostats.exp;
        }
        return stats.getExp();
    }

    public final void setOverrideStats(final OverrideMonsterStats ostats) {
        this.ostats = new ChangeableStats(stats, ostats);
        this.hp = ostats.getHp();
        this.mp = ostats.getMp();
    }

    public final void changeLevel(final int newLevel) {
        changeLevel(newLevel, true);
    }

    public final void changeLevel(final int newLevel, boolean pqMob) {
        this.ostats = new ChangeableStats(stats, newLevel, pqMob);
        this.hp = ostats.getHp();
        this.mp = ostats.getMp();
    }
    public final void changeLevel(final int newLevel, long hp) {
        this.ostats = new ChangeableStats(stats, newLevel, hp);
        this.hp = ostats.getHp();
        this.mp = ostats.getMp();
    }
    public final void HellChangeLevel(final int newLevel, final int rate_hp, final int rate_exp) { //Custom hell
        this.ostats = new ChangeableStats(stats, newLevel, rate_hp, rate_exp);
        this.hp = ostats.getHp();
        this.mp = ostats.getMp();
    }
    public final void dojoChangeLevel(final int newLevel, final Long Hp, final long EXP) { //Custom hell


        this.ostats = new ChangeableStats(stats, newLevel, Hp, EXP);
        this.hp = ostats.getHp();
        this.mp = ostats.getMp();
    }

    public final MapleMonster getSponge() {
        return sponge.get();
    }

    public final void setSponge(final MapleMonster mob) {
        sponge = new WeakReference<MapleMonster>(mob);
        if (linkoid <= 0) {
            linkoid = mob.getObjectId();
        }
    }

    public final void damage(final MapleCharacter from, final long damage, final boolean updateAttackTime) {
        damage(from, damage, updateAttackTime, 0);
    }

    public final void damage(final MapleCharacter from, final long damage, final boolean updateAttackTime, final int lastSkill) {
        if (from == null || damage <= 0 || !isAlive()) {
            return;
        }
        AttackerEntry attacker = null;

        if (from.getParty() != null) {
            attacker = new PartyAttackerEntry(from.getParty().getId());
        } else {
            attacker = new SingleAttackerEntry(from);
        }
        boolean replaced = false;
        for (final AttackerEntry aentry : getAttackers()) {
            if (aentry != null && aentry.equals(attacker)) {
                attacker = aentry;
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            attackers.add(attacker);
        }
        long rDamage = Math.max(0, Math.min(damage, hp));
        attacker.addDamage(from, rDamage, updateAttackTime);
        if (stats.getSelfD() != -1) {
            hp -= rDamage;
            if (hp > 0) {
                if (hp < stats.getSelfDHp()) { // HP is below the selfd level
                    map.killMonster(this, from, false, false, stats.getSelfD(), lastSkill);
                } else { // Show HP
                    for (final AttackerEntry mattacker : getAttackers()) {
                        for (final AttackingMapleCharacter cattacker : mattacker.getAttackers()) {
                            if (cattacker.getAttacker().getMap() == from.getMap()) { // current attacker is on the map of the monster
                                if (cattacker.getLastAttackTime() >= System.currentTimeMillis() - 4000) {
                                    cattacker.getAttacker().getClient().sendPacket(MobPacket.showMonsterHP(getObjectId(), getHPPercent()));
                                }
                            }
                        }
                    }
                }
            } else { // Character killed it without explosing :(
                map.killMonster(this, from, true, false, (byte) 1, lastSkill);
            }
        } else {
            if (sponge.get() != null) {
                if (sponge.get().hp > 0) { // If it's still alive, dont want double/triple rewards
                    // Sponge are always in the same map, so we can use this.map
                    // The only mob that uses sponge are PB/HT
                    sponge.get().hp -= rDamage;

                    if (sponge.get().hp <= 0) {
                        map.broadcastMessage(MobPacket.showBossHP(sponge.get().getId(), -1, sponge.get().getMobMaxHp()));
                        map.killMonster(sponge.get(), from, true, false, (byte) 1, lastSkill);
                    } else {
                        map.broadcastMessage(MobPacket.showBossHP(sponge.get()));
                    }
                }
            }
            if (hp > 0) {
                hp -= rDamage;
                if (eventInstance != null) {
                    eventInstance.monsterDamaged(from, this, (int) rDamage);
                } else {
                    final EventInstanceManager em = from.getEventInstance();
                    if (em != null) {
                        em.monsterDamaged(from, this, (int) rDamage);
                    }
                }
                if (sponge.get() == null && hp > 0) {
                    if (this.getId() == 9300275) {
                        return;
                    }
                    switch (stats.getHPDisplayType()) {
                        case 0:
                            map.broadcastMessage(MobPacket.showBossHP(this), this.getTruePosition());
                            break;
                        case 1:
                            map.broadcastMessage(from, MobPacket.damageFriendlyMob(this, (int) Math.min((long) Integer.MAX_VALUE, damage), true), false);
                            break;
                        case 2:
                            map.broadcastMessage(MobPacket.showMonsterHP(getObjectId(), getHPPercent()));
                            from.mulung_EnergyModify(true);
                            break;
                        case 3:
                            for (final AttackerEntry mattacker : getAttackers()) {
                                if (mattacker != null) {
                                    for (final AttackingMapleCharacter cattacker : mattacker.getAttackers()) {
                                        if (cattacker != null && cattacker.getAttacker().getMap() == from.getMap()) { // current attacker is on the map of the monster
                                            if (cattacker.getLastAttackTime() >= System.currentTimeMillis() - 4000) {
                                                cattacker.getAttacker().getClient().sendPacket(MobPacket.showMonsterHP(getObjectId(), getHPPercent()));
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                    }
                }
                if (hp <= 0) {
                    if (stats.getHPDisplayType() == 0) {
                        map.broadcastMessage(MobPacket.showBossHP(getId(), -1, getMobMaxHp()), this.getTruePosition());
                    }
                    map.killMonster(this, from, true, false, (byte) 1, lastSkill);
                }
            }
        }
        startDropItemSchedule();
        if (map.getChrFromMob(this) != null) {
            if (map.getMobFromChr(from) != null) { // Makes sure the attacker is also part of the PvP game
                MapleCharacter victim = map.getChrFromMob(this);
                if (from != victim) {
//                    int balancedDmg = (int) Math.round(rDamage * (victim.getLevel() * 50) / WizerDual.pvp_Difficulty / GameConstants.getMonsterHP((int) from.getLevel()));
//                    victim.handlePvpDmg(balancedDmg);
//                    setHp(Long.MAX_VALUE);
//                    map.broadcastMessage(MobPacket.moveMonster(false, -1, 0, getObjectId(), getTruePosition(), victim.getLastRes()));
//                    setPosition(victim.getPosition());
                } else {
                    from.getClient().sendPacket(MobPacket.killMonster(getObjectId(), (byte) 1));
                }
            }
        }
    }

    public int getHPPercent() {
        return (int) Math.ceil((hp * 100.0) / getMobMaxHp());
    }

    public final void heal(int hp, int mp, final boolean broadcast) {
        final long TotalHP = getHp() + hp;
        final int TotalMP = getMp() + mp;

        if (TotalHP >= getMobMaxHp()) {
            setHp(getMobMaxHp());
        } else {
            setHp(TotalHP);
        }
        if (TotalMP >= getMp()) {
            setMp(getMp());
        } else {
            setMp(TotalMP);
        }
        if (broadcast) {
            map.broadcastMessage(MobPacket.healMonster(getObjectId(), hp));
        } else if (sponge.get() != null) { // else if, since only sponge doesn't broadcast
            sponge.get().hp += hp;
        }
    }

    public final void killed() {
        if (listener != null) {
            listener.monsterKilled();
        }
        listener = null;
    }

    private final void giveExpToCharacter(final MapleCharacter attacker, int exp, final boolean highestDamage, final int numExpSharers, final byte pty, final byte Class_Bonus_EXP_PERCENT, final byte Premium_Bonus_EXP_PERCENT, final int lastskillID) {
        if (highestDamage) {
            if (eventInstance != null) {
                eventInstance.monsterKilled(attacker, this);
            } else {
                final EventInstanceManager em = attacker.getEventInstance();
                if (em != null) {
                    em.monsterKilled(attacker, this);
                }
            }
            highestDamageChar = attacker.getId();
        }
        if (exp > 0) {
            final MonsterStatusEffect ms = stati.get(MonsterStatus.SHOWDOWN);
            if (ms != null) {
                exp += (int) (exp * (ms.getX() / 100.0));
            }
            final Integer holySymbol = attacker.getBuffedValue(MapleBuffStatus.HOLY_SYMBOL);
            if (holySymbol != null) {
                exp *= 1.0 + (holySymbol.doubleValue() / 100.0);
            }
            if (attacker.hasDisease(MapleBuffStatus.CURSE)) {
                exp /= 2;
            }
            exp = (int) Math.min(Integer.MAX_VALUE, exp * attacker.getEXPMod() * attacker.getStat().expBuff / 100.0 * attacker.getClient().getWorldServer().getExpRate());
            //do this last just incase someone has a 2x exp card and its set to max value
            int Class_Bonus_EXP = 0;
            if (Class_Bonus_EXP_PERCENT > 0) {
                Class_Bonus_EXP = (int) ((exp / 100.0) * Class_Bonus_EXP_PERCENT);
            }
            int Premium_Bonus_EXP = 0;
            if (Premium_Bonus_EXP_PERCENT > 0) {
                Premium_Bonus_EXP = (int) ((exp / 100.0) * Premium_Bonus_EXP_PERCENT);
            }
            int Equipment_Bonus_EXP = (int) ((exp / 100.0) * attacker.getStat().equipmentBonusExp);
            if (attacker.getStat().equippedFairy > 0 && attacker.getFairyExp() > 0) {
                Equipment_Bonus_EXP += (int) ((exp / 100.0) * attacker.getFairyExp());
            }
            attacker.getTrait(MapleTraitType.charisma).addExp(stats.getCharismaEXP(), attacker);
            //if (attacker.getApprentice() > 0) {
            //   attacker.getApp().gainExpMonster(exp, true, highestDamage, pty, Class_Bonus_EXP, Equipment_Bonus_EXP, Premium_Bonus_EXP, stats.isPartyBonus(), stats.getPartyBonusRate());
            //} else {
            attacker.gainExpMonster(exp, true, highestDamage, pty, Class_Bonus_EXP, Equipment_Bonus_EXP, Premium_Bonus_EXP, stats.isPartyBonus(), stats.getPartyBonusRate());
            //}
        }
        attacker.mobKilled(getId(), lastskillID);
        if (attacker.getApprentice() > 0) {
            return;
        }
        if ((int) (Math.random() * 100) < 3 && attacker.retrieveOccupation().is(Occupations.NX_Addict)) {
            int gain = randomNX * attacker.getOccLevel(); // generate the same identity but making it num as 'gain' then used to proceed //same as gain equaling randomNX;
            attacker.modifyCSPoints(4, gain);
            attacker.getClient().sendPacket(CField.sendHint("You have gained #e#r" + gain + "#k#n NX!", 0, 0));
        }
         /*int num = MapleCharacter.rand(1, 100);
         if (num < 10 && attacker.getOccId() != 0 && attacker.getOccLevel() < 150) {
             attacker.gainOccExp(MapleCharacter.rand(10, 50));
                //attacker.gainOccExp((exp / (exp / 50)) * MapleCharacter.rand(1, (attacker.getLevel() <= 15 ? attacker.getLevel() / 3 : 20)));
         }*/
    }

    public final int killBy(final MapleCharacter killer, final int lastSkill) {
        if (killed) {
            return 1;
        }
        killed = true;
        int totalBaseExp = getMobExp();
        AttackerEntry highest = null;
        long highdamage = 0;
        final List<AttackerEntry> list = getAttackers();
        for (final AttackerEntry attackEntry : list) {
            if (attackEntry != null && attackEntry.getDamage() > highdamage) {
                highest = attackEntry;
                highdamage = attackEntry.getDamage();
            }
        }
        int baseExp;
        for (final AttackerEntry attackEntry : list) {
            if (attackEntry != null) {
                baseExp = (int) Math.ceil(totalBaseExp * ((double) attackEntry.getDamage() / getMobMaxHp()));
                attackEntry.killedMob(getMap(), baseExp, attackEntry == highest, lastSkill);
            }
        }
        final MapleCharacter controll = controller.get();
        if (controll != null) { // this can/should only happen when a hidden gm attacks the monster
            if (GameConstants.isAswanMap(killer.getMapId())) {
                controll.getClient().sendPacket(MobPacket.stopControllingAswanMonster(getObjectId()));
            } else {
                controll.getClient().sendPacket(MobPacket.stopControllingMonster(getObjectId()));
            }
            controll.stopControllingMonster(this);
        }
        if (killer != null && stats.isBoss()) {
            killer.finishAchievement(18);
        }
        spawnRevives(getMap());
        if (eventInstance != null) {
            eventInstance.unregisterMonster(this);
            eventInstance = null;
        }
        if (killer != null && killer.getPyramidSubway() != null) {
            killer.getPyramidSubway().onKill(killer);
        }
        hp = 0;
        MapleMonster oldSponge = getSponge();
        sponge = new WeakReference<MapleMonster>(null);
        if (oldSponge != null && oldSponge.isAlive()) {
            boolean set = true;
            for (MapleMapObject mon : map.getAllMonstersThreadsafe()) {
                MapleMonster mons = (MapleMonster) mon;
                if (mons.isAlive() && mons.getObjectId() != oldSponge.getObjectId() && mons.getStats().getLevel() > 1 && mons.getObjectId() != this.getObjectId() && (mons.getSponge() == oldSponge || mons.getLinkOid() == oldSponge.getObjectId())) { //sponge was this, please update
                    set = false;
                    break;
                }
            }
            if (set) { //all sponge monsters are dead, please kill off the sponge
                map.killMonster(oldSponge, killer, true, false, (byte) 1);
            }
        }

        reflectpack = null;
        nodepack = null;
        if (stati.size() > 0) {
            List<MonsterStatus> statuses = new LinkedList<MonsterStatus>(stati.keySet());
            for (MonsterStatus ms : statuses) {
                cancelStatus(ms);
            }
            statuses.clear();
        }
        if (poisons.size() > 0) {
            List<MonsterStatusEffect> ps = new LinkedList<MonsterStatusEffect>();
            poisonsLock.readLock().lock();
            try {
                ps.addAll(poisons);
            } finally {
                poisonsLock.readLock().unlock();
            }
            for (MonsterStatusEffect p : ps) {
                cancelSingleStatus(p);
            }
            ps.clear();
        }
        //attackers.clear();
        cancelDropItem();
        int v1 = highestDamageChar;
        this.highestDamageChar = 0; //reset so we dont kill twice
        return v1;
    }

    public final void spawnRevives(final MapleMap map) {
        final List<Integer> toSpawn = stats.getRevives();

        if (toSpawn == null || this.getLinkCID() > 0) {
            return;
        }
        MapleMonster spongy = null;
        switch (getId()) {
            case 8820002:
            case 8820003:
            case 8820004:
            case 8820005:
            case 8820006:
            case 8840000:
            case 6160003:
            case 8850011:
                break;
            case 8810118:
            case 8810119:
            case 8810120:
            case 8810121: //must update sponges
                for (final int i : toSpawn) {
                    final MapleMonster mob = MapleLifeFactory.getMonster(i);

                    mob.setPosition(getTruePosition());
                    if (eventInstance != null) {
                        eventInstance.registerMonster(mob);
                    }
                    if (dropsDisabled()) {
                        mob.disableDrops();
                    }
                    switch (mob.getId()) {
                        case 8810119:
                        case 8810120:
                        case 8810121:
                        case 8810122:
                            spongy = mob;
                            break;
                    }
                }
                if (spongy != null && map.getMonsterById(spongy.getId()) == null) {
                    map.spawnMonster(spongy, -2);
                    for (MapleMapObject mon : map.getAllMonstersThreadsafe()) {
                        MapleMonster mons = (MapleMonster) mon;
                        if (mons.getObjectId() != spongy.getObjectId() && (mons.getSponge() == this || mons.getLinkOid() == this.getObjectId())) { //sponge was this, please update
                            mons.setSponge(spongy);
                        }
                    }
                }
                break;
            case 8810026:
            case 8810130:
            case 8820008:
            case 8820009:
            case 8820010:
            case 8820011:
            case 8820012:
            case 8820013: {
                final List<MapleMonster> mobs = new ArrayList<MapleMonster>();


                for (final int i : toSpawn) {
                    final MapleMonster mob = MapleLifeFactory.getMonster(i);

                    mob.setPosition(getTruePosition());
                    if (eventInstance != null) {
                        eventInstance.registerMonster(mob);
                    }
                    if (dropsDisabled()) {
                        mob.disableDrops();
                    }
                    switch (mob.getId()) {
                        case 8810018: // Horntail Sponge
                        case 8810118:
                        case 8820009: // PinkBeanSponge0
                        case 8820010: // PinkBeanSponge1
                        case 8820011: // PinkBeanSponge2
                        case 8820012: // PinkBeanSponge3
                        case 8820013: // PinkBeanSponge4
                        case 8820014: // PinkBeanSponge5
                            spongy = mob;
                            break;
                        default:
                            mobs.add(mob);
                            break;
                    }
                }
                if (spongy != null && map.getMonsterById(spongy.getId()) == null) {
                    map.spawnMonster(spongy, -2);

                    for (final MapleMonster i : mobs) {
                        map.spawnMonster(i, -2);
                        i.setSponge(spongy);
                    }
                }
                break;
            }
            case 8820014: {
                for (final int i : toSpawn) {
                    final MapleMonster mob = MapleLifeFactory.getMonster(i);

                    if (eventInstance != null) {
                        eventInstance.registerMonster(mob);
                    }
                    mob.setPosition(getTruePosition());
                    if (dropsDisabled()) {
                        mob.disableDrops();
                    }
                    map.spawnMonster(mob, -2);
                }
                break;
            }
            default: {
                for (final int i : toSpawn) {
                    final MapleMonster mob = MapleLifeFactory.getMonster(i);

                    if (eventInstance != null) {
                        eventInstance.registerMonster(mob);
                    }
                    mob.setPosition(getTruePosition());
                    if (dropsDisabled()) {
                        mob.disableDrops();
                    }
                    map.spawnRevives(mob, this.getObjectId());

                    if (mob.getId() == 9300216) {
                        map.broadcastMessage(CField.environmentChange("Dojang/clear", 4));
                        map.broadcastMessage(CField.environmentChange("dojang/end/clear", 3));
                    }
                }
                break;
            }
        }
    }

    public final boolean isAlive() {
        return hp > 0;
    }

    public final byte getCarnivalTeam() {
        return carnivalTeam;
    }

    public final void setCarnivalTeam(final byte team) {
        carnivalTeam = team;
    }

    public final MapleCharacter getController() {
        return controller.get();
    }

    public final void setController(final MapleCharacter controller) {
        this.controller = new WeakReference<>(controller);
    }

    public final void switchController(final MapleCharacter newController, final boolean immediateAggro) {
        final MapleCharacter controllers = getController();
        if (controllers == newController) {
            return;
        } else if (controllers != null) {
            controllers.stopControllingMonster(this);
            if (GameConstants.isAswanMap(newController.getMapId())) {
                controllers.getClient().sendPacket(MobPacket.stopControllingAswanMonster(getObjectId()));
            } else {
                controllers.getClient().sendPacket(MobPacket.stopControllingMonster(getObjectId()));
            }
            sendStatus(controllers.getClient());
        }
        newController.controlMonster(this, immediateAggro);
        setController(newController);
        if (immediateAggro) {
            setControllerHasAggro(true);
        }
    }

    public final void addListener(final MonsterListener listener) {
        this.listener = listener;
    }

    public final boolean isControllerHasAggro() {
        return controllerHasAggro;
    }

    public final void setControllerHasAggro(final boolean controllerHasAggro) {
        this.controllerHasAggro = controllerHasAggro;
    }

    public final void sendStatus(final MapleClient client) {
        if (reflectpack != null) {
            client.sendPacket(reflectpack);
        }
        if (poisons.size() > 0) {
            poisonsLock.readLock().lock();
            try {
                client.sendPacket(MobPacket.applyMonsterStatus(this, poisons));
            } finally {
                poisonsLock.readLock().unlock();
            }
        }
    }

    @Override
    public final void sendSpawnData(final MapleClient client) {
        if (!isAlive()) {
            return;
        }
        if (GameConstants.isAswanMap(client.getPlayer().getMapId())) {
            client.sendPacket(MobPacket.spawnAswanMonster(this, fake && linkCID <= 0 ? -4 : -1, 0));
        } else {
            client.sendPacket(MobPacket.spawnMonster(this, fake && linkCID <= 0 ? -4 : -1, 0));
        }
        if (!this.monsterPet) {
            sendStatus(client);
            if (map != null && client.getPlayer() != null && client.getPlayer().getTruePosition().distanceSq(getTruePosition()) <= GameConstants.maxViewRangeSq_Half()) {
                map.updateMonsterController(this);
            }
        }
    }

    @Override
    public final void sendDestroyData(final MapleClient client) {
        if (stats.isEscort() && getEventInstance() != null && lastNode >= 0) { //shammos
            map.resetShammos(client);
        } else {
            if (GameConstants.isAswanMap(client.getPlayer().getMapId())) {
                client.sendPacket(MobPacket.killAswanMonster(getObjectId(), 0));
            } else {
                client.sendPacket(MobPacket.killMonster(getObjectId(), 0));
            }
            if (getController() != null && client.getPlayer() != null && client.getPlayer().getId() == getController().getId()) {
                client.getPlayer().stopControllingMonster(this);
            }
        }
    }

    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append(stats.getName());
        sb.append("(");
        sb.append(getId());
        sb.append(") (Level ");
        if(ostats != null)
            sb.append(ostats.level);
        else
            sb.append(stats.getLevel());
        sb.append(") at (X");
        sb.append(getTruePosition().x);
        sb.append("/ Y");
        sb.append(getTruePosition().y);
        sb.append(") with ");
        sb.append(getHp());
        sb.append("/ ");
        sb.append(getMobMaxHp());
        sb.append("hp, ");
        sb.append(getMp());
        sb.append("/ ");
        sb.append(getMobMaxMp());
        sb.append(" mp, oid: ");
        sb.append(getObjectId());
        sb.append(" || Controller : ");
        final MapleCharacter chr = controller.get();
        sb.append(chr != null ? chr.getName() : "none");

        return sb.toString();
    }

    public final String toString_() {
        final StringBuilder sb = new StringBuilder();
        sb.append(stats.getName());
        sb.append("(Level ");
        sb.append(stats.getLevel());
        sb.append(") has ");
        sb.append(getHp());
        sb.append("/");
        sb.append(getMobMaxHp());
        sb.append("HP, and ");
        sb.append(getMp());
        sb.append("/");
        sb.append(getMobMaxMp());
        sb.append(" MP.");
        return sb.toString();
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.MONSTER;
    }

    public final EventInstanceManager getEventInstance() {
        return eventInstance;
    }

    public final void setEventInstance(final EventInstanceManager eventInstance) {
        this.eventInstance = eventInstance;
    }

    public final int getStatusSourceID(final MonsterStatus status) {
        if (status == MonsterStatus.POISON || status == MonsterStatus.BURN) {
            poisonsLock.readLock().lock();
            try {
                for (MonsterStatusEffect ps : poisons) {
                    if (ps != null) {
                        return ps.getSkill();
                    }
                }
                return -1;
            } finally {
                poisonsLock.readLock().unlock();
            }
        }
        final MonsterStatusEffect effect = stati.get(status);
        if (effect != null) {
            return effect.getSkill();
        }
        return -1;
    }

    public final ElementalEffectiveness getEffectiveness(final Element e) {
        if (stati.size() > 0 && stati.containsKey(MonsterStatus.DOOM)) {
            return ElementalEffectiveness.NORMAL; // like blue snails
        }
        return stats.getEffectiveness(e);
    }

    public final void applyStatus(final MapleCharacter from, final MonsterStatusEffect status, final boolean poison, long duration, final boolean checkboss, final MapleStatEffect eff) {
        if (!isAlive() || getLinkCID() > 0) {
            return;
        }
        Skill skilz = SkillFactory.getSkill(status.getSkill());
        if (skilz != null) {
            switch (stats.getEffectiveness(skilz.getElement())) {
                case IMMUNE:
                case STRONG:
                    return;
                case NORMAL:
                case WEAK:
                    break;
                default:
                    return;
            }
        }
        // compos don't have an elemental (they have 2 - so we have to hack here...)
        final int statusSkill = status.getSkill();
        switch (statusSkill) {
            case 2111006: { // FP compo
                switch (stats.getEffectiveness(Element.POISON)) {
                    case IMMUNE:
                    case STRONG:
                        return;
                }
                break;
            }
            case 2211006: { // IL compo
                switch (stats.getEffectiveness(Element.ICE)) {
                    case IMMUNE:
                    case STRONG:
                        return;
                }
                break;
            }
            case 4120005:
            case 4220005:
            case 14110004: {
                switch (stats.getEffectiveness(Element.POISON)) {
                    case IMMUNE:
                    case STRONG:
                        return;
                }
                break;
            }
        }
        if (duration >= 2000000000) {
            duration = 5000; //teleport master
        }
        final MonsterStatus stat = status.getStatus();
        if (stats.isNoDoom() && stat == MonsterStatus.DOOM) {
            return;
        }

        if (stats.isBoss()) {
            if (stat == MonsterStatus.STUN) {
                return;
            }
            if (checkboss && stat != (MonsterStatus.SPEED) && stat != (MonsterStatus.NINJA_AMBUSH) && stat != (MonsterStatus.WATK) && stat != (MonsterStatus.POISON) && stat != MonsterStatus.BURN && stat != (MonsterStatus.DARKNESS) && stat != (MonsterStatus.MAGIC_CRASH)) {
                return;
            }
            //hack: don't magic crash cygnus boss
            if (getId() == 8850011 && stat == MonsterStatus.MAGIC_CRASH) {
                return;
            }
        }
        if (stats.isFriendly() || isFake()) {
            if (stat == MonsterStatus.STUN || stat == MonsterStatus.SPEED || stat == MonsterStatus.POISON || stat == MonsterStatus.BURN) {
                return;
            }
        }
        if ((stat == MonsterStatus.BURN || stat == MonsterStatus.POISON) && eff == null) {
            return;
        }
        if (stati.containsKey(stat)) {
            cancelStatus(stat);
        }
        if (stat == MonsterStatus.POISON || stat == MonsterStatus.BURN) {
            poisonsLock.readLock().lock();
            try {
                for (MonsterStatusEffect mse : poisons) {
                    if (mse != null && (mse.getSkill() == eff.getSourceId() || mse.getSkill() == GameConstants.getLinkedAranSkill(eff.getSourceId()) || GameConstants.getLinkedAranSkill(mse.getSkill()) == eff.getSourceId())) {
                        return;
                    }
                }
            } finally {
                poisonsLock.readLock().unlock();
            }
        }
        if (poison && getHp() > 1 && eff != null) {
            duration = Math.max(duration, eff.getDOTTime() * 1000);
        }
        duration += from.getStat().dotTime * 1000;
        long aniTime = duration;
        if (skilz != null) {
            aniTime += skilz.getAnimationTime();
        }
        status.setCancelTask(aniTime);
        if (poison && getHp() > 1) {
            status.setValue(status.getStatus(), Integer.valueOf((int) ((eff.getDOT() + from.getStat().dot + from.getStat().getDamageIncrease(eff.getSourceId())) * from.getStat().getCurrentMaxBaseDamage() / 100.0)));
            int dam = Integer.valueOf((int) (aniTime / 1000 * status.getX() / 2));
            status.setPoisonSchedule(dam, from);
            if (dam > 0) {
                if(dam > 999999)
                    dam = 999999;
                if (dam >= hp) {
                    dam = (int) (hp - 1);
                }
                damage(from, dam, false);
            }
        } else if (statusSkill == 4111003 || statusSkill == 14111001) { // shadow web
            status.setValue(status.getStatus(), (int) (getMobMaxHp() / 50.0 + 0.999));
            status.setPoisonSchedule(Integer.valueOf(status.getX()), from);
        } else if (statusSkill == 4341003) { // monsterbomb
            status.setPoisonSchedule((int) (eff.getDamage() * from.getStat().getCurrentMaxBaseDamage() / 100.0), from);

        } else if (statusSkill == 4121004 || statusSkill == 4221004) {
            status.setValue(status.getStatus(), Math.min(Short.MAX_VALUE, Integer.valueOf((int) (eff.getDamage() * from.getStat().getCurrentMaxBaseDamage() / 100.0))));
            int dam = Integer.valueOf((int) (aniTime / 1000 * status.getX() / 2));
            status.setPoisonSchedule(dam, from);
            if (dam > 0) {
                if(dam > 999999)
                    dam = 999999;
                if (dam >= hp) {
                    dam = (int) (hp - 1);
                }
                damage(from, dam, false);
            }
        }
        final MapleCharacter con = getController();
        if (stat == MonsterStatus.POISON || stat == MonsterStatus.BURN) {
            poisonsLock.writeLock().lock();
            try {
                poisons.add(status);
                if (con != null) {
                    map.broadcastMessage(con, MobPacket.applyMonsterStatus(this, poisons), getTruePosition());
                    con.getClient().sendPacket(MobPacket.applyMonsterStatus(this, poisons));
                } else {
                    map.broadcastMessage(MobPacket.applyMonsterStatus(this, poisons), getTruePosition());
                }
            } finally {
                poisonsLock.writeLock().unlock();
            }
        } else {
            stati.put(stat, status);
            if (con != null) {
                map.broadcastMessage(con, MobPacket.applyMonsterStatus(this, status), getTruePosition());
                con.getClient().sendPacket(MobPacket.applyMonsterStatus(this, status));
            } else {
                map.broadcastMessage(MobPacket.applyMonsterStatus(this, status), getTruePosition());
            }
        }
    }

    public void applyStatus(MonsterStatusEffect status) {
        if (stati.containsKey(status.getStatus())) {
            cancelStatus(status.getStatus());
        }
        stati.put(status.getStatus(), status);
        map.broadcastMessage(MobPacket.applyMonsterStatus(this, status), getTruePosition());
    }

    public final void dispelSkill(final MobSkill skillId) {
        List<MonsterStatus> toCancel = new ArrayList<MonsterStatus>();
        for (Entry<MonsterStatus, MonsterStatusEffect> effects : stati.entrySet()) {
            MonsterStatusEffect mse = effects.getValue();
            if (mse.getMobSkill() != null && mse.getMobSkill().getSkillId() == skillId.getSkillId()) { //not checking for level.
                toCancel.add(effects.getKey());
            }
        }
        for (MonsterStatus stat : toCancel) {
            cancelStatus(stat);
        }
    }

    public void applyMonsterBuff(final Map<MonsterStatus, Integer> effect, final int x, final int skillId, final long duration, final MobSkill skill, final List<Integer> reflection) {
        final MapleCharacter con = getController();
        Timer.BuffTimer BuffTimer = Timer.BuffTimer.getInstance();
        final Runnable cancelTask = new Runnable() {
            @Override
            public void run() {
                if (isAlive()) {
                    List<MonsterStatusEffect> mse = new ArrayList<>();
                    for (Entry<MonsterStatus, Integer> z : effect.entrySet()) {
                        mse.add(new MonsterStatusEffect(z.getKey(), z.getValue(), skillId, skill, true, reflection.size() > 0));
                    }
                    map.broadcastMessage(con, MobPacket.cancelMonsterStatus(MapleMonster.this, mse), getPosition());
                    if (getController() != null && !getController().isMapObjectVisible(MapleMonster.this)) {
                        getController().getClient().getSession().writeAndFlush(MobPacket.cancelMonsterStatus(MapleMonster.this, mse));
                    }
                    for (final MonsterStatus stat : effect.keySet()) {
                        stati.remove(stat);
                    }
                    reflection.clear();
                }
                if (con != null && ServerConstants.DEBUG) {
                    con.dropMessage(6, "結束 => 持續傷害: 結束時間[" + System.currentTimeMillis() + "]");
                }
            }
        };
        for (Entry<MonsterStatus, Integer> z : effect.entrySet()) {
            if (stati.containsKey(z.getKey())) {
                cancelStatus(z.getKey());
            }
            final MonsterStatusEffect effectz = new MonsterStatusEffect(z.getKey(), z.getValue(), 0, skill, true, reflection.size() > 0);
            effectz.setCancelTask(duration);
            stati.put(z.getKey(), effectz);
        }
        if (reflection.size() > 0) {
            List<MonsterStatusEffect> mse = new ArrayList<>();
            for (Entry<MonsterStatus, Integer> z : effect.entrySet()) {
                mse.add(new MonsterStatusEffect(z.getKey(), z.getValue(), 0, skill, true, reflection.size() > 0));
            }
            this.reflectpack = MobPacket.applyMonsterStatus(this, mse);
            if (con != null) {
                map.broadcastMessage(con, reflectpack, getTruePosition());
                con.getClient().getSession().writeAndFlush(this.reflectpack);
            } else {
                map.broadcastMessage(reflectpack, getTruePosition());
            }
        } else {
            for (Entry<MonsterStatus, Integer> z : effect.entrySet()) {
                final MonsterStatusEffect effectz = new MonsterStatusEffect(z.getKey(), z.getValue(), 0, skill, true, reflection.size() > 0);
                if (con != null) {
                    map.broadcastMessage(con, MobPacket.applyMonsterStatus(this, effectz), getTruePosition());
                    con.getClient().getSession().writeAndFlush(MobPacket.applyMonsterStatus(this, effectz));
                } else {
                    map.broadcastMessage(MobPacket.applyMonsterStatus(this, effectz), getTruePosition());
                }
            }
        }
        BuffTimer.schedule(cancelTask, duration);
        if (con != null && ServerConstants.DEBUG) {
            String bfn = "";
            for (Entry<MonsterStatus, Integer> z : effect.entrySet()) {
                bfn += "[" + z.getKey().name() + "] ";
            }
            con.dropMessage(6, "開始 => 怪物施放狀態: 持續時間[" + duration + "] 開始時間[" + System.currentTimeMillis() + "] 狀態效果:" + bfn);
        }
    }


    public final void setTempEffectiveness(final Element e, final long milli) {
        stats.setEffectiveness(e, ElementalEffectiveness.WEAK);
        EtcTimer.getInstance().schedule(new Runnable() {

            public void run() {
                stats.removeEffectiveness(e);
            }
        }, milli);
    }

    public final boolean isBuffed(final MonsterStatus status) {
        if (status == MonsterStatus.POISON || status == MonsterStatus.BURN) {
            return poisons.size() > 0 || stati.containsKey(status);
        }
        return stati.containsKey(status);
    }

    public final MonsterStatusEffect getBuff(final MonsterStatus status) {
        return stati.get(status);
    }

    public final int getStatiSize() {
        return stati.size() + (poisons.size() > 0 ? 1 : 0);
    }

    public final ArrayList<MonsterStatusEffect> getAllBuffs() {
        ArrayList<MonsterStatusEffect> ret = new ArrayList<MonsterStatusEffect>();
        for (MonsterStatusEffect e : stati.values()) {
            ret.add(e);
        }
        poisonsLock.readLock().lock();
        try {
            for (MonsterStatusEffect e : poisons) {
                ret.add(e);
            }
        } finally {
            poisonsLock.readLock().unlock();
        }
        return ret;
    }

    public final boolean isFake() {
        return fake;
    }

    public final void setFake(final boolean fake) {
        this.fake = fake;
    }

    public final MapleMap getMap() {
        return map;
    }

    public final void setMap(final MapleMap map) {
        this.map = map;
        startDropItemSchedule();
    }

    public final List<Pair<Integer, Integer>> getSkills() {
        return stats.getSkills();
    }

    public final boolean hasSkill(final int skillId, final int level) {
        return stats.hasSkill(skillId, level);
    }

    public final long getLastSkillUsed(final int skillId) {
        if (usedSkills.containsKey(skillId)) {
            return usedSkills.get(skillId);
        }
        return 0;
    }

    public final void setLastSkillUsed(final int skillId, final long now, final long cooltime) {
        switch (skillId) {
            case 140:
                usedSkills.put(skillId, now + (cooltime * 2));
                usedSkills.put(141, now);
                break;
            case 141:
                usedSkills.put(skillId, now + (cooltime * 2));
                usedSkills.put(140, now + cooltime);
                break;
            default:
                usedSkills.put(skillId, now + cooltime);
                break;
        }
    }

    public final byte getNoSkills() {
        return stats.getNoSkills();
    }

    public final boolean isFirstAttack() {
        return stats.isFirstAttack();
    }

    public final int getBuffToGive() {
        return stats.getBuffToGive();
    }

    public final void doPoison(final MonsterStatusEffect status, final WeakReference<MapleCharacter> weakChr) {
        if ((status.getStatus() == MonsterStatus.BURN || status.getStatus() == MonsterStatus.POISON) && poisons.size() <= 0) {
            return;
        }
        if (status.getStatus() != MonsterStatus.BURN && status.getStatus() != MonsterStatus.POISON && !stati.containsKey(status.getStatus())) {
            return;
        }
        if (weakChr == null) {
            return;
        }
        int damage = status.getPoisonSchedule();
        final boolean shadowWeb = status.getSkill() == 4111003 || status.getSkill() == 14111001;
        final MapleCharacter chr = weakChr.get();
        boolean cancel = damage <= 0 || chr == null || chr.getMapId() != map.getId();
        if (damage >= hp) {
            damage = (int) (hp - 1);
            cancel = !shadowWeb || cancel;
        }
        if (!cancel) {
            damage(chr, damage, false);
            if (shadowWeb) {
                map.broadcastMessage(MobPacket.damageMonster(getObjectId(), damage), getTruePosition());
            }
        }
    }

    public int getLinkOid() {
        return linkoid;
    }

    public void setLinkOid(int lo) {
        this.linkoid = lo;
    }

    public final ConcurrentEnumMap<MonsterStatus, MonsterStatusEffect> getStati() {
        return stati;
    }

    public void addEmpty() {
        for (MonsterStatus stat : MonsterStatus.values()) {
            if (stat.isDefault()) {
                stati.put(stat, new MonsterStatusEffect(stat, 0, 0, null, false));
            }
        }
    }

    public final int getStolen() {
        return stolen;
    }

    public final void setStolen(final int s) {
        this.stolen = s;
    }

    public final void handleSteal(MapleCharacter chr) {
        double showdown = 100.0;
        final MonsterStatusEffect mse = getBuff(MonsterStatus.SHOWDOWN);
        if (mse != null) {
            showdown += mse.getX();
        }

        Skill steal = SkillFactory.getSkill(4201004);
        final int level = chr.getTotalSkillLevel(steal), chServerrate = chr.getClient().getWorldServer().getDropRate();
        if (level > 0 && !getStats().isBoss() && stolen == -1 && steal.getEffect(level).makeChanceResult()) {
            final MapleMonsterInformationProvider mi = MapleMonsterInformationProvider.getInstance();
            final List<MonsterDropEntry> de = mi.retrieveDrop(getId());
            if (de == null) {
                stolen = 0;
                return;
            }
            final List<MonsterDropEntry> dropEntry = new ArrayList<MonsterDropEntry>(de);
            Collections.shuffle(dropEntry);
            Item idrop;
            for (MonsterDropEntry d : dropEntry) { //set to 4x rate atm, 40% chance + 10x
                if (d.itemId > 0 && d.questid == 0 && d.itemId / 10000 != 238 && Randomizer.nextInt(999999) < (int) (10 * d.chance * chServerrate * chr.getDropMod() * (chr.getStat().dropBuff / 100.0) * (showdown / 100.0))) { //kinda op
                    if (GameConstants.getInventoryType(d.itemId) == MapleInventoryType.EQUIP) {
                        Equip eq = (Equip) MapleItemInformationProvider.getInstance().getEquipById(d.itemId);
                        idrop = MapleItemInformationProvider.getInstance().randomizeStats(eq);
                    } else {
                        idrop = new Item(d.itemId, (byte) 0, (short) (d.Maximum != 1 ? Randomizer.nextInt(d.Maximum - d.Minimum) + d.Minimum : 1), (byte) 0);
                    }
                    stolen = d.itemId;
                    map.spawnMobDrop(idrop, map.calcDropPos(getPosition(), getTruePosition()), this, chr, (byte) 0, (short) 0);
                    break;
                }
            }
        } else {
            stolen = 0; //failed once, may not go again
        }
    }

    public final int getLastNode() {
        return lastNode;
    }

    public final void setLastNode(final int lastNode) {
        this.lastNode = lastNode;
    }

    public final void cancelStatus(final MonsterStatus stat) {
        if (stat == MonsterStatus.BLEED || stat == MonsterStatus.SUMMON) {
            return;
        }
        final MonsterStatusEffect mse = stati.get(stat);
        if (mse == null || !isAlive()) {
            return;
        }
        if (mse.isReflect()) {
            reflectpack = null;
        }
        mse.cancelPoisonSchedule(this);
        final MapleCharacter con = getController();
        if (con != null) {
            map.broadcastMessage(con, MobPacket.cancelMonsterStatus(this, mse), getTruePosition());
            con.getClient().getSession().writeAndFlush(MobPacket.cancelMonsterStatus(this, mse));
        } else {
            map.broadcastMessage(MobPacket.cancelMonsterStatus(this, mse), getTruePosition());
        }
        stati.remove(stat);
    }


    public final void cancelSingleStatus(final MonsterStatusEffect stat) {
        if (stat == null || stat.getStatus() == MonsterStatus.BLEED || stat.getStatus() == MonsterStatus.SUMMON || !isAlive()) {
            return;
        }
        if (stat.getStatus() != MonsterStatus.POISON && stat.getStatus() != MonsterStatus.VENOMOUS_WEAPON) {
            cancelStatus(stat.getStatus());
            return;
        }
        poisonsLock.writeLock().lock();
        try {
            if (!poisons.contains(stat)) {
                return;
            }
            poisons.remove(stat);
            if (stat.isReflect()) {
                reflectpack = null;
            }
            stat.cancelPoisonSchedule(this);
            final MapleCharacter con = getController();
            if (con != null) {
                map.broadcastMessage(con, MobPacket.cancelMonsterStatus(this, stat), getTruePosition());
                con.getClient().getSession().writeAndFlush(MobPacket.cancelMonsterStatus(this, stat));
            } else {
                map.broadcastMessage(MobPacket.cancelMonsterStatus(this, stat), getTruePosition());
            }
        } finally {
            poisonsLock.writeLock().unlock();
        }
    }

    public final void cancelDropItem() {
        lastDropTime = 0;
    }

    public final void startDropItemSchedule() {
        cancelDropItem();
        if (stats.getDropItemPeriod() <= 0 || !isAlive()) {
            return;
        }
        shouldDropItem = false;
        lastDropTime = System.currentTimeMillis();
    }

    public boolean shouldDrop(long now) {
        return lastDropTime > 0 && lastDropTime + (stats.getDropItemPeriod() * 1000) < now;
    }

    public void doDropItem(long now) {
        final int itemId;
        switch (getId()) {
            case 9300061:
                itemId = 4001101;
                break;
            default: //until we find out ... what other mobs use this and how to get the ITEMID
                cancelDropItem();
                return;
        }
        if (isAlive() && map != null) {
            if (shouldDropItem) {
                map.spawnAutoDrop(itemId, getTruePosition());
            } else {
                shouldDropItem = true;
            }
        }
        lastDropTime = now;
    }

    public byte[] getNodePacket() {
        return nodepack;
    }

    public void setNodePacket(final byte[] np) {
        this.nodepack = np;
    }

    public void registerKill(final long next) {
        this.nextKill = System.currentTimeMillis() + next;
    }

    public boolean shouldKill(long now) {
        return nextKill > 0 && now > nextKill;
    }

    public int getLinkCID() {
        return linkCID;
    }

    public void setLinkCID(int lc) {
        this.linkCID = lc;
        if (lc > 0) {
            stati.put(MonsterStatus.HYPNOTIZE, new MonsterStatusEffect(MonsterStatus.HYPNOTIZE, 60000, 30001062, null, false));
        }
    }

    private interface AttackerEntry {

        List<AttackingMapleCharacter> getAttackers();

        public void addDamage(MapleCharacter from, long damage, boolean updateAttackTime);

        public long getDamage();

        public boolean contains(MapleCharacter chr);

        public void killedMob(MapleMap map, int baseExp, boolean mostDamage, int lastSkill);
    }

    private static class AttackingMapleCharacter {

        private MapleCharacter attacker;
        private long lastAttackTime;

        public AttackingMapleCharacter(final MapleCharacter attacker, final long lastAttackTime) {
            super();
            this.attacker = attacker;
            this.lastAttackTime = lastAttackTime;
        }

        public final long getLastAttackTime() {
            return lastAttackTime;
        }

        public final void setLastAttackTime(final long lastAttackTime) {
            this.lastAttackTime = lastAttackTime;
        }

        public final MapleCharacter getAttacker() {
            return attacker;
        }
    }

    private static final class ExpMap {

        public final int exp;
        public final byte ptysize;
        public final byte Class_Bonus_EXP;
        public final byte Premium_Bonus_EXP;

        public ExpMap(final int exp, final byte ptysize, final byte Class_Bonus_EXP, final byte Premium_Bonus_EXP) {
            super();
            this.exp = exp;
            this.ptysize = ptysize;
            this.Class_Bonus_EXP = Class_Bonus_EXP;
            this.Premium_Bonus_EXP = Premium_Bonus_EXP;
        }
    }

    private static final class OnePartyAttacker {

        public MapleParty lastKnownParty;
        public long damage;
        public long lastAttackTime;

        public OnePartyAttacker(final MapleParty lastKnownParty, final long damage) {
            super();
            this.lastKnownParty = lastKnownParty;
            this.damage = damage;
            this.lastAttackTime = System.currentTimeMillis();
        }
    }

    private final class SingleAttackerEntry implements AttackerEntry {

        private long damage = 0;
        private int chrid;
        private long lastAttackTime;

        public SingleAttackerEntry(final MapleCharacter from) {
            this.chrid = from.getId();
        }

        @Override
        public void addDamage(final MapleCharacter from, final long damage, final boolean updateAttackTime) {
            if (chrid == from.getId()) {
                this.damage += damage;
                if (updateAttackTime) {
                    lastAttackTime = System.currentTimeMillis();
                }
            }
        }

        @Override
        public final List<AttackingMapleCharacter> getAttackers() {
            final MapleCharacter chr = map.getCharacterById(chrid);
            if (chr != null) {
                return Collections.singletonList(new AttackingMapleCharacter(chr, lastAttackTime));
            } else {
                return Collections.emptyList();
            }
        }

        @Override
        public boolean contains(final MapleCharacter chr) {
            return chrid == chr.getId();
        }

        @Override
        public long getDamage() {
            return damage;
        }

        @Override
        public void killedMob(final MapleMap map, final int baseExp, final boolean mostDamage, final int lastSkill) {
            final MapleCharacter chr = map.getCharacterById(chrid);
            if (chr != null && chr.isAlive()) {
                giveExpToCharacter(chr, baseExp, mostDamage, 1, (byte) 0, (byte) 0, (byte) 0, lastSkill);
            }
        }

        @Override
        public int hashCode() {
            return chrid;
        }

        @Override
        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final SingleAttackerEntry other = (SingleAttackerEntry) obj;
            return chrid == other.chrid;
        }
    }

    private class PartyAttackerEntry implements AttackerEntry {

        private final Map<Integer, OnePartyAttacker> attackers = new HashMap<Integer, OnePartyAttacker>(6);
        private long totDamage = 0;
        private int partyid;

        public PartyAttackerEntry(final int partyid) {
            this.partyid = partyid;
        }

        public List<AttackingMapleCharacter> getAttackers() {
            final List<AttackingMapleCharacter> ret = new ArrayList<AttackingMapleCharacter>(attackers.size());
            for (final Entry<Integer, OnePartyAttacker> entry : attackers.entrySet()) {
                final MapleCharacter chr = map.getCharacterById(entry.getKey());
                if (chr != null) {
                    ret.add(new AttackingMapleCharacter(chr, entry.getValue().lastAttackTime));
                }
            }
            return ret;
        }

        private final Map<MapleCharacter, OnePartyAttacker> resolveAttackers() {
            final Map<MapleCharacter, OnePartyAttacker> ret = new HashMap<MapleCharacter, OnePartyAttacker>(attackers.size());
            for (final Entry<Integer, OnePartyAttacker> aentry : attackers.entrySet()) {
                final MapleCharacter chr = map.getCharacterById(aentry.getKey());
                if (chr != null) {
                    ret.put(chr, aentry.getValue());
                }
            }
            return ret;
        }

        @Override
        public final boolean contains(final MapleCharacter chr) {
            return attackers.containsKey(chr.getId());
        }

        @Override
        public final long getDamage() {
            return totDamage;
        }

        public void addDamage(final MapleCharacter from, final long damage, final boolean updateAttackTime) {
            final OnePartyAttacker oldPartyAttacker = attackers.get(from.getId());
            if (oldPartyAttacker != null) {
                oldPartyAttacker.damage += damage;
                oldPartyAttacker.lastKnownParty = from.getParty();
                if (updateAttackTime) {
                    oldPartyAttacker.lastAttackTime = System.currentTimeMillis();
                }
            } else {
                // TODO actually this causes wrong behaviour when the party changes between attacks
                // only the last setup will get exp - but otherwise we'd have to store the full party
                // constellation for every attack/everytime it changes, might be wanted/needed in the
                // future but not now
                final OnePartyAttacker onePartyAttacker = new OnePartyAttacker(from.getParty(), damage);
                attackers.put(from.getId(), onePartyAttacker);
                if (!updateAttackTime) {
                    onePartyAttacker.lastAttackTime = 0;
                }
            }
            totDamage += damage;
        }

        @Override
        public final void killedMob(final MapleMap map, final int baseExp, final boolean mostDamage, final int lastSkill) {
            MapleCharacter pchr, highest = null;
            long iDamage, highestDamage = 0;
            int iexp = 0;
            MapleParty party;
            double addedPartyLevel, levelMod, innerBaseExp;
            List<MapleCharacter> expApplicable;
            final Map<MapleCharacter, ExpMap> expMap = new HashMap<MapleCharacter, ExpMap>(6);
            byte Class_Bonus_EXP;
            byte Premium_Bonus_EXP, added_partyinc;

            for (final Entry<MapleCharacter, OnePartyAttacker> attacker : resolveAttackers().entrySet()) {
                party = attacker.getValue().lastKnownParty;
                addedPartyLevel = 0;
                added_partyinc = 0;
                Class_Bonus_EXP = 0;
                Premium_Bonus_EXP = 0;
                expApplicable = new ArrayList<MapleCharacter>();
                for (final MaplePartyCharacter partychar : party.getMembers()) {
                    if (attacker.getKey().getLevel() - partychar.getLevel() <= 5 || stats.getLevel() - partychar.getLevel() <= 5) {
                        pchr = map.getCharacterById(partychar.getId());
                        if (pchr != null && pchr.isAlive()) {
                            expApplicable.add(pchr);
                            addedPartyLevel += pchr.getLevel();

                            Class_Bonus_EXP += GameConstants.Class_Bonus_EXP(pchr.getJob());
                            if (pchr.getStat().equippedWelcomeBackRing && Premium_Bonus_EXP == 0) {
                                Premium_Bonus_EXP = 80;
                            }
                            if (pchr.getStat().hasPartyBonus && added_partyinc < 4 && map.getPartyBonusRate() <= 0) {
                                added_partyinc++;
                            }
                        }
                    }
                }
                iDamage = attacker.getValue().damage;
                if (iDamage > highestDamage) {
                    highest = attacker.getKey();
                    highestDamage = iDamage;
                }
                innerBaseExp = baseExp * ((double) iDamage / totDamage);
                if (expApplicable.size() <= 1) {
                    Class_Bonus_EXP = 0; //no class bonus if not in a party.
                }

                for (final MapleCharacter expReceiver : expApplicable) {
                    iexp = expMap.get(expReceiver) == null ? 0 : expMap.get(expReceiver).exp;
                    levelMod = expReceiver.getLevel() / addedPartyLevel * (GameConstants.GMS ? 0.8 : 0.4);
                    iexp += (int) Math.round(((attacker.getKey().getId() == expReceiver.getId() ? (GameConstants.GMS ? 0.2 : 0.6) : 0.0) + levelMod) * innerBaseExp);
                    expMap.put(expReceiver, new ExpMap(iexp, (byte) (expApplicable.size() + added_partyinc), Class_Bonus_EXP, Premium_Bonus_EXP));
                }
            }
            ExpMap expmap;
            for (final Entry<MapleCharacter, ExpMap> expReceiver : expMap.entrySet()) {
                expmap = expReceiver.getValue();
                giveExpToCharacter(expReceiver.getKey(), expmap.exp, mostDamage ? expReceiver.getKey() == highest : false, expMap.size(), expmap.ptysize, expmap.Class_Bonus_EXP, expmap.Premium_Bonus_EXP, lastSkill);
            }
        }

        @Override
        public final int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + partyid;
            return result;
        }

        @Override
        public final boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final PartyAttackerEntry other = (PartyAttackerEntry) obj;
            if (partyid != other.partyid) {
                return false;
            }
            return true;
        }
    }
}
