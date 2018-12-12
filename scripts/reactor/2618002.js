/**
 * @author: Eric
 * @reactor: Card Data reactor 2
 * @func: Romeo and Juliet GMS-like PQ
*/

function act() {
	if (rm.haveItem(4001133)) {
		rm.getMap(rm.getMapId() + 1).getReactorByName(rm.getMapId() == 926100200 ? "rnj31_out" : "jnr31_out").forceHitReactor(1);
		rm.gainItem(4001133, -1);
	}
}