/* 
	道具合成模板
*/

var status = 0;
var selectedType = -1;
var selectedItem = -1;
var item;
var mats;
var matQty;
var cost;
var qty;
var equip;

var sele_item;
var items;

var req_num= -1;


function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
	items =  Array(
		Array(1152121, // 欲合成之道具 ID
			[[1152120, 1],[4011000 , 3],[4260005, 100],[4008000, 10],[4008001, 10],[4008002, 10],[4008003, 10],[0, 1000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[false], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			0, //等級限制
			true),
		Array(1152122, // 欲合成之道具 ID
			[[1152121, 1],[4011003 , 3],[4260006, 100],[4008000, 20],[4008001, 20],[4008002, 20],[4008003, 20],[0, 5000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[false], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			0, //等級限制
			true),
		Array(1152123, // 欲合成之道具 ID
			[[1152122, 1],[4011005 , 3],[4260007, 100],[4008000, 30],[4008001, 30],[4008002, 30],[4008003, 30],[0, 8000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[false], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			0, //等級限制
			true),
		Array(1152124, // 欲合成之道具 ID
			[[1152123, 1],[4011006 , 3],[4260008, 100],[4008000, 40],[4008001, 40],[4008002, 40],[4008003, 40],[0, 10000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[false], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			0, //等級限制
			true)
	); 
	
	
	
    if (mode == 1)
        status++;
    else
        cm.dispose();
    if (status == 0 && mode == 1) {

		
        var selStr = "哈囉，我可以幫你製作一些對你有幫助的物品\r\n"
        for (var i = 0; i < items.length; i++) {
			if(items[i][6])
				selStr += "#L" + i + "# 製作 #b#v" + items[i][0] + "##z" + items[i][0] + "##l#k\r\n";
        }

        cm.sendSimple(selStr);
    } else if (status == 1 && mode == 1) {
		selectedType = selection;
		sele_item = items[selectedType];

		
		if(!sele_item[4] || sele_item[2][0])
		{
			req_num = 1;
		}
		if(sele_item[4] && req_num == -1){
			var selStr = "您選擇的物品是 #b#v" + sele_item[0] + "##z" + sele_item[0] + "# #r(1 單位)#k所需材料如下 : \r\n"
			if(sele_item[5] > 0)
				selStr += "#b所需等級 : #r" + sele_item[5] + "\r\n";
			if(sele_item[3] > 0)
				selStr += "#b使用期限 : #r" + parseExpireString(sele_item[3]) + "\r\n";
			for(var i = 0; i < sele_item[1].length;i++){
				var temp = sele_item[1][i];
				
				selStr += parseReqPic(temp[0]) + " #r" + temp[1] + " #b" + parseReqString(temp[0]) + "\r\n"
			}
			cm.sendGetNumber(selStr, 1, 1, 100);
		}else{
			var selStr = "您選擇的物品是 #b#v" + sele_item[0] + "##z" + sele_item[0] + "# #r(" + req_num + " 單位)#k所需材料如下 : \r\n"
			if(sele_item[5] > 0)
				selStr += "#b所需等級 : #r" + sele_item[5] + "\r\n";
			if(sele_item[3] > 0)
				selStr += "#b使用期限 : #r" + parseExpireString(sele_item[3]) + "\r\n";
			for(var i = 0; i < sele_item[1].length;i++){
				var temp = sele_item[1][i];
				
				selStr += parseReqPic(temp[0]) + " #r" + temp[1] * req_num + " #b" + parseReqString(temp[0]) + "\r\n"
			}
			if(sele_item[2][0]){
				selStr+= "#k空裝數值(滑鼠移至上方道具名稱後顯示之數值)加成:\r\n";
				selStr+= "#b額外力量#r+" + sele_item[2][1] + " #b額外敏捷#r+" + sele_item[2][2]+ " #b額外智力#r+" + sele_item[2][3] + "\r\n";
				selStr+= "#b額外幸運#r+" + sele_item[2][4] + " #b額外物攻#r+" + sele_item[2][5]+ " #b額外魔攻#r+" + sele_item[2][6] + "\r\n";
				selStr+= "#b額外卷軸數#r+" + sele_item[2][7] + "\r\n";
			}
			cm.sendNext(selStr);
		}
	}else if (status == 2 && mode == 1) {
		if(sele_item[4])
			req_num = selection;
		
		var selStr = "您選擇的物品是 #b#v" + sele_item[0] + "##z" + sele_item[0] + "# #r(" + req_num + " 單位)#k所需材料如下 : \r\n"
		if(sele_item[5] > 0)
			selStr += "#b所需等級 : #r" + sele_item[5] + "\r\n";
		if(sele_item[3] > 0)
				selStr += "#b使用期限 : #r" + parseExpireString(sele_item[3]) + "\r\n";
		for(var i = 0; i < sele_item[1].length;i++){
				var temp = sele_item[1][i];
				
				selStr += parseReqPic(temp[0]) + " #r" + temp[1] * req_num + " #b" + parseReqString(temp[0]) + "\r\n"
		}
		if(sele_item[2][0]){
				selStr+= "#k空裝數值(滑鼠移至上方道具名稱後顯示之數值)加成:\r\n";
				selStr+= "#b額外力量#r+" + sele_item[2][1] + " #b額外敏捷#r+" + sele_item[2][2]+ " #b額外智力#r+" + sele_item[2][3] + "\r\n";
				selStr+= "#b額外幸運#r+" + sele_item[2][4] + " #b額外物攻#r+" + sele_item[2][5]+ " #b額外魔攻#r+" + sele_item[2][6] + "\r\n";
				selStr+= "#b額外卷軸數#r+" + sele_item[2][7] + "\r\n";
			}
		
		selStr += "\r\n\r\n請問您要繼續合成嗎? #r(按下確定後無法後悔)"
		cm.sendYesNo(selStr);
		
	} else if (status == 3 && mode == 1) {
		if(!cm.canHold(sele_item[0], req_num)){
			cm.sendOk("很抱歉,您的背包空間不足");
			cm.dispose();
			return;
		}
		
		for(var i = 0; i < sele_item[1].length;i++){
			var temp = sele_item[1][i];
			if(!checkReq(temp[0], temp[1] * req_num)){
				cm.sendOk("很抱歉,您的的 " + parseReqPic(temp[0]) + "不足!");
				cm.dispose();
				return;
			}
		}
		
		if(cm.getPlayer().getLevel() < sele_item[5]){
			cm.sendOk("很抱歉,您的等級未達 #r" + sele_item[5]);
			cm.dispose();
			return;
		}
		
		for(var i = 0; i < sele_item[1].length;i++){
			var temp = sele_item[1][i];
			costReq(temp[0], temp[1] * req_num);
		}
		
		if(sele_item[2][0]){
			var tee = sele_item[2];
			//itemid, str, dex, int, luk, watk, matk, expira, addi_slot
			cm.addWithPara(sele_item[0], tee[1], tee[2], tee[3], tee[4], tee[5], tee[6], sele_item[3], tee[7]);
		}else{
			cm.gainItemPeriod(sele_item[0], req_num, sele_item[3]);
		}
		cm.sendOk("製作 #b#v" + sele_item[0] + "##t" + sele_item[0] + "# #r(" + req_num + " 單位)#k - 成功!" );
		cm.dispose();
	}
}
function parseExpireString(time){
	if(time <= 0)
		return "無期限";
	else if(time < 1000)
		return time + " 天";
	else{
		if(time < (1000 * 60 * 60))
			return Math.floor(time / 1000 / 60) + " 分鐘";
		else if(time < (1000 * 60 * 60 * 24))
			return Math.floor(time / 1000 / 60 / 60) + " 小時";
		else
			return Math.floor(time / 1000 / 60 / 60 / 24) + " 天";
	}
}

function parseReqString(item){
	switch(item){
		case 0 : 
			return "楓幣";
		case 1 : 
			return "Gash";
		case 2 : 
			return "楓點";
		case 3 : 
			return "贊助點";
		default:
			return "#t" + item + "#";
	}
}

function parseReqPic(item){
	switch(item){
		case 0 : 
			return "#v4031138#";
		case 1 : 
			return "#v4310003#";
		case 2 : 
			return "#v4310003#";
		case 3 : 
			return "#v4310005#";
		default:
			return "#v" + item + "#";
	}
}

function checkReq(item, qua){
	switch(item){
		case 0 : 
			return cm.getMeso() >= qua;
		case 1 : 
			return cm.getNX(1) >= qua;
		case 2 : 
			return cm.getNX(2) >= qua;
		case 3 : 
			return cm.getPlayer().getPoints() >= qua;
		default:
			return cm.haveItem(item, qua);
	}
}

function costReq(item, qua){
	switch(item){
		case 0 : 
			return cm.gainMeso(-qua);
		case 1 : 
			return cm.gainNX(1, -qua);
		case 2 : 
			return cm.gainNX(2, -qua);
		case 3 : 
			return cm.getPlayer().gainPoints(-qua);
		default:
			return cm.gainItem(item, -qua);
	}
}
