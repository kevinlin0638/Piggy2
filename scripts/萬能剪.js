var status;
var text;
var target_EQP;
var sele;
var bk;
var prise = [0, 250000, 0, 400000];
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
			text = "#b#h ##k 您好，使用此功能可以將您一個裝備\r\n#r使用萬能剪刀#k\r\n";
			text += "#b您目前剩餘 #r" +cm.getNX(2)+ " 點#k #b楓點#k 。"+
			"\r\n#d使用回真卷軸仍會保有增加的衝捲數，價位:裝備-25萬楓點 裝飾-40萬楓點\r\n";
			text += "\r\n#L1#剪裝備(#k請將道具放在#r裝備欄第一格#k)#l";
			text += "\r\n#L3#剪裝飾(#k請將道具放在#r裝飾欄第一格#k)#l";
			cm.sendSimple(text);
            break;
        case 1: 
			sele = selection;
			target_EQP = cm.getInventory(sele).getItem(1);
			if(target_EQP != null){
				if(!cm.isCash(target_EQP.getItemId())){
					text = "#h # 您好，您目前剩餘 #r" +cm.getNX(2)+ " 點#k #b贊助點#k\r\n將要使用剪刀的裝備為\r\n#v"+target_EQP.getItemId()+"##b#z"+target_EQP.getItemId()+"#\r\n";
					text += "\r\n#k您所需花費 : #r" + prise[sele]+" #b楓點"
					cm.sendYesNo(text);
				}else{
					cm.sendOk("此道具 #v"+target_EQP.getItemId()+"##b#z"+target_EQP.getItemId()+"# 無法使用剪刀功能!");
					cm.dispose();
					return;
				}
			}else{
				cm.sendOk("請確認有將道具放置於選定欄位第一格!!");
				cm.dispose();
				return;
			}
            break;
        case 2:
			if(cm.getNX(2) < prise[sele]){
				cm.sendOk("您的楓點不足。");
				cm.dispose();
				return;
			}
			cm.gainNX(2, -prise);
			cm.doScissor(target_EQP, sele);
			text = "#h # 您好，您目前剩餘 #r" +cm.getNX(2)+ " 點#k #b楓點#k\r\n成功使用萬能剪刀 : #v"+target_EQP.getItemId()+"##b\r\n";
			cm.sendOk(text);
			cm.dispose();
			break;
        case 3:
            cm.dispose();
            break;
    }
}
