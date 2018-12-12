/**
 * @author: Eric
 * @npc: So Gong
 * @map: Mu Lung Training Center
 * @desc : Training Center Start
*/
// TODO: Fix the "Time:" in Resting Spot, and reset the players' time at midnight. (Make global script?? idk)

var status = -1;
var sel;

function start() {
    if (cm.getMapId() == 925020001) {
		cm.sendSimple("My master is the strongest person in Mu Lung, and YOU wish to challenge HIM? Just don't regret it later.\r\n#b#L0#I'll challenge myself to Mu Lung Dojo.#l\r\n#L2#I want to receive Mu Gong's Belt.\r\n#L3#I want to see what rewards I can get from Mu Lung Dojo.\r\n#L4#What's Mu Lung Dojo?\r\n#L5#I want to check how many more times I can do the challenge today.\r\n#L6#I want to know my Hard Mode points and grade.#l#k\r\n\r\n#L7##rDevelopment's Mu Lung Dojo Specials#k");
    } else if (isRestingSpot(cm.getMapId())) {
		var dojoTime = Packages.tools.StringUtil.getReadableMillis(cm.getPlayer().dojoStartTime, cm.getPlayer().dojoMapEndTime);
		cm.sendSimple("I'm surprised you made it this far! But it won't be easy from here on out. You still want the challenge?\r\n#r[Time : " + dojoTime + " ]#k\r\n#b#L0#I want recovery and buff effects.\r\n#L1#I want to continue.\r\n#L2#I want to leave.");
    } else {
		cm.sendYesNo("What? You're ready to quit already? You just need to move on to the next level. Are you sure you want to quit?");
    }
}

function action(mode, type, selection) {
    if (cm.getMapId() == 925020001) {
		(mode == 1 ? status++ : cm.dispose());
		if (status == 0) {
			sel = selection;
			if (selection == 0) {
				if (cm.getParty() != null) {
					cm.sendOk("You can't enter in a party! Stand on your own strength!");
					cm.dispose();
					return;
				}
				status = 4;
				cm.sendSimple("You can take on the dojo on three different difficulties: #bNormal, Hard, and Ranked#k. That way, even shrimps like you can participate. You've gotta be #rLV. 120#k for Normal Mode and for Hard Mode, and #rLV. 130#k for the Ranking Mode. How tough are you feeling?\r\n\r\n#L1##bI'm pretty Normal, let's try that!\r\n#L2#I haven't gotten beaten up lately. Hard mode!#k\r\n#L3##rI want to get Ranked!#k");
			} else if (selection == 2) {
				cm.sendYesNo("I'll give you a #bbelt#k if you have some #i4001620:# #bMu Gong's Emblem#k.\r\nThis belt will disappear after a week, though, so collect those Mu Gong's Emblems again if you want another.");
			} else if (selection == 3) {
				status = 5;
				cm.sendSimple("You can collect Mu Gong's Emblems on any difficulty level, though you'll get more in #bHard Mode#k and #bRanked Mode#k. Once you have enough Mu Gong's Emblems, you can exchange them for one of #bMu Gong's Belts.\r\n\r\n#L0#What types of Mu Gong's Belts are there?\r\n#L1#What rewards can be earned in Hard Mode and how do I earn them?\r\n#L2#What rewards can be earned in Ranked Mode and how do I earn them?");
			} else if (selection == 4) {
				cm.sendNext("Our master is the strongest person in Mu Lung. The place he built is called the Mu Lung Dojo, a building that is 47 stories tall! You can train yourself as you go up each level. Of course, it'll be hard for someone at your level to reach the top.");
				cm.dispose();
			} else if (selection == 5) { // TODO
				var dojoData_Regular = cm.getQuestRecord(150136);
				var dojo_Regular = dojoData_Regular.getCustomData();
				if (dojo_Regular == null) {
					dojoData_Regular.setCustomData("3");
					dojo_Regular = "3";
				}
				var dojoData_Ranked = cm.getQuestRecord(150137);
				var dojo_Ranked = dojoData_Ranked.getCustomData();
				if (dojo_Ranked == null) {
					dojoData_Ranked.setCustomData("3");
					dojo_Ranked = "3";
				}
				cm.sendNext("You can do the challenge  " + parseInt(dojo_Regular) + "more time(s) today. [Normal, Hard], " + parseInt(dojo_Ranked) + "more time(s) today.[Ranking]");
				cm.dispose();
			} else if (selection == 6) { // TODO
				cm.sendOk("Yikes, you currently only have #b" + cm.getDojoPoints() + "#k points. That's not even enough to reach Grade B. To get to Grade B, you need 99600 more points. Work HARDER. Pretend you're ME!");
				cm.dispose();
			} else if (selection == 7) {
				status = 6;
				cm.sendSimple("#e<Development's Specials>#n\r\n#r - Items that have a 7 Day (or any day) Duration last #eforever#n.\r\n - #eParty Mode#n for Normal and Hard Mode has been brought back!\r\n - Monsters have been #ebuffed#n to nearly #e15x their original hp#n!\r\n - The #eWhite Belt#n has been brought back and is given for free.#k\r\n\r\n#e<Access Party Mode>#n\r\n#L1##bMy party and I will challenge ourselves to Mu Lung Dojo.#l#k\r\n\r\n\r\n#e<Receive a White Belt>#n\r\n#L2##bMay I have a #z1132000#?#k");
			}
		} else if (status == 1) {
			if (mode > 0) {
				cm.sendSimple("Which belt do you want?\r\n#L0##i1132000:# #b#t1132000##k #r(5 Mu Gong's Emblems required)#k#l\r\n#L1##i1132001:# #b#t1132001##k #r(25 Mu Gong's Emblems required)#k#l\r\n#L2##i1132002:# #b#t1132002##k #r(50 Mu Gong's Emblems required)#k#l\r\n#L3##i1132003:# #b#t1132003##k #r(100 Mu Gong's Emblems required)#k#l\r\n#L4##i1132004:# #b#t1132004##k #r(125 Mu Gong's Emblems required)#k#l");
			}
		} else if (status == 2) {
			var required = 0;
			switch (selection) {
				case 0:
					required = 5;
					break;
				case 1:
					required = 25;
					break;
				case 2:
					required = 50;
					break;
				case 3:
					required = 100;
					break;
				case 4:
					required = 125;
					break;
			}
			if (cm.haveItem(4001620, required)) {
				var item = 1132000 + selection;
				if (cm.canHold(item)) {
					cm.gainItem(item, 1);
					cm.gainItem(4001620, required);
				} else {
					cm.sendOk("Please check if you have any available slot in your inventory.");
				}
			} else {
				cm.sendOk("You either already have it or insufficient training points. Do try getting the weaker belts first.");
			}
			cm.dispose();
		} else if (status == 5) {
			if (selection == 0) {
				cm.setDojoMode(0); // Easy
				cm.start_DojoAgent(true, false);
				var dojoData_Regular = cm.getQuestRecord(150136);
				var dojo_Regular = dojoData_Regular.getCustomData();
				dojoData_Regular.setCustomData("" + (parseInt(dojo_Regular) - 1) + "");
				cm.dispose();
			} else if (selection == 1) {
				cm.setDojoMode(1); // Normal
				cm.start_DojoAgent(true, false);
				var dojoData_Regular = cm.getQuestRecord(150136);
				var dojo_Regular = dojoData_Regular.getCustomData();
				dojoData_Regular.setCustomData("" + (parseInt(dojo_Regular) - 1) + "");
				cm.dispose();
			} else if (selection == 2) {
				cm.setDojoMode(2); // Hard
				cm.start_DojoAgent(true, false);
				var dojoData_Regular = cm.getQuestRecord(150136);
				var dojo_Regular = dojoData_Regular.getCustomData();
				dojoData_Regular.setCustomData("" + (parseInt(dojo_Regular) - 1) + "");
				cm.dispose();
			} else if (selection == 3) {
				cm.setDojoMode(3); // Ranked
				cm.start_DojoAgent(true, false);
				var dojoData_Ranked = cm.getQuestRecord(150137);
				var dojo_Ranked = dojoData_Ranked.getCustomData();
				dojoData_Ranked.setCustomData("" + (parseInt(dojo_Ranked) - 1) + "");
				cm.dispose();
			}
		} else if (status == 6) {
			switch(selection) {
				case 0:
					cm.sendNext("Collect #i4001620:# Mu Gong's Emblems in the dojo to exchange for #bMu Gong's Belts#k. Use the #bBelt Exclusive Scrolls#k that drop once in a while in the dojo to upgrade the belts.\r\n#e<Mu Gong's Emblem Rewards: 15 Day Duration>#n\r\n#i1132112:# #bMu Gong's Yellow Belt#k #r(Mu Gong's Emblem x 25 required)#k\r\n#i1132113:# #bMu Gong's Blue Belt#k #r(Mu Gong's Emblem x 50 required)#k\r\n#i1132114:# #bMu Gong's Red Belt#k #r(Mu Gong's Emblem x 100 required)#k\r\n#i1132115:# #bMu Gong's Black Belt#k #r(Mu Gong's Emblem x125 required)#k");
					break;
				case 1:
					cm.sendNext("You'll be rewarded points based on how well you did in Hard Mode. Points add up each week to put you in a certain Grade.\r\nYou can then get rewards based on what Grade you achieved. You'll have to work REALLY hard if you want to achieve anything with muscles like that...\r\n#e<Hard Mode Rewards>#n\r\n#e#bGrade SS: #i1022135:# So Gong's Panda Ornament#k #r(Duration: 7 Days)#k\r\n#bGrade S: #i1022136:# Modest Panda Ornament#k #r(Duration: 7 Days)#k\r\n#bGrade A: #i2022957:# Mu Gong's Elixir x3#k #r(Duration: 7 Days)#k\r\n#bGrade B: #i2022457:# x10#k #r(Duration: 7 Days)#k#n");
					break;
				case 2:
					cm.sendNext("To earn a reward in Ranked Mode, you have to be ranked one of the top 50 players! You'll be competing for the best time against other players for these rewards. Think YOU've got what it takes? Puh-lease! But you should still try.\r\n#e< Ranked Mode Rewards: 7 Day Duration>#n\r\n#i1082392:# #bHero's Gloves#k #r(Rank 1)#k\r\n#i1082393:# #bMu Gong's Gloves#k #r(Ranks 2-10)#k\r\n#i1082394:# #bSo Gong's Gloves#k #r(Ranks 11-50)#k");
					break;
			}
			cm.dispose();
		} else if (status == 7) {
			if (selection == 1) {
				if (cm.getParty() != null && cm.isLeader()) {
					cm.sendSimple("You can take on the dojo on three different difficulties: #bNormal, Hard, and Ranked#k. That way, even shrimps like you can participate. Your party members gotta be #rLV. 120#k for Normal Mode and Hard Mode. How tough are you feeling?\r\n\r\n#L1##bWe're pretty Normal, let's try that!\r\n#L2#We haven't gotten beaten up lately. Hard mode!#k");
				} else {
					cm.sendOk("Hey, you're not even a leader of your party. What are you doing trying to sneak in? Tell your party leader to talk to me if you want to enter the premise...");
					cm.dispose();
				}
			} else if (selection == 2) {
				if (!cm.getPlayer().haveItem(1132000) && !cm.getPlayer().hasEquipped(1132000)) {
					cm.gainItem(1132000, 1);
					cm.dispose();
				} else {
					cm.sendOk("You already have a #eWhite Belt#n!");
					cm.dispose();
				}
			}
		} else if (status == 8) {
			if (selection == 0) {
				cm.setDojoMode(0); // Easy
				cm.start_DojoAgent(true, true);
				cm.dispose();
			} else if (selection == 1) {
				cm.setDojoMode(1); // Normal
				cm.start_DojoAgent(true, true);
				for (var i = 0; i < cm.getPlayer().getParty().getMembers().size(); i++) {
					var partychrz = cm.getPlayer().getClient().getChannelServer().getPlayerStorage().getCharacterByName(cm.getPlayer().getParty().getMemberByIndex(i).getName());
					var dojoData_Regular = partychrz.getQuestNAdd(Packages.server.quest.MapleQuest.getInstance(150136));
					var dojo_Regular = dojoData_Regular.getCustomData();
					dojoData_Regular.setCustomData("" + (parseInt(dojo_Regular) - 1) + "");
				}
				cm.dispose();
			} else if (selection == 2) {
				cm.setDojoMode(2); // Hard
				cm.start_DojoAgent(true, true);
				for (var i = 0; i < cm.getPlayer().getParty().getMembers().size(); i++) {
					var partychrz = cm.getPlayer().getClient().getChannelServer().getPlayerStorage().getCharacterByName(cm.getPlayer().getParty().getMemberByIndex(i).getName());
					var dojoData_Regular = partychrz.getQuestNAdd(Packages.server.quest.MapleQuest.getInstance(150136));
					var dojo_Regular = dojoData_Regular.getCustomData();
					dojoData_Regular.setCustomData("" + (parseInt(dojo_Regular) - 1) + "");
				}
				cm.dispose();
			}
		}
	} else if (isRestingSpot(cm.getMapId())) {
		(mode == 1 ? status++ : cm.dispose());
		if (status == 0) {
			if (selection == 0) {
				status = 4;
				potions = [[2022855, "50% HP Recovery"], [2022856, "100% HP Recovery"], [2022857, "MaxHP + 10000 (Duration: 10 Minutes)"], [2022858, "Weapon/Magic ATT + 30 (Duration: 10 Minutes)"], [2022859, "Weapon/Magic ATT + 60 (Duration: 10 Minutes)"], [2022860, "Weapon/Magic DEF + 2500 (Duration: 10 Minutes)"], [2022861, "Weapon/Magic DEF + 4000 (Duration: 10 Minutes)"], [2022862, "Accuracy/Avoidability + 2000 (Duration: 10 Minutes)"], [2022863, "Speed/Jump Max (Duration: 10 Minutes)"], [2022864, "Attack Speed + 1 (Duration: 10 Minutes)"]];
				var text = "#0# 50% HP Recovery";
				for (var i = 1; i < potions.length; text += "#" + i + "# " + potions[i][1] + "", i++);
				cm.askBuffSelection(text);
			} else if (selection == 1) {
				if (cm.getParty() == null || cm.isLeader()) {
					cm.dojoAgent_NextMap(true, true);
				} else {
					cm.sendOk("Only the party leader may continue.");
				}
				cm.dispose();
			} else if (selection == 2) {
				cm.askAcceptDecline("Do you want to quit? You really want to leave here?");
			}
		} else if (status == 1) {
			if (cm.isLeader()) {
				cm.warpParty(925020002);
			} else {
				cm.warp(925020002);
			}
			cm.dispose();
		} else if (status == 4) { // used for -1 mode on buff/recovery window 2lazy2fix
			cm.dispose();
		} else if (status == 5) {
			sel = selection;
			cm.sendYesNo("Use #i" + potions[selection][0] + "# " + potions[selection][1] + "? You can only select one item per Rest Stage, so choose wisely.");
		} else if (status == 6) {
			cm.useItem(potions[sel][0]);
			cm.dispose();
		}
    } else {
		if (mode == 1) {
			if (cm.isLeader()) {
				cm.warpParty(925020002);
			} else {
				cm.warp(925020002);
			}
		}
		cm.dispose();
    }
}

function getRestingFieldID(id) {
	var idd = 925020002;
    switch (id) {
		case 1:
			idd = 925020600;
			break;
		case 2:
			idd = 925021200;
			break;
		case 3:
			idd = 925021800;
			break;
		case 4:
			idd = 925022400;
			break;
		case 5:
			idd = 925023000;
			break;
		case 6:
			idd = 925023600;
			break;
    }
    for (var i = 0; i < 10; i++) {
		var canenterr = true;
		for (var x = 1; x < 39; x++) {
			var map = cm.getMap(925020000 + 100 * x + i);
			if (map.getCharactersSize() > 0) {
				canenterr = false;
				break;
			}
		}
		if (canenterr) {
			idd += i;
			break;
		}
	}
	return idd;
}

function getStageId(mapid) {
    if (mapid >= 925020600 && mapid <= 925020614) {
		return 1;
    } else if (mapid >= 925021200 && mapid <= 925021214) {
		return 2;
    } else if (mapid >= 925021800 && mapid <= 925021814) {
		return 3;
    } else if (mapid >= 925022400 && mapid <= 925022414) {
		return 4;
    } else if (mapid >= 925023000 && mapid <= 925023014) {
		return 5;
    } else if (mapid >= 925023600 && mapid <= 925023614) {
		return 6;
    }
    return 0;
}

function isRestingSpot(id) {
    return (getStageId(id) > 0);
}
