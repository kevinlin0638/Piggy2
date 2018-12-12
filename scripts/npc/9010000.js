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
	   cm.sendSimple("What would you like to do?\r\n\r\n#L1#Change my job");
	} else if (status == 1) {
	   if (selection == 1) { 
		  cm.dispose();
	      cm.openNpc(9900002);
	   } 
	}
}


/**
@author Alejandro made this npc weird and y u not just status switch
@npc Maple Administrator
@function Starter NPC

var text = "What Job do you want to become?\r\n #L100#Warrior#l \r\n #L200#Magician#l \r\n #L300#Bowman#l \r\n #L400#Thief#l \r\n #L430#Dual Blade#l \r\n #L500#Pirate#l \r\n #L501#Cannoneer#l \r\n #L508#Jett#l \r\n #L1100#Dawn Warrior#l \r\n #L1200#Blaze Wizard#l \r\n #L1300#Wind Archer#l \r\n #L1400#Night Walker#l \r\n #L1500#Thunder Breaker#l \r\n #L2100#Aran#l \r\n #L2200#Evan#l \r\n #L2300#Mercedes#l \r\n #L2400#Phantom#l \r\n #L3100#Demon Slayer#l \r\n #L3200#Battle Mage#l \r\n #L3300#Wild Hunter#l \r\n #L3500#Mechanic#l \r\n #L5100#Mihile#l";

function start() {
	status = -1;
	action(1, 0, 0);
}

function action (m, t, s) {
	(m == 1 ? status++ : status--);
	switch (status) {
		case 0:
		  if (cm.getPlayer().getJob() == 0 || cm.getPlayer().getJob() == 3000 || cm.getPlayer().getJob() == 2000 || cm.getPlayer().getJob() == 1000) {
			cm.sendSimple(text);
	          } else {
		   cm.sendOk("you are not a beginner");
		   cm.dispose();
			}
			break;
		case 1:
			if (s == 3300) {
			}
			cm.getPlayer().changeJob(s);
			cm.getPlayer().setLevel(14);
			cm.getPlayer().levelUp();
			//cm.getPlayer().getStat().setStr(4, cm.getPlayer());
			//cm.getPlayer().getStat().setDex(4, cm.getPlayer());
			//cm.getPlayer().getStat().setInt(4, cm.getPlayer());
			//cm.getPlayer().getStat().setLuk(4, cm.getPlayer());
			//cm.getPlayer().setRemainingAp(50);
			//cm.warp(100000000, 0);
			cm.dispose();
			break;
		default:
			cm.dispose();
			break;
	}
}*/