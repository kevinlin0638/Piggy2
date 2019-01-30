
/*
	NPC Name: 		Kimu
	Description: 		Quest - Cygnus tutorial helper
*/

var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 3) {
            qm.sendNext("你確定要拒絕我的任務嗎,有很多經驗窩.");
            qm.safeDispose();
            return;
        }
        status--;
    }
    if (status == 0) {
        qm.sendNext("哈摟 #b#h0##k! 很高興見到你,我已經等妳很久了. 你是來成為 #p1101000# , 對吧? 我的名字是 #p1102004#, 我是來引導貴族們成為.");
    } else if (status == 1) {
        qm.forceStartQuest();
        qm.summonMsg(2);
        qm.dispose();
    }
}

function end(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        qm.sendNext("你是貴族?? 我哥哥 #p1102004# 派來的? 很高興認識你! 我是 #p1102005#. 我將送你 #p1102004# . 請記得，你可以按你的道具欄 #bI 鍵#k. 紅藥水能幫助你恢復HP藍色藥水能幫助你恢復MP，這是一個好主意學習如何使用它們能讓你事先充分了解當你處於危險之中使用。. \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#i2000020# 5 #t2000020# \r\n#i2000021# 5 #t2000021# 5 \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 15 經驗值");
    } else if (status == 1) {
        qm.gainItem(2000020, 5);
        qm.gainItem(2000021, 5);
        qm.forceCompleteQuest();
        qm.gainExp(15);
        qm.summonMsg(3);
        qm.dispose();
    }
}