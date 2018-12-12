var status = -1;

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else
        status--;
    if (status == 0) {
        cm.sendNext("嗯…實驗似乎進行的相當順利，順利的拿到露。和黑色翅膀合作果然是明智之舉…呵呵呵");
    } else if (status == 1) {
        cm.sendNextPrevS("傑利麥勒果然有先見之明。", 4, 2159008);
    } else if (status == 2) {
        cm.sendNextPrev("黑色翅膀無法挑剔的機器人，就快要完成了。現在實驗要開始下一個階段了。比他們時候的還要有趣。");
    } else if (status == 3) {
        cm.sendNextPrevS("下一個階段呢？", 4, 2159008);
    } else if (status == 4) {
        cm.sendNextPrev("呼呼…到現在還不知道嗎？光看這個實驗室就應該會知道，我現在要製造什麼東西。只製造及其不夠好玩，比機器人還有趣的…");
    } else if (status == 5) {
        cm.sendNextPrevS("嗯？這實驗室嗎？你打算對這實驗者做什麼事嗎？", 4, 2159008);
    } else if (status == 6) {
        cm.sendNextPrev("什麼，我能了解在你眼中，看不見這實驗室偉大的地方。至於你呢！只要把你的任務做好就行了。顧好在這裡的沒一個實驗者，讓他們沒辦法逃跑就行了。");
    } else if (status == 7) {
        cm.sendNextPrev("…嗯？有沒有聽到什麼奇怪的聲音？");
    } else if (status == 8) {
        cm.sendNextPrevS("嗯？奇怪的聲音？這樣一說，好像有什麼…？", 4, 2159008);
    } else if (status == 9) {
        cm.updateInfoQuest(23007, "vel00=2;vel01=1");
        cm.trembleEffect(0, 500);
        cm.lockUI(true);
        cm.lockKey(true);
        cm.showWZEffect("Effect/Direction4.img/Resistance/TalkInLab");
        cm.dispose();
    }
}