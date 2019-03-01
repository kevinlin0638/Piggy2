var status = -1;
var sel = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 0) {
	    cm.dispose();
	}
	status--;
    }
    if (status == 0) {
	cm.sendSimple("#b#L0#學習/放棄 採集#l\r\n");
    } else if (status == 1) {
	sel = selection;
	if (sel == 0) {
	    if (cm.getPlayer().getProfessionLevel(92020000) > 0 || cm.getPlayer().getProfessionLevel(92030000) > 0 || cm.getPlayer().getProfessionLevel(92040000) > 0) {
		cm.sendOk("請先放棄 裝備製作/飾品製作/鍊金.");
		cm.dispose();
		return;
	    }
	    if (cm.getPlayer().getProfessionLevel(92000000) > 0) {
		cm.sendYesNo("您確定要放棄 採集 嗎? 您將會失去所有 採集 的經驗與等級.");
	    } else if (cm.getPlayer().getProfessionLevel(92010000) > 0) {
		cm.sendOk("您不能學習 採集 因為您已經有 採礦 技能.");
		cm.dispose();
	    } else {
		cm.sendYesNo("您確定要學習 採集?");
	    }
	} else if (sel == 1) {
	    if (!cm.haveItem(4022023, 100)) {
		cm.sendOk("You need 100 Herb Roots.");
 	    } else if (!cm.canHold(2028066, 1)) {
		cm.sendOk("Please make some USE space.");
	    } else {
		cm.sendOk("Thank you.");
		cm.gainItem(2028066, 1);
		cm.gainItem(4022023, -100);
	    } 
	    cm.dispose();
	}
    } else if (status == 2) {
	if (sel == 0) {
	    if (cm.getPlayer().getProfessionLevel(92000000) > 0) {
		cm.sendOk("您放棄了 採集.");
		cm.teachSkill(92000000, 0, 0);
	    } else {
		cm.sendOk("您學習了 採集.");
		cm.teachSkill(92000000, 0x1000000, 0); //00 00 00 01
		if (cm.canHold(1502000,1)) {
			cm.gainItem(1502000,1);
		}
	    }
	    cm.dispose();
	}
    }
}