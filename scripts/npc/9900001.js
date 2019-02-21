/** Author: nejevoli
	NPC Name: 		NimaKin
	Map(s): 		Victoria Road : Ellinia (180000000)
	Description: 		Maxes out your stats and able to modify your equipment stats
*/
importPackage(java.lang);

var status = 0;
var slot = Array();
var stats = Array("Strength", "Dexterity", "Intellect", "Luck", "HP", "MP", "Weapon Attack", "Magic Attack", "Weapon Defense", "Magic Defense", "Accuracy", "Avoidability", "Hands", "Speed", "Jump", "Slots", "Vicious Hammer", "Used slot", "Enhancements", "Potential stat 1", "Potential stat 2", "Potential stat 3", "Owner");
var selected;
var statsSel;

var sele;
var sele_index;
var all;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status >= 0 && mode == 0) {
	cm.dispose();
	return;
    }
    if (mode == 1)
	status++;
    else
	status--;

    if (status == 0) {
	if (cm.getPlayerStat("ADMIN") == 1) {
		cm.sendSimple("你想要幹嘛?#b\r\n#L0#讓我滿等滿素質!#l\r\n#L1#讓我滿技!#l\r\n#L2#更改裝備數值!#l\r\n#L3#看看潛能數值#l\r\n#L4#設定 AP/SP 為 0#l\r\n#L5#我想要拿全部新手裝備#l\r\n#L6#我想要去裝備墳墓#l\r\n#L7#我想要斗內#l#k");
	} else if (cm.getPlayerStat("GM") == 1) {
		cm.sendSimple("你想要幹嘛?#b\r\n#L0#讓我滿等滿素質!#l\r\n#L1#讓我滿技!#l\r\n#L4#設定 AP/SP 為 0#l#\r\n#L5#我想要拿全部新手裝備#lk");
	} else {
	    cm.dispose();
	}
    } else if (status == 1) {
		sele = selection;
		if (selection == 0) {
			if (cm.getPlayer().getGMLevel() > 3) {
			cm.maxStats();
			cm.sendOk("I have maxed your stats. Happy Mapling!");
			}
			cm.dispose();
		} else if (selection == 1) {
			//Beginner
			if (cm.getPlayerStat("GM") == 1) {
				cm.maxAllSkills();
			}
			cm.dispose();
		} else if (selection == 2 && cm.getPlayerStat("ADMIN") == 1) {
			var avail = "";
			for (var i = -1; i > -199; i--) {
			if (cm.getInventory(-1).getItem(i) != null) {
				avail += "#L" + Math.abs(i) + "##t" + cm.getInventory(-1).getItem(i).getItemId() + "##l\r\n";
			}
			slot.push(i);
			}
			cm.sendSimple("Which one of your equips would you like to modify?\r\n#b" + avail);
		} else if (selection == 3 && cm.getPlayerStat("ADMIN") == 1) {
			var eek = cm.getAllPotentialInfo();
			var avail = "";
			for (var ii = 0; ii < eek.size(); ii++) {
				avail += "#L" + eek.get(ii) + "#Potential ID " + eek.get(ii) + "#l\r\n";
			}
			cm.sendSimple("What would you like to learn about?\r\n#b"+ avail);
			status = 9;
		} else if (selection == 4) {
			cm.getPlayer().resetAPSP();
			cm.sendNext("Done.");
			cm.dispose();
		} else if (selection == 5) {
			
			cm.gainItem(1302000, 1); // 劍(單)
			cm.gainItem(1402001, 1); // 劍(雙)
			cm.gainItem(1312004, 1); // 斧(單)
			cm.gainItem(1412001, 1); // 斧(雙)
			cm.gainItem(1322008, 1); // 棍(單)
			cm.gainItem(1422000, 1); // 棍(雙)
			cm.gainItem(1442000, 1); // 矛
			cm.gainItem(1432000, 1); // 槍
			
			cm.gainItem(1492000, 1); // 火槍
			cm.gainItem(1482000, 1); // 指虎
			
			cm.gainItem(1382000, 1); // 長杖
			cm.gainItem(1372005, 1); // 短杖
			
			cm.gainItem(1472000, 1); // 拳套
			cm.gainItem(1332007, 1); // 短刀
			
			cm.gainItem(1452002, 1); // 弓
			cm.gainItem(1462001, 1); // 弩
			
			cm.gainItem(1342000, 1); // 雙刀
			
			cm.gainItem(1522000, 1); // 雙弩
			cm.gainItem(1532000, 1); // 重砲
			cm.gainItem(1352004, 1); // 魔法箭
			
			cm.sendNext("Done.");
			cm.dispose();
		}else if(selection == 6 && cm.getPlayer().getGMLevel() > 3){
			all = cm.getbangbang(cm.getPlayer().getId());
			var text = "請選擇您想回復的道具\r\n#b";
			if(all[0] == null){
				cm.sendOk("您沒有可回復的道具");
				cm.dispose();
				return;
			}
			for(var i = 0; i <  all.length; i++){
				text += "#L" + i + "# #i" + all[i].getItemId() + "# #t" + all[i].getItemId() + "# - #r回復所需楓點 : " + (300000 + (all[i].getEnhance() * 35000) * all[i].getEnhance() + 9487) + "#b#l\r\n";
			}
			cm.sendSimple(text);
		}else if(selection == 7){
			cm.getPayBill(2000);
			cm.dispose();
		}else {
			cm.dispose();
		}
    } else if (status == 2 && cm.getPlayerStat("ADMIN") == 1 && sele != 6) {
		selected = selection - 1;
		var text = "";
		for (var i = 0; i < stats.length; i++) {
			text += "#L" + i + "#" + stats[i] + "#l\r\n";
		}
		cm.sendSimple("You have decided to modify your #b#t" + cm.getInventory(-1).getItem(slot[selected]).getItemId() + "##k.\r\nWhich stat would you like to modify?\r\n#b" + text);
	} else if (status == 3 && cm.getPlayerStat("ADMIN") == 1 && sele != 6) {
		statsSel = selection;
		if (selection == 22) {
			cm.sendGetText("What would you like to set your #b#t" + cm.getInventory(-1).getItem(slot[selected]).getItemId() + "##k's " + stats[statsSel] + " to?");
		} else {
			cm.sendGetNumber("What would you like to set your #b#t" + cm.getInventory(-1).getItem(slot[selected]).getItemId() + "##k's " + stats[statsSel] + " to?", 0, 0, 32767);
		}
    } else if (status == 4 && cm.getPlayerStat("ADMIN") == 1 && sele != 6) {
		cm.changeStat(slot[selected], statsSel, selection);
		cm.sendOk("Your #b#t" + cm.getInventory(-1).getItem(slot[selected]).getItemId() + "##k's " + stats[statsSel] + " has been set to " + selection + ".");
		cm.dispose();
	} else if (status == 10 && cm.getPlayerStat("ADMIN") == 1 && sele != 6) {
		cm.sendSimple("#L3#" + cm.getPotentialInfo(selection) + "#l");
		status = 0;
	} else if(status == 2){
		if(sele == 6){
			sele_index = selection;
			text = "以下為 #i" + all[sele_index].getItemId() + "# #t" + all[sele_index].getItemId() + "# 的資訊#r\r\n";

			text += "此裝備因為 #b#i" + all[sele_index].getGiftFrom() + "# #t"+ all[sele_index].getGiftFrom() + "# #r而炸裝\r\n\r\n";
			text += "星級 	    	: " + all[sele_index].getEnhance() + "#b\r\n";

			all[sele_index].getLevel() != 0?(text += "等級 			: " + all[sele_index].getLevel() + "\r\n"): "";
			all[sele_index].getStr() != 0?text += "力量 			: " + all[sele_index].getStr() + "\r\n": "";
			all[sele_index].getDex() != 0?text += "敏捷 			: " + all[sele_index].getDex() + "\r\n": "";
			all[sele_index].getInt() != 0?text += "智力 			: " + all[sele_index].getInt() + "\r\n": "";
			all[sele_index].getLuk() != 0?text += "幸運 			: " + all[sele_index].getLuk() + "\r\n": "";
			all[sele_index].getHp() != 0?text += "HP   			: " + all[sele_index].getHp() + "\r\n": "";
			all[sele_index].getMp() != 0?text += "MP   			: " + all[sele_index].getMp() + "\r\n": "";
			all[sele_index].getWatk() != 0?text += "攻擊力    	    : " + all[sele_index].getWatk() + "\r\n": "";
			all[sele_index].getMatk() != 0?text += "魔法攻擊力      : " + all[sele_index].getMatk() + "\r\n": "";
			all[sele_index].getWdef() != 0?text += "物理防禦 		: " + all[sele_index].getWdef() + "\r\n": "";
			all[sele_index].getMdef() != 0?text += "魔法防禦		: " + all[sele_index].getMdef() + "\r\n": "";
			all[sele_index].getAcc() != 0?text += "命中 			: " + all[sele_index].getAcc() + "\r\n": "";
			all[sele_index].getAvoid() != 0?text += "迴避 			: " + all[sele_index].getAvoid() + "\r\n": "";
			all[sele_index].getSpeed() != 0?text += "移動速度		: " + all[sele_index].getSpeed() + "\r\n": "";
			all[sele_index].getJump() != 0?text += "跳躍力			: " + all[sele_index].getJump() + "\r\n": "";
			text += "黃金錘			: " + all[sele_index].getViciousHammer() + "\r\n";
			if(all[sele_index].getDurability() == -1)
				text += "耐久度    		: 不會損毀得到具\r\n";
			else
				text += "耐久度 		: " + all[sele_index].getDurability() + "\r\n";

			all[sele_index].getHpR() != 0?text += "生命回復		: " + all[sele_index].getHpR() + "\r\n": "";
			all[sele_index].getMpR() != 0?text += "魔力回復		: " + all[sele_index].getMpR() + "\r\n": "";
			
			if(all[sele_index].getPotential1() <= 0)
				text += "潛能第一行		: 沒有潛能屬性\r\n";
			else
				text += "潛能第一行		: " + cm.getPotentialString(all[sele_index].getPotential1()) + "\r\n";
			if(all[sele_index].getPotential2() <= 0)
				text += "潛能第二行		: 沒有潛能屬性\r\n";
			else
				text += "潛能第二行		: " + cm.getPotentialString(all[sele_index].getPotential2()) + "\r\n";
			if(all[sele_index].getPotential3() <= 0)
				text += "潛能第三行		: 沒有潛能屬性\r\n";
			else
				text += "潛能第三行		: " + cm.getPotentialString(all[sele_index].getPotential3()) + "\r\n";
			
			if(all[sele_index].getOwner() == 0)
				text += ""
			else
				text += "擁有者刻印     	: " + all[sele_index].getOwner() + "\r\n";
			if(all[sele_index].getExpiration() == -1){
				text += "到期時間		: 永久道具\r\n";
			}else{
				var date = new Date(all[sele_index].getExpiration());
				text += "到期時間		: " + date.toString() + "\r\n";
			}
			text += "衝捲剩餘次數	: " + all[sele_index].getUpgradeSlots() + "\r\n";

			text += "您確定要 回復此裝備嗎? - #r需要耗費 #b" + (300000 + (all[sele_index].getEnhance() * 35000) * all[sele_index].getEnhance() + 9487) + "點#r 楓點"
			cm.sendYesNo(text);
		}
	}else if(status == 3){
		if(sele == 6){
			var rq = (300000 + (all[sele_index].getEnhance() * 35000) * all[sele_index].getEnhance() + 9487);
			if(cm.canHold(all[sele_index].getItemId()) && cm.getPlayer().getCSPoints(2) >= rq){
				cm.gainEquipItem(all[sele_index], cm.getPlayer().getClient());
				cm.DeleteBangEquip(all[sele_index].getGMLog());
				cm.getPlayer().modifyCSPoints(2, - rq, true);
				cm.sendOk("已回復!");
			}else{
				cm.sendOk("背包空間不足或楓點不足!");
			}
			cm.dispose();
		}
	}else {
		cm.dispose();
    }
}