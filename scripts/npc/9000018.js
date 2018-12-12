var status = 0;

function start() {
 status = -1;
 action(1,0,0);
}

function action(mode, type, selection) {
 if (mode == 1) 
	status++;
	else if (mode == 0 && status == 0) {
	  cm.dispose();
	  return;
	} else
	status--;
	if (status == 0) {
	   cm.sendSimple("What would you like to do?\r\n\r\n" 
	   + (cm.getPlayer().AutoJQOnline() ? "#L0#Join a(n) #rAutoJQ#k" : "")
	   + (cm.getPlayer().GMEventOpen() ? "#L1#Join a(n) #bevent#k " : "")
	   + (cm.getChannelServer().getEvent() > 0 ? "#L2#Join a(n) #revent#k" : "") // Maple event
	   + "#l");
	} else if (status == 1) {
		if (selection == 0) {
			if (cm.getPlayer().AutoJQOnline() && cm.getPlayer().getMapId() != cm.getJQMap()) { // check anyways
				cm.getPlayer().JoinEvent();
				cm.dispose();
			} else {
				cm.sendOk("There is no #rAutoJQ#k online.");
				cm.dispose();
			}
		} else if (selection == 1) {
			if (cm.getPlayer().GMEventOpen()) {
				cm.warp(cm.getEventMapByGM());
				cm.dispose();
			} else {
				cm.sendOk("There is no #bevent#k going on.");
				cm.dispose();
			}
		} else if (selection == 2) {
			if (cm.getChannelServer().getEvent() > 0) {
				cm.warp(cm.getChannelServer().getEvent(), cm.getChannelServer().getEvent() == 109080000 || cm.getChannelServer().getEvent() == 109080010 ? 0 : "join00");
				cm.dispose();
			} else {
				cm.sendOk("There is no #bevent#k going on.");
				cm.dispose();
			}
		}
	} 
}