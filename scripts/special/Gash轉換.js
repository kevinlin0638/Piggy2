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
			cm.sendYesNo("您好 我是Gash轉換員 請問您要轉換Gash嗎?\r\n#b我一次可以將#r500楓點#b轉換為#r500Gash#k");
            break;
        case 1: //
            cm.sendYesNo("您確定要將 #r500楓點#b轉換為#r500Gash #k嗎? 確認後無法反悔");
            break;
        case 2:
			if(cm.getNX(2) < 500){
				cm.sendOk("楓點不足");
				cm.dispose();
				return;
			}
			cm.gainNX(2, -500);
			cm.gainNX(1, 500);
			cm.sendOk("轉換完成");
            cm.dispose();
        case 3:
            cm.dispose();
            break;
    }
}
