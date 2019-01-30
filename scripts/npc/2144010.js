var status = -1;

function start() {
    cm.askAcceptDecline("#r正面挑戰我吧!");
}

function action(mode, type, selection) {
    if (mode == 1 && cm.getMap().getAllMonstersThreadsafe().size() == 0) {
	cm.removeNpc(cm.getMapId(), 2144010);
	cm.spawnMob(8860010, 0, -181);
	if (!cm.getPlayer().isGM()) {
		cm.getMap().startSpeedRun();
	}
    }else{
		cm.sendOk("連我的分身都無法擊敗,還想挑戰我?");
	}
    cm.dispose();
}