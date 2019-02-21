/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import client.MapleCharacter;
import handling.world.MaplePartyCharacter;

import java.lang.ref.WeakReference;

/**
 * TODO : Make this a function for NPC instead.. cleaner
 *
 * @author Rob
 */
public class MapleCarnivalChallenge {

    WeakReference<MapleCharacter> challenger;
    String challengeinfo = "";

    public MapleCarnivalChallenge(MapleCharacter challenger) {
        this.challenger = new WeakReference<>(challenger);
        challengeinfo += "#b";
        for (MaplePartyCharacter pc : challenger.getParty().getMembers()) {
            MapleCharacter c = challenger.getMap().getCharacterById(pc.getId());
            if (c != null) {
                challengeinfo += (c.getName() + ", a Level " + c.getLevel() + " " + getJobNameById(c.getJob()) + ", wants to battle your party!");
            }
        }
        challengeinfo += "#k";
    }

    /*     */
/*     */
    public static final String getJobNameById(int job) {
/*  42 */
        switch (job) {
/*     */
            case 0:
/*  44 */
                return "初心者";
/*     */
            case 1000:
/*  46 */
                return "貴族";
/*     */
            case 2000:
/*  48 */
                return "傳說";
/*     */
            case 2001:
/*  50 */
                return "龍魔導";
/*     */
            case 3000:
/*  52 */
                return "市民";
/*     */
            case 100:
/*  54 */
                return "戰士";
/*     */
            case 110:
/*  56 */
                return "狂戰士";
/*     */
            case 111:
/*  58 */
                return "十字軍";
/*     */
            case 112:
/*  60 */
                return "英雄";
/*     */
            case 120:
/*  62 */
                return "見習騎士";
/*     */
            case 121:
/*  64 */
                return "騎士";
/*     */
            case 122:
/*  66 */
                return "聖騎士";
/*     */
            case 130:
/*  68 */
                return "槍騎兵";
/*     */
            case 131:
/*  70 */
                return "龍騎士";
/*     */
            case 132:
/*  72 */
                return "黑騎士";
/*     */
            case 200:
/*  74 */
                return "法師";
/*     */
            case 210:
/*  76 */
                return "術師(火,毒)";
/*     */
            case 211:
/*  78 */
                return "魔導士(火,毒)";
/*     */
            case 212:
/*  80 */
                return "大魔導士(火,毒)";
/*     */
            case 220:
/*  82 */
                return "術師(冰,雷)";
/*     */
            case 221:
/*  84 */
                return "魔導士(冰,雷)";
/*     */
            case 222:
/*  86 */
                return "大魔導士(冰,雷)";
/*     */
            case 230:
/*  88 */
                return "僧侶";
/*     */
            case 231:
/*  90 */
                return "祭司";
/*     */
            case 232:
/*  92 */
                return "主教";
/*     */
            case 300:
/*  94 */
                return "弓箭手";
/*     */
            case 310:
/*  96 */
                return "獵人";
/*     */
            case 311:
/*  98 */
                return "遊俠";
/*     */
            case 312:
/* 100 */
                return "箭神";
/*     */
            case 320:
/* 102 */
                return "弩弓手";
/*     */
            case 321:
/* 104 */
                return "狙擊手";
/*     */
            case 322:
/* 106 */
                return "神射手";
/*     */
            case 400:
/* 108 */
                return "盜賊";
/*     */
            case 410:
/* 110 */
                return "刺客";
/*     */
            case 411:
/* 112 */
                return "暗殺者";
/*     */
            case 412:
/* 114 */
                return "夜使者";
/*     */
            case 420:
/* 116 */
                return "俠盜";
/*     */
            case 421:
/* 118 */
                return "神偷";
/*     */
            case 422:
/* 120 */
                return "暗影神偷";
/*     */
            case 430:
/* 122 */
                return "盜賊(影武)";
/*     */
            case 431:
/* 124 */
                return "下忍";
/*     */
            case 432:
/* 126 */
                return "中忍";
/*     */
            case 433:
/* 128 */
                return "上忍";
/*     */
            case 434:
/* 130 */
                return "影武者";
/*     */
            case 500:
/* 132 */
                return "海盜";
/*     */
            case 510:
/* 134 */
                return "打手";
/*     */
            case 511:
/* 136 */
                return "格鬥手";
/*     */
            case 512:
/* 138 */
                return "拳霸";
/*     */
            case 520:
/* 140 */
                return "槍手";
/*     */
            case 521:
/* 142 */
                return "神射手";
/*     */
            case 522:
/* 144 */
                return "槍神";
/*     */
            case 501:
/* 146 */
                return "海盜 (重砲手)";
/*     */
            case 530:
/* 148 */
                return "重砲手二轉";
/*     */
            case 531:
/* 150 */
                return "重砲手三轉";
/*     */
            case 532:
/* 152 */
                return "重砲手四轉";
/*     */
            case 1100:
/*     */
            case 1110:
/*     */
            case 1111:
/*     */
            case 1112:
/* 157 */
                return "聖魂騎士r";
/*     */
            case 1200:
/*     */
            case 1210:
/*     */
            case 1211:
/*     */
            case 1212:
/* 162 */
                return "烈焰巫師";
/*     */
            case 1300:
/*     */
            case 1310:
/*     */
            case 1311:
/*     */
            case 1312:
/* 167 */
                return "破風使者";
/*     */
            case 1400:
/*     */
            case 1410:
/*     */
            case 1411:
/*     */
            case 1412:
/* 172 */
                return "暗夜行者";
/*     */
            case 1500:
/*     */
            case 1510:
/*     */
            case 1511:
/*     */
            case 1512:
/* 177 */
                return "閃雷悍將";
/*     */
            case 2100:
/*     */
            case 2110:
/*     */
            case 2111:
/*     */
            case 2112:
/* 182 */
                return "狂狼勇士";
/*     */
            case 2200:
/*     */
            case 2210:
/*     */
            case 2211:
/*     */
            case 2212:
/*     */
            case 2213:
/*     */
            case 2214:
/*     */
            case 2215:
/*     */
            case 2216:
/*     */
            case 2217:
/*     */
            case 2218:
/* 193 */
                return "龍魔導";
/*     */
            case 2002:
/*     */
            case 2300:
/*     */
            case 2310:
/*     */
            case 2311:
/*     */
            case 2312:
/* 199 */
                return "精靈遊俠";
/*     */
            case 3001:
/*     */
            case 3100:
/*     */
            case 3110:
/*     */
            case 3111:
/*     */
            case 3112:
/* 205 */
                return "惡魔殺手";
/*     */
            case 3200:
/*     */
            case 3210:
/*     */
            case 3211:
/*     */
            case 3212:
/* 210 */
                return "煉獄巫師";
/*     */
            case 3300:
/*     */
            case 3310:
/*     */
            case 3311:
/*     */
            case 3312:
/* 215 */
                return "狂豹獵人";
/*     */
            case 3500:
/*     */
            case 3510:
/*     */
            case 3511:
/*     */
            case 3512:
/* 220 */
                return "機甲戰神";
/*     */
            case 2003:
/* 222 */
                return "???";
/*     */
            case 2400:
/*     */
            case 2410:
/*     */
            case 2411:
/*     */
            case 2412:
/* 227 */
                return "幻影";
/*     */
            case 508:
/*     */
            case 570:
/*     */
            case 571:
/*     */
            case 572:
/* 232 */
                return "傑特";
            case 5000:
/*     */
            case 5100:
            case 5110:
/*     */
            case 5111:
/*     */
            case 5112:
/* 232 */
                return "米哈逸";
/*     */
            case 900:
/* 234 */
                return "GM";
/*     */
            case 910:
/* 236 */
                return "SuperGM";
/*     */
            case 800:
/* 238 */
                return "Manager";
/*     */
        }
/* 240 */
        return "";
/*     */
    }

    /*     */
/*     */
    public static final String getJobBasicNameById(int job)
/*     */ {
/* 245 */
        switch (job) {
/*     */
            case 0:
/*     */
            case 1000:
/*     */
            case 2000:
/*     */
            case 2001:
/*     */
            case 2002:
/*     */
            case 2003:
/*     */
            case 3000:
/*     */
            case 3001:
/* 254 */
                return "初心者";
/*     */
            case 100:
/*     */
            case 110:
/*     */
            case 111:
/*     */
            case 112:
/*     */
            case 120:
/*     */
            case 121:
/*     */
            case 122:
/*     */
            case 130:
/*     */
            case 131:
/*     */
            case 132:
/*     */
            case 1100:
/*     */
            case 1110:
/*     */
            case 1111:
/*     */
            case 1112:
/*     */
            case 2100:
/*     */
            case 2110:
/*     */
            case 2111:
/*     */
            case 2112:
/*     */
            case 3100:
/*     */
            case 3110:
/*     */
            case 3111:
/*     */
            case 3112:
/* 277 */
                return "戰士";
/*     */
            case 200:
/*     */
            case 210:
/*     */
            case 211:
/*     */
            case 212:
/*     */
            case 220:
/*     */
            case 221:
/*     */
            case 222:
/*     */
            case 230:
/*     */
            case 231:
/*     */
            case 232:
/*     */
            case 1200:
/*     */
            case 1210:
/*     */
            case 1211:
/*     */
            case 1212:
/*     */
            case 2200:
/*     */
            case 2210:
/*     */
            case 2211:
/*     */
            case 2212:
/*     */
            case 2213:
/*     */
            case 2214:
/*     */
            case 2215:
/*     */
            case 2216:
/*     */
            case 2217:
/*     */
            case 2218:
/*     */
            case 3200:
/*     */
            case 3210:
/*     */
            case 3211:
/*     */
            case 3212:
/* 306 */
                return "法師";
/*     */
            case 300:
/*     */
            case 310:
/*     */
            case 311:
/*     */
            case 312:
/*     */
            case 320:
/*     */
            case 321:
/*     */
            case 322:
/*     */
            case 1300:
/*     */
            case 1310:
/*     */
            case 1311:
/*     */
            case 1312:
/*     */
            case 2300:
/*     */
            case 2310:
/*     */
            case 2311:
/*     */
            case 2312:
/*     */
            case 3300:
/*     */
            case 3310:
/*     */
            case 3311:
/*     */
            case 3312:
/* 326 */
                return "弓箭手";
/*     */
            case 400:
/*     */
            case 410:
/*     */
            case 411:
/*     */
            case 412:
/*     */
            case 420:
/*     */
            case 421:
/*     */
            case 422:
/*     */
            case 430:
/*     */
            case 431:
/*     */
            case 432:
/*     */
            case 433:
/*     */
            case 434:
/*     */
            case 1400:
/*     */
            case 1410:
/*     */
            case 1411:
/*     */
            case 1412:
/*     */
            case 2400:
/*     */
            case 2410:
/*     */
            case 2411:
/*     */
            case 2412:
/* 347 */
                return "盜賊";
/*     */
            case 500:
/*     */
            case 501:
/*     */
            case 508:
/*     */
            case 510:
/*     */
            case 511:
/*     */
            case 512:
/*     */
            case 520:
/*     */
            case 521:
/*     */
            case 522:
/*     */
            case 530:
/*     */
            case 531:
/*     */
            case 532:
/*     */
            case 570:
/*     */
            case 571:
/*     */
            case 572:
/*     */
            case 1500:
/*     */
            case 1510:
/*     */
            case 1511:
/*     */
            case 1512:
/*     */
            case 3500:
/*     */
            case 3510:
/*     */
            case 3511:
/*     */
            case 3512:
/* 371 */
                return "海盜";
/*     */
        }
/* 373 */
        return "";
/*     */
    }

    public MapleCharacter getChallenger() {
        return challenger.get();
    }

    public String getChallengeInfo() {
        return challengeinfo;
    }
/*     */
}

