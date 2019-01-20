var status = 0;
var typed = 0;
var head = "";
var icon = "";
var all;
var sele_index;

function start() {
	status = -1;
	action(1, 0, 0);
}


function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
		return;
	} else {
		if (mode == 0 && status == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1) status++;
		else status--;
		if (status == 0) {
			all = cm.getbangbang(cm.getPlayer().getId());
			var text = "請選擇您想回復的道具\r\n#b";
			if(all[0] == null){
				cm.sendOk("您沒有可回復的道具");
				cm.dispose();
				return;
			}
			for(var i = 0; i <  all.length; i++){
				text += "#L" + i + "# #i" + all[i].getItemId() + "# #t" + all[i].getItemId() + "# - #r回復所需楓點 : " + (300000 + (all[i].getEnhance() * 2000) * all[i].getEnhance() + 9487) + "#b#l\r\n";
			}
			cm.sendSimple(text);
		} else if (status == 1) {
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

            if(all[sele_index].getPotential4() <= 0)
                text += "潛能第行		: 沒有潛能屬性\r\n";
            else
                text += "潛能第四四行		: " + cm.getPotentialString(all[sele_index].getPotential4()) + "\r\n";

            if(all[sele_index].getPotential3() <= 0)
                text += "潛能第五行		: 沒有潛能屬性\r\n";
            else
                text += "潛能第五行		: " + cm.getPotentialString(all[sele_index].getPotential5()) + "\r\n";
			
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

			text += "您確定要 回復此裝備嗎? - #r需要耗費 #b" + (300000 + (all[sele_index].getEnhance() * 2000) * all[sele_index].getEnhance() + 9487) + "點#r 楓點"
			cm.sendYesNo(text);
		} else if (status == 2) {
			var rq = (300000 + (all[sele_index].getEnhance() * 2000) * all[sele_index].getEnhance() + 9487);
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
	}
}

