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
        ms.showEnvironment(5, "cannonshooter/bang", []);
        ms.getDirectionStatus(true);
        ms.showWZEffect("Effect/Direction4.img/cannonshooter/Scene01");
        ms.showWZEffect("Effect/Direction4.img/cannonshooter/out02");
        ms.dispose();
    } else {
        ms.dispose();
    }
}
