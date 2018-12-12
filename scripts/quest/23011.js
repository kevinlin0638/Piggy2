var status = -1;

function end(mode, type, selection) {
    if (mode == 0) {
        if (status == 0) {
            qm.sendNext("This is an important decision to make.");
            qm.safeDispose();
            return;
        }
        status--;
    } else {
        status++;
    }
    if (status == 0) {
        if (qm.getJob() == 3000) {
            qm.gainItem(1382100, 1);
            qm.expandInventory(1, 4);
            qm.expandInventory(2, 4);
            qm.expandInventory(4, 4);
            qm.changeJob(3200);
        }
        qm.forceCompleteQuest();
        qm.sendNext("好！正式歡迎你成為末日反抗軍。從現在開始你是一名煉獄巫師。身為一名懷著狂氣的魔法師，要比任何人搶先一步對抗敵人。");
    } else if (status == 1) {
        qm.sendNextPrev("倘若煉獄巫師的身分曝光大家都知道了，會變的很棘手？如果被黑色翅膀發現的話，會變的麻煩的。那你從現在開始叫我老師吧。你需要特別課程來成為一位能夠獨當一面的反抗軍。");
    } else if (status == 2) {
        qm.safeDispose();
    }
}