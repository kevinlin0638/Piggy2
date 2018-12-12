//Script by Alcandon

var status;
var text = "Hey Quick Pick One Before I Shoot You!";

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else {
        cm.sendOk("#e#kOk, see you next time!");
        cm.dispose();
        return;
    }
        if (status == 12) {   
            cm.sendNext("I summon Bosses for #bAlcandonMS#l I summon 20 monsters at a time for free.");  
        }  
        else if (status == 0) {  
    cm.sendSimple("Hello, im the Boss Spawner of #rMaple Blade#k. But just know that it's not free, Needs a little fee for each Mob. Spawns two at a time Select your Food :)\r\n#L0#Headless Horseman #b[Bonus Exp 25%]#k #r(Required 200 Leaves and 1 Blue Wish Ticket)#k\r\n#L1#Capt. Latanica #b[Bonus Exp 15%]#k #r(Required 100 Leaves and 1 Blue Wish Ticket)#k\r\n#L2#Black Crow #b[Bonus Exp 30%]#k #r(Required 3 Reborns, 500 Leaves, 1 Yellow Wish Ticket, 1 White Scroll)#k\r\n#L3#Papulatus #b[Bonus Exp 40%]#k #r(Required 2 Reborns, 300 Leaves, 2 Green Wish Tickets, 1 Chaos Scroll)#k\r\n#L4#Pianus #b[Bonus Exp 45%]#k #r(Required: 4 Reborns, 450 Leaves, 1 Yellow Wish Ticket, 2 Chaos Scrolls)#k\r\n#L5#Zakum #b[Bonus Exp 65%]#k #r(Required: 5 Reborns, 700 Leaves, 1 Eye of Fire, 2 White Scrolls, 2 Yellow Wish Tickets)#k\r\n#L6#Big Foot #b[Bonus Exp 40%]#k #r(Required: 5 Reborns, 650 Leaves, 1 Yellow Wish Ticket, 1 Blue Wish Ticket, 1 Chaos Scroll, 1 Big Foot Toe#k\r\n#L7#Anego #b[Bonus Exp 20%]#k #r(Required: 10 Reborns, 550 Leaves, 3 Yellow Wish Tickets, 1 Lady Boss's Comb, 1 White Scroll");
        } 
        else if (status == 1) {
        
        if (selection == 0) {  
     if (cm.getPlayer().getMap().getMonsterCount() == 0 && cm.haveItem(4001126, 200) && cm.haveItem(4031545, 1)) {
            cm.summonMob(9400549, 3500000, 600000, 2);
			cm.gainItem(4001126, -200);
			cm.gainItem(4031545, -1);
            cm.dispose();
        } else {
      cm.sendOk("You don't have the Required items, or Monsters are already summoned in the Map, or you do not have enough Reborns.");
      cm.dispose();
}
}
        else if (selection == 1) {  
     if (cm.getPlayer().getMap().getMonsterCount() == 0 && cm.haveItem(4001126, 100) && cm.haveItem(4031545, 1)) {
            cm.summonMob(9420513, 2000000, 370000, 2);
			cm.gainItem(4001126, -100);
			cm.gainItem(4031545, -1);
            cm.dispose();  
        } else {
      cm.sendOk("You don't have the Required items, or Monsters are already summoned in the Map, or you do not have enough Reborns.");
      cm.dispose();
}
}
        else if (selection == 2) {
     if (cm.getPlayer().getMap().getMonsterCount() == 0 && cm.haveItem(4001126, 500) && cm.haveItem(4031543, 1) && cm.haveItem(2340000, 1) && cm.getPlayer().getReborns() > 2) {
            cm.summonMob(9400014, 35000000, 2980000, 2);
			cm.gainItem(4001126, -500);
			cm.gainItem(4031543, -1);
			cm.gainItem(2340000, -1);
            cm.dispose();  
        } else {
      cm.sendOk("You don't have the Required items, or Monsters are already summoned in the Map, or you do not have enough Reborns.");
      cm.dispose();
}
}
        else if (selection == 3) {
     if (cm.getPlayer().getMap().getMonsterCount() == 0 && cm.haveItem(4001126, 300) && cm.haveItem(4031544, 2) && cm.haveItem(2049100, 1) && cm.getPlayer().getReborns() > 1) {
            cm.summonMob(8500001, 23000000, 1210000, 2);
			cm.gainItem(4001126, -300);
			cm.gainItem(4031544, -2);
			cm.gainItem(2049100, -1);
            cm.dispose();  
        } else {
      cm.sendOk("You don't have the Required items, or Monsters are already summoned in the Map, or you do not have enough Reborns.");
      cm.dispose();
}
}
        else if (selection == 4) {
     if (cm.getPlayer().getMap().getMonsterCount() == 0 && cm.haveItem(4001126, 450) && cm.haveItem(4031543, 1) && cm.haveItem(2049100, 2) && cm.getPlayer().getReborns() > 3) {
            cm.summonMob(8510000, 30000000, 1800000, 2);
			cm.gainItem(4001126, -450);
			cm.gainItem(4031543, -1);
			cm.gainItem(2049100, -2);
            cm.dispose();  
        } else {
      cm.sendOk("You don't have the Required items, or Monsters are already summoned in the Map, or you do not have enough Reborns.");
      cm.dispose();
}
}
        else if (selection == 5) {
     if (cm.getPlayer().getMap().getMonsterCount() == 0 && cm.haveItem(4001126, 700) && cm.haveItem(4031543, 2) && cm.haveItem(4001017, 1) && cm.haveItem(2340000, 2) && cm.getPlayer().getReborns() > 4) {
            cm.summonMob(8800000, 66000000, 4000000, 1);
			cm.summonMob(8800003, 33000000, 2100000, 1);
			cm.summonMob(8800004, 33000000, 2100000, 1);
			cm.summonMob(8800005, 22000000, 1400000, 1);
			cm.summonMob(8800006, 30000000, 1100000, 1);
			cm.summonMob(8800007, 27500000, 1750000, 1);
			cm.summonMob(8800008, 30000000, 1750000, 1);
			cm.summonMob(8800009, 25300000, 1490400, 1);
			cm.summonMob(8800010, 25300000, 1490400, 1);
			cm.gainItem(4001126, -700);
			cm.gainItem(4031543, -2);
			cm.gainItem(4001017, -1);
			cm.gainItem(2340000, -2);
            cm.dispose();  
        } else {
      cm.sendOk("You don't have the Required items, or Monsters are already summoned in the Map, or you do not have enough Reborns.");
      cm.dispose();
}
}
        else if (selection == 6) {
     if (cm.getPlayer().getMap().getMonsterCount() == 0 && cm.haveItem(4001126, 650) && cm.haveItem(4031543, 1) && cm.haveItem(4031545, 1) && cm.haveItem(4032013, 1) && cm.haveItem(2049100, 1) && cm.getPlayer().getReborns() > 4) {
            cm.summonMob(9400575, 32000000, 4260000, 2);
			cm.gainItem(4001126, -650);
			cm.gainItem(4031543, -1);
			cm.gainItem(4031545, -1);
			cm.gainItem(4032013, -1);
			cm.gainItem(2049100, -1);
            cm.dispose();
        } else {
      cm.sendOk("You don't have the Required items, or Monsters are already summoned in the Map, or you do not have enough Reborns.");
      cm.dispose();
}
}
        else if (selection == 7) {
     if (cm.getPlayer().getMap().getMonsterCount() == 0 && cm.haveItem(4001126, 550) && cm.haveItem(4031543, 3) && cm.haveItem(2340000, 1) && cm.haveItem(4000138, 1) && cm.getPlayer().getReborns() > 9) {
            cm.summonMob(9400121, 75000000, 6900000, 2);
			cm.gainItem(4001126, -550);
			cm.gainItem(4031543, -3);
			cm.gainItem(2340000, -1);
			cm.gainItem(4000138, -1);
            cm.dispose();
        } else {
      cm.sendOk("You don't have the Required items, or Monsters are already summoned in the Map, or you do not have enough Reborns.");
      cm.dispose();
}
}
        else if (selection == 8) {
     if (cm.getPlayer().getMap().getMonsterCount() == 0) {
            cm.summonMob(9420513, 4000000, 210000, 10);
            cm.dispose();
        } else {
      cm.sendOk("You can't spawn monsters if there is already monsters on the map");
      cm.dispose();
}
}
        else if (selection == 9) {
     if (cm.getPlayer().getMap().getMonsterCount() == 0) {
            cm.summonMob(8510000, 55000000, 1000000, 10);
            cm.dispose();
        } else {
      cm.sendOk("You can't spawn monsters if there is already monsters on the map");
      cm.dispose();
}
}
        else if (selection == 10) {
     if (cm.getPlayer().getMap().getMonsterCount() == 0) {
            cm.summonMob(8520000, 50000000, 800000, 10);
            cm.dispose();
        } else {
      cm.sendOk("You can't spawn monsters if there is already monsters on the map");
      cm.dispose();
}
}
        else if (selection == 11) {
     if (cm.getPlayer().getMap().getMonsterCount() == 0) {
            cm.summonMob(8220003, 23400000, 80000, 10);
            cm.dispose();
        } else {
      cm.sendOk("You can't spawn monsters if there is already monsters on the map");
      cm.dispose();
}
}
        else if (selection == 12) {
     if (cm.getPlayer().getMap().getMonsterCount() == 0) {
            cm.summonMob(9500150, 6900, 1400, 20);
            cm.dispose();  
        } else {
      cm.sendOk("You can't spawn monsters if there is already monsters on the map");
      cm.dispose();
}
}
        else if (selection == 13) {
     if (cm.getPlayer().getMap().getMonsterCount() == 0) {
            cm.summonMob(9500139, 50000, 6000, 10);
            cm.dispose();  
        } else {
      cm.sendOk("You can't spawn monsters if there is already monsters on the map");
      cm.dispose();
}
}
        else if (selection == 14) {
     if (cm.getPlayer().getMap().getMonsterCount() == 0) {
            cm.summonMob(9500140, 100000, 10000, 10);
            cm.dispose();  
        } else {
      cm.sendOk("You can't spawn monsters if there is already monsters on the map");
      cm.dispose();
}
}
        else if (selection == 15) {
cm.killAllMonsters();
            cm.dispose();  
        } 

        else if (selection == 16) {
     if (cm.getPlayer().getMap().getPlayerCount() == 1) {
            cm.clearDrops();
            cm.dispose(); 
        } else {
      cm.sendOk("You can't use it while theres players!");
      cm.dispose();
}
}
        else if (selection == 17) {
     if (cm.getPlayer().getMap().getMonsterCount() == 0) {
            cm.summonMob(9400013, 45000, 1850, 20);
            cm.dispose();  
        } else {
      cm.sendOk("You can't spawn monsters if there is already monsters on the map");
      cm.dispose();
}
}
        else if (selection == 18) {
     if (cm.getPlayer().getMap().getMonsterCount() == 0) {
            cm.summonMob(100101, 100, 30, 15);
            cm.dispose();  
        } else {
      cm.sendOk("You can't spawn monsters if there is already monsters on the map");
      cm.dispose();
}
}
        else if (selection == 19) {
     if (cm.getPlayer().getMap().getMonsterCount() == 0) {
            cm.summonMob(9300106, 680000, 9000, 10);
            cm.dispose();  
        } else {
      cm.sendOk("You can't spawn monsters if there is already monsters on the map");
      cm.dispose();
}
}
        else if (selection == 20) {
     if (cm.getPlayer().getMap().getMonsterCount() == 0) {
            cm.summonMob(9400581, 45000, 3547, 20);
            cm.dispose();  
        } else {
      cm.sendOk("You can't spawn monsters if there is already monsters on the map");
      cm.dispose();
}
}
        else if (selection == 21) {
     if (cm.getPlayer().getMap().getMonsterCount() == 0) {
            cm.summonMob(9400112, 200000000, 11800000, 10);
            cm.dispose();  
        } else {
      cm.sendOk("You can't spawn monsters if there is already monsters on the map");
      cm.dispose();
}
}
        }  
    } 