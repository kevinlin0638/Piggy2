/* ===========================================================
			Resonance
	NPC Name: 		Maple Administrator
	Map(s): 		All Towns
	Description: 	Quest -  The Return of Aran
=============================================================
Version 1.0 - Script Done.(16/5/2010)
=============================================================
*/

importPackage(Packages.client);

var status = -1;

function start(mode, type, selection) {
    status++;
	if (mode != 1) {
	    if(type == 1 && mode == 0)
		    status -= 2;
		else{
			qm.sendNext("Oh, was it confusing? Then I'll explain it to you again.");
		    qm.dispose();
			return;
		}
	}
	if (status == 0)
		qm.sendNext("Hi there, aaroncsn1! You may have heard by now that Aran has returned to the world of Maple, right? To celebrate his triumphant return,I,the Maple Administrator, and Lirin, the girl who waited patiently for Aran have a special present for you.");
	if (status == 1)
		qm.sendNextPrev("#v1112405# AllStat + 3 Weapon ATT +3 Magic ATT +3 HP +30 MP +30 \r\nOnce you reach Level 50 as Aran. Lirin will give you a ring that she treasures.");
	if (status == 2)
		qm.sendNextPrev("The item is normally untradeable, but that means only the Arans are allowed to use it, so I decided to do something about it.");
	if (status == 3)
		qm.sendNextPrev("I have changed the settings so that her ring can be transferred within the account. The ring cannot be traded with other users, but you can use the ring with different characters on your account.");
	if (status == 4)
		qm.sendAcceptDecline("Did you get all that?");
	if (status == 5){
		if(!qm.canHold(2031008)){
			qm.sendNext("Hmmm...I was going to give you Rien Teleport Ticket, which is very useful for Arans, but... your 'use' inventory seems to be full. Please make at least a single slot available.");
			qm.dispose();
		} else {
			qm.gainItem(2031008,1);
			qm.sendNext("I have just give you the Rien Teleport Ticket, which will allow you to teleport directly to Rien, where Lirin resides. If you ever become a Level 50 Aran, then use it!");
			qm.forceCompleteQuest();
	} if (status == 6){
		qm.sendPrev("Now I hope each and every one of you enjoy the festives that come with the return of Aran!");
		qm.dispose();
	}
}
}
	