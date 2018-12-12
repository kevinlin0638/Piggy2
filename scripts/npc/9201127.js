importPackage(Packages.tools.packet);

var status = 0;
var Smega = -1;

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
		cm.sendGetText("Enter the ID of the Notice:\r\n\r\n");
	} else if (status == 2) {
		var Notice = cm.getText(); // is this an int ?:|
		cm.getPlayer().getClient().announce(CField.multiChat(cm.getPlayer().getName(), "test", 0));
	}
}