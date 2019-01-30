/*
	NPC Name: 		Cygnus
	Description: 		Quest - Encounter with the Young Queen
*/

var status = -1;

function start(mode, type, selection) {

    if (mode == 1) {
        status++;
    } else {
        if (status == 2) {
            qm.sendNext("Hmm, there is nothing to worry about. This will be a breeze for someone your level. Muster your courage and let me know when you're ready.");
            qm.safeDispose();
            return;
        }
        status--;
    }
    if (status == 0) {
        qm.sendNext("嗯？是#p1101002#派你來的？啊哈！看來是本次新來的騎士嘛！？歡迎，歡迎，我叫#p1102000#。我是教你們這些貴族的修煉教官。哦…為什麼>      這麼看我…啊，看來你是第一次看到皮妖嘛。");
    } else if (status == 1) {
        qm.sendNextPrev("我們是叫皮妖的種族。與小女皇旁邊的#p1101001#對話過嗎？皮妖跟#p1101001#是同一個種族。雖然系列不同…但基本差不多。只生活在耶雷弗>      ，很快你也會習慣的。");
    } else if (status == 2) {
        qm.sendNextPrev("啊，你知道嗎？在耶雷弗裡沒有怪物。帶有邪惡氣息的東西是無法生存在耶雷弗裡。但不用擔心，用#p1101001#製造的幻想生物「提提」修煉就>      可以，那麼開始吧。");
    } else if (status == 3) {
        qm.askAcceptDecline("修煉的還不錯嘛！那麼…看你的水準應該能消滅比較強的提提了。去消滅#m130010100#裡的#o0100122# 15隻吧，怎麼樣？能抓到#o0100122#吧？");
    } else if (status == 4) {
        qm.summonMsg(12);
        qm.forceCompleteQuest(20003);
        qm.forceStartQuest();
        qm.dispose();
    }
}

function end(mode, type, selection) {}
