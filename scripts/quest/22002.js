var status = -1;

function start(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0)
            status -= 2;
        else {
            qm.sendOk("什麼？你不吃早餐嗎？一日之計在於晨，一天當中最重要的一餐就是早餐！你想通了再來找我吧，不要的話我就幫你吃掉了。");
            qm.dispose();
            return;
        }
    }
    if (status == 0)
        qm.sendNext("你拿飼料給#p1013102#吃了嗎？");
    else if (status == 1)
        qm.sendAcceptDecline("來，我會給你#b三明治#k吃，吃完之後去找媽媽，她有事要交代給你做。");
    else if (status == 2) {
        qm.forceStartQuest();
        qm.gainItem(2022620, 1);
        qm.sendAcceptDecline("#b(想說的話？ 總之先吃了#t2022620#在回家去。)#k");
    } else if (status == 3) {
        qm.evanTutorial("UI/tutorial/evan/3/0", 1);
        qm.dispose();
    }
}

function end(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0)
            status -= 2;
        else {
            qm.dispose();
            return;
        }
    }
    if (status == 0)
        qm.sendNext("早餐吃了嗎？　吃完可以幫我一個忙嗎？\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i1003028# 1x #t1003028#  \r\n#i2022621# 5x #t2022621#s \r\n#i2022622# 5x #t2022622# \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 60 經驗值");
    if (status == 1) {
        qm.forceCompleteQuest();
        qm.evanTutorial("UI/tutorial/evan/4/0", 1);
        qm.gainItem(1003028, 1);
        qm.gainItem(2022621, 5);
        qm.gainItem(2022622, 5);
        qm.gainExp(60);
        qm.dispose();
    }
}