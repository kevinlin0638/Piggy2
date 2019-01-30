var status = -1;

function start(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0)
            status -= 2;
        else {
            qm.sendNext("嗯？你沒告訴猶他嗎？你要對哥哥好一點。");
            qm.dispose();
            return;
        }
    }
    if (status == 0)
        qm.sendNext("睡得好嗎?");
    else if (status == 1)
        qm.PlayerToNpc("#b好啊，媽媽那你呢#k");
    else if (status == 2)
        qm.sendNextPrev("我也睡得很好,可是你看起來有點疲倦,你真的有睡好嗎? 還是昨天晚上的閃電一直吵醒你了?");
    else if (status == 3)
        qm.PlayerToNpc("#b跟那沒有關係啦　媽媽~，只是昨天做了一個很奇怪的夢...#k");
    else if (status == 4)
        qm.sendNextPrev("奇怪的夢? 怎樣奇怪的夢?");
    else if (status == 5)
        qm.PlayerToNpc("#b恩...#k");
    else if (status == 6)
        qm.PlayerToNpc("#b如此這般~如此這般(開始跟媽媽說關於夢到一隻龍的事情.)");
    else if (status == 7)
        qm.sendAcceptDecline("哈哈哈, 一隻龍? 真是太不可思議了. 幸好牠沒把你吃掉哈哈 你該把這個夢告訴猶他,他會很樂意聽這個故事的.");
    else if (status == 8) {
        qm.forceStartQuest();
        qm.sendNext("#b猶他#k 跑去 #b前院#k 餵獵犬了. 你出去就會看到他了.");
    } else if (status == 9) {
        qm.evanTutorial("UI/tutorial/evan/1/0", 1);
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
        qm.sendNext("嗨 怎麼了? 你怎黑眼圈這麼重啊? 沒有睡好嗎? 摁? 奇怪的夢? 那個夢怎樣啊? 摁摁? 關於龍的夢?");
    if (status == 1)
        qm.sendNextPrev("挖 龍嗎? 你認真嗎? 我不知道該怎解釋這個夢欸, 但是聽起來是個好夢吧! 你有在夢裡看到你的狗嗎? XDDDD!\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 20 exp");
    if (status == 2) {
        qm.gainExp(20);
        qm.evanTutorial("UI/tutorial/evan/2/0", 1);
        qm.forceCompleteQuest();
        qm.dispose();
    }
}