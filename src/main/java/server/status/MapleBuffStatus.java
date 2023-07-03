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
package server.status;

import constants.GameConstants;
import tools.ExternalCodeLongTableGetter;
import tools.StringUtil;
import tools.WritableLongValueHolder;

import java.io.*;
import java.util.Properties;

public enum MapleBuffStatus implements WritableLongValueHolder, IBuffStat {

    //物理攻擊力
    WATK(0),
    //物理防禦力
    WDEF(1),
    //魔法攻擊力
    MATK(2),
    //魔法防禦力
    MDEF(3),
    //命中率
    ACC(4),
    //迴避率
    AVOID(5),
    //手技
    HANDS(6),
    //移動速度
    SPEED(7),
    //跳躍力
    JUMP(8),
    //魔心防禦
    MAGIC_GUARD(9),
    //隱藏術
    DARK_SIGHT(10),
    //攻擊加速
    BOOSTER(11),
    //反射之盾
    POWERGUARD(12),
    //最大HP
    MAXHP(13),
    //最大MP
    MAXMP(14),
    //神聖之光
    INVINCIBLE(15),
    //無形之箭
    SOULARROW(16),
    // MapleDisease
    //昏迷
    STUN(17),
    //中毒
    POISON(18),
    //封印
    SEAL(19),
    //黑暗
    DARKNESS(20),
    //鬥氣集中
    COMBO(21),
    //召喚獸
    SUMMON(21),
    //屬性攻擊
    WK_CHARGE(22),
    //龍之魂
    DRAGONBLOOD(23),
    //神聖祈禱
    HOLY_SYMBOL(24),
    //幸運術
    MESOUP(25),
    //影分身
    SHADOWPARTNER(26),
    //勇者掠奪術
    PICKPOCKET(27),
    //替身術
    PUPPET(28),
    //楓幣護盾
    MESOGUARD(29),

    // MapleDisease
    //虛弱
    WEAKEN(30),
    //詛咒
    CURSE(31),
    //緩慢
    SLOW(32),
    //變身

    MORPH(33),
    //恢復
    RECOVERY(34),
    //楓葉祝福
    MAPLE_WARRIOR(35),
    //格擋(穩如泰山)
    STANCE(36),
    //銳利之眼
    SHARP_EYES(37),
    //魔法反擊
    MANA_REFLECTION(38),
    //誘惑
    SEDUCE(39),
    //暗器傷人
    SPIRIT_CLAW(40),
    //魔力無限
    INFINITY(41),
    //進階祝福
    HOLY_SHIELD(42),
    //敏捷提升
    HAMSTRING(43),
    //命中率增加
    BLIND(44),
    //集中精力
    CONCENTRATE(45),
    //不死化
    ZOMBIFY(46),
    //英雄的回響
    ECHO_OF_HERO(47),
    //
    UNKNOWN3(48),
    // 鬼魂變身
    GHOST_MORPH(49),
    // 狂暴戰魂
    ARIANT_COSS_IMU(50),
    //
    REVERSE_DIRECTION(51),
    // 掉寶倍率
    DROP_RATE(52),
    // 楓幣倍率
    MESO_RATE(53),
    // 經驗倍率
    EXPRATE(54),
    // GASH倍率
    ACASH_RATE(55),
    // 終極隱身
    GM_HIDE(56),
    // 地火天爆
    BERSERK_FURY(57),
    // 龍魔島-四連殺
    ILLUSION(58),
    // 閃光擊
    SPARK(59),
    // 金剛霸體
    DIVINE_BODY(60),
    // 近距離終極攻擊[完成]
    FINAL_MELEE_ATTACK(61),
    // 遠距離終極攻擊[完成]
    FINAL_SHOOT_ATTACK(62),
    // 風影漫步
    WIND_WALK(64),
    // 自然力重置
    ELEMENT_RESET(63),
    // 矛之鬥氣
    ARAN_COMBO(66), //68
    // 連環吸血
    COMBO_DRAIN(67), //69
    // 宙斯之盾
    COMBO_BARRIER(68), //70
    // 強化連擊
    BODY_PRESSURE(69), //71
    // 精準擊退
    SMART_KNOCKBACK(70), //72

    PYRAMID_PQ(71),
    MAGIC_SHIELD(77),
    MAGIC_RESISTANCE(78),
    // 靈魂之石
    SOUL_STONE(79), //same as pyramid_pq
    // 飛天騎乘
    SOARING(80),
    // 冰凍
    FREEZE(81),
    // 雷鳴之劍
    LIGHTNING_CHARGE(82),
    // 替身術
    MIRROR_IMAGE(83),
    // 貓頭鷹召喚
    OWL_SPIRIT(84),
    //幻影替身
    MIRROR_TARGET(84),
    //絕殺刃
    FINAL_CUT(86),
    //荊棘特效
    THORNS(87),
    BUFF_72(72),
    POTION(73),
    SHADOW(74),
    BUFF_75(75),
    WEIRD_FLAME(76),
    BUFF_76(76),
    BUFF_77(77),
    BUFF_78(78),
    BUFF_79(79),
    BUFF_80(80),
    BUFF_81(81),
    BUFF_82(82),
    BUFF_83(83),
    BUFF_84(84),
    ENRAGE(85),
    DAMAGE_BUFF(88),
    ATTACK_BUFF(89), //attack %? feline berserk
    RAINING_MINES(90),
    ENHANCED_MAXHP(91),
    ENHANCED_MAXMP(92),
    ENHANCED_WATK(93),
    //增強_魔法攻擊力
    ENHANCED_MATK(94),
    //增強_物理防禦力
    ENHANCED_WDEF(95),
    //增強_魔法防禦力
    ENHANCED_MDEF(96),
    //全備型盔甲
    PERFECT_ARMOR(97),
    //終極賽特拉特_PROC
    SATELLITESAFE_PROC(98),
    //終極賽特拉特_吸收
    SATELLITESAFE_ABSORB(99),
    //颶風
    TORNADO(100),
    //咆哮_會心一擊機率增加
    CRITICAL_RATE_BUFF(101),
    //咆哮_MaxMP 增加
    MP_BUFF(102),
    //咆哮_傷害減少
    DAMAGE_TAKEN_BUFF(103),
    //咆哮_迴避機率
    DODGE_CHANGE_BUFF(104),
    CONVERSION(106),
    //甦醒
    REAPER(106),
    INFILTRATE(107),
    MECH_CHANGE(108),
    AURA(109),
    DARK_AURA(110),
    BLUE_AURA(111),
    YELLOW_AURA(112),
    BODY_BOOST(113),
    FELINE_BERSERK(114),
    DICE_ROLL(115),
    DIVINE_SHIELD(116),
    PIRATES_REVENGE(117),
    TELEPORT_MASTERY(119),
    COMBAT_ORDERS(118), // 戰鬥命令 完成
    BEHOLDER(120),
    BUFF_121(121),
    GIANT_POTION(122),
    ONYX_SHROUD(123),
    ONYX_WILL(124),
    BUFF_125(125),
    BLESS(126),
    BUFF_127(127),
    BUFF_128(128),
    //2 debuff	 but idk
    THREATEN_PVP(129),
    ICE_KNIGHT(130),
    BUFF_131(131),
    STR(132),
    INT(133),
    DEX(134),
    LUK(135),
    BUFF_132(136),
    BUFF_137(137),
    BUFF_138(138),
    ANGEL_ATK(139, true),
    ANGEL_MATK(140, true),
    HP_BOOST(141, true), //indie hp
    MP_BOOST(142, true),
    ANGEL_ACC(143, true),
    ANGEL_AVOID(144, true),
    ANGEL_JUMP(145, true),
    ANGEL_SPEED(146, true),
    ANGEL_STAT(147, true),
    ITEM_EFFECT(148),
    PVP_DAMAGE(149),
    PVP_ATTACK(150), //skills
    INVINCIBILITY(151),
    HIDDEN_POTENTIAL(152),
    ELEMENT_WEAKEN(153),
    SNATCH(154), //however skillid is 90002000, 1500 duration
    FROZEN(155),
    BUFF_156(156),
    ICE_SKILL(157),
    BUFF_158(158),
    BOUNDLESS_RAGE(159),
    BUFF_160(160),
    BUFF_161(161),
    BUFF_162(162),
    BUFF_163(163),
    HOLY_MAGIC_SHELL(164), //max amount of attacks absorbed
    BUFF_165(165),
    ARCANE_AIM(166, true),
    BUFF_MASTERY(171),
    ABNORMAL_STATUS_R(168), // %
    ELEMENTAL_STATUS_R(169), // %
    WATER_SHIELD(170),
    DARK_METAMORPHOSIS(167), //惡魔殺手的變形 萬成
    BUFF_172(172),
    SPIRIT_SURGE(173),
    SPIRIT_LINK(174, true),
    BUFF_175(175),
    VIRTUE_EFFECT(176),
    BUFF_176(176),
    BUFF_177(177),
    CRITICAL_RATE(178),
    NO_SLIP(179),
    FAMILIAR_SHADOW(180),
    BUFF_184(184),
    BUFF_185(185),
    BUFF_186(186),
    BUFF_187(187),
    BUFF_188(188),
    BUFF_189(189),
    DEFENCE_BOOST_R(190),
    BUFF_191(191),
    BUFF_192(192),
    BUFF_193(193),
    BUFF_194(194),
    BUFF_195(195),
    BUFF_196(196),
    BUFF_197(197),
    BUFF_198(198),
    BUFF_199(199),
    BUFF_200(200),
    BUFF_201(201),
    BUFF_202(202),
    BUFF_203(203),
    BUFF_204(204),
    BUFF_205(205),
    BUFF_206(206),
    BUFF_207(207),
    BUFF_208(208),
    BUFF_209(209),
    BUFF_210(210),
    BUFF_211(211),
    BUFF_212(212),
    BUFF_213(213),
    BUFF_214(214),
    BUFF_215(215),
    BUFF_216(216),
    BUFF_217(217),
    BUFF_218(218),
    BUFF_219(219),
    BUFF_220(220),
    BUFF_221(221),
    BUFF_222(222),
    BUFF_223(223),
    BUFF_224(224),
    BUFF_225(225),
    BUFF_226(226),
    BUFF_227(227),
    BUFF_228(228),
    // 4
    // 8

    //CRITICAL_RATE(0x1000000, 6),
    //0x2000000 unknown
    //0x4000000 unknown DEBUFF?
    //0x8000000 unknown DEBUFF?

    // 1 unknown	
    // 2 unknown	
    // DEFENCE_BOOST_R(0x40000000, 6), // weapon def and magic def
    // 8 unknown

    // 0x1
    // 0x2
    // 0x4
    // 0x8 got somekind of statEffect when buff ends...

    // 0x10
    // 0x20 dc, should be overrride
    // 0x40 add attack, 425 wd, 425 md, 260 for acc, and avoid
    // 0x80

    // 0x100	
//    HP_BOOST_PERCENT(0x200, 7, true),
//    MP_BOOST_PERCENT(0x400, 7, true),
    //WEAPON_ATTACK(0x800, 7),

    // 0x1000, 7, true + 5003 wd
    // 0x2000,
    // 0x4000, true
    // 0x8000

    // WEAPON ATTACK 0x10000, true
    // 0x20000, true
    // 0x40000, true
    // 0x80000, true

    // 0x100000  true
    // 0x200000 idk
    // 0x400000  true
    // 0x800000 idk
//
//    JUDGMENT_DRAW(0x10, 8)/* 271 */
//    , UNKNOWN12(0x1000, 7),
//    UNKNOWN9(0x800000, 7),
//    UNKNOWN8(0x20, 7),
//    ABSORB_DAMAGE_HP(0x20000000, 6),


    // ----- 預設BUF  --------
    // 能量獲得 完成
    ENERGY_CHARGE(248),
    // 衝鋒_速度 完成
    DASH_SPEED(249),
    // 衝鋒_跳躍 完成
    DASH_JUMP(250),
    // 怪物騎乘 完成
    MONSTER_RIDING(251),
    // 最終極速 完成
    SPEED_INFUSION(252),
    // 指定攻擊(無盡追擊)
    HOMING_BEACON(253),
    DEFAULTBUFF1(254),
    DEFAULTBUFF2(255),;

    private int buffStatus;
    private int pos;
    private boolean stacked = false;
//
//    /* 275 */   FINAL_FEINT(0x4, 8),
//    /* 276 */   SHROUD_WALK(0x8, 8),
// HP_LOSS_GUARD(0x20000000, 1), //?????? == WEAKEN ??

    MapleBuffStatus(int i) {
        this.buffStatus = 1 << (i % 32);
        this.pos = (GameConstants.MAX_BUFFSTAT - 1) - (int) Math.floor(i / 32);
    }

    MapleBuffStatus(int i, boolean stacked) {
        this.buffStatus = 1 << (i % 32);
        this.pos = (GameConstants.MAX_BUFFSTAT - 1) - (int) Math.floor(i / 32);
        this.stacked = stacked;
    }

    public static String nameOf(int value) {
        for (MapleBuffStatus buffstatus : MapleBuffStatus.values()) {
            if (buffstatus.getValue() == value) {
                return buffstatus.name();
            }
        }
        return "UNKNOWN";
    }

    public static void reloadValues() {
        String fileName = "buffstatus.properties";
        Properties props = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(".\\src\\main\\java\\properties\\" + fileName); BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream, StringUtil.codeString(".\\src\\main\\java\\properties\\" + fileName)))) {
            props.load(br);
        } catch (IOException ex) {
            InputStream in = MapleBuffStatus.class.getClassLoader().getResourceAsStream("properties/" + fileName);
            if (in == null) {
                System.err.println("錯誤: 未加載 " + fileName + " 檔案");
                return;
            }
            try {
                props.load(in);
                in.close();
            } catch (IOException e) {
                throw new RuntimeException("加載 " + fileName + " 檔案出錯", e);
            }
        }
        ExternalCodeLongTableGetter.populateValues(props, values());
    }

    public final int getPosition() {
        return pos;
    }

    @Override
    public long get() {
        return buffStatus;
    }

    @Override
    public int getValue() {
        return buffStatus;
    }

    public final boolean canStack() {
        return stacked;
    }

    @Override
    public void set(long i) {
        this.buffStatus = 1 << (i % 32);
        this.pos = (GameConstants.MAX_BUFFSTAT - 1) - (int) Math.floor(i / 32);
    }

}
