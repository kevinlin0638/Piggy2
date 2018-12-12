/*      
 *  功能：萬能 NPC
 *  製作：Winter冬季
 *  時間：2018年06月15日
 */

var status;
var x0 = "#fEffect/CharacterEff/1112949/0/0#";//高音譜記號
var x1 = "#fEffect/CharacterEff/1112949/1/0#";//高音譜記號
var x2 = "#fEffect/CharacterEff/1112949/2/0#";//高音譜記號
var x3 = "#fEffect/CharacterEff/1112949/3/0#";//高音譜記號
var x4 = "#fEffect/CharacterEff/1112949/4/0#";//高音譜記號
var up = "#fUI/Login/CharSelect/icon/up#";
var new_ = "#fUI/Login/CharSelect/icon/new#";
var down = "#fUI/Login/CharSelect/icon/down#";
var same = "#fUI/Login/CharSelect/icon/same#";
var N = "#fUI/Login/WorldNotice/2/0#";

var Message = Array(
        Array("#fUI/Logo/1#", Array(//管理員
			Array("方便傳送", 9000003, "方便傳送"),
			Array("方便轉職", 9900003, "方便轉職"),
			Array("測試服領取", 9900007, "測試服領取")
			)),
        Array("#fUI/Logo/2#", Array(//用戶
			Array("帳戶", 9900007, "帳戶"),//待修復
			Array("傳送", 9900007, "傳送"),
			Array("寵物", 9900007, "home", "Pet"),
			Array("回收", 9900007, "回收"),
			//Array("公告", 9900007, "news"),
			//Array("楓武", 9900007, "楓武")
			Array("髮型", 1012117, "髮型"),
			//Array("髮型1", 1012117, "髮型1"),
			Array("臉型", 1012117, "臉型")
			)),
        Array("#fUI/Logo/3#", Array(//獎勵
			//Array("補償", 1012117, "補償"),
			Array("升級", 9900007, "升級"),
			Array("在線", 9900007, "在線"),
			Array("兌換", 9900007, "兌換"),
			Array("補償", 9900007, "加碼獎勵"),
			Array("序號", 9900007, "序號")
			//Array("瞬間移動", 9900007, "瞬間移動"),
			//Array("Facebook", 9900007, "FB分享"),
			//Array("封測", 9900007, "封測領取")
			)),
        Array("#fUI/Logo/4#", Array(//功能
			Array("轉生", 9900007, "轉生"),
			Array("排行", 9900007, "排行"),
			Array("附魔", 9900007, "附魔"),
			Array("分解", 9900007, "分解"),//待修復
			Array("技能", 9900007, "技能"),
			Array("爆率", 9900007, "爆率"),
			Array("相簿", 9900007, "相簿功能")
			//Array("物品兌換", 9900003, "兌換"),
			//Array("印章系統", 9900003, "印章"),
			//Array("轉／飛升", 9900007, "轉升"),
			//Array("隨機理髮", 9105006, null),
			//Array("建議回報", 9900007, "回報")
			))
    );
	
function start() {
    status = -1;
    action(1, 0, 0);
}

var skill = [	
4101004,
1,//施放毒氣
4,//小範圍西物
2,//治癒補血
1301007,
3,//治癒補魔
5,//大範圍西物
3121002,
3121000,
2311003,
6//全圖西物
];

var pet = [5000595,5000685,5000696,5000707,5000708,5000709,5000721,5000722,5000723,5000727,5000736,5000737,5000738,5000740,5000749,5000751,5000752,5000753,5000754,5000762,5000763,5000764,5000765,5000766,5000767,5000768,5000769,5000772,5000773,5000774,5000786,5000790,5000791,5000792,5000793,5000794,5000795,5000796,5000797,5000798,5000806,5000807,5000808,5000906,5000907,5000908,5000918,
5000919,5000920,5000921,5000922,5000923,5000924,5000925,5000945,5000953];
pet = [5000906,5000907,5000908];

var itemss = [1054002,1006002,1104002,1083002,1074002];
itemss = [1054003,1006003,1104003,1083003,1074003];



function action(mode, type, selection) {
var change = 1;

if (change == 1) {
	cm.dispose();
	cm.warp(9100000000, 0);
	//cm.openNpc(9900007, "開服加碼升級", "Player/List2");
	return;
} else if (change == 2) {
	cm.dispose();
	cm.openShop(95);
	return;
} else if (change == 3) {
	for (var t in pet) {
		cm.gainPetItem(pet[t], 1, 1);
		//cm.playerMessage(1, "購買成功: " + pet[t]);
	}
} else if (change == 4) {
	for (var t in pet) {
		cm.DELPetItem(pet[t]);
	}
} else if (change == 5) {
	cm.teachSkill(1004,1);
} else if (change == 6) {
	for (var t in itemss)
		cm.gainItem(itemss[t],1);
}
    if (mode == 0) {
		cm.dispose();
		return;
	} else if (cm.getPlayer().getLevel() < 10){
		cm.sendOk("妳目前的等級無法使用");
		cm.dispose();
		return;
    } else if (mode == 1){
		status++;
    } else {
		status--;
    }
    switch (status) {
        case 0:
			var UI = "#fUI/Logo/0#", AUI = "#fUI/Logo/1#";
			var admin = (cm.getChar().isGM()? "#r#L999#" + AUI: "\t\t　 " + UI);
			var text = "#e#b"+admin+"#n#k\r\n";
			var L = Message;
			for (var i in L) {
				//略過指令功能
				if (i == 0)
					continue;
				text += L[i][0];
				text += cor(i);
				for (var j in L[i][1]) {
					var 圖示 = L[i][1][j][0];
					if (j%6 == 0)
						text += "\r\n";
					text += "#L" + i+j + "#[" + 圖示 + "]#l";
				}
				if (i!=3)
					text += "\r\n\r\n\r\n";
            }
			cm.sendOk(text);
            break;
        case 1:
            var i = Math.floor(selection/10);
			var j = Math.floor(selection%10);
			cm.dispose();
			if (selection == 999) {
				cm.openNpc(9900007, "home", "Admin");
				return;
			}
			var NPC = Message[i][1][j][1];
			var 腳本 = Message[i][1][j][2];
			var 路徑 = Message[i][1][j][3];
			if (NPC == null)
				Packages.client.messages.CommandProcessor.processCommand(cm.getClient(), 腳本, Packages.constants.ServerConstants.CommandType.NORMAL);
			else if (腳本 == null)
				cm.openNpc(NPC);
			else if (路徑 != null)
				cm.openNpc(NPC, 腳本, 路徑);
			/*else if (腳本 == "news")
				cm.openNpc(NPC, "news_eve");*/
			else 
				cm.openNpc(NPC, 腳本, "Player/List"+i);
            break;
        case 2:
        case 3:
            cm.dispose();
            break;
    }
}

function cor(i) {
	var c = i%10;
    switch (c) {
        case 0:
			return "#d";
        case 1:
            return "#r";
        case 2:
			return "#b";
        case 3:
			return "#k";
		default:
            return "";
    }

}

var format = function FormatString(c, length, content) {
    var str = "";
    var cs = "";
    if (content.length > length) {
        str = content;
    } else {
        for (var j = 0; j < length - content.getBytes("big5").length; j++) {
            cs = cs + c;
        }
    }
    str = content + cs;
    return str;
}
