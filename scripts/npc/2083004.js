/*
	NPC Name: 		Mark of the Squad
	Map(s): 		Entrance to Horned Tail's Cave
	Description: 		Horntail Battle starter
*/
var status = -1;
var event_s = "HorntailBattle";
var squal_s = "Horntail";
var level_res = 80;
var quest = 160100;



function start() {
	if(cm.getChannelNumber() > 10){
		event_s = "ChaosHorntail";
		squal_s = "ChaosHT"
		level_res = 140;
		quest = 160103;
	}
		if (cm.getPlayer().getLevel() < level_res) {
			cm.sendOk("您需要到達"+ level_res +" 等才可挑戰 Horntail.");
			cm.dispose();
			return;
		}
    var em = cm.getEventManager(event_s);

    if (em == null) {
	cm.sendOk("活動腳本尚未啟用,請聯絡GM.");
	cm.dispose();
	return;
    }
    var prop = em.getProperty("state");

	    var marr = cm.getQuestRecord(quest);
	    var data = marr.getCustomData();
	    if (data == null) {
		marr.setCustomData("0");
	        data = "0";
	    }
	    var time = parseInt(data);
    if (prop == null || prop.equals("0")) {
	var squadAvailability = cm.getSquadAvailability(squal_s);
	if (squadAvailability == -1) {
	    status = 0;
	    cm.sendYesNo("您想要成為遠征隊隊長嗎?");

	} else if (squadAvailability == 1) {
	    // -1 = Cancelled, 0 = not, 1 = true
	    var type = cm.isSquadLeader(squal_s);
	    if (type == -1) {
		cm.sendOk("遠征隊已解散, 請重新組織.");
		cm.dispose();
	    } else if (type == 0) {
		var memberType = cm.isSquadMember(squal_s);
		if (memberType == 2) {
		    cm.sendOk("你被從遠征隊剔除.");
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
			var eim = cm.getDisconnected(event_s);
			if (eim == null) {
				var squd = cm.getSquad(squal_s);
				if (squd != null) {
					cm.sendYesNo("遠征戰鬥已經開始.\r\n" + squd.getNextPlayer());
					status = 3;
				} else {
					cm.sendOk("遠征戰鬥已經開始.");
					cm.safeDispose();
				}
			} else {
				cm.sendYesNo("歐,你回來ㄌ.您要重新加入戰鬥嗎?");
				status = 1;
			}
	}
    } else {
			var eim = cm.getDisconnected(event_s);
			if (eim == null) {
				var squd = cm.getSquad(squal_s);
				if (squd != null) {
					cm.sendYesNo("遠征戰鬥已經開始.\r\n" + squd.getNextPlayer());
					status = 3;
				} else {
					cm.sendOk("遠征戰鬥已經開始.");
					cm.safeDispose();
				}
			} else {
				cm.sendYesNo("歐,你回來ㄌ.您要重新加入戰鬥嗎?");
				status = 1;
			}
    }
}

function action(mode, type, selection) {
    switch (status) {
	case 0:
	    	if (mode == 1) {
			if (cm.registerSquad(squal_s, 5, " 您已經成為了遠征隊隊長 . 請在時間內請隊員加入.")) {
				cm.sendOk("您已經成為了遠征隊隊長. 您有五分鐘集結時間, 請在時間內請隊員加入.");
			} else {
				cm.sendOk("發生錯誤.");
			}
	    	}
	    cm.dispose();
	    break;
	case 1:
		if (!cm.reAdd(event_s, squal_s)) {
			cm.sendOk("錯誤.請在試一次.");
		}
		cm.safeDispose();
		break;
	case 3:
		if (mode == 1) {
			var squd = cm.getSquad(squal_s);
			if (squd != null && !squd.getAllNextPlayer().contains(cm.getPlayer().getName())) {
				squd.setNextPlayer(cm.getPlayer().getName());
				cm.sendOk("您獲得了保留位置.");
			}
		}
		cm.dispose();
		break;
	case 5:
	    if (selection == 0) {
		if (!cm.getSquadList(squal_s, 0)) {
		    cm.sendOk("發生未知錯誤.");
		}
	    } else if (selection == 1) { // join
		var ba = cm.addMember(squal_s, true);
		if (ba == 2) {
		    cm.sendOk("遠征隊目前額滿,請稍後再試.");
		} else if (ba == 1) {
		    cm.sendOk("您成功加入遠征隊");
		} else {
		    cm.sendOk("您已經是遠征隊的一員.");
		}
	    } else {// withdraw
		var baa = cm.addMember(squal_s, false);
		if (baa == 1) {
		    cm.sendOk("你離開了遠征隊");
		} else {
		    cm.sendOk("你並非遠征隊的一員.");
		}
	    }
	    cm.dispose();
	    break;
	case 10:
	    if (mode == 1) {
		if (selection == 0) {
		    if (!cm.getSquadList(squal_s, 0)) {
			cm.sendOk("發生未知錯誤.");
		    }
		    cm.dispose();
		} else if (selection == 1) {
		    status = 11;
		    if (!cm.getSquadList(squal_s, 1)) {
			cm.sendOk("發生未知錯誤.");
			cm.dispose();
		    }
		} else if (selection == 2) {
		    status = 12;
		    if (!cm.getSquadList(squal_s, 2)) {
			cm.sendOk("發生未知錯誤.");
			cm.dispose();
		    }
		} else if (selection == 3) { // get insode
		    if (cm.getSquad(squal_s) != null) {
			var dd = cm.getEventManager(event_s);
			dd.startInstance(cm.getSquad(squal_s), cm.getMap(), 160100);
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
	    cm.banMember(squal_s, selection);
	    cm.dispose();
	    break;
	case 12:
	    if (selection != -1) {
		cm.acceptMember(squal_s, selection);
	    }
	    cm.dispose();
	    break;
	default:
	    cm.dispose();
	    break;
    }
}