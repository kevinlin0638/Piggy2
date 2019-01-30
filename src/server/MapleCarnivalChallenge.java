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
                return "Beginner";
/*     */
            case 1000:
/*  46 */
                return "Nobless";
/*     */
            case 2000:
/*  48 */
                return "Legend";
/*     */
            case 2001:
/*  50 */
                return "Evan";
/*     */
            case 3000:
/*  52 */
                return "Citizen";
/*     */
            case 100:
/*  54 */
                return "Warrior";
/*     */
            case 110:
/*  56 */
                return "Fighter";
/*     */
            case 111:
/*  58 */
                return "Crusader";
/*     */
            case 112:
/*  60 */
                return "Hero";
/*     */
            case 120:
/*  62 */
                return "Page";
/*     */
            case 121:
/*  64 */
                return "White Knight";
/*     */
            case 122:
/*  66 */
                return "Paladin";
/*     */
            case 130:
/*  68 */
                return "Spearman";
/*     */
            case 131:
/*  70 */
                return "Dragon Knight";
/*     */
            case 132:
/*  72 */
                return "Dark Knight";
/*     */
            case 200:
/*  74 */
                return "Magician";
/*     */
            case 210:
/*  76 */
                return "Wizard(Fire,Poison)";
/*     */
            case 211:
/*  78 */
                return "Mage(Fire,Poison)";
/*     */
            case 212:
/*  80 */
                return "Arch Mage(Fire,Poison)";
/*     */
            case 220:
/*  82 */
                return "Wizard(Ice,Lightning)";
/*     */
            case 221:
/*  84 */
                return "Mage(Ice,Lightning)";
/*     */
            case 222:
/*  86 */
                return "Arch Mage(Ice,Lightning)";
/*     */
            case 230:
/*  88 */
                return "Cleric";
/*     */
            case 231:
/*  90 */
                return "Priest";
/*     */
            case 232:
/*  92 */
                return "Bishop";
/*     */
            case 300:
/*  94 */
                return "Archer";
/*     */
            case 310:
/*  96 */
                return "Hunter";
/*     */
            case 311:
/*  98 */
                return "Ranger";
/*     */
            case 312:
/* 100 */
                return "Bowmaster";
/*     */
            case 320:
/* 102 */
                return "Crossbow man";
/*     */
            case 321:
/* 104 */
                return "Sniper";
/*     */
            case 322:
/* 106 */
                return "Crossbow Master";
/*     */
            case 400:
/* 108 */
                return "Rogue";
/*     */
            case 410:
/* 110 */
                return "Assassin";
/*     */
            case 411:
/* 112 */
                return "Hermit";
/*     */
            case 412:
/* 114 */
                return "Night Lord";
/*     */
            case 420:
/* 116 */
                return "Bandit";
/*     */
            case 421:
/* 118 */
                return "Chief Bandit";
/*     */
            case 422:
/* 120 */
                return "Shadower";
/*     */
            case 430:
/* 122 */
                return "Blade Recruit";
/*     */
            case 431:
/* 124 */
                return "Blade Acolyte";
/*     */
            case 432:
/* 126 */
                return "Blade Specialist";
/*     */
            case 433:
/* 128 */
                return "Blade Lord";
/*     */
            case 434:
/* 130 */
                return "Blade Master";
/*     */
            case 500:
/* 132 */
                return "Pirate";
/*     */
            case 510:
/* 134 */
                return "Brawler";
/*     */
            case 511:
/* 136 */
                return "Marauder";
/*     */
            case 512:
/* 138 */
                return "Buccaneer";
/*     */
            case 520:
/* 140 */
                return "Gunslinger";
/*     */
            case 521:
/* 142 */
                return "Outlaw";
/*     */
            case 522:
/* 144 */
                return "Corsair";
/*     */
            case 501:
/* 146 */
                return "Pirate (Cannoneer)";
/*     */
            case 530:
/* 148 */
                return "Cannoneer";
/*     */
            case 531:
/* 150 */
                return "Cannon Blaster";
/*     */
            case 532:
/* 152 */
                return "Cannon Master";
/*     */
            case 1100:
/*     */
            case 1110:
/*     */
            case 1111:
/*     */
            case 1112:
/* 157 */
                return "Soul Master";
/*     */
            case 1200:
/*     */
            case 1210:
/*     */
            case 1211:
/*     */
            case 1212:
/* 162 */
                return "Flame Wizard";
/*     */
            case 1300:
/*     */
            case 1310:
/*     */
            case 1311:
/*     */
            case 1312:
/* 167 */
                return "Wind Breaker";
/*     */
            case 1400:
/*     */
            case 1410:
/*     */
            case 1411:
/*     */
            case 1412:
/* 172 */
                return "Night Walker";
/*     */
            case 1500:
/*     */
            case 1510:
/*     */
            case 1511:
/*     */
            case 1512:
/* 177 */
                return "Striker";
/*     */
            case 2100:
/*     */
            case 2110:
/*     */
            case 2111:
/*     */
            case 2112:
/* 182 */
                return "Aran";
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
                return "Evan";
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
                return "Mercedes";
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
                return "Demon Slayer";
/*     */
            case 3200:
/*     */
            case 3210:
/*     */
            case 3211:
/*     */
            case 3212:
/* 210 */
                return "Battle Mage";
/*     */
            case 3300:
/*     */
            case 3310:
/*     */
            case 3311:
/*     */
            case 3312:
/* 215 */
                return "Wild Hunter";
/*     */
            case 3500:
/*     */
            case 3510:
/*     */
            case 3511:
/*     */
            case 3512:
/* 220 */
                return "Mechanic";
/*     */
            case 2003:
/* 222 */
                return "Miser";
/*     */
            case 2400:
/*     */
            case 2410:
/*     */
            case 2411:
/*     */
            case 2412:
/* 227 */
                return "Phantom";
/*     */
            case 508:
/*     */
            case 570:
/*     */
            case 571:
/*     */
            case 572:
/* 232 */
                return "Jett";
            case 5000:
/*     */
            case 5100:
            case 5110:
/*     */
            case 5111:
/*     */
            case 5112:
/* 232 */
                return "Mihile";
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

