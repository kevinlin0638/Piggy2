// Joel : Ellinia Station Usher
// Was this different in v83? Dunno, will use this anyways until then. o-o

var status = 0;

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (status == 0) {
		cm.sendYesNo("Do you wish to go to #bEllinia Station#k?");
		status++;
	} else {
		if ((status == 1 && type == 1 && selection == -1 && mode == 0) || mode == -1) {
			cm.dispose();
		} else {
			if (status == 1) {
					cm.sendNext ("Alright, see you next time.");
					status++
			} else if (status == 2) {
					cm.warp(104020111, 0);
					cm.dispose();
			}
		}
	}
}
