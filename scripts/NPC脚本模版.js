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
	    cm.sendOk("第一次");
            cm.dispose();
            break;
        case 1: //
            cm.dispose();//这是结束脚本，請按照实际情况使用
            break;
        case 2:
        case 3:
            cm.dispose();
            break;
    }
}
