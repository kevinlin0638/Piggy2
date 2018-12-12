/**
 * @author: Eric
 * @npc: Cesar
 * @func: Ariant PQ
*/

var status = 0;
var sel;
var empty = [false, false, false];
var closed = false;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection){
    (mode == 1 ? status++ : status--);
    if (status == 0) {
		cm.sendSimple("#e<Competition: Ariant Coliseum>#n\r\nWelcome to the Ariant Coliseum where you can compete against the other warriors and show your skills.#b\r\n#L0#Request to enter the [Ariant Coliseum].\r\n#L1#Explanation on the [Ariant Coliseum]\r\n#L2#[Ariant Coliseum] Evaluation Standard\r\n#L3#Check today's remaining challenge count.\r\n#L4#Receive the Ariant Coliseum reward.");
	} else if (status == 1) {
		if (selection == 0) {
			if (closed || (cm.getPlayer().getLevel() < 50 && !cm.getPlayer().isGM())) {
				cm.sendOk(closed ? "Ariant Coliseum is getting a good hosing down right now. Please come back later." : "You're not between level 50 and 200. Sorry, you may not participate.");
				cm.dispose();
				return;
			}
			var text = "What do you want?#b";
			for(var i = 0; i < 3; i += 1)
				if (cm.getPlayerCount(980010100 + (i * 100)) > 0)
					if (cm.getPlayerCount(980010101 + (i * 100)) > 0)
						continue;
					else
						text += "\r\n#L" + i + "# Battle Arena " + (i + 1) + " (" + cm.getPlayerCount(980010100 + (i * 100)) + "/" + cm.getPlayer().getAriantSlotsRoom(i) + " Users. Leader: " + cm.getPlayer().getAriantRoomLeaderName(i) + ")#l";
				else {
					empty[i] = true;
					text += "\r\n#L" + i + "# Battle Arena " + (i + 1) + " (Empty)#l";
					if (cm.getPlayer().getAriantRoomLeaderName(i) != "")
						cm.getPlayer().removeAriantRoom(i);
				}
			cm.sendSimple(text);
		} else if (selection == 1) {
			cm.sendNext("Ariant Coliseum is a fierce battlefield where true fighters will be sorted out! Don't you even lay your eyes on it if you're a coward! An explorer who makes Areda's favorite jewel the most will be selected as the best fighter! Simple, huh?\r\n - #eLevel#n : 50 or above#r(Recommended Level : 50 - 80 )#k\r\n - #eTime Limit#n : 8 minutes\r\n - #ePlayers#n : 2-6\r\n - #eItem Acqusition#n :\r\n#i1113048:# Champion Ring");
			cm.dispose();
		} else if (selection == 2) {
			status = 9;
			cm.sendNext("Do you want to know how #rexceptional Champions#k get #bsorted out#k? How ambitious! Great, I will explain it to you.");
		} else if (selection == 3) {
			var ariant = cm.getQuestRecord(150139);
			var data = ariant.getCustomData();
			if (data == null) {
				ariant.setCustomData("10");
				data = "10";
			}
			cm.sendNext("#r#h ##k, you can participate in the Ariant Coliseum #b" + parseInt(data) + "#k time(s) today.");
			cm.dispose();
		} else if (selection == 4) {
			status = 4;
			cm.sendNext("Show what you've got at the Ariant Coliseum! If your coliseum score is higher than 150, you will get #i1113048:# #bChampion Ring#k.\r\nThat's the symbol of the true fighter.");
		}
	} else if (status == 2) {
		var sel = selection;
		if(cm.getPlayer().getAriantRoomLeaderName(sel) != "" && empty[sel])
            empty[sel] = false;
        else if(cm.getPlayer().getAriantRoomLeaderName(sel) != "") {
			cm.warp(980010100 + (sel * 100));
            cm.dispose();
            return;
        }
        if (!empty[sel]) {
            cm.sendNext("Another combatant has created the battle arena first. I advise you to either set up a new one, or join the battle arena that's already been set up.");
            cm.dispose();
            return;
        }
		cm.getPlayer().setApprentice(sel);
        cm.sendGetNumber("Up to how many participants can join in this match? (2~6 ppl)", 0, 2, 6);
	} else if (status == 3) {
		var sel = cm.getPlayer().getApprentice(); // how 2 final in javascript.. const doesn't work for shit
		if (cm.getPlayer().getAriantRoomLeaderName(sel) != "" && empty[sel])
			empty[sel] = false;
        if (!empty[sel]) {
            cm.sendNext("Another combatant has created the battle arena first. I advise you to either set up a new one, or join the battle arena that's already been set up.");
            cm.dispose();
            return;
        }
        cm.getPlayer().setAriantRoomLeader(sel, cm.getPlayer().getName());
        cm.getPlayer().setAriantSlotRoom(sel, selection);
        cm.warp(980010100 + (sel * 100));
		cm.getPlayer().setApprentice(0);
        cm.dispose();
	} else if (status == 5) {
		cm.sendNextPrev("The problem is that your coliseum score is only #b0#k. You must score higher than #b150#k to get #bChampion Ring#k. Score high enough to prove that you're qualified to get this goodie.");
	} else if (status == 6) { // todo: code champion rings :c
		cm.dispose();
	} else if (status == 10) {
		cm.sendNextPrev("Let me tell you the easiest rule first. A Champion who makes the greatest number of #bSpirit Jewel#ks will be elected as the best Champion. Of course, you will receive even higher praises if you win a match where you have to compete against #bnumerous Champions#k.\r\n\r\n(#bWhen the match ends, your ranking will be determined by the number of Spirit Jewels you have. Also, your will receive more rewards if more participants remain.)#k");
	} else if (status == 11) {
		cm.sendNextPrev("Don't worry even if you aren't strong enough. If you can make #bat least 15#k Spirit Jewel items, no one will dare deny the fact that you're a great warrior.\r\n\r\n(If you make #bat least 15 Spirit Jewel items, you will receive average rewards.)#k");
	} else if (status == 12) {
		cm.sendNextPrev("What if you make more than 15? Of course, we will award more rewards to such exceptional Champions! That doesn't mean you'll get #runlimited amount of rewards#k, though. If you make #b30#k gems, you will receive the #rbest reward#k.\r\n\r\n(Make #b30 Spirit Jewel items to receive top quality rewards.)#k");
	} else if (status == 13) {
		cm.sendNextPrev("Does that mean you won't receive any reward if you don't make at least 15 gems? No, that's not going to be the case! Our beautiful Queen Areda commanded us to give some rewards to #bChampions who even fail to make at least 15#k gems. In this case, you'll receive #rfewer rewards#k. Got any complaints? If you don't like it, train your skills and perform well during a Coliseum match!\r\n\r\n(If you make #bless than 15 Spirit Jewel items, you will receive low quality rewards.)#k");
	} else if (status == 14) {
		cm.sendNextPrev("Of course, a notoriously bad Champion doesn't deserve to be treated as well as others. If even #b6 Spirit Jewel items#k are too much for #ryou to make#k, then that simply means you aren't up to par. Anyway, you will receive scarcely #rany rewards#k for fighting in a match. So, try to get at least 6 or more gems.\r\n\r\n(If you make #b5 or less Spirit Jewel items, you will receive scarcely any reward.)#k");
	} else if (status == 15) {
		cm.sendNextPrev("Lastly, #rcowards#k and the Champions who couldn't complete the mission within the #btime limit#k will receive some rewards based on #rthe elapsed time#k.\r\n\r\n(#bIf the coliseum gets stopped in the middle of it, a reward will be given based on the elapsed time.)#k");
		cm.dispose();
	}
}
