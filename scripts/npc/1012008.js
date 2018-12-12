var status = 0;
var itemType = 0; // 1 = Equip, 2 = Scrolls, 3 = Misc
// Type 1 : Equips
items_sel1 = [1004031, 1442298, 1102399, 1703999];
items_name1 = ["Monster Energy Cap (Hat)", "Monster Energy Can (Polearm)", "WRS Wings (Cape)", "WRS Scythe (Polearm)"];
items_amount1 = [1, 1, 1, 1];
items_price1 = [15, 15, 100, 100];
// Type 2 : Scrolls
items_sel2 = [2049406, 2049303, 2049303, 5062005, 5062005, 2450039, 2022918, 2022913, 2340000, 2340000];
items_name2 = ["Special Potential Scroll 100%", "Adv Equip Enhance Scroll (2)", "Adv Equip Enhance Scroll (20)", "Enlighten Miracle Cube (3)", "Enlighten Miracle Cube (30)", "1.5 EXP Coupon", "1.5 DROP Coupon", "Virtue Blessing (3)", "White Scroll (1)", "White Scroll (3)"];
items_amount2 = [1, 2, 20, 3, 30, 1, 1, 3, 1, 3];
items_price2 = [12, 1, 9, 1, 9, 21, 30, 1, 5, 13];
// Type 3 : Misc
items_sel3 = [4007099];
items_name3 = ["Wiz Coins (5)"];
items_amount3 = [5];
items_price3 = [1];

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
            if (cm.getPlayer().getVPoints() >= 1) {
				cm.sendSimple("Hello and Welcome to the #eWizStory Vote Shop#n.\r\nYou have #r" + cm.getPlayer().getVPoints() + "#k Vote Points.\r\nWhat would you like to purchase?\r\n"
				+	"\r\n#L1##rEquips#k"
				+	"\r\n#L2##bScrolls#k"
				+	"\r\n#L3##gMisc.#k");
         } else {
				cm.sendOk("Hello and Welcome to the #eWizStory Vote Shop#n.\r\nYou have #r" + cm.getPlayer().getVPoints() + "#k Vote Points.\r\nWhat would you like to purchase?\r\n\r\n\r\n#b#ePlease Vote at least once to unlock the categories.#k#n");
				cm.dispose();
         }
		} else if (status == 1) {
			if (selection == 1) {
				itemType = 1;
				var selStr = "Which piece of equipment would you like to buy?:\r\n";
                for (var i = 0; i < items_sel1.length; i++){
                    selStr += "\r\n#b#L" + i + "##v" + items_sel1[i] + "# - " + items_name1[i] + " (" + items_price1[i] + " Vote Points)#l#k";
                }
                cm.sendSimple(selStr);
			} else if (selection == 2) {
				itemType = 2;
				var selStr = "Which type of scroll would you like to buy?:\r\n";
                for (var i = 0; i < items_sel2.length; i++){
                    selStr += "\r\n#b#L" + i + "##v" + items_sel2[i] + "# - " + items_name2[i] + " (" + items_price2[i] + " Vote Points)#l#k";
                }
                cm.sendSimple(selStr);
			} else if (selection == 3) {
				itemType = 3;
				var selStr = "Which kind of misc items would you like to buy?:\r\n";
                for (var i = 0; i < items_sel3.length; i++){
                    selStr += "\r\n#b#L" + i + "##v" + items_sel3[i] + "# - " + items_name3[i] + " (" + items_price3[i] + " Vote Points)#l#k";
                }
                cm.sendSimple(selStr);
			}
		} else if (status == 2) {
			if (itemType == 1) {
			 if (cm.canHold(items_sel1[selection]) && cm.getPlayer().getVPoints() >= items_price1[selection]) {
				cm.gainItem(items_sel1[selection], items_amount1[selection]);
				cm.getPlayer().gainVotePoints(-items_price1[selection]);
				cm.sendOk("Here is your #v" + items_sel1[selection] + "# - #b" + items_name1[selection] + "#k that you wanted.");
				cm.dispose();
				} else {
					cm.sendOk("You don't have enough #rVote Points#k, or your #binventory#k is full.");
					cm.dispose();
				}
			} else if (itemType == 2) {
			 if (cm.canHold(items_sel2[selection]) && cm.getPlayer().getVPoints() >= items_price2[selection]) {
				cm.gainItem(items_sel2[selection], items_amount2[selection]);
				cm.getPlayer().gainVotePoints(-items_price2[selection]);
				cm.sendOk("Here is your #v" + items_sel2[selection] + "# - #b" + items_name2[selection] + "#k that you wanted.");
				cm.dispose();
				} else {
					cm.sendOk("You don't have enough #rVote Points#k, or your #binventory#k is full.");
					cm.dispose();
				}
			} else if (itemType == 3) {
			 if (cm.canHold(items_sel3[selection]) && cm.getPlayer().getVPoints() >= items_price3[selection]) {
				cm.gainItem(items_sel3[selection], items_amount3[selection]);
				cm.getPlayer().gainVotePoints(-items_price3[selection]);
				cm.sendOk("Here is your #v" + items_sel3[selection] + "# - #b" + items_name3[selection] + "#k that you wanted.");
				cm.dispose();
				} else {
					cm.sendOk("You don't have enough #rVote Points#k, or your #binventory#k is full.");
					cm.dispose();
				}
			}
        }
   }
}