/*
	NPC Name: 		Ponicher
	Map(s): 		Neo Tokyo 2100 : Odaiba (802000210)
	Description: 		Vergamot Battle starter
*/
var status = -1;

function start() {
    if (cm.getMapId() == 802000210) {
	var em = cm.getEventManager("Vergamot");
	
	if (em == null) {
	    cm.sendOk("活動腳本尚未啟用,請聯絡GM.");
	    cm.dispose();
	    return;
	}
	//	var prop = em.getProperty("vergamotSummoned");

	//	if (((prop.equals("PQCleared") || (prop.equals("1")) && cm.getPlayerCount(802000211) == 0)) || prop.equals("0") || prop == null) {
	    	var prop = em.getProperty("state");
	if (prop == null || prop.equals("0")) {
	var squadAvailability = cm.getSquadAvailability("VERGAMOT");
	if (squadAvailability == -1) {
	    status = 0;
	    cm.sendYesNo("您想要成為遠征隊隊長嗎?");

	} else if (squadAvailability == 1) {
	    // -1 = Cancelled, 0 = not, 1 = true
	    var type = cm.isSquadLeader("VERGAMOT");
	    if (type == -1) {
		cm.sendOk("遠征戰鬥已經開始");
		cm.dispose();
	    } else if (type == 0) {
		var memberType = cm.isSquadMember("VERGAMOT");
		if (memberType == 2) {
		    cm.sendOk("您從遠征隊被剔除.");
		    cm.dispose();
		} else if (memberType == 1) {
		    status = 5;
		    cm.sendSimple("您想要做什麼? \r\n#b#L0#查看隊員#l \r\n#b#L1#加入遠征隊#l \r\n#b#L2#退出遠征隊#l");
		} else if (memberType == -1) {
		    cm.sendOk("遠征戰鬥已經開始");
		    cm.dispose();
		} else {
		    status = 5;
		    cm.sendSimple("您想要做什麼? \r\n#b#L0#查看隊員#l \r\n#b#L1#加入遠征隊#l \r\n#b#L2#退出遠征隊#l");
		}
	    } else { // Is leader
		status = 10;
		cm.sendSimple("您想要做什麼? \r\n#b#L0#查看隊員#l \r\n#b#L1#刪除隊員#l \r\n#b#L2#編輯對戰列表#l \r\n#r#L3#進入地圖#l");
	    // TODO viewing!
	    }
	    } else {
			var eim = cm.getDisconnected("Vergamot");
			if (eim == null) {
				var squd = cm.getSquad("VERGAMOT");
				if (squd != null) {
					cm.sendYesNo("遠征隊對戰已經開始.\r\n" + squd.getNextPlayer());
					status = 3;
				} else {
					cm.sendOk("遠征隊對戰已經開始.");
					cm.safeDispose();
				}
			} else {
				cm.sendYesNo("歐,您回來了!您要繼續遠征隊對戰嗎?");
				status = 2;
			}
	    }
	} else {
			var eim = cm.getDisconnected("Vergamot");
			if (eim == null) {
				var squd = cm.getSquad("VERGAMOT");
				if (squd != null) {
					cm.sendYesNo("遠征隊對戰已經開始.\r\n" + squd.getNextPlayer());
					status = 3;
				} else {
					cm.sendOk("遠征隊對戰已經開始.");
					cm.safeDispose();
				}
			} else {
				cm.sendYesNo("歐,您回來了!您要繼續遠征隊對戰嗎?");
				status = 2;
			}
	}
    } else {
	status = 25;
	cm.sendNext("Do you want to get out now?");
    }
}

function action(mode, type, selection) {
    switch (status) {
	case 0:
	    if (mode == 1) {
			if (cm.registerSquad("VERGAMOT", 5, " 您已經成為了遠征隊隊長. 請在時間內請隊員加入.")) {
				cm.sendOk("您已經成為了遠征隊隊長. 您有五分鐘集結時間, 請在時間內請隊員加入.");
			} else {
				cm.sendOk("發生錯誤.");
			}
	    }
	    cm.dispose();
	    break;
	case 2:
		if (!cm.reAdd("Vergamot", "VERGAMOT")) {
			cm.sendOk("錯誤.請在試一次.");
		}
		cm.safeDispose();
		break;
	case 3:
		if (mode == 1) {
			var squd = cm.getSquad("VERGAMOT");
			if (squd != null && !squd.getAllNextPlayer().contains(cm.getPlayer().getName())) {
				squd.setNextPlayer(cm.getPlayer().getName());
				cm.sendOk("您獲得了保留位置.");
			}
		}
		cm.dispose();
		break;
	case 5:
	    if (selection == 0) {
		if (!cm.getSquadList("VERGAMOT", 0)) {
		    cm.sendOk("發生未知錯誤.");
		}
	    } else if (selection == 1) { // join
		var ba = cm.addMember("VERGAMOT", true);
		if (ba == 2) {
		    cm.sendOk("遠征隊目前額滿,請稍後再試.");
		} else if (ba == 1) {
		    cm.sendOk("您成功加入遠征隊");
		} else {
		    cm.sendOk("您已經是遠征隊的一員.");
		}
	    } else {// withdraw
		var baa = cm.addMember("VERGAMOT", false);
		if (baa == 1) {
		    cm.sendOk("你離開了遠征隊.");
		} else {
		    cm.sendOk("你並非遠征隊的一員.");
		}
	    }
	    cm.dispose();
	    break;
	case 10:
	    if (mode == 1) {
		if (selection == 0) {
		    if (!cm.getSquadList("VERGAMOT", 0)) {
			cm.sendOk("發生未知錯誤.");
		    }
		    cm.dispose();
		} else if (selection == 1) {
		    status = 11;
		    if (!cm.getSquadList("VERGAMOT", 1)) {
			cm.sendOk("發生未知錯誤.");
			cm.dispose();
		    }
		} else if (selection == 2) {
		    status = 12;
		    if (!cm.getSquadList("VERGAMOT", 2)) {
			cm.sendOk("發生未知錯誤.");
			cm.dispose();
		    }
		} else if (selection == 3) { // get insode
		    if (cm.getSquad("VERGAMOT") != null) {
			var dd = cm.getEventManager("Vergamot");
			dd.startInstance(cm.getSquad("VERGAMOT"), cm.getMap());
		    } else {
			cm.sendOk("發生未知錯誤.");
		    }
		    cm.dispose();
		}
	    } else {
		cm.dispose();
	    }
	    break;
	case 11:
	    cm.banMember("VERGAMOT", selection);
	    cm.dispose();
	    break;
	case 12:
	    if (selection != -1) {
		cm.acceptMember("VERGAMOT", selection);
	    }
	    cm.dispose();
	    break;
	case 25:
	    cm.warp(802000210, 0);
	    cm.dispose();
	    break;
    }
}