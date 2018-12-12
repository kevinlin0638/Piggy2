/**
 * @author: Eric
 * @npc: Blue Balloon
 * @func: LudiPQ 5th (was 8th) stage NPC
 * @todo: Add more math questions
*/

var status = 0;
var stage8question = Array("9*9+100-143 = ?", "3*8+10 = ?", "400+140-72 = ?", "5*60+5*5 = ?");
var stage8answer = Array(Array(0, 0, 1, 0, 0, 0, 0, 1, 0), Array(0, 0, 1, 1, 0, 0, 0, 0, 0), Array(0, 0, 0, 1, 0, 1, 0, 1, 0), Array(0, 1, 1, 0, 1, 0, 0, 0, 0));

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
	(mode == 1 ? status++ : mode == 0 ? status-- : cm.dispose());
	var eim = cm.getEventInstance();
    var objset = [0, 0, 0, 0, 0, 0, 0, 0, 0];
	if (status == 0) {
		if (!cm.isLeader()) {
			cm.sendNext("In the fifth stage, you will find a number of platforms. Of these platforms, #b3 are connected to the portal that leads to the next stage. 3 members of your party must stand in the center of these 3 platforms.#k\r\nRemember, exactly 3 members must be on a platform. No more, no less. While they are on the platform, the party leader must #bclick on me to check whether the members have chosen the right platform.#k Good luck!");
			cm.dispose();
			return;
		}
	    if (eim.getProperty("leadereighthpreamble") == null) {
			cm.sendNext("In the fifth stage, you will find a number of platforms. Of these platforms, #b3 are connected to the portal that leads to the next stage. 3 members of your party must stand in the center of these 3 platforms.#k\r\nRemember, exactly 3 members must be on a platform. No more, no less. While they are on the platform, the party leader must #bclick on me to check whether the members have chosen the right platform.#k Good luck!");
	    } else { // otherwise, check for stage completed
			if (eim.getProperty("8stageclear") != null) {
				eim.setProperty("8stageclear", "true"); // Just to be sure
				cm.sendNext("Please hurry on to the next stage, the portal has opened!");
			} else {
				var totplayers = 0;
				for (i = 0; i < objset.length; i++) {
					var present = cm.getMap().getNumPlayersItemsInArea(i);
					if (present != 0) {
						objset[i] = objset[i] + 1;
						totplayers = totplayers + 1;
					}
				}
				if (totplayers == 2) {
					var combo = stage8answer[parseInt(eim.getProperty("stageeighthcombo"))];
					var testcombo = true;
					for (i = 0; i < objset.length; i++) {
						if (combo[i] != objset[i]){
							testcombo = false;
						}
					}
					if (testcombo) {
						clear(8, eim, cm);
						if (cm.getEventInstance().getProperty("s8start") != null) { // useless shit
							var starts4Time = cm.getEventInstance().getProperty("s8start");
							var nowTime = new Date().getTime();
							if ((nowTime - starts4Time) < 90000)
								cm.getEventInstance().setProperty("s8achievement", "true");
						}
						cm.dispose();
					} else {
						failstage(eim, cm);
						cm.dispose();
					}
				} else {
					cm.sendNext("The #r3 numbers corresponding to the answer of my question are the key to opening the portal to the next stage.\r\n" + stage8question[0] + "#k\r\nPlease find the correct answer.");
					cm.dispose();
				}
			}
	    }
	} else if (status == 1) {
		if (eim.getProperty("leadereighthpreamble") == null) {
			cm.sendNextPrev("The #r3 numbers corresponding to the answer of my question are the key to opening the portal to the next stage.\r\n" + stage8question[0] + "#k\r\nPlease find the correct answer.");
			eim.setProperty("leadereighthpreamble", "done");
			var answer = Math.floor(Math.random() * stage8answer.length);
			eim.setProperty("stageeighthcombo", answer);
			cm.getMap().startSimpleMapEffect(stage8question[answer], 5120018);
		} else 
			cm.dispose();
	}
}

function clear(stage, eim, cm) {
    eim.setProperty("8stageclear", "true");
    cm.showEffect(true, "quest/party/clear");
    cm.playSound(true, "Party1/Clear");
    cm.environmentChange(true, "gate");
    cm.givePartyExp(5040, eim.getPlayers());
}

function failstage(eim, cm) {
    cm.showEffect(true, "quest/party/wrong_kor");
    cm.playSound(true, "Party1/Failed");
}