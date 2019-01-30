/*海盜轉蛋機*/

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
var rates = [10000,60000,430000,500000];//總共100萬 最好->最差

//物品代碼 數量 時間期限(沒有就0)  只有Advanced會廣播
var itemListAdvanced = Array(
	Array(1004043, 1, 0),
	Array(1003130, 1, 0),
	Array(1003131, 1, 0),
	Array(1003132, 1, 0),
	Array(1003133, 1, 0),
	Array(1003135, 1, 0),
	Array(1003136, 1, 0),
	
	Array(1003141, 1, 0),
	Array(1003144, 1, 0),
	Array(1003146, 1, 0),
	Array(1003147, 1, 0),
	Array(1003148, 1, 0),
	Array(1003149, 1, 0),
	
	Array(1003161, 1, 0),
	Array(1003163, 1, 0),

	Array(1003171, 1, 0),
	
	Array(1003186, 1, 0),
	Array(1003187, 1, 0),
	
	Array(1003192, 1, 0),
	Array(1003193, 1, 0),
	Array(1003194, 1, 0),
	Array(1003196, 1, 0),
	
	Array(1003203, 1, 0),
	Array(1003204, 1, 0),
	Array(1003207, 1, 0),
	Array(1003208, 1, 0),
	
	Array(1003210, 1, 0),
	Array(1003211, 1, 0),
	Array(1003214, 1, 0),
	Array(1003215, 1, 0),
	Array(1003216, 1, 0),
	Array(1003217, 1, 0),
	Array(1003218, 1, 0),
	
	Array(1003220, 1, 0),
	Array(1003221, 1, 0),
	Array(1003222, 1, 0),
	Array(1003223, 1, 0),
	Array(1003226, 1, 0),
	
	Array(1003232, 1, 0),
	Array(1003233, 1, 0),
	Array(1003234, 1, 0),
	Array(1003235, 1, 0),
	Array(1003239, 1, 0),
	
	Array(1003240, 1, 0),
	Array(1003241, 1, 0),
	Array(1003246, 1, 0),
	Array(1003247, 1, 0),
	Array(1003249, 1, 0),
	
	Array(1003250, 1, 0),
	Array(1003251, 1, 0),
	Array(1003252, 1, 0),
	Array(1003253, 1, 0),
	Array(1003254, 1, 0),
	Array(1003255, 1, 0),
	Array(1003256, 1, 0)
	
);
var itemListGold = Array(
	Array(1022047, 1, 0),//貓頭鷹
	Array(1000040, 1, 0),//???帽子
	Array(1102041, 1, 0),//粉紅冒險家披風
	Array(1102084, 1, 0),//粉紅蓋亞披風
	Array(1022058, 1, 0),//貍貓眼部裝飾
	Array(1302120, 1, 0),//天使之劍
	Array(1332113, 1, 0),//火雪天刀
	Array(1032026, 1, 0),//金祖母綠耳環
	Array(1012153, 1, 0),//真實的短鼻子
	Array(1012154, 1, 0),//真實的短鼻子
	Array(1012155, 1, 0),//真實的短鼻子
	Array(1012156, 1, 0),//真實的短鼻子
	Array(1012156, 1, 0),//真實的短鼻子
	Array(1012156, 1, 0),//真實的短鼻子
	Array(1092022, 1, 0),//調色盤
	Array(1092035, 1, 0)//可樂戰盾
);
var itemListSilver = Array(
	Array(1102011, 1, 0), //藍色守護披風
	Array(1102012, 1, 0), //紅色守護披風
	Array(1102017, 1, 0), //白色魔法披風
	Array(1102021, 1, 0), //藍鬥士披風
	Array(1102035, 1, 0), //黑龍紋披風
	Array(1102043, 1, 0), //褐色冒險家披風
	Array(1102057, 1, 0), //玩具披風
	Array(1102064, 1, 0), //鬼怪披風
	Array(1102085, 1, 0), //黃色蓋亞披風
	Array(01482007, 1, 0), //惡魔之爪
	Array(1482008, 1, 0), //塞爾拉利刃拳套
	Array(1482009, 1, 0), //拜爾毀滅拳套
	Array(1482010, 1, 0), //紗羅毀滅拳套
	Array(1482011, 1, 0), //吸血鬼之爪
	Array(1482012, 1, 0), //王者毀滅拳套
	Array(1482057, 1, 0), //紗羅毀滅拳套
	Array(1492007, 1, 0), //拉斯菲特之槍
	Array(1492008, 1, 0), //灼熱地獄
	Array(1492009, 1, 0), //深淵射手
	Array(1492010, 1, 0), //因弗蒂之槍
	Array(1492011, 1, 0), //調停者
	Array(1492012, 1, 0), //死亡協奏曲
	Array(1002328, 1, 0), //綠色海盜帽
	Array(1002327, 1, 0), //褐色海盜帽
	Array(1002573, 1, 0), //海盜王帽子
	Array(1002637, 1, 0), //黑色海盜頭巾
	Array(1002643, 1, 0), //紅色勇者頭巾
	Array(1002640, 1, 0), //藍色船型帽
	Array(1002646, 1, 0), //黑色的航海帽
	Array(1052116, 1, 0), //綠色強化服
	Array(1052119, 1, 0), //紫色皇家套裝
	Array(1052122, 1, 0), //紅色威斯克套裝
	Array(1052125, 1, 0), //白色潘尼爾套裝
	Array(1052128, 1, 0), //白色馬其尼套裝
	Array(1052131, 1, 0), //紅色公爵套裝
	Array(1072303, 1, 0), //褐色強化皮靴
	Array(1072306, 1, 0), //黑色巴西里靴
	Array(1072309, 1, 0), //黑色航海靴
	Array(1072312, 1, 0), //藍色伯爵靴
	Array(1072315, 1, 0), //黑色馬金斯靴
	Array(1072318, 1, 0), //黑色公爵靴
	Array(1082198, 1, 0), //褐色羅伊斯手套
	Array(1082201, 1, 0), //黑色舒爾特手套
	Array(1082204, 1, 0), //黑色威斯克手套
	Array(1082207, 1, 0), //藍色伯爵手套
	Array(1082210, 1, 0), //紅色馬蒂爾手套
	Array(1082213, 1, 0)//黑色斯卡洛伯爵手套
	
);
var itemListNormal = Array(
//全職業玩具
	Array(1302000, 1, 0),//劍
	Array(1302013, 1, 0),//紅色鞭子
	Array(1302031, 1, 0),//七星劍
	Array(1302071, 1, 0),//粉紅色花紋游泳圈
	Array(1442106, 1, 0),//雪冰的地圖
	Array(1442015, 1, 0),//黃金雪板
	Array(1332020, 1, 0),//太極扇
	Array(1432016, 1, 0),//橙色滑雪板
	Array(1442016, 1, 0),//黑雪板
	Array(1442025, 1, 0),//青龍偃月刀
	Array(1472063, 1, 0),//魔法手套
	Array(1002006, 1, 0),//骷髏頭盔
	Array(1002012, 1, 0),//紅色棒球帽
	Array(1002026, 1, 0),//褐色斗笠
	Array(1002097, 1, 0),//黃星頭巾
	Array(1002492, 1, 0),//白色棒球帽
	Array(1040014, 1, 0),//橙色運動T恤
	Array(1041004, 1, 0),//星形短T
	Array(1050018, 1, 0),//藍色桑那服
	Array(1050100, 1, 0),//男沐浴毛巾
	Array(1051017, 1, 0),//紅色桑那服
	Array(1051098, 1, 0),//女沐浴毛巾
	Array(1052166, 1, 0),//超強幹員套裝
	Array(1052191, 1, 0),//蕃茄服裝
	Array(1052217, 1, 0),//雲之服
	Array(1060005, 1, 0),//密林部隊褲
	Array(1060004, 1, 0),//灰色橡膠褲
	Array(1062001, 1, 0),//藍牛仔褲
	Array(1072264, 1, 0),//銀色桃樂斯皮鞋
	Array(1072275, 1, 0),//娜娜的拖鞋
	Array(1072369, 1, 0),//黏稠稠鞋子
	Array(1082002, 1, 0),//工地手套
	Array(1082149, 1, 0),//褐色工作手套
	Array(1082176, 1, 0),//藍擊中手套
	Array(1082177, 1, 0),//紫擊中手套
	Array(1082179, 1, 0),//黃擊中手套
	Array(1082245, 1, 0),//幹員O的特殊手套
	Array(1082276, 1, 0),//???手套
	Array(1102174, 1, 0),//幹員披風
	Array(1012058, 1, 0),//皮諾丘的鼻子
	Array(1012059, 1, 0),//皮諾丘的鼻子
	Array(1012060, 1, 0),//皮諾丘的鼻子
	Array(1012061, 1, 0),//皮諾丘的鼻子
	Array(1012161, 1, 0),//小麋鹿的發光的鼻子
	Array(1022097, 1, 0),//龍眼鏡
	Array(1032008, 1, 0),//貓眼耳環
	Array(1032009, 1, 0),//方形耳環
	Array(1032010, 1, 0),//星星耳環
	Array(1032025, 1, 0)//綠葉耳環
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
		var ps = conn.prepareStatement("SELECT * FROM lottery WHERE type=9330116 ORDER BY id desc LIMIT 5");
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
        text += "-----------------------------------------------\r\n#L0##b【抽獎 1張 轉蛋券/次】#l #L1##d【獎品預覽】#l\r\n#L2##r【抽獎 "+ priceD +" 贊助點/次】#l#k"+ ttxt +" \r\n\r\n \r\n";
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
			if(selection == 0 && cm.haveItem(5220000) >= 1)
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
					if(selection == 0 && cm.haveItem(5220000) >= 1)
						cm.gainItem(5220000,-1);
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
                        insert.setInt(6, 9330116);
                        insert.executeUpdate(); //更新
                        insert.close();
						var time = item[2] == 0?"":("為期" +item[2]+ "天");
						cm.gainGachaponItem(item[0], item[1], "海盜轉蛋機", true, item[2]);
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
			text = "#r-----------------各等級資訊--------------------\r\n";
			text += "#g傳說級人品 : " + rates[0]/10000 +"%\r\n";
			text += "#r尊貴級人品 : " + rates[1]/10000 +"%\r\n";
			text += "#b普通級人品 : " + rates[2]/10000 +"%\r\n";
			text += "#d垃圾級人品 : " + rates[3]/10000 +"%\r\n";
			text += "------------------稀有物品資訊--------------------\r\n#b";
			text += "#g傳說級:\r\n"
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