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
        ms.showEnvironment(5, "cannonshooter/flying", []);
        ms.getEventEffect("Effect/Direction4.img/effect/cannonshooter/balloon/0", [9000, 0, 0, 1, 0, 0]);
        ms.exceTime(1500);
        ms.getDirectionStatus(true);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction4.img/effect/cannonshooter/balloon/1", [9000, 0, 0, 1, 0, 0]);
        ms.exceTime(1500);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction4.img/effect/cannonshooter/balloon/2", [9000, 0, 0, 1, 0, 0]);
        ms.showWZEffect("Effect/Direction4.img/cannonshooter/face04");
        ms.showWZEffect("Effect/Direction4.img/cannonshooter/out01");
        ms.dispose();
    } else {
        ms.dispose();
    }
}
