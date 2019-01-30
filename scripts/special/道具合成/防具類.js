// 萬能NPC


var status;

var NPC_List = [ //id, text
	[1012008, "道具合成/防具/皇凡雷恩", "皇凡雷恩"],
	[1012008, "道具合成/防具/女皇系列", "女皇系列"],
	[1012008, "道具合成/防具/深淵系列", "深淵系列"]
	//[1012008, 4, "暴君系列"]
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
			var Text = "歡迎加入 #b小喵谷#k 請問您有什麼需求:#b\r\n\r\n";
			
			for(var i = 0; i < NPC_List.length;i++){
				Text += "#L" + i + "#" + NPC_List[i][2];
				if((i + 1) % 4 == 0)
					Text += "\r\n"
			}
			cm.sendSimple(Text);
            break;
        case 1:
			switch(selection){
				default:
					cm.dispose();
					cm.openNpc(NPC_List[selection][0], NPC_List[selection][1]);
					break;
			}
            break;
    }
}
