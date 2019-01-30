/* Mu Young
	Boss Balrog
*/


var status = -1;
var balrogMode; // false = easy, true = hard

function action(mode, type, selection) {
    switch (status) {
	case -1:
	    status = 0;
	    switch (cm.getChannelNumber()) {
		case 1:
		    balrogMode = true;
		    cm.sendNext("The channel you are currently staying is available for #bNormal Balrog Expedition Squad#k. If you wish to join a different mode, please select the correct channel. \n\r #b#i3994116# Ch.1 / Level 50 and above / 6 ~ 15 users \n#b#i3994115# The rest of the channel  / Level 50 ~ Level 70 / 3 ~ 6 users.");
		    break;
		default:
		    balrogMode = false;
		    cm.sendNext("The channel you are currently staying is available for #bEasy Balrog Expedition Squad#k. If you wish to join a different mode, please select the correct channel. \n\r #b#i3994116# Ch.1 / Level 50 and above / 6 ~ 15 users \n#b#i3994115# The rest of the channel  / Level 50 ~ Level 70 / 3 ~ 6 users.");
		    break;
	    }
	    break;
	case 0:
	    var em = cm.getEventManager(balrogMode ? "BossBalrog_NORMAL" : "BossBalrog_EASY");

	    if (em == null) {
		cm.sendOk("活動腳本尚未啟用,請聯絡GM.");
		cm.safeDispose();
		return;
	    }

	    if (cm.getParty() != null) {
	var prop = em.getProperty("state");
	    var marr = cm.getQuestRecord(balrogMode ? 160106 : 160105);
	    var data = marr.getCustomData();
	    if (data == null) {
		marr.setCustomData("0");
	        data = "0";
	    }
	    var time = parseInt(data);
	if (prop == null || prop.equals("0")) {
		var squadAvailability = cm.getSquadAvailability("BossBalrog");
		if (squadAvailability == -1) {
		    status = 1;
	    if (time + (6 * 3600000) >= cm.getCurrentTime() && !cm.getPlayer().isGM()) {
		cm.sendOk("您已經挑戰過 Balrog 在六小時内.剩餘時間: " + cm.getReadableMillis(cm.getCurrentTime(), time + (6 * 3600000)));
		cm.dispose();
		return;
	    }
		    cm.sendYesNo("Would you like to become the leader of the Balrog Expedition Squad?");

		} else if (squadAvailability == 1) {
	    if (time + (6 * 3600000) >= cm.getCurrentTime() && !cm.getPlayer().isGM()) {
		cm.sendOk("您已經挑戰過 Balrog 在六小時内.剩餘時間: " + cm.getReadableMillis(cm.getCurrentTime(), time + (6 * 3600000)));
		cm.dispose();
		return;
	    }
		    // -1 = Cancelled, 0 = not, 1 = true
		    var type = cm.isSquadLeader("BossBalrog");
		    if (type == -1) {
			cm.sendOk("遠征戰鬥已經開始");
			cm.safeDispose();
		    } else if (type == 0) {
			var memberType = cm.isSquadMember("BossBalrog");
			if (memberType == 2) {
			    cm.sendOk("您從遠征隊被剔除.");
			    cm.safeDispose();
			} else if (memberType == 1) {
			    status = 5;
			    cm.sendSimple("您想要做什麼? \r\n#b#L0#查看隊員#l \r\n#b#L1#加入遠征隊#l \r\n#b#L2#退出遠征隊#l");
			} else if (memberType == -1) {
			    cm.sendOk("遠征戰鬥已經開始");
			    cm.safeDispose();
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
			var eim = cm.getDisconnected(balrogMode ? "BossBalrog_NORMAL" : "BossBalrog_EASY");
			if (eim == null) {
				var squd = cm.getSquad("BossBalrog");
				if (squd != null) {
	    if (time + (6 * 3600000) >= cm.getCurrentTime() && !cm.getPlayer().isGM()) {
		cm.sendOk("您已經挑戰過 Balrog 在六小時内. 剩餘時間: " + cm.getReadableMillis(cm.getCurrentTime(), time + (6 * 3600000)));
		cm.dispose();
		return;
	    }
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
			var eim = cm.getDisconnected(balrogMode ? "BossBalrog_NORMAL" : "BossBalrog_EASY");
			if (eim == null) {
				var squd = cm.getSquad("BossBalrog");
				if (squd != null) {
	    if (time + (6 * 3600000) >= cm.getCurrentTime() && !cm.getPlayer().isGM()) {
		cm.sendOk("您已經挑戰過 Balrog 在六小時内. 剩餘時間: " + cm.getReadableMillis(cm.getCurrentTime(), time + (6 * 3600000)));
		cm.dispose();
		return;
	    }
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
		cm.sendPrev("You need a party.");
		cm.safeDispose();
	    }
	    break;
	case 1:
	    if (mode == 1) {
		if (!balrogMode) { // Easy Mode
		    var lvl = cm.getPlayerStat("LVL");
		    if (lvl >= 50 && lvl <= 70) {
			if (cm.registerSquad("BossBalrog", 5, " 您已經成為了遠征隊隊長. 請在時間內請隊員加入.")) {
				cm.sendOk("您已經成為了遠征隊隊長. 您有五分鐘集結時間, 請在時間內請隊員加入.");
			} else {
				cm.sendOk("Error, try again.");
			}
		    } else {
			cm.sendNext("A member of the party is not within the range of Levels 50 and 70. Please set up your party so that everyone fits the level limit.");
		    }
		} else { // Normal Mode
			if (cm.registerSquad("BossBalrog", 5, " 您已經成為了遠征隊隊長. 請在時間內請隊員加入.")) {
				cm.sendOk("您已經成為了遠征隊隊長. 您有五分鐘集結時間, 請在時間內請隊員加入.");
			} else {
				cm.sendOk("Error, try again.");
			}
		}
	    } else {
		cm.sendOk("如果您想要成為遠征隊長再跟我對話")
	    }
	    cm.safeDispose();
	    break;
	case 2:
		if (!cm.reAdd(balrogMode ? "BossBalrog_NORMAL" : "BossBalrog_EASY", "BossBalrog")) {
			cm.sendOk("錯誤.請在試一次.");
		}
		cm.safeDispose();
		break;
	case 3:
		if (mode == 1) {
			var squd = cm.getSquad("BossBalrog");
			if (squd != null && !squd.getAllNextPlayer().contains(cm.getPlayer().getName())) {
				squd.setNextPlayer(cm.getPlayer().getName());
				cm.sendOk("您獲得了保留位置.");
			}
		}
		cm.dispose();
		break;
	case 5:
	    if (selection == 0) {
		if (!cm.getSquadList("BossBalrog", 0)) {
		    cm.sendOk("發生未知錯誤.");
		    cm.safeDispose();
		} else {
		    cm.dispose();
		}
	    } else if (selection == 1) { // join
		var ba = cm.addMember("BossBalrog", true);
		if (ba == 2) {
		    cm.sendOk("遠征隊目前額滿,請稍後再試.");
		    cm.safeDispose();
		} else if (ba == 1) {
		    cm.sendOk("您成功加入遠征隊");
		    cm.safeDispose();
		} else {
		    cm.sendOk("您已經是遠征隊的一員.");
		    cm.safeDispose();
		}
	    } else {// withdraw
		var baa = cm.addMember("BossBalrog", false);
		if (baa == 1) {
		    cm.sendOk("你離開了遠征隊.");
		    cm.safeDispose();
		} else {
		    cm.sendOk("你並非遠征隊的一員.");
		    cm.safeDispose();
		}
	    }
	    break;
	case 10:
	    if (selection == 0) {
		if (!cm.getSquadList("BossBalrog", 0)) {
		    cm.sendOk("發生未知錯誤.");
		}
		cm.safeDispose();
	    } else if (selection == 1) {
		status = 11;
		if (!cm.getSquadList("BossBalrog", 1)) {
		    cm.sendOk("發生未知錯誤.");
		}
		cm.safeDispose();
	    } else if (selection == 2) {
		status = 12;
		if (!cm.getSquadList("BossBalrog", 2)) {
		    cm.sendOk("發生未知錯誤.");
		}
		cm.safeDispose();
	    } else if (selection == 3) { // get insode
		if (cm.getSquad("BossBalrog") != null) {
		    var dd = cm.getEventManager(balrogMode ? "BossBalrog_NORMAL" : "BossBalrog_EASY");
		    dd.startInstance(cm.getSquad("BossBalrog"), cm.getMap(), balrogMode ? 160106 : 160105);
		    cm.dispose();
		} else {
		    cm.sendOk("發生未知錯誤.");
		    cm.safeDispose();
		}
	    }
	    break;
	case 11:
	    cm.banMember("BossBalrog", selection);
	    cm.dispose();
	    break;
	case 12:
	    if (selection != -1) {
		cm.acceptMember("BossBalrog", selection);
	    }
	    cm.dispose();
	    break;
    }
}