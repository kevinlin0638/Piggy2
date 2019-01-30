/*戰士轉蛋機*/

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
var price = 5000000;
var priceD = 3;
var debug = false;




/*以下為要修改的*/
var rates = [10000,60000,430000,500000];//總共100萬 最好->最差

//物品代碼 數量 時間期限(沒有就0)  只有Advanced會廣播
var itemListAdvanced = Array(
	Array(1112127, 1, 18000000),//80% 戒指
	Array(4030003, 1, 18000000),//寵物全圖撿
	Array(4030005, 1, 18000000),//楓葉點數兩倍
	Array(1122017, 1, 72000000),//精靈墜飾
	
	
	Array(3010522, 1, 0),//乘著暴風的兔子椅
	Array(3010400, 1, 0),//水晶花園椅子
	Array(3010682, 1, 0),//天文台椅子
	Array(3010651, 1, 0),//紅楓葉戰士座艙
	Array(3010610, 1, 0),//落寞單身椅
	Array(3010531, 1, 0),//小企鵝合唱團
	
	
	Array(1122019, 1, 0),//楓葉之心
	Array(1012164, 1, 0),//恰吉最低階
	Array(1032205, 1, 0),//神話耳環最低階
	Array(1132211, 1, 0),//楓之谷強韌意志黃色腰帶
	
	
	Array(1012371, 1, 0),//手掌臉部裝飾
	Array(1032101, 1, 0),//溫暖的亞泰爾耳環
	
	Array(1302102, 1, 0)//雙十國旗
	
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
	Array(1092035, 1, 0),//可樂戰盾
	Array(1012416, 1, 0),//草莓冰棒
	Array(1012417, 1, 0),//草莓冰棒
	Array(1012418, 1, 0),//草莓冰棒
	Array(1012419, 1, 0),//草莓冰棒
	Array(1012420, 1, 0)//草莓冰棒
);
var itemListSilver = Array(
	Array(1302010, 1, 0), //樹靈之劍
	Array(1302011, 1, 0), //奇型刀
	Array(1302012, 1, 0), //火焰刀
	Array(1302032, 1, 0), //火靈之劍
	Array(1302047, 1, 0), //湯勺
	Array(1312011, 1, 0), //花靈之斧
	Array(1312015, 1, 0), //雙利刃斧
	Array(1312024, 1, 0), //黃金斧
	Array(1312025, 1, 0), //花靈之斧
	Array(1312027, 1, 0), //猛禽斧
	Array(1322020, 1, 0), //黃金暴風錘
	Array(1322029, 1, 0), //毀滅之鎚
	Array(1322019, 1, 0), //暴風錘Array(1002377, 1, 0), //綠色尼爾哲頭盔
	Array(1002532, 1, 0), //黑色格萊西頭盔
	Array(1442020, 1, 0), //飛翔斧
	Array(1442044, 1, 0), //阿基里斯斧矛
	Array(1432011, 1, 0), //佛羅利刃
	Array(1432030, 1, 0), //火尖槍
	Array(1412021, 1, 0), //雷神大斧
	Array(1422013, 1, 0), //鐳奧釘錘
	Array(1302023, 1, 0), //坲羅劍
	Array(1402005, 1, 0), //斬魔刀
	Array(1332026, 1, 0), //可撒之劍
	Array(1040112, 1, 0), //藍色崇高服
	Array(1040121, 1, 0), //藍色涅奧斯盔甲
	Array(1041121, 1, 0), //黑貴族鎧甲
	Array(1041123, 1, 0), //紫色厄索爾盔甲
	Array(1060101, 1, 0), //藍色強化褲
	Array(1060110, 1, 0), //藍色涅奧斯長褲
	Array(1072222, 1, 0), //黑色新月型長靴
	Array(1082140, 1, 0), //藍色凱蘭特手套
	/*盜賊短劍共用*/
	Array(1332019, 1, 0), //金剛刃
	Array(1332022, 1, 0), //小妖精刀
	Array(1332016, 1, 0), //華氏短劍
	Array(1402003, 1, 0), //虎劍
	Array(1402004, 1, 0), //青雲劍
	Array(1412009, 1, 0), //大力神之斧
	Array(1402011, 1, 0), //無極劍
	Array(1402012, 1, 0), //霸王劍
	Array(1402015, 1, 0), //亞歷山大之劍
	Array(1412010, 1, 0), //格洛斧
	Array(1412007, 1, 0), //光明斧
	Array(1412008, 1, 0), //雷電斧
	Array(1422012, 1, 0), //雷神之錘
	Array(1422010, 1, 0), //封魂之錘
	Array(1422009, 1, 0), //妖精之錘
	Array(1432004, 1, 0), //丈八蛇矛
	Array(1432006, 1, 0), //十字槍
	Array(1432007, 1, 0), //銀龍槍
	Array(1432010, 1, 0), //奧丁手戟
	Array(1442005, 1, 0), //九龍刀
	Array(1442010, 1, 0), //方天戟
	Array(1442019, 1, 0), //月神刀
	Array(1442033, 1, 0), //黃龍刀
	Array(1000039, 1, 0), //戰士覺醒套裝帽子
	Array(1001059, 1, 0), //戰士覺醒套裝帽子
	Array(1002028, 1, 0), //銀製十字軍帽子
	Array(1002029, 1, 0), //將軍的紅頭盔
	Array(1002030, 1, 0), //銀製戰盔
	Array(1002338, 1, 0), //赤龍頭盔
	Array(1002339, 1, 0), //藍龍頭盔
	Array(1002340, 1, 0), //黑龍頭盔
	Array(1040087, 1, 0), //藍板金鎧甲
	Array(1040091, 1, 0), //紅鋼玉鎧甲
	Array(1040103, 1, 0), //鋰礦暴風甲
	Array(1050082, 1, 0), //藍白銀鎧甲
	Array(1050162, 1, 0), //戰士覺醒套裝套服
	Array(1051077, 1, 0), //黃白銀鎧甲
	Array(1051079, 1, 0), //藍白銀鎧甲
	Array(1060076, 1, 0), //藍板金褲
	Array(1060080, 1, 0), //紅鋼玉褲
	Array(1060092, 1, 0), //紫礦暴風褲
	Array(1061119, 1, 0), //粉色虎雲裙
	Array(1061122, 1, 0), //紫色厄索爾裙子
	Array(1072135, 1, 0), //黃金將軍靴
	Array(1072156, 1, 0), //黑飛魂鞋
	Array(1072212, 1, 0), //黑色水晶靴
	Array(1082010, 1, 0), //鋰礦合金手套
	Array(1082060, 1, 0), //藍戰鬥手套
	Array(1082114, 1, 0), //馬爾斯藍拳套
	Array(1082105, 1, 0), //黑暗影手套
	Array(1102011, 1, 0), //藍色守護披風
	Array(1102012, 1, 0), //紅色守護披風
	Array(1102017, 1, 0), //白色魔法披風
	Array(1102021, 1, 0), //藍鬥士披風
	Array(1102035, 1, 0), //黑龍紋披風
	Array(1102043, 1, 0), //褐色冒險家披風
	Array(1102057, 1, 0), //玩具披風
	Array(1102064, 1, 0), //鬼怪披風
	Array(1102085, 1, 0), //黃色蓋亞披風
	Array(1092010, 1, 0), //古老銀盾
	Array(1092015, 1, 0), //鋼鐵古盾牌
	Array(1092023, 1, 0), //青銅尖刺護盾
	Array(1092025, 1, 0), //金尖刺護盾
	Array(1092028, 1, 0), //金圓盾
	Array(1092037, 1, 0) //紫色斯巴達盾
	
);
var itemListNormal = Array(
	//全職業玩具
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
		var ps = conn.prepareStatement("SELECT * FROM lottery WHERE type=9330117 ORDER BY id desc LIMIT 5");
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
        text += "-----------------------------------------------\r\n#L0##b【抽獎 5百萬 楓幣/次】#l #L1##d【獎品預覽】#l\r\n#L2##r【抽獎 "+ priceD +" 贊助點/次】#l#k"+ ttxt +" \r\n\r\n \r\n";
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
			if(selection == 0 && cm.getMeso() >= 10000000)
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
					if(selection == 0 && cm.getMeso() >= 5000000)
						cm.gainMeso(-5000000);
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
                        insert.setInt(6, 9330117);
                        insert.executeUpdate(); //更新
                        insert.close();
						var time = item[2] == 0?"":("為期" +item[2]+ "天");
						cm.gainGachaponItem(item[0], item[1], "經驗值轉蛋機", true, item[2]);
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