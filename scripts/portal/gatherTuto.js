function enter(pi) {
    if (pi.haveItem(4001480)) {
    	if(pi.getPlayer().getEventCount("秘密礦場910001005") >= 3) {
			pi.getPlayer().dropMessage(5, "一個帳號最多進入此地圖三次");
			return;
		}
		pi.getPlayer().setEventCount("秘密礦場910001005");
		pi.warp(910001005,0);
		pi.gainItem(4001480, -1);
    } else if (pi.haveItem(4001481)) {
		if(pi.getPlayer().getEventCount("秘密礦場910001006") >= 3) {
			pi.getPlayer().dropMessage(5, "一個帳號最多進入此地圖三次");
			return;
		}
		pi.getPlayer().setEventCount("秘密礦場910001006");
		pi.warp(910001006, 0);
		pi.gainItem(4001481, -1);
    } else if (pi.haveItem(4001482)) {
		if(pi.getPlayer().getEventCount("秘密礦場910001003") >= 3) {
			pi.getPlayer().dropMessage(5, "一個帳號最多進入此地圖三次");
			return;
		}
		pi.getPlayer().setEventCount("秘密礦場910001003");
		pi.warp(910001003, 0);
		pi.gainItem(4001482, -1);
    } else if (pi.haveItem(4001483)) {
		if(pi.getPlayer().getEventCount("秘密礦場910001004") >= 3) {
			pi.getPlayer().dropMessage(5, "一個帳號最多進入此地圖三次");
			return;
		}
		pi.getPlayer().setEventCount("秘密礦場910001004");
		pi.warp(910001004, 0);
		pi.gainItem(4001483, -1);
    } else if (pi.isQuestActive(3197) || pi.isQuestActive(3198)) {
		pi.warp(910001002, 0);
    } else if (pi.isQuestActive(3195) || pi.isQuestActive(3196)) {
		pi.warp(910001001, 0);
    }
	pi.getPlayer().dropMessage(5,"您沒有任何票券");
}