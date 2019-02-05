// ?祈NPC


var status;
var sele;

var NPC_List = [ //id, text
	["封測新手禮包",//顯示名稱
		[
		 [0, 500000, -1, false, true],
		 [4310003, 1, -1, false, true],
		 [5062002, 100, -1, false, true],
		],
		1,//type 1:帳號領取 2.只有Giftsender 3.只有領獎帳號可領取
		10,// 等級限制
		"封測新手禮包"//領獎名稱
	],
	["封測贊助點補助",//顯示名稱
		[
		 [4310005, 5, -1, false, true],
		],
		3,//type 1:帳號領取 2.只有Giftsender 3.只有領獎帳號可領取
		10,// 等級限制
		"封測贊助點補助"//領獎名稱
	],
	["新年活動禮物",//顯示名稱
		[
		 [1702492, 1, -1, false, true],
		 [5451000, 1, -1, false, true]
		],
		3,//type 1:帳號領取 2.只有Giftsender 3.只有領獎帳號可領取
		10,// 等級限制
		"新年活動禮物"//領獎名稱
	],
	["領獎帳號註冊",//顯示名稱
		[[5062002, 50, -1, false, true]//道具id, 數量 , 限制天數, 是否鎖定, 是否可交易
		],
		2,//type 1:帳號領取 2.只有Giftsender 3.只有領獎帳號可領取
		10,// 等級限制
		"領獎帳號註冊"//領獎名稱
	],
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
			var vip = cm.getEventCount("領獎帳號註冊",1) > 0?"領獎帳號":"非領獎帳號";
			var Text = "歡迎加入 #b小喵谷#k 請問您有什麼需求:\r\n";
			if(cm.getEventCount("領獎帳號註冊",1) <= 0){
				Text += "#r您的帳號是 : " + vip + "\r\n#b"
			}else{
				Text += "#b您的帳號是 : " + vip + "\r\n"
			}
			
			for(var i = 0; i < NPC_List.length;i++){
				Text += "#L" + i + "#" + NPC_List[i][0];
				if((i + 1) % 4 == 0)
					Text += "\r\n"
			}
			cm.sendSimple(Text);
            break;
        case 1:
			sele = selection;
			var ttext = "#r您確定要領取此禮包?\r\n";
			var eqs = NPC_List[sele][1];
			for(var i = 0; i < eqs.length;i++){
				if(eqs[i][0] == 0){
					ttext += "#b楓幣 x " + eqs[i][1] + "\r\n";
				}else if(eqs[i][0] == 2){
					ttext += "#b楓點 x " + eqs[i][1] + "\r\n";
				}else{
					ttext += "#b#v" + eqs[i][0] + "##z" + eqs[i][0] + "# x " + eqs[i][1];
					if(eqs[i][2] > 0)
						ttext += " 期限:" + eqs[i][2] + "天";
					
					ttext += "\r\n"
				}
			}
			cm.sendYesNo(ttext);
			break;
        case 2:
			switch(NPC_List[sele][2]){
				case 1:
					if ((cm.getEventCount(NPC_List[sele][4],1) == -1 || cm.getEventCount(NPC_List[sele][4],1) == 0) && cm.getPlayer().getLevel() >= NPC_List[sele][3]) {
						
						var eqs = NPC_List[sele][1];
						for(var i = 0; i < eqs.length;i++){
							if(eqs[i][0] == 0 || eqs[i][0] == 2)
								continue;
							if(!cm.canHold(eqs[i][0], eqs[i][1])){
								cm.sendOk("背包空間不足");
								cm.dispose();
								return;
							}
						}
						for(var i = 0; i < eqs.length;i++){
							if(eqs[i][0] == 0){
								cm.gainMeso(eqs[i][1]);
							}else if(eqs[i][0] == 2){
								cm.gainNX(2,eqs[i][1]);
							}else{
								if(!eqs[i][4])
									cm.addWithPara(eqs[i][0], eqs[i][1], eqs[i][2], eqs[i][3], false)
								else if(eqs[i][3])
									cm.addWithPara(eqs[i][0], eqs[i][2], true);
								else if(eqs[i][2] > 0)
									cm.gainItemPeriod(eqs[i][0], eqs[i][1], eqs[i][2]);
								else
									cm.gainItem(eqs[i][0], eqs[i][1]);
							}
						}
						
						
						cm.setEventCount(NPC_List[sele][4],1);
						cm.sendOk("恭喜獲得 "+ NPC_List[sele][0]);
						cm.dispose();
						return
					} else {
						cm.sendOk("已領取過獎勵或等級未達十等");
						cm.dispose();
						return;
					}
				case 2:
					if (cm.CanGetGitft(NPC_List[sele][4]) &&(cm.getEventCount(NPC_List[sele][4],1) == -1 || cm.getEventCount(NPC_List[sele][4],1) == 0) && cm.getPlayer().getLevel() >= NPC_List[sele][3]) {
						
						var eqs = NPC_List[sele][1];
						for(var i = 0; i < eqs.length;i++){
							if(eqs[i][0] == 0 || eqs[i][0] == 2)
								continue;
							if(!cm.canHold(eqs[i][0], eqs[i][1])){
								cm.sendOk("背包空間不足");
								cm.dispose();
								return;
							}
						}
						for(var i = 0; i < eqs.length;i++){
							if(eqs[i][0] == 0){
								cm.gainMeso(eqs[i][1]);
							}else if(eqs[i][0] == 2){
								cm.gainNX(2,eqs[i][1]);
							}else{
								if(!eqs[i][4])
									cm.addWithPara(eqs[i][0], eqs[i][1], eqs[i][2], eqs[i][3], false)
								else if(eqs[i][3])
									cm.addWithPara(eqs[i][0], eqs[i][2], true);
								else if(eqs[i][2] > 0)
									cm.gainItemPeriod(eqs[i][0], eqs[i][1], eqs[i][2]);
								else
									cm.gainItem(eqs[i][0], eqs[i][1]);
							}
						}
						
						
						cm.setEventCount(NPC_List[sele][4],1);
						cm.SentGitft(NPC_List[sele][4]);
						cm.sendOk("恭喜獲得 "+ NPC_List[sele][0]);
						cm.dispose();
						return
					} else {
						cm.sendOk("已領取過獎勵或尚未達到領取條件\r\n\r\n#r回文分享獎勵須等到十點半管理員才會新增今日可領獎之名單\r\n#b造成不便還請見諒");
						cm.dispose();
						return;
					}
				case 3:
					if (cm.getEventCount("領獎帳號註冊",1) > 0 &&(cm.getEventCount(NPC_List[sele][4],1) == -1 || cm.getEventCount(NPC_List[sele][4],1) == 0) && cm.getPlayer().getLevel() >= NPC_List[sele][3]) {
						
						var eqs = NPC_List[sele][1];
						for(var i = 0; i < eqs.length;i++){
							if(eqs[i][0] == 0 || eqs[i][0] == 2)
								continue;
							if(!cm.canHold(eqs[i][0], eqs[i][1])){
								cm.sendOk("背包空間不足");
								cm.dispose();
								return;
							}
						}
						for(var i = 0; i < eqs.length;i++){
							if(eqs[i][0] == 0){
								cm.gainMeso(eqs[i][1]);
							}else if(eqs[i][0] == 2){
								cm.gainNX(2,eqs[i][1]);
							}else{
								if(!eqs[i][4])
									cm.addWithPara(eqs[i][0], eqs[i][1], eqs[i][2], eqs[i][3], false)
								else if(eqs[i][3])
									cm.addWithPara(eqs[i][0], eqs[i][2], true);
								else if(eqs[i][2] > 0)
									cm.gainItemPeriod(eqs[i][0], eqs[i][1], eqs[i][2]);
								else
									cm.gainItem(eqs[i][0], eqs[i][1]);
							}
						}
						
						
						cm.setEventCount(NPC_List[sele][4],1);
						cm.sendOk("恭喜獲得 "+ NPC_List[sele][0]);
						cm.dispose();
						return
					} else {
						cm.sendOk("已領取過獎勵或尚未達到領取條件\r\n\r\n#r必須是領獎帳號才可領取此獎勵\r\n#b造成不便還請見諒");
						cm.dispose();
						return;
					}
			}
            break;
    }
}
