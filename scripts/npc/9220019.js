/**
 * @author: Eric
 * @npc: Milla
 * @func: Guy Fawkes (MV) Multi-Map NPC
 * @note: Matched to be GMS-like. (Boss warp-out is English translation from EMS)
*/
var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode,type,selection) {
    (mode == 1 ? status++ : mode == 0 ? status-- : cm.dispose());
	if (status == 0) {
		if (cm.getPlayer().getMapId() == 674030000) {
			cm.sendYesNo("Do you see Charles somewhere in here? He seems to need someone to help him out. Anyway, if you want, I'll send you out from here.");
		} else if (cm.getPlayer().getMapId() == 674030200 || cm.getPlayer().getMapId() == 674030100){
			cm.sendYesNo("Do you want to leave here?");
		} else {
			cm.sendOk("Are you having fun here?");
			cm.dispose();
		}
	} else if (status == 1) {
		if (mode > 0) {
			if (cm.getPlayer().getMapId() == 674030000) {
				cm.warp(674030100, 0);
				cm.dispose();
			} else if (cm.getPlayer().getMapId() == 674030200) {
				cm.warp(674030100, 0);
				cm.dispose();
			} else if (cm.getPlayer().getMapId() == 674030100) {
				var map = cm.getSavedLocation("MULUNG_TC");
				cm.warp(map, 0);
				cm.dispose();
			}
		} else {
			cm.dispose();
		}
	}
}