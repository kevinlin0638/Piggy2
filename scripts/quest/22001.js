var status = -1;

function start(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0)
            status -= 2;
        else {
            qm.sendOk("別偷懶了，你想看你哥哥被狗咬嗎？快一點！給我接受任務！");
            qm.dispose();
            return;
        }
    }
    if (status == 0)
        qm.sendNext("哈哈，一大早就笑了半天。喂，還站在那邊幹啥，還不快去餵#b#p1013102##k。");
    else if (status == 1)
        qm.PlayerToNpc("#b欸！？那不是猶他的工作嗎？#k");
    else if (status == 2)
        qm.sendAcceptDecline("這個傢伙！居然這樣叫哥哥！你又不是不知道那隻狗有多討厭我，我靠近他一定會被咬。#b#p1013102##k喜歡你，你拿去啦。");
    else if (status == 3) {
        qm.gainItem(4032447, 1);
        qm.forceStartQuest();
        qm.sendOk("趕快到#左邊#k去把飼料拿給#b#p1013102##k再回來。那隻狗從剛剛開始汪汪叫，可能是肚子餓了快去快回。");
        qm.dispose();
    } else if (status == 4) {
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
        qm.sendNext("#b(You place food in Bulldog's bowl.)#k");
    if (status == 1)
        qm.sendOk("#b(Bulldog is totally sweet. Utah is just a coward.)#k\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 35 exp");
    if (status == 2) {
        qm.forceCompleteQuest();
        qm.gainItem(4032447, -1);
        qm.gainExp(35);
        qm.sendOk("#b(Looks like Bulldog has finished eating. Return to Utah and let him know.)#k");
        qm.dispose();
    }
}