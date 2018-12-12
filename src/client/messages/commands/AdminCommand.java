package client.messages.commands;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import constants.ServerConstants;
import handling.channel.ChannelServer;

import java.util.List;

public class AdminCommand {

    public static ServerConstants.PlayerGMRank getPlayerLevelRequired() {
        return ServerConstants.PlayerGMRank.ADMIN;
    }

    public static class ExpRate extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            return false;
        }

        @Override
        public String getHelpMessage() {
            return "@expRate <世界> <頻道> <倍率> - 設定經驗值倍率";
        }
    }

    public static class maxskills extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            c.getPlayer().maxAllSkills();
            c.getPlayer().dropMessage("技能已全滿");
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!maxskills 技能全滿";
        }
    }

    public static class maxskillsJob extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            c.getPlayer().maxSkillsByJob();
            c.getPlayer().dropMessage("當前職業技能已全滿");
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!maxskillsJob 技能全滿(職業)";
        }
    }

    public static class Level extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            c.getPlayer().setLevel(Short.parseShort(args.get(1)));
            c.getPlayer().levelUp();
            c.getPlayer().setExp(0);
            c.getPlayer().updateSingleStat(MapleStat.EXP, c.getPlayer().getExp());
            if (c.getPlayer().getExp() < 0) {
                c.getPlayer().gainExp(-c.getPlayer().getExp(), false, false, true);
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!level <等級> - 等級提升至<等級>";
        }
    }

}
