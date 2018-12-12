/**
 * @author: Eric Of EricMS Reborn
 * @func: (Advanced) Monster Rush System 2.0
 * @rev: 7.4 - Additional EIM Checking upon startup, reset and disposal.
 * @npc: Mia
 * @notes: Need to update the text, i couldn't think of anything. 
 *
*/
importPackage(Packages.client);
var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1)
        cm.dispose();
    else {
        if (mode == 0 && status == 0)
            cm.dispose();
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
               if ((cm.getMMesos() <= 1) || (cm.getMMesos() == 0)) {
                    var em = cm.getEventManager("MonsterRush");
                     if (em == null) {
					    status = 999;
                        cm.sendOk("EventManager #rMonsterRush#k is #enull#n");
                        cm.dispose();
                    } else {
                        em.getIv().invokeFunction("setup", null);
						status = 999;
                        cm.sendOk("Looks like you're ready to fight!");
						cm.dispose();
						
                }    
                 cm.resetMonsterRush();
        } else {
		cm.sendYesNo("Hey. Would you people like to start #rMonster Rush#k?\r\nYou guys need to donate " + cm.getDigits() + " more mesos#k#n before I can start the epic event.\r\nWant me to explain?\r\n#rMonster Rush#k is an event which you people can start anytime.\r\nAfter donating the amount, the event will start in 5 minutes.\r\nMonsters will then spawn in #r10#k towns.\r\nThey won't be easy to take down though.. So #bparty#k with your friends!\r\n#r#eThey are summoned at these towns:#k\r\nHenesys\r\nAriant\r\nEl Nath\r\nNew Leaf City\r\nNautilus\r\nKerning\r\nAmoria\r\nLudibrium\r\nOrbis\r\nLeafre\r\n\r\n#bWanna take them on?#k#n");
        }
        } else if (status == 1) {
            cm.sendSimple("How much mesos would you like to donate?\r\n\r\n#L0#50Mil Mesos\r\n#L1#100Mil Mesos\r\n#L2#200Mil Mesos\r\n#L3#400Mil Mesos\r\n#L4#800Mil Mesos\r\n#L5#1.5Bil Mesos\r\n#L6#69 Mesos\r\n#L7#All of my smexy mesos\r\n#L8#Im poor.");
        } else if (status == 2) {
           if (selection == 0) {
	      if (cm.getPlayer().getMeso() >= 50000000) {
              cm.sendOk("Haha, #rThank you!#k With your donations, the event will be up soon!");
			  cm.deduct50M();
			  cm.gainMeso(-50000000);
	          cm.dispose();
            } else {
              cm.sendOk("You don't even have #r50mil#k.");
			  cm.dispose();
	    }
          } else if (selection == 1) {
	      if (cm.getPlayer().getMeso() >= 100000000) {
              cm.sendOk("#rThank you#k for your donation, it's a great help!");
			  cm.deduct100M();
			  cm.gainMeso(-100000000);
              cm.dispose();
            } else {
              cm.sendOk("You don't even have #r100mil#k.");
			  cm.dispose();
	    }
          } else if (selection == 2) {
	      if (cm.getPlayer().getMeso() >= 200000000) {
              cm.sendOk("#rThank you#k for so much mesos!\r\n*cough* I might use some for personal use!");
			  cm.deduct200M();
			  cm.gainMeso(-200000000);
              cm.dispose();
            } else {
              cm.sendOk("You don't even have #r200mil#k.");
			  cm.dispose();
	    }
          } else if (selection == 3) {
	      if (cm.getPlayer().getMeso() >= 400000000) {
              cm.sendOk("It may not be enough as 1.5b, but it will still help! #rThank you!#k");
			  cm.deduct400M();
		      cm.gainMeso(-400000000);
              cm.dispose();
            } else {
              cm.sendOk("You don't even have #r400mil#k.");
			  cm.dispose();
	    }
          } else if (selection == 4) {
	      if (cm.getPlayer().getMeso() >= 800000000) {
              cm.sendOk("The lucky number 8! ;)\r\n#rThank you#k for your kind donation!");
			  cm.deduct800M();
	          cm.gainMeso(-800000000);
              cm.dispose();
            } else {
              cm.sendOk("You don't even have #r800mil#k.");
			  cm.dispose();
	    }
          } else if (selection == 5) {
	      if (cm.getPlayer().getMeso() >= 1500000000) {
              cm.sendOk("Wow, you're fucking rich!\r\n#rThank you#k for your kind donation!");
			  cm.deduct1_5B();
			  cm.gainMeso(-1500000000);
              cm.dispose();
            } else {
              cm.sendOk("You don't even have #r1.5bil#k.");
			  cm.dispose();
	    }
          } else if (selection == 6) {
	      if (cm.getPlayer().getMeso() >= 69) {
              cm.sendOk("We can three way 69! No homo.");
			  cm.deduct69();
              cm.gainMeso(-69);
              cm.dispose();
            } else {
              cm.sendOk("You don't even have #r69 mesos#k. What the fuck?");
			  cm.dispose();
	    }
          } else if (selection == 7) {
	      if (cm.getPlayer().getMeso() >= 1) {
              cm.sendOk("Thank you for your #bgenerous support#k!");
			  cm.deductAll();
			  var pget = cm.getPlayer().getMeso();
              cm.gainMeso(-pget);
              cm.dispose();
            } else {
              cm.sendOk("You don't even have #r1#k god damn #bmeso#k go fuck yourself!");
			  cm.dispose();
	    }
          } else if (selection == 8) {
              cm.sendOk("Laooo y so nibzy?! zzz");
			  cm.getPlayer().setHp(0);
              cm.getPlayer().updateSingleStat(MapleStat.HP, 0);
              cm.dispose();
          } 
       }
    }
}