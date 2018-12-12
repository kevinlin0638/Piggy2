/**
 * @author: Eric
 * @npc: Mary
 * @func: Monster Park Ticket Seller
*/

var status = 0;
var ticketExch = [[4001513, 4001514], [4001515, 4001516], [4001521, 4001522]];
var ticketPurch = [[4001514, 50000], [4001516, 100000], [4001522, 200000]];

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    (mode == 1 ? status++ : mode == 0 ? status-- : cm.dispose());
	if (status == 0) {
		cm.sendSimple("Hello! If you want to enjoy the Monster Park, then you came to the right person! So, what can I do for you?#b\r\n#L0#Exchange Zebra Stripe Ticket Piece\r\n#L1#Exchange Leopard Stripe Ticket Piece\r\n#L2#Exchange Tiger Stripe Ticket Piece\r\n#L3#Purchase an Entrance Ticket");
	} else if (status == 1) {
		if (selection == 0 || selection == 1 || selection == 2) {
			if (cm.haveItem(ticketExch[selection][0], 10)) {
				cm.gainItem(ticketExch[selection][0], -10);
				cm.gainItem(ticketExch[selection][1], 1); // does gms give a notice after exchange?
			} else {
				cm.sendNext("Hey, you don't have enough Ticket Pieces. Remember, you need to have #b10 Ticket Pieces#k to exchange for a Ticket.");
			}
			cm.dispose();
		} else if (selection == 3) {
			cm.sendSimple("Hm... I'm not supposed to do this, but since I'm having such a great day, I will give you a special discount, just for you! #rBut I'll sell you 3 Tickets per day, no matter the types of the Tickets.\r\nAnd, er, keep this a secret from Spiegelmann!#k#b\r\n#L0#Zebra Stripe Ticket 50,000 meso\r\n#L1#Leopard Spot Ticket 100,000 meso\r\n#L2#Tiger Stripe Ticket 200,000 meso");
		}
	} else if (status == 2) {
		if (selection >= 0 && selection <= 2) {
			if (cm.haveMeso(ticketPurch[selection][1])) {
				cm.gainMeso(-ticketPurch[selection][1]);
				cm.gainItem(ticketPurch[selection][0], 1);
				cm.sendOk("Okay, have a great time at the Monster Park!");
			} else {
				cm.sendNext("Hey, you don't have enough mesos. Remember, you need to have #b" + ticketPurch[selection][1] + " mesos#k to purchase a Ticket."); // not gms-like
			}
		}
		cm.dispose();
	}
}