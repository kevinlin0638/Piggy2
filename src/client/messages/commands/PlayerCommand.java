package client.messages.commands;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventory;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ServerConstants.PlayerGMRank;
import database.DatabaseConnection;
import handling.channel.ChannelServer;
import handling.channel.handler.NPCHandler;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import scripting.NPCScriptManager;
import server.shops.IMaplePlayerShop;
import server.shops.MapleMiniGame;
import server.shops.MaplePlayerShopItem;
import tools.FileoutputUtil;
import tools.StringUtil;
import tools.packet.CWvsContext;
import tools.packet.PlayerShopPacket;

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
            String s = "";
            if(c.getPlayer().getLevel() < 250) {
                s = c.getPlayer().getExp() + "(" + Math.round(Long.valueOf(c.getPlayer().getExp()).floatValue() / GameConstants.getExpNeededForLevel(c.getPlayer().getLevel()) * 100) + "%)";
            }
//            long time = System.currentTimeMillis() - c.getPlayer().getOnline_time();
//            String ss = (time / (1000 * 60 * 60)) + " 小時 " +  ((time % (1000 * 60 * 60)) / (1000 * 60)) + " 分鐘 " + (((time % (1000 * 60 * 60)) % (1000 * 60)) / 1000) + " 秒";
            if(c.getPlayer().isAdmin())
                c.getPlayer().dropMessage(5, "目前地圖 " + c.getPlayer().getMap().getId() + "座標 (" + String.valueOf(c.getPlayer().getPosition().x) + " , " + String.valueOf(c.getPlayer().getPosition().y) + ")");
//            c.getPlayer().showInfo("指令", true, "解卡成功。");
            c.getPlayer().dropMessage(5, "當前時間是" + FileoutputUtil.CurrentReadable_Time() + " GMT+8 ");
            c.getPlayer().dropMessage(5, "角色資訊 物理攻擊 : " + c.getPlayer().getStat().getTotalWatk() + "||魔法攻擊 : " + c.getPlayer().getStat().getTotalMagic() + "||");
            c.getPlayer().dropMessage(5, "力量 : " + c.getPlayer().getStat().getTotalStr() +
                    "||敏捷 : " + c.getPlayer().getStat().getTotalDex() +"||智力 : " + c.getPlayer().getStat().getTotalInt() +"||幸運 : " + c.getPlayer().getStat().getTotalLuk());
            c.getPlayer().dropMessage(5, "經驗倍率 " + (Math.round(c.getPlayer().getEXPMod()) * 100) * Math.round(c.getPlayer().getStat().expBuff / 100.0) +"%");
            c.getPlayer().dropMessage(5, "掉寶倍率 " + (Math.round(c.getPlayer().getDropMod()) * 100) * Math.round(c.getPlayer().getStat().dropBuff / 100.0) + "%");
            c.getPlayer().dropMessage(5, "楓幣倍率 " + Math.round(c.getPlayer().getStat().mesoBuff / 100.0) * 100 + "%");
            c.getPlayer().dropMessage(5, "當前經驗 " + s);
            c.getPlayer().dropMessage(5, "楓點 " + c.getPlayer().getCSPoints(2));
            c.getPlayer().dropMessage(5, "當前延遲 " + c.getPlayer().getClient().getLatency() + " 毫秒");

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

    public static class Online extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            ArrayList<ArrayList<MapleCharacter>> onl = new ArrayList<>();
            for (ChannelServer cserv : ChannelServer.getAllInstance(0)) {
                ArrayList<MapleCharacter> arr = new ArrayList<>();
                for (MapleCharacter chrr : cserv.getPlayerStorage().getAllCharacters()){
                    if(!chrr.isGM()){
                        arr.add(chrr);
                    }
                }
                onl.add(arr);
            }

            int cha = 1;
            int total = 0;
            if(splitted.size() < 2){
                for (ArrayList<MapleCharacter> ar : onl){
                    c.getPlayer().dropMessage(5, "頻道 " + cha + " : " + ar.size() + " 人在線");
                    cha++;
                    total += ar.size();
                }
                c.getPlayer().dropMessage(5, "總上線人數 : " + total + " 人在線");
            }else {
                int ch;
                try {
                    ch = Integer.valueOf(splitted.get(1));
                } catch (NumberFormatException nfe) {
                    c.getPlayer().dropMessage(5, "錯誤 : 輸入的數字無效 .");
                    return false;
                }
                if (ch > 0 && ch <= 10) {
                    ArrayList<MapleCharacter> ar = onl.get(ch - 1);
                    c.getPlayer().dropMessage(5, "頻道 " + ch + " : " + ar.size() + " 人在線");
                    c.getPlayer().dropMessage(5, "在線ID:");
                    StringBuilder sb = new StringBuilder();
                    for (MapleCharacter cc : ar) {
                        sb.append(cc.getName()).append(" ");
                    }
                    c.getPlayer().dropMessage(5, sb.toString());
                }
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "@online <頻道(可選)> - 當前在線人數";
        }
    }
    public static class mob extends 怪物{
    }

    public static class 怪物 extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            MapleMonster mob = null;
            for (final MapleMapObject obj : c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 100000, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) obj;
                if (mob.isAlive()) {
                    c.getPlayer().dropMessage(6, "怪物: " + mob.toString());
                    break; //only one
                }
            }
            if (mob == null) {
                c.getPlayer().dropMessage(6, "沒找到任何怪物");
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "@怪物 - 怪物資訊";
        }
    }

    public static class round extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            for (int i : GameConstants.blockedMaps) {
                if (c.getPlayer().getMapId() == i || c.getPlayer().getMapId() == 910000000) {
                    c.getPlayer().dropMessage(1, "你不能在這裡使用此指令.");
                    return false;
                }
            }
            if (c.getPlayer().getMap().getSquadByMap() != null || c.getPlayer().getEventInstance() != null || c.getPlayer().getMap().getEMByMap() != null || c.getPlayer().getMapId() >= 990000000/* || FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())*/) {
                c.getPlayer().dropMessage(1, "你不能在這裡使用此指令.");
                return false;
            }
            if ((c.getPlayer().getMapId() >= 680000210 && c.getPlayer().getMapId() <= 680000502) || (c.getPlayer().getMapId() / 1000 == 980000 && c.getPlayer().getMapId() != 980000000) || (c.getPlayer().getMapId() / 100 == 1030008) || (c.getPlayer().getMapId() / 100 == 922010) || (c.getPlayer().getMapId() / 10 == 13003000)) {
                c.getPlayer().dropMessage(1, "你不能在這裡使用此指令.");
                return false;
            }
            MapleMonster mob = null;
            Item item = c.getPlayer().getInventory(MapleInventoryType.ETC).findById(4030004);
            if (item != null) {
                List<MapleMonster>ms = c.getPlayer().getMap().getAllMonster();
                for(MapleMonster moo : ms){
                    if(moo.getId() == 9700100) {
                        c.getPlayer().dropMessage(6, "地圖上已有 輪迴石碑.");
                        return true;
                    }
                }
                mob = MapleLifeFactory.getMonster(9700100);
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, c.getPlayer().getPosition());
                c.getPlayer().setOpenRound(true);
            } else {
                c.getPlayer().dropMessage(6, "您沒有召喚輪迴石碑的道具.");
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "@round - 招喚輪迴";
        }
    }

    /*public static class 克隆我 extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            c.getPlayer().cloneLook();
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "@克隆我 - 產生一個克隆人";
        }
    }*/

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

    public static class 查看贊助 extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            Connection con = DatabaseConnection.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM paybill_paylog WHERE  account = ?", Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, c.getAccID());
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    c.getPlayer().dropMessage("帳號 : " + c.getAccountName() + "斗內金額 : " + rs.getInt("money") + " 自" + rs.getDate("paytime").toString() + " 已付款 - " +  rs.getInt("dps") + "贊助點 存入帳號");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "@查看贊助 - 查看贊助";
        }
    }



    public static class sell extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            final int start, end;
            int inv;
            if(splitted.size() < 4) {
                c.getPlayer().dropMessage(1, "使用方式 @sell 欄位 開始格數 結束格數.");
                return false;
            }
            MapleInventoryType innv;
            try {
                inv = Integer.parseInt(splitted.get(1));
                if(inv == 4)
                    inv = 3;
                else if(inv == 3)
                    inv = 4;
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
    /*public static class 清除克隆 extends AbstractsCommandExecute {

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
    }*/

    public static class STR extends DistributeStatCommands {


        public STR() {
            stat = MapleStat.STR;
        }

        @Override
        public String getHelpMessage() {
            return "@str <數字>";
        }
    }


    public static class DEX extends DistributeStatCommands {


        public DEX() {
            stat = MapleStat.DEX;
        }

        @Override
        public String getHelpMessage() {
            return "@dex <數字>";
        }
    }


    public static class INT extends DistributeStatCommands {


        public INT() {
            stat = MapleStat.INT;
        }

        @Override
        public String getHelpMessage() {
            return "@int <數字>";
        }
    }


    public static class LUK extends DistributeStatCommands {


        public LUK() {
            stat = MapleStat.LUK;
        }

        @Override
        public String getHelpMessage() {
            return"@luk <數字>";
        }
    }

    public static class HP extends DistributeStatCommands {


        public HP() {
            stat = MapleStat.MAX_HP;
        }

        @Override
        public String getHelpMessage() {
            return "@hp <數字>";
        }
    }

    public static class MP extends DistributeStatCommands {


        public MP() {
            stat = MapleStat.MAX_MP;
        }

        @Override
        public String getHelpMessage() {
            return "@mp <數字>";
        }
    }

    public abstract static class DistributeStatCommands extends AbstractsCommandExecute {


        protected MapleStat stat = null;

        private void setStat(MapleCharacter player, int amount) {
            switch (stat) {
                case STR:
                    player.getStat().setStr((short) amount, player);
                    player.updateSingleStat(MapleStat.STR, player.getStat().getStr());
                    break;
                case DEX:
                    player.getStat().setDex((short) amount, player);
                    player.updateSingleStat(MapleStat.DEX, player.getStat().getDex());
                    break;
                case INT:
                    player.getStat().setInt((short) amount, player);
                    player.updateSingleStat(MapleStat.INT, player.getStat().getInt());
                    break;
                case LUK:
                    player.getStat().setLuk((short) amount, player);
                    player.updateSingleStat(MapleStat.LUK, player.getStat().getLuk());
                    break;
                case MAX_HP:
                    amount =  Math.min(99999, Math.abs(amount));
                    player.getStat().setMaxHp(amount, player);
                    player.updateSingleStat(MapleStat.MAX_HP, player.getStat().getMaxHp());
                    break;
                case MAX_MP:
                    amount =  Math.min(99999, Math.abs(amount));
                    player.getStat().setMaxMp(amount, player);
                    player.updateSingleStat(MapleStat.MAX_MP, player.getStat().getMaxMp());
                    break;
            }
        }


        private int getStat(MapleCharacter player) {
            switch (stat) {
                case STR:
                    return player.getStat().getStr();
                case DEX:
                    return player.getStat().getDex();
                case INT:
                    return player.getStat().getInt();
                case LUK:
                    return player.getStat().getLuk();
                case MAX_HP:
                    return player.getStat().getMaxHp();
                case MAX_MP:
                    return player.getStat().getMaxMp();
                default:
                    throw new RuntimeException(); //Will never happen.
            }
        }

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 2) {
                c.getPlayer().dropMessage(5, "使用方式 @STR/DEX/INT/LUK/HP/MP 數字(負的需使用 洗能力點卷軸).");
                return true;
            }
            int change, changestate;
            try {
                change = Integer.parseInt(splitted.get(1));
                changestate = change;
            } catch (NumberFormatException nfe) {
                c.getPlayer().dropMessage(1, "輸入的數字無效.");
                return true;
            }

            if (change <= 0) {
                if(getStat(c.getPlayer()) <= 4){
                    c.getPlayer().dropMessage(1, "能力點不能小於4.");
                    return true;
                }

                if(change <= -100){
                    c.getPlayer().dropMessage(1, "不可減少超過 100 點.");
                    return true;
                }

                if (stat.name() == "MAX_HP" || stat.name() == "MAX_MP") {
                    if(c.getPlayer().getHpApUsed() <= 0){
                        c.getPlayer().dropMessage(1, "您在HP與MP沒有投注過任何能力點.");
                        return true;
                    }
                    if(c.getPlayer().getHpApUsed() - Math.abs(change) < 0){
                        c.getPlayer().dropMessage(1, "您只能輸入不小於 -" + (c.getPlayer().getHpApUsed()) + " 的數字");
                        return true;
                    }
                    if (getStat(c.getPlayer()) <= 50) {
                        c.getPlayer().dropMessage(1, "您的血(魔)已到達最低.");
                        return true;
                    }
                    changestate = 0;
                    for(int i  = 1; i <= Math.abs(change);i++) {
                        changestate -= Randomizer.rand(15, 25);
                    }
                }else{
                    if(getStat(c.getPlayer()) + change < 4){
                        c.getPlayer().dropMessage(1, "能力點不能小於4.");
                        return true;
                    }
                }
                if(!c.getPlayer().haveItem(5050000, Math.abs(change))){
                    c.getPlayer().dropMessage(1, "您沒有足夠的 洗能力點卷軸.");
                    return true;
                }
                if (MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, 5050000, -change, false, false)) {
                    c.getSession().write(CWvsContext.InfoPacket.getShowItemGain(5050000, (short) -change, true));
                }else {
                    return true;
                }
            }else{
                if (stat.name().equals("MAX_HP")) {
                    if (getStat(c.getPlayer()) == 99999) {
                        c.getPlayer().dropMessage(1, "您的血已到達上限.");
                        return true;
                    }
                }else if(stat.name().equals("MAX_MP")){
                    if (getStat(c.getPlayer()) == 99999) {
                        c.getPlayer().dropMessage(1, "您的魔已到達上限.");
                        return true;
                    }
                }
                if(stat.name() == "MAX_HP" || stat.name() == "MAX_MP"){
                    changestate = 0;
                    for(int i  = 1; i <= Math.abs(change);i++) {
                        changestate += Randomizer.rand(50, 65);
                    }
                }
            }
            if (c.getPlayer().getRemainingAp() < change) {
                c.getPlayer().dropMessage(1, "您的能力點不足.");
                return true;
            }
            if (!(stat.name() == "MAX_HP") && !(stat.name() == "MAX_MP")) {
                if (getStat(c.getPlayer()) + change > 99999) {
                    c.getPlayer().dropMessage(1, "所要分配後的能力總和不可大於 " + 99999 + " 點.");
                    return true;
                }
            }else{
                if(getStat(c.getPlayer()) + changestate <= 50)
                    changestate = getStat(c.getPlayer()) - 50;
            }
            if(stat.name() == "MAX_HP" || stat.name() == "MAX_MP"){
                c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() + change));
            }
            setStat(c.getPlayer(), getStat(c.getPlayer()) + changestate);
            c.getPlayer().setRemainingAp((short) (c.getPlayer().getRemainingAp() - change));
            c.getPlayer().updateSingleStat(MapleStat.AVAILABLE_AP, c.getPlayer().getRemainingAp());
            c.getPlayer().dropMessage(5, "加(扣)點成功 您的 " + StringUtil.makeEnumHumanReadable(stat.name()) + " 提高(減少)了 " + changestate + " 點.");
            return true;
        }
    }


    public static class tradeInfo extends AbstractsCommandExecute.TradeExecute{
        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().getTrade().ShowTradeInfo();
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "使用 @tradeInfo 可以查看目前交易物品(以防有些道具不會顯示數量)";
        }
    }

    public static class pmerch extends AbstractsCommandExecute.MerchExecute{

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if(splitted.size() < 6)
            {
                c.getPlayer().dropMessage(-2, "幫助 : 使用 @help 可以查看如何使用指令");
                return true;
            }

            MapleInventoryType type;
            byte slot;
            short bundles; // How many in a bundle
            short perBundle; // Price per bundle
            int price;
            final MapleCharacter chr = c.getPlayer();
            try {
                byte inv = (byte) Integer.parseInt(splitted.get(1));
                if(inv == 3)
                    inv = 4;
                else if(inv == 4)
                    inv = 3;
                type = MapleInventoryType.getByType(inv);
                if(type == null || type == MapleInventoryType.EQUIPPED)
                {
                    c.getPlayer().dropMessage(-2, "錯誤 : 輸入的欄位無效.");
                    return true;
                }

                int slo = Integer.parseInt(splitted.get(2));
                if(slo <= 0 || slo > 96){
                    c.getPlayer().dropMessage(-2, "錯誤 : 輸入的欄位位置無效.");
                    return true;
                }
                slot = (byte) slo;

                int temp = Integer.parseInt(splitted.get(3));
                if (temp <= 0 || temp > 32767){
                    c.getPlayer().dropMessage(-2, "錯誤 : 輸入的組數無效.");
                    return true;
                }
                bundles = (short) temp; // How many in a bundle

                temp = Integer.parseInt(splitted.get(4));
                if (temp <= 0 || temp > 32767){
                    c.getPlayer().dropMessage(-2, "錯誤 : 輸入的單組數量無效.");
                    return true;
                }
                perBundle = (short) temp; // Price per bundle

                price = Integer.parseInt(splitted.get(5));
                if(price <= 0){
                    c.getPlayer().dropMessage(-2, "錯誤 : 輸入的價格無效.");
                    return true;
                }

            } catch (NumberFormatException nfe) {
                c.getPlayer().dropMessage(-2, "錯誤 : 輸入的數字無效.");
                return true;
            }

            final Item ivItem = chr.getInventory(type).getItem(slot);

            if (bundles <= 0 && !GameConstants.isRechargable(ivItem.getItemId()) || perBundle <= 0) {
                return true;
            }
            final IMaplePlayerShop shop = chr.getPlayerShop();

            if (shop == null || !shop.isOwner(chr) || shop instanceof MapleMiniGame) {
                return true;
            }

            if(!shop.isAvailable()){
                c.getPlayer().dropMessage(-2, "錯誤 : 此指令只能在整理商店使用,請先讓商店開張.");
                return true;
            }
            final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();


            if (ivItem != null) {
                if(!ii.isCash(ivItem.getItemId())){
                    c.getPlayer().dropMessage(-2, "錯誤 : 只能對點數裝備使用指令.");
                    return true;
                }


                long check = bundles * perBundle;
                if (check > 32767 || check <= 0) { //This is the better way to check.
                    return true;
                }
                final short bundles_perbundle = (short) (bundles * perBundle);
//                    if (bundles_perbundle < 0) { // int_16 overflow
//                        return;
//                    }
                if (ivItem.getQuantity() >= bundles_perbundle || ( !GameConstants.isRechargable(ivItem.getItemId()) && bundles_perbundle == 1  ) ) {
                    final short flags = ivItem.getFlag();
                    if (ItemFlag.UNTRADEABLE.check(flags) || ItemFlag.LOCK.check(flags)) {
                        c.getPlayer().dropMessage(-2, "錯誤 : 此道具已加鎖  ");
                        c.getSession().write(CWvsContext.enableActions());
                        return true;
                    }
                    if (ii.isDropRestricted(ivItem.getItemId()) || ii.isAccountShared(ivItem.getItemId())) {
                        if (!(ItemFlag.KARMA_EQ.check(flags) || ItemFlag.KARMA_USE.check(flags))) {
                            c.getPlayer().dropMessage(-2, "錯誤 : 此道具無法交易  ");
                            c.getSession().write(CWvsContext.enableActions());
                            return true;
                        }
                    }
                    if (GameConstants.isThrowingStar(ivItem.getItemId()) || GameConstants.isBullet(ivItem.getItemId())) {
                        // Ignore the bundles
                        MapleInventoryManipulator.removeFromSlot(c, type, slot, ivItem.getQuantity(), true);

                        final Item sellItem = ivItem.copy();
                        shop.addItem(new MaplePlayerShopItem(sellItem, (short) 1, price));
                    } else {
                        MapleInventoryManipulator.removeFromSlot(c, type, slot, bundles_perbundle, true);

                        final Item sellItem = ivItem.copy();
                        sellItem.setQuantity(perBundle);
                        shop.addItem(new MaplePlayerShopItem(sellItem, bundles, price));
                    }
                    c.getSession().write(PlayerShopPacket.shopItemUpdate(shop));
                }else{
                    c.getPlayer().dropMessage(-2, "錯誤 : 您沒有足夠的道具.");
                    return true;
                }
            }else{
                c.getPlayer().dropMessage(-2, "錯誤 : 無此道具.");
                return true;
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "使用方式 @pmerch 欄位 位置 組數 每組數量 價格";
        }
    }
    public static class peq extends AbstractsCommandExecute.TradeExecute{
        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 2) {
                c.getPlayer().dropMessage(-2, "使用方式 @peq + 道具位置(1~96).");
                return true;
            }
            int slot;
            try {
                slot = Integer.parseInt(splitted.get(1));
            } catch (NumberFormatException nfe) {
                c.getPlayer().dropMessage(5, "錯誤 : 輸入的數字無效.");
                return true;
            }
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

            MapleInventory invy = c.getPlayer().getInventory(MapleInventoryType.EQUIP);
            Item item = null;
            for (Item i : invy.list()) {
                if (i.getPosition() == slot) {
                    item = i;
                    break;
                }
            }
            if(item == null){
                c.getPlayer().dropMessage(-2, "錯誤 : 沒有此道具");
                return true;
            }


            final short flag = item.getFlag();
            if (ItemFlag.UNTRADEABLE.check(flag) || ItemFlag.LOCK.check(flag)) {
                c.getPlayer().dropMessage(-2, "錯誤 : 此道具已加鎖  ");
                return true;
            }

            short quantity = 1;
            byte targetSlot = -1;
            if (c.getPlayer().getTrade() != null) {
                boolean canTrade = true;
                if(!ii.isCash(item.getItemId()))
                    canTrade = false;
                if (item.getItemId() == 4000463 || !canTrade) {
                    c.getPlayer().dropMessage(-2, "錯誤 : 該道具無法使用指令進行交易.");
                    c.getSession().write(CWvsContext.enableActions());
                } else if (quantity <= item.getQuantity() || GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
                    c.getPlayer().getTrade().setItems(c, item, targetSlot, quantity);
                } else{
                    c.getPlayer().dropMessage(-2, "錯誤 : 您沒有這麼多這樣道具.  ");
                    c.getSession().write(CWvsContext.enableActions());
                }
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "@peq <位置>";
        }
    }
    public static class pcs extends AbstractsCommandExecute.TradeExecute{
        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 3) {
                c.getPlayer().dropMessage(-2, "使用方式 @pcs+道具位置(1~96)+數量.");
                return true;
            }
            int slot;
            short quantity;
            try {
                slot = Integer.parseInt(splitted.get(1));
                quantity = (short) Integer.parseInt(splitted.get(2));
            } catch (NumberFormatException nfe) {
                c.getPlayer().dropMessage(5, "錯誤 : 輸入的數字無效.");
                return true;
            }
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

            MapleInventory invy = c.getPlayer().getInventory(MapleInventoryType.USE);
            Item item = null;
            for (Item i : invy.list()) {
                if (i.getPosition() == slot) {
                    item = i;
                    break;
                }
            }
            if(item == null){
                c.getPlayer().dropMessage(-2, "錯誤 : 沒有此道具");
                return true;
            }

            final short flag = item.getFlag();
            if (ItemFlag.UNTRADEABLE.check(flag) || ItemFlag.LOCK.check(flag)) {
                c.getPlayer().dropMessage(-2, "錯誤 : 此道具已加鎖  ");
                return true;
            }

            byte targetSlot = -1;
            if (c.getPlayer().getTrade() != null) {
                boolean canTrade = true;
                if(!ii.isCash(item.getItemId()))
                    canTrade = false;
                if (item.getItemId() == 4000463 || !canTrade) {
                    c.getPlayer().dropMessage(-2, "錯誤 : 道具無法使用指令進行交易.");
                    c.getSession().write(CWvsContext.enableActions());
                } else if ((quantity <= item.getQuantity() && quantity >= 0) || GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
                    c.getPlayer().getTrade().setItems(c, item, targetSlot, quantity);
                } else{
                    c.getPlayer().dropMessage(-2, "錯誤 : 您沒有這麼多這樣道具.  ");
                    c.getSession().write(CWvsContext.enableActions());
                }
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "@pcs <位置> <數量>";
        }
    }
    public static class petc extends AbstractsCommandExecute.TradeExecute{
        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 3) {
                c.getPlayer().dropMessage(-2, "使用方式 @petc + 道具位置(1~96) + 數量.");
                return true;
            }
            int slot;
            short quantity;
            try {
                slot = Integer.parseInt(splitted.get(1));
                quantity = (short) Integer.parseInt(splitted.get(2));
            } catch (NumberFormatException nfe) {
                c.getPlayer().dropMessage(5, "錯誤 : 輸入的數字無效 .");
                return false;
            }
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

            MapleInventory invy = c.getPlayer().getInventory(MapleInventoryType.ETC);
            Item item = null;
            for (Item i : invy.list()) {
                if (i.getPosition() == slot) {
                    item = i;
                    break;
                }
            }
            if(item == null){
                c.getPlayer().dropMessage(-2, "錯誤 : 沒有此道具");
                return true;
            }

            final short flag = item.getFlag();
            if (ItemFlag.UNTRADEABLE.check(flag) || ItemFlag.LOCK.check(flag)) {
                c.getPlayer().dropMessage(-2, "錯誤 : 此道具已加鎖  ");
                return true;
            }

            byte targetSlot = -1;
            if (c.getPlayer().getTrade() != null) {
                boolean canTrade = true;
                if(!ii.isCash(item.getItemId()))
                    canTrade = false;
                if (item.getItemId() == 4000463 || !canTrade) {
                    c.getPlayer().dropMessage(-2, "錯誤 : 道具無法使用指令進行交易 .");
                    c.getSession().write(CWvsContext.enableActions());
                } else if ((quantity <= item.getQuantity() && quantity >= 0)|| GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
                    c.getPlayer().getTrade().setItems(c, item, targetSlot, quantity);
                    c.getPlayer().dropMessage(-2, "玩家 " + c.getPlayer().getName() + " 提供 " + MapleItemInformationProvider.getInstance().getName(item.getItemId()) + "x" + quantity);
                    c.getPlayer().getTrade().getPartner().getChr().dropMessage(-2, "玩家 " + c.getPlayer().getName() + " 提供 " + MapleItemInformationProvider.getInstance().getName(item.getItemId()) + "x" + quantity);
                } else{
                    c.getPlayer().dropMessage(-2, "錯誤 : 您沒有這麼多這樣道具.  ");
                    c.getSession().write(CWvsContext.enableActions());
                }
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "使用方式 @petc <位置> <數量>";
        }
    }
    public static class pset extends AbstractsCommandExecute.TradeExecute{
        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 3) {
                c.getPlayer().dropMessage(-2, "使用方式 @pset + 道具位置(1~96) + 數量.");
                return true;
            }
            int slot;
            short quantity;
            try {
                slot = Integer.parseInt(splitted.get(1));
                quantity = (short) Integer.parseInt(splitted.get(2));
            } catch (NumberFormatException nfe) {
                c.getPlayer().dropMessage(5, "錯誤 : 輸入的數字無效.  ");
                return true;
            }
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

            MapleInventory invy = c.getPlayer().getInventory(MapleInventoryType.SETUP);
            Item item = null;
            for (Item i : invy.list()) {
                if (i.getPosition() == slot) {
                    item = i;
                    break;
                }
            }
            if(item == null){
                c.getPlayer().dropMessage(-2, "錯誤 : 沒有此道具");
                return true;
            }

            final short flag = item.getFlag();
            if (ItemFlag.UNTRADEABLE.check(flag) || ItemFlag.LOCK.check(flag)) {
                c.getPlayer().dropMessage(-2, "錯誤 : 此道具已加鎖  ");
                return true;
            }



            byte targetSlot = -1;
            if (c.getPlayer().getTrade() != null) {
                boolean canTrade = true;
                if(!ii.isCash(item.getItemId()))
                    canTrade = false;
                if (item.getItemId() == 4000463 || !canTrade) {
                    c.getPlayer().dropMessage(-2, "錯誤 : 道具無法進行交易  .");
                    c.getSession().write(CWvsContext.enableActions());
                } else if ((quantity <= item.getQuantity() && quantity >= 0) || GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
                    c.getPlayer().getTrade().setItems(c, item, targetSlot, quantity);
                    c.getPlayer().dropMessage(-2, "玩家 " + c.getPlayer().getName() + " 提供 " + MapleItemInformationProvider.getInstance().getName(item.getItemId()) + "x" + quantity);
                    c.getPlayer().getTrade().getPartner().getChr().dropMessage(-2, "玩家 " + c.getPlayer().getName() + " 提供 " + MapleItemInformationProvider.getInstance().getName(item.getItemId()) + "x" + quantity);
                } else{
                    c.getPlayer().dropMessage(-2, "錯誤 : 您沒有這麼多這樣道具.  ");
                    c.getSession().write(CWvsContext.enableActions());
                }
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "@pset <位置> <數量>";
        }
    }
    public static class pcash extends AbstractsCommandExecute.TradeExecute{
        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 3) {
                c.getPlayer().dropMessage(-2, "使用方式 @pcash + 道具位置(1~96) + 數量.");
                return true;
            }
            int slot;
            short quantity;
            try {
                slot = Integer.parseInt(splitted.get(1));
                quantity = (short) Integer.parseInt(splitted.get(2));
            } catch (NumberFormatException nfe) {
                c.getPlayer().dropMessage(5, "錯誤 : 輸入的數字無效.   ");
                return true;
            }
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();

            MapleInventory invy = c.getPlayer().getInventory(MapleInventoryType.CASH);
            Item item = null;
            for (Item i : invy.list()) {
                if (i.getPosition() == slot) {
                    item = i;
                    break;
                }
            }
            if(item == null){
                c.getPlayer().dropMessage(-2, "錯誤 : 沒有此道具  ");
                return true;
            }

            final short flag = item.getFlag();
            if (ItemFlag.UNTRADEABLE.check(flag) || ItemFlag.LOCK.check(flag)) {
                c.getPlayer().dropMessage(-2, "錯誤 : 此道具已加鎖  ");
                return true;
            }



            byte targetSlot = -1;
            if (c.getPlayer().getTrade() != null) {
                boolean canTrade = true;
                if(!ii.isCash(item.getItemId()))
                    canTrade = false;
                if (item.getItemId() == 4000463 || !canTrade) {
                    c.getPlayer().dropMessage(-2, "錯誤 : 道具無法使用指令進行交易.  ");
                    c.getSession().write(CWvsContext.enableActions());
                } else if ((quantity <= item.getQuantity() && quantity >= 0)|| GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
                    c.getPlayer().getTrade().setItems(c, item, targetSlot, quantity);
                    c.getPlayer().dropMessage(-2, "玩家 " + c.getPlayer().getName() + " 提供 " + MapleItemInformationProvider.getInstance().getName(item.getItemId()) + "x" + quantity);
                    c.getPlayer().getTrade().getPartner().getChr().dropMessage(-2, "玩家 " + c.getPlayer().getName() + " 提供 " + MapleItemInformationProvider.getInstance().getName(item.getItemId()) + "x" + quantity);
                } else{
                    c.getPlayer().dropMessage(-2, "錯誤 : 您沒有這麼多這樣道具.  ");
                    c.getSession().write(CWvsContext.enableActions());
                }
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "@pcash <位置> <數量>";
        }
    }

    public static class 掉寶查詢 extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            NPCScriptManager.getInstance().start(c, 9330003, "掉寶查詢");
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "@掉寶查詢 - 掉寶查詢";
        }
    }
}
