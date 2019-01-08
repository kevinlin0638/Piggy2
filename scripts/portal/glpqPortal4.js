function enter(pi) {
    var em = pi.getEventManager("CWKPQ");
    if (em != null) {
	if (!em.getProperty("glpq4").equals("5")){
	    pi.playerMessage("此傳點尚未開啟");
	} else {
	    pi.warp(610030500, 0);
	}
    }
}