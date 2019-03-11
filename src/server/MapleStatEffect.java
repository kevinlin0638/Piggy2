package server;

import client.MapleCharacter;
import client.MapleCoolDownValueHolder;
import client.MapleStat;
import client.MapleTrait.MapleTraitType;
import client.PlayerStats;
import client.inventory.Item;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import client.skill.Skill;
import client.skill.SkillFactory;
import constants.GameConstants;
import constants.MapConstants;
import handling.channel.ChannelServer;
import handling.world.MaplePartyCharacter;
import handling.world.PlayerBuffValueHolder;
import handling.world.World;
import provider.MapleData;
import provider.MapleDataTool;
import provider.MapleDataType;
import server.MapleCarnivalFactory.MCSkill;
import server.Timer.BuffTimer;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MobSkill;
import server.maps.*;
import server.status.MapleBuffStatus;
import server.status.MonsterStatus;
import server.status.MonsterStatusEffect;
import tools.CaltechEval;
import tools.FileoutputUtil;
import tools.packet.CField;
import tools.packet.CField.EffectPacket;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.BuffPacket;
import tools.types.Pair;
import tools.types.Triple;

import java.awt.*;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;

public class MapleStatEffect implements Serializable {

    private static final long serialVersionUID = 9179541993413738569L;
    private byte mastery, mhpR, mmpR, mobCount, attackCount, bulletCount, reqGuildLevel, period, expR, familiarTarget, iceGageCon, //1 = party 2 = nearby
            recipeUseCount, recipeValidDay, reqSkillLevel, slotCount, effectedOnAlly, effectedOnEnemy, type, preventslip, immortal, bs;
    private short hp, mp, watk, matk, wdef, mdef, acc, avoid, hands, speed, jump, psdSpeed, psdJump, mpCon, hpCon, forceCon, bdR, damage, prop,
            ehp, emp, ewatk, ewdef, emdef, ignoreMob, dot, dotTime, criticaldamageMin, criticaldamageMax, pddR, mddR,
            asrR, terR, er, damR, padX, madX, mesoR, thaw, selfDestruction, PVPdamage, indiePad, indieMad, fatigueChange,
            str, dex, int_, luk, strX, dexX, intX, lukX, lifeId, imhp, immp, inflation, useLevel, mpConReduce,
            indieMhp, indieMmp, indieAllStat, indieSpeed, indieJump, indieAcc, indieEva, indiePdd, indieMdd, incPVPdamage, indieMhpR, indieMmpR,
            mobSkill, mobSkillLevel; //ar = accuracy rate
    private double hpR, mpR;
    private Map<MapleTraitType, Integer> traits;
    private int duration, subTime, sourceid, recipe, moveTo, t, u, v, w, x, y, z, cr, itemCon, itemConNo, bulletConsume, moneyCon,
            cooldown, morphId = 0, expinc, exp, consumeOnPickup, range, price, extendPrice, charColor, interval, rewardMeso, totalprob, cosmetic;
    private boolean overTime, skill, partyBuff = true;
    private EnumMap<MapleBuffStatus, Integer> statups;
    private ArrayList<Pair<Integer, Integer>> availableMap;
    private EnumMap<MonsterStatus, Integer> monsterStatus;
    private Point lt, rb;
    private int expBuff, itemup, mesoup, cashup, berserk, illusion, booster, berserk2, cp, nuffSkill;
    private byte level;
    //    private List<Pair<Integer, Integer>> randomMorph;
    private List<MapleBuffStatus> cureDebuffs;
    private List<Integer> petsCanConsume, familiars, randomPickup;
    private List<Triple<Integer, Integer, Integer>> rewardItem;

    public static MapleStatEffect loadSkillEffectFromData(final MapleData source, final int skillid, final boolean overtime, final int level, final String variables) {
        return loadFromData(source, skillid, true, overtime, level, variables);
    }

    public static MapleStatEffect loadItemEffectFromData(final MapleData source, final int itemid) {
        return loadFromData(source, itemid, false, false, 1, null);
    }

    private static void addBuffStatPairToListIfNotZero(final EnumMap<MapleBuffStatus, Integer> list, final MapleBuffStatus buffstat, final Integer val) {
        if (val != 0) {
            list.put(buffstat, val);
        }
    }

    public static int parseEval(String data, int level) {
        String variables = "x";
        String dddd = data.replace(variables, String.valueOf(level));
        if (dddd.substring(0, 1).equals("-")) { //-30+3*x
            if (dddd.substring(1, 2).equals("u") || dddd.substring(1, 2).equals("d")) { //-u(x/2)
                dddd = "n(" + dddd.substring(1, dddd.length()) + ")"; //n(u(x/2))
            } else {
                dddd = "n" + dddd.substring(1, dddd.length()); //n30+3*x
            }
        } else if (dddd.substring(0, 1).equals("=")) { //lol nexon and their mistakes
            dddd = dddd.substring(1, dddd.length());
        }
        return (int) (new CaltechEval(dddd).evaluate());
    }

    private static int parseEval(String path, MapleData source, int def, String variables, int level) {
        if (variables == null) {
            return MapleDataTool.getIntConvert(path, source, def);
        } else {
            final MapleData dd = source.getChildByPath(path);
            if (dd == null) {
                return def;
            }
            if (dd.getType() != MapleDataType.STRING) {
                return MapleDataTool.getIntConvert(path, source, def);
            }
            String dddd = MapleDataTool.getString(dd).replace(variables, String.valueOf(level));
            switch (dddd.substring(0, 1)) {
                case "-":
                    //-30+3*x
                    if (dddd.substring(1, 2).equals("u") || dddd.substring(1, 2).equals("d")) { //-u(x/2)
                        dddd = "n(" + dddd.substring(1, dddd.length()) + ")"; //n(u(x/2))
                    } else {
                        dddd = "n" + dddd.substring(1, dddd.length()); //n30+3*x
                    }
                    break;
                case "=":
                    //lol nexon and their mistakes
                    dddd = dddd.substring(1, dddd.length());
                    break;
            }
            return (int) (new CaltechEval(dddd).evaluate());
        }
    }

    private static MapleStatEffect loadFromData(final MapleData source, final int sourceid, final boolean skill, final boolean overTime, final int level, final String variables) {
        final MapleStatEffect ret = new MapleStatEffect();
        ret.sourceid = sourceid;
        ret.skill = skill;
        ret.level = (byte) level;
        if (source == null) {
            return ret;
        }
        ret.duration = parseEval("time", source, -1, variables, level);
        ret.subTime = parseEval("subTime", source, -1, variables, level); // used for debuff time..
        ret.hp = (short) parseEval("hp", source, 0, variables, level);
        ret.hpR = parseEval("hpR", source, 0, variables, level) / 100.0;
        ret.mp = (short) parseEval("mp", source, 0, variables, level);
        ret.mpR = parseEval("mpR", source, 0, variables, level) / 100.0;
        ret.mhpR = (byte) parseEval("mhpR", source, 0, variables, level);
        ret.mmpR = (byte) parseEval("mmpR", source, 0, variables, level);
        ret.pddR = (short) parseEval("pddR", source, 0, variables, level);
        ret.mddR = (short) parseEval("mddR", source, 0, variables, level);
        ret.ignoreMob = (short) parseEval("ignoreMobpdpR", source, 0, variables, level);
        ret.asrR = (short) parseEval("asrR", source, 0, variables, level);
        ret.terR = (short) parseEval("terR", source, 0, variables, level);
        ret.bdR = (short) parseEval("bdR", source, 0, variables, level);
        ret.damR = (short) parseEval("damR", source, 0, variables, level);
        ret.mesoR = (short) parseEval("mesoR", source, 0, variables, level);
        ret.thaw = (short) parseEval("thaw", source, 0, variables, level);
        ret.padX = (short) parseEval("padX", source, 0, variables, level);
        ret.madX = (short) parseEval("madX", source, 0, variables, level);
        ret.dot = (short) parseEval("dot", source, 0, variables, level);
        ret.dotTime = (short) parseEval("dotTime", source, 0, variables, level);
        ret.criticaldamageMin = (short) parseEval("criticaldamageMin", source, 0, variables, level);
        ret.criticaldamageMax = (short) parseEval("criticaldamageMax", source, 0, variables, level);
        ret.mpConReduce = (short) parseEval("mpConReduce", source, 0, variables, level);
        ret.forceCon = (short) parseEval("forceCon", source, 0, variables, level);
        ret.mpCon = (short) parseEval("mpCon", source, 0, variables, level);
        ret.hpCon = (short) parseEval("hpCon", source, 0, variables, level);
        ret.prop = (short) parseEval("prop", source, 100, variables, level);
        ret.cooldown = Math.max(0, parseEval("cooltime", source, 0, variables, level));
        ret.interval = parseEval("interval", source, 0, variables, level);
        ret.expinc = parseEval("expinc", source, 0, variables, level);
        ret.exp = parseEval("exp", source, 0, variables, level);
        ret.range = parseEval("range", source, 0, variables, level);
        ret.morphId = parseEval("morph", source, 0, variables, level);
        ret.cp = parseEval("cp", source, 0, variables, level);
        ret.cosmetic = parseEval("cosmetic", source, 0, variables, level);
        ret.er = (short) parseEval("er", source, 0, variables, level);
        ret.slotCount = (byte) parseEval("slotCount", source, 0, variables, level);
        ret.preventslip = (byte) parseEval("preventslip", source, 0, variables, level);
        ret.useLevel = (short) parseEval("useLevel", source, 0, variables, level);
        ret.nuffSkill = parseEval("nuffSkill", source, 0, variables, level);
        ret.familiarTarget = (byte) (parseEval("familiarPassiveSkillTarget", source, 0, variables, level) + 1);
        ret.mobCount = (byte) parseEval("mobCount", source, 1, variables, level);
        ret.immortal = (byte) parseEval("immortal", source, 0, variables, level);
        ret.iceGageCon = (byte) parseEval("iceGageCon", source, 0, variables, level);
        ret.expR = (byte) parseEval("expR", source, 0, variables, level);
        ret.reqGuildLevel = (byte) parseEval("reqGuildLevel", source, 0, variables, level);
        ret.period = (byte) parseEval("period", source, 0, variables, level);
        ret.type = (byte) parseEval("type", source, 0, variables, level);
        ret.bs = (byte) parseEval("bs", source, 0, variables, level);
        ret.attackCount = (byte) parseEval("attackCount", source, 1, variables, level);
        ret.bulletCount = (byte) parseEval("bulletCount", source, 1, variables, level);
        int priceUnit = parseEval("priceUnit", source, 0, variables, level);
        if (priceUnit > 0) {
            ret.price = parseEval("price", source, 0, variables, level) * priceUnit;
            ret.extendPrice = parseEval("extendPrice", source, 0, variables, level) * priceUnit;
        } else {
            ret.price = 0;
            ret.extendPrice = 0;
        }

        if (ret.skill) {
            switch (sourceid) {
                case 1100002:
                case 1200002:
                case 1300002:
                case 3100001:
                case 3200001:
                case 11101002:
                case 13101002:
                case 2111007:
                case 2211007:
                case 2311007:
                case 32111010:
                case 22161005:
                case 12111007:
                case 33100009:
                case 22150004:
                case 22181004: //All Final Attack
                case 1120013:
                case 3120008:
                case 23100006:
                case 23120012:
                case 24100003: // TODO: for now, or could it be card stack? (1 count)
                    ret.mobCount = 6;
                    break;
                case 35121005:
                case 35111004:
                case 35121013:
                    ret.attackCount = 6;
                    ret.bulletCount = 6;
                    break;
            }
            if (GameConstants.isNoDelaySkill(sourceid)) {
                ret.mobCount = 6;
            }
        }


        if (!ret.skill && ret.duration > -1) {
            ret.overTime = true;
        } else if (ret.isBadSkill()) {
            ret.duration *= 1000;
            ret.overTime = overTime || ret.isMorph() || ret.isPirateMorph() || ret.isFinalAttack() || ret.isAngel();
        } else {
            ret.duration *= 1000;
            ret.overTime = overTime || ret.isMorph() || ret.isPirateMorph() || ret.isFinalAttack() || ret.isAngel();
        }
        ret.statups = new EnumMap<>(MapleBuffStatus.class);
        ret.mastery = (byte) parseEval("mastery", source, 0, variables, level);
        ret.watk = (short) parseEval("pad", source, 0, variables, level);
        ret.wdef = (short) parseEval("pdd", source, 0, variables, level);
        ret.matk = (short) parseEval("mad", source, 0, variables, level);
        ret.mdef = (short) parseEval("mdd", source, 0, variables, level);
        ret.ehp = (short) parseEval("emhp", source, 0, variables, level);
        ret.emp = (short) parseEval("emmp", source, 0, variables, level);
        ret.ewatk = (short) parseEval("epad", source, 0, variables, level);
        ret.ewdef = (short) parseEval("epdd", source, 0, variables, level);
        ret.emdef = (short) parseEval("emdd", source, 0, variables, level);
        ret.acc = (short) parseEval("acc", source, 0, variables, level);
        ret.avoid = (short) parseEval("eva", source, 0, variables, level);
        ret.speed = (short) parseEval("speed", source, 0, variables, level);
        ret.jump = (short) parseEval("jump", source, 0, variables, level);
        ret.psdSpeed = (short) parseEval("psdSpeed", source, 0, variables, level);
        ret.psdJump = (short) parseEval("psdJump", source, 0, variables, level);
        ret.indiePad = (short) parseEval("indiePad", source, 0, variables, level);
        ret.indieMad = (short) parseEval("indieMad", source, 0, variables, level);
        ret.indieMhp = (short) parseEval("indieMhp", source, 0, variables, level);
        ret.indieMmp = (short) parseEval("indieMmp", source, 0, variables, level);
        ret.indieSpeed = (short) parseEval("indieSpeed", source, 0, variables, level);
        ret.indieJump = (short) parseEval("indieJump", source, 0, variables, level);
        ret.indieAcc = (short) parseEval("indieAcc", source, 0, variables, level);
        ret.indieEva = (short) parseEval("indieEva", source, 0, variables, level);
        ret.indiePdd = (short) parseEval("indiePdd", source, 0, variables, level);
        ret.indieMdd = (short) parseEval("indieMdd", source, 0, variables, level);
        ret.indieAllStat = (short) parseEval("indieAllStat", source, 0, variables, level);
        ret.indieMhpR = (short) parseEval("indieMhpR", source, 0, variables, level);
        ret.indieMmpR = (short) parseEval("indieMmpR", source, 0, variables, level);
        ret.str = (short) parseEval("str", source, 0, variables, level);
        ret.dex = (short) parseEval("dex", source, 0, variables, level);
        ret.int_ = (short) parseEval("int", source, 0, variables, level);
        ret.luk = (short) parseEval("luk", source, 0, variables, level);
        ret.strX = (short) parseEval("strX", source, 0, variables, level);
        ret.dexX = (short) parseEval("dexX", source, 0, variables, level);
        ret.intX = (short) parseEval("intX", source, 0, variables, level);
        ret.lukX = (short) parseEval("lukX", source, 0, variables, level);
        ret.expBuff = parseEval("expBuff", source, 0, variables, level);
        ret.cashup = parseEval("cashBuff", source, 0, variables, level);
        ret.itemup = parseEval("itemupbyitem", source, 0, variables, level);
        ret.mesoup = parseEval("mesoupbyitem", source, 0, variables, level);
        ret.berserk = parseEval("berserk", source, 0, variables, level);
        ret.berserk2 = parseEval("berserk2", source, 0, variables, level);
        ret.booster = parseEval("booster", source, 0, variables, level);
        ret.lifeId = (short) parseEval("lifeId", source, 0, variables, level);
        ret.inflation = (short) parseEval("inflation", source, 0, variables, level);
        ret.imhp = (short) parseEval("imhp", source, 0, variables, level);
        ret.immp = (short) parseEval("immp", source, 0, variables, level);
        ret.illusion = parseEval("illusion", source, 0, variables, level);
        ret.consumeOnPickup = parseEval("consumeOnPickup", source, 0, variables, level);
        if (ret.consumeOnPickup == 1) {
            if (parseEval("party", source, 0, variables, level) > 0) {
                ret.consumeOnPickup = 2;
            }
        }
        ret.charColor = 0;
        String cColor = MapleDataTool.getString("charColor", source, null);
        if (cColor != null) {

            try {
                ret.charColor |= Integer.parseInt("0x" + cColor.substring(0, 2));
                ret.charColor |= Integer.parseInt("0x" + cColor.substring(2, 4) + "00");
                ret.charColor |= Integer.parseInt("0x" + cColor.substring(4, 6) + "0000");
                ret.charColor |= Integer.parseInt("0x" + cColor.substring(6, 8) + "000000");

            } catch (NumberFormatException e) {
                //  System.out.println("This is not a number");
                //System.out.println(e.getMessage());


            }
        }
        ret.traits = new EnumMap<>(MapleTraitType.class);
        for (MapleTraitType t : MapleTraitType.values()) {
            int expz = parseEval(t.name() + "EXP", source, 0, variables, level);
            if (expz != 0) {
                ret.traits.put(t, expz);
            }
        }

        ret.recipe = parseEval("recipe", source, 0, variables, level);
        ret.recipeUseCount = (byte) parseEval("recipeUseCount", source, 0, variables, level);
        ret.recipeValidDay = (byte) parseEval("recipeValidDay", source, 0, variables, level);
        ret.reqSkillLevel = (byte) parseEval("reqSkillLevel", source, 0, variables, level);

        ret.effectedOnAlly = (byte) parseEval("effectedOnAlly", source, 0, variables, level);
        ret.effectedOnEnemy = (byte) parseEval("effectedOnEnemy", source, 0, variables, level);

        List<MapleBuffStatus> cure = new ArrayList<>(5);
        if (parseEval("poison", source, 0, variables, level) > 0) {
            cure.add(MapleBuffStatus.POISON);
        }
        if (parseEval("seal", source, 0, variables, level) > 0) {
            cure.add(MapleBuffStatus.SEAL);
        }
        if (parseEval("darkness", source, 0, variables, level) > 0) {
            cure.add(MapleBuffStatus.DARKNESS);
        }
        if (parseEval("weakness", source, 0, variables, level) > 0) {
            cure.add(MapleBuffStatus.WEAKEN);
        }
        if (parseEval("curse", source, 0, variables, level) > 0) {
            cure.add(MapleBuffStatus.CURSE);
        }
        ret.cureDebuffs = cure;

        ret.petsCanConsume = new ArrayList<>();
        for (int i = 0; true; i++) {
            final int dd = parseEval(String.valueOf(i), source, 0, variables, level);
            if (dd > 0) {
                ret.petsCanConsume.add(dd);
            } else {
                break;
            }
        }
        final MapleData mdd = source.getChildByPath("0");
        if (mdd != null && mdd.getChildren().size() > 0) {
            ret.mobSkill = (short) parseEval("mobSkill", mdd, 0, variables, level);
            ret.mobSkillLevel = (short) parseEval("level", mdd, 0, variables, level);
        } else {
            ret.mobSkill = 0;
            ret.mobSkillLevel = 0;
        }
        final MapleData pd = source.getChildByPath("randomPickup");
        if (pd != null) {
            ret.randomPickup = new ArrayList<>();
            for (MapleData p : pd) {
                ret.randomPickup.add(MapleDataTool.getInt(p));
            }
        }
        final MapleData ltd = source.getChildByPath("lt");
        if (ltd != null) {
            ret.lt = (Point) ltd.getData();
            ret.rb = (Point) source.getChildByPath("rb").getData();
        }

        final MapleData ltc = source.getChildByPath("con");
        if (ltc != null) {
            ret.availableMap = new ArrayList<>();
            for (MapleData ltb : ltc) {
                ret.availableMap.add(new Pair<>(MapleDataTool.getInt("sMap", ltb, 0), MapleDataTool.getInt("eMap", ltb, 999999999)));
            }
        }
        final MapleData ltb = source.getChildByPath("familiar");
        if (ltb != null) {
            ret.fatigueChange = (short) (parseEval("incFatigue", ltb, 0, variables, level) - parseEval("decFatigue", ltb, 0, variables, level));
            ret.familiarTarget = (byte) parseEval("target", ltb, 0, variables, level);
            final MapleData lta = ltb.getChildByPath("targetList");
            if (lta != null) {
                ret.familiars = new ArrayList<>();
                for (MapleData ltz : lta) {
                    ret.familiars.add(MapleDataTool.getInt(ltz, 0));
                }
            }
        } else {
            ret.fatigueChange = 0;
        }
        int totalprob = 0;
        final MapleData lta = source.getChildByPath("reward");
        if (lta != null) {
            ret.rewardMeso = parseEval("meso", lta, 0, variables, level);
            final MapleData ltz = lta.getChildByPath("case");
            if (ltz != null) {
                ret.rewardItem = new ArrayList<>();
                for (MapleData lty : ltz) {
                    ret.rewardItem.add(new Triple<>(MapleDataTool.getInt("id", lty, 0), MapleDataTool.getInt("count", lty, 0), MapleDataTool.getInt("prop", lty, 0)));
                    totalprob += MapleDataTool.getInt("prob", lty, 0);
                }
            }
        } else {
            ret.rewardMeso = 0;
        }
        ret.totalprob = totalprob;
        ret.cr = parseEval("cr", source, 0, variables, level);
        ret.t = parseEval("t", source, 0, variables, level);
        ret.u = parseEval("u", source, 0, variables, level);
        ret.v = parseEval("v", source, 0, variables, level);
        ret.w = parseEval("w", source, 0, variables, level);
        ret.x = parseEval("x", source, 0, variables, level);
        ret.y = parseEval("y", source, 0, variables, level);
        ret.z = parseEval("z", source, 0, variables, level);
        ret.damage = (short) parseEval("damage", source, 100, variables, level);
        ret.PVPdamage = (short) parseEval("PVPdamage", source, 0, variables, level);
        ret.incPVPdamage = (short) parseEval("incPVPDamage", source, 0, variables, level);
        ret.selfDestruction = (short) parseEval("selfDestruction", source, 0, variables, level);
        ret.bulletConsume = parseEval("bulletConsume", source, 0, variables, level);
        ret.moneyCon = parseEval("moneyCon", source, 0, variables, level);

        ret.itemCon = parseEval("itemCon", source, 0, variables, level);
        ret.itemConNo = parseEval("itemConNo", source, 0, variables, level);
        ret.moveTo = parseEval("moveTo", source, -1, variables, level);
        ret.monsterStatus = new EnumMap<>(MonsterStatus.class);

        if (ret.overTime && ret.getSummonMovementType() == null && !ret.isEnergyCharge()) {
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.WATK, (int) ret.watk);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.WDEF, (int) ret.wdef);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.MATK, (int) ret.matk);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.MDEF, (int) ret.mdef);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.ACC, (int) ret.acc);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.AVOID, (int) ret.avoid);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.SPEED, sourceid == 32120001 || sourceid == 32101003 ? Integer.valueOf(ret.x) : Integer.valueOf(ret.speed));
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.JUMP, (int) ret.jump);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.MAXHP, (int) ret.mhpR);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.MAXMP, (int) ret.mmpR);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.BOOSTER, ret.booster);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.WEAKEN, (int) ret.thaw);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.EXPRATE, ret.expBuff); // EXP
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.ACASH_RATE, ret.cashup); // custom
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.DROP_RATE, GameConstants.getModifier(ret.sourceid, ret.itemup)); // defaults to 2x
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.MESO_RATE, GameConstants.getModifier(ret.sourceid, ret.mesoup)); // defaults to 2x
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.BERSERK_FURY, ret.berserk2);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.ILLUSION, ret.illusion);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.PYRAMID_PQ, ret.berserk);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.ENHANCED_MAXHP, (int) ret.ehp);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.ENHANCED_MAXMP, (int) ret.emp);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.ENHANCED_WATK, (int) ret.ewatk);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.ENHANCED_WDEF, (int) ret.ewdef);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.ENHANCED_MDEF, (int) ret.emdef);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.GIANT_POTION, (int) ret.inflation);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.STR, (int) ret.str);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.DEX, (int) ret.dex);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.INT, (int) ret.int_);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.LUK, (int) ret.luk);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.ANGEL_ATK, (int) ret.indiePad);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.ANGEL_MATK, (int) ret.indieMad);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.HP_BOOST, (int) ret.imhp);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.MP_BOOST, (int) ret.immp); //same one? lol
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.HP_BOOST, (int) ret.indieMhp);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.MP_BOOST, (int) ret.indieMmp);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.PVP_DAMAGE, (int) ret.incPVPdamage);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.ANGEL_JUMP, (int) ret.indieJump);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.ANGEL_SPEED, (int) ret.indieSpeed);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.ANGEL_ACC, (int) ret.indieAcc);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.ANGEL_AVOID, (int) ret.indieEva);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.ANGEL_STAT, (int) ret.indieAllStat);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.PVP_ATTACK, (int) ret.PVPdamage);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.INVINCIBILITY, (int) ret.immortal);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.NO_SLIP, (int) ret.preventslip);
            addBuffStatPairToListIfNotZero(ret.statups, MapleBuffStatus.FAMILIAR_SHADOW, ret.charColor > 0 ? 1 : 0);
            if (sourceid == 5221006 || ret.isPirateMorph()) { //HACK: add stance :D and also this buffstat has to be the first one..
                ret.statups.put(MapleBuffStatus.STANCE, 100); //100% :D:D:D
            }
        }

        if(ret.cooldown == 30 || ret.cooldown == 20)
            ret.cooldown = 10;
        if (ret.skill) { // hack because we can't get from the datafile...
            switch (sourceid) {
                case 80001079:
                    ret.statups.put(MapleBuffStatus.WATK, (int) ret.damage);
                    ret.statups.put(MapleBuffStatus.MATK, (int) ret.damage);
                    ret.duration = 30 * 1000;
                    break;
                case 80001080:
                    ret.statups.put(MapleBuffStatus.WDEF, ret.x);
                    ret.statups.put(MapleBuffStatus.MDEF, ret.y);
                    ret.duration = 30 * 1000;
                    break;
                case 80001081:
                    ret.statups.put(MapleBuffStatus.EXPRATE, ret.x);
                    ret.duration = 30 * 1000;
                    break;
                case 2001002: // magic guard
                case 12001001:
                case 22111001:
                    ret.statups.put(MapleBuffStatus.MAGIC_GUARD, ret.x);
                    break;
                case 2301003: // invincible
                    ret.statups.put(MapleBuffStatus.INVINCIBLE, ret.x);
                    break;
                case 35120000:
                case 35001002:
                    ret.duration = 2100000000;
                    break;
                case 13101006: // Wind Walk
                    ret.statups.put(MapleBuffStatus.WIND_WALK, ret.x);
                    break;
                case 4330001:
                    ret.statups.put(MapleBuffStatus.DARK_SIGHT, (int) ret.level);
                    break;
                case 4001003: // darksight
                case 14001003: // cygnus ds
                    ret.statups.put(MapleBuffStatus.DARK_SIGHT, ret.x);
                    break;
                case 4211003: // pickpocket
                    ret.statups.put(MapleBuffStatus.PICKPOCKET, ret.x);
                    break;
                case 4211005: // mesoguard
                    ret.statups.put(MapleBuffStatus.MESOGUARD, ret.x);
                    break;
                case 4111001: // mesoup
                    ret.statups.put(MapleBuffStatus.MESOUP, ret.x);
                    break;
                case 4111002: // shadowpartner
                case 14111000: // cygnus
                case 4331002:
                    ret.statups.put(MapleBuffStatus.SHADOWPARTNER, ret.x);
                    break;
                case 5211009:
                case 4221013:
                case 51101004:
                    ret.statups.put(MapleBuffStatus.ENHANCED_WATK, 20);

                    //          ret.statups.put(MapleBuffStatus., 30);
                    break;

                case 24121004:
                    ret.statups.put(MapleBuffStatus.ENHANCED_WATK, 30);
                    //          ret.statups.put(MapleBuffStatus., 30);
                    break;
                case 5121015:
                    ret.statups.put(MapleBuffStatus.ENHANCED_WATK, 15);
                    ret.statups.put(MapleBuffStatus.ABNORMAL_STATUS_R, 30);
                    //          ret.statups.put(MapleBuffStatus., 30);
                    break;
                case 22181000:
                    ret.statups.put(MapleBuffStatus.ENHANCED_MATK, 10 + ret.x);
                    break;

//                case 51111004:
//                    ret.statups.put(MapleBuffStatus.DEFENCE_BOOST_R, 100);
//                    ret.statups.put(MapleBuffStatus.ELEMENTAL_STATUS_R, 60);
//                    ret.statups.put(MapleBuffStatus.ABNORMAL_STATUS_R, 60);
//                    //          ret.statups.put(MapleBuffStatus., 30);
//                    break;
//                case 51121006:
//                    ret.statups.put(MapleBuffStatus.ENHANCED_WATK, 60);
//                    ret.statups.put(MapleBuffStatus.CRITICAL_RATE_BUFF, 30);
//                    //          ret.statups.put(MapleBuffStatus., 30);
//                    break;
//                case 5111010:
//                    ret.statups.put(MapleBuffStatus.DEFENCE_BOOST_R, 15);
//                    //          ret.statups.put(MapleBuffStatus., 30);
//                    break;
                case 4211008:
                    ret.statups.put(MapleBuffStatus.SHADOWPARTNER, (int) ret.level);
                    break;
                case 11101002: // All Final attack
                case 13101002:
                    ret.statups.put(MapleBuffStatus.FINAL_SHOOT_ATTACK, ret.x);
                    break;
                case 22161004:
                    ret.statups.put(MapleBuffStatus.ONYX_SHROUD, ret.x);
                    break;
                case 3101004: // soul arrow
                case 3201004:
                case 2311002: // mystic door - hacked buff icon
                case 35101005:
                case 13101003:
                case 33101003:
                    ret.statups.put(MapleBuffStatus.SOULARROW, ret.x);
                    break;
                case 2321010:
                case 2221009:
                case 2121009:
                    ret.duration = 2100000000;
                    ret.statups.put(MapleBuffStatus.BUFF_MASTERY, ret.x);
                    break;
                case 2320011:
                case 2220010:
                case 2120010:
                    ret.duration = 2100000000;
                    ret.statups.put(MapleBuffStatus.ARCANE_AIM, ret.x);
                    break;
                case 1211006: // wk charges
                case 1211004:
                case 51111003:
                case 1211008:
                case 1221004:
                case 11111007:
                case 21101006:
                case 21111005:
                case 15101006:
                    ret.statups.put(MapleBuffStatus.WK_CHARGE, ret.x);
                    break;
                case 2111008:
                case 2211008:
                case 12101005:
                case 22121001: // Elemental Reset
                    ret.statups.put(MapleBuffStatus.ELEMENT_RESET, ret.x);
                    break;
                case 3111000:
                case 3121008:
                case 13111001:
                    ret.statups.put(MapleBuffStatus.CONCENTRATE, ret.x);
                    break;
                case 5110001: // Energy Charge
                case 15100004:
                    ret.statups.put(MapleBuffStatus.ENERGY_CHARGE, 0);
                    break;
                case 1101004:
                case 1201004:
                case 1301004:
                case 3101002:
                case 3201002:
                case 4101003:
                case 4201002:
                case 23101002:
                case 31001001:
                case 2111005: // spell booster, do these work the same?
                case 2211005:
                case 5101006:
                case 5201003:
                case 51101003:
                case 11101001:
                case 12101004:
                case 13101001:
                case 14101002:
                case 15101002:
                case 22141002: // Magic Booster
                case 2311006: // Magic Booster
                case 4301002:
                case 4311009:
                case 32101005:
                case 33001003:
                case 5701005:
                case 35101006:
                case 5301002:
                case 24101005:
                    ret.statups.put(MapleBuffStatus.BOOSTER, ret.x);
                    break;
                case 21001003: // Aran - Pole Arm Booster
                    ret.statups.put(MapleBuffStatus.BOOSTER, -ret.y);
                    break;
                case 35111013:
                case 5111007:
                case 5211007:
                case 15111011: //dice  
                case 5311005:
                case 5311004:
                case 5711011:
                case 5320007:
                    ret.duration = 90 * 1000;
                    ret.statups.put(MapleBuffStatus.DICE_ROLL, 0);
                    break;
                case 5120011:
                case 5220012:
                    ret.cooldown = ret.x;
                    ret.statups.put(MapleBuffStatus.PIRATES_REVENGE, (int) ret.damR); //i think
                    break;
                case 5121009:
                case 15111005:
                    ret.statups.put(MapleBuffStatus.SPEED_INFUSION, ret.x);
                    break;
                case 4321000: //tornado spin uses same buffstats
                    ret.duration = 1000;
                    ret.statups.put(MapleBuffStatus.DASH_SPEED, 100 + ret.x);
                    ret.statups.put(MapleBuffStatus.DASH_JUMP, ret.y); //always 0 but its there
                    break;
                case 5001005: // Dash
                case 15001003:
                    ret.statups.put(MapleBuffStatus.DASH_SPEED, ret.x);
                    ret.statups.put(MapleBuffStatus.DASH_JUMP, ret.y);
                    break;
                case 1101007: // pguard
                case 1201007:
                    ret.statups.put(MapleBuffStatus.POWERGUARD, ret.x);
                    break;
                case 32111004: //conversion
                    ret.statups.put(MapleBuffStatus.CONVERSION, ret.x);
                    break;
                case 1301007: // hyper body
                case 9001008:
                case 9101008:
                    ret.statups.put(MapleBuffStatus.MAXHP, ret.x);
                    ret.statups.put(MapleBuffStatus.MAXMP, ret.x);
                    break;
                case 1111002: // combo
                case 11111001: // combo
                    ret.statups.put(MapleBuffStatus.COMBO, 1);
                    break;
                case 21120007: //combo barrier
                    ret.statups.put(MapleBuffStatus.COMBO_BARRIER, ret.x);
                    break;
                case 5211006: // Homing Beacon
                case 5220011: // Bullseye
                case 22151002: //killer wings
                    ret.duration = 2100000000;
                    ret.statups.put(MapleBuffStatus.HOMING_BEACON, ret.x);
                    break;
                case 4341007:
                    ret.statups.put(MapleBuffStatus.STANCE, (int) ret.prop);
                    break;
                case 21111009: //combo recharge
                case 1311006: //dragon roar
                case 1311005: //NOT A BUFF - Sacrifice
                    ret.hpR = -ret.x / 100.0;
                    break;
                case 1211010: //NOT A BUFF - HP Recover
                    ret.hpR = ret.x / 100.0;
                    break;
                case 4341002:
                    ret.duration = 60 * 1000;
                    ret.hpR = -ret.x / 100.0;
                    ret.statups.put(MapleBuffStatus.FINAL_CUT, ret.y);
                    ret.statups.put(MapleBuffStatus.ATTACK_BUFF, ret.x);
                    break;
                case 2111007:
                case 2211007:
                case 2311007:
                case 32111010:
                case 22161005:
                case 12111007:
                    ret.mpCon = (short) ret.y;
                    ret.duration = 2100000000;
                    ret.statups.put(MapleBuffStatus.TELEPORT_MASTERY, ret.x);
                    //       ret.monsterStatus.put(MonsterStatus.STUN, Integer.valueOf(1));
                    break;

                case 4331003:
                    ret.duration = 2100000000;
                    ret.statups.put(MapleBuffStatus.OWL_SPIRIT, ret.y);
                    break;
                case 1311008: // dragon blood
                    if (!GameConstants.GMS) { //TODO JUMP
                        ret.statups.put(MapleBuffStatus.DRAGONBLOOD, ret.x);
                    }
                    break;
                case 5321005:
                case 1121000: // maple warrior, all classes
                case 1221000:
                case 1321000:
                case 51121005:
                case 2121000:
                case 2221000:
                case 2321000:
                case 3121000:
                case 3221000:
                case 4121000:
                case 4221000:
                case 5121000:
                case 5221000:
                case 21121000: // Aran - Maple Warrior
                case 22171000:
                case 4341000:
                case 32121007:
                case 33121007:
                case 5721000:
                case 35121007:
                case 23121005:
                case 31121004:
                case 24121008: // phantom
                    ret.statups.put(MapleBuffStatus.MAPLE_WARRIOR, ret.x);
                    break;
                case 15111006: //spark
                    ret.statups.put(MapleBuffStatus.SPARK, ret.x);
                    break;
                case 3121002: // sharp eyes bow master
                case 3221002: // sharp eyes marksmen
                case 33121004:
                    ret.statups.put(MapleBuffStatus.SHARP_EYES, (ret.x << 8) + ret.criticaldamageMax);
                    break;
                case 22151003: //magic resistance
                    ret.statups.put(MapleBuffStatus.MAGIC_RESISTANCE, ret.x);
                    break;
                case 2000007:
                case 12000006:
                case 22000002:
                case 32000012:
                    ret.statups.put(MapleBuffStatus.ELEMENT_WEAKEN, ret.x);
                    break;
                case 21101003: // Body Pressure
                    ret.statups.put(MapleBuffStatus.BODY_PRESSURE, ret.x);
                    break;
                case 21000000: // Aran Combo
                    ret.statups.put(MapleBuffStatus.ARAN_COMBO, 100);
                    break;
                case 23101003:
                    ret.statups.put(MapleBuffStatus.SPIRIT_SURGE, ret.x);
                    break;
                case 21100005: // Combo Drain
                case 32101004:
                case 31121002:
                    ret.statups.put(MapleBuffStatus.COMBO_DRAIN, ret.x);
                    break;
                case 21111001: // Smart Knockback
                    ret.statups.put(MapleBuffStatus.SMART_KNOCKBACK, ret.x);
                    break;
                case 23121004://TODO LEGEND
                    ret.statups.put(MapleBuffStatus.PIRATES_REVENGE, (int) ret.damR);
                    break;
                case 1211009:
                case 1111007:
                case 1311007: //magic crash
                case 51111005:
                    ret.monsterStatus.put(MonsterStatus.MAGIC_CRASH, 1);
                    break;
                case 1220013:
                    ret.statups.put(MapleBuffStatus.DIVINE_SHIELD, ret.x + 1);
                    break;
                case 1211011:
                    ret.statups.put(MapleBuffStatus.COMBAT_ORDERS, ret.x);
                    break;
                case 23111005:
                    ret.statups.put(MapleBuffStatus.ABNORMAL_STATUS_R, (int) ret.terR);
                    ret.statups.put(MapleBuffStatus.ELEMENTAL_STATUS_R, (int) ret.terR);
                    ret.statups.put(MapleBuffStatus.WATER_SHIELD, ret.x);
                    break;
                case 22131001: //magic shield
                    ret.statups.put(MapleBuffStatus.MAGIC_SHIELD, ret.x);
                    break;
                case 22181003: //soul stone
                    ret.statups.put(MapleBuffStatus.SOUL_STONE, 1);
                    break;
                case 32121003: //twister
                    ret.statups.put(MapleBuffStatus.TORNADO, ret.x);
                    break;
                case 2311009: //holy magic
                    ret.statups.put(MapleBuffStatus.HOLY_MAGIC_SHELL, ret.x);
                    ret.cooldown = ret.y;
                    ret.hpR = ret.z / 100.0;
                    break;
                case 32111005: //body boost
                    ret.duration = 60000;
                    ret.statups.put(MapleBuffStatus.BODY_BOOST, (int) ret.level); //lots of variables
                    break;
                case 22131002:
                case 22141003: // Slow
                    ret.statups.put(MapleBuffStatus.SLOW, ret.x);
                    break;
                case 4001002: // disorder
                case 14001002: // cygnus disorder
                    ret.monsterStatus.put(MonsterStatus.WATK, ret.x);
                    ret.monsterStatus.put(MonsterStatus.WDEF, ret.y);
                    break;
                case 5221009: // Mind Control
                    ret.monsterStatus.put(MonsterStatus.HYPNOTIZE, 1);
                    break;
                case 4341003: // Monster Bomb
                    ret.monsterStatus.put(MonsterStatus.MONSTER_BOMB, (int) ret.damage);
                    break;
                case 1201006: // threaten
                    ret.monsterStatus.put(MonsterStatus.WATK, ret.x);
                    ret.monsterStatus.put(MonsterStatus.WDEF, ret.x);
                    ret.monsterStatus.put(MonsterStatus.DARKNESS, ret.z);
                    break;
                case 22141001:
                case 1211002: // charged blow
                case 1111008: // shout
                case 4211002: // assaulter
                case 3101005: // arrow bomb
                case 1111005: // coma: sword
                case 4221007: // boomerang step
                case 5101002: // Backspin Blow
                case 5101003: // Double Uppercut
                case 5121004: // Demolition
                case 5121005: // Snatch
                    //     case 5121007: // Barrage
                case 5201004: // pirate blank shot
                case 4121008: // Ninja Storm
                case 22151001:
                case 4201004: //steal, new
                case 33101001:
                case 33101002:
                case 32101001:
                case 32111011:
                case 32121004:
                case 33111002:
                case 33121002:
                case 35101003:
                case 35111015:
                case 5111002: //energy blast
                case 15101005:
                case 4331005:
                case 1121001: //magnet
                case 1221001:
                case 1321001:
                case 9001020:
                case 31111001:
                case 31101002:
                case 9101020:
                case 2211003:
                case 2311004:
                case 3120010:
                case 22181001:
                case 21110006:
                case 22131000:
                case 5301001:
                case 5311001:
                case 5311002:
                case 2221006:
                case 5310008:
                    ret.monsterStatus.put(MonsterStatus.STUN, 1);
                    break;
                case 90001004:
                case 4321002:
                case 1111003:
                case 11111002:
                    ret.monsterStatus.put(MonsterStatus.DARKNESS, ret.x);
                    break;
                case 4221003:
                case 4121003:
                case 33121005:
                    ret.monsterStatus.put(MonsterStatus.SHOWDOWN, ret.x);
                    ret.monsterStatus.put(MonsterStatus.MDEF, ret.x);
                    ret.monsterStatus.put(MonsterStatus.WDEF, ret.x);
                    break;
                case 31121003:
                    ret.monsterStatus.put(MonsterStatus.SHOWDOWN, ret.w);
                    ret.monsterStatus.put(MonsterStatus.MDEF, ret.x);
                    ret.monsterStatus.put(MonsterStatus.WDEF, ret.x);
                    ret.monsterStatus.put(MonsterStatus.MATK, ret.x);
                    ret.monsterStatus.put(MonsterStatus.WATK, ret.x);
                    ret.monsterStatus.put(MonsterStatus.ACC, ret.x);
                    break;
                case 23121002: //not sure if negative
                    ret.monsterStatus.put(MonsterStatus.WDEF, -ret.x);
                    break;
                case 2201004: // cold beam
                case 2221003:
                case 2211002: // ice strike
                case 3211003: // blizzard
                case 2211006: // il elemental compo
                case 2221007: // Blizzard
                case 5211005: // Ice Splitter
                case 2121006: // Paralyze
                case 21120006: // Tempest
                case 22121000:
                case 90001006:
                case 2221001:
                    ret.monsterStatus.put(MonsterStatus.FREEZE, 1);
                    ret.duration *= 2; // freezing skills are a little strange
                    break;
                case 2101003: // fp slow
                case 2201003: // il slow
                case 12101001:
                case 90001002:
                    ret.monsterStatus.put(MonsterStatus.SPEED, ret.x);
                    break;
                case 4121015:
                    ret.monsterStatus.put(MonsterStatus.SPEED, ret.x);
                    ret.monsterStatus.put(MonsterStatus.WDEF, ret.x);
                    break;
                case 5011002:
                    ret.monsterStatus.put(MonsterStatus.SPEED, ret.z);
                    break;
                case 1121010: //enrage
                    ret.statups.put(MapleBuffStatus.ENRAGE, ret.x * 100 + ret.mobCount);
                    break;
                case 23111002: //TODO LEGEND: damage increase?
                case 22161002: //phantom imprint
                    ret.monsterStatus.put(MonsterStatus.IMPRINT, ret.x);
                    break;
                case 90001003:
                    ret.monsterStatus.put(MonsterStatus.POISON, 1);
                    break;
                case 4121004: // Ninja ambush
                case 4221004:
                    ret.monsterStatus.put(MonsterStatus.NINJA_AMBUSH, (int) ret.damage);
                    break;
                case 2311005:
                    ret.monsterStatus.put(MonsterStatus.DOOM, 1);
                    break;
                case 32111006:
                    ret.statups.put(MapleBuffStatus.REAPER, 1);
                    break;
                case 35121003:
                case 5211011:
                case 5211015:
                case 5211016:
                case 33111003:
                    ret.duration = 2100000000;
                    ret.statups.put(MapleBuffStatus.SUMMON, 1);
                    break;
                case 35111001:
                case 35111010:
                case 35111009:
                    ret.duration = 2100000000;
                    ret.statups.put(MapleBuffStatus.PUPPET, 1);
                    break;
                case 4341006:
                case 3120012:
                case 3220012:
                case 3111002: // puppet ranger
                case 3211002: // puppet sniper
                case 13111004: // puppet cygnus
                case 5211014: // Pirate octopus summon
                case 5220002: // wrath of the octopi

                case 5321003:
                    ret.statups.put(MapleBuffStatus.PUPPET, 1);
                    break;
                case 3120006:
                case 3220005:
                    ret.statups.put(MapleBuffStatus.ELEMENTAL_STATUS_R, (int) ret.terR);
                    ret.statups.put(MapleBuffStatus.SPIRIT_LINK, 1);
                    break;
                case 2121005: // elquines
                case 3201007:
                case 3101007:
                case 3211005: // golden eagle
                case 3111005: // golden hawk
                case 33111005:
                case 35111002:
                case 5711001:
                case 3121006: // phoenix
                case 23111008:
                case 23111009:
                case 23111010:
                    ret.statups.put(MapleBuffStatus.SUMMON, 1);
                    ret.monsterStatus.put(MonsterStatus.STUN, 1);
                    break;
                case 4111009:
                case 5201008:
                    ret.statups.put(MapleBuffStatus.SPIRIT_CLAW, 0);
                    break;
                case 3221005: // frostprey
                case 2221005: // ifrit
                    ret.statups.put(MapleBuffStatus.SUMMON, 1);
                    ret.monsterStatus.put(MonsterStatus.FREEZE, 1);
                    break;
                case 35111005:
                    ret.statups.put(MapleBuffStatus.SUMMON, 1);
                    ret.monsterStatus.put(MonsterStatus.SPEED, ret.x);
                    ret.monsterStatus.put(MonsterStatus.WDEF, ret.y);
                    break;
                case 1321007: // Beholder
                    ret.statups.put(MapleBuffStatus.BEHOLDER, (int) ret.level);
                    break;
                case 2321003: // bahamut
                case 5211002: // Pirate bird summon
                case 11001004:
                case 12001004:
                case 12111004: // Itrit
                case 13001004:
                case 14001005:
                case 15001004:
                case 35111011:
                case 35121009:
                case 35121011:
                case 33101008: //summon - its raining mines
                case 4111007: //dark flare
                case 4211007: //dark flare
                case 5321004:
                    ret.statups.put(MapleBuffStatus.SUMMON, 1);
                    break;
                case 35121010:
                    ret.duration = 60000;
                    ret.statups.put(MapleBuffStatus.DAMAGE_BUFF, ret.x);
                    break;
                case 31121005:
                    ret.statups.put(MapleBuffStatus.PIRATES_REVENGE, (int) ret.damR);
                    ret.statups.put(MapleBuffStatus.DARK_METAMORPHOSIS, 6); // mob count
                    break;
                case 2311003: // hs
                case 9001002: // GM hs
                case 9101002:
                    ret.statups.put(MapleBuffStatus.HOLY_SYMBOL, ret.x);
                    break;
                case 80001034: //virtue
                case 80001035: //virtue
                case 80001036: //virtue
                    ret.statups.put(MapleBuffStatus.VIRTUE_EFFECT, 1);
                    break;
                case 2211004: // il seal
                case 2111004: // fp seal
                case 12111002: // cygnus seal
                case 90001005:
                    ret.monsterStatus.put(MonsterStatus.SEAL, 1);
                    break;
                case 4111003: // shadow web
                case 14111001:
                    ret.monsterStatus.put(MonsterStatus.SHADOW_WEB, 1);
                    break;
                case 4121006: // spirit claw
                    ret.statups.put(MapleBuffStatus.SPIRIT_CLAW, 0);
                    break;
                case 2121004:
                case 2221004:
                case 2321004: // Infinity
                    ret.hpR = ret.y / 100.0;
                    ret.mpR = ret.y / 100.0;
                    ret.statups.put(MapleBuffStatus.INFINITY, ret.x);
                    if (GameConstants.GMS) { //TODO JUMP
                        ret.statups.put(MapleBuffStatus.STANCE, (int) ret.prop);
                    }
                    break;
                case 22181004:
                    ret.statups.put(MapleBuffStatus.ONYX_WILL, (int) ret.damage); //is this the right order
                    ret.statups.put(MapleBuffStatus.STANCE, (int) ret.prop);
                    break;
                case 1121002:
                case 32111014:
                case 1221002:
                case 51121004:
                case 1321002: // Stance
                case 21121003: // Aran - Freezing Posture
                case 32121005:
                case 5321010:
                    ret.statups.put(MapleBuffStatus.STANCE, (int) ret.prop);
                    break;
                case 2121002: // mana reflection
                case 2221002:
                case 2321002:
                    ret.statups.put(MapleBuffStatus.MANA_REFLECTION, 1);
                    break;
                case 2321005: // holy shield, TODO JUMP
                    ret.statups.put(MapleBuffStatus.HOLY_SHIELD, GameConstants.GMS ? (int) ret.level : ret.x);
                    break;
                case 3121007: // Hamstring
                    ret.statups.put(MapleBuffStatus.HAMSTRING, ret.x);
                    ret.monsterStatus.put(MonsterStatus.SPEED, ret.x);
                    break;
                case 3221006: // Blind
                case 33111004:
                    ret.statups.put(MapleBuffStatus.BLIND, ret.x);
                    ret.monsterStatus.put(MonsterStatus.ACC, ret.x);
                    break;
                case 33111007: //feline berserk
                    ret.statups.put(MapleBuffStatus.SPEED, ret.z);
                    ret.statups.put(MapleBuffStatus.DAMAGE_BUFF, ret.y);
                    ret.statups.put(MapleBuffStatus.ATTACK_BUFF, ret.x);
                    break;
                case 2301004:
                case 9001003:
                case 9101003:
                    ret.statups.put(MapleBuffStatus.BLESS, (int) ret.level);
                    break;
                case 32120000:
                    ret.dot = ret.damage;
                    ret.dotTime = 3;
                case 32001003: //dark aura
                case 32110007:
                    ret.duration = (sourceid == 32110007 ? 60000 : 2100000000);
                    ret.statups.put(MapleBuffStatus.AURA, (int) ret.level);
                    ret.statups.put(MapleBuffStatus.DARK_AURA, ret.x);
                    break;
                case 32111012: //blue aura
                case 32110000:
                case 32110008:
                    ret.duration = (sourceid == 32110008 ? 60000 : 2100000000);
                    ret.statups.put(MapleBuffStatus.AURA, (int) ret.level);
                    ret.statups.put(MapleBuffStatus.BLUE_AURA, (int) ret.level);
                    break;
                case 32120001:
                    ret.monsterStatus.put(MonsterStatus.SPEED, (int) ret.speed);
                case 32101003: //yellow aura
                case 32110009:
                    ret.duration = (sourceid == 32110009 ? 60000 : 2100000000);
                    ret.statups.put(MapleBuffStatus.AURA, (int) ret.level);
                    ret.statups.put(MapleBuffStatus.YELLOW_AURA, (int) ret.level);
                    break;
                case 33101004: //it's raining mines
                    ret.statups.put(MapleBuffStatus.RAINING_MINES, ret.x); //x?
                    break;
                case 35101007: //perfect armor
                    ret.duration = 2100000000;
                    ret.statups.put(MapleBuffStatus.PERFECT_ARMOR, ret.x);
                    break;
                case 31101003:
                    ret.statups.put(MapleBuffStatus.PERFECT_ARMOR, ret.y);
                    break;
                case 35121006: //satellite safety
                    ret.duration = 2100000000;
                    ret.statups.put(MapleBuffStatus.SATELLITESAFE_PROC, ret.x);
                    ret.statups.put(MapleBuffStatus.SATELLITESAFE_ABSORB, ret.y);
                    break;
                case 80001040:
                case 20021110:
                    ret.moveTo = ret.x;
                    break;
                case 80001089: // Soaring
                    ret.duration = 2100000000;
                    ret.statups.put(MapleBuffStatus.SOARING, ret.x);
                    break;
                case 35001001: //flame
                case 35101009:
                    ret.duration = 1000;
                    ret.statups.put(MapleBuffStatus.MECH_CHANGE, (int) level); //ya wtf
                    break;
                case 35121013:
                case 35111004: //siege
                    ret.duration = 2100000000;
                    ret.statups.put(MapleBuffStatus.MECH_CHANGE, (int) level); //ya wtf
                    break;
                case 35121005: //missile
                    ret.duration = 2100000000;
                    ret.statups.put(MapleBuffStatus.MECH_CHANGE, (int) level); //ya wtf
                    break;
                case 10001075: // Cygnus Echo
                    ret.statups.put(MapleBuffStatus.ECHO_OF_HERO, ret.x);
                    break;
                case 31121007: //無限力量
                    ret.statups.put(MapleBuffStatus.BOUNDLESS_RAGE, 1); // for now
                    break;
                case 31111004: // black hearted strength
                    ret.statups.put(MapleBuffStatus.ABNORMAL_STATUS_R, ret.y);
                    ret.statups.put(MapleBuffStatus.ELEMENTAL_STATUS_R, ret.z);
                    ret.statups.put(MapleBuffStatus.DEFENCE_BOOST_R, ret.x);
                    break;
                default:
                    break;
            }
            if (GameConstants.isBeginnerJob(sourceid / 10000)) {
                switch (sourceid % 10000) {
                    //angelic blessing: HACK, we're actually supposed to use the passives for atk/matk buff
                    case 1087:
                        ret.duration = 2100000000;
                        ret.statups.put(MapleBuffStatus.ANGEL_ATK, 10);
                        ret.statups.put(MapleBuffStatus.ANGEL_MATK, 10);
                        break;
                    case 1085:
                    case 1090:
                        ret.duration = 2100000000;
                        ret.statups.put(MapleBuffStatus.ANGEL_ATK, 5);
                        ret.statups.put(MapleBuffStatus.ANGEL_MATK, 5);
                        break;
                    case 1179:
                        ret.duration = 2100000000;
                        ret.statups.put(MapleBuffStatus.ANGEL_ATK, 12);
                        ret.statups.put(MapleBuffStatus.ANGEL_MATK, 12);
                        break;
                    case 1105:
                        ret.statups.put(MapleBuffStatus.ICE_SKILL, 1);
                        ret.duration = 2100000000;
                        break;
                    case 93:
                        ret.statups.put(MapleBuffStatus.HIDDEN_POTENTIAL, 1);
                        break;
                    case 8001:
                        ret.statups.put(MapleBuffStatus.SOULARROW, ret.x);
                        break;
                    case 1005: // Echo of Hero
                        ret.statups.put(MapleBuffStatus.ECHO_OF_HERO, ret.x);
                        break;
//                    case 1011: // Berserk fury
//                        ret.statups.put(MapleBuffStatus.BERSERK_FURY, ret.x);
//                        break;
//                    case 1010:
//                        ret.statups.put(MapleBuffStatus.DIVINE_BODY, 1);
//                        break;
                    case 1001:
                        if (sourceid / 10000 == 3001 || sourceid / 10000 == 3000) { //resistance is diff
                            ret.statups.put(MapleBuffStatus.INFILTRATE, ret.x);
                        } else {
                            ret.statups.put(MapleBuffStatus.RECOVERY, ret.x);
                        }
                        break;
                    case 8003:
                        ret.statups.put(MapleBuffStatus.MAXHP, ret.x);
                        ret.statups.put(MapleBuffStatus.MAXMP, ret.x);
                        break;
                    case 8004:
                        ret.statups.put(MapleBuffStatus.COMBAT_ORDERS, ret.x);
                        break;
                    case 8005:
                        ret.statups.put(MapleBuffStatus.HOLY_SHIELD, 1);
                        break;
                    case 8006:
                        ret.statups.put(MapleBuffStatus.SPEED_INFUSION, ret.x);
                        break;
                    case 103:
                        ret.monsterStatus.put(MonsterStatus.STUN, 1);
                        break;
                    case 99:
                    case 104:
                        ret.monsterStatus.put(MonsterStatus.FREEZE, 1);
                        ret.duration *= 2; // freezing skills are a little strange
                        break;
                    case 8002:
                        ret.statups.put(MapleBuffStatus.SHARP_EYES, (ret.x << 8) + ret.criticaldamageMax);
                        break;
                    case 1026: // Soaring
                    case 1142: // Soaring
                        //    ret.duration = 2100000000;
                        //   ret.statups.put(MapleBuffStatus.SOARING, 1);
                        break;
                }
            }
        } else {
            switch (sourceid) {
                case 2022746: //angel bless
                case 2022747: //d.angel bless
                case 2022823:
                case 2022764:
                    ret.statups.clear(); //no atk/matk
                    ret.statups.put(MapleBuffStatus.PYRAMID_PQ, 1); //ITEM_EFFECT buff
                    break;
            }
        }
        if (ret.isPoison()) {
            ret.monsterStatus.put(MonsterStatus.POISON, 1);
        }
        if (ret.isMorph() || ret.isPirateMorph()) {
            ret.statups.put(MapleBuffStatus.MORPH, ret.getMorph());
        }


        return ret;
    }

    public static Rectangle calculateBoundingBox(final Point posFrom, final boolean facingLeft, final Point lt, final Point rb, final int range) {
        if (lt == null || rb == null) {
            return new Rectangle((facingLeft ? (-200 - range) : 0) + posFrom.x, (-100 - range) + posFrom.y, 200 + range, 100 + range);
        }
        Point mylt;
        Point myrb;
        if (facingLeft) {
            mylt = new Point(lt.x + posFrom.x - range, lt.y + posFrom.y);
            myrb = new Point(rb.x + posFrom.x, rb.y + posFrom.y);
        } else {
            myrb = new Point(lt.x * -1 + posFrom.x + range, rb.y + posFrom.y);
            mylt = new Point(rb.x * -1 + posFrom.x, lt.y + posFrom.y);
        }
        return new Rectangle(mylt.x, mylt.y, myrb.x - mylt.x, myrb.y - mylt.y);
    }

    public static int parseMountInfo(final MapleCharacter player, final int skillid) {
        switch (skillid) {
            case 80001000:
            case 1004: // Monster riding
            case 11004: // Monster riding
            case 10001004:
            case 20001004:
            case 20011004:
            case 20021004:
                if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -118) != null && player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -119) != null) {
                    return player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -118).getItemId();
                }
                return parseMountInfo_Pure(player, skillid);
            default:
                return GameConstants.getMountItem(skillid, player);
        }
    }

    public static int parseMountInfo_Pure(final MapleCharacter player, final int skillid) {
        switch (skillid) {
            case 80001000:
            case 1004: // Monster riding
            case 11004: // Monster riding
            case 10001004:
            case 20001004:
            case 20011004:
            case 20021004:
                if (player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18) != null && player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -19) != null) {
                    return player.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -18).getItemId();
                }
                return 0;
            default:
                return GameConstants.getMountItem(skillid, player);
        }
    }

    private static int makeHealHP(double rate, double stat, double lowerfactor, double upperfactor) {
        return (int) ((Math.random() * ((int) (stat * upperfactor * rate) - (int) (stat * lowerfactor * rate) + 1)) + (int) (stat * lowerfactor * rate));
    }

    /**
     * @param applyto
     * @param obj
     * @param damage  done by the skill
     */
    public final void applyPassive(final MapleCharacter applyto, final MapleMapObject obj) {
        if (makeChanceResult() && !GameConstants.isDemon(applyto.getJob())) { // demon can't heal mp
            switch (sourceid) { // MP eater
                case 2100000:
                case 2200000:
                case 2300000:
                    if (obj == null || obj.getType() != MapleMapObjectType.MONSTER) {
                        return;
                    }
                    final MapleMonster mob = (MapleMonster) obj; // x is absorb percentage
                    if (!mob.getStats().isBoss()) {
                        final int absorbMp = Math.min((int) (mob.getMobMaxMp() * (getX() / 100.0)), mob.getMp());
                        if (absorbMp > 0) {
                            mob.setMp(mob.getMp() - absorbMp);
                            applyto.getStat().setMp(applyto.getStat().getMp() + absorbMp, applyto);
                            applyto.getClient().sendPacket(EffectPacket.showOwnBuffEffect(sourceid, 1, applyto.getLevel(), level));
                            applyto.getMap().broadcastMessage(applyto, EffectPacket.showBuffeffect(applyto.getId(), sourceid, 1, applyto.getLevel(), level), false);
                        }
                    }
                    break;
            }
        }
    }

    public final boolean applyTo(MapleCharacter chr) {
        return applyTo(chr, chr, true, null, duration);
    }

    public final boolean applyTo(MapleCharacter chr, Point pos) {
        return applyTo(chr, chr, true, pos, duration);
    }

    public final boolean applyTo(final MapleCharacter applyfrom, final MapleCharacter applyto, final boolean primary, final Point pos, int newDuration) {
        if (!applyfrom.isGM() && isSoaring() && MapConstants.isEventMap(applyfrom.getMapId())) {
            applyfrom.kickFromJQ();
            return false;
        } else if (!applyfrom.isGM() && sourceid == 23111001 && MapConstants.isEventMap(applyfrom.getMapId())) { // why would a gm use this idk
            applyfrom.dropMessage(5, "You can't use this skill in this map.");
            applyfrom.getClient().sendPacket(CWvsContext.enableActions());
            return false;
        }
        if ((isSoaring_Mount() && applyfrom.getBuffedValue(MapleBuffStatus.MONSTER_RIDING) == null) || (isSoaring_Normal() && !applyfrom.getMap().canSoar())) {
            applyfrom.getClient().sendPacket(CWvsContext.enableActions());
            return false;
        } else if (sourceid == 4341006 && applyfrom.getBuffedValue(MapleBuffStatus.SHADOWPARTNER) == null) {
            applyfrom.getClient().sendPacket(CWvsContext.enableActions());
            return false;
        } else if (sourceid == 33101008 && (applyfrom.getBuffedValue(MapleBuffStatus.RAINING_MINES) == null || applyfrom.getBuffedValue(MapleBuffStatus.SUMMON) != null || !applyfrom.canSummon())) {
            applyfrom.getClient().sendPacket(CWvsContext.enableActions());
            return false;
        } else if (isShadow() && applyfrom.getJob() != 411 && applyfrom.getJob() != 421 && applyfrom.getJob() != 412 && applyfrom.getJob() != 422 && applyfrom.getJob() != 1411 && applyfrom.getJob() != 1412) { //pirate/shadow = dc
            applyfrom.getClient().sendPacket(CWvsContext.enableActions());
            return false;
        } else if (isMI() && (applyfrom.getJob() != 434 && applyfrom.getJob() != 433)) { //pirate/shadow = dc
            applyfrom.getClient().sendPacket(CWvsContext.enableActions());
            return false;
        } else if (sourceid == 33101004 && applyfrom.getMap().isTown()) {
            applyfrom.dropMessage(5, "You may not use this skill in towns.");
            applyfrom.getClient().sendPacket(CWvsContext.enableActions());
            return false;
        } else if (sourceid == 20031203) { // To the Skies
            applyfrom.changeMap(150000000);
            return true;
        }
        if (!applyfrom.isGM()) {
            if (skill && (sourceid == 9001000 || sourceid == 9001001 || sourceid == 9001002 || sourceid == 9101000 || sourceid == 9101001 || sourceid == 9101002 || sourceid == 9101003 || sourceid == 9001004 || sourceid == 9101005 || sourceid == 9101006 || sourceid == 9101007 || sourceid == 9101008)) {
                World.Broadcast.broadcastMessage(applyfrom.getWorld(), CWvsContext.broadcastMsg(6, "[AutoBan]: " + applyfrom.getName() + "has Packet edited GM skills."));
                applyfrom.ban("Packet edited a GM Skill!", true);
                return false;
            }
        }
        int hpchange = calcHPChange(applyfrom, primary);
        int mpchange = calcMPChange(applyfrom, primary);

        final PlayerStats stat = applyto.getStat();
        if (primary) {
            if (itemConNo != 0 && !applyto.inPVP()) {
                if (!applyto.haveItem(itemCon, itemConNo, false, true)) {
                    applyto.getClient().sendPacket(CWvsContext.enableActions());
                    return false;
                }
                MapleInventoryManipulator.removeById(applyto.getClient(), GameConstants.getInventoryType(itemCon), itemCon, itemConNo, false, true);
            }
        } else if (!primary && isResurrection()) {
            hpchange = applyto.getStat().getMaxHp();
            applyto.getStat().heal(applyto);
            applyto.setStance(0); //TODO fix death bug, player doesnt spawn on other screen
        }
        if (isDispel() && makeChanceResult()) {
            applyto.cancelAllDiseaseBuff();
        } else if (isHeroWill()) {
            applyto.cancelAllDiseaseBuff();
        } else if (cureDebuffs.size() > 0) {
            for (final MapleBuffStatus debuff : cureDebuffs) {
                applyfrom.cancelDeiseaseBuff(debuff);
            }
        } else if (isMPRecovery()) {
            final int toDecreaseHP = ((stat.getMaxHp() / 100) * 10);
            if (stat.getHp() > toDecreaseHP) {
                hpchange += -toDecreaseHP; // -10% of max HP
                mpchange += ((toDecreaseHP / 100) * getY());
            } else {
                hpchange = stat.getHp() == 1 ? 0 : stat.getHp() - 1;
            }
        }
        LinkedHashMap statups = new LinkedHashMap();

        applyto.getAllBuffs().stream().forEach((PlayerBuffValueHolder pbvh) -> {
            pbvh.statup.keySet().stream().forEach((bu) -> {
                statups.put(bu, pbvh.statup.get(bu));
            });
        });
        if(statups.containsKey(MapleBuffStatus.DARK_METAMORPHOSIS)){
            mpchange = 0;
            hpchange = 0;
        }
        final Map<MapleStat, Integer> hpmpupdate = new EnumMap<>(MapleStat.class);
        if (hpchange != 0) {
            if (hpchange < 0 && (-hpchange) > stat.getHp() && !applyto.hasDisease(MapleBuffStatus.ZOMBIFY)) {
                applyto.getClient().sendPacket(CWvsContext.enableActions());
                return false;
            }
            stat.setHp(stat.getHp() + hpchange, applyto);
        }
        if (mpchange != 0) {
            if (mpchange < 0 && (-mpchange) > stat.getMp()) {
                applyto.getClient().sendPacket(CWvsContext.enableActions());
                return false;
            }
            //short converting needs math.min cuz of overflow
            if ((mpchange < 0 && GameConstants.isDemon(applyto.getJob())) || !GameConstants.isDemon(applyto.getJob())) { // heal
                stat.setMp(stat.getMp() + mpchange, applyto);
            }
        }
        hpmpupdate.put(MapleStat.HP, Integer.valueOf(stat.getHp()));
        hpmpupdate.put(MapleStat.MP, Integer.valueOf(stat.getMp()));
        applyto.getClient().sendPacket(CWvsContext.updatePlayerStats(hpmpupdate, true, applyto));
        if (expinc != 0) {
            applyto.gainExp(expinc, true, true, false);
            applyto.getClient().sendPacket(EffectPacket.showForeignEffect(20));
        } else if (sourceid / 10000 == 238) {
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            final int mobid = ii.getCardMobId(sourceid);
            if (mobid > 0) {
                final boolean done = applyto.getMonsterBook().monsterCaught(applyto.getClient(), mobid, MapleLifeFactory.getMonsterStats(mobid).getName());
                applyto.getClient().sendPacket(CWvsContext.getCard(done ? sourceid : 0, 1));
            }
        } else if (isReturnScroll()) {
            applyReturnScroll(applyto);
        } else if (useLevel > 0 && !skill) {
            applyto.setExtractor(new MapleExtractor(applyto, sourceid, useLevel * 50, 1440)); //no clue about time left
            applyto.getMap().spawnExtractor(applyto.getExtractor());
        } else if (isMistEruption()) {
            int i = y;
            for (MapleMist m : applyto.getMap().getAllMistsThreadsafe()) {
                if (m.getOwnerId() == applyto.getId() && m.getSourceSkill().getId() == 2111003) {
                    if (m.getSchedule() != null) {
                        m.getSchedule().cancel(false);
                        m.setSchedule(null);
                    }
                    if (m.getPoisonSchedule() != null) {
                        m.getPoisonSchedule().cancel(false);
                        m.setPoisonSchedule(null);
                    }
                    applyto.getMap().broadcastMessage(CField.removeMist(m.getObjectId(), true));
                    applyto.getMap().removeMapObject(m);

                    i--;
                    if (i <= 0) {
                        break;
                    }
                }
            }
        } else if (cosmetic > 0) {
            if (cosmetic >= 30000) {
                applyto.setHair(cosmetic);
                applyto.updateSingleStat(MapleStat.HAIR, cosmetic);
            } else if (cosmetic >= 20000) {
                applyto.setFace(cosmetic);
                applyto.updateSingleStat(MapleStat.FACE, cosmetic);
            } else if (cosmetic < 100) {
                applyto.setSkinColor((byte) cosmetic);
                applyto.updateSingleStat(MapleStat.SKIN, cosmetic);
            }
            applyto.equipChanged();
        } else if (bs > 0) {
            if (!applyto.inPVP()) {
                return false;
            }
            final int xd = Integer.parseInt(applyto.getEventInstance().getProperty(String.valueOf(applyto.getId())));
            applyto.getEventInstance().setProperty(String.valueOf(applyto.getId()), String.valueOf(xd + bs));
            applyto.getClient().sendPacket(CField.getPVPScore(xd + bs, false));
        } else if (iceGageCon > 0) {
            if (!applyto.inPVP()) {
                return false;
            }
            final int fdf = Integer.parseInt(applyto.getEventInstance().getProperty("icegage"));
            if (fdf < iceGageCon) {
                return false;
            }
            applyto.getEventInstance().setProperty("icegage", String.valueOf(fdf - iceGageCon));
            applyto.getClient().sendPacket(CField.getPVPIceGage(fdf - iceGageCon));
            applyto.applyIceGage(fdf - iceGageCon);
        } else if (recipe > 0) {
            if (applyto.getSkillLevel(recipe) > 0 || applyto.getProfessionLevel((recipe / 10000) * 10000) < reqSkillLevel) {
                return false;
            }
            applyto.changeSingleSkillLevel(SkillFactory.getCraft(recipe), Integer.MAX_VALUE, recipeUseCount, (long) (recipeValidDay > 0 ? (System.currentTimeMillis() + recipeValidDay * 24L * 60 * 60 * 1000) : -1L));
        } else if (isComboRecharge()) {
            applyto.setCombo((short) Math.min(30000, applyto.getCombo() + y));
            applyto.setLastCombo(System.currentTimeMillis());
            applyto.getClient().sendPacket(CField.rechargeCombo(applyto.getCombo()));
            SkillFactory.getSkill(21000000).getEffect(10).applyComboBuff(applyto, applyto.getCombo());
        } else if (isDragonBlink()) {
            final MaplePortal portal = applyto.getMap().getPortal(Randomizer.nextInt(applyto.getMap().getPortals().size()));
            if (portal != null) {
                applyto.getClient().sendPacket(CField.dragonBlink(portal.getId()));
                applyto.getMap().movePlayer(applyto, portal.getPosition());
                applyto.checkFollow();
            }
        } else if (isSpiritClaw()) {
            MapleInventory use = applyto.getInventory(MapleInventoryType.USE);
            boolean itemz = false;
            for (int i = 0; i < use.getSlotLimit(); i++) { // impose order...
                Item item = use.getItem((byte) i);
                if (item != null) {
                    if (GameConstants.isThrowingStar(item.getItemId()) && item.getQuantity() >= 100 || GameConstants.isBullet(item.getItemId()) && item.getQuantity() >= 100) {
                        MapleInventoryManipulator.removeFromSlot(applyto.getClient(), MapleInventoryType.USE, (short) i, (short) 100, false, true);
                        itemz = true;
                        break;
                    }
                }
            }
            if (!itemz) {
                return false;
            }
        } else if (cp != 0 && applyto.getCarnivalParty() != null) {
            applyto.getCarnivalParty().addCP(applyto, cp);
            applyto.CPUpdate(false, applyto.getAvailableCP(), applyto.getTotalCP(), 0);
            if (applyto.getMapId() == 980031100 || applyto.getMapId() == 980032100 || applyto.getMapId() == 980033100) {
                applyto.dropMessage(-1, applyto.getName() + " has earned " + cp + " points for Team [" + (applyto.getCarnivalParty().getTeam() == 0 ? "Maple Red" : "Maple Blue") + "]!");
            }
            // @eric: this should fix CPQ giving the CP for all of the teams, gMS BB now has ONE board and does not have 2 team digits, it's client-sided.
            for (MaplePartyCharacter mpc : applyto.getParty().getMembers()) {
                if (mpc.getId() != applyto.getId() && mpc.getChannel() == applyto.getClient().getChannel() && mpc.getMapid() == applyto.getMapId() && mpc.isOnline()) {
                    MapleCharacter mc = applyto.getMap().getCharacterById(mpc.getId());
                    if (mc != null) {
                        if (mc.getMapId() == 980031100 || mc.getMapId() == 980032100 || mc.getMapId() == 980033100) {
                            mc.dropMessage(-1, applyto.getName() + " has earned " + cp + " points for Team [" + (applyto.getCarnivalParty().getTeam() == 0 ? "Maple Red" : "Maple Blue") + "]!");
                        } else {
                            mc.CPUpdate(true, applyto.getCarnivalParty().getAvailableCP(), applyto.getCarnivalParty().getTotalCP(), applyto.getCarnivalParty().getTeam());
                        }
                    }
                }
            }
        } else if (nuffSkill != 0 && applyto.getParty() != null) {
            final MCSkill skil = MapleCarnivalFactory.getInstance().getSkill(nuffSkill);
            if (skil != null) {
                final MapleBuffStatus dis = skil.getDisease();
                for (MapleCharacter chr : applyto.getMap().getCharactersThreadsafe()) {
                    if (applyto.getParty() == null || chr.getParty() == null || (chr.getParty().getId() != applyto.getParty().getId())) {
                        if (skil.targetsAll || Randomizer.nextBoolean()) {
                            if (dis == null) {
                                chr.dispel();
                            } else if (skil.getSkill() == null) {
                                chr.getDiseaseBuff(dis, 1, 30000, MobSkill.getMobSkillByBuffStatus(dis), 1);
                            } else {
                                chr.getDiseaseBuff(dis, skil.getSkill());
                            }
                            if (!skil.targetsAll) {
                                break;
                            }
                        }
                    }
                }
            }
        } else if ((effectedOnEnemy > 0 || effectedOnAlly > 0) && primary && applyto.inPVP()) {
            final int typeee = Integer.parseInt(applyto.getEventInstance().getProperty("type"));
            if (typeee > 0 || effectedOnEnemy > 0) {
                for (MapleCharacter chr : applyto.getMap().getCharactersThreadsafe()) {
                    if (chr.getId() != applyto.getId() && (effectedOnAlly > 0 ? (chr.getTeam() == applyto.getTeam()) : (chr.getTeam() != applyto.getTeam() || typeee == 0))) {
                        applyTo(applyto, chr, false, pos, newDuration);
                    }
                }
            }
        } else if (mobSkill > 0 && mobSkillLevel > 0 && primary && applyto.inPVP()) {
            if (effectedOnEnemy > 0) {
                final int ffsa = Integer.parseInt(applyto.getEventInstance().getProperty("type"));
                for (MapleCharacter chr : applyto.getMap().getCharactersThreadsafe()) {
                    if (chr.getId() != applyto.getId() && (chr.getTeam() != applyto.getTeam() || ffsa == 0)) {
                        chr.disease(mobSkill, mobSkillLevel);
                    }
                }
            } else {
                if (sourceid == 2910000 || sourceid == 2910001) { //red WORLD_FLAGS
                    applyto.getClient().sendPacket(EffectPacket.showOwnBuffEffect(sourceid, 13, applyto.getLevel(), level));
                    applyto.getMap().broadcastMessage(applyto, EffectPacket.showBuffeffect(applyto.getId(), sourceid, 13, applyto.getLevel(), level), false);

                    applyto.getClient().sendPacket(EffectPacket.showOwnCraftingEffect("UI/UIWindow2.img/CTF/Effect", 0, 0));
                    applyto.getMap().broadcastMessage(applyto, EffectPacket.showCraftingEffect(applyto.getId(), "UI/UIWindow2.img/CTF/Effect", 0, 0), false);
                    if (applyto.getTeam() == (sourceid - 2910000)) { //restore duh WORLD_FLAGS
                        if (sourceid == 2910000) {
                            applyto.getEventInstance().broadcastPlayerMsg(-7, "The Red Team's WORLD_FLAGS has been restored.");
                        } else {
                            applyto.getEventInstance().broadcastPlayerMsg(-7, "The Blue Team's WORLD_FLAGS has been restored.");
                        }
                        applyto.getMap().spawnAutoDrop(sourceid, applyto.getMap().getGuardians().get(sourceid - 2910000).left);
                    } else {
                        applyto.disease(mobSkill, mobSkillLevel);
                        if (sourceid == 2910000) {
                            applyto.getEventInstance().setProperty("redflag", String.valueOf(applyto.getId()));
                            // applyto.getEventInstance().broadcastPlayerMsg(-7, "The Red Team's WORLD_FLAGS has been captured!");
                            applyto.getClient().sendPacket(EffectPacket.showOwnCraftingEffect("UI/UIWindow2.img/CTF/Tail/Red", 600000, 0));
                            applyto.getMap().broadcastMessage(applyto, EffectPacket.showCraftingEffect(applyto.getId(), "UI/UIWindow2.img/CTF/Tail/Red", 600000, 0), false);
                        } else {
                            applyto.getEventInstance().setProperty("blueflag", String.valueOf(applyto.getId()));
                            // applyto.getEventInstance().broadcastPlayerMsg(-7, "The Blue Team's WORLD_FLAGS has been captured!");
                            applyto.getClient().sendPacket(EffectPacket.showOwnCraftingEffect("UI/UIWindow2.img/CTF/Tail/Blue", 600000, 0));
                            applyto.getMap().broadcastMessage(applyto, EffectPacket.showCraftingEffect(applyto.getId(), "UI/UIWindow2.img/CTF/Tail/Blue", 600000, 0), false);
                        }
                    }
                } else {
                    applyto.disease(mobSkill, mobSkillLevel);
                }
            }
        } else if (randomPickup != null && randomPickup.size() > 0) {
            MapleItemInformationProvider.getInstance().getItemEffect(randomPickup.get(Randomizer.nextInt(randomPickup.size()))).applyTo(applyto);
        }
        for (Entry<MapleTraitType, Integer> tt : traits.entrySet()) {
            applyto.getTrait(tt.getKey()).addExp(tt.getValue(), applyto);
        }
        final SummonMovementType summonMovementType = getSummonMovementType();
        if (summonMovementType != null && (sourceid != 32111006 || (applyfrom.getBuffedValue(MapleBuffStatus.REAPER) != null && !primary))) {
            int summId = sourceid;
            if (sourceid == 3111002) {
                final Skill elite = SkillFactory.getSkill(3120012);
                if (applyfrom.getTotalSkillLevel(elite) > 0) {
                    return elite.getEffect(applyfrom.getTotalSkillLevel(elite)).applyTo(applyfrom, applyto, primary, pos, newDuration);
                }
            } else if (sourceid == 3211002) {
                final Skill elite = SkillFactory.getSkill(3220012);
                if (applyfrom.getTotalSkillLevel(elite) > 0) {
                    return elite.getEffect(applyfrom.getTotalSkillLevel(elite)).applyTo(applyfrom, applyto, primary, pos, newDuration);
                }
            }
            final MapleSummon tosummon = new MapleSummon(applyfrom, summId, getLevel(), new Point(pos == null ? applyfrom.getTruePosition() : pos), summonMovementType);
            applyfrom.cancelEffect(this, true, -1, this.statups);
            applyfrom.getMap().spawnSummon(tosummon);
            applyfrom.addSummon(tosummon);
            tosummon.addHP((short) x);
            if (isBeholder()) {
                tosummon.addHP((short) 1);
            } else if (sourceid == 4341006) {
                applyfrom.cancelEffectFromBuffStat(MapleBuffStatus.SHADOWPARTNER);
            } else if (sourceid == 32111006) {
                return true; //no buff
            } else if (sourceid == 35111002) {
                List<Integer> count = new ArrayList<>();
                final List<MapleSummon> ss = applyfrom.getSummonsReadLock();
                try {
                    for (MapleSummon s : ss) {
                        if (s.getSkill() == sourceid) {
                            count.add(s.getObjectId());
                        }
                    }
                } finally {
                    applyfrom.unlockSummonsReadLock();
                }
                if (count.size() != 3) {
                    return true; //no buff until 3
                }
                applyfrom.getClient().sendPacket(CField.skillCooldown(sourceid, getCooldown(applyfrom)));
                applyfrom.addCooldown(sourceid, System.currentTimeMillis(), getCooldown(applyfrom) * 1000);
                applyfrom.getMap().broadcastMessage(CField.teslaTriangle(applyfrom.getId(), count.get(0), count.get(1), count.get(2)));
            } else if (sourceid == 35121003) {
                applyfrom.getClient().sendPacket(CWvsContext.enableActions()); //doubt we need this at all
            }
        } else if (isMechDoor()) {
            int newId = 0;
            boolean applyBuff = false;
            if (applyto.getMechDoors().size() >= 2) {
                final MechDoor remove = applyto.getMechDoors().remove(0);
                newId = remove.getId();
                applyto.getMap().broadcastMessage(CField.removeMechDoor(remove, true));
                applyto.getMap().removeMapObject(remove);
            } else {
                for (MechDoor d : applyto.getMechDoors()) {
                    if (d.getId() == newId) {
                        applyBuff = true;
                        newId = 1;
                        break;
                    }
                }
            }
            final MechDoor door = new MechDoor(applyto, new Point(pos == null ? applyto.getTruePosition() : pos), newId);
            applyto.getMap().spawnMechDoor(door);
            applyto.addMechDoor(door);
            applyto.getClient().sendPacket(CWvsContext.mechPortal(door.getTruePosition()));
            if (!applyBuff) {
                return true; //do not apply buff until 2 doors spawned
            }
        }
        if (primary && availableMap != null) {
            for (Pair<Integer, Integer> e : availableMap) {
                if (applyto.getMapId() < e.left || applyto.getMapId() > e.right) {
                    applyto.getClient().sendPacket(CWvsContext.enableActions());
                    return true;
                }
            }
        }
        if (overTime && !isEnergyCharge()) {
            if(sourceid != 31121005 || !statups.containsKey(MapleBuffStatus.DARK_METAMORPHOSIS))
                applyBuffEffect(applyfrom, applyto, primary, newDuration);
        }
        if (skill) {
            removeMonsterBuff(applyfrom);
        }
        if (primary) {
            if ((overTime || isHeal()) && !isEnergyCharge()) {
                applyBuff(applyfrom, newDuration);
            }
            if (isMonsterBuff()) {
                applyMonsterBuff(applyfrom);
            }
        }
        if (isMagicDoor()) { // Magic Door
            MapleDoor door = new MapleDoor(applyto, new Point(pos == null ? applyto.getTruePosition() : pos), sourceid); // Current Map door
            if (door.getTownPortal() != null) {

                applyto.getMap().spawnDoor(door);
                applyto.addDoor(door);

                MapleDoor townDoor = new MapleDoor(door); // Town door
                applyto.addDoor(townDoor);
                door.getTown().spawnDoor(townDoor);

                if (applyto.getParty() != null) { // update town doors
                    applyto.silentPartyUpdate();
                }
            } else {
                applyto.dropMessage(5, "You may not spawn a door because all doors in the town are taken.");
            }
        } else if (isMist()) {
            final Rectangle bounds = calculateBoundingBox(pos != null ? pos : applyfrom.getPosition(), applyfrom.isFacingLeft());
            final MapleMist mist = new MapleMist(bounds, applyfrom, this);
            applyfrom.getMap().spawnMist(mist, 3000, false); // 3seconds for EventTimer instance
        } else if (isTimeLeap()) { // Time Leap
            for (MapleCoolDownValueHolder i : applyto.getCooldowns()) {
                if (i.skillId != 5121010) {
                    applyto.removeCooldown(i.skillId);
                    applyto.getClient().sendPacket(CField.skillCooldown(i.skillId, 0));
                }
            }
        }
        if (fatigueChange != 0 && applyto.getSummonedFamiliar() != null && (familiars == null || familiars.contains(applyto.getSummonedFamiliar().getFamiliar()))) {
            applyto.getSummonedFamiliar().addFatigue(applyto, fatigueChange);
        }
        if (rewardMeso != 0) {
            applyto.gainMeso(rewardMeso, false);
        }
        if (rewardItem != null && totalprob > 0) {
            for (Triple<Integer, Integer, Integer> reward : rewardItem) {
                if (MapleInventoryManipulator.checkSpace(applyto.getClient(), reward.left, reward.mid, "") && reward.right > 0 && Randomizer.nextInt(totalprob) < reward.right) { // Total prob
                    if (GameConstants.getInventoryType(reward.left) == MapleInventoryType.EQUIP) {
                        final Item item = MapleItemInformationProvider.getInstance().getEquipById(reward.left);
                        item.setGMLog("Reward item (statEffect): " + sourceid + " on " + FileoutputUtil.CurrentReadable_Date());
                        MapleInventoryManipulator.addbyItem(applyto.getClient(), item);
                    } else {
                        MapleInventoryManipulator.addById(applyto.getClient(), reward.left, reward.mid.shortValue(), "Reward item (statEffect): " + sourceid + " on " + FileoutputUtil.CurrentReadable_Date());
                    }
                }
            }
        }
        if (familiarTarget == 2 && applyfrom.getParty() != null && primary) { //to party
            for (MaplePartyCharacter mpc : applyfrom.getParty().getMembers()) {
                if (mpc.getId() != applyfrom.getId() && mpc.getChannel() == applyfrom.getClient().getChannel() && mpc.getMapid() == applyfrom.getMapId() && mpc.isOnline()) {
                    MapleCharacter mc = applyfrom.getMap().getCharacterById(mpc.getId());
                    if (mc != null) {
                        applyTo(applyfrom, mc, false, null, newDuration);
                    }
                }
            }
        } else if (familiarTarget == 3 && primary) {
            for (MapleCharacter mc : applyfrom.getMap().getCharactersThreadsafe()) {
                if (mc.getId() != applyfrom.getId()) {
                    applyTo(applyfrom, mc, false, null, newDuration);
                }
            }
        }
        return true;
    }

    public final boolean applyReturnScroll(final MapleCharacter applyto) {
        if (moveTo != -1) {
            if (applyto.getMap().getReturnMapId() != applyto.getMapId() || sourceid == 2031010 || sourceid == 2030021) {
                MapleMap target;
                if (moveTo == 999999999) {
                    target = applyto.getMap().getReturnMap();
                } else {
                    target = ChannelServer.getInstance(applyto.getClient().getWorld(), applyto.getClient().getChannel()).getMapFactory().getMap(moveTo);
                    if (target.getId() / 10000000 != 60 && applyto.getMapId() / 10000000 != 61) {
                        if (target.getId() / 10000000 != 21 && applyto.getMapId() / 10000000 != 20) {
                            if (target.getId() / 10000000 != applyto.getMapId() / 10000000) {
                                return false;
                            }
                        }
                    }
                }
                applyto.changeMap(target, target.getPortal(0));
                return true;
            }
        }
        return false;
    }

    private boolean isSoulStone() {
        return skill && sourceid == 22181003;
    }

    private void applyBuff(final MapleCharacter applyfrom, int newDuration) {
        if (isSoulStone()) {
            if (applyfrom.getParty() != null) {
                int membrs = 0;
                for (MapleCharacter chr : applyfrom.getMap().getCharactersThreadsafe()) {
                    if (chr.getParty() != null && chr.getParty().getId() == applyfrom.getParty().getId() && chr.isAlive()) {
                        membrs++;
                    }
                }
                List<MapleCharacter> awarded = new ArrayList<>();
                while (awarded.size() < Math.min(membrs, y)) {
                    for (MapleCharacter chr : applyfrom.getMap().getCharactersThreadsafe()) {
                        if (chr != null && chr.isAlive() && chr.getParty() != null && chr.getParty().getId() == applyfrom.getParty().getId() && !awarded.contains(chr) && Randomizer.nextInt(y) == 0) {
                            awarded.add(chr);
                        }
                    }
                }
                for (MapleCharacter chr : awarded) {
                    applyTo(applyfrom, chr, false, null, newDuration);
                    chr.getClient().sendPacket(EffectPacket.showOwnBuffEffect(sourceid, 2, applyfrom.getLevel(), level));
                    chr.getMap().broadcastMessage(chr, EffectPacket.showBuffeffect(chr.getId(), sourceid, 2, applyfrom.getLevel(), level), false);
                }
            }
        } else if (isPartyBuff() && (applyfrom.getParty() != null || isGmBuff() || applyfrom.inPVP())) {
            final Rectangle bounds = calculateBoundingBox(applyfrom.getTruePosition(), applyfrom.isFacingLeft());
            final List<MapleMapObject> affecteds = applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapleMapObjectType.PLAYER));

            for (final MapleMapObject affectedmo : affecteds) {
                final MapleCharacter affected = (MapleCharacter) affectedmo;
                if (affected.getId() != applyfrom.getId() && (isGmBuff() || (applyfrom.inPVP() && affected.getTeam() == applyfrom.getTeam() && Integer.parseInt(applyfrom.getEventInstance().getProperty("type")) != 0) || (applyfrom.getParty() != null && affected.getParty() != null && applyfrom.getParty().getId() == affected.getParty().getId()))) {
                    if ((isResurrection() && !affected.isAlive()) || (!isResurrection() && affected.isAlive())) {
                        applyTo(applyfrom, affected, false, null, newDuration);
                        affected.getClient().sendPacket(EffectPacket.showOwnBuffEffect(sourceid, 2, applyfrom.getLevel(), level));
                        affected.getMap().broadcastMessage(affected, EffectPacket.showBuffeffect(affected.getId(), sourceid, 2, applyfrom.getLevel(), level), false);
                    }
                    if (isTimeLeap()) {
                        for (MapleCoolDownValueHolder i : affected.getCooldowns()) {
                            if (i.skillId != 5121010) {
                                affected.removeCooldown(i.skillId);
                                affected.getClient().sendPacket(CField.skillCooldown(i.skillId, 0));
                            }
                        }
                    }
                }
            }
        }
    }

    private void removeMonsterBuff(final MapleCharacter applyfrom) {
        List<MonsterStatus> cancel = new ArrayList<>();
        switch (sourceid) {
            case 1111007:
            case 1211009:
            case 1311007:
            case 51111005:
                cancel.add(MonsterStatus.WEAPON_DEFENSE_UP);
                cancel.add(MonsterStatus.MAGIC_DEFENSE_UP);
                cancel.add(MonsterStatus.WEAPON_ATTACK_UP);
                cancel.add(MonsterStatus.MAGIC_ATTACK_UP);
                break;
            default:
                return;
        }
        final Rectangle bounds = calculateBoundingBox(applyfrom.getTruePosition(), applyfrom.isFacingLeft());
        final List<MapleMapObject> affected = applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapleMapObjectType.MONSTER));
        int i = 0;

        for (final MapleMapObject mo : affected) {
            if (makeChanceResult()) {
                for (MonsterStatus stat : cancel) {
                    ((MapleMonster) mo).cancelStatus(stat);
                }
            }
            i++;
            if (i >= mobCount) {
                break;
            }
        }
    }

    public final void applyMonsterBuff(final MapleCharacter applyfrom) {
        final Rectangle bounds = calculateBoundingBox(applyfrom.getTruePosition(), applyfrom.isFacingLeft());
        final boolean pvp = applyfrom.inPVP();
        final MapleMapObjectType typefe = pvp ? MapleMapObjectType.PLAYER : MapleMapObjectType.MONSTER;
        final List<MapleMapObject> affected = sourceid == 35111005 ? applyfrom.getMap().getMapObjectsInRange(applyfrom.getTruePosition(), Double.POSITIVE_INFINITY, Arrays.asList(typefe)) : applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(typefe));
        int i = 0;

        for (final MapleMapObject mo : affected) {
            if (makeChanceResult()) {
                for (Map.Entry<MonsterStatus, Integer> stat : getMonsterStati().entrySet()) {
                    if (pvp) {
                        MapleCharacter chr = (MapleCharacter) mo;
                        MapleBuffStatus d = MonsterStatus.getLinkedDisease(stat.getKey());
                        if (d != null) {
                            chr.getDiseaseBuff(d, stat.getValue(), getDuration(), MobSkill.getMobSkillByBuffStatus(d), 1);
                        }
                    } else {
                        MapleMonster mons = (MapleMonster) mo;
                        if (sourceid == 35111005 && mons.getStats().isBoss()) {
                            break;
                        }
                        mons.applyStatus(applyfrom, new MonsterStatusEffect(stat.getKey(), stat.getValue(), sourceid, null, false), isPoison(), isSubTime(sourceid) ? getSubTime() : getDuration(), true, this);
                    }
                }
                if (pvp && skill) {
                    MapleCharacter chr = (MapleCharacter) mo;
                    handleExtraPVP(applyfrom, chr);
                }
            }
            i++;
            if (i >= mobCount && sourceid != 35111005) {
                break;
            }
        }
    }

    public final boolean isSubTime(final int source) {
        switch (source) {
            case 1201006: // threaten
            case 23111008: // spirits
            case 23111009:
            case 23111010:
            case 31101003:
            case 31121003:
            case 31121005:
                return true;
        }
        return false;
    }

    public final void handleExtraPVP(MapleCharacter applyfrom, MapleCharacter chr) {
        if (sourceid == 2311005 || sourceid == 5121005 || sourceid == 1201006 || (GameConstants.isBeginnerJob(sourceid / 10000) && sourceid % 10000 == 104)) { //doom, threaten, snatch
            final long starttime = System.currentTimeMillis();

            final int localsourceid = sourceid == 5121005 ? 90002000 : sourceid;
            final Map<MapleBuffStatus, Integer> localstatups = new EnumMap<>(MapleBuffStatus.class);
            if (sourceid == 2311005) {
                localstatups.put(MapleBuffStatus.MORPH, 7);
            } else if (sourceid == 1201006) {
                localstatups.put(MapleBuffStatus.THREATEN_PVP, (int) level);
            } else if (sourceid == 5121005) {
                localstatups.put(MapleBuffStatus.SNATCH, 1);
            } else {
                localstatups.put(MapleBuffStatus.MORPH, x);
            }
            chr.getClient().sendPacket(BuffPacket.giveBuff(localsourceid, getDuration(), localstatups, this));
            chr.registerEffect(this, starttime, BuffTimer.getInstance().schedule(new CancelEffectAction(chr, this, starttime, localstatups), isSubTime(sourceid) ? getSubTime() : getDuration()), localstatups, false, getDuration(), applyfrom.getId());
        }
    }

    public final Rectangle calculateBoundingBox(final Point posFrom, final boolean facingLeft) {
        return calculateBoundingBox(posFrom, facingLeft, lt, rb, range);
    }

    public final Rectangle calculateBoundingBox(final Point posFrom, final boolean facingLeft, int addedRange) {
        return calculateBoundingBox(posFrom, facingLeft, lt, rb, range + addedRange);
    }

    public final double getMaxDistanceSq() { //lt = infront of you, rb = behind you; not gonna distanceSq the two points since this is in relative to player position which is (0,0) and not both directions, just one
        final int maxX = Math.max(Math.abs(lt == null ? 0 : lt.x), Math.abs(rb == null ? 0 : rb.x));
        final int maxY = Math.max(Math.abs(lt == null ? 0 : lt.y), Math.abs(rb == null ? 0 : rb.y));
        return (maxX * maxX) + (maxY * maxY);
    }

    public final void silentApplyBuff(final MapleCharacter chr, final long starttime, final int localDuration, final Map<MapleBuffStatus, Integer> statup, final int cid) {
        chr.registerEffect(this, starttime, BuffTimer.getInstance().schedule(new CancelEffectAction(chr, this, starttime, statup),
                ((starttime + localDuration) - System.currentTimeMillis())), statup, true, localDuration, cid);

        final SummonMovementType summonMovementType = getSummonMovementType();
        if (summonMovementType != null) {
            final MapleSummon tosummon = new MapleSummon(chr, this, chr.getTruePosition(), summonMovementType);
            if (!tosummon.isPuppet()) {
                chr.getMap().spawnSummon(tosummon);
                chr.addSummon(tosummon);
                tosummon.addHP((short) x);
                if (isBeholder()) {
                    tosummon.addHP((short) 1);
                }
            }
        }
    }

    public final void applyComboBuff(final MapleCharacter applyto, short combo) {
        final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
        stat.put(MapleBuffStatus.ARAN_COMBO, (int) combo);
        applyto.getClient().sendPacket(BuffPacket.giveBuff(sourceid, 99999, stat, this)); // Hackish timing, todo find out

        final long starttime = System.currentTimeMillis();
//	final CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime);
//	final ScheduledFuture<?> schedule = TimerManager.getInstance().schedule(cancelAction, ((starttime + 99999) - System.currentTimeMillis()));
        applyto.registerEffect(this, starttime, null, applyto.getId());
    }

    public final void applyEnergyBuff(final MapleCharacter applyto, final boolean infinity) {
        final long starttime = System.currentTimeMillis();
        if (infinity) {
            applyto.getClient().sendPacket(BuffPacket.giveEnergyChargeTest(0, duration / 1000));
            applyto.registerEffect(this, starttime, null, applyto.getId());
        } else {
            final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
            stat.put(MapleBuffStatus.ENERGY_CHARGE, 10000);
            applyto.cancelEffect(this, true, -1, stat);
            applyto.getMap().broadcastMessage(applyto, BuffPacket.giveEnergyChargeTest(applyto.getId(), 10000, duration / 1000), false);
            final CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime, stat);
            final ScheduledFuture<?> schedule = BuffTimer.getInstance().schedule(cancelAction, ((starttime + duration) - System.currentTimeMillis()));
            applyto.registerEffect(this, starttime, schedule, stat, false, duration, applyto.getId());

        }
    }

    private void applyBuffEffect(final MapleCharacter applyfrom, final MapleCharacter applyto, final boolean primary, final int newDuration) {
        int localDuration = newDuration;
        if (primary) {
            localDuration = Math.max(newDuration, alchemistModifyVal(applyfrom, localDuration, false));
        }
        Map<MapleBuffStatus, Integer> localstatups = statups, maskedStatups = null;
        boolean normal = true, showEffect = primary;
        int maskedDuration = 0;
        switch (sourceid) {
            case 80001079: // Fixed these monster carnival skills already, no use here anymore. - Eric
            case 80001080:
            case 80001081:
                // applyto.dropMessage(5, "Monster Carnival buffs are currently being coded.");
                break;
            case 9001004: {
                if (isHide() && applyfrom.isGM()) {
                    applyfrom.toggleHide(false, !applyfrom.isHidden());
                    return;
                }
                break;
            }
            case 35111013:
            case 5111007:
            case 5311005:
            case 5211007:
            case 15111011: //dice
            case 5711011:
            case 5311004: {//dice
                final int zz = Randomizer.nextInt(6) + 1;
                applyto.getMap().broadcastMessage(applyto, EffectPacket.showDiceEffect(applyto.getId(), sourceid, zz, -1, level), false);
                applyto.getClient().sendPacket(EffectPacket.showOwnDiceEffect(sourceid, zz, -1, level));
                if (zz <= 1) {
                    return;
                }
                localstatups = new EnumMap<>(MapleBuffStatus.class);
                localstatups.put(MapleBuffStatus.DICE_ROLL, zz);
                applyto.getClient().sendPacket(BuffPacket.giveDice(zz, sourceid, localDuration, localstatups));
                normal = false;
                showEffect = false;
                break;
            }
            case 5320007: {//dice
                final int zz = Randomizer.nextInt(6) + 1;
                final int zz2 = makeChanceResult() ? (Randomizer.nextInt(6) + 1) : 0;
                applyto.getMap().broadcastMessage(applyto, EffectPacket.showDiceEffect(applyto.getId(), sourceid, zz, zz2 > 0 ? -1 : 0, level), false);
                applyto.getClient().sendPacket(EffectPacket.showOwnDiceEffect(sourceid, zz, zz2 > 0 ? -1 : 0, level));
                if (zz <= 1 && zz2 <= 1) {
                    return;
                }
                final int buffid = zz == zz2 ? (zz * 100) : (zz <= 1 ? zz2 : (zz2 <= 1 ? zz : (zz * 10 + zz2)));
                if (buffid >= 100) { //just because of animation lol
                    applyto.dropMessage(-6, "[Double Lucky Dice] You have rolled a Double Down! (" + (buffid / 100) + ")");
                } else if (buffid >= 10) {
                    applyto.dropMessage(-6, "[Double Lucky Dice] You have rolled two dice. (" + (buffid / 10) + " and " + (buffid % 10) + ")");
                }
                localstatups = new EnumMap<>(MapleBuffStatus.class);
                localstatups.put(MapleBuffStatus.DICE_ROLL, buffid);
                applyto.getClient().sendPacket(BuffPacket.giveDice(zz, sourceid, localDuration, localstatups));
                normal = false;
                showEffect = false;
                break;
            }
            case 33101006: {//jaguar oshi
                applyto.clearLinkMid();
                MapleBuffStatus theBuff = null;
                int theStat = y;
                switch (Randomizer.nextInt(6)) {
                    case 0:
                        theBuff = MapleBuffStatus.CRITICAL_RATE_BUFF;
                        break;
                    case 1:
                        theBuff = MapleBuffStatus.MP_BUFF;
                        break;
                    case 2:
                        theBuff = MapleBuffStatus.DAMAGE_TAKEN_BUFF;
                        theStat = x;
                        break;
                    case 3:
                        theBuff = MapleBuffStatus.DODGE_CHANGE_BUFF;
                        theStat = x;
                        break;
                    case 4:
                        theBuff = MapleBuffStatus.DAMAGE_BUFF;
                        break;
                    case 5:
                        theBuff = MapleBuffStatus.ATTACK_BUFF;
                        break;
                }
                localstatups = new EnumMap<>(MapleBuffStatus.class);
                localstatups.put(theBuff, theStat);
                applyto.getClient().sendPacket(BuffPacket.giveBuff(sourceid, localDuration, localstatups, this));
                normal = false;
                break;
            }
            case 8006:
            case 10008006:
            case 20008006:
            case 20018006:
            case 20028006:
            case 30008006:
            case 30018006:
            case 5121009: // Speed Infusion
            case 15111005:
            case 5001005: // Dash
            case 4321000: //tornado spin
            case 15001003: {
                applyto.getClient().sendPacket(BuffPacket.givePirate(statups, localDuration / 1000, sourceid));
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignPirate(statups, localDuration / 1000, applyto.getId(), sourceid), false);
                normal = false;
                break;
            }
            case 5211006: // Homing Beacon
            case 22151002: //killer wings
            case 5220011: {// Bullseye
                if (applyto.getFirstLinkMid() > 0) {
                    applyto.getClient().sendPacket(BuffPacket.cancelHoming());
                    applyto.getClient().sendPacket(BuffPacket.giveHoming(sourceid, applyto.getFirstLinkMid(), 1));
                } else {
                    return;
                }
                normal = false;
                break;
            }
            case 2120010:
            case 2220010:
            case 2320011: //arcane aim
                if (applyto.getFirstLinkMid() > 0) {
                    applyto.getClient().sendPacket(BuffPacket.giveArcane(applyto.getAllLinkMid(), localDuration));
                } else {
                    return;
                }
                normal = false;
                break;
            case 30011001:
            case 30001001: { // Wind Walk
                if (applyto.isHidden()) {
                    break;
                }
                final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                stat.put(MapleBuffStatus.INFILTRATE, 0);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 13101006: { // Wind Walk
                if (applyto.isHidden()) {
                    break;
                }
                final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                stat.put(MapleBuffStatus.DARK_SIGHT, 1); // HACK..
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 4001003: {
                if (applyfrom.getTotalSkillLevel(4330001) > 0 && ((applyfrom.getJob() >= 430 && applyfrom.getJob() <= 434) || (applyfrom.getJob() == 400 && applyfrom.getSubcategory() == 1))) {
                    SkillFactory.getSkill(4330001).getEffect(applyfrom.getTotalSkillLevel(4330001)).applyBuffEffect(applyfrom, applyto, primary, newDuration);
                    return;
                } else{
                    final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                    stat.put(MapleBuffStatus.DARK_SIGHT, 0);
                    applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                    break;
                }
            }
            case 4330001:
            case 14001003: { // Dark Sight
                if (applyto.isHidden()) {
                    return; //don't even apply the buff
                }
                final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                stat.put(MapleBuffStatus.DARK_SIGHT, 0);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 23111005: {
                final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                stat.put(MapleBuffStatus.WATER_SHIELD, x);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 23101003: {
                final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                stat.put(MapleBuffStatus.SPIRIT_SURGE, x);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 22131001: {//magic shield
                final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                stat.put(MapleBuffStatus.MAGIC_SHIELD, x);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 32121003: { //twister
                if (applyto.isHidden()) {
                    break;
                }
                final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                stat.put(MapleBuffStatus.TORNADO, x);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 32111005: { //body boost
                applyto.cancelEffectFromBuffStat(MapleBuffStatus.BODY_BOOST);

                Pair<MapleBuffStatus, Integer> statt;
                int sourcez;
                if (applyfrom.getStatForBuff(MapleBuffStatus.DARK_AURA) != null) {
                    sourcez = 32001003;
                    statt = new Pair<>(MapleBuffStatus.DARK_AURA, level + 10 + applyto.getTotalSkillLevel(32001003)); //i think
                } else if (applyfrom.getStatForBuff(MapleBuffStatus.YELLOW_AURA) != null) {
                    sourcez = 32101003;
                    statt = new Pair<>(MapleBuffStatus.YELLOW_AURA, applyto.getTotalSkillLevel(32101003));
                } else if (applyfrom.getStatForBuff(MapleBuffStatus.BLUE_AURA) != null) {
                    sourcez = 32111012;
                    localDuration = 10000;
                    statt = new Pair<>(MapleBuffStatus.BLUE_AURA, applyto.getTotalSkillLevel(32111012));
                } else {
                    return;
                }
                localstatups = new EnumMap<>(MapleBuffStatus.class);
                localstatups.put(MapleBuffStatus.BODY_BOOST, (int) level);
                applyto.getClient().sendPacket(BuffPacket.giveBuff(sourceid, localDuration, localstatups, this));
                final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                stat.put(statt.left, statt.right);
                applyto.cancelEffectFromBuffStat(MapleBuffStatus.BLUE_AURA, applyfrom.getId());
                applyto.cancelEffectFromBuffStat(MapleBuffStatus.YELLOW_AURA, applyfrom.getId());
                applyto.cancelEffectFromBuffStat(MapleBuffStatus.DARK_AURA, applyfrom.getId());
                applyto.getClient().sendPacket(BuffPacket.giveBuff(sourcez, localDuration, stat, this));
                normal = false;
                break;
            }
            case 32001003: {//dark aura
                if (applyfrom.getTotalSkillLevel(32120000) > 0) {
                    SkillFactory.getSkill(32120000).getEffect(applyfrom.getTotalSkillLevel(32120000)).applyBuffEffect(applyfrom, applyto, primary, newDuration);
                    return;
                }
            }
            case 32110007:
            case 32120000: { // adv dark aura
                applyto.cancelEffectFromBuffStat(MapleBuffStatus.DARK_AURA);
                applyto.cancelEffectFromBuffStat(MapleBuffStatus.BLUE_AURA, applyfrom.getId());
                applyto.cancelEffectFromBuffStat(MapleBuffStatus.YELLOW_AURA, applyfrom.getId());
                applyto.cancelEffectFromBuffStat(MapleBuffStatus.BODY_BOOST);
                final EnumMap<MapleBuffStatus, Integer> statt = new EnumMap<>(MapleBuffStatus.class);
                statt.put(sourceid == 32110007 ? MapleBuffStatus.BODY_BOOST : MapleBuffStatus.AURA, (int) (sourceid == 32120000 ? applyfrom.getTotalSkillLevel(32001003) : level));
                applyto.getClient().sendPacket(BuffPacket.giveBuff(sourceid == 32120000 ? 32001003 : sourceid, localDuration, statt, this));
                statt.clear();
                statt.put(MapleBuffStatus.DARK_AURA, x);
                applyto.getClient().sendPacket(BuffPacket.giveBuff(sourceid, localDuration, statt, this));
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), statt, this), false);
                normal = false;
                break;
            }
            case 32111012: { // blue aura
                if (applyfrom.getTotalSkillLevel(32110000) > 0) {
                    SkillFactory.getSkill(32110000).getEffect(applyfrom.getTotalSkillLevel(32110000)).applyBuffEffect(applyfrom, applyto, primary, newDuration);
                    return;
                }
            }
            case 32110008: {
                localDuration = 10000;
            }
            case 32110000: { // advanced blue aura
                applyto.cancelEffectFromBuffStat(MapleBuffStatus.BLUE_AURA);
                applyto.cancelEffectFromBuffStat(MapleBuffStatus.DARK_AURA, applyfrom.getId());
                applyto.cancelEffectFromBuffStat(MapleBuffStatus.YELLOW_AURA, applyfrom.getId());
                applyto.cancelEffectFromBuffStat(MapleBuffStatus.BODY_BOOST);
                final EnumMap<MapleBuffStatus, Integer> statt = new EnumMap<>(MapleBuffStatus.class);
                statt.put(sourceid == 32110008 ? MapleBuffStatus.BODY_BOOST : MapleBuffStatus.AURA, (int) (sourceid == 32110000 ? applyfrom.getTotalSkillLevel(32111012) : level));
                applyto.getClient().sendPacket(BuffPacket.giveBuff(sourceid == 32110000 ? 32111012 : sourceid, localDuration, statt, this));
                statt.clear();
                statt.put(MapleBuffStatus.BLUE_AURA, (int) level);
                applyto.getClient().sendPacket(BuffPacket.giveBuff(sourceid, localDuration, statt, this));
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), statt, this), false);
                normal = false;
                break;
            }
            case 32101003: { // yellow aura
                if (applyfrom.getTotalSkillLevel(32120001) > 0) {
                    SkillFactory.getSkill(32120001).getEffect(applyfrom.getTotalSkillLevel(32120001)).applyBuffEffect(applyfrom, applyto, primary, newDuration);
                    return;
                }
            }
            case 32110009:
            case 32120001: { // advanced yellow aura
                applyto.cancelEffectFromBuffStat(MapleBuffStatus.YELLOW_AURA);
                applyto.cancelEffectFromBuffStat(MapleBuffStatus.BLUE_AURA, applyfrom.getId());
                applyto.cancelEffectFromBuffStat(MapleBuffStatus.DARK_AURA, applyfrom.getId());
                applyto.cancelEffectFromBuffStat(MapleBuffStatus.BODY_BOOST);
                final EnumMap<MapleBuffStatus, Integer> statt = new EnumMap<>(MapleBuffStatus.class);
                statt.put(sourceid == 32110009 ? MapleBuffStatus.BODY_BOOST : MapleBuffStatus.AURA, (int) (sourceid == 32120001 ? applyfrom.getTotalSkillLevel(32101003) : level));
                applyto.getClient().sendPacket(BuffPacket.giveBuff(sourceid == 32120001 ? 32101003 : sourceid, localDuration, statt, this));
                statt.clear();
                statt.put(MapleBuffStatus.YELLOW_AURA, (int) level);
                applyto.getClient().sendPacket(BuffPacket.giveBuff(sourceid, localDuration, statt, this));
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), statt, this), false);
                normal = false;
                break;
            }
            case 1211008: { //lightning
                if (applyto.getBuffedValue(MapleBuffStatus.WK_CHARGE) != null && applyto.getBuffSource(MapleBuffStatus.WK_CHARGE) != sourceid) {
                    localstatups = new EnumMap<>(MapleBuffStatus.class);
                    localstatups.put(MapleBuffStatus.LIGHTNING_CHARGE, 1);
                } else if (!applyto.isHidden()) {
                    final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                    stat.put(MapleBuffStatus.WK_CHARGE, 1);
                    applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                }
                applyto.getClient().sendPacket(BuffPacket.giveBuff(sourceid, localDuration, localstatups, this));
                normal = false;
                break;
            }
            case 35111004: {//siege
                if (applyto.getBuffedValue(MapleBuffStatus.MECH_CHANGE) != null && applyto.getBuffSource(MapleBuffStatus.MECH_CHANGE) == 35121005) {
                    SkillFactory.getSkill(35121013).getEffect(level).applyBuffEffect(applyfrom, applyto, primary, newDuration);
                    return;
                }
                if (applyto.isHidden()) {
                    break;
                }
                final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                stat.put(MapleBuffStatus.MECH_CHANGE, 1);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 35001001: //flame
            case 35101009:
            case 35121013:
            case 35121005: { //missile
                if (applyto.isHidden()) {
                    break;
                }
                final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                stat.put(MapleBuffStatus.MECH_CHANGE, 1);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 1220013: {
                if (applyto.isHidden()) {
                    break;
                }
                final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                stat.put(MapleBuffStatus.DIVINE_SHIELD, 1);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 1111002:
            case 11111001: { // Combo
                if (applyto.isHidden()) {
                    break;
                }
                final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                stat.put(MapleBuffStatus.COMBO, 0);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 3101004:
            case 3201004:
            case 13101003: { // Soul Arrow
                if (applyto.isHidden()) {
                    break;
                }
                final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                stat.put(MapleBuffStatus.SOULARROW, 0);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 2321005: //holy shield
                if (GameConstants.GMS) { //TODO JUMP
                    applyto.cancelEffectFromBuffStat(MapleBuffStatus.BLESS);
                }
                break;
            case 4211008:
            case 4331002:
            case 4111002:
            case 14111000: { // Shadow Partner
                if (applyto.isHidden()) {
                    break;
                }
                final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                stat.put(MapleBuffStatus.SHADOWPARTNER, x);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 15111006: { // Spark
                localstatups = new EnumMap<>(MapleBuffStatus.class);
                localstatups.put(MapleBuffStatus.SPARK, x);
                applyto.getClient().sendPacket(BuffPacket.giveBuff(sourceid, localDuration, localstatups, this));
                normal = false;
                break;
            }
            case 4341002: { // Final Cut
                localstatups = new EnumMap<>(MapleBuffStatus.class);
                localstatups.put(MapleBuffStatus.FINAL_CUT, y);
                applyto.getClient().sendPacket(BuffPacket.giveBuff(sourceid, localDuration, localstatups, this));
                normal = false;
                break;
            }
            case 3211005: {// golden eagle
                if (applyfrom.getTotalSkillLevel(3220005) > 0) {
                    SkillFactory.getSkill(3220005).getEffect(applyfrom.getTotalSkillLevel(3220005)).applyBuffEffect(applyfrom, applyto, primary, newDuration);
                }
                break;
            }
            case 3111005: {// golden hawk
                if (applyfrom.getTotalSkillLevel(3120006) > 0) {
                    SkillFactory.getSkill(3120006).getEffect(applyfrom.getTotalSkillLevel(3120006)).applyBuffEffect(applyfrom, applyto, primary, newDuration);
                }
                break;
            }
            case 1211006: // wk charges
            case 1211004:
            case 1221004:
            case 11111007:
            case 21101006:
            case 21111005:
            case 15101006: { // Soul Arrow
                if (applyto.isHidden()) {
                    break;
                }
                final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                stat.put(MapleBuffStatus.WK_CHARGE, 1);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 3120006:
            case 3220005: { // Spirit Link
                if (applyto.isHidden()) {
                    break;
                }
                final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                stat.put(MapleBuffStatus.SPIRIT_LINK, 0);
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 31121005: { // Dark Metamorphosis
                if (applyto.isHidden()) {
                    break;
                }
                final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                stat.put(MapleBuffStatus.DARK_METAMORPHOSIS, 6); // mob count
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                break;
            }
            case 2121004:
            case 2221004:
            case 2321004: { //Infinity
                maskedDuration = alchemistModifyVal(applyfrom, 4000, false);
                break;
            }
            case 4331003: { // Owl Spirit
                localstatups = new EnumMap<>(MapleBuffStatus.class);
                localstatups.put(MapleBuffStatus.OWL_SPIRIT, y);
                applyto.getClient().sendPacket(BuffPacket.giveBuff(sourceid, localDuration, localstatups, this));
                applyto.setBattleshipHP(x); //a variable that wouldnt' be used by a db
                normal = false;
                break;
            }
            case 1121010: // Enrage
                applyto.handleOrbconsume(10);
                break;
            case 2022746: //angel bless
            case 2022747: //d.angel bless
                if (applyto.isHidden()) {
                    break;
                }
                applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), maskedStatups == null ? localstatups : maskedStatups, this), false);
                break;
            case 35001002:
                if (applyfrom.getTotalSkillLevel(35120000) > 0) {
                    SkillFactory.getSkill(35120000).getEffect(applyfrom.getTotalSkillLevel(35120000)).applyBuffEffect(applyfrom, applyto, primary, newDuration);
                    return;
                }

                //fallthrough intended
            default:
                if (isPirateMorph()) {
                    final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                    stat.put(MapleBuffStatus.MORPH, getMorph(applyto));
                    applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                    applyto.getClient().sendPacket(BuffPacket.giveBuff(sourceid, localDuration, stat, this));
                    maskedStatups = new EnumMap<>(localstatups);
                    maskedStatups.remove(MapleBuffStatus.MORPH);
                    normal = false;
                } else if (isMorph()) {
                    if (applyto.isHidden()) {
                        break;
                    }
                    if (isIceKnight()) {
                        //odd
                        final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                        stat.put(MapleBuffStatus.ICE_KNIGHT, 2);
                        applyto.getClient().sendPacket(BuffPacket.giveBuff(0, localDuration, stat, this));
                    }
                    final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                    stat.put(MapleBuffStatus.MORPH, getMorph(applyto));
                    applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                } else if (isInflation()) {
                    if (applyto.isHidden()) {
                        break;
                    }
                    final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                    stat.put(MapleBuffStatus.GIANT_POTION, (int) inflation);
                    applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                } else if (charColor > 0) {
                    if (applyto.isHidden()) {
                        break;
                    }
                    final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                    stat.put(MapleBuffStatus.FAMILIAR_SHADOW, 1);
                    applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                } else if (isMonsterRiding()) {
                    localDuration = 2100000000;
                    localstatups = new EnumMap<>(statups);
                    localstatups.put(MapleBuffStatus.MONSTER_RIDING, 1);
                    final int mountid = parseMountInfo(applyto, sourceid);
                    final int mountid2 = parseMountInfo_Pure(applyto, sourceid);
                    if (mountid != 0 && mountid2 != 0) {
                        final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                        stat.put(MapleBuffStatus.MONSTER_RIDING, 0);
                        applyto.cancelEffectFromBuffStat(MapleBuffStatus.POWERGUARD);
                        applyto.cancelEffectFromBuffStat(MapleBuffStatus.MANA_REFLECTION);
                        applyto.getClient().sendPacket(BuffPacket.giveMount(mountid2, sourceid, stat));
                        applyto.getMap().broadcastMessage(applyto, BuffPacket.showMonsterRiding(applyto.getId(), stat, mountid, sourceid), false);
                    } else {
                        return;
                    }
                    normal = false;
                } else if (isSoaring()) {
                    if (applyto.isHidden()) {
                        break;
                    }
                    //  applyto.dropMessage(5, "Disabled..");
                    final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<MapleBuffStatus, Integer>(MapleBuffStatus.class);
                    stat.put(MapleBuffStatus.SOARING, 0);
                    applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                } else if (berserk > 0) {
                    if (applyto.isHidden()) {
                        break;
                    }
                    final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                    stat.put(MapleBuffStatus.PYRAMID_PQ, 0);
                    applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                } else if (isBerserkFury() || berserk2 > 0) {
                    if (applyto.isHidden()) {
                        break;
                    }
                    final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                    stat.put(MapleBuffStatus.BERSERK_FURY, 1);
                    applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                } else if (isDivineBody()) {
                    if (applyto.isHidden()) {
                        break;
                    }
                    final EnumMap<MapleBuffStatus, Integer> stat = new EnumMap<>(MapleBuffStatus.class);
                    stat.put(MapleBuffStatus.DIVINE_BODY, 1);
                    applyto.getMap().broadcastMessage(applyto, BuffPacket.giveForeignBuff(applyto.getId(), stat, this), false);
                }
                break;
        }
        if (showEffect && !applyto.isHidden()) {
            applyto.getMap().broadcastMessage(applyto, EffectPacket.showBuffeffect(applyto.getId(), sourceid, 1, applyto.getLevel(), level), false);
        }
        WeakReference<MapleCharacter>[] clones = applyto.getClones();
        final MapleMap map = applyto.getMap();
        for (int i = 0; i < clones.length; i++) {
            if (clones[i].get() != null) {
                final MapleCharacter clone = clones[i].get();
                if (clone.getMap() == map && clone.getDragon() != null) {
                    if (clone.isHidden()) {
                        map.broadcastGMMessage(clone, EffectPacket.showBuffeffect(clone.getId(), sourceid, 1, clone.getLevel(), level), false);
                    } else {
                        map.broadcastMessage(clone, EffectPacket.showBuffeffect(clone.getId(), sourceid, 1, clone.getLevel(), level), false);
                    }
                }
            }
        }
        if (isMechPassive()) {
            applyto.getClient().sendPacket(EffectPacket.showOwnBuffEffect(sourceid - 1000, 1, applyto.getLevel(), level, (byte) 1));
        }
        if (!isMonsterRiding() && !isMechDoor()) {
            applyto.cancelEffect(this, true, -1, localstatups);
        }
        // Broadcast statEffect to self
        if (normal && localstatups.size() > 0) {
            applyto.getClient().sendPacket(BuffPacket.giveBuff((skill ? sourceid : -sourceid), localDuration, maskedStatups == null ? localstatups : maskedStatups, this));
        }
        final long starttime = System.currentTimeMillis();
        final CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime, localstatups);
        final ScheduledFuture<?> schedule = BuffTimer.getInstance().schedule(cancelAction, maskedDuration > 0 ? maskedDuration : localDuration);
        applyto.registerEffect(this, starttime, schedule, localstatups, false, localDuration, applyfrom.getId());
    }

    private int calcHPChange(final MapleCharacter applyfrom, final boolean primary) {
        int hpchange = 0;
        if (hp != 0) {
            if (!skill) {
                if (primary) {
                    hpchange += alchemistModifyVal(applyfrom, hp, true);
                } else {
                    hpchange += hp;
                }
                if (applyfrom.hasDisease(MapleBuffStatus.ZOMBIFY)) {
                    hpchange /= 2;
                }
            } else { // assumption: this is heal
                hpchange += makeHealHP(hp / 100.0, applyfrom.getStat().getTotalMagic(), 3, 5);
                if (applyfrom.hasDisease(MapleBuffStatus.ZOMBIFY)) {
                    hpchange = -hpchange;
                }
            }
        }
        if (hpR != 0) {
            hpchange += (int) (applyfrom.getStat().getCurrentMaxHp() * hpR) / (applyfrom.hasDisease(MapleBuffStatus.ZOMBIFY) ? 2 : 1);
        }
        // actually receivers probably never get any hp when it's not heal but whatever
        if (primary) {
            if (hpCon != 0) {
                hpchange -= hpCon;
            }
        }
        switch (this.sourceid) {
            case 4211001: // Chakra
                final PlayerStats stat = applyfrom.getStat();
                int v42 = getY() + 100;
                int v38 = Randomizer.rand(1, 100) + 100;
                hpchange = (int) ((v38 * stat.getLuk() * 0.033 + stat.getDex()) * v42 * 0.002);
                hpchange += makeHealHP(getY() / 100.0, applyfrom.getStat().getTotalLuk(), 2.3, 3.5);
                break;
        }
        return hpchange;
    }

    private int calcMPChange(final MapleCharacter applyfrom, final boolean primary) {
        int mpchange = 0;
        if (mp != 0) {
            if (primary) {
                mpchange += alchemistModifyVal(applyfrom, mp, false); // recovery up doesn't apply for mp
            } else {
                mpchange += mp;
            }
        }
        if (mpR != 0) {
            mpchange += (int) (applyfrom.getStat().getCurrentMaxMp(applyfrom.getJob()) * mpR);
        }
        if (GameConstants.isDemon(applyfrom.getJob())) {
            mpchange = 0;
        }
        if (primary) {
            if (mpCon != 0 && !GameConstants.isDemon(applyfrom.getJob())) {
                if (applyfrom.getBuffedValue(MapleBuffStatus.INFINITY) != null) {
                    mpchange = 0;
                } else {
                    mpchange -= (mpCon - (mpCon * applyfrom.getStat().mpconReduce / 100)) * (applyfrom.getStat().mpconPercent / 100.0);
                }
            } else if (forceCon != 0 && GameConstants.isDemon(applyfrom.getJob())) {
                if (applyfrom.getBuffedValue(MapleBuffStatus.BOUNDLESS_RAGE) != null) {
                    mpchange = 0;
                } else {
                    mpchange -= forceCon;
                }
            }
        }

        return mpchange;
    }

    public final int alchemistModifyVal(final MapleCharacter chr, final int val, final boolean withX) {
        if (!skill) { // RecoveryUP only used for hp items and skills
            return (val * (100 + (withX ? chr.getStat().RecoveryUP : chr.getStat().BuffUP)) / 100);
        }
        return (val * (100 + (withX ? chr.getStat().RecoveryUP : (chr.getStat().BuffUP_Skill + (getSummonMovementType() == null ? 0 : chr.getStat().BuffUP_Summon)))) / 100);
    }

    public final boolean isGmBuff() {
        switch (sourceid) {
            case 1005: // Echo of Hero
            case 10001075: //Empress Prayer
            case 9001000: // GM dispel
            case 9001001: // GM haste
            case 9001002: // GM Holy Symbol
            case 9001003: // GM Bless
            case 9001005: // GM resurrection
            case 9001008: // GM Hyper body

            case 9101000:
            case 9101001:
            case 9101002:
            case 9101004:
            case 9101003:
            case 9101005:
            case 9101008:
                return true;
            default:
                return GameConstants.isBeginnerJob(sourceid / 10000) && sourceid % 10000 == 1005;
        }
    }

    public final boolean isInflation() {
        return inflation > 0;
    }

    public final int getInflation() {
        return inflation;
    }

    public final boolean isEnergyCharge() {
        return skill && (sourceid == 5110001 || sourceid == 15100004);
    }

    public boolean isMonsterBuff() {
        switch (sourceid) {
            case 1201006: // threaten
            case 2101003: // fp slow
            case 2201003: // il slow
            case 5011002:
            case 12101001: // cygnus slow
            case 2211004: // il seal
            case 2111004: // fp seal
            case 12111002: // cygnus seal
            case 2311005: // doom
            case 4111003: // shadow web
            case 14111001: // cygnus web
            case 4121004: // Ninja ambush
            case 4221004: // Ninja ambush
            case 22151001:
            case 22121000:
            case 22161002:
            case 4321002:
            case 4341003:
            case 90001002:
            case 90001003:
            case 90001004:
            case 90001005:
            case 90001006:
            case 1111007:
            case 1211009:
            case 1311007:
            case 51111005:
            case 35111005:
            case 32120000:
            case 32120001:
                return skill;
        }
        return false;
    }

    private boolean isPartyBuff() {
        if (lt == null || rb == null || !partyBuff) {
            return isSoulStone();
        }
        switch (sourceid) {
            case 1211003:
            case 1211004:
            case 1211005:
            case 1211006:
            case 1211007:
            case 1211008:
            case 1221003:
            case 1221004:
            case 11111007:
            case 12101005:
            case 4311001:
            case 4331003:
            case 4341002:
            case 35121005:
                return false;
        }
        if (GameConstants.isNoDelaySkill(sourceid)) {
            return false;
        }
        return true;
    }

    public final void setPartyBuff(boolean pb) {
        this.partyBuff = pb;
    }

    public final boolean isArcane() {
        return skill && (sourceid == 2320011 || sourceid == 2220010 || sourceid == 2120010);
    }

    public final boolean isHeal() {
        return skill && (sourceid == 2301002 || sourceid == 9101000 || sourceid == 9001000);
    }

    public final boolean isResurrection() {
        return skill && (sourceid == 9001005 || sourceid == 9101005 || sourceid == 2321006);
    }

    public final boolean isTimeLeap() {
        return skill && sourceid == 5121010;
    }

    public final short getHp() {
        return hp;
    }

    public final short getMp() {
        return mp;
    }

    public final double getHpR() {
        return hpR;
    }

    public final double getMpR() {
        return mpR;
    }

    public final byte getMastery() {
        return mastery;
    }

    public final short getWatk() {
        return watk;
    }

    public final short getMatk() {
        return matk;
    }

    public final short getWdef() {
        return wdef;
    }

    public final short getMdef() {
        return mdef;
    }

    public final short getAcc() {
        return acc;
    }

    public final short getAvoid() {
        return avoid;
    }

    public final short getHands() {
        return hands;
    }

    public final short getSpeed() {
        return speed;
    }

    public final short getJump() {
        return jump;
    }

    public final short getPassiveSpeed() {
        return psdSpeed;
    }

    public final short getPassiveJump() {
        return psdJump;
    }

    public final int getDuration() {
        return duration;
    }

    public final void setDuration(int d) {
        this.duration = d;
    }

    public final int getSubTime() {
        return subTime;
    }

    public final boolean isOverTime() {
        return overTime;
    }

    public final Map<MapleBuffStatus, Integer> getStatups() {
        return statups;
    }

    public final boolean sameSource(final MapleStatEffect effect) {
        boolean sameSrc = this.sourceid == effect.sourceid;
        switch (this.sourceid) { // All these are passive skills, will have to cast the normal ones.
            case 32120000: // Advanced Dark Aura
                sameSrc = effect.sourceid == 32001003;
                break;
            case 32110000: // Advanced Blue Aura
                sameSrc = effect.sourceid == 32111012;
                break;
            case 32120001: // Advanced Yellow Aura
                sameSrc = effect.sourceid == 32101003;
                break;
            case 35120000: // Extreme Mech
                sameSrc = effect.sourceid == 35001002;
                break;
            case 35121013: // Mech: Siege Mode
                sameSrc = effect.sourceid == 35111004;
                break;
        }
        return effect != null && sameSrc && this.skill == effect.skill;
    }

    public final int getCr() {
        return cr;
    }

    public final int getT() {
        return t;
    }

    public final int getU() {
        return u;
    }

    public final int getV() {
        return v;
    }

    public final int getW() {
        return w;
    }

    public final int getX() {
        return x;
    }

    public final int getY() {
        return y;
    }

    public final int getZ() {
        return z;
    }

    public final short getDamage() {
        return damage;
    }

    public final short getPVPDamage() {
        return PVPdamage;
    }

    public final byte getAttackCount() {
        return attackCount;
    }

    public final byte getBulletCount() {
        return bulletCount;
    }

    public final int getBulletConsume() {
        return bulletConsume;
    }

    public final byte getMobCount() {
        return mobCount;
    }

    public final int getMoneyCon() {
        return moneyCon;
    }

    public final int getCooldown(final MapleCharacter chra) {
//        if (chra.isGM()) {
//            return Math.max(0, 0);
//        }
        return Math.max(0, (cooldown - chra.getStat().reduceCooltime));
    }

    public final Map<MonsterStatus, Integer> getMonsterStati() {
        return monsterStatus;
    }

    public final int getBerserk() {
        return berserk;
    }

    public final boolean isHide() {
        return skill && (sourceid == 9001004 || sourceid == 9101004);
    }

    public final boolean isDragonBlood() {
        return skill && sourceid == 1311008;
    }

    public final boolean isRecovery() {
        return skill && (sourceid == 1001 || sourceid == 10001001 || sourceid == 20001001 || sourceid == 20011001 || sourceid == 20021001 || sourceid == 11001 || sourceid == 35121005);
    }

    public final boolean isBerserk() {
        return skill && sourceid == 1320006;
    }

    public final boolean isBeholder() {
        return skill && sourceid == 1321007;
    }

    public final boolean isMPRecovery() {
        return skill && sourceid == 5101005;
    }

    public final boolean isInfinity() {
        return skill && (sourceid == 2121004 || sourceid == 2221004 || sourceid == 2321004);
    }

    public final boolean isBadSkill() {
        return sourceid == 5211014 || sourceid == 32121003 || sourceid == 35121013 || sourceid == 35111004 || sourceid == 35121003 || sourceid == 1320006 || sourceid == 32111006 || sourceid == 13111004 || sourceid == 3220012 || sourceid == 3211002 || sourceid == 3111002 || sourceid == 3120012 || sourceid == 12111004 || sourceid == 13001004 || sourceid == 14001004 || sourceid == 15001004 || sourceid == 12001004 || sourceid == 11001004 || sourceid == 2121005 || sourceid == 2221005 || sourceid == 2321003 || sourceid == 3211005 || sourceid == 3111005 || sourceid == 35121009 || sourceid == 5311004 || sourceid == 5321004 || sourceid == 5321003 || sourceid == 2321004 || sourceid == 2121004 || sourceid == 2221004 || sourceid == 86 || sourceid == 91 || sourceid == 30000091 || sourceid == 30000086 || sourceid == 20010091 || sourceid == 20010086 || sourceid == 20001090 || sourceid == 20001085 || sourceid == 10001085 || sourceid == 10001090 || sourceid == 1090 || sourceid == 1085 || sourceid == 30001085 || sourceid == 30001090 || sourceid == 20011085 || sourceid == 20011090 || sourceid == 10000091 || sourceid == 10000086 || sourceid == 20000091 || sourceid == 20000086;
    }

    public final boolean isMonsterRiding_() {
        return skill && (sourceid == 1004 || sourceid == 10001004 || sourceid == 20001004 || sourceid == 20011004 || sourceid == 11004 || sourceid == 20021004 || sourceid == 80001000);
    }

    public final boolean isMonsterRiding() {
        return skill && (isMonsterRiding_() || GameConstants.getMountItem(sourceid, null) != 0);
    }

    public final boolean isMagicDoor() {
        return skill && (sourceid == 2311002 || sourceid % 10000 == 8001);
    }

    public final boolean isMesoGuard() {
        return skill && sourceid == 4211005;
    }

    public final boolean isMechDoor() {
        return skill && sourceid == 35101005;
    }

    public final boolean isComboRecharge() {
        return skill && sourceid == 21111009;
    }

    public final boolean isDragonBlink() {
        return skill && sourceid == 22141004;
    }

    public final boolean isCharge() {
        switch (sourceid) {
            case 1211003:
            case 1211008:
            case 11111007:
            case 12101005:
            case 15101006:
            case 21111005:
                return skill;
        }
        return false;
    }

    public final boolean isPoison() {
        return dot > 0 && dotTime > 0;
    }

    private boolean isMist() {
        return skill && (sourceid == 2111003 || sourceid == 4221006 || sourceid == 12111005 || sourceid == 14111006 || sourceid == 22161003 || sourceid == 32121006 || sourceid == 1076 || sourceid == 11076); // poison mist, smokescreen and flame gear, recovery aura
    }

    private boolean isSpiritClaw() {
        return sourceid == 4111009 || sourceid == 5201008;
    }

    private boolean isDispel() {
        return skill && (sourceid == 2311001 || sourceid == 9001000 || sourceid == 9101000);
    }

    private boolean isHeroWill() {
        switch (sourceid) {
            case 1121011:
            case 1221012:
            case 1321010:
            case 2121008:
            case 2221008:
            case 2321009:
            case 3121009:
            case 3221008:
            case 4121009:
            case 4221008:
            case 5121008:
            case 5221010:
            case 21121008:
            case 22171004:
            case 4341008:
            case 32121008:
            case 33121008:
            case 35121008:
            case 5321008:
            case 23121008:
            case 24121009:
                return skill;
        }
        return false;
    }

    public final boolean isAranCombo() {
        return sourceid == 21000000;
    }

    public final boolean isCombo() {
        switch (sourceid) {
            case 1111002:
            case 11111001: // Combo
                return skill;
        }
        return false;
    }

    public final boolean isPirateMorph() {
        switch (sourceid) {
            case 13111005:
            case 15111002:
            case 5111005:
            case 5121003:
                return skill;
        }
        return false;
    }

    public final boolean isMorph() {
        return morphId > 0;
    }

    public final int getMorph() {
        switch (sourceid) {
            case 15111002:
            case 5111005:
                return 1000;
            case 5121003:
                return 1001;
            case 5101007:
                return 1002;
            case 13111005:
                return 1003;
        }
        return morphId;
    }

    public final boolean isDivineBody() {
        return skill && GameConstants.isBeginnerJob(sourceid / 10000) && sourceid % 10000 == 1010;
    }

    public final boolean isDivineShield() {
        switch (sourceid) {
            case 1220013:
                return skill;
        }
        return false;
    }

    public final boolean isBerserkFury() {
        return skill && GameConstants.isBeginnerJob(sourceid / 10000) && sourceid % 10000 == 1011;
    }

    public final int getMorph(final MapleCharacter chr) {
        final int morph = getMorph();
        switch (morph) {
            case 1000:
            case 1001:
            case 1003:
                return morph + (chr.getGender() == 1 ? 100 : 0);
        }
        return morph;
    }

    public final byte getLevel() {
        return level;
    }

    public final SummonMovementType getSummonMovementType() {
        if (!skill) {
            return null;
        }
        switch (sourceid) {
            case 3211002: // puppet sniper
            case 3111002: // puppet ranger
            case 33111003:
            case 13111004: // puppet cygnus
            case 5211014: // octopus - pirate
            case 5220002: // advanced octopus - pirate
            case 4341006:
            case 35111002:
            case 35111005:
            case 35111011:
            case 35121009:
            case 35121010:
            case 35121011:
            case 5711001:
            case 4111007: //dark flare
            case 4211007: //dark flare
            case 33101008:
            case 35121003:
            case 3120012:
            case 3220012:
            case 5321003:
                return SummonMovementType.STATIONARY;
            case 3211005: // golden eagle
            case 3111005: // golden hawk
            case 3101007:
            case 3201007:
            case 33111005:
            case 3221005: // frostprey
            case 3121006: // phoenix
            case 23111008:
            case 23111009:
            case 23111010:
                return SummonMovementType.CIRCLE_FOLLOW;
            case 5211002: // bird - pirate
                return SummonMovementType.CIRCLE_STATIONARY;
            case 32111006: //reaper
            case 5211011:
            case 5211015:
            case 5211016:
                return SummonMovementType.WALK_STATIONARY;
            case 1321007: // beholder
            case 2121005: // elquines
            case 2221005: // ifrit
            case 2321003: // bahamut
            case 12111004: // Ifrit
            case 11001004: // soul
            case 12001004: // flame
            case 13001004: // storm
            case 14001005: // darkness
            case 15001004: // lightning
            case 35111001:
            case 35111010:
            case 35111009:
            case 5321004:
                return SummonMovementType.FOLLOW;
        }
        if (isAngel()) {
            return SummonMovementType.FOLLOW;
        }
        return null;
    }

    public final boolean isAngel() {
        return GameConstants.isAngel(sourceid);
    }

    public final boolean isSkill() {
        return skill;
    }

    public final int getSourceId() {
        return sourceid;
    }

    public final void setSourceId(final int newid) {
        sourceid = newid;
    }

    public final boolean isIceKnight() {
        return skill && GameConstants.isBeginnerJob(sourceid / 10000) && sourceid % 10000 == 1105;
    }

    public final boolean isSoaring() {
        return isSoaring_Normal() || isSoaring_Mount();
    }

    public final boolean isSoaring_Normal() {
        return skill && GameConstants.isBeginnerJob(sourceid / 10000) && sourceid % 10000 == 1026;
    }

    public final boolean isSoaring_Mount() {
        return skill && ((GameConstants.isBeginnerJob(sourceid / 10000) && sourceid % 10000 == 1142) || sourceid == 80001089);
    }

    public final boolean isFinalAttack() {
        switch (sourceid) {
            case 11101002:
            case 13101002:
            case 21120002:
                return skill;
        }
        return false;
    }

    public final boolean isMistEruption() {
        switch (sourceid) {
            case 2121003:
                return skill;
        }
        return false;
    }

    public final boolean isShadow() {
        switch (sourceid) {
            case 4111002: // shadowpartner
            case 14111000: // cygnus
            case 4211008:
                return skill;
        }
        return false;
    }

    public final boolean isMI() {
        switch (sourceid) {
            case 4331002: // shadowpartner
                return skill;
        }
        return false;
    }


    public final boolean isMechPassive() {
        switch (sourceid) {
            //case 35121005:
            case 35121013:
                return true;
        }
        return false;
    }

    /**
     * @return true if the statEffect should happen based on it's probablity, false otherwise
     */
    public final boolean makeChanceResult() {
        return prop >= 100 || Randomizer.nextInt(100) < prop;
    }

    public final short getProb() {
        return prop;
    }

    public final short getIgnoreMob() {
        return ignoreMob;
    }

    public final int getEnhancedHP() {
        return ehp;
    }

    public final int getEnhancedMP() {
        return emp;
    }

    public final int getEnhancedWatk() {
        return ewatk;
    }

    public final int getEnhancedWdef() {
        return ewdef;
    }

    public final int getEnhancedMdef() {
        return emdef;
    }

    public final short getDOT() {
        return dot;
    }

    public final short getDOTTime() {
        return dotTime;
    }

    public final short getCriticalMax() {
        return criticaldamageMax;
    }

    public final short getCriticalMin() {
        return criticaldamageMin;
    }

    public final short getASRRate() {
        return asrR;
    }

    public final short getTERRate() {
        return terR;
    }

    public final short getDAMRate() {
        return damR;
    }

    public final short getMesoRate() {
        return mesoR;
    }

    public final int getEXP() {
        return exp;
    }

    public final short getAttackX() {
        return padX;
    }

    public final short getMagicX() {
        return madX;
    }

    public final int getPercentHP() {
        return mhpR;
    }

    public final int getPercentMP() {
        return mmpR;
    }

    public final int getConsume() {
        return consumeOnPickup;
    }

    public final int getSelfDestruction() {
        return selfDestruction;
    }

    public final int getCharColor() {
        return charColor;
    }

    public final List<Integer> getPetsCanConsume() {
        return petsCanConsume;
    }

    public final boolean isReturnScroll() {
        return skill && (sourceid == 80001040 || sourceid == 20021110);
    }

    public final boolean isMechChange() {
        switch (sourceid) {
            case 35111004: //siege
            case 35001001: //flame
            case 35101009:
            case 35121013:
            case 35121005:
                return skill;
        }
        return false;
    }

    public final int getRange() {
        return range;
    }

    public final short getER() {
        return er;
    }

    public final int getPrice() {
        return price;
    }

    public final int getExtendPrice() {
        return extendPrice;
    }

    public final byte getPeriod() {
        return period;
    }

    public final byte getReqGuildLevel() {
        return reqGuildLevel;
    }

    public final byte getEXPRate() {
        return expR;
    }

    public final short getLifeID() {
        return lifeId;
    }

    public final short getUseLevel() {
        return useLevel;
    }

    public final byte getSlotCount() {
        return slotCount;
    }

    public final short getStr() {
        return str;
    }

    public final short getStrX() {
        return strX;
    }

    public final short getDex() {
        return dex;
    }

    public final short getDexX() {
        return dexX;
    }

    public final short getInt() {
        return int_;
    }

    public final short getIntX() {
        return intX;
    }

    public final short getLuk() {
        return luk;
    }

    public final short getLukX() {
        return lukX;
    }

    public final short getMPConReduce() {
        return mpConReduce;
    }

    public final short getIndieMHp() {
        return indieMhp;
    }

    public final short getIndieMMp() {
        return indieMmp;
    }

    public final short getIndieAllStat() {
        return indieAllStat;
    }

    public final byte getType() {
        return type;
    }

    public int getBossDamage() {
        return bdR;
    }

    public int getInterval() {
        return interval;
    }

    public ArrayList<Pair<Integer, Integer>> getAvailableMaps() {
        return availableMap;
    }

    public short getWDEFRate() {
        return pddR;
    }

    public short getMDEFRate() {
        return mddR;
    }

    public String getName() {
        String Name = "";
        try {
            Name = SkillFactory.getSkillName(getSourceId());
        } catch (Exception localException) {
        }
        return Name;
    }

    public final boolean isUnstealable() {
        for (MapleBuffStatus b : statups.keySet()) {
            if (b == MapleBuffStatus.MAPLE_WARRIOR) {
                return true;
            }
        }
        return sourceid == 4221013;
    }

    public static class CancelEffectAction implements Runnable {

        private final MapleStatEffect effect;
        private final WeakReference<MapleCharacter> target;
        private final long startTime;
        private final Map<MapleBuffStatus, Integer> statup;

        public CancelEffectAction(final MapleCharacter target, final MapleStatEffect effect, final long startTime, final Map<MapleBuffStatus, Integer> statup) {
            this.effect = effect;
            this.target = new WeakReference<>(target);
            this.startTime = startTime;
            this.statup = statup;
        }

        @Override
        public void run() {
            final MapleCharacter realTarget = target.get();
            if (realTarget != null) {
                realTarget.cancelEffect(effect, false, startTime, statup);
            }
        }
    }

}
