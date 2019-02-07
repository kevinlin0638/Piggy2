function enter(pi) {
    if (!pi.haveItem(4032246)) {
	pi.playerMessage(5, "你沒有 夢幻公園的意念.");
    } else {
	pi.openNpc(9270047);
    }
}