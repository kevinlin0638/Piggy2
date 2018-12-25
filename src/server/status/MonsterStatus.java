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


import java.io.Serializable;

public enum MonsterStatus implements Serializable, IBuffStat {

    //物攻
    WATK(0),
    //物防
    WDEF(1),
    //魔攻
    MATK(2),
    //魔防
    MDEF(3),
    //命中
    ACC(4),
    //迴避
    AVOID(5),
    //速度
    SPEED(6),
    //暈眩
    STUN(7),
    //結冰
    FREEZE(8),
    //中毒
    POISON(9),
    //封印、沉默
    SEAL(10),
    //黑暗
    DARKNESS(11),
    //物理攻擊提昇
    WEAPON_ATTACK_UP(12),
    //物理防禦提昇
    WEAPON_DEFENSE_UP(13),
    //魔法攻擊提昇
    MAGIC_ATTACK_UP(14),
    //魔法防禦提昇
    MAGIC_DEFENSE_UP(15),
    //死亡
    DOOM(16, 18),
    //影網
    SHADOW_WEB(17, 19),
    //物攻免疫
    WEAPON_IMMUNITY(18, 16),
    //魔攻免疫
    MAGIC_IMMUNITY(19, 17),
    //挑釁
    SHOWDOWN(20, 35),
    //免疫傷害
    DAMAGE_IMMUNITY(21, 20),
    //忍者伏擊
    NINJA_AMBUSH(22, 21),
    //
    DANAGED_ELEM_ATTR(23, 37),
    //武器荼毒
    VENOMOUS_WEAPON(24, 21),
    //致盲
    BLIND(25, 22),

    BURN(24, 23),
    //技能封印
    SEAL_SKILL(26, 24),
    //
    BLEED(27, 44),
    //心靈控制
    HYPNOTIZE(28, 28),
    //反勝物攻
    WEAPON_DAMAGE_REFLECT(29, 29),
    //反射魔攻
    MAGIC_DAMAGE_REFLECT(30, 30),
    //
    SUMMON(31),
    MOB_BUFF_32(32, 31),
    NEUTRALISE(33, 32),
    IMPRINT(34, 33),
    MONSTER_BOMB(35, 34),
    MAGIC_CRASH(36, 36),
    MOB_BUFF_37(37),
    MOB_BUFF_38(38, 38),
    MOB_BUFF_39(3, 39),
    MOB_BUFF_40(40, 40),
    MOB_BUFF_41(41, 41 ),
    MOB_BUFF_42(42, 42 ),
    MOB_BUFF_43(43, 43)
    ;

    /***
     * WATK(0x1, 1),
     * WDEF(0x2, 1),
     * MATK(0x4, 1),
     * MDEF(0x8, 1),
     * ACC(0x10, 1),
     * AVOID(0x20, 1),
     * SPEED(0x40, 1),
     * STUN(0x80, 1),
     * FREEZE(0x100, 1),
     * POISON(0x200, 1),
     * SEAL(0x400, 1),
     * SHOWDOWN(0x800, 1),
     * WEAPON_ATTACK_UP(0x1000, 1),
     * WEAPON_DEFENSE_UP(0x2000, 1),
     * MAGIC_ATTACK_UP(0x4000, 1),
     * MAGIC_DEFENSE_UP(0x8000, 1),
     * DOOM(0x10000, 1),
     * SHADOW_WEB(0x20000, 1),
     * WEAPON_IMMUNITY(0x40000, 1),
     * MAGIC_IMMUNITY(0x80000, 1),
     * DAMAGE_IMMUNITY(0x200000, 1),
     * NINJA_AMBUSH(0x400000, 1),
     * BURN(0x1000000, 1),
     * DARKNESS(0x2000000, 1),
     * HYPNOTIZE(0x10000000, 1),
     * WEAPON_DAMAGE_REFLECT(0x20000000, 1),
     * MAGIC_DAMAGE_REFLECT(0x40000000, 1),
     * NEUTRALISE(0x2, 2), // first int on v.87 or else it won't work.
     * IMPRINT(0x4, 2),
     * MONSTER_BOMB(0x8, 2),
     * MAGIC_CRASH(0x10, 2),
     * //speshul comes after
     * EMPTY(0x8000000, 1, true),
     * SUMMON(0x80000000, 1, true), //all summon bag mobs have.
     * EMPTY_1(0x20, 2, !GameConstants.GMS), //chaos
     * EMPTY_2(0x40, 2, true),
     * EMPTY_3(0x80, 2, true),
     * EMPTY_4(0x100, 2, GameConstants.GMS), //jump
     * EMPTY_5(0x200, 2, GameConstants.GMS),
     * EMPTY_6(0x400, 2, GameConstants.GMS),
     * EMPTY_7(0x2000, 2, GameConstants.GMS);
     */


    static final long serialVersionUID = 0L;
    private final int i;
    private final int pos;
    private final boolean isDefault;
    private final int order;
    private final int bitnum;

    private MonsterStatus(int i) {
        this.bitnum = i;
        this.i = 1 << (i % 32);
        this.pos = 3 - (int) Math.floor(i / 32);
        this.isDefault = false;
        this.order = i;
    }

    private MonsterStatus(int i, int order) {
        this.bitnum = i;

        this.i = 1 << (i % 32);
        this.pos = 3 - (int) Math.floor(i / 32);
        this.isDefault = false;
        this.order = order;
    }

    private MonsterStatus(int i, int order, boolean isDefault) {
        this.bitnum = i;
        this.i = 1 << (i % 32);
        this.pos = 3 - (int) Math.floor(i / 32);
        this.isDefault = isDefault;
        this.order = order;
    }

    private MonsterStatus(int i, boolean isDefault) {
        this.bitnum = i;
        this.i = 1 << (i % 32);
        this.pos = 3 - (int) Math.floor(i / 32);
        this.isDefault = isDefault;
        this.order = i;
    }

    public int getOrder() {
        return order;
    }

    @Override
    public int getPosition() {
        return pos;
    }

    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public int getValue() {
        return i;
    }

    public int getBitNumber() {
        return bitnum;
    }


    public static MapleBuffStatus getLinkedDisease(final MonsterStatus skill) {
        switch (skill) {
            case STUN:
            case SHADOW_WEB:
                return MapleBuffStatus.STUN;
            case POISON:
            case BURN:
                return MapleBuffStatus.POISON;
            case SEAL:
            case MAGIC_CRASH:
                return MapleBuffStatus.SEAL;
            case FREEZE:
                return MapleBuffStatus.FREEZE;
            case DARKNESS:
                return MapleBuffStatus.DARKNESS;
            case SPEED:
                return MapleBuffStatus.SLOW;
        }
        return null;
    }

}
