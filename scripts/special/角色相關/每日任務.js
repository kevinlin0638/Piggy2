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
			text = "\t\t\t   #b每日任務\r\n\r\n#n";
			text += "\t#e#b已領取\t#r完成\t#k未完成\r\n\r\n#n";
			for(var i = 1; i <= 20;i++){
				ll = cm.getPlayer().getDailyQuest(i);
				if(ll != null){
					if(cm.getPlayer().DailyQuestRewarded(i))
						achi_count++;
				}
			}
			text += tt +"\t#d目前完成 : #r" + achi_count + " #d個每日任務!\r\n\r\n";
			for(var i = 0; i < 120;i++){
				ll = cm.getPlayer().getDailyQuest(i);
				if(ll != null){
					if(cm.getPlayer().DailyQuestRewarded(i)){
						text+= "\t#b";
						text+=tt + ll.getName()+"\r\n";
					}else if(cm.getPlayer().DailyQuestFinished(i)){
						text+=" #L" +i + "##r";
						text+=tt + ll.getName()+"#l\r\n\r\n";
					}else{
						text += "\t#k"
						text+=tt + ll.getName()+"\r\n";
					}
					
				}
			}
			text += "\r\n\r\n\t\t   #b#L999#" + leaf +"#e#r回上一頁#l"
			cm.sendSimple(text);
            break;
		case 1:
			if(selection == 999){
				cm.dispose();
				cm.openNpc(9900002)
			}else{
				if(!cm.canHold(4310014, 1)){
					cm.sendOk("您沒有足夠的背包空位!");
					cm.dispose();
					return;
				}
				
				if(selection == 6){
					if(!cm.haveItem(4000003, 200)){
						cm.sendOk("您沒有足夠的 樹枝!");
						cm.dispose();
						return;
					}
					cm.gainItem(4000003, -200);
				}else if(selection == 7){
					if(!cm.haveItem(4000016, 200)){
						cm.sendOk("您沒有足夠的 紅寶殼!");
						cm.dispose();
						return;
					}
					cm.gainItem(4000016, -200);
				}else if(selection == 8){
					if(!cm.haveItem(4000001, 200)){
						cm.sendOk("您沒有足夠的 菇菇寶貝傘!");
						cm.dispose();
						return;
					}
					cm.gainItem(4000001, -200);
				}
				cm.getPlayer().setDailyQuestRewarded(selection)
				if(cm.getPlayer().getGuild() != null)
					cm.getPlayer().getGuild().gainGP(50, true, cm.getPlayer().getId());
				cm.gainItem(4310014, 1);
				cm.dispose();
				cm.openNpc(9330003, "角色相關/每日任務");
			}
			break;
        default:
			cm.sendOk("發生錯誤，請聯繫管理員!");
			cm.dispose();
			break;
    }
}

    