package client.messages.commands;

import client.MapleClient;
import client.MapleStat;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ServerConstants.PlayerGMRank;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.life.MapleMonster;
import server.maps.MapleMap;
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

    public static class 自殺 extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            c.getPlayer().getStat().setHp( 0, c.getPlayer());
            c.getPlayer().getStat().setMp(0, c.getPlayer());
            c.getPlayer().updateSingleStat(MapleStat.HP, 0);
            c.getPlayer().updateSingleStat(MapleStat.MP, 0);
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "@自殺 - 自殺";
        }
    }

    public static class emo extends 自殺{
    }

    public static class sell extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            final int inv, start, end;
            if(splitted.size() < 4) {
                c.getPlayer().dropMessage(1, "使用方式 @sell 欄位 開始格數 結束格數.");
                return false;
            }
            MapleInventoryType innv;
            try {
                inv = Integer.parseInt(splitted.get(1));

                if(inv > 5 || inv < 1){
                    c.getPlayer().dropMessage(1, "欄位無效 只能 1 ~ 5.");
                    return false;
                }
                innv = MapleInventoryType.getByType((byte) inv);
            } catch (NumberFormatException nfe) {
                if(splitted.get(1).equals("裝備欄")){
                    innv = MapleInventoryType.getByType((byte) 1);
                }else if(splitted.get(1).equals("消耗欄")){
                    innv = MapleInventoryType.getByType((byte) 2);
                }else if(splitted.get(1).equals("裝飾欄")){
                    innv = MapleInventoryType.getByType((byte) 3);
                }else if(splitted.get(1).equals("其他欄")){
                    innv = MapleInventoryType.getByType((byte) 4);
                }else if(splitted.get(1).equals("特殊欄")){
                    innv = MapleInventoryType.getByType((byte) 5);
                }else
                    return false;
            }

            try {
                start = Integer.parseInt(splitted.get(2));
                end = Integer.parseInt(splitted.get(3));
            } catch (NumberFormatException nfe) {
                c.getPlayer().dropMessage(1, "輸入的數字無效.");
                return false;
            }

            if(start > 96 || end > 96){
                c.getPlayer().dropMessage(1, "格數無效 最多96.");
                return false;
            }



            int count = 0;
            for(int i = start; i <= end;i++) {
                Item item = c.getPlayer().getInventory(innv).getItem((short) i);
                if(item == null)
                    continue;
                int qua = item.getQuantity();
                int price = new Double(MapleItemInformationProvider.getInstance().getPrice(item.getItemId())).intValue();
                if(price > 0 && !MapleItemInformationProvider.getInstance().isCash(item.getItemId())){
                    count += price;
                }


                MapleInventoryManipulator.removeFromSlot(c, innv, (short) i, (short) qua, false);
                c.getSession().write(CWvsContext.InfoPacket.getShowItemGain(item.getItemId(), (short) -qua, true));
            }
            if(count > 0)
                c.getPlayer().gainMeso(count, true, true);

            return true;
        }

        @Override
        public String getHelpMessage() {
            return "@sell 欄位(1~5) 開始格數(1~96) 結束格數(1~96) <賣東西(若誤賣 恕不補償)>";
        }
    }
    public static class fm extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            for (int i : GameConstants.blockedMaps) {
                if (c.getPlayer().getMapId() == i) {
                    c.getPlayer().dropMessage(1, "你不能在這裡使用指令.");
                    return false;
                }
            }
            if (c.getPlayer().getLevel() < 10) {
                c.getPlayer().dropMessage(1, "你的等級必須是10等.");
                return false;
            }
            if (c.getPlayer().getMap().getSquadByMap() != null || c.getPlayer().getEventInstance() != null || c.getPlayer().getMap().getEMByMap() != null || c.getPlayer().getMapId() >= 990000000/* || FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())*/) {
                c.getPlayer().dropMessage(1, "你不能在這裡使用指令.");
                return false;
            }
            if ((c.getPlayer().getMapId() >= 680000210 && c.getPlayer().getMapId() <= 680000502) || (c.getPlayer().getMapId() / 1000 == 980000 && c.getPlayer().getMapId() != 980000000) || (c.getPlayer().getMapId() / 100 == 1030008) || (c.getPlayer().getMapId() / 100 == 922010) || (c.getPlayer().getMapId() / 10 == 13003000)) {
                c.getPlayer().dropMessage(1, "你不能在這裡使用指令.");
                return false;
            }
            MapleMap free = c.getChannelServer().getMapFactory().getMap(910000000);
            c.getPlayer().changeMap(free, free.getPortal(0));
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "@fm - 回自由";
        }
    }
    public static class 自由 extends fm{

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
