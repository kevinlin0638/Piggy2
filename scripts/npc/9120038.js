/*
	NPC Name: 		Dida
	Map(s): 		2095 Park
	Description: 		Battle starter
 */
var status = -1;

function start() {
    if (cm.getMapId() == 802000310) {
	var em = cm.getEventManager("2095_tokyo");

	if (em == null) {
	    cm.sendOk("活動腳本尚未啟用,請聯絡GM.");
	    cm.dispose();
	    return;
	}
	var prop = em.getProperty("state");
	if (prop == null || prop.equals("0")) {
	var squadAvailability = cm.getSquadAvailability("tokyo_2095");
	if (squadAvailability == -1) {
	    status = 0;
	    cm.sendYesNo("您想要成為遠征隊隊長嗎?");

	} else if (squadAvailability == 1) {
	    // -1 = Cancelled, 0 = not, 1 = true
	    var type = cm.isSquadLeader("tokyo_2095");
	    if (type == -1) {
		cm.sendOk("遠征戰鬥已經開始");
		cm.dispose();
	    } else if (type == 0) {
		var memberType = cm.isSquadMember("tokyo_2095");
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
		cm.sendSimple("Your task is to acquire 10 energy transmitter.. \r\n#b#L0#查看隊員#l \r\n#b#L1#刪除隊員#l \r\n#b#L2#編輯對戰列表#l \r\n#r#L3#進入地圖#l \r\n#b#L4#I need Simplified Electric Pulse Transmitter#l");
	    }
    } else {
			var eim = cm.getDisconnected("2095_tokyo");
			if (eim == null) {
				var squd = cm.getSquad("tokyo_2095");
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
			var eim = cm.getDisconnected("2095_tokyo");
			if (eim == null) {
				var squd = cm.getSquad("tokyo_2095");
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
			if (cm.registerSquad("tokyo_2095", 5, " 您已經成為了遠征隊隊長. 請在時間內請隊員加入.")) {
				cm.sendOk("您已經成為了遠征隊隊長. 您有五分鐘集結時間, 請在時間內請隊員加入.");
			} else {
				cm.sendOk("發生錯誤.");
			}
	    }
	    cm.dispose();
	    break;
	case 2:
		if (!cm.reAdd("2095_tokyo", "tokyo_2095")) {
			cm.sendOk("錯誤.請在試一次.");
		}
		cm.safeDispose();
		break;
	case 3:
		if (mode == 1) {
			var squd = cm.getSquad("tokyo_2095");
			if (squd != null && !squd.getAllNextPlayer().contains(cm.getPlayer().getName())) {
				squd.setNextPlayer(cm.getPlayer().getName());
				cm.sendOk("您獲得了保留位置.");
			}
		}
		cm.dispose();
		break;
	case 5:
	    if (selection == 0) {
		if (!cm.getSquadList("tokyo_2095", 0)) {
		    cm.sendOk("發生未知錯誤.");
		}
	    } else if (selection == 1) { // join
		var ba = cm.addMember("tokyo_2095", true);
		if (ba == 2) {
		    cm.sendOk("遠征隊目前額滿,請稍後再試.");
		} else if (ba == 1) {
		    cm.sendOk("您成功加入遠征隊");
		} else {
		    cm.sendOk("您已經是遠征隊的一員.");
		}
	    } else {// withdraw
		var baa = cm.addMember("tokyo_2095", false);
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
		    if (!cm.getSquadList("tokyo_2095", 0)) {
			cm.sendOk("發生未知錯誤.");
		    }
		    cm.dispose();
		} else if (selection == 1) {
		    status = 11;
		    if (!cm.getSquadList("tokyo_2095", 1)) {
			cm.sendOk("發生未知錯誤.");
			cm.dispose();
		    }
		} else if (selection == 2) {
		    status = 12;
		    if (!cm.getSquadList("tokyo_2095", 2)) {
			cm.sendOk("發生未知錯誤.");
			cm.dispose();
		    }
		} else if (selection == 3) { // get insode
		    status = 13;
		    cm.sendNext("#b#t4032202##k, don't forget to place it in front of Marr.")
		} else if (selection == 4) { // Transmitter
		    status = 17;
		    cm.sendNext("Take this, It's an important piece of item from dad, but this should enough to drive that robot far away. Can you please leave it in front of Marr?");
		}
	    } else {
		cm.dispose();
	    }
	    break;
	case 11:
	    cm.banMember("tokyo_2095", selection);
	    cm.dispose();
	    break;
	case 12:
	    if (selection != -1) {
		cm.acceptMember("tokyo_2095", selection);
	    }
	    cm.dispose();
	    break;
	case 13:
	    status = 14;
	    cm.sendNextPrev("The opponent you are facing is using #b#t4032192##k as the driving force. You'll need to elliminate the enermies and gather up of them, then immediately send them to Marr, so she can run away.");
	    break;
	case 14:
	    status = 15;
	    cm.sendNextPrev("That should be no more than enough for 20 minutes. I suggest you run away within 20 minutes!");
	    break;
	case 15:
	    status = 16;
	    cm.sendNextPrev("The #b#t4032192##k you've gathered up should be handed to Marr by the Leader of the Squad!");
	    break;
	case 16:
	    if (cm.getSquad("tokyo_2095") != null) {
		var dd = cm.getEventManager("2095_tokyo");
		dd.startInstance(cm.getSquad("tokyo_2095"), cm.getMap());
	    } else {
		cm.sendOk("發生未知錯誤.");
	    }
	    cm.dispose();
	    break;
	case 17:
	    cm.gainItem(4032202, 1);
	    cm.sendNextPrev("Be careful. Please the item in front of marr in 6 minutes, or the mission is a failure.");
	    cm.dispose();
	    break;
	case 25:
	    cm.warp(802000210, 0);
	    cm.dispose();
	    break;
	default:
	    cm.dispose();
	    break;
    }
}