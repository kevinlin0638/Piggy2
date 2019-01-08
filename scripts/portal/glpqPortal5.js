function enter(pi) {
    var em = pi.getEventManager("CWKPQ");
    if (em != null) {
	if (!em.getProperty("glpq5").equals("5")){
	    pi.playerMessage("此傳點尚未開啟");
	} else {
	pi.removeAll(4001256);
	pi.removeAll(4001257);
	pi.removeAll(4001258);
	pi.removeAll(4001259);
	pi.removeAll(4001260);
	    pi.warp(610030600, 0);
	}
    }
}