/**
 * @author: Eric
 * @npc: Yulete
 * @func: Romeo and Juliet GMS-like PQ
*/
var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	(mode == 1 ? status++ : mode == 0 ? status-- : cm.dispose());
	if (status == 0) {
		cm.sendNext("I... I ... I must... let ... the whole world... know... of my... work.....\r\n(Yulete can't continue.)");
	} else if (status == 1) {
		cm.sendNextPrev("(We better leave this place right now.)");
	} else if (status == 2) {
		cm.warp(926110600, 0);
		cm.dispose();
	}
}