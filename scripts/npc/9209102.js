/*法師轉蛋機*/

var ttt6 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var time = new Date();
var hour = time.getHours(); //獲得小時
var minute = time.getMinutes(); //獲得分鐘
var second = time.getSeconds(); //獲得秒
var Year = time.getFullYear();
var month = time.getMonth() + 1; //獲取当前月份(0-11,0代表1月)
var dates = time.getDate(); //獲取当前日(1-31)
var status = -1;
var rand = 0;

var nx = false;
var nxx = false;
var price = 250000;
var priceD = 3;
var debug = false;




/*以下為要修改的*/
var rates = [10000000,0,0,0];//總共100萬 最好->最差

//物品代碼 數量 時間期限(沒有就0)  只有Advanced會廣播
var itemListAdvanced = Array(
	Array(3010495, 1, 0),
	Array(3010798, 1, 0),
	Array(3010797, 1, 0)
);
var itemListGold = Array(
);
var itemListSilver = Array(
);
var itemListNormal = Array(
);
function doCheck(){
	temp = [];
	for(var i in itemListAdvanced){
		if(cm.ExistItem(itemListAdvanced[i][0]))
			temp.push(itemListAdvanced[i])
	}
	itemListAdvanced = temp;
	
	temp = [];
	for(var i in itemListGold){
		if(cm.ExistItem(itemListGold[i][0]))
			temp.push(itemListGold[i])
	}
	itemListGold = temp;
	
	temp = [];
	for(var i in itemListSilver){
		if(cm.ExistItem(itemListSilver[i][0]))
			temp.push(itemListSilver[i])
	}
	itemListSilver = temp;
	
	temp = [];
	for(var i in itemListNormal){
		if(cm.ExistItem(itemListNormal[i][0]))
			temp.push(itemListNormal[i])
	}
	itemListNormal = temp;
}
	
function action(mode, type, selection) {
	var InsertData = false;
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
        }
        status--;
    }
    if (status == 0) {
		if(debug && cm.getPlayer().getGMLevel() < 4){
			cm.sendOk("尚未開放，將於二月啟動!");
            cm.dispose();
			return;
		}
		var conn = cm.getConnection();
		var ps = conn.prepareStatement("SELECT * FROM lottery WHERE type=9330119 ORDER BY id desc LIMIT 5");
        var RankDataBase = ps.executeQuery();
        var text = ""
        var i = 1;
        text += "#d#e最新抽中大獎消息：#k#n#r\r\n\r\n-----------------------------------------------\r\n";
        while (RankDataBase.next()) {
            text += "#r" + RankDataBase.getString("charName") + "#k 在 #b" + RankDataBase.getString("time") + "#k 抽中 #r" + RankDataBase.getString("itemid") + "#k"
            text += "\r\n"
            i++;
        }
		var ttxt = cm.getPlayer().getGMLevel() > 4?"#L3#【GM測試1百抽】#l":"";
        text += "-----------------------------------------------\r\n#L0##b【抽獎 1張 轉蛋券/次】#l #L1##d【獎品預覽】#l\r\n#k"+ ttxt +" \r\n\r\n \r\n";
        RankDataBase.close();
		ps.close();
        cm.sendSimple(text);

    } else if (status == 1) {
        if (selection == 0 || selection == 2) {
			if(cm.getInventory(1).getNextFreeSlot() < 1 || cm.getInventory(2).getNextFreeSlot() < 1 || cm.getInventory(3).getNextFreeSlot() < 1 || cm.getInventory(4).getNextFreeSlot() < 1|| cm.getInventory(5).getNextFreeSlot() < 1)
			{
				cm.sendOk("對不起，您的背包每個欄位至少皆要有一個空位!");
                cm.dispose();
				return;
			}
			var canUse = false;
			if(selection == 0 && cm.haveItem(5451000) >= 1)
				canUse = true;
			else if(selection == 2 && cm.getPlayer().getPoints() >= priceD)
				canUse = true;
            var ii = cm.getItemInfo();
            if (canUse) {
                var item;
                var xxx = Math.floor(Math.random() * 1000000);
                if (xxx < rates[0]) {//傳說物品
                    rand = Math.floor(Math.random() * itemListAdvanced.length);
					item = itemListAdvanced[rand];
                    InsertData = true;
                } else if (xxx < rates[1]) {//金牌物品
                    rand = Math.floor(Math.random() * itemListGold.length);
                    item = itemListGold[rand];
                } else if (xxx < rates[2]) {//銀牌物品
                    rand = Math.floor(Math.random() * itemListSilver.length);
                    item = itemListSilver[rand];
                } else {//垃圾
                    rand = Math.floor(Math.random() * itemListNormal.length);
                    item = itemListNormal[rand];
                }
                if (item == -1) {
                    cm.sendOk("對不起，您的背包已經滿了");
                    cm.dispose();
                } else {
					if(selection == 0 && cm.haveItem(5451000) >= 1)
						cm.gainItem(5451000,-1);
					else if(selection == 2 && cm.getPlayer().getPoints() >= priceD)
						cm.getPlayer().gainPoints(-priceD);
					else{//不該發生
						cm.sendOk("發生錯誤!");
						cm.dispose();
						return;
					}
                    
                    cm.setEventCount("累計明星抽獎", 1);
                    if (InsertData) {
						var conn = cm.getConnection();
                        var insert = conn.prepareStatement("INSERT INTO lottery (id,itemid,charid,charName,time, type) VALUES(?,?,?,?,?,?)"); // 载入數据
                        insert.setString(1, null); //载入记录ID
                        insert.setString(2, "#t" + item[0] + "#"); //载入记录ID
                        insert.setString(3, cm.getPlayer().getId());
                        insert.setString(4, cm.getPlayer().getName());
                        insert.setString(5, Year + "-" + month + "-" + dates + "");
                        insert.setInt(6, 9330119);
                        insert.executeUpdate(); //更新
                        insert.close();
						var time = item[2] == 0?"":("為期" +item[2]+ "天");
						cm.gainGachaponItem(item[0], item[1], "新年轉蛋機", true, item[2]);
                        cm.getMap().startMapEffect("恭喜玩家 " + cm.getChar().getName() + " 人品爆發抽中大獎。", 9209102);
                    }else{
						cm.gainItemPeriod(item[0], item[1], item[2]);
					}
                    status = -1;
                    cm.sendOk("恭喜您從幸運抽獎中獲得 #b#t" + item + "##k.");
                }
            } else {
                cm.sendOk("您沒有足夠的轉蛋券或贊助點!");//暂時關闭。增加物品中。
                cm.safeDispose();
            }
        } else if (selection == 1) {
			text = "#r------------------稀有物品資訊--------------------\r\n#b";
			for(var i in itemListAdvanced){
				if(itemListAdvanced[i][2] > 0){
					text += '#b#v'+itemListAdvanced[i]+'##z'+itemListAdvanced[i]+'# 持續' + (itemListAdvanced[i][2]/1000/60/60)+ ' 小時';
					if(cm.getPlayer().isGM())
						text += '-' + itemListAdvanced[i][0] + '\r\n';
					else
						text += '\r\n';
				}else{
					text += '#b#v'+itemListAdvanced[i]+'##z'+itemListAdvanced[i]+'# #r永久';
					if(cm.getPlayer().isGM())
						text += '-' + itemListAdvanced[i][0] + '\r\n';
					else
						text += '\r\n';
				}
			}
            cm.sendOk(text);
            cm.dispose();
        } else if (selection == 3) {
			var a=0,b=0,c=0,d=0
			for(i = 0;i < 100;i++){
				var xxx = Math.floor(Math.random() * 1000000);
				if (xxx < rates[0]) {//傳說物品
                    a++;
                } else if (xxx < rates[1]) {//金牌物品
                    b++;
                } else if (xxx < rates[2]) {//銀牌物品
                    c++;
                } else {//垃圾
                    d++;
                }
			}
			text = "#b本次測試 #r100#b 次\r\n\r\n";
			text += "#g傳說級人品 : " + a +"次\r\n";
			text += "#r尊貴級人品 : " + b +"次\r\n";
			text += "#b普通級人品 : " + c +"次\r\n";
			text += "#d垃圾級人品 : " + d +"次\r\n";
            cm.sendOk(text);
            cm.dispose();
        }else {
            cm.sendOk("請聯繫管理員。")
            cm.dispose();
        }
    }
}