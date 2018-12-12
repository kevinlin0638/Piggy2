/**
 * @author: Eric
 * @npc: Shammos
 * @func: Resurrection of the Hoblin King
*/
var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	(mode == 1 ? status++ : mode == 0 ? status-- : cm.dispose());
    if (status == 0) {
		cm.sendSimple("#e<Party Quest: Resurrection of the Hoblin King>#n\r\nWelcome, #b#h ##k. What brings you here?#b\r\n#L0#I want to go stop the resurrection of Rex the Hoblin King.\r\n#L1#I need an empty bowl to hold Ancient Glacial Water.\r\n#L2#I would like an explanation.\r\n#L3#I want to receive an item.\r\n#L4#View remaining attempts for today.");
    } else if (status == 1) {
		if (selection == 0) {
			if (cm.getPlayer().getParty() == null || !cm.isLeader()) {
				cm.sendOk("The leader of the party must be here.");
				cm.dispose();
			} else {
				status = 4;
				cm.sendNext("Okay, I'll explain the mission to you again. We are going to stop the resurrection of Rex the Hoblin King. On the way there, we'll be attacked by Rex's minions. Your #bmission is to protect me and make sure I safely reach the location where Rex is sealed.#k");
			}
		} else if (selection == 1) {
			status = 8;
			cm.sendNext("Ah, yes, preparing Ancient Glacial Water for me is a great idea. If I fall into danger while you are escorting me, just make me drink that. If I die, all your effort will be wasted.");
		} else if (selection == 2) {
			cm.sendOk("The Hoblin King, #r#o9300281##k, will rise any moment now! The Seal Stone that held #r#o9300281##k back is losing its power. Our only hope is to go to #r#o9300281##k, where #m9300281# is sealed and stop his return directly. I can guide you there, but you'll have to protect me.\r\n - #eLevel#n: 150 or above#r(Recommended Level: 150 - 200 )#k\r\n - #eTime Limit#n: 20 minutes\r\n - #ePlayers#n: 2 - 6\r\n - #eRewards#n:\r\n#i1032102:# Rex's Perfect Green Earrings\r\n#i1032103:# Rex's Perfect Red Earrings\r\n#i1032104:# Rex's Perfect Blue Earrings\r\n#i1902048:# Rex's Hyena");
			cm.dispose();
		} else if (selection == 3) {
			status = 9;
			cm.sendSimple("Which item do you want?#b\r\n#L0#1. #i1032102:# Rex's Perfect Green Earrings\r\n#L1#2. #i1032103:# Rex's Perfect Red Earrings\r\n#L2#3. #i1032104:# Rex's Perfect Blue Earrings\r\n#L3#4. #i1902048:# Rex's Hyena");
		} else if (selection == 4) {
			var hoblin = cm.getQuestRecord(150138);
			var data = hoblin.getCustomData();
			if (data == null) {
				hoblin.setCustomData("10");
				data = "10";
			}
			cm.sendOk("You can do this quest " + parseInt(data) + "more time(s) today."); // TODO: reset this after midnight :c
			cm.dispose();
		}
    } else if (status == 5) {
		cm.sendNextPrev("Currently working on matching GMS's text. Click next to enter!");
	} else if (status == 6) {
		if (cm.getPlayer().getParty() == null || !cm.isLeader()) {
			cm.sendOk("The leader of the party must be here.");
			cm.dispose();
			return;
		}
		cm.warpParty(921120000);
	} else if (status == 9) {
		if (!cm.haveItem(4032649, 1)) {
			cm.gainItem(4032649, 1);
		}
		cm.dispose();
	} else if (status == 10) {
		if (selection == 0) {
			if (cm.haveItem(4001530, 25)) {
				if (!cm.canHold(1032102,1)) {
					cm.sendOk("Please make sure you have room in your #eEquip#n tab.");
				} else {
					cm.gainItem(1032102, 1);
					cm.gainItem(4001530, -20);
				}
			} else {
				cm.sendNext("You need #bHobb Warrior Mark x25#k to receive #bRex's Perfect Green Earrings#k. You get the things from Rex, and we'll deal.");
			}
		} else if (selection == 1) {
			if (cm.haveItem(1032078, 1) && cm.haveItem(4001530, 25)) {
				if (!cm.canHold(1032103, 1)) {
					cm.sendOk("Please make sure you have room in your #eEquip#n tab.");
				} else {
					cm.gainItem(1032103, 1);
					cm.gainItem(4001530, -25);
				}
			} else {
				cm.sendNext("In order to get #bRex's Perfect Red Earrings#k, you need 25 #bHobb Warrior Masks#k and 1 #bRex's Red Earrings.#k Now hurry up and go get them.");
			}
		} else if (selection == 2) {
			if (cm.haveItem(4001530, 25)) {
				if (!cm.canHold(1032104, 1)) {
					cm.sendOk("Please make sure you have room in your #eEquip#n tab.");
				} else {
					cm.gainItem(1032104, 1);
					cm.gainItem(4001530, -25);
				}
			} else {
				cm.sendNext("In order to get #bRex's Perfect Blue Earrings#k, you need 25 #bHobb Warrior Marks.#k Now hurry up and go get them.");
			}
		} else if (selection == 3) {
			if (cm.haveItem(4001530, 300)) {
				if (!cm.canHold(1902048, 2)) {
					cm.sendOk("Please make sure you have room in your #eEquip#n tab.");
				} else {
					cm.gainItem(1902048, 1);
					cm.gainItem(1912041, 1);
					cm.gainItem(4001530, -300);
				}
			} else {
				cm.sendNext("In order to get Rex's Hyena, you need 300 #bHobb Warrior Marks#k.\r\nNow hurry up and go get them.");
			}
		}
		cm.dispose();
	}
}