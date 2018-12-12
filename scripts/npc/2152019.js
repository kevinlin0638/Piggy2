function action(mode, type, selection) {
    if (cm.isQuestActive(23005) && cm.haveItem(4032783)) {
        cm.sendNext("你已經貼了一張傳單到布告欄上了。");
        cm.forceStartQuest(23006, "1");
        cm.gainItem(4032783, -1);
    } else {
        cm.sendOk("佈告欄上貼著傳單。");
    }
}