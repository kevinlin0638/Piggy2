var tt = ""; 
var month = 3;
// 每個阶段禮包所需的贊助数
var condition = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11];
// 禮包内容
var reward = [
					// 禮包1 10000
			Array(1, 1142855, 1, 30), // 最強公會
			Array(1, 4310003, 20), // 金牌
			Array(1, 2340000, 10), // 祝福捲軸
			Array(1, 2049120, 8), // 神蹟混沌捲軸
			Array(1, 4030007, 2), // 破攻 1000 萬

			// 禮包2 30000
			Array(2, 1142855, 1, 20), // 最強公會
			Array(2, 4310003, 15), // 金牌
			Array(2, 2340000, 9), // 祝福捲軸
			Array(2, 2049120, 5), // 神蹟混沌捲軸
			Array(2, 4030007, 1), // 破攻 1000 萬

			// 禮包3 50000
			Array(3, 4310003, 10), // 金牌
			Array(3, 2340000, 8), // 祝福捲軸
			Array(3, 2049119, 5), // 神蹟混沌捲軸
			Array(3, 4030006, 8), // 破攻 100 萬

			// 禮包4 75000
			Array(4, 4310003, 8), // 金牌
			Array(4, 2340000, 7), // 祝福捲軸
			Array(4, 2049118, 5), // 神蹟混沌捲軸
			Array(4, 4030006, 6), // 破攻 100 萬

			// 禮包5 100000
			Array(5, 4310003, 6), // 金牌
			Array(5, 2340000, 6), // 祝福捲軸
			Array(5, 2049117, 5), // 神蹟混沌捲軸
			Array(5, 4030006, 4), // 破攻 100 萬
			
			// 禮包6 130000
			Array(6, 4310003, 5), // 金牌
			Array(6, 2340000, 5), // 祝福捲軸
			Array(6, 2049116, 5), // 神蹟混沌捲軸
			Array(6, 4030006, 2), // 破攻 100 萬

			// 禮包7 150000
			Array(7, 4310003, 5), // 金牌
			Array(7, 2340000, 5), // 祝福捲軸
			Array(7, 4030006, 2), // 破攻 100 萬
			
			// 禮包8 200000
			Array(8, 4310003, 5), // 金牌
			Array(8, 2340000, 3), // 祝福捲軸
			Array(8, 4030006, 1), // 破攻 100 萬
			
			// 禮包9 300000
			Array(9, 4310003, 5), // 金牌
			Array(9, 2340000, 3), // 祝福捲軸
			
			// 禮包10 500000
			Array(10, 4310003, 5), // 金牌
			Array(10, 2340000, 1), // 祝福捲軸
			
			// 禮包11 800000
			Array(11, 4310003, 3), // 金牌
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

    if (status == 10000) {
        var revenue = cm.getTotalDonate();
        text = "\t\t\t" + tt + " #r累積贊助禮包中心#k#n " + tt + "\r\n\r\n";
        text += "#d當前累積贊助數量： #r" + revenue.formatMoney(0, "") + " #d贊助點#k\r\n";



        for (var i = 1; i <= condition.length; i++) {
            if (cm.getEventCount("累積贊助禮包" + i, 1) == 1) {
                text += "#b#L" + i + "#" + tt + " [#r已完成#d]累積贊助福利 #r\t\t\t" + condition[i - 1] + "#l\r\n";
                curlevel = curlevel == -1 ? i : curlevel;
            } else {
                text += "#b#L" + i + "#" + tt + " [未完成]累積贊助福利 #r\t\t\t" + condition[i - 1] + "#l\r\n";
            }
        }
        text += "#k";
        cm.sendSimple(text);
    } else if (status == 0) {
		selection = cm.getPlayer().getGuild().getLastMrk();
		if(selection > 11)
			selection = 11;
        sel = selection;
		can_choose = 0;
		var ss = '';
        if(i != 11)
			text = "\t\t\t#b #fEffect/CharacterEff/1112904/0/0# 排名第 " + sel + " 獎勵 #fEffect/CharacterEff/1112904/0/0##k#n\r\n\r\n";
		else
			text = "\t\t\t#b #fEffect/CharacterEff/1112904/0/0# 排名第 11 名以上 獎勵 #fEffect/CharacterEff/1112904/0/0##k#n\r\n\r\n";
        for (var i = 0; i < reward.length; i++) {
            if (reward[i][0] == selection) {
				if(reward[i][3] != null && reward[i][3] == -1){
					can_choose += 1;
					text += "\t#L"+ i +"##k#i" + reward[i][1] + "# #z" + reward[i][1] + "##r[" + reward[i][2] + "個]#l\r\n";
                }else{
					ss += "\t\t\t#k#i" + reward[i][1] + "# #z" + reward[i][1] + "##r[" + reward[i][2] + "個]";
					if(reward[i][3] != null && reward[i][3] > 0){
						ss += ' \r\n\t\t\t#b全能力增加 ' + reward[i][3] + '點 - 時效 30 天\r\n'
					}else if(reward[i][1] == 1142544){
						ss +='\r\n\t\t\t  全能力增加 60 且 增加強力潛能\r\n\r\n';
					}else{
						ss +='\r\n';
					}
				}
            }
        }
		if(can_choose > 0){
			text += "\r\n\t\t\t\t#b- 以上道具可選一個 -#k#n\r\n";
		}
		text += ss;
        cm.sendYesNo(text);
    } else if (status == 1) {
        if (cm.getEventCount(month + "月公會排名禮包" + sel, 1) == 1) {
            cm.sendOk("#r\r\n\r\n\t\t這個禮包您已經領取過了");
            cm.dispose();
            return;
        }
		
		if(cm.getEventCount("領獎帳號註冊",1) <= 0){
			cm.sendOk("#r\r\n\r\n\t\t只有領獎帳號才能領取");
            cm.dispose();
            return;
		}
		
		if(cm.getPlayer().getGuildContribution() < 3000){
			cm.sendOk("#r\r\n\r\n\t\t只有曾在此公會貢獻超過3000的玩家可以領取");
            cm.dispose();
            return;
		}

        var rewardlist = [];
        for (var i = 0; i < reward.length; i++) {
            if (reward[i][0] == sel) {
                if (reward[i][3] == null)
                    reward[i][3] = 0;
				else if(reward[i][3] == -1 && i != selection){
					continue;
				}
                rewardlist.push([reward[i][1], reward[i][2], reward[i][3]]);
            }
        }
        if (!cm.canHoldSlots(rewardlist.length)) {
            cm.sendOk("背包空間不足，請卻保背包每個欄位有至少 " + rewardlist.length + " 格空間");
            cm.dispose();
            return;
        }
        for (var i = 0; i < rewardlist.length; i++) {
            if(rewardlist[i][0] == 1142544){
				cm.addWithPara(1142544, 58, 58, 58, 58, 60, 60, -1, 0);
			}else if(rewardlist[i][2] > 0){
				cm.addWithPara(rewardlist[i][0], rewardlist[i][2], rewardlist[i][2], rewardlist[i][2], rewardlist[i][2], rewardlist[i][2], rewardlist[i][2], 30, 0);
			}else {
                cm.gainItem(rewardlist[i][0], rewardlist[i][1]);
            }
        }
        cm.setEventCount(month + "月公會排名禮包" + sel, 1);
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