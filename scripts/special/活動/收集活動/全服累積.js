var tt = ""; //饼干兔子
var event_name = '紅包活動';
var event_tiem = 4000306;
// 每個阶段禮包所需的贊助数
var condition = [300000, 500000, 800000, 1000000, 1500000, 2000000, 3000000, 5000000, 8000000, 10000000, 15000000, 20000000, 30000000];
// 禮包内容
var reward = [
		// 禮包1 300000
		Array(1, 4310003, 3), // 楓點

        // 禮包2 500000
		Array(2, 4310003, 5), // 楓點

        // 禮包3 800000
		Array(3, 3015448, 1), // 替我報仇椅子

        // 禮包4 1000000
		Array(4, 4030006, 3), // 破攻100萬

        // 禮包5 1500000
		Array(5, 4030006, 5), // 破攻100萬
		
		// 禮包6 2000000
		Array(6, 4310003, 10), // 楓點
		
		// 禮包7 3000000
		Array(7, 3010590, 1), // 酸酸甜甜好滋味的甜點椅子
		
		// 禮包8 5000000
		Array(8, 4030006, 7), // 破攻100萬
		
		// 禮包9 8000000
		Array(9, 4030006, 10), // 破攻100萬
		
		// 禮包10 10000000
		Array(10, 3015160, 1), // 乓乓！皮卡啾椅子
		
		// 禮包11 15000000
		Array(11, 4030007, 3), // 破攻1000萬
		
		// 禮包12 20000000
		Array(12, 3015862, 1), // "嗨喲拔蘿蔔～
		
		// 禮包13 30000000
		Array(13, 2049119, 10), // 神蹟渾沌
		];





var sel;
var status = -1;
var text;
var ljname;
var curlevel = -1;
function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }

    if (status == 0) {
        var revenue = cm.getPlayer().getEventCountAll(event_name);
        text = "\t\t\t" + tt + " #r累積#v" + event_tiem + "##z" + event_tiem + "#禮包(全服)中心#k#n " + tt + "\r\n\r\n";
        text += "#d當前累積 #v" + event_tiem + "##z" + event_tiem + "# 數量： #r" + revenue.formatMoney(0, "") + " #d個#k\r\n";



        for (var i = 1; i <= condition.length; i++) {
            if (cm.getEventCount("累積 #v" + event_tiem + "##z" + event_tiem + "# 禮包(全服)" + i, 1) == 1) {
                text += "#b#L" + i + "#" + tt + " [#r已完成#d]累積 #v" + event_tiem + "##z" + event_tiem + "# 福利 #r\t\t\t" + condition[i - 1] + "#l\r\n";
                curlevel = curlevel == -1 ? i : curlevel;
            } else {
                text += "#b#L" + i + "#" + tt + " [未完成]累積 #v" + event_tiem + "##z" + event_tiem + "# 福利 #r\t\t\t" + condition[i - 1] + "#l\r\n";
            }
        }
        text += "#k";
        cm.sendSimple(text);
    } else if (status == 1) {
        sel = selection;
        text = "\t\t\t#r- 累積 #v" + event_tiem + "##z" + event_tiem + "# " + condition[selection - 1] + "個福利 -#k#n\r\n\r\n";
        for (var i = 0; i < reward.length; i++) {
            if (reward[i][0] == selection) {
                text += "\t\t\t#k#i" + reward[i][1] + "# #z" + reward[i][1] + "##r[" + reward[i][2] + "個]";
				if(reward[i][3] != null && reward[i][3] > 0){
					text += ' #b全能力增加 ' + reward[i][3] + '點\r\n'
				}else{
					text +='\r\n'
				}
            }
        }
        cm.sendYesNo(text);
    } else if (status == 2) {
        if (cm.getEventCount("累積" + event_name + "禮包(全服)" + sel, 1) == 1) {
            cm.sendOk("#r\r\n\r\n\t\t這個禮包您已經領取過了");
            status = -1;
            //cm.dispose();
            return;
        }
		if(cm.getEventCount("領獎帳號註冊",1) <= 0){
			cm.sendOk("#r\r\n\r\n\t\t只有領獎帳號才能領取");
            status = -1;
            //cm.dispose();
            return;
		}
		
        if (cm.getPlayer().getEventCountAll(event_name) < condition[sel - 1]) {
            cm.playerMessage(1, "累積個不足！");
            cm.dispose();
            return;
        }

        var rewardlist = [];
        for (var i = 0; i < reward.length; i++) {
            if (reward[i][0] == sel) {
                if (reward[i][3] == null)
                    reward[i][3] = -1;
                rewardlist.push([reward[i][1], reward[i][2], reward[i][3]]);
            }
        }
        if (!cm.canHoldSlots(rewardlist.length)) {
            cm.sendOk("背包空間不足，請卻保背包每個欄位有至少 " + rewardlist.length + " 格空間");
            cm.dispose();
            return;
        }
        for (var i = 0; i < rewardlist.length; i++) {
            if(rewardlist[i][0] == 1143247){
				cm.addWithPara(1143247, 100, 100, 100, 100, 80, 80, -1, 0);
			}else if(rewardlist[i][2] > 0){
				cm.addWithPara(rewardlist[i][0], rewardlist[i][2], rewardlist[i][2], rewardlist[i][2], rewardlist[i][2], rewardlist[i][2], rewardlist[i][2], -1, 0);
			}else {
                cm.gainItem(rewardlist[i][0], rewardlist[i][1]);
            }
        }
        cm.setEventCount("累積" + event_name + "禮包(全服)" + sel, 1);
        cm.playerMessage(1, "領取成功");
        cm.dispose();
    }
}

Number.prototype.formatMoney = function (places, symbol, thousand, decimal) {
    places = !isNaN(places = Math.abs(places)) ? places : 2;
    symbol = symbol !== undefined ? symbol : "　";
    thousand = thousand || ",";
    decimal = decimal || ".";
    var number = this,
            negative = number < 0 ? "-" : "",
            i = parseInt(number = Math.abs(+number || 0).toFixed(places), 10) + "",
            j = (j = i.length) > 3 ? j % 3 : 0;
    return symbol + negative + (j ? i.substr(0, j) + thousand : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousand) + (places ? decimal + Math.abs(number - i).toFixed(places).slice(2) : "");
};