/* ===========================================================
            Resonance
    NPC Name:       Dragon
    Map(s):         Dream World: Dream Forest(900010200)
    Description:    Warps to Utah's House
=============================================================
Version 1.0 - Script Done.(31/5/2010)
=============================================================
*/

var status = 0;

function start() {
status = -1;
action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 1)
            status++;
        else
            status--;
        if(status == 0)
            cm.sendNext("終於找到符合條件的人了...");
        if(status == 1)
            cm.sendNextPrev("我們來立下契約吧..");
        if(status == 2){
            cm.warp(900090101);
            cm.dispose();
        }
    }
}