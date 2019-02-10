package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.MaplePet;
import client.inventory.PetCommand;
import client.inventory.PetDataFactory;
import client.skill.Skill;
import client.skill.SkillFactory;
import constants.GameConstants;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.maps.FieldLimitType;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.quest.MapleQuest;
import tools.packet.PetPacket;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import server.Randomizer;
import server.movement.ILifeMovementFragment;
import server.movement.MovementKind;
import server.status.MapleBuffStatus;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CField.EffectPacket;
import tools.packet.CWvsContext;

public class PetHandler {

    /*
     * 召喚寵物
     */
    public static void SpawnPet(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        //[9A 00] [B8 19 35 01] [05] [00]
        //chr.updateTick(slea.readInt());
        slea.readInt();
        chr.spawnPet(slea.readByte(), slea.readByte() > 0);
    }
    public static void Pet_AutoBuff(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        int petid = slea.readInt();
        MaplePet pet = chr.getSpawnPet(petid);
        if (chr.getMap() == null || pet == null) {
            return;
        }
        int skillId = slea.readInt();
        Skill buffId = SkillFactory.getSkill(skillId);
        if (chr.getSkillLevel(buffId) > 0 || skillId == 0) {
            pet.setSkillid(skillId);
            chr.petUpdateStats(pet, true);
        }
        c.sendPacket(CWvsContext.enableActions());
    }

    /*
     * 寵物自動喝藥
     */
    public static void Pet_AutoPotion(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        slea.skip(1);
        if (chr == null) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        //chr.updateTick(slea.readInt());
        slea.readInt();
        short slot = slea.readShort();
        if (chr == null || !chr.isAlive() || chr.getMapId() == 749040100 || chr.getMap() == null || chr.hasDisease(MapleBuffStatus.POTION)) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        if (toUse == null || toUse.getQuantity() < 1 || toUse.getItemId() != slea.readInt()) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        long time = System.currentTimeMillis();
        if (chr.getNextConsume() > time) {
            chr.dropMessage(5, "暫時無法使用道具.");
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

    public static void PetExcludeItems(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        /*
         * FF 00
         * 00 00 00 00
         * 01
         * 63 BF 0F 00
         *//*
        int petSlot = slea.readInt();
        MaplePet pet = chr.getSpawnPet(petSlot);
        if (pet == null || !PetFlag.PET_IGNORE_PICKUP.check(pet.getFlags())) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        pet.clearExcluded(); //清除以前的過濾
        byte amount = slea.readByte(); //有多少個過濾的道具ID
        for (int i = 0; i < amount; i++) {
            pet.addExcluded(i, slea.readInt());
        }*/
    }

    /*
     * 寵物說話
     */
    public static void PetChat(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        /*
         * FB 00
         * 00 00 00 00
         * 40 62 BB 00
         * 01 13
         * 06 00 DF C6 DF C6 DF C6
         */
        if (slea.available() < 12) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        int petid = slea.readInt();
        slea.readInt();
        if (chr == null || chr.getMap() == null || chr.getSpawnPet(petid) == null) {
            return;
        }
        short act = slea.readShort();
        String text = slea.readMapleAsciiString();
        if (text.length() < 1) {
            //FileoutputUtil.log(FileoutputUtil.寵物說話, "玩家寵物說話為空 - 操作: " + act + " 寵物ID: " + chr.getSpawnPet(petid).getPetItemId(), true);
            return;
        }
        chr.getMap().broadcastMessage(chr, PetPacket.petChat(chr.getId(), act, text, (byte) petid), true);
    }

    /*
     * 使用寵物命令
     */
    public static void PetCommand(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        /*
         * FC 00
         * 00 00 00 00
         * 00
         * 0C
         */
        int petId = slea.readInt();
        MaplePet pet = null;
        pet = chr.getSpawnPet((byte) petId);
        slea.readByte(); //always 0?
        if (pet == null) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        byte command = slea.readByte();
        PetCommand petCommand = PetDataFactory.getPetCommand(pet.getPetItemId(), command);
        if (petCommand == null) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        byte petIndex = chr.getPetIndex(pet);
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
                    chr.getMap().broadcastMessage(PetPacket.showPetLevelUp(chr, petIndex));
                }
                chr.petUpdateStats(pet, true);
            }
        }
        chr.getMap().broadcastMessage(PetPacket.commandResponse(chr.getId(), (byte) petCommand.getSkillId(), petIndex, success, false));
    }

    /*
     * 使用寵物食品
     */
    public static void PetFood(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        /*
         * 74 00
         * 04 3A 41 01
         * 06 00 - slot
         * 40 59 20 00 - itemId
         */
        if (chr == null || chr.getMap() == null) {
            return;
        }
        int previousFullness = 100;
        byte petslot = 0;
        MaplePet[] pets = chr.getSpawnPets();
        for (byte i = 0; i < 3; i++) {
            if (pets[i] != null && pets[i].getFullness() < previousFullness) {
                petslot = i;
                break;
            }
        }
        MaplePet pet = chr.getSpawnPet(petslot);
        //chr.updateTick(slea.readInt());
        slea.readInt();
        short slot = slea.readShort();
        int itemId = slea.readInt();
        Item petFood = chr.getInventory(MapleInventoryType.USE).getItem(slot);
        if (pet == null || petFood == null || petFood.getItemId() != itemId || petFood.getQuantity() <= 0 || itemId / 10000 != 212) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        boolean gainCloseness = false;
        if (Randomizer.nextInt(101) > 50) {
            gainCloseness = true;
        }
        if (pet.getFullness() < 100) {
            int newFullness = pet.getFullness() + 30;
            if (newFullness > 100) {
                newFullness = 100;
            }
            pet.setFullness(newFullness);
            byte index = chr.getPetIndex(pet);
            if (gainCloseness && pet.getCloseness() < 30000) {
                int newCloseness = pet.getCloseness() + 1;
                if (newCloseness > 30000) {
                    newCloseness = 30000;
                }
                pet.setCloseness(newCloseness);
                if (newCloseness >= GameConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                    pet.setLevel(pet.getLevel() + 1);
                    c.sendPacket(EffectPacket.showOwnPetLevelUp(index));
                    chr.getMap().broadcastMessage(PetPacket.showPetLevelUp(chr, index));
                }
            }
            chr.petUpdateStats(pet, true);
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
                chr.dropMessage(5, "您的寵物的飢餓感是滿值，如果繼續使用將會有50%的幾率減少1點親密度。");
            }
            chr.petUpdateStats(pet, true);
            chr.getMap().broadcastMessage(chr, PetPacket.commandResponse(chr.getId(), (byte) 1, chr.getPetIndex(pet), false, true), true);
        }
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, true, false);
        c.sendPacket(CWvsContext.enableActions());
    }

    /*
     * 寵物移動
     */
    public static void MovePet(LittleEndianAccessor slea, MapleCharacter chr) {
        int maxdist = 0;
        int petSlot = slea.readInt();
        //slea.skip(1); //[01] V.103 新增
        //slea.skip(4); //[00 00 00 00] V.112 新增
        Point startPos = slea.readPos(); //開始的坐標
        slea.skip(4); //未知
        List<ILifeMovementFragment> res = MovementParse.parseMovement(slea, startPos, MovementKind.PET_MOVEMENT);
        if (res != null && chr != null && !res.isEmpty() && chr.getMap() != null) { // map crash hack
//            if (slea.available() != 8) {
//                System.out.println("slea.available != 8 (寵物移動出錯) 剩餘封包長度: " + slea.available());
//                FileoutputUtil.log(FileoutputUtil.Movement_Log, "slea.available != 8 (寵物移動出錯) 封包: " + slea.toString(true));
//                return;
//            }
            MapleCharacter player = chr;
            MaplePet pet = chr.getSpawnPet(petSlot);
            if (pet == null) {
                return;
            }
            chr.getSpawnPet(chr.getPetIndex(pet)).updatePosition(res);
            chr.getMap().broadcastMessage(chr, PetPacket.movePet(chr.getId(), petSlot, startPos, res), false);

            Boolean meso = false, item = false, boots = false, bino = false;
            if (chr.getInventory(MapleInventoryType.EQUIPPED).findById(1812001) != null) {
                item = true;
                maxdist = 30;
            }
            if (chr.getInventory(MapleInventoryType.EQUIPPED).findById(1812000) != null) {
                meso = true;
                maxdist = 30;
            }
            if (chr.getInventory(MapleInventoryType.EQUIPPED).findById(1812004) != null) {
                boots = true;
                maxdist = 80;
            }
            //寵物全圖撿物
            if (chr.getInventory(MapleInventoryType.ETC).findById(4030003) != null) {
                bino = true;
                boots = true;
                meso = true;
                item = true;
                maxdist = 1000;
            } else {
                bino = false;
                boots = false;
                meso = false;
                item = false;
            }


            if (((boots || bino) || meso || item) && (System.currentTimeMillis() - chr.getLast_vac() > 7000)) {
                List<MapleMapObject> objects = player.getMap().getMapObjectsInRange(player.getPosition(), GameConstants.maxViewRangeSq(), Arrays.asList(MapleMapObjectType.ITEM));
                chr.setLast_vac(System.currentTimeMillis());
                for (ILifeMovementFragment move : res) {
                    Point petPos = move.getPosition();
                    double petX = petPos.getX();
                    double petY = petPos.getY();
                    for (MapleMapObject map_object : objects) {
                        Point objectPos = map_object.getPosition();
                        double objectX = objectPos.getX();
                        double objectY = objectPos.getY();
                        if (Math.abs(petX - objectX) <= maxdist || Math.abs(objectX - petX) <= maxdist) {
                            if (Math.abs(petY - objectY) <= maxdist || Math.abs(objectY - petY) <= maxdist) {
                                if (map_object instanceof MapleMapItem) {
                                    MapleMapItem mapitem = (MapleMapItem) map_object;
                                    if (mapitem.getMeso() <= 0 || !meso) {
                                        if(!MapleInventoryManipulator.checkSpace(chr.getClient(), mapitem.getItem().getItemId(), mapitem.getItem().getQuantity(), mapitem.getItem().getOwner()) || MapleItemInformationProvider.getInstance().isQuestItem(mapitem.getItemId())) {
                                            continue;
                                        }
                                        if(!MapleItemInformationProvider.getInstance().itemExists(mapitem.getItem().getItemId())){
                                            chr.getMap().removeMapObject(map_object);
                                            mapitem.setPickedUp(true);
                                            continue;
                                        }
                                    }
                                    synchronized (mapitem) {
                                        if (mapitem.isPickedUp() || mapitem.getOwner() != chr.getId()) {
                                            continue;
                                        }
                                        if (mapitem.getMeso() > 0 && meso) {
                                            chr.gainMeso(mapitem.getMeso(), true, false);
                                            chr.getMap().broadcastMessage(
                                                    CField.removeItemFromMap(mapitem.getObjectId(), 5, chr.getId(), petSlot),
                                                    mapitem.getPosition());
                                            chr.getMap().removeMapObject(map_object);
                                            mapitem.setPickedUp(true);
                                        } else {
                                            if (item) {
                                                if (mapitem.getItem().getItemId() >= 5000000 && mapitem.getItem().getItemId() <= 5000045) {
                                                    MapleInventoryManipulator.addById(chr.getClient(), mapitem.getItem().getItemId(), mapitem.getItem().getQuantity(), "撿起人 : " + chr.getName(), null);
                                                    chr.getMap().broadcastMessage(
                                                            CField.removeItemFromMap(mapitem.getObjectId(), 5, chr.getId(), petSlot),
                                                            mapitem.getPosition());
                                                    chr.getMap().removeMapObject(map_object);
                                                    mapitem.setPickedUp(true);
                                                } else {
                                                    if(MapleInventoryManipulator.checkSpace(chr.getClient(), mapitem.getItem().getItemId(), mapitem.getItem().getQuantity(), mapitem.getItem().getOwner())) {
                                                        StringBuilder logInfo = new StringBuilder("撿起人 : ");
                                                        logInfo.append(chr.getName());
                                                        if (MapleInventoryManipulator.addFromDrop(chr.getClient(), mapitem.getItem(), true)) {
                                                            chr.getMap().broadcastMessage(
                                                                    CField.removeItemFromMap(mapitem.getObjectId(), 5, chr.getId(), petSlot),
                                                                    mapitem.getPosition());
                                                            chr.getMap().removeMapObject(map_object);
                                                            mapitem.setPickedUp(true);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    /*
    public static void AllowPetLoot(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            c.announce(MaplePacketCreator.enableActions());
            return;
        }
        slea.skip(4);
        int data = slea.readShort();
        if (data > 0) {
            chr.getQuestNAdd(MapleQuest.getInstance(GameConstants.ALLOW_PET_LOOT)).setCustomData(String.valueOf(data));
        } else {
            chr.getQuestRemove(MapleQuest.getInstance(GameConstants.ALLOW_PET_LOOT));
        }
        MaplePet[] pet = chr.getSpawnPets();
        for (int i = 0; i < 3; i++) {
            if (pet[i] != null && pet[i].getSummoned()) {
                pet[i].setCanPickup(data > 0);
                chr.petUpdateStats(pet[i], true);
            }
        }
        c.announce(PetPacket.showPetPickUpMsg(data > 0, 1));
    }

    public static void AllowPetAutoEat(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if (chr == null || chr.getMap() == null) {
            c.announce(MaplePacketCreator.enableActions());
            return;
        }
        slea.skip(4); //
        slea.skip(4); // [00 08 00 00] 寵物是否有這個狀態
        boolean data = slea.readByte() > 0;
        chr.updateInfoQuest(GameConstants.寵物自動餵食, data ? "autoEat=1" : "autoEat=0");
        c.announce(PetPacket.showPetAutoEatMsg());
    }*/
}
