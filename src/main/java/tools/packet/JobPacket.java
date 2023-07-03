package tools.packet;

import client.MapleCharacter;
import tools.data.MaplePacketLittleEndianWriter;
import tools.types.Pair;

import java.util.ArrayList;
import java.util.List;

public class JobPacket {

    //TODO: 幻影技能包
    public static class PhantomPacket {

        public static byte[] addStolenSkill(int jobNum, int index, int skill, int level) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

//            mplew.writeShort(SendPacketOpcode.LP_ChangeStealMemoryResult.getValue());
//            mplew.write(1);
//            mplew.write(0);
//            mplew.writeInt(jobNum);
//            mplew.writeInt(index);
//            mplew.writeInt(skill);
//            mplew.writeInt(level);
//            mplew.writeInt(0);
//            mplew.write(0);

            return mplew.getPacket();
        }

        public static byte[] removeStolenSkill(int jobNum, int index) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
//
//            mplew.writeShort(SendPacketOpcode.LP_ChangeStealMemoryResult.getValue());
//            mplew.write(1);
//            mplew.write(3);
//            mplew.writeInt(jobNum);
//            mplew.writeInt(index);
//            mplew.write(0);

            return mplew.getPacket();
        }

        public static byte[] replaceStolenSkill(int base, int skill) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

//            mplew.writeShort(SendPacketOpcode.LP_ResultSetStealSkill.getValue());
            mplew.write(1);
            mplew.write(skill > 0 ? 1 : 0);
            mplew.writeInt(base);
            mplew.writeInt(skill);

            return mplew.getPacket();
        }

        public static byte[] gainCardStack(MapleCharacter chr, int runningId, int color, int skillid, int damageTo, int count) {
            List<Integer> mobid = new ArrayList<>();
            mobid.add(damageTo);

            List<Pair<Integer, Integer>> forceinfo = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                if (damageTo == 0) {
                    forceinfo.add(new Pair<>(runningId + i, color));
                } else {
                    forceinfo.add(new Pair<>(runningId, color));
                }
            }

            return null;//CField.gainForce(false, chr, mobid, 1, skillid, forceinfo);
        }

        public static byte[] updateCardStack(final int total) {
            MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();

//            mplew.writeShort(SendPacketOpcode.LP_IncJudgementStack.getValue());
//            mplew.write(total);

            return mplew.getPacket();
        }

        public static byte[] getCarteAnimation(MapleCharacter chr, int oid, int job, int total, int numDisplay) {
            List<Integer> mobid = new ArrayList<>();
            mobid.add(oid);

            List<Pair<Integer, Integer>> forceinfo = new ArrayList<>();
            for (int i = 1; i <= numDisplay; i++) {
                forceinfo.add(new Pair<>(total - (numDisplay - i), job == 2412 ? 2 : 1));
            }

            return null;//(false, chr, mobid, 1, job == 2412 ? 24120002 : 24100003, forceinfo);
        }
    }
}
