var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) { 
            cm.sendSimple("#rSo you've trained hard...are you ready to put your skills to the test? Seeking challenges that will thrill and amuse your senses?\r\n		If so..then PvP is just the thing for you! Choose a map:#k#b\r\n#L1#Regular#l\r\n#L2#City#l");//idk what they are lol
        } else if (status == 1) {
            if (selection == 1) {
                cm.warp(20, 0);
				cm.dispose();
            } else if (selection == 2) {
				cm.warp(48, 0);
				cm.dispose();
			}
        } 
    }
}