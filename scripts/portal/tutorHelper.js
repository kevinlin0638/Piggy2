function enter(pi) {
    if (pi.getQuestStatus(20021) == 0) {
	pi.playerSummonHint(true);
	pi.summonMsg("歡迎來到小喵谷, 如果有任何問題歡迎雙擊我.");
//	pi.forceCompleteQuest(20100);
	pi.forceCompleteQuest(20021);
    }
}