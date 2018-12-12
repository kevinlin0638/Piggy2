var status = -1;
function action(mode, type, selection) {
    if (mode == 1) 
        status++;
    else 
    status--;
    if (cm.getPlayer().getMapId() == 931000011 || cm.getPlayer().getMapId() == 931000030) {
    cm.dispose();
    return;
    }
    if (cm.getInfoQuest(23007).indexOf("vel01=2") == -1 && cm.getInfoQuest(23007).indexOf("vel01=3") == -1) {
        if (status == 0) {
            cm.sendNext("…哦，哦？怎麼搞的？剛剛那個震動的關係，把玻璃變脆弱了？破掉了？");
        } else if (status == 1) {
        cm.sendNextPrevS("哈，現在沒東西阻擋你了吧？那麼我們走吧！", 2);
        } else if (status == 2) {
            cm.sendNext("但、但是…");
        } else if (status == 3) {
        cm.sendNextPrevS("難道你想一直在這裡？", 2);
        } else if (status == 4) {
            cm.sendNextPrev("當然不是啊！我不想要呆在實驗室裡！");
        } else if (status == 5) {
        cm.sendNextPrevS("那就一起走吧！快點！", 2);
        } else if (status == 6) {
        cm.warp(931000020,1);
        cm.updateInfoQuest(23007, "vel00=2;vel01=2");
        cm.dispose();
    }
    } else if (cm.getInfoQuest(23007).indexOf("vel01=2") != -1 && cm.getInfoQuest(23007).indexOf("vel01=3") == -1) {
        if (status == 0) {
            cm.sendNext("已、已經好久沒有走出實驗室了…這是哪裡啊？");
        } else if (status == 1) {
        cm.sendNextPrevS("去我們的村莊，埃德爾斯坦的路上。趁那奇怪的黑色翅膀追上來前，我們快點逃跑吧！", 2);
        } else if (status == 2) {
        cm.updateInfoQuest(23007, "vel00=2;vel01=3");
        cm.ShowWZEffect("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow1");
        cm.dispose();
    }
    } else {
    cm.sendOk("已經好久沒有走出實驗室了…");
        cm.dispose();
    }
}