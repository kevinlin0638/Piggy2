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
	cm.sendSimple("#b#L0#學習/放棄 鍊金術#l");
    } else if (status == 1) {
	    if (cm.getPlayer().getProfessionLevel(92040000) > 0) {
		cm.sendYesNo("您確定要放棄 鍊金術? 您將會失去所有 鍊金術 的經驗與等級.");
	    } else if (cm.getPlayer().getProfessionLevel(92020000) > 0 || cm.getPlayer().getProfessionLevel(92030000) > 0 || cm.getPlayer().getProfessionLevel(92000000) <= 0) {
		cm.sendOk("您不能學習 鍊金術 因為您已經有 裝備製作 或 飾品製作 或 您沒有學習 採集.");
		cm.dispose();
	    } else {
		cm.sendYesNo("您確定要學習 鍊金術?");
	    }
    } else if (status == 2) {
	    if (cm.getPlayer().getProfessionLevel(92040000) > 0) {
		cm.sendOk("您放棄了 鍊金術.");
		cm.teachSkill(92040000, 0, 0);
	    } else {
		cm.sendOk("您學習了 鍊金術.");
		cm.teachSkill(92040000, 0x1000000, 0); //00 00 00 01
	    }
	    cm.dispose();
    }
}