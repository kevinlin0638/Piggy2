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
		Array(1342090, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 300],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 100],[4008001, 100],[4008002, 100],[4008003, 100],[0, 300000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 20, 20, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1332247, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 300],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 100],[4008001, 100],[4008002, 100],[4008003, 100],[0, 300000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 20, 20, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1472235, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 300],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 100],[4008001, 100],[4008002, 100],[4008003, 100],[0, 300000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 20, 20, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1302297, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 300],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 100],[4008001, 100],[4008002, 100],[4008003, 100],[0, 300000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 20, 20, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1312173, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 300],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 100],[4008001, 100],[4008002, 100],[4008003, 100],[0, 300000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 20, 20, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1322223, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 300],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 100],[4008001, 100],[4008002, 100],[4008003, 100],[0, 300000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 20, 20, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1402220, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 300],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 100],[4008001, 100],[4008002, 100],[4008003, 100],[0, 300000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 20, 20, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1412152, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 300],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 100],[4008001, 100],[4008002, 100],[4008003, 100],[0, 300000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 20, 20, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1422158, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 300],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 100],[4008001, 100],[4008002, 100],[4008003, 100],[0, 300000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 20, 20, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1432187, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 300],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 100],[4008001, 100],[4008002, 100],[4008003, 100],[0, 300000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 20, 20, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1442242, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 300],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 100],[4008001, 100],[4008002, 100],[4008003, 100],[0, 300000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 20, 20, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1372195, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 300],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 100],[4008001, 100],[4008002, 100],[4008003, 100],[0, 300000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 20, 20, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1382231, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 300],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 100],[4008001, 100],[4008002, 100],[4008003, 100],[0, 300000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 20, 20, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1452226, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 300],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 100],[4008001, 100],[4008002, 100],[4008003, 100],[0, 300000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 20, 20, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1462213, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 300],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 100],[4008001, 100],[4008002, 100],[4008003, 100],[0, 300000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 20, 20, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1482189, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 300],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 100],[4008001, 100],[4008002, 100],[4008003, 100],[0, 300000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 20, 20, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1492199, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 300],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 100],[4008001, 100],[4008002, 100],[4008003, 100],[0, 300000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 20, 20, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
			/*防裝*/
		Array(1003976, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 600],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 150],[4008001, 150],[4008002, 150],[4008003, 150],[0, 500000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 60, 60, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1052669, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 600],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 150],[4008001, 150],[4008002, 150],[4008003, 150],[0, 500000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 40, 40, 40, 40, 80, 80, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1102623, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 600],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 150],[4008001, 150],[4008002, 150],[4008003, 150],[0, 500000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 60, 60, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1032224, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 600],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 150],[4008001, 150],[4008002, 150],[4008003, 150],[0, 500000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 60, 60, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1132247, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 600],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 150],[4008001, 150],[4008002, 150],[4008003, 150],[0, 500000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 60, 60, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1122269, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 600],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 150],[4008001, 150],[4008002, 150],[4008003, 150],[0, 500000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 60, 60, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1022211, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 600],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 150],[4008001, 150],[4008002, 150],[4008003, 150],[0, 500000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 60, 60, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1152160, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 600],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 150],[4008001, 150],[4008002, 150],[4008003, 150],[0, 500000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 60, 60, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1082556, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 600],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 150],[4008001, 150],[4008002, 150],[4008003, 150],[0, 500000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 60, 60, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1072870, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 600],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 150],[4008001, 150],[4008002, 150],[4008003, 150],[0, 500000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 60, 60, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
			true),
		Array(1012438, // 欲合成之道具 ID
			[[4021030, 5],[4260008, 600],[4021016, 40],[4011007, 10],[4021009, 10],[4008000, 150],[4008001, 150],[4008002, 150],[4008003, 150],[0, 500000000]], //欲合成之道具 1 個所需材料 [id, num], id = 0 楓幣, id = 1 Gash, id = 2 楓點, id = 3 贊助點
			[true, 20, 20, 20, 20, 60, 60, 3], //是否特殊強化, str, dex, int, luk, watk, matk, addi_slot
			-1, //是否限制時間, 時間
			false, // 是否批量合成
			220, //等級限制
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
