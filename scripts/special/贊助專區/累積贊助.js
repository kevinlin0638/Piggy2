var tt = ""; //饼干兔子
// 每個阶段禮包所需的贊助数
var condition = [100, 500, 1000, 3000, 5000, 7500, 10000,12000, 15000];
// 禮包内容
var reward = [
		// 禮包1 100元
		Array(1, 2046048 , 5, -1), // 傳說武器攻擊卷軸 60%
		Array(1, 2046049 , 5, -1), // 傳說武器攻擊卷軸 60%
		Array(1, 2046132 , 5, -1), // 傳說武器攻擊卷軸 60%
		Array(1, 2046135 , 5, -1), // 傳說武器攻擊卷軸 60%
		
		Array(1, 2000005, 1000), // 超水

        // 禮包2 500元
		Array(2, 2046048 , 5, -1), // 傳說武器攻擊卷軸 60%
		Array(2, 2046049 , 5, -1), // 傳說武器攻擊卷軸 60%
		Array(2, 2046132 , 5, -1), // 傳說武器攻擊卷軸 60%
		Array(2, 2046135 , 5, -1), // 傳說武器攻擊卷軸 60%
		
		Array(2, 5220010, 2), // 超轉
		Array(2, 1082102, 1, 15), //透明手套
		Array(2, 3015755, 1), //跟雪女一起的陰陽師椅子

        // 禮包3 1000元
		Array(3, 2046048 , 5, -1), // 傳說武器攻擊卷軸 60%
		Array(3, 2046049 , 5, -1), // 傳說武器攻擊卷軸 60%
		Array(3, 2046132 , 5, -1), // 傳說武器攻擊卷軸 60%
		Array(3, 2046135 , 5, -1), // 傳說武器攻擊卷軸 60%
		
		Array(3, 5220010, 3), // 超轉
		Array(3, 4021029, 1), //靈魂精隨
		Array(3, 3015816, 1), //火之精髓椅子
		Array(3, 1072153, 1, 15), //透明鞋子

        // 禮包4 3000元
		Array(4, 2046048 , 5, -1), // 傳說武器攻擊卷軸 60%
		Array(4, 2046049 , 5, -1), // 傳說武器攻擊卷軸 60%
		Array(4, 2046132 , 5, -1), // 傳說武器攻擊卷軸 60%
		Array(4, 2046135 , 5, -1), // 傳說武器攻擊卷軸 60%
		
		Array(4, 5220010, 10), // 超轉
		Array(4, 2340000, 5), //祝福卷軸
		Array(4, 4021030, 1), //6D
		Array(4, 3015817, 1), //花之風椅子
		Array(4, 1122249, 1, 15), //闇黑龍王項鍊

        // 禮包5 5000元
		Array(5, 2046052 , 5, -1), // 不滅的傳說
		Array(5, 2046053 , 5, -1), // 不滅的傳說
		Array(5, 2046134 , 5, -1), // 不滅的傳說
		Array(5, 2046137 , 5, -1), // 不滅的傳說
		
		Array(5, 5220010, 10), // 超轉
		Array(5, 2340000, 10), //祝福卷軸
		Array(5, 4021029, 1), //靈魂精隨
		Array(5, 3015756, 1), //火焰龍的俠客椅子
		
		// 禮包6 7500元
		Array(6, 2046052 , 5, -1), // 不滅的傳說
		Array(6, 2046053 , 5, -1), // 不滅的傳說
		Array(6, 2046134 , 5, -1), // 不滅的傳說
		Array(6, 2046137 , 5, -1), // 不滅的傳說
		
		Array(6, 5220010, 10), // 超轉
		Array(6, 2340000, 20), //祝福卷軸
		Array(6, 1152101, 1, 15), //杜恩肩部裝飾
		Array(6, 3015757, 1), //重力歪曲傑特椅子

        // 禮包7 10000元
		Array(7, 2046052 , 5, -1), // 不滅的傳說
		Array(7, 2046053 , 5, -1), // 不滅的傳說
		Array(7, 2046134 , 5, -1), // 不滅的傳說
		Array(7, 2046137 , 5, -1), // 不滅的傳說
		
		Array(7, 5220010, 25), // 超轉
		Array(7, 2340000, 20), //祝福卷軸
		Array(7, 4021029, 1), //靈魂精隨
		Array(7, 1132183, 1, 15), // 瑞迪門的弓箭袋腰帶
		Array(7, 3015754, 1), //月光的劍豪椅子
		
		// 禮包8 12000元
		Array(8, 2046052 , 10, -1), // 不滅的傳說
		Array(8, 2046053 , 10, -1), // 不滅的傳說
		Array(8, 2046134 , 10, -1), // 不滅的傳說
		Array(8, 2046137 , 10, -1), // 不滅的傳說
		
		Array(8, 5220010, 10), // 超轉
		Array(8, 2340000, 20), //祝福卷軸
		Array(8, 4021029, 1), //靈魂精隨
		Array(8, 1112908, 1, 15), //歐若拉
		Array(8, 3015750, 1), //黑玫瑰鞦韆椅子
		
		// 禮包9 15000元 
		Array(9, 1142544, 1), // 神蹟渾沌卷軸
		Array(9, 2049120, 20), // 神蹟渾沌卷軸
		Array(9, 5220010, 25), // 超轉
		Array(9, 2340000, 25), //祝福卷軸
		Array(9, 1112908, 1, 15), //歐若拉
		Array(9, 4021029, 1), //靈魂精隨
		Array(9, 3015811, 1) //動物急救隊
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
    } else if (status == 1) {
        sel = selection;
		can_choose = 0;
		var ss = '';
        text = "\t\t\t#r- 累積贊助" + condition[selection - 1] + "贊助點福利 -#k#n\r\n\r\n";
        for (var i = 0; i < reward.length; i++) {
            if (reward[i][0] == selection) {
				if(reward[i][3] != null && reward[i][3] == -1){
					can_choose += 1;
					text += "\t#L"+ i +"##k#i" + reward[i][1] + "# #z" + reward[i][1] + "##r[" + reward[i][2] + "個]#l\r\n";
                }else{
					ss += "\t\t\t#k#i" + reward[i][1] + "# #z" + reward[i][1] + "##r[" + reward[i][2] + "個]";
					if(reward[i][3] != null && reward[i][3] > 0){
						ss += ' #b全能力增加 ' + reward[i][3] + '點\r\n'
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
    } else if (status == 2) {
        if (cm.getEventCount("累積贊助禮包" + sel, 1) == 1) {
            cm.sendOk("#r\r\n\r\n\t\t這個禮包您已經領取過了");
            status = -1;
            //cm.dispose();
            return;
        }
        if (cm.getTotalDonate() < condition[sel - 1]) {
            cm.playerMessage(1, "累積贊助點不足！");
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
				cm.addWithPara(rewardlist[i][0], rewardlist[i][2], rewardlist[i][2], rewardlist[i][2], rewardlist[i][2], rewardlist[i][2], rewardlist[i][2], -1, 0);
			}else {
                cm.gainItem(rewardlist[i][0], rewardlist[i][1]);
            }
        }
        cm.setEventCount("累積贊助禮包" + sel, 1);
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