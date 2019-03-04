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
			cm.sendSimple("請問您想要做什麼?\r\n#L0#觀看公會排名#l\r\n#L1#公會系統#l");
            break;
        case 1: //
			if(selection == 0){
				cm.displayGuildRanks();
				cm.dispose();
			}else if(selection == 1){
				cm.dispose();
				cm.openNpc(9300011, "活動/公會系統/公會系統主選單");
			}else
				cm.dispose();
            break;
        case 2:
        case 3:
            cm.dispose();
            break;
    }
}
