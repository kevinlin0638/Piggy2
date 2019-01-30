var status = -1;

function start(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0)
            status -= 2;
        else {
            qm.sendNext("好孩子都會聽媽媽的話，寶貝乖，給我聽話。");
            qm.dispose();
            return;
        }
    }
    if (status == 0)
        qm.sendAcceptDecline("你#b爸#k早上離開農場的時候忘記帶便當盒走，寶貝你願意拿#b便當盒#k過去給你爸嗎？他就在#b#m100030300##k。");
    else if (status == 1) {
        qm.forceStartQuest();
        if (!qm.haveItem(4032448))
            qm.gainItem(4032448, 1);
        qm.sendNext("你果然是我的好孩子！離開房子後#b往左邊走#k。直接往你爸那過去吧，他一定餓壞了。");
    } else if (status == 2)
        qm.sendNextPrev("如果便當盒被狗吃了，再回來找我領便當，我再幫他準備一個。");
    else if (status == 3) {
        qm.evanTutorial("UI/tutorial/evan/5/0", 1);
        qm.dispose();
    }
}

function end(mode, type, selection) {
    qm.dispose();
}