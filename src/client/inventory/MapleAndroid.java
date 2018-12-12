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
package client.inventory;

import database.DatabaseConnection;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.movement.AbsoluteLifeMovement;
import server.movement.ILifeMovement;
import server.movement.ILifeMovementFragment;
import tools.types.Pair;

import java.awt.*;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MapleAndroid implements Serializable {

    private static final long serialVersionUID = 9179541993413738569L;
    private int stance = 0, uniqueid, itemid, hair, face, skin;
    private String name;
    private Point pos = new Point(0, 0);

    private MapleAndroid(final int itemid, final int uniqueid) {
        this.itemid = itemid;
        this.uniqueid = uniqueid;
    }

    public static MapleAndroid loadFromDb(final int itemid, final int uid) {
        try {
            final MapleAndroid ret = new MapleAndroid(itemid, uid);

            Connection con = DatabaseConnection.getConnection(); // Get a connection to the database
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM androids WHERE uniqueid = ?")) {
                ps.setInt(1, uid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        rs.close();
                        ps.close();
                        return null;
                    }
                    ret.setSkin(rs.getInt("skin"));
                    ret.setHair(rs.getInt("hair"));
                    ret.setFace(rs.getInt("face"));
                    ret.setName(rs.getString("name"));
                }
            }

            return ret;
        } catch (SQLException ex) {
            System.out.println(ex);
            return null;
        }
    }

    public static MapleAndroid create(final int itemid, final int uniqueid) {
        Pair<List<Integer>, List<Integer>> aInfo = MapleItemInformationProvider.getInstance().getAndroidInfo(itemid == 1662006 ? 5 : (itemid - 1661999));
        if (aInfo == null) {
            return null;
        }
        return create(itemid, uniqueid, aInfo.left.get(Randomizer.nextInt(aInfo.left.size())), aInfo.right.get(Randomizer.nextInt(aInfo.right.size())), 0);
    }

    public static MapleAndroid create(int itemid, int uniqueid, int hair, int face, int skin) {
        if (uniqueid <= -1) { //wah
            uniqueid = MapleInventoryIdentifier.getInstance();
        }
        try {
            try (PreparedStatement pse = DatabaseConnection.getConnection().prepareStatement("INSERT INTO androids (uniqueid, hair, face, name, skin) VALUES (?, ?, ?, ?, ?)")) {
                pse.setInt(1, uniqueid);
                pse.setInt(2, hair);
                pse.setInt(3, face);
                pse.setString(4, "Android");
                pse.setInt(5, skin);
                pse.executeUpdate();
            }
        } catch (final SQLException ex) {
            return null;
        }
        final MapleAndroid pet = new MapleAndroid(itemid, uniqueid);
        pet.setHair(hair);
        pet.setFace(face);
        pet.setName("Android");

        return pet;
    }

    public final void saveToDb() {
        try {
            try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE androids SET skin = ?, hair = ?, face = ?, name = ? WHERE uniqueid = ?")) {
                ps.setInt(1, skin);
                ps.setInt(2, hair);
                ps.setInt(3, face);
                ps.setString(4, name);
                ps.setInt(5, uniqueid); // Set ID
                ps.executeUpdate();
            }
        } catch (final SQLException ex) {
            System.out.println(ex);
        }
    }

    public int getUniqueId() {
        return uniqueid;
    }

    public final int getHair() {
        return hair;
    }

    public final void setHair(final int closeness) {
        this.hair = closeness;
    }

    public final int getFace() {
        return face;
    }

    public final void setFace(final int closeness) {
        this.face = closeness;
    }

    public final int getSkin() {
        return skin;
    }

    public final void setSkin(final int s) {
        this.skin = s;
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        this.name = n;
    }

    public final Point getPos() {
        return pos;
    }

    public final void setPos(final Point pos) {
        this.pos = pos;
    }

    public final int getStance() {
        return stance;
    }

    public final void setStance(final int stance) {
        this.stance = stance;
    }

    public final int getItemId() {
        return itemid;
    }

    public final void updatePosition(final List<ILifeMovementFragment> movement) {
        for (final ILifeMovementFragment move : movement) {
            if (move instanceof ILifeMovement) {
                if (move instanceof AbsoluteLifeMovement) {
                    setPos(((ILifeMovement) move).getPosition());
                }
                setStance(((ILifeMovement) move).getNewState());
            }
        }
    }
}

