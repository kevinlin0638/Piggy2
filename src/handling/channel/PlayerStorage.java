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
import client.MapleCharacterUtil;
import handling.world.CharacterTransfer;
import handling.world.World;
import server.Timer.PingTimer;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PlayerStorage {

    private final ReentrantReadWriteLock locks = new ReentrantReadWriteLock();
    private final Lock wL = locks.writeLock(); // Pending Players (CS/MTS)
    private final Lock rlock = locks.readLock();
    private final Lock wlock = locks.writeLock();
    private final Map<Integer, MapleCharacter> storage = new LinkedHashMap<>();
    private final Map<String, MapleCharacter> nameToChar = new HashMap<>();
    private final Map<Integer, CharacterTransfer> pendingChars = new HashMap<>();


    public PlayerStorage() {
        PingTimer.getInstance().register(new PersistingTask(), 60000);
    }

    public void addPlayer(MapleCharacter chr) {
        wlock.lock();
        try {
            World.Find.register(chr.getId(), chr.getName(), chr.getWorld(), chr.getClient().getChannel());
            nameToChar.put(chr.getName(), chr);
            storage.put(chr.getId(), chr);
        } finally {
            wlock.unlock();
        }
    }

    public MapleCharacter removePlayer(int chr) {
        wlock.lock();
        try {
            String naam = getCharacterById(chr).getName();
            World.Find.forceDeregister(chr, naam);
            nameToChar.remove(naam);
            return storage.remove(chr);
        } finally {
            wlock.unlock();
        }
    }

    public final void deregisterPlayer(final MapleCharacter chr) {
        wL.lock();
        try {
            nameToChar.remove(chr.getName().toLowerCase());
            storage.remove(chr.getId());
        } finally {
            wL.unlock();
        }
        World.Find.forceDeregister(chr.getId(), chr.getName());
    }

    public MapleCharacter getCharacterByName(String name) {
        rlock.lock();
        try {
            for (MapleCharacter chr : storage.values()) {
                if (chr.getName().toLowerCase().equals(name.toLowerCase()))
                    return chr;
            }
            return null;
        } finally {
            rlock.unlock();
        }
    }

    public MapleCharacter getCharacterById(int id) {
        rlock.lock();
        try {
            return storage.get(id);
        } finally {
            rlock.unlock();
        }
    }

    public Collection<MapleCharacter> getAllCharacters() {
        rlock.lock();
        try {
            return storage.values();
        } finally {
            rlock.unlock();
        }
    }

    public final void registerPendingPlayer(final CharacterTransfer chr, final int playerid) {
        wL.lock();
        try {
            pendingChars.put(playerid, chr);
        } finally {
            wL.unlock();
        }
    }

    public final int pendingCharacterSize() {
        return pendingChars.size();
    }

    public final void deregisterPendingPlayer(final int charid) {
        wL.lock();
        try {
            pendingChars.remove(charid);
        } finally {
            wL.unlock();
        }
    }

    public final CharacterTransfer getPendingCharacter(final int charid) {
        wL.lock();
        try {
            return pendingChars.remove(charid);
        } finally {
            wL.unlock();
        }
    }

    public final int getConnectedClients() {
        return storage.size();
    }

    public final void disconnectAll() {
        disconnectAll(false);
    }

    public final void disconnectAll(final boolean checkGM) {
        wL.lock();
        try {
            final Iterator<MapleCharacter> itr = nameToChar.values().iterator();
            MapleCharacter chr;
            while (itr.hasNext()) {
                chr = itr.next();
                if (!chr.isGM() || !checkGM) {
                    chr.getClient().disconnect(false, false, true);
                    chr.getClient().getSession().close();
                    World.Find.forceDeregister(chr.getId(), chr.getName());
                    itr.remove();
                }
            }
        } finally {
            wL.unlock();
        }
    }

    public final String getOnlinePlayers(final boolean byGM) {
        final StringBuilder sb = new StringBuilder();
        if (byGM) {
            rlock.lock();
            try {
                final Iterator<MapleCharacter> itr = nameToChar.values().iterator();
                while (itr.hasNext()) {
                    sb.append(MapleCharacterUtil.makeMapleReadable(itr.next().getName()));
                    sb.append(", ");
                }
            } finally {
                rlock.unlock();
            }
        } else {
            rlock.lock();
            try {
                final Iterator<MapleCharacter> itr = nameToChar.values().iterator();
                MapleCharacter chr;
                while (itr.hasNext()) {
                    chr = itr.next();
                    if (!chr.isGM()) {
                        sb.append(MapleCharacterUtil.makeMapleReadable(chr.getName()));
                        sb.append(", ");
                    }
                }
            } finally {
                rlock.unlock();
            }
        }
        return sb.toString();
    }

    public class PersistingTask implements Runnable {
        @Override
        public void run() {
            wlock.lock();
            try {
                final long currenttime = System.currentTimeMillis();
                // 40 sec
                pendingChars.entrySet().removeIf(integerCharacterTransferEntry -> currenttime - integerCharacterTransferEntry.getValue().TranferTime > 40000);
            } finally {
                wlock.unlock();
            }
        }
    }
}
