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
        ms.exceTime(90);
    } else if (status == 1) {
        ms.showEffect(false, "demonSlayer/text11");
        ms.exceTime(4000);
    } else {
        ms.showWZEffect("Effect/Direction6.img/DemonTutorial/Scene2");
        ms.dispose();
    }
}


