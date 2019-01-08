/**
 * @author: Eric
 * @rev: 1.1 - Lithium Support for Maple Ascension
 * @desc: Auto-Tip for v1.17.2
*/

var setupTask;
var serverName = "小豬谷公告"

function init() {
    scheduleNew();
}

function scheduleNew() {
    var cal = java.util.Calendar.getInstance();
    cal.set(java.util.Calendar.HOUR, 0);
    cal.set(java.util.Calendar.MINUTE, 0);
    cal.set(java.util.Calendar.SECOND, 0);
    var nextTime = cal.getTimeInMillis();
    while (nextTime <= java.lang.System.currentTimeMillis())
        nextTime += 900 * 1000; //420 * 1000 = 7minutes
    setupTask = em.scheduleAtTimestamp("start", nextTime);
}

function cancelSchedule() {
    setupTask.cancel(true);
}

function start() {
    scheduleNew();
    var Message = new Array("歡迎加入 " + serverName + ", 希望您能開心遊玩!", "您知道我們的倍率是  3x 經驗 1x 楓幣 1x 掉寶?", "您知道 " + serverName + " 支援 Windows 8, 10 嗎!", "如果想要看所有指令, 請輸入 @help!", "您可以在每6個小時至 ", "請勿自行修改 wz.", "請多利用 @save 來避免回朔!", "請勿使用外掛軟體!", "使用第三方軟體或卡鍵會被警告,嚴重者會進行封鎖,請愛惜自身帳號", "Donate 比目前為 1:1.5. 贊助者可使用贊助點數在遊戲內換取獎勵窩!");
    em.getChannelServer().yellowWorldMessage("[" + serverName + "] " + Message[Math.floor(Math.random() * Message.length)]);
}
