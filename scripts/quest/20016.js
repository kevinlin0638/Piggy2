/*
	NPC Name: 		Nineheart
	Description: 		Quest - Do you know the black Magician?
*/

var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 8) {
            qm.sendNext("Oh, do you still have some questions? Talk to me again and I'll explain it to you from the very beginning.");
            qm.safeDispose();
            return;
        }
        status--;
    }
    if (status == 0) {
        qm.sendNext("嗨, #h0#. 來迎來到 #p1101000# 騎士團. 我的名字是 #p1101002# 而我目前作為年輕的戰術家。哈哈！");
    } else if (status == 1) {
        qm.sendNextPrev("我敢肯定，你有很多的問題，因為一切都發生得太快。我會解釋這一切，一個接一個，從那裡你是你在這裡做什麼。");
    } else if (status == 2) {
        qm.sendNextPrev("這個島被稱為耶雷弗。");
    } else if (status == 3) {
        qm.sendNextPrev("這位年輕的女皇是楓之谷世界的統治者。什麼？這是你聽說過她的第一次？啊，是的。嗯，她是楓之谷世界的統治者，但她不喜歡來控制它。她從遠處觀看，以確保一切都很好。好吧，至少這是她一貫的作用。");
    } else if (status == 4) {
        qm.sendNextPrev("但是，這不是這種情況現在。我們一直在尋找的標牌立在楓的世界，預示黑法師的復興。我們不能讓黑法師回來恐嚇楓之谷，因為他在過去!");
    } else if (status == 5) {
        qm.sendNextPrev("但是，這是很久以前的今天，人們不要為實現黑法師是有多嚇人的。我們都成了被寵壞和平楓之谷世界我們今天所享有和遺忘曾經是如何混亂和可怕的楓之谷世界。如果我們不做些什麼，黑法師將再次統治楓之谷世界！");
    } else if (status == 6) {
        qm.askAcceptDecline("以上是我的解釋，有問題嗎？ \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#fUI/UIWindow.img/QuestIcon/8/0# 380 經驗值");
    } else if (status == 7) {
        if (qm.getQuestStatus(20016) == 0) {
            qm.gainExp(380);
            qm.forceCompleteQuest();
        }
        qm.sendNext("我很高興你清楚我們目前的情況，但你知道在你目前的能力，你甚至沒有強大到足以面對黑法師的爪牙，更別說黑魔導士本人。連自己的奴才'奴才，作為一個事實問題。你將如何保護楓之谷世界在你目前的等級？");
    } else if (status == 10) {
        qm.sendNextPrev("雖然你已被錄入爵位，但你還不能被認為是騎士。 你不是官方騎士，因為你甚至不是訓練中的騎士。 如果你保持現有水平，你將只不過是#p1101000#皇家騎士團的掃地工。");
    } else if (status == 11) {
        qm.sendNextPrev("但是沒有人在第一天開始作為強大的騎士。 女皇不希望有人強大。 她想要一個有勇氣的人，她可以通過嚴格的訓練發展成為一個強大的騎士。 所以，你應該首先成為一名騎士訓練師。 當你達到這一點時，我們會談談你的任務。")
    } else if (status == 12) {
        qm.sendPrev("從左側的門戶到達訓練森林。 在那裡，你會找到#p1102000#，訓練師，他將教你如何變得更強大。 我不想讓你漫無目的地四處遊蕩，直到你到達Lv10，你聽到了嗎？");
        qm.safeDispose();
    }
}

function end(mode, type, selection) {}