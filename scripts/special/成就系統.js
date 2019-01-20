var eff = "#fEffect/CharacterEff/1051296/1/0#";
/*
*	冰火家族萬能NPC
*
*
*
*
*
*/
var x = "";//高音譜記號
var kk = "";//BOSS
var c = "";//"#fUI/UIMiniGame/starPlanetRPS/heart#";//愛心
var cmark = "";//愛心
var lb = "";//藍色小光
var b = "";//超萌熊熊
var cat = "";//超萌喵咪
var leaf = "";
var tt = "";
var status;
var ll;
var achi_count = 0;

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
			text = "\t\t\t #k#d成就系統\r\n\r\n#n";
			text += "\t\t   #e#b完成\t\t#r未完成\r\n\r\n#n";
			for(var i = 0; i < 120;i++){
				ll = cm.getPlayer().getAchievement(i);
				if(ll != null){
					if(cm.getPlayer().achievementFinished(i))
						achi_count++;
				}
			}
			text += tt +"#d目前完成 : #r" + achi_count + " #d個成就!\r\n";
			for(var i = 0; i < 120;i++){
				ll = cm.getPlayer().getAchievement(i);
				if(ll != null){
					if(cm.getPlayer().achievementFinished(i)){
						text+= "#b";
					}else{
						text+="#r";
					}
					text+=tt + ll.getName()+"\r\n";
				}
			}
			text += "\r\n\r\n\t\t   #b#L999#" + leaf +"#e#r回上一頁#l"
			cm.sendOk(text);
            break;
		case 1:
			if(selection == 999){
				cm.dispose();
				cm.openNpc(9900002)
			}
			break;
        default:
			cm.sendOk("發生錯誤，請聯繫管理員!");
			cm.dispose();
			break;
    }
}

    