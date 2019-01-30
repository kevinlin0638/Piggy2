/*
	NPC Name: 		Kisan
	Description: 		Quest - Cygnus tutorial helper
*/

var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 2) {
            qm.sendNext("You don't want to? It's not even that hard, and you'll receive special equipment as a reward! Well, give it some thought and let me know if you change your mind.");
            qm.safeDispose();
            return;
        }
        status--;
    }
    if (status == 0) {
        qm.sendNext("有許多方法打獵，但最基本的方法是用你的 #b普通攻擊#k. 所有你需要的是在你的手的武器，因為它只是擺動你的武器在怪物一件簡單的事情。");
    } else if (status == 1) {
        qm.sendNextPrev("請按 #bCtrl#k 使用你的普通攻擊. 通常下 Ctrl 位於 #b鍵盤的左下角#k, 但你並不需要我告訴你對不對？ 發現Ctrl 並嘗試攻擊！");
    } else if (status == 2) {
        qm.askAcceptDecline("現在，你已經嘗試過了，我們一定要測試它。在這方面，你可以找到最薄弱 #r#o100120##k 在耶雷弗, 這是您的最佳選擇。嘗試狩獵 #r1隻#k. 當你回來我給你的獎勵。.");
    } else if (status == 3) {
        qm.forceStartQuest();
        qm.summonMsg(4);
        qm.dispose();
    }
}

function end(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        qm.sendNext("很棒唷看你學得很快，將來一定是強大的王者！");
    } else if (status == 1) {
        qm.sendNextPrev("這身裝備是貴族專屬的。 它將送給你穿，穿上它吧！ 然後按照箭頭的方向去找我的兄弟 #b#p1102006##k. 他會告訴你下一步該怎麼做。 \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#i1002869# #t1002869# - 1 \r\n#i1052177# #t1052177# - 1 \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 30 經驗值");
    } else if (status == 2) {
        qm.gainItem(1002869, 1);
        qm.gainItem(1052177, 1);
        qm.forceCompleteQuest();
        qm.gainExp(30);
        qm.summonMsg(6);
        qm.dispose();
    }
}