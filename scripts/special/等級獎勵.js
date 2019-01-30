/*      
 *  
 *  功能：等級送禮
 *  
 */

var status = 0;

var giftId = -1;
var giftToken = Array();
var gifts = null;
var debug = false;

var giftContent = Array(
			Array(2450000, 2, 0),//10
			Array(1003528, 1, 0),
			Array(2001502, 100, 0),
			Array(2001506, 100, 0),
			Array(0, 200000, 0),
			
			Array(2450000, 2, 1),//30
			Array(1004427, 1, 1),
			Array(1052166, 1, 1),
			Array(1072844, 1, 1),
			Array(1082536, 1, 1),
			Array(1102777, 1, 1),
			Array(1012325, 1, 1),
			Array(2001513, 100, 1),
			Array(2001514, 100, 1),
			Array(4001126, 300, 1),
			Array(0, 400000, 1),
			
			Array(2450000, 2, 2),//50
			Array(1022143, 1, 2),
			Array(1122153, 1, 2),
			Array(1102248, 1, 2),
			Array(1003787, 1, 2),
			Array(2001513, 200, 2),
			Array(2001514, 200, 2),
			Array(0, 500000, 2),
			
			
			Array(2450000, 2, 3),//70
			Array(5220000, 30, 3),
			Array(2001513, 200, 3),
			Array(2001514, 200, 3),
			Array(4001126, 700, 3),
			
			Array(2450022, 1, 4),//100
			Array(2022531, 1, 4),
			Array(1052893, 1, 4),
			Array(1102799, 1, 4),
			Array(1004404, 1, 4),
			Array(1122220, 1, 4),
			Array(1132209, 1, 4),
			Array(1032168, 1, 4),
			Array(1112794, 1, 4),
			Array(1152119, 1, 4),
			//Array(1143243, 1, 4),
			
			Array(2450022, 3, 5),//120
			Array(2022531, 3, 5),
			Array(2022179, 1, 5),
			Array(2046000 , 3, 5),
			Array(2046001 , 3, 5),
			Array(2046000 , 3, 5),
			Array(2046001 , 3, 5),
			
			Array(2450022, 3, 6),//140
			Array(2022531, 3, 6),
			Array(5062000, 200, 6),
			
			Array(2450022, 5, 7),//160
			Array(2022531, 5, 7),
			Array(2049301, 20, 7),
			Array(2022179, 3, 7),
			
			
			Array(2450022, 5, 8),//180
			Array(2022531, 5, 8),
			Array(5220000, 40, 8),
			
			//200
			Array(1022189, 1, 9),
			//Array(1143244, 1, 9),
			Array(2022179, 3, 9),
			Array(2049116, 3, 9),
			Array(2340000, 5, 9),
			
			//230
			Array(2049116, 5, 10),
			Array(2340000, 10, 10),
			Array(2022179, 5, 10),
			//Array(1143245, 5, 10),
			
			//250
			Array(2049116, 10, 11),
			Array(2340000, 15, 11),
			Array(2022179, 5, 11),
			//Array(1143246, 1, 11),
			
			//252
			Array(2049118, 10, 12),
			Array(2340000, 15, 12),
			Array(4310003, 10, 12),
			
			//252
			Array(2049118, 10, 13),
			Array(2340000, 15, 13),
			Array(4310003, 15, 13),
			
			//253
			Array(2049119, 10, 14),
			Array(2340000, 15, 14),
			Array(4310003, 15, 14),
			//254
			Array(2049120, 10, 15),
			Array(2340000, 15, 15),
			Array(4310003, 20, 15),
			Array(4310005, 3, 15),
			
			//255
			Array(2049120, 10, 16),
			Array(2340000, 15, 16),
			Array(4310005, 5, 16)
        );
		
		var bossid = 209487;
		var giftLevel = Array(10, 30, 50, 70, 100, 120, 140, 160, 180, 200, 230, 250, 251 ,252 ,253 ,254, 255);
		

function start() {
    status = -1;
    action(1, 0, 0);
	
	
}
		
function action(mode, type, selection) {
	if(debug && !cm.getPlayer().isGM()){
		cm.sendOk("NPC修復中!");
		cm.dispose();
        return;
	}

    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        var text = "";
        text += "嘿，我為您準備了不少好東西，等您到達相應等級時就可領取，另外點擊可以查看禮包内容呢，快搶先看看吧！\r\n";
		var counter = 0;
        for (var key in giftLevel) {
            var tips = "";
            giftToken[key] = false;
            if (cm.getChar().getLevel() >= giftLevel[key]) {
				var ss = bossid + counter;
                if (cm.getQuestStatus(ss) != 2) {
                    tips = "(可領取)";
                    giftToken[key] = true;
                } else {
                    tips = "#d(已領取)#b";
                }
            } else {
                tips = "#r(等級不足)#b";
            }
            text += "#b#L" + (parseInt(key)) + "#領取#r#e" + giftLevel[key] + "#n#b級等級禮包 " + tips + "#l#k\r\n";
			counter += 1;
        }
        cm.sendSimple(text);
    } else if (status == 1) {
        giftId = parseInt(selection);
        var text = "#r#e" + giftLevel[giftId] + "#n#b 級禮包內容：\r\n";
		
		/*if(giftLevel[giftId] >= 200 && !cm.getPlayer().isGM()){
			cm.sendOk("200級以上等級禮包 籌備中!!")
			cm.dispose();
			return;
		}*/
		
        gifts = getGift(giftId);
        for (var key in gifts) {
            var itemId = gifts[key][0];
            var itemQuantity = gifts[key][1];
			if(itemId == 0)
				text += "#b楓幣 " + itemQuantity +" 元#k\r\n";
			else
				text += "#v" + itemId + "##b#t" + itemId + "##k #rx " + itemQuantity + "#k\r\n";
        }
        text += "\r\n#d是否現在就領取該禮包？#k";
        cm.sendYesNo(text);
    } else if (status == 2) {
        if (giftId != -1 && gifts != null) {
            if (!cm.canHoldSlots(gifts.length)) {
                cm.sendOk("您的背包空間不足，請確認每個欄位至少" + gifts.length +"格的空間，以避免領取失敗。");
                cm.dispose();
                return;
            }
            var job = cm.getChar().getJob();
			var gg = bossid + giftId;
            if (giftToken[giftId] && cm.getQuestStatus(gg) == 0) {
                cm.forceCompleteQuest(gg);
                for (var key in gifts) {
                    var itemId = gifts[key][0];
                    var itemQuantity = gifts[key][1];
					switch(itemId){
						case 0:
							cm.gainMeso(itemQuantity);
							break;
						case 1143243:
							//itemid, str, dex, int, luk, watk, matk, expira, addi_slot
							cm.addWithPara(1143243, 10, 10, 10, 10, 15, 15, -1, 0);
							break;
						case 1143244:
							//itemid, str, dex, int, luk, watk, matk, expira, addi_slot
							cm.addWithPara(1143244, 20, 20, 20, 20, 25, 25, -1, 0);
							break;
						case 1143245:
							//itemid, str, dex, int, luk, watk, matk, expira, addi_slot
							cm.addWithPara(1143245, 30, 30, 30, 30, 35, 35, -1, 0);
							break;
						case 1143246:
							//itemid, str, dex, int, luk, watk, matk, expira, addi_slot
							cm.addWithPara(1143246, 45, 45, 45, 45, 50, 50, -1, 0);
							break;
						default:
							cm.gainItem(itemId, itemQuantity);
							break;
					}
                }
                cm.sendOk("恭喜您，領取成功！快打開包裹看看吧！");
                cm.dispose();
            } else {
                status = -1;
                cm.sendSimple("您已經領過了該禮包或者等級未達到要求，無法領取。");
            }
        } else {
            cm.sendOk("領取錯誤！請聯繫管理員！");
            cm.dispose();
        }
    }
}
function getGift(id) {
    var lastGiftContent = Array();
    for (var key in giftContent) {
        if (giftContent[key][2] == id)
            lastGiftContent.push(giftContent[key]);
    }
    return lastGiftContent;
}