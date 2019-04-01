var status;
var event_name = '收集活動3月';
var event_tiem = 4032056;
var ye = "#fUI/Basic.img/icon/arrow#";
var lu = "#fEffect/CharacterEff/1032053/0/0#";
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
			if(cm.getPlayer().getGuildId() <= 0){
				cm.sendOk("您沒有加入公會!");
				cm.dispose();
				return;
			}
			text = ye+'#k 目前公會排名: #r' + cm.getGuildRank();
			if(cm.getPlayer().getGuild().getLastMrk() > 0)
				text += '\r\n' + ye+ '#k 公會上月排名: #r' + cm.getPlayer().getGuild().getLastMrk();
			text += '\r\n' + ye + '#k 目前公會總GP: #r' + cm.getGP();
			text += '\r\n' + ye + '#k 目前個人累積GP: #r' + cm.getPlayer().getGpcon();
			
			text += '\r\n\r\n#d請問您需要什麼服務？#b\r\n#L0#兌換道具#l#b\r\n'/*#L2#本月獎勵預覽#l\r\n'*/;
			if(cm.getPlayer().getGuild().getLastMrk() > 0)
				text += '#L1#領取公會上月排名獎勵(第 ' + cm.getPlayer().getGuild().getLastMrk() + ' 名)#l\r\n';
			text += '#L3#說明#l'
			cm.sendSimple(text);
            break;
        case 1: //
			switch(selection){
				case 0:
					cm.dispose();
					cm.openNpc(2001000, "活動/公會系統/兌換道具");
					break;
				case 1:
					cm.dispose();
					cm.openNpc(2001000, "活動/公會系統/上月獎勵領取");
					break;
				case 2:
					cm.dispose();
					cm.openNpc(2001000, "活動/公會系統/獎勵預覽");
					break;
				case 3:
					text = "說明如下:\r\n"
					text += "#r每月#k將會以公會的GP做為排名，獲取豐盛的獎勵。#b(領獎成員的個人貢獻GP須達到5000)\r\n\r\n";
					text += lu + lu + lu + lu + lu + lu + lu + lu + lu + lu + lu + lu + lu + "\r\n";
					text += "獲得GP的方式如下:\r\n"
					text += "#b1.購買公會技能 - #r30點/1技能(不會算入個人貢獻)\r\n\r\n#b2.BOSS 擊殺(BOSS傳送裡面) - \r\n#r炎魔 - 100點\r\n雄王 - 130點\r\n獅王 - 130點\r\n龍王 - 150點\r\n皮卡 - 180點\r\n凡雷恩 - 180點\r\n阿卡伊濃- 200點\r\n西格諾斯- 230點\r\n#b渾沌BOSS 點數兩倍\r\n\r\n#b3.每日任務 - #r1個 50點\r\n\r\n#b4.蒐集任務 - #r個人累積達到進度點 100點(領取獎勵時獲得)\r\n\r\n#b5.武陵道場 - #r\r\n完成困難一場 10 點(不會算入個人貢獻)\r\n完成夢魘一場 20 點(不會算入個人貢獻)\r\n\r\n#b6.每小時在線 - #r可獲得 20點\r\n\r\n#b7.每日開精靈商人 - #r50 點"
					cm.sendOk(text);
					status = -1;
			}
            break;
        case 2:
            cm.dispose();
            break;
    }
}
