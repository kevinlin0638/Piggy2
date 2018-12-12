var status = -1;

function action(mode, type, selection) {
	if (cm.getPlayer().getLevel() >= 30) {
		  cm.sendPVPWindow();
		} else {
		  cm.sendOk("You are not a high enough #rlevel#k to #rPVP#k.");
		}
	cm.dispose();
}