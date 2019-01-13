// 萬能NPC


var status;
var blue_heart = "#fEffect/CharacterEff/1112902/0/0#";
var pony_heart = "#fEffect/CharacterEff/1112904/0/0#";
var pink_heart = "#fEffect/CharacterEff/1112903/0/0#";

var NPC_List_1 = [ //id, text
	[0, 0, "帳號資訊"],
	[1012117, 0, "萬能造型師"],
	/*[9330003, 2, "貨幣兌換"],
	[9330003, 5, "新手專區"],
	[9330003, 9, "成就系統"],*/
	[9330003, 11, "每日任務"],
	[9330003, "home", "萬能轉職"],
	[9310055, null, "#r地圖傳送#b"],
	[9330003, "等級獎勵", "等級獎勵"],
	//[9330003, 17, "我要變性"],
]

var NPC_List_2 = [ //id, text
	[9330003, "封測道具販賣", "封測道具購買"],
	[9330003, "獎勵", "獎勵"],
	/*[9330003, 10, "道具回真"],
	[9209000, 0, "#r雜貨店"],
	[9330003, 14, "萬能剪刀"],
	[9330003, 6, "裝備墳墓"],
	[9209001, 0,  "#d百貨公司"],
	[9330003, 7, "#b寵物藥水"],
	[9209000, 0, "#r道具合成"],
	[9209001, 7, "騎寵相關"],
	[9330003, 16, "#r道具鐵砧"],
	[9330003, 15, "Gash轉換"]*/
]

var NPC_List_3 = [ //id, text
	[9250050, 1, "裝備捲數增加"],
	[9250050, 2, "時裝裝飾"],
	[9250050, 4, "消耗品"],
	[9250050, 3, "特殊道具"],
	[9250050, 5, "累積贊助"]
]

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {

    if (mode == 0) {
		cm.dispose();
		return;
    } else if (mode == 1){
		status++;
    } else {
		status--;
    }

    switch (status) {
        case 0: 
			var Text = "\t\t\t\t   #fEffect/CharacterEff/1042107/0/0##b小豬谷#fEffect/CharacterEff/1042107/0/0#";
			
			Text += "\r\n\r\n\r\n#r"+blue_heart+blue_heart+blue_heart+blue_heart+blue_heart+blue_heart+blue_heart+blue_heart+blue_heart+blue_heart+" 角色相關 "+ blue_heart+blue_heart+blue_heart+blue_heart+blue_heart+blue_heart+blue_heart+blue_heart+blue_heart+"#b\r\n\r\n"
			for(var i = 0; i < NPC_List_1.length;i++){
				Text += "#b#L" + i + "##fEffect/CharacterEff/1082229/0/0# " + NPC_List_1[i][2] + "#l  ";
				if((i + 1) % 3 == 0)
					Text += "\r\n"
			}
			
			Text += "\r\n\r\n\r\n#r"+pony_heart+pony_heart+pony_heart+pony_heart+pony_heart+pony_heart+pony_heart+pony_heart+pony_heart+pony_heart+" 萬能百貨 "+pony_heart+pony_heart+pony_heart+pony_heart+pony_heart+pony_heart+pony_heart+pony_heart+pony_heart+pony_heart+"#b\r\n\r\n"
			for(var i = 0; i < NPC_List_2.length;i++){
				Text += "#b#L" + (i+100) + "##fEffect/CharacterEff/1082229/0/0# " + NPC_List_2[i][2] + "#l  ";
				if((i + 1) % 3 == 0)
					Text += "\r\n"
			}
			
			Text += "\r\n\r\n\r\n#r"+pink_heart+pink_heart+pink_heart+pink_heart+pink_heart+pink_heart+pink_heart+pink_heart+pink_heart+pink_heart+" 贊助相關 "+pink_heart+pink_heart+pink_heart+pink_heart+pink_heart+pink_heart+pink_heart+pink_heart+pink_heart+pink_heart+"#b\r\n\r\n"
			for(var i = 0; i < NPC_List_3.length;i++){
				Text += "#b#L" + (i+200) + "##fEffect/CharacterEff/1082229/0/0# " + NPC_List_3[i][2] + "#l  ";
				if((i + 1) % 3 == 0)
					Text += "\r\n"
			}
			cm.sendOk(Text);
            break;
        case 1:
			cm.dispose();
			if(selection == 0){
				Text = "以下為您的帳號資訊 : \r\n"
				Text += "#b帳號 : #r" + cm.getClient().getAccountName();
				Text += "\r\n#b贊助點 : #r" + cm.getPlayer().getPoints();
				Text += "\r\n#b楓點 : #r" + cm.getPlayer().getCSPoints(2);
				cm.sendOk(Text);
				cm.dispose();
			}else if(selection == 102){
				cm.openShop(778);
			}else if(selection < 100){
				cm.openNpc(NPC_List_1[selection][0], NPC_List_1[selection][1]);
			}else if(selection < 200){
				cm.openNpc(NPC_List_2[selection - 100][0], NPC_List_2[selection - 100][1]);
			}else if(selection < 300){
				cm.openNpc(NPC_List_3[selection - 200][0], NPC_List_3[selection - 200][1]);
			}
            break;
    }
}
