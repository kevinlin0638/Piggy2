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

import server.status.MapleBuffStatus;
import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.PetCommand;
import constants.GameConstants;
import constants.Occupations;
import java.awt.Point;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.maps.FieldLimitType;
import server.maps.MapleMapObjectType;
import server.movement.ILifeMovementFragment;
import server.movement.MovementKind;
import tools.data.LittleEndianAccessor;
import tools.packet.CField.EffectPacket;
import tools.packet.CWvsContext;
import tools.packet.MobPacket;
import tools.packet.PetPacket;

import java.util.List;

public class PetHandler {

    public static void SpawnPet(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        //[9C 00] [4D 05 85 06] [0A] [00] v145
        slea.readInt();
        chr.spawnPet(slea.readByte(), slea.readByte() > 0);

    }

    public static void Pet_AutoPotion(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        slea.skip(GameConstants.GMS ? 9 : 1);
        slea.readInt();
        final short slot = slea.readShort();
        if (chr == null || !chr.isAlive() || chr.getMapId() == 749040100 || chr.getMap() == null || chr.hasDisease(MapleBuffStatus.POTION)) {
            return;
        }
        final Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != slea.readInt()) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        final long time = System.currentTimeMillis();
        if (chr.getNextConsume() > time) {
            chr.dropMessage(5, "You may not use this item yet.");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (!FieldLimitType.PotionUse.check(chr.getMap().getFieldLimit())) { //cwk quick hack
            if (MapleItemInformationProvider.getInstance().getItemEffect(toUse.getItemId()).applyTo(chr)) {
                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, false);
                if (chr.getMap().getConsumeItemCoolTime() > 0) {
                    chr.setNextConsume(time + (chr.getMap().getConsumeItemCoolTime() * 1000));
                }
            }
        } else {
            c.sendPacket(CWvsContext.enableActions());
        }
    }

    public static void PetChat(final int petid, final short command, final String text, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null || chr.getSpawnPet(petid) == null) {
            return;
        }
        //新架構 [寵物]
        chr.getMap().broadcastMessage(chr, PetPacket.petChat(chr.getId(), command, text, (byte) petid), true);
    }

    public static void PetCommand(final MaplePet pet, final PetCommand petCommand, final MapleClient c, final MapleCharacter chr) {

        if (petCommand == null) {
            return;
        }
        byte petIndex = (byte) chr.getPetIndex(pet);
        boolean success = false;
        if (Randomizer.nextInt(99) <= petCommand.getProbability()) {
            success = true;
            if (pet.getCloseness() < 30000) {
                int newCloseness = pet.getCloseness() + (petCommand.getIncrease() * c.getWorldServer().getTraitRate());
                if (newCloseness > 30000) {
                    newCloseness = 30000;
                }
                pet.setCloseness(newCloseness);
                if (newCloseness >= GameConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                    pet.setLevel(pet.getLevel() + 1);
                    c.sendPacket(EffectPacket.showOwnPetLevelUp(petIndex));
                    //新架構 [寵物]
                    chr.getMap().broadcastMessage(PetPacket.showPetLevelUp(chr, petIndex));
                }
                //新架構 [寵物]
                c.getSession().write(PetPacket.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()), true));
            }
        }
        //新架構 [寵物]
        chr.getMap().broadcastMessage(PetPacket.commandResponse(chr.getId(), (byte) petCommand.getSkillId(), petIndex, success, false));
    }

    public static void PetFood(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        int previousFullness = 99;
        MaplePet pet = null;
        if (chr == null) {
            return;
        }
        for (final MaplePet pets : chr.getPets()) {
            if (pets.getSummoned()) {
                if (pets.getFullness() < previousFullness) {
                    previousFullness = pets.getFullness();
                    pet = pets;
                }
            }
        }
        if (pet == null) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }

        slea.readInt();
        short slot = slea.readShort();
        final int itemId = slea.readInt();
        Item petFood = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
        if (petFood == null || petFood.getItemId() != itemId || petFood.getQuantity() <= 0 || itemId / 10000 != 212) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        boolean gainCloseness = false;

        if (Randomizer.nextInt(99) <= 50) {
            gainCloseness = true;
        }
        if (pet.getFullness() < 100) {
            int newFullness = pet.getFullness() + 30;
            if (newFullness > 100) {
                newFullness = 100;
            }
            pet.setFullness(newFullness);
            final byte index = chr.getPetIndex(pet);

            if (gainCloseness && pet.getCloseness() < 30000) {
                int newCloseness = pet.getCloseness() + 1;
                if (newCloseness > 30000) {
                    newCloseness = 30000;
                }
                pet.setCloseness(newCloseness);
                if (newCloseness >= GameConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                    pet.setLevel(pet.getLevel() + 1);

                    c.sendPacket(EffectPacket.showOwnPetLevelUp(index));
                    //新架構 [寵物]
                    chr.getMap().broadcastMessage(PetPacket.showPetLevelUp(chr, index));
                }
            }
            //新架構 [寵物]
            c.sendPacket(PetPacket.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()), true));
            chr.getMap().broadcastMessage(c.getPlayer(), PetPacket.commandResponse(chr.getId(), (byte) 1, index, true, true), true);
        } else {
            if (gainCloseness) {
                int newCloseness = pet.getCloseness() - 1;
                if (newCloseness < 0) {
                    newCloseness = 0;
                }
                pet.setCloseness(newCloseness);
                if (newCloseness < GameConstants.getClosenessNeededForLevel(pet.getLevel())) {
                    pet.setLevel(pet.getLevel() - 1);
                }
            }
            //新架構 [寵物]
            c.sendPacket(PetPacket.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem((byte) pet.getInventoryPosition()), true));
            chr.getMap().broadcastMessage(chr, PetPacket.commandResponse(chr.getId(), (byte) 1, chr.getPetIndex(pet), false, true), true);
        }
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, true, false);
        c.sendPacket(CWvsContext.enableActions());
    }

    public static void conductPetAttacking(MapleCharacter chr, boolean perk) {
        if (chr.getPets() != null) {
            String[] monsterDialog = {"BOOM! HEADSHOT!", "fyte mi!", "FALCOOOON PAWNCH!", "attack_4", "attack_5"};

            for (int i = 0; i < chr.getPets().size(); i++) {
                List<server.life.MapleMonster> moInRange = chr.getMap().getMapMonstersInRange(chr.getSpawnPet(i).getPos(), 15000.0, MapleMapObjectType.MONSTER);
                int damage = (int) (((chr.getSpawnPet(i).getLevel() * 5) * (int) (2.0 * Math.random() + 2)));
                int level = chr.getLevel();
                int chance = (int) (100.0 * Math.random());
                int attackAmount = (int) (100.0 * Math.random());
                if (level >= 10 && level < 30) {
                    damage *= 5;
                } else if (level >= 30 && level < 70) {
                    damage *= 12;
                } else if (level >= 70 && level < 120) {
                    damage *= 17;
                } else if (level >= 120 && level < 150) {
                    damage *= 27;
                } else if (level >= 120) {
                    damage *= 40;
                }
                if (attackAmount >= 95) {
                    attackAmount = (int) (3.0 * Math.random()) + 1;
                } else {
                    attackAmount = 1;
                }
                if (perk) { // should technically just remove this xD
                    attackAmount *= 2;
                    damage *= 20;
                }
                if ((System.currentTimeMillis() - chr.getSpawnPet(i).lastAttack) <= 1250 || moInRange.isEmpty()) {
                    moInRange = null;
                    return;
                }
                if (chr.getSpawnPet(i).getCloseness() >= constants.GameConstants.getClosenessNeededForLevel(chr.getSpawnPet(i).getLevel())) {
                    //新架構 [寵物]
                    chr.announce(PetPacket.showPetLevelUp(chr, (byte) i));
                    chr.getMap().broadcastMessage(chr, PetPacket.showPetLevelUp(chr, (byte) i), false);
                    chr.getSpawnPet(i).setLevel((byte) (chr.getSpawnPet(i).getLevel() + 1));
                }
                if (chance >= 70 && attackAmount == 1) {
                    //新架構 [寵物]
                    chr.getMap().broadcastMessage(PetPacket.petChat(chr.getId(), 1, monsterDialog[(int) (monsterDialog.length * Math.random())], (byte) i));
                }
                if (attackAmount > 1) {
                    //新架構 [寵物]
                    chr.getMap().broadcastMessage(PetPacket.petChat(chr.getId(), 1, "Critical hit!!", (byte) i));
                }
                for (int e = 0; e < attackAmount; e++) {
                    if (moInRange.get(1) != null) {
                        server.life.MapleMonster locked_on = moInRange.get(1);
                        chr.getMap().broadcastMessage(chr, MobPacket.damageMonster(locked_on.getObjectId(), damage), true);
                        locked_on.damage(chr, damage, true);
                        chr.getSpawnPet(i).lastAttack = System.currentTimeMillis();
                    }
                }
                chr.getSpawnPet(i).gainCloseness(1);
                moInRange = null; // dispose
            }
        }
    }

    public static void MovePet(final LittleEndianAccessor slea, final MapleCharacter chr) {
        if (chr == null) {
            return;
        }
        final int petId = (int) slea.readLong();
        final MaplePet pet = chr.getSpawnPet(!GameConstants.GMS ? (chr.getPetIndex(petId)) : petId);
        if (pet == null) {
            return;
        }

        slea.skip(9); // byte(index?), int(pos), int
        final List<ILifeMovementFragment> res = MovementParse.parseMovement(slea, pet.getPos(), MovementKind.PET_MOVEMENT);
        if (res != null && !res.isEmpty() && chr.getMap() != null) { // map crash hack

            pet.updatePosition(res);
            //新架構 [寵物]
            Point p = pet.getPos();
            chr.getMap().broadcastMessage(chr, PetPacket.movePet(chr.getId(), petId, chr.getPetIndex(petId), p, res), false);
            if (chr.getOccupation().is(Occupations.Hacker)) { // the question is, should we make this level 2 or something
                // Handle Pet Attacking System Here
                if (chr.getMap().getMobsSize() > 0) { // TODO: check if mobs are in range automatically.. rather then spamming this method
                    conductPetAttacking(chr, Math.random() > 0.5);
                }      // will change the way PERKs are ran, we'll use this as a sort of "critical" hit?
            }
        }
    }
}
