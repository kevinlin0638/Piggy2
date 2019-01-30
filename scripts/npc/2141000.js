/*
 * Time Temple - Kirston
 * Twilight of the Gods
 */

function start() {
    cm.sendOk("如果我有善良之鏡,我就能召喚黑魔法師!\r\n等等!好像哪裡錯了!為什麼召喚不了黑魔法師?我感覺到跟黑魔法師完全不同的……啊啊啊!!!!!!!\r\n\r\n #b(請把奇拉的使命傳遞下去.)");
}

function action(m, t, s) {
    if (m > 0) {
		cm.removeNpc(270050100, 2141000);
		cm.forceStartReactor(270050100, 2709000);
    }
    cm.dispose();
}