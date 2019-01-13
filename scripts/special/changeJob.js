/*
 * Job Advancer
 * @author Aristocat
 */


var job = [
[[100, "戰士"], [200, "魔法師"], [300, "弓箭手"], [400, "盜賊"], [500, "海盜"]],
[[1100, "聖魂騎士"], [1200, "烈焰巫師"], [1300, "破風使者"], [1400, "暗夜行者"], [1500, "閃雷悍將"]],
[[3200, "煉獄巫師"], [3300, "狂暴獵人"], [3500, "機甲戰神"]],
[[110, "戰士"], [120, "騎士"], [130, "槍騎兵"]],
[[210, "巫師 ( 火/獨 )"], [220, "巫師 ( 冰/雷 )"], [230, "憎侶"]],
[[310, "獵人"], [320, "孥弓手"]],
[[410, "刺客"], [420, "俠盜"]],
[[510, "格鬥家"], [520, "槍手"]]];
var extrajobs = [
[2300, "精靈遊俠"], [3100, "惡魔殺手"]
];
var specialextrajobs = [
[9400, "影武者"],[9501, "重砲手"]
];
var extra = true;
var status = -1;
var select;
var tempest = false; //V123+
var jobindex;

function start() {
    jobindex = null;
    select = null;
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 || mode == -1 && status == 0) {
        cm.dispose();
        return;
    } else
        (mode == 1 ? status++ : status--);
    if (status == 0) {
        if (
            (cm.getPlayer().getLevel() >= 10 &&
                (cm.getPlayer().getJob() % 1000 == 0 || cm.getPlayer().getJob() == 501 || cm.getPlayer().getJob() == 3001 || cm.getPlayer().getJob() >= 2001 && cm.getPlayer().getJob() <= 2003)
                || cm.getPlayer().getLevel() >= 30 && (cm.getPlayer().getJob() % 1000 > 0 && cm.getPlayer().getJob() % 100 == 0
                    || cm.getPlayer().getJob() == 508)
                || cm.getPlayer().getLevel() >= (tempest ? 60 : 70) && cm.getPlayer().getJob() % 10 == 0 && cm.getPlayer().getJob() % 100 != 0
                || cm.getPlayer().getLevel() >= (tempest ? 100 : 120) && cm.getPlayer().getJob() % 10 == 1
                || cm.getPlayer().getLevel() >= 20 && cm.getPlayer().getJob() == 400 && cm.getPlayer().getSubcategory() == 1
                || cm.getPlayer().getLevel() >= 30 && cm.getPlayer().getJob() == 430 || cm.getPlayer().getLevel() >= (tempest ? 45 : 55) && cm.getPlayer().getJob() == 431 || cm.getPlayer().getLevel() >= (tempest ? 60 : 70) && cm.getPlayer().getJob() == 432 || cm.getPlayer().getLevel() >= (tempest ? 100 : 120) && cm.getPlayer().getJob() == 433)
            && (cm.getPlayer().getJob() % 10 != 2 && cm.getPlayer().getJob() % 10 != 4 || cm.getPlayer().getJob() == 432))
            cm.sendYesNo("你想要轉職嗎?");
        else {
            cm.sendOk("你已經有職業了.");
            cm.dispose();
        }
    } else if (status == 1) {
        if (cm.getPlayer().getSubcategory() == 1 && cm.getPlayer().getJob() == 0) { //Dual Blade
            cm.getPlayer().changeJob(400);
            cm.dispose();
            return;
        }
        if (cm.getPlayer().getSubcategory() == 1 && cm.getPlayer().getJob() == 400) { //Dual Blade
            cm.getPlayer().changeJob(430);
			cm.gainItem(5620000, 1);//
				cm.gainItem(5620001, 1);// Mastery Books before 4th job
				cm.gainItem(5620002, 1);//
				cm.gainItem(5620003, 1);//
            cm.dispose();
            return;
        }
        if (cm.getPlayer().getSubcategory() == 2 && cm.getPlayer().getJob() == 0) { //Cannoneer
            cm.getPlayer().changeJob(501);
            cm.dispose();
            return;
        }
        switch (cm.getPlayer().getJob()) {
            //Jobs with selections
            case 0: // Beginner
                jobSelection(0);
                break;
            case 1000: // Noblesse
                jobSelection(1);
                break;
            //Note: Heroes doesn't get job selection, the same goes about Nova.
            case 3000: // Citizen
                jobSelection(2);
                break;
            case 100: // Warrior
                jobSelection(3);
                break;
            case 200: // Magician
                jobSelection(4);
                break;
            case 300: // Bowman
                jobSelection(5);
                break;
            case 400: // Thief
                jobSelection(6);
                break;
            case 500: // Pirate
                jobSelection(7);
                break;
            //Special Jobs
            case 501: // Pirate(Cannoneer)
                cm.getPlayer().changeJob(530);
                cm.dispose();
                return;
            case 2000: // Legend(Aran)
                cm.getPlayer().changeJob(2100);
                cm.dispose();
                return;
            case 2001: // Farmer(Evan)
                cm.getPlayer().changeJob(2200);
                cm.dispose();
                return;
            case 2002: // Mercedes
                cm.getPlayer().changeJob(2300);
                cm.dispose();
                return;
            case 2004: // Luminous
                cm.getPlayer().changeJob(2700);
                cm.dispose();
                return;
            case 3001: // Demon Slayer
                cm.getPlayer().changeJob(3100);
                //cm.getPlayer().forceChangeChannel(cm.getPlayer().getClient().getChannel());
                cm.dispose();
                return;
            // Dual Blader
            case 430: // Blade Reqruit
            case 431: // Blade Acolyte
            case 432: // Blade Specialist
            case 433: // Blade Lord
                cm.getPlayer().changeJob(cm.getPlayer().getJob() + 1);
                //cm.getPlayer().forceChangeChannel(cm.getPlayer().getClient().getChannel());
                cm.dispose();
                return;
            //1st Job
            case 900:  // GM lol
            case 1100: // Dawn Warrior
            case 1200: // Blaze Wizard
            case 1300: // Wind Archer
            case 1400: // Night Walker
            case 1500: // Thunder Breaker
            case 2100: // Aran
            case 2300: // Mercedes
            case 2400: // Phantom
            case 3100: // Demon Slayer
            case 3200: // Battle Mage
            case 3300: // Wild Hunter
            case 3500: // Mechanic
            case 5100: // Mihile
            case 6100: // Kaiser
            case 6500: // Angelic Burster
                cm.getPlayer().changeJob(cm.getPlayer().getJob() + 10);
                //cm.getPlayer().forceChangeChannel(cm.getPlayer().getClient().getChannel());
                cm.dispose();
                return;
            //2nd Job
            case 110: // Fighter
            case 120: // Page
            case 130: // Spearman
            case 210: // Wizard(F/P)
            case 220: // Wizard(I/L)
            case 230: // Cleric
            case 310: // Hunter
            case 320: // Crossbow man
            case 410: // Assassin
            case 420: // Bandit
            case 510: // Brawler
            case 520: // Gunslinger
            case 530: // Cannoneer
            case 570: // Jett
            case 1110: // Dawn Warrior
            case 1210: // Blaze Wizard
            case 1310: // Wind Archer
            case 1410: // Night Walker
            case 1510: // Thunder Breaker
            case 2110: // Aran
            case 2310: // Mercedes
            case 2410: // Phantom
            case 2710: // Luminous
            case 3110: // Demon Slayer
            case 3210: // Battle Mage
            case 3310: // Wild Hunter
            case 3510: // Mechanic
            case 5110: // Mihile
            case 6110: // Kaiser
            case 6510: // Angelic Burster
            //3rd Job
            case 111: // Crusader
            case 121: // White Knight
            case 131: // Dragon Knight
            case 211: // Mage(F/P)
            case 221: // Mage(I/L)
            case 231: // Priest
            case 311: // Ranger
            case 321: // Sniper
            case 411: // Hermit
            case 421: // Chief Bandit
            case 511: // Marauder
            case 521: // Outlaw
            case 531: // Cannon Trooper
            case 571: // Jett
            case 1111: // Dawn Warrior
            case 1211: // Blaze Wizard
            case 1311: // Wind Archer
            case 1411: // Night Walker
            case 1511: // Thunder Breaker
            case 2111: // Aran
            case 2311: // Mercedes
            case 2411: // Phantom
            case 2711: // Luminous
            case 3111: // Demon Slayer
            case 3211: // Battle Mage
            case 3311: // Wild Hunter
            case 3511: // Mechanic
            case 5111: // Mihile
            case 6111: // Kaiser
            case 6511: // Angelic Burster
                cm.getPlayer().changeJob(cm.getPlayer().getJob() + 1);
                cm.dispose();
                return;
            default:
                cm.sendOk("此職業有錯誤.\r\n請回報給管理員.\r\n你的職業ID: " + cm.getPlayer().getJob() + "");
                cm.dispose();
                return;
        }
    } else if (status == 2) {
        select = selection;
		var text = "以下是您選擇的職業#b";
			text += "\r\n"+cm.getPlayer().getJobName(select)+"#l";
		cm.sendYesNo(text);
    } else if (status == 3) {
		getItem(select);
        if (select != 3100 && getSubcategory(select) == 0) {
            cm.getPlayer().changeJob(getRealJob(select));
            cm.dispose();
            return;
        } else 
            cm.sendSimple("As a Demon Slayer, you will have to choose a #bDemon Marking#k.\r\n#L1012276##i1012276##l\r\n#L1012277##i1012277##l\r\n#L1012278##i1012278##l\r\n#L1012279##i1012279##l\r\n#L1012280##i1012280##l");
        if (getSubcategory(select) != 0) {
            cm.getPlayer().changeJob(getRealJob(select));
            cm.getPlayer().setSubcategory(getSubcategory(select));
            cm.getPlayer().dropMessage(0, "You will change channel now so the special job change will effect you. No worries, you will land on the same channel you were in before.");
            cm.dispose();
            return;
        }
    } else if (status == 4) {
        cm.getPlayer().setDemonMarking(selection);
        cm.getPlayer().setSkinColor(4);
        cm.getPlayer().changeJob(getRealJob(select));
        if (select == 3100) {
            cm.sendOk("As a Demon Slayer, your Mana Points(MP) will turn into Demon Force (DF) as soon as you log off.");
        }
        cm.dispose();
    }
	cm.getPlayer().maxSkillsByJob();
}

function jobSelection(index) {
    jobindex = index;
    var choose = "請選擇你要的職業:"
    for (var i = 0; i < job[index].length; i++)
        choose += "\r\n#L" + job[index][i][0] + "#" + job[index][i][1] + "#l";
    if (extra == true && index <= 2/*Beginner Jobs Only*/) {
        choose += "\r\n\r\n#e#b目前可選職業#k#n: #e#r(New)#k#n";
        for (var e = 0; e < extrajobs.length; e++)
            choose += "\r\n#L" + extrajobs[e][0] + "#" + extrajobs[e][1] + "#l";
        for (var s = 0; s < specialextrajobs.length; s++)
            choose += "\r\n#L" + specialextrajobs[s][0] + "#" + specialextrajobs[s][1] + "#l";
    }
    cm.sendSimple(choose);
}


function getItem(jobid) {
	var i = [
	[[0],[1302000],[1362000],[1452000,1462000],[1332000,1472000],[1482000,1492000]],//冒險家
	[[0],[1302000],[1362000],[1452000,1462000],[1332000,1472000],[1482000,1492000]],//皇家騎士團
	[[0],[1442000],[1372000],[1522000,1352000],[0],[0]],//精靈遊俠
	[[0],[1322000],[1382000],[1462000],[0],[1492000]],//末日反抗軍
	[[0],[0],[0],[0],[1332000,1342000],[1532000]],//特殊職業
	]; 
	var s = parseInt(jobid/1000);//第幾列
	if (s == 9)
		s = 4;
	var b = parseInt(jobid/100)%10;//職業
	var z = i[s][b];
	for (var x in z) {
		if (z[x] == 0)
			continue;
		if (cm.haveItem(z[x]))
			continue;
		cm.gainItem(z[x], 1);
	}
}

function getSubcategory(special) {
    switch (special) {
        case 9400:
        case 430:
        case 431:
        case 432:
        case 433:
        case 434:
            return 1;
        case 9501:
            return 2;
        case 9508:
            return 10;
    }
    return 0;
}

function getRealJob(fakejob) {
    switch (fakejob) {
        case 9400:
            return 400;
        case 9501:
            return 501;
        case 9508:
            return 508;
    }
    return fakejob;
}