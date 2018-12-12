function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    }
    else {
        if (status >= 0 && mode == 0) {
            cm.dispose();
            return;    
        }
        if (mode == 1) {
            status++;
        }
        else {
            status--;
        }
        if (status == 0) {
            cm.sendSimple("Hello #r#h ##k, I'm #eWizStory's#n #rUniversal NPC#k!\r\n\r\n#fUI/UIWindow.img/QuestIcon/3/0# #b\r\n#L1#Job Advancer#l\r\n#L2#World Tour#l\r\n#L3#All in One Shop#l\r\n#L4#Male Hair Styler#l\r\n#L5#Female Hair Styler#l\r\n#L6#Trade Mesos for Wiz Coins#l\r\n#L7#Custom Map Warper#l\r\n#L8#Universal Shop#l#k");
        } else if (status == 1) {
            if (selection == 1) {
                cm.dispose();
				cm.openNpc(9000036);
            } else if (selection == 2) {
				cm.dispose();
               	cm.openNpc(9000020);
            } else if (selection == 3) {
				cm.dispose();
                cm.openNpc(1061008);
            } else if (selection == 4) {
				cm.dispose();
                cm.openNpc(9900000);
            } else if (selection == 5) {
				cm.dispose();
                cm.openNpc(9900001);
            } else if (selection == 6) {
				cm.sendOk("Use the commands #r@coinplz#k (#eto get coins#n) and #r@mesoplz#k (#eto get mesos#n) instead.");
				cm.dispose();
            } else if (selection == 7) {
				cm.dispose();
                cm.openNpc(9001108);
			} else if (selection == 8) {
				cm.dispose();
				cm.openShop(61);
            }
        }
    }
}