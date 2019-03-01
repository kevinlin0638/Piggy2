package server;

import client.MapleCharacter;
import client.MapleJob;
import client.MapleTrait.MapleTraitType;
import client.inventory.*;
import constants.GameConstants;
import constants.ServerConstants;
import database.DatabaseConnection;
import io.netty.util.internal.ThreadLocalRandom;
import provider.*;
import server.StructSetItem.SetItem;
import tools.types.Pair;
import tools.types.Triple;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

public class MapleItemInformationProvider {

    protected static Map<Integer, ItemInformation> dataCache = new HashMap<>();
    protected static Map<Integer, String> itemNameCache = new LinkedHashMap<>();
    private static MapleItemInformationProvider instance = new MapleItemInformationProvider();
    protected MapleDataProvider chrData = MapleDataProviderFactory.getDataProvider("Character.wz");
    protected MapleDataProvider etcData = MapleDataProviderFactory.getDataProvider("Etc.wz");
    protected MapleDataProvider itemData = MapleDataProviderFactory.getDataProvider("Item.wz");
    protected MapleDataProvider stringData = MapleDataProviderFactory.getDataProvider("String.wz");
    protected Map<String, List<Triple<String, Point, Point>>> afterImage = new HashMap<>();
    protected Map<Integer, List<StructItemOption>> potentialCache = new HashMap<>();
    protected Map<Integer, Map<Integer, StructItemOption>> socketCache = new HashMap<>(); // Grade, (id, data)
    protected Map<Integer, MapleStatEffect> itemEffects = new HashMap<>();
    protected Map<Integer, MapleStatEffect> itemEffectsEx = new HashMap<>();
    protected Map<Integer, Integer> mobIds = new HashMap<>();
    protected Map<Integer, Pair<Integer, Integer>> potLife = new HashMap<>(); //itemid to lifeid, levels
    protected Map<Integer, StructFamiliar> familiars = new HashMap<>(); //by familiarID
    protected Map<Integer, StructFamiliar> familiars_Item = new HashMap<>(); //by cardID
    protected Map<Integer, StructFamiliar> familiars_Mob = new HashMap<>(); //by mobID
    protected List<Integer> itemIdCache = new ArrayList<>();
    protected Map<Integer, Pair<List<Integer>, List<Integer>>> androids = new HashMap<>();
    protected Map<Integer, Triple<Integer, List<Integer>, List<Integer>>> monsterBookSets = new HashMap<>();
    protected Map<Integer, StructSetItem> setItems = new HashMap<>();
    private ItemInformation tmpInfo = null;

    public static MapleItemInformationProvider getInstance() {
        return instance;
    }

    public static ItemInformation getItemInformation(int itemId) {
        if (itemId <= 0) {
            return null;
        }
        return dataCache.get(itemId);
    }

    public void runEtc() {
        if (!setItems.isEmpty() || !potentialCache.isEmpty() || !socketCache.isEmpty()) {
            return;
        }
        MapleData setsData = etcData.getData("SetItemInfo.img");
        StructSetItem itemz;
        SetItem itez;
        for (MapleData dat : setsData) {
            itemz = new StructSetItem();
            itemz.setItemID = Integer.parseInt(dat.getName());
            itemz.completeCount = (byte) MapleDataTool.getIntConvert("completeCount", dat, 0);
            for (MapleData level : dat.getChildByPath("ItemID")) {
                if (level.getType() != MapleDataType.INT) {
                    for (MapleData leve : level) {
                        if (!leve.getName().equals("representName") && !leve.getName().equals("typeName")) {
                            try {
                                itemz.itemIDs.add(MapleDataTool.getIntConvert(leve));
                            } catch (Exception e) {
                                System.err.println("出錯數據： leve = " + leve.getData());
                            }
                        }
                    }
                } else {
                    itemz.itemIDs.add(MapleDataTool.getInt(level));
                }
            }
            for (MapleData level : dat.getChildByPath("Effect")) {
                itez = new SetItem();
                itez.incPDD = MapleDataTool.getIntConvert("incPDD", level, 0);
                itez.incMDD = MapleDataTool.getIntConvert("incMDD", level, 0);
                itez.incSTR = MapleDataTool.getIntConvert("incSTR", level, 0);
                itez.incDEX = MapleDataTool.getIntConvert("incDEX", level, 0);
                itez.incINT = MapleDataTool.getIntConvert("incINT", level, 0);
                itez.incLUK = MapleDataTool.getIntConvert("incLUK", level, 0);
                itez.incACC = MapleDataTool.getIntConvert("incACC", level, 0);
                itez.incPAD = MapleDataTool.getIntConvert("incPAD", level, 0);
                itez.incMAD = MapleDataTool.getIntConvert("incMAD", level, 0);
                itez.incSpeed = MapleDataTool.getIntConvert("incSpeed", level, 0);
                itez.incMHP = MapleDataTool.getIntConvert("incMHP", level, 0);
                itez.incMMP = MapleDataTool.getIntConvert("incMMP", level, 0);
                itez.incMHPr = MapleDataTool.getIntConvert("incMHPr", level, 0);
                itez.incMMPr = MapleDataTool.getIntConvert("incMMPr", level, 0);
                itez.incAllStat = MapleDataTool.getIntConvert("incAllStat", level, 0);
                itez.option1 = MapleDataTool.getIntConvert("Option/1/option", level, 0);
                itez.option2 = MapleDataTool.getIntConvert("Option/2/option", level, 0);
                itez.option1Level = MapleDataTool.getIntConvert("Option/1/level", level, 0);
                itez.option2Level = MapleDataTool.getIntConvert("Option/2/level", level, 0);
                itemz.items.put(Integer.parseInt(level.getName()), itez);
            }
            setItems.put(itemz.setItemID, itemz);
        }
        StructItemOption item;
        MapleData potsData = itemData.getData("ItemOption.img");
        List<StructItemOption> items;
        for (MapleData dat : potsData) {
            items = new LinkedList<>();
            for (MapleData potLevel : dat.getChildByPath("level")) {
                item = new StructItemOption();
                item.opID = Integer.parseInt(dat.getName());
                item.optionType = MapleDataTool.getIntConvert("info/optionType", dat, 0);
                item.reqLevel = MapleDataTool.getIntConvert("info/reqLevel", dat, 0);
                item.opString = MapleDataTool.getString("info/string", dat, "");
                for (String i : StructItemOption.types) {
                    if (i.equals("face")) {
                        item.face = MapleDataTool.getString("face", potLevel, "");
                    } else {
                        int level = MapleDataTool.getIntConvert(i, potLevel, 0);
                        if (level > 0) { // Save memory
                            item.data.put(i, level);
                        }
                    }
                }
                switch (item.opID) {
                    case 31001: // Haste
                    case 31002: // Mystic Door
                    case 31003: // Sharp Eyes
                    case 31004: // Hyper Body
                        item.data.put("skillID", (item.opID - 23001));
                        break;
                    case 41005: // Combat Orders
                    case 41006: // Advanced Blessing
                    case 41007: // Speed Infusion
                        item.data.put("skillID", (item.opID - 33001));
                        break;
                }
                items.add(item);
            }
            potentialCache.put(Integer.parseInt(dat.getName()), items);
        }
        Map<Integer, StructItemOption> gradeS = new HashMap<>();
        Map<Integer, StructItemOption> gradeA = new HashMap<>();
        Map<Integer, StructItemOption> gradeB = new HashMap<>();
        Map<Integer, StructItemOption> gradeC = new HashMap<>();
        Map<Integer, StructItemOption> gradeD = new HashMap<>();

        socketCache.put(4, gradeS);
        socketCache.put(3, gradeA);
        socketCache.put(2, gradeB);
        socketCache.put(1, gradeC);
        socketCache.put(0, gradeD);

        MapleDataDirectoryEntry e = (MapleDataDirectoryEntry) etcData.getRoot().getEntry("Android");
        for (MapleDataEntry d : e.getFiles()) {
            MapleData iz = etcData.getData("Android/" + d.getName());
            List<Integer> hair = new ArrayList<>(), face = new ArrayList<>();
            for (MapleData ds : iz.getChildByPath("costume/hair")) {
                hair.add(MapleDataTool.getInt(ds, 30000));
            }
            for (MapleData ds : iz.getChildByPath("costume/face")) {
                face.add(MapleDataTool.getInt(ds, 20000));
            }
            androids.put(Integer.parseInt(d.getName().substring(0, 4)), new Pair<>(hair, face));
        }

        MapleData lifesData = etcData.getData("ItemPotLifeInfo.img");
        for (MapleData d : lifesData) {
            if (d.getChildByPath("info") != null && MapleDataTool.getInt("type", d.getChildByPath("info"), 0) == 1) {
                potLife.put(MapleDataTool.getInt("counsumeItem", d.getChildByPath("info"), 0), new Pair<>(Integer.parseInt(d.getName()), d.getChildByPath("level").getChildren().size()));
            }
        }
        List<Triple<String, Point, Point>> thePointK = new ArrayList<>();
        List<Triple<String, Point, Point>> thePointA = new ArrayList<>();

        MapleDataDirectoryEntry a = (MapleDataDirectoryEntry) chrData.getRoot().getEntry("Afterimage");
        for (MapleDataEntry b : a.getFiles()) {
            MapleData iz = chrData.getData("Afterimage/" + b.getName());
            List<Triple<String, Point, Point>> thePoint = new ArrayList<>();
            Map<String, Pair<Point, Point>> dummy = new HashMap<>();
            for (MapleData i : iz) {
                for (MapleData xD : i) {
                    if (xD.getName().contains("prone") || xD.getName().contains("double") || xD.getName().contains("triple")) {
                        continue;
                    }
                    if ((b.getName().contains("bow") || b.getName().contains("Bow")) && !xD.getName().contains("shoot")) {
                        continue;
                    }
                    if ((b.getName().contains("gun") || b.getName().contains("cannon")) && !xD.getName().contains("shot")) {
                        continue;
                    }
                    if (dummy.containsKey(xD.getName())) {
                        if (xD.getChildByPath("lt") != null) {
                            Point lt = (Point) xD.getChildByPath("lt").getData();
                            Point ourLt = dummy.get(xD.getName()).left;
                            if (lt.x < ourLt.x) {
                                ourLt.x = lt.x;
                            }
                            if (lt.y < ourLt.y) {
                                ourLt.y = lt.y;
                            }
                        }
                        if (xD.getChildByPath("rb") != null) {
                            Point rb = (Point) xD.getChildByPath("rb").getData();
                            Point ourRb = dummy.get(xD.getName()).right;
                            if (rb.x > ourRb.x) {
                                ourRb.x = rb.x;
                            }
                            if (rb.y > ourRb.y) {
                                ourRb.y = rb.y;
                            }
                        }
                    } else {
                        Point lt = null, rb = null;
                        if (xD.getChildByPath("lt") != null) {
                            lt = (Point) xD.getChildByPath("lt").getData();
                        }
                        if (xD.getChildByPath("rb") != null) {
                            rb = (Point) xD.getChildByPath("rb").getData();
                        }
                        dummy.put(xD.getName(), new Pair<>(lt, rb));
                    }
                }
            }
            for (Entry<String, Pair<Point, Point>> ez : dummy.entrySet()) {
                if (ez.getKey().length() > 2 && ez.getKey().substring(ez.getKey().length() - 2, ez.getKey().length() - 1).equals("D")) { //D = double weapon
                    thePointK.add(new Triple<>(ez.getKey(), ez.getValue().left, ez.getValue().right));
                } else if (ez.getKey().contains("PoleArm")) { //D = double weapon
                    thePointA.add(new Triple<>(ez.getKey(), ez.getValue().left, ez.getValue().right));
                } else {
                    thePoint.add(new Triple<>(ez.getKey(), ez.getValue().left, ez.getValue().right));
                }
            }
            afterImage.put(b.getName().substring(0, b.getName().length() - 4), thePoint);
        }
        afterImage.put("katara", thePointK); //hackish
        afterImage.put("aran", thePointA); //hackish
    }

    public void runItems() {

        try {
            Connection con = DatabaseConnection.getConnection();

            // Load Item Data
            PreparedStatement ps = con.prepareStatement("SELECT * FROM wz_itemdata");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                initItemInformation(rs);
            }
            rs.close();
            ps.close();

            // Load Item Equipment Data
            ps = con.prepareStatement("SELECT * FROM wz_itemequipdata ORDER BY itemid");
            rs = ps.executeQuery();
            while (rs.next()) {
                initItemEquipData(rs);
            }
            rs.close();
            ps.close();

            // Load Item Addition Data
            ps = con.prepareStatement("SELECT * FROM wz_itemadddata ORDER BY itemid");
            rs = ps.executeQuery();
            while (rs.next()) {
                initItemAddData(rs);
            }
            rs.close();
            ps.close();

            // Load Item Reward Data
            ps = con.prepareStatement("SELECT * FROM wz_itemrewarddata ORDER BY itemid");
            rs = ps.executeQuery();
            while (rs.next()) {
                initItemRewardData(rs);
            }
            rs.close();
            ps.close();

            // Finalize all Equipments
            dataCache.entrySet().stream().filter(entry -> GameConstants.getInventoryType(entry.getKey()) == MapleInventoryType.EQUIP).forEach(entry -> {
                finalizeEquipData(entry.getValue());
            });
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<StructItemOption> getPotentialInfo(int potId) {
        return potentialCache.get(potId);
    }

    public Map<Integer, List<StructItemOption>> getAllPotentialInfo() {
        return potentialCache;
    }

    public StructItemOption getSocketInfo(int potId) {
        int grade = GameConstants.getNebuliteGrade(potId);
        if (grade == -1) {
            return null;
        }
        return socketCache.get(grade).get(potId);
    }

    public Map<Integer, StructItemOption> getAllSocketInfo(int grade) {
        return socketCache.get(grade);
    }

    public Collection<Integer> getMonsterBookList() {
        return mobIds.values();
    }

    public Map<Integer, Integer> getMonsterBook() {
        return mobIds;
    }

    public Pair<Integer, Integer> getPot(int f) {
        return potLife.get(f);
    }

    public StructFamiliar getFamiliar(int f) {
        return familiars.get(f);
    }

    public Map<Integer, StructFamiliar> getFamiliars() {
        return familiars;
    }

    public StructFamiliar getFamiliarByItem(int f) {
        return familiars_Item.get(f);
    }

    public StructFamiliar getFamiliarByMob(int f) {
        return familiars_Mob.get(f);
    }

    public Collection<ItemInformation> getAllItems() {
        return dataCache.values();
    }

    public Pair<List<Integer>, List<Integer>> getAndroidInfo(int i) {
        return androids.get(i);

    }

    public Triple<Integer, List<Integer>, List<Integer>> getMonsterBookInfo(int i) {
        return monsterBookSets.get(i);
    }

    public Map<Integer, Triple<Integer, List<Integer>, List<Integer>>> getAllMonsterBookInfo() {
        return monsterBookSets;
    }

    protected MapleData getItemData(int itemId) {
        MapleData ret = null;
        String idStr = "0" + String.valueOf(itemId);
        MapleDataDirectoryEntry root = itemData.getRoot();
        for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
            // we should have .img files here beginning with the first 4 IID
            for (MapleDataFileEntry iFile : topDir.getFiles()) {
                if (iFile.getName().equals(idStr.substring(0, 4) + ".img")) {
                    ret = itemData.getData(topDir.getName() + "/" + iFile.getName());
                    if (ret == null) {
                        return null;
                    }
                    ret = ret.getChildByPath(idStr);
                    return ret;
                } else if (iFile.getName().equals(idStr.substring(1) + ".img")) {
                    ret = itemData.getData(topDir.getName() + "/" + iFile.getName());
                    return ret;
                }
            }
        }
        //equips dont have item effects :)
        /*root = equipData.getRoot();
        for (MapleDataDirectoryEntry topDir : root.getSubdirectories()) {
        for (MapleDataFileEntry iFile : topDir.getFiles()) {
        if (iFile.getName().equals(idStr + ".img")) {
        ret = equipData.getData(topDir.getName() + "/" + iFile.getName());
        return ret;
        }
        }
        }*/

        return ret;
    }

    public Integer getItemIdByMob(int mobId) {
        return mobIds.get(mobId);
    }

    public Integer getSetId(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return Integer.valueOf(i.cardSet);
    }

    /**
     * returns the maximum of items in one slot
     */
    public short getSlotMax(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return 0;
        }
        return i.slotMax;
    }

    public int getWholePrice(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return 0;
        }
        return i.wholePrice;
    }

    public final boolean isOnlyTradeBlock(final int itemId) {
        final MapleData data = getItemData(itemId);
        boolean tradeblock = false;
        if (MapleDataTool.getIntConvert("info/tradeBlock", data, 0) == 1) {
            tradeblock = true;
        }
        return tradeblock;
    }

    public double getPrice(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return -1.0;
        }
        return i.price;
    }

    protected int rand(int min, int max) {
        return Math.abs((int) Randomizer.rand(min, max));
    }

    public Equip levelUpEquip(Equip equip, Map<String, Integer> sta) {
        Equip nEquip = (Equip) equip.copy();
        //is this all the stats?
        try {
            for (Entry<String, Integer> stat : sta.entrySet()) {
                switch (stat.getKey()) {
                    case "STRMin":
                        nEquip.setStr((short) (nEquip.getStr() + rand(stat.getValue().intValue(), sta.get("STRMax").intValue())));
                        break;
                    case "DEXMin":
                        nEquip.setDex((short) (nEquip.getDex() + rand(stat.getValue().intValue(), sta.get("DEXMax").intValue())));
                        break;
                    case "INTMin":
                        nEquip.setInt((short) (nEquip.getInt() + rand(stat.getValue().intValue(), sta.get("INTMax").intValue())));
                        break;
                    case "LUKMin":
                        nEquip.setLuk((short) (nEquip.getLuk() + rand(stat.getValue().intValue(), sta.get("LUKMax").intValue())));
                        break;
                    case "PADMin":
                        nEquip.setWatk((short) (nEquip.getWatk() + rand(stat.getValue().intValue(), sta.get("PADMax").intValue())));
                        break;
                    case "PDDMin":
                        nEquip.setWdef((short) (nEquip.getWdef() + rand(stat.getValue().intValue(), sta.get("PDDMax").intValue())));
                        break;
                    case "MADMin":
                        nEquip.setMatk((short) (nEquip.getMatk() + rand(stat.getValue().intValue(), sta.get("MADMax").intValue())));
                        break;
                    case "MDDMin":
                        nEquip.setMdef((short) (nEquip.getMdef() + rand(stat.getValue().intValue(), sta.get("MDDMax").intValue())));
                        break;
                    case "ACCMin":
                        nEquip.setAcc((short) (nEquip.getAcc() + rand(stat.getValue().intValue(), sta.get("ACCMax").intValue())));
                        break;
                    case "EVAMin":
                        nEquip.setAvoid((short) (nEquip.getAvoid() + rand(stat.getValue().intValue(), sta.get("EVAMax").intValue())));
                        break;
                    case "SpeedMin":
                        nEquip.setSpeed((short) (nEquip.getSpeed() + rand(stat.getValue().intValue(), sta.get("SpeedMax").intValue())));
                        break;
                    case "JumpMin":
                        nEquip.setJump((short) (nEquip.getJump() + rand(stat.getValue().intValue(), sta.get("JumpMax").intValue())));
                        break;
                    case "MHPMin":
                        nEquip.setHp((short) (nEquip.getHp() + rand(stat.getValue().intValue(), sta.get("MHPMax").intValue())));
                        break;
                    case "MMPMin":
                        nEquip.setMp((short) (nEquip.getMp() + rand(stat.getValue().intValue(), sta.get("MMPMax").intValue())));
                        break;
                    case "MaxHPMin":
                        nEquip.setHp((short) (nEquip.getHp() + rand(stat.getValue().intValue(), sta.get("MaxHPMax").intValue())));
                        break;
                    case "MaxMPMin":
                        nEquip.setMp((short) (nEquip.getMp() + rand(stat.getValue().intValue(), sta.get("MaxMPMax").intValue())));
                        break;
                }
            }
        } catch (NullPointerException e) {
            //catch npe because obviously the wz have some error XD
        }
        return nEquip;
    }

    public EnumMap<EquipAdditions, Pair<Integer, Integer>> getEquipAdditions(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.equipAdditions;
    }

    public Map<Integer, Map<String, Integer>> getEquipIncrements(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.equipIncs;
    }

    public List<Integer> getEquipSkills(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.incSkill;
    }

    public Map<String, Integer> getEquipStats(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.equipStats;
    }

    public boolean canEquip(Map<String, Integer> stats, int itemid, int level, int job, int fame, int str, int dex, int luk, int int_) {
        if (level >= (stats.containsKey("reqLevel") ? stats.get("reqLevel") : 0) && str >= (stats.containsKey("reqSTR") ? stats.get("reqSTR") : 0) && dex >= (stats.containsKey("reqDEX") ? stats.get("reqDEX") : 0) && luk >= (stats.containsKey("reqLUK") ? stats.get("reqLUK") : 0) && int_ >= (stats.containsKey("reqINT") ? stats.get("reqINT") : 0)) {
            Integer fameReq = stats.get("reqPOP");
            if (fameReq != null && fame < fameReq) {
                return false;
            }
            return true;
        }
        return false;
    }

    public int getReqLevel(int itemId) {
        if (getEquipStats(itemId) == null || !getEquipStats(itemId).containsKey("reqLevel")) {
            return 0;
        }
        return getEquipStats(itemId).get("reqLevel");
    }

    public int getSlots(int itemId) {
        if (getEquipStats(itemId) == null || !getEquipStats(itemId).containsKey("tuc")) {
            return 0;
        }
        return getEquipStats(itemId).get("tuc");
    }

    public Integer getSetItemID(int itemId) {
        if (getEquipStats(itemId) == null || !getEquipStats(itemId).containsKey("setItemID")) {
            return 0;
        }
        return getEquipStats(itemId).get("setItemID");
    }

    public StructSetItem getSetItem(int setItemId) {
        return setItems.get(setItemId);
    }

    public List<Integer> getScrollReqs(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.scrollReqs;
    }

    public Item scrollEquipWithId(Item equip, Item scrollId, boolean ws, MapleCharacter chr, int vegas, boolean checkIfGM) {
        if (equip.getType() == 1) { // See Item.java
            Equip nEquip = (Equip) equip;
            Map<String, Integer> stats = getEquipStats(scrollId.getItemId());
            Map<String, Integer> eqstats = getEquipStats(equip.getItemId());
            int succ = (GameConstants.isTablet(scrollId.getItemId()) ? GameConstants.getSuccessTablet(scrollId.getItemId(), nEquip.getLevel()) : ((GameConstants.isEquipScroll(scrollId.getItemId()) || GameConstants.isPotentialScroll(scrollId.getItemId()) || !stats.containsKey("success") ? 0 : stats.get("success"))));
            int curse = (GameConstants.isTablet(scrollId.getItemId()) ? GameConstants.getCurseTablet(scrollId.getItemId(), nEquip.getLevel()) : ((GameConstants.isEquipScroll(scrollId.getItemId()) || GameConstants.isPotentialScroll(scrollId.getItemId()) || !stats.containsKey("cursed") ? 0 : stats.get("cursed"))));
            int added = (ItemFlag.LUCKS_KEY.check(equip.getFlag()) ? 10 : 0) + (chr.getTrait(MapleTraitType.craft).getLevel() / 10);
            int success = succ + (vegas == 5610000 && succ == 10 ? 20 : (vegas == 5610001 && succ == 60 ? 30 : 0)) + added;
            if (ItemFlag.LUCKS_KEY.check(equip.getFlag()) && !GameConstants.isPotentialScroll(scrollId.getItemId()) && !GameConstants.isEquipScroll(scrollId.getItemId()) && !GameConstants.isSpecialScroll(scrollId.getItemId())) {
                equip.setFlag((short) (equip.getFlag() - ItemFlag.LUCKS_KEY.getValue()));
            }
            if (GameConstants.isPotentialScroll(scrollId.getItemId()) || GameConstants.isEquipScroll(scrollId.getItemId()) || GameConstants.isSpecialScroll(scrollId.getItemId()) || Randomizer.nextInt(100) <= success || (checkIfGM == true)) {
                switch (scrollId.getItemId()) {
                    case 2049000:
                    case 2049001:
                    case 2049002:
                    case 2049003:
                    case 2049004:
                    case 2049005: {
                        if (eqstats.containsKey("tuc") && nEquip.getLevel() + nEquip.getUpgradeSlots() < eqstats.get("tuc")) {
                            nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() + 1));
                        }
                        break;
                    }
                    case 2049006:
                    case 2049007:
                    case 2049008: {
                        if (eqstats.containsKey("tuc") && nEquip.getLevel() + nEquip.getUpgradeSlots() < eqstats.get("tuc")) {
                            nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() + 2));
                        }
                        break;
                    }
                    case 2040727: { // Spikes on shoe, prevents slip
                        short flag = nEquip.getFlag();
                        flag |= ItemFlag.SPIKES.getValue();
                        nEquip.setFlag(flag);
                        break;
                    }
                    case 2041058: { // Cape for Cold protection
                        short flag = nEquip.getFlag();
                        flag |= ItemFlag.COLD.getValue();
                        nEquip.setFlag(flag);
                        break;
                    }
                    case 2530000:
                    case 2530001:
                    case 2530002: {
                        short flag = nEquip.getFlag();
                        flag |= ItemFlag.LUCKS_KEY.getValue();
                        nEquip.setFlag(flag);
                        break;
                    }
                    case 2531000: {
                        short flag = nEquip.getFlag();
                        flag |= ItemFlag.SHIELD_WARD.getValue();
                        nEquip.setFlag(flag);
                        break;
                    }
                    case 5063000: {
                        short flag = nEquip.getFlag();
                        flag |= ItemFlag.LUCKS_KEY.getValue();
                        nEquip.setFlag(flag);
                        break;
                    }
                    case 5064000:
                    case 5064002: {
                        short flag = nEquip.getFlag();
                        flag |= ItemFlag.SHIELD_WARD.getValue();
                        nEquip.setFlag(flag);
                        break;
                    }
                    case 5064100: {
                        short flag = nEquip.getFlag();
                        flag |= ItemFlag.SLOTS_PROTECT.getValue();
                        nEquip.setFlag(flag);
                        break;
                    }
                    case 5064300:
                    case 5064301: {
                        short flag = nEquip.getFlag();
                        flag |= ItemFlag.SCROLL_PROTECT.getValue();
                        nEquip.setFlag(flag);
                        break;
                    }
                    case 2049600:
                    case 2049601:
                    case 2049604:
                        Item item;
                        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                        item = ii.getStats((Equip) ii.getEquipById(nEquip.getItemId()), nEquip.getPotential1(), nEquip.getPotential2(), nEquip.getPotential3(), nEquip.getPotential4(), nEquip.getPotential5(), nEquip.getExtraScroll(), nEquip.getAddi_str(), nEquip.getAddi_dex() , nEquip.getAddi_int(), nEquip.getAddi_luk(), nEquip.getAddi_watk(), nEquip.getAddi_matk(), nEquip.getBreak_dmg());
                        if (chr.getInventory(GameConstants.getInventoryType(item.getItemId())).getNextFreeSlot() > 0 && !isMSI(nEquip, (short) 32760)) {
                            MapleInventoryManipulator.addbyItem(chr.getClient(), item);
                        } else {
                            break;
                        }
                        break;
                    default: {
                        if (GameConstants.isChaosScroll(scrollId.getItemId())) {
                            final int z = GameConstants.getChaosNumber(scrollId.getItemId());
                            final int nagtive = scrollId.getItemId() > 2049120? 1: (Randomizer.nextBoolean() ? 1 : -1);

                            if (nEquip.getStr() > 0) {
                                nEquip.setStr((short) (nEquip.getStr() + Randomizer.nextInt(z) * nagtive));
                            }
                            if (nEquip.getDex() > 0) {
                                nEquip.setDex((short) (nEquip.getDex() + Randomizer.nextInt(z) * nagtive));
                            }
                            if (nEquip.getInt() > 0) {
                                nEquip.setInt((short) (nEquip.getInt() + Randomizer.nextInt(z) * nagtive));
                            }
                            if (nEquip.getLuk() > 0) {
                                nEquip.setLuk((short) (nEquip.getLuk() + Randomizer.nextInt(z) * nagtive));
                            }
                            if (nEquip.getWatk() > 0) {
                                nEquip.setWatk((short) (nEquip.getWatk() + Randomizer.nextInt(z) * nagtive));
                            }
                            if (nEquip.getWdef() > 0) {
                                nEquip.setWdef((short) (nEquip.getWdef() + Randomizer.nextInt(z) * nagtive));
                            }
                            if (nEquip.getMatk() > 0) {
                                nEquip.setMatk((short) (nEquip.getMatk() + Randomizer.nextInt(z) * nagtive));
                            }
                            if (nEquip.getMdef() > 0) {
                                nEquip.setMdef((short) (nEquip.getMdef() + Randomizer.nextInt(z) * nagtive));
                            }
                            if (nEquip.getAcc() > 0) {
                                nEquip.setAcc((short) (nEquip.getAcc() + Randomizer.nextInt(z) * nagtive));
                            }
                            if (nEquip.getAvoid() > 0) {
                                nEquip.setAvoid((short) (nEquip.getAvoid() + Randomizer.nextInt(z) * nagtive));
                            }
                            if (nEquip.getSpeed() > 0) {
                                nEquip.setSpeed((short) (nEquip.getSpeed() + Randomizer.nextInt(z) * nagtive));
                            }
                            if (nEquip.getJump() > 0) {
                                nEquip.setJump((short) (nEquip.getJump() + Randomizer.nextInt(z) * nagtive));
                            }
                            if (nEquip.getHp() > 0) {
                                nEquip.setHp((short) (nEquip.getHp() + Randomizer.nextInt(z) * nagtive));
                            }
                            if (nEquip.getMp() > 0) {
                                nEquip.setMp((short) (nEquip.getMp() + Randomizer.nextInt(z) * nagtive));
                            }
                            break;
                        } else if (GameConstants.is8HappyScroll(scrollId.getItemId())) {
                            int wtk = 0;
                            int mtk = 0;
                            switch (scrollId.getItemId()){
                                case 2046025:
                                case 2046119:
                                    wtk =  ThreadLocalRandom.current().nextInt(7, 8 + 1);
                                    break;
                                case 2046026:
                                case 2046120:
                                    mtk =  ThreadLocalRandom.current().nextInt(7, 8 + 1);
                                    break;
                            }
                            if (wtk > 0) {
                                nEquip.setWatk((short) (nEquip.getWatk() + Randomizer.nextInt(wtk)));
                            }


                            if (mtk > 0) {
                                nEquip.setMatk((short) (nEquip.getMatk() + Randomizer.nextInt(mtk)));
                            }
                            break;
                        }else if (GameConstants.isEquipScroll(scrollId.getItemId())) {
                            boolean isSucc = true;
                            Pair<Integer, Integer> chanc = (scrollId.getItemId() == 2049300 ? getEnhanceSucceRate(true, nEquip.getEnhance()) : getEnhanceSucceRate(false, nEquip.getEnhance()));
//                            if(chr.getGMLevel() > 4)
//                                chanc = new Pair<Integer, Integer>(0, 100);
                            if(!ServerConstants.isEnhanceEnable){
                                chr.dropMessage(1, "裝備強化暫時關閉");
                                break;
                            }
                            if(!canlevelEnhance(nEquip)){
                                chr.dropMessage(1, "裝備已達最高星等");
                                break;
                            }
                            if(getEnhanceFee(true, nEquip.getEnhance()) > chr.getCSPoints(2) || getEnhanceFee(false, nEquip.getEnhance()) > chr.getMeso()){
                                chr.dropMessage(1, "楓幣或楓點不足，詳細費用請見小喵谷衝星說明");
                                break;
                            }else{
                                chr.modifyCSPoints(2, -1 * getEnhanceFee(true, nEquip.getEnhance()), true);
                                chr.gainMeso(-1 * getEnhanceFee(false, nEquip.getEnhance()), false, true);
                            }
                            if (Randomizer.nextInt(1000000) > chanc.getLeft() * 10000) {
                                if(Randomizer.nextInt(100) < chanc.getRight())
                                    return null; //destroyed, nib
                                else
                                    isSucc = false;
                            }
                            if(!isSucc){
                                if(nEquip.getEnhance() > 0 && nEquip.getEnhance() != 5 && nEquip.getEnhance() != 10 && nEquip.getEnhance() != 15 && nEquip.getEnhance() != 20 && nEquip.getEnhance() != 25) {
                                    nEquip.setEnhance((byte) (nEquip.getEnhance() - 1));
                                    setEnhanceStats(nEquip, true);
                                    chr.dropMessage("失敗裝備扣星 目前星級 : " + nEquip.getEnhance());
                                }else{
                                    chr.dropMessage("失敗星級保護 目前星級 : " + nEquip.getEnhance());
                                }
                            }else{
                                setEnhanceStats(nEquip, false);
                                nEquip.setEnhance((byte) (nEquip.getEnhance() + 1));
                                chr.dropMessage("成功裝備加星 目前星級 : " + nEquip.getEnhance());
                                if(nEquip.getEnhance() == 5)
                                    chr.finishAchievement(40);
                                if(nEquip.getEnhance() == 10)
                                    chr.finishAchievement(41);
                                if(nEquip.getEnhance() == 15)
                                    chr.finishAchievement(42);
                                if(nEquip.getEnhance() == 20)
                                    chr.finishAchievement(43);
                                if(nEquip.getEnhance() == 25)
                                    chr.finishAchievement(44);
                                if(nEquip.getEnhance() == 30)
                                    chr.finishAchievement(45);
                            }
                            break;
                        } else if (GameConstants.isPotentialScroll(scrollId.getItemId())) {
                            if (nEquip.getState() <= 17 && (scrollId.getItemId() / 100 == 20497)) {
                                int chanc = (scrollId.getItemId() == 2049700 ? 100 : 80) + added; // 2049701
                                if (Randomizer.nextInt(100) > chanc) {
                                    return null; //destroyed, nib
                                }
                                nEquip.renewPotential(2);
                            } else if (nEquip.getState() == 0) {
                                int chanc = (scrollId.getItemId() == 5534000 || scrollId.getItemId() == 2049402 || scrollId.getItemId() == 2049406 ? 100 : (scrollId.getItemId() == 2049400 ? 90 : 70)) + added;
                                if (Randomizer.nextInt(100) > chanc) {
                                    return null; //destroyed, nib
                                }
                                nEquip.resetPotential();
                            }
                            break;
                        } else {
                            for (Entry<String, Integer> stat : stats.entrySet()) {
                                String key = stat.getKey();
                                switch (key) {
                                    case "STR":
                                        nEquip.setStr((short) (nEquip.getStr() + stat.getValue().intValue()));
                                        break;
                                    case "DEX":
                                        nEquip.setDex((short) (nEquip.getDex() + stat.getValue().intValue()));
                                        break;
                                    case "INT":
                                        nEquip.setInt((short) (nEquip.getInt() + stat.getValue().intValue()));
                                        break;
                                    case "LUK":
                                        nEquip.setLuk((short) (nEquip.getLuk() + stat.getValue().intValue()));
                                        break;
                                    case "PAD":
                                        nEquip.setWatk((short) (nEquip.getWatk() + stat.getValue().intValue()));
                                        break;
                                    case "PDD":
                                        nEquip.setWdef((short) (nEquip.getWdef() + stat.getValue().intValue()));
                                        break;
                                    case "MAD":
                                        nEquip.setMatk((short) (nEquip.getMatk() + stat.getValue().intValue()));
                                        break;
                                    case "MDD":
                                        nEquip.setMdef((short) (nEquip.getMdef() + stat.getValue().intValue()));
                                        break;
                                    case "ACC":
                                        nEquip.setAcc((short) (nEquip.getAcc() + stat.getValue().intValue()));
                                        break;
                                    case "EVA":
                                        nEquip.setAvoid((short) (nEquip.getAvoid() + stat.getValue().intValue()));
                                        break;
                                    case "Speed":
                                        nEquip.setSpeed((short) (nEquip.getSpeed() + stat.getValue().intValue()));
                                        break;
                                    case "Jump":
                                        nEquip.setJump((short) (nEquip.getJump() + stat.getValue().intValue()));
                                        break;
                                    case "MHP":
                                        nEquip.setHp((short) (nEquip.getHp() + stat.getValue().intValue()));
                                        break;
                                    case "MMP":
                                        nEquip.setMp((short) (nEquip.getMp() + stat.getValue().intValue()));
                                        break;
                                }
                            }
                            break;
                        }
                    }
                }
                if (GameConstants.isInnocence(scrollId.getItemId())) {
                    nEquip.setAcc((short) 6969);
                }
                if (!GameConstants.isCleanSlate(scrollId.getItemId()) && !GameConstants.isInnocence(scrollId.getItemId()) && !GameConstants.isSpecialScroll(scrollId.getItemId()) && !GameConstants.isEquipScroll(scrollId.getItemId()) && !GameConstants.isPotentialScroll(scrollId.getItemId())) {
                    nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() - 1));
                    nEquip.setLevel((byte) (nEquip.getLevel() + 1));
                }
            } else {
                if (!checkIfGM && !ws && !GameConstants.isInnocence(scrollId.getItemId()) && !GameConstants.isCleanSlate(scrollId.getItemId()) && !GameConstants.isSpecialScroll(scrollId.getItemId()) && !GameConstants.isEquipScroll(scrollId.getItemId()) && !GameConstants.isPotentialScroll(scrollId.getItemId())) {
                    nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() - 1));
                }
                if (Randomizer.nextInt(99) < curse) {
                    return null;
                }
            }
        }
        return equip;
    }

    public void setEnhanceStats(final Equip equip, final boolean isCurse){
        boolean isW = false;
        if(GameConstants.isWeapon(equip.getItemId())){
            isW = true;
        }
        int curse = isCurse?-1 : 1;

        switch (equip.getEnhance()){
            case 0:
                if (isW) {
                    if(!isCurse)
                        setStats(equip, (byte) 1, 5, curse);
                } else {
                    if(!isCurse) {
                        equip.setStr((short) (equip.getStr() + 7 * curse));
                        equip.setDex((short) (equip.getDex() + 7 * curse));
                        equip.setInt((short) (equip.getInt() + 7 * curse));
                        equip.setLuk((short) (equip.getLuk() + 7 * curse));
                    }
                }
                break;
            case 1:
                if (isW) {
                    setStats(equip, (byte) 4, 5, curse);
                } else {
                    setStats(equip, (byte) 4, 7, curse);
                }
                break;
            case 2:
                if (isW) {
                    setStats(equip, (byte) 3, 5, curse);
                } else {
                    setStats(equip, (byte) 3, 8, curse);
                }
                break;
            case 3:
                if (isW) {
                    setStats(equip, (byte) 2, 25, curse);
                } else {
                    setStats(equip, (byte) 2, 40, curse);
                }
                break;
            case 4:
                if (isW) {
                    setStats(equip, (byte) 4, 5, curse);
                } else {
                    setStats(equip, (byte) 4, 7, curse);
                }
                break;
            case 5:
                if (isW) {
                    setStats(equip, (byte) 3, 5, curse);
                } else {
                    setStats(equip, (byte) 3, 8, curse);
                }
                break;
            case 6:
                if (isW) {
                    setStats(equip, (byte) 2, 25, curse);
                } else {
                    setStats(equip, (byte) 2, 40, curse);
                }
                break;
            case 7:
                if (isW) {
                    setStats(equip, (byte) 1, 5, curse);
                } else {
                    setStats(equip, (byte) 0, 5, curse);
                }
                break;
            case 8:
                if (isW) {
                    setStats(equip, (byte) 1, 6, curse);
                } else {
                    setStats(equip, (byte) 0, 6, curse);
                }
                break;
            case 9:
                if (isW) {
                    setStats(equip, (byte) 1, 7, curse);
                } else {
                    setStats(equip, (byte) 0, 10, curse);
                }
                break;
            case 10:
                if (isW) {
                    setStats(equip, (byte) 1, 8, curse);
                } else {
                    setStats(equip, (byte) 0, 13, curse);
                }
                break;
            case 11:
                if (isW) {
                    setStats(equip, (byte) 1, 9, curse);
                } else {
                    setStats(equip, (byte) 0, 15, curse);
                }
                break;
            case 12:
                if (isW) {
                    setStats(equip, (byte) 1, 10, curse);
                    setStats(equip, (byte) 0, 10, curse);
                } else {
                    setStats(equip, (byte) 0, 18, curse);
                }
                break;
            case 13:
                if (isW) {
                    setStats(equip, (byte) 1, 15, curse);
                    setStats(equip, (byte) 0, 10, curse);
                } else {
                    setStats(equip, (byte) 0, 20, curse);
                }
                break;
            case 14:
                if (isW) {
                    setStats(equip, (byte) 1, 20, curse);
                    setStats(equip, (byte) 0, 11, curse);
                } else {
                    setStats(equip, (byte) 0, 25, curse);
                }
                break;
            case 15:
                if (isW) {
                    setStats(equip, (byte) 1, 21, curse);
                    setStats(equip, (byte) 0, 12, curse);
                } else {
                    setStats(equip, (byte) 0, 30, curse);
                }
                break;
            case 16:
                if (isW) {
                    setStats(equip, (byte) 1, 22, curse);
                    setStats(equip, (byte) 0, 13, curse);
                } else {
                    setStats(equip, (byte) 1, 3, curse);
                    setStats(equip, (byte) 0, 30, curse);
                }
                break;
            case 17:
                if (isW) {
                    setStats(equip, (byte) 1, 23, curse);
                    setStats(equip, (byte) 0, 14, curse);
                } else {
                    setStats(equip, (byte) 1, 3, curse);
                    setStats(equip, (byte) 0, 32, curse);
                }
                break;
            case 18:
                if (isW) {
                    setStats(equip, (byte) 1, 24, curse);
                    setStats(equip, (byte) 0, 15, curse);
                } else {
                    setStats(equip, (byte) 1, 5, curse);
                    setStats(equip, (byte) 0, 33, curse);
                }
                break;
            case 19:
                if (isW) {
                    setStats(equip, (byte) 1, 25, curse);
                    setStats(equip, (byte) 0, 20, curse);
                } else {
                    setStats(equip, (byte) 1, 5, curse);
                    setStats(equip, (byte) 0, 34, curse);
                }
                break;
            case 20:
                if (isW) {
                    setStats(equip, (byte) 1, 30, curse);
                    setStats(equip, (byte) 0, 21, curse);
                } else {
                    setStats(equip, (byte) 1, 10, curse);
                    setStats(equip, (byte) 0, 40, curse);
                }
                break;
            case 21:
                if (isW) {
                    setStats(equip, (byte) 1, 31, curse);
                    setStats(equip, (byte) 0, 22, curse);
                } else {
                    setStats(equip, (byte) 1, 11, curse);
                    setStats(equip, (byte) 0, 41, curse);
                }
                break;
            case 22:
                if (isW) {
                    setStats(equip, (byte) 1, 32, curse);
                    setStats(equip, (byte) 0, 23, curse);
                } else {
                    setStats(equip, (byte) 1, 12, curse);
                    setStats(equip, (byte) 0, 42, curse);
                }
                break;
            case 23:
                if (isW) {
                    setStats(equip, (byte) 1, 33, curse);
                    setStats(equip, (byte) 0, 24, curse);
                } else {
                    setStats(equip, (byte) 1, 13, curse);
                    setStats(equip, (byte) 0, 43, curse);
                }
                break;
            case 24:
                if (isW) {
                    setStats(equip, (byte) 1, 34, curse);
                    setStats(equip, (byte) 0, 25, curse);
                } else {
                    setStats(equip, (byte) 1, 14, curse);
                    setStats(equip, (byte) 0, 44, curse);
                }
                break;
            case 25:
                if (isW) {
                    setStats(equip, (byte) 1, 50, curse);
                    setStats(equip, (byte) 0, 30, curse);
                } else {
                    setStats(equip, (byte) 1, 20, curse);
                    setStats(equip, (byte) 0, 50, curse);
                }
                break;
            case 26:
                if (isW) {
                    setStats(equip, (byte) 1, 60, curse);
                    setStats(equip, (byte) 0, 40, curse);
                } else {
                    setStats(equip, (byte) 1, 25, curse);
                    setStats(equip, (byte) 0, 60, curse);
                }
                break;
            case 27:
                if (isW) {
                    setStats(equip, (byte) 1, 70, curse);
                    setStats(equip, (byte) 0, 50, curse);
                } else {
                    setStats(equip, (byte) 1, 30, curse);
                    setStats(equip, (byte) 0, 70, curse);
                }
                break;
            case 28:
                if (isW) {
                    setStats(equip, (byte) 1, 80, curse);
                    setStats(equip, (byte) 0, 60, curse);
                } else {
                    setStats(equip, (byte) 1, 35, curse);
                    setStats(equip, (byte) 0, 80, curse);
                }
                break;
            case 29:
                if (isW) {
                    setStats(equip, (byte) 1, 90, curse);
                    setStats(equip, (byte) 0, 70, curse);
                } else {
                    setStats(equip, (byte) 1, 40, curse);
                    setStats(equip, (byte) 0, 90, curse);
                }
                break;
        }

    }

    public void setStats(final Equip nEquip,final byte flag, final int q, final int curse){ // 0: stats 1: attack 2: def 3: speed jmp 4:acc avo
        switch (flag){
            case 0:
                nEquip.setStr((short) (nEquip.getStr() + q * curse));
                nEquip.setDex((short) (nEquip.getDex() + q * curse));
                nEquip.setInt((short) (nEquip.getInt() + q * curse));
                nEquip.setLuk((short) (nEquip.getLuk() + q * curse));
                break;
            case 1:
                nEquip.setWatk((short) (nEquip.getWatk() + q * curse));
                nEquip.setMatk((short) (nEquip.getMatk() + q * curse));
                break;
            case 2:
                nEquip.setWdef((short) (nEquip.getWdef() + q * curse));
                nEquip.setMdef((short) (nEquip.getMdef() + q * curse));
                break;
            case 3:
                nEquip.setJump((short) (nEquip.getJump() + q * curse));
                nEquip.setSpeed((short) (nEquip.getSpeed() + q * curse));
            case 4:
                nEquip.setAcc((short) (nEquip.getAcc() + q * curse));
                nEquip.setAvoid((short) (nEquip.getAvoid() + q * curse));
                break;
        }
    }

    public boolean canlevelEnhance(final Equip eq){
        final int reqLevel = getReqLevel(eq.getItemId());
        if(eq.getItemId() == 1112915)
            return eq.getEnhance() < 30;

        if(reqLevel < 30)
            return eq.getEnhance() < 5;
        else if(reqLevel < 70)
            return eq.getEnhance() < 10;
        else if(reqLevel < 130)
            return eq.getEnhance() < 20;
        else
            return eq.getEnhance() < 30;
    }

    public Pair<Integer, Integer> getEnhanceSucceRate(final boolean isHigh, final byte level){
        boolean isW = false;
        if(isHigh){
            isW = true;
        }
        switch (level){
            case 0:
                if(isW)
                    return new Pair<>(100, 0);
                else
                    return new Pair<>(95, 0);
            case 1:
                if(isW)
                    return new Pair<>(100, 0);
                else
                    return new Pair<>(90, 0);
            case 2:
                if(isW)
                    return new Pair<>(100, 0);
                else
                    return new Pair<>(85, 0);
            case 3:
                if(isW)
                    return new Pair<>(100, 0);
                else
                    return new Pair<>(80, 0);
            case 4:
                if(isW)
                    return new Pair<>(95, 0);
                else
                    return new Pair<>(75, 0);
            case 5:
                if(isW)
                    return new Pair<>(90, 0);
                else
                    return new Pair<>(70, 0);
            case 6:
                if(isW)
                    return new Pair<>(85, 0);
                else
                    return new Pair<>(65, 0);
            case 7:
                if(isW)
                    return new Pair<>(80, 1);
                else
                    return new Pair<>(60, 1);
            case 8:
                if(isW)
                    return new Pair<>(75, 3);
                else
                    return new Pair<>(55, 3);
            case 9:
                if(isW)
                    return new Pair<>(70, 5);
                else
                    return new Pair<>(50, 5);
            case 10:
                if(isW)
                    return new Pair<>(65, 10);
                else
                    return new Pair<>(45, 10);
            case 11:
                if(isW)
                    return new Pair<>(60, 15);
                else
                    return new Pair<>(40, 15);
            case 12:
                if(isW)
                    return new Pair<>(55, 16);
                else
                    return new Pair<>(35, 16);
            case 13:
                if(isW)
                    return new Pair<>(50, 17);
                else
                    return new Pair<>(30, 17);
            case 14:
                if(isW)
                    return new Pair<>(45, 18);
                else
                    return new Pair<>(27, 18);
            case 15:
                if(isW)
                    return new Pair<>(36, 19);
                else
                    return new Pair<>(26, 19);
            case 16:
                if(isW)
                    return new Pair<>(35, 20);
                else
                    return new Pair<>(25, 20);
            case 17:
                if(isW)
                    return new Pair<>(34, 21);
                else
                    return new Pair<>(24, 21);
            case 18:
                if(isW)
                    return new Pair<>(33, 22);
                else
                    return new Pair<>(23, 22);
            case 19:
                if(isW)
                    return new Pair<>(32, 23);
                else
                    return new Pair<>(22, 23);
            case 20:
                if(isW)
                    return new Pair<>(31, 24);
                else
                    return new Pair<>(21, 24);
            case 21:
                if(isW)
                    return new Pair<>(30, 25);
                else
                    return new Pair<>(20, 25);
            case 22:
                if(isW)
                    return new Pair<>(30, 30);
                else
                    return new Pair<>(15, 30);
            case 23:
                if(isW)
                    return new Pair<>(26, 35);
                else
                    return new Pair<>(13, 35);
            case 24:
                if(isW)
                    return new Pair<>(20, 40);
                else
                    return new Pair<>(10, 40);
            case 25:
                if(isW)
                    return new Pair<>(18, 42);
                else
                    return new Pair<>(9, 42);
            case 26:
                if(isW)
                    return new Pair<>(16, 45);
                else
                    return new Pair<>(8, 45);
            case 27:
                if(isW)
                    return new Pair<>(14, 50);
                else
                    return new Pair<>(7, 50);
            case 28:
                if(isW)
                    return new Pair<>(12, 55);
                else
                    return new Pair<>(6, 55);
            case 29:
                if(isW)
                    return new Pair<>(10, 60);
                else
                    return new Pair<>(5, 60);
            default:
                return new Pair<>(0, 0);
        }

    }
    public int getEnhanceFee(final boolean isMP, final byte level){
        boolean isW = false; // true : 楓點 false : 楓幣
        if(isMP){
            isW = true;
        }
        switch (level){
            case 0:
                if(isW)
                    return 50000;
                else
                    return 500000;
            case 1:
                if(isW)
                    return 100000;
                else
                    return 700000;
            case 2:
                if(isW)
                    return 120000;
                else
                    return 800000;
            case 3:
                if(isW)
                    return 150000;
                else
                    return 1030000;
            case 4:
                if(isW)
                    return 180000;
                else
                    return 1200000;
            case 5:
                if(isW)
                    return 200000;
                else
                    return 1500000;
            case 6:
                if(isW)
                    return 250000;
                else
                    return 1700000;
            case 7:
                if(isW)
                    return 300000;
                else
                    return 2000000;
            case 8:
                if(isW)
                    return 350000;
                else
                    return 2400000;
            case 9:
                if(isW)
                    return 400000;
                else
                    return 3200000;
            case 10:
                if(isW)
                    return 450000;
                else
                    return 4800000;
            case 11:
                if(isW)
                    return 500000;
                else
                    return 5200000;
            case 12:
                if(isW)
                    return 550000;
                else
                    return 8770000;
            case 13:
                if(isW)
                    return 600000;
                else
                    return 11800000;
            case 14:
                if(isW)
                    return 650000;
                else
                    return 13200000;
            case 15:
                if(isW)
                    return 700000;
                else
                    return 15400000;
            case 16:
                if(isW)
                    return 750000;
                else
                    return 20800000;
            case 17:
                if(isW)
                    return 800000;
                else
                    return 26900000;
            case 18:
                if(isW)
                    return 900000;
                else
                    return 31500000;
            case 19:
                if(isW)
                    return 1100000;
                else
                    return 38870000;
            case 20:
                if(isW)
                    return 1400000;
                else
                    return 42530000;
            case 21:
                if(isW)
                    return 1800000;
                else
                    return 46310000;
            case 22:
                if(isW)
                    return 2300000;
                else
                    return 51350000;
            case 23:
                if(isW)
                    return 2900000;
                else
                    return 55500000;
            case 24:
                if(isW)
                    return 3600000;
                else
                    return 59500000;
            case 25:
                if(isW)
                    return 4400000;
                else
                    return 63150000;
            case 26:
                if(isW)
                    return 5300000;
                else
                    return 69500000;
            case 27:
                if(isW)
                    return 6300000;
                else
                    return 76150000;
            case 28:
                if(isW)
                    return 7400000;
                else
                    return 81500000;
            case 29:
                if(isW)
                    return 8700000;
                else
                    return 87870000;
            default:
                return 0;
        }
    }

    public Item getEquipById(int equipId) {
        return getEquipById(equipId, -1);
    }

    public Item getEquipById(int equipId, int ringId) {
        ItemInformation i = getItemInformation(equipId);
        if (i == null) {
            return new Equip(equipId, (short) 0, ringId, (byte) 0);
        }
        Item eq = i.eq.copy();
        eq.setUniqueId(ringId);
        return eq;
    }

    private short getRandStatFusion(short defaultValue, int value1, int value2) {
        if (defaultValue == 0) {
            return 0;
        }
        int range = ((value1 + value2) / 2) - defaultValue;
        int rand = Randomizer.nextInt(Math.abs(range) + 1);
        return (short) (defaultValue + (range < 0 ? -rand : rand));
    }

    private short getRandStat(short defaultValue, int maxRange) {
        if (defaultValue == 0) {
            return 0;
        }
        // vary no more than ceil of 10% of stat
        int lMaxRange = (int) Math.min(Math.ceil(defaultValue * 0.1), maxRange);

        return (short) ((defaultValue - lMaxRange) + Randomizer.nextInt(lMaxRange * 2 + 1));
    }

    private short getRandStatAbove(short defaultValue, int maxRange) {
        if (defaultValue <= 0) {
            return 0;
        }
        int lMaxRange = (int) Math.min(Math.ceil(defaultValue * 0.1), maxRange);

        return (short) ((defaultValue) + Randomizer.nextInt(lMaxRange + 1));
    }

    public Equip getStats(Equip equip, int pot1, int pot2, int pot3, int pot4, int pot5, int addscro, short addstr, short adddex, short addint, short addluk, short addwatk, short addmatk, int bkg) {
        setaddition(equip, addstr, adddex, addint, addluk, addwatk, addmatk);
        equip.setAcc(equip.getAcc());
        equip.setAvoid(equip.getAvoid());
        equip.setJump(equip.getJump());
        equip.setHands(equip.getHands());
        equip.setSpeed(equip.getSpeed());
        equip.setWdef(equip.getWdef());
        equip.setMdef(equip.getMdef());
        equip.setHp(equip.getHp());
        equip.setMp(equip.getMp());
        equip.setPotential1(pot1);
        equip.setPotential2(pot2);
        equip.setPotential3(pot3);
        equip.setPotential4(pot4);
        equip.setPotential5(pot5);
        equip.setUpgradeSlots((byte)(equip.getUpgradeSlots() + addscro));
        equip.setExtraScroll(addscro);
        equip.setAddi_str(addstr);
        equip.setAddi_dex(adddex);
        equip.setAddi_int(addint);
        equip.setAddi_luk(addluk);
        equip.setAddi_watk(addwatk);
        equip.setAddi_matk(addmatk);
        equip.setBreak_dmg(bkg);

        return equip;
    }

    private void setaddition(Equip equip, short addstr, short adddex, short addint, short addluk, short addwatk, short addmatk) {
        equip.setStr((short) (equip.getStr() + addstr));
        equip.setDex((short) (equip.getDex() + adddex));
        equip.setInt((short) (equip.getInt() + addint));
        equip.setLuk((short) (equip.getLuk() + addluk));
        equip.setMatk((short) (equip.getMatk() + addwatk));
        equip.setWatk((short) (equip.getWatk() + addmatk));
    }

    public Equip randomizeStats(Equip equip) {
        equip.setStr(getRandStat(equip.getStr(), 5));
        equip.setDex(getRandStat(equip.getDex(), 5));
        equip.setInt(getRandStat(equip.getInt(), 5));
        equip.setLuk(getRandStat(equip.getLuk(), 5));
        equip.setMatk(getRandStat(equip.getMatk(), 5));
        equip.setWatk(getRandStat(equip.getWatk(), 5));
        equip.setAcc(getRandStat(equip.getAcc(), 5));
        equip.setAvoid(getRandStat(equip.getAvoid(), 5));
        equip.setJump(getRandStat(equip.getJump(), 5));
        equip.setHands(getRandStat(equip.getHands(), 5));
        equip.setSpeed(getRandStat(equip.getSpeed(), 5));
        equip.setWdef(getRandStat(equip.getWdef(), 10));
        equip.setMdef(getRandStat(equip.getMdef(), 10));
        equip.setHp(getRandStat(equip.getHp(), 10));
        equip.setMp(getRandStat(equip.getMp(), 10));

        return equip;
    }

    public Equip randomizeStats3(Equip equip) {
        equip.setStr(getRandStat(equip.getStr(), 5));
        equip.setDex(getRandStat(equip.getDex(), 5));
        equip.setInt(getRandStat(equip.getInt(), 5));
        equip.setLuk(getRandStat(equip.getLuk(), 5));
        equip.setMatk(getRandStat(equip.getMatk(), 5));
        equip.setWatk(getRandStat(equip.getWatk(), 5));
        equip.setAcc(getRandStat(equip.getAcc(), 5));
        equip.setAvoid(getRandStat(equip.getAvoid(), 5));
        equip.setJump(getRandStat(equip.getJump(), 5));
        equip.setHands(getRandStat(equip.getHands(), 5));
        equip.setSpeed(getRandStat(equip.getSpeed(), 5));
        equip.setWdef(getRandStat(equip.getWdef(), 10));
        equip.setMdef(getRandStat(equip.getMdef(), 10));
        equip.setHp(getRandStat(equip.getHp(), 10));
        equip.setMp(getRandStat(equip.getMp(), 10));
        equip.setFlag((byte) 1);
        return equip;
    }

    public Equip voteitem(Equip equip) {
        short stat = 100;
        setaddition(equip, stat, stat, stat, stat, stat, stat);
        equip.setAcc((short) (equip.getAcc() + stat));
        equip.setAvoid((short) (equip.getAvoid() + stat));
        equip.setJump((short) (equip.getJump() + stat));
        equip.setHands((short) (equip.getHands() + stat));
        equip.setSpeed((short) (equip.getSpeed() + stat));
        equip.setWdef((short) (equip.getWdef() + stat));
        equip.setMdef((short) (equip.getMdef() + stat));
        equip.setHp((short) (equip.getHp() + stat));
        equip.setMp((short) (equip.getMp() + stat));
        return equip;
    }

    public Equip randomizeStats_Above(Equip equip) {
        equip.setStr(getRandStatAbove(equip.getStr(), 5));
        equip.setDex(getRandStatAbove(equip.getDex(), 5));
        equip.setInt(getRandStatAbove(equip.getInt(), 5));
        equip.setLuk(getRandStatAbove(equip.getLuk(), 5));
        equip.setMatk(getRandStatAbove(equip.getMatk(), 5));
        equip.setWatk(getRandStatAbove(equip.getWatk(), 5));
        equip.setAcc(getRandStatAbove(equip.getAcc(), 5));
        equip.setAvoid(getRandStatAbove(equip.getAvoid(), 5));
        equip.setJump(getRandStatAbove(equip.getJump(), 5));
        equip.setHands(getRandStatAbove(equip.getHands(), 5));
        equip.setSpeed(getRandStatAbove(equip.getSpeed(), 5));
        equip.setWdef(getRandStatAbove(equip.getWdef(), 10));
        equip.setMdef(getRandStatAbove(equip.getMdef(), 10));
        equip.setHp(getRandStatAbove(equip.getHp(), 10));
        equip.setMp(getRandStatAbove(equip.getMp(), 10));
        return equip;
    }

    public Equip fuse(Equip equip1, Equip equip2) {
        if (equip1.getItemId() != equip2.getItemId()) {
            return equip1;
        }
        Equip equip = (Equip) getEquipById(equip1.getItemId());
        equip.setStr(getRandStatFusion(equip.getStr(), equip1.getStr(), equip2.getStr()));
        equip.setDex(getRandStatFusion(equip.getDex(), equip1.getDex(), equip2.getDex()));
        equip.setInt(getRandStatFusion(equip.getInt(), equip1.getInt(), equip2.getInt()));
        equip.setLuk(getRandStatFusion(equip.getLuk(), equip1.getLuk(), equip2.getLuk()));
        equip.setMatk(getRandStatFusion(equip.getMatk(), equip1.getMatk(), equip2.getMatk()));
        equip.setWatk(getRandStatFusion(equip.getWatk(), equip1.getWatk(), equip2.getWatk()));
        equip.setAcc(getRandStatFusion(equip.getAcc(), equip1.getAcc(), equip2.getAcc()));
        equip.setAvoid(getRandStatFusion(equip.getAvoid(), equip1.getAvoid(), equip2.getAvoid()));
        equip.setJump(getRandStatFusion(equip.getJump(), equip1.getJump(), equip2.getJump()));
        equip.setHands(getRandStatFusion(equip.getHands(), equip1.getHands(), equip2.getHands()));
        equip.setSpeed(getRandStatFusion(equip.getSpeed(), equip1.getSpeed(), equip2.getSpeed()));
        equip.setWdef(getRandStatFusion(equip.getWdef(), equip1.getWdef(), equip2.getWdef()));
        equip.setMdef(getRandStatFusion(equip.getMdef(), equip1.getMdef(), equip2.getMdef()));
        equip.setHp(getRandStatFusion(equip.getHp(), equip1.getHp(), equip2.getHp()));
        equip.setMp(getRandStatFusion(equip.getMp(), equip1.getMp(), equip2.getMp()));
        return equip;
    }

    public int getTotalStat(Equip equip) { //i get COOL when my defense is higher on gms...
        return equip.getStr() + equip.getDex() + equip.getInt() + equip.getLuk() + equip.getMatk() + equip.getWatk() + equip.getAcc() + equip.getAvoid() + equip.getJump()
                + equip.getHands() + equip.getSpeed() + equip.getHp() + equip.getMp() + equip.getWdef() + equip.getMdef();
    }

    public MapleStatEffect getItemEffect(int itemId) {
        MapleStatEffect ret = itemEffects.get(itemId);
        if (ret == null) {
            MapleData item = getItemData(itemId);
            if (item == null || item.getChildByPath("spec") == null) {
                return null;
            }
            ret = MapleStatEffect.loadItemEffectFromData(item.getChildByPath("spec"), itemId);
            itemEffects.put(itemId, ret);
        }
        return ret;
    }

    public MapleStatEffect getItemEffectEX(int itemId) {
        MapleStatEffect ret = itemEffectsEx.get(Integer.valueOf(itemId));
        if (ret == null) {
            MapleData item = getItemData(itemId);
            if (item == null || item.getChildByPath("specEx") == null) {
                return null;
            }
            ret = MapleStatEffect.loadItemEffectFromData(item.getChildByPath("specEx"), itemId);
            itemEffectsEx.put(Integer.valueOf(itemId), ret);
        }
        return ret;
    }

    public String resolvePotentialId(int itemId, int potId) {
        int eqLevel = getReqLevel(itemId);
        int potLevel;
        List<StructItemOption> potInfo = getPotentialInfo(potId);
        if (eqLevel == 0) {
            potLevel = 1;
        } else {
            potLevel = (eqLevel + 1) / 10;
//            potLevel++;
        }
        if (potId <= 0) {
            return "沒有潛能屬性";
        }
        StructItemOption itemOption = potInfo.get((potLevel - 1)<20?potLevel-1:19);
        String ret = itemOption.opString;
        for (int i = 0; i < itemOption.opString.length(); i++) {
            //# denotes the beginning of the parameter name that needs to be replaced, e.g. "Weapon DEF: +#incPDD"
            if (itemOption.opString.charAt(i) == '#') {
                int j = i + 2;
                while ((j < itemOption.opString.length()) && itemOption.opString.substring(i + 1, j).matches("^(?!_)(?!.*?_$)[a-zA-Z0-9_\u4e00-\u9fa5]+$")) {
                    j++;
                }
                String curParam = itemOption.opString.substring(i, j);
                String curParamStripped;
                //get rid of any trailing percent signs on the parameter name
                if (j != itemOption.opString.length() || itemOption.opString.charAt(itemOption.opString.length() - 1) == '%') { //hacky
                    curParamStripped = curParam.substring(1, curParam.length() - 1);
                } else {
                    curParamStripped = curParam.substring(1);
                }
                String paramValue = Integer.toString(itemOption.get(curParamStripped));
                if (curParam.charAt(curParam.length() - 1) == '%') {
                    paramValue = paramValue.concat("%");
                }
                ret = ret.replace(curParam, paramValue);
            }
        }
        return ret;
    }

    public int getCreateId(int id) {
        ItemInformation i = getItemInformation(id);
        if (i == null) {
            return 0;
        }
        return i.create;
    }

    public int getCardMobId(int id) {
        ItemInformation i = getItemInformation(id);
        if (i == null) {
            return 0;
        }
        return i.monsterBook;
    }

    public int getBagType(int id) {
        ItemInformation i = getItemInformation(id);
        if (i == null) {
            return 0;
        }
        return i.flag & 0xF;
    }

    public int getWatkForProjectile(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null || i.equipStats == null || i.equipStats.get("incPAD") == null) {
            return 0;
        }
        return i.equipStats.get("incPAD");
    }

    public boolean canScroll(int scrollid, int itemid) {
        return (scrollid / 100) % 100 == (itemid / 10000) % 100;
    }

    public String getName(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.name;
    }

    public String getDesc(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.desc;
    }

    public String getMsg(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.msg;
    }

    public short getItemMakeLevel(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return 0;
        }
        return i.itemMakeLevel;
    }

    /*   public boolean isDropRestricted(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return ((i.WORLD_FLAGS & 0x200) != 0 || (i.WORLD_FLAGS & 0x400) != 0 || GameConstants.isDropRestricted(itemId)) && (itemId == 3012000 || itemId == 3012015 || itemId / 10000 != 301) && itemId != 2041200 && itemId != 5640000 && itemId != 4170023 && itemId != 2040124 && itemId != 2040125 && itemId != 2040126 && itemId != 2040211 && itemId != 2040212 && itemId != 2040227 && itemId != 2040228 && itemId != 2040229 && itemId != 2040230 && itemId != 1002926 && itemId != 1002906 && itemId != 1002927 && !PokemonItem.isPokemonItem(itemId);
    }

    public boolean isPickupRestricted(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return ((i.WORLD_FLAGS & 0x80) != 0 || GameConstants.isPickupRestricted(itemId)) && itemId != 4001168 && itemId != 4031306 && itemId != 4031307;
    }
    * 
     */
    public boolean isAccountShared(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.flag & 0x100) != 0;
    }

    public int getStateChangeItem(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return 0;
        }
        return i.stateChange;
    }

    public int getMeso(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return 0;
        }
        return i.meso;
    }

    public boolean isShareTagEnabled(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.flag & 0x800) != 0;
    }

    public boolean isKarmaEnabled(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return i.karmaEnabled == 1;
    }

    public boolean isPKarmaEnabled(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return i.karmaEnabled == 2;
    }

    /* public boolean isPickupBlocked(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.WORLD_FLAGS & 0x40) != 0;
    }
    * 
     */

 /*   public boolean isLogoutExpire(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.WORLD_FLAGS & 0x20) != 0;
    }
    * 
     */

 /*  public boolean cantSell(int itemId) { //true = cant sell, false = can sell
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.WORLD_FLAGS & 0x10) != 0;
    }
    * 
     */
    public Pair<Integer, List<StructRewardItem>> getRewardItem(int itemid) {
        ItemInformation i = getItemInformation(itemid);
        if (i == null) {
            return null;
        }
        return new Pair<>(i.totalprob, i.rewardItems);
    }

    public boolean isMobHP(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.flag & 0x1000) != 0;
    }

    public boolean isQuestItem(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return (i.flag & 0x200) != 0 && itemId / 10000 != 301;
    }

    public Pair<Integer, List<Integer>> questItemInfo(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return new Pair<>(i.questId, i.questItems);
    }

    public Pair<Integer, String> replaceItemInfo(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return new Pair<>(i.replaceItem, i.replaceMsg);
    }

    public List<Triple<String, Point, Point>> getAfterImage(String after) {
        return afterImage.get(after);
    }

    public String getAfterImage(int itemId) {
        ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return null;
        }
        return i.afterImage;
    }

    public boolean itemExists(int itemId) {
        if (GameConstants.getInventoryType(itemId) == MapleInventoryType.UNDEFINED) {
            return false;
        }
        if(itemId == 1012616 || itemId == 1022194 || itemId == 1022183 || itemId == 1022184)
            return false;
        return getItemInformation(itemId) != null;
    }

    public boolean isCash(int itemId) {
        if (getEquipStats(itemId) == null) {
            return GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH;
        }
        return GameConstants.getInventoryType(itemId) == MapleInventoryType.CASH || getEquipStats(itemId).get("cash") != null;
    }

    public void initItemRewardData(ResultSet sqlRewardData) throws SQLException {
        int itemID = sqlRewardData.getInt("itemid");
        if (tmpInfo == null || tmpInfo.itemId != itemID) {
            if (!dataCache.containsKey(itemID)) {
                System.out.println("[initItemRewardData] Tried to Load an item while this is not in the cache: " + itemID);
                return;
            }
            tmpInfo = dataCache.get(itemID);
        }

        if (tmpInfo.rewardItems == null) {
            tmpInfo.rewardItems = new ArrayList<>();
        }

        StructRewardItem add = new StructRewardItem();
        add.itemid = sqlRewardData.getInt("item");
        add.period = (add.itemid == 1122017 ? Math.max(sqlRewardData.getInt("period"), 7200) : sqlRewardData.getInt("period"));
        add.prob = sqlRewardData.getInt("prob");
        add.quantity = sqlRewardData.getShort("quantity");
        add.worldmsg = sqlRewardData.getString("worldMsg").length() <= 0 ? null : sqlRewardData.getString("worldMsg");
        add.effect = sqlRewardData.getString("effect");

        tmpInfo.rewardItems.add(add);
    }

    public void initItemAddData(ResultSet sqlAddData) throws SQLException {
        int itemID = sqlAddData.getInt("itemid");
        if (tmpInfo == null || tmpInfo.itemId != itemID) {
            if (!dataCache.containsKey(itemID)) {
                System.out.println("[initItemAddData] Tried to Load an item while this is not in the cache: " + itemID);
                return;
            }
            tmpInfo = dataCache.get(itemID);
        }

        if (tmpInfo.equipAdditions == null) {
            tmpInfo.equipAdditions = new EnumMap<>(EquipAdditions.class);
        }

        EquipAdditions z = EquipAdditions.fromString(sqlAddData.getString("key"));
        if (z != null) {
            tmpInfo.equipAdditions.put(z, new Pair<>(sqlAddData.getInt("value1"), sqlAddData.getInt("value2")));
        }
    }

    public void initItemEquipData(ResultSet sqlEquipData) throws SQLException {
        int itemID = sqlEquipData.getInt("itemid");
        if (tmpInfo == null || tmpInfo.itemId != itemID) {
            if (!dataCache.containsKey(itemID)) {
                System.out.println("[initItemEquipData] Tried to Load an item while this is not in the cache: " + itemID);
                return;
            }
            tmpInfo = dataCache.get(itemID);
        }

        if (tmpInfo.equipStats == null) {
            tmpInfo.equipStats = new HashMap<>();
        }

        int itemLevel = sqlEquipData.getInt("itemLevel");
        if (itemLevel == -1) {
            tmpInfo.equipStats.put(sqlEquipData.getString("key"), sqlEquipData.getInt("value"));
        } else {
            if (tmpInfo.equipIncs == null) {
                tmpInfo.equipIncs = new HashMap<>();
            }

            Map<String, Integer> toAdd = tmpInfo.equipIncs.get(itemLevel);
            if (toAdd == null) {
                toAdd = new HashMap<>();
                tmpInfo.equipIncs.put(itemLevel, toAdd);
            }
            toAdd.put(sqlEquipData.getString("key"), sqlEquipData.getInt("value"));
        }
    }

    public void finalizeEquipData(ItemInformation item) {
        int itemId = item.itemId;

        // Some equips do not have equip data. So we initialize it anyway if not initialized
        // already
        // Credits: Jay :)
        if (item.equipStats == null) {
            item.equipStats = new HashMap<>();
        }

        item.eq = new Equip(itemId, (byte) 0, -1, (byte) 0);
        short stats = GameConstants.getStat(itemId, 0);
        if (stats > 0) {
            item.eq.setStr(stats);
            item.eq.setDex(stats);
            item.eq.setInt(stats);
            item.eq.setLuk(stats);
        }
        stats = GameConstants.getATK(itemId, 0);
        if (stats > 0) {
            item.eq.setWatk(stats);
            item.eq.setMatk(stats);
        }
        stats = GameConstants.getHpMp(itemId, 0);
        if (stats > 0) {
            item.eq.setHp(stats);
            item.eq.setMp(stats);
        }
        stats = GameConstants.getDEF(itemId, 0);
        if (stats > 0) {
            item.eq.setWdef(stats);
            item.eq.setMdef(stats);
        }
        if (item.equipStats.size() > 0) {
            for (Entry<String, Integer> stat : item.equipStats.entrySet()) {
                String key = stat.getKey();
                switch (key) {
                    case "STR":
                        item.eq.setStr(GameConstants.getStat(itemId, stat.getValue().intValue()));
                        break;
                    case "DEX":
                        item.eq.setDex(GameConstants.getStat(itemId, stat.getValue().intValue()));
                        break;
                    case "INT":
                        item.eq.setInt(GameConstants.getStat(itemId, stat.getValue().intValue()));
                        break;
                    case "LUK":
                        item.eq.setLuk(GameConstants.getStat(itemId, stat.getValue().intValue()));
                        break;
                    case "PAD":
                        item.eq.setWatk(GameConstants.getATK(itemId, stat.getValue().intValue()));
                        break;
                    case "PDD":
                        item.eq.setWdef(GameConstants.getDEF(itemId, stat.getValue().intValue()));
                        break;
                    case "MAD":
                        item.eq.setMatk(GameConstants.getATK(itemId, stat.getValue().intValue()));
                        break;
                    case "MDD":
                        item.eq.setMdef(GameConstants.getDEF(itemId, stat.getValue().intValue()));
                        break;
                    case "ACC":
                        item.eq.setAcc((short) stat.getValue().intValue());
                        break;
                    case "EVA":
                        item.eq.setAvoid((short) stat.getValue().intValue());
                        break;
                    case "Speed":
                        item.eq.setSpeed((short) stat.getValue().intValue());
                        break;
                    case "Jump":
                        item.eq.setJump((short) stat.getValue().intValue());
                        break;
                    case "MHP":
                        item.eq.setHp(GameConstants.getHpMp(itemId, stat.getValue().intValue()));
                        break;
                    case "MMP":
                        item.eq.setMp(GameConstants.getHpMp(itemId, stat.getValue().intValue()));
                        break;
                    case "tuc":
                        item.eq.setUpgradeSlots(stat.getValue().byteValue());
                        break;
                    case "Craft":
                        item.eq.setHands(stat.getValue().shortValue());
                        break;
                    case "durability":
                        item.eq.setDurability(stat.getValue().intValue());
                        break;
                    case "charmEXP":
                        item.eq.setCharmEXP(stat.getValue().shortValue());
                        break;
                    case "PVPDamage":
                        item.eq.setPVPDamage(stat.getValue().shortValue());
                        break;
                }
            }
            if (item.equipStats.get("cash") != null && item.eq.getCharmEXP() <= 0) { //set the exp
                short exp = 0;
                int identifier = itemId / 10000;
                if (GameConstants.isWeapon(itemId) || identifier == 106) { //weapon overall
                    exp = 60;
                } else if (identifier == 100) { //hats
                    exp = 50;
                } else if (GameConstants.isAccessory(itemId) || identifier == 102 || identifier == 108 || identifier == 107) { //gloves shoes accessory
                    exp = 40;
                } else if (identifier == 104 || identifier == 105 || identifier == 110) { //top bottom cape
                    exp = 30;
                }
                item.eq.setCharmEXP(exp);
            }
        }
    }

    public void initItemInformation(ResultSet sqlItemData) throws SQLException {
        ItemInformation ret = new ItemInformation();
        int itemId = sqlItemData.getInt("itemid");
        ret.itemId = itemId;
        ret.slotMax = GameConstants.getSlotMax(itemId) > 0 ? GameConstants.getSlotMax(itemId) : sqlItemData.getShort("slotMax");
        ret.price = Double.parseDouble(sqlItemData.getString("price"));
        ret.wholePrice = sqlItemData.getInt("wholePrice");
        ret.stateChange = sqlItemData.getInt("stateChange");
        ret.name = sqlItemData.getString("name");
        ret.desc = sqlItemData.getString("desc");
        ret.msg = sqlItemData.getString("msg");

        ret.flag = sqlItemData.getInt("flags");

        ret.karmaEnabled = sqlItemData.getByte("karma");
        ret.meso = sqlItemData.getInt("meso");
        ret.monsterBook = sqlItemData.getInt("monsterBook");
        ret.itemMakeLevel = sqlItemData.getShort("itemMakeLevel");
        ret.questId = sqlItemData.getInt("questId");
        ret.create = sqlItemData.getInt("create");
        ret.replaceItem = sqlItemData.getInt("replaceId");
        ret.replaceMsg = sqlItemData.getString("replaceMsg");
        ret.afterImage = sqlItemData.getString("afterImage");
        ret.cardSet = 0;
        if (ret.monsterBook > 0 && itemId / 10000 == 238) {
            mobIds.put(ret.monsterBook, itemId);
            for (Entry<Integer, Triple<Integer, List<Integer>, List<Integer>>> set : monsterBookSets.entrySet()) {
                if (set.getValue().mid.contains(itemId)) {
                    ret.cardSet = set.getKey();
                    break;
                }
            }
        }

        String scrollRq = sqlItemData.getString("scrollReqs");
        if (scrollRq.length() > 0) {
            ret.scrollReqs = new ArrayList<>();
            String[] scroll = scrollRq.split(",");
            for (String s : scroll) {
                if (s.length() > 1) {
                    ret.scrollReqs.add(Integer.parseInt(s));
                }
            }
        }
        String consumeItem = sqlItemData.getString("consumeItem");
        if (consumeItem.length() > 0) {
            ret.questItems = new ArrayList<>();
            String[] scroll = scrollRq.split(",");
            for (String s : scroll) {
                if (s.length() > 1) {
                    ret.questItems.add(Integer.parseInt(s));
                }
            }
        }

        ret.totalprob = sqlItemData.getInt("totalprob");
        String incRq = sqlItemData.getString("incSkill");
        if (incRq.length() > 0) {
            ret.incSkill = new ArrayList<>();
            String[] scroll = incRq.split(",");
            for (String s : scroll) {
                if (s.length() > 1) {
                    ret.incSkill.add(Integer.parseInt(s));
                }
            }
        }
        dataCache.put(itemId, ret);
    }

    public Equip MSI(Equip equip, short stat) {
        final int uid = MapleInventoryIdentifier.getInstance();
        equip.setStr(stat);
        equip.setDex(stat);
        equip.setInt(stat);
        equip.setLuk(stat);
        equip.setMatk(stat);
        equip.setWatk(stat);
        equip.setAcc(stat);
        equip.setAvoid(stat);
        equip.setJump(stat);
        equip.setSpeed(stat);
        equip.setWdef(stat);
        equip.setMdef(stat);
        equip.setHp(stat);
        equip.setMp(stat);
        equip.setUpgradeSlots((byte) 0);
        equip.setViciousHammer((byte) 2);
        equip.setGiftFrom(Integer.toString(uid));
        return equip;
    }

    public boolean isMSI(Equip equip, short stat) {
        if (equip.getStr() > stat && equip.getDex() > stat && equip.getInt() > stat && equip.getLuk() > stat && equip.getMatk() > stat && equip.getWatk() > stat && equip.getAcc() > stat && equip.getAvoid() > stat && equip.getSpeed() > stat && equip.getJump() > stat && equip.getWdef() > stat && equip.getMdef() > stat && equip.getMp() > stat && equip.getHp() > stat) {
            return true;
        } else {
            return false;
        }
    }

    public Equip SRB2(Equip equip) {
        // short stat = ;
        equip.setStr((short) Math.max(3000, (Randomizer.nextInt(20000))));
        equip.setDex((short) Math.max(3000, (Randomizer.nextInt(20000))));
        equip.setInt((short) Math.max(3000, (Randomizer.nextInt(20000))));
        equip.setLuk((short) Math.max(3000, (Randomizer.nextInt(20000))));
        equip.setMatk((short) Math.max(50, (Randomizer.nextInt(200))));
        equip.setWatk((short) Math.max(50, (Randomizer.nextInt(200))));
        equip.setAcc((short) Math.max(3000, (Randomizer.nextInt(20000))));
        equip.setAvoid((short) Math.max(3000, (Randomizer.nextInt(20000))));
        equip.setJump((short) Math.max(3000, (Randomizer.nextInt(20000))));
        equip.setSpeed((short) Math.max(3000, (Randomizer.nextInt(20000))));
        equip.setWdef((short) Math.max(3000, (Randomizer.nextInt(20000))));
        equip.setMdef((short) Math.max(3000, (Randomizer.nextInt(20000))));
        equip.setHp((short) Math.max(3000, (Randomizer.nextInt(20000))));
        equip.setMp((short) Math.max(3000, (Randomizer.nextInt(20000))));
        return equip;
    }

    public Equip SRB3(Equip equip) {
        // short stat = ;
        equip.setStr((short) Math.max(20000, (Randomizer.nextInt(32767))));
        equip.setDex((short) Math.max(20000, (Randomizer.nextInt(32767))));
        equip.setInt((short) Math.max(20000, (Randomizer.nextInt(32767))));
        equip.setLuk((short) Math.max(20000, (Randomizer.nextInt(32767))));
        equip.setMatk((short) Math.max(200, (Randomizer.nextInt(500))));
        equip.setWatk((short) Math.max(200, (Randomizer.nextInt(500))));
        equip.setAcc((short) Math.max(20000, (Randomizer.nextInt(32767))));
        equip.setAvoid((short) Math.max(20000, (Randomizer.nextInt(32767))));
        equip.setJump((short) Math.max(20000, (Randomizer.nextInt(32767))));
        equip.setSpeed((short) Math.max(20000, (Randomizer.nextInt(32767))));
        equip.setWdef((short) Math.max(20000, (Randomizer.nextInt(32767))));
        equip.setMdef((short) Math.max(20000, (Randomizer.nextInt(32767))));
        equip.setHp((short) Math.max(20000, (Randomizer.nextInt(32767))));
        equip.setMp((short) Math.max(20000, (Randomizer.nextInt(32767))));
        return equip;
    }

    public final boolean isDropRestricted(final int itemId) {
        final ItemInformation i = getItemInformation(itemId);
        if (i == null) {
            return false;
        }
        return ((i.flag & 0x200) != 0 || (i.flag & 0x400) != 0 || GameConstants.isIllegal(itemId)) && (itemId == 3012000 || itemId == 3012015 || itemId / 10000 != 301) && itemId != 2041200 && itemId != 5640000 && itemId != 4170023 && itemId != 2040124 && itemId != 2040125 && itemId != 2040126 && itemId != 2040211 && itemId != 2040212 && itemId != 2040227 && itemId != 2040228 && itemId != 2040229 && itemId != 2040230 && itemId != 1002926 && itemId != 1002906 && itemId != 1002927;
    }

    public static enum JobInfoFlag {

        臉型(0x1),
        髮型(0x2),
        臉飾(0x4),
        耳朵(0x8),
        尾巴(0x10),
        帽子(0x20),
        衣服(0x40),
        褲裙(0x80),
        披風(0x100),
        鞋子(0x200),
        手套(0x400),
        武器(0x800),
        副手(0x1000),;
        private final int value;

        private JobInfoFlag(int value) {
            this.value = value;
        }

        public int getVelue() {
            return value;
        }

        public boolean check(int x) {
            return (value & x) != 0;
        }
    }

    public static enum JobType {

        終極冒險家(-1, MapleJob.初心者.getId(), 100000000, JobInfoFlag.褲裙.getVelue()),
        末日反抗軍(0, MapleJob.市民.getId(), 931000000),
        冒險家(1, MapleJob.初心者.getId(), 4000000),
        皇家騎士團(2, MapleJob.貴族.getId(), 130030000, JobInfoFlag.披風.getVelue()),
        狂狼勇士(3, MapleJob.傳說.getId(), 914000000, JobInfoFlag.褲裙.getVelue()),
        龍魔導士(4, MapleJob.龍魔導士.getId(), 900010000, JobInfoFlag.褲裙.getVelue()),
        精靈遊俠(5, MapleJob.精靈遊俠.getId(), 910150000),
        惡魔(6, MapleJob.惡魔殺手.getId(), 931050310, JobInfoFlag.臉飾.getVelue() | JobInfoFlag.副手.getVelue()),
        幻影俠盜(7, MapleJob.幻影俠盜.getId(), 915000000, JobInfoFlag.披風.getVelue()),
        影武者(8, MapleJob.初心者.getId(), 103050900),
        米哈逸(9, MapleJob.米哈逸.getId(), 913070000, JobInfoFlag.褲裙.getVelue()),
        夜光(10, MapleJob.夜光.getId(), 927020080, JobInfoFlag.披風.getVelue()),
        凱撒(11, MapleJob.凱撒.getId(), 940001000),
        天使破壞者(12, MapleJob.天使破壞者.getId(), 940011000),
        重砲指揮官(13, MapleJob.初心者.getId(), 3000600),
        傑諾(14, MapleJob.傑諾.getId(), 931060089, JobInfoFlag.臉飾.getVelue()),
        神之子(15, MapleJob.神之子.getId(), 321000001, JobInfoFlag.披風.getVelue() | JobInfoFlag.副手.getVelue()),
        隱月(16, MapleJob.隱月.getId(), 927030050, JobInfoFlag.褲裙.getVelue() | JobInfoFlag.披風.getVelue()),
        皮卡啾(17, MapleJob.皮卡啾1轉.getId(), 927030090),
        凱內西斯(18, MapleJob.凱內西斯.getId(), 331001100),
        蒼龍俠客(19, MapleJob.蒼龍俠客1轉.getId(), 743020100, JobInfoFlag.褲裙.getVelue()),
        劍豪(20, MapleJob.劍豪.getId(), 807100010, JobInfoFlag.帽子.getVelue() | JobInfoFlag.手套.getVelue()),
        陰陽師(21, MapleJob.陰陽師.getId(), 807100110, JobInfoFlag.帽子.getVelue() | JobInfoFlag.手套.getVelue()),
        幻獸師(22, MapleJob.幻獸師.getId(), 866100000, JobInfoFlag.臉飾.getVelue() | JobInfoFlag.耳朵.getVelue() | JobInfoFlag.尾巴.getVelue());
        public int type, id, map;
        public int flag = JobInfoFlag.臉型.getVelue() | JobInfoFlag.髮型.getVelue() | JobInfoFlag.衣服.getVelue() | JobInfoFlag.鞋子.getVelue() | JobInfoFlag.武器.getVelue();

        private JobType(int type, int id, int map) {
            this.type = type;
            this.id = id;
            this.map = map;
        }

        private JobType(int type, int id, int map, int flag) {
            this.type = type;
            this.id = id;
            this.map = map;
            this.flag |= flag;
        }

        public static JobType getByType(int g) {
            for (JobType e : JobType.values()) {
                if (e.type == g) {
                    return e;
                }
            }
            return null;
        }

        public static JobType getById(int g) {
            for (JobType e : JobType.values()) {
                if (e.id == g) {
                    return e;
                }
            }
            return null;
        }
    }
}
