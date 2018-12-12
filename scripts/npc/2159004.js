var status = -1;
function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (cm.getInfoQuest(23999).indexOf("exp2=1") != -1) {
        cm.sendNext("找到約翰和潘了嗎？潘相當難找吧？可以找的地方全找找看唷！");
        cm.dispose();
        return;
    }

    if (status == 0) {
        cm.sendNext("?？被發現了嗎？哈哈哈…我躲的地方太容易被找到了嗎？");
    } else if (status == 1) {
        cm.sendNextPrev("找到約翰和潘了嗎？ 潘相當難找吧？可以找的地方全找找看。\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 5 exp");
    } else if (status == 2) {
        cm.gainExp(5);
        if (cm.getInfoQuest(23999).equals("")) {
            cm.updateInfoQuest(23999, "exp2=1");
        } else {
            cm.updateInfoQuest(23999, cm.getInfoQuest(23999) + ";exp2=1");
        }
        cm.dispose();
    }
}