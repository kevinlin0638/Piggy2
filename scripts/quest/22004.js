var status = -1;

function start(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0)
            status -= 2;
        else {
            qm.sendNext("Hm, #p1013101# would have done it at the drop of a hat.");
            qm.dispose();
            return;
        }
    }
    if (status == 0)
        qm.sendNext("實際上近來的#o1210100#們有點奇怪，不明原因的暴怒。我有點擔心，所以今天早了一些離開農場來確認情形，似乎是部份的#o1210100#逃離了籬笆。");
    else if (status == 1)
        qm.sendAcceptDecline("在我找到這些逃走的#o1210100#之前，我必須把籬笆修好。幸好，籬笆不是被破壞得很嚴重，我只需要一點#t4032498#來修理它。你可以幫我帶回#b#t4032498#3個#k嗎？");
    else if (status == 2) {
        qm.forceStartQuest();
        qm.sendNext("你真是太棒了！你可以在#r#o0130100##k身上找到#b#t4032498##k。他們有點強大，遇到危險時，記得使用技能保護自己。");
    } else if (status == 3) {
        qm.evanTutorial("UI/tutorial/evan/6/0", 1);
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
        qm.sendNext("摁　你拿到所有的#t4032498#了嗎？  孩子你蒸蚌 我該給你一點獎勵... 讓我看看... 喔～有了！ \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#i3010097# 1個 #t3010097# \r\n#i2022621# 15個 #t2022621# \r\n#i2022622# 15個 #t2022622# \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 210 經驗值");
    if (status == 1) {
        qm.forceCompleteQuest();
        qm.removeAll(4032498);
        qm.gainItem(3010097, 1);
        qm.gainItem(2022621, 15);
        qm.gainItem(2022622, 15);
        qm.gainExp(210);
        qm.sendNextPrev("來，幫你做了一張新椅子，這是從剩下的材料做出來的。雖然看起來不怎麼樣，但是挺堅固的。相信之後會派上用場的！");
    }
    if (status == 2) {
        qm.evanTutorial("UI/tutorial/evan/7/0", 1);
        qm.dispose();
    }
}