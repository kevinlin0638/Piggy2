var status = -1;

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
	cm.sendYesNo("您確定要出去了嗎?");
    } else if (status == 1) {
	cm.warp(100000000,0);
	cm.dispose();
    }
}