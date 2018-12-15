/* guild creation npc */
var status = -1;
var sel;

function start() {
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
	cm.sendSimple("您想要做什麼?\r\n#b#L0#創建公會#l\r\n#L1#解散公會#l\r\n#L2#使用#r楓幣#b增加公會成員數量限制 (最高提升至100人)#l\r\n#L3#使用#rGP#b增加公會成員數量限制 (最高提升至200人)#l#k");
    else if (status == 1) {
	sel = selection;
	if (selection == 0) {
	    if (cm.getPlayerStat("GID") > 0) {
		cm.sendOk("您已經加入公會 無法創建新的公會.");
		cm.dispose();
	    } else
		cm.sendYesNo("創建公會需要花費 #b500,000 楓幣#k, 您確定要繼續嗎?");
	} else if (selection == 1) {
	    if (cm.getPlayerStat("GID") <= 0 || cm.getPlayerStat("GRANK") != 1) {
		cm.sendOk("只有公會長可以解散公會.");
		cm.dispose();
	    } else
		cm.sendYesNo("您確定您要解散公會? 此執行無法反悔且您將失去所有的 GP.");
	} else if (selection == 2) {
	    if (cm.getPlayerStat("GID") <= 0 || cm.getPlayerStat("GRANK") != 1) {
		cm.sendOk("只有公會長可以擴增公會成員數量限制.");
		cm.dispose();
	    } else
		cm.sendYesNo("擴增公會成員數量限制 #b5#k 人 - 需要 #b500,000 楓幣#k, 您確定要繼續?");
	} else if (selection == 3) {
	    if (cm.getPlayerStat("GID") <= 0 || cm.getPlayerStat("GRANK") != 1) {
		cm.sendOk("只有公會長可以擴增公會成員數量限制.");
		cm.dispose();
	    } else
		cm.sendYesNo("擴增公會成員數量限制 #b5#k 人 - 需要 #b25,000 GP#k, 您確定要繼續?");
	}
    } else if (status == 2) {
	if (sel == 0 && cm.getPlayerStat("GID") <= 0) {
	    cm.genericGuildMessage(1);
	    cm.dispose();
	} else if (sel == 1 && cm.getPlayerStat("GID") > 0 && cm.getPlayerStat("GRANK") == 1) {
	    cm.disbandGuild();
	    cm.dispose();
	} else if (sel == 2 && cm.getPlayerStat("GID") > 0 && cm.getPlayerStat("GRANK") == 1) {
	    cm.increaseGuildCapacity(false);
	    cm.dispose();
	} else if (sel == 3 && cm.getPlayerStat("GID") > 0 && cm.getPlayerStat("GRANK") == 1) {
	    cm.increaseGuildCapacity(true);
	    cm.dispose();
	}
    }
}