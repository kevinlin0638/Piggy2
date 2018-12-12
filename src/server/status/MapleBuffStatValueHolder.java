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
package server.status;

import server.MapleStatEffect;

import java.util.concurrent.ScheduledFuture;

public class MapleBuffStatValueHolder {

    private final MapleStatEffect statEffect;
    private final long startTime;
    private final int characterId;
    private int value, localDuration;
    private ScheduledFuture<?> schedule;

    public MapleBuffStatValueHolder(MapleStatEffect effect, long startTime, ScheduledFuture<?> schedule, int value, int localDuration, int characterId) {
        super();
        this.statEffect = effect;
        this.startTime = startTime;
        this.schedule = schedule;
        this.value = value;
        this.localDuration = localDuration;
        this.characterId = characterId;
    }

    public MapleStatEffect getStatEffect() {
        return statEffect;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getCharacterId() {
        return characterId;
    }

    public int getValue() {
        return value;
    }

    public int getLocalDuration() {
        return localDuration;
    }

    public ScheduledFuture<?> getSchedule() {
        return schedule;
    }


    public void setValue(int value) {
        this.value = value;
    }

    public void setLocalDuration(int localDuration) {
        this.localDuration = localDuration;
    }

    public void setSchedule(ScheduledFuture<?> schedule) {
        this.schedule = schedule;
    }
}
