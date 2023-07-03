package server;

import client.MapleCharacter;

public class WordFilter {
    private final static String[] blocked = {"boredms", "b0r3dms", "b0redms", "bor3dms"};

    public static String illegalArrayCheck(String text, MapleCharacter player) {
        StringBuilder sb = new StringBuilder(text);
        String subString = text.toLowerCase();
        for (int i = 0; i < blocked.length; i++) {
            if (subString.contains(blocked[i].toLowerCase())) {
                sb.replace(sb.indexOf(blocked[i].toLowerCase()), sb.lastIndexOf(blocked[i].toLowerCase()) + blocked[i].length(), "boringms");
                // player.getClient().getSession().close();
            }
        }
        return sb.toString();
    }
}  