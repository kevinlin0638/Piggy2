/**
 * @author: Eric
 * @npc: Juliet
 * @func: Romeo and Juliet GMS-like PQ
*/
var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 0 && status == 99) {
		cm.sendNext("Send an invite to friends nearby. Remember that using the Party Search function (Hotkey O) will allow you to find a party anytime, anywhere.");
		cm.dispose();
		return;
	} else {
		(mode == 1 ? status++ : mode == 0 ? status-- : cm.dispose());
	}
	var em = cm.getEventManager("Juliet");
	if (status == 0) {
		if (cm.getPlayer().getMapId() == 261000021) {
			cm.sendSimple("#eParty Quest: Romeo and Juliet>#n\r\nMagatia faces a grave threat. We need brave adventurers to help us.#b\r\n#L0#Listen to Juliet's story.\r\n#L1#Start the quest.\r\n#L2#Find a party.\r\n#L3#Make a necklace with Alcadno Marbles.\r\n#L4#Combine two necklaces into one.#k"); //#L5#Check the number of tries left for today.
		} else {
			switch(cm.getPlayer().getMapId()) {
				case 926110000:
				case 926110001:
				case 926110100:
				case 926110300:
				case 926110400:
					status = 16;
					cm.sendSimple("How can I help you?#b\r\n#L0#Where am I?\r\n#L1#I want to get out of here!");
					break;
				case 926110200:
					if (cm.haveItem(4001131,1)) {
						cm.sendOk("Oh, the Letter I wrote! Thank you!"); // TODO.. :(
						cm.gainItem(4001131,-1);
						em.setProperty("stage", "1");
						cm.dispose();
					} else if (cm.haveItem(4001134, 1)) {
						status = 19;
						cm.sendSimple("Hey, isn't that #bAlcadno's Experiment Files?#k This should prove that the Zenumists are not responsible for stealing Alcadno's source of energy! Please give me that right now!\r\n\r\n#b#L0#Give the Alcadno's Experiment Files to Juliet.#l");
					} else if (cm.haveItem(4001135, 1) && em.getProperty("stage4").equals("1")) {
						status = 19;
						cm.sendSimple("Ah, aren't these #bZenumist's Experiment Files#k? With these, we can prove that Alcadno did not steal the Zenumist's source of energy! Please, give them to me!\r\n\r\n#b#L1#Give the Zenumist's Experiment Files to Juliet.#l");
					} else {
						status = 16;
						cm.sendSimple("How can I help you?#b\r\n#L0#Where am I?\r\n#L1#I want to get out of here!");
					}
					break;
				case 926110401:
					status = 24;
					cm.sendNext("Thank you so much for your help in saving Romeo. Thank you so, so much.");
					break;
				case 926110600:
					status = 29;
					cm.sendNext("Again, thank you so much for helping us out. Magatia may still be on the threat of danger, but I think this is enough to snuff out the big fire for now.");
					// cm.openNpc(2112018);
					break;
			}
		}
	} else if (status == 1) {
		if (selection == 0) {
			status = 10;
			cm.sendNext("Romeo and I are in love. But I am an Alcadno, and he is a Zenumist. There's no hope for us to be together...");
		} else if (selection == 1) {
			var items = [4001130, 4001131, 4001132, 4001133, 4001134, 4001135];
			for (var i = 0; i < items.length; i++) {
				cm.removeAll(items[i]);
			}
			if (em == null || !cm.getPlayer().isGM()) {
				cm.sendOk("Please try again later.");
				cm.dispose();
				return;
			}
			if (cm.getPlayer().getParty() == null || !cm.isLeader()) {
				cm.sendOk("The leader of the party must be here.");
			} else {
				var party = cm.getPlayer().getParty().getMembers();
				var mapId = cm.getPlayer().getMapId();
				var next = true;
				var size = 0;
				var it = party.iterator();
				while (it.hasNext()) {
					var cPlayer = it.next();
					var ccPlayer = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
					if (ccPlayer == null || ccPlayer.getLevel() < 70 || ccPlayer.getLevel() > 255) {
						next = false;
						cm.dispose();
					}
					size += (ccPlayer.isGM() ? 4 : 1);
				}	
				if (next && (cm.getPlayer().isGM() || size == 4)) {
					var prop = em.getProperty("state");
					if (prop.equals("0") || prop == null) {
						em.startInstance(cm.getPlayer().getParty(), cm.getPlayer().getMap(), cm.getPlayer().getAveragePartyLevel());
						cm.sendOk("This is the lab where rumors are abound that a suspicious noise can be heard from here every night. If there's anything hidden in here, it has to be in this place. Please look thoroughly into this lab.");
					} else {
						cm.sendOk("Another party quest has already entered on this channel.");
					}
				} else {
					status = 99;
					cm.sendYesNo("Your party does not meet the requirements. You need a party with exactly four members to participate in this quest. If you need to find people to party with, why not try using the Party Search function?");
				}
			}
		} else if (selection == 2) {
			cm.findParty();
			cm.dispose();
		} else if (selection == 3) {
			if (!cm.haveItem(4001160, 15)) {
				cm.sendNext("To make Juliet's Pendant, we need 15 Alcadno Marbles. You seem to be missing a few.");
				cm.dispose();
			} else {
				cm.sendOk("Eric is working on scripting the #eexact#n #bRomeo and Juliet PQ#k from #rGlobal MapleStory#k.\r\nBecause the GMS-like script has not been found, it is unfunctional.\r\n\r\nIf you have a screenshot or text that this window uses, please report this to our forums.");
				cm.dispose();
			}
		} else if (selection == 4) {
			if (cm.haveItem(1122116, 1) && cm.haveItem(1122117, 1)) {
				cm.sendOk("Eric is working on scripting the #eexact#n #bRomeo and Juliet PQ#k from #rGlobal MapleStory#k.\r\nBecause the GMS-like script has not been found, it is unfunctional.\r\n\r\nIf you have a screenshot or text that this window uses, please report this to our forums.");
				cm.dispose();
			} else {
				cm.sendNext("You need Romeo's Pendant and Juliet's Pendant to combine them.");
				cm.dispose();
			}
		}
	} else if (status == 11) {
		cm.sendNextPrev("The Alcadno and Zenumist were not always enemies! There must be a way to bring peace to our two sides!");
	} else if (status == 12) {
		cm.sendNextPrev("But in spite of everything I've tried, Magatia is #bon the verge of war#k. It's all because #bsomeone stole the power source of both Zenumist and Alcadno#k. And the two sides are blaming each other for it!");
	} else if (status == 13) {
		cm.sendNextPrev("I got a tip that the real thief is #ba third party.#k If we're ever going to have peace -- and love for me and Romeo -- we need to find #bthe third party#k and stop his evil plan!");
	} else if (status == 14) {
		cm.sendNextPrev("Fight for the peace of Magatia!\r\n - #eLevel#n: 70+ #r( Recommended: 70 - 99 )#k\r\n - #eTime Limit#n: 20 min\r\n - #ePlayers#n: 4\r\n - #eReward#n:\r\n#i1122117# Juliet's Pendant\r\n(Can be obtained from #bJuliet#k once you collect #r20#k #bAlcadno Marbles.#k)\r\n#i1122118# Symbol of Eternal Love\r\n(Can be traded for 1 #bRomeo's Pendant#k and 1 #bJuliet's Pendant#k)");
	} else if (status == 15) {
		cm.dispose();
	} else if (status == 17) {
		if (selection == 0) {
			switch(cm.getPlayer().getMapId()) {
				case 926110000:
					cm.sendNext("This is the lab where rumors are abound that a suspicious noise can be heard from here every night. If there's anything hidden in here, it has to be in this place. Please look thoroughly into this lab.");
					break;
				case 926110001:
					cm.sendNext("This is the tunnel that leads to the secret lab!! Once you go past this place, I'm sure you'll find SOMETHING inside!! You'll have to defeat these monsters in order to go through the tunnel.");
					break;
				case 926110100:
					cm.sendNext("It looks like you're inside the lab, and... something about the beaker on top of the desk seems awfully suspicious. It looks like the beaker can hold something inside. Find something in this lab that you can fill up the beaker with.");
					break;
				case 926110200:
					status = 17;
					cm.sendNext("I can't believe there's a place like this underneath Magatia!! This is most likely a secret lab of a scientist. I believe this person is responsible for stealing the energy sources of Alcadno and Zenumist and put Magatia in grave danger.");
					break;
				case 926110300:
					status = 18;
					cm.sendNext("This... is a secret security tunnel created to hide something very important in Magatia.\r\nOnly one person can enter one of these four tunnels at a time, and each room presents different platforms to step on.");
					break;
				case 926110400:
					cm.sendNext("Once you enter the tunnel at the end of this map, you'll enter the Center Lab. No one knows exactly what's inside there, so I suggest you brace yourself for the worst!!");
					break;
			}
			if (cm.getMapId() != 926110200 && cm.getMapId() != 926110300)
				cm.dispose();
		} else if (selection == 1) {
			cm.warp(261000021, 0);
			cm.dispose();
		}
	} else if (status == 18) {
		cm.sendNextPrev("First, in order to stop the war, we'll need to find a concrete evidence that this person is responsible for stealing the energy sources of Zenumist and Alcadno. There's got to be a #brecord of Alcadno and Zenumist#k somewhere in this lab, so please find that first.");
		cm.dispose();
	} else if (status == 19) {
		cm.sendNextPrev("But... if my instincts serve me right, there should be an easier way to pass this through your teamwork.");
		cm.dispose();
	} else if (status == 20) {
		if (selection == 0) {
			cm.sendOk("In order to stop the war, we still need to find a hard evidence that convinces the Zenumists that it's not Alcadno's fault. I'll leave the door open so please find a concrete evidence for us!");
			cm.gainItem(4001134, -1);
			em.setProperty("stage4", "1");
			cm.dispose();
		} else if (selection == 1) { // TODO: broadcast cm.sendOk to the party/map!
			cm.showEffect(true, "quest/party/clear");
			cm.showEffect(false, "quest/party/clear");
			cm.playSound(true, "Party1/Clear");
			cm.playSound(false, "Party1/Clear");
			cm.sendOk("Now that we've proven that the Zemunist and the Alcadno did not steal each other's energy sources, they have no real reason to fight. I think we're safe now. Thank you so much. I've opened the door to the next room, so please go find out who caused this mess.");
			cm.gainItem(4001135, -1);
			em.setProperty("stage4", "2");
			cm.getMap().getReactorByName("jnr3_out3").hitReactor(cm.getClient());
			cm.dispose();
		}
	} else if (status == 25) {
		cm.sendNextPrev("Unfortunately, Yulete got away from us, so this is not over yet. I doubt he is too far from here, so please find him right now!!");
	} else if (status == 26) {
		cm.warpParty(926110500);
		cm.dispose();
	} else if (status == 30) {
		cm.sendNextPrev("Eventhough our love is still littered with obstacles, I can promise you that I will not give up in my quest to be with Romeo until the end.");
	} else if (status == 31) {
		cm.sendNextPrev("Here's the Alcadno Marble that I have had for the longest time. Please take it. I have also given you some rewards for the job well done. I will now lead your way out of here.");
	} else if (status == 32) {
		var items = [4001130, 4001131, 4001132, 4001133, 4001134, 4001135];
		for (var i = 0; i < items.length; i++) {
			cm.removeAll(items[i]);
		}
		var em = cm.getEventManager("Juliet");
		if (em != null) {
			var itemid = cm.getMapId() == 926100600 ? 4001160 : 4001159;
			if (!cm.canHold(itemid, 1)) {
				cm.sendOk("Please make some space in your ETC inventory.");
				cm.dispose();
				return;
			}
			cm.gainItem(itemid, 1);
			if (em.getProperty("stage").equals("2")) {
				cm.gainExpR(140000); // TODO: calculate the exp gains after boss kill, not here.
			} else {
				cm.gainExpR(105000);
			}
		}
		cm.getPlayer().endPartyQuest(1205);
		cm.warp(926110700, 0);
		cm.addTrait("will", 5);
		cm.addTrait("sense", 10);
		cm.dispose();
	} else if (status == 100) {
		if (mode > 0) {
			cm.findParty();
		}
	}
}