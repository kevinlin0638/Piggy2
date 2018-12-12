/**
 * @author: Eric
 * @npc: Shammos
 * @function: Resurrection of the Hoblin King
*/
var status = -1;
var start;

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	(mode == 1 ? status++ : mode == 0 ? status-- : cm.dispose());
	if (status == 0) {
		if (cm.getPlayer().getMapId() == 921120005) {
			cm.sendNext(!cm.getPlayer().getMap().getAllMonstersThreadsafe().isEmpty() ? "There's still monsters left on the field. Take care of them first before talking to me." : "Good job, you've eliminated all the monsters. Now, let's go seal up Rex.");
		} else {
			start = 1;
			cm.sendYesNo("Are you all ready? If you're not ready, don't bother."); // TODO: match gms-like text and fix walk in portal animation
		}
	} else if (status == 1) {
		if (start == 1) {
			cm.sendNext("Okay. Let's go. Follow me.");
		} else if (cm.getPlayer().getMapId() == 921120005 && cm.getPlayer().getMap().getAllMonstersThreadsafe().isEmpty()) {
			cm.warpParty(921120100, 0);
			cm.dispose();
			return;
		}
	} else if (status == 2) {
		var party = cm.getPlayer().getParty().getMembers();
		var mapId = cm.getPlayer().getMapId();
		var next = true;
		var size = 0;
		var it = party.iterator();
		while (it.hasNext()) {
			var cPlayer = it.next();
			var ccPlayer = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
			if (ccPlayer == null || ccPlayer.getLevel() < 120) {
				next = false;
				break;
			}
			size += (ccPlayer.isGM() ? 4 : 1);
		}	
		if (next && size >= 2) {
			var em = cm.getEventManager("Rex");
			if (em == null) {
				cm.sendOk("I don't want to see Rex at the moment. Please try again later.");
			} else {
				var prop = em.getProperty("state");
				if (prop.equals("0") || prop == null) {
					em.startInstance(cm.getPlayer().getParty(), cm.getPlayer().getMap(), cm.getPlayer().getAveragePartyLevel());
					for (var i = 0; i < cm.getPlayer().getParty().getMembers().size(); i++) {
						var partychrz = cm.getPlayer().getClient().getChannelServer().getPlayerStorage().getCharacterByName(cm.getPlayer().getParty().getMemberByIndex(i).getName());
						var hoblin = partychrz.getQuestNAdd(Packages.server.quest.MapleQuest.getInstance(150138));
						var data = hoblin.getCustomData();
						hoblin.setCustomData("" + (parseInt(data) - 1) + "");
					}
				} else {
					cm.sendOk("Another party quest has already entered this channel.");
					cm.dispose();
				}
			}
		} else {
			cm.sendOk("All 2+ members of your party must be here and level 120 or greater.");
			cm.dispose();
		}
	}
}