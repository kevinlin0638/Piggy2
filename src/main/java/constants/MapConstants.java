package constants;

public class MapConstants {

    public static String FM_BGM = "BgmTW.img/NightMarket";

    public static boolean isStartingEventMap(final int mapid) {
        switch (mapid) {
            case 109010000: // [尋寶遊戲]
            case 109020001: // [選邊站]
            case 109030001: // [向上攀升]
            case 109030101: // [向上攀升]
            case 109030201: // [向上攀升]
            case 109030301: // [向上攀升]
            case 109030401: // [向上攀升]
            case 109040000: // [障礙競走]
            case 109060000: // [滾雪球]
            case 109060001: // 活動地圖入口
            case 109060002: // 滾雪球<1階段>
            case 109060003: // 活動地圖入口
            case 109060004: // 滾雪球<2階段>
            case 109060005:
            case 109060006:
            case 109080000:
            case 109080001:
            case 109080002:
            case 109080003:
                return true;
        }
        return false;
    }

    public static boolean isEventMap(final int mapid) {
        boolean ret = (mapid >= 109010000 && mapid < 109050000) || (mapid > 109050001 && mapid < 109090000) || (mapid >= 809040000 && mapid <= 809040100);
        ret = ret || (mapid >= 280010000 && mapid <= 280030000); // 比斯的任務;
        ret = ret || (mapid >= 109020001 && mapid <= 109080012); // 活動地圖
        ret = ret || (mapid >= 910130000 && mapid <= 910130102); // 忍耐的森林
        ret = ret || (mapid >= 910530000 && mapid <= 910530202); // 沉睡森林
        ret = ret || (mapid >= 910530000 && mapid <= 910530202); // 沉睡森林
        return ret;
    }

    //農夫的樂趣
    public static boolean isCoconutMap(final int mapid) {
        return mapid == 109080000 || mapid == 109080001 || mapid == 109080002 || mapid == 109080003 || mapid == 109080010 || mapid == 109080011 || mapid == 109080012 || mapid == 109090300 || mapid == 109090301 || mapid == 109090302 || mapid == 109090303 || mapid == 109090304 || mapid == 910040100;
    }

    public boolean isStartupMap(int mapid) {
        switch (mapid) {
            case 130000000:
            case 931000000:
            case 10000:
            case 913040000:
            case 914000000:
            case 900090000:
            case 910150000:
            case 931050310:
            case 915000000:
                return true;
        }
        return false;
    }
}
