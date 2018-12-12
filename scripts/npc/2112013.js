/**
 * @author: Eric
 * @npc: Investigation Result
 * @func: Romeo and Juliet GMS-like PQ
*/
// TODO: Make the NPC clickable only ONCE PER POSITION
var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	(mode == 1 ? status++ : mode == 0 ? status-- : cm.dispose());
	var em = cm.getEventManager("Juliet");
	if (status == 0) {
		if (em == null) {
			cm.dispose();
			return;
		}
		if (!cm.canHold(4001131,1)) {
			cm.sendOk("Please clear a spot in your ETC inventory and try again.");
			cm.dispose();
			return;
		}
		if (cm.getPlayer().getMapId() == 926110000) {
			if (java.lang.Math.random() < 0.1) {
				if (em.getProperty("stage1").equals("0")) {
					cm.sendSimple("This is one suspicious-looking switch.#b\r\n\r\n#L10#Press the switch.\r\n#L11#Leave it as it is.");
				}
			} else if (java.lang.Math.random() < 0.05) {
				cm.sendNext("Found 500 mesos!");
				cm.gainMeso(500);
				cm.dispose();
				return;
			} else if (java.lang.Math.random() < 0.10) {
				cm.sendNext("Earned 500 EXP!");
				cm.gainExp(500);
				cm.dispose();
				return;
			// TODO: Juliet's letter for Angry Roid.. 
			// } else if (java.lang.Math.random() < 0.07) {
			//	cm.sendNext("I found a letter that Juliet/Romeo needs."); // TODO: gms-like
			//	cm.gainItem(4001131, 1);
			//	cm.dispose();
			//	return;
			} else {
				cm.sendNext("Unable to find anything here.");
				cm.dispose();
				return;
			}
		} else if (cm.getPlayer().getMapId() == 926110203) {
			if (java.lang.Math.random() < 0.1) {
				if (em.getProperty("stage5").equals("1") && cm.getMap().getAllMonstersThreadsafe().size() == 0) {
					 cm.sendSimple("This is one suspicious-looking switch.#b\r\n\r\n#L20#Press the switch.");
				}
			} else {
				cm.sendNext(!cm.getMap().getAllMonstersThreadsafe().size() == 0 ? "There is still monsters within the map, please eliminate them." : "Unable to find anything here.");
				cm.dispose();
				return;
			}
		}
	} else if (status == 1) {
		if (cm.getPlayer().getMapId() == 926110000) {
			if (selection == 10) {
				cm.mapMessage(6, cm.getPlayer().getName() + " pressed the switch, and a special portal appeared.");
				cm.showEffect(true, "quest/party/clear"); // map
				cm.showEffect(false, "quest/party/clear"); // client
				cm.playSound(true, "Party1/Clear"); // map
				cm.playSound(false, "Party1/Clear"); // client
				em.setProperty("stage1", "1");
				cm.getMap().setReactorState();
				cm.dispose();
			} else if (selection == 11) {
				cm.dispose();
			}
		} else if (cm.getPlayer().getMapId() == 926110203) {
			if (selection == 20) {
				cm.mapMessage(6, "After pressing the switch, a hidden portal emerged.");
				cm.showEffect(true, "quest/party/clear"); // map
				cm.showEffect(false, "quest/party/clear"); // client
				cm.playSound(true, "Party1/Clear"); // map
				cm.playSound(false, "Party1/Clear"); // client
				em.setProperty("stage5", "2");
				cm.getMap().setReactorState();
				cm.dispose();
			}
		}
	}
}

/*
function start() {
	if (!cm.checkNpcCoord(cm.getNpc(), cm.getPlayer().getPosition().x, cm.getPlayer().getPosition().y)) {
		cm.sendOk("This place has already been investigated.");
		cm.dispose();
		return;
	} else {
		cm.sendSimple("Well then.\r\n#L0#Flip le switch");
	}
}

function action(mode, type, selection) {
	if (mode > 0) {
		cm.mapMessage(6, cm.getPlayer().getName() + " has flipped the switch!");
	}
}*/