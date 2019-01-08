function enter(pi) {
    if (pi.getMap().getAllMonstersThreadsafe().size() == 0) {
	pi.givePartyItems(4310020, 1);
	pi.warpParty(951000000,0); //next
    } else {
	pi.playerMessage(5, "此傳點尚未開啟");
    }
}