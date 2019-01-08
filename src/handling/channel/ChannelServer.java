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
package handling.channel;

import client.MapleCharacter;
import constants.ServerConstants;
import constants.WorldConstants;
import handling.login.LoginServer;
import handling.netty.ServerConnection;
import handling.world.World;
import java.io.FileInputStream;
import java.io.IOException;
import scripting.EventScriptManager;
import server.MapleSquad;
import server.MapleSquad.MapleSquadType;
import server.worldevents.*;
import server.life.PlayerNPC;
import server.maps.AramiaFireWorks;
import server.maps.MapleMapFactory;
import server.maps.MapleMapObject;
import server.shops.HiredMerchant;
import tools.packet.CWvsContext;
import tools.types.ConcurrentEnumMap;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ChannelServer {

    private final MapleMapFactory mapFactory;
    private final Map<MapleSquadType, MapleSquad> mapleSquads = new ConcurrentEnumMap<>(MapleSquadType.class);
    private final Map<Integer, HiredMerchant> merchants = new HashMap<>();
    private final List<PlayerNPC> playerNPCs = new LinkedList<>();
    private final ReentrantReadWriteLock merchLock = new ReentrantReadWriteLock(); //merchant
    private final Map<MapleEventType, MapleEvent> events = new EnumMap<>(MapleEventType.class);
    public int eventMap = 0;
    private int port = 7575;
    private int world, channel, running_MerchantID = 0;
    private String serverMessage, ip;
    private EventScriptManager eventSM;
    private boolean shutdown = false, finishedShutdown = false, MegaphoneMuteState = false;
    private PlayerStorage players = new PlayerStorage();
    private ServerConnection acceptor;
    private AramiaFireWorks works = new AramiaFireWorks();
    private String ShopPack;

    private ChannelServer(final int world, final int channel) {
        this.world = world;
        this.channel = channel;
        mapFactory = new MapleMapFactory(world, channel);
    }

    public static Set<ChannelServer> getAllInstance(int world) {
        return new HashSet<>(LoginServer.getWorld(world).getChannels());
    }

    public static ChannelServer newInstance(final int world, final int channel) {
        return new ChannelServer(world, channel);
    }

    public static ChannelServer getInstance(int world, int channel) {
        return LoginServer.getInstance().getChannel(world, channel);
    }

    public static int getChannelCount(int world) { // needs to be fixed for multi-world
        return LoginServer.getWorld(world).getChannels().size();
    }

    public static Map<Integer, Integer> getChannelLoad(int world) { // needs to be fixed for multi-world
        Map<Integer, Integer> ret = new HashMap<>();
        for (ChannelServer cs : LoginServer.getWorld(world).getChannels()) {
            ret.put(cs.getChannel(), cs.getConnectedClients());
        }
        return ret;
    }

    public final void init() {
        serverMessage = ServerConstants.SERVER_MESSAGE;
        eventSM = new EventScriptManager(this, WorldConstants.Events.split(","));
        port = 7575 + this.channel - 1;
        port += (world * 100);
        ip = ServerConstants.SERVER_IP + ":" + port;
        acceptor = new ServerConnection(port, world, channel);
        loadEvents();
        try {
            acceptor.run();
            System.out.printf("[World:%d Channel:%d] is Bind on port %d\n", world, channel, port);
            eventSM.init();
        } catch (Exception e) {
            System.err.printf("Binding to port %d failed (world: %d ch: %d)\n", port, world, channel);
            e.printStackTrace();
            acceptor.close();
        }
    }

    public final void shutdown() {
        if (finishedShutdown) {
            return;
        }
        broadcastPacket(CWvsContext.broadcastMsg(0, "This channel will now shut down."));
        shutdown = true;
        System.out.println("Channel " + channel + ", Saving characters...");
        players.disconnectAll();
        System.out.println("Channel " + channel + ", Unbinding...");
        acceptor.close();
        acceptor = null;
        LoginServer.getWorld(world).removeChannel(this.channel);
        setFinishShutdown();
    }

    public final void unbind() {
        acceptor.close();
    }

    public final boolean hasFinishedShutdown() {
        return finishedShutdown;
    }

    public final MapleMapFactory getMapFactory() {
        return mapFactory;
    }

    public PlayerStorage getPlayerStorage() {
        return players;
    }

    public void addPlayer(MapleCharacter chr) {
        players.addPlayer(chr);
        chr.announce(CWvsContext.broadcastMessage(serverMessage));
    }

    public final void removePlayer(final MapleCharacter chr) {
        players.removePlayer(chr.getId());
    }

    public final String getServerMessage() {
        return serverMessage;
    }

    public final void setServerMessage(final String newMessage) {
        serverMessage = newMessage;
        broadcastPacket(CWvsContext.broadcastMessage(serverMessage));
    }

    public final void broadcastPacket(final byte[] data) {
        for (MapleCharacter chr : players.getAllCharacters()) {
            chr.announce(data);
        }
    }

    public EventScriptManager getEventSM() {
        return eventSM;
    }

    public final void reloadEvents() {
        eventSM.cancel();
        eventSM = new EventScriptManager(this, WorldConstants.Events.split(","));
        eventSM.init();
    }

    public final void broadcastSmegaPacket(final byte[] data) {
        for (MapleCharacter chr : players.getAllCharacters()) {
            if (chr.getSmega()) {
                chr.announce(data);
            }
        }
    }

    public final void broadcastGMPacket(final byte[] data) {
        for (MapleCharacter chr : players.getAllCharacters()) {
            if (chr.isGM()) {
                chr.announce(data);
            }
        }
    }

    public final int getChannel() {
        return channel;
    }

    public final int getWorld() {
        return world;
    }

    public final String getIP() {
        return ip;
    }

    public final boolean isShutdown() {
        return shutdown;
    }

    public final int getLoadedMaps() {
        for (World worlds : LoginServer.getWorlds()) { // forloop worlds
            for (ChannelServer cs : worlds.getChannels()) {// forloop channels
                return cs.getMapFactory().getLoadedMaps();
            }
        }
        return mapFactory.getLoadedMaps();
    }

    public final void loadEvents() {
        if (!events.isEmpty()) {
            return;
        }
        events.put(MapleEventType.CokePlay, new MapleCoconut(world, channel, MapleEventType.CokePlay));
        events.put(MapleEventType.Coconut, new MapleCoconut(world, channel, MapleEventType.Coconut));
        events.put(MapleEventType.Fitness, new MapleFitness(world, channel, MapleEventType.Fitness));
        events.put(MapleEventType.OlaOla, new MapleOla(world, channel, MapleEventType.OlaOla));
        events.put(MapleEventType.OxQuiz, new MapleOxQuiz(world, channel, MapleEventType.OxQuiz));
        events.put(MapleEventType.Snowball, new MapleSnowball(world, channel, MapleEventType.Snowball));
        events.put(MapleEventType.Survival, new MapleSurvival(world, channel, MapleEventType.Survival));
    }

    public Map<MapleSquadType, MapleSquad> getAllSquads() {
        return Collections.unmodifiableMap(mapleSquads);
    }

    public final MapleSquad getMapleSquad(final String type) {
        return getMapleSquad(MapleSquadType.valueOf(type.toLowerCase()));
    }

    public final MapleSquad getMapleSquad(final MapleSquadType type) {
        return mapleSquads.get(type);
    }

    public final boolean addMapleSquad(final MapleSquad squad, final String type) {
        final MapleSquadType types = MapleSquadType.valueOf(type.toLowerCase());
        if (types != null && !mapleSquads.containsKey(types)) {
            mapleSquads.put(types, squad);
            squad.scheduleRemoval();
            return true;
        }
        return false;
    }

    public void blueWorldMessage(String msg) {
        for (MapleCharacter chr : players.getAllCharacters()) {
            chr.dropMessage(6, msg);
        }
    }

    public void yellowWorldMessage(String msg) {
        for (MapleCharacter mc : getPlayerStorage().getAllCharacters()) {
            mc.getClient().sendPacket(CWvsContext.yellowChat(msg));
        }
    }

    public final boolean removeMapleSquad(final MapleSquadType types) {
        if (types != null && mapleSquads.containsKey(types)) {
            mapleSquads.remove(types);
            return true;
        }
        return false;
    }

    public void saveWorlds() {
        for (World worlds : LoginServer.getWorlds()) {
            for (MapleCharacter chr : worlds.getPlayerStorage().getAllCharacters()) {
                chr.saveToDB(false, false);
                if (!chr.Spam(3600000, 26)) { // every hour will notify yet still save.
                    chr.dropMessage(6, "自動存檔成功!");
                }
            }
        }
    }

    public boolean allowUndroppablesDrop() {
        return ServerConstants.dropUndroppables;
    }

    public boolean allowMoreThanOne() {
        return ServerConstants.moreThanOne;
    }

    public final int closeAllMerchant() {
        int ret = 0;
        merchLock.writeLock().lock();
        try {
            final Iterator<Entry<Integer, HiredMerchant>> merchants_ = merchants.entrySet().iterator();
            while (merchants_.hasNext()) {
                HiredMerchant hm = merchants_.next().getValue();
                hm.closeShop(true, false);
                hm.getMap().removeMapObject(hm);
                merchants_.remove();
                ret++;
            }
        } finally {
            merchLock.writeLock().unlock();
        }
        for (World worlds : LoginServer.getWorlds()) { // forloop every world
            for (ChannelServer channels : worlds.getChannels()) { // forloop every channel of every world
                for (int i = 910000001; i <= 910000022; i++) { // forloop every fm map of every channel of every world
                    for (MapleMapObject mmo : channels.getMapFactory().getMap(i).getAllHiredMerchantsThreadsafe()) { // get ALL merchants
                        ((HiredMerchant) mmo).closeShop(true, false);
                        ret++;
                    }
                }
            }
        }
        return ret;
    }

    public final int addMerchant(final HiredMerchant hMerchant) {
        merchLock.writeLock().lock();
        try {
            running_MerchantID++;
            merchants.put(running_MerchantID, hMerchant);
            return running_MerchantID;
        } finally {
            merchLock.writeLock().unlock();
        }
    }

    public final void removeMerchant(final HiredMerchant hMerchant) {
        merchLock.writeLock().lock();
        try {
            merchants.remove(hMerchant.getStoreId());
        } finally {
            merchLock.writeLock().unlock();
        }
    }

    public final boolean containsMerchant(final int accid, int cid) {
        boolean contains = false;
        merchLock.readLock().lock();
        try {
            final Iterator itr = merchants.values().iterator();
            while (itr.hasNext()) {
                HiredMerchant hm = (HiredMerchant) itr.next();
                if (hm.getOwnerAccId() == accid || hm.getOwnerId() == cid) {
                    contains = true;
                    break;
                }
            }
        } finally {
            merchLock.readLock().unlock();
        }
        return contains;
    }

    public final List<HiredMerchant> searchMerchant(final int itemSearch) {
        final List<HiredMerchant> list = new LinkedList<>();
        merchLock.readLock().lock();
        try {
            final Iterator itr = merchants.values().iterator();
            while (itr.hasNext()) {
                HiredMerchant hm = (HiredMerchant) itr.next();
                if (hm.searchItem(itemSearch).size() > 0) {
                    list.add(hm);
                }
            }
        } finally {
            merchLock.readLock().unlock();
        }
        return list;
    }

    public final void toggleMegaphoneMuteState() {
        this.MegaphoneMuteState = !this.MegaphoneMuteState;
    }

    public final boolean getMegaphoneMuteState() {
        return MegaphoneMuteState;
    }

    public int getEvent() {
        return eventMap;
    }

    public final void setEvent(final int ze) {
        this.eventMap = ze;
    }

    public MapleEvent getEvent(final MapleEventType t) {
        return events.get(t);
    }

    public final Collection<PlayerNPC> getAllPlayerNPC() {
        return playerNPCs;
    }

    public final void addPlayerNPC(final PlayerNPC npc) {
        if (playerNPCs.contains(npc)) {
            return;
        }
        playerNPCs.add(npc);
        for (World worlds : LoginServer.getWorlds()) { // forloop worlds
            for (ChannelServer cs : worlds.getChannels()) {// forloop channels
                cs.getMapFactory().getMap(npc.getMapId()).addMapObject(npc); // add to every world and channel
            }
        }
    }

    public final void removePlayerNPC(final PlayerNPC npc) {
        if (playerNPCs.contains(npc)) {
            playerNPCs.remove(npc);
            for (World worlds : LoginServer.getWorlds()) { // forloop worlds
                for (ChannelServer cs : worlds.getChannels()) {// forloop channels
                    cs.getMapFactory().getMap(npc.getMapId()).removeMapObject(npc); // add to every world and channel
                }
            }
        }
    }

    public final int getPort() {
        return port;
    }

    public final void setShutdown() {
        this.shutdown = true;
        System.out.println("Channel " + channel + " has set to shutdown and is closing Hired Merchants...");
    }

    public final void setFinishShutdown() {
        this.finishedShutdown = true;
        System.out.println("Channel " + channel + " has finished shutdown.");
    }

    public int getConnectedClients() {
        return players.getAllCharacters().size();
    }

    public void broadcastMessage(byte[] message) {
        broadcastPacket(message);
    }

    public void broadcastSmega(byte[] message) {
        broadcastSmegaPacket(message);
    }

    public void broadcastGMMessage(byte[] message) {
        broadcastGMPacket(message);
    }

    public AramiaFireWorks getFireWorks() {
        return works;
    }

    public String getShopPack() {
        if (this.ShopPack != null) {
            return this.ShopPack;
        }
        Properties props = new Properties();
        try {
            try (FileInputStream is = new FileInputStream("購物商城.txt")) {
                props.load(is);
            }
        } catch (IOException ex) {
            System.out.println("無法讀取 購物商城.txt 檔案資訊。");
        }
        this.ShopPack = props.getProperty("pack");
        return this.ShopPack;
    }
}
