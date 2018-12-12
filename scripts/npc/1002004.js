var status = 0;

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
			cm.sendSimple("Ever heard of #rJouga City#k? It's a city that gets invaded by \r\n#dJouga Wolf#k during the night. Would you like to go there?\r\n#b#L1#Yeah, sure!#k#l\r\n\#r#L2#NO WAY!#k#l");
		} else if (status == 1) {
			if (selection == 1) {
				// cm.warp(8, 1);
				cm.sendOk("This feature is #rcoming back soon#k to #eWizStory#n!");
				cm.dispose();
			} else if (selection == 2) {
				cm.sendOk("As you wish...but you need to know that the leader of the\r\n#rJouga Wolf#k has a #rpowerful mask#k that would boost up your stats.");
				cm.dispose();
			}
		}
	}
}