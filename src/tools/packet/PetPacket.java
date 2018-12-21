package tools.packet;

import client.MapleCharacter;
import client.MapleStat;
import client.inventory.Item;
import client.inventory.MaplePet;
import handling.SendPacketOpcode;

import java.awt.*;
import java.util.List;
import server.movement.ILifeMovementFragment;
import tools.data.MaplePacketLittleEndianWriter;

public class PetPacket {

    public static byte[] updatePet(final MaplePet pet, final Item item, final boolean active) {
        final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MODIFY_INVENTORY_ITEM.getValue());
        mplew.write(0);
        mplew.write(2);
        mplew.write(3);
        mplew.write(5);
        mplew.writeShort(pet.getInventoryPosition());
        mplew.write(0);
        mplew.write(5);
        mplew.writeShort(pet.getInventoryPosition());
        mplew.write(3);
        mplew.writeInt(pet.getPetItemId());
        mplew.write(1);
        mplew.writeLong(pet.getUniqueId());
        PacketHelper.addPetItemInfo(mplew, item, pet, active);
        return mplew.getPacket();
    }

    public static byte[] showPet(MapleCharacter chr, MaplePet pet, boolean remove, boolean hunger) {
        return showPet(chr, pet, remove, hunger, false);
    }

    public static byte[] showPet(MapleCharacter chr, MaplePet pet, boolean remove, boolean hunger, boolean show) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(show ? SendPacketOpcode.SHOW_PET.getValue() : SendPacketOpcode.SPAWN_PET.getValue());
        mplew.writeInt(chr.getId());
        mplew.writeInt(chr.getPetIndex(pet));
        mplew.write(remove ? 0 : 1);
        /*
         * 0 = 手動召回
         * 1 = 寵物飢餓度為0自動回去
         * 2 = 寵物時間到期
         */
        mplew.write(hunger ? 1 : 0);
        if (!remove) {
            addPetInfo(mplew, chr, pet, false);
        }

        return mplew.getPacket();
    }

    public static void addPetInfo(MaplePacketLittleEndianWriter mplew, MapleCharacter chr, MaplePet pet, boolean showpet) {
        if (showpet) {
            mplew.write(1);
            mplew.writeInt(chr.getPetIndex(pet));
        }
        mplew.writeInt(pet.getPetItemId());  //寵物ID
        mplew.writeMapleAsciiString(pet.getName()); //寵物名字
        mplew.writeLong(pet.getUniqueId()); //寵物的SQL唯一ID
        mplew.writePos(pet.getPos()); //寵物的坐標
        mplew.write(pet.getStance()); //姿勢
        mplew.writeShort(pet.getFh());
        /*
        mplew.writeInt(-1); //T071新增
        mplew.writeInt(0x64); //V.109新增 未知
         */
    }

    public static byte[] movePet(int chrId, int slot, Point startPos, List<ILifeMovementFragment> moves) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.MOVE_PET.getValue());
        mplew.writeInt(chrId);
        mplew.writeInt(slot);
        mplew.writeInt(0); //V.112新增
        mplew.writePos(startPos);
        //mplew.writeInt(0);
        PacketHelper.serializeMovementList(mplew, moves);

        return mplew.getPacket();
    }

    public static byte[] petChat(int chaId, short act, String text, byte slot) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PET_CHAT.getValue());
        mplew.writeInt(chaId);
        mplew.writeInt(slot);
        mplew.writeShort(act);
        mplew.writeMapleAsciiString(text);

        return mplew.getPacket();
    }

    public static byte[] commandResponse(int chrId, byte command, byte slot, boolean success, boolean food) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PET_COMMAND.getValue());
        mplew.writeInt(chrId);
        mplew.writeInt(slot);
        mplew.write(food ? 2 : 1);
        mplew.write(command);
        if (food) {
            mplew.writeInt(0); //T071修改為 Int
        } else {
            mplew.write(success ? 1 : 0);  //T071修改為 byte
        }
        return mplew.getPacket();
    }

    public static byte[] showPetLevelUp(MapleCharacter chr, byte index) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.SHOW_FOREIGN_EFFECT.getValue());
        mplew.writeInt(chr.getId());
        mplew.write(6);
        mplew.write(0);
        mplew.writeInt(index);

        return mplew.getPacket();
    }

    public static byte[] loadExceptionList(MapleCharacter chr, MaplePet pet) {
        MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

        mplew.writeShort(SendPacketOpcode.PET_EXCEPTION_LIST.getValue());
        mplew.writeInt(chr.getId());
        mplew.writeInt(chr.getPetIndex(pet));
        mplew.writeLong(pet.getUniqueId());
        //----------------------------------
        mplew.write(0);
        /*List<Integer> excluded = pet.getExcluded();
        mplew.write(excluded.size());
        for (Integer anExcluded : excluded) {
            mplew.writeInt(anExcluded);
        }*/

        return mplew.getPacket();
    }
}
