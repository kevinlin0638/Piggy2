package client.messages.commands;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.messages.CommandProcessorUtil;
import constants.ServerConstants;
import handling.channel.ChannelServer;
import handling.world.World;
import server.Timer;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.OverrideMonsterStats;
import server.maps.MapleMap;

import java.awt.*;
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

    public static class Spawn extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c,  List<String> splitted) {
            final int mid = Integer.parseInt(splitted.get(1));
            final int num = Math.min(CommandProcessorUtil.getOptionalIntArg(splitted.toArray(new String[0]), 2, 1), 500);

            Long hp = CommandProcessorUtil.getNamedLongArg(splitted.toArray(new String[0]), 1, "hp");
            Integer exp = CommandProcessorUtil.getNamedIntArg(splitted.toArray(new String[0]), 1, "exp");
            Double php = CommandProcessorUtil.getNamedDoubleArg(splitted.toArray(new String[0]), 1, "php");
            Double pexp = CommandProcessorUtil.getNamedDoubleArg(splitted.toArray(new String[0]), 1, "pexp");

            MapleMonster onemob;
            try {
                onemob = MapleLifeFactory.getMonster(mid);
            } catch (RuntimeException e) {
                c.getPlayer().dropMessage(5, "Error: " + e.getMessage());
                return false;
            }

            long newhp = 0;
            int newexp = 0;
            if (hp != null) {
                newhp = hp.longValue();
            } else if (php != null) {
                newhp = (long) (onemob.getMobMaxHp() * (php.doubleValue() / 100));
            } else {
                newhp = onemob.getMobMaxHp();
            }
            if (exp != null) {
                newexp = exp.intValue();
            } else if (pexp != null) {
                newexp = (int) (onemob.getMobExp() * (pexp.doubleValue() / 100));
            } else {
                newexp = onemob.getMobExp();
            }
            if (newhp < 1) {
                newhp = 1;
            }

            final OverrideMonsterStats overrideStats = new OverrideMonsterStats(newhp, onemob.getMobMaxMp(), newexp, false);
            for (int i = 0; i < num; i++) {
                MapleMonster mob = MapleLifeFactory.getMonster(mid);
                mob.setHp(newhp);
                mob.setOverrideStats(overrideStats);
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, c.getPlayer().getPosition());
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!spawn ID <hp> <exp> <php> <pexp> - 招喚怪物";
        }
    }

    public static class TestDmg extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            final MapleCharacter chr = c.getPlayer();
            if (!chr.isTestingDPS()) {
                chr.toggleTestingDPS();
                chr.dropMessage("持續攻擊怪物 15秒.");
                final MapleMonster mm = MapleLifeFactory.getMonster(9001007);
                int distance;
                int job = chr.getJob();
                if ((job >= 300 && job < 413) || (job >= 1300 && job < 1500) || (job >= 520 && job < 600))
                    distance = 125;
                else
                    distance = 50;
                Point p = new Point((int) chr.getPosition().getX() - distance, (int) chr.getPosition().getY());
                long newhp = 800000000000L; //set it to what you want.
                assert mm != null;
                mm.changeLevel(250, 800000000000L);
                chr.getMap().spawnMonsterOnGroundBelow(mm, p);
                Timer.EtcTimer.getInstance().schedule(new Runnable() {
                    public void run() {
                        long health = mm.getHp();
                        chr.getMap().killMonster(mm.getId());
                        long dps = (newhp - health) / 15;
                        chr.dropMessage("您的每秒傷害為 " + dps + ".");
                        chr.dropMessage("您的15秒總傷害為 " + (newhp - health) + ".");
                        chr.toggleTestingDPS();
                    }
                }, 15000);
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!testDmg - 測試傷害";
        }
    }

    public static class Warp extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
            if (victim != null) {
                if (splitted.size() == 2) {
                    c.getPlayer().changeMap(victim.getMap(), victim.getMap().findClosestSpawnpoint(victim.getPosition()));
                } else {
                    MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted.get(2)));
                    victim.changeMap(target, target.getPortal(0));
                }
            } else {
                try {
                    victim = c.getPlayer();
                    int ch = World.Find.findChannel(splitted.get(1));
                    if (ch < 0) {
                        MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted.get(1)));
                        c.getPlayer().changeMap(target, target.getPortal(Integer.parseInt(splitted.get(2))));
                    } else {
                        victim = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(splitted.get(1));
                        c.getPlayer().dropMessage(6, "換頻道中. 請稍後.");
                        if (victim.getMapId() != c.getPlayer().getMapId()) {
                            final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(victim.getMapId());
                            c.getPlayer().changeMap(mapp, mapp.getPortal(0));
                        }
                        c.getPlayer().changeChannel(ch);
                    }
                } catch (Exception e) {
                    c.getPlayer().dropMessage(6, "有東西出問題 " + e.getMessage());
                    return false;
                }
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!warp <地圖ID/腳色名稱> - 飛地圖";
        }
    }

}
