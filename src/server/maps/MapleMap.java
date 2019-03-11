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
package server.maps;

import client.MapleCharacter;
import client.MapleCharacter.DojoMode;
import client.MapleClient;
import client.MapleStat;
import client.MonsterFamiliar;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import constants.GameConstants;
import constants.MapConstants;
import constants.WorldConfig;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import handling.login.LoginServer;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.World;
import handling.world.exped.ExpeditionType;
import scripting.EventInstanceManager;
import scripting.EventManager;
import server.*;
import server.MapleCarnivalFactory.MCSkill;
import server.MapleSquad.MapleSquadType;
import server.Timer.EtcTimer;
import server.Timer.MapTimer;
import server.life.*;
import server.maps.MapleNodes.DirectionInfo;
import server.maps.MapleNodes.MapleNodeInfo;
import server.maps.MapleNodes.MaplePlatform;
import server.maps.MapleNodes.MonsterPoint;
import server.status.MapleBuffStatus;
import server.status.MonsterStatus;
import server.status.MonsterStatusEffect;
import server.worldevents.MapleEvent;
import tools.FileoutputUtil;
import tools.StringUtil;
import tools.packet.CField;
import tools.packet.CField.EffectPacket;
import tools.packet.CField.NPCTalkPacket;
import tools.packet.CField.SummonPacket;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.PartyPacket;
import tools.packet.MobPacket;
import tools.packet.PetPacket;
import tools.types.Pair;

import java.awt.*;
import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class MapleMap {

    /*
     * Holds mappings of OID -> MapleMapObject separated by MapleMapObjectType.
     * Please acquire the appropriate lock when reading and writing to the LinkedHashMaps.
     * The MapObjectType Maps themselves do not need to synchronized in any way since they should never be modified.
     */
    private final Map<MapleMapObjectType, LinkedHashMap<Integer, MapleMapObject>> mapobjects;
    private final Map<MapleMapObjectType, ReentrantReadWriteLock> mapobjectlocks;
    private final List<MapleCharacter> characters = new ArrayList<>();
    private final ReentrantReadWriteLock charactersLock = new ReentrantReadWriteLock();
    private final Lock runningOidLock = new ReentrantLock();
    private final List<Spawns> monsterSpawn = new ArrayList<>();
    private final AtomicInteger spawnedMonstersOnMap = new AtomicInteger(0);
    private final Map<Integer, MaplePortal> portals = new HashMap<>();
    public boolean spawnsDisabled = false;
    // public ArrayList antiks_bm; // not sure why i'm using objects and not storing a list of integers?
    public List<Integer> blockedmaps = new ArrayList<>();
    public List<Integer> pvpmaps = new ArrayList<>();
    private int runningOid = 500000;
    private MapleFootholdTree footholds = null;
    private float monsterRate, recoveryRate;
    private MapleMapEffect mapEffect;
    private int channel, world;
    private short decHP = 0, createMobInterval = 6000, top = 0, bottom = 0, left = 0, right = 0;
    private int consumeItemCoolTime = 0, protectItem = 0, decHPInterval = 10000, mapid, returnMapId, timeLimit,
            fieldLimit, maxRegularSpawn = 0, fixedMob, forcedReturnMap = 999999999, instanceid = -1,
            lvForceMove = 0, lvLimit = 0, permanentWeather = 0, partyBonusRate = 0;
    private boolean town, clock, personalShop, everlast = false, dropsDisabled = false, gDropsDisabled = false,
            soaring = false, squadTimer = false, isSpawns = true, checkStates = true;
    private String mapName, streetName, onUserEnter, onFirstUserEnter, speedRunLeader = "";
    private List<Integer> dced = new ArrayList<>();
    private ScheduledFuture<?> squadSchedule;
    private long speedRunStart = 0, lastSpawnTime = 0, lastHurtTime = 0;
    private MapleNodes nodes;
    private MapleSquadType squad;
    private Map<String, Integer> environment = new LinkedHashMap<>();
    private boolean muted;
    private HashMap<MapleCharacter, MapleMonster> pvp_Players = null;
    private WeakReference<MapleCharacter> changeMobOrigin = null;

    public MapleMap(final int mapid, final int world, final int channel, final int returnMapId, final float monsterRate) {
        this.mapid = mapid;
        this.channel = (byte) channel;
        this.world = world;
        this.returnMapId = returnMapId;
        if (this.returnMapId == 999999999) {
            this.returnMapId = mapid;
        }
        if (GameConstants.getPartyPlay(mapid) > 0) {
            this.monsterRate = (monsterRate - 1.0f) * 2.5f + 1.0f;
        } else {
            this.monsterRate = monsterRate;
        }
        EnumMap<MapleMapObjectType, LinkedHashMap<Integer, MapleMapObject>> objsMap = new EnumMap<>(MapleMapObjectType.class);
        EnumMap<MapleMapObjectType, ReentrantReadWriteLock> objlockmap = new EnumMap<>(MapleMapObjectType.class);
        for (MapleMapObjectType type : MapleMapObjectType.values()) {
            objsMap.put(type, new LinkedHashMap<Integer, MapleMapObject>());
            objlockmap.put(type, new ReentrantReadWriteLock());
        }
        mapobjects = Collections.unmodifiableMap(objsMap);
        mapobjectlocks = Collections.unmodifiableMap(objlockmap);
    }

    public final boolean getSpawns() {
        return isSpawns;
    }

    public final void setSpawns(final boolean fm) {
        this.isSpawns = fm;
    }

    public final void toggleSpawns(final boolean fm) {
        this.spawnsDisabled = fm;
    }

    public final boolean spawnsDisabled() {
        return spawnsDisabled;
    }

    public boolean isAriantPQMap() {
        switch (this.getId()) {
            case 980010101:
            case 980010201:
            case 980010301:
                return true;
        }
        return false;
    }

    public final void setFixedMob(int fm) {
        this.fixedMob = fm;
    }

    public final int getForceMove() {
        return lvForceMove;
    }

    public final void setForceMove(int fm) {
        this.lvForceMove = fm;
    }

    public boolean getMuted() {
        return muted;
    }

    public void setMuted(boolean isMuted) {
        this.muted = isMuted;
    }

    public final int getLevelLimit() {
        return lvLimit;
    }

    public final void setLevelLimit(int fm) {
        this.lvLimit = fm;
    }

    public final void setSoaring(boolean b) {
        this.soaring = b;
    }

    public final boolean canSoar() {
        return soaring;
    }

    public final void toggleDrops() {
        this.dropsDisabled = !dropsDisabled;
    }

    public final void setDrops(final boolean b) {
        this.dropsDisabled = b;
    }

    public final void toggleGDrops() {
        this.gDropsDisabled = !gDropsDisabled;
    }

    public final int getId() {
        return mapid;
    }

    public final MapleMap getReturnMap() {
        return LoginServer.getInstance().getWorld(world).getChannel(channel).getMapFactory().getMap(returnMapId);
    }

    public final int getReturnMapId() {
        return returnMapId;
    }

    public final void setReturnMapId(int rmi) {
        this.returnMapId = rmi;
    }

    public final int getForcedReturnId() {
        return forcedReturnMap;
    }

    public final MapleMap getForcedReturnMap() {
        return LoginServer.getInstance().getWorld(world).getChannel(channel).getMapFactory().getMap(forcedReturnMap);
    }

    public final void setForcedReturnMap(final int map) {
        this.forcedReturnMap = map;
    }

    public final MapleMap getCrap2() {
        return ChannelServer.getInstance(world, channel).getMapFactory().getMap(100000000);
    }

    public final float getRecoveryRate() {
        return recoveryRate;
    }

    public final void setRecoveryRate(final float recoveryRate) {
        this.recoveryRate = recoveryRate;
    }

    public final int getFieldLimit() {
        return fieldLimit;
    }

    public final void setFieldLimit(final int fieldLimit) {
        this.fieldLimit = fieldLimit;
    }

    public final void setCreateMobInterval(final short createMobInterval) {
        this.createMobInterval = createMobInterval;
    }

    public final void setTimeLimit(final int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public final String getMapName() {
        return mapName;
    }

    public final void setMapName(final String mapName) {
        this.mapName = mapName;
    }

    public final String getStreetName() {
        return streetName;
    }

    public final void setStreetName(final String streetName) {
        this.streetName = streetName;
    }

    public final String getFirstUserEnter() {
        return onFirstUserEnter;
    }

    public final void setFirstUserEnter(final String onFirstUserEnter) {
        this.onFirstUserEnter = onFirstUserEnter;
    }

    public final String getUserEnter() {
        return onUserEnter;
    }

    public final void setUserEnter(final String onUserEnter) {
        this.onUserEnter = onUserEnter;
    }

    public final boolean hasClock() {
        return clock;
    }

    public final void setClock(final boolean hasClock) {
        this.clock = hasClock;
    }

    public final boolean isTown() {
        return town;
    }

    public final void setTown(final boolean town) {
        this.town = town;
    }

    public final boolean allowPersonalShop() {
        return personalShop;
    }

    public final void setPersonalShop(final boolean personalShop) {
        this.personalShop = personalShop;
    }

    public final boolean getEverlast() {
        return everlast;
    }

    public final void setEverlast(final boolean everlast) {
        this.everlast = everlast;
    }

    public final int getHPDec() {
        return decHP;
    }

    public final void setHPDec(final int delta) {
        if (delta > 0 || mapid == 749040100) { //pmd
            lastHurtTime = System.currentTimeMillis(); //start it up
        }
        decHP = (short) delta;
    }

    public final int getHPDecInterval() {
        return decHPInterval;
    }

    public final void setHPDecInterval(final int delta) {
        decHPInterval = delta;
    }

    public final int getHPDecProtect() {
        return protectItem;
    }

    public final void setHPDecProtect(final int delta) {
        this.protectItem = delta;
    }

    public final int getCurrentPartyId() {
        charactersLock.readLock().lock();
        try {
            final Iterator<MapleCharacter> ltr = characters.iterator();
            MapleCharacter chr;
            while (ltr.hasNext()) {
                chr = ltr.next();
                if (chr.getParty() != null) {
                    return chr.getParty().getId();
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return -1;
    }

    public final void addMapObject(final MapleMapObject mapobject) {
        runningOidLock.lock();
        int newOid;
        try {
            newOid = ++runningOid;
        } finally {
            runningOidLock.unlock();
        }

        mapobject.setObjectId(newOid);

        mapobjectlocks.get(mapobject.getType()).writeLock().lock();
        try {
            mapobjects.get(mapobject.getType()).put(newOid, mapobject);
        } finally {
            mapobjectlocks.get(mapobject.getType()).writeLock().unlock();
        }
    }

    private void spawnAndAddRangedMapObject(MapleMapObject mapobject, final DelayedPacketCreation packetbakery) {
        addMapObject(mapobject);

        charactersLock.readLock().lock();
        try {
            final Iterator<MapleCharacter> itr = characters.iterator();
            MapleCharacter chr;
            while (itr.hasNext()) {
                chr = itr.next();
                if (!chr.isClone() && (mapobject.getType() == MapleMapObjectType.MIST || chr.getTruePosition().distanceSq(mapobject.getTruePosition()) <= GameConstants.maxViewRangeSq())) {
                    packetbakery.sendPackets(chr.getClient());
                    chr.addVisibleMapObject(mapobject);
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
    }

    public final void removeMapObject(final MapleMapObject obj) {
        mapobjectlocks.get(obj.getType()).writeLock().lock();
        try {
            mapobjects.get(obj.getType()).remove(Integer.valueOf(obj.getObjectId()));
        } finally {
            mapobjectlocks.get(obj.getType()).writeLock().unlock();
        }
    }

    public final Point calcPointBelow(final Point initial) {
        final MapleFoothold fh = footholds.findBelow(initial);
        if (fh == null) {
            return null;
        }
        int dropY = fh.getY1();
        if (!fh.isWall() && fh.getY1() != fh.getY2()) {
            final double s1 = Math.abs(fh.getY2() - fh.getY1());
            final double s2 = Math.abs(fh.getX2() - fh.getX1());
            if (fh.getY2() < fh.getY1()) {
                dropY = fh.getY1() - (int) (Math.cos(Math.atan(s2 / s1)) * (Math.abs(initial.x - fh.getX1()) / Math.cos(Math.atan(s1 / s2))));
            } else {
                dropY = fh.getY1() + (int) (Math.cos(Math.atan(s2 / s1)) * (Math.abs(initial.x - fh.getX1()) / Math.cos(Math.atan(s1 / s2))));
            }
        }
        return new Point(initial.x, dropY);
    }

    public final Point calcDropPos(final Point initial, final Point fallback) {
        final Point ret = calcPointBelow(new Point(initial.x, initial.y - 50));
        if (ret == null) {
            return fallback;
        }
        return ret;
    }

    private void dropFromMonster(final MapleCharacter chr, final MapleMonster mob, final boolean instanced) {
        if (mob == null || chr == null || ChannelServer.getInstance(world, channel) == null || dropsDisabled || mob.dropsDisabled() || chr.getPyramidSubway() != null) { //no drops in pyramid ok? no cash either
            return;
        }

        //We choose not to readLock for this.
        //This will not affect the internal state, and we don't want to
        //introduce unneccessary locking, especially since this function
        //is probably used quite often.
        if (!instanced && mapobjects.get(MapleMapObjectType.ITEM).size() >= 250) {
            removeDrops();
        }

        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final byte droptype = (byte) (mob.getStats().isExplosiveReward() ? 3 : mob.getStats().isFfaLoot() ? 2 : chr.getParty() != null ? 1 : 0);
        final int mobpos = mob.getTruePosition().x;
        final int cmServerrate = WorldConfig.getById(world).getMesoRate();
        final int chServerrate = WorldConfig.getById(world).getDropRate();
        Item idrop;
        byte d = 1;
        Point pos = new Point(0, mob.getTruePosition().y);
        double showdown = 100.0;
        final MonsterStatusEffect mse = mob.getBuff(MonsterStatus.SHOWDOWN);
        if (mse != null) {
            showdown += mse.getX();
        }

        final MapleMonsterInformationProvider mi = MapleMonsterInformationProvider.getInstance();
        final List<MonsterDropEntry> drops = mi.retrieveDrop(mob.getId());
        if (drops == null) {
            return;
        }
        final List<MonsterDropEntry> dropEntry = new ArrayList<>(drops);

        if(mob.getMobLevel() >= 31 && mob.getMobLevel() <= 50)
            dropEntry.add(new MonsterDropEntry(4260000, 10000, 1 ,1, 0));
        else if(mob.getMobLevel() >= 51 && mob.getMobLevel() <= 70)
            dropEntry.add(new MonsterDropEntry(4260001, 10000, 1 ,1, 0));
        else if(mob.getMobLevel() >= 71 && mob.getMobLevel() <= 90)
            dropEntry.add(new MonsterDropEntry(4260002, 10000, 1 ,1, 0));
        else if(mob.getMobLevel() >= 91 && mob.getMobLevel() <= 110)
            dropEntry.add(new MonsterDropEntry(4260003, 10000, 1 ,1, 0));
        else if(mob.getMobLevel() >= 111 && mob.getMobLevel() <= 130)
            dropEntry.add(new MonsterDropEntry(4260004, 10000, 1 ,1, 0));
        else if(mob.getMobLevel() >= 131 && mob.getMobLevel() <= 150)
            dropEntry.add(new MonsterDropEntry(4260005, 10000, 1 ,1, 0));
        else if(mob.getMobLevel() >= 151 && mob.getMobLevel() <=170)
            dropEntry.add(new MonsterDropEntry(4260006, 10000, 1 ,1, 0));
        else if(mob.getMobLevel() >= 171 && mob.getMobLevel() <= 190)
            dropEntry.add(new MonsterDropEntry(4260007, 10000, 1 ,1, 0));
        else if(mob.getMobLevel() >= 191)
            dropEntry.add(new MonsterDropEntry(4260008, 2500, 1 ,1, 0));

        if(mob.getMobLevel() >= 140)
            dropEntry.add(new MonsterDropEntry(4021020, 1000, 1 ,1, 0));

        Collections.shuffle(dropEntry);

        boolean mesoDropped = false;
        for (final MonsterDropEntry de : dropEntry) {
            if (de.itemId == mob.getStolen()) {
                continue;
            }
            if(GameConstants.isHellChannelDrop(de.itemId) && channel < 11)
                continue;
            if (Randomizer.nextInt(999999) < (int) (de.chance * chServerrate * chr.getDropMod() * (chr.getStat().dropBuff / 100.0) * (showdown / 100.0))) {
                if (mesoDropped && droptype != 3 && de.itemId == 0) { //not more than 1 sack of meso
                    continue;
                }
                if (de.questid > 0 && chr.getQuestStatus(de.questid) != 1) {
                    continue;
                }
                if (de.itemId / 10000 == 238 && !mob.getStats().isBoss() && chr.getMonsterBook().getLevelByCard(ii.getCardMobId(de.itemId)) >= 2) {
                    continue;
                }
                if(de.itemId == 2000005 && !mob.getStats().isBoss())
                    continue;
                if (droptype == 3) {
                    pos.x = (mobpos + (d % 2 == 0 ? (40 * (d + 1) / 2) : -(40 * (d / 2))));
                } else {
                    pos.x = (mobpos + ((d % 2 == 0) ? (25 * (d + 1) / 2) : -(25 * (d / 2))));
                }
                if (de.itemId == 0) { // meso
                    int mesos = Randomizer.nextInt(1 + Math.abs(de.Maximum - de.Minimum)) + de.Minimum;
                    int mod = 1;
                    if(channel >= 11)
                        mod = 5;
                    if (mesos > 0) {
                        spawnMobMesoDrop((int) (mesos * mod * (chr.getStat().mesoBuff / 100.0) * chr.getDropMod() * cmServerrate), calcDropPos(pos, mob.getTruePosition()), mob, chr, false, droptype);
                        mesoDropped = true;
                    }
                } else {
                    if (GameConstants.getInventoryType(de.itemId) == MapleInventoryType.EQUIP) {
                        idrop = ii.randomizeStats((Equip) ii.getEquipById(de.itemId));
                    } else {
                        final int range = Math.abs(de.Maximum - de.Minimum);
                        idrop = new Item(de.itemId, (byte) 0, (short) (de.Maximum != 1 ? Randomizer.nextInt(range <= 0 ? 1 : range) + de.Minimum : 1), (byte) 0);
                    }
                    idrop.setGMLog("Dropped from monster " + mob.getId() + " on " + mapid);
                    if (chr.wantDrops()) {
                        spawnMobDrop(idrop, calcDropPos(pos, mob.getTruePosition()), mob, chr, droptype, de.questid);
                    }
                }
                d++;
            }
        }
        final List<MonsterGlobalDropEntry> globalEntry = new ArrayList<>(mi.getGlobalDrop());
        Collections.shuffle(globalEntry);
        final int cashz = (int) ((mob.getStats().isBoss() && mob.getStats().getHPDisplayType() == 0 ? 20 : 1));
        final int cashModifier = (int) ((mob.getStats().isBoss() ? 0 : ((mob.getMobExp() / 351 + 1) + (mob.getMobMaxHp() / 20000 + 1)))); //no rate
        // Global Drops
        for (final MonsterGlobalDropEntry de : globalEntry) {
            if (Randomizer.nextInt(999999) < de.chance && (de.continent < 0 || (de.continent < 10 && mapid / 100000000 == de.continent) || (de.continent < 100 && mapid / 10000000 == de.continent) || (de.continent < 1000 && mapid / 1000000 == de.continent))) {
                if (de.questid > 0 && chr.getQuestStatus(de.questid) != 1) {
                    continue;
                }
                if (de.itemId == 0) {
                    int all = Randomizer.rand(70, 100);
                    int pre = (int) ((Randomizer.nextInt(cashz) + cashz + cashModifier) * (chr.getStat().cashBuff / 100.0) * chr.getCashMod());
                    all = (all * pre) / 100;
                    if(chr.getLevel() > 251)
                        all += (all / 10 * (chr.getLevel() - 251));
                    chr.modifyCSPoints(2, all, true);
                } else if (!gDropsDisabled) {
                    if (droptype == 3) {
                        pos.x = (mobpos + (d % 2 == 0 ? (40 * (d + 1) / 2) : -(40 * (d / 2))));
                    } else {
                        pos.x = (mobpos + ((d % 2 == 0) ? (25 * (d + 1) / 2) : -(25 * (d / 2))));
                    }
                    if (GameConstants.getInventoryType(de.itemId) == MapleInventoryType.EQUIP) {
                        idrop = ii.randomizeStats((Equip) ii.getEquipById(de.itemId));
                    } else {
                        idrop = new Item(de.itemId, (byte) 0, (short) (de.Maximum != 1 ? Randomizer.nextInt(de.Maximum - de.Minimum) + de.Minimum : 1), (byte) 0);
                    }
                    idrop.setGMLog("Dropped from monster " + mob.getId() + " on " + mapid + " (Global)");
                    if (chr.wantDrops()) {
                        spawnMobDrop(idrop, calcDropPos(pos, mob.getTruePosition()), mob, chr, de.onlySelf ? 0 : droptype, de.questid);
                    }
                    d++;
                }
            }
        }
    }

    public void removeMonster(final MapleMonster monster) {
        if (monster == null) {
            return;
        }
        spawnedMonstersOnMap.decrementAndGet();
        if (GameConstants.isAswanMap(mapid)) {
            broadcastMessage(MobPacket.killAswanMonster(monster.getObjectId(), 0));
        } else {
            broadcastMessage(MobPacket.killMonster(monster.getObjectId(), 0));
        }
        removeMapObject(monster);
        monster.killed();
    }

    public void killMonster(final MapleMonster monster) { // For mobs with removeAfter
        if (monster == null) {
            return;
        }
        spawnedMonstersOnMap.decrementAndGet();
        monster.setHp(0);
        if (monster.getLinkCID() <= 0) {
            monster.spawnRevives(this);
        }
        broadcastMessage(MobPacket.killMonster(monster.getObjectId(), monster.getStats().getSelfD() < 0 ? 1 : monster.getStats().getSelfD()));
        removeMapObject(monster);
        monster.killed();
    }

    public final void killMonster(final MapleMonster monster, final MapleCharacter chr, final boolean withDrops, final boolean second, byte animation) {
        killMonster(monster, chr, withDrops, second, animation, 0);
    }

    public final void killMonster(final MapleMonster monster, final MapleCharacter chr, final boolean withDrops, final boolean second, byte animation, final int lastSkill) {
        if ((monster.getId() == 8810122 || monster.getId() == 8810018) && !second) {
            MapTimer.getInstance().schedule(new Runnable() {

                @Override
                public void run() {
                    killMonster(monster, chr, true, true, (byte) 1);
                    killAllMonsters(true);
                }
            }, 3000);
            return;
        }
        if (monster.getId() == 8820014) { //pb sponge, kills pb(w) first before dying
            killMonster(8820000);
        } else if (monster.getId() == 9300166) { //ariant pq bomb
            animation = 2; //or is it 3?
        }
        spawnedMonstersOnMap.decrementAndGet();
        removeMapObject(monster);
        monster.killed();
        final MapleSquad sqd = getSquadByMap();
        final boolean instanced = sqd != null || monster.getEventInstance() != null || getEMByMap() != null;
        int dropOwner = monster.killBy(chr, lastSkill);
        if (animation >= 0) {
            if (GameConstants.isAswanMap(getId())) {
                broadcastMessage(MobPacket.killAswanMonster(monster.getObjectId(), animation));
            } else {
                broadcastMessage(MobPacket.killMonster(monster.getObjectId(), animation));
            }
        }

        if (monster.getBuffToGive() > -1) {
            final int buffid = monster.getBuffToGive();
            final MapleStatEffect buff = MapleItemInformationProvider.getInstance().getItemEffect(buffid);

            charactersLock.readLock().lock();
            try {
                for (final MapleCharacter mc : characters) {
                    if (mc.isAlive()) {
                        buff.applyTo(mc);

                        switch (monster.getId()) {
                            case 8810018:
                            case 8810122:
                            case 8820001:
                                mc.getClient().sendPacket(EffectPacket.showOwnBuffEffect(buffid, 13, mc.getLevel(), 1)); // HT nine spirit
                                broadcastMessage(mc, EffectPacket.showBuffeffect(mc.getId(), buffid, 13, mc.getLevel(), 1), false); // HT nine spirit
                                break;
                        }
                    }
                }
            } finally {
                charactersLock.readLock().unlock();
            }
        }
        final int mobid = monster.getId();
        ExpeditionType type = null;
        if (mobid == 9400566) {
            chr.setHp(0);
            chr.updateSingleStat(MapleStat.HP, 0);
        } else if (mobid == 8810018 && mapid == 240060200) {
            World.Broadcast.broadcastGMMessage(chr.getWorld(), CWvsContext.broadcastMsg(5, "[GM訊息] Horntail was killed by : " + chr.getName()));
            World.Broadcast.broadcastMessage(chr.getWorld(), CWvsContext.broadcastMsg(6, "經過無數次的挑戰，"+ chr.getName() +" 所帶領的隊伍終於擊破了闇黑龍王的遠征隊！你們才是龍之林的真正英雄~"));
            for (MapleCharacter c : getCharactersThreadsafe()) {
                c.finishAchievement(16);
                c.finishDailyQuest(17);
                if(c.getGuild() != null)
                    c.getGuild().gainGP(150, false, c.getId());
            }
            if (speedRunStart > 0) {
                type = ExpeditionType.Horntail;
            }
            doShrine(true);
        } else if (mobid == 8810122 && mapid == 240060201) { // Horntail
            World.Broadcast.broadcastGMMessage(chr.getWorld(), CWvsContext.broadcastMsg(5, "[GM訊息] 寒霜冰龍 Horntail was killed by : " + chr.getName()));
            World.Broadcast.broadcastMessage(chr.getWorld(), CWvsContext.broadcastMsg(6, "To the crew that have finally conquered 寒霜冰龍 Horned Tail after numerous attempts, I salute thee! You are the true heroes of Leafre!!"));
            charactersLock.readLock().lock();
            try {
                for (MapleCharacter c : characters) {
                     c.finishAchievement(24);
                    c.finishDailyQuest(24);
                    if(c.getGuild() != null)
                        c.getGuild().gainGP(300, false, c.getId());
                }
            } finally {
                charactersLock.readLock().unlock();
            }
            if (speedRunStart > 0) {
                type = ExpeditionType.ChaosHT;
            }
            doShrine(true);
        } else if (mobid == 9400266 && mapid == 802000111) {
            doShrine(true);
        } else if (mobid == 9400265 && mapid == 802000211) {
            doShrine(true);
        } else if (mobid == 9400270 && mapid == 802000411) {
            doShrine(true);
        } else if (mobid == 9400273 && mapid == 802000611) {
            doShrine(true);
        } else if (mobid == 9400294 && mapid == 802000711) {
            doShrine(true);
        } else if (mobid == 9400296 && mapid == 802000803) {
            doShrine(true);
        } else if (mobid == 9400289 && mapid == 802000821) {
            doShrine(true);
            //INSERT HERE: 2095_tokyo
        } else if (mobid == 8830000 && mapid == 105100300) {
            if (speedRunStart > 0) {
                type = ExpeditionType.Normal_Balrog;
            }
        } else if ((mobid == 9420544 || mobid == 9420549) && mapid == 551030200 && monster.getEventInstance() != null && monster.getEventInstance().getName().contains(getEMByMap().getName())) {
            doShrine(getAllReactor().isEmpty());
            if (speedRunStart > 0) {
                if (mobid == 9420549) {
                    for (MapleCharacter c : getCharactersThreadsafe()) {
                        c.finishAchievement(15);
                        int rate = 1;
                        if(channel >= 11)
                            rate = 2;
                        if(c.getGuild() != null)
                            c.getGuild().gainGP(130 * rate, false, c.getId());
                    }
                } else {
                    for (MapleCharacter c : getCharactersThreadsafe()) {
                        c.finishAchievement(16);
                        int rate = 1;
                        if(channel >= 11)
                            rate = 2;
                        if(c.getGuild() != null)
                            c.getGuild().gainGP(130 * rate, false, c.getId());
                    }
                }
            }
        } else if (mobid == 8820001 && mapid == 270050100) {
            for (MapleCharacter c : getCharactersThreadsafe()) {
                c.finishAchievement(17);
                int rate = 1;
                if(channel >= 11)
                    rate = 2;
                if(c.getGuild() != null)
                    c.getGuild().gainGP(180 * rate, false, c.getId());
            }
            World.Broadcast.broadcastGMMessage(chr.getWorld(), CWvsContext.broadcastMsg(5, "[GM訊息] Pink bean was killed by : " + chr.getName()));
            World.Broadcast.broadcastMessage(chr.getWorld(), CWvsContext.broadcastMsg(6, "經過帶領的隊伍經過無數次的挑戰，終於擊破了時間的寵兒－皮卡丘的遠征隊！你們才是時間神殿的真正英雄~"));
            charactersLock.readLock().lock();
            try {
                for (MapleCharacter c : characters) {
                    // c.finishAchievement(17);
                }
            } finally {
                charactersLock.readLock().unlock();
            }
            if (speedRunStart > 0) {
                type = ExpeditionType.Pink_Bean;
            }
            doShrine(true);
        } else if (mobid == 8850011 && mapid == 271040100) {
            World.Broadcast.broadcastGMMessage(chr.getWorld(), CWvsContext.broadcastMsg(5, "[GM訊息] Empress was killed by : " + chr.getName()));
            World.Broadcast.broadcastMessage(chr.getWorld(), CWvsContext.broadcastMsg(6, "您是我們的英雄!!擊敗了 西格諾斯"));
            if (speedRunStart > 0) {
                type = ExpeditionType.Cygnus;
            }
            for (MapleCharacter c : getCharactersThreadsafe()) {
                int rate = 1;
                if(channel >= 11)
                    rate = 2;
                if(c.getGuild() != null)
                    c.getGuild().gainGP(230 * rate, false, c.getId());
            }
            doShrine(true);
        } else if (mobid == 8860000 && mapid == 272030400) {
            World.Broadcast.broadcastGMMessage(chr.getWorld(), CWvsContext.broadcastMsg(5, "[GM訊息] 阿卡 was killed by : " + chr.getName()));
            World.Broadcast.broadcastMessage(chr.getWorld(), CWvsContext.broadcastMsg(6, "您是我們的英雄!!擊敗了 阿卡伊濃"));

            for (MapleCharacter c : getCharactersThreadsafe()) {
                int rate = 1;
                if(channel >= 11)
                    rate = 2;
                if(c.getGuild() != null)
                    c.getGuild().gainGP(200 * rate, false, c.getId());
            }
            doShrine(true);
        } else if (mobid == 8840000 && mapid == 211070100) {
            if (speedRunStart > 0) {
                type = ExpeditionType.Von_Leon;
            }
            for (MapleCharacter c : getCharactersThreadsafe()) {
                int rate = 1;
                if(channel >= 11)
                    rate = 2;
                if(c.getGuild() != null)
                    c.getGuild().gainGP(180 * rate, false, c.getId());
            }
            doShrine(true);
        } else if (mobid == 8800002 && mapid == 280030000) {
            for (MapleCharacter c : getCharactersThreadsafe()) {
                c.finishAchievement(15);
                c.finishDailyQuest(13);
                if(c.getGuild() != null)
                    c.getGuild().gainGP(100, false, c.getId());
            }
//            FileoutputUtil.log(FileoutputUtil.Zakum_Log, MapDebug_Log());
            if (speedRunStart > 0) {
                type = ExpeditionType.Zakum;
            }
            doShrine(true);
        } else if (mobid == 8800102 && mapid == 280030001) {
            for (MapleCharacter c : getCharactersThreadsafe()) {
                c.finishAchievement(23);
                c.finishDailyQuest(23);
                if(c.getGuild() != null)
                    c.getGuild().gainGP(200, false, c.getId());
            }
            //FileoutputUtil.log(FileoutputUtil.Zakum_Log, MapDebug_Log());
            if (speedRunStart > 0) {
                type = ExpeditionType.Chaos_Zakum;
            }
            doShrine(true);
        } else if (mobid == 8870000 && mapid == 262031300) { //hilla
            //World.Broadcast.broadcastMessage(CWvsContext.broadcastMsg(6, "Hilla has been conquered!"));
            if (speedRunStart > 0) {
                type = ExpeditionType.Hilla;
            }
            doShrine(true);
        } else if (mobid == 9300281 && mapid == 921120300) {
            startMapEffect("How... How could my plan fail like this...? Even Rex... Ughhh...", 5120035);
            /* this isn't automatic in gms anymore, it's actually done by npc. :)
            } else if (mobid == 9300010 && mapid == 922010700 && getAllMonstersThreadsafe().size() == 0) { // LudiPQ - Rombots
            for (MaplePartyCharacter pchr : chr.getParty().getMembers()) {
                MapleCharacter chrz = chr.getClient().getChannelServer().getPlayerStorage().getCharacterById(pchr.getWorldId());
                EventInstanceManager eim = chr.getEventInstance();
                if (eim.getProperty("stage7status") == null) { // just in case
                    chrz.removeAll(4001022);
                    chrz.getMap().broadcastMessage(CField.showEffect("quest/party/clear"));
                    chrz.getMap().broadcastMessage(CField.playSound("Party1/Clear"));
                    chrz.getMap().broadcastMessage(CField.environmentChange("gate", 2));
                    eim.setProperty("stage7status", "clear");
                    chrz.gainExp(4620 * chr.getClient().getChannelServer().getExpRate(), true, true, true);
                }
            }*/
        } else if ((mapid >= 922010401 && mapid <= 922010405) && (mobid == 9300008 || mobid == 9300014)) { // LudiPQ - Dark Eyes/Shadow Eyes
            MapleMapFactory mf = chr.getClient().getChannelServer().getMapFactory();
            int q = 0;
            for (int i = 0; i < 5; i++) {
                q += mf.getMap(922010401 + i).getAllMonstersThreadsafe().size();
            }
            if (q == 0) {
                for (MaplePartyCharacter pchr : chr.getParty().getMembers()) {
                    MapleCharacter chrz = chr.getClient().getChannelServer().getPlayerStorage().getCharacterById(pchr.getId());
                    EventInstanceManager eim = chr.getEventInstance();
                    if (eim.getProperty("stage4status") == null) { // just in case
                        chrz.removeAll(4001022);
                        chrz.getMap().broadcastMessage(CField.showEffect("quest/party/clear"));
                        chrz.getMap().broadcastMessage(CField.playSound("Party1/Clear"));
                        eim.setProperty("stage4status", "clear");
                        chrz.gainExp(3360 * chr.getClient().getWorldServer().getExpRate(), true, true, true);
                    }
                }
            }
        } else if (mapid == 922010900 && mobid == 9300012) {
            for (MaplePartyCharacter pchr : chr.getParty().getMembers()) {
                MapleCharacter chrz = chr.getClient().getChannelServer().getPlayerStorage().getCharacterById(pchr.getId());

            }
        } else if ((mapid == 955000100 || mapid == 955000200 || mapid == 955000300) && getAllMonstersThreadsafe().isEmpty()) {
            charactersLock.readLock().lock();
            try {
                for (MapleCharacter c : characters) {
                    c.getClient().sendPacket(CField.showEffect("aswan/clear"));
                    switch (mapid) {
                        case 955000100:
                            c.dropMessage(5, "The portal to Stage 2 has been unlocked!");
                            break;
                        case 955000200:
                            c.dropMessage(5, "The portal to the Final Stage has been unlocked!");
                            break;
                        case 955000300:
                            c.dropMessage(5, "You are a brave warrior! Please, enter the portal for I wish to honor you!");
                            break;
                    }
                }
            } finally {
                charactersLock.readLock().unlock();
            }
        } else if (mapid == 926110001) {
            charactersLock.readLock().lock();
            try {
                for (MapleCharacter c : characters) {
                    if (getAllMonstersThreadsafe().size() > 0) {
                        c.dropMessage(-1, "There are " + getAllMonstersThreadsafe().size() + " monsters left.");
                    } else {
                        c.getMap().broadcastMessage(CField.showEffect("quest/party/clear")); // client
                        c.getClient().sendPacket(CField.showEffect("quest/party/clear")); // map
                        c.getMap().broadcastMessage(CField.playSound("Party1/Clear")); // client
                        c.getClient().sendPacket(CField.playSound("Party1/Clear")); // map
                    }
                }
            } finally {
                charactersLock.readLock().unlock();
            }
        } else if (mobid >= 8800003 && mobid <= 8800010) {
            boolean makeZakReal = true;
            final Collection<MapleMonster> monsters = getAllMonstersThreadsafe();

            for (final MapleMonster mons : monsters) {
                if (mons.getId() >= 8800003 && mons.getId() <= 8800010) {
                    makeZakReal = false;
                    break;
                }
            }
            if (makeZakReal) {
                for (final MapleMapObject object : monsters) {
                    final MapleMonster mons = ((MapleMonster) object);
                    if (mons.getId() == 8800000) {
                        final Point pos = mons.getTruePosition();
                        this.killAllMonsters(true);
                        spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8800000), pos);
                        break;
                    }
                }
            }
        } else if (mobid >= 8800103 && mobid <= 8800110) {
            boolean makeZakReal = true;
            final Collection<MapleMonster> monsters = getAllMonstersThreadsafe();

            for (final MapleMonster mons : monsters) {
                if (mons.getId() >= 8800103 && mons.getId() <= 8800110) {
                    makeZakReal = false;
                    break;
                }
            }
            if (makeZakReal) {
                for (final MapleMonster mons : monsters) {
                    if (mons.getId() == 8800100) {
                        final Point pos = mons.getTruePosition();
                        this.killAllMonsters(true);
                        spawnMonsterOnGroundBelow(MapleLifeFactory.getMonster(8800100), pos);
                        break;
                    }
                }
            }
        } else if (mobid == 8820008) { //wipe out statues and respawn
            for (final MapleMapObject mmo : getAllMonstersThreadsafe()) {
                MapleMonster mons = (MapleMonster) mmo;
                if (mons.getLinkOid() != monster.getObjectId()) {
                    killMonster(mons, chr, false, false, animation);
                }
            }
        } else if (mobid >= 8820010 && mobid <= 8820014) {
            for (final MapleMapObject mmo : getAllMonstersThreadsafe()) {
                MapleMonster mons = (MapleMonster) mmo;
                if (mons.getId() != 8820000 && mons.getId() != 8820001 && mons.getObjectId() != monster.getObjectId() && mons.isAlive() && mons.getLinkOid() == monster.getObjectId()) {
                    killMonster(mons, chr, false, false, animation);
                }
            }
        } else if (mobid / 100000 == 98 && chr.getMapId() / 10000000 == 95 && getAllMonstersThreadsafe().isEmpty()) {
            switch ((chr.getMapId() % 1000) / 100) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                    chr.getClient().sendPacket(CField.MapEff("monsterPark/clear"));
                    break;
                case 5:
                    if (chr.getMapId() / 1000000 == 952) {
                        chr.getClient().sendPacket(CField.MapEff("monsterPark/clearF"));
                    } else {
                        chr.getClient().sendPacket(CField.MapEff("monsterPark/clear"));
                    }
                    break;
                case 6:
                    chr.getClient().sendPacket(CField.MapEff("monsterPark/clearF"));
                    break;
            }
        } else if (mobid == 8150000 && (chr.getMapId() == 200090000 || chr.getMapId() == 200090000)) {
            chr.getClient().getChannelServer().getMapFactory().getMap(mapid).broadcastMessage(CField.boatPacket(10, 5));
        }else if ((mapid / 100 == 9250201 && monster.getStats().isBoss() && mobid != 9300216) ||(mapid / 100 == 9250301 && monster.getStats().isBoss()  && mobid != 9300216)){
            final int current_stage = Event_DojoAgent.CheckStage(this, mobid);
            final int next_mob = Event_DojoAgent.CheckNextMob(this, mobid);

            if(Event_DojoAgent.startNextStage(chr, this, current_stage, next_mob)) {
                final MapleMap warp_map = chr.getClient().getChannelServer().getMapFactory().getMap(925020001);
                if (chr.getParty() != null) {
                    for (MaplePartyCharacter mem : chr.getParty().getMembers()) {
                        MapleCharacter chr_mem = this.getCharacterById(mem.getId());
                        if (chr_mem != null) {
                            chr_mem.changeMap(warp_map, warp_map.getPortal(0));
                        }
                    }
                } else {
                    chr.changeMap(warp_map, warp_map.getPortal(0));
                }
            }
        }
        if (type != null) {
            if (speedRunStart > 0 && speedRunLeader.length() > 0) {
                long endTime = System.currentTimeMillis();
                String time = StringUtil.getReadableMillis(speedRunStart, endTime);
                broadcastMessage(CWvsContext.broadcastMsg(5, speedRunLeader + "'s squad has taken " + time + " to defeat " + type.name() + "!"));
                getRankAndAdd(speedRunLeader, time, type, (endTime - speedRunStart), (sqd == null ? null : sqd.getMembers()));
                endSpeedRun();
            }

        }
        if (withDrops && dropOwner != 1) {
            MapleCharacter drop;
            if (dropOwner <= 0) {
                drop = chr;
            } else {
                drop = getCharacterById(dropOwner);
                if (drop == null) {
                    drop = chr;
                }
            }
            dropFromMonster(drop, monster, instanced);
        }
    }

    public List<MapleReactor> getAllReactor() {
        return getAllReactorsThreadsafe();
    }

    public List<MapleReactor> getAllReactorsThreadsafe() {
        ArrayList<MapleReactor> ret = new ArrayList<>();
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                ret.add((MapleReactor) mmo);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        return ret;
    }

    public List<MapleSummon> getAllSummonsThreadsafe() {
        ArrayList<MapleSummon> ret = new ArrayList<>();
        mapobjectlocks.get(MapleMapObjectType.SUMMON).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.SUMMON).values()) {
                if (mmo instanceof MapleSummon) {
                    ret.add((MapleSummon) mmo);
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.SUMMON).readLock().unlock();
        }
        return ret;
    }

    public List<MapleMapObject> getAllDoor() {
        return getAllDoorsThreadsafe();
    }

    public List<MapleMapObject> getAllDoorsThreadsafe() {
        ArrayList<MapleMapObject> ret = new ArrayList<>();
        mapobjectlocks.get(MapleMapObjectType.DOOR).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.DOOR).values()) {
                if (mmo instanceof MapleDoor) {
                    ret.add(mmo);
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.DOOR).readLock().unlock();
        }
        return ret;
    }

    public List<MapleMapObject> getAllMechDoorsThreadsafe() {
        ArrayList<MapleMapObject> ret = new ArrayList<>();
        mapobjectlocks.get(MapleMapObjectType.DOOR).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.DOOR).values()) {
                if (mmo instanceof MechDoor) {
                    ret.add(mmo);
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.DOOR).readLock().unlock();
        }
        return ret;
    }

    public List<MapleMapObject> getAllMerchant() {
        return getAllHiredMerchantsThreadsafe();
    }

    public List<MapleMapObject> getAllHiredMerchantsThreadsafe() {
        ArrayList<MapleMapObject> ret = new ArrayList<>();
        mapobjectlocks.get(MapleMapObjectType.HIRED_MERCHANT).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.HIRED_MERCHANT).values()) {
                ret.add(mmo);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.HIRED_MERCHANT).readLock().unlock();
        }
        return ret;
    }

    public List<MapleMonster> getAllMonster() {
        return getAllMonstersThreadsafe();
    }

    public List<MapleMonster> getAllMonstersThreadsafe() {
        ArrayList<MapleMonster> ret = new ArrayList<>();
        mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.MONSTER).values()) {
                ret.add((MapleMonster) mmo);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
        }
        return ret;
    }

    public List<Integer> getAllUniqueMonsters() {
        ArrayList<Integer> ret = new ArrayList<>();
        mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.MONSTER).values()) {
                final int theId = ((MapleMonster) mmo).getId();
                if (!ret.contains(theId)) {
                    ret.add(theId);
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
        }
        return ret;
    }

    public final void killAllMonsters(final boolean animate) {
        for (final MapleMapObject monstermo : getAllMonstersThreadsafe()) {
            final MapleMonster monster = (MapleMonster) monstermo;
            spawnedMonstersOnMap.decrementAndGet();
            monster.setHp(0);
            if (GameConstants.isAswanMap(mapid)) {
                broadcastMessage(MobPacket.killAswanMonster(monster.getObjectId(), animate ? 1 : 0));
            } else {
                broadcastMessage(MobPacket.killMonster(monster.getObjectId(), animate ? 1 : 0));
            }
            removeMapObject(monster);
            monster.killed();
        }
    }

    public final void killMonster(final int monsId) {
        for (final MapleMapObject mmo : getAllMonstersThreadsafe()) {
            if (((MapleMonster) mmo).getId() == monsId) {
                spawnedMonstersOnMap.decrementAndGet();
                removeMapObject(mmo);
                if (GameConstants.isAswanMap(mapid)) {
                    broadcastMessage(MobPacket.killAswanMonster(mmo.getObjectId(), 1));
                } else {
                    broadcastMessage(MobPacket.killMonster(mmo.getObjectId(), 1));
                }
                ((MapleMonster) mmo).killed();
                break;
            }
        }
    }

    public final void killMonster2(final int monsId) {
        for (final MapleMapObject mmo : getAllMonstersThreadsafe()) {
            if (((MapleMonster) mmo).getId() == monsId) {
                spawnedMonstersOnMap.decrementAndGet();
                ((MapleMonster) mmo).setHp(0);
                broadcastMessage(MobPacket.killMonster(mmo.getObjectId(), 1));
                removeMapObject((MapleMonster) mmo);
                ((MapleMonster) mmo).killed();
                break;
            }
        }
    }

    private String MapDebug_Log() {
        final StringBuilder sb = new StringBuilder("Defeat time : ");
        sb.append(FileoutputUtil.CurrentReadable_Time());

        sb.append(" | Mapid : ").append(this.mapid);

        charactersLock.readLock().lock();
        try {
            sb.append(" Users [").append(characters.size()).append("] | ");
            for (MapleCharacter mc : characters) {
                sb.append(mc.getName()).append(", ");
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return sb.toString();
    }

    public final void limitReactor(final int rid, final int num) {
        List<MapleReactor> toDestroy = new ArrayList<>();
        Map<Integer, Integer> contained = new LinkedHashMap<>();
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                MapleReactor mr = (MapleReactor) obj;
                if (contained.containsKey(mr.getReactorId())) {
                    if (contained.get(mr.getReactorId()) >= num) {
                        toDestroy.add(mr);
                    } else {
                        contained.put(mr.getReactorId(), contained.get(mr.getReactorId()) + 1);
                    }
                } else {
                    contained.put(mr.getReactorId(), 1);
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        for (MapleReactor mr : toDestroy) {
            destroyReactor(mr.getObjectId());
        }
    }

    public final void destroyReactors(final int first, final int last) {
        List<MapleReactor> toDestroy = new ArrayList<>();
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                MapleReactor mr = (MapleReactor) obj;
                if (mr.getReactorId() >= first && mr.getReactorId() <= last) {
                    toDestroy.add(mr);
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        for (MapleReactor mr : toDestroy) {
            destroyReactor(mr.getObjectId());
        }
    }

    public final boolean checkOpenedGates() {
        List<MapleReactor> toDestroy = new ArrayList<>();
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                MapleReactor mr = (MapleReactor) obj;
                if ((mr.getReactorId() >= 9211000 && mr.getReactorId() <= 9218000) && mr.getState() == 1) {
                    toDestroy.add(mr);
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        return toDestroy.size() > 0;
    }

    public final void destroyReactor(final int oid) {
        final MapleReactor reactor = getReactorByOid(oid);
        if (reactor == null) {
            return;
        }
        broadcastMessage(CField.destroyReactor(reactor));
        reactor.setAlive(false);
        removeMapObject(reactor);
        reactor.setTimerActive(false);

        if (reactor.getDelay() > 0) {
            MapTimer.getInstance().schedule(new Runnable() {

                @Override
                public final void run() {
                    respawnReactor(reactor);
                }
            }, reactor.getDelay());
        }
    }

    public final void reloadReactors() {
        List<MapleReactor> toSpawn = new ArrayList<>();
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                final MapleReactor reactor = (MapleReactor) obj;
                broadcastMessage(CField.destroyReactor(reactor));
                reactor.setAlive(false);
                reactor.setTimerActive(false);
                toSpawn.add(reactor);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        for (MapleReactor r : toSpawn) {
            removeMapObject(r);
            if (!r.isCustom()) { //guardians cpq
                respawnReactor(r);
            }
        }
    }

    /*
     * command to reset all item-reactors in a map to state 0 for GM/NPC use - not tested (broken reactors get removed
     * from mapobjects when destroyed) Should create instances for multiple copies of non-respawning reactors...
     */
    public final void resetReactors() {
        setReactorState((byte) 0);
    }

    public final void setReactorState() {
        setReactorState((byte) 1);
    }

    public final void setReactorState(final byte state) {
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                ((MapleReactor) obj).forceHitReactor((byte) state);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    public final void setReactorDelay(final int state) {
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                if(((MapleReactor) obj).getReactorId() < 300000 && ((MapleReactor) obj).getState() == 4) {
                    if (getId() == 910001003 || getId() == 910001004 || getId() == 910001005 || getId() == 910001006 || getId() == 910001007 || getId() == 910001008){
                        ((MapleReactor) obj).setState((byte) 3);
                        ((MapleReactor) obj).scheduleSetState((byte) 3, (byte) state, 10000L);
                    }else {
                        ((MapleReactor) obj).setState((byte) 3);
                        ((MapleReactor) obj).scheduleSetState((byte) 3, (byte) state, 60000L);
                    }
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    /*
     * command to shuffle the positions of all reactors in a map for PQ purposes (such as ZPQ/LMPQ)
     */
    public final void shuffleReactors() {
        shuffleReactors(0, 9999999); //all
    }

    public final void shuffleReactors(int first, int last) {
        List<Point> points = new ArrayList<>();
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                MapleReactor mr = (MapleReactor) obj;
                if (mr.getReactorId() >= first && mr.getReactorId() <= last) {
                    points.add(mr.getPosition());
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
        Collections.shuffle(points);
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                MapleReactor mr = (MapleReactor) obj;
                if (mr.getReactorId() >= first && mr.getReactorId() <= last) {
                    mr.setPosition(points.remove(points.size() - 1));
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    /**
     * Automagically finds a new controller for the given monster from the chars
     * on the map...
     *
     * @param monster
     */
    public final void updateMonsterController(final MapleMonster monster) {
        if (!monster.isAlive() || monster.getLinkCID() > 0) {
            return;
        }
        if (monster.getController() != null) {
            if (monster.getController().getMap() != this || monster.getController().getTruePosition().distanceSq(monster.getTruePosition()) > monster.getRange()) {
                monster.getController().stopControllingMonster(monster);
            } else { // Everything is fine :)
                return;
            }
        }
        int mincontrolled = -1;
        MapleCharacter newController = null;

        charactersLock.readLock().lock();
        try {
            final Iterator<MapleCharacter> ltr = characters.iterator();
            MapleCharacter chr;
            while (ltr.hasNext()) {
                chr = ltr.next();
                if (!chr.isHidden() && !chr.isClone() && (chr.getControlledSize() < mincontrolled || mincontrolled == -1) && chr.getTruePosition().distanceSq(monster.getTruePosition()) <= monster.getRange()) {
                    mincontrolled = chr.getControlledSize();
                    newController = chr;
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        if (newController != null) {
            if (monster.isFirstAttack()) {
                newController.controlMonster(monster, true);
                monster.setControllerHasAggro(true);
            } else {
                newController.controlMonster(monster, false);
            }
        }
    }

    public final MapleMapObject getMapObject(int oid, MapleMapObjectType type) {
        mapobjectlocks.get(type).readLock().lock();
        try {
            return mapobjects.get(type).get(oid);
        } finally {
            mapobjectlocks.get(type).readLock().unlock();
        }
    }

    public final boolean containsNPC(int npcid) {
        mapobjectlocks.get(MapleMapObjectType.NPC).readLock().lock();
        try {
            Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.NPC).values().iterator();
            while (itr.hasNext()) {
                MapleNPC n = (MapleNPC) itr.next();
                if (n.getId() == npcid) {
                    return true;
                }
            }
            return false;
        } finally {
            mapobjectlocks.get(MapleMapObjectType.NPC).readLock().unlock();
        }
    }

    public MapleNPC getNPCById(int id) {
        mapobjectlocks.get(MapleMapObjectType.NPC).readLock().lock();
        try {
            Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.NPC).values().iterator();
            while (itr.hasNext()) {
                MapleNPC n = (MapleNPC) itr.next();
                if (n.getId() == id) {
                    return n;
                }
            }
            return null;
        } finally {
            mapobjectlocks.get(MapleMapObjectType.NPC).readLock().unlock();
        }
    }

    public MapleMonster getMonsterById(int id) {
        mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();
        try {
            MapleMonster ret = null;
            Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.MONSTER).values().iterator();
            while (itr.hasNext()) {
                MapleMonster n = (MapleMonster) itr.next();
                if (n.getId() == id) {
                    ret = n;
                    break;
                }
            }
            return ret;
        } finally {
            mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
        }
    }

    public int countMonsterById(int id) {
        mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();
        try {
            int ret = 0;
            Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.MONSTER).values().iterator();
            while (itr.hasNext()) {
                MapleMonster n = (MapleMonster) itr.next();
                if (n.getId() == id) {
                    ret++;
                }
            }
            return ret;
        } finally {
            mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
        }
    }

    public MapleReactor getReactorById(int id) {
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            MapleReactor ret = null;
            Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.REACTOR).values().iterator();
            while (itr.hasNext()) {
                MapleReactor n = (MapleReactor) itr.next();
                if (n.getReactorId() == id) {
                    ret = n;
                    break;
                }
            }
            return ret;
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    /**
     * returns a monster with the given oid, if no such monster exists returns
     * null
     *
     * @param oid
     * @return
     */
    public final MapleMonster getMonsterByOid(final int oid) {
        MapleMapObject mmo = getMapObject(oid, MapleMapObjectType.MONSTER);
        if (mmo == null) {
            return null;
        }
        return (MapleMonster) mmo;
    }

    public final MapleNPC getNPCByOid(final int oid) {
        MapleMapObject mmo = getMapObject(oid, MapleMapObjectType.NPC);
        if (mmo == null) {
            return null;
        }
        return (MapleNPC) mmo;
    }

    public final MapleReactor getReactorByOid(final int oid) {
        MapleMapObject mmo = getMapObject(oid, MapleMapObjectType.REACTOR);
        if (mmo == null) {
            return null;
        }
        return (MapleReactor) mmo;
    }

    public final MonsterFamiliar getFamiliarByOid(final int oid) {
        MapleMapObject mmo = getMapObject(oid, MapleMapObjectType.FAMILIAR);
        if (mmo == null) {
            return null;
        }
        return (MonsterFamiliar) mmo;
    }

    public final MapleReactor getReactorByName(final String name) {
        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (MapleMapObject obj : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                MapleReactor mr = ((MapleReactor) obj);
                if (mr.getName().equalsIgnoreCase(name)) {
                    return mr;
                }
            }
            return null;
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    public final void spawnNpc(final int id, final Point pos) {
        final MapleNPC npc = MapleLifeFactory.getNPC(id);
        npc.setPosition(pos);
        npc.setCy(pos.y);
        npc.setRx0(pos.x + 50);
        npc.setRx1(pos.x - 50);
        npc.setFh(getFootholds().findBelow(pos).getId());
        npc.setCustom(true);
        addMapObject(npc);
        broadcastMessage(NPCTalkPacket.spawnNPC(npc, true));
    }

    public final void removeNpc(final int npcid) {
        mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().lock();
        try {
            Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.NPC).values().iterator();
            while (itr.hasNext()) {
                MapleNPC npc = (MapleNPC) itr.next();
                if (npc.isCustom() && (npcid == -1 || npc.getId() == npcid)) {
                    broadcastMessage(NPCTalkPacket.removeNPCController(npc.getObjectId()));
                    broadcastMessage(NPCTalkPacket.removeNPC(npc.getObjectId()));
                    itr.remove();
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.NPC).writeLock().unlock();
        }
    }

    public final void hideNpc(final int npcid) {
        mapobjectlocks.get(MapleMapObjectType.NPC).readLock().lock();
        try {
            Iterator<MapleMapObject> itr = mapobjects.get(MapleMapObjectType.NPC).values().iterator();
            while (itr.hasNext()) {
                MapleNPC npc = (MapleNPC) itr.next();
                if (npcid == -1 || npc.getId() == npcid) {
                    broadcastMessage(NPCTalkPacket.removeNPCController(npc.getObjectId()));
                    broadcastMessage(NPCTalkPacket.removeNPC(npc.getObjectId()));
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.NPC).readLock().unlock();
        }
    }

    public final void spawnReactorOnGroundBelow(final MapleReactor mob, final Point pos) {
        mob.setPosition(pos); //reactors dont need FH lol
        mob.setCustom(true);
        spawnReactor(mob);
    }

    public final void spawnMonster_sSack(final MapleMonster mob, final Point pos, final int spawnType) {
        if (mob != null) {
            mob.setPosition(calcPointBelow(new Point(pos.x, pos.y - 1)));
            spawnMonster(mob, spawnType);
        }
    }

    public final void spawnMonsterOnGroundBelow(final MapleMonster mob, final Point pos) {
        spawnMonster_sSack(mob, pos, -2);
    }

    public final int spawnMonsterWithEffectBelow(final MapleMonster mob, final Point pos, final int effect) {
        final Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        return spawnMonsterWithEffect(mob, effect, spos);
    }

    public final void spawnZakum(final int x, final int y) {
        final Point pos = new Point(x, y);
        final MapleMonster mainb = MapleLifeFactory.getMonster(8800000);
        final Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        mainb.setPosition(spos);
        mainb.setFake(true);

        // Might be possible to use the map object for reference in future.
        spawnFakeMonster(mainb);

        final int[] zakpart = {8800003, 8800004, 8800005, 8800006, 8800007,
            8800008, 8800009, 8800010};

        for (final int i : zakpart) {
            final MapleMonster part = MapleLifeFactory.getMonster(i);
            part.setPosition(spos);

            spawnMonster(part, -2);
        }
        if (squadSchedule != null) {
            cancelSquadSchedule(false);
        }
    }

    public final void spawnChaosZakum(final int x, final int y) {
        final Point pos = new Point(x, y);
        final MapleMonster mainb = MapleLifeFactory.getMonster(8800100);
        final Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        mainb.setPosition(spos);
        mainb.setFake(true);

        // Might be possible to use the map object for reference in future.
        spawnFakeMonster(mainb);

        final int[] zakpart = {8800103, 8800104, 8800105, 8800106, 8800107,
            8800108, 8800109, 8800110};

        for (final int i : zakpart) {
            final MapleMonster part = MapleLifeFactory.getMonster(i);
            part.setPosition(spos);

            spawnMonster(part, -2);
        }
        if (squadSchedule != null) {
            cancelSquadSchedule(false);
        }
    }

    public final void spawnFakeMonsterOnGroundBelow(final MapleMonster mob, final Point pos) {
        Point spos = calcPointBelow(new Point(pos.x, pos.y - 1));
        spos.y -= 1;
        mob.setPosition(spos);
        spawnFakeMonster(mob);
    }

    public Point getGroundBelow(Point pos) {
        Point spos = new Point(pos.x, pos.y - 1);
        spos = calcPointBelow(spos);
        spos.y--;
        return spos;
    }

    private void checkRemoveAfter(final MapleMonster monster) {
        final int ra = monster.getStats().getRemoveAfter();

        if (ra > 0 && monster.getLinkCID() <= 0) {
            monster.registerKill(ra * 1000);
        }
    }

    public final void spawnRevives(final MapleMonster monster, final int oid) {
        monster.setMap(this);
        checkRemoveAfter(monster);
        monster.setLinkOid(oid);

        changelevel(monster);

        spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {

            @Override
            public final void sendPackets(MapleClient c) {
                if (GameConstants.isAswanMap(c.getPlayer().getMapId())) {
                    c.sendPacket(MobPacket.spawnAswanMonster(monster, monster.getStats().getSummonType() <= 1 ? -3 : monster.getStats().getSummonType(), oid)); // TODO statEffect
                } else {
                    c.sendPacket(MobPacket.spawnMonster(monster, monster.getStats().getSummonType() <= 1 ? -3 : monster.getStats().getSummonType(), oid)); // TODO statEffect
                }
            }
        });
        updateMonsterController(monster);

        spawnedMonstersOnMap.incrementAndGet();
    }

    private void changelevel(MapleMonster monster) {
        if(channel > 10 && (monster.getStats().getLevel() > 10 || monster.getStats().isBoss())){
            if(!monster.getStats().isBoss()) {
                int level = monster.getStats().getLevel();
                if (level >= 200)
                    level = 250;
                else
                    level = (short) ((level * 150 / 200) + 100);
                monster.HellChangeLevel(level, 512, 90);
            }else{

                int level = monster.getStats().getLevel();
                if (level >= 200)
                    level = 250;
                else
                    level = (short) ((level * 150 / 200) + 100);
                monster.HellChangeLevel(level, 200, 90);
            }
        }
    }

    public final void spawnMonster(final MapleMonster monster, final int spawnType) {
        spawnMonster(monster, spawnType, false);
    }

    public final void spawnMonster(final MapleMonster monster, final int spawnType, final boolean overwrite) {
        monster.setMap(this);
        checkRemoveAfter(monster);

        changelevel(monster);
        spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {

            @Override
            public final void sendPackets(MapleClient c) {
                if (GameConstants.isAswanMap(c.getPlayer().getMapId())) {
                    c.sendPacket(MobPacket.spawnAswanMonster(monster, monster.getStats().getSummonType() <= 1 || monster.getStats().getSummonType() == 27 || overwrite ? spawnType : monster.getStats().getSummonType(), 0));
                } else {
                    c.sendPacket(MobPacket.spawnMonster(monster, monster.getStats().getSummonType() <= 1 || monster.getStats().getSummonType() == 27 || overwrite ? spawnType : monster.getStats().getSummonType(), 0));
                }
                if (monster.getId() == 9300166) {
                    MapTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            for (MapleCharacter chr : getCharactersThreadsafe()) {
                                killMonster(monster, chr, false, false, (byte) 2);
                            }
                            //broadcastMessage(MobPacket.killMonster(monster.getObjectId(), 2));
                        }
                    }, 3000);
                }
            }
        });
        updateMonsterController(monster);

        spawnedMonstersOnMap.incrementAndGet();
    }

    public final int spawnMonsterWithEffect(final MapleMonster monster, final int effect, Point pos) {
        try {
            if(this.getId() == 925020000 || this.getId() == 925020001)
                return -1;
            monster.setMap(this);
            monster.setPosition(pos);

            spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {

                @Override
                public final void sendPackets(MapleClient c) {
                    if (GameConstants.isAswanMap(c.getPlayer().getMapId())) {
                        c.sendPacket(MobPacket.spawnAswanMonster(monster, effect, 0));
                    } else {
                        c.sendPacket(MobPacket.spawnMonster(monster, effect, 0));
                    }
                }
            });
            updateMonsterController(monster);

            spawnedMonstersOnMap.incrementAndGet();
            return monster.getObjectId();
        } catch (Exception e) {
            return -1;
        }
    }

    public final void spawnFakeMonster(final MapleMonster monster) {
        monster.setMap(this);
        monster.setFake(true);

        spawnAndAddRangedMapObject(monster, new DelayedPacketCreation() {

            @Override
            public final void sendPackets(MapleClient c) {
                if (GameConstants.isAswanMap(c.getPlayer().getMapId())) {
                    c.sendPacket(MobPacket.spawnAswanMonster(monster, -4, 0));
                } else {
                    c.sendPacket(MobPacket.spawnMonster(monster, -4, 0));
                }
            }
        });
        updateMonsterController(monster);

        spawnedMonstersOnMap.incrementAndGet();
    }

    public final void spawnReactor(final MapleReactor reactor) {
        reactor.setMap(this);

        spawnAndAddRangedMapObject(reactor, new DelayedPacketCreation() {

            @Override
            public final void sendPackets(MapleClient c) {
                c.sendPacket(CField.spawnReactor(reactor));
            }
        });
    }

    private void respawnReactor(final MapleReactor reactor) {
        reactor.setState((byte) 0);
        reactor.setAlive(true);
        spawnReactor(reactor);
    }

    public final void spawnDoor(final MapleDoor door) {
        spawnAndAddRangedMapObject(door, new DelayedPacketCreation() {

            @Override
            public final void sendPackets(MapleClient c) {
                door.sendSpawnData(c);
                c.sendPacket(CWvsContext.enableActions());
            }
        });
    }

    public final void spawnMechDoor(final MechDoor door) {
        spawnAndAddRangedMapObject(door, new DelayedPacketCreation() {

            @Override
            public final void sendPackets(MapleClient c) {
                c.sendPacket(CField.spawnMechDoor(door, true));
                c.sendPacket(CWvsContext.enableActions());
            }
        });
    }

    public final void spawnSummon(final MapleSummon summon) {
        summon.updateMap(this);
        spawnAndAddRangedMapObject(summon, new DelayedPacketCreation() {

            @Override
            public void sendPackets(MapleClient c) {
                if (summon != null && c.getPlayer() != null && (!summon.isChangedMap() || summon.getOwnerId() == c.getPlayer().getId())) {
                    c.sendPacket(SummonPacket.spawnSummon(summon, true));
                }
            }
        });
    }

    public final void spawnFamiliar(final MonsterFamiliar familiar) {
        spawnAndAddRangedMapObject(familiar, new DelayedPacketCreation() {

            @Override
            public void sendPackets(MapleClient c) {
                if (familiar != null && c.getPlayer() != null) {
                    c.sendPacket(CField.spawnFamiliar(familiar, true));
                }
            }
        });
    }

    public final void spawnExtractor(final MapleExtractor ex) {
        spawnAndAddRangedMapObject(ex, new DelayedPacketCreation() {

            @Override
            public void sendPackets(MapleClient c) {
                ex.sendSpawnData(c);
            }
        });
    }

    public final void spawnMist(final MapleMist mist, final int duration, boolean fake) {
        spawnAndAddRangedMapObject(mist, new DelayedPacketCreation() {

            @Override
            public void sendPackets(MapleClient c) {
                mist.sendSpawnData(c);
            }
        });

        final MapTimer tMan = MapTimer.getInstance();
        final ScheduledFuture<?> poisonSchedule;
        switch (mist.isPoisonMist()) {
            case 1:
                //poison: 0 = none, 1 = poisonous, 2 = recovery
                final MapleCharacter owner = getCharacterById(mist.getOwnerId());
                final boolean pvp = owner.inPVP();
                poisonSchedule = tMan.register(new Runnable() {

                    @Override
                    public void run() {
                        for (final MapleMapObject mo : getMapObjectsInRect(mist.getBox(), Collections.singletonList(pvp ? MapleMapObjectType.PLAYER : MapleMapObjectType.MONSTER))) {
                            if (pvp && mist.makeChanceResult() && !((MapleCharacter) mo).hasDOT() && ((MapleCharacter) mo).getId() != mist.getOwnerId()) {
                                ((MapleCharacter) mo).setDOT(mist.getSource().getDOT(), mist.getSourceSkill().getId(), mist.getSkillLevel());
                            } else if (!pvp && mist.makeChanceResult() && !((MapleMonster) mo).isBuffed(MonsterStatus.POISON)) {
                                ((MapleMonster) mo).applyStatus(owner, new MonsterStatusEffect(MonsterStatus.POISON, 1, mist.getSourceSkill().getId(), null, false), true, 60000, true, mist.getSource());
                            }
                        }
                    }
                }, 2000, 2500);
                break;
            case 4:
                poisonSchedule = tMan.register(new Runnable() {

                    @Override
                    public void run() {
                        for (final MapleMapObject mo : getMapObjectsInRect(mist.getBox(), Collections.singletonList(MapleMapObjectType.PLAYER))) {
                            if (mist.makeChanceResult()) {
                                final MapleCharacter chr = ((MapleCharacter) mo);
                                chr.addMP((int) (mist.getSource().getX() * (chr.getStat().getMaxMp() / 100.0)));
                            }
                        }
                    }
                }, 2000, 2500);
                break;
            default:
                poisonSchedule = null;
                break;
        }
        mist.setPoisonSchedule(poisonSchedule);
        mist.setSchedule(tMan.schedule(new Runnable() {

            @Override
            public void run() {
                broadcastMessage(CField.removeMist(mist.getObjectId(), false));
                removeMapObject(mist);
                if (poisonSchedule != null) {
                    poisonSchedule.cancel(false);
                }
            }
        }, 60000));
    }

    public final void disappearingItemDrop(final MapleMapObject dropper, final MapleCharacter owner, final Item item, final Point pos) {
        final Point droppos = calcDropPos(pos, pos);
        final MapleMapItem drop = new MapleMapItem(item, droppos, dropper, owner, (byte) 1, false);
        broadcastMessage(CField.dropItemFromMapObject(drop, dropper.getTruePosition(), droppos, (byte) 3), drop.getTruePosition());
    }

    public final void spawnMesoDrop(final int meso, final Point position, final MapleMapObject dropper, final MapleCharacter owner, final boolean playerDrop, final byte droptype) {
        final Point droppos = calcDropPos(position, position);
        final MapleMapItem mdrop = new MapleMapItem(meso, droppos, dropper, owner, droptype, playerDrop);

        spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation() {

            @Override
            public void sendPackets(MapleClient c) {
                c.sendPacket(CField.dropItemFromMapObject(mdrop, dropper.getTruePosition(), droppos, (byte) 1));
            }
        });
        if (!everlast) {
            mdrop.registerExpire(120000);
            if (droptype == 0 || droptype == 1) {
                mdrop.registerFFA(30000);
            }
        }
    }

    public final void spawnMobMesoDrop(final int meso, final Point position, final MapleMapObject dropper, final MapleCharacter owner, final boolean playerDrop, final byte droptype) {
        final MapleMapItem mdrop = new MapleMapItem(meso, position, dropper, owner, droptype, playerDrop);

        spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation() {

            @Override
            public void sendPackets(MapleClient c) {
                c.sendPacket(CField.dropItemFromMapObject(mdrop, dropper.getTruePosition(), position, (byte) 1));
            }
        });

        mdrop.registerExpire(120000);
        if (droptype == 0 || droptype == 1) {
            mdrop.registerFFA(30000);
        }
    }

    public final void spawnMobDrop(final Item idrop, final Point dropPos, final MapleMonster mob, final MapleCharacter chr, final byte droptype, final int questid) {
        final MapleMapItem mdrop = new MapleMapItem(idrop, dropPos, mob, chr, droptype, false, questid);

        spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation() {

            @Override
            public void sendPackets(MapleClient c) {
                if (c != null && c.getPlayer() != null && (questid <= 0 || c.getPlayer().getQuestStatus(questid) == 1) && (idrop.getItemId() / 10000 != 238 || c.getPlayer().getMonsterBook().getLevelByCard(idrop.getItemId()) >= 2) && mob != null && dropPos != null) {
                    c.sendPacket(CField.dropItemFromMapObject(mdrop, mob.getTruePosition(), dropPos, (byte) 1));
                }
            }
        });
//	broadcastMessage(CField.dropItemFromMapObject(mdrop, mob.getTruePosition(), dropPos, (byte) 0));

        mdrop.registerExpire(120000);
        if (droptype == 0 || droptype == 1) {
            mdrop.registerFFA(30000);
        }
        activateItemReactors(mdrop, chr.getClient());
    }

    public final void spawnRandDrop() {
        if (mapid != 910000000 || channel != 1) {
            return; //fm, ch1
        }

        mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();
        try {
            for (MapleMapObject o : mapobjects.get(MapleMapObjectType.ITEM).values()) {
                if (((MapleMapItem) o).isRandDrop()) {
                    return;
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
        }
        MapTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                final Point pos = new Point(Randomizer.nextInt(800) + 531, -806);
                final int theItem = Randomizer.nextInt(1000);
                int itemid;
                if (theItem < 950) { //0-949 = normal, 950-989 = rare, 990-999 = super
                    itemid = GameConstants.normalDrops[Randomizer.nextInt(GameConstants.normalDrops.length)];
                } else if (theItem < 990) {
                    itemid = GameConstants.rareDrops[Randomizer.nextInt(GameConstants.rareDrops.length)];
                } else {
                    itemid = GameConstants.superDrops[Randomizer.nextInt(GameConstants.superDrops.length)];
                }
                spawnAutoDrop(itemid, pos);
            }
        }, 20000);
    }

    public final void spawnAutoDrop(final int itemid, final Point pos) {
        Item idrop;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        if (GameConstants.getInventoryType(itemid) == MapleInventoryType.EQUIP) {
            idrop = ii.randomizeStats((Equip) ii.getEquipById(itemid));
        } else {
            idrop = new Item(itemid, (byte) 0, (short) 1, (byte) 0);
        }
        idrop.setGMLog("Dropped from auto " + " on " + mapid);
        final MapleMapItem mdrop = new MapleMapItem(pos, idrop);
        spawnAndAddRangedMapObject(mdrop, new DelayedPacketCreation() {

            @Override
            public void sendPackets(MapleClient c) {
                c.sendPacket(CField.dropItemFromMapObject(mdrop, pos, pos, (byte) 1));
            }
        });
        broadcastMessage(CField.dropItemFromMapObject(mdrop, pos, pos, (byte) 0));
        if (itemid / 10000 != 291) {
            mdrop.registerExpire(120000);
        }
    }

    public final void spawnItemDrop(final MapleMapObject dropper, final MapleCharacter owner, final Item item, Point pos, final boolean ffaDrop, final boolean playerDrop) {
        final Point droppos = calcDropPos(pos, pos);
        final MapleMapItem drop = new MapleMapItem(item, droppos, dropper, owner, (byte) 2, playerDrop);

        spawnAndAddRangedMapObject(drop, new DelayedPacketCreation() {

            @Override
            public void sendPackets(MapleClient c) {
                c.sendPacket(CField.dropItemFromMapObject(drop, dropper.getTruePosition(), droppos, (byte) 1));
            }
        });
        broadcastMessage(CField.dropItemFromMapObject(drop, dropper.getTruePosition(), droppos, (byte) 0));

        if (!everlast) {
            drop.registerExpire(120000);
            activateItemReactors(drop, owner.getClient());
        }
    }

    private void activateItemReactors(final MapleMapItem drop, final MapleClient c) {
        final Item item = drop.getItem();

        mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().lock();
        try {
            for (final MapleMapObject o : mapobjects.get(MapleMapObjectType.REACTOR).values()) {
                final MapleReactor react = (MapleReactor) o;

                if (react.getReactorType() == 100) {
                    if (item.getItemId() == GameConstants.getCustomReactItem(react.getReactorId(), react.getReactItem().getLeft()) && react.getReactItem().getRight() == item.getQuantity()) {
                        if (react.getArea().contains(drop.getTruePosition())) {
                            if (!react.isTimerActive()) {
                                MapTimer.getInstance().schedule(new ActivateItemReactor(drop, react, c), 5000);
                                react.setTimerActive(true);
                                break;
                            }
                        }
                    }
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.REACTOR).readLock().unlock();
        }
    }

    public int getItemsSize() {
        return mapobjects.get(MapleMapObjectType.ITEM).size();
    }

    public int getExtractorSize() {
        return mapobjects.get(MapleMapObjectType.EXTRACTOR).size();
    }

    public int getMobsSize() {
        return mapobjects.get(MapleMapObjectType.MONSTER).size();
    }

    public List<MapleMapItem> getAllItems() {
        return getAllItemsThreadsafe();
    }

    public List<MapleMapItem> getAllItemsThreadsafe() {
        ArrayList<MapleMapItem> ret = new ArrayList<>();
        mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.ITEM).values()) {
                ret.add((MapleMapItem) mmo);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
        }
        return ret;
    }

    public Point getPointOfItem(int itemid) {
        mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.ITEM).values()) {
                MapleMapItem mm = ((MapleMapItem) mmo);
                if (mm.getItem() != null && mm.getItem().getItemId() == itemid) {
                    return mm.getPosition();
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
        }
        return null;
    }

    public List<MapleMist> getAllMistsThreadsafe() {
        ArrayList<MapleMist> ret = new ArrayList<>();
        mapobjectlocks.get(MapleMapObjectType.MIST).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.MIST).values()) {
                ret.add((MapleMist) mmo);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.MIST).readLock().unlock();
        }
        return ret;
    }

    public final void returnEverLastItem(final MapleCharacter chr) {
        for (final MapleMapObject o : getAllItemsThreadsafe()) {
            final MapleMapItem item = ((MapleMapItem) o);
            if (item.getOwner() == chr.getId()) {
                item.setPickedUp(true);
                broadcastMessage(CField.removeItemFromMap(item.getObjectId(), 2, chr.getId()), item.getTruePosition());
                if (item.getMeso() > 0) {
                    chr.gainMeso(item.getMeso(), false);
                } else {
                    MapleInventoryManipulator.addFromDrop(chr.getClient(), item.getItem(), false);
                }
                removeMapObject(item);
            }
        }
        spawnRandDrop();
    }

    public final void talkMonster(final String msg, final int itemId, final int objectid, final int seconds) {
        if (itemId > 0) {
            startMapEffect(msg, itemId, false);
        }
        broadcastMessage(MobPacket.talkMonster(objectid, itemId, seconds, msg)); //5120035
        broadcastMessage(MobPacket.removeTalkMonster(objectid));
    }

    public final void startMapEffect(final String msg, final int itemId) {
        startMapEffect(msg, itemId, false);
    }

    public final void startMapEffect(final String msg, final int itemId, final boolean jukebox) {
        if (mapEffect != null) {
            return;
        }
        mapEffect = new MapleMapEffect(msg, itemId);
        mapEffect.setJukebox(jukebox);
        broadcastMessage(mapEffect.makeStartData());
        MapTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (mapEffect != null) {
                    broadcastMessage(mapEffect.makeDestroyData());
                    mapEffect = null;
                }
            }
        }, jukebox ? 300000 : 30000);
    }

    public final void startExtendedMapEffect(final String msg, final int itemId) {
        broadcastMessage(CField.startMapEffect(msg, itemId, true));
        MapTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                broadcastMessage(CField.removeMapEffect());
                broadcastMessage(CField.startMapEffect(msg, itemId, false));
                //dont remove mapeffect.
            }
        }, 60000);
    }

    public final void startSimpleMapEffect(final String msg, final int itemId) {
        broadcastMessage(CField.startMapEffect(msg, itemId, true));
    }

    public final void startJukebox(final String msg, final int itemId) {
        startMapEffect(msg, itemId, true);
    }

    public final void addPlayer(final MapleCharacter chr) {
        mapobjectlocks.get(MapleMapObjectType.PLAYER).writeLock().lock();
        try {
            mapobjects.get(MapleMapObjectType.PLAYER).put(chr.getObjectId(), chr);
        } finally {
            mapobjectlocks.get(MapleMapObjectType.PLAYER).writeLock().unlock();
        }

        charactersLock.writeLock().lock();
        try {
            characters.add(chr);
        } finally {
            charactersLock.writeLock().unlock();
        }
        chr.setChangeTime();
        if (GameConstants.isTeamMap(mapid) && !chr.inPVP()) {
            chr.setTeam(getAndSwitchTeam() ? 0 : 1);
        }
        final byte[] packet = CField.spawnPlayerMapobject(chr);
        if (!chr.isHidden()) {
            broadcastMessage(chr, packet, false);
            if (chr.isIntern() && speedRunStart > 0) {
                endSpeedRun();
                broadcastMessage(CWvsContext.broadcastMsg(5, "The speed run has ended."));
            }
        } else {
            broadcastGMMessage(chr, packet, false);
        }
        if (!chr.isClone()) {
            if (!onFirstUserEnter.equals("")) {
                if (getCharactersSize() == 1) {
                    MapScriptMethods.startScript_FirstUser(chr.getClient(), onFirstUserEnter);
                }
            }

            if (!onUserEnter.equals("")) {
                MapScriptMethods.startScript_User(chr.getClient(), onUserEnter);
            }
            chr.getClient().setPlayer(chr);
            sendObjectPlacement(chr);
            GameConstants.achievementRatio(chr.getClient());
            chr.getClient().sendPacket(packet);
            chr.getClient().sendPacket(CField.spawnFlags(nodes.getFlags()));
            if (GameConstants.isTeamMap(mapid) && !chr.inPVP()) {
                chr.getClient().sendPacket(CField.showEquipEffect(chr.getTeam()));
            }
            switch (mapid) {
                case 809000101:
                case 809000201:
                    chr.getClient().sendPacket(CField.showEquipEffect());
                    break;
                case 689000000:
                case 689000010:
                    chr.getClient().sendPacket(CField.getCaptureFlags(this));
                    break;
            }
        }
        MaplePet[] pets = chr.getSpawnPets();
        for (int i = 0; i < 3; i++) {
            if (pets[i] != null && pets[i].getSummoned()) {
                pets[i].setPos(chr.getTruePosition());
                chr.petUpdateStats(pets[i], true);
                chr.announce(PetPacket.showPet(chr, pets[i], false, false, true));
                chr.announce(PetPacket.loadExceptionList(chr, pets[i]));
            }
        }
        if (chr.getSummonedFamiliar() != null) {
            chr.spawnFamiliar(chr.getSummonedFamiliar());
        }
        if (chr.getAndroid() != null) {
            chr.getAndroid().setPos(chr.getPosition());
            broadcastMessage(CField.spawnAndroid(chr, chr.getAndroid()));
        }
        if (chr.getParty() != null && !chr.isClone()) {
            chr.silentPartyUpdate();
            chr.getClient().sendPacket(PartyPacket.updateParty(chr.getClient().getChannel(), chr.getParty(), PartyOperation.SILENT_UPDATE, null));
            chr.updatePartyMemberHP();
            chr.receivePartyMemberHP();
        }
        if (!chr.isInBlockedMap() && chr.getLevel() > 10) {
            chr.getClient().sendPacket(CField.getPublicNPCInfo());
        }
        if (GameConstants.isPhantom(chr.getJob())) {
            chr.getClient().sendPacket(CField.updateCardStack(chr.getCardStack()));
        }

        if (!chr.isClone()) {
            final List<MapleSummon> ss = chr.getSummonsReadLock();
            try {
                for (MapleSummon summon : ss) {
                    summon.setPosition(chr.getTruePosition());
                    chr.addVisibleMapObject(summon);
                    this.spawnSummon(summon);
                }
            } finally {
                chr.unlockSummonsReadLock();
            }
        }
        if (mapEffect != null) {
            mapEffect.sendStartData(chr.getClient());
        }
        if (timeLimit > 0 && getForcedReturnMap() != null && !chr.isClone()) {
            chr.startMapTimeLimitTask(timeLimit, getForcedReturnMap());
        }
        if (chr.getBuffedValue(MapleBuffStatus.MONSTER_RIDING) != null && !GameConstants.isResist(chr.getJob())) {
            if (FieldLimitType.Mount.check(fieldLimit)) {
                chr.cancelEffectFromBuffStat(MapleBuffStatus.MONSTER_RIDING);
            }
        }
        if (!chr.isClone()) {
            if (chr.getEventInstance() != null && chr.getEventInstance().isTimerStarted()) {
                if (chr.inPVP()) {
                    chr.getClient().sendPacket(CField.getPVPClock(Integer.parseInt(chr.getEventInstance().getProperty("type")), (int) (chr.getEventInstance().getTimeLeft() / 1000)));
                } else {
                    chr.getClient().sendPacket(CField.getClock((int) (chr.getEventInstance().getTimeLeft() / 1000)));
                }
            }
            if (hasClock()) {
                final Calendar cal = Calendar.getInstance();
                chr.getClient().sendPacket((CField.getClockTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND))));
            }
            if (chr.getCarnivalParty() != null && chr.getEventInstance() != null) {
                chr.getEventInstance().onMapLoad(chr);
            }
            MapleEvent.mapLoad(chr, channel);
            if (getSquadBegin() != null && getSquadBegin().getTimeLeft() > 0 && getSquadBegin().getStatus() == 1) {
                chr.getClient().sendPacket(CField.getClock((int) (getSquadBegin().getTimeLeft() / 1000)));
            }
            if (mapid / 1000 != 105100 && mapid / 100 != 8020003 && mapid / 100 != 8020008) { //no boss_balrog/2095/coreblaze/auf. but coreblaze/auf does AFTER
                final MapleSquad sqd = getSquadByMap(); //for all squads
                if (!squadTimer && sqd != null && chr.getName().equals(sqd.getLeaderName()) && !chr.isClone()) {
                    //leader? display
                    doShrine(false);
                    squadTimer = true;
                }
            }
            for (final WeakReference<MapleCharacter> chrz : chr.getClones()) {
                if (chrz.get() != null) {
                    chrz.get().setPosition(new Point(chr.getPosition()));
                    chrz.get().setMap(this);
                    addPlayer(chrz.get());
                }
            }
            switch (mapid) {
                case 922010401: // LudiPQ
                case 922010402:
                case 922010403:
                case 922010404:
                case 922010405:
                    startSimpleMapEffect((getAllMonstersThreadsafe().size() == 0
                            ? "I can't feel the energies of the monsters. Please proceed to a different room."
                            : "I can feel the energies of the monsters. Please find and defeat them."), 5120018);
                    break;
            }
            if (getNumMonsters() > 0 && (mapid == 280030001 || mapid == 240060201 || mapid == 280030000 || mapid == 240060200 || mapid == 220080001 || mapid == 541020800 || mapid == 541010100)) {
                String music = "Bgm09/TimeAttack";
                switch (mapid) {
                    case 240060200:
                    case 240060201:
                        music = "Bgm14/HonTale";
                        break;
                    case 280030000:
                    case 280030001:
                        music = "Bgm06/FinalFight";
                        break;
                }
                chr.getClient().sendPacket(CField.musicChange(music));
                //maybe timer too for zak/ht
            }
            if (mapid == 914000000 || mapid == 927000000) {
                chr.getClient().sendPacket(CWvsContext.temporaryStats_Aran());
            } else if (mapid == 105100300 && chr.getLevel() >= 91) {
                chr.getClient().sendPacket(CWvsContext.temporaryStats_Balrog(chr));
            } else if (mapid == 140090000 || mapid == 105100301 || mapid == 105100401 || mapid == 105100100) {
                chr.getClient().sendPacket(CWvsContext.temporaryStats_Reset());
            } else if (mapid == 240080000 || mapid == 240080600) { // Dragon Rider PQ
                if (!chr.isFlying()) {
                    chr.fly_PQ(chr);
                }
            } else if ((mapid >= 925020600 && mapid <= 925020609) || (mapid >= 925021200 && mapid <= 925021209) || (mapid >= 925021800 && mapid <= 925021809)
                    || (mapid >= 925022400 && mapid <= 925022409) || (mapid >= 925023000 && mapid <= 925023009) || (mapid >= 925023600 && mapid <= 925023609)) {
                chr.dojoMapEndTime = System.currentTimeMillis();
            } else if (mapid == 910000000) {
                chr.getClient().sendPacket(CField.musicChange(MapConstants.FM_BGM));
                chr.dropMessage("    **** 歡迎來到小喵谷的 自由市場! ****");
                chr.dropMessage("- @help - 查看可使用指令");
            }
        }
        if (GameConstants.isEvan(chr.getJob()) && chr.getJob() >= 2200) {
            if (chr.getDragon() == null) {
                chr.makeDragon();
            } else {
                chr.getDragon().setPosition(chr.getPosition());
            }
            if (chr.getDragon() != null) {
                broadcastMessage(CField.spawnDragon(chr.getDragon()));
            }
        }
        if (permanentWeather > 0) {
            chr.getClient().sendPacket(CField.startMapEffect("", permanentWeather, false)); //snow, no msg
        }
        if (getPlatforms().size() > 0) {
            chr.getClient().sendPacket(CField.getMovingPlatforms(this));
        }
        if (environment.size() > 0) {
            chr.getClient().sendPacket(CField.getUpdateEnvironment(this));
        }
//        if (partyBonusRate > 0) {
//            chr.dropMessage(-1, partyBonusRate + "% additional EXP will be applied per each party member here.");
//            chr.dropMessage(-1, ".");
//        }
        if (isTown()) {
            chr.cancelEffectFromBuffStat(MapleBuffStatus.RAINING_MINES);
        }
        if (!canSoar() || mapid == 240080700) {
            chr.cancelEffectFromBuffStat(MapleBuffStatus.SOARING);
        }
        if (chr.getJob() < 3200 || chr.getJob() > 3212) {
            chr.cancelEffectFromBuffStat(MapleBuffStatus.AURA);
        }
        if (GameConstants.isPhantom(chr.getJob())) {
            chr.fixSkillsByJob(); // because masteries reset upon cc, relog, or even cs exit. when we add to map we fix this issue.
        }
    }

    public int getNumItems() {
        mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();
        try {
            return mapobjects.get(MapleMapObjectType.ITEM).size();
        } finally {
            mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
        }
    }

    public int getNumMonsters() {
        mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().lock();
        try {
            return mapobjects.get(MapleMapObjectType.MONSTER).size();
        } finally {
            mapobjectlocks.get(MapleMapObjectType.MONSTER).readLock().unlock();
        }
    }

    public void doShrine(final boolean spawned) { //false = entering map, true = defeated
        if (squadSchedule != null) {
            cancelSquadSchedule(true);
        }
        final MapleSquad sqd = getSquadByMap();
        if (sqd == null) {
            return;
        }
        final int mode = (mapid == 280030000 ? 1 : (mapid == 280030001 ? 2 : (mapid == 240060200 || mapid == 240060201 ? 3 : 0)));
        //chaos_horntail message for horntail too because it looks nicer
        final EventManager em = getEMByMap();
        if (sqd != null && em != null && getCharactersSize() > 0) {
            final String leaderName = sqd.getLeaderName();
            final String state = em.getProperty("state");
            final Runnable run;
            MapleMap returnMapa = getForcedReturnMap();
            if (returnMapa == null || returnMapa.getId() == mapid) {
                returnMapa = getReturnMap();
            }
            if (mode == 1 || mode == 2) { //chaoszakum
                broadcastMessage(CField.showChaosZakumShrine(spawned, 5));
            } else if (mode == 3) { //ht/chaosht
                broadcastMessage(CField.showChaosHorntailShrine(spawned, 5));
            } else {
                broadcastMessage(CField.showHorntailShrine(spawned, 5));
            }
            if (spawned) { //both of these together dont go well
                broadcastMessage(CField.getClock(300)); //5 min
            }
            final MapleMap returnMapz = returnMapa;
            if (!spawned) { //no monsters yet; inforce timer to spawn it quickly
                final List<MapleMonster> monsterz = getAllMonstersThreadsafe();
                final List<Integer> monsteridz = new ArrayList<>();
                for (MapleMapObject m : monsterz) {
                    monsteridz.add(m.getObjectId());
                }
                run = new Runnable() {

                    @Override
                    public void run() {
                        final MapleSquad sqnow = MapleMap.this.getSquadByMap();
                        if (MapleMap.this.getCharactersSize() > 0 && MapleMap.this.getNumMonsters() == monsterz.size() && sqnow != null && sqnow.getStatus() == 2 && sqnow.getLeaderName().equals(leaderName) && MapleMap.this.getEMByMap().getProperty("state").equals(state)) {
                            boolean passed = monsterz.isEmpty();
                            for (MapleMapObject m : MapleMap.this.getAllMonstersThreadsafe()) {
                                for (int i : monsteridz) {
                                    if (m.getObjectId() == i) {
                                        passed = true;
                                        break;
                                    }
                                }
                                if (passed) {
                                    break;
                                } //even one of the monsters is the same
                            }
                            if (passed) {
                                //are we still the same squad? are monsters still == 0?
                                byte[] packet;
                                if (mode == 1 || mode == 2) { //chaoszakum
                                    packet = CField.showChaosZakumShrine(spawned, 0);
                                } else {
                                    packet = CField.showHorntailShrine(spawned, 0); //chaoshorntail message is weird
                                }
                                for (MapleCharacter chr : MapleMap.this.getCharactersThreadsafe()) { //warp all in map
                                    chr.getClient().sendPacket(packet);
                                    chr.changeMap(returnMapz, returnMapz.getPortal(0)); //hopefully event will still take care of everything once warp out
                                }
                                checkStates("");
                                resetFully();
                            }
                        }

                    }
                };
            } else { //inforce timer to gtfo
                run = new Runnable() {

                    @Override
                    public void run() {
                        MapleSquad sqnow = MapleMap.this.getSquadByMap();
                        //we dont need to stop clock here because they're getting warped out anyway
                        if (MapleMap.this.getCharactersSize() > 0 && sqnow != null && sqnow.getStatus() == 2 && sqnow.getLeaderName().equals(leaderName) && MapleMap.this.getEMByMap().getProperty("state").equals(state)) {
                            //are we still the same squad? monsters however don't count
                            byte[] packet;
                            if (mode == 1 || mode == 2) { //chaoszakum
                                packet = CField.showChaosZakumShrine(spawned, 0);
                            } else {
                                packet = CField.showHorntailShrine(spawned, 0); //chaoshorntail message is weird
                            }
                            for (MapleCharacter chr : MapleMap.this.getCharactersThreadsafe()) { //warp all in map
                                chr.getClient().sendPacket(packet);
                                chr.changeMap(returnMapz, returnMapz.getPortal(0)); //hopefully event will still take care of everything once warp out
                            }
                            checkStates("");
                            resetFully();
                        }
                    }
                };
            }
            squadSchedule = MapTimer.getInstance().schedule(run, 300000); //5 mins
        }
    }

    public final MapleSquad getSquadByMap() {
        MapleSquadType zz;
        switch (mapid) {
            case 105100400:
            case 105100300:
                zz = MapleSquadType.bossbalrog;
                break;
            case 280030000:
                zz = MapleSquadType.zak;
                break;
            case 280030001:
                zz = MapleSquadType.chaoszak;
                break;
            case 240060200:
                zz = MapleSquadType.horntail;
                break;
            case 240060201:
                zz = MapleSquadType.chaosht;
                break;
            case 270050100:
                zz = MapleSquadType.pinkbean;
                break;
            case 802000111:
                zz = MapleSquadType.nmm_squad;
                break;
            case 802000211:
                zz = MapleSquadType.vergamot;
                break;
            case 802000311:
                zz = MapleSquadType.tokyo_2095;
                break;
            case 802000411:
                zz = MapleSquadType.dunas;
                break;
            case 802000611:
                zz = MapleSquadType.nibergen_squad;
                break;
            case 802000711:
                zz = MapleSquadType.dunas2;
                break;
            case 802000801:
            case 802000802:
            case 802000803:
                zz = MapleSquadType.core_blaze;
                break;
            case 802000821:
            case 802000823:
                zz = MapleSquadType.aufheben;
                break;
            case 211070100:
            case 211070101:
            case 211070110:
                zz = MapleSquadType.vonleon;
                break;
            case 551030200:
                zz = MapleSquadType.scartar;
                break;
            case 271040100:
                zz = MapleSquadType.cygnus;
                break;
            case 262031300:
                zz = MapleSquadType.hilla;
                break;
            case 272030400:
                zz = MapleSquadType.arkarium;
                break;
            default:
                return null;
        }
        return ChannelServer.getInstance(world, channel).getMapleSquad(zz);
    }

    public final MapleSquad getSquadBegin() {
        if (squad != null) {
            return ChannelServer.getInstance(world, channel).getMapleSquad(squad);
        }
        return null;
    }

    public final EventManager getEMByMap() {
        String em;
        switch (mapid) {
            case 105100400:
                em = "BossBalrog_EASY";
                break;
            case 105100300:
                em = "BossBalrog_NORMAL";
                break;
            case 280030000:
                em = "ZakumBattle";
                break;
            case 240060200:
                em = "HorntailBattle";
                break;
            case 280030001:
                em = "ChaosZakum";
                break;
            case 240060201:
                em = "ChaosHorntail";
                break;
            case 270050100:
                em = "PinkBeanBattle";
                break;
            case 802000111:
                em = "NamelessMagicMonster";
                break;
            case 802000211:
                em = "Vergamot";
                break;
            case 802000311:
                em = "2095_tokyo";
                break;
            case 802000411:
                em = "Dunas";
                break;
            case 802000611:
                em = "Nibergen";
                break;
            case 802000711:
                em = "Dunas2";
                break;
            case 802000801:
            case 802000802:
            case 802000803:
                em = "CoreBlaze";
                break;
            case 802000821:
            case 802000823:
                em = "Aufhaven";
                break;
            case 211070100:
            case 211070101:
            case 211070110:
                em = "VonLeonBattle";
                break;
            case 551030200:
                em = "ScarTarBattle";
                break;
            case 271040100:
                em = "CygnusBattle";
                break;
            case 262031300:
                em = "HillaBattle";
                break;
            case 272020110:
            case 272030400:
                em = "ArkariumBattle";
                break;
            case 955000100:
            case 955000200:
            case 955000300:
                em = "AswanOffSeason";
                break;
            default:
                return null;
        }
        return LoginServer.getInstance().getChannel(world, channel).getEventSM().getEventManager(em);
    }

    public final void removePlayer(final MapleCharacter chr) {
        //log.warn("[dc] [level2] Player {} leaves map {}", new Object[] { chr.getName(), mapid });

        if (everlast) {
            returnEverLastItem(chr);
        }

        charactersLock.writeLock().lock();
        try {
            characters.remove(chr);
        } finally {
            charactersLock.writeLock().unlock();
        }
        removeMapObject(chr);
        chr.checkFollow();
        chr.removeExtractor();
        broadcastMessage(CField.removePlayerFromMap(chr.getId()));
        if (chr.getSummonedFamiliar() != null) {
            chr.removeVisibleFamiliar();
        }
        List<MapleSummon> toCancel = new ArrayList<>();
        final List<MapleSummon> ss = chr.getSummonsReadLock();
        try {
            for (final MapleSummon summon : ss) {
                broadcastMessage(SummonPacket.removeSummon(summon, true));
                removeMapObject(summon);
                if (summon.getMovementType() == SummonMovementType.STATIONARY || summon.getMovementType() == SummonMovementType.CIRCLE_STATIONARY || summon.getMovementType() == SummonMovementType.WALK_STATIONARY) {
                    toCancel.add(summon);
                } else {
                    summon.setChangedMap(true);
                }
            }
        } finally {
            chr.unlockSummonsReadLock();
        }
        for (MapleSummon summon : toCancel) {
            chr.removeSummon(summon);
            chr.dispelSkill(summon.getSkill()); //remove the buff
        }
        checkStates(chr.getName());
        if (mapid == 109020001) {
            chr.canTalk(true);
        }
        chr.leaveMap(this);
    }

    public final void broadcastMessage(final byte[] packet) {
        broadcastMessage(null, packet, Double.POSITIVE_INFINITY, null);
    }

    public final void broadcastMessage(final MapleCharacter source, final byte[] packet, final boolean repeatToSource) {
        broadcastMessage(repeatToSource ? null : source, packet, Double.POSITIVE_INFINITY, source.getTruePosition());
    }

    public final void broadcastMessage(final byte[] packet, final Point rangedFrom) {
        broadcastMessage(null, packet, GameConstants.maxViewRangeSq(), rangedFrom);
    }

    public final void broadcastMessage(final MapleCharacter source, final byte[] packet, final Point rangedFrom) {
        broadcastMessage(source, packet, GameConstants.maxViewRangeSq(), rangedFrom);
    }

    public void broadcastMessage(final MapleCharacter source, final byte[] packet, final double rangeSq, final Point rangedFrom) {
        charactersLock.readLock().lock();
        try {
            for (MapleCharacter chr : characters) {
                if (chr != source) {
                    if (rangeSq < Double.POSITIVE_INFINITY) {
                        if (rangedFrom.distanceSq(chr.getTruePosition()) <= rangeSq) {
                            chr.getClient().sendPacket(packet);
                        }
                    } else {
                        chr.getClient().sendPacket(packet);
                    }
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
    }

    private void sendObjectPlacement(final MapleCharacter c) {
        if (c == null || c.isClone()) {
            return;
        }
        for (final MapleMapObject o : getMapObjectsInRange(c.getTruePosition(), c.getRange(), GameConstants.rangedMapobjectTypes)) {
            if (o.getType() == MapleMapObjectType.REACTOR) {
                if (!((MapleReactor) o).isAlive()) {
                    continue;
                }
            }
            o.sendSpawnData(c.getClient());
            c.addVisibleMapObject(o);
        }
    }

    public final List<MaplePortal> getPortalsInRange(final Point from, final double rangeSq) {
        final List<MaplePortal> ret = new ArrayList<>();
        for (MaplePortal type : portals.values()) {
            if (from.distanceSq(type.getPosition()) <= rangeSq && type.getTargetMapId() != mapid && type.getTargetMapId() != 999999999) {
                ret.add(type);
            }
        }
        return ret;
    }

    public final List<MapleMapObject> getMapObjectsInRange(final Point from, final double rangeSq) {
        final List<MapleMapObject> ret = new ArrayList<>();
        for (MapleMapObjectType type : MapleMapObjectType.values()) {
            mapobjectlocks.get(type).readLock().lock();
            try {
                Iterator<MapleMapObject> itr = mapobjects.get(type).values().iterator();
                while (itr.hasNext()) {
                    MapleMapObject mmo = itr.next();
                    if (from.distanceSq(mmo.getTruePosition()) <= rangeSq) {
                        ret.add(mmo);
                    }
                }
            } finally {
                mapobjectlocks.get(type).readLock().unlock();
            }
        }
        return ret;
    }

    public List<MapleMapObject> getItemsInRange(Point from, double rangeSq) {
        return getMapObjectsInRange(from, rangeSq, Arrays.asList(MapleMapObjectType.ITEM));
    }

    public final List<MapleMapObject> getMapObjectsInRange(Point from, double rangeSq, List<MapleMapObjectType> MapObject_types) {
        final List<MapleMapObject> ret = new ArrayList<>();
        for (MapleMapObjectType type : MapObject_types) {
            mapobjectlocks.get(type).readLock().lock();
            try {
                Iterator<MapleMapObject> itr = mapobjects.get(type).values().iterator();
                while (itr.hasNext()) {
                    MapleMapObject mmo = itr.next();
                    if (from.distanceSq(mmo.getTruePosition()) <= rangeSq) {
                        ret.add(mmo);
                    }
                }
            } finally {
                mapobjectlocks.get(type).readLock().unlock();
            }
        }
        return ret;
    }

    // Eric: Restored this from v83, we will use this for Pet Attacking (Hacker Occupation)
    // Update: Rather then using lists, we'll now only use MONSTER
    // Update2: Just in case, I have made it stop forlooping all types and we'll now use the original (similar to v83)
    // Update3: Rather than sticking with the diamond operator I decided to do it the way it was used before..
    public final List<MapleMonster> getMapMonstersInRange(Point from, double rangeSq, MapleMapObjectType MapObject_type) {
        final List<MapleMonster> ret = new ArrayList<MapleMonster>(); // yes I know we can just use the diamond operator, but idc
        MapleMapObjectType type = MapObject_type;
        mapobjectlocks.get(type).readLock().lock();
        try {
            Iterator<MapleMapObject> itr = mapobjects.get(type).values().iterator();
            while (itr.hasNext()) {
                MapleMapObject mmo = itr.next();
                if (from.distanceSq(mmo.getTruePosition()) <= rangeSq) {
                    ret.add((MapleMonster) mmo);
                }
            }
        } finally {
            mapobjectlocks.get(type).readLock().unlock();
        }
        return ret;
    }

    public final List<MapleMapObject> getMapObjectsInRect(final Rectangle box, final List<MapleMapObjectType> MapObject_types) {
        final List<MapleMapObject> ret = new ArrayList<>();
        for (MapleMapObjectType type : MapObject_types) {
            mapobjectlocks.get(type).readLock().lock();
            try {
                Iterator<MapleMapObject> itr = mapobjects.get(type).values().iterator();
                while (itr.hasNext()) {
                    MapleMapObject mmo = itr.next();
                    if (box.contains(mmo.getTruePosition())) {
                        ret.add(mmo);
                    }
                }
            } finally {
                mapobjectlocks.get(type).readLock().unlock();
            }
        }
        return ret;
    }

    public final List<MapleCharacter> getCharactersIntersect(final Rectangle box) {
        final List<MapleCharacter> ret = new ArrayList<>();
        charactersLock.readLock().lock();
        try {
            for (MapleCharacter chr : characters) {
                if (chr.getBounds().intersects(box)) {
                    ret.add(chr);
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return ret;
    }

    public final List<MapleCharacter> getPlayersInRectAndInList(final Rectangle box, final List<MapleCharacter> chrList) {
        final List<MapleCharacter> character = new LinkedList<>();

        charactersLock.readLock().lock();
        try {
            final Iterator<MapleCharacter> ltr = characters.iterator();
            MapleCharacter a;
            while (ltr.hasNext()) {
                a = ltr.next();
                if (chrList.contains(a) && box.contains(a.getTruePosition())) {
                    character.add(a);
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return character;
    }

    public final void addPortal(final MaplePortal myPortal) {
        portals.put(myPortal.getId(), myPortal);
    }

    public final MaplePortal getPortal(final String portalname) {
        for (final MaplePortal port : portals.values()) {
            if (port.getName().equals(portalname)) {
                return port;
            }
        }
        return null;
    }

    public final MaplePortal getPortal(final int portalid) {
        return portals.get(portalid);
    }

    public final void resetPortals() {
        for (final MaplePortal port : portals.values()) {
            port.setPortalState(true);
        }
    }

    public final MapleFootholdTree getFootholds() {
        return footholds;
    }

    public final void setFootholds(final MapleFootholdTree footholds) {
        this.footholds = footholds;
    }

    public final int getNumSpawnPoints() {
        return monsterSpawn.size();
    }

    public final float getMonsterRate() {
        return monsterRate;
    }

    public final void setMonsterRate(int rate) {
        this.monsterRate *= rate;
    }

    public final void loadMonsterRate(final boolean first) {
        final int spawnSize = monsterSpawn.size();
        if (spawnSize >= 20 || partyBonusRate > 0) {
            maxRegularSpawn = Math.round(spawnSize / monsterRate);
        } else {
            maxRegularSpawn = (int) Math.ceil(spawnSize * monsterRate);
        }
        if (fixedMob > 0) {
            maxRegularSpawn = fixedMob;
        } else if (maxRegularSpawn <= 2) {
            maxRegularSpawn = 2;
        } else if (maxRegularSpawn > spawnSize) {
            maxRegularSpawn = Math.max(10, spawnSize);
        }

        Collection<Spawns> newSpawn = new LinkedList<>();
        Collection<Spawns> newBossSpawn = new LinkedList<>();
        for (final Spawns s : monsterSpawn) {
            if (s.getCarnivalTeam() >= 2) {
                continue; // Remove carnival spawned mobs
            }
            if (s.getMonster().isBoss()) {
                newBossSpawn.add(s);
            } else {
                newSpawn.add(s);
            }
        }
        monsterSpawn.clear();
        monsterSpawn.addAll(newBossSpawn);
        monsterSpawn.addAll(newSpawn);

        if (first && spawnSize > 0) {
            lastSpawnTime = System.currentTimeMillis();
            if (GameConstants.isForceRespawn(mapid)) {
                createMobInterval = 15000;
            }
            respawn(false); // this should do the trick, we don't need to wait upon entering map
        }
    }

    public final SpawnPoint addMonsterSpawn(final MapleMonster monster, final int mobTime, final byte carnivalTeam, final String msg) {
        final Point newpos = calcPointBelow(monster.getPosition());
        newpos.y -= 1;
        final SpawnPoint sp = new SpawnPoint(monster, newpos, mobTime, carnivalTeam, msg);
        if (carnivalTeam > -1) {
            monsterSpawn.add(0, sp); //at the beginning
        } else {
            monsterSpawn.add(sp);
        }
        return sp;
    }

    public final void addAreaMonsterSpawn(final MapleMonster monster, Point pos1, Point pos2, Point pos3, final int mobTime, final String msg, final boolean shouldSpawn) {
        pos1 = calcPointBelow(pos1);
        pos2 = calcPointBelow(pos2);
        pos3 = calcPointBelow(pos3);
        if (pos1 != null) {
            pos1.y -= 1;
        }
        if (pos2 != null) {
            pos2.y -= 1;
        }
        if (pos3 != null) {
            pos3.y -= 1;
        }
        if (pos1 == null && pos2 == null && pos3 == null) {
            System.out.println("WARNING: mapid " + mapid + ", monster " + monster.getId() + " could not be spawned.");

            return;
        } else if (pos1 != null) {
            if (pos2 == null) {
                pos2 = new Point(pos1);
            }
            if (pos3 == null) {
                pos3 = new Point(pos1);
            }
        } else if (pos2 != null) {
            if (pos1 == null) {
                pos1 = new Point(pos2);
            }
            if (pos3 == null) {
                pos3 = new Point(pos2);
            }
        } else if (pos3 != null) {
            if (pos1 == null) {
                pos1 = new Point(pos3);
            }
            if (pos2 == null) {
                pos2 = new Point(pos3);
            }
        }
        if (monster != null) {
            monsterSpawn.add(new SpawnPointAreaBoss(monster, pos1, pos2, pos3, mobTime, msg, shouldSpawn));
        }
    }

    public final List<MapleCharacter> getCharacters() {
        return getCharactersThreadsafe();
    }

    public final List<MapleCharacter> getCharactersThreadsafe() {
        final List<MapleCharacter> chars = new ArrayList<>();
        charactersLock.readLock().lock();
        try {
            for (MapleCharacter mc : characters) {
                chars.add(mc);
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return chars;
    }

    public final MapleCharacter getCharacterByName(final String id) {
        charactersLock.readLock().lock();
        try {
            for (MapleCharacter mc : characters) {
                if (mc.getName().equalsIgnoreCase(id)) {
                    return mc;
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return null;
    }

    public final MapleCharacter getCharacterById_InMap(final int id) {
        return getCharacterById(id);
    }

    public final MapleCharacter getCharacterById(final int id) {
        charactersLock.readLock().lock();
        try {
            for (MapleCharacter mc : characters) {
                if (mc.getId() == id) {
                    return mc;
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return null;
    }

    public final void updateMapObjectVisibility(final MapleCharacter chr, final MapleMapObject mo) {
        if (chr == null || chr.isClone()) {
            return;
        }
        if (!chr.isMapObjectVisible(mo)) { // monster entered view range
            if (mo.getType() == MapleMapObjectType.MIST || mo.getType() == MapleMapObjectType.EXTRACTOR || mo.getType() == MapleMapObjectType.SUMMON || mo.getType() == MapleMapObjectType.FAMILIAR || mo instanceof MechDoor || mo.getTruePosition().distanceSq(chr.getTruePosition()) <= mo.getRange()) {
                chr.addVisibleMapObject(mo);
                mo.sendSpawnData(chr.getClient());
            }
        } else { // monster left view range
            if (!(mo instanceof MechDoor) && mo.getType() != MapleMapObjectType.MIST && mo.getType() != MapleMapObjectType.EXTRACTOR && mo.getType() != MapleMapObjectType.SUMMON && mo.getType() != MapleMapObjectType.FAMILIAR && mo.getTruePosition().distanceSq(chr.getTruePosition()) > mo.getRange()) {
                chr.removeVisibleMapObject(mo);
                mo.sendDestroyData(chr.getClient());
            } else if (mo.getType() == MapleMapObjectType.MONSTER) { //monster didn't leave view range, and is visible
                if (chr.getTruePosition().distanceSq(mo.getTruePosition()) <= GameConstants.maxViewRangeSq_Half()) {
                    updateMonsterController((MapleMonster) mo);
                }
            }
        }
    }

    public void moveMonster(MapleMonster monster, Point reportedPos) {
        monster.setPosition(reportedPos);

        charactersLock.readLock().lock();
        try {
            for (MapleCharacter mc : characters) {
                updateMapObjectVisibility(mc, monster);
            }
        } finally {
            charactersLock.readLock().unlock();
        }
    }

    public void movePlayer(final MapleCharacter player, final Point newPosition) {
        player.setPosition(newPosition);
        if (!player.isClone()) {
            try {
                Collection<MapleMapObject> visibleObjects = player.getAndWriteLockVisibleMapObjects();
                ArrayList<MapleMapObject> copy = new ArrayList<>(visibleObjects);
                Iterator<MapleMapObject> itr = copy.iterator();
                while (itr.hasNext()) {
                    MapleMapObject mo = itr.next();
                    if (mo != null && getMapObject(mo.getObjectId(), mo.getType()) == mo) {
                        updateMapObjectVisibility(player, mo);
                    } else if (mo != null) {
                        visibleObjects.remove(mo);
                    }
                }
                for (MapleMapObject mo : getMapObjectsInRange(player.getTruePosition(), player.getRange())) {
                    if (mo != null && !visibleObjects.contains(mo)) {
                        mo.sendSpawnData(player.getClient());
                        visibleObjects.add(mo);
                    }
                }
            } finally {
                player.unlockWriteVisibleMapObjects();
            }
        }

    }

    public MaplePortal findClosestSpawnpoint(Point from) {
        MaplePortal closest = getPortal(0);
        double distance, shortestDistance = Double.POSITIVE_INFINITY;
        for (MaplePortal portal : portals.values()) {
            distance = portal.getPosition().distanceSq(from);
            if (portal.getType() >= 0 && portal.getType() <= 2 && distance < shortestDistance && portal.getTargetMapId() == 999999999) {
                closest = portal;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    public MaplePortal findClosestPortal(Point from) {
        MaplePortal closest = getPortal(0);
        double distance, shortestDistance = Double.POSITIVE_INFINITY;
        for (MaplePortal portal : portals.values()) {
            distance = portal.getPosition().distanceSq(from);
            if (distance < shortestDistance) {
                closest = portal;
                shortestDistance = distance;
            }
        }
        return closest;
    }

    public String spawnDebug() {
        StringBuilder sb = new StringBuilder("Mobs in map : ");
        sb.append(this.getMobsSize());
        sb.append(" spawnedMonstersOnMap: ");
        sb.append(spawnedMonstersOnMap);
        sb.append(" spawnpoints: ");
        sb.append(monsterSpawn.size());
        sb.append(" maxRegularSpawn: ");
        sb.append(maxRegularSpawn);
        sb.append(" actual monsters: ");
        sb.append(getNumMonsters());
        sb.append(" monster rate: ");
        sb.append(monsterRate);
        sb.append(" fixed: ");
        sb.append(fixedMob);

        return sb.toString();
    }

    public int characterSize() {
        return characters.size();
    }

    public final int getMapObjectSize() {
        return mapobjects.size() + getCharactersSize() - characters.size();
    }

    public final int getCharactersSize() {
        int ret = 0;
        charactersLock.readLock().lock();
        try {
            final Iterator<MapleCharacter> ltr = characters.iterator();
            MapleCharacter chr;
            while (ltr.hasNext()) {
                chr = ltr.next();
                if (!chr.isClone()) {
                    ret++;
                }

            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return ret;
    }

    public Collection<MaplePortal> getPortals() {
        return Collections.unmodifiableCollection(portals.values());
    }

    public int getSpawnedMonstersOnMap() {
        return spawnedMonstersOnMap.get();
    }

    public void spawnMonsterOnGroudBelow(MapleMonster mob, Point pos) {
        spawnMonsterOnGroundBelow(mob, pos);
    }

    public Collection<MaplePortal> getAllPortals() {
        return portals.values();
    }

    public void broadcastNONAdminMessage(MapleCharacter source, byte[] packet, boolean repeatToSource) {
        synchronized (characters) {
            for (MapleCharacter chr : characters) {
                if (chr != source && !chr.isSuperGM()) {
                    chr.getClient().sendPacket(packet);
                }
            }
        }
    }

    public boolean getBlockedMap() {
        for (int i : blockedmaps) {
            if (mapid == i) {
                return true;
            }
        }
        return false;
    }

    public void blockMap(int map) {
        //if (!blockedmaps.contains(map)) {
        blockedmaps.add(map);
        //}
    }

    public void unblockMap(int map) {
        //if (blockedmaps.contains(map)) {
        List<Integer> stored_bm = blockedmaps;
        for (int i : stored_bm) {
            blockedmaps.clear(); // store the list as i, clear, then re-add unless == map
            if (i != map) {
                blockedmaps.add(i); // add i of list != to map. This is because we're unable to list.remove properly.
            }
        }
        //}
    }

    public boolean pvpEnabled() { // checks if map has pvp enabled
        for (int i : pvpmaps) {
            if (mapid == i) {
                return true;
            }
        }
        return false;
    }

    public void addPvPMap(int map) {
        pvpmaps.add(map);
    }

    public void removePvPMap(int map) {
        List<Integer> stored_bm = pvpmaps;
        for (int i : stored_bm) {
            pvpmaps.clear(); // store the list as i, clear, then re-add unless == map
            if (i != map) {
                pvpmaps.add(i); // add i of list != to map. This is because we're unable to list.remove properly.
            }
        }
    }

    public void addPvpPlayer(MapleCharacter chr) {
        spawnPvpTarget(chr);
    }

    public void removePvpPlayer(MapleCharacter chr) {
        MapleMonster target = pvp_Players.get(chr);
        target.setHp(0);
        chr.getMap().broadcastMessage(MobPacket.killMonster(target.getObjectId(), 1));
        chr.getMap().removeMapObject(target);
        target.killed();
        pvp_Players.remove(chr);
    }

    public MapleMonster spawnPvpTarget(MapleCharacter chr) {
        //TODO : fixit
        MapleMonster target = null;//new MapleMonster(WizerDual.pvp_TargetId, MapleLifeFactory.getMonster(5120504).getStats());
        target.getStats().setHPDisplayType((byte) -1);
        target.setOverrideStats(new OverrideMonsterStats(Long.MAX_VALUE, target.getMobMaxMp(), target.getMobExp(), false));
        target.getChangedStats().level = 1;
        target.getChangedStats().watk = 0;
        target.getChangedStats().matk = 0;
        target.getChangedStats().acc = 0;
        target.getChangedStats().pushed = Integer.MAX_VALUE;
        target.setController(null);
        target.setMap(chr.getMap());
        target.setPosition(chr.getPosition());
        chr.getMap().spawnMonster(target, 20);
        chr.getClient().sendPacket(MobPacket.killMonster(target.getObjectId(), (byte) 1));
        pvp_Players.put(chr, target);
        return target;
    }

    public MapleMonster getMobFromChr(MapleCharacter chr) {
        if (pvp_Players == null) {
            return null;
        }
        return pvp_Players.get(chr);
    }

    public MapleCharacter getChrFromMob(MapleMonster mob) {
        if (pvp_Players == null) {
            return null;
        }
        for (Map.Entry<MapleCharacter, MapleMonster> chrMobPair : pvp_Players.entrySet()) {
            if (chrMobPair.getValue() == mob) {
                return chrMobPair.getKey();
            }
        }
        return null;
    }

    public Collection<MapleCharacter> getNearestPvpChar(Point attacker, double maxRange, double maxHeight, Collection<MapleCharacter> chr) {
        Collection<MapleCharacter> character = new LinkedList<MapleCharacter>();
        for (MapleCharacter a : characters) {
            if (chr.contains(a.getClient().getPlayer())) {
                Point attackedPlayer = a.getPosition();
                MaplePortal Port = a.getMap().findClosestSpawnpoint(a.getPosition());
                Point nearestPort = Port.getPosition();
                double safeDis = attackedPlayer.distance(nearestPort);
                double distanceX = attacker.distance(attackedPlayer.getX(), attackedPlayer.getY());
//                if (WizerDual.isLeft) {
//                    if (attacker.x > attackedPlayer.x && distanceX < maxRange && distanceX > 2 &&
//                            attackedPlayer.y >= attacker.y - maxHeight && attackedPlayer.y <= attacker.y + maxHeight && safeDis > 2) {
//                        character.add(a);
//                    }
//                }
//                if (WizerDual.isRight) {
//                    if (attacker.x < attackedPlayer.x && distanceX < maxRange && distanceX > 2 &&
//                            attackedPlayer.y >= attacker.y - maxHeight && attackedPlayer.y <= attacker.y + maxHeight && safeDis > 2) {
//                        character.add(a);
//                    }
//                }
            }
        }
        return character;
    }

    public final MapleCharacter getChangeableMobOrigin() {
        if (changeMobOrigin == null) {
            return null;
        }
        return changeMobOrigin.get();
    }

    public final void setChangeableMobOrigin(MapleCharacter d) {
        this.changeMobOrigin = new WeakReference<MapleCharacter>(d);
    }

    /*public boolean getAntiKSAllowed(int map) {
        for (Object i : antiks_bm) {
            final String mapId = "" + map + "";
            if (mapId == i.toString()) {
                System.out.println(mapId + " has been blocked! WOOP.");
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    
    public void toggleAntiKSAllowed(int map) {
        antiks_bm.add("" + map + "");
        // antiks_bm.add(antiks_bm.toString().length() + 1, "" + map + "");
        System.out.println("Array length: " + antiks_bm.toString().length() + " | Map: " + map + " | ");
        if (AntiKS_Allowed[map] == false) {
            AntiKS_Allowed[map] = true;
            //disabled_map = map;
        } else if (AntiKS_Allowed[map] == true) {
            AntiKS_Allowed[map] = false;
            //disabled_map = -1;
        } else {
            System.out.println("AntiKS on map " + mapid + " (using function map: " + map + ") is undefined {{" + AntiKS_Allowed + "}}");
        }
    }*/
    public void respawn(final boolean force) {
        respawn(force, System.currentTimeMillis());
    }

    public void respawn(final boolean force, final long now) {
        if (spawnsDisabled) {
            return;
        }
        lastSpawnTime = now;
        if (force) { //cpq quick hack
            final int numShouldSpawn = monsterSpawn.size() - spawnedMonstersOnMap.get();

            if (numShouldSpawn > 0) {
                int spawned = 0;

                for (Spawns spawnPoint : monsterSpawn) {
                    spawnPoint.spawnMonster(this);
                    spawned++;
                    if (spawned >= numShouldSpawn) {
                        break;
                    }
                }
            }
        } else {
            List<MapleMonster> ms = getAllMonstersThreadsafe();
            boolean isFast = false;
            for(MapleMonster mob : ms){
                if (mob.getId() == 9700100)
                    isFast = true;
            }

            final int numShouldSpawn;

            if (isFast)
                numShouldSpawn  = (maxRegularSpawn * 3 > 50?maxRegularSpawn * 3: 50) - spawnedMonstersOnMap.get();
            else
                numShouldSpawn  = maxRegularSpawn + 8 - spawnedMonstersOnMap.get();

            if (numShouldSpawn > 0) {
                int spawned = 0;

                final List<Spawns> randomSpawn = new ArrayList<>(monsterSpawn);
                Collections.shuffle(randomSpawn);

                for (Spawns spawnPoint : randomSpawn) {
                    if (!isSpawns && spawnPoint.getMobTime() > 0) {
                        continue;
                    }
                    if (spawnPoint.shouldSpawn(lastSpawnTime) || GameConstants.isForceRespawn(mapid) || (monsterSpawn.size() < 10 && maxRegularSpawn > monsterSpawn.size() && partyBonusRate > 0)) {
                        spawnPoint.spawnMonster(this);
                        spawned++;
                    }
                    if (spawned >= numShouldSpawn) {
                        break;
                    }
                }
            }
        }
    }

    public String getSnowballPortal() {
        int[] teamss = new int[2];
        charactersLock.readLock().lock();
        try {
            for (MapleCharacter chr : characters) {
                if (chr.getTruePosition().y > -80) {
                    teamss[0]++;
                } else {
                    teamss[1]++;
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        if (teamss[0] > teamss[1]) {
            return "st01";
        } else {
            return "st00";
        }
    }

    public boolean isDisconnected(int id) {
        return dced.contains(Integer.valueOf(id));
    }

    public void addDisconnected(int id) {
        dced.add(Integer.valueOf(id));
    }

    public void resetDisconnected() {
        dced.clear();
    }

    public void startSpeedRun() {
        final MapleSquad squadfd = getSquadByMap();
        if (squadfd != null) {
            charactersLock.readLock().lock();
            try {
                for (MapleCharacter chr : characters) {
                    if (chr.getName().equals(squadfd.getLeaderName()) && !chr.isIntern()) {
                        startSpeedRun(chr.getName());
                        return;
                    }
                }
            } finally {
                charactersLock.readLock().unlock();
            }
        }
    }

    public void startSpeedRun(String leader) {
        speedRunStart = System.currentTimeMillis();
        speedRunLeader = leader;
    }

    public void endSpeedRun() {
        speedRunStart = 0;
        speedRunLeader = "";
    }

    public void getRankAndAdd(String leader, String time, ExpeditionType type, long timz, Collection<String> squad) {
        try {
            long lastTime = SpeedRunner.getSpeedRunData(type) == null ? 0 : SpeedRunner.getSpeedRunData(type).right;
            //if(timz > lastTime && lastTime > 0) {
            //return;
            //}
            //Pair<String, Map<Integer, String>>
            StringBuilder rett = new StringBuilder();
            if (squad != null) {
                for (String chr : squad) {
                    rett.append(chr);
                    rett.append(",");
                }
            }
            String z = rett.toString();
            if (squad != null) {
                z = z.substring(0, z.length() - 1);
            }
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("INSERT INTO speedruns(`type`, `leader`, `timestring`, `time`, `members`) VALUES (?,?,?,?,?)")) {
                ps.setString(1, type.name());
                ps.setString(2, leader);
                ps.setString(3, time);
                ps.setLong(4, timz);
                ps.setString(5, z);
                ps.executeUpdate();
            }

            if (lastTime == 0) { //great, we just add it
                SpeedRunner.addSpeedRunData(type, SpeedRunner.addSpeedRunData(new StringBuilder(SpeedRunner.getPreamble(type)), new HashMap<Integer, String>(), z, leader, 1, time), timz);
            } else {
                //i wish we had a way to get the rank
                //TODO revamp
                SpeedRunner.removeSpeedRunData(type);
                SpeedRunner.loadSpeedRunData(type);
            }
        } catch (Exception e) {
        }
    }

    public long getSpeedRunStart() {
        return speedRunStart;
    }

    public final void disconnectAll() {
        for (MapleCharacter chr : getCharactersThreadsafe()) {
            if (!chr.isGM()) {
                chr.getClient().disconnect(true, false);
                chr.getClient().getSession().close();
            }
        }
    }

    public List<MapleNPC> getAllNPCs() {
        return getAllNPCsThreadsafe();
    }

    public List<MapleNPC> getAllNPCsThreadsafe() {
        ArrayList<MapleNPC> ret = new ArrayList<>();
        mapobjectlocks.get(MapleMapObjectType.NPC).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.NPC).values()) {
                ret.add((MapleNPC) mmo);
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.NPC).readLock().unlock();
        }
        return ret;
    }

    public final void resetNPCs() {
        removeNpc(-1);
    }

    public final void resetPQ(int level) {
        resetFully();
        for (MapleMonster mons : getAllMonstersThreadsafe()) {
            mons.changeLevel(level, true);
        }
        resetSpawnLevel(level);
    }

    public final void resetAriantPQ(int level) {
        killAllMonsters(true);
        reloadReactors();
        removeDrops();
        resetNPCs();
        resetSpawns();
        resetDisconnected();
        endSpeedRun();
        cancelSquadSchedule(true);
        resetPortals();
        environment.clear();
        respawn(true);
        for (MapleMonster mons : getAllMonstersThreadsafe()) {
            mons.changeLevel(level, true);
        }
        resetSpawnLevel(level);
    }

    public final void resetSpawnLevel(int level) {
        for (Spawns spawn : monsterSpawn) {
            if (spawn instanceof SpawnPoint) {
                ((SpawnPoint) spawn).setLevel(level);
            }
        }
    }

    public final void resetFully() {
        resetFully(true);
    }

    public final void resetFully(final boolean respawn) {
        killAllMonsters(false);
        reloadReactors();
        removeDrops();
        resetNPCs();
        resetSpawns();
        resetDisconnected();
        endSpeedRun();
        cancelSquadSchedule(true);
        resetPortals();
        environment.clear();
        if (respawn) {
            respawn(true);
        }
    }

    public final void cancelSquadSchedule(boolean interrupt) {
        squadTimer = false;
        checkStates = true;
        if (squadSchedule != null) {
            squadSchedule.cancel(interrupt);
            squadSchedule = null;
        }
    }

    public final void removeDrops() {
        List<MapleMapItem> items = this.getAllItemsThreadsafe();
        for (MapleMapItem i : items) {
            i.expire(this);
        }
    }

    public final void resetAllSpawnPoint(int mobid, int mobTime) {
        Collection<Spawns> sss = new LinkedList<>(monsterSpawn);
        resetFully();
        monsterSpawn.clear();
        for (Spawns s : sss) {
            MapleMonster newMons = MapleLifeFactory.getMonster(mobid);
            newMons.setF(s.getF());
            newMons.setFh(s.getFh());
            newMons.setPosition(s.getPosition());
            addMonsterSpawn(newMons, mobTime, (byte) -1, null);
        }
        loadMonsterRate(true);
    }

    public final void resetSpawns() {
        boolean changed = false;
        Iterator<Spawns> sss = monsterSpawn.iterator();
        while (sss.hasNext()) {
            if (sss.next().getCarnivalId() > -1) {
                sss.remove();
                changed = true;
            }
        }
        setSpawns(true);
        if (changed) {
            loadMonsterRate(true);
        }
    }

    public final boolean makeCarnivalSpawn(final int team, final MapleMonster newMons, final int num) {
        MonsterPoint ret = null;
        for (MonsterPoint mp : nodes.getMonsterPoints()) {
            if (mp.team == team || mp.team == -1) {
                final Point newpos = calcPointBelow(new Point(mp.x, mp.y));
                newpos.y -= 1;
                boolean found = false;
                for (Spawns s : monsterSpawn) {
                    if (s.getCarnivalId() > -1 && (mp.team == -1 || s.getCarnivalTeam() == mp.team) && s.getPosition().x == newpos.x && s.getPosition().y == newpos.y) {
                        found = true;
                        break; //this point has already been used.
                    }
                }
                if (!found) {
                    ret = mp; //this point is safe for use.
                    break;
                }
            }
        }
        if (ret != null) {
            newMons.setCy(ret.cy);
            newMons.setF(0); //always.
            newMons.setFh(ret.fh);
            newMons.setRx0(ret.x + 50);
            newMons.setRx1(ret.x - 50); //does this matter
            newMons.setPosition(new Point(ret.x, ret.y));
            newMons.setHide(false);
            final SpawnPoint sp = addMonsterSpawn(newMons, 1, (byte) team, null);
            sp.setCarnival(num);
        }
        return ret != null;
    }

    public final boolean makeCarnivalReactor(final int team, final int num) {
        final MapleReactor old = getReactorByName(team + "" + num);
        if (old != null && old.getState() < 5) { //already exists
            return false;
        }
        Point guardz = null;
        final List<MapleReactor> react = getAllReactorsThreadsafe();
        for (Pair<Point, Integer> guard : nodes.getGuardians()) {
            if (guard.right == team || guard.right == -1) {
                boolean found = false;
                for (MapleReactor r : react) {
                    if (r.getTruePosition().x == guard.left.x && r.getTruePosition().y == guard.left.y && r.getState() < 5) {
                        found = true;
                        break; //already used
                    }
                }
                if (!found) {
                    guardz = guard.left; //this point is safe for use.
                    break;
                }
            }
        }
        if (guardz != null) {
            final MapleReactor my = new MapleReactor(MapleReactorFactory.getReactor(9980000 + team), 9980000 + team);
            my.setState((byte) 1);
            my.setName(team + "" + num); //lol
            //with num. -> guardians in factory
            spawnReactorOnGroundBelow(my, guardz);
            final MCSkill skil = MapleCarnivalFactory.getInstance().getGuardian(num);
            for (MapleMonster mons : getAllMonstersThreadsafe()) {
                if (mons.getCarnivalTeam() == team) {
                    skil.getSkill().applyEffect(null, mons, false);
                }
            }
        }
        return guardz != null;
    }

    public final void blockAllPortal() {
        for (MaplePortal p : portals.values()) {
            p.setPortalState(false);
        }
    }

    public boolean getAndSwitchTeam() {
        return getCharactersSize() % 2 != 0;
    }

    public void setSquad(MapleSquadType s) {
        this.squad = s;

    }

    public int getChannel() {
        return channel;
    }

    public int getConsumeItemCoolTime() {
        return consumeItemCoolTime;
    }

    public void setConsumeItemCoolTime(int ciit) {
        this.consumeItemCoolTime = ciit;
    }

    public int getPermanentWeather() {
        return permanentWeather;
    }

    public void setPermanentWeather(int pw) {
        this.permanentWeather = pw;
    }

    public void checkStates(final String chr) {
        if (!checkStates) {
            return;
        }
        final MapleSquad sqd = getSquadByMap();
        final EventManager em = getEMByMap();
        final int size = getCharactersSize();
        if (sqd != null && sqd.getStatus() == 2) {
            sqd.removeMember(chr);
            if (em != null) {
                if (sqd.getLeaderName().equalsIgnoreCase(chr)) {
                    em.setProperty("leader", "false");
                }
                if (chr.equals("") || size == 0) {
                    em.setProperty("state", "0");
                    em.setProperty("leader", "true");
                    cancelSquadSchedule(!chr.equals(""));
                    sqd.clear();
                    sqd.copy();
                }
            }
        }
        if (em != null && em.getProperty("state") != null && (sqd == null || sqd.getStatus() == 2) && size == 0) {
            em.setProperty("state", "0");
            if (em.getProperty("leader") != null) {
                em.setProperty("leader", "true");
            }
        }
        if (speedRunStart > 0 && size == 0) {
            endSpeedRun();
        }
        //if (squad != null) {
        //    final MapleSquad sqdd = ChannelServer.getInstance(channel).getMapleSquad(squad);
        //    if (sqdd != null && chr != null && chr.length() > 0 && sqdd.getAllNextPlayer().contains(chr)) {
        //	sqdd.getAllNextPlayer().remove(chr);
        //	broadcastMessage(CWvsContext.broadcastMsg(5, "The queued player " + chr + " has left the map."));
        //    }
        //}
    }

    public void setCheckStates(boolean b) {
        this.checkStates = b;
    }

    public final List<MaplePlatform> getPlatforms() {
        return nodes.getPlatforms();
    }

    public Collection<MapleNodeInfo> getNodes() {
        return nodes.getNodes();
    }

    public void setNodes(final MapleNodes mn) {
        this.nodes = mn;
    }

    public MapleNodeInfo getNode(final int index) {
        return nodes.getNode(index);
    }

    public boolean isLastNode(final int index) {
        return nodes.isLastNode(index);
    }

    public final List<Rectangle> getAreas() {
        return nodes.getAreas();
    }

    public final Rectangle getArea(final int index) {
        return nodes.getArea(index);
    }

    public final void changeEnvironment(final String ms, final int type) {
        broadcastMessage(CField.environmentChange(ms, type));
    }

    public final void toggleEnvironment(final String ms) {
        if (environment.containsKey(ms)) {
            moveEnvironment(ms, environment.get(ms) == 1 ? 2 : 1);
        } else {
            moveEnvironment(ms, 1);
        }
    }

    public final void moveEnvironment(final String ms, final int type) {
        broadcastMessage(CField.environmentMove(ms, type));
        environment.put(ms, type);
    }

    public final Map<String, Integer> getEnvironment() {
        return environment;
    }

    public final int getNumPlayersInArea(final int index) {
        return getNumPlayersInRect(getArea(index));
    }

    public final int getNumPlayersInRect(final Rectangle rect) {
        int ret = 0;

        charactersLock.readLock().lock();
        try {
            final Iterator<MapleCharacter> ltr = characters.iterator();
            MapleCharacter a;
            while (ltr.hasNext()) {
                if (rect.contains(ltr.next().getTruePosition())) {
                    ret++;
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
        return ret;
    }

    public final int getNumPlayersItemsInArea(final int index) {
        return getNumPlayersItemsInRect(getArea(index));
    }

    public final int getNumPlayersItemsInRect(final Rectangle rect) {
        int ret = getNumPlayersInRect(rect);

        mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().lock();
        try {
            for (MapleMapObject mmo : mapobjects.get(MapleMapObjectType.ITEM).values()) {
                if (rect.contains(mmo.getTruePosition())) {
                    ret++;
                }
            }
        } finally {
            mapobjectlocks.get(MapleMapObjectType.ITEM).readLock().unlock();
        }
        return ret;
    }

    public void broadcastNONGMMessage(MapleCharacter source, byte[] packet, boolean repeatToSource) {
        broadcastNONGMMessage(repeatToSource ? null : source, packet);
    }

    private void broadcastNONGMMessage(MapleCharacter source, byte[] packet) {
        charactersLock.readLock().lock();
        if (source.isFiction() && source.isMegaHidden()) {
            try {
                for (MapleCharacter chr : characters) {
                    if (chr != source && !chr.isFiction()) {
                        chr.getClient().sendPacket(packet);
                    }
                }
            } finally {
                charactersLock.readLock().unlock();
            }

        } else {
            try {
                for (MapleCharacter chr : characters) {
                    if (chr != source && !chr.isGM()) {
                        chr.getClient().sendPacket(packet);
                    }
                }
            } finally {
                charactersLock.readLock().unlock();
            }
        }
    }

    public void broadcastGMMessage(MapleCharacter source, byte[] packet, boolean repeatToSource) {
        broadcastGMMessage(repeatToSource ? null : source, packet);
    }

    private void broadcastGMMessage(MapleCharacter source, byte[] packet) {
        charactersLock.readLock().lock();
        try {
            if (source == null) {
                for (MapleCharacter chr : characters) {
                    if (chr.isFiction() && chr.isMegaHidden()) {
                        if (chr.isFiction()) {
                            chr.getClient().sendPacket(packet);
                        }
                    } else {
                        if (chr.isStaff()) {
                            chr.getClient().sendPacket(packet);
                        }
                    }
                }
            } else {
                for (MapleCharacter chr : characters) {
                    if (source.isFiction() && source.isMegaHidden()) {
                        if (chr != source && chr.isFiction()) {
                            chr.getClient().sendPacket(packet);
                        }
                    } else {
                        if (chr != source && chr.isGM()) {
                            chr.getClient().sendPacket(packet);
                        }
                    }
                }
            }
        } finally {
            charactersLock.readLock().unlock();
        }
    }

    public final List<Pair<Integer, Integer>> getMobsToSpawn() {
        return nodes.getMobsToSpawn();
    }

    public final List<Integer> getSkillIds() {
        return nodes.getSkillIds();
    }
    //todo: 這邊修改召喚時間
    public final boolean canSpawn(long now) {
        List<MapleMonster> ms = getAllMonstersThreadsafe();
        boolean isFast = false;
        for(MapleMonster mob : ms){
            if (mob.getId() == 9700100)
                isFast = true;
        }

        if (isFast) {
            return lastSpawnTime > 0 && isSpawns && lastSpawnTime + 3000 < now;
        }else{
            return lastSpawnTime > 0 && isSpawns && lastSpawnTime + createMobInterval < now;
        }
    }

    public final boolean canHurt(long now) {
        if (lastHurtTime > 0 && lastHurtTime + decHPInterval < now) {
            lastHurtTime = now;
            return true;
        }
        return false;
    }

    public final void resetShammos(final MapleClient c) {
        killAllMonsters(true);
        broadcastMessage(CWvsContext.broadcastMsg(5, "A player has moved too far from Shammos. Shammos is going back to the start."));
        EtcTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (c.getPlayer() != null) {
                    c.getPlayer().changeMap(MapleMap.this, getPortal(0));
                    if (getCharactersThreadsafe().size() > 1) {
                        MapScriptMethods.startScript_FirstUser(c, "shammos_Fenter");
                    }
                }
            }
        }, 500); //avoid dl
    }

    public int getInstanceId() {
        return instanceid;
    }

    public void setInstanceId(int ii) {
        this.instanceid = ii;
    }

    public int getPartyBonusRate() {
        return partyBonusRate;
    }

    public void setPartyBonusRate(int ii) {
        this.partyBonusRate = ii;
    }

    public short getTop() {
        return top;
    }

    public void setTop(int ii) {
        this.top = (short) ii;
    }

    public short getBottom() {
        return bottom;
    }

    public void setBottom(int ii) {
        this.bottom = (short) ii;
    }

    public short getLeft() {
        return left;
    }

    public void setLeft(int ii) {
        this.left = (short) ii;
    }

    public short getRight() {
        return right;
    }

    public void setRight(int ii) {
        this.right = (short) ii;
    }

    public List<Pair<Point, Integer>> getGuardians() {
        return nodes.getGuardians();
    }

    public DirectionInfo getDirectionInfo(int i) {
        return nodes.getDirection(i);
    }

    private static interface DelayedPacketCreation {

        void sendPackets(MapleClient c);
    }

    private class ActivateItemReactor implements Runnable {

        private MapleMapItem mapitem;
        private MapleReactor reactor;
        private MapleClient c;

        public ActivateItemReactor(MapleMapItem mapitem, MapleReactor reactor, MapleClient c) {
            this.mapitem = mapitem;
            this.reactor = reactor;
            this.c = c;
        }

        @Override
        public void run() {
            if (mapitem != null && mapitem == getMapObject(mapitem.getObjectId(), mapitem.getType()) && !mapitem.isPickedUp()) {
                mapitem.expire(MapleMap.this);
                reactor.hitReactor(c);
                reactor.setTimerActive(false);

                if (reactor.getDelay() > 0) {
                    MapTimer.getInstance().schedule(new Runnable() {

                        @Override
                        public void run() {
                            reactor.forceHitReactor((byte) 0);
                        }
                    }, reactor.getDelay());
                }
            } else {
                reactor.setTimerActive(false);
            }
        }
    }

    @Override
    public String toString() {
        return "'" + getStreetName() + " : " + getMapName() + "'(" + getId() + ")";
    }

    public boolean isMarketMap() {
        return (this.mapid >= 910000000) && (this.mapid <= 910000017);
    }

    public boolean isBossMap() {
        return GameConstants.isBossMap(mapid);
    }
}
