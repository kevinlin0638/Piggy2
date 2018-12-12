var status = -1;
function action(mode, type, selection) {
    if (mode == 1) 
        status++;
    else 
	status--;
    if (status == 0) {
    	cm.sendNextS("遲到了啦， #h0#！快過來這裡！", 8);
    } else if (status == 1) {
	cm.sendNextPrevS("為什麼這麼慢？以前不是說好要來這裡玩了嗎！ 不會是害怕了吧？", 4, 2159002);
    } else if (status == 2) {
	cm.sendNextPrevS("別傻了，我才沒有害怕。", 2);
    } else if (status == 3) {
	cm.sendNextPrevS("真的嗎？ 但是我好害怕哦…老人家們不是有警告說不要來#b雷朋礦山#k這裡玩嗎。有#r黑色翅膀的壞人們#k守在這裡…", 4, 2159000);
    } else if (status == 4) {
	cm.sendNextPrevS("所以才故意避開沒有監視者的路，來到這裡的啊。不趁現在的話我們什麼時候可以從#b埃爾德斯坦#k 裡跑出來玩？ 真是的，膽小鬼欸！！", 4, 2159002);
    } else if (status == 5) {
	cm.sendNextPrevS("但是…被罵怎麼辦？", 4, 2159000);
    } else if (status == 6) {
	cm.sendNextPrevS("都已經來到這裡了，還可以怎麼辦。反正都會被罵了，我們玩玩再回去吧，我們來玩捉迷藏！", 8);
    } else if (status == 7) {
	cm.sendNextPrevS("咦？捉迷藏！", 2);
    } else if (status == 8) {
	cm.sendNextPrevS("真幼稚…", 4, 2159002);
    } else if (status == 9) {
	cm.sendNextPrevS("什麼幼稚！ 在這裡還可以玩什麼？說來聽聽啊！還有你當鬼，#h0#！因為你遲到啊。哈，那我們要躲了哦。數到十後開始找！", 8);
    } else if (status == 10) {
	cm.warp(931000001, 1);
    	cm.dispose();
    }
}
