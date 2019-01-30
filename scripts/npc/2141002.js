/*
	NPC Name: 		The Forgotten Temple Manager
	Map(s): 		Deep in the Shrine - Twilight of the gods
	Description: 		Pink Bean
 */

function start() {
    cm.sendYesNo("你想從這裡出去嗎?");
}

function action(mode, type, selection) {
    if (mode == 1) {
        cm.warp(270050000, 0);
    }
    cm.dispose();
}