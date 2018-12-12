/**
 * @author - Eric
 * @rev: 4.0 - Removal of this NPC, we now directly join via source command.
 * @desc: This will continue to remain incase I feel to revert. /Eric
 * @func: AutoJQ Join System, Re-coded from memory.
*/

 function start() {
     if (!(cm.getEventMap() == 0)) { 
        cm.sendYesNo("Auto Jump Quest System System - #gOnline.#k\r\n\r\nWant to do a JumpQuest?\r\n#bWinner#k recieves #r150#k Troll Coins!");
    } else {
        cm.sendOk("Auto Jump Quest System System - #rOffline.#k\r\n\r\n#ePlease try again later..#n");
		cm.dispose();
    }
}

 function action(mode, type, selection) {
    if (mode < 1) { 
        cm.sendOk("What the fuck? Don't want #r150#k Wiz Coins?\r\nThat prize is beast, niqqa!");
        cm.dispose();
        return;
    } else {
	  if (cm.getPlayer().isGM()) {
        cm.warp(cm.getEventMap());
      } else {
	    cm.sendOk("Just type @join, rather than trying to #rexploit#k direct warp."); // we don't like exploiters! :(
	  }
		cm.dispose();
    }
}