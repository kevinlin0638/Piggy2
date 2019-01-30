var pop = 2;

function enter(pi) {
	pi.playerMessage(5, "拉圖斯目前不開放挑戰。");
    return false;
    if (pi.getPlayer().getClient().getChannel() != 1 && pi.getPlayer().getClient().getChannel() != 2) {
        pi.playerMessage(5, "拉圖斯只能在頻道1和2 打而已。");
        return false;
    }
    if (pi.haveItem(4031870)) {
        pi.warp(922020300, 0);
        return true;
    }
    if (!pi.haveItem(4031172)) {
        pi.playerMessage(5, "不明的力量無法進入，需要有玩具獎牌。");
        return false;
    }
	/*if (pi.getPlayer().getBossLog("pop") >= 6) {
		pi.playerMessage(5, "一天只能打兩次拉圖斯。");
		return false;
	}*/
    if (pi.getPlayerCount(220080001) <= 0) { // Papu Map
        var papuMap = pi.getMap(220080001);
        papuMap.resetFully();
        pi.playPortalSE();
        pi.warp(220080001, "st00");
        return true;
    } else {
        /*if (/*pi.getMap(220080001).getPapfight() == 0pi.getMap(220080001).getSpeedRunStart() == 0 && (pi.getMonsterCount(220080001) <= 0 || pi.getMap(220080001).isDisconnected(pi.getPlayer().getId()))) {
            var papuMap = pi.getMap(220080001);
            pi.playPortalSE();
            pi.warp(220080001, "st00");
            return true;
        } else {*/
            pi.playerMessage(5, "裡面的戰鬥已經開始，請稍後再嘗試。");
            return false;
        //}
    }
}