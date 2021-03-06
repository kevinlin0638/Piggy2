var status = 0;
var text;
var choice;
var type;
var get_type;
var set_type;
var items = Array(//(價格,type,ItemID,批量購買,是否顯示) type : -1 - 楓幣 0 - 贊助點 1 - Gash 2 - 楓點 3 - 道場點數
		Array(1000,3,1132000,false,true),Array(3000,3,1132001,false,true),Array(6000,3,1132002,false,true),Array(10000,3,1132003,false,true),
		Array(20000,3,1132004,false,true),Array(500000,3,1142064,false,true),Array(1000000,3,3010054,false,true),
		Array(500000,3,2022530,true,true),Array(1800,3,2000004,true,true),
		Array(150000,3,2340000,true,true),
		Array(1000,3,4008000,true,true),Array(1000,3,4008001,true,true),Array(1000,3,4008002,true,true),Array(1000,3,4008003,true,true),
		Array(750,3,5050000,true,true)
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
							get_type = cm.getRMB();
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
							type = "#v" + items[i][2] + "##z" + items[i][2]+ "#";
							get_type = cm.getItemQuantity(items[choice][1]);
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
					get_type = cm.getRMB();
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
					get_type = cm.getRMB();
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
						cm.setRMB((cm.getRMB()-money));
						get_type = cm.getRMB();
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
						cm.getPlayer().setDojo(cm.getDojoPoints() - money);
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
