var status = -1;

function start() {
    cm.askAcceptDecline("你是要來打敗我的英雄嗎? 還是你是黑魔法師的同夥? 都沒關係... 沒有必要跟你多說什麼...\r\n來把,試著擊敗我!");
}

function action(mode, type, selection) {
    if (mode == 1 && cm.getMap().getAllMonstersThreadsafe().size() == 0) {
	cm.removeNpc(cm.getMapId(), 2161000);
	cm.spawnMob(8840010, 0, -181);
	if (!cm.getPlayer().isGM()) {
		cm.getMap().startSpeedRun();
	}
    }
    cm.dispose();
}