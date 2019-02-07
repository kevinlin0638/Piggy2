/*
    Zakum Entrance
*/

function enter(pi) {
    if (pi.getQuestStatus(100200) != 2) {
	pi.playerMessage(5, "您還沒準備好挑戰 Boss.");
	return false;

    } else if (!pi.haveItem(4001017)) {
	pi.playerMessage(5, "沒有火焰之眼.  您無法挑戰 boss.");
	return false;
    }
    
    pi.playPortalSE();
    pi.warp(pi.getPlayer().getMapId() + 100, "west00");
    return true;
}