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
    } else {
        ms.playerWaite();
        ms.dispose();
        ms.openNpc(2159310);
    }
}


