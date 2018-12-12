/*
 Author: Pungin
 */
var status = -1;

function action(mode, type, selection) {
    status++;
    if (status == 0) {
        ms.lockUI(true);
        ms.disableOthers(true);
        ms.exceTime(3000);
        ms.getDirectionStatus(true);
    } else if (status == 1) {
        ms.showEffect(false, "demonSlayer/text12");
        ms.playerMoveLeft();
        ms.exceTime(10);
    } else {
        ms.playerWaite();
        ms.dispose();
        ms.openNpc(2159311);
    }
}