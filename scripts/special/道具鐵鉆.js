var status;
var text;
var target_EQP;
var ability_EQP;
var sele;
var bk;
var prise = 3000000;
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
			text = "#b#h ##k 您好，使用此功能可以將您一個裝備\r\n#r複製外型至其他裝備#k\r\n";
			text += "#b您目前剩餘 #r" +cm.getNX(2)+ " 點#k #b楓點#k 。\r\n#d以下為價位:\r\n#r1000 萬楓點";
			text += "\r\n#k請將#r提供外型道具 放在#r裝備欄第一格\r\n\r\n";
			text += "\r\n#k請將#r提供能力的道具 放在#r裝備欄第二格\r\n#e(注意此道具使用完畢會消失)#n。\r\n\r\n#k然後按下一步";
			cm.sendNext(text);
            break;
        case 1: 
			sele = selection;
			target_EQP = cm.getInventory(1).getItem(1);
			ability_EQP = cm.getInventory(1).getItem(2);
			if(target_EQP != null && ability_EQP != null){
				if(cm.isCash(target_EQP.getItemId()) && cm.isCash(ability_EQP.getItemId())){
					text = "#h # 您好，您目前剩餘 #r" +cm.getNX(2)+ " 點#k #b贊助點#k\r\n\r\n";
					text += "#r將要提供外觀裝備為\r\n#v"+target_EQP.getItemId()+"##b#z"+target_EQP.getItemId()+"#\r\n\r\n"
					text += "#r將要提供能力裝備為\r\n#v"+ability_EQP.getItemId()+"##b#z"+ability_EQP.getItemId()+"# #r#e（此裝備會消失）#n"
					text += "\r\n\r\n#b您所需花費 : #r" + prise+" #b楓點\r\n\r\n"
					
					text += "#r#e注意：按下是之後無法反悔\r\n\r\n"
					cm.sendYesNo(text);
				}else{
					cm.sendOk("非點數道具無法使用鐵砧功能!");
					cm.dispose();
					return;
				}
			}else{
				cm.sendOk("請確認有將裝備放置於裝備欄第一格與第二格!!");
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
			if(cm.replaceItem(ability_EQP, target_EQP)){
				cm.gainNX(2, -prise);
				text = "#h # 您好，您目前剩餘 #r" +cm.getNX(2)+ " 點#k #b楓點#k\r\n成功 : #v"+target_EQP.getItemId()+"##b\r\n";
				cm.sendOk(text);
				cm.dispose();
			}else{
				cm.sendOk("失敗 請確認道具是否為同部位");
				cm.dispose();
			}
			break;
        case 3:
            cm.dispose();
            break;
    }
}
