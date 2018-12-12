var status = -1;
function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else
        status--;
    if (status == 0) {
        cm.sendNextS("呼…終於擺脫掉了。雖然不覺得會輸給須勒這個傢伙，但卻沒有信心能保護你們。到底為什麼會在那裡？太危險了。村莊的老人沒有跟你們說不要到礦山這邊嗎？", 8);
    } else if (status == 1) {
        cm.sendNextPrevS("對、對不起。#h0#沒有錯，反而還救了我。", 4, 2159007);
    } else if (status == 2) {
        cm.sendNextPrevS("嗯？這樣看來，你…不像是村莊的人。這奇怪的衣服到底是什麼？你該不會是被黑色翅膀抓走吧？", 8);
    } else if (status == 3) {
        cm.sendNextPrevS("#b（斐勒簡單地說明剛才發生的事情。）#k", 4, 2159007);
    } else if (status == 4) {
        cm.sendNextPrevS("…呼…這樣啊…雖然猜測黑色翅膀可能在進行危險的計劃，沒想到是真的…真是可怕，快去通知大家，要想出對策才行。", 8);
    } else if (status == 5) {
        cm.sendNextPrevS("那個…請問你是誰呢？為什麼會突然在那裡出現？還有，為什麼會救我們呢？", 2);
    } else if (status == 6) {
        cm.sendNextPrevS("…這個…你也都長大了，也遇到這樣的事情，相瞞也瞞不了你…好吧，就告訴你。你也知道我們的村莊埃德爾斯坦被黑色翅膀統治的事吧？", 8);
    } else if (status == 7) {
        cm.sendNextPrevS("被搶走的礦山、被控制的議會、監視著的存在……我們村莊的人像奴隸一樣乖乖的聽從他們的命令。但是黑色翅膀再厲害，也沒有辦法統治我們的心。", 8);
    } else if (status == 8) {
        cm.sendNextPrevS("我是末日反抗軍，和隊友一起對抗黑色翅膀的埃德爾斯坦末日反抗軍一員。不能告訴你名字，但可以告訴你我的代號叫 J。現在了解吧？", 8);
    } else if (status == 9) {
        cm.sendNextPrevS("懂了的話，就快回村莊吧，太危險了，不要再跑來這裡。曾是實驗者的這孩子，讓他在這裡有可能再被抓回去，我把他帶回我隊友那裡。在這裡看見我的事要保密，不可以說出去。", 8);
    } else if (status == 10) {
        cm.sendNextPrevS("我可以再問一個問題嗎？我也可以參加末日反抗軍隊嗎？", 2);
    } else if (status == 11) {
        cm.sendNextPrevS("呵…你想也對抗黑色翅膀啊？只要有心，也不是不能加入末日反抗軍。但不是現在，等級十以上，末日反抗軍會先和你連絡。如果到時還想成為隊友的話會有機會再見面的，那就先這樣了。", 8);
    } else if (status == 12) {
        cm.forceCompleteQuest(23007);
        cm.gainItem(2000000, 3);
        cm.gainItem(2000003, 3);
        cm.gainExp(90);
        cm.dispose();
        cm.warp(310000000, 8);
    }
}