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
var rates = [10000,60000,430000,500000];//總共100萬 最好->最差

//物品代碼 數量 時間期限(沒有就0)  只有Advanced會廣播
var itemListAdvanced = Array(
	Array(1702490, 1, 0),
	Array(1702480, 1, 0),
	Array(1702628, 1, 0),//鄉村美味蛋捲
	Array(1702631, 1, 0),//血腥童話
	Array(1702632, 1, 0),//炎魔之手
	Array(1702634, 1, 0),//楓之谷殭屍
	
	Array(1702682, 1, 0),//楓之谷突擊X戰略家的戰場
	Array(1702679, 1, 0),//粉紅拼圖武器
	Array(1702678, 1, 0),//粉紅拼圖武器
	
	Array(1702722, 1, 0),
	Array(1702718, 1, 0),
	Array(1702736, 1, 0),
	Array(1702705, 1, 0),
	Array(1702694, 1, 0),
	
	Array(1702746, 1, 0),
	Array(1702804, 1, 0),
	Array(1702795, 1, 0),
	
	
	Array(1702301, 1, 0),
	Array(1702304, 1, 0),
	Array(1702305, 1, 0),
	Array(1702306, 1, 0),
	Array(1702308, 1, 0),
	Array(1702309, 1, 0),
	Array(1702310, 1, 0),
	
	Array(1702313, 1, 0),
	Array(1702314, 1, 0),
	Array(1702315, 1, 0),
	Array(1702316, 1, 0),
	Array(1702317, 1, 0),
	Array(1702318, 1, 0),
	Array(1702319, 1, 0),
	Array(1702320, 1, 0),
	
	
	Array(1702321, 1, 0),
	Array(1702322, 1, 0),
	Array(1702323, 1, 0),
	Array(1702324, 1, 0),
	Array(1702328, 1, 0),
	Array(1702329, 1, 0),
	Array(1702330, 1, 0),
	
	Array(1702334, 1, 0),
	Array(1702336, 1, 0),
	Array(1702337, 1, 0),
	Array(1702340, 1, 0),
	
	
	Array(1702341, 1, 0),
	Array(1702342, 1, 0),
	Array(1702346, 1, 0),
	Array(1702347, 1, 0),
	Array(1702348, 1, 0),
	Array(1702349, 1, 0),
	Array(1702350, 1, 0),

	Array(1702352, 1, 0),
	Array(1702357, 1, 0),
	Array(1702359, 1, 0),
	
	Array(1702361, 1, 0),
	Array(1702362, 1, 0),
	Array(1702363, 1, 0),
	Array(1702364, 1, 0),
	Array(1702366, 1, 0),
	Array(1702367, 1, 0),
	Array(1702368, 1, 0),
	
	Array(1702370, 1, 0),
	Array(1702371, 1, 0),
	Array(1702372, 1, 0),
	Array(1702375, 1, 0),
	Array(1702377, 1, 0),
	Array(1702379, 1, 0),
	
	Array(1702382, 1, 0),
	Array(1702385, 1, 0),
	Array(1702386, 1, 0),
	Array(1702387, 1, 0),
	Array(1702389, 1, 0),
	
	Array(1702390, 1, 0),
	Array(1702392, 1, 0),
	Array(1702393, 1, 0),
	Array(1702394, 1, 0),
	Array(1702395, 1, 0),
	Array(1702397, 1, 0),
	Array(1702399, 1, 0)
	
);
var itemListGold = Array(
	Array(1702410, 1, 0),//米哈逸之守護
	Array(1702411, 1, 0),//奧茲之守護
	Array(1702412, 1, 0),//伊麗娜之守護
	Array(1702413, 1, 0),//伊卡勒特之守護
	Array(1702414, 1, 0),//鷹眼之守護
	Array(1702416, 1, 0),//紅蘿蔔帝王
	Array(1702417, 1, 0),//名媛遮陽傘
	Array(1702418, 1, 0),//獵鷹
	Array(1702419, 1, 0),//我的朋友皮卡啾
	Array(1702421, 1, 0),//龍瑟雷弗
	Array(1702200, 1, 0),
	Array(1702201, 1, 0),
	Array(1702202, 1, 0),
	Array(1702203, 1, 0),
	Array(1702204, 1, 0),
	Array(1702207, 1, 0),
	Array(1702209, 1, 0),
	Array(1702210, 1, 0),
	Array(1702211, 1, 0),
	Array(1702212, 1, 0),
	Array(1702213, 1, 0),
	Array(1702215, 1, 0),
	Array(1702216, 1, 0),
	Array(1702217, 1, 0),
	Array(1702218, 1, 0),
	Array(1702219, 1, 0),
	Array(1702220, 1, 0),
	Array(1702221, 1, 0),
	Array(1702226, 1, 0),
	Array(1702228, 1, 0),
	Array(1702230, 1, 0),
	Array(1702256, 1, 0),
	Array(1702257, 1, 0),
	Array(1702258, 1, 0),
	Array(1702259, 1, 0),
	Array(1702260, 1, 0),
	Array(1702261, 1, 0),
	Array(1702262, 1, 0),
	Array(1702263, 1, 0),
	Array(1702264, 1, 0),
	Array(1702266, 1, 0),
	Array(1702268, 1, 0),
	Array(1702269, 1, 0),
	Array(1702270, 1, 0),
	Array(1702271, 1, 0),
	Array(1702272, 1, 0),
	Array(1702273, 1, 0),
	Array(1702274, 1, 0),
	Array(1702275, 1, 0),
	Array(1702277, 1, 0),
	Array(1702279, 1, 0),
	Array(1702280, 1, 0),
	Array(1702281, 1, 0),
	Array(1702282, 1, 0),
	Array(1702283, 1, 0),
	Array(1702284, 1, 0),
	Array(1702285, 1, 0),
	Array(1702287, 1, 0),
	Array(1702288, 1, 0),
	Array(1702289, 1, 0),
	Array(1702291, 1, 0),
	Array(1702293, 1, 0),
	Array(1702299, 1, 0),
	

	
	Array(1702424, 1, 0),//時尚鋼鐵
	Array(1702427, 1, 0),//歐布林路斯戰鬥機
	Array(1702428, 1, 0)//血色紅寶石戰鬥機
);
var itemListSilver = Array(
	Array(1102614, 1, 0), //尾巴造型感應
	Array(1702099, 1, 0), //透明拳套
	Array(1702190, 1, 0), //透明指虎
	Array(1702220, 1, 0), //透明法杖
	Array(1702141, 1, 0), //尾巴造型感應
	Array(1702142, 1, 0), //尾巴造型感應
	Array(1702143, 1, 0), //尾巴造型感應
	Array(1702144, 1, 0), //尾巴造型感應
	Array(1702146, 1, 0), //尾巴造型感應
	Array(1702147, 1, 0), //尾巴造型感應
	Array(1702148, 1, 0), //尾巴造型感應
	Array(1702149, 1, 0), //尾巴造型感應
	Array(1702150, 1, 0), //尾巴造型感應
	Array(1702151, 1, 0), //尾巴造型感應
	Array(1702152, 1, 0), //尾巴造型感應
	Array(1702153, 1, 0), //尾巴造型感應
	Array(1702154, 1, 0), //尾巴造型感應
	Array(1702155, 1, 0), //尾巴造型感應
	Array(1702156, 1, 0),
	Array(1702157, 1, 0),
	Array(1702158, 1, 0),
	Array(1702161, 1, 0),
	Array(1702162, 1, 0),
	Array(1702163, 1, 0),
	Array(1702164, 1, 0),
	Array(1702165, 1, 0),
	Array(1702166, 1, 0),
	Array(1702167, 1, 0),
	Array(1702168, 1, 0),
	Array(1702169, 1, 0),
	Array(1702170, 1, 0),
	Array(1702171, 1, 0),
	Array(1702172, 1, 0),
	Array(1702173, 1, 0),
	Array(1702175, 1, 0),
	Array(1702176, 1, 0),
	Array(1702177, 1, 0),
	Array(1702179, 1, 0),
	Array(1702180, 1, 0),
	Array(1702181, 1, 0),
	Array(1702182, 1, 0),
	Array(1702183, 1, 0),
	Array(1702184, 1, 0),
	Array(1702185, 1, 0),
	Array(1702187, 1, 0),
	Array(1702188, 1, 0),
	Array(1702191, 1, 0),
	Array(1702193, 1, 0),
	Array(1702195, 1, 0),
	Array(1702196, 1, 0),
	Array(1702199, 1, 0),
	Array(1702260, 1, 0),
	//各種椅子
	Array(3010001, 1, 0),
	Array(3010002, 1, 0),
	Array(3010003, 1, 0),
	Array(3010004, 1, 0),
	Array(3010005, 1, 0),
	Array(3010006, 1, 0),
	Array(3010007, 1, 0),
	Array(3010008, 1, 0),
	Array(3010009, 1, 0),
	Array(3010010, 1, 0),
	Array(3010011, 1, 0),
	Array(3010012, 1, 0),
	Array(3010013, 1, 0),
	Array(3010014, 1, 0),
	Array(3010015, 1, 0),
	Array(3010016, 1, 0),
	Array(3010017, 1, 0),
	Array(3010018, 1, 0),
	Array(3010019, 1, 0),
	Array(3010020, 1, 0),
	Array(3010021, 1, 0),
	Array(3010024, 1, 0),
	Array(3010025, 1, 0),
	Array(3010026, 1, 0),
	Array(3010028, 1, 0),
	Array(3010029, 1, 0),
	Array(3010030, 1, 0),
	Array(3010031, 1, 0),
	Array(3010032, 1, 0),
	Array(3010033, 1, 0),
	Array(3010034, 1, 0),
	Array(3010035, 1, 0),
	Array(3010036, 1, 0),
	Array(3010037, 1, 0),
	Array(3010038, 1, 0),
	Array(3010040, 1, 0),
	Array(3010041, 1, 0),
	Array(3010043, 1, 0),
	Array(3010044, 1, 0),
	Array(3010045, 1, 0),
	Array(3010046, 1, 0),
	Array(3010047, 1, 0),
	Array(3010048, 1, 0),
	Array(3010049, 1, 0),
	Array(3010050, 1, 0),
	Array(3010051, 1, 0),
	Array(3010052, 1, 0),
	Array(3010053, 1, 0),
	Array(3010054, 1, 0),
	Array(3010055, 1, 0),
	Array(3010056, 1, 0),
	Array(3010057, 1, 0),
	Array(3010058, 1, 0),
	Array(3010071, 1, 0)
);
var itemListNormal = Array(
	//全職業玩具
	Array(1072884, 1, 0),//結衣的鞋子
	Array(1072877, 1, 0),//黑暗的小惡魔鞋
	Array(1072873, 1, 0),//亞絲娜的鞋子
	Array(1072871, 1, 0),//萬聖節裝飾皮鞋
	Array(1072869, 1, 0),//時間公主的高跟鞋
	Array(1072867, 1, 0),//羊妹妹鞋
	Array(1072866, 1, 0),//巧克力洋娃娃鞋子
	Array(1072865, 1, 0),//花魁木屐
	Array(1072863, 1, 0),//勃肯涼鞋
	Array(1072855, 1, 0),//異變捍衛者長靴
	Array(1072854, 1, 0),//異變捍衛者長靴
	Array(1072851, 1, 0),//泡泡鞋
	Array(1072849, 1, 0),//馴龍者之靴
	Array(1072843, 1, 0),//彩色泡沫拖鞋
	Array(1072838, 1, 0),//貓熊拖鞋
	
	Array(1062091, 1, 0),//灰色格紋短褲
	Array(1062092, 1, 0),//亮桃紅龐克褲
	Array(1062093, 1, 0),//格雷斯綠色短褲
	Array(1062094, 1, 0),//紅腰帶褲裙
	Array(1062095, 1, 0),//米蘭牛仔褲
	Array(1062096, 1, 0),//多層次奶油色褲裙
	Array(1062097, 1, 0),//黃色補丁牛仔褲
	Array(1062098, 1, 0),//水藍單寧褲
	Array(1062100, 1, 0),//嘻哈造型褲
	Array(1062101, 1, 0),//反折緊身牛仔褲
	Array(1062102, 1, 0),//雙星深藍牛仔褲
	Array(1062103, 1, 0),//螢光褲
	Array(1062104, 1, 0),//暗紫色牛仔褲
	Array(1062105, 1, 0),//英倫反折丹寧褲
	
	Array(1042146, 1, 0),//星星連帽棉T
	Array(1042145, 1, 0),//可愛鴨子T恤
	Array(1042107, 1, 0),//小花飄飄T恤
	Array(1042106, 1, 0),//三色線條套頭T恤
	Array(1042105, 1, 0),//皇冠套頭T恤
	Array(1042104, 1, 0),//綠色基本款T恤
	
	Array(1002973, 1, 0),//神秘的面具
	Array(1002995, 1, 0),//皇家海軍帽
	Array(1003005, 1, 0),//F1安全帽
	Array(1003006, 1, 0),//貓咪童子軍帽
	Array(1003008, 1, 0),//法老王冠
	Array(1003009, 1, 0),//聖誕光芒
	Array(1003010, 1, 0),//飛舞蝴蝶髮飾
	Array(1003014, 1, 0),//桃色防風造型帽
	Array(1003000, 1, 0),//粉紅色rocker頭飾
	Array(1002999, 1, 0),//金毛戰鬥頭套
	Array(1003001, 1, 0),//鐵面雪吉拉面具
	Array(1002296, 1, 0),//綠色水靈帽
	Array(1002293, 1, 0),//藍色睡帽
	Array(1002290, 1, 0)//偽裝鐵帽
);
function doCheck(){
	temp = [];
	for(var i = 1702801;i <= 1702853;i++){
		if(cm.ExistItem(i))
			temp.push(Array(i, 1, 0));
	}
	itemListAdvanced = temp;
	
	temp = [];
	for(var i = 1702601;i <= 1702800;i++){
		if(cm.ExistItem(i))
			temp.push(Array(i, 1, 0));
	}
	itemListGold = temp;
	
	temp = [];
	for(var i = 1702401;i <= 1702600;i++){
		if(cm.ExistItem(i))
			temp.push(Array(i, 1, 0));
	}
	itemListSilver = temp;
	
	temp = [];
	for(var i = 1702000;i <= 1702400;i++){
		if(cm.ExistItem(i))
			temp.push(Array(i, 1, 0));
	}
	itemListNormal = temp;
}
	
function action(mode, type, selection) {
	doCheck();
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
                        insert.setInt(6, 9330119);
                        insert.executeUpdate(); //更新
                        insert.close();
						var time = item[2] == 0?"":("為期" +item[2]+ "天");
						cm.gainGachaponItem(item[0], item[1], "潮流轉蛋機", true, item[2]);
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