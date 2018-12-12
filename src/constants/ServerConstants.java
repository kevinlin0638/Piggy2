package constants;

import java.util.Arrays;
import java.util.List;

public class ServerConstants {

    public static final int MAPLE_LOCALE = 6;
    public static final short MAPLE_VERSION = 145;
    public static final String MAPLE_PATCH = "1";
    public static final String WZ_PATH = "./wz";
    public static final String SERVER_IP = "apple040285.ddns.net";

    public static String SERVER_NAME = "開發中 v145";

    public static boolean BLOCK_CASH_SHOP = false;
    public static boolean DEBUG = true;
    public static boolean ADMIN_ONLY = false;
    public static boolean ONLY_LOCALHOST = false; // Only allow accounted admins to connect pass login server
    public static boolean USE_SECOND_PASSWORD_AUTH = false;
    public static int CHANNEL_LOAD = 150; // players per channel

    // 登入畫面氣球
    private static final List<MapleLoginBalloon> mapleLoginBalloonList = Arrays.asList(
            new MapleLoginBalloon("歡迎來到" + ServerConstants.SERVER_NAME, 240, 140),
            new MapleLoginBalloon("禁止開外掛", 100, 150),
            new MapleLoginBalloon("遊戲愉快", 370, 150));

    public static final int Currency = 4000999;
    public static final boolean MerchantsUseCurrency = false; // Log Packets = true | Allow people to connect = false
    public static boolean dropUndroppables = true;
    public static boolean moreThanOne = true;

    // 歡迎訊息
    public static String SERVER_MESSAGE = "歡迎來到v145.1，此端正在開發中 OuO/";
    public static String WELCOME_MESSAGE = "【歡迎訊息】";

    public static List<MapleLoginBalloon> getBalloons() {
        return mapleLoginBalloonList;
    }

    public enum PlayerGMRank {

        NORMAL('@', 0),
        INTERN('!', 3),
        GM('!', 4),
        SUPER_GM('!', 5),
        ADMIN('!', 6),
        GOD('!', 100);
        private char commandPrefix;
        private int level;

        PlayerGMRank(char ch, int level) {
            commandPrefix = ch;
            this.level = level;
        }

        public char getCommandPrefix() {
            return commandPrefix;
        }

        public int getLevel() {
            return level;
        }

        public static PlayerGMRank getByLevel(int level) {
            for (PlayerGMRank i : PlayerGMRank.values()) {
                if (i.getLevel() == level) {
                    return i;
                }
            }
            return PlayerGMRank.NORMAL;
        }
    }

    public enum CommandType {

        NORMAL(0),
        TRADE(1);
        private int level;

        CommandType(int level) {
            this.level = level;
        }

        public int getType() {
            return level;
        }
    }

    public static class MapleLoginBalloon {
        public int nX, nY;
        public String sMessage;

        public MapleLoginBalloon(String sMessage, int nX, int nY) {
            this.sMessage = sMessage;
            this.nX = nX;
            this.nY = nY;
        }
    }
}
