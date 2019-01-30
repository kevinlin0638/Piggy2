/**
 * @author: Eric
 * @npc: Monster Park Shuttle
 * @func: Warps to-and-from Monster Park
*/
var status = -1;

function start() {
    cm.sendYesNo(cm.getMapId() != 951000000 ? "您好，您想要去怪物公園嗎?" : "您好，您要回城鎮了嗎?");
}

function action(mode, type, selection) {
	if (mode == 1) {
		++status;
		if (status == 0) {
			var recChannel = 0; // in the future if we have all 19 channels we will recommend..
			switch(cm.getPlayer().getLevel()) {
				case 160:
				case 161:
				case 162:
				case 163:
					recChannel = 9;
					break;
				default:
					recChannel = 1;
					break;
			}
			// cm.sendNext("Your current level is " + cm.getPlayer().getLevel() + ", so you can enter the level #bAdvanced Battleground#k.\r\nYou are being moved to the #rChannel " + recChannel + "#k to improve your Monster Park experience.");
			if (cm.getMapId() != 951000000)
				cm.saveReturnLocation("MULUNG_TC");
			(cm.getMapId() != 951000000 ? cm.warp(951000000, 0) : cm.sendNext("OK，現在帶您回去."));
		} else {
			var map = cm.getSavedLocation("MULUNG_TC");
			(cm.getMapId() != 951000000 ? cm.dispose() : cm.warp(map, 0));
			cm.dispose();
		}
    } else {
		cm.sendNext(cm.getMapId() != 951000000 ? "歡迎回來." : "如果您想離開再與我對話. 隨時觀迎!");
		cm.dispose();
    }
}