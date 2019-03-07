var status = 0;
var text;
var choice;
var type;
var get_type;
var set_type;
var items = Array(//(價格,type,ItemID,批量購買,是否顯示) type : -1 - 楓幣 0 - 贊助點 1 - Gash 2 - 楓點 3 - 道場點數 其它 - 任意道具
		Array(1000, 4001126 ,1522068,false,true),Array(5000, 4001126 ,1522071,false,true),//雙弩
		Array(1000, 4001126 ,1532073,false,true),Array(5000, 4001126 ,1532074,false,true),//重砲
		Array(500, 4001126 ,1302030,false,true),Array(1000, 4001126 ,1302064,false,true),Array(5000, 4001126 ,1302142,false,true),//單手劍
		Array(500, 4001126 ,2049300,false,false),Array(1000, 4001126 ,1312032,false,true),Array(5000, 4001126 ,1312056,false,true),//單手斧
		Array(500, 4001126 ,2049300,false,false),Array(1000, 4001126 ,1322054,false,true),Array(5000, 4001126 ,1322084,false,true),//單手棍
		Array(500, 4001126 ,1332025,false,true),Array(1000, 4001126 ,1332056,false,true),Array(5000, 4001126 ,1332114,false,true),//短刀
		Array(500, 4001126 ,1342026,false,true),Array(1000, 4001126 ,1342027,false,true),Array(5000, 4001126 ,1342028,false,true),//雙刀
		Array(500, 4001126 ,2049300,false,false),Array(1000, 4001126 ,1372034,false,true),Array(5000, 4001126 ,1372071,false,true),//短杖
		Array(500, 4001126 ,1382012,false,true),Array(1000, 4001126 ,1382039,false,true),Array(5000, 4001126 ,1382093,false,true),//長杖
		Array(500, 4001126 ,2049300,false,false),Array(1000, 4001126 ,1402039,false,true),Array(5000, 4001126 ,1402085,false,true),//雙手劍
		Array(500, 4001126 ,1412011,false,true),Array(1000, 4001126 ,1412027,false,true),Array(5000, 4001126 ,1412055,false,true),//雙手斧
		Array(500, 4001126 ,1422014,false,true),Array(1000, 4001126 ,1422029,false,true),Array(5000, 4001126 ,1422057,false,true),//雙手錘
		Array(500, 4001126 ,1432012,false,true),Array(1000, 4001126 ,1432040,false,true),Array(5000, 4001126 ,1432075,false,true),//槍
		Array(500, 4001126 ,1442024,false,true),Array(1000, 4001126 ,1442051,false,true),Array(5000, 4001126 ,1442104,false,true),//長矛
		Array(500, 4001126 ,1452022,false,true),Array(1000, 4001126 ,1452045,false,true),Array(5000, 4001126 ,1452100,false,true),//弓
		Array(500, 4001126 ,1462019,false,true),Array(1000, 4001126 ,1462040,false,true),Array(5000, 4001126 ,1462085,false,true),//弩
		Array(500, 4001126 ,1472032,false,true),Array(1000, 4001126 ,1472055,false,true),Array(5000, 4001126 ,1472111,false,true),//拳套
		Array(500, 4001126 ,1482021,false,true),Array(1000, 4001126 ,1482022,false,true),Array(5000, 4001126 ,1482073,false,true),//指虎
		Array(500, 4001126 ,1492021,false,true),Array(1000, 4001126 ,1492022,false,true),Array(5000, 4001126 ,1492073,false,true)	//火槍
	);

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) cm.dispose();
    else {
        if (status == 0 && mode == 0) {
            cm.dispose();
            return;
        } else if (status >= 1 && mode == 0) {
            cm.sendOk("好吧，歡迎下次再次光臨！.");
            cm.dispose();
            return;
        }
        if (mode == 1) status++;
        else status--;
        if (status == 0) {
			var choices = "";
            text = "請選擇您要購買的道具：\r\n#r#e#n";
            for (var i = 0; i < items.length; i++) {
				if(items[i][4]){
					switch(items[i][1]){
						case -1:
							type = "楓幣";
							get_type = cm.getMeso();
							break;
						case 0:
							type = "贊助點";
							get_type = cm.getPlayer().getPoints();
							break;
						case 1:
							type = "Gash";
							get_type = cm.getPlayer().getCSPoints(1);
							break;
						case 2:
							type = "楓點";
							get_type = cm.getPlayer().getCSPoints(2);
							break;
						case 3:
							type = "道場點數";
							get_type = cm.getDojoPoints();
							break;
						default:
							type = "#v" + items[i][1] + "##z" + items[i][1]+ "#";
							get_type = cm.getItemQuantity(items[i][1]);
							break;
					}
					choices += "\r\n#b#L" + i + "##v" + items[i][2] + "##z" + items[i][2] + "#　#d需要#r" + items[i][0] + "#d" + type +"#k#l";
				}
			}
			text += "#r您目前有 " + get_type + " " + type + "#b";
			text += choices;
            cm.sendSimple("" + text);
        } else if (status == 1) {
			choice = selection;
			switch(items[choice][1]){
				case -1:
					type = "楓幣";
					get_type = cm.getMeso();
					break;
				case 0:
					type = "贊助點";
					get_type = cm.getPlayer().getPoints();
					break;
				case 1:
					type = "Gash";
					get_type = cm.getPlayer().getCSPoints(1);
					break;
				case 2:
					type = "楓點";
					get_type = cm.getPlayer().getCSPoints(2);
					break;
				case 3:
					type = "道場點數";
					get_type = cm.getDojoPoints();
					break;
				default:
					type = "#v" + items[choice][1] + "##z" + items[choice][1]+ "#";
					get_type = cm.getItemQuantity(items[choice][1]);
					break;
			}
			if(items[choice][3])
				cm.sendGetNumber("你選擇的商品為#b#v" + items[choice][2] + "#售價為：" + items[choice][0] + type +"/個(張)\r\n請輸入您要購買的數量",1,1,(get_type/items[choice][0]));
			else{
				cm.sendYesNo("你選擇的商品為 #b#v" + items[choice][2] + "# #b售價為：#r" + items[choice][0]  +"#b " + type +"#r\r\n\r\n請問您確定要購買?");
			}
        } else if (status == 2) {
			switch(items[choice][1]){
				case -1:
					type = "楓幣";
					get_type = cm.getMeso();
					break;
				case 0:
					type = "贊助點";
					get_type = cm.getPlayer().getPoints();
					break;
				case 1:
					type = "Gash";
					get_type = cm.getPlayer().getCSPoints(1);
					break;
				case 2:
					type = "楓點";
					get_type = cm.getPlayer().getCSPoints(2);
					break;
				case 3:
					type = "道場點數";
					get_type = cm.getDojoPoints();
					break;
				default:
					type = "#v" + items[choice][1] + "##z" + items[choice][1]+ "#";
					get_type = cm.getItemQuantity(items[choice][1]);
					break;
			}
			if(items[choice][3])
				fee = selection;
			else
				fee = 1;
            money = fee*items[choice][0];
			
			if(!cm.canHold(items[choice][2],fee)){
				cm.sendOk("您的背包沒有足夠空間!");
				cm.dispose();
				return;
			}
            if (fee < 0) {
				cm.sendOk("不能輸入0.或者你沒有足夠的"+ type +"!");
				cm.dispose();
            } else if (get_type < money) {
				cm.sendOk("購買失敗，你沒有" + money + type);
				cm.dispose();
            } else {
				switch(items[choice][1]){
					case -1:
						cm.gainMeso(-money);
						get_type = cm.getMeso();
						break;
					case 0:
						cm.getPlayer().gainPoints((cm.getPlayer().getPoints()-money));
						get_type = cm.getPlayer().getPoints();
						break;
					case 1:
						cm.gainNX(1, -money);
						get_type = cm.getPlayer().getCSPoints(1);
						break;
					case 2:
						cm.gainNX(2, -money);
						get_type = cm.getPlayer().getCSPoints(2);
						break;
					case 3:
						cm.setDojoRecord(false,false, -money);
						get_type = cm.getDojoPoints();
						break;
					default:
						cm.gainItem(items[choice][1], -money);
						get_type = cm.getItemQuantity(items[choice][1]);
						break;
				}
				cm.gainItem(items[choice][2], fee);
				cm.sendOk("#b恭喜，購買了#r " + fee + " #b個#r #v" + items[choice][2] + "#\r\n\r\n#r"+ type + " #b餘額為:#r " + get_type + " #b");
				cm.dispose();
            }
        }
    }
}
