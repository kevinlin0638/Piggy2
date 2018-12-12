/*
 * This file is part of the OdinMS MapleStory Private Server
 * Copyright (C) 2012 Patrick Huy and Matthias Butz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package server.movement;

import tools.data.MaplePacketLittleEndianWriter;

import java.awt.*;

public class RelativeLifeMovement extends AbstractLifeMovement {

    private short unk;

    public RelativeLifeMovement(int type, Point position, int duration, int newState, MovementKind kind) {
        super(type, position, duration, newState, kind);
    }

    public short getUnk() {
        return unk;
    }

    public void setUnk(short unk) {
        this.unk = unk;
    }

    @Override
    public void serialize(MaplePacketLittleEndianWriter lew) {
        lew.write(getValue());
        lew.writePos(getPosition());
        if (getValue() == 18 || getValue() == 19) {
            lew.writeShort(unk);
        }
        lew.write(getNewState());
        lew.writeShort(getDuration());
    }
}
