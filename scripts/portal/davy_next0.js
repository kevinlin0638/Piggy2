function enter(pi) {
    if (pi.getMap().getAllMonstersThreadsafe().size() == 0) {
	pi.warp(925100100,0); //next
    } else {
	pi.playerMessage(5, "此傳點尚未開啟");
    }
}