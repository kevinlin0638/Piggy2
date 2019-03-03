var baseid = 251010402;
var dungeonid = 251010410;
var dungeons = 30;

function enter(pi) {
    if (pi.getMapId() == baseid) {
	if (pi.getPlayer().getFame() < 10) {
	    pi.playerMessage(5, "您必須擁有10名聲才能進入.");
	    return;
	}
	if (pi.getParty() != null) {
	    if (pi.isLeader()) {
		for (var i = 0; i < dungeons; i++) {
		   if (pi.getPlayerCount(dungeonid + i) == 0) {
			pi.warpParty(dungeonid + i);
			return true;
	         }
	      }
	    } else {
		pi.playerMessage(5, "你不是隊長.");
		return false;
	    }
	} else {
	    for (var i = 0; i < dungeons; i++) {
	    	if (pi.getPlayerCount(dungeonid + i) == 0) {
	    	    pi.warp(dungeonid + i);
	          return true;
		}
	    }
	}
	pi.playerMessage(5, "All of the Mini-Dungeons are in use right now, please try again later.");
	return false;
    } else {
	pi.playPortalSE();
	pi.warp(baseid, "MD00");
	return true;
    }
}