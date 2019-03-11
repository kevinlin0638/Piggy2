var status;
var text;
var target_EQP;
var sele;
var bk;
var prise = 1500000;
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
			text = "#b#h ##k 您好，使用此功能可以將您一個裝備\r\n#r回復初始數值#k\r\n";
			text += "#b您目前剩餘 #r" +cm.getNX(2)+ " 點#k #b楓點#k 。\r\n#d使用回真卷軸仍會保有增加的衝捲數\r\n";
			text += "\r\n#k請將道具放在#r裝備欄第一格#k然後按下一步。";
			cm.sendNext(text);
            break;
        case 1: 
			sele = selection;
			target_EQP = cm.getInventory(1).getItem(1);
			if(target_EQP != null){
				if(!cm.isCash(target_EQP.getItemId())){
					bk = target_EQP.getExtraScroll();
					text = "#h # 您好，您目前剩餘 #r" +cm.getNX(2)+ " 點#k #b贊助點#k\r\n將要回真的裝備為\r\n#v"+target_EQP.getItemId()+"##b#z"+target_EQP.getItemId()+"#-已增加 "+ bk +" 贊助衝捲數\r\n";
					text += "\r\n#k您所需花費 : #r" + prise+" #b楓點"
					cm.sendYesNo(text);
				}else{
					cm.sendOk("此道具 #v"+target_EQP.getItemId()+"##b#z"+target_EQP.getItemId()+"# 無法使用回真功能!");
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
			if(cm.getNX(2) < prise){
				cm.sendOk("您的楓點不足。");
				cm.dispose();
				return;
			}
			cm.gainNX(2, -prise);
			cm.replaceItem(target_EQP);
			text = "#h # 您好，您目前剩餘 #r" +cm.getNX(2)+ " 點#k #b楓點#k\r\n成功回真 : #v"+target_EQP.getItemId()+"##b\r\n";
			cm.sendOk(text);
			cm.dispose();
			break;
        case 3:
            cm.dispose();
            break;
    }
}
