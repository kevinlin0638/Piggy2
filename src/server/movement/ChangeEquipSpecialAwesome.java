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

public class ChangeEquipSpecialAwesome extends AbstractLifeMovement {

    private int type, wui;

    public ChangeEquipSpecialAwesome(int type, Point pos, int wui, MovementKind kind) {
        super(type, pos, 0, 0, kind);
        this.type = type;
        this.wui = wui;
    }

    @Override
    public MovementKind getKind() {
        return null;
    }

    @Override
    public void serialize(MaplePacketLittleEndianWriter lew) {
        lew.write(type);
        lew.write(wui);
    }

    @Override
    public Point getPosition() {
        return new Point(0, 0);
    }
}
