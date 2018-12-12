var status = -1;
function action(mode, type, selection) {
    status++;
    var complete = cm.getInfoQuest(23999).indexOf("exp1=1") != -1;

    if (complete) {
        if (status == 0)
            cm.sendNext("找到烏利卡和潘了嗎？由其是潘特別會躲，有仔細的找嗎？");
        else
            cm.dispose();
    } else {

        if (status == 0) {
            cm.sendNext("啊！被發現了...");
        } else if (status == 1) {
            cm.sendNext("嗚...本來想躲到牛車裡面的，但是頭進不去...");
        } else if (status == 2) {
            cm.sendNext("找到烏利卡和潘了嗎？尤其是潘特別會躲，有仔細找嗎？\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 5 exp");
        } else if (status == 3) {
            cm.gainExp(5);
            if (cm.getInfoQuest(23999).equals("")) {
                cm.updateInfoQuest(23999, "exp1=1");
            } else {
                cm.updateInfoQuest(23999, cm.getInfoQuest(23999) + ";exp1=1");
            }
        } else {
            cm.dispose();
        }
    }
}