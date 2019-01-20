/*
	制作：彩虹工作室
	功能：贊助點、点券、中介币的相互兌換
	時間：2016年12月22日
*/
var status = -1;
var selected = 0;
var ttt = "#fUI/UIWindow/Quest/icon2/7#";
var ico = "#fEffect/CharacterEff/1112905/0/1#";	//ICO美化圖标



function start () {
	action(1,0,0);
}
function action (mode,type,selection) {
	if (mode == -1 || mode == 0 && status == 0) {
		cm.dispose();
		return;
	} else {
		if (mode == 1) {
			status++;
		} else {
			status--;
		}
	}
	
	if (status == 0) {
		var I = ico;
		text = (I+I+I+I+I+I+I+I+I+I+I+"#e  金融中心  #n"+I+I+I+I+I+I+I+I+I+I+I);
		text +=("\r\n\r\n\t\t（#v4310003##v4310005##v4310004#皆可以與其他玩家交易）\r\n");
		text +=("\r\n#n#b#L0##v4310003##z4310003# :: 楓點\t#r[兌換]");
		text +=("\r\n#n#b#L2##v4310004##z4310004# :: 楓幣  #r[兌換]");
		text +=("\r\n#n#b#L1##v4310005##z4310005# :: 贊助點 \t#r[兌換]#l\r\n\r\n");
		text +=(I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I);
		cm.sendNextPrev(text);
	} else if (status == 1){
		//贊助點換点券
		if (selection == 0){
			cm.dispose();
			cm.openNpc(9300011, "貨幣楓點");
		//点券換金色枫叶
		} else if (selection == 1) {
			cm.dispose();
			cm.openNpc(9300011, "貨幣楓幣");
		//金色枫叶換点券
		}else if (selection == 2) {
			cm.dispose();
			cm.openNpc(9300011, "貨幣贊助點");
		}
		//贊助點成功兌換点券
	}
}