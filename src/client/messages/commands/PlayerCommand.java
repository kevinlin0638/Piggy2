package client.messages.commands;

import client.MapleClient;
import client.MapleStat;
import constants.ServerConstants.PlayerGMRank;
import server.life.MapleMonster;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;

import java.util.Arrays;
import java.util.List;
import scripting.NPCScriptManager;
import tools.packet.CWvsContext;

public class PlayerCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.NORMAL;
    }

    public static class ea extends 查看 {
    }

    public static class 查看 extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            String pet = tools.StringUtil.joinStringFrom(args.toArray(new String[0]), 1) + " 01 85 A3 07 00 00 00 00 00 00 04 00 00 00 34 00 00 00 07 00 00 00 00 00 00 00 00 00 00 00";
//110
           // c.getSession().write(tools.packet.CField.getPacketFromHexString("E2 00 01 B6 A3 07 00 01 04 00 00 00 07 00 00 00 00"));
//0A 01 02 00 00 00 00 01 00 40 4B 4C 00 0B 00 42 72 6F 77 6E 20 4B 69 74 74 79 35 00 00 00 00 00 00 00 34 02 06 01 00 68 00 00 00
//0A 01 02 00 00 00 00 00 00 00 01 00 40 4B 4C 00 0B 00 42 72 6F 77 6E 20 4B 69 74 74 79 10 00 00 00 00 00 00 00 BA 02 12 01 00 61 00 FF FF FF FF 64 00 00 00
            //tools.packet.MTSCSPacket.playCashSong(5100000, "456");

            // c.getSession().write(tools.packet.CField.sendPyramidKills(1));
            //c.sendPacket(tools.packet.CField.UIPacket.getDirectionInfo("Effect/Direction5.img/effect/mercedesInIce/merBalloon/2", 2000, 0, -100, 1, 0));
            NPCScriptManager.getInstance().dispose(c);
            c.sendPacket(CWvsContext.enableActions());
            c.getPlayer().dropMessage(5, "目前地圖 " + c.getPlayer().getMap().getId() + "座標 (" + String.valueOf(c.getPlayer().getPosition().x) + " , " + String.valueOf(c.getPlayer().getPosition().y) + ")");
            c.getPlayer().showInfo("指令", true, "解卡成功。");
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "@ea - 解卡";
        }
    }

    public static class 經驗值歸零 extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            c.getPlayer().setExp(0);
            c.getPlayer().updateSingleStat(MapleStat.EXP, c.getPlayer().getExp());
            c.getPlayer().showInfo("指令", true, "經驗值歸零成功。");
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "@經驗值歸零 - 經驗值歸零";
        }
    }

    public static class 怪物 extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            MapleMonster mob = null;
            for (final MapleMapObject obj : c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 100000, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) obj;
                if (mob.isAlive()) {
                    c.getPlayer().dropMessage(-11, "怪物: " + mob.toString());
                    break; //only one
                }
            }
            if (mob == null) {
                c.getPlayer().dropMessage(-11, "沒找到任何怪物");
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "@怪物 - 怪物資訊";
        }
    }

    public static class 克隆我 extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            c.getPlayer().cloneLook();
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "@克隆我 - 產生一個克隆人";
        }
    }

    public static class 清除克隆 extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            c.getPlayer().dropMessage(-11, c.getPlayer().getCloneSize() + " 個克隆被清除了");
            c.getPlayer().disposeClones();
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "@清除克隆 - 清除克隆人";
        }
    }

}
