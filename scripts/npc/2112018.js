/**
 * @author: Eric
 * @npc: Yulete
 * @func: Romeo and Juliet GMS-like PQ
*/
var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	(mode == 1 ? status++ : mode == 0 ? status-- : cm.dispose());
	if (status == 0) {
		cm.sendNext("Again, thank you so much for helping us out. Magatia may still be on the threat of danger, but I think this is enough to snuff out the big fire for now.");
	} else if (status == 1) {
		cm.sendNextPrev("Eventhough our love is still littered with obstacles, I can promise you that I will not give up in my quest to be with Romeo until the end.");
	} else if (status == 2) {
		cm.sendNextPrev("Here's the Alcadno Marble that I have had for the longest time. Please take it. I have also given you some rewards for the job well done. I will now lead your way out of here.");
	} else if (status == 3) {
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
		cm.addTrait("will", 1); // todo: randomize
		cm.addTrait("sense", 1); // todo: randomize
		cm.dispose();
	}
}