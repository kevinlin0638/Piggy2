/**
 * @author Alejandro (lol he forgets rebirths when this is the rebirthing npc)
 * @npc Mr. Moneybags
 * @function Rebirthing NPC
 * @rev: 2 - Added rebirth change rather then jobchange.
**/
var text = "What Job do you want to become?\r\n #L0#Explorer#l \r\n #L1#Dual Blade#l \r\n #L2#Cannoneer#l \r\n #L3#Jett#l \r\n #L4#Cygnus#l\r\n #L5#Aran#l \r\n #L6#Evan#l \r\n #L7#Mercedes#l \r\n #L8#Phantom#l \r\n #L9#Demon Slayer#l \r\n #L10#Battle Mage#l \r\n #L11#Wild Hunter#l \r\n #L12#Mechanic#l \r\n #L13#Mihile#l";

function start() {
	status = -1;
	action(1, 0, 0);
}

function action (m, t, s) {
	if (m == 0 && status >= 0)
		cm.dispose();
	if (m == 1)
		status++;
	else
		status--;
	  if (status == 0) {
			if (cm.getPlayer().getLevel() >= 200) {
				cm.sendSimple(text);
			} else {
				cm.sendOk("You may rebirth only once you are level 200."); // incase people think this is job adv
				cm.dispose();
			}
	} else if (status == 1) {
		switch(s) {
			case 0:
				cm.getPlayer().doEXPRB(); // Explorer
				break;
			case 1:
				cm.getPlayer().doDBRB(); // Dual Blade
				break;
			case 2:
				cm.getPlayer().doCANNONRB(); // Cannoneer
				break;
			case 3:
				cm.getPlayer().doJett(); // Jett
				break;
			case 4:
				cm.getPlayer().doCRB(); // Cygnus
				break;
			case 5:
				cm.getPlayer().doARB(); // Aran
				break;
			case 6:
				cm.getPlayer().doERB(); // Evan
				break;
			case 7:
				cm.getPlayer().doMERCRB(); // Mercedes
				break;
			case 8:
				cm.getPlayer().doPhantom(); // Phantom
				break;
			case 9:
				cm.getPlayer().doDSRB(); // Demon Slayer
				break;
			case 10:
				cm.getPlayer().doBAMRB(); // Battle Mage
				break;
			case 11:
				cm.getPlayer().doWHRB(); // Wild Hunter
				break;
			case 12:
				cm.getPlayer().doMRB(); // Mechanic
				break;
			case 13:
				cm.getPlayer().doEXPRB(); // Mihile
				break;
		}
			cm.dispose();
	}
}