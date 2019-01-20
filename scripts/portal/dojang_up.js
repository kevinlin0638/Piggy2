function enter(pi) {
    if (!pi.haveMonster(9300216)) {
	pi.playerMessage("目前地圖上還有怪物存在.");
    } else {
	pi.dojo_getUp();
	pi.getMap().setReactorState();
    }
}