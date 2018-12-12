importPackage(java.lang);
importPackage(java.util);
importPackage(Packages.tools);
importPackage(Packages.server.quest);
importPackage(Packages.client);
importPackage(Packages.scripting);
importPackage(Packages.handling.channel);
importPackage(Packages.handling);
importPackage(Packages.handling.world);
var status = -1;
var partymembers;

function start() {
	partymembers = cm.getPartyMembers();
    status = -1;
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1 || mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    }
    if (status == 0) {
        var date = Calendar.getInstance().get(Calendar.YEAR)%100+"/"+StringUtil.getLeftPaddedStr(Calendar.getInstance().get(Calendar.MONTH)+"", "0", 2)+"/"+Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        if (cm.getPlayer().getKeyValue("AswanOffSeason_LastDate") == null) {
            cm.getPlayer().setKeyValue("AswanOffSeason_LastDate", date);
        }
        if (cm.getQuestStatus(7963) == 0 || !cm.getPlayer().getKeyValue("AswanOffSeason_LastDate").equals(date)) {
            cm.forceStartQuest(7963, "0");
            cm.getPlayer().setKeyValue("AswanOffSeason_LastDate", date);
        }
         cm.sendSimple("#e<Fight for Azwan> #n\r\n\r\nJoin in the Fight for Azwan? #e(Can enter 10 times a day)#n#b\n\r\n#L0#Take down Hilla. (Lv. 120 and above)#l\r\n#L1#Enter the Fight for Azwan (Remaining entries: #r" + (10-Integer.parseInt(cm.getQuestCustomData(7963))) + "#k)");
    } else if (status == 1) {
        if (selection == 1) {
			if (cm.getMap(955000100).getCharactersSize() >= 1 || cm.getMap(955000200).getCharactersSize() >= 1 || cm.getMap(955000300).getCharactersSize() >= 1) {
				cm.sendNext("Another party has already entered the #rFight for Azwan#k.\r\nPlease try another channel, or wait for the current party to finish.");
				cm.dispose();
				return;
            }
            if (Integer.parseInt(cm.getQuestCustomData(7963)) == 10) {
                cm.sendOk("You've already done this #e10 times#n today.\r\nPlease, come back tomorrow to continue!");
                cm.dispose();
                return;
            }
            if (!checkLevel(cm.getPlayer().getLevel(), 40, 255)) {
                cm.sendOk("You may not enter because you lack being level 40+.");
                cm.dispose();
                return;
            }
                var em = cm.getEventManager("AswanOffSeason");
				var prop = em.getProperty("state");
					if (prop.equals("0") || prop == null) {
						em.startInstance(cm.getParty(), cm.getMap(), 120);
						cm.prepareAswanMob(955000100, em);
						cm.forceStartQuest(7963, (Integer.parseInt(cm.getQuestCustomData(7963))+1) + "");
						cm.dispose();
						return;
					} else {
						cm.sendSimple("Another party has already entered the #rFight for Azwan#k.\r\nPlease try another channel, or wait for the current party to finish.");
					}
            } else if (selection == 0) {
				if (cm.getPlayer().getParty() == null) {
					cm.sendOk("You must be in a party to continue.");
					cm.dispose();
					return;
				} else {
					cm.warp(262030000, 0);
					cm.dispose();
				}
			}
        } else {
            cm.dispose();
        }
    }

function checkLevel(cur, min, max) {
    return (cur >= min && cur <= max);
}