var status = 0;
var invs = Array(0, 1); // Equip
var invc = Array(0, 5); // Cash
var invv;
var selected;
var slot_1 = Array(); // Equip
var slot_2 = Array(); // Equip
var slot_3 = Array(); // Cash
var slot_4 = Array(); // Cash
var statsSel;

function start() {
    action(1,0,0);
}

function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
        return;
    }
    status++;
	if (status == 1) { // nx is only eq/cash right?! D:
		status = selection + 1; // 2 = Equip || 200 = Cash
	    cm.sendSimple("Choose an inventory to drop your items from :\r\n\r\n#L1##bEquip#l\r\n#L199#Cash#l#k");
    } else if (status == 2) {
	  if (selection == 1) {
        var bbb = false;
        var selStr = "I can drop your NX from the #bEquip#k Slot. Choose an item to drop:\r\n\r\n";
        for (var x = 0; x < invs.length; x++) {
            var inv = cm.getInventory(invs[x]);
            for (var i = 0; i <= inv.getSlotLimit(); i++) {
                if (x == 0) {
                    slot_1.push(i);
                } else {
                    slot_2.push(i);
                }
                var it = inv.getItem(i);
                if (it == null) {
                    continue;
                }
                var itemid = it.getItemId();
                if (!cm.isCash(itemid)) {
                    continue;
                }
                bbb = true;
                selStr += "#L" + ((invs[x] * 1000) + i) + "##v" + itemid + "##l";
            }
        }
        if (!bbb) {
            cm.sendOk("You don't have any NX items, I can only drop NX items.");
            cm.dispose();
            return;
        }
        cm.sendSimple(selStr);
		} else {
		 status = 199;
		var ccc = false;
        var selStr1 = "I can drop your NX from the #bCash#k Slot. Choose an item to drop:\r\n\r\n";
        for (var x = 0; x < invc.length; x++) {
            var inv = cm.getInventory(invc[x]);
            for (var i = 0; i <= inv.getSlotLimit(); i++) {
                if (x == 0) {
                    slot_3.push(i);
                } else {
                    slot_4.push(i);
                }
                var it = inv.getItem(i);
                if (it == null) {
                    continue;
                }
                var itemid = it.getItemId();
                if (!cm.isCash(itemid)) {
                    continue;
                }
                ccc = true;
                selStr1 += "#L" + ((invc[x] * 1000) + i) + "##v" + itemid + "##l";
            }
        }
        if (!ccc) {
            cm.sendOk("You don't have any NX items, I can only drop NX items.");
            cm.dispose();
            return;
        }
        cm.sendSimple(selStr1);
		}
    } else if (status == 3) {
        invv = (selection / 1000) | 0;
        selected = (selection % 1000) | 0;
        var inzz = cm.getInventory(invv);
        if (selected >= inzz.getSlotLimit()) { 
            cm.sendOk("What? How did you do that?");
            cm.dispose();
            return;
        }
        if (invv == invs[0]) {
            statsSel = inzz.getItem(slot_1[selected]);
        } else if (invv == invs[1]) {
            statsSel = inzz.getItem(slot_2[selected]);
        }
        if (statsSel == null) {
            cm.sendOk("What? You selected an invalid item.");
            cm.dispose();
            return;
        }
        cm.sendGetNumber("You are about to drop < #v" + statsSel.getItemId() + "# >.\r\nHow many of this item do you want to drop?", 1, 1, statsSel.getQuantity());
    } else if (status == 4) {
        if (!cm.dropItem(selected, invv, selection)) {
            cm.sendOk("Unable to drop your item.");
            cm.dispose();
        } else {
		    cm.dispose();
        }
    } else if (status == 200) {
		invv = (selection / 1000) | 0;
        selected = (selection % 1000) | 0;
        var inzz = cm.getInventory(invv);
        if (selected >= inzz.getSlotLimit()) { 
            cm.sendOk("What? How did you do that?");
            cm.dispose();
            return;
        }
        if (invv == invc[0]) {
            statsSel = inzz.getItem(slot_3[selected]);
        } else if (invv == invc[1]) {
            statsSel = inzz.getItem(slot_4[selected]);
        }
        if (statsSel == null) {
            cm.sendOk("What? You selected an invalid item.");
            cm.dispose();
            return;
        }
        cm.sendGetNumber("You are about to drop < #v" + statsSel.getItemId() + "# >.\r\nHow many of this item do you want to drop?", 1, 1, statsSel.getQuantity());
	} else if (status == 201) {
		if (!cm.dropItem(selected, invv, selection)) {
            cm.sendOk("Unable to drop your item.");
            cm.dispose();
        } else {
		    cm.dispose();
        }
	}
}  