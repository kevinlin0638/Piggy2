/**
 * @author: Eric
 * @npc: Tylus
 * @func: El Nath PQ
*/

var status = -1;

function action(mode, type, selection) {
    (mode == 1 ? status ++ : mode == 0 ? status-- : cm.dispose());
    if (status == 0) {
	    cm.sendNext("Thank you for guarding me. I could do my mission thanks to you. Talk to me when you're out.");
    } else if (status == 1) {
		if (!cm.haveItem(4031495) && (cm.getPlayer().getJobId() >= 100 && cm.getPlayer().getJobId() <= 132)) {
			if (cm.canHold(4031495)) {
				cm.gainItem(4031495, 1);
				cm.warp(211000001, 0);
				cm.dispose();
			} else {
				cm.sendOk("Please make room in your #rETC#k tab.");
				cm.dispose();
			}
		} else {
			cm.warp(211000001, 0);
			cm.dispose();
		}
    }
}