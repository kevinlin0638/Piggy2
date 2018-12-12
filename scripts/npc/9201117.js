var status = -1;
var maps;
var pqMaps;
var selectedMap = -1;
var selectedArea = -1;

function start() {
    action(1, 0, 0);
		maps = Array(910001000, 680000000, 230000000, 260000000, 101000000, 211000000, 120030000, 130000200, 100000000, 103000000, 222000000, 240000000, 104000000, 220000000, 802000101, 120000000, 221000000, 200000000, 102000000, 300000000, 801000000, 540000000, 541000000, 250000000, 251000000, 551000000, 550000000, 800040000, 261000000, 541020000, 270000000, 682000000, 140000000, 970010000, 103040000, 555000000, 310000000, 200100000, 211060000, 310040300, 219000000, 960000000, 220080000); 
		pqMaps = Array(682010200, 541000300, 220050300, 229000020, 230040200, 541010010, 551030100, 240040500, 800020110, 801040004, 105030500, 610020004, 102040200, 105100100, 211041100, 610030010, 670010000, 674030100, 310040200, 219010000, 219020000);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status >= 2 || status == 0) {
            cm.dispose();
            return;
        }
        status--;
    }
    if (status == 0) {
        cm.sendSimple("Hello #g#h ##k!. #r\r\n#L1#Job advance#l \r\n");
    } else if (status == 1) {
        if (selection == 1) {
        if (cm.getChar().getJob() == 0 || cm.getChar().getJob() == 100 || cm.getChar().getJob() == 200 || cm.getChar().getJob() == 300 || cm.getChar().getJob() == 400 || cm.getChar().getJob() == 500 || cm.getChar().getJob() == 1000 || cm.getChar().getJob() == 2000 || cm.getChar().getJob() == 2001 || cm.getChar().getJob() == 3000) {
        cm.sendSimple("What would you like to become? \r\n#b#L128#Mihile#l\r\n#b#L100#Fighter#l\r\n#b#L101#Page#l\r\n#b#L102#Spearman#l\r\n#b#L103#F/P Wizard#l\r\n#b#L104#I/L Wizard#l\r\n#b#L105#Cleric#l\r\n#b#L106#Bowman#l\r\n#b#L107#Crossbowman#l\r\n#b#L108#Assassin#l\r\n#b#L109#Bandit#l\r\n#b#L110#Blade Recruit#l\r\n#b#L111#Brawler#l\r\n#b#L112#Gunslinger#l\r\n#b#L113#Dawn Warrior#l\r\n#b#L114#Blaze Wizard#l\r\n#b#L115#Wind Archer#l\r\n#b#L116#Night Walker#l\r\n#b#L117#Thunder Breaker#l\r\n#b#L118#Aran#l\r\n#b#L119#Evan#l\r\n#b#L120#Battle Mage#l\r\n#b#L121#Wild Hunter#l\r\n#b#L122#Mechanic#l \r\n#b#L123#Demon Slayer#l \r\n#b#L124#Mercedes#l \r\n#b#L125#Cannon Master#l \r\n#b#L126#Phantom#l \r\n#b#L127#Jett#l");
    } else {
   cm.sendOk("Please talk to me again once you have a beginner job!");
}
        } else if (selection == 3) {
            cm.sendSimple("#b#L0#Town maps#l\r\n#L1#Monster maps and PQ Maps(Meant for level 50+) #l\r\n#L2#Dimensional Mirror#l\r\n#L3#Internet Cafe#l#k");

        
        } else if (selection == 11) {
                cm.dispose();
                cm.openShop(61);

       }
     } else if (status == 2) {
            
}
       if (selection == 100) {
          cm.changeJob(110);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 101) {
          cm.changeJob(120); 
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 102) {
          cm.changeJob(130);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 103) {
          cm.changeJob(210);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 104) {
          cm.changeJob(220);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 105) {
          cm.changeJob(230);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 106) {
          cm.changeJob(310);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 107) {
          cm.changeJob(320);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 108) {
          cm.changeJob(410);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 109) {
          cm.changeJob(420);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 110) {
          cm.changeJob(430);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 111) {
          cm.changeJob(510);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 112) {
          cm.changeJob(520);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 113) {
          cm.changeJob(1100);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 114) {
          cm.changeJob(1200);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 115) {
          cm.changeJob(1300);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 116) {
          cm.changeJob(1400);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 117) {
          cm.changeJob(1500);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 118) {
          cm.changeJob(2000); 
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 119) {
          cm.changeJob(2001);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 120) {
          cm.changeJob(3200);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 121) {
          cm.changeJob(3300);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 122) {
          cm.changeJob(3500);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 123) {
          cm.changeJob(3100);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 124) {
          cm.changeJob(2300);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 125) {
          cm.changeJob(530);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 126) {
          cm.changeJob(2412);
          cm.getChar().autoAdvance();
	      cm.dispose();
} else if (selection == 127) {
          cm.changeJob(572);
          cm.getChar().autoAdvance();
	   cm.dispose();
} else if (selection == 128) {
          cm.changeJob(5112);
          cm.getChar().autoAdvance();
	      cm.dispose();	   
} else if (selection == 1001) {
	   cm.gainItem(3992017, 20);
	   cm.gainItem(3992018, 20);
          cm.gainItem(3992019, 20);
	   cm.dispose();
} else if (selection == 1002) {
	   cm.gainItem(4007007, 20);
	   cm.gainItem(4007006, 20);
          cm.gainItem(4007005, 20);
          cm.gainItem(4007004, 20);
          cm.gainItem(4007003, 20);
          cm.gainItem(4007002, 20);
          cm.gainItem(4007001, 20);
          cm.gainItem(4007000, 20);
	   cm.dispose();
} else if (selection == 1003) {
	   cm.gainItem(4011004, 20);
	   cm.gainItem(4011006, 20);
          cm.gainItem(4021007, 20);
          cm.gainItem(4011007, 20);
          cm.gainItem(4021009, 20);
	   cm.dispose();
} else if (selection == 1004) {
	   cm.gainItem(2003516, 2);
	   cm.gainItem(2003517, 2);
          cm.gainItem(2003518, 2);
          cm.gainItem(2003519, 2);
          cm.gainItem(2003520, 2);
	   cm.dispose();
}
}
