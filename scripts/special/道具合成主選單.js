// 萬能NPC


var status;

var NPC_List = [ //id, text
	[1012002, "道具合成/消耗品製作", "消耗製作"],
	[9209000, "道具合成/武器類", "武器類品"],
	[9209000, "道具合成/防具類", "防具類品"],
	[1012002, "道具合成/特殊道具", "特殊道具"],
	[1012002, "道具合成/楓葉道具", "楓葉道具"],
	[1012002, "道具合成/神話耳環", "#r神話耳環"],
	[1012002, "道具合成/恰吉面具", "#r恰吉面具"],
	[1012002, "道具合成/楓葉之心", "#r楓葉之心"],
	[1012002, "道具合成/意志腰帶", "#r意志腰帶"],
	[1012002, "道具合成/獨眼巨人", "#r獨眼巨人"],
	[1012002, "道具合成/波賽頓系列", "#r波賽頓套"],
	[1012002, "道具合成/神蹟", "#r神蹟道具"]
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
