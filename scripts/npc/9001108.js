var status = 0;
var maps = new Array(4, 7, 5, 21, 1337, 3441, 6339);
var mapNames = new Array("#rMario Bros Map 1-1#k", "#bClock Tower FM#k", "#gHeaven FM#k", "#dMusical Metropolis#k", "#bThe Universe (beta)#k", "#rTyrant Overlord Baal Tower#k", "#gGreen Screen Map#k");
var selectedMap = -1;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 1)
		status++;
	else if (mode == 0 && status >= 0) {
		status = 999;
		cm.dispose();
		return;
	} else
		status--;
	if (status == 0) {
		var where = "Hello! #b #h #!#k I'm the #dSpecial#k #bMap#k #rWarper#k! I can warp you to:";
		for (var i = 0; i < maps.length; where += "\r\n#L" + i + "# " + mapNames[i] + "#l", i++);
			cm.sendSimple(where);
		} else if (status == 1) {
			cm.sendNext("#gHa#k#rve#k #bF#k#gu#k#rn#k#b!#k ");
			selectedMap = selection;
		} else if (status == 2) {
			cm.warp(maps[selectedMap], 0);
			cm.dispose();
		}
}
