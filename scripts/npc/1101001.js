/**
 * @author: Eric
 * @npc Cygnus
 * @func: Ultimate Adventurer Creation
*/

var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    (mode == 1 ? status++ : mode == 0 ? status-- : cm.dispose());
    if (status == 0) {
		if ((cm.getPlayer().getJob() >= 1112 && cm.getPlayer().getJob() <= 1512) && cm.getPlayer().haveItem(4032861, 5)) {
			if (cm.getPlayer().getLevel() >= 120) {
				cm.sendSimple("As a #bCygnus Knight#k you're blessed to make a #rUltimate Adventurer#k..");
			} else {
				cm.sendOk("You have to be level 120+ to do this.");
				cm.dispose();
			}
		} else if (cm.getPlayer().getJob() >= 0) {
			cm.sendOk("Don't stop training. Every ounce of your energy is required to protect the world of Maple...");
			cm.dispose();
		}
    } else if (status == 1) {
        if (selection == 100) {
			cm.sendOk("You have to be level 120+ for this.");
			cm.dispose();
		} else {
		    cm.gainItem(4032861, -5);
			cm.sendUltimateExplorer();
			cm.dispose();
		}
    }
}