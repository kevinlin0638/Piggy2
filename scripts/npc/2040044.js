/**
 * @author: Eric
 * @npc: Violet Balloon
 * @func: LudiPQ 6th (was 9th) stage NPC
*/

var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
	(mode == 1 ? status++ : mode == 0 ? status-- : cm.dispose());
	var eim = cm.getEventInstance();
	if (status == 0) {
		if (!cm.isLeader()) {
			cm.sendNext("Now that you've come this far, it's time to defeat the one responsible for this mess, #b#o9300012#.#k I suggest you be careful, though, as he is not in a very good mood.\r\nIf you and your party members defeat him, the Dimensional Schism will close forever. I'm counting on you!");
			cm.dispose();
			return;
		}
		if (eim.getProperty("crackLeaderPreamble") == null) {
			eim.setProperty("crackLeaderPreamble", "done");
			cm.sendNext("Now that you've come this far, it's time to defeat the one responsible for this mess, #b#o9300012#.#k I suggest you be careful, though, as he is not in a very good mood.\r\nIf you and your party members defeat him, the Dimensional Schism will close forever. I'm counting on you!");
			cm.dispose();
		} else {
			if (cm.getMap().getAllMonstersThreadsafe().size() == 0) {
				status = 0;
				cm.sendNext("You've defeated #b#o9300012#!#k Magnificent! Thanks to you, the Dimensional Schism has been safely closed. I will now help you leave this place.");
			} else {
				cm.sendNext("Now that you've come this far, it's time to defeat the one responsible for this mess, #b#o9300012#.#k I suggest you be careful, though, as he is not in a very good mood.\r\nIf you and your party members defeat him, the Dimensional Schism will close forever. I'm counting on you!");
				cm.dispose();
			}
		}
	} else if (status == 1) {
		clear(9, eim, cm);
		cm.removeAll(4001023);
		var players = eim.getPlayers();
		cm.givePartyExp_PQ(70, 1.0, players);
		eim.setProperty("cleared", "true"); //set determine
		eim.restartEventTimer(60000);
		cm.warpParty(922011100, 0);
		cm.dispose();
	}
}

function clear(stage, eim) {
    eim.setProperty("stage" + stage.toString() + "status", "clear");
    cm.showEffect(true, "quest/party/clear");
    cm.playSound(true, "Party1/Clear");
}