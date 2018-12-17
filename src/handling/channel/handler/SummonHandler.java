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
package handling.channel.handler;

import client.*;
import client.skill.Skill;
import client.skill.SkillFactory;
import client.skill.SummonSkillEntry;
import server.status.MapleBuffStatus;
import server.status.MonsterStatus;
import server.status.MonsterStatusEffect;
import constants.GameConstants;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.Randomizer;
import server.life.MapleMonster;
import server.life.MobSkill;
import server.maps.*;
import server.movement.ILifeMovementFragment;
import server.movement.MovementKind;
import tools.types.AttackPair;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CField.EffectPacket;
import tools.packet.CField.SummonPacket;
import tools.packet.MobPacket;
import tools.types.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SummonHandler {

    public static void MoveDragon(final LittleEndianAccessor slea, final MapleCharacter chr) {
        if (chr.getDragon() == null) {
            return;
        }
        //[D8 01 12 01] [00 00 00 00] [03]
        //座標          空白           類型
        slea.skip(8);
        final List<ILifeMovementFragment> res = MovementParse.parseMovement(slea, chr.getDragon().getPosition(), MovementKind.DRAGON_MOVEMENT);
        if (res.size() > 0) {
            final Point pos = chr.getDragon().getPosition();
            MovementParse.updatePosition(res, chr.getDragon(), 0);
            if (!chr.isHidden()) {
                chr.getMap().broadcastMessage(chr, CField.moveDragon(chr.getDragon(), pos, res), chr.getTruePosition());
            }
        }
    }

    public static void MoveSummon(final LittleEndianAccessor slea, final MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            return;
        }
        final MapleMapObject obj = chr.getMap().getMapObject(slea.readInt(), MapleMapObjectType.SUMMON);
        if (obj == null) {
            return;
        }
        if (obj instanceof MapleDragon) {
            MoveDragon(slea, chr);
            return;
        }
        final MapleSummon sum = (MapleSummon) obj;
        if (sum.getOwnerId() != chr.getId() || sum.getSkillLevel() <= 0 || sum.getMovementType() == SummonMovementType.STATIONARY) {
            return;
        }
        slea.skip(8); //startPOS
        final List<ILifeMovementFragment> res = MovementParse.parseMovement(slea, sum.getPosition(), MovementKind.SUMMON_MOVEMENT);

        final Point pos = sum.getPosition();
        MovementParse.updatePosition(res, sum, 0);
        if (res.size() > 0) {
            chr.getMap().broadcastMessage(chr, SummonPacket.moveSummon(chr.getId(), sum.getObjectId(), pos, res), sum.getTruePosition());
        }
    }

    public static void DamageSummon(final LittleEndianAccessor slea, final MapleCharacter chr) {
        final int unkByte = slea.readByte();
        final int damage = slea.readInt();
        final int monsterIdFrom = slea.readInt();
        //       slea.readByte(); // stance

        final Iterator<MapleSummon> iter = chr.getSummonsReadLock().iterator();
        MapleSummon summon;
        boolean remove = false;
        try {
            while (iter.hasNext()) {
                summon = iter.next();
                if (summon.isPuppet() && summon.getOwnerId() == chr.getId() && damage > 0) { //We can only have one puppet(AFAIK O.O) so this check is safe.
                    summon.addHP((short) -damage);
                    if (summon.getHP() <= 0) {
                        remove = true;
                    }
                    chr.getMap().broadcastMessage(chr, SummonPacket.damageSummon(chr.getId(), summon.getSkill(), damage, unkByte, monsterIdFrom), summon.getTruePosition());
                    break;
                }
            }
        } finally {
            chr.unlockSummonsReadLock();
        }
        if (remove) {
            chr.cancelEffectFromBuffStat(MapleBuffStatus.PUPPET);
        }
    }

    public static void SummonAttack(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (!chr.isAlive()) || (chr.getMap() == null)) {
            return;
        }
        MapleMap map = chr.getMap();
        MapleMapObject obj = map.getMapObject(slea.readInt(), MapleMapObjectType.SUMMON);
        if ((obj == null) || (!(obj instanceof MapleSummon))) {
            chr.dropMessage(5, "The summon has disappeared.");
            return;
        }
        MapleSummon summon = (MapleSummon) obj;
        if ((summon.getOwnerId() != chr.getId()) || (summon.getSkillLevel() <= 0)) {
            chr.dropMessage(5, "Error.");
            return;
        }
        SummonSkillEntry sse = SkillFactory.getSummonData(summon.getSkill());
        if ((summon.getSkill() / 1000000 != 35) && (summon.getSkill() != 33101008) && (sse == null)) {
            chr.dropMessage(5, "Error in processing attack.");
            return;
        }
        if (!GameConstants.GMS) {
            slea.skip(8);
        }
        slea.readInt();
        if (!GameConstants.GMS) {
            slea.skip(8);
        }
        byte animation = slea.readByte();
        if (!GameConstants.GMS) {
            slea.skip(8);
        }
        byte numAttacked = slea.readByte();
        // if ((sse != null) && (numAttacked > sse.mobCount)) {
        // chr.dropMessage(5, "Warning: Attacking more monster than summon can do");
        //  return;
        //}
        slea.skip(summon.getSkill() == 35111002 ? 24 : 12);
        List<Pair<Integer, Integer>> allDamage = new ArrayList<>();
        for (int i = 0; i < numAttacked; i++) {
            int mob = slea.readInt();//MapleMonster mob = map.getMonsterByOid(slea.readInt());
            /*if (mob == null) {
                continue;
            }*/
            slea.skip(18);
            int damge = slea.readInt();
            if (chr.isShowInfo()) {
                chr.dropMessage(-5, "召喚獸攻擊[" + summon.getSkill() + "] - 攻擊數量: " + numAttacked + " 怪物ID " + mob + " 傷害: " + damge);
            }
            allDamage.add(new Pair(Integer.valueOf(mob), Integer.valueOf(damge)));
            slea.readInt();//[?? ?? ?? ??]
        }
        map.broadcastMessage(chr, CField.SummonPacket.summonAttack(summon.getOwnerId(), summon.getObjectId(), animation, allDamage, chr.getLevel(), false), summon.getTruePosition());
        Skill summonSkill = SkillFactory.getSkill(summon.getSkill());
        MapleStatEffect summonEffect = summonSkill.getEffect(summon.getSkillLevel());
        if (summonEffect == null) {
            chr.dropMessage(5, "Error in attack.");
            return;
        }
        for (Pair<Integer, Integer> attackEntry : allDamage) {
            int toDamage = ((Integer) attackEntry.right).intValue();
            MapleMonster mob = map.getMonsterByOid(((Integer) attackEntry.left).intValue());
            if (mob == null) {
                continue;
            }
            if ((toDamage > 0) && (summonEffect.getMonsterStati().size() > 0)
                    && (summonEffect.makeChanceResult())) {
                for (Map.Entry z : summonEffect.getMonsterStati().entrySet()) {
                    mob.applyStatus(chr, new MonsterStatusEffect((MonsterStatus) z.getKey(), (Integer) z.getValue(), summonSkill.getId(), null, false), summonEffect.isPoison(), 4000, true, summonEffect);
                }
            }
            if (chr.isGM() || toDamage < (chr.getStat().getCurrentMaxBaseDamage() * 5.0 * (summonEffect.getSelfDestruction() + summonEffect.getDamage() + chr.getStat().getDamageIncrease(summonEffect.getSourceId())) / 100.0)) {
                if (toDamage >= 999999) {
                    toDamage = 999999;
                }
                mob.damage(chr, toDamage, true);
                chr.checkMonsterAggro(mob);
                if (!mob.isAlive()) {
                    chr.getClient().getSession().write(MobPacket.killMonster(mob.getObjectId(), 1));
                }
            } else {
                chr.getClient().getSession().write(MobPacket.killMonster(mob.getObjectId(), 1));
                break;
            }
            /*
            if ((toDamage < chr.getStat().getCurrentMaxBaseDamage() * 5.0 * (summonEffect.getSelfDestruction() + summonEffect.getDamage() + chr.getStat().getDamageIncrease(summonEffect.getSourceId())) / 100.0)) {
                mob.damage(chr, toDamage, true);
                chr.checkMonsterAggro(mob);
                if (!mob.isAlive()) {
                    chr.getClient().sendPacket(MobPacket.killMonster(mob.getObjectId(), 1));
                }
            }*/
        }
        if (!summon.isMultiAttack()) {
            chr.getMap().broadcastMessage(CField.SummonPacket.removeSummon(summon, true));
            chr.getMap().removeMapObject(summon);
            chr.removeVisibleMapObject(summon);
            chr.removeSummon(summon);
            if (summon.getSkill() != 35121011) /* 256 */ {
                chr.cancelEffectFromBuffStat(MapleBuffStatus.SUMMON);
            }
        }
    }

    /*   public static void SummonAttack(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
           if (chr == null || !chr.isAlive() || chr.getMap() == null) {
               return;
           }
           final MapleMap map = chr.getMap();
           final MapleMapObject obj = map.getMapObject(slea.readInt(), MapleMapObjectType.SUMMON);
           if (obj == null || !(obj instanceof MapleSummon)) {
               chr.dropMessage(5, "The summon has disappeared.");
               return;
           }
           final MapleSummon summon = (MapleSummon) obj;
           if (summon.getOwnerId() != chr.getWorldId() || summon.getSkillLevel() <= 0) {
               chr.dropMessage(5, "Error.");
               return;
           }
           final SummonSkillEntry sse = SkillFactory.getSummonData(summon.getSkill());
           if (summon.getSkill() / 1000000 != 35 && summon.getSkill() != 33101008 && sse == null) {
               chr.dropMessage(5, "Error in processing attack.");
               return;
           }
           if (!GameConstants.GMS) {
               slea.skip(8);
           }
           int tick = slea.readInt();
           if (!GameConstants.GMS) {
               slea.skip(8);
           }
           final byte animation = slea.readByte();
           if (!GameConstants.GMS) {
               slea.skip(8);
           }
           final byte numAttacked = slea.readByte();
           if (sse != null && numAttacked > sse.mobCount) {
               chr.dropMessage(5, "Warning: Attacking more monster than summon can do");
               //AutobanManager.getInstance().autoban(c, "Attacking more monster that summon can do (Skillid : "+summon.getSkill()+" Count : " + numAttacked + ", allowed : " + sse.mobCount + ")");
               return;
           }
           slea.skip(summon.getSkill() == 35111002 ? 24 : 12); //some pos stuff
           final List<Pair<Integer, Integer>> allDamage = new ArrayList<>();
           for (int i = 0; i < numAttacked; i++) {
               final MapleMonster mob = map.getMonsterByOid(slea.readInt());

               if (mob == null) {
                   continue;
               }
               slea.skip(18); // who knows
               final int damge = slea.readInt();
               allDamage.add(new Pair<>(mob.getObjectId(), damge));
           }
           //if (!summon.isChangedMap()) {
           map.broadcastMessage(chr, SummonPacket.summonAttack(summon.getOwnerId(), summon.getObjectId(), animation, allDamage, chr.getLevel(), false), summon.getTruePosition());
           //}
           final Skill summonSkill = SkillFactory.getSkill(summon.getSkill());
           final MapleStatEffect summonEffect = summonSkill.getEffect(summon.getSkillLevel());
           if (summonEffect == null) {
               chr.dropMessage(5, "Error in attack.");
               return;
           }
           for (Pair<Integer, Integer> attackEntry : allDamage) {
               final int toDamage = attackEntry.right;
               final MapleMonster mob = map.getMonsterByOid(attackEntry.left);
               if (mob == null) {
                   continue;
               }
               if (toDamage > 0 && summonEffect.getMonsterStati().size() > 0) {
                   if (summonEffect.makeChanceResult()) {
                       for (Map.Entry<MonsterStatus, Integer> z : summonEffect.getMonsterStati().entrySet()) {
                           mob.applyStatus(chr, new MonsterStatusEffect(z.getKey(), z.getValue(), summonSkill.getWorldId(), null, false), summonEffect.isPoison(), 4000, true, summonEffect);
                       }
                   }
               }
               if (toDamage < (chr.getStat().getCurrentMaxBaseDamage() * 5.0 * (summonEffect.getSelfDestruction() + summonEffect.getDamage() + chr.getStat().getDamageIncrease(summonEffect.getSourceId())) / 100.0)) { //10 x dmg.. eh
                   mob.damage(chr, toDamage, true);
                   chr.checkMonsterAggro(mob);
                   if (!mob.isAlive()) {
                       chr.getClient().sendPacket(MobPacket.killMonster(mob.getObjectId(), 1));
                   }
               } else {
                   break;
               }
           }
           if (!summon.isMultiAttack()) {
               chr.getMap().broadcastMessage(SummonPacket.removeSummon(summon, true));
               chr.getMap().removeMapObject(summon);
               chr.removeVisibleMapObject(summon);
               chr.removeSummon(summon);
               if (summon.getSkill() != 35121011) {
                   chr.cancelEffectFromBuffStat(MapleBuffStatus.SUMMON);
               }
           }
       }

     */
    public static void RemoveSummon(final LittleEndianAccessor slea, final MapleClient c) {
        final MapleMapObject obj = c.getPlayer().getMap().getMapObject(slea.readInt(), MapleMapObjectType.SUMMON);
        if (obj == null || !(obj instanceof MapleSummon)) {
            return;
        }
        final MapleSummon summon = (MapleSummon) obj;
        if (summon.getOwnerId() != c.getPlayer().getId() || summon.getSkillLevel() <= 0) {
            c.getPlayer().dropMessage(5, "Error.");
            return;
        }
        if (summon.getSkill() == 35111002 || summon.getSkill() == 35121010) { //rock n shock, amp
            return;
        }
        c.getPlayer().getMap().broadcastMessage(SummonPacket.removeSummon(summon, true));
        c.getPlayer().getMap().removeMapObject(summon);
        c.getPlayer().removeVisibleMapObject(summon);
        c.getPlayer().removeSummon(summon);
        if (summon.getSkill() != 35121011) {
            c.getPlayer().cancelEffectFromBuffStat(MapleBuffStatus.SUMMON);
            //TODO: Multi Summoning, must do something about hack buffstat
        }
    }

    public static void SubSummon(final LittleEndianAccessor slea, final MapleCharacter chr) {
        final MapleMapObject obj = chr.getMap().getMapObject(slea.readInt(), MapleMapObjectType.SUMMON);
        if (obj == null || !(obj instanceof MapleSummon)) {
            return;
        }
        final MapleSummon sum = (MapleSummon) obj;
        if (sum == null || sum.getOwnerId() != chr.getId() || sum.getSkillLevel() <= 0 || !chr.isAlive()) {
            return;
        }
        switch (sum.getSkill()) {
            case 35121009:
                if (!chr.canSummon(2000)) {
                    return;
                }
                final int skillId = slea.readInt(); // 35121009?
                if (sum.getSkill() != skillId) {
                    return;
                }
                slea.skip(1); // 0E?
                slea.readInt();
                for (int i = 0; i < 3; i++) {
                    final MapleSummon tosummon = new MapleSummon(chr, SkillFactory.getSkill(35121011).getEffect(sum.getSkillLevel()), new Point(sum.getTruePosition().x, sum.getTruePosition().y - 5), SummonMovementType.WALK_STATIONARY);
                    chr.getMap().spawnSummon(tosummon);
                    chr.addSummon(tosummon);
                }
                break;
            case 35111011: //healing
                if (!chr.canSummon(1000)) {
                    return;
                }
                chr.addHP((int) (chr.getStat().getCurrentMaxHp() * SkillFactory.getSkill(sum.getSkill()).getEffect(sum.getSkillLevel()).getHp() / 100.0));
                chr.getClient().sendPacket(EffectPacket.showOwnBuffEffect(sum.getSkill(), 2, chr.getLevel(), sum.getSkillLevel()));
                chr.getMap().broadcastMessage(chr, EffectPacket.showBuffeffect(chr.getId(), sum.getSkill(), 2, chr.getLevel(), sum.getSkillLevel()), false);
                break;
            case 1321007: //beholder
                Skill bHealing = SkillFactory.getSkill(slea.readInt());
                final int bHealingLvl = chr.getTotalSkillLevel(bHealing);
                if (bHealingLvl <= 0 || bHealing == null) {
                    return;
                }
                final MapleStatEffect healEffect = bHealing.getEffect(bHealingLvl);
                if (bHealing.getId() == 1320009) {
                    healEffect.applyTo(chr);
                } else if (bHealing.getId() == 1320008) {
                    if (!chr.canSummon(healEffect.getX() * 1000)) {
                        return;
                    }
                    chr.addHP(healEffect.getHp());
                }
                chr.getClient().sendPacket(EffectPacket.showOwnBuffEffect(sum.getSkill(), 2, chr.getLevel(), bHealingLvl));
                chr.getMap().broadcastMessage(SummonPacket.summonSkill(chr.getId(), sum.getSkill(), bHealing.getId() == 1320008 ? 5 : (Randomizer.nextInt(3) + 6)));
                chr.getMap().broadcastMessage(chr, EffectPacket.showBuffeffect(chr.getId(), sum.getSkill(), 2, chr.getLevel(), bHealingLvl), false);
                break;
        }
        if (GameConstants.isAngel(sum.getSkill())) {
            if (sum.getSkill() % 10000 == 1087) {
                MapleItemInformationProvider.getInstance().getItemEffect(2022747).applyTo(chr);
            } else if (sum.getSkill() % 10000 == 1179) {
                MapleItemInformationProvider.getInstance().getItemEffect(2022823).applyTo(chr);
            } else {
                MapleItemInformationProvider.getInstance().getItemEffect(2022746).applyTo(chr);
            }
            chr.getClient().sendPacket(EffectPacket.showOwnBuffEffect(sum.getSkill(), 2, 2, 1));
            chr.getMap().broadcastMessage(chr, EffectPacket.showBuffeffect(chr.getId(), sum.getSkill(), 2, 2, 1), false);
        }
    }

    public static void SummonPVP(final LittleEndianAccessor slea, final MapleClient c) {
        final MapleCharacter chr = c.getPlayer();
        if (chr == null || chr.isHidden() || !chr.isAlive() || chr.hasBlockedInventory() || chr.getMap() == null || !chr.inPVP() || !chr.getEventInstance().getProperty("started").equals("1")) {
            return;
        }
        final MapleMap map = chr.getMap();
        final MapleMapObject obj = map.getMapObject(slea.readInt(), MapleMapObjectType.SUMMON);
        if (obj == null || !(obj instanceof MapleSummon)) {
            chr.dropMessage(5, "The summon has disappeared.");
            return;
        }
        int tick = -1;
        if (slea.available() == 27) {
            slea.skip(23);
            tick = slea.readInt();
        }
        final MapleSummon summon = (MapleSummon) obj;
        if (summon.getOwnerId() != chr.getId() || summon.getSkillLevel() <= 0) {
            chr.dropMessage(5, "Error.");
            return;
        }
        final Skill skil = SkillFactory.getSkill(summon.getSkill());
        final MapleStatEffect effect = skil.getEffect(summon.getSkillLevel());
        final int lvl = Integer.parseInt(chr.getEventInstance().getProperty("lvl"));
        final int type = Integer.parseInt(chr.getEventInstance().getProperty("type"));
        final int ourScore = Integer.parseInt(chr.getEventInstance().getProperty(String.valueOf(chr.getId())));
        int addedScore = 0;
        final boolean magic = skil.isMagic();
        boolean killed = false, didAttack = false;
        double maxdamage = lvl == 3 ? chr.getStat().getCurrentMaxBasePVPDamageL() : chr.getStat().getCurrentMaxBasePVPDamage();
        maxdamage *= (effect.getDamage() + chr.getStat().getDamageIncrease(summon.getSkill())) / 100.0;
        int mobCount = 1, attackCount = 1, ignoreDEF = chr.getStat().ignoreTargetDEF;

        final SummonSkillEntry sse = SkillFactory.getSummonData(summon.getSkill());
        if (summon.getSkill() / 1000000 != 35 && summon.getSkill() != 33101008 && sse == null) {
            chr.dropMessage(5, "Error in processing attack.");
            return;
        }
        Point lt, rb;
        if (sse != null) {
            if (sse.delay > 0) {
                if (tick != -1) {
                    summon.CheckSummonAttackFrequency(chr, tick);
                } else {
                    summon.CheckPVPSummonAttackFrequency(chr);
                }
            }
            mobCount = sse.mobCount;
            attackCount = sse.attackCount;
            lt = sse.lt;
            rb = sse.rb;
        } else {
            lt = new Point(-100, -100);
            rb = new Point(100, 100);
        }
        final Rectangle box = MapleStatEffect.calculateBoundingBox(chr.getTruePosition(), chr.isFacingLeft(), lt, rb, 0);
        List<AttackPair> ourAttacks = new ArrayList<>();
        List<Pair<Integer, Boolean>> attacks;
        maxdamage *= chr.getStat().dam_r / 100.0;
        for (MapleMapObject mo : chr.getMap().getCharactersIntersect(box)) {
            final MapleCharacter attacked = (MapleCharacter) mo;
            if (attacked.getId() != chr.getId() && attacked.isAlive() && !attacked.isHidden() && (type == 0 || attacked.getTeam() != chr.getTeam())) {
                double rawDamage = maxdamage / Math.max(0, ((magic ? attacked.getStat().mdef : attacked.getStat().wdef) * Math.max(1.0, 100.0 - ignoreDEF) / 100.0) * (type == 3 ? 0.1 : 0.25));
                if (attacked.getBuffedValue(MapleBuffStatus.INVINCIBILITY) != null || PlayersHandler.inArea(attacked)) {
                    rawDamage = 0;
                }
                rawDamage += (rawDamage * chr.getDamageIncrease(attacked.getId()) / 100.0);
                rawDamage *= attacked.getStat().mesoGuard / 100.0;
                rawDamage = attacked.modifyDamageTaken(rawDamage, attacked).left;
                final double min = (rawDamage * chr.getStat().trueMastery / 100);
                attacks = new ArrayList<>(attackCount);
                int totalMPLoss = 0, totalHPLoss = 0;
                for (int i = 0; i < attackCount; i++) {
                    int mploss = 0;
                    double ourDamage = Randomizer.nextInt((int) Math.abs(Math.round(rawDamage - min)) + 1) + min;
                    if (attacked.getStat().dodgeChance > 0 && Randomizer.nextInt(100) < attacked.getStat().dodgeChance) {
                        ourDamage = 0;
                        //i dont think level actually matters or it'd be too op
                        //} else if (attacked.getLevel() > chr.getLevel() && Randomizer.nextInt(100) < (attacked.getLevel() - chr.getLevel())) {
                        //	ourDamage = 0;
                    }
                    if (attacked.getBuffedValue(MapleBuffStatus.MAGIC_GUARD) != null) {
                        mploss = (int) Math.min(attacked.getStat().getMp(), (ourDamage * attacked.getBuffedValue(MapleBuffStatus.MAGIC_GUARD).doubleValue() / 100.0));
                    }
                    ourDamage -= mploss;
                    if (attacked.getBuffedValue(MapleBuffStatus.INFINITY) != null) {
                        mploss = 0;
                    }
                    attacks.add(new Pair<>((int) Math.floor(ourDamage), false));

                    totalHPLoss += Math.floor(ourDamage);
                    totalMPLoss += mploss;
                }
                attacked.addMPHP(-totalHPLoss, -totalMPLoss);
                ourAttacks.add(new AttackPair(attacked.getId(), attacked.getPosition(), attacks));
                if (totalHPLoss > 0) {
                    didAttack = true;
                }
                if (attacked.getStat().getHPPercent() <= 20) {
                    SkillFactory.getSkill(PlayerStats.getSkillByJob(93, attacked.getJob())).getEffect(1).applyTo(attacked);
                }
                if (effect != null) {
                    if (effect.getMonsterStati().size() > 0 && effect.makeChanceResult()) {
                        for (Map.Entry<MonsterStatus, Integer> z : effect.getMonsterStati().entrySet()) {
                            MapleBuffStatus d = MonsterStatus.getLinkedDisease(z.getKey());
                            if (d != null) {
                                attacked.getDiseaseBuff(d, z.getValue(), effect.getDuration(), MobSkill.getMobSkillByBuffStatus(d), 1);
                            }
                        }
                    }
                    effect.handleExtraPVP(chr, attacked);
                }
                chr.getClient().sendPacket(CField.getPVPHPBar(attacked.getId(), attacked.getStat().getHp(), attacked.getStat().getCurrentMaxHp()));
                addedScore += (totalHPLoss / 100) + (totalMPLoss / 100); //ive NO idea
                if (!attacked.isAlive()) {
                    killed = true;
                }

                if (ourAttacks.size() >= mobCount) {
                    break;
                }
            }
        }
        if (killed || addedScore > 0) {
            chr.getEventInstance().addPVPScore(chr, addedScore);
            chr.getClient().sendPacket(CField.getPVPScore(ourScore + addedScore, killed));
        }
        if (didAttack) {
            chr.getMap().broadcastMessage(SummonPacket.pvpSummonAttack(chr.getId(), chr.getLevel(), summon.getObjectId(), summon.isFacingLeft() ? 4 : 0x84, summon.getTruePosition(), ourAttacks));
            if (!summon.isMultiAttack()) {
                chr.getMap().broadcastMessage(SummonPacket.removeSummon(summon, true));
                chr.getMap().removeMapObject(summon);
                chr.removeVisibleMapObject(summon);
                chr.removeSummon(summon);
                if (summon.getSkill() != 35121011) {
                    chr.cancelEffectFromBuffStat(MapleBuffStatus.SUMMON);
                }
            }
        }
    }
}
