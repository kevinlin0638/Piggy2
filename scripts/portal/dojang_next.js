function enter(pi) {
    if (!pi.haveMonster(9300216)) {
	pi.playerMessage("地圖上目前還有怪物.");
    } else {
	pi.dojoAgent_NextMap(true, false);
    }
}