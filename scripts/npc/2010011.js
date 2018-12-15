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
	cm.sendYesNo("您要去 #b公會本部(英雄之殿)#k 嗎?");
    } else if (status == 1) {
	cm.warp(200000301);
	cm.dispose();
    }
}