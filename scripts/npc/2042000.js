/**
 * @author: Eric
 * @npc Spiegelmann
 * @func: Monster Carnival
*/

var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}


function action(mode, type, selection) {
    (mode == 1 ? status++ : cm.dispose());
    if (status == 0) {
		cm.sendSimple("#e<Competition: Monster Carnival>#n\r\nIf you're itching for some action, then the Monster Carnival is the place for you!#b\r\n\r\n#L0#I want to participate in the Monster Carnival.\r\n#L1#Tell me more about the Monster Carnival.\r\n#L2#I want to trade in my shiny Maple Coins.");
	} else if (status == 1) {
		if (selection == 0) {
			// gMS v1.51: cm.sendOk("The Monster Carnival's had to close its doors for a bit. Why don't you go find something else to entertain you for now?");
			var selStr = "Sign up for Monster Carnival!#b";
			var found = false;
			for (var i = 0; i < 6; i++){
				if (getCPQField(i+1) != "") {
					selStr += "\r\n#L" + i + "# " + getCPQField(i+1) + "#l";
					found = true;
				}
			}
			if (cm.getParty() == null) {
				cm.sendOk("You are not in a party.");
				cm.dispose();
			} else {
				if (cm.isLeader()) {
					if (found) {
						cm.sendSimple(selStr);
					} else {
						cm.sendOk("There are no rooms available at the moment.");
						cm.dispose();
					}
				} else {
					cm.sendOk("Please tell your party leader to speak with me.");
					cm.dispose();
				}
			}
		} else if (selection == 1) {
			status = 4;
			cm.sendNext("The #bMonster Carnival#k is that magical place where you team up with others to obliterate hordes of monsters faster than the other folks.");
		} else if (selection == 2) {
			if (!cm.getPlayer().haveItem(4001254)) {
				cm.sendOk("What? You don't even have a single Shiny Maple Coin! If you want #i1102556# Spiegelmann's Mighty Mustache or #i1012270# Spiegelmann's Cape of Moxy, #i1122162# Spiegelmann's Mighty Bow Tie, then bring me more #i4001254# #bShiny Maple Coin!#k");
				cm.dispose();
			} else {
				status = 2;
				cm.sendSimple("\r\n#b#L0#50 Maple Coin = Spiegelmann Necklace#l\r\n#L1#30 Maple Coin = Spiegelmann Marble#l\r\n#L2#50 Sparkling Maple Coin = Spiegelmann Necklace of Chaos#l#k");
			}
		}
    } else if (status == 2) {
		if (selection >= 0 && selection < 9) {
			var mapid = 980000000+((selection+1)*100);
			if (cm.getEventManager("cpq").getInstance("cpq"+mapid) == null) {
				if ((cm.getParty() != null && 1 < cm.getParty().getMembers().size() && cm.getParty().getMembers().size() < (selection == 4 || selection == 5 || selection == 8 ? 4 : 3)) || cm.getPlayer().isGM()) {
					if (checkLevelsAndMap(30, 255) == 1) {
						cm.sendOk("A player in your party is not the appropriate level.");
					} else if (checkLevelsAndMap(30, 255) == 2) {
						cm.sendOk("Everyone in your party isnt in this map.");
					} else {
						cm.getEventManager("cpq").startInstance("" + mapid, cm.getPlayer());
					}
				} else {
					cm.sendOk("Your party is not the appropriate size.");
				}
			} else if (cm.getParty() != null && cm.getEventManager("cpq").getInstance("cpq"+mapid).getPlayerCount() == cm.getParty().getMembers().size()) {
				if (checkLevelsAndMap(30, 255) == 1) {
					cm.sendOk("A player in your party is not the appropriate level.");
				} else if (checkLevelsAndMap(30, 255) == 2) {
					cm.sendOk("Everyone in your party isnt in this map.");
				} else {
					var owner = cm.getChannelServer().getPlayerStorage().getCharacterByName(cm.getEventManager("cpq").getInstance("cpq"+mapid).getPlayers().get(0).getParty().getLeader().getName());
					owner.addCarnivalRequest(cm.getCarnivalChallenge(cm.getChar()));
					cm.openNpc(owner.getClient(), 2042001);
					cm.sendOk("Your challenge has been sent.");
				}
			} else {
				cm.sendOk("The two parties participating in Monster Carnival must have an equal number of party member");
			}
			cm.dispose();
		}
	} else if (status == 3) {
	    if (selection == 0) {
			if (!cm.haveItem(4001129,50)) {
				cm.sendOk("You have no items.");
			} else if (!cm.canHold(1122007,1)) {
				cm.sendOk("Please make room");
			} else {
				cm.gainItem(1122007,1);
				cm.gainItem(4001129,-50);
			}
			cm.dispose();
	    } else if (selection == 1) {
			if (!cm.haveItem(4001129,30)) {
				cm.sendOk("You have no items.");
			} else if (!cm.canHold(2041211,1)) {
				cm.sendOk("Please make room");
			} else {
				cm.gainItem(2041211,1);
				cm.gainItem(4001129,-30);
			}
			cm.dispose();
	    } else if (selection == 2) {
			if (!cm.haveItem(4001254,50)) {
				cm.sendOk("You have no items.");
			} else if (!cm.canHold(1122058,1)) {
				cm.sendOk("Please make room");
			} else {
				cm.gainItem(1122058,1);
				cm.gainItem(4001254,-50);
			}
			cm.dispose();
	    }
	} else if (status == 5) {
		cm.sendNextPrev("Don't think you can do it alone? Worry not, my friend, I will enlist others to join you! All you have to tell me is, are you game? If you are, I'll give a holler when I have your group ready.\r\n - #eLevel#n: 110 - 130\r\n - #eRewards#n:\r\n#i1102556# Spiegelmann's Mighty Mustache\r\n#i1012270# Spiegelmann's Cape of Moxy\r\n#i1122162# Spiegelmann's Mighty Bow Tie");
		cm.dispose();
	}
}

function checkLevelsAndMap(lowestlevel, highestlevel) {
    var party = cm.getParty().getMembers();
    var mapId = cm.getMapId();
    var valid = 0;
    var inMap = 0;

    var it = party.iterator();
    while (it.hasNext()) {
        var cPlayer = it.next();
        if (!(cPlayer.getLevel() >= lowestlevel && cPlayer.getLevel() <= highestlevel) && cPlayer.getJobId() != 900) {
            valid = 1;
        }
        if (cPlayer.getMapid() != mapId) {
            valid = 2;
        }
    }
    return valid;
}

function getCPQField(fieldnumber) {
    var status = "";
    var event1 = cm.getEventManager("cpq");
    if (event1 != null) {
        var event = event1.getInstance("cpq"+(980000000+(fieldnumber*100)));
        if (event == null && fieldnumber != 4 && fieldnumber != 5 && fieldnumber != 6) {
            status = "Carnival Field "+fieldnumber+"(2~4ppl)";
        } else if (event == null) {
            status = "Carnival Field "+fieldnumber+"(3~6ppl)";
        } else if (event != null && (event.getProperty("started").equals("false"))) {
            var averagelevel = 0;
            for (i = 0; i < event.getPlayerCount(); i++) {
                averagelevel += event.getPlayers().get(i).getLevel();
            }
            averagelevel /= event.getPlayerCount();
            status = event.getPlayers().get(0).getParty().getLeader().getName()+"/"+event.getPlayerCount()+"users/Avg. Level "+averagelevel;
        }
    }
    return status;
}
