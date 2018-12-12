function start() {
    cm.sendYesNo("Do you want to max your skills?"); 
}

function action(mode, type, selection) {
     if (mode > 0) {
		if (cm.getPlayer().isGM()) {
			cm.getPlayer().maxAllSkills();
		} else
			cm.sendOk("Hello, I'm #rDuey#k!\r\n\r\nI offer the #bDuey Service#k.");
     }
     cm.dispose();
}  