/**
 * @author: Eric
 * @npc: Yulete
 * @func: Romeo and Juliet GMS-like PQ
*/
var status = 0;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	(mode == 1 ? status++ : mode == 0 ? status-- : cm.dispose());
	var em = cm.getEventManager("Juliet");
	if (status == 0) {
		if (!cm.isLeader() || em == null) {
			cm.sendOk("...");
			cm.dispose();
			return;
		}
		cm.sendNext("...After a long, long wait... the time has finally come for me to unveil my true work of genius to the dummies of Magatia! And to think they have been down on my work all these years... wait, who are you guys? Have you been sent here by Alcando and Zenumist?");
	} else if (status == 1) {
		cm.sendNextPrev("Hahaha. this is actually not bad. You people look like the perfect target for my experiments. Think of it as an honor to the highest degree. You will see first hand the magical creation of alchemy and mechanical engineering!!");
	} else if (status == 2) {
		cm.getPlayer().getMap().startMapEffect("Please protect Romeo by defeating Frankenroid!", 5120022);
		var mobId = 9300139;
		if (em.getProperty("stage").equals("1") && em.getProperty("stage5").equals("0")) {
			mobId = 9300140;
			em.setProperty("stage", "2");
		}
		var mob = em.getMonster(mobId);
		var hp = mobId == 9300140 ? (809000 * cm.getPlayer().getAveragePartyLevel()) : (627000 * cm.getPlayer().getAveragePartyLevel());
		cm.getPlayer().getMap().setSpawns(true);
		cm.getPlayer().getMap().respawn(true);
		cm.getPlayer().getMap().removeNpc(2112010); // this
		cm.getPlayer().spawnCustomMonster(mobId, hp, 2500, 1, 240, 150);
		cm.dispose();
	}
}