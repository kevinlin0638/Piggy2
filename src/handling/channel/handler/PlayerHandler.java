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

import client.MapleCharacter;
import client.MapleClient;
import client.PlayerStats;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.skill.Skill;
import client.skill.SkillFactory;
import client.skill.SkillMacro;
import constants.GameConstants;
import constants.ItemConstants;
import constants.ServerConstants;
import constants.SkillConstants;
import handling.RecvPacketOpcode;
import handling.channel.ChannelServer;
import server.*;
import server.life.*;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import server.movement.AbstractLifeMovement;
import server.movement.ILifeMovementFragment;
import server.movement.MovementKind;
import server.quest.MapleQuest;
import server.status.MapleBuffStatus;
import server.status.MonsterStatus;
import server.status.MonsterStatusEffect;
import server.worldevents.MapleEvent;
import server.worldevents.MapleEventType;
import server.worldevents.MapleSnowball.MapleSnowballs;
import tools.FileoutputUtil;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CField.EffectPacket;
import tools.packet.CField.UIPacket;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.InventoryPacket;
import tools.packet.MTSCSPacket;
import tools.packet.MobPacket;
import tools.types.Pair;
import server.Timer.CloneTimer;
import java.awt.*;
import java.lang.ref.WeakReference;
import java.util.List;

public class PlayerHandler {

    public static int isFinisher(final int skillId) {
        switch (skillId) {
            case 1111003:
            case 11111002:
                return 1;
            case 1111005:
            case 11111003:
                return 2;
        }
        return 0;
    }

    public static void ChangeSkillMacro(final LittleEndianAccessor slea, final MapleCharacter chr) {
        final int num = slea.readByte();
        String name;
        int shout, skill1, skill2, skill3;
        SkillMacro macro;

        for (int i = 0; i < num; i++) {
            name = slea.readMapleAsciiString();
            shout = slea.readByte();
            skill1 = slea.readInt();
            skill2 = slea.readInt();
            skill3 = slea.readInt();

            macro = new SkillMacro(skill1, skill2, skill3, name, shout, i);
            chr.updateMacros(i, macro);
        }
    }

    public static void ChangeKeymap(final LittleEndianAccessor slea, final MapleCharacter chr) {
        if (slea.available() > 8 && chr != null) { // else = pet auto pot
            slea.readInt();
            final int numChanges = slea.readInt();
            for (int i = 0; i < numChanges; i++) {
                final int key = slea.readInt();
                final byte type = slea.readByte();
                final int action = slea.readInt();
                if (type == 1 && action >= 1000) { //0 = normal key, 1 = skill, 2 = item
                    final Skill skil = SkillFactory.getSkill(action);
                    if (skil != null) { //not sure about aran tutorial skills..lol
                        if ((!skil.isFourthJob() && !skil.isBeginnerSkill() && skil.isInvisible() && chr.getSkillLevel(skil) <= 0) || GameConstants.isLinkedAranSkill(action) || action % 10000 < 1000 || action >= 91000000) { //cannot put on a key
                            continue;
                        }
                    }
                }
                chr.changeKeybinding(key, type, action);
            }
        } else if (chr != null) {
            final int type = slea.readInt(), data = slea.readInt();
            switch (type) {
                case 1:
                    if (data <= 0) {
                        chr.getQuestRemove(MapleQuest.getInstance(GameConstants.HP_ITEM));
                    } else {
                        chr.getQuestNAdd(MapleQuest.getInstance(GameConstants.HP_ITEM)).setCustomData(String.valueOf(data));
                    }
                    break;
                case 2:
                    if (data <= 0) {
                        chr.getQuestRemove(MapleQuest.getInstance(GameConstants.MP_ITEM));
                    } else {
                        chr.getQuestNAdd(MapleQuest.getInstance(GameConstants.MP_ITEM)).setCustomData(String.valueOf(data));
                    }
                    break;
            }
        }
    }

    public static void UseTitle(final int itemId, final MapleClient c, final MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            return;
        }
        final Item toUse = chr.getInventory(MapleInventoryType.SETUP).findById(itemId);
        if (toUse == null) {
            return;
        }
        if (itemId <= 0) {
            chr.getQuestRemove(MapleQuest.getInstance(GameConstants.ITEM_TITLE));
        } else {
            chr.getQuestNAdd(MapleQuest.getInstance(GameConstants.ITEM_TITLE)).setCustomData(String.valueOf(itemId));
        }
        chr.getMap().broadcastMessage(chr, CField.showTitle(chr.getId(), itemId), false);
        c.sendPacket(CWvsContext.enableActions());
    }

    public static void UseChair(final int itemId, final MapleClient c, final MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            return;
        }
        final Item toUse = chr.getInventory(MapleInventoryType.SETUP).findById(itemId);
        if (toUse == null) {
            return;
        }
        if (GameConstants.isFishingMap(chr.getMapId()) && (!GameConstants.GMS || itemId == 3011000)) {
            if (chr.getStat().canFish) {
                chr.startFishingTask();
            }
        }
        chr.setChair(itemId);
        chr.getMap().broadcastMessage(chr, CField.showChair(chr.getId(), itemId), false);
        if (c.getPlayer().getWatcher() != null) {
            c.getPlayer().getWatcher().dropMessage(5, "" + c.getPlayer().getName() + " has sat down in [Chair ID #" + itemId + " - " + (MapleItemInformationProvider.getInstance().getName(itemId)) + "]");
        }
        c.sendPacket(CWvsContext.enableActions());
    }

    public static void CancelChair(final short id, final MapleClient c, final MapleCharacter chr) {
        if (id == -1) { // Cancel Chair
            chr.cancelFishingTask();
            chr.setChair(0);
            c.sendPacket(CField.cancelChair(-1));
            if (chr.getMap() != null) {
                chr.getMap().broadcastMessage(chr, CField.showChair(chr.getId(), 0), false);
            }
            if (c.getPlayer().getWatcher() != null) {
                c.getPlayer().getWatcher().dropMessage(5, "" + c.getPlayer().getName() + " has got out of their chair.");
            }
        } else { // Use In-Map Chair
            chr.setChair(id);
            c.sendPacket(CField.cancelChair(id));
        }
    }

    public static void TrockAddMap(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final byte addrem = slea.readByte();
        final byte vip = slea.readByte();

        if (vip == 1) { // Regular rocks
            if (addrem == 0) {
                chr.deleteFromRegRocks(slea.readInt());
            } else if (addrem == 1) {
                if (!FieldLimitType.VipRock.check(chr.getMap().getFieldLimit())) {
                    chr.addRegRockMap();
                } else {
                    chr.dropMessage(1, "This map is not available to enter for the list.");
                }
            }
        } else if (vip == 2) { // VIP Rock
            if (addrem == 0) {
                chr.deleteFromRocks(slea.readInt());
            } else if (addrem == 1) {
                if (!FieldLimitType.VipRock.check(chr.getMap().getFieldLimit())) {
                    chr.addRockMap();
                } else {
                    chr.dropMessage(1, "This map is not available to enter for the list.");
                }
            }
        } else if (vip == 3) { // Hyper Rocks
            if (addrem == 0) {
                chr.deleteFromHyperRocks(slea.readInt());
            } else if (addrem == 1) {
                if (!FieldLimitType.VipRock.check(chr.getMap().getFieldLimit())) {
                    chr.addHyperRockMap();
                } else {
                    chr.dropMessage(1, "This map is not available to enter for the list.");
                }
            }
        }
        c.sendPacket(MTSCSPacket.OnMapTransferResult(chr, vip, addrem == 0));
    }

    public static final void CharInfoRequest(int objectid, MapleClient c, MapleCharacter chr) {
        /*  228 */
        if ((c.getPlayer() == null) || (c.getPlayer().getMap() == null)) {
            /*  229 */
            return;
            /*      */
        }
        MapleCharacter player = c.getPlayer().getMap().getCharacterById(objectid);
        if ((player.isGM()) && (player.getCharToggle() == 1)) {
            c.sendPacket(CWvsContext.enableActions());
        } else {
            if ((!c.getPlayer().isGM()) && player.isDonator() && player.getTicklePower() == 1) {
                if (!c.getPlayer().Spam(60000, player.getId())) {
                    player.dropMessage(5, "[Tickle]: " + c.getPlayer().getName() + " has clicked on you!");
                }
            }
            if (c.getPlayer().getWatcher() != null) {
                c.getPlayer().getWatcher().dropMessage(5, "" + c.getPlayer().getName() + " has clicked on " + player.getName());
            }
            c.sendPacket(CWvsContext.charInfo(player, c.getPlayer().getId() == objectid));
            c.sendPacket(CWvsContext.enableActions());
        }
        /*  233 */     //if ((player != null) && ((!player.isGM()) || (c.getPlayer().isGM())))
/*  235 */       //c.sendPacket(CWvsContext.charInfo(player, c.getPlayer().getWorldId() == objectid));
/*      */
    }

    public static void TakeDamage(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        //System.out.println("Take Damage :" + slea.toString());
        slea.skip(4); // randomized
        slea.readInt();
        final byte type = slea.readByte(); //-4 is mist, -3 and -2 are map damage.
        slea.skip(1); // Element - 0x00 = elementless, 0x01 = ice, 0x02 = fire, 0x03 = lightning
        int damage = slea.readInt();
        slea.skip(2);
        boolean isDeadlyAttack = false;
        boolean pPhysical = false;
        int oid;
        int monsteridfrom = 0;
        int fake = 0;
        int mpattack = 0;
        int skillid = 0;
        int pID = 0;
        int pDMG = 0;
        byte direction = 0;
        byte pType = 0;
        Point pPos = new Point(0, 0);
        MapleMonster attacker = null;
        if (chr == null || chr.isHidden() || chr.getMap() == null) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (chr.isGM() && chr.isInvincible()) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        final PlayerStats stats = chr.getStat();
        if (type != -2 && type != -3 && type != -4) { // Not map damage
            monsteridfrom = slea.readInt();
            if (MapleLifeFactory.getMonster(monsteridfrom) == null) {
                System.out.println("Fag trying to dc " + chr.getName());
                return;
            }
            oid = slea.readInt();
            attacker = chr.getMap().getMonsterByOid(oid);
            direction = slea.readByte(); // Knock direction

            if (attacker == null || attacker.getId() != monsteridfrom || attacker.getLinkCID() > 0 || attacker.isFake() || attacker.getStats().isFriendly()) {
                return;
            }
            if (type != -1 && damage > 0) { // Bump damage
                final MobAttackInfo attackInfo = attacker.getStats().getMobAttack(type);
                if (attackInfo != null) {
                    if (attackInfo.isElement && stats.TER > 0 && Randomizer.nextInt(100) < stats.TER) {
                        //       System.out.println("Avoided ER from mob id: " + monsteridfrom);
                        return;
                    }
                    if (attackInfo.isDeadlyAttack()) {
                        isDeadlyAttack = true;
                        mpattack = stats.getMp() - 1;
                    } else {
                        mpattack += attackInfo.getMpBurn();
                    }
                    final MobSkill skill = MobSkillFactory.getMobSkill(attackInfo.getDiseaseSkill(), attackInfo.getDiseaseLevel());
                    if (skill != null && (damage == -1 || damage > 0)) {
                        skill.applyEffect(chr, attacker, false);
                    }
                    attacker.setMp(attacker.getMp() - attackInfo.getMpCon());
                }
            }
            skillid = slea.readInt();
            pDMG = slea.readInt(); // we don't use this, incase packet edit..
            final byte defType = slea.readByte();
            slea.skip(1); // ?
            if (defType == 1) { // Guard
                final Skill bx = SkillFactory.getSkill(31110008);
                final int bof = chr.getTotalSkillLevel(bx);
                if (bof > 0) {
                    final MapleStatEffect eff = bx.getEffect(bof);
                    if (Randomizer.nextInt(100) <= eff.getX()) { // estimate
                        chr.handleForceGain(oid, 31110008, eff.getZ());
                    }
                }
            }
            if (skillid != 0) {
                /*  320 */     //    pPhysical = slea.readByte() > 0;
/*  321 */        // pID = slea.readInt();
/*  322 */       //  pType = slea.readByte();
/*  323 */       //  slea.skip(4);
/*  324 */       //  pPos = slea.readPos();
            }
        }
        if (damage == -1) {
            fake = 4020002 + ((chr.getJob() / 10 - 40) * 100000);
            if (fake != 4120002 && fake != 4220002) {
                fake = 4120002;
            }
            if (type == -1 && chr.getJob() == 122 && attacker != null && chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -10) != null) {
                if (chr.getTotalSkillLevel(1220006) > 0) {
                    final MapleStatEffect eff = SkillFactory.getSkill(1220006).getEffect(chr.getTotalSkillLevel(1220006));
                    attacker.applyStatus(chr, new MonsterStatusEffect(MonsterStatus.STUN, 1, 1220006, null, false), false, eff.getDuration(), true, eff);
                    fake = 1220006;
                }
            }
            if (chr.getTotalSkillLevel(fake) <= 0) {
                return;
            }
        } else if (damage < -1 || damage > 200000) {
            //AutobanManager.getInstance().addPoints(c, 1000, 60000, "Taking abnormal amounts of damge from " + monsteridfrom + ": " + damage);
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (chr.getStat().dodgeChance > 0 && Randomizer.nextInt(100) < chr.getStat().dodgeChance) {
            c.sendPacket(EffectPacket.showForeignEffect(20));
            return;
        }
        if (pPhysical && skillid == 1201007 && chr.getTotalSkillLevel(1201007) > 0) { // Only Power Guard decreases damage
            damage = (damage - pDMG);
            if (damage > 0) {
                final MapleStatEffect eff = SkillFactory.getSkill(1201007).getEffect(chr.getTotalSkillLevel(1201007));
                int enemyDMG = (int) Math.min((damage * (eff.getY() / 100)), (attacker.getMobMaxHp() / 2));
                if (enemyDMG > pDMG) {
                    enemyDMG = pDMG; // ;)
                }
                if (enemyDMG > 1000) { // just a rough estimation, we cannot reflect > 1k
                    enemyDMG = 1000; // too bad
                }
                attacker.damage(chr, enemyDMG, true, 1201007);
            } else {
                damage = 1;
            }
        }
        Pair<Double, Boolean> modify = chr.modifyDamageTaken((double) damage, attacker);
        damage = modify.left.intValue();
        if (damage > 0) {

            if (chr.getBuffedValue(MapleBuffStatus.MORPH) != null) {
                chr.cancelMorphs();
            }
            // if (slea.available() == 3 || slea.available() == 4) {
            //     byte level = slea.readByte();
            //     if (level > 0) {
            //         final MobSkill skill = MobSkillFactory.getMobSkill(slea.readShort(), level);
            //          if (skill != null) {
            //             skill.applyEffect(chr, attacker, false);
            //         }
            //      }
            //  }
            boolean mpAttack = chr.getBuffedValue(MapleBuffStatus.MECH_CHANGE) != null && chr.getBuffSource(MapleBuffStatus.MECH_CHANGE) != 35121005;
            if (chr.getBuffedValue(MapleBuffStatus.MAGIC_GUARD) != null) {
                int hploss = 0, mploss = 0;
                if (isDeadlyAttack) {
                    if (stats.getHp() > 1) {
                        hploss = stats.getHp() - 1;
                    }
                    if (stats.getMp() > 1) {
                        mploss = stats.getMp() - 1;
                    }
                    if (chr.getBuffedValue(MapleBuffStatus.INFINITY) != null) {
                        mploss = 0;
                    }
                    chr.addMPHP(-hploss, -mploss);
                    //} else if (mpattack > 0) {
                    //    chr.addMPHP(-damage, -mpattack);
                } else {
                    mploss = (int) (damage * (chr.getBuffedValue(MapleBuffStatus.MAGIC_GUARD).doubleValue() / 100.0)) + mpattack;
                    hploss = damage - mploss;
                    if (chr.getBuffedValue(MapleBuffStatus.INFINITY) != null) {
                        mploss = 0;
                    } else if (mploss > stats.getMp()) {
                        mploss = stats.getMp();
                        hploss = damage - mploss + mpattack;
                    }
                    chr.addMPHP(-hploss, -mploss);
                }

            } else if (chr.getStat().mesoGuardMeso > 0) {
                //damage = (int) Math.ceil(damage * chr.getStat().mesoGuard / 100.0);
                //handled in client
                final int mesoloss = (int) (damage * (chr.getStat().mesoGuardMeso / 100.0));
                if (chr.getMeso() < mesoloss) {
                    chr.gainMeso(-chr.getMeso(), false);
                    chr.cancelBuffStats(MapleBuffStatus.MESOGUARD);
                } else {
                    chr.gainMeso(-mesoloss, false);
                }
                if (isDeadlyAttack && stats.getMp() > 1) {
                    mpattack = stats.getMp() - 1;
                }
                chr.addMPHP(-damage, -mpattack);
            } else {
                if (isDeadlyAttack) {
                    chr.addMPHP(stats.getHp() > 1 ? -(stats.getHp() - 1) : 0, stats.getMp() > 1 && !mpAttack ? -(stats.getMp() - 1) : 0);
                } else {
                    chr.addMPHP(-damage, mpAttack ? 0 : -mpattack);
                }
            }
            if (!GameConstants.GMS) { //TODO JUMP
                chr.handleBattleshipHP(-damage);
            }
            if (chr.inPVP() && chr.getStat().getHPPercent() <= 20) {
                SkillFactory.getSkill(PlayerStats.getSkillByJob(93, chr.getJob())).getEffect(1).applyTo(chr);
            }
        }
        byte offset = 0;
        int offset_d = 0;
        if (slea.available() == 1) {
            offset = slea.readByte();
            if (offset == 1 && slea.available() >= 4) {
                offset_d = slea.readInt();
            }
            if (offset < 0 || offset > 2) {
                offset = 0;
            }
        }
        //c.sendPacket(CWvsContext.enableActions());
        chr.getMap().broadcastMessage(chr, CField.damagePlayer(chr.getId(), type, damage, monsteridfrom, direction, skillid, pDMG, pPhysical, pID, pType, pPos, offset, offset_d, fake), false);
    }

    public static void AranCombo(final MapleClient c, final MapleCharacter chr, int toAdd) {
        if (chr != null && chr.getJob() >= 2000 && chr.getJob() <= 2112) {
            short combo = chr.getCombo();
            final long curr = System.currentTimeMillis();

            if (combo > 0 && (curr - chr.getLastCombo()) > 7000) {
                // Official MS timing is 3.5 seconds, so 7 seconds should be safe.
                //chr.getCheatTracker().registerOffense(CheatingOffense.ARAN_COMBO_HACK);
                combo = 0;
            }
            combo = (short) Math.min(30000, combo + toAdd);
            chr.setLastCombo(curr);
            chr.setCombo(combo);

            c.sendPacket(CField.testCombo(combo));

            switch (combo) { // Hackish method xD
                case 10:
                case 20:
                case 30:
                case 40:
                case 50:
                case 60:
                case 70:
                case 80:
                case 90:
                case 100:
                    if (chr.getSkillLevel(21000000) >= (combo / 10)) {
                        SkillFactory.getSkill(21000000).getEffect(combo / 10).applyComboBuff(chr, combo);
                    }
                    break;
            }
        }
    }

    public static void UseItemEffect(final int itemId, final MapleClient c, final MapleCharacter chr) {
        if (itemId != 0) {
            final Item toUse = chr.getInventory(MapleInventoryType.CASH).findById(itemId);

            if (toUse == null || toUse.getItemId() != itemId || toUse.getQuantity() < 1) {
                c.sendPacket(CWvsContext.enableActions());
                return;
            }
        }
        chr.setItemEffect(itemId);
        if (itemId != 5510000) {
            chr.setItemEffect(itemId);
        }
        chr.getMap().broadcastMessage(chr, CField.itemEffect(chr.getId(), itemId), false);
    }

    public static void CancelItemEffect(final int id, final MapleCharacter chr) {
        chr.cancelEffect(MapleItemInformationProvider.getInstance().getItemEffect(-id), false, -1);
    }

    public static void CancelBuffHandler(final int sourceid, final MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            return;
        }
        final Skill skill = SkillFactory.getSkill(sourceid);
        if (skill != null) {
            if (skill.isChargeSkill()) {
                chr.setKeyDownSkill_Time(0);
                chr.getMap().broadcastMessage(chr, CField.skillCancel(chr, sourceid), false);
            } else {
                chr.cancelEffect(skill.getEffect(1), false, -1);
            }
        }
    }

    public static void CancelMech(final LittleEndianAccessor slea, final MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        int sourceid = slea.readInt();
        if (sourceid % 10000 < 1000 && SkillFactory.getSkill(sourceid) == null) {
            sourceid += 1000;
        }
        final Skill skill = SkillFactory.getSkill(sourceid);
        if (skill == null) { //not sure
            return;
        }
        if (skill.isChargeSkill()) {
            chr.setKeyDownSkill_Time(0);
            chr.getMap().broadcastMessage(chr, CField.skillCancel(chr, sourceid), false);
        } else {
            chr.cancelEffect(skill.getEffect(slea.readByte()), false, -1);
        }
    }

    public static void QuickSlot(final LittleEndianAccessor slea, final MapleCharacter chr) {
        if (slea.available() == 32 && chr != null) {
            final StringBuilder ret = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                ret.append(slea.readInt()).append(",");
            }
            ret.deleteCharAt(ret.length() - 1);
            chr.getQuestNAdd(MapleQuest.getInstance(GameConstants.QUICK_SLOT)).setCustomData(ret.toString());
        }
    }

    public static void SkillEffect(final LittleEndianAccessor slea, final MapleCharacter chr) {
        final int skillId = slea.readInt();
        if (skillId >= 91000000) { //guild/recipe? no
            chr.getClient().sendPacket(CWvsContext.enableActions());
            return;
        }
        final byte level = slea.readByte();
        final short direction = slea.readShort();
        final byte unk = slea.readByte(); // Added on v.82

        final Skill skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(skillId));
        if (chr == null || skill == null || chr.getMap() == null) {
            return;
        }
        final int skilllevel_serv = chr.getTotalSkillLevel(skill);

        if (skilllevel_serv > 0 && skilllevel_serv == level && (skillId == 33101005 || skill.isChargeSkill())) {
            chr.setKeyDownSkill_Time(System.currentTimeMillis());
            if (skillId == 33101005) {
                chr.setLinkMid(slea.readInt(), 0);
            }
            chr.getMap().broadcastMessage(chr, CField.skillEffect(chr, skillId, level, direction, unk), false);
        }
    }

    public static void SpecialMove(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        if (chr == null || chr.hasBlockedInventory() || chr.getMap() == null || slea.available() < 9) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        slea.skip(4); // Old X and Y
        int skillid = slea.readInt();
        if (skillid >= 91000000) { //guild/recipe? no
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (skillid == 23111008) { //spirits, hack
            skillid += Randomizer.nextInt(2);
        }
        if (skillid == 5211011) { //spirits, hack
            int bla = Randomizer.nextInt(10);
            if (bla > 5) {
                skillid += 4 + (int) (Math.random() * ((5 - 4) + 1));
            } else {
                skillid = 5211011;
            }
        }
        int skillLevel = slea.readByte();
        final Skill skill = SkillFactory.getSkill(skillid);
        if (skill == null || (GameConstants.isAngel(skillid) && (chr.getStat().equippedSummon % 10000) != (skillid % 10000)) || (chr.inPVP() && skill.isPVPDisabled())) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (chr.getTotalSkillLevel(GameConstants.getLinkedAranSkill(skillid)) <= 0 || chr.getTotalSkillLevel(GameConstants.getLinkedAranSkill(skillid)) != skillLevel) {
            if (!GameConstants.isMulungSkill(skillid) && !GameConstants.isPyramidSkill(skillid) && chr.getTotalSkillLevel(GameConstants.getLinkedAranSkill(skillid)) <= 0) {
                c.getSession().close();
                return;
            }
            if (GameConstants.isMulungSkill(skillid)) {
                if (chr.getMapId() / 10000 != 92502) {
                    return;
                } else {
                    if (chr.getMulungEnergy() < 10000) {
                        return;
                    }
                    chr.mulung_EnergyModify(false);
                }
            } else if (GameConstants.isPyramidSkill(skillid)) {
                if (chr.getMapId() / 10000 != 92602 && chr.getMapId() / 10000 != 92601) {
                    return;
                }
            }
        }
        if (GameConstants.isEventMap(chr.getMapId())) {
            for (MapleEventType t : MapleEventType.values()) {
                final MapleEvent e = ChannelServer.getInstance(chr.getClient().getWorld(), chr.getClient().getChannel()).getEvent(t);
                if (e.isRunning() && !chr.isGM()) {
                    for (int i : e.getType().mapids) {
                        if (chr.getMapId() == i) {
                            chr.dropMessage(5, "You may not use that here.");
                            return; //non-skill cannot use
                        }
                    }
                }
            }
        }
        skillLevel = chr.getTotalSkillLevel(GameConstants.getLinkedAranSkill(skillid));
        final MapleStatEffect effect = chr.inPVP() ? skill.getPVPEffect(skillLevel) : skill.getEffect(skillLevel);
        if (effect.isMPRecovery() && chr.getStat().getHp() < (chr.getStat().getMaxHp() / 100) * 10) { //less than 10% hp
            c.getPlayer().dropMessage(5, "You do not have the HP to use this skill.");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (effect.getCooldown(chr) > 0 && !chr.isGM()) {
            if (chr.skillisCooling(skillid) && false) {
                c.sendPacket(CWvsContext.enableActions());
                return;
            }
            if (skillid != 5221006 && skillid != 35111002) { // Battleship
                c.sendPacket(CField.skillCooldown(skillid, effect.getCooldown(chr)));
                chr.addCooldown(skillid, System.currentTimeMillis(), effect.getCooldown(chr) * 1000);
            }
        }
        //chr.checkFollow(); //not msea-like but ALEX'S WISHES
        switch (skillid) {
            case 1121001:
            case 1221001:
            case 1321001:
            case 9001020: // GM magnet
            case 9101020:
            case 31111003:
                final byte number_of_mobs = slea.readByte();
                slea.skip(3);
                for (int i = 0; i < number_of_mobs; i++) {
                    int mobId = slea.readInt();

                    final MapleMonster mob = chr.getMap().getMonsterByOid(mobId);
                    if (mob != null) {
//			chr.getMap().broadcastMessage(chr, CField.showMagnet(mobId, slea.readByte()), chr.getTruePosition());
                        mob.switchController(chr, mob.isControllerHasAggro());
                        mob.applyStatus(chr, new MonsterStatusEffect(MonsterStatus.STUN, 1, skillid, null, false), false, effect.getDuration(), true, effect);
                    }
                }
                chr.getMap().broadcastMessage(chr, EffectPacket.showBuffeffect(chr.getId(), skillid, 1, chr.getLevel(), skillLevel, slea.readByte()), chr.getTruePosition());
                c.sendPacket(CWvsContext.enableActions());
                break;
            case 30001061: //capture
                int mobID = slea.readInt();
                MapleMonster mob = chr.getMap().getMonsterByOid(mobID);
                if (mob != null) {
                    boolean success = mob.getHp() <= mob.getMobMaxHp() / 2 && mob.getId() >= 9304000 && mob.getId() < 9305000;
                    chr.getMap().broadcastMessage(chr, EffectPacket.showBuffeffect(chr.getId(), skillid, 1, chr.getLevel(), skillLevel, (byte) (success ? 1 : 0)), chr.getTruePosition());
                    if (success) {
                        chr.getQuestNAdd(MapleQuest.getInstance(GameConstants.JAGUAR)).setCustomData(String.valueOf((mob.getId() - 9303999) * 10));
                        chr.getMap().killMonster(mob, chr, true, false, (byte) 1);
                        chr.cancelEffectFromBuffStat(MapleBuffStatus.MONSTER_RIDING);
                        c.sendPacket(CWvsContext.updateJaguar(chr));
                    } else {
                        chr.dropMessage(5, "The monster has too much physical strength, so you cannot catch it.");
                    }
                }
                c.sendPacket(CWvsContext.enableActions());
                break;
            case 30001062: //hunter call
                chr.dropMessage(5, "No monsters can be summoned. Capture a monster first."); //lool
                c.sendPacket(CWvsContext.enableActions());
                break;
            case 33101005: //jaguar oshi
                mobID = chr.getFirstLinkMid();
                mob = chr.getMap().getMonsterByOid(mobID);
                chr.setKeyDownSkill_Time(0);
                chr.getMap().broadcastMessage(chr, CField.skillCancel(chr, skillid), false);
                if (mob != null) {
                    boolean success = mob.getStats().getLevel() < chr.getLevel() && mob.getId() < 9000000 && !mob.getStats().isBoss();
                    if (success) {
                        chr.getMap().broadcastMessage(MobPacket.suckMonster(mob.getObjectId(), chr.getId()));
                        chr.getMap().killMonster(mob, chr, false, false, (byte) -1);
                    } else {
                        chr.dropMessage(5, "The monster has too much physical strength, so you cannot catch it.");
                    }
                } else {
                    chr.dropMessage(5, "No monster was sucked. The skill failed.");
                }
                c.sendPacket(CWvsContext.enableActions());
                break;
            case 4341003: //monster bomb
                chr.setKeyDownSkill_Time(0);
                chr.getMap().broadcastMessage(chr, CField.skillCancel(chr, skillid), false);
            //fallthrough intended
            default:
                Point pos = null;
                if (slea.available() == 5 || slea.available() == 7) {
                    pos = slea.readPos();
                }
                if (effect.isMagicDoor()) { // Mystic Door
                    if (!FieldLimitType.MysticDoor.check(chr.getMap().getFieldLimit())) {
                        effect.applyTo(c.getPlayer(), pos);
                    } else {
                        c.sendPacket(CWvsContext.enableActions());
                    }
                } else {
                    final int mountid = MapleStatEffect.parseMountInfo(c.getPlayer(), skill.getId());
                    if (mountid != 0 && mountid != GameConstants.getMountItem(skill.getId(), c.getPlayer()) && !c.getPlayer().isIntern() && c.getPlayer().getBuffedValue(MapleBuffStatus.MONSTER_RIDING) == null && c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -122) == null) {
                        if (!GameConstants.isMountItemAvailable(mountid, c.getPlayer().getJob())) {
                            c.sendPacket(CWvsContext.enableActions());
                            return;
                        }
                    }
                    effect.applyTo(c.getPlayer(), pos);
                }
                break;
        }
    }

    public static void attack2(LittleEndianAccessor slea, MapleClient c, RecvPacketOpcode header) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        if (chr.hasBlockedInventory() || chr.getMap() == null) {
            chr.dropMessage(5, "現在還不能進行攻擊。");
            c.getSession().writeAndFlush(CWvsContext.enableActions());
            return;
        }
        if (chr.isIntern() && !chr.isAdmin() && chr.getMap().isBossMap()) {
            chr.dropMessage(5, "管理員不能打BOSS。");
            c.getSession().writeAndFlush(CWvsContext.enableActions());
            return;
        }
        if (!chr.isAdmin() && chr.getMap().isMarketMap()) {
            chr.dropMessage(5, "在自由市場無法使用技能。");
            c.getSession().writeAndFlush(CWvsContext.enableActions());
            return;
        }

        int level = chr.getLevel();
        switch (header) {
            case CP_UserMeleeAttack: // 近距離攻擊
            case CP_UserShootAttack: // 遠距離攻擊
            case CP_UserMagicAttack: // 魔法攻擊
            case CP_UserBodyAttack: // 被動攻擊, BUFF觸發的, 需要有BUFF才有效果
                AttackInfo attack = DamageParse.parseDamage(slea, chr, header);
                userAttack(attack, c, chr);
                break;
            case CP_SummonedAttack: // 召喚獸攻擊
                SummonHandler.SummonAttack(slea, c, chr);
                break;
        }
    }

    public static void userAttack(AttackInfo attack, MapleClient c, MapleCharacter chr) {
        if (attack == null) {
            chr.dropMessage(5, "當前狀態限制了攻擊。");
            c.getSession().writeAndFlush(CWvsContext.enableActions());
            return;
        }
    }

    public static void attack(LittleEndianAccessor slea, MapleClient c, RecvPacketOpcode header) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        if (chr.hasBlockedInventory() || chr.getMap() == null) {
            chr.dropMessage(5, "現在還不能進行攻擊。");
            c.getSession().writeAndFlush(CWvsContext.enableActions());
            return;
        }
        if (chr.isIntern() && !chr.isAdmin() && chr.getMap().isBossMap()) {
            chr.dropMessage(5, "管理員不能打BOSS。");
            c.getSession().writeAndFlush(CWvsContext.enableActions());
            return;
        }
        if (!chr.isAdmin() && chr.getMap().isMarketMap()) {
            chr.dropMessage(5, "在自由市場無法使用技能。");
            c.getSession().writeAndFlush(CWvsContext.enableActions());
            return;
        }

        int level = chr.getLevel();
        switch (header) {
            case CP_UserMeleeAttack: // 近距離攻擊
                closeRangeAttack(slea, c, chr);
                break;
            case CP_UserShootAttack: // 遠距離攻擊
                rangedAttack(slea, c, chr);
                break;
            case CP_UserMagicAttack: // 魔法攻擊
                magicAttack(slea, c, chr);
                break;
            case CP_UserBodyAttack: // 被動攻擊, BUFF觸發的, 需要有BUFF才有效果
                if (chr.getBuffedValue(MapleBuffStatus.ENERGY_CHARGE) == null
                        && //能量获得
                        chr.getBuffedValue(MapleBuffStatus.BODY_PRESSURE) == null
                        && //战神抗压
                        chr.getBuffedValue(MapleBuffStatus.DARK_AURA) == null
                        && //黑暗灵气
                        chr.getBuffedValue(MapleBuffStatus.TORNADO) == null
                        && //幻灵飓风
                        chr.getBuffedValue(MapleBuffStatus.SUMMON) == null
                        && //召唤兽
                        chr.getBuffedValue(MapleBuffStatus.RAINING_MINES) == null
                        && //地雷
                        chr.getBuffedValue(MapleBuffStatus.TELEPORT_MASTERY) == null) { //皮卡啾的品格
                    chr.dropMessage(5, "當前狀態限制了攻擊。");
                    c.getSession().writeAndFlush(CWvsContext.enableActions());
                    return;
                }
                passiveRangeAttack(slea, c, chr);
                break;
            case CP_SummonedAttack: // 召喚獸攻擊
                SummonHandler.SummonAttack(slea, c, chr);
                break;
        }
    }

    //方便解析
    public static void Data_Display(AttackInfo attack) {
        System.gc();
        System.err.println("ｔｂｙｅ　　　　" + attack.tbyte + " >> " + tools.HexTool.toString(attack.tbyte) + " [??]");
        System.err.println("攻擊數量　　　　" + attack.targets);
        System.err.println("攻擊次數　　　　" + attack.hits);
        System.err.println("技能代碼　　　　" + attack.skill + " >> " + tools.HexTool.toString(attack.skill) + " [?? ?? ?? ??]");
        System.err.println("Ｃｈａｒｇｅ　　" + attack.charge);
        System.err.println("方向　　　　　　" + attack.direction + " [0 = 右邊 | 80 = 左邊]");//unk
        System.err.println("動作　　　　　　" + attack.display + " >> " + tools.HexTool.toString(attack.display));
        System.err.println("攻擊速度　　　　" + attack.speed + " >> " + tools.HexTool.toString(attack.speed));
        System.err.println("最後使用時間　　" + attack.lastAttackTickCount + " >> " + tools.HexTool.toString(attack.lastAttackTickCount));
        System.err.println("遠Ｓｌｏｔ　　　" + attack.slot + " >> " + tools.HexTool.toString(attack.slot));
        System.err.println("遠Ｃｓｓｔａｒ　" + attack.csstar + " >> " + tools.HexTool.toString(attack.csstar));
        System.err.println("遠ＡＯＣ　　　　" + attack.AOE + " >> " + tools.HexTool.toString(attack.AOE));
        System.err.print("全部傷害　　　　");
        attack.allDamage.forEach(playerID -> {
            System.err.print("怪物ID:" + playerID.objectId + ", 傷害:" + playerID.attack);
            System.err.print("　");
        });
        System.err.println("");
        System.err.println("座標　　　　　　" + attack.position);
        System.gc();
    }

    public static void closeRangeAttack(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        PlayerHandler.closeRangeAttack(slea, c, chr, false);
    }

    public static void passiveRangeAttack(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        PlayerHandler.closeRangeAttack(slea, c, chr, true);
    }

    public static void closeRangeAttack(LittleEndianAccessor slea, MapleClient c, final MapleCharacter chr, boolean passive) {
        AttackInfo attack = DamageParse.parseCloseRangeAttack(slea, chr);
        Data_Display(attack);//方便解析
        if (attack == null) {
            chr.dropMessage(5, "攻擊出現錯誤。");
            c.getSession().writeAndFlush(CWvsContext.enableActions());
            return;
        }
        final boolean mirror = chr.getBuffedValue(MapleBuffStatus.SHADOWPARTNER) != null;
        double maxBaseDamage = chr.getStat().getCurrentMaxBaseDamage();
        Item shield = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -10);
        int maxAttackCount = (shield != null) && (ItemConstants.類型.雙刀(shield.getItemId())) ? 2 : 1;
        int skillLevel = 0;
        MapleStatEffect effect = null;
        Skill skill = null;

        if (attack.skill != 0) {
            skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(attack.skill));
            if (skill == null || (GameConstants.isAngel(attack.skill) && (chr.getStat().equippedSummon % 10000) != (attack.skill % 10000))) {
                c.sendPacket(CWvsContext.enableActions());
                return;
            }
            skillLevel = chr.getTotalSkillLevel(skill);
            effect = attack.getAttackEffect(chr, skillLevel, skill);
            if (effect == null) {
                if (chr.isShowErr()) {
                    chr.dropMessage(5, "近距離攻擊效果為空。使用技能: " + skill.getId() + " - " + skill.getName() + " 技能等級: " + skillLevel);
                }
                return;
            }
            if (GameConstants.isEventMap(chr.getMapId())) {
                for (MapleEventType t : MapleEventType.values()) {
                    MapleEvent e = ChannelServer.getInstance(c.getWorld(), c.getChannel()).getEvent(t);
                    if ((e.isRunning()) && (!chr.isGM())) {
                        for (int i : e.getType().mapids) {
                            if (chr.getMapId() == i) {
                                chr.dropMessage(5, "無法在這裡使用。");
                                return;
                            }
                        }
                    }
                }
            }

            maxBaseDamage *= (effect.getDamage() + chr.getStat().getDamageIncrease(attack.skill)) / 100.0;
            maxAttackCount = effect.getAttackCount() > effect.getBulletCount() ? effect.getAttackCount() : effect.getBulletCount();

            if (effect.getCooldown(chr) > 0 && !passive) {
                if (chr.skillisCooling(attack.skill)) {
                    chr.dropMessage(5, "技能由於冷卻時間限制，暫時無法使用。");
                    c.getSession().writeAndFlush(CWvsContext.enableActions());
                    return;
                }
                if (!chr.skillisCooling(attack.skill)) {
                    chr.addCooldown(attack.skill, System.currentTimeMillis(), effect.getCooldown(chr) * 1000);
                }
            }
        }

        attack = DamageParse.Modify_AttackCrit(attack, chr, 1, effect);
        maxAttackCount *= (mirror ? 2 : 1);

        if (!passive) {
            if ((chr.getMapId() == 109060000 || chr.getMapId() == 109060002 || chr.getMapId() == 109060004) && attack.skill == 0) {
                MapleSnowballs.hitSnowball(chr);
            }
            //消耗鬥氣的技能
            if (isFinisher(attack.skill) > 0) {
                int numFinisherOrbs = 0;
                Integer comboBuff = chr.getBuffedValue(MapleBuffStatus.COMBO);
                if (comboBuff != null) {
                    numFinisherOrbs = comboBuff - 1;
                }
                if (numFinisherOrbs <= 0) {
                    return;
                }
                chr.handleOrbconsume(isFinisher(attack.skill));
                maxBaseDamage *= numFinisherOrbs;
            }

            chr.checkFollow();

            switch (chr.getJob()) {
                case 511:
                case 512: {
                    chr.handleEnergyCharge(5110001, attack.targets * attack.hits);
                    break;
                }
                case 1510:
                case 1511:
                case 1512: {
                    chr.handleEnergyCharge(15100004, attack.targets * attack.hits);
                    break;
                }
            }

            if (attack.targets > 0 && attack.skill == 1211002) { // handle charged blow
                final int advcharge_level = chr.getSkillLevel(SkillFactory.getSkill(1220010));
                if (advcharge_level > 0) {
                    if (!SkillFactory.getSkill(1220010).getEffect(advcharge_level).makeChanceResult()) {
                        chr.cancelEffectFromBuffStat(MapleBuffStatus.WK_CHARGE);
                        chr.cancelEffectFromBuffStat(MapleBuffStatus.LIGHTNING_CHARGE);
                    }
                } else {
                    chr.cancelEffectFromBuffStat(MapleBuffStatus.WK_CHARGE);
                    chr.cancelEffectFromBuffStat(MapleBuffStatus.LIGHTNING_CHARGE);
                }
            }
        }
        chr.checkFollow();

        //給地圖上的玩家顯示當前玩家使用技能的效果
        byte[] packet;
        if (passive) {
            packet = CField.passiveAttack(chr, skillLevel, 0, attack);
        } else {
            packet = CField.closeRangeAttack(chr, skillLevel, 0, attack);
        }

        if (!chr.isHidden()) {
            chr.getMap().broadcastMessage(chr, packet, chr.getTruePosition());
        } else {
            chr.getMap().broadcastGMMessage(chr, packet, false);
        }

        DamageParse.applyAttack(attack, skill, c.getPlayer(), maxAttackCount, maxBaseDamage, effect, mirror ? AttackType.NON_RANGED_WITH_MIRROR : AttackType.NON_RANGED);

    }

    public static void rangedAttack(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        AttackInfo attack = DamageParse.parseRangedAttack(slea, chr);
        Data_Display(attack);//方便解析
        if (attack == null) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        int bulletCount = 1, skillLevel = 0;
        MapleStatEffect effect = null;
        Skill skill = null;
        boolean AOE = attack.skill == 4111004;
        boolean noBullet = (chr.getJob() >= 3500 && chr.getJob() <= 3512) || GameConstants.isPhantom(chr.getJob()) || GameConstants.isCannon(chr.getJob()) || GameConstants.isMercedes(chr.getJob()) || GameConstants.isJett(chr.getJob());
        if (attack.skill != 0) {
            skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(attack.skill));
            if (skill == null || (GameConstants.isAngel(attack.skill) && (chr.getStat().equippedSummon % 10000) != (attack.skill % 10000))) {
                c.sendPacket(CWvsContext.enableActions());
                return;
            }
            skillLevel = chr.getTotalSkillLevel(skill);
            effect = attack.getAttackEffect(chr, skillLevel, skill);
            if (effect == null) {
                return;
            }
            if (GameConstants.isEventMap(chr.getMapId())) {
                for (MapleEventType t : MapleEventType.values()) {
                    final MapleEvent e = ChannelServer.getInstance(chr.getClient().getWorld(), chr.getClient().getChannel()).getEvent(t);
                    if (e.isRunning() && !chr.isGM()) {
                        for (int i : e.getType().mapids) {
                            if (chr.getMapId() == i) {
                                chr.dropMessage(5, "You may not use that here.");
                                return; //non-skill cannot use
                            }
                        }
                    }
                }
            }
            switch (attack.skill) {
                case 13101005:
                case 21110004: // Ranged but uses attackcount instead
                case 14101006: // Vampure
                case 21120006:
                case 11101004:
                case 51001004:
                case 1077:
                case 1078:
                case 1079:
                case 11077:
                case 11078:
                case 5121013:
                case 15111008:
                case 5221013:
                case 11079:
                case 51121008:
                case 5121016:
                case 51111007:
                case 15111007:
                case 13111007: //Wind Shot
                case 33101007:
                case 33101002:
                case 33121002:
                case 4101008:
                case 4111013:
                case 14101008:
                case 33121001:
                case 21100004:
                case 21110011:
                case 21100007:
                case 21000004:
                case 3221001:
                case 5121002:
                case 4121003:
                case 4221003:
                case 3111004: // arrow rain
                case 5211008:
                case 5221017:
                case 5721001:
                case 5221004: // Rapidfire
                case 13111000: //   arrow rain
                case 3211004: // arrow eruption
                    AOE = true;
                    bulletCount = effect.getAttackCount();
                    break;
                case 35121005:
                case 35111004:
                case 35121013:
                    AOE = true;
                    bulletCount = 6;
                    break;
                default:
                    bulletCount = effect.getBulletCount();
                    break;
            }
            if (noBullet && effect.getBulletCount() < effect.getAttackCount()) {
                bulletCount = effect.getAttackCount();
            }
            if (effect.getCooldown(chr) > 0 && !chr.isGM() && ((attack.skill != 35111004 && attack.skill != 35121013) || chr.getBuffSource(MapleBuffStatus.MECH_CHANGE) != attack.skill)) {
                if (chr.skillisCooling(attack.skill)) {
                    c.sendPacket(CWvsContext.enableActions());
                    return;
                }
                c.sendPacket(CField.skillCooldown(attack.skill, effect.getCooldown(chr)));
                chr.addCooldown(attack.skill, System.currentTimeMillis(), effect.getCooldown(chr) * 1000);
            }
        }
        attack = DamageParse.Modify_AttackCrit(attack, chr, 2, effect);
        final Integer ShadowPartner = chr.getBuffedValue(MapleBuffStatus.SHADOWPARTNER);
        if (ShadowPartner != null) {
            bulletCount *= 2;
        }
        int projectile = 0, visProjectile = 0;
        if (!AOE && chr.getBuffedValue(MapleBuffStatus.SOULARROW) == null && !noBullet) {
            Item ipp = chr.getInventory(MapleInventoryType.USE).getItem(attack.slot);
            if (ipp == null) {
                return;
            }
            projectile = ipp.getItemId();

            if (attack.csstar > 0) {
                if (chr.getInventory(MapleInventoryType.CASH).getItem(attack.csstar) == null) {
                    return;
                }
                visProjectile = chr.getInventory(MapleInventoryType.CASH).getItem(attack.csstar).getItemId();
            } else {
                visProjectile = projectile;
            }
            // Handle bulletcount
            if (chr.getBuffedValue(MapleBuffStatus.SPIRIT_CLAW) == null) {
                int bulletConsume = bulletCount;
                if (effect != null && effect.getBulletConsume() != 0) {
                    bulletConsume = effect.getBulletConsume() * (ShadowPartner != null ? 2 : 1);
                }
                if (chr.getJob() == 412 && bulletConsume > 0 && ipp.getQuantity() < MapleItemInformationProvider.getInstance().getSlotMax(projectile)) {
                    final Skill expert = SkillFactory.getSkill(4120010);
                    if (chr.getTotalSkillLevel(expert) > 0) {
                        final MapleStatEffect eff = expert.getEffect(chr.getTotalSkillLevel(expert));
                        if (eff.makeChanceResult()) {
                            ipp.setQuantity((short) (ipp.getQuantity() + 1));
                            c.getSession().writeAndFlush(InventoryPacket.updateInventorySlot(ipp, false));
                            bulletConsume = 0;
                            c.getSession().writeAndFlush(InventoryPacket.updateInventoryFull());
                        }
                    }
                }
                if (bulletConsume > 0) {
                    if (!MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, projectile, bulletConsume, false, true)) {
                        chr.dropMessage(5, "You do not have enough arrows/bullets/stars.");
                        return;
                    }
                }
            }
        } else if (chr.getJob() >= 3500 && chr.getJob() <= 3512 || GameConstants.isJett(chr.getJob())) {
            visProjectile = 2333000;
        } else if (GameConstants.isCannon(chr.getJob())) {
            visProjectile = 2333001;
        }

        int projectileWatk = 0;
        if (projectile != 0) {
            projectileWatk = MapleItemInformationProvider.getInstance().getWatkForProjectile(projectile);
        }

        PlayerStats statst = chr.getStat();
        double basedamage;
        switch (attack.skill) {
            case 4001344:
            case 4121007:
            case 14001004:
            case 14111005:
                basedamage = Math.max(statst.getCurrentMaxBaseDamage(), statst.getTotalLuk() * 5.0F * (statst.getTotalWatk() + projectileWatk) / 100.0F);
                break;
            case 4111004:
                basedamage = 53000.0D;
                break;
            default:
                basedamage = statst.getCurrentMaxBaseDamage();
                switch (attack.skill) {
                    case 3101005:
                        basedamage *= effect.getX() / 100.0D;
                        break;
                }
        }

        if (effect != null) {
            basedamage *= (effect.getDamage() + statst.getDamageIncrease(attack.skill)) / 100.0D;
            int money = effect.getMoneyCon();
            if (money != 0) {
                if (money > chr.getMeso()) {
                    money = chr.getMeso();
                }
                chr.gainMeso(-money, false);
            }
        }
        chr.checkFollow();
        if (!chr.isHidden()) {
            chr.getMap().broadcastMessage(chr, CField.rangedAttack(chr, skillLevel, visProjectile, attack), chr.getTruePosition());
        } else {
            chr.getMap().broadcastGMMessage(chr, CField.rangedAttack(chr, skillLevel, visProjectile, attack), false);
        }

        DamageParse.applyAttack(attack, skill, chr, bulletCount, basedamage, effect, ShadowPartner != null ? AttackType.RANGED_WITH_SHADOWPARTNER : AttackType.RANGED);
        WeakReference<MapleCharacter>[] clones = chr.getClones();
        for (int i = 0; i < clones.length; i++) {
            if (clones[i].get() != null) {
                final MapleCharacter clone = clones[i].get();
                final Skill skil2 = skill;
                final MapleStatEffect eff2 = effect;
                final double basedamage2 = basedamage;
                final int bulletCount2 = bulletCount;
                final int visProjectile2 = visProjectile;
                final int skillLevel2 = skillLevel;
                final AttackInfo attack2 = DamageParse.DivideAttack(attack, chr.isGM() ? 1 : 4);
                Timer.CloneTimer.getInstance().schedule(() -> {
                    if (!clone.isHidden()) {
                        clone.getMap().broadcastMessage(CField.rangedAttack(clone, skillLevel2, visProjectile2, attack2));
                    } else {
                        clone.getMap().broadcastGMMessage(clone, CField.rangedAttack(clone, skillLevel2, visProjectile2, attack2), false);
                    }
                    DamageParse.applyAttack(attack2, skil2, chr, bulletCount2, basedamage2, eff2, AttackType.RANGED);
                }, 500 * i + 500);
            }
        }
    }

    public static void magicAttack(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        AttackInfo attack = DamageParse.parseMagicDamage(slea, chr);
        Data_Display(attack);//方便解析
        if (attack == null) {
            System.out.println("Return 2");
            c.getSession().writeAndFlush(CWvsContext.enableActions());
            return;
        }
        double maxdamage = chr.getStat().getCurrentMaxBaseDamage();
        int attackCount = 1;
        final Skill skill = SkillFactory.getSkill(GameConstants.getLinkedAranSkill(attack.skill));
        if (skill == null && (chr.getStat().equippedSummon % 10000) != (attack.skill % 10000)) {
            c.getSession().writeAndFlush(CWvsContext.enableActions());
            System.out.println("Return 3");
            return;
        }
        final int skillLevel = chr.getTotalSkillLevel(skill);
        final MapleStatEffect effect = attack.getAttackEffect(chr, skillLevel, skill);
        if (effect == null) {
            System.out.println("Return 4");
            return;
        }
        attack = DamageParse.Modify_AttackCrit(attack, chr, 3, effect);
        if (GameConstants.isEventMap(chr.getMapId())) {
            for (MapleEventType t : MapleEventType.values()) {
                final MapleEvent e = ChannelServer.getInstance(c.getWorld(), c.getChannel()).getEvent(t);
                if (e.isRunning() && !chr.isGM()) {
                    for (int i : e.getType().mapids) {
                        if (chr.getMapId() == i) {
                            chr.dropMessage(5, "You may not use that here.");
                            System.out.println("Return 5");
                            return; //non-skill cannot use
                        }
                    }
                }
            }
        }
        if (effect.getCooldown(chr) > 0) {
            if (chr.skillisCooling(attack.skill)) {
                c.getSession().writeAndFlush(CWvsContext.enableActions());
                System.out.println("Return 6");
                return;
            }
            chr.addCooldown(attack.skill, System.currentTimeMillis(), effect.getCooldown(chr) * 1000);
        }
        chr.checkFollow();
        if (!chr.isHidden()) {
            chr.getMap().broadcastMessage(chr, CField.magicAttack(chr, skillLevel, 0, attack), chr.getTruePosition());
        } else {
            chr.getMap().broadcastGMMessage(chr, CField.magicAttack(chr, skillLevel, 0, attack), false);
        }
//        DamageParse.applyAttackMagic(attack, skill, c.getPlayer(), effect, maxdamage);
        DamageParse.applyAttack(attack, skill, c.getPlayer(), attackCount, maxdamage, effect, AttackType.MAGIC);
        WeakReference<MapleCharacter>[] clones = chr.getClones();
        for (int i = 0; i < clones.length; i++) {
            if (clones[i].get() != null) {
                final MapleCharacter clone = clones[i].get();
                final Skill skil2 = skill;
                final MapleStatEffect eff2 = effect;
                final double maxd = maxdamage;
                final int attackCount2 = attackCount;
                final int skillLevel2 = skillLevel;
                final AttackInfo attack2 = DamageParse.DivideAttack(attack, chr.isGM() ? 1 : 4);
                CloneTimer.getInstance().schedule(new Runnable() {

                    public void run() {
                        if (!clone.isHidden()) {
                            clone.getMap().broadcastMessage(CField.magicAttack(clone, skillLevel2, 0, attack2));
                        } else {
                            clone.getMap().broadcastGMMessage(clone, CField.magicAttack(clone, skillLevel2, 0, attack2), false);
                        }
//                        DamageParse.applyAttackMagic(attack2, skil2, chr, eff2, maxd);
                        DamageParse.applyAttack(attack2, skil2, chr, attackCount2, maxd, eff2, AttackType.MAGIC);
                    }
                }, 500 * i + 500);
            }
        }
    }

    public static void DropMeso(final int meso, final MapleCharacter chr) {
        if (!chr.isAlive() || (meso < 10 || meso > 50000) || (meso > chr.getMeso())) {
            chr.getClient().sendPacket(CWvsContext.enableActions());
            return;
        }
        chr.gainMeso(-meso, false, true);
        chr.getMap().spawnMesoDrop(meso, chr.getTruePosition(), chr, chr, true, (byte) 0);
        // chr.getCheatTracker().checkDrop(true);
    }

    public static void ChangeAndroidEmotion(final int emote, final MapleCharacter chr) {
        if (emote > 0 && chr != null && chr.getMap() != null && !chr.isHidden() && emote <= 17 && chr.getAndroid() != null) { //O_o
            chr.getMap().broadcastMessage(CField.showAndroidEmotion(chr.getId(), emote));
        }
    }

    public static void MoveAndroid(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        slea.skip(8);

        if (chr.getAndroid() == null) {
            return;
        }
        final List<ILifeMovementFragment> res = MovementParse.parseMovement(slea, chr.getAndroid().getPos(), MovementKind.PET_MOVEMENT);

        if (res != null && chr != null && !res.isEmpty() && chr.getMap() != null && chr.getAndroid() != null) { // map crash hack
            final Point pos = new Point(chr.getAndroid().getPos());
            chr.getAndroid().updatePosition(res);
            chr.getMap().broadcastMessage(chr, CField.moveAndroid(chr.getId(), pos, res), false);
        }
    }

    public static void ChangeEmotion(final int emote, final MapleCharacter chr) {
        if (emote > 7) {
            final int emoteid = 5159992 + emote;
            final MapleInventoryType type = GameConstants.getInventoryType(emoteid);
            if (chr.getInventory(type).findById(emoteid) == null) {
                return;
            }
        }
        if (emote > 0 && chr != null && chr.getMap() != null && !chr.isHidden()) { //O_o
            if (chr.getWatcher() != null) {
                chr.getWatcher().dropMessage(5, "" + chr.getName() + " has used Emotion: F" + emote);
            }
            chr.getMap().broadcastMessage(chr, CField.facialExpression(chr, emote), false);

            WeakReference<MapleCharacter>[] clones = chr.getClones();
            for (int i = 0; i < clones.length; i++) {
                if (clones[i].get() != null) {
                    final MapleCharacter clone = clones[i].get();
                    CloneTimer.getInstance().schedule(() -> {
                        if (chr.isHidden()) {
                            chr.getMap().broadcastGMMessage(null, CField.facialExpression(clone, emote), true);
                        } else {
                            clone.getMap().broadcastMessage(CField.facialExpression(clone, emote));
                        }
                    }, 500 * i + 500);
                }
            }
        }
    }

    public static void Heal(final LittleEndianAccessor slea, final MapleCharacter chr) {
        if (chr == null) {
            return;
        }

        slea.readInt();

        if (slea.available() >= 8L) {
            slea.skip(slea.available() >= 12L ? 8 : 4);
        }

        int healHP = slea.readShort();
        int healMP = slea.readShort();

        final PlayerStats stats = chr.getStat();

        if (stats.getHp() <= 0) {
            return;
        }

        final long now = System.currentTimeMillis();
        if (healHP != 0 && chr.canHP(now + 1000)) {
            if (healHP > stats.getHealHP()) {
                //chr.getCheatTracker().registerOffense(CheatingOffense.REGEN_HIGH_HP, String.valueOf(healHP));
                healHP = (int) stats.getHealHP();
            }
            chr.addHP(healHP);
        }
        if (healMP != 0 && !GameConstants.isDemon(chr.getJob()) && chr.canMP(now + 1000)) { //just for lag
            if (healMP > stats.getHealMP()) {
                //chr.getCheatTracker().registerOffense(CheatingOffense.REGEN_HIGH_MP, String.valueOf(healMP));
                healMP = (int) stats.getHealMP();
            }
            chr.addMP(healMP);
        }
    }

    public static void MovePlayer(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        //System.out.println("Move Player " + slea.toString());
        slea.skip(1); // portal count
        slea.skip(4); // crc?
        slea.skip(4); // tickcount
        final Point startPos = slea.readPos();
        slea.skip(4);
        if (chr == null) {
            return;
        }
        final Point Original_Pos = chr.getPosition(); // 4 bytes Added on v.80 MSEA
        final List<ILifeMovementFragment> res;
        try {
            res = MovementParse.parseMovement(slea, chr.getTruePosition(), MovementKind.PLAYER_MOVEMENT);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("AIOBE Type1:\n" + slea.toString(true));
            return;
        }

        int unk = slea.readByte();
        for (int i = 0;; i += 2) {
            if (i >= unk) {
                break;
            }
            slea.readByte();
        }

        slea.readShort();
        slea.readShort();
        slea.readShort();
        slea.readShort();

        if (res != null && c.getPlayer().getMap() != null) {
            final MapleMap map = c.getPlayer().getMap();

            if (chr.isHidden()) {
                chr.setLastRes(res);
                c.getPlayer().getMap().broadcastGMMessage(chr, CField.movePlayer(chr.getId(), res, Original_Pos), false);
            } else {
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), CField.movePlayer(chr.getId(), res, Original_Pos), false);
            }

            MovementParse.updatePosition(res, chr, 0);
            final Point pos = chr.getTruePosition();
            map.movePlayer(chr, pos);
            if (chr.getFollowId() > 0 && chr.isFollowOn() && chr.isFollowInitiator()) {
                final MapleCharacter fol = map.getCharacterById(chr.getFollowId());
                if (fol != null) {
                    final Point original_pos = fol.getPosition();
                    fol.getClient().sendPacket(CField.moveFollow(Original_Pos, original_pos, pos, res));
                    MovementParse.updatePosition(res, fol, 0);
                    map.movePlayer(fol, pos);
                    map.broadcastMessage(fol, CField.movePlayer(fol.getId(), res, original_pos), false);
                    if ((fol.getId() == 45 && fol.getMapId() == 910000000) && (fol.getPosition().x >= 340 && fol.getPosition().x <= 430) && fol.getPosition().y == 82) {
                        fol.setChair(0);
                        fol.getClient().sendPacket(CField.cancelChair(-1));
                        fol.getMap().broadcastMessage(fol, CField.showChair(fol.getId(), 0), false);
                        fol.getDiseaseBuff(MapleBuffStatus.SEDUCE, MobSkillFactory.getMobSkill(128, 10));
                    }
                } else {
                    chr.checkFollow();
                }
            }
            WeakReference<MapleCharacter>[] clones = chr.getClones();
            for (int i = 0; i < clones.length; i++) {
                if (clones[i].get() != null) {
                    final MapleCharacter clone = clones[i].get();
                    final List<ILifeMovementFragment> res3 = res;
                    Timer.CloneTimer.getInstance().schedule(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (clone.getMap() == map) {
                                    if (clone.isHidden()) {
                                        map.broadcastGMMessage(clone, CField.movePlayer(clone.getId(), res3, Original_Pos), false);
                                    } else {
                                        map.broadcastMessage(clone, CField.movePlayer(clone.getId(), res3, Original_Pos), false);
                                    }
                                    MovementParse.updatePosition(res3, clone, 0);
                                    map.movePlayer(clone, pos);
                                }
                            } catch (Exception e) {
                                //very rarely swallowed
                            }
                        }
                    }, 500 * i + 500);
                }
            }
            if (c.getPlayer().getWatcher() != null) { // todo: update this lol
                if (!c.getPlayer().Spam(10000, 27)) {
                    c.getPlayer().getWatcher().dropMessage(5, "" + c.getPlayer().getName() + " is moving.");
                }
            }
            int count = c.getPlayer().getFallCounter();
            final boolean samepos = pos.y > c.getPlayer().getOldPosition().y && Math.abs(pos.x - c.getPlayer().getOldPosition().x) < 5;
            if (samepos && (pos.y > (map.getBottom() + 250) || map.getFootholds().findBelow(pos) == null)) {
                if (count > 5) {
                    c.getPlayer().changeMap(map, map.getPortal(0));
                    c.getPlayer().setFallCounter(0);
                } else {
                    c.getPlayer().setFallCounter(++count);
                }
            } else if (count > 0) {
                c.getPlayer().setFallCounter(0);
            }
            c.getPlayer().setOldPosition(pos);

            if (!samepos && c.getPlayer().getBuffSource(MapleBuffStatus.DARK_AURA) == 32120000) { //dark aura
                c.getPlayer().getStatForBuff(MapleBuffStatus.DARK_AURA).applyMonsterBuff(c.getPlayer());
            } else if (!samepos && c.getPlayer().getBuffSource(MapleBuffStatus.YELLOW_AURA) == 32120001) { //yellow aura
                c.getPlayer().getStatForBuff(MapleBuffStatus.YELLOW_AURA).applyMonsterBuff(c.getPlayer());
            }
        }
    }

    public static void ChangeMapSpecial(final String portal_name, final MapleClient c, final MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            return;
        }
        final MaplePortal portal = chr.getMap().getPortal(portal_name);
        if (portal != null) {
            portal.enterPortal(c);
        } else {
            c.sendPacket(CWvsContext.enableActions());
        }
    }

    public static void ChangeMap(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        /**
         *
         * 40 00 01 FF FF FF FF 05 00 6F 75 74 30 30 36 04 E5 01 00 00 00 00 00
         * 00
         *
         * *
         */
        if (chr == null || chr.getMap() == null) {
            return;
        }
        if (slea.available() != 0) {
            //slea.skip(6); //D3 75 00 00 00 00
            slea.readByte(); // 1 = from dying 2 = regular portals
            int targetId = slea.readInt(); // FF FF FF FF

            final MaplePortal portal = chr.getMap().getPortal(slea.readMapleAsciiString());
            slea.skip(1);
            final int wheelType = slea.readShort();
            final boolean wheel = wheelType > 0 && !GameConstants.isEventMap(chr.getMapId()) && chr.haveItem(5510000, 1, false, true) && chr.getMapId() / 1000000 != 925;

            if (targetId != -1 && !chr.isAlive()) {
                chr.setStance(0);
                if (chr.getEventInstance() != null && chr.getEventInstance().revivePlayer(chr) && chr.isAlive()) {
                    return;
                }
                if (chr.getPyramidSubway() != null) {
                    chr.getStat().setHp((short) 50, chr);
                    chr.getPyramidSubway().fail(chr);
                    return;
                }

                if (!wheel) {
                    chr.getStat().setHp((short) 50, chr);

                    final MapleMap to = chr.getMap().getReturnMap();
                    chr.changeMap(to, to.getPortal(0));
                } else {
                    c.sendPacket(EffectPacket.useWheel((byte) (chr.getInventory(MapleInventoryType.CASH).countById(5510000) - 1)));
                    chr.getStat().setHp(((chr.getStat().getMaxHp() / 100) * 40), chr);
                    MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, 5510000, 1, true, false);

                    final MapleMap to = chr.getMap();
                    chr.changeMap(to, to.getPortal(0));
                }
            } else if (targetId != -1 && chr.isIntern()) {
                final MapleMap to = ChannelServer.getInstance(c.getWorld(), c.getChannel()).getMapFactory().getMap(targetId);
                if (to != null) {
                    chr.changeMap(to, to.getPortal(0));
                } else {
                    chr.dropMessage(5, "Map is NULL. Use !warp <mapid> instead.");
                }

            } else if (targetId != -1) {
                int divi = chr.getMapId() / 100;
                boolean unlock = false;
                boolean warp = false;
                if (chr.getMapId() == 4000005) {
                    warp = targetId == 104000000;
                    unlock = targetId == 104000000;
                } else if (chr.getMapId() == 743020100) {
                    warp = targetId == 743030000;
                } else if (chr.getMapId() == 743020101) {
                    warp = targetId == 743030002;
                } else if (chr.getMapId() == 743020102) {
                    warp = targetId == 743000203;
                } else if (chr.getMapId() == 743020103) {
                    warp = targetId == 743020402;
                } else if (chr.getMapId() == 743020200) {
                    warp = targetId == 743030001;
                } else if (chr.getMapId() == 743020201) {
                    warp = targetId == 743030003;
                } else if (chr.getMapId() == 743020401) {
                    warp = targetId == 743030201;
                } else if (chr.getMapId() == 743020400) {
                    warp = targetId == 743020000;
                } else if (chr.getMapId() == 912060300) {
                    warp = targetId == 912060400;
                } else if (chr.getMapId() == 912060400) {
                    warp = targetId == 912060500;
                } else if (chr.getMapId() == 913070071) {
                    warp = targetId == 130000000;
                    unlock = true;
                } else if (divi == 9130401) {
                    warp = (targetId / 100 == 9130400) || (targetId / 100 == 9130401);
                    if (targetId / 10000 != 91304) {
                        warp = true;
                        unlock = true;
                        targetId = 130030000;
                    }
                } else if (divi == 9130400) {
                    warp = (targetId / 100 == 9130400) || (targetId / 100 == 9130401);
                    if (targetId / 10000 != 91304) {
                        warp = true;
                        unlock = true;
                        targetId = 130030000;
                    }
                } else if (divi == 9140900) {
                    warp = (targetId == 914090011) || (targetId == 914090012) || (targetId == 914090013) || (targetId == 140090000);
                } else if ((divi == 9120601) || (divi == 9140602) || (divi == 9140603) || (divi == 9140604) || (divi == 9140605)) {
                    warp = (targetId == 912060100) || (targetId == 912060200) || (targetId == 912060300) || (targetId == 912060400) || (targetId == 912060500) || (targetId == 3000100);
                    unlock = true;
                } else if (divi == 9101500) {
                    warp = (targetId == 910150006) || (targetId == 101050010);
                    unlock = true;
                } else if ((divi == 9140901) && (targetId == 140000000)) {
                    unlock = true;
                    warp = true;
                } else if ((divi == 9240200) && (targetId == 924020000)) {
                    unlock = true;
                    warp = true;
                } else if ((targetId == 980040000) && (divi >= 9800410) && (divi <= 9800450)) {
                    warp = true;
                } else if ((divi == 9140902) && ((targetId == 140030000) || (targetId == 140000000))) {
                    unlock = true;
                    warp = true;
                } else if ((divi == 9000900) && (targetId / 100 == 9000900) && (targetId > chr.getMapId())) {
                    warp = true;
                } else if ((divi / 1000 == 9000) && (targetId / 100000 == 9000)) {
                    unlock = (targetId < 900090000) || (targetId > 900090004);
                    warp = true;
                } else if ((divi / 10 == 1020) && (targetId == 1020000 || targetId == 4000026)) {
                    unlock = true;
                    warp = true;
                } else if ((chr.getMapId() == 900090101) && (targetId == 100030100)) {
                    unlock = true;
                    warp = true;
                } else if ((chr.getMapId() == 2010000) && (targetId == 104000000)) {
                    unlock = true;
                    warp = true;
                } else if ((chr.getMapId() == 106020001) || (chr.getMapId() == 106020502)) {
                    if (targetId == chr.getMapId() - 1) {
                        unlock = true;
                        warp = true;
                    }
                } else if ((chr.getMapId() == 0) && (targetId == 10000)) {
                    unlock = true;
                    warp = true;
                } else if ((chr.getMapId() == 931000011) && (targetId == 931000012)) {
                    unlock = true;
                    warp = true;
                } else if ((chr.getMapId() == 931000021) && (targetId == 931000030)) {
                    unlock = true;
                    warp = true;
                }
                if (unlock) {
                    c.getSession().writeAndFlush(CField.UIPacket.lockUI(false));
                    c.getSession().writeAndFlush(CField.UIPacket.disableOthers(false));
                    c.getSession().writeAndFlush(CField.UIPacket.lockKey(false));
                    c.getSession().writeAndFlush(CWvsContext.enableActions());
                }
                if (warp) {
                    MapleMap to = c.getChannelServer().getMapFactory().getMap(targetId);
                    chr.changeMap(to, to.getPortal(0));
                } else if (ServerConstants.DEBUG) {
                    chr.showInfo("未觸發傳送", true, "unlock-" + unlock + " warp-" + warp + " targetId-" + targetId);
                    c.getSession().writeAndFlush(CWvsContext.enableActions());
                }
            } else {
                if (portal != null && !chr.hasBlockedInventory()) {
                    portal.enterPortal(c);
                } else {
                    c.sendPacket(CWvsContext.enableActions());
                }
            }
        }
    }

    public static final void InnerPortal(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (chr.getMap() == null)) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        MaplePortal portal = chr.getMap().getPortal(slea.readMapleAsciiString());
        int toX = slea.readShort();
        int toY = slea.readShort();
        if (portal == null) {
            if ((portal.getPosition().distanceSq(chr.getTruePosition()) > 22500.0D) && (!chr.isGM())) {
                c.sendPacket(CWvsContext.enableActions());
                return;
            }
        }
        chr.getMap().movePlayer(chr, new Point(toX, toY));
        chr.checkFollow();
    }

    public static void snowBall(LittleEndianAccessor slea, MapleClient c) {
        //B2 00
        //01 [team]
        //00 00 [unknown]
        //89 [position]
        //01 [stage]
        c.sendPacket(CWvsContext.enableActions());
        //empty, we do this in closerange
    }

    public static void leftKnockBack(LittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer().getMapId() / 10000 == 10906) { //must be in snowball map or else its like infinite FJ
            c.sendPacket(CField.leftKnockBack());
            c.sendPacket(CWvsContext.enableActions());
        }
    }

    public static void ReIssueMedal(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        final MapleQuest q = MapleQuest.getInstance(slea.readShort());
        final int itemid = q.getMedalItem();
        if (itemid != slea.readInt() || itemid <= 0 || q == null || chr.getQuestStatus(q.getId()) != 2) {
            c.sendPacket(UIPacket.reissueMedal(itemid, 4));
            return;
        }
        if (chr.haveItem(itemid, 1, true, true)) {
            c.sendPacket(UIPacket.reissueMedal(itemid, 3));
            return;
        }
        if (!MapleInventoryManipulator.checkSpace(c, itemid, (short) 1, "")) {
            c.sendPacket(UIPacket.reissueMedal(itemid, 2));
            return;
        }
        if (chr.getMeso() < 100) {
            c.sendPacket(UIPacket.reissueMedal(itemid, 1));
            return;
        }
        chr.gainMeso(-100, true, true);
        MapleInventoryManipulator.addById(c, itemid, (short) 1, "Redeemed item through medal quest " + q.getId() + " on " + FileoutputUtil.CurrentReadable_Date());
        c.sendPacket(UIPacket.reissueMedal(itemid, 0));
    }
}
