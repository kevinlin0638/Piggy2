/* Kedrick
	Fishking King NPC
*/

var status = -1;
var sel;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
		if (status == 0) {
			cm.dispose();
			return;
		}
	status--;
    }

    if (status == 0) {
	cm.sendSimple("你想做什麼？#b\n\r #L1#購買釣魚餌#l \n\r #L3#使用高級魚餌罐頭#l#k");
    } else if (status == 1) {
	sel = selection;
	if (sel == 1) {
	    cm.sendYesNo("#b120個#k 魚餌需要 #b300000#k 楓幣。 您要購買嗎？");
	} else if (sel == 3) {
	    if (cm.canHold(2300001,120) && cm.haveItem(5350000,1)) {
		if (!cm.haveItem(2300001)) {
		    cm.gainItem(2300001, 120);
		    cm.gainItem(5350000,-1);
		    cm.sendNext("快樂釣魚去~");
		} else {
		    cm.sendNext("你已經有魚餌。");
		}
	    } else {
		cm.sendOk("請確定你有足夠的 #b高級魚餌罐頭#k 或 #b背包空間.");
	    }
	    cm.safeDispose();
	}
    } else if (status == 2) {
	if (sel == 1) {
	    if (cm.canHold(2300000,120) && cm.getMeso() >= 300000) {
		if (!cm.haveItem(2300000)) {
		    cm.gainMeso(-300000);
		    cm.gainItem(2300000, 120);
		    cm.sendNext("快樂釣魚去~");
		} else {
		    cm.sendNext("你已經有魚餌。");
		}
	    } else {
		cm.sendOk("請確定你有足夠的 #b楓幣#k 或 #b背包空間.");
	    }
	    cm.safeDispose();
	}
    }
}