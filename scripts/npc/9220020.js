/**
 * @author: Eric
 * @npc: Charles
 * @func: Guy Fawkes (MV) Initiation NPC
 * @note: Matched to be GMS-like.
*/
importPackage(Packages.client);
var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
    (mode == 1 ? status++ : mode == 0 ? status-- : cm.dispose());
	if (status == 0) {
		if (!cm.isLeader() || cm.getPlayer().getParty() == null) {
			cm.sendNext("I wish for your leader to talk to me.");
			cm.dispose();
		} else {
			if (cm.haveItem(4032118, 7)) {
				cm.sendNext("Alright, your party has collected all 7 secret letters as I asked.");
			} else {
				cm.sendOk("Hey! Find the 7 secret letters from the rocks here!");
				cm.dispose();
			}
		}
	} else if (status == 1) {
		cm.removeAll(4032118);
		cm.sendNext("I will activate the portal next to me. Enter the portal to receive your rewards.");
	} else if (status == 2) {
		cm.openGate();
		cm.mapMessage(6, "The escape portal is activated now. Enter the portal to receive your rewards. Hurry before time runs out!");
		cm.dispose(); 
	}
}