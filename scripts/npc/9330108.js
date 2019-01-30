/* Kedrick
	Fishking King NPC
*/

var status = -1;
var sel;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
		if (status == 0) {
			cm.dispose();
			return;
		}
	status--;
    }

    if (status == 0) {
	cm.sendSimple("在自由市場坐下任何椅子就可以釣魚摟! 您想做什麼？\n\r #b#L3#買魚餌#l");
    } else if (status == 1) {
	sel = selection;
	if (sel == 3) {
	    cm.dispose();
			cm.openNpc(9330109);
	} else if (sel == 2) {
	    var returnMap = cm.getSavedLocation("FISHING");
	    if (returnMap < 0 || cm.getMap(returnMap) == null) {
		returnMap = 910000000; // to fix people who entered the fm trough an unconventional way
	    }
	    cm.clearSavedLocation("FISHING");
	    cm.warp(returnMap,0);
	    cm.dispose();
	}
    } else if (status == 2) {
	if (sel == 0 && selection <= 2 && selection >= 0) {
	    if (cm.getPlayer().getMapId() < 749050500 || cm.getPlayer().getMapId() > 749050502) {
	    	cm.saveLocation("FISHING");
	    }
	    cm.warp(749050500 + selection);
	    cm.dispose();
	} else {
	    cm.dispose();
	}
    }
}