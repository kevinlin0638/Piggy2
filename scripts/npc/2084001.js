var status;

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
			cm.sendSimple("您想要?\r\n#b#L1#活動獎勵#l\r\n#L2#等級獎勵#l\r\n#L3#每日任務#l\r\n#L4#累積贊助#l\r\n#L5#收集活動#l");
            break;
        case 1: //
            cm.dispose();//这是结束脚本，請按照实际情况使用
			if(selection == 1){
				cm.openNpc(2084001, "獎勵");
			}else if(selection == 2){
				cm.openNpc(2084001, "等級獎勵");
			}else if(selection == 3){
				cm.openNpc(2084001, "每日任務");
			}else if(selection == 4){
				cm.openNpc(2084001, "累積贊助");
			}else{
				cm.openNpc(2084001, "活動/收集活動/收集活動主選單");
			}
            break;
        case 2:
        case 3:
            cm.dispose();
            break;
    }
}
