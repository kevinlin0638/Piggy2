// 萬能NPC


var status;

var NPC_List = [ //id, text
	[9209001, "道具合成/楓葉道具s/楓葉道具合成", "楓葉武器"],
	[0, 0, "換成經驗"]
]


function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {

    if (mode == 0) {
		cm.dispose();
		return;
    } else if (mode == 1){
		status++;
    } else {
		status--;
    }

    switch (status) {
        case 0: 
			var Text = "歡迎加入 #b小熊谷#k 請問您有什麼需求:#b\r\n\r\n";
			
			for(var i = 0; i < NPC_List.length;i++){
				Text += "#L" + i + "#" + NPC_List[i][2];
				if((i + 1) % 4 == 0)
					Text += "\r\n"
			}
			cm.sendOk(Text);
            break;
        case 1:
			if(selection == 1){
				cm.sendGetNumber("請問您要將多少楓葉換成經驗 1: 500",1,1,5000);
			}else{
				cm.dispose();
				cm.openNpc(NPC_List[selection][0], NPC_List[selection][1]);
			}
			break;
		case 2:
			var num = selection;
			if(cm.haveItem(4001126, num)){
				cm.gainItem(4001126, -num)
				cm.gainExp(500 * num);
				cm.dispose();
			}else{
				cm.sendOk("您沒有足夠的楓葉");
				cm.dispose();
			}
    }
}
