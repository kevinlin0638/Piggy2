var status = -1;
function action(mode, type, selection) {
    status++;
    if (cm.getInfoQuest(23999).indexOf("exp4=1") != -1) {
	if (status == 0)
	   cm.sendNext("居然找到我了...");
        else
	   cm.dispose();
    } else {
        if (status == 0) {
    	    cm.sendNext("嗚？被發現了？我嬌小的身軀這麼容易找到，真是不簡單喔。\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 3 exp");
        } else if (status == 1) {
            cm.gainExp(3);
            if (cm.getInfoQuest(23999).equals("")) {
                cm.updateInfoQuest(23999, "exp4=1");
            } else {
                cm.updateInfoQuest(23999, cm.getInfoQuest(23999) + ";exp4=1");
            }
            cm.dispose();
        }
    }
}
