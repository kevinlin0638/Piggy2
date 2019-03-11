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
var priceD = 20;
var debug = false;




/*以下為要修改的*/
var rates = [5000,205000, 790000];//總共100萬 最好->最差

//物品代碼 數量 時間期限(沒有就0)  只有Advanced會廣播
var itemListAdvanced = Array(
	Array(4030004, 1, 0),//輪迴石碑
	
	Array(1122034, 1, 0),//楓葉之心
	Array(1122035, 1, 0),//楓葉之心
	Array(1122036, 1, 0),//楓葉之心
	Array(1122037, 1, 0),//楓葉之心
	Array(1122038, 1, 0),//楓葉之心
	
	Array(1032208, 1, 0),//神話
	
	Array(1012172, 1, 0),//恰吉
	Array(1132214, 1, 0),//意志腰帶
	
	Array(2049119, 5, 0),//渾沌神蹟
	Array(2049118, 3, 0),//渾沌神蹟
	
	
	Array(4030008, 3, 0),//破攻
	
	Array(4030007, 10, 0),//破攻
	Array(4030006, 30, 0),//破攻
	
	Array(4021030, 1, 0),//六月
	
	Array(4030008, 3, 0),//破攻
	Array(4030006, 30, 0),//破攻
	Array(4030006, 30, 0),//破攻
	Array(4030006, 30, 0),//破攻
	Array(4030006, 30, 0),//破攻
	Array(2049119, 5, 0),//渾沌神蹟
	Array(2049118, 5, 0),//渾沌神蹟
	Array(2049118, 5, 0),//渾沌神蹟
	Array(2049118, 5, 0),//渾沌神蹟
	Array(2049118, 5, 0),//渾沌神蹟
	
	Array(2049119, 3, 0)//渾沌神蹟
);
var itemListGold = Array(
	Array(2430215, 1, 7),//寵物全圖
	Array(2430216, 1, 7),//點數加倍
	Array(2430217, 1, 7),//輪迴石碑
	Array(2049116, 5, 0),//渾沌神蹟
	Array(2049116, 7, 0),//渾沌神蹟
	Array(2049117, 5, 0),//混沌驚訝
	Array(2049117, 7, 0),//混沌驚訝
	Array(2046048 , 5, 0),//傳說武器攻擊卷軸 60%
	Array(2046049 , 5, 0),//傳說武器攻擊卷軸 60%
	Array(2046132 , 5, 0),//傳說武器攻擊卷軸 60%
	Array(2046135 , 5, 0),//傳說武器攻擊卷軸 60%
	Array(2046048 , 7, 0),//傳說武器攻擊卷軸 60%
	Array(2046049 , 7, 0),//傳說武器攻擊卷軸 60%
	Array(2046132 , 7, 0),//傳說武器攻擊卷軸 60%
	Array(2046135 , 7, 0),//傳說武器攻擊卷軸 60%
	Array(4030007, 5, 0),//破攻
	Array(4030006, 10, 0),//破攻
	Array(1122029, 1, 0),//楓葉之心
	Array(1122030, 1, 0),//楓葉之心
	Array(1122031, 1, 0),//楓葉之心
	Array(1122032, 1, 0),//楓葉之心
	Array(1122033, 1, 0),//楓葉之心
	Array(1032207, 1, 0),//神話
	Array(1073031, 1, 0),//立體積動裝置
	Array(1102793, 1, 0),//覺醒調查兵團披風
	Array(1702563, 1, 0),//覺醒刀片
	Array(1132213, 1, 0),//意志腰帶
	Array(1102604, 1, 0),//
	Array(1102605, 1, 0),//
	Array(2046052 , 5, 0),//不滅的傳說武器攻擊力卷軸
	Array(2046052 , 3, 0),//不滅的傳說武器攻擊力卷軸
	Array(2046053 , 5, 0),//不滅的傳說武器攻擊力卷軸
	Array(2046053 , 3, 0),//不滅的傳說武器攻擊力卷軸
	Array(2046134 , 5, 0),//不滅的傳說武器攻擊力卷軸
	Array(2046134 , 3, 0),//不滅的傳說武器攻擊力卷軸
	Array(2046137 , 5, 0),//不滅的傳說武器攻擊力卷軸
	Array(2046137 , 3, 0),//不滅的傳說武器攻擊力卷軸
	Array(3010282, 1, 0),
	Array(3010283, 1, 0),
	Array(3010289, 1, 0),
	Array(3010290, 1, 0),
	Array(3010296, 1, 0),
	Array(3010298, 1, 0),
	Array(3010354, 1, 0),
	Array(3010361, 1, 0),
	Array(3010519, 1, 0),
	Array(3010520, 1, 0),
	Array(3010521, 1, 0),
	Array(3010522, 1, 0),
	Array(3010681, 1, 0),
	Array(3010680, 1, 0),
	Array(3010677, 1, 0),
	Array(3010679, 1, 0),
	Array(3010671, 1, 0),
	Array(3010690, 1, 0),
	Array(3010699, 1, 0),
	Array(3010714, 1, 0),
	Array(3010716, 1, 0),
	Array(3010717, 1, 0),
	Array(3010718, 1, 0),
	Array(3010719, 1, 0),
	Array(3010720, 1, 0),
	Array(3010721, 1, 0),
	Array(3010722, 1, 0),
	Array(3010723, 1, 0),
	Array(3010729, 1, 0),
	Array(3010731, 1, 0),
	Array(3010732, 1, 0),
	Array(3010734, 1, 0),
	Array(3010735, 1, 0),
	Array(3010736, 1, 0),
	Array(3010737, 1, 0),
	Array(3010738, 1, 0),
	Array(3010739, 1, 0),
	Array(3010742, 1, 0),
	Array(3010744, 1, 0),
	Array(3010750, 1, 0),
	Array(3010751, 1, 0),
	Array(3010752, 1, 0),
	Array(3010754, 1, 0),
	Array(3010756, 1, 0),
	Array(3010757, 1, 0),
	Array(3010760, 1, 0),
	Array(3010761, 1, 0),
	Array(3010767, 1, 0),
	Array(3010779, 1, 0),
	Array(3010788, 1, 0),
	Array(3010824, 1, 0),
	Array(3010825, 1, 0),
	Array(3010826, 1, 0),
	Array(3010827, 1, 0),
	Array(3010828, 1, 0),
	Array(3010829, 1, 0),
	Array(3010830, 1, 0),
	Array(3010831, 1, 0),
	Array(3010832, 1, 0),
	Array(3010851, 1, 0),
	Array(3010944, 1, 0),
	Array(3010949, 1, 0),
	Array(3010950, 1, 0),
	Array(3010951, 1, 0),
	Array(3010952, 1, 0),
	Array(3010955, 1, 0),
	Array(3015010, 1, 0),
	Array(3015014, 1, 0),
	Array(3015014, 1, 0),
	Array(3015015, 1, 0),
	Array(3015016, 1, 0),
	Array(3015017, 1, 0),
	Array(3015018, 1, 0),
	Array(3015019, 1, 0),
	Array(3015020, 1, 0),
	Array(3015021, 1, 0),
	Array(3015022, 1, 0),
	Array(3015023, 1, 0),
	Array(3015024, 1, 0),
	Array(3015025, 1, 0),
	Array(3015026, 1, 0),
	Array(3015027, 1, 0),
	Array(3015049, 1, 0),
	Array(1012171, 1, 0),//恰吉
	Array(3015850, 1, 0),//噴發吧！間歇泉
	Array(3015856, 1, 0),//真理之門椅子
	Array(3015857, 1, 0),//寒冬冰霜椅子
	
	Array(2046048 , 5, 0),//傳說武器攻擊卷軸 60%
	Array(2046049 , 5, 0),//傳說武器攻擊卷軸 60%
	Array(2046132 , 5, 0),//傳說武器攻擊卷軸 60%
	Array(2046135 , 5, 0),//傳說武器攻擊卷軸 60%
	Array(2046048 , 5, 0),//傳說武器攻擊卷軸 60%
	Array(2046049 , 5, 0),//傳說武器攻擊卷軸 60%
	Array(2046132 , 5, 0),//傳說武器攻擊卷軸 60%
	Array(2046135 , 5, 0)//傳說武器攻擊卷軸 60%
);
var itemListSilver = Array(
	Array(5062002, 15, 0),//方塊
	Array(5062002, 30, 0),//方塊
	
	
	Array(4030006, 2, 0),//破攻
	Array(4030006, 3, 0),//破攻
	
	Array(2340000 , 3, 0),//祝福
	Array(2340000 , 5, 0),//祝福
	
	Array(5062002, 15, 0),//方塊
	Array(5062002, 30, 0),//方塊
	
	Array(5062002, 15, 0),//方塊
	Array(5062002, 30, 0),//方塊
	
	Array(5062002, 15, 0),//方塊
	Array(5062002, 30, 0),//方塊
	
	Array(5062002, 15, 0),//方塊
	Array(5062002, 30, 0),//方塊
	
	Array(5062002, 15, 0),//方塊
	Array(5062002, 30, 0),//方塊
	
	Array(5062002, 15, 0),//方塊
	Array(5062002, 30, 0)//方塊
);

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
		var ps = conn.prepareStatement("SELECT * FROM lottery WHERE type=9330136 ORDER BY id desc LIMIT 5");
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
        text += "-----------------------------------------------\r\n#L0##b【抽獎 1張 超級轉蛋券/次】#l #L1##d【獎品預覽】#l\r\n#L2##r【抽獎 "+ priceD +" 贊助點/次】#l#k"+ ttxt +" \r\n\r\n \r\n";
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
			if(selection == 0 && cm.haveItem(5220010) >= 1)
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
                }else{
					rand = Math.floor(Math.random() * itemListSilver.length);
                    item = itemListSilver[rand];
				}
					
                if (item == -1) {
                    cm.sendOk("對不起，您的背包已經滿了");
                    cm.dispose();
                } else {
					if(selection == 0 && cm.haveItem(5220010) >= 1)
						cm.gainItem(5220010,-1);
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
                        var insert = conn.prepareStatement("INSERT INTO lottery (id,itemid,charid,charName,time,type) VALUES(?,?,?,?,?, ?)"); // 载入數据
                        insert.setString(1, null); //载入记录ID
                        insert.setString(2, "#t" + item[0] + "#"); //载入记录ID
                        insert.setString(3, cm.getPlayer().getId());
                        insert.setString(4, cm.getPlayer().getName());
                        insert.setString(5, Year + "-" + month + "-" + dates + "");
                        insert.setInt(6, 9330136);
                        insert.executeUpdate(); //更新
                        insert.close();
						var time = item[2] == 0?"":("為期" +item[2]+ "天");
						cm.gainGachaponItem(item[0], item[1], "恭喜玩家 " + cm.getChar().getName() + " 抽中大獎", false, item[2]);
                        cm.getMap().startMapEffect("恭喜玩家 " + cm.getChar().getName() + " 人品爆發抽中大獎。", 5120012);
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
			var inc = 0;
			for(var i in itemListAdvanced){
				inc++;
				if(itemListAdvanced[i][2] > 0){
					text += '#b#v'+itemListAdvanced[i]+'##z'+itemListAdvanced[i]+'# 持續' + itemListAdvanced[i][2]+ ' 天\r\n';
				}else{
					text += '#b#v'+itemListAdvanced[i]+'##z'+itemListAdvanced[i]+'# x ' + itemListAdvanced[i][1] + ' #r永久\r\n';
				}
				if(inc >= 15)
					break;
			}
			inc = 0;
			text += "#r------------------el 高級物品資訊--------------------\r\n#b";
			for(var i in itemListGold){
				inc++;
				if(itemListGold[i][2] > 0){
					text += '#b#v'+itemListGold[i]+'##z'+itemListGold[i]+'# 持續' + itemListGold[i][2]+ ' 天\r\n';
				}else{
					text += '#b#v'+itemListGold[i]+'##z'+itemListGold[i]+'# x ' + itemListGold[i][1] + ' #r永久\r\n';
				}
				if(inc >= 124)
					break;
			}
			
			inc = 0;
			
			text += "#r------------------普通物品資訊--------------------\r\n#b";
			for(var i in itemListSilver){
				inc++;
				if(itemListSilver[i][2] > 0){
					text += '#b#v'+itemListSilver[i]+'##z'+itemListSilver[i]+'# 持續' + itemListSilver[i][2]+ ' 天\r\n';
				}else{
					text += '#b#v'+itemListSilver[i]+'##z'+itemListSilver[i]+'# x ' + itemListSilver[i][1] + ' #r永久\r\n';
				}
				if(inc >= 5)
					break;
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
                } else {//垃圾
                    c++;
                }
			}
			text = "#b本次測試 #r100#b 次\r\n\r\n";
			text += "#g傳說級人品 : " + a +"次\r\n";
			text += "#r尊貴級人品 : " + b +"次\r\n";
			text += "#b普通級人品 : " + c +"次\r\n";
            cm.sendOk(text);
            cm.dispose();
        }else {
            cm.sendOk("請聯繫管理員。")
            cm.dispose();
        }
    }
}