/**
 * @author: Eric
 * @npc: Sky-blue Balloon - LudiPQ 4th (was 7th) stage NPC
*/

var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
	(mode == 1 ? status++ : mode == 0 ? status-- : cm.dispose());
	var eim = cm.getEventInstance();
	var stage7status = eim.getProperty("stage7status");
	if (status == 0) {
		if (stage7status == null) {
			if (cm.isLeader()) { // Leader
				var stage7leader = eim.getProperty("stage7leader");
				if (stage7leader == "done") { // not in gms anymore because i just tested this
					if (cm.getMap().getAllMonstersThreadsafe().size() == 0) {
						status = 0;
						cm.sendNext("Wow, not a single #b#o9300010##k left! I'm impressed! I can open the portal to the next stage now.");
					} else { // Not done yet
						cm.sendNext("Welcome to the fourth stage. Here, you must face the powerful #b#o9300010#. #o9300010##k is a fearsome opponent, so do not let your guard down. Once you do defeat it, let me know and I'll show you to the next stage.");
						cm.dispose();
					}
				} else {
					cm.sendNext("Welcome to the fourth stage. Here, you must face the powerful #b#o9300010#. #o9300010##k is a fearsome opponent, so do not let your guard down. Once you do defeat it, let me know and I'll show you to the next stage.");
					eim.setProperty("stage7leader","done");
					cm.dispose();
				}
			} else { // Members
				cm.sendNext("Welcome to the fourth stage. Here, you must face the powerful #b#o9300010#. #o9300010##k is a fearsome opponent, so do not let your guard down. Once you do defeat it, let me know and I'll show you to the next stage.");
				cm.dispose();
			}
		} else {
			cm.sendNext("Wow, not a single #b#o9300010##k left! I'm impressed! I can open the portal to the next stage now.");
			cm.dispose();
		}
	} else if (status == 1) {
		cm.sendNextPrev("The portal that leads you to the next stage is now open.");
	} else if (status == 2) {
		if (eim.getProperty("stage7status") == null) { // this is manual in gms, turns out it's not automated like every other stage..
			cm.removeAll(4001022); // not even dropped in this stage but whatever
			clear(7, eim, cm);
			cm.givePartyExp(4620, eim.getPlayers());
		}
		cm.dispose();
	}
}

function clear(stage, eim, cm) {
    eim.setProperty("stage" + stage.toString() + "status", "clear");
    cm.showEffect(true, "quest/party/clear");
    cm.playSound(true, "Party1/Clear");
    cm.environmentChange(true, "gate");
}