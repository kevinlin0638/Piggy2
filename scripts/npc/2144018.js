var status = -1;
var boss_times = 10;
var boss_hard_times = 2;
var event_t = "阿卡次數";
var event_ht = "渾沌阿卡次數";

function start() {
	if (cm.getPlayer().getMapId() == 272030400) {
		cm.sendYesNo("您確定要出去了嗎?");
		status = 1;
		return;
	}
		if (cm.getPlayer().getLevel() < 120) {
			cm.sendOk("您需要到達120 等才可挑戰 Arkarium.");
			cm.dispose();
			return;
		}
    var em = cm.getEventManager("ArkariumBattle");

    if (em == null) {
	cm.sendOk("活動腳本尚未啟用,請聯絡GM.");
	cm.dispose();
	return;
    }
    var eim_status = em.getProperty("state");
	    var marr = cm.getQuestRecord(160111);
	    var data = marr.getCustomData();
	    if (data == null) {
		marr.setCustomData("0");
	        data = "0";
	    }
	    var time = parseInt(data);
	if (eim_status == null || eim_status.equals("0")) {
    var squadAvailability = cm.getSquadAvailability("Arkarium");
    if (squadAvailability == -1) {
	status = 0;
	    
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
	var type = cm.isSquadLeader("Arkarium");
	if (type == -1) {
	    cm.sendOk("遠征戰鬥已經開始");
	    cm.dispose();
	} else if (type == 0) {
	    var memberType = cm.isSquadMember("Arkarium");
	    if (memberType == 2) {
		cm.sendOk("您從遠征隊被剔除.");
		cm.dispose();
	    } else if (memberType == 1) {
		status = 5;
		cm.sendSimple("您想要做什麼? \r\n#b#L0#加入遠征隊#l \r\n#b#L1#離開遠征隊#l \r\n#b#L2#查看遠征隊員#l");
	    } else if (memberType == -1) {
		cm.sendOk("遠征戰鬥已經開始");
		cm.dispose();
	    } else {
		status = 5;
		cm.sendSimple("您想要做什麼? \r\n#b#L0#加入遠征隊#l \r\n#b#L1#離開遠征隊#l \r\n#b#L2#查看遠征隊員#l");
	    }
	} else { // Is leader
	    status = 10;
	    cm.sendSimple("您想要做什麼, 遠征隊隊長? \r\n#b#L0#查看隊員列表#l \r\n#b#L1#從遠征隊剔除#l \r\n#b#L2#從封鎖名單中刪除#l \r\n#r#L3#進入地圖#l");
	// TODO viewing!
	}
	    } else {
			var eim = cm.getDisconnected("ArkariumBattle");
			if (eim == null) {
				var squd = cm.getSquad("Arkarium");
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
			var eim = cm.getDisconnected("ArkariumBattle");
			if (eim == null) {
				var squd = cm.getSquad("Arkarium");
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
}

function action(mode, type, selection) {
    switch (status) {
	case 0:
	    if (mode == 1) {
			if (cm.registerSquad("Arkarium", 5, " 您已經成為了遠征隊隊長. 請在時間內請隊員加入.")) {
				cm.sendOk("您已經成為了遠征隊隊長. 您有五分鐘集結時間, 請在時間內請隊員加入.");
			} else {
				cm.sendOk("發生錯誤.");
			}
	    }
	    cm.dispose();
	    break;
	case 1:
	    if (mode == 1) {
		cm.warp(272020110, 0);
	    }
	    cm.dispose();
	    break;
	case 2:
		if (!cm.reAdd("ArkariumBattle", "Arkarium")) {
			cm.sendOk("錯誤.請在試一次.");
		}
		cm.safeDispose();
		break;
	case 3:
		if (mode == 1) {
			var squd = cm.getSquad("Arkarium");
			if (squd != null && !squd.getAllNextPlayer().contains(cm.getPlayer().getName())) {
				squd.setNextPlayer(cm.getPlayer().getName());
				cm.sendOk("您獲得了保留位置.");
			}
		}
		cm.dispose();
		break;
	case 5:
	    if (selection == 0) { // join
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
		var ba = cm.addMember("Arkarium", true);
		if (ba == 2) {
		    cm.sendOk("遠征隊目前額滿,請稍後再試.");
		} else if (ba == 1) {
		    cm.sendOk("您成功加入遠征隊");
		} else {
		    cm.sendOk("您已經是遠征隊的一員.");
		}
	    } else if (selection == 1) {// withdraw
		var baa = cm.addMember("Arkarium", false);
		if (baa == 1) {
		    cm.sendOk("你離開了遠征隊.");
		} else {
		    cm.sendOk("你並非遠征隊的一員.");
		}
	    } else if (selection == 2) {
		if (!cm.getSquadList("Arkarium", 0)) {
		    cm.sendOk("發生未知錯誤.");
		}
	    }
	    cm.dispose();
	    break;
	case 10:
	    if (mode == 1) {
		if (selection == 0) {
		    if (!cm.getSquadList("Arkarium", 0)) {
			cm.sendOk("發生未知錯誤.");
		    }
		    cm.dispose();
		} else if (selection == 1) {
		    status = 11;
		    if (!cm.getSquadList("Arkarium", 1)) {
			cm.sendOk("發生未知錯誤.");
			cm.dispose();
		    }
		} else if (selection == 2) {
		    status = 12;
		    if (!cm.getSquadList("Arkarium", 2)) {
			cm.sendOk("發生未知錯誤.");
			cm.dispose();
		    }
		} else if (selection == 3) { // get insode
		    if (cm.getSquad("Arkarium") != null) {
			var dd = cm.getEventManager("ArkariumBattle");
			if(cm.getChannelNumber() <= 10){
				cm.setSquadEventCount("Arkarium", event_t);
			}else{
				cm.setSquadEventCount("Arkarium", event_ht);
			}
			dd.startInstance(cm.getSquad("Arkarium"), cm.getMap(), 160111);
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
	    cm.banMember("Arkarium", selection);
	    cm.dispose();
	    break;
	case 12:
	    if (selection != -1) {
		cm.acceptMember("Arkarium", selection);
	    }
	    cm.dispose();
	    break;
    }
}