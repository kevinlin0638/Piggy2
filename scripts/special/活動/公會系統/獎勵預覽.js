var tt = ""; //饼干兔子
var event_name = '紅包活動'
var event_tiem = 4000306;
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
			Array(1, 3015262, 1), // 椅子
			Array(1, 1202118, 1), // 圖騰

			// 禮包2 30000
			Array(2, 1142855, 1, 20), // 最強公會
			Array(2, 4310003, 15), // 金牌
			Array(2, 2340000, 9), // 祝福捲軸
			Array(2, 2049120, 5), // 神蹟混沌捲軸
			Array(2, 4030007, 1), // 破攻 1000 萬
			Array(2, 3015915, 1), // 椅子
			Array(2, 1202118, 1), // 圖騰

			// 禮包3 50000
			Array(3, 4310003, 10), // 金牌
			Array(3, 2340000, 8), // 祝福捲軸
			Array(3, 2049119, 5), // 神蹟混沌捲軸
			Array(3, 4030006, 8), // 破攻 100 萬
			Array(3, 3015915, 1), // 椅子
			Array(3, 1202118, 1), // 圖騰

			// 禮包4 75000
			Array(4, 4310003, 8), // 金牌
			Array(4, 2340000, 7), // 祝福捲軸
			Array(4, 2049118, 5), // 神蹟混沌捲軸
			Array(4, 4030006, 6), // 破攻 100 萬
			Array(4, 3015915, 1), // 椅子
			Array(4, 1202118, 1), // 圖騰

			// 禮包5 100000
			Array(5, 4310003, 6), // 金牌
			Array(5, 2340000, 6), // 祝福捲軸
			Array(5, 2049117, 5), // 神蹟混沌捲軸
			Array(5, 4030006, 4), // 破攻 100 萬
			Array(5, 3015915, 1), // 椅子
			Array(5, 1202118, 1), // 圖騰
			
			// 禮包6 130000
			Array(6, 4310003, 5), // 金牌
			Array(6, 2340000, 5), // 祝福捲軸
			Array(6, 2049116, 5), // 神蹟混沌捲軸
			Array(6, 4030006, 2), // 破攻 100 萬
			Array(6, 3015915, 1), // 椅子
			Array(6, 1202118, 1), // 圖騰

			// 禮包7 150000
			Array(7, 4310003, 5), // 金牌
			Array(7, 2340000, 5), // 祝福捲軸
			Array(7, 4030006, 2), // 破攻 100 萬
			Array(7, 3015915, 1), // 椅子
			Array(7, 1202118, 1), // 圖騰
			
			// 禮包8 200000
			Array(8, 4310003, 5), // 金牌
			Array(8, 2340000, 3), // 祝福捲軸
			Array(8, 4030006, 1), // 破攻 100 萬
			Array(8, 3015915, 1), // 椅子
			Array(8, 1202118, 1), // 圖騰
			
			// 禮包9 300000
			Array(9, 4310003, 5), // 金牌
			Array(9, 2340000, 3), // 祝福捲軸
			Array(9, 3015915, 1), // 椅子
			Array(9, 1202118, 1), // 圖騰
			
			// 禮包10 500000
			Array(10, 4310003, 5), // 金牌
			Array(10, 2340000, 1), // 祝福捲軸
			Array(10, 3015915, 1), // 椅子
			Array(10, 1202118, 1), // 圖騰
			
			// 禮包11 800000
			Array(11, 4310003, 5), // 金牌
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
        text = "\t\t\t" + tt + " #r公會本月排名獎勵預覽#k#n " + tt + "\r\n\r\n";
        text += "#d當前公會排名 :#r " + cm.getGuildRank() + " #dGP： #r" + cm.getGP() + "#k\r\n";



        for (var i = 1; i <= condition.length; i++) {
			if(i != 11)
				text += "#b#L" + i + "#" + tt + " #fEffect/CharacterEff/1112904/0/0# 排名第 " + condition[i - 1] + " 獎勵預覽#l\r\n";
			else
				text += "#b#L11#" + tt + " #fEffect/CharacterEff/1112904/0/0# 排名第 11 名以上 獎勵預覽#l\r\n ";
        }
        text += "#k";
        cm.sendSimple(text);
    } else if (status == 1) {
        sel = selection;
		if(i != 11)
			text = "\t\t\t#b #fEffect/CharacterEff/1112904/0/0# 排名第 " + sel + " 獎勵預覽 #fEffect/CharacterEff/1112904/0/0##k#n\r\n\r\n";
		else
			text = "\t\t\t#b #fEffect/CharacterEff/1112904/0/0# 排名第 11 名以上 獎勵預覽 #fEffect/CharacterEff/1112904/0/0##k#n\r\n\r\n";
        for (var i = 0; i < reward.length; i++) {
            if (reward[i][0] == selection) {
                text += "\t\t\t#k#i" + reward[i][1] + "# #z" + reward[i][1] + "##r[" + reward[i][2] + "個]";
				if( (reward[i][3] != null && reward[i][3] > 0) || reward[i][1] == 1142855){
					text += ' #b\r\n\t\t\t全能力增加 ' + reward[i][3] + '點 - 時效 30 天\r\n'
				}else{
					text +='\r\n'
				}
            }
        }
        cm.sendYesNo(text);
    } else{
		cm.dispose();
		return;
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