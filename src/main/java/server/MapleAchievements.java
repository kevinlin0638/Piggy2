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
package server;


import tools.types.ArrayMap;

import java.util.Map;
import java.util.Map.Entry;

public class MapleAchievements {

    private Map<Integer, MapleAchievement> achievements = new ArrayMap<Integer, MapleAchievement>();
     private static MapleAchievements instance = new MapleAchievements();

     protected MapleAchievements() {
         //achievements.put(1, new MapleAchievement("首次儲值", 1000000, false));
         achievements.put(2, new MapleAchievement("達到等級 30", 50000, false));
         achievements.put(3, new MapleAchievement("達到等級 70", 200000, false));
         achievements.put(4, new MapleAchievement("達到等級 120", 700000, false));
         achievements.put(5, new MapleAchievement("達到等級 200", 1000000, false));
         achievements.put(7, new MapleAchievement("獲得 50 名聲", 500000, false));
         achievements.put(8, new MapleAchievement("穿著獅王道具", 7, false));
         achievements.put(9, new MapleAchievement("穿著女皇道具", 7, false));
         achievements.put(10, new MapleAchievement("穿著恰吉面具", 7, false));
         achievements.put(11, new MapleAchievement("說 : 我愛小喵谷", 6, false));
         achievements.put(12, new MapleAchievement("擊敗 黑道大姊頭", 500000, false));
         achievements.put(13, new MapleAchievement("擊敗 拉圖斯", 500000, false));
         achievements.put(14, new MapleAchievement("擊敗 海怒斯", 500000, false));
         achievements.put(15, new MapleAchievement("擊敗 炎魔", 5000, false));
         achievements.put(16, new MapleAchievement("擊敗 龍王", 30000, false));
         achievements.put(17, new MapleAchievement("擊敗 皮卡丘", 50000, false));
         achievements.put(18, new MapleAchievement("擊敗 一個BOSS怪物", 1000, false));
         achievements.put(19, new MapleAchievement("首次贏得 'OX 問答' 活動", 10000, false));
         achievements.put(20, new MapleAchievement("首次贏得 '忍耐任務' 活動", 10000, false));
         achievements.put(21, new MapleAchievement("首次贏得 'Ola Ola' 活動", 10000, false));
         //achievements.put(22, new MapleAchievement("defeating BossQuest HELL mode", 100000));
         achievements.put(23, new MapleAchievement("擊敗 混沌炎魔", 15000, false));
         achievements.put(24, new MapleAchievement("擊敗 混沌龍王", 40000, false));
         achievements.put(25, new MapleAchievement("達到等級 210", 2000000, true));
         achievements.put(26, new MapleAchievement("達到等級 220", 3000000, true));
         achievements.put(27, new MapleAchievement("達到等級 230", 5000000, true));
         achievements.put(28, new MapleAchievement("達到等級 240", 10000000, true));
         achievements.put(29, new MapleAchievement("達到等級 250", 20000000, true));
         achievements.put(30, new MapleAchievement("達到等級 10", 77, true));
         achievements.put(31, new MapleAchievement("獲得 100 名聲", 1000000, true));
         achievements.put(32, new MapleAchievement("獲得 500 名聲", 5000000, true));

         achievements.put(40, new MapleAchievement("首次使用星力強化(裝備強化捲)至 5 星", 0, true));
         achievements.put(41, new MapleAchievement("首次使用星力強化(裝備強化捲)至 10 星", 0, true));
         achievements.put(42, new MapleAchievement("首次使用星力強化(裝備強化捲)至 15 星", 0, true));
         achievements.put(43, new MapleAchievement("首次使用星力強化(裝備強化捲)至 20 星", 5000000, true));
         achievements.put(44, new MapleAchievement("首次使用星力強化(裝備強化捲)至 25 星", 10000000, true));
         achievements.put(45, new MapleAchievement("首次使用星力強化(裝備強化捲)至 30 星", 50000000, true));


         achievements.put(60, new MapleAchievement("單下傷害超過 50000", 0, false));
         achievements.put(61, new MapleAchievement("單下傷害超過 99999", 0, false));
         achievements.put(62, new MapleAchievement("單下傷害超過 499999", 0, true));
         achievements.put(63, new MapleAchievement("單下傷害超過 999999", 0, true));
         achievements.put(64, new MapleAchievement("單下傷害超過 1999999", 0, true));
         achievements.put(65, new MapleAchievement("單下傷害超過 4999999", 0, true));
         achievements.put(66, new MapleAchievement("單下傷害超過 9999999", 0, true));
         achievements.put(67, new MapleAchievement("單下傷害超過 19999999", 0, true));
         achievements.put(68, new MapleAchievement("單下傷害超過 59999999", 0, true));
         achievements.put(69, new MapleAchievement("單下傷害超過 99999999", 0, true));

         achievements.put(70, new MapleAchievement("首次獲得 100 萬 楓幣", 0, false));
         achievements.put(71, new MapleAchievement("首次獲得 1000 萬 楓幣", 0, false));
         achievements.put(72, new MapleAchievement("首次獲得 1 億 楓幣", 0, false));
         achievements.put(73, new MapleAchievement("首次獲得 10 億 楓幣", 0, false));

         achievements.put(75, new MapleAchievement("首次通過普通武陵", 77, false));
         achievements.put(76, new MapleAchievement("首次通過困難武陵", 777, false));
         achievements.put(77, new MapleAchievement("首次通過地獄武陵", 7777, false));
         achievements.put(78, new MapleAchievement("首次通過夢魘武陵", 77777, false));

     }

     public static MapleAchievements getInstance() {
     return instance;
     }

     public MapleAchievement getById(int id) {
     return achievements.get(id);
     }

     public Integer getByMapleAchievement(MapleAchievement ma) {
         for (Entry<Integer, MapleAchievement> achievement : this.achievements.entrySet()) {
             if (achievement.getValue() == ma) {
                return achievement.getKey();
         }
     }
     return null;
     }
}
