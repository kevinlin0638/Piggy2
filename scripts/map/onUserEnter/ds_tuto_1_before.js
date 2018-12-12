/*
 Author: Pungin
 */
var status = -1;

function action(mode, type, selection) {
    status++;
    if (status == 0) {
        ms.lockUI(true);
        ms.disableOthers(true);
        ms.playerMoveLeft();
        ms.exceTime(30);
        ms.getDirectionStatus(true);
    } else if (status == 0) {
        ms.playerWaite();
        ms.showEffect(false, "demonSlayer/text8");
        ms.exceTime(500);
    } else if (status == 1) {
        ms.showEffect(false, "demonSlayer/text9");
        ms.exceTime(3000);
    } else {
        ms.dispose();
        ms.warp(927000010, 0);
    }
}


