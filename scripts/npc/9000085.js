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
	   cm.sendSimple("What would you like to do?\r\n\r\n#L1#Check the #rdrops#k of any monster in the map");
	} else if (status == 1) {
	if (cm.getMap().getAllMonstersThreadsafe().size() <= 0) {
	    cm.sendOk("There are no monsters in this map.");
	    cm.dispose();
	    return;
	}
	var selStr = "Select which monster you wish to check.\r\n\r\n#b";
	var iz = cm.getMap().getAllUniqueMonsters().iterator();
	while (iz.hasNext()) {
	    var zz = iz.next();
	    selStr += "#L" + zz + "##o" + zz + "##l\r\n";
	} 
			cm.sendSimple(selStr);
    } else if (status == 2) {
		cm.sendNext(cm.checkDrop(selection));
		cm.dispose();
	}
}