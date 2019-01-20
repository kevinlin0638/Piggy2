/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
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
package server.daily;

import client.MapleCharacter;
import tools.packet.CWvsContext;

/**
 *
 * @author KyleShum
 */
public class MapleDaily {

     private String name;

     public MapleDaily(String name) {
        this.name = name;
     }

     public String getName() {
     return name;
     }

     public void setName(String name) {
     this.name = name;
     }

     public void finishDailyQuest(MapleCharacter chr) {
         chr.setDailyQuestFinished(MapleDailyQuests.getInstance().getByMapleDailyQuest(this));
         chr.getClient().getSession().write(CWvsContext.broadcastMsg(5, "[每日任務] 完成每日任務 - " + name + " 請至萬能領取代幣."));
     }
}
