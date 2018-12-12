/**
 * @author: Eric
 * @reactor: Card Data reactor 1
 * @func: Romeo and Juliet GMS-like PQ
*/

function act() {
	if (rm.haveItem(4001133)) {
		rm.getMap(rm.getMapId() + 2).getReactorByName(rm.getMapId() == 926100200 ? "rnj32_out" : "jnr32_out").forceHitReactor(1);
		rm.gainItem(4001133, -1);
	}
}