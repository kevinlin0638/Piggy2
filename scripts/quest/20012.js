/*
	NPC Name: 		Kinu
	Description: 		Quest - Cygnus tutorial helper
*/

var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 2) {
            qm.sendNext("普通攻擊是基本技能，很容易使用。重要的是要記住，使用技巧做真正的狩獵是很重要的。我建議你重新考慮。");
            qm.safeDispose();
            return;
        }
        status--;
    }
    if (status == 0) {
        qm.sendNext("我一直在等待你, #h0#. 我的名字是 #p1102006# 為了要讓你滿足我的兄弟。 所以，你已經學會了如何使用普通攻擊了？\r\n 好了接下來你會了解 #b如何使用技能#k, 你會發現這對你很有幫助！");
    } else if (status == 1) {
        qm.sendNextPrev("當你每次升等你會獲得技能點數，這意味這你可以有一些能力了！ 請案 #bK 鍵#k 看看你的技能. 好好善用你的技能點數在技能上。 #b將技能拉到快捷鍵上更方便使用。#k.");
    } else if (status == 2) {
        qm.askAcceptDecline("時間過得真快，忘了你是要練習了... 接下來你會發現很多的 #o100121# 在這張地圖。你需要打倒 #r3隻 #o100121##k 使用你的 #b攻擊#b 技能 然後給我 1 #b#t4000483##k 作為證明OK？ 我會在這裡等你的。");
    } else if (status == 3) {
        qm.forceStartQuest();
        qm.summonMsg(8);
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
        qm.sendNext("你已經成功地打敗了 #o100121# 並給我帶來了 一個 #t4000483#. 這是非常令人印象深刻! #b你善用了 3 個技能點數 當你每一次升級的時候, 你會獲得更多技能點數，接下來請照著箭頭走去找我的兄弟 #b#p1102007##k, 他將告訴你下一步怎麼做。\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#fUI/UIWindow.img/QuestIcon/8/0# 40 經驗值");
    } else if (status == 1) {
        qm.gainItem(4000483, -1);
        qm.forceCompleteQuest();
        qm.gainExp(40);
        qm.dispose();
    }
}