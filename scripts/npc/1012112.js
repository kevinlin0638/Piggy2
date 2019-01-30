var status = -1;
var minLevel = 20; // 35
var maxLevel = 255; // 65

var minPartySize = 2;
var maxPartySize = 6;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 0) {
	    cm.dispose();
	    return;
	}
	status--;
    }
    if (cm.getPlayer().getMapId() != 910010500) {
	if (status == 0) {
	    cm.sendYesNo("您想要去組隊任務的地圖嗎?");
	} else {
	    cm.saveLocation("MULUNG_TC");
	    cm.warp(910010500,0);
	    cm.dispose();
	}
	return;
    }
    if (status == 0) {
	if (cm.getParty() == null) { // No Party
	    cm.sendSimple("您沒有組隊? . 如果你想要挑戰,麻煩請 #b隊長#k 與我對話.\r\n\r\n#需求: " + minPartySize + " 組隊隊員,成員等級 " + minLevel + " 到 " + maxLevel + ".#b\r\n#L0#我想要年糕帽.#l");
	} else if (!cm.isLeader()) { // Not Party Leader
	    cm.sendSimple("如果你想要挑戰,麻煩請 #b隊長#k 與我對話.\r\n#L0#我想要年糕帽.#l");
	} else {
	    // Check if all party members are within PQ levels
	    var party = cm.getParty().getMembers();
	    var mapId = cm.getMapId();
	    var next = true;
	    var levelValid = 0;
	    var inMap = 0;
	    var it = party.iterator();

	    while (it.hasNext()) {
		var cPlayer = it.next();
		if ((cPlayer.getLevel() >= minLevel) && (cPlayer.getLevel() <= maxLevel)) {
		    levelValid += 1;
		} else {
		    next = false;
		}
		if (cPlayer.getMapid() == mapId) {
		    inMap += (cPlayer.getJobId() == 900 ? 6 : 1);
		}
	    }
	    if (party.size() > maxPartySize || inMap < minPartySize) {
		next = false;
	    }
	    if (next) {
		var em = cm.getEventManager("HenesysPQ");
		if (em == null) {
		    cm.sendSimple("發生錯誤,請向粉專回報.#b\r\n#L0#我想要年糕帽.#l");
		} else {
		    var prop = em.getProperty("state");
		    if (prop.equals("0") || prop == null) {
			em.startInstance(cm.getParty(), cm.getMap(), 70);
			cm.removeAll(4001101);
			cm.dispose();
			return;
		    } else {
			cm.sendSimple("另一個隊伍正在挑戰中. 請換頻道再試, 或等到此隊伍結束.#b\r\n#L0#我想要年糕帽.#");
		    }
		}
	    } else {
		cm.sendSimple("您有隊員不符合資格:\r\n\r\n#r要求: " + minPartySize + " 隊員, 成員等級 " + minLevel + " 到 " + maxLevel  + ".#b\r\n#L0#I want the Rice Cake Hat.#l");
	    }
	}
    } else { //broken glass
	if (cm.haveItem(1002798,1)) {
		if (!cm.canHold(1003266,1)) {
			cm.sendOk("請空出背包空間.");
		} else if (cm.haveItem(4001101,20) && cm.isGMS()) { //TODO JUMP
			cm.gainItem(1003266, 1);
			cm.gainItem(4001101, -20);
		} else {
			cm.sendOk("取得 20 個年糕再回來ㄅ.");
		}
	} else if (!cm.canHold(1002798,1)) {
	    cm.sendOk("請空出背包空間.");
	} else if (cm.haveItem(4001101,10)) {
	    cm.gainItem(4001101,-10); //should handle automatically for "have"
	    cm.gainItem(1002798,1);
	} else {
	    cm.sendOk("請確認您有 10 個年糕.");
	}
	cm.dispose();

    }
}