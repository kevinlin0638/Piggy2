var eff = "#fEffect/CharacterEff/1051296/1/0#";
/*
*	冰火家族萬能NPC
*
*
*
*
*
*/
var search_str;
var status;
var ll;
var achi_count = 0;
var sele;
var mobs;
var items;

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
			text = "您想要查詢什麼呢?\r\n#b";
			
			text += "#L0#怪物掉寶#l\r\n#b";
			text += "#L1#會掉落此道具的怪物#l\r\n#b";
			cm.sendSimple(text);
            break;
		case 1:
			sele = selection;
			if(sele == 0){
				cm.sendGetText("請輸入想查詢的怪物名稱");
			}else if(sele == 1){
				cm.sendGetText("請輸入想查詢的道具名稱");
			}
			break;
		case 2:
			search_str = cm.getText();
			if(search_str != null && search_str != ""){
				cm.sendYesNo("您確定要查詢 #b" + search_str + "#k ?");
			}else{
				cm.sendOk("輸入錯誤!");
				cm.dispose();
			}
			break;
		case 3:
			if(sele == 0){
				mobs = cm.getSMob(search_str);
				text = "以下為查詢到相關的怪物\r\n";
				for(var i = 0; i < mobs.length;i++){
					text += "#b#L" + i + "##o" + mobs[i] + "#";
					if(cm.getPlayer().isGM())
						text += " - " + mobs[i];
					text += "#l#k\r\n";
				}
				cm.sendSimple(text);
			}else if(sele == 1){
				items = cm.getSItem(search_str);
				text = "以下為查詢到相關的道具\r\n";
				for(var i = 0; i < items.length;i++){
					text += "#b#L" + i + "##v" + items[i] + "##z" + items[i] + "#";
					if(cm.getPlayer().isGM())
						text += " - " + items[i];
					text += "#l#k\r\n";
				}
				cm.sendSimple(text);
			}
			break;
		case 4:
			if(sele == 0){
				var drops = cm.getDrops(mobs[selection]);
				text = "以下為查詢到 #b#o" + mobs[selection] + "##k 會掉落的道具\r\n";
				for(var i = 0; i < drops.length;i++){
					text += "#b#L" + i + "##v" + drops[i] + "##z" + drops[i] + "#";
					if(cm.getPlayer().isGM())
						text += " - " + drops[i];
					text += "#l#k\r\n";
				}
				cm.sendOk(text);
			}else if(sele == 1){
				var dropers = cm.getDropers(items[selection]);
				text = "以下為查詢到會掉落 #b#v" + items[selection] + "##z" + items[selection] + "##k 的怪物\r\n";
				for(var i = 0; i < dropers.length;i++){
					text += "#b#o" + dropers[i] + "#";
					if(cm.getPlayer().isGM())
						text += " - " + dropers[i];
					text += "#k\r\n";
				}
				cm.sendOk(text);
			}
			cm.dispose();
			break;
        default:
			cm.sendOk("發生錯誤，請聯繫管理員!");
			cm.dispose();
			break;
    }
}

    