var tt = ""; //饼干兔子
// 每個阶段禮包所需的贊助数
var condition = [100, 500, 1000, 3000, 5000, 7500, 10000,12000, 15000];
// 禮包内容
var reward = [
		// 禮包1 100元
		Array(1, 2046002 , 5, -1), // 攻擊力卷軸 50%
		Array(1, 2046003 , 5, -1), // 攻擊力卷軸 50%
		Array(1, 2046102 , 5, -1), // 攻擊力卷軸 50%
		Array(1, 2046103 , 5, -1), // 攻擊力卷軸 50%
		
		Array(1, 2000005, 1000), // 超水

        // 禮包2 500元
		Array(2, 2046002 , 10, -1), // 攻擊力卷軸 50%
		Array(2, 2046003 , 10, -1), // 攻擊力卷軸 50%
		Array(2, 2046102 , 10, -1), // 攻擊力卷軸 50%
		Array(2, 2046103 , 10, -1), // 攻擊力卷軸 50%
		
		Array(2, 5220010, 2), // 超轉
		Array(2, 1102451, 1, 15), //黑暗雷鳥奧拉
		Array(2, 3010703, 1), //彩虹椅

        // 禮包3 1000元
		Array(3, 2046025 , 5, -1), // 8樂
		Array(3, 2046026 , 5, -1), // 8樂
		Array(3, 2046119 , 5, -1), // 8樂
		Array(3, 2046120 , 5, -1), // 8樂
		
		Array(3, 5220010, 3), // 超轉
		Array(3, 4021029, 1), //靈魂精隨
		Array(3, 3010704, 1), //嚴懲的月妙椅
		Array(3, 1003861, 1, 15), //漂浮皇冠

        // 禮包4 3000元
		Array(4, 2046025 , 10, -1), // 8樂
		Array(4, 2046026 , 10, -1), // 8樂
		Array(4, 2046119 , 10, -1), // 8樂
		Array(4, 2046120 , 10, -1), // 8樂
		
		Array(4, 5220010, 10), // 超轉
		Array(4, 2340000, 5), //祝福卷軸
		Array(4, 4021030, 1), //6D
		Array(4, 3015278, 1), //烏魯斯覓食椅
		Array(4, 1012639, 1, 15), //彩虹臉紅紅

        // 禮包5 5000元
		Array(5, 2046048 , 5, -1), // 傳說武器攻擊卷軸 60%
		Array(5, 2046049 , 5, -1), // 傳說武器攻擊卷軸 60%
		Array(5, 2046132 , 5, -1), // 傳說武器攻擊卷軸 60%
		Array(5, 2046135 , 5, -1), // 傳說武器攻擊卷軸 60%
		
		Array(5, 5220010, 10), // 超轉
		Array(5, 2340000, 10), //祝福卷軸
		Array(5, 4021029, 1), //靈魂精隨
		Array(5, 5540000, 1), //折價券
		Array(5, 3010747, 1), //綠水靈放風箏
		
		// 禮包6 7500元
		Array(6, 2046048 , 10, -1), // 傳說武器攻擊卷軸 60%
		Array(6, 2046049 , 10, -1), // 傳說武器攻擊卷軸 60%
		Array(6, 2046132 , 10, -1), // 傳說武器攻擊卷軸 60%
		Array(6, 2046135 , 10, -1), // 傳說武器攻擊卷軸 60%
		
		Array(6, 5220010, 10), // 超轉
		Array(6, 2340000, 20), //祝福卷軸
		Array(6, 5540000, 1), //折價券
		Array(6, 1053257, 1, 15), //玩偶之夢
		Array(6, 3015181, 1), //夜空中的月亮椅子

        // 禮包7 10000元
		Array(7, 2046052 , 5, -1), // 不滅的傳說
		Array(7, 2046053 , 5, -1), // 不滅的傳說
		Array(7, 2046134 , 5, -1), // 不滅的傳說
		Array(7, 2046137 , 5, -1), // 不滅的傳說
		
		Array(7, 5220010, 25), // 超轉
		Array(7, 2340000, 20), //祝福卷軸
		Array(7, 4021029, 1), //靈魂精隨
		Array(7, 1032234, 1, 40), // 藍色愛心耳環
		Array(7, 3015111, 1), //青蛙自由落體椅
		
		// 禮包8 12000元
		Array(8, 2046052 , 10, -1), // 不滅的傳說
		Array(8, 2046053 , 10, -1), // 不滅的傳說
		Array(8, 2046134 , 10, -1), // 不滅的傳說
		Array(8, 2046137 , 10, -1), // 不滅的傳說
		
		Array(8, 5220010, 10), // 超轉
		Array(8, 2340000, 20), //祝福卷軸
		Array(8, 4021029, 1), //靈魂精隨
		Array(8, 1702563, 1, 15), //覺醒刀片
		Array(8, 3015545, 1), //開花的玫瑰天使
		
		// 禮包9 15000元 
		Array(9, 1142544, 1), // 神蹟渾沌卷軸
		Array(9, 2049120, 20), // 神蹟渾沌卷軸
		Array(9, 5220010, 25), // 超轉
		Array(9, 2340000, 25), //祝福卷軸
		Array(9, 1022274, 1, 15), //紫色 玫瑰人
		Array(9, 4021029, 1), //靈魂精隨
		Array(9, 3015801, 1) //鯨魚雲朵椅子
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