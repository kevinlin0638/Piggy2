/**
 * @author : Eric
 * @npc: Bulletin Board
 * @func: @news
*/

function start() {
	if (cm.getPlayer().getMapId() == 102040200) {
		cm.sendOk("<Notice> \r\n Are you part of a Guild that possesses an ample amount of courage and trust? Then take on the Guild Quest and challenge yourselves!\r\n\r\n#bTo Participate :#k\r\n1. The Guild must consist of at least 6 people!\r\n2. The leader of the Guild Quest must be a Master or a Jr. Master of the Guild!\r\n3. The Guild Quest may end early if the number of guild members participating falls below 6, or if the leader decides to end it early!");
	} else {
		cm.sendOk("#r-- Development's News & Updates -- #k \r\n" + cm.getDevNews());
	}
    cm.dispose();
}