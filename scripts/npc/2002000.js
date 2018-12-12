importPackage(net.sf.odinms.client);

var status = 0;
//var items = Array(1902011, 1902013, 1902014, 1902020, 1902021, 1902022, 1902023, 1902024, 1902025, 1902026, 1902027, 1902028, 1902031, 1902032, 1902033, 1902034, 1902035, 1902036, 1902037, 1902038, 1902036, 1902045, 1902059, 1902060, 1902061, 1912007, 1912009, 1912010, 1912013, 1912014, 1912015, 1912016, 1912017, 1912018, 1912019, 1912020, 1912021, 1912024, 1912025, 1902026, 1912027, 1912028, 1912029, 1912030, 1912031, 1912032, 1912038, 1912026, 1912052, 1912053, 1912054); 
var items = Array(1902000, 1902001, 1902002, 1912000);

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) { // this npc needs help. We also need to give mount + saddle not just mount f3
            cm.sendSimple("Hi Im #eWizStory's#n #bS#k#rp#k#ge#k#bc#k#ri#k#ga#k#bl#k Mount Shop!\r\nDo want to buy a #bmount#k?\r\n#b#L0#Yes please!#l#k");
        } else if (status == 1) {
            if (selection == 0) {
                cm.sendNext("Great! Remember that they all cost #b500 Wiz Coins#k each.");
            }
        } else if (status == 2) {
                var selStr = "Which #bMount or Saddle#k to you want to buy?";
                for (var i = 0; i < items.length; i++){
                    selStr += "\r\n#b#L" + i + "# #v" + items[i] + "# #t" + items[i] + "##l#k";
                }
                cm.sendSimple(selStr);
        } else if (status == 3) {
                if (!cm.haveItem(4007099, 500)) {
                    cm.sendOk("You do not have enough Wiz Coins.");
                    cm.dispose();
                } else {
                    cm.gainItem(4007099, -500);
                    cm.gainItem(items[selection], 1);
                    cm.dispose();
				}
            }
    }
}