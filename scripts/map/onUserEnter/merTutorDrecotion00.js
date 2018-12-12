/* global ms */

var status = -1;

function action(mode, type, selection) {
    if (mode === 0 && status !== 0) {
        status--;
    } else {
        status++;
    }

    var i = -1;
    if (status <= i++) {
        ms.dispose();
    } else if (status === i++) {
        ms.teachSkill(20021166, 1, 0);
        ms.teachSkill(20021181, 1, 0);
        ms.teachSkill(20020111, 1, 0);
        ms.lockUI(true);
        ms.playMovie("Mercedes.avi");
    } else if (status === i++) {
        ms.disableOthers(true);
        ms.playerMoveDown();
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction5.img/effect/mercedesInIce/merBalloon/0", [2000, 0, -100, 1, 0, 0]);
        ms.exceTime(3000);
    } else if (status === i++) {
        ms.playerMoveRight();
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction5.img/effect/mercedesInIce/merBalloon/1", [2000, 0, -100, 1, 0, 0]);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.playerMoveRight();
        ms.getDirectionStatus(true);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction5.img/effect/mercedesInIce/merBalloon/2", [2000, 0, -100, 1, 0, 0]);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.playerMoveRight();
        ms.getDirectionStatus(true);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction5.img/effect/mercedesInIce/merBalloon/3", [2000, 0, -100, 1, 0, 0]);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.playerMoveRight();
        ms.getDirectionStatus(true);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction5.img/effect/mercedesInIce/merBalloon/4", [2000, 0, -100, 1, 0, 0]);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.playerMoveRight();
        ms.getDirectionStatus(true);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction5.img/effect/mercedesInIce/merBalloon/5", [2000, 0, -100, 1, 0, 0]);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.lockUI(false);
        ms.disableOthers(false);
        ms.dispose();
    } else {
        ms.dispose();
    }
}