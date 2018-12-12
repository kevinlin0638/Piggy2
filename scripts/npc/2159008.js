var status = -1;
function start() {
    if (cm.getMapId() == 931000020) {
        action(1, 0, 0);
    } else {
        cm.dispose();
    }
}

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else
        status--;
    if (status == 0) {
        cm.sendNext("哼。好小子，膽敢給我逃跑？");
    } else if (status == 1) {
        cm.sendNextPrevS("啊！被發現了！", 2);
    } else if (status == 2) {
        cm.sendNextPrev("不要掙扎了快投降吧。實驗者想要去哪裡…咦？後面那個小子就算了，你不是實驗者嘛？你是什麼？村莊的人？");
    } else if (status == 3) {
        cm.sendNextPrevS("怎麼樣！我是埃德爾斯坦的居民！", 2);
    } else if (status == 4) {
        cm.sendNextPrev("…小鬼頭們，說了幾次叫你們不要靠近礦山，聽不懂是吧？笨居民…沒辦法，不能讓你回到村莊亂說有關實驗室的事情。要把你抓起來。");
    } else if (status == 5) {
        cm.sendNextPrevS("什麼？誰說要乖乖地給你抓？", 2);
    } else if (status == 6) {
        cm.sendNextPrev("不知好歹…看你可以囂張到什麼時後？");
    } else if (status == 7) {
        cm.sendNextPrevS("#b（被須勒攻擊，體力減半了！該怎麼辦？好像打不贏！）#k", 2);
    } else if (status == 8) {
        cm.sendNextPrev("現在沒有辦法耍嘴皮子了吧？我要建議傑利麥勒給你做更強的實驗。呼呼…乖乖的投降吧！");
    } else if (status == 9) {
        cm.sendNextPrevS("停！", 4, 2159010);
    } else if (status == 10) {
        cm.dispose();
        cm.warp(931000021, 1);
    }
}