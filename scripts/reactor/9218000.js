/**
 * @author: Eric
 * @script: Reactor 9218000
 * @func: Gate Unlocking for LHC PQ
*/
// This PQ is unfunctional as the gates are not working.

function act() {
	//TODO: code a variable to check all gates with the data given :P
	//rm.getPlayer().runGate();
	if (rm.getPlayer().isGM()) {
		rm.getPlayer().dropMessage(5, "There are debugging and system errors with this PQ rendering it broken. Bug Eric about this! :(");
	} else {
		rm.getPlayer().dropMessage(5, "Please try again later.");
	}
	//rm.mapMessage(5, "You've unlocked the gate! I grant you access to the portal.");
	//rm.getPlayer().getEventInstance().setProperty("kentaSaving", "0");
}