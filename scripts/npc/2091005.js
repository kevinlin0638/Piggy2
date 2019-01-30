var status = -1;
var sel;
var mapid;
var level = 0;

/* sele == 5 */
var chrs;
var target;
/* sele == 5 */

function start() {
    mapid = cm.getMapId();

    if (mapid == 925020001) {
		cm.sendSimple("想要挑戰 #r武陵道場#k 嗎?#b \n\r #L1# 我想要進行 #r團隊挑戰#b#l\r\n#r#L5# 我想要將獎勵分給他人。#b#l\r\n#r#L6# 清除獎勵分配。#b#l\r\n#L2# 我要 #r換道具#b#l \n\r");
    } else if (isRestingSpot(mapid)) {
		cm.sendSimple("I'm amazed to know that you've safely reached up to this level. I can guarantee you, however, that it won't get any easier. What do you think? Do you want to keep going?#b \n\r #L0# Yes, I'll keep going.#l \n\r #L1# I want out#l \n\r #L2# I want to save my progress on record.#l");
    } else {
		cm.sendYesNo("什麼? 你想要放棄了嗎!? 挑戰成功有這麼好的福利呢!");
    }
}

function action(mode, type, selection) {
    if (mapid == 925020001) {
	if (mode == 1) {
	    status++;
	} else {
	    cm.dispose();
		return;
	}
	if (status == 0) {
	    sel = selection;

	    if (sel == 5) {
			var text = "請選擇欲分配的角色名字。\r\n#r請確認此人與你在#e同一個地圖#n\r\n且分配後#e無法#n有其他人分配給#e您#n!#b\r\n"
			
			chrs = cm.getPlayer().getMap().getCharacters()
			if(chrs.length == 1){
				cm.sendOk("地圖上沒有其他人!");
				cm.dispose();
				return;
			}
			for(var index = 0; index < chrs.length;index++){
				if(chrs[index].getName() != cm.getPlayer().getName())
					if(chrs[index].getShareID() == -1)
						text += "#L"+ index + "#"+(chrs[index].getName()) + "#l\r\n"
			}
			
			cm.sendSimple(text);
	    } else if (sel == 3) {
		cm.sendYesNo("You know if you reset your training points, then it'll return to 0, right? I can honestly say that it's not necessarily a bad thing. Once you reset your training points and start over again, then you'll be able to receive the belts once more. Do you want to reset your training points?");
	    } else if (sel == 6) {
			cm.getPlayer().setShareID(-1)
			cm.getPlayer().setSharePercent(-1)
			cm.sendOk("清除成功!");
			cm.dispose();
		}else if (sel == 2) {
			cm.dispose();
			cm.openNpc(2091005, "武陵道場兌換");
		} else if (sel == 1) {
			var text = "以下的玩家分配獎勵給您:\r\n"
			chrs = cm.getPlayer().getMap().getCharacters()
			for(var index = 0; index < chrs.length;index++){
				if(chrs[index].getName() != cm.getPlayer().getName())
					if(cm.getPlayer().getId() == chrs[index].getShareID())
						text += "#b"+(chrs[index].getName()) + " : #r" + chrs[index].getSharePercent() + "%\r\n"
			}
			cm.sendSimple(text + "\r\n#k請選擇模式\r\n#b#L0# 簡單模式(120等)#l\r\n#L1# 困難模式(150等)#l\r\n#L2##d 地獄模式(180等)#l\r\n#L3##r 夢魘模式(230等)#l\r\n")
	    } else if (sel == 0) {
		if (cm.getParty() != null) {
			cm.sendOk("Please leave your party.");
			cm.dispose();
		}
		var record = cm.getQuestRecord(150000);
		var data = record.getCustomData();

		if (data != null) {
		    var idd = get_restinFieldID(parseInt(data));
		    if (idd != 925020002) {
		        cm.dojoAgent_NextMap(true, true , idd );
		        record.setCustomData(null);
		    } else {
			cm.sendOk("Please try again later.");
		    }
		} else {
		    cm.start_DojoAgent(true, false, 2);
		}
		cm.dispose();
	    // cm.sendYesNo("The last time you took the challenge yourself, you were able to reach Floor #18. I can take you straight to that floor, if you want. Are you interested?");
	    }
	} else if (status == 1) {
	    if (sel == 3) {
		cm.setDojoRecord(true);
		cm.sendOk("I have resetted your training points to 0.");
	    } else if (sel == 2) {
			var record = cm.getDojoRecord();
			var required = 0;
			
			switch (record) {
				case 0:
				required = 500;
				break;
				case 1:
				required = 1500;
				break;
				case 2:
				required = 3000;
				break;
				case 3:
				required = 4500;
				break;
				case 4:
				required = 6000;
				break;
				case 5:
				required = 9000;
				break;
				case 6:
				required = 11000;
				break;
				case 7:
				required = 15000;
				break;
			}

			if (record == selection && cm.getDojoPoints() >= required) {
				var item = 1132000 + record;
				if (cm.canHold(item)) {
				cm.gainItem(item, 1);
				cm.setDojoRecord(false);
				} else {
				cm.sendOk("Please check if you have any available slot in your inventory.");
				}
			} else {
				cm.sendOk("You either already have it or insufficient training points. Do try getting the weaker belts first.");
			}
			cm.dispose();
	    } else if (sel == 1) {
			level = selection;
			if (cm.getParty() != null) {
				if (cm.isLeader()) {
					if (checkLevelsAndMap(120, 255) == 1) {
						cm.sendOk("隊伍裡有人等級不符合.");
						cm.dispose();
					} else if (checkLevelsAndMap(150, 255) == 2) {
						cm.sendOk("隊伍裡有其他人不在本地圖.");
						cm.dispose();
					} else if (checkLevelsAndMap(180, 255) == 3) {
						cm.sendOk("隊伍裡有其他人不在本頻道.");
						cm.dispose();
					}else{
						cm.sendYesNo("#b您要進去了嗎?\r\n\r\n#r請確認所有隊員已到位");
					}
				} else {
					cm.sendOk("你不是隊長，請您的隊長與我對話。");
					cm.dispose();
				}
			}else{
				cm.sendOk("請先進行組隊!");
				cm.dispose();
			}
	    }else if(sel == 5){
			target = chrs[selection];
			if(target != null){
				cm.sendGetNumber("將#b分配#k部分在武陵道場獲得的 #b楓點與道場點數 #k\r\n#r分配對象 : " + target.getName()+"\r\n#b#e請問您要分配多少百分比給他(#r最多 80%#b)",1,1,80);
			}else{
				cm.sendOk("請確認此人與您同一個地圖!");
				cm.dispose();
			}
		}
	}else if(status == 2){
		if(sel == 1){
			cm.start_DojoAgent(true, true, level);
			cm.dispose();
		}else if(sel == 5){
			cm.getPlayer().setShareID(target.getId());
			cm.getPlayer().setSharePercent(selection);
			cm.getPlayer().dropMessage(5,"您已將 " + selection +"% 在武陵道場中取得的 楓點與道場點數 分配給 "+ target.getName());
			target.dropMessage(5,cm.getPlayer().getName()+"已將 " + selection +"% 在武陵道場中取得的 楓點與道場點數 分配給您");
			cm.dispose();
		}
	}
    } else if (isRestingSpot(mapid)) {
	if (mode == 1) {
	    status++;
	} else {
	    cm.dispose();
	    return;
	}

	if (status == 0) {
	    sel = selection;

	    if (sel == 0) {
		if (cm.getParty() == null || cm.isLeader()) {
		    cm.dojoAgent_NextMap(true, true);
		} else {
		    cm.sendOk("Only the leader may go on.");
		}
		//cm.getQuestRecord(150000).setCustomData(null);
		cm.dispose();
	    } else if (sel == 1) {
		cm.askAcceptDecline("Do you want to quit? You really want to leave here?");
	    } else if (sel == 2) {
		if (cm.getParty() == null) {
			var stage = get_stageId(cm.getMapId());

			cm.getQuestRecord(150000).setCustomData(stage);
			cm.sendOk("I have just recorded your progress. The next time you get here, I'll sent you directly to this level.");
			cm.dispose();
		} else {
			cm.sendOk("Hey.. you can't record your progress with a team...");
			cm.dispose();
		}
	    }
	} else if (status == 1) {
	    if (sel == 1) {
		if (cm.isLeader()) {
			cm.warpParty(925020002);
		} else {
			cm.warp(925020002);
		}
	    }
	    cm.dispose();
	}
    } else {
	if (mode == 1) {
		if (cm.isLeader()) {
			cm.warpParty(925020001);
		} else {
			cm.warp(925020001);
		}
	}
	cm.dispose();
    }
}

function get_restinFieldID(id) {
	var idd = 925020002;
    switch (id) {
	case 1:
	    idd =  925020600;
	    break;
	case 2:
	    idd =  925021200;
	    break;
	case 3:
	    idd =  925021800;
	    break;
	case 4:
	    idd =  925022400;
	    break;
	case 5:
	    idd =  925023000;
	    break;
	case 6:
	    idd =  925023600;
	    break;
    }
    for (var i = 0; i < 10; i++) {
	var canenterr = true;
	for (var x = 1; x < 39; x++) {
		var map = cm.getMap(925020000 + 100 * x + i);
		if (map.getCharactersSize() > 0) {
			canenterr = false;
			break;
		}
	}
	if (canenterr) {
		idd += i;
		break;
	}
}
	return idd;
}

function get_stageId(mapid) {
    if (mapid >= 925020600 && mapid <= 925020614) {
	return 1;
    } else if (mapid >= 925021200 && mapid <= 925021214) {
	return 2;
    } else if (mapid >= 925021800 && mapid <= 925021814) {
	return 3;
    } else if (mapid >= 925022400 && mapid <= 925022414) {
	return 4;
    } else if (mapid >= 925023000 && mapid <= 925023014) {
	return 5;
    } else if (mapid >= 925023600 && mapid <= 925023614) {
	return 6;
    }
    return 0;
}

function isRestingSpot(id) {
    return (get_stageId(id) > 0);
}

function checkLevelsAndMap(lowestlevel, highestlevel) {
	var party = cm.getParty().getMembers();
	var mapId = cm.getMapId();
	var Ch = cm.getClient().getChannel();
	var valid = 0;
	var inMap = 0;

	var it = party.iterator();
	while (it.hasNext()) {
		var cPlayer = it.next();
		if (!(cPlayer.getLevel() >= lowestlevel && cPlayer.getLevel() <= highestlevel)) {
			valid = 1;
		}
		if (cPlayer.getMapid() != mapId) {
			valid = 2;
		}
		if (cPlayer.getChannel() != Ch) {
			valid = 3;
		}
	}
	return valid;
}