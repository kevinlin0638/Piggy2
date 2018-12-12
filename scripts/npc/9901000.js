var items = [1522000, 1352000, 1362000, 1352100, 1532000];
var itemNames = ["Dual Bowgun", "Magic Arrows", "Cane", "Carte Magique", "Cannon"];

function start() {
	var text = "Here's a list of commonly wanted items from players!\r\nEach item costs #e100,000 mesos#n.\r\n";
	for (var i = 0; i < items.length; text += "\r\n #L" + i + "# #i" + items[i] + "# - " + itemNames[i] + "#l", i++); 
	 cm.sendSimple(text);
}

function action(m, t, s) {
	if (m > 0) { 
            if (cm.canHold(items[s]) && cm.getMeso() >= 100000) { 
                cm.gainItem(items[s], 1); 
				cm.gainMeso(-100000);
		        cm.sendOk("Here is your #i" + items[s] + "##b" + itemNames[s] + "#k.");
            } else {
			  if (cm.getMeso() != 100000)
                cm.sendOk("You don't have #e100,000 mesos#n.");
					else
			    cm.sendOk("Your inventory is full."); 
			}
    } 
    cm.dispose(); 
}