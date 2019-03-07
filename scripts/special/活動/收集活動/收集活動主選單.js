var status;
var event_name = '收集活動3月';
var event_tiem = 4032056;
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
			text = '#k目前全服累積: #b' + cm.getPlayer().getEventCountAll(event_name);
			text += '\r\n#k目前個人累積: #b' + cm.getEventCount(event_name, 1);
			
			text += '\r\n\r\n#d請問您需要什麼服務？#b\r\n#L0#上繳 #r#v' + event_tiem + '##z' + event_tiem + '##l#b\r\n#L1#領取個人累積獎勵#l\r\n#L2#領取全服累積獎勵#l\r\n'
			
			cm.sendSimple(text);
            break;
        case 1: //
			switch(selection){
				case 0:
					cm.dispose();
					cm.openNpc(2001000, "活動/收集活動/上繳收集");
					break;
				case 1:
					cm.dispose();
					cm.openNpc(2001000, "活動/收集活動/個人累積");
					break;
				case 2:
					cm.dispose();
					cm.openNpc(2001000, "活動/收集活動/全服累積");
					break;
			}
            break;
        case 2:
            cm.dispose();
            break;
    }
}
