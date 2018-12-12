/**
 * @author: Eric
 * @npc: Charles
 * @func: Guy Fawkes (MV) Full PQ System
*/
var status = 0;
var quest = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	(mode == 1 ? status++ : mode == 0 ? status-- : cm.dispose());
	if (status == 0) {
		if (cm.getPlayer().getParty() != null) {
			cm.sendSimple("Help out a fellow hero!#b\r\n\r\n#L1#I would like to be sent where I can collect MV's secret letters.");
		} else {
			cm.sendOk("Whoa..Are you crazy?! You think you can defeat #eMV#n without your fellow #bParty Members#k??");
			cm.dispose();
		}
	} else if (status == 1) {
		if (selection == 1) {
		if (!cm.isLeader()) { cm.sendOk("Please tell your #eParty Leader#n to talk to me!"); cm.dispose(); return; } // yes this looks neater to me..
			if (cm.haveItem(4032248, 1)) {
				cm.sendNext("Now I can send you to the place where MV is lurking in.");
			} else { // quest text unknown from gms..need to find this
				cm.sendNext("It seems you have to complete a Quest before fighting MV.\r\nWell? What are you waiting for? Click next, and only fear will await you.");
				quest = 1;
			}
		}
	} else if (status == 2) {
		if (quest == 1) { // quest text unknown from gms.. need to find it.
			cm.sendNext("Ah, click next to get on your journey then!"); //I'm still working on matching GMS's text! Click next to start the quest!");
		} else {
			cm.sendYesNo("Is your party ready to enter the vanue? Do you want to enter now?");
		}
	} else if (status == 3) {
		if (quest == 1) {
			var party = cm.getPlayer().getParty().getMembers();
			var mapId = cm.getPlayer().getMapId();
			var next = true;
			var size = 0;
			var it = party.iterator();
			while (it.hasNext()) {
				var cPlayer = it.next();
				var ccPlayer = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
				if (ccPlayer == null || ccPlayer.getLevel() < 10) {
					next = false;
					break;
				}
				size += (ccPlayer.isGM() ? 2 : 1);
			}	
			if (next && size >= 2) {
				var em = cm.getEventManager("MV");
				if (em == null) {
					cm.sendOk("Please contact a #rGame Master#k about a #enull Event Instance#n.");
					cm.dispose();
				} else {
					var prop = em.getProperty("state");
					if (prop.equals("0") || prop == null) {
						em.startInstance(cm.getPlayer().getParty(), cm.getPlayer().getMap());
					} else {
						cm.sendNext("Another band of heroes is hunting for the #bSecret Letter's#k, please wait until they finish. Thank you.");
						cm.dispose();
					}
				}
			} else {
				cm.sendOk("All 2+ members of your party must be here and above level 10.");
				cm.dispose();
			}
		} else {
			var party = cm.getPlayer().getParty().getMembers();
			var mapId = cm.getPlayer().getMapId();
			var next = true;
			var size = 0;
			var it = party.iterator();
			while (it.hasNext()) {
				var cPlayer = it.next();
				var ccPlayer = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
				if (ccPlayer == null || ccPlayer.getLevel() < 10) {
					next = false;
					break;
				}
				size += (ccPlayer.isGM() ? 2 : 1);
			}	
			if (next && size >= 2) {
				var em = cm.getEventManager("MVBattle");
				if (em == null) {
					cm.sendOk("Please contact a #rGame Master#k about a #enull Event Instance#n.");
					cm.dispose();
				} else {
					var prop = em.getProperty("state");
					if (prop.equals("0") || prop == null) {
						cm.removeAll(4032248); //map to mv's lair
						em.startInstance(cm.getPlayer().getParty(), cm.getPlayer().getMap());
					} else {
						cm.sendNext("Another band of heroes is battling MV, please wait until they finish. Thank you.");
						cm.dispose();
					}
				}
			} else {
				cm.sendOk("All 2+ members of your party must be here and above level 10.");
				cm.dispose();
			}
		}
	}
}