/*
*	冰火家族萬能NPC
*
*
*
*
*
*/
var select_item;
var select_index;
var status;
var item_p;
var price = 1;
		
var choice = Array( // index 2 之後的array [到期時間, 點數, 顯示文字, 是否上架, 取得數量, 是否只能有一個]
		Array(4030003, "寵物全圖撿", //寵物全圖撿
				[43200000, 12, "12 小時", true, 1, false], [1, 20, "1 天", true, 1, false],
				[3, 50, "3 天", true, 1, false], [7, 110, "7 天", true, 1, false],
				[30, 220, "30 天", true, 1, false]),
		Array(4030002, "打怪點數加倍", //點數雙倍
				[43200000, 15, "12 小時", true, 1, false], [1, 25, "1 天", true, 1, false],
				[3, 70, "3 天", true, 1, false], [7, 150, "7 天", true, 1, false],
				[30, 300, "30 天", true, 1, false]),
		Array(1182053, "防Debuff道具",  //防Debuff道具
				[43200000, 18, "12 小時", true, 1, false], [1, 30, "1 天", true, 1, false],
				[3, 88, "3 天", true, 1, false], [7, 200, "7 天", true, 1, false],
				[30, 400, "30 天", true, 1, false]),
		Array(4030004, "輪迴石碑",  //輪迴
				[43200000, 20, "12 小時", true, 1, false], [1, 35, "1 天", true, 1, false],
				[3, 95, "3 天", true, 1, false], [7, 230, "7 天", true, 1, false],
				[-1, 2300, "無期限", true, 1, false])
);
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
			text = "各式特殊道具:\r\n\r\n";
			for (var i = 0; i < choice.length; i++) {
				text += "#b#v" + choice[i][0] + "##z" + choice[i][0] + "# #r("+ choice[i][1] +")#b\r\n";
				for(var j = 2; j < choice[i].length;j++){
					var temp = choice[i][j]
					if(temp[3]){ // 顯示
						text += "#L" + (i * 10 + j) + "# 道具使用期限 : #r" + temp[2] + "#b 價格 : #r" + temp[1] + " 贊助點#b#l\r\n";
					}
				}
				text += "\r\n\r\n---------------------------------------------------\r\n\r\n"
			}
			
			text += "\t\t\t\t  #L999#返回上一頁#l"
			cm.sendSimple(text);
            break;
        case 1:
			var temp_item;
			select_item = Math.floor(selection / 10);
			select_index = selection % 10;
			switch (selection){
				case 999://回上一頁
					cm.dispose();
					cm.openNpc(9900002);
					break;
				default:
					temp_item = choice[select_item][select_index];
					text = "您確定要購買 #b#e#v" + choice[select_item][0] + "##z" + choice[select_item][0] + "# #r("+ choice[select_item][1] +")#n\r\n\r\n#b花費:#r#e" + temp_item[1] +" 贊助點#n\r\n\r\n確認後將無法返回!";
					cm.sendYesNo(text);
					break;
			}
            break;
		case 2:
			var temp_item = choice[select_item][select_index];
			if (select_item >= 0 && select_item < choice.length && choice[select_item] != null) {
				if(cm.getPlayer().getPoints()>= temp_item[1]){
					
					text = "#e恭喜您獲得了\r\n#r#v" + choice[select_item][0] + "##z" + choice[select_item][0] + "# #r("+ choice[select_item][1] +") 為期 " + temp_item[2];
					if(cm.canHold(choice[select_item][0], temp_item[4])){
						if(!temp_item[5] || !cm.haveItem(choice[select_item][0], temp_item[4])){
							cm.gainItemPeriod(choice[select_item][0], temp_item[4], temp_item[0]);
							cm.getPlayer().gainPoints(-temp_item[1]);
							text += "\r\n\r\n#b您的贊助點餘額為 : #r" + cm.getPlayer().getPoints() + " #b點";
						}else
							text = "您已經擁有 #r#v" + choice[select_item][0] + "##z" + choice[select_item][0] + "# #k!!";
					}else{
						text = "您的背包已滿!!"
					}
					cm.sendOk(text);
					cm.dispose();
				}else{
					cm.sendOk("您的贊助點不足!");
					cm.dispose();
				}
			} else {
				cm.sendOk("傳送出現錯誤 請聯繫管理員");
				cm.dispose();
			}
			break;
        default:
			cm.sendOk("發生錯誤，請聯繫管理員!");
			cm.dispose();
			break;
    }
}
