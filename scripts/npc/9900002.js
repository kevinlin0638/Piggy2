// 萬能NPC


var status;

var NPC_List_1 = [ //id, text
	[0, 0, "帳號資訊"],
	[1012117, 0, "萬能造型師"],
	[9330003, 2, "貨幣兌換"],
	[9330003, 5, "新手專區"],
	[9330003, 9, "成就系統"],
	[9330003, 8, "#r獎勵活動"],
	[9330003, 11, "每日任務"],
	[9330003, "home", "萬能轉職"],
	[9310055, null, "#r地圖傳送#b"],
	[9330003, 12, "等級獎勵"],
	[9330003, 17, "我要變性"],
]

var NPC_List_2 = [ //id, text
	[9330003, 3, "道具刪除"],
	[9330003, 10, "道具回真"],
	[9209000, 0, "#r雜貨店"],
	[9330003, 14, "萬能剪刀"],
	[9330003, 6, "裝備墳墓"],
	[9209001, 0,  "#d百貨公司"],
	[9330003, 7, "#b寵物藥水"],
	[9209000, 0, "#r道具合成"],
	[9209001, 7, "騎寵相關"],
	[9330003, 16, "#r道具鐵砧"],
	[9330003, 15, "Gash轉換"]
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
			var Text = "歡迎加入 #b小熊谷#k 請問您有什麼需求:#b";
			
			Text += "\r\n\r\n\r\n#r---------------------角色相關---------------------#b\r\n\r\n"
			for(var i = 0; i < NPC_List_1.length;i++){
				Text += "#b#L" + i + "#" + NPC_List_1[i][2] + "#l";
				if((i + 1) % 4 == 0)
					Text += "\r\n"
			}
			
			Text += "\r\n\r\n\r\n#r---------------------萬能百貨---------------------#b\r\n\r\n"
			for(var i = 0; i < NPC_List_2.length;i++){
				Text += "#b#L" + (i+100) + "#" + NPC_List_2[i][2] + "#l";
				if((i + 1) % 4 == 0)
					Text += "\r\n"
			}
			
			Text += "\r\n\r\n\r\n#r---------------------贊助相關---------------------#b\r\n\r\n"
			for(var i = 0; i < NPC_List_3.length;i++){
				Text += "#b#L" + (i+200) + "#" + NPC_List_3[i][2] + "#l";
				if((i + 1) % 4 == 0)
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
