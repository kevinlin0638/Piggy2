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
import handling.channel.ChannelServer;
import handling.world.World;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.ItemInformation;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Timer;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MapleNPC;
import server.life.OverrideMonsterStats;
import server.maps.*;
import tools.StringUtil;
import tools.packet.CField;
import tools.types.Pair;

import java.awt.*;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import server.MapleCarnivalChallenge;
import server.MapleInventoryManipulator;
import tools.packet.CWvsContext;

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
                    chr.dropMessage(6, "----------------------------------(all / ch)-----------------------------------------");
                }
                chr.dropMessage(6, "-------------------------------------------------------------------------------------");
                for (ChannelServer cserv : ChannelServer.getAllInstance(world)) {
                    int curConnected = cserv.getConnectedClients();
                    //curConnected += 服務端配置.上線人數底 / ChannelServer.getAllInstances().size();
                    chr.dropMessage(6, "頻道: " + cserv.getChannel() + " 在線人數: " + curConnected);
                    total += curConnected;
                }
                chr.dropMessage(6, "當前伺服器總計線上人數: " + total);
                chr.dropMessage(6, "-------------------------------------------------------------------------------------");
                return true;
            }
            if (splitted.size() < 4 && splitted.get(1).equalsIgnoreCase("all") && chr.isGM()) {
                int total = 0;
                chr.dropMessage(6, "-------------------------------------------------------------------------------------");
                for (ChannelServer cserv : ChannelServer.getAllInstance(world)) {
                    int curConnected = cserv.getConnectedClients();
                    if (curConnected != 0) {
                        chr.dropMessage(6, "頻道: " + cserv.getChannel() + " 在線人數: " + curConnected);
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

    public static class Spawn extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
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
                c.getPlayer().dropMessage(5, "Please purchase a pet from the cash shop instead.");
            } else if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + "  不存在");
            } else {
                client.inventory.Item item;

                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                    item = ii.randomizeStats((Equip) ii.getEquipById(itemId));

                } else {
                    item = new client.inventory.Item(itemId, (byte) 0, quantity, (byte) 0);
                }
                item.setOwner(c.getPlayer().getName());
                item.setGMLog(c.getPlayer().getName());

                MapleInventoryManipulator.addbyItem(c, item);
            }
            return true;
        }

        @Override
        public String getHelpMessage() {
            return "!item <itemID> <數量> - 製作道具";
        }
    }

    public static class Drop extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> splitted) {
            final int itemId = Integer.parseInt(splitted.get(1));
            final short quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted.toArray(new String[0]), 2, 1);
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (GameConstants.isPet(itemId)) {
                c.getPlayer().dropMessage(5, "Please purchase a pet from the cash shop instead.");
            } else if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " does not exist");
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

}
