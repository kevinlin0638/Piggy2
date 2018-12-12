/* global ms */

var status = -1;

function action(mode, type, selection) {
    if (mode === 0) {
        status--;
    } else {
        status++;
    }

    if (ms.isQuestActive(24007) || ms.isQuestFinished(24007)) {
        ms.dispose();
        return;
    }
    var i = -1;
    if (status <= i++) {
        ms.dispose();
    } else if (status === i++) {
        ms.lockUI(true);
        ms.getEventEffect("Effect/Direction5.img/effect/mercedesInIce/merBalloon/6", [2000, 0, -100, 1, 0, 0]);
        ms.exceTime(2000);
        ms.getDirectionStatus(true);
    } else if (status === i++) {
        ms.playerMoveRight();
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction5.img/effect/mercedesInIce/merBalloon/8", [2000, 0, -100, 1, 0, 0]);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.lockUI(false);
        ms.dispose();
    } else {
        ms.dispose();
    }
}