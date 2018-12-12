/**
 * @author: Eric
 * @npc: Papulatus
 * @func: GM Scroll and Timeless Equip Seller
 * @notes: Restored and updated version of the v83 Papulatus NPC, was too lazy to switch from selections -> arrays. 
*/

var status = 0;
var gmScrollPrice = 1000; // 1000 Wiz Coins
var timelessPrice = 100; // 100 Wiz Coins
var insufficientFunds = "Sorry, but you don't have the required amount of #bWiz Coins#k this item requires.";
var gainitem = "Thanks for those #bWiz Coins#k! They come in handy!";

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0 && status == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
                if (status == 0) {
	  //    cm.sendSimple("I honestly don't know how to greet myself. I'm just a flipping ghost with a remote control on a clock.#b" +
      //           "\r\n#L81#GM Scrolls (" + gmScrollPrice + " Wiz Coins)" +
      //           "\r\n#L83#Timeless Weapons (" + timelessPrice + " Wiz Coins)");
	  //} else if (selection == 81) {
               cm.sendSimple ("No worries, all GM Scrolls are in stock!#b" +
			"\r\n#L8#Bottomwear for DEF | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L9#Bow for ATT | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L10#Cape for Magic DEF | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L11#Cape for Weapon DEF | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L12#Claw for ATT | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L13#Crossbow for ATT | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L14#Dagger for ATT | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L15#Gloves for ATT | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L16#Gloves for DEX | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L17#Helmet for DEF | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L18#Helmet for HP | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L19#One-Handed Axe for ATT | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L20#One-Handed BW for ATT | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L21#One-Handed Sword for ATT | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L22#Overall Armor for DEX | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L23#Pole Arm for ATT | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L24#Shield for DEF | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L25#Shoes for DEX | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L26#Shoes for Jump | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L27#Shoes for Speed | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L28#Spear for ATT | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L29#Staff for Magic ATT | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L30#Topwear for DEF | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L31#Two-Handed Aex for ATT | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L32#Two-Handed BW for ATT | " + gmScrollPrice + " Wiz Coins" +
			"\r\n#L33#Two-Handed Sword for ATT | " + gmScrollPrice + " Wiz Coins");
			// "\r\n#L34#Wand for Magic ATT | " + gmScrollPrice + " Wiz Coins");
	  } else if (selection == 83) {
               cm.sendSimple ("Just some Timeless Weapons I've had sitting around!#b" +
			"\r\n#L45##z1482023# | " + timelessPrice + " Wiz Coins" +
			"\r\n#L46##z1302081# | " + timelessPrice + " Wiz Coins" +
			"\r\n#L47##z1312037# | " + timelessPrice + " Wiz Coins" +
			"\r\n#L48##z1322060# | " + timelessPrice + " Wiz Coins" +
			"\r\n#L49##z1402046# | " + timelessPrice + " Wiz Coins" +
			"\r\n#L50##z1412033# | " + timelessPrice + " Wiz Coins" +
			"\r\n#L51##z1422037# | " + timelessPrice + " Wiz Coins" +
			"\r\n#L52##z1442063# | " + timelessPrice + " Wiz Coins" +
			"\r\n#L53##z1332073# | " + timelessPrice + " Wiz Coins" +
			"\r\n#L54##z1332074# | " + timelessPrice + " Wiz Coins" +
			"\r\n#L55##z1372044# | " + timelessPrice + " Wiz Coins" +
			"\r\n#L56##z1382057# | " + timelessPrice + " Wiz Coins" +
			"\r\n#L57##z1432047# | " + timelessPrice + " Wiz Coins" +
			"\r\n#L58##z1452057# | " + timelessPrice + " Wiz Coins" +
			"\r\n#L59##z1462050# | " + timelessPrice + " Wiz Coins" +
			"\r\n#L60##z1472068# | " + timelessPrice + " Wiz Coins" +
			"\r\n#L61##z1492023# | " + timelessPrice + " Wiz Coins");
	} else if (selection == 8) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2040603, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 9) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2044503, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 10) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2041024, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 11) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2041025, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 12) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2044703, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 13) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2044603, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 14) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2043303, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 15) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2040807, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 16) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2040806, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 17) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2040006, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 18) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2040007, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 19) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2043103, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 20) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2043203, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 21) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2043003, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 22) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2040506, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 23) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2044403, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 24) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2040903, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 25) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2040709, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 26) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2040710, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 27) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2040711, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 28) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2044303, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 29) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2043803, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 30) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2040403, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 31) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2044103, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 32) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2044203, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 33) {
		if (cm.haveItem(4007099, gmScrollPrice)){
		cm.gainItem(2044003, 1);
		cm.gainItem(4007099, -gmScrollPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	} // where's selection 34 for the last scroll?
	//
	// END OF GM SCROLLS -- START OF TIMELESS
	//
	} else if (selection == 45) {
		if (cm.haveItem(4007099, timelessPrice)){
		cm.gainItem(1482023, 1);
		cm.gainItem(4007099, -timelessPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 46) {
		if (cm.haveItem(4007099, timelessPrice)){
		cm.gainItem(1302081, 1);
		cm.gainItem(4007099, -timelessPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 47) {
		if (cm.haveItem(4007099, timelessPrice)){
		cm.gainItem(1312037, 1);
		cm.gainItem(4007099, -timelessPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 48) {
		if (cm.haveItem(4007099, timelessPrice)){
		cm.gainItem(1322060, 1);
		cm.gainItem(4007099, -timelessPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 49) {
		if (cm.haveItem(4007099, timelessPrice)){
		cm.gainItem(1402046, 1);
		cm.gainItem(4007099, -timelessPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 50) {
		if (cm.haveItem(4007099, timelessPrice)){
		cm.gainItem(1412033, 1);
		cm.gainItem(4007099, -timelessPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 51) {
		if (cm.haveItem(4007099, timelessPrice)){
		cm.gainItem(1422037, 1);
		cm.gainItem(4007099, -timelessPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 52) {
		if (cm.haveItem(4007099, timelessPrice)){
		cm.gainItem(1442063, 1);
		cm.gainItem(4007099, -timelessPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 53) {
		if (cm.haveItem(4007099, timelessPrice)){
		cm.gainItem(1332073, 1);
		cm.gainItem(4007099, -timelessPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 54) {
		if (cm.haveItem(4007099, timelessPrice)){
		cm.gainItem(1332074, 1);
		cm.gainItem(4007099, -timelessPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 55) {
		if (cm.haveItem(4007099, timelessPrice)){
		cm.gainItem(1372044, 1);
		cm.gainItem(4007099, -timelessPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 56) {
		if (cm.haveItem(4007099, timelessPrice)){
		cm.gainItem(1382057, 1);
		cm.gainItem(4007099, -timelessPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 57) {
		if (cm.haveItem(4007099, timelessPrice)){
		cm.gainItem(1432047, 1);
		cm.gainItem(4007099, -timelessPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 58) {
		if (cm.haveItem(4007099, timelessPrice)){
		cm.gainItem(1452057, 1);
		cm.gainItem(4007099, -timelessPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 59) {
		if (cm.haveItem(4007099, timelessPrice)){
		cm.gainItem(1462050, 1);
		cm.gainItem(4007099, -timelessPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 60) {
		if (cm.haveItem(4007099, timelessPrice)){
		cm.gainItem(1472068, 1);
		cm.gainItem(4007099, -timelessPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 61) {
		if (cm.haveItem(4007099, timelessPrice)){
		cm.gainItem(1492023, 1);
		cm.gainItem(4007099, -timelessPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
	} else if (selection == 62) {
		if (cm.haveItem(4007099, timelessPrice)){
		cm.gainItem(1093411, 1);
		cm.gainItem(4007099, -timelessPrice);
		cm.sendOk(gainitem);
		cm.dispose();
	} else {
		cm.sendOk(insufficientFunds);
		cm.dispose();
	}
   }
 }
}