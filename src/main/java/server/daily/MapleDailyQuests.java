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

import tools.types.ArrayMap;

import java.util.Map;
import java.util.Map.Entry;

public class MapleDailyQuests {

    private Map<Integer, MapleDaily> daily_quest = new ArrayMap<Integer, MapleDaily>();
     private static MapleDailyQuests instance = new MapleDailyQuests();

     protected MapleDailyQuests() {
         daily_quest.put(1, new MapleDaily("持續在線 30分鐘"));//
         daily_quest.put(2, new MapleDaily("持續在線 1小時"));//
         daily_quest.put(3, new MapleDaily("持續在線 1小時30分鐘"));//
         daily_quest.put(4, new MapleDaily("持續在線 2小時"));//
         daily_quest.put(5, new MapleDaily("提升他人名聲"));//
         daily_quest.put(6, new MapleDaily("蒐集樹枝 200根"));//
         daily_quest.put(7, new MapleDaily("蒐集紅寶殼 200個"));//
         daily_quest.put(8, new MapleDaily("蒐集菇菇寶貝傘 200個"));//
         //daily_quest.put(9, new MapleDaily("完成任意組隊任務"));
         daily_quest.put(10, new MapleDaily("通過武陵道場 簡單"));//
         daily_quest.put(11, new MapleDaily("通過武陵道場 困難"));//
         daily_quest.put(12, new MapleDaily("通過武陵道場 地獄"));//
         daily_quest.put(22, new MapleDaily("通過武陵道場 夢魘"));//
         daily_quest.put(13, new MapleDaily("擊敗 殘暴炎魔"));//
         daily_quest.put(15, new MapleDaily("擊敗 獅王"));//
         daily_quest.put(16, new MapleDaily("擊敗 熊王"));//
         daily_quest.put(17, new MapleDaily("擊敗 暗黑龍王"));//
         daily_quest.put(21, new MapleDaily("贏得一場小遊戲"));//
         daily_quest.put(23, new MapleDaily("擊敗 渾沌殘暴炎魔"));//
         daily_quest.put(24, new MapleDaily("擊敗 渾沌暗黑龍王"));
     }

     public static MapleDailyQuests getInstance() {
     return instance;
     }

     public MapleDaily getById(int id) {
     return daily_quest.get(id);
     }

     public Integer getByMapleDailyQuest(MapleDaily ma) {
         for (Entry<Integer, MapleDaily> DailyQuest : this.daily_quest.entrySet()) {
             if (DailyQuest.getValue() == ma) {
                return DailyQuest.getKey();
             }
        }
        return null;
     }
}
