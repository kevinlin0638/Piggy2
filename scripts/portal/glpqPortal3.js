function enter(pi) {
    var em = pi.getEventManager("CWKPQ");
    if (em != null) {
	if (!em.getProperty("glpq3").equals("10")){
	    pi.playerMessage("此傳點尚未開啟");
	} else {
	    pi.warp(610030400, 0);
	}
    }
}