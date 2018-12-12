var status = -1;
var answer = false;
function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 5) {
            cm.sendNext("#b（本來不打算要下手的，突然手一滑！）#k");
            return;
        }
        status--;
    }
    if (cm.getPlayer().getMapId() == 931000011) {
        cm.dispose();
        return;
    }

    if (cm.getInfoQuest(23007).indexOf("vel00=1") == -1 && cm.getInfoQuest(23007).indexOf("vel01=1") == -1) {
        if (status == 0) {
            cm.sendNext("不可以再靠近了…！");
        } else if (status == 1) {
            cm.sendNextPrev("怎麼會來這裡？這裡是禁止出入的地方。");
        } else if (status == 2) {
            cm.sendNextPrevS("你是誰？！", 2);
        } else if (status == 3) {
            cm.sendNextPrev("我…我這裡，往上看。");
        } else if (status == 4) {
            cm.updateInfoQuest(23007, "vel00=1");
            cm.showWZEffect("Effect/Direction4.img/Resistance/ClickVel");
            cm.dispose();
        }
    } else if (cm.getInfoQuest(23007).indexOf("vel00=1") != -1 && cm.getInfoQuest(23007).indexOf("vel01=1") == -1) {
        if (status == 0) {
            cm.sendNext("我是…　#r傑利麥勒博士#k的實驗者。我叫作#b斐勒#k… 雖然不知道你們怎麼跑進來的，快點出去！要是被博士發現的話，就完蛋了！");
        } else if (status == 1) {
            cm.sendNextPrevS("實驗者？傑利麥勒？到底在說什麼啊？這裡到底是什麼地方？你為什麼要進去裡面啊？", 2);
        } else if (status == 2) {
            cm.sendNextPrev("你不知道傑利麥勒？ 傑利麥勒博士… 黑色翅膀的瘋狂科學家！這裡是傑利麥勒的研究室，傑利麥勒在這裡盡心人體實驗…");
        } else if (status == 3) {
            cm.sendNextPrevS("人體…實驗？", 2);
        } else if (status == 4) {
            cm.sendNextPrev("對，人體實驗，你如果被抓到，說不定也會變成實驗品！快逃跑！");
        } else if (status == 5) {
            cm.sendNextPrevS("什麼？逃、逃跑…？但是你…！", 2);
        } else if (status == 6) {
            cm.sendNextPrev("…噓！小聲一點！傑利麥勒博士來了。");
        } else if (status == 7) {
            cm.updateInfoQuest(23007, "vel00=2");
            cm.dispose();
            cm.warp(931000011, 0);
        }
    } else if (cm.getInfoQuest(23007).indexOf("vel01=1") != -1) {
        if (status == 0) {
            cm.sendNext("好險…傑利麥勒好像有事出去了…快，就趁現在，你快點走吧。");
        } else if (status == 1) {
            cm.sendNextPrevS("我一個人逃走嗎？那你呢？", 2);
        } else if (status == 2) {
            cm.sendNext("我沒有辦法逃走。傑利麥勒博士記得自己實驗過的所有東西，只要少一個，馬上就會發現的…所以你快走吧。");
        } else if (status == 3) {
            cm.sendNextPrevS("不可以！你也跟我一起走！", 2);
        } else if (status == 4) {
            cm.sendNext("就說不可能了，更何況我…被關在這裡面。想要逃也逃不了…謝謝你為我操心。好久沒有人這麼關心我了。快，快去吧。");
        } else if (status == 5) {
            cm.sendYesNo("#b（斐勒把眼睛閉了起來，就像放棄了一切，該怎麼辦？去關斐勒的實驗室看看！）#k");
        } else if (status == 6) {
            cm.gainExp(60);
            cm.dispose();
            cm.warp(931000013, 0);
        }
    }
}