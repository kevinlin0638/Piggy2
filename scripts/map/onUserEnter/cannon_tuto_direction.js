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
        ms.getDirectionStatus(true);
        ms.showWZEffect("Effect/Direction4.img/cannonshooter/Scene00");
        ms.showWZEffect("Effect/Direction4.img/cannonshooter/out00");
        ms.dispose();
    } else {
        ms.dispose();
    }
}
