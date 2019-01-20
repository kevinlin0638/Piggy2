var status;
var text;
var target_EQP;
var sele;
var bk;
var prise;
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {

    if (mode == 0) {
	cm.dispose();
	return;
    } else if (mode == 1){
	status++;
    } else {
	status--;
    }

    switch (status) {
        case 0: 
			text = "#b#h ##k 您好，使用此功能可以將您一個裝備，增加 #r1 #k個#r衝捲數\r\n";
			text += "#b您目前剩餘 #r" +cm.getPlayer().getPoints()+ " 點#k #b贊助點#k 。\r\n#d使用回真卷軸仍會保有增加的衝捲數，以下為價位(最多5次):\r\n";
			text += "#e第一次增加 : 50 贊助點\r\n";
			text += "第二次增加 : 100 贊助點\r\n";
			text += "第三次以後 : 150 贊助點\r\n#n";
			text += "\r\n#k請將道具放在#r裝備欄第一格#k然後按下一步。";
			cm.sendNext(text);
            break;
        case 1: 
			sele = selection;
			target_EQP = cm.getInventory(1).getItem(1);
			if(target_EQP != null){
				if(cm.getMaxEnhance(target_EQP) > 0){
					if(target_EQP.getExtraScroll() >= 5){
						cm.sendOk("此道具 #v"+target_EQP.getItemId()+"##b#z"+target_EQP.getItemId()+"#的衝捲數已到達上限!");
						cm.dispose();
						return;
					}
					bk = target_EQP.getExtraScroll();
					prise = bk==0?50:bk==1?100:150;
					text = "#h # 您好，您目前剩餘 #r" +cm.getPlayer().getPoints()+ " 點#k #b贊助點#k\r\n將要加捲的裝備為\r\n#v"+target_EQP.getItemId()+"##b#z"+target_EQP.getItemId()+"#-已增加 "+ bk +" 衝捲數\r\n";
					text += "\r\n#k您目前為第 #b" + (bk+1) + " #k次增加衝捲次數 所需花費 : #r" + prise+" #b贊助點"
					cm.sendYesNo(text);
				}else{
					cm.sendOk("此道具 #v"+target_EQP.getItemId()+"##b#z"+target_EQP.getItemId()+"# 無法使用卷軸強化!");
					cm.dispose();
					return;
				}
			}else{
				cm.sendOk("請確認有將裝備放置於裝備欄第一格!!");
				cm.dispose();
				return;
			}
            break;
        case 2:
			if(cm.getPlayer().getPoints() < prise){
				cm.sendOk("您的贊助點不足。");
				cm.dispose();
				return;
			}
			cm.getPlayer().gainPoints(-prise);
			target_EQP.setUpgradeSlots((target_EQP.getUpgradeSlots() + 1));
			target_EQP.setExtraScroll(target_EQP.getExtraScroll() + 1);
			cm.resetItem(target_EQP, 1);
			text = "#h # 您好，您目前剩餘 #r" +cm.getPlayer().getPoints()+ " 點#k #b贊助點#k\r\n#v"+target_EQP.getItemId()+"##b-已增加 1 衝捲數\r\n";
			cm.sendOk(text);
			cm.dispose();
			break;
        case 3:
            cm.dispose();
            break;
    }
}
