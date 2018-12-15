/* guild emblem npc */
var status = 0;
var sel;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 && status == 0) {
	cm.dispose();
	return;
    }
    if (mode == 1)
	status++;
    else
	status--;

    if (status == 0)
	cm.sendSimple("您想要做什麼?\r\n#b#L0#新增/修改 公會標誌#l#k");
    else if (status == 1) {
	sel = selection;
	if (selection == 0) {
	    if (cm.getPlayerStat("GRANK") == 1)
		cm.sendYesNo("新增/修改 公會標誌需要 #b1,500,000 楓幣#k, 您確定要繼續?");
	    else
		cm.sendOk("您必須是公會長.  請公會長來找我對話.");
	}
				
    } else if (status == 2) {
	if (sel == 0) {
	    cm.genericGuildMessage(18);
	    cm.dispose();
	}
    }
}
