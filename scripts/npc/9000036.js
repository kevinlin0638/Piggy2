/**
 *
 * @author: Eric
 * @func: 2nd Job Advancement
 * @rev: 2 - Added Job Selection and added Beginner
 *
*/

var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
	  if (cm.getPlayer().getJob() == 100 || cm.getPlayer().getJob() == 200 || cm.getPlayer().getJob() == 300 || cm.getPlayer().getJob() == 400 || cm.getPlayer().getJob() == 500) {
		cm.sendNext("Well, shit. You want to job advance.\r\n\r\n");
	  } else if (cm.getPlayer().getJob() == 0 || cm.getPlayer().getJob() == 1000 || cm.getPlayer().getJob () == 2000 || cm.getPlayer().getJob() == 3000) {
	    status = 3; // ++ing here
		cm.sendNext("Well, shit. You want a job."); 
	  } else {
		cm.sendOk("You are #eNOT#n in need of a #e2nd Job Advance#n.");
		cm.dispose();
	  }
	  } else if (status == 1) {
	     if (cm.getPlayer().getJob() == 100) {
	     cm.sendSimple("Cool! A true #bWarrior#k! Choose a job:\r\n#L120#Page\r\n#L130#Spearman\r\n#L110#Fighter");
		} else if (cm.getPlayer().getJob() == 200) {
	     cm.sendSimple("Woah! A #bMagician!#k Can you do a magic trick?\r\nHaha, What job do you want to become? :\r\n#L220#Ice / Lightning\r\n#L210#Fire / Poison\r\n#L230# Cleric");
		 } else if (cm.getPlayer().getJob() == 300) {
	     cm.sendSimple("Damn! A #bBowman!#k Can you hit a bullseye every time?\r\nWell, if not maybe upgrading your job will help :\r\n#L310#Bowman\r\n#L320#Crossbowman");
		 } else if (cm.getPlayer().getJob() == 400) {
	     cm.sendSimple("Uh-oh! A #bThief!#k Don't rob me plis!11!\r\nHere, take your job! :\r\n#L410#Assassin\r\n#L420#Bandit");
		 } else if (cm.getPlayer().getJob() == 500) {
	     cm.sendSimple("Don't shoot, all-mighty #bPirate#k!\r\nJust take your job! :\r\n#L510#Brawler\r\n#L520#Gunsligner");
		} else {
		 cm.sendOk("How'd you get here in the first place?");
		 cm.dispose();
	}
    } else if (status == 2) {
	  status = 999;
	  cm.getPlayer().changeJob(selection);
	  cm.dispose();
	  // job selection (not advance) 
	} else if (status == 4) {
	  var joblist = "What Job do you want to become?\r\n #L0#Beginner#l \r\n #L100#Warrior#l \r\n #L200#Magician#l \r\n #L300#Bowman#l \r\n #L400#Thief#l \r\n #L430#Dual Blade#l \r\n #L500#Pirate#l \r\n #L501#Cannoneer#l \r\n #L508#Jett#l \r\n #L1100#Dawn Warrior#l \r\n #L1200#Blaze Wizard#l \r\n #L1300#Wind Archer#l \r\n #L1400#Night Walker#l \r\n #L1500#Thunder Breaker#l \r\n #L2100#Aran#l \r\n #L2200#Evan#l \r\n #L2300#Mercedes#l \r\n #L2400#Phantom#l \r\n #L3100#Demon Slayer#l \r\n #L3200#Battle Mage#l \r\n #L3300#Wild Hunter#l \r\n #L3500#Mechanic#l \r\n #L5100#Mihile#l";
	  cm.sendSimple(joblist);
	} else if (status == 5) {
	   cm.getPlayer().changeJob(selection);
	   //for (var i = 0; i < 14; i++)
	   //cm.getPlayer().levelUp(); // for ap due to force setting, should we forloop? 
	   cm.dispose();
    }
}