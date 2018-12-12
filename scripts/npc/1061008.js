/**
 * @author: Eric
 * @npc: Mr. O (1061008)
 * @func: All-in-one SHOP (Up-to-date to v117, removal of NX Items and useless etc)
 * @notes: Need to add Phantom cards, removal of NX items within BOTTOMS/OVERALLS.
*/

var status = 0;

function start() { 
	status = -1;
    action(1, 0, 0);
} 

function action(mode, type, selection) { 
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
    if (status == 0) { 
        cm.sendNext("Welcome to the #bone-and-only#k #eWizStory#n #rAll-In-One#k!\r\nI sell items for #eevery#n class in-game for #b1,000,000 mesos#k.");
	} else if (status == 1) {
		cm.sendSimple("Choose a type of equipment you would like to purchase:\r\n#L0##bWarrior Hats#l\r\n#L1#Warrior Tops#l\r\n#L2#Warrior Bottoms#l\r\n#L3#Warrior Overalls\r\n#L4#Warrior Gloves\r\n#L5#Warrior Shields\r\n#L6#Warrior Shoes\r\n#L7#1-Handed Axes\r\n#L8#2-Handed Axes\r\n#L9#1-Handed Blunt Weapons\r\n#L10#2-Handed Blunt Weapons\r\n#L11#1-Handed Swords\r\n#L12#2-Handed Swords\r\n#L13#Spears\r\n#L14#PoleArms\r\n#L15#Demon Maces#k#r\r\n#L16#Magician Hats\r\n#L17#Magician Overalls\r\n#L18#Magician Gloves\r\n#L19#Magician Shields\r\n#L20#Magician Shoes\r\n#L21#Wands\r\n#L22#Staffs#k#d\r\n#L23#Archer Hats\r\n#L24#Archer Overalls\r\n#L25#Archer Gloves\r\n#L26#Archer Shoes\r\n#L27#Bows\r\n#L28#Crossbows\r\n#L29#Dual Crossbows\r\n#L30#Arrows and Magic Arrows#k#g\r\n#L31#Thief Hats\r\n#L32#Thief Tops\r\n#L33#Thief Bottoms\r\n#L34#Thief Overalls\r\n#L35#Thief Gloves\r\n#L36#Thief Shields\r\n#L37#Thief Shoes\r\n#L38#Daggers\r\n#L39#Claws\r\n#L40#Canes\r\n#L41#Throwing Stars#k#e\r\n#L42#Pirate Hats\r\n#L43#Pirate Overalls\r\n#L44#Pirate Gloves\r\n#L45#Pirate Shoes\r\n#L46#Guns and Knuckles\r\n#L47#Cannons\r\n#L48#Bullets and Capsules#n");
    } else if (status == 2) {
	   cm.dispose(); // dispose then load shop from sel? :|
		if (selection == 0) { // Warrior
			cm.openShop(6100); 
		} else if (selection == 1) { 
			cm.openShop(6101); 
		} else if (selection == 2) {
			cm.openShop(6102); 
		} else if (selection == 3) {
			cm.openShop(6103); 
		} else if (selection == 4) {
			cm.openShop(6104); 
		} else if (selection == 5) {
			cm.openShop(6105); 
		} else if (selection == 6) {
			cm.openShop(6106); 
		} else if (selection == 7) {
			cm.openShop(6107); 
		} else if (selection == 8) {
			cm.openShop(6108); 
		} else if (selection == 9) {
			cm.openShop(6109); 
		} else if (selection == 10) {
			cm.openShop(6110); 
		} else if (selection == 11) {
			cm.openShop(6111); 
		} else if (selection == 12) {
			cm.openShop(6112); 
		} else if (selection == 13) {
			cm.openShop(6113); 
		} else if (selection == 14) {
			cm.openShop(6114); 
		} else if (selection == 15) {
			cm.openShop(6115); 
		} else if (selection == 16) { // Magician
			cm.openShop(6200); 
		} else if (selection == 17) {
			cm.openShop(6201); 
		} else if (selection == 18) {
			cm.openShop(6202); 
		} else if (selection == 19) {
			cm.openShop(6203); 
		} else if (selection == 20) {
			cm.openShop(6204); 
		} else if (selection == 21) {
			cm.openShop(6205); 
		} else if (selection == 22) {
			cm.openShop(6206); 
		} else if (selection == 23) { // Archer
			cm.openShop(6300); 
		} else if (selection == 24) {
			cm.openShop(6301); 
		} else if (selection == 25) {
			cm.openShop(6302); 
		} else if (selection == 26) {
			cm.openShop(6303); 
		} else if (selection == 27) {
			cm.openShop(6304); 
		} else if (selection == 28) {
			cm.openShop(6305); 
		} else if (selection == 29) {
			cm.openShop(6306); 
		} else if (selection == 30) {
			cm.openShop(6307); 
		} else if (selection == 31) { // Thief
			cm.openShop(6400); 
		} else if (selection == 32) {
			cm.openShop(6401); 
		} else if (selection == 33) {
			cm.openShop(6402); 
		} else if (selection == 34) {
			cm.openShop(6403); 
		} else if (selection == 35) {
			cm.openShop(6404); 
		} else if (selection == 36) {
			cm.openShop(6405); 
		} else if (selection == 37) {
			cm.openShop(6406); 
		} else if (selection == 38) {
			cm.openShop(6407); 
		} else if (selection == 39) {
			cm.openShop(6408); 
		} else if (selection == 40) {
			cm.openShop(6409); 
		} else if (selection == 41) {
			cm.openShop(6410); 
		} else if (selection == 42) { // Pirate
			cm.openShop(6500); 
		} else if (selection == 43) {
			cm.openShop(6501); 
		} else if (selection == 44) {
			cm.openShop(6502); 
		} else if (selection == 45) {
			cm.openShop(6503); 
		} else if (selection == 46) {
			cm.openShop(6504); 
		} else if (selection == 47) {
			cm.openShop(6505); 
		} else if (selection == 48) {
			cm.openShop(6506); 
		} else if (selection == 49) { // General Store (Not Available)
			cm.openShop(6700); 
		} else if (selection == 50) { // Boss Items (Not Available)
			cm.openShop(6701); 
		}
    } 
  }
}  