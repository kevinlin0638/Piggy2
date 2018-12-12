/**
 * @author: Eric
 * @npc: Spiegelmann
 * @func: Monster Park
*/

var status = 0;
var items = [1012270, 1162008, 2430275, 2550000];

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	(mode == 1 ? status++ : mode == 0 ? status-- : cm.dispose());
	if (status == 0) {
		cm.sendSimple("Welcome to the Monster Park! My name is Spiegelmann, and I am the owner and operator of the Monster Park. That funny face you're making tells me you have some questions. What would you like to know?#b\r\n#L0#What is the Monster Park?\r\n#L1#I heard I can get a special item at the Monster Park..\r\n#L2#I would like to exchange Monster Park Commemorative Coins for an item.");
	} else if (status == 1) {
		if (selection == 0) {
			cm.sendNext("What is the #bMonster Park#k? It's a park full of monsters! Ha! But don't worry, it's more fun than it sounds. You see, I've brought monsters here from all over the world, and set them up as special challenges for hardy adventurers.");
		} else if (selection == 1) {
			status = 5;
			cm.sendNext("Ha! Word travels fast, I guess. Yes, we do have special items here. Why don't you take a look?\r\n#i" + items[0] + ":# #bSpiegelmann's Mustache (5 days)#k\r\n#i" + items[1] + ":# #bSpiegelmann's Autograph (7 days)#k\r\n#i" + items[2] + ":# #bSpiegelmann's Hot-Air Balloon 7-Day Coupon#k\r\n#i" + items[3] + ":# #bSpiegelmann's Badge Chest#k\r\n\r\nSee something that interests you? Well, I left some special souvenirs in the Monster Park. When you find #bMonster Park Commemorative Coins#k while hunting monsters in the Monster Park, bring them to #bLaku#k to exchange them for these special gifts.");
		} else if (selection == 2) {
			cm.sendOk("I'm very busy running the Monster Park, so I cannot exchange your souvenirs for gifts.\r\nHowever, my assistant #bLaku#k can help you with that. Remember, take your items to Laku for special gifts! Have fun, now!");
			cm.dispose();
		}
	} else if (status == 2) {
		cm.sendNextPrev("As you must know, I only want to see peace and harmony flourish in Maple World. For that reason, I made all the dungeons in the monster Park #bParty Zones#k, to encourage people to work and have fun together.");
	} else if (status == 3) {
		cm.sendNextPrevS("Is there another reason that you opened the Monster Park?");
	} else if (status == 4) {
		cm.sendNextPrev("Another reason? Why, don't be ridiculous! I just want everyone to enjoy my Monster Park. Of course, I can't just let everyone in. No, see, you need a ticket to enter the park.");
	} else if (status == 5) {
		cm.sendNextPrev("But don't worry, they're easy to get. When you are hunting regular monsters, you will sometimes find #bZebra Stripe Ticket Pieces, Leopard Stripe Ticket Pieces#k, or #bTiger Stripe Ticket Pieces#k. Once you collect 10 of any of them, go see #b#p9071002##k. She will exchange them for an entrance ticket.");
		cm.dispose();
	} else if (status == 6) {
		cm.sendNextPrev("All right, have fun at the Monster Park!");
		cm.dispose();
	}
}