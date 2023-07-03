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

import java.awt.*;

public abstract class AbstractLifeMovement implements ILifeMovement {

    private Point position;
    private int duration;
    private int newState;
    private int value;
    private MovementKind kind;

    public AbstractLifeMovement(int value, Point position, int duration, int newState, MovementKind kind) {
        super();
        this.value = value;
        this.position = position;
        this.duration = duration;
        this.newState = newState;
        this.kind = kind;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public int getNewState() {
        return newState;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public MovementKind getKind() {
        return kind;
    }
}
