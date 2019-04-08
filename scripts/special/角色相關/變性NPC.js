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
		cm.sendYesNo("您確定要變性?\r\n#r需花費 500萬 楓點");
    }else if(status == 1){
		if(cm.getNX(2) > 5000000){
			var g = cm.getPlayer().getGender();
			if(g == 0){
				cm.getPlayer().setGender(1)
			}else{
				cm.getPlayer().setGender(0)
			}
			cm.gainNX(2, -5000000);
			cm.sendOk("變性成功!");
		}else{
			cm.sendOk("您的楓點不足!");
		}
		cm.dispose();
	}
}