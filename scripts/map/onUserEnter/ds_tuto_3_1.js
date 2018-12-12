/*
 Author: Pungin
 */
var status = -1;

function action(mode, type, selection) {
    status++;
    if (status == 0) {
        ms.disableOthers(true);
        ms.lockUI(true);
        ms.spawnNPCRequestController(2159340, 175, 0);
        ms.spawnNPCRequestController(2159341, 300, 0);
        ms.spawnNPCRequestController(2159342, 600, 0);
        ms.setNPCSpecialAction(2159340, "panic");
        ms.setNPCSpecialAction(2159341, "panic");
        ms.getEventEffect("Effect/Direction6.img/effect/tuto/balloonMsg1/3", [1500, 0, -100, 1, 1, 0, 2159340, 0]);
        ms.getEventEffect("Effect/Direction6.img/effect/tuto/balloonMsg1/3", [1500, 0, -100, 1, 1, 0, 2159341, 0]);
        ms.getEventEffect("Effect/Direction6.img/effect/tuto/balloonMsg1/3", [1500, 0, -100, 1, 1, 0, 2159342, 0]);
        ms.getEventEffect("Effect/Direction6.img/effect/tuto/balloonMsg2/0", [1500, 0, -100, 0, 0]);
        ms.exceTime(1500);
    } else {
        ms.dispose();
        ms.openNpc(2159340);
    }
}