var status = -1;

function end(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0)
            status -= 2;
        else {
            qm.sendNext("你不要孵化器喔？");
            qm.dispose();
            return;
        }
    }
    if (!qm.isQuestActive(22007)) {
        qm.forceStartQuest();
        qm.dispose();
        return;
    }
    if (status == 0)
        qm.sendNext("你帶#t4032451#來了嗎？來，拿給我。我給你孵化器。");
    if (status == 1)
        qm.sendNext("好了，拿去。我根本不知道能不能用，既然你要... \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 360 exp");
    if (status == 2) {
        qm.forceCompleteQuest();
        qm.gainExp(360);
        if (qm.haveItem(4032451)) {
            qm.gainItem(4032451, -1);
        }
        qm.evanTutorial("UI/tutorial/evan/9/0", 1);
        qm.dispose();
    }
}