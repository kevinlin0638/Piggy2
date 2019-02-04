/*      
 *  
 *  功能：等級送禮
 *  
 */

var status = 0;

var giftId = -1;
var giftToken = Array();
var gifts = null;
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
		cm.sendYesNo("您確定要點滿技能?");
    }else if(status == 1){
		/*if(cm.getPlayer().getJob() >= 3300 && cm.getPlayer().getJob() <= 3312){
			cm.teachSkill(30001061, 1, 0);
			cm.teachSkill(30001062, 1, 0);
		}
		else if(cm.getPlayer().getJob() >= 3100 && cm.getPlayer().getJob() <= 3112){
			cm.teachSkill(30010112, 1, 1);
			cm.getPlayer().maxSingleSkill(1, 30010110); // Dark Winds
            cm.getPlayer().maxSingleSkill(1, 30010185); // Demonic Blood
		}else if(cm.getPlayer().getJob() == 432){
			cm.teachSkill(4321000, 0, 5);
		}else if(cm.getPlayer().getJob() >= 433 && cm.getPlayer().getJob() <= 434){
			cm.teachSkill(4331002, 0, 10);
		}*/
		cm.getPlayer().maxSkillsByJob();
		
	    //cm.getPlayer().fakeRelog();
		cm.dispose();
	}
}