package client.messages.commands;

import client.MapleCharacter;
import client.MapleClient;
import client.MapleStat;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import client.inventory.MapleInventoryIdentifier;
import client.messages.CommandProcessorUtil;
import client.skill.SkillFactory;
import constants.GameConstants;
import constants.MapConstants;
import constants.ServerConstants;
import database.DatabaseConnection;
import ecpay.payment.integration.PaymentAIO;
import handling.RecvPacketOpcode;
import handling.SendPacketOpcode;
import handling.channel.ChannelServer;
import handling.channel.MapleGuildRanking;
import handling.world.World;
import handling.world.guild.MapleBBSThread;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import scripting.PortalScriptManager;
import scripting.ReactorScriptManager;
import server.*;
import server.Timer;
import server.cashshop.CashItemFactory;
import server.life.*;
import server.maps.*;
import server.quest.MapleQuest;
import server.status.MapleBuffStatus;
import tools.StringUtil;
import tools.data.MaplePacketLittleEndianWriter;
import tools.packet.CField;
import tools.types.Pair;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import server.MapleInventoryManipulator;
import tools.packet.CWvsContext;

import static constants.ServerConstants.DonateRate;

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

    public static class SendToClient extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(22);
            mplew.write(World.MD5.getBytes());
            c.getClinetS().sendPacket(mplew.getPacket());
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "";
        }
    }

    public static class setDetectMD5 extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            World.MD5 = args.get(1);
            c.getPlayer().dropMessage("設定成功 : " + args.get(1));
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "設定 !setDetectMD5 <MD5>";
        }
    }
    public static class setDetectVers extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            World.MD5 = args.get(1);
            c.getPlayer().dropMessage("設定成功 : " + args.get(1));
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "設定 !setDetectVers <Vers>";
        }
    }

    public static class online extends 上線 {
    }

    public static class 上線 extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleCharacter chr = c.getPlayer();
            int world = World.Find.findWorld(chr.getId());
            if (splitted.size() == 1) {
                int total = 0;
                if (chr.isGM()) {
                    chr.dropMessage(5, "----------------------------------(all / ch)-----------------------------------------");
                }
                chr.dropMessage(5, "-------------------------------------------------------------------------------------");
                for (ChannelServer cserv : ChannelServer.getAllInstance(world)) {
                    int curConnected = cserv.getConnectedClients();
                    //curConnected += 服務端配置.上線人數底 / ChannelServer.getAllInstances().size();
                    chr.dropMessage(6, "頻道: " + cserv.getChannel() + " 在線人數: " + curConnected);
                    total += curConnected;
                }
                chr.dropMessage(5, "當前伺服器總計線上人數: " + total);
                chr.dropMessage(5, "-------------------------------------------------------------------------------------");
                return true;
            }
            if (splitted.size() < 4 && splitted.get(1).equalsIgnoreCase("all") && chr.isGM()) {
                int total = 0;
                chr.dropMessage(5, "-------------------------------------------------------------------------------------");
                for (ChannelServer cserv : ChannelServer.getAllInstance(world)) {
                    int curConnected = cserv.getConnectedClients();
                    if (curConnected != 0) {
                        chr.dropMessage(5, "頻道: " + cserv.getChannel() + " 在線人數: " + curConnected);
                    }
                    total += curConnected;
                    for (MapleCharacter chr1 : cserv.getPlayerStorage().getAllCharacters()) {
                        //略過自由市場 和 釣魚地圖的玩家
                        /*if (splitted.size() == 3 && MapConstants.isMarketMap(chr1.getMapId()) && MapConstants.isFishingMap(chr1.getMapId())) {
                        continue;
                    }*/
                        if (chr1 != null) {
                            StringBuilder ret = new StringBuilder();
                            ret.append("  ");
                            ret.append(StringUtil.getRightPaddedStr(chr1.getName(), ' ', 12));
                            ret.append(" ID: ");
                            ret.append(StringUtil.getRightPaddedStr(String.valueOf(chr1.getId()), ' ', 3));
                            ret.append(" 等級: ");
                            ret.append(StringUtil.getRightPaddedStr(String.valueOf(chr1.getLevel()), ' ', 3));
                            ret.append(" 職業: ");
                            ret.append(StringUtil.getRightPaddedStr(MapleCarnivalChallenge.getJobNameById(chr1.getJob()), ' ', 14));
                            if (chr1.getMap() != null) {
                                ret.append(" 地圖: ");
                                ret.append(chr1.getMapId());
                                ret.append("-");
                                ret.append(chr1.getMap().getMapName());
                            }
                            chr.dropMessage(6, ret.toString());
                        }
                    }
                }
                chr.dropMessage(5, "當前伺服器總計線上人數: " + total);
                chr.dropMessage(6, "-------------------------------------------------------------------------------------");
            } else if (splitted.get(1).equalsIgnoreCase("ch")) {
                chr.dropMessage(6, "上線的角色 頻道-" + c.getChannel() + ":");
                chr.dropMessage(6, c.getChannelServer().getPlayerStorage().getOnlinePlayers(true));
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!online - 查看在線人數";
        }
    }

    public static class LevelUp extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            if (c.getPlayer().getLevel() >= 200){
                c.getPlayer().setPrimexe(GameConstants.getExpNeededForHighLevel(c.getPlayer().getLevel(), c.getPlayer().getJob()) - 1);
                c.getPlayer().setExp(0);
            }else {
                c.getPlayer().setExp(GameConstants.getExpNeededForLevel(c.getPlayer().getLevel()) - 1);
                c.getPlayer().setPrimexe(0);
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!LevelUp - 獲得經驗";
        }
    }

    public static class EnableEnhance extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            if(ServerConstants.isEnhanceEnable){
                ServerConstants.isEnhanceEnable = false;
                c.getPlayer().dropMessage(1, "成功 關閉裝備強化");
            }else{
                ServerConstants.isEnhanceEnable = true;
                c.getPlayer().dropMessage(1, "成功 開啟裝備強化");
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!EnableEnhance - 裝備強化開關";
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
            c.getPlayer().setLevel((short) (Short.parseShort(args.get(1)) - 1));
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

    public static class WarpHere extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
            if (victim != null) {
                victim.changeMap(c.getPlayer().getMap(), c.getPlayer().getMap().findClosestSpawnpoint(c.getPlayer().getPosition()));
            } else {
                int ch = World.Find.findChannel(splitted.get(1));
                if (ch < 0) {
                    c.getPlayer().dropMessage(5, "Not found.");
                    return false;
                }
                victim = ChannelServer.getInstance(c.getWorld(), ch).getPlayerStorage().getCharacterByName(splitted.get(1));
                c.getPlayer().dropMessage(5, "Victim is cross changing channel.");
                victim.dropMessage(5, "Cross changing channel.");
                if (victim.getMapId() != c.getPlayer().getMapId()) {
                    final MapleMap mapp = victim.getClient().getChannelServer().getMapFactory().getMap(c.getPlayer().getMapId());
                    victim.changeMap(mapp, mapp.getPortal(0));
                }
                victim.changeChannel(c.getChannel());
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!WarpHere <玩家ID>";
        }
    }

    public static class GiveMP extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 3) {
                c.getPlayer().dropMessage(6, "!giveMP <player> <amount>.");
                return false;
            }
            MapleCharacter chrs = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
            if (chrs == null) {
                c.getPlayer().dropMessage(6, "請確認有在正確的頻道");
            } else {
                chrs.modifyCSPoints(2, Integer.parseInt(splitted.get(2)));
                c.getPlayer().dropMessage(6, "在您給了" + splitted.get(1) +" " + splitted.get(2)  + "楓點了之後 共擁有 " + chrs.getCSPoints(2) + " 楓點");
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!giveMP <player> <amount>";
        }
    }

    public static class GiveMeso extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 3) {
                c.getPlayer().dropMessage(6, "!GiveMeso <player> <amount>.");
                return false;
            }
            MapleCharacter chrs = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
            if (chrs == null) {
                c.getPlayer().dropMessage(6, "請確認有在正確的頻道");
            } else {
                chrs.gainMeso(Integer.parseInt(splitted.get(2)), true);
                c.getPlayer().dropMessage(6, "在您給了" + splitted.get(1) +" " + splitted.get(2)  + "楓幣了之後 共擁有 " + chrs.getCSPoints(2) + " 楓幣");
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!GiveMeso <player> <amount>";
        }
    }

    public static class GivePoint extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 3) {
                c.getPlayer().dropMessage(6, "!GivePoint <player> <amount>.");
                return false;
            }
            MapleCharacter chrs = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
            if (chrs == null) {
                c.getPlayer().dropMessage(6, "請確認有在正確的頻道");
            } else {
                chrs.gainPoints(Integer.parseInt(splitted.get(2)));
                c.getPlayer().dropMessage(6, "在您給了" + splitted.get(1) +" " + splitted.get(2)  + "贊助點了之後 共擁有 " + chrs.getCSPoints(2) + " 贊助點");
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!GivePoint <player> <amount>";
        }
    }

    public static class Spawn extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            final int mid = Integer.parseInt(splitted.get(1));
            final int num = Math.min(CommandProcessorUtil.getOptionalIntArg(splitted.toArray(new String[0]), 2, 1), 500);

            Long hp = CommandProcessorUtil.getNamedLongArg(splitted.toArray(new String[0]), 1, "hp");
            Integer exp = CommandProcessorUtil.getNamedIntArg(splitted.toArray(new String[0]), 1, "exp");
            Double php = CommandProcessorUtil.getNamedDoubleArg(splitted.toArray(new String[0]), 1, "php");
            Double pexp = CommandProcessorUtil.getNamedDoubleArg(splitted.toArray(new String[0]), 1, "pexp");
            Integer level = CommandProcessorUtil.getNamedIntArg(splitted.toArray(new String[0]), 1, "level");

            MapleMonster onemob;
            try {
                onemob = MapleLifeFactory.getMonster(mid);
            } catch (RuntimeException e) {
                c.getPlayer().dropMessage(5, "Error: " + e.getMessage());
                return false;
            }
            if(onemob == null){
                c.getPlayer().dropMessage(5, "Error: 無此ID");
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

            if(level != null)
                onemob.getStats().setLevel(level.shortValue());
            if (newhp < 1) {
                newhp = 1;
            }


            final OverrideMonsterStats overrideStats = new OverrideMonsterStats(newhp, onemob.getMobMaxMp(), newexp, false);
            for (int i = 0; i < num; i++) {
                MapleMonster mob = MapleLifeFactory.getMonster(mid);
                mob.setGMSpawn(true);
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

    public static class GainMeso extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            c.getPlayer().gainMeso(Integer.MAX_VALUE - c.getPlayer().getMeso(), true);
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!GainMeso - 楓幣全滿";
        }
    }

    public static class Packet extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            if (args.size() > 1) {
                c.getSession().write(CField.getPacketFromHexString(StringUtil.joinStringFrom(args.toArray(new String[0]), 1)));
            } else {
                c.getPlayer().dropMessage(6, "請輸入數據!");
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!Packet <封包資料> - 傳送封包";
        }
    }

    public static class setAdminMode extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            if(ServerConstants.ADMIN_ONLY){
                ServerConstants.ADMIN_ONLY = false;
                c.getPlayer().dropMessage("管理員登入模式已關閉");
            }else{
                ServerConstants.ADMIN_ONLY = true;
                c.getPlayer().dropMessage("管理員登入模式已啟用");
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!Packet <封包資料> - 傳送封包";
        }
    }

    public static class Hide extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            SkillFactory.getSkill(9001004).getEffect(1).applyTo(c.getPlayer());
            c.getPlayer().dropMessage(6, "管理員隱藏 = 開啟 \r\n 解除請輸入!unhide");
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!hide - 管理員隱藏";
        }
    }

    public static class UnHide extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            c.getPlayer().dispelBuff(9001004);
            c.getPlayer().dropMessage(6, "管理員隱藏 = 關閉 \r\n 開啟請輸入!hide");
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!unhide - 解除管理員隱藏";
        }
    }

    public static class SReactor extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            MapleReactorStats reactorSt = MapleReactorFactory.getReactor(Integer.parseInt(args.get(1)));
            MapleReactor reactor = new MapleReactor(reactorSt, Integer.parseInt(args.get(1)));
            reactor.setDelay(-1);
            reactor.setPosition(c.getPlayer().getPosition());
            c.getPlayer().getMap().spawnReactor(reactor);
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!SReactor [ID]- 招喚反應物";
        }
    }

    public static class ResetReactor extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            c.getPlayer().getMap().resetReactors();
            return true;
        }
        @Override
        public String getHelpMessage() {
            return "!ResetReactor - 重新整理反應物";
        }
    }

    public static class SetHair extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            int id = Integer.parseInt(splitted.get(1));
            MapleCharacter player = c.getPlayer();
            player.setHair(id);
            player.updateSingleStat(MapleStat.HAIR, id);
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!SetHair [ID]- 更換髮型";
        }
    }

    public static class SetFace extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            int id = Integer.parseInt(splitted.get(1));
            MapleCharacter player = c.getPlayer();
            player.setFace(id);
            player.updateSingleStat(MapleStat.FACE, id);
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!SetFace [ID]- 更換臉型";
        }
    }

    public static class LoadWebScript extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            String exe = "python";
            String command = "C:\\chromedriver\\__main__.py";
            String num1 = "1";
            String num2 = "2";
            String[] cmdArr = new String[] {exe, command, num1, num2};
            try {
                Process process = Runtime.getRuntime().exec(cmdArr);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!LoadWebScript - 重載爬蟲";
        }
    }

    public static class SetSkin extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            byte id = Byte.parseByte(splitted.get(1));
            MapleCharacter player = c.getPlayer();
            player.setSkinColor(id);
            player.updateSingleStat(MapleStat.SKIN, id);
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!SetSkin [ID]- 更換皮膚";
        }
    }

    public static class Find extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() == 1) {
                c.getPlayer().dropMessage(6, splitted.get(0) + ": <NPC> <MOB> <ITEM> <MAP> <SKILL>");
            } else if (splitted.size() == 2) {
                c.getPlayer().dropMessage(6, "請輸入要搜尋的關鍵字.");
            } else {
                String type = splitted.get(1);
                String search = StringUtil.joinStringFrom(splitted.toArray(new String[0]), 2);
                MapleData data = null;
                MapleDataProvider dataProvider = MapleDataProviderFactory.getDataProvider(new File("./wz" + "/" + "String.wz"));
                c.getPlayer().dropMessage(6, "<<Type: " + type + " | Search: " + search + ">>");

                if (type.equalsIgnoreCase("NPC")) {
                    List<String> retNpcs = new ArrayList<String>();
                    data = dataProvider.getData("Npc.img");
                    List<Pair<Integer, String>> npcPairList = new LinkedList<Pair<Integer, String>>();
                    for (MapleData npcIdData : data.getChildren()) {
                        npcPairList.add(new Pair<Integer, String>(Integer.parseInt(npcIdData.getName()), MapleDataTool.getString(npcIdData.getChildByPath("name"), "NO-NAME")));
                    }
                    for (Pair<Integer, String> npcPair : npcPairList) {
                        if (npcPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            retNpcs.add(npcPair.getLeft() + " - " + npcPair.getRight());
                        }
                    }
                    if (retNpcs != null && retNpcs.size() > 0) {
                        for (String singleRetNpc : retNpcs) {
                            c.getPlayer().dropMessage(6, singleRetNpc);
                        }
                    } else {
                        c.getPlayer().dropMessage(6, "無此NPC");
                    }

                } else if (type.equalsIgnoreCase("MAP")) {
                    List<String> retMaps = new ArrayList<String>();
                    data = dataProvider.getData("Map.img");
                    List<Pair<Integer, String>> mapPairList = new LinkedList<Pair<Integer, String>>();
                    for (MapleData mapAreaData : data.getChildren()) {
                        for (MapleData mapIdData : mapAreaData.getChildren()) {
                            mapPairList.add(new Pair<Integer, String>(Integer.parseInt(mapIdData.getName()), MapleDataTool.getString(mapIdData.getChildByPath("streetName"), "NO-NAME") + " - " + MapleDataTool.getString(mapIdData.getChildByPath("mapName"), "NO-NAME")));
                        }
                    }
                    for (Pair<Integer, String> mapPair : mapPairList) {
                        if (mapPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            retMaps.add(mapPair.getLeft() + " - " + mapPair.getRight());
                        }
                    }
                    if (retMaps != null && retMaps.size() > 0) {
                        for (String singleRetMap : retMaps) {
                            c.getPlayer().dropMessage(6, singleRetMap);
                        }
                    } else {
                        c.getPlayer().dropMessage(6, "無此地圖");
                    }
                } else if (type.equalsIgnoreCase("MOB")) {
                    List<String> retMobs = new ArrayList<String>();
                    data = dataProvider.getData("Mob.img");
                    List<Pair<Integer, String>> mobPairList = new LinkedList<Pair<Integer, String>>();
                    for (MapleData mobIdData : data.getChildren()) {
                        mobPairList.add(new Pair<Integer, String>(Integer.parseInt(mobIdData.getName()), MapleDataTool.getString(mobIdData.getChildByPath("name"), "NO-NAME")));
                    }
                    for (Pair<Integer, String> mobPair : mobPairList) {
                        if (mobPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            retMobs.add(mobPair.getLeft() + " - " + mobPair.getRight());
                        }
                    }
                    if (retMobs != null && retMobs.size() > 0) {
                        for (String singleRetMob : retMobs) {
                            c.getPlayer().dropMessage(6, singleRetMob);
                        }
                    } else {
                        c.getPlayer().dropMessage(6, "無此怪物");
                    }

                } else if (type.equalsIgnoreCase("ITEM")) {
                    List<String> retItems = new ArrayList<String>();
                    for (ItemInformation itemPair : MapleItemInformationProvider.getInstance().getAllItems()) {
                        if (itemPair.name.toLowerCase().contains(search.toLowerCase())) {
                            retItems.add(itemPair.itemId + " - " + itemPair.name);
                        }
                    }
                    if (retItems != null && retItems.size() > 0) {
                        for (String singleRetItem : retItems) {
                            c.getPlayer().dropMessage(6, singleRetItem);
                        }
                    } else {
                        c.getPlayer().dropMessage(6, "無此道具");
                    }

                } else if (type.equalsIgnoreCase("SKILL")) {
                    List<String> retSkills = new ArrayList<String>();
                    data = dataProvider.getData("Skill.img");
                    List<Pair<Integer, String>> skillPairList = new LinkedList<Pair<Integer, String>>();
                    for (MapleData skillIdData : data.getChildren()) {
                        skillPairList.add(new Pair<Integer, String>(Integer.parseInt(skillIdData.getName()), MapleDataTool.getString(skillIdData.getChildByPath("name"), "NO-NAME")));
                    }
                    for (Pair<Integer, String> skillPair : skillPairList) {
                        if (skillPair.getRight().toLowerCase().contains(search.toLowerCase())) {
                            retSkills.add(skillPair.getLeft() + " - " + skillPair.getRight());
                        }
                    }
                    if (retSkills != null && retSkills.size() > 0) {
                        for (String singleRetSkill : retSkills) {
                            c.getPlayer().dropMessage(6, singleRetSkill);
                        }
                    } else {
                        c.getPlayer().dropMessage(6, "無此技能");
                    }
                } else {
                    c.getPlayer().dropMessage(6, "抱歉，請重新輸入!");
                }
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!search <NPC> <MOB> <ITEM> <MAP> <SKILL> - 搜尋";
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
                if ((job >= 300 && job < 413) || (job >= 1300 && job < 1500) || (job >= 520 && job < 600)) {
                    distance = 125;
                } else {
                    distance = 50;
                }
                Point p = new Point((int) chr.getPosition().getX() - distance, (int) chr.getPosition().getY());
                long newhp = 800000000000L; //set it to what you want.
                assert mm != null;
                mm.changeLevel(100, 800000000000L);
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
                        if (splitted.size() == 2) {
                            c.getPlayer().changeMap(target, target.getPortal(0));
                        } else {
                            c.getPlayer().changeMap(target, target.getPortal(Integer.parseInt(splitted.get(2))));
                        }
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

    public static class SP extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().setRemainingSp(CommandProcessorUtil.getOptionalIntArg(splitted, 1, 1));
            c.getPlayer().updateSingleStat(MapleStat.AVAILABLE_SP, 0); // we don't care the value here
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!sp [數量] - 增加SP";
        }
    }

    public static class AP extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleCharacter player = c.getPlayer();
            if (splitted.size() != 2) {
            }
            int ap = Integer.parseInt(splitted.get(1));
            if (ap + player.getRemainingAp() > 32767) {
                player.setRemainingAp(32767);
                player.updateSingleStat(MapleStat.AVAILABLE_AP, 32767);
            } else {
                player.setRemainingAp((short) (ap + player.getRemainingAp()));
                player.updateSingleStat(MapleStat.AVAILABLE_AP, player.getRemainingAp());
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!ap [數量] - 增加AP";
        }
    }

    public static class Job extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().changeJob(Integer.parseInt(splitted.get(1)));
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!job <職業ID> - 切換職業";
        }
    }

    public static class WhereAmI extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().dropMessage(5, "目前地圖 " + c.getPlayer().getMap().getId() + "座標 (" + String.valueOf(c.getPlayer().getPosition().x) + " , " + String.valueOf(c.getPlayer().getPosition().y) + ")");
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!WhereAmI - 查詢目前地圖ID 與 腳色座標";
        }
    }

    public static class Item extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            final int itemId = Integer.parseInt(splitted.get(1));
            final short quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted.toArray(new String[0]), 2, 1);

            if (!c.getPlayer().isAdmin()) {
                for (int i : GameConstants.itemBlock) {
                    if (itemId == i) {
                        c.getPlayer().dropMessage(5, "很抱歉，此物品您的ＧＭ等級無法呼叫.");
                        return false;
                    }
                }
            }
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (GameConstants.isPet(itemId)) {
                MapleInventoryManipulator.addById(c, itemId, (short) 1, "", client.inventory.MaplePet.createPet(itemId, MapleInventoryIdentifier.getInstance()), 10, "GM獲得");
                c.getPlayer().dropMessage(5, "獲得寵物.");
            } else if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + "  不存在");
            } else {
                client.inventory.Item item;

                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                    item = (Equip) ii.getEquipById(itemId);

                } else {
                    item = new client.inventory.Item(itemId, (byte) 0, quantity, (byte) 0);
                }
                item.setGMLog(c.getPlayer().getName());
                c.getPlayer().dropMessage(5, ii.getName(itemId) + "-" + itemId);

                MapleInventoryManipulator.addbyItem(c, item);
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!item <itemID> <數量> - 製作道具";
        }
    }

    public static class ReloadOps extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            SendPacketOpcode.reloadValues();
            RecvPacketOpcode.reloadValues();
            MapleBuffStatus.reloadValues();
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!ReloadOps - 重載包頭";
        }
    }

    public static class ReloadCashshop extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            CashItemFactory.getInstance().initialize();
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!ReloadCashshop - 重載商城";
        }
    }

    public static class ReloadEvents extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            for (ChannelServer instance : ChannelServer.getAllInstance(0)) {
                instance.reloadEvents();
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!ReloadEvents - 重載活動";
        }
    }

    public static class ReloadQuests extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleQuest.InitQuests();
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!ReloadQuests - 重載任務";
        }
    }

    public static class Drop extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            final int itemId = Integer.parseInt(splitted.get(1));
            final short quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted.toArray(new String[0]), 2, 1);
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (GameConstants.isPet(itemId)) {
                c.getPlayer().dropMessage(5, "請在購物商城購買.");
            } else if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " 不存在");
            } else {
                client.inventory.Item toDrop;
                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {

                    toDrop = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                } else {
                    toDrop = new client.inventory.Item(itemId, (byte) 0, (short) quantity, (byte) 0);
                }
                toDrop.setOwner(c.getPlayer().getName());
                toDrop.setGMLog(c.getPlayer().getName());

                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true);
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!drop <itemID> <數量> - 丟道具";
        }
    }

    public static class Heal extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().getStat().setHp(c.getPlayer().getStat().getCurrentMaxHp(), c.getPlayer());
            c.getPlayer().getStat().setMp(c.getPlayer().getStat().getCurrentMaxMp(c.getPlayer().getJob()), c.getPlayer());
            c.getPlayer().updateSingleStat(MapleStat.HP, c.getPlayer().getStat().getCurrentMaxHp());
            c.getPlayer().updateSingleStat(MapleStat.MP, c.getPlayer().getStat().getCurrentMaxMp(c.getPlayer().getJob()));
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!heal - 回復血魔";
        }
    }

    public static class ReloadPortal extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            PortalScriptManager.getInstance().clearScripts();
            c.getPlayer().dropMessage("重新載入傳送點腳本");
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!ReloadPortal - 重新載入傳送點腳本";
        }
    }

    public static class ReloadDrops extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleMonsterInformationProvider.getInstance().clearDrops();
            ReactorScriptManager.getInstance().clearDrops();
            c.getPlayer().dropMessage("重新載入掉寶腳本");
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!ReloadDrops - 重新載入掉寶腳本";
        }
    }

    public static class 關閉伺服器 extends AbstractsCommandExecute {

        protected static Thread t = null;

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().dropMessage(6, "正在關閉伺服器...");
            if (t == null || !t.isAlive()) {
                t = new Thread(ShutdownServer.getInstance());
                ShutdownServer.getInstance().shutdown();
                t.start();
            } else {
                c.getPlayer().dropMessage(6, "關閉進程正在進行或者關閉已完成，請稍候。");
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "關閉伺服器";
        }
    }

    public static class shutdowntime extends 定時關閉伺服器{

    }

    public static class 定時關閉伺服器 extends 關閉伺服器 {

        private static ScheduledFuture<?> ts = null;
        private int minutesLeft = 0;

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 2) {
                c.getPlayer().dropMessage(0, splitted.get(0) + " <時間:分鐘>");
                return false;
            }
            minutesLeft = Integer.parseInt(splitted.get(1));
            c.getPlayer().dropMessage(6, "伺服器將在 " + minutesLeft + " 分鐘后關閉");
            if (ts == null && (t == null || !t.isAlive())) {
                t = new Thread(ShutdownServer.getInstance());
                ts = Timer.EventTimer.getInstance().register(new Runnable() {
                    @Override
                    public void run() {
                        if (minutesLeft == 0) {
                            ShutdownServer.getInstance().shutdown();
                            t.start();
                            ts.cancel(false);
                            return;
                        }
                        World.Broadcast.broadcastMessage(c.getWorld(), CWvsContext.broadcastMsg(6,"伺服器將在 " + minutesLeft + " 分鐘后進行停機維護, 請及時安全的下線, 以免造成不必要的損失。"));
                        minutesLeft--;
                    }
                }, 60000);
            } else {
                c.getPlayer().dropMessage(6, "關閉進程正在進行或者關閉已完成，請稍候。");
            }
            return true;
        }
    }

    public static class ReloadShops extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleShopFactory.getInstance().clear();
            c.getPlayer().dropMessage("重新載入商店數據");
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!ReloadShops - 重新載入商店數據";
        }
    }

    public static class Shop extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleShopFactory shop = MapleShopFactory.getInstance();
            int shopId = Integer.parseInt(splitted.get(1));
            if (shop.getShop(shopId) != null) {
                shop.getShop(shopId).sendShop(c);
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!shop <ID> - 開啟商店";
        }
    }



    public static class Kill extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleCharacter player = c.getPlayer();
            if (splitted.size() < 2) {
                c.getPlayer().dropMessage(6, "Syntax: !kill <list player names>");
                return false;
            }
            MapleCharacter victim = null;
            for (int i = 1; i < splitted.size(); i++) {
                try {
                    victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(i));
                } catch (Exception e) {
                    c.getPlayer().dropMessage(6, "Player " + splitted.get(i) + " not found.");
                }
                if (player.allowedToTarget(victim)) {
                    victim.getStat().setHp(0, victim);
                    victim.getStat().setMp(0, victim);
                    victim.updateSingleStat(MapleStat.HP, 0);
                    victim.updateSingleStat(MapleStat.MP, 0);
                }
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!kill <玩家名字> - 殺";
        }
    }

    public static class KillAll extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;

            if (splitted.size() > 1) {
                int irange = Integer.parseInt(splitted.get(1));
                if (splitted.size() <= 2) {
                    range = irange * irange;
                } else {
                    map = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted.get(2)));
                }
            }
            MapleMonster mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                map.killMonster(mob, c.getPlayer(), false, false, (byte) 1);
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!killall - 殺全地圖怪物(無掉寶)";
        }
    }

    public static class KillAllDrops extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;

            if (splitted.size() > 1) {
                int irange = Integer.parseInt(splitted.get(1));
                if (splitted.size() <= 2) {
                    range = irange * irange;
                } else {
                    map = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted.get(2)));
                }
            }
            MapleMonster mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (MapleMonster) monstermo;
                map.killMonster(mob, c.getPlayer(), true, false, (byte) 1);
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!killalldrops - 殺全地圖怪物";
        }
    }

    public static class NPC extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            int npcId = Integer.parseInt(splitted.get(1));
            MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            if (npc != null && !npc.getName().equals("MISSINGNO")) {
                npc.setPosition(c.getPlayer().getPosition());
                npc.setCy(c.getPlayer().getPosition().y);
                npc.setRx0(c.getPlayer().getPosition().x + 50);
                npc.setRx1(c.getPlayer().getPosition().x - 50);
                npc.setFh(c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId());
                npc.setCustom(true);
                c.getPlayer().getMap().addMapObject(npc);
                c.getPlayer().getMap().broadcastMessage(CField.NPCTalkPacket.spawnNPC(npc, true));
            } else {
                c.getPlayer().dropMessage(6, "錯誤的NPC ID");
                return false;
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!npc - 招喚NPC(非永久)";
        }
    }

    public static class PNPC extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            int npcId = Integer.parseInt(splitted.get(1));
            MapleNPC npc = MapleLifeFactory.getNPC(npcId);
            if (npc != null && !npc.getName().equals("MISSINGNO")) {
                npc.setPosition(c.getPlayer().getPosition());
                npc.setCy(c.getPlayer().getPosition().y);
                npc.setRx0(c.getPlayer().getPosition().x + 50);
                npc.setRx1(c.getPlayer().getPosition().x - 50);
                npc.setFh(c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId());
                npc.setCustom(true);

                try {
                    PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("INSERT INTO wz_customl ( idd, f, fh, cy, rx0, rx1, type, x, y, mid ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )");
                    ps.setInt(1, npcId);
                    ps.setInt(2, 0);
                    ps.setInt(3, c.getPlayer().getMap().getFootholds().findBelow(c.getPlayer().getPosition()).getId());
                    ps.setInt(4, c.getPlayer().getPosition().y);
                    ps.setInt(5, c.getPlayer().getPosition().x + 50);
                    ps.setInt(6, c.getPlayer().getPosition().x - 50);
                    ps.setString(7, "n");
                    ps.setInt(8, c.getPlayer().getPosition().x);
                    ps.setInt(9, c.getPlayer().getPosition().y);
                    ps.setInt(10, c.getPlayer().getMapId());
                    ps.executeUpdate();
                } catch (SQLException SE) {
                    System.err.println("SQL THROW");
                    SE.printStackTrace();
                }

                c.getPlayer().getMap().addMapObject(npc);
                c.getPlayer().getMap().broadcastMessage(CField.NPCTalkPacket.spawnNPC(npc, true));
            } else {
                c.getPlayer().dropMessage(6, "錯誤的NPC ID");
                return false;
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!pnpc - 招喚NPC(永久)";
        }
    }

    public static class lookallmob extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
            MapleMonster mob = null;
            for (final MapleMapObject obj : c.getPlayer().getMap().getAllMonster()) {
                mob = (MapleMonster) obj;
                if (mob.isAlive()) {
                    c.getPlayer().dropMessage(6, "怪物: " + mob.toString());
                }
            }
            if (mob == null) {
                c.getPlayer().dropMessage(6, "沒找到任何怪物");
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!lookallmob - 怪物資訊";
        }
    }

    public static class setGiftMap extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            int count = 0;
            for (MapleCharacter victim : c.getPlayer().getMap().getCharactersThreadsafe()) {
                Connection con = DatabaseConnection.getConnection();
                try {
                    PreparedStatement ps = con.prepareStatement("INSERT INTO giftsender (id , GiftName, isSent, charid, account, SentTime, url) VALUES (DEFAULT , ? , ?, ?, ?, CURRENT_TIMESTAMP, ?)");
                    ps.setString(1, splitted.get(1));
                    ps.setInt(2, 0);
                    ps.setInt(3, victim.getId());
                    ps.setInt(4, victim.getClient().getAccID());
                    ps.setString(5, "管理員新增");
                    ps.executeUpdate();
                    ps.close();
                    count++;
                } catch (SQLException se) {
                    se.printStackTrace();
                }
            }

            c.getPlayer().dropMessage("成功添加 " + count + " 人 - " + splitted.get(1));
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!setGiftMap <活動名稱> - 將該地圖所有人設定獎勵";
        }
    }

    public static class setGift extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {

            int chrID = -1;
            String ChrName = "";
            Connection con = DatabaseConnection.getConnection();
            ResultSet rs = null;
            ChrName = splitted.get(1);
            chrID = -1;
            try {
                PreparedStatement ps = null;
                if(chrID > 0) {
                    ps = con.prepareStatement("SELECT * FROM characters WHERE id = ?");
                    ps.setInt(1, chrID);
                    rs = ps.executeQuery();
                    if (!rs.next()) {
                        c.getPlayer().dropMessage("查無此ID");
                        ps.close();
                        rs.close();
                        return false;
                    }

                    ps = con.prepareStatement("INSERT INTO giftsender (id, FBName , GiftName, isSent, charid, account, SentTime, url) VALUES (DEFAULT, ? , ? , ?, ?, ?, CURRENT_TIMESTAMP, ?)");
                    ps.setString(1, splitted.get(2));
                    ps.setString(2, splitted.get(3));
                    ps.setInt(3, 0);
                    ps.setInt(4, chrID);
                    ps.setString(5, rs.getString("name"));
                    ps.setString(6, rs.getString("name"));

                    ps.executeUpdate();
                    ps.close();
                    rs.close();

                    c.getPlayer().dropMessage("添加成功 腳色id : " + chrID + " 禮物名 : " + splitted.get(3));
                }else{
                    ps = con.prepareStatement("SELECT * FROM characters WHERE name = ?");
                    ps.setString(1, ChrName);
                    rs = ps.executeQuery();
                    if (!rs.next()) {
                        c.getPlayer().dropMessage("查無此名稱");
                        ps.close();
                        rs.close();
                        return false;
                    }

                    ps = con.prepareStatement("INSERT INTO giftsender (id, FBName , GiftName, isSent, charid, account, SentTime, url) VALUES (DEFAULT, ? , ? , ?, ?, ?, CURRENT_TIMESTAMP, ?)");
                    ps.setString(1, splitted.get(2));
                    ps.setString(2, splitted.get(3));
                    ps.setInt(3, 0);
                    ps.setInt(4, rs.getInt("id"));
                    ps.setInt(5, rs.getInt("accountid"));
                    ps.setString(6, "管理員新增");

                    ps.executeUpdate();
                    ps.close();
                    rs.close();

                    c.getPlayer().dropMessage("添加成功 腳色 : " + ChrName + " 禮物名 : " + splitted.get(3));

                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!setGift <腳色名稱> <活動名稱> - 將該繳色設定獎勵";
        }
    }


    public static class setGiftWorld extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            int count = 0;
            for (ChannelServer cserv : ChannelServer.getAllInstance(c.getWorld())) {
                for (MapleCharacter victim : cserv.getPlayerStorage().getAllCharacters()) {
                    Connection con = DatabaseConnection.getConnection();
                    try {
                        PreparedStatement ps = null;

                        ps = con.prepareStatement("INSERT INTO giftsender (id , GiftName, isSent, charid, account, SentTime, url) VALUES (DEFAULT , ? , ?, ?, ?, CURRENT_TIMESTAMP, ?)");
                        ps.setString(1, splitted.get(1));
                        ps.setInt(2, 0);
                        ps.setInt(3, victim.getId());
                        ps.setInt(4, victim.getClient().getAccID());
                        ps.setString(5, "管理員新增");

                        ps.executeUpdate();
                        ps.close();
                        count++;
                    } catch (SQLException se) {
                        se.printStackTrace();
                    }
                }
            }
            c.getPlayer().dropMessage("成功添加 " + count + " 人 - " + splitted.get(1));
            return true;
        }


        @Override
        public String getHelpMessage() {
            return "!setGiftWorld <活動名稱> - 將該伺服器所有人設定獎勵";
        }
    }

    public static class SetBillRate extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            DonateRate = Double.parseDouble(splitted.get(1));
            c.getPlayer().dropMessage(6, "設定成功 目前斗內比 1 : " + DonateRate);
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!SetBillRate <Rate> - 設定贊助比";
        }
    }

    public static class openBill extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            String account = splitted.get(1);
            Integer amount = Integer.parseInt(splitted.get(2));
            Date time = Calendar.getInstance().getTime();
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(time);
            PaymentAIO mp = new PaymentAIO();
            String as = amount >= 1000?String.valueOf(amount / 100):String.valueOf(amount);
            String ss = timeStamp+as;
            String url = "";
            int key = 0;
            int accid = 0;
            try (Connection con = DatabaseConnection.getConnection()) {
                boolean isC = true;
                while(isC){
                    Random r = new Random();
                    String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
                    StringBuilder s = new StringBuilder();
                    for (int i = 0; i < 6; i++) {
                        s.append(alphabet.charAt(r.nextInt(alphabet.length())));
                    }
                    try (PreparedStatement ps = con.prepareStatement("SELECT url from paybill_bills where url = ?")){
                        ps.setString(1, s.toString());
                        ResultSet rs = ps.executeQuery();
                        if(rs.next()){
                            continue;
                        }
                        url = s.toString();
                        isC = false;
                    }
                }

                boolean has_acc = false;
                PreparedStatement pps = con.prepareStatement("SELECT * FROM accounts WHERE name = ?", Statement.RETURN_GENERATED_KEYS);
                pps.setString(1, account);
                ResultSet rs = pps.executeQuery();
                if (rs.next()) {
                    has_acc = true;
                    accid = rs.getInt("id");
                }
                if(!has_acc){
                    c.getPlayer().dropMessage("無此帳號");
                    return true;
                }

                try (PreparedStatement ps = con.prepareStatement("INSERT INTO paybill_bills (BillID, money, account, accountID, characterID, Date,isSent,TradeNo, url) VALUES (DEFAULT,?, ?, ?, ?, CURRENT_TIMESTAMP,?,?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, amount);
                    ps.setString(2, account);
                    ps.setInt(3, accid);
                    ps.setInt(4, -1);
                    ps.setInt(5, -1);
                    ps.setString(6, ss);
                    ps.setString(7, url);
                    ps.executeUpdate();

                    rs = ps.getGeneratedKeys();
                    if (!rs.next()) {
                        throw new RuntimeException("[saveItems] 保存道具失败.");
                    }else{
                        key = rs.getInt(1);
                    }
                }

            } catch (SQLException ex) {//130.211.243.179
                ex.printStackTrace();
            }

            String poststr = mp.getCustomBill(amount, Integer.toString(accid), key,time, ss);
            try {
                FileWriter fw = new FileWriter(new File("C:/Bills/" + url + ".html "));
                fw.write(poststr);
                fw.close();
            }catch (IOException e){
                System.out.println(e);
            }

            c.getPlayer().dropMessage(6, "斗內開單成功 帳號 : " + account + " 金額 : " + amount + " 可領取斗內點 : " + (int) Math.ceil(amount.doubleValue() * DonateRate));

            final MaplePacketLittleEndianWriter mplew = new MaplePacketLittleEndianWriter();
            mplew.writeShort(21);
            String real = "http://35.220.252.54:80/" + url;
            mplew.write(real.getBytes());
            c.getClinetS().sendPacket(mplew.getPacket());
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!openBill <帳號> <金額>";
        }
    }

    public static class 封號 extends AbstractsCommandExecute {

        protected boolean hellban = false, ipBan = false;

        private String getCommand() {
            if (hellban) {
                return "匿名封號";
            } else if (ipBan) {
                return "封IP";
            } else {
                return "封號";
            }
        }

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 3) {
                c.getPlayer().dropMessage(5, splitted.get(0) + " <玩家名稱> <理由>");
//                c.getPlayer().dropMessage(5, "If you want to consider this ban as an autoban, set the reason \"AutoBan\"");
                return false;
            }
            StringBuilder sb = new StringBuilder();
            if (hellban) {
                sb.append(splitted.get(1)).append("被封號: ").append(StringUtil.joinStringFrom((String[]) splitted.toArray(), 2));
            } else {
                sb.append(c.getPlayer().getName()).append(" 對 ").append(splitted.get(1)).append(" 進行封號處理: ").append(StringUtil.joinStringFrom((String[]) splitted.toArray(), 2));
            }
            MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
            if (target != null) {
                if ((c.getPlayer().getGMLevel() > target.getGMLevel() || c.getPlayer().isAdmin()) && !target.getClient().isGM() && !target.isAdmin()) {
                    //sb.append(" (IP: ").append(target.getClient().getSessionIPAddress()).append(")");
                    if (target.ban(sb.toString(), hellban || ipBan, false, hellban)) {
                        c.getPlayer().dropMessage(6, "[" + getCommand() + "] " + splitted.get(1) + " 已經被封號");
                        return true;
                    } else {
                        c.getPlayer().dropMessage(6, "[" + getCommand() + "] 封號失敗");
                        return false;
                    }
                } else {
                    c.getPlayer().dropMessage(6, "[" + getCommand() + "] 無法封GM...");
                    return true;
                }
            } else {
                if (MapleCharacter.Ban.ban(splitted.get(1), sb.toString(), false)) {
                    c.getPlayer().dropMessage(6, "[" + getCommand() + "] " + splitted.get(1) + " 已經被離線封號");
                    return true;
                } else {
                    c.getPlayer().dropMessage(6, "[" + getCommand() + "] " + splitted.get(1) + "封號失敗");
                    return false;
                }
            }
        }

        @Override
        public String getHelpMessage() {
            return "!封號 <玩家名稱> <理由>";
        }
    }

    public static class dc extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 2) {
                c.getPlayer().dropMessage(6, splitted.get(0) + " <玩家名稱> ([玩家名稱] [玩家名稱]...)");
                return false;
            }
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
            if (victim != null && c.getPlayer().getGMLevel() >= victim.getGMLevel()) {
                victim.getClient().getSession().close();
                victim.getClient().disconnect(true, false);
                return true;
            } else {
                c.getPlayer().dropMessage(6, "受害者不存在或不在線上。");
                return false;
            }
        }

        @Override
        public String getHelpMessage() {
            return "!dc <玩家名稱> ([玩家名稱] [玩家名稱]...)";
        }
    }

    public static class MapClock extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 2) {
                c.getPlayer().dropMessage(6, splitted.get(0) + " <時間>");
                return false;
            }
            int minutes = Integer.parseInt(splitted.get(1));
            c.getPlayer().getMap().broadcastMessage(CField.getClock(minutes * 60));
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!MapClock <時間>";
        }
    }

    public static class 殺 extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            MapleCharacter player = c.getPlayer();
            if (splitted.size() < 2) {
                c.getPlayer().dropMessage(6, splitted.get(0) + " <玩家名稱> ([玩家名稱] [玩家名稱]...)");
                return false;
            }
            MapleCharacter victim = null;
            for (int i = 1; i < splitted.size(); i++) {
                try {
                    victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(i));
                } catch (Exception e) {
                    c.getPlayer().dropMessage(6, "沒找到受害者 " + splitted.get(i));
                }
                if (player.allowedToTarget(victim) && player.getGMLevel() >= victim.getGMLevel()) {
                    victim.getStat().setHp((short) 0, victim);
                    victim.getStat().setMp((short) 0, victim);
                    victim.updateSingleStat(MapleStat.HP, victim.getStat().getHp());
                    victim.updateSingleStat(MapleStat.MP, victim.getStat().getMp());
                }
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!殺 <玩家名稱> ([玩家名稱] [玩家名稱]...)";
        }
    }

    public static class 臨時封號 extends AbstractsCommandExecute {

        protected boolean ipBan = false;
        private final String[] types = {"外掛", "BOT", "AD", "HARASS", "CURSE", "SCAM", "MISCONDUCT", "SELL", "ICASH", "TEMP", "GM", "IPROGRAM", "MEGAPHONE"};

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 4) {
                c.getPlayer().dropMessage(5, splitted.get(0) + " <玩家名稱> <理由> <時間(小時)>");
                StringBuilder s = new StringBuilder("臨時封號理由: ");
                for (int i = 0; i < types.length; i++) {
                    s.append(i + 1).append(" - ").append(types[i]).append(", ");
                }
                c.getPlayer().dropMessage(6, s.toString());
                return false;
            }
            final MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
            final int reason = Integer.parseInt(splitted.get(2));
            final int numHour = Integer.parseInt(splitted.get(3));

            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, numHour);
            final DateFormat df = DateFormat.getInstance();

            if (victim == null || reason < 0 || reason >= types.length) {
                c.getPlayer().dropMessage(5, "無法找到玩家或者理由是無效的, 輸入" + splitted.get(0) + "查看可用理由");
                return false;
            }
            victim.tempban("因 " + types[reason] + " 被 " + c.getPlayer().getName() + " 臨時封號", cal, reason, ipBan);
            c.getPlayer().dropMessage(5, "玩家 " + splitted.get(1) + " 被臨時封號到 " + df.format(cal.getTime()));
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!臨時封號 <玩家名稱> <理由> <時間(小時)>";
        }
    }

    public static class 臨時封IP extends 臨時封號 {

        public 臨時封IP() {
            ipBan = true;
        }
    }

    public static class 封IP extends 封號 {

        public 封IP() {
            ipBan = true;
        }
    }

    public static class 監禁 extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            if (splitted.size() < 3) {
                c.getPlayer().dropMessage(6, splitted.get(0) + " <玩家名稱> <時間(分鐘,0為永久)>");
                return false;
            }
            MapleCharacter victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted.get(1));
            final int minutes = Math.max(0, Integer.parseInt(splitted.get(2)));
            if (victim != null && c.getPlayer().getGMLevel() >= victim.getGMLevel()) {
                MapleMap target = ChannelServer.getInstance(c.getWorld(), c.getChannel()).getMapFactory().getMap(GameConstants.JAIL);
                victim.getQuestNAdd(MapleQuest.getInstance(GameConstants.JAIL_QUEST)).setCustomData(String.valueOf(minutes * 60));
                victim.changeMap(target, target.getPortal(0));
            } else {
                c.getPlayer().dropMessage(6, "請到此玩家所在的頻道");
                return false;
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!監禁 <玩家名稱> <時間(分鐘,0為永久)>";
        }
    }

    private static void updateGuildRK(){
        try {
            Connection con = DatabaseConnection.getConnection();
            List<MapleGuildRanking.GuildRankingInfo> grs = MapleGuildRanking.getInstance().getRankall();
            for (MapleGuildRanking.GuildRankingInfo gf : grs) {
                PreparedStatement ps = con.prepareStatement("UPDATE guilds SET rankinglastmonth = ? WHERE name = ?");
                ps.setInt(1, (grs.indexOf(gf) + 1));
                ps.setString(2, gf.getName());
                ps.executeUpdate();
                ps.close();
                Objects.requireNonNull(World.Guild.getGuildByName(gf.getName())).setLastMrk((grs.indexOf(gf) + 1));
            }
        }catch (SQLException se) {
            System.err.println("Error saving guild to SQL");
        }
    }

    public static class LoadGR extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            updateGuildRK();
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!LoadGR - 重載上月公會排名)";
        }
    }

    public static class ResetDR extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("DELETE FROM dojo_ranks_month");
                ps.executeUpdate();
                ps.close();

                ps = con.prepareStatement("SELECT `name`, `time` FROM dojo_ranks ORDER BY `time` ASC LIMIT 50");
                int counter = 1;
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    PreparedStatement pss = con.prepareStatement("INSERT INTO dojo_ranks_month(name, `rank`) VALUES(?, ?)");
                    pss.setString(1, rs.getString("name"));
                    pss.setInt(2, counter);
                    pss.executeUpdate();
                    pss.close();
                    counter++;
                }
                ps.close();
                rs.close();

                PreparedStatement pss = con.prepareStatement("INSERT INTO dojo_ranks_month(name, `rank`) VALUES(?, ?)");
                pss.setString(1, new Date().toString());
                pss.setInt(2, 0);
                pss.executeUpdate();
                pss.close();

                ps = con.prepareStatement("DELETE FROM dojo_ranks");
                ps.executeUpdate();
                ps.close();
            }catch (SQLException se) {
                se.printStackTrace();
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!ResetDR - 重載上月武陵排名並將排名重製)";
        }
    }

    public static class ResetGR extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            updateGuildRK();
            try {
                Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement("UPDATE guilds SET guildtotalpoints = GP + guildtotalpoints");
                ps.executeUpdate();
                ps.close();

                List<MapleGuildRanking.GuildRankingInfo> grs = MapleGuildRanking.getInstance().getRankall();
                for (MapleGuildRanking.GuildRankingInfo gf : grs) {
                    World.Guild.getGuildByName(gf.getName()).gainGP(-gf.getGP(), false);
                }
            }catch (SQLException se) {
                System.err.println("Error saving guild to SQL");
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!ResetGR - 重載上月公會排名並將公會貢獻歸零)";
        }
    }

    public static class gainGP extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            c.getPlayer().getGuild().gainGP(100000, true, c.getPlayer().getId());
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!gainGP - 增加公會貢獻 100000)";
        }
    }


}
