var tt = ""; //饼干兔子
var event_name = '收集活動3月'
var event_tiem = 4032056;
// 每個阶段禮包所需的贊助数
var condition = [10000, 30000, 50000, 75000, 100000, 130000, 150000, 200000, 300000, 500000, 800000, 1000000, 1500000, 2000000, 3000000];
// 禮包内容
var reward = [
		// 禮包1 10000
		Array(1, 2340000, 15), // 祝福捲軸

        // 禮包2 30000
		Array(2, 4310003, 5), // 金牌

        // 禮包3 50000
		Array(3, 4030006, 3), // 破攻 100 萬

        // 禮包4 75000
		Array(4, 2049401 , 5), // 稀有潛在能力附予卷軸 80%

        // 禮包5 100000
		Array(5, 4030006, 5), // 破攻 100 萬
		
		// 禮包6 130000
		Array(6, 4030006, 10), // 破攻 100 萬

        // 禮包7 150000
		Array(7, 2049116, 5), // 驚訝混沌捲軸
		
		// 禮包8 200000
		Array(8, 4310003, 15), // 金牌
		
		// 禮包9 300000
		Array(9, 2049116, 10), // 驚訝混沌捲軸
		
		// 禮包10 500000
		Array(10, 4030007, 3), // 破攻 1000 萬
		
		// 禮包11 800000
		Array(11, 2049117, 10), // 驚訝混沌捲軸
		
		// 禮包12 1000000
		Array(12, 5062002, 250), // 傳說方塊
		
		// 禮包13 1500000
		Array(13, 4030007, 5), // 神蹟混沌捲軸
		
		// 禮包14 2000000
		Array(14, 4030008, 1), // 破攻 1 億
		
		// 禮包15 3000000
		Array(15, 3015718, 20), // 大吉椅子
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
        var revenue = cm.getEventCount(event_name, 1);
        text = "\t\t\t" + tt + " #r累積" + event_name +"禮包中心#k#n " + tt + "\r\n\r\n";
        text += "#d當前累積 #v" + event_tiem + "##z" + event_tiem + "# 數量： #r" + revenue.formatMoney(0, "") + " #d個#k\r\n";



        for (var i = 1; i <= condition.length; i++) {
            if (cm.getEventCount("累積" + event_name +"禮包" + i, 1) == 1) {
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
        if (cm.getEventCount("累積" + event_name +"禮包" + sel, 1) == 1) {
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
		
        if (cm.getEventCount(event_name, 1) < condition[sel - 1]) {
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
        if(cm.getPlayer().getGuild() != null)
		    cm.getPlayer().getGuild().gainGP(100, true, cm.getPlayer().getId());
        cm.setEventCount("累積" + event_name +"禮包" + sel, 1);
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