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

var gold_item = Array(
	Array(1132211, 1, 0), //
	Array(1032205, 1, 0), //
	Array(1012164, 1, 0), //
	Array(1022189, 1, 0), //
	Array(1122019, 1, 0) //
);
var mor = Array(
Array(1372035, 1, 0), //旋火短杖
	Array(1372036, 1, 0), //五毒短杖
	Array(1372037, 1, 0), //極凍短杖
	Array(1372038, 1, 0), //雷鳴短杖
	Array(1102011, 1, 0), //藍色守護披風
	Array(1102012, 1, 0), //紅色守護披風
	Array(1102017, 1, 0), //白色魔法披風
	Array(1102021, 1, 0), //藍鬥士披風
	Array(1102035, 1, 0), //黑龍紋披風
	Array(1102043, 1, 0), //褐色冒險家披風
	Array(1102057, 1, 0), //玩具披風
	Array(1102064, 1, 0), //鬼怪披風
	Array(1102085, 1, 0), //黃色蓋亞披風
	Array(1372008, 1, 0), //日本扇
	Array(1372009, 1, 0), //聖權杖
	Array(1372010, 1, 0), //鬼頭杖
	Array(1372011, 1, 0), //羽扇
	Array(1372014, 1, 0), //火焰之杖
	Array(1372016, 1, 0), //鳳凰短杖
	Array(1372025, 1, 0), //天使之翼
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
	Array(1032025, 1, 0),//綠葉耳環
	Array(1482007, 1, 0), //惡魔之爪
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
	Array(1452008, 1, 0), //天弓
	Array(1452009, 1, 0), //赤神弓
	Array(1452011, 1, 0), //黃神弓
	Array(1452013, 1, 0), //火焰艾倫弓
	Array(1452015, 1, 0), //黑色艾倫弓
	Array(1452017, 1, 0), //梅杜斯
	Array(1452019, 1, 0), //白色雷鳥弓
	Array(1452021, 1, 0), //黑色雷鳥弓
	Array(1452071, 1, 0), //貝恩長弓**
	Array(1462007, 1, 0), //炎弩
	Array(1462008, 1, 0), //黃金弩
	Array(1462009, 1, 0), //破天弩弓
	Array(1462010, 1, 0), //滄海弩弓
	Array(1462012, 1, 0), //金天弩弓
	Array(1462013, 1, 0), //黑暗弩弓
	Array(1462017, 1, 0), //黑色獵鷹弓
	Array(1462022, 1, 0), //黃色可撒弩
	Array(1332026, 1, 0), //可撒之劍
	Array(1332019, 1, 0), //金剛刃
	Array(1332022, 1, 0), //小妖精刀
	Array(1332016, 1, 0), //華氏短劍
	Array(1332003, 1, 0), //破碎刃
	Array(1332015, 1, 0), //雙翼刃
	Array(1332017, 1, 0), //金蛇劍
	Array(1332018, 1, 0),//鳳凰刃
	Array(1332037, 1, 0), //黑精靈拳刃
	Array(1342004, 1, 0), //修羅刀
	Array(1342005, 1, 0), //天無刀
	Array(1342006, 1, 0),//龍華刀
	Array(1342007, 1, 0), //萬血刀
	Array(1342008, 1, 0), //流星刀
	Array(1472028, 1, 0), //藍閃電甲
	Array(1472025, 1, 0),//黑戰神拳套
	Array(1472033, 1, 0), //卡帝斯拳套
	Array(1472053, 1, 0), //克利思拳套
	Array(1472904, 1, 0), //龍之拳
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
	Array(1322019, 1, 0), //暴風錘
	Array(1002532, 1, 0), //黑色格萊西頭盔
	Array(1442020, 1, 0), //飛翔斧
	Array(1442044, 1, 0), //阿基里斯斧矛
	Array(1432011, 1, 0), //佛羅利刃
	Array(1432030, 1, 0), //火尖槍
	Array(1412021, 1, 0), //雷神大斧
	Array(1422013, 1, 0), //鐳奧釘錘
	Array(1302023, 1, 0), //坲羅劍
	Array(1402005, 1, 0), //斬魔刀
	Array(1332026, 1, 0) //可撒之劍
);

/*以下為要修改的*/
var rates = [1000,69000,430000,500000];//總共100萬 最好->最差

//物品代碼 數量 時間期限(沒有就0)  只有Advanced會廣播
var itemListAdvanced = Array(
	Array(1022104, 1, 0),
	Array(1022108, 1, 0),
	Array(1022109, 1, 0),
	
	Array(1022110, 1, 0),
	
	Array(1022122, 1, 0),
	
	Array(1022142, 1, 0),
	
	Array(1022173, 1, 0),
	Array(1022174, 1, 0),
	Array(1022176, 1, 0),
	Array(1022177, 1, 0),

	Array(1022187, 1, 0),
	
	Array(1022196, 1, 0),
	
	Array(1022201, 1, 0),
	
	Array(1022223, 1, 0),
	Array(1022227, 1, 0),
	Array(1022229, 1, 0),
	
	Array(1022230, 1, 0),
	
	Array(1022243, 1, 0),
	Array(1022244, 1, 0),
	Array(1022247, 1, 0),
	Array(1022248, 1, 0),
	
	Array(1022259, 1, 0),
	
	Array(1022262, 1, 0),
	Array(1022263, 1, 0),
	Array(1022265, 1, 0),
	Array(1022267, 1, 0),
	
	Array(1022270, 1, 0),
	Array(1022274, 1, 0),
	Array(1022275, 1, 0),
	Array(1022276, 1, 0),
	Array(1022279, 1, 0),
	
	Array(1022280, 1, 0),
	Array(1022282, 1, 0),
	Array(1022283, 1, 0)
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
	
	Array(1382049, 1, 0), //旋火短杖
	Array(1382050, 1, 0), //玄武之杖
	Array(1382051, 1, 0), //青龍之杖
	Array(1382052, 1, 0), //白虎之杖
	
	Array(1382045, 1, 0), //旋火短杖
	Array(1382046, 1, 0), //五毒短杖
	Array(1382047, 1, 0), //極凍短杖
	Array(1382048, 1, 0) //雷鳴短杖
);
var itemListSilver = Array(
	Array(1372035, 1, 0), //旋火短杖
	Array(1372036, 1, 0), //五毒短杖
	Array(1372037, 1, 0), //極凍短杖
	Array(1372038, 1, 0), //雷鳴短杖
	Array(1102011, 1, 0), //藍色守護披風
	Array(1102012, 1, 0), //紅色守護披風
	Array(1102017, 1, 0), //白色魔法披風
	Array(1102021, 1, 0), //藍鬥士披風
	Array(1102035, 1, 0), //黑龍紋披風
	Array(1102043, 1, 0), //褐色冒險家披風
	Array(1102057, 1, 0), //玩具披風
	Array(1102064, 1, 0), //鬼怪披風
	Array(1102085, 1, 0), //黃色蓋亞披風
	Array(1372008, 1, 0), //日本扇
	Array(1372009, 1, 0), //聖權杖
	Array(1372010, 1, 0), //鬼頭杖
	Array(1372011, 1, 0), //羽扇
	Array(1372014, 1, 0), //火焰之杖
	Array(1372016, 1, 0), //鳳凰短杖
	Array(1372025, 1, 0), //天使之翼
	Array(1372038, 1, 0), //雷鳴短杖
	Array(1372035, 1, 0), //旋火短杖
	Array(1372036, 1, 0), //五毒短杖
	Array(1372037, 1, 0), //極凍短杖
	Array(1372059, 1, 0), //奈歐鳳凰短杖
	Array(1382008, 1, 0), //魔靈之魂
	Array(1382013, 1, 0), //火靈之杖
	Array(1382016, 1, 0), //椎茸
	Array(1382035, 1, 0), //怒濤之杖
	Array(1382039, 1, 0), //楓葉智慧長杖
	Array(1002215, 1, 0), //火黃金法冠
	Array(1002242, 1, 0), //紅和平之帽
	Array(1002274, 1, 0), //黑十字帽
	Array(1002365, 1, 0), //紅色綢緞帽
	Array(1002399, 1, 0),//藍色巴爾帽
	Array(1050055, 1, 0), //白神聖服
	Array(1050068, 1, 0), //紅天上之衣
	Array(1050069, 1, 0), //褐天上之衣
	Array(1050073, 1, 0), //藍神仙之服
	Array(1050094, 1, 0), //紅綢緞長袍
	Array(1051052, 1, 0), //藍天上之衣
	Array(1051097, 1, 0), //黑綢緞長袍
	Array(1051103, 1, 0), //紅色拜爾裝 
	Array(1072143, 1, 0), //綠黃金風
	Array(1072157, 1, 0), //藍水晶涼鞋
	Array(1072160, 1, 0), //金水晶涼鞋
	Array(1072208, 1, 0), //紅色艾倫鞋
	Array(1072209, 1, 0), //黑色艾倫鞋
	Array(1072224, 1, 0), //藍色巴爾鞋
	Array(1072225, 1, 0), //紅色巴爾鞋
	Array(1082080, 1, 0), //黑元素手套
	Array(1082082, 1, 0), //藍元素手套
	Array(1082088, 1, 0), //黑封印手套
	Array(1082100, 1, 0), //黑戰魂手套
	Array(1082123, 1, 0), //魔力青手套
	Array(1082134, 1, 0), //黑色貴族手套
	Array(1082133, 1, 0),//紅色貴族手套
	Array(1082153, 1, 0), //紅色希亞手套
	Array(1082154, 1, 0)//紫色希亞手套
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
	for(var i = 1082551;i <= 1082728;i++){
		if(cm.ExistItem(i))
			temp.push(Array(i, 1, 0));
	}
	for(var i = 1102800;i <= 1103122;i++){
		if(cm.ExistItem(i))
			temp.push(Array(i, 1, 0));
	}
	itemListAdvanced = temp;
	for(var item in gold_item)
		itemListAdvanced.push(gold_item[item]);
	
	temp = [];
	for(var i = 1082451;i <= 1082550;i++){
		if(cm.ExistItem(i))
			temp.push(Array(i, 1, 0));
	}
	for(var i = 1102601;i <= 1102800;i++){
		if(cm.ExistItem(i))
			temp.push(Array(i, 1, 0));
	}
	itemListGold = temp;
	
	temp = [];
	for(var i = 1082351;i <= 1082450;i++){
		if(cm.ExistItem(i))
			temp.push(Array(i, 1, 0));
	}
	for(var i = 1102401;i <= 1102600;i++){
		if(cm.ExistItem(i))
			temp.push(Array(i, 1, 0));
	}
	itemListSilver = temp;
	
	temp = [];
	for(var i = 1082233;i <= 1082350;i++){
		if(cm.ExistItem(i))
			temp.push(Array(i, 1, 0));
	}
	for(var i = 1102288;i <= 1102400;i++){
		if(cm.ExistItem(i))
			temp.push(Array(i, 1, 0));
	}
	itemListNormal = temp;
	for(var item in mor)
		itemListNormal.push(mor[item]);
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
		var ps = conn.prepareStatement("SELECT * FROM lottery WHERE type=9330113 ORDER BY id desc LIMIT 5");
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
                        insert.setInt(6, 9330113);
                        insert.executeUpdate(); //更新
                        insert.close();
						var time = item[2] == 0?"":("為期" +item[2]+ "天");
						cm.gainGachaponItem(item[0], item[1], "法師轉蛋機", true, item[2]);
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