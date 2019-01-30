
/* 
 *  NPC  : Guide Summoner
 *  Maps : Erev Map of the Start // 20021
 */

var status = -1;

function start() {
    cm.sendSimple("這些事情你必須都得曉得的\r\n好了，你想要知道想一項事情？？  \n\r #b#L0#告訴我更多關於你的事情。#l \n\r #b#L1#小地圖介紹。#l \n\r #b#L2#如何打開任務視窗。#l \n\r #b#L3#如何打開道具欄。#l \n\r #b#L4#如何攻擊。#l \n\r #b#L5#如何撿道具。#l \n\r #b#L6#如何穿裝備。#l \n\r #b#L7#技能視窗。#l \n\r #b#L8#如何把技能放到快捷鍵上。#l \n\r #b#L9#如何打破箱子。#l \n\r #b#L10#如何坐椅子。#l \n\r #b#L11#如何查看世界地圖。#l \n\r #b#L12#什麼是皇家騎士團。#l");
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
    }
    if (status == 0) {
        if (selection == 0) {
            cm.sendNext("你好我是提酷！");
        } else if (selection == 12) {
            cm.sendOk("皇家騎士團就是皇家騎士團。");
            cm.dispose();
        } else {
            cm.summonMsg(selection);
            cm.dispose();
        }
    } else if (status == 1) {
        cm.sendNext("很高興認識你。");
        cm.dispose();
    }
}