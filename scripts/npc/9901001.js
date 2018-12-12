/**
 * @author: Eric
 * @func: Equipment Upgrading System
 * @npc: Eric
 * @rev: 1.5
*/

var status = 0;
var slot;
var equipLevel;
var equipExp;
var equipExpNeeded;
var equipId;
var equipMSIUpgrades;
var equipStatIncs;
var player = null;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 2 && status == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            player = cm.getCharByName(cm.getPlayer().getName());
            cm.sendSimple("#r-- #eE#nquipment #eU#npgrade #eS#nystem -- #k \r\n\r\n** Choose a piece of #eEquipment#n to view your information about it!\r\n#L0##bView Equipment#k#l\r\n\r\n\r\n** Want to learn more information about the #eEUS#n?\r\n#L1##bMore Information#k#l\r\n\r\n\r\n** Ready to #eupgrade#n? Let's talk.\r\n#L2##bUpgrade Equipment#k#l");
        } else if (status == 1) {
            Loader = "Okay, cool. Some #e#dEquipment#k#n has been upgraded by the #e#rEUS#k#n.\r\nHere is your inventory, select an item to learn of its #e#rEUS Stats#k#n.\r\n";
            if (selection == 0) {
                cm.sendSimple(Loader + cm.getPlayer().loadEquipment(player.getClient()));
            } else if (selection == 1) {
				cm.sendOk("#d-- #eE#nquipment #eU#npgrade #eS#nystem #eF#n.#eA#n.#eQ#n. -- #k \r\n\r\n#rWhat does the EUS do?#k\r\n#bThe EUS stands for #eE#nquipment #eU#npgrade #eS#nystem.\r\nIt simply upgrades your Equipments stats to become very strong and powerful, similar to the ways of an MSI (Max Stat Item).#k\r\n\r\n#rWhat does 'Available Stat Upgrades' mean?#k\r\n#bAvailable Stat Upgrades are the amount of stats you can increase every upgrade.\r\nWhen you go to upgrade an item, you can increase your STR/DEX/INT/LUK +(The amount of your Available Stat Upgrade).\r\nYour Stat Upgrade increases depending on your Level and EXP of your Equipment. You can't Stat Upgrade after your Equipments stats have reached 30,000. This is because 30,000 stats is the maximum per item (not per stat, but total).#k\r\n\r\n#rWhat does Equipment Level/Exp have to do with upgrading items?#k\r\n#bUpon gaining Equipment Exp, you gain Equipment Levels.\r\nAfter you've gained Equipment Levels, you unlock new features.\r\nFor starters, like stated above, you get upgraded 'Available Stats' every time you level. Once you've reached Equipment Level 10 (max level) you unlock Potential Upgrades. (Not available yet)#k\r\n\r\n#rWhat happens once you've achieved Level 10/Max Stats?#k\r\n#bAh.. you're a smart one, aren't you?! This feature isn't available yet.#k");
				cm.dispose();
			} else if (selection == 2) { 
				if (cm.getPlayer().getAPS() >= 100 // 100 is our minimum.. for now
				&& cm.getPlayer().getStat().getStr() >= 32765 
				&& cm.getPlayer().getStat().getDex() >= 32765 
				&& cm.getPlayer().getStat().getInt() >= 32765 
				&& cm.getPlayer().getStat().getLuk() >= 32765) {
					cm.sendOk("#r-- #eE#nquipment #eU#npgrade #eS#nystem -- #k \r\n\r\nComing Soon to #eWizStory#n!");
					cm.dispose();
				} else {
					cm.sendOk("#d-- #eE#nquipment #eU#npgrade #eS#nystem #r#eRequirements#n#k: -- #k \r\n\r\n" +
					"* #b32767 STR#k (" + (cm.getPlayer().getStat().getStr() >= 32765 ? "#gComplete#k" : "#rIncomplete#k") + ")\r\n" + 
					"* #b32767 DEX#k (" + (cm.getPlayer().getStat().getDex() >= 32765 ? "#gComplete#k" : "#rIncomplete#k") + ")\r\n" + 
					"* #b32767 INT#k (" + (cm.getPlayer().getStat().getInt() >= 32765 ? "#gComplete#k" : "#rIncomplete#k") + ")\r\n" + 
					"* #b32767 LUK#k (" + (cm.getPlayer().getStat().getLuk() >= 32765 ? "#gComplete#k" : "#rIncomplete#k") + ")\r\n" + 
					"* #bAt least 100 AP in AP Storage#k (" + (cm.getPlayer().getAPS() >= 100 ? "#gComplete#k" : "#rIncomplete#k") + ")\r\n" + 
					"* #b300 or more Rebirths#k (" + (cm.getPlayer().getReborns() >= 300 ? "#gComplete#k" : "#rIncomplete#k") + ")");
					cm.dispose();
				}
			}
        } else if (status == 2) {
            slot = selection;
			equipLevel = player.getEquipLevel(selection);
			equipExp = player.getEquipExp(selection);
			equipExpNeeded = player.getEquipExpNeeded(selection);
            equipId = player.getEquipId(selection);
			equipMSIUpgrades = player.getEquipMSIUpgrades(selection); 
			equipStatIncs = player.getEquipStatInc(equipLevel, equipExp);
            cm.sendOk("#e -- Information on Equipment #v" + equipId + "# -- #n \r\n#bEquipment Level#k : #r" + equipLevel + "#k\r\n#bEquipment Exp#k : #r" + equipExp + "/" + equipExpNeeded + "#k\r\n#bEquipment MSI Upgrades#k : #r" + equipMSIUpgrades + "#k\r\n\r\n-- #eAvailable Stat Upgrades#n --#n \r\n#bStrength Increase#k : #r+" + equipStatIncs + "#k\r\n#bDexterity Increase#k : #r+" + equipStatIncs + "#k\r\n#bIntelligence Increase#k : #r+" + equipStatIncs + "#k\r\n#bLuck Increase#k : #r+" + equipStatIncs + "#k");
			cm.dispose();
        }
    }
}  