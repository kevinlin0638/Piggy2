var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 0) {
	    cm.dispose();
	}
	status--;
    }
    if (status == 0) {
		if (cm.getPlayer().getMapId() == 262000300) {
			cm.sendAzwanWindow();
		} else {
			cm.sendYesNo("Would you like to enter #eFight for Azwan - Occupy Lobby#n?");
		}
    } else if (status == 1) {
		if (cm.getPlayer().getMapId() != 262000300) {
			cm.warp(262000300, 0);
			cm.dispose();
		}
	}
}