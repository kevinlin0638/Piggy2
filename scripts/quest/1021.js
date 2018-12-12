importPackage(Packages.client.MapleStat);

/* Author: Xterminator (Modified by RMZero213)
	NPC Name: 		Roger
	Map(s): 		Maple Road : Lower level of the Training Camp (2)
	Description: 		Quest - Roger's Apple
*/
var status = -1;

function start(mode, type, selection) {
    if (mode == -1) {
	qm.dispose();
    } else {
	if (mode == 1)
	    status++;
	else
	    status--;
	
	if (status == 0) {
	    qm.sendNext("嗨, 怎麼了嗎? 我是羅傑，可以教你一些有用的知識。");
	} else if (status == 1) {
	    qm.sendNextPrev("你問我為什麼在這嗎? 哈哈哈!\r我想要教導那些剛進楓之谷的冒險者們。");
	} else if (status == 2) {
	    qm.askAcceptDecline("所以..... 讓我們來玩點有趣的~!");
	} else if (status == 3) {
	    if (qm.getPlayerStat("HP") >= 50) {
			qm.getPlayer().addHP(-30);
	    }
	    if (!qm.haveItem(2010007)) {
			qm.gainItem(2010007, 1);
	    }
	    qm.sendNext("哈哈，嚇死你了吧！ 如果你血量歸零將是個大問題的。 那現在我會給你一顆 #r羅傑的貧果#k。 請把它吃下去，你會變得更有活力的。按下鍵盤上的#bI#k 來打開物品欄");
	} else if (status == 4) {
	    qm.sendPrev("請先把剛剛給你的蘋果吃下去，你會發現你的血量會上升，等你血量 100% 的時候再來跟我說話");
	} else if (status == 5) {
	    qm.forceStartQuest();
	    qm.dispose();
	}
    }
}

function end(mode, type, selection) {
    if (mode == -1) {
		qm.dispose();
    } else {
	if (mode == 1)
	    status++;
	else
	    status--;
	if (status == 0) {
	    if (qm.getPlayerStat("HP") < 50) {
			qm.sendNext("哈囉，你還沒把我給你的蘋果吃掉啊，趕快吃了再來找我吧。");
			qm.dispose();
	    } else {
			qm.sendNext("你看～是不是很簡單？ 你可以在右側的欄位設定#b熱鍵#k。 哈哈，你聽不懂對吧？ 喔，每隔一段時間，血量就會恢復了。 雖然很花時間，但好好運用的話可以幫助不少的。");
	    }
	} else if (status == 1) {
	    qm.sendNextPrev("Alright! Now that you have learned alot, I will give you a present. This is a must for your travel in Maple World, so thank me! Please use this under emergency cases!");
	} else if (status == 2) {
	    qm.sendNextPrev("好了，該教你的都教了，該說再見了。 祝你好運囉！\r\n\r\n" +
		"#fUI/UIWindow.img/QuestIcon/4/0#\r\n"+
		"#v2010000# 3 #t2010000#\r\n"+
		"#v2010009# 3 #t2010009#\r\n\r\n"+
		"#fUI/UIWindow.img/QuestIcon/8/0# 10 exp");
	} else if (status == 3) {
	    qm.gainExp(10);
	    qm.gainItem(2010000, 3);
	    qm.gainItem(2010009, 3);
	    qm.forceCompleteQuest();
	    qm.dispose();
	}
    }
}