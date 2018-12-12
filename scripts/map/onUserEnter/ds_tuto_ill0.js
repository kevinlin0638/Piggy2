/*
 Author: Pungin
 */
var status = -1;

function action(mode, type, selection) {
    status++;
    if (status == 0) {
        ms.lockUI(true);
        ms.disableOthers(true);
        ms.showWZEffect("Effect/Direction6.img/DemonTutorial/SceneLogo");
        ms.exceTime(6300);
    } else {
        ms.dispose();
        ms.warp(927000000, 0);
    }
}


