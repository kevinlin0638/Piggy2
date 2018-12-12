/* global ms */

var status = -1;

function action(mode, type, selection) {
    if (mode === 0) {
        status--;
    } else {
        status++;
    }

    var i = -1;
    if (status <= i++) {
        ms.dispose();
    } else if (status === i++) {
        ms.lockUI(true);
        ms.getEventEffect("Effect/Direction5.img/effect/mercedesInIce/merBalloon/9", [2000, 0, -100, 1, 0, 0]);
        ms.exceTime(2000);
        ms.getDirectionStatus(true);
    } else if (status === i++) {
        ms.playerMoveLeft();
    } else if (status === i++) {
        ms.lockKey(true);
        ms.dispose();
        ms.warp(910150005, 0);
    } else {
        ms.dispose();
    }
}