/*
	NPC Name: 		Adobis
	Map(s): 		El Nath : Entrance to Zakum Altar
	Description: 		Zakum battle starter
*/
var status = 0;
var boss_times = 10;
var boss_hard_times = 2;
var event_t = "炎魔次數";
var event_ht = "渾沌炎魔次數";

function action(mode, type, selection) {
	if (cm.getPlayer().getMapId() == 211042200) {
		if (selection < 100) {
			cm.sendSimple("#r#L100#普通炎魔#l\r\n#L101#混沌炎魔#l");
		} else {
			if (selection == 100) {
				if(cm.getChannelNumber() > 10){
					cm.sendOk("渾沌需頻道10以內");
					cm.dispose();
					return;
				}
				cm.warp(211042300,0);
			} else if (selection == 101) {
				if(cm.getChannelNumber() <= 10){
					cm.sendOk("渾沌需頻道11以後");
					cm.dispose();
					return;
				}
				cm.warp(211042301,0);
			}
			cm.dispose();
		}
		return;
	} else if (cm.getPlayer().getMapId() == 211042401) {
    switch (status) {
	case 0:
		if (cm.getPlayer().getLevel() < 100) {
			cm.sendOk("您需要到達100 等才可挑戰 Chaos Zakum.");
			cm.dispose();
			return;
		}
	    var em = cm.getEventManager("ChaosZakum");

	    if (em == null) {
		cm.sendOk("活動腳本尚未啟用,請聯絡GM.");
		cm.safeDispose();
		return;
	    }
	var prop = em.getProperty("state");
	    var marr = cm.getQuestRecord(160102);
	    var data = marr.getCustomData();
	    if (data == null) {
		marr.setCustomData("0");
	        data = "0";
	    }
	    var time = parseInt(data);
	if (prop == null || prop.equals("0")) {
	    if(cm.getChannelNumber() <= 10){
			if (cm.getEventCount(event_t) >= boss_times) {
				cm.sendNext("很抱歉每天只能打" + boss_times + "次..");
				cm.dispose();
				return;
			}
		}else{
			if (cm.getEventCount(event_ht) >= boss_hard_times) {
				cm.sendNext("很抱歉每天只能打" + boss_hard_times + "次..");
				cm.dispose();
				return;
			}
		}
		cm.sendYesNo("您想要成為遠征隊隊長嗎?");

	    } else if (squadAvailability == 1) {
	    if(cm.getChannelNumber() <= 10){
			if (cm.getEventCount(event_t) >= boss_times) {
				cm.sendNext("很抱歉每天只能打" + boss_times + "次..");
				cm.dispose();
				return;
			}
		}else{
			if (cm.getEventCount(event_ht) >= boss_hard_times) {
				cm.sendNext("很抱歉每天只能打" + boss_hard_times + "次..");
				cm.dispose();
				return;
			}
		}
		// -1 = Cancelled, 0 = not, 1 = true
		var type = cm.isSquadLeader("ChaosZak");
		if (type == -1) {
		    cm.sendOk("遠征戰鬥已經開始");
		    cm.safeDispose();
		} else if (type == 0) {
		    var memberType = cm.isSquadMember("ChaosZak");
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
			var eim = cm.getDisconnected("ChaosZakum");
			if (eim == null) {
				var squd = cm.getSquad("ChaosZak");
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
	    break;
	case 1:
	    	if (mode == 1) {
			if (cm.registerSquad("ChaosZak", 5, " 您已經成為了遠征隊隊長 (Chaos). 請在時間內請隊員加入.")) {
				cm.sendOk("您已經成為了遠征隊隊長. 您有五分鐘集結時間, 請在時間內請隊員加入.");
			} else {
				cm.sendOk("發生錯誤.");
			}
	    	} else {
			cm.sendOk("如果您想要成為遠征隊長再跟我對話")
	    	}
	    cm.safeDispose();
	    break;
	case 2:
		if (!cm.reAdd("ChaosZakum", "ChaosZak")) {
			cm.sendOk("錯誤.請在試一次.");
		}
		cm.dispose();
		break;
	case 3:
		if (mode == 1) {
			var squd = cm.getSquad("ChaosZak");
			if (squd != null && !squd.getAllNextPlayer().contains(cm.getPlayer().getName())) {
				squd.setNextPlayer(cm.getPlayer().getName());
				cm.sendOk("您獲得了保留位置.");
			}
		}
		cm.dispose();
		break;
	case 5:
	    if (selection == 0) {
		if (!cm.getSquadList("ChaosZak", 0)) {
		    cm.sendOk("發生未知錯誤.");
		    cm.safeDispose();
		} else {
		    cm.dispose();
		}
	    } else if (selection == 1) { // join
			if(cm.getChannelNumber() <= 10){
				if (cm.getEventCount(event_t) >= boss_times) {
					cm.sendNext("很抱歉每天只能打" + boss_times + "次..");
					cm.dispose();
					return;
				}
			}else{
				if (cm.getEventCount(event_ht) >= boss_hard_times) {
					cm.sendNext("很抱歉每天只能打" + boss_hard_times + "次..");
					cm.dispose();
					return;
				}
			}
		var ba = cm.addMember("ChaosZak", true);
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
		var baa = cm.addMember("ChaosZak", false);
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
		if (!cm.getSquadList("ChaosZak", 0)) {
		    cm.sendOk("發生未知錯誤.");
		}
		cm.safeDispose();
	    } else if (selection == 1) {
		status = 11;
		if (!cm.getSquadList("ChaosZak", 1)) {
		    cm.sendOk("發生未知錯誤.");
		cm.safeDispose();
		}

	    } else if (selection == 2) {
		status = 12;
		if (!cm.getSquadList("ChaosZak", 2)) {
		    cm.sendOk("發生未知錯誤.");
		cm.safeDispose();
		}

	    } else if (selection == 3) { // get insode
		if (cm.getSquad("ChaosZak") != null) {
		    var dd = cm.getEventManager("ChaosZakum");
			if(cm.getChannelNumber() <= 10){
				cm.setSquadEventCount("ChaosZak", event_t);
			}else{
				cm.setSquadEventCount("ChaosZak", event_ht);
			}
		    dd.startInstance(cm.getSquad("ChaosZak"), cm.getMap(), 160102);
		    cm.dispose();
		} else {
		    cm.sendOk("發生未知錯誤.");
		    cm.safeDispose();
		}
	    }
	    break;
	case 11:
	    cm.banMember("ChaosZak", selection);
	    cm.dispose();
	    break;
	case 12:
	    if (selection != -1) {
		cm.acceptMember("ChaosZak", selection);
	    }
	    cm.dispose();
	    break;
    }
	} else {
    switch (status) {
	case 0:
		if (cm.getPlayer().getLevel() < 50) {
			cm.sendOk("您需要到達50 等才可挑戰 Zakum.");
			cm.dispose();
			return;
		}
	    var em = cm.getEventManager("ZakumBattle");

	    if (em == null) {
		cm.sendOk("活動腳本尚未啟用,請聯絡GM.");
		cm.safeDispose();
		return;
	    }
	var prop = em.getProperty("state");
	    var marr = cm.getQuestRecord(160101);
	    var data = marr.getCustomData();
	    if (data == null) {
		marr.setCustomData("0");
	        data = "0";
	    }
	    var time = parseInt(data);
	if (prop == null || prop.equals("0")) {
	    var squadAvailability = cm.getSquadAvailability("ZAK");
	    if (squadAvailability == -1) {
		status = 1;
	    if(cm.getChannelNumber() <= 10){
			if (cm.getEventCount(event_t) >= boss_times) {
				cm.sendNext("很抱歉每天只能打" + boss_times + "次..");
				cm.dispose();
				return;
			}
		}else{
			if (cm.getEventCount(event_ht) >= boss_hard_times) {
				cm.sendNext("很抱歉每天只能打" + boss_hard_times + "次..");
				cm.dispose();
				return;
			}
		}
		cm.sendYesNo("您想要成為遠征隊隊長嗎?");

	    } else if (squadAvailability == 1) {
	    if(cm.getChannelNumber() <= 10){
			if (cm.getEventCount(event_t) >= boss_times) {
				cm.sendNext("很抱歉每天只能打" + boss_times + "次..");
				cm.dispose();
				return;
			}
		}else{
			if (cm.getEventCount(event_ht) >= boss_hard_times) {
				cm.sendNext("很抱歉每天只能打" + boss_hard_times + "次..");
				cm.dispose();
				return;
			}
		}
		// -1 = Cancelled, 0 = not, 1 = true
		var type = cm.isSquadLeader("ZAK");
		if (type == -1) {
		    cm.sendOk("遠征戰鬥已經開始");
		    cm.safeDispose();
		} else if (type == 0) {
		    var memberType = cm.isSquadMember("ZAK");
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
			var eim = cm.getDisconnected("ZakumBattle");
			if (eim == null) {
				var squd = cm.getSquad("ZAK");
				if (squd != null) {
					cm.sendYesNo("遠征隊對戰已經開始.\r\n" + squd.getNextPlayer());
					status = 3;
				} else {
					cm.sendOk("遠征隊對戰已經開始.");
					cm.safeDispose();
				}
			} else {
				cm.sendYesNo("歐,您回來了!您要繼續遠征隊對戰嗎?");
				status = 1;
			}
	    }
	} else {
			var eim = cm.getDisconnected("ZakumBattle");
			if (eim == null) {
				var squd = cm.getSquad("ZAK");
				if (squd != null) {
					cm.sendYesNo("遠征隊對戰已經開始.\r\n" + squd.getNextPlayer());
					status = 3;
				} else {
					cm.sendOk("遠征隊對戰已經開始.");
					cm.safeDispose();
				}
			} else {
				cm.sendYesNo("歐,您回來了!您要繼續遠征隊對戰嗎?");
				status = 1;
			}
	}
	    break;
	case 1:
	    	if (mode == 1) {
			if (cm.registerSquad("ZAK", 5, " 您已經成為了遠征隊隊長 (Regular). 請在時間內請隊員加入.")) {
				cm.sendOk("您已經成為了遠征隊隊長. 您有五分鐘集結時間, 請在時間內請隊員加入.");
			} else {
				cm.sendOk("發生錯誤.");
			}
	    	} else {
			cm.sendOk("如果您想要成為遠征隊長再跟我對話")
	    	}
	    cm.safeDispose();
	    break;
	case 2:
		if (!cm.reAdd("ZakumBattle", "ZAK")) {
			cm.sendOk("錯誤.請在試一次.");
		}
		cm.safeDispose();
		break;
	case 3:
		if (mode == 1) {
			var squd = cm.getSquad("ZAK");
			if (squd != null && !squd.getAllNextPlayer().contains(cm.getPlayer().getName())) {
				squd.setNextPlayer(cm.getPlayer().getName());
				cm.sendOk("您獲得了保留位置.");
			}
		}
		cm.dispose();
		break;
	case 5:
	    if (selection == 0) {
		if (!cm.getSquadList("ZAK", 0)) {
		    cm.sendOk("發生未知錯誤.");
		    cm.safeDispose();
		} else {
		    cm.dispose();
		}
	    } else if (selection == 1) { // join
			if(cm.getChannelNumber() <= 10){
				if (cm.getEventCount(event_t) >= boss_times) {
					cm.sendNext("很抱歉每天只能打" + boss_times + "次..");
					cm.dispose();
					return;
				}
			}else{
				if (cm.getEventCount(event_ht) >= boss_hard_times) {
					cm.sendNext("很抱歉每天只能打" + boss_hard_times + "次..");
					cm.dispose();
					return;
				}
			}
		var ba = cm.addMember("ZAK", true);
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
		var baa = cm.addMember("ZAK", false);
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
		if (!cm.getSquadList("ZAK", 0)) {
		    cm.sendOk("發生未知錯誤.");
		}
		cm.safeDispose();
	    } else if (selection == 1) {
		status = 11;
		if (!cm.getSquadList("ZAK", 1)) {
		    cm.sendOk("發生未知錯誤.");
		cm.safeDispose();
		}

	    } else if (selection == 2) {
		status = 12;
		if (!cm.getSquadList("ZAK", 2)) {
		    cm.sendOk("發生未知錯誤.");
		cm.safeDispose();
		}

	    } else if (selection == 3) { // get insode
		if (cm.getSquad("ZAK") != null) {
		    var dd = cm.getEventManager("ZakumBattle");
			
			if(cm.getChannelNumber() <= 10){
				cm.setSquadEventCount("ZAK", event_t);
			}else{
				cm.setSquadEventCount("ZAK", event_ht);
			}
		    dd.startInstance(cm.getSquad("ZAK"), cm.getMap(), 160101);
		    cm.dispose();
		} else {
		    cm.sendOk("發生未知錯誤.");
		    cm.safeDispose();
		}
	    }
	    break;
	case 11:
	    cm.banMember("ZAK", selection);
	    cm.dispose();
	    break;
	case 12:
	    if (selection != -1) {
		cm.acceptMember("ZAK", selection);
	    }
	    cm.dispose();
	    break;
    }
	}
}