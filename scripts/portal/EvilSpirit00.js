/*
 六手邪神PQ
 **/
var badgod = 2;

function enter(pi) {
    if (pi.getPlayer().getClient().getChannel() != 2) {
        pi.playerMessage(5, "六手邪神只能在頻道2打而已。");
        return false;
    }
    if (!pi.haveItem(4031722)) {
        pi.playerMessage(5, "不明的力量無法進入，需要有太陽火花。");
        return false;
    }
    if (pi.getParty() == null) {
        pi.playerMessage(5, "請組隊再來找我。");
        return false;
    } else if (!pi.isLeader()) {
        pi.playerMessage(5, "請叫你的隊長來找我!");
        return false;
    }
    if (pi.getPlayer().getBossLog("badgod") >= 6) {
        pi.playerMessage(5, "一天只能打兩次六手邪神。");
        return false;
    }
    var em = pi.getEventManager("Ravana");
    if (em == null) {
        pi.playerMessage(6, "找不到腳本請聯絡管理員");
    } else {
        var nextmap = pi.getC().getChannelServer().getMapFactory().getMap(501030105);
        if (pi.getPlayerCount(501030105) == 0) {
            pi.getMap(501030104).resetReactors();
            nextmap.resetFully();
            pi.playPortalSE();
            em.startInstance(pi.getParty(), pi.getMap());
            return true;
        } else {
            pi.playerMessage(6, "裡面有人正在挑戰，請稍後再嘗試。");
            return false;
        }
    }
}