/**
 * @author: Eric
 * @reactor: Beaker(_dummy)
 * @func: Romeo and Juliet GMS-like PQ (Takes suspicious liquid to fill beaker)
*/

function act() {
	if (rm.haveItem(4001132, 1)) {
		rm.gainItemSilent(4001132, -1);
	}
	if (rm.getReactor().getState() >= 7) {
		var em = rm.getEventManager("Juliet");
		if (rm.getPlayer().getParty() != null && em != null && rm.getReactor().getState() >= 7) {
			var react = rm.getMap().getReactorByName(rm.getMapId() == 926100100 ? "rnj2_door" : "jnr2_door");
			em.setProperty("stage3", parseInt(em.getProperty("stage3")) + 1);
			react.forceHitReactor(react.getState() + 1);
			rm.addCount(1);
			if (rm.getPlayer().getReactorClicks() == 3) {
				rm.showEffect(true, "quest/party/clear"); // map
				rm.showEffect(false, "quest/party/clear"); // client
				rm.playSound(true, "Party1/Clear"); // map
				rm.playSound(false, "Party1/Clear"); // client
				rm.setCount(0); // refresh
				rm.getPlayer().getMap().setSpawns(false); // TODO: fix spawns.. they respawn even after this idk why
				rm.getPlayer().getMap().killAllMonsters(true);
			}
		}
    }
}