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
package handling.login;

import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import tools.types.Triple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LoginInformationProvider {

    private final static LoginInformationProvider instance = new LoginInformationProvider();
    protected final List<String> ForbiddenName = new ArrayList<>();
    //gender, val, job
    protected final Map<Triple<Integer, Integer, Integer>, List<Integer>> makeCharInfo = new HashMap<>();
    protected LoginInformationProvider() {
        final MapleDataProvider prov = MapleDataProviderFactory.getDataProvider("Etc.wz");
        MapleData nameData = prov.getData("ForbiddenName.img");
        ForbiddenName.addAll(nameData.getChildren().stream().map(MapleDataTool::getString).collect(Collectors.toList()));
        nameData = prov.getData("Curse.img");
        ForbiddenName.addAll(nameData.getChildren().stream().map(data -> MapleDataTool.getString(data).split(",")[0]).collect(Collectors.toList()));
        final MapleData infoData = prov.getData("MakeCharInfo.img");
        for (MapleData dat : infoData) {
            if (!dat.getName().matches("^\\d+$") && !dat.getName().equals("000_1")) {
                continue;
            }
            for (MapleData d : dat) {
                int gender;
                if (d.getName().startsWith("female")) {
                    gender = 1;
                } else if (d.getName().startsWith("male")) {
                    gender = 0;
                } else {
                    continue;
                }

                for (MapleData da : d) {
                    Triple<Integer, Integer, Integer> key = new Triple<>(gender, Integer.parseInt(da.getName()), dat.getName().equals("000_1") ? 1 : Integer.parseInt(dat.getName()));
                    List<Integer> our = makeCharInfo.get(key);
                    if (our == null) {
                        our = new ArrayList<>();
                        makeCharInfo.put(key, our);
                    }
                    for (MapleData dd : da) {
                        if (dd.getName().equalsIgnoreCase("color")) {
                            for (MapleData dda : dd) {
                                for (MapleData ddd : dda) {
                                    our.add(MapleDataTool.getInt(ddd, -1));
                                }
                            }
                        } else if (!dd.getName().equalsIgnoreCase("name")) {
                            our.add(MapleDataTool.getInt(dd, -1));
                        }
                    }
                }
            }
        }

        final MapleData uA = infoData.getChildByPath("UltimateAdventurer");
        for (MapleData dat : uA) {
            final Triple<Integer, Integer, Integer> key = new Triple<Integer, Integer, Integer>(-1, Integer.parseInt(dat.getName()), JobType.UltimateAdventurer.type);
            List<Integer> our = makeCharInfo.get(key);
            if (our == null) {
                our = new ArrayList<Integer>();
                makeCharInfo.put(key, our);
            }
            for (MapleData d : dat) {
                our.add(MapleDataTool.getInt(d, -1));
            }
        }
    }
    //0 = eyes 1 = hair 2 = haircolor 3 = skin 4 = top 5 = bottom 6 = shoes 7 = weapon

    public static LoginInformationProvider getInstance() {
        return instance;
    }

    public final boolean isForbiddenName(final String in) {
        for (final String name : ForbiddenName) {
            if (in.toLowerCase().contains(name.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public final boolean isEligibleItem(int gender, int val, int job, int item) {
        if (item < 0) {
            return false;
        }
        final Triple<Integer, Integer, Integer> key = new Triple<>(gender, val, job);
        final List<Integer> our = makeCharInfo.get(key);
        if (our == null) {
            return false;
        }
        return our.contains(item);
    }

    public enum JobType {

        UltimateAdventurer(-1, "終極冒險家", 0, 130000000),
        Resistance(0, "反抗軍", 3000, 931000000),
        Adventurer(1, "冒險家", 0, 10000),
        DualBlade(1, "影武者", 0, 10000, (short) 1),
        Cygnus(2, "皇家騎士團", 1000, 913040000),
        Aran(3, "狂狼勇士", 2000, 140000000),
        Evan(4, "龍魔導士", 2001, 900090000),
        Mercedes(5, "精靈遊俠", 2002, 910150000),
        Demon(6, "惡魔殺手", 3001, 931050310),
        Phantom(7, "捷諾", 2400, 915000000);
        public int type, id, map;
        public short sub = 0;
        public String name;

        JobType(int type, String job, int id, int map) {
            this.type = type;
            this.name = job;
            this.id = id;
            this.map = map;
        }

        JobType(int type, String job, int id, int map, short sub) {
            this.type = type;
            this.name = job;
            this.id = id;
            this.map = map;
            this.sub = sub;
        }

        public static JobType getByJob(String g) {
            for (JobType e : JobType.values()) {
                if (e.name.length() > 0 && g.startsWith(e.name)) {
                    return e;
                }
            }
            return Adventurer;
        }

        public static JobType getByType(int g, int sub) {
            for (JobType e : JobType.values()) {
                if (e.type == g && e.sub == sub) {
                    return e;
                }
            }
            return Adventurer;
        }

        public static JobType getById(int g) {
            for (JobType e : JobType.values()) {
                if (e.id == g) {
                    return e;
                }
            }
            return Adventurer;
        }
    }
}
