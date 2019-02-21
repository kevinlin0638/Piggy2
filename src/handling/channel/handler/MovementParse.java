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
package handling.channel.handler;

import constants.ServerConstants;
import server.maps.AnimatedMapleMapObject;
import server.movement.*;
import tools.FileoutputUtil;
import tools.data.LittleEndianAccessor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MovementParse {

    //1 = player, 2 = mob, 3 = pet, 4 = summon, 5 = dragon
    public static List<ILifeMovementFragment> parseMovement(final LittleEndianAccessor lea, Point currentPos, final MovementKind kind) {
        final List<ILifeMovementFragment> res = new ArrayList<>();
        final byte numCommands = lea.readByte();

        for (byte i = 0; i < numCommands; i++) {
            final byte command = lea.readByte();
            switch (command) {
                case 0:
                case 7:
                case 14:
                case 16:
                case 45:
                case 46: {
                    final short xPos = lea.readShort();
                    final short yPos = lea.readShort();
                    final short xWobble = lea.readShort();
                    final short yWobble = lea.readShort();
                    final short unk = lea.readShort();
                    short fh = 0;
                    if (command == 14) {
                        fh = lea.readShort();
                    }
                    final short xOffset = lea.readShort();
                    final short yOffset = lea.readShort();
                    final byte newState = lea.readByte();
                    final short duration = lea.readShort();
                    final AbsoluteLifeMovement alm = new AbsoluteLifeMovement(command, new Point(xPos, yPos), duration, newState, kind);
                    alm.setUnk(unk);
                    alm.setFh(fh);
                    alm.setPixelsPerSecond(new Point(xWobble, yWobble));
                    alm.setOffset(new Point(xOffset, yOffset));
                    res.add(alm);
                    break;
                }
                case 44: {
                    final short xPos = lea.readShort();
                    final short yPos = lea.readShort();
                    final short xWobble = lea.readShort();
                    final short yWobble = lea.readShort();
                    final short unk = lea.readShort();
                    final byte newState = lea.readByte();
                    final short duration = lea.readShort();
                    final AbsoluteLifeMovement alm = new AbsoluteLifeMovement(command, new Point(xPos, yPos), duration, newState, kind);
                    alm.setUnk(unk);
                    alm.setFh((short) 0);
                    alm.setPixelsPerSecond(new Point(xWobble, yWobble));
                    alm.setOffset(new Point(0, 0));
                    res.add(alm);
                    break;
                }
                case 1:
                case 2:
                case 15:
                case 18:
                case 19:
                case 21:
                case 40:
                case 41:
                case 42:
                case 43: {
                    final short xMod = lea.readShort();
                    final short yMod = lea.readShort();
                    short unk = 0;
                    if (command == 18 || command == 19) {
                        unk = lea.readShort();
                    }
                    final byte newState = lea.readByte();
                    final short duration = lea.readShort();
                    final RelativeLifeMovement rlm = new RelativeLifeMovement(command, new Point(xMod, yMod), duration, newState, kind);
                    rlm.setUnk(unk);
                    res.add(rlm);
                    break;
                }
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39: {
                    final byte newState = lea.readByte();
                    final short unk = lea.readShort();
                    final GroundMovement am = new GroundMovement(command, currentPos, unk, newState, kind);
                    res.add(am);
                    break;
                }
                case 3:
                case 4:
                case 5:
                case 6:
                case 8:
                case 9:
                case 10:
                case 12: {
                    final short xPos = lea.readShort();
                    final short yPos = lea.readShort();
                    final short fh = lea.readShort();
                    final byte newState = lea.readByte();
                    final short duration = lea.readShort();
                    final TeleportMovement tm = new TeleportMovement(command, new Point(xPos, yPos), duration, newState, kind);
                    tm.setFh(fh);
                    res.add(tm);
                    break;
                }
                case 13: {
                    final short xWobble = lea.readShort();
                    final short yWobble = lea.readShort();
                    final short unk = 0;
                    short fh = lea.readShort();
                    final byte newState = lea.readByte();
                    final short duration = lea.readShort();
                    final AbsoluteLifeMovement alm = new AbsoluteLifeMovement(command, currentPos, duration, newState, kind);
                    alm.setUnk(unk);
                    alm.setFh(fh);
                    alm.setPixelsPerSecond(new Point(xWobble, yWobble));
                    alm.setOffset(new Point(0, 0));
                    res.add(alm);
                    break;
                }
                case 20: {
                    final short xPos = lea.readShort();
                    final short yPos = lea.readShort();
                    final short xOffset = lea.readShort();
                    final short yOffset = lea.readShort();
                    final byte newState = lea.readByte();
                    final short duration = lea.readShort();
                    final BounceMovement bm = new BounceMovement(command, new Point(xPos, yPos), duration, newState, kind);
                    bm.setOffset(new Point(xOffset, yOffset));
                    res.add(bm);
                    break;
                }
                case 11: { // Update Equip or Dash
                    res.add(new ChangeEquipSpecialAwesome(command, currentPos, lea.readByte(), kind));
                    break;
                }
                default:
                    if(ServerConstants.DEBUG)
                        System.out.println("Kind movement: " + kind + ", Remaining : " + (numCommands - res.size()) + " New type of movement ID : " + command + ", packet : " + lea.toString(true));
                    FileoutputUtil.log(FileoutputUtil.Movement_Log, "Kind movement: " + kind + ", Remaining : " + (numCommands - res.size()) + " New type of movement ID : " + command + ", packet : " + lea.toString(true));
                    return null;
            }
        }
        if (numCommands != res.size()) {
            return null; // Probably hack
        }
        return res;
    }

    static void updatePosition(final List<ILifeMovementFragment> movement, final AnimatedMapleMapObject target, final int yoffset) {
        if (movement == null) {
            return;
        }
        movement.stream().filter(move -> move instanceof ILifeMovement).forEach(move -> {
            if (move instanceof AbsoluteLifeMovement) {
                final Point position = move.getPosition();
                position.y += yoffset;
                target.setPosition(position);
            }
            target.setStance(((ILifeMovement) move).getNewState());
        });
    }
}
