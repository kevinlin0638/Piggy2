var status = -1;

function start(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0)
            status -= 2;
        else {
            qm.sendNext("你怕狐狸唷？膽小鬼。別告訴其他人我是你哥哥。");
            qm.dispose();
            return;
        }
    }
    if (status == 0)
        qm.sendAcceptDecline("發生了一件很奇怪的事情。 母雞們行為怪怪的，他們應該生產更多的#t4032451#才對。 你覺得是不是狐狸幹的好事啊？ 如果是的話，我們趕緊做些什麼．．．");
    else if (status == 1) {
        qm.forceStartQuest();
        qm.sendNext("對吧？ 來去擊退這些狐狸吧。 先前往#b#m100030103##k擊敗#r10隻#o9300385##k。 我會在你後面接應你，一起看看發生啥事 ，趕快過去#m100030103#吧！");
    } else if (status == 2) {
        qm.evanTutorial("UI/tutorial/evan/10/0", 1);
        qm.dispose();
    }
}

function end(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0)
            status -= 2;
        else {
            qm.dispose();
            return;
        }
    }
    if (status == 0)
        qm.sendNext("你擊敗狐狸了嗎？");
    if (status == 1)
        qm.PlayerToNpc("#b擊退狐狸後有發生什麼事嗎？");
    if (status == 2)
        qm.sendNextPrev("喔？　我只是追逐他們，確保你不會被#o9300385#或是其他什麼東西給吃掉。");
    if (status == 3)
        qm.PlayerToNpc("#b你確定．．．你不是害怕狐狸而躲在後面嗎？");
    if (status == 4)
        qm.sendNextPrev("什麼？ 我什麼都不怕好不好！");
    if (status == 5)
        qm.PlayerToNpc("#b欸小心！ 有隻#o9300385#在你右邊！！！！");
    if (status == 6)
        qm.sendNextPrev("啊啊啊啊啊  媽咪我好怕~");
    if (status == 7)
        qm.PlayerToNpc("#b．．．");
    if (status == 8)
        qm.sendNextPrev("．．．");
    if (status == 9)
        qm.sendNextPrev("你這個乳臭味乾的小子！ 我是你老哥欸。 請尊重我一點好嗎 你老哥我心臟很脆弱的你知道嗎。 不要那樣嚇我！");
    if (status == 10)
        qm.PlayerToNpc("#b(所以我才不想叫你老哥就是這樣子...)");
    if (status == 11)
        qm.sendNextPrev("算了，不管了 我很高興你擊退了#o9300385#。 給你一個很久以前一個冒險家送我的東西當作禮物。 拿去吧。 \r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0# \r\n#i1372043# 1個 #t1372043# \r\n#i2022621# 25 #t2022621# \r\n#i2022622# 25 #t2022622#s \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 910 exp");
    if (status == 12) {
        qm.forceCompleteQuest();
        qm.gainItem(1372043, 1);
        qm.gainItem(2022621, 25);
        qm.gainItem(2022622, 25);
        qm.gainExp(910);
        qm.sendNext("#b這把短仗是法師用的武器#k。你可能用不到，但是帶在身上可以讓你看起來比較不邊緣，呵呵呵呵...");
    }
    if (status == 13) {
        qm.sendPrev("總之，狐狸的數量增加了，對吧？真奇怪，為什麼一天比一天還多？我們應該好好檢視一下有沒有異音。");
        qm.dispose();
    }
}