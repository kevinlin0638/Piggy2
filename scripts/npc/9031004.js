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
	cm.sendSimple("#b#L0#學習/放棄 飾品製作#l");
    } else if (status == 1) {
	    if (cm.getPlayer().getProfessionLevel(92030000) > 0) {
		cm.sendYesNo("您確定要放棄 飾品製作? 您將會失去所有 飾品製作 的經驗與等級.");
	    } else if (cm.getPlayer().getProfessionLevel(92020000) > 0 || cm.getPlayer().getProfessionLevel(92040000) > 0 || cm.getPlayer().getProfessionLevel(92010000) <= 0) {
		cm.sendOk("您不能學習 飾品製作 因為您已經有 裝備製作 或 鍊金術 或 您沒有學習 採礦.");
		cm.dispose();
	    } else {
		cm.sendYesNo("您確定要學習 飾品製作?");
	    }
    } else if (status == 2) {
	    if (cm.getPlayer().getProfessionLevel(92030000) > 0) {
		cm.sendOk("您放棄了 飾品製作.");
		cm.teachSkill(92030000, 0, 0);
	    } else {
		cm.sendOk("您學習了 飾品製作.");
		cm.teachSkill(92030000, 0x1000000, 0); //00 00 00 01
	    }
	    cm.dispose();
    }
}