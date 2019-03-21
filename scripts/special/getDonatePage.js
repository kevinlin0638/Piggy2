/*伤害皮肤*/
var status = 0;
var choice;
var type;
var get_type;
var set_type;
var getDate;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) cm.dispose();
    else {
        if (status == 0 && mode == 0) {
            cm.dispose();
            return;
        } else if (status >= 1 && mode == 0) {
            cm.sendOk("好吧，歡迎下次再次光臨！.");
            cm.dispose();
            return;
        }
        if (mode == 1) status++;
        else status--;
        if (status == 0) {
            var text = "請輸入您要(贊助)抖內的金額\r\n#r超過萬元將無法使用超商繳款!";
			cm.sendGetNumber(text,100,100,50000);
        } else if (status == 1) {
			choice = selection;
			getDate = cm.getPayBill(choice);
		    cm.sendYesNo("您總共要贊助的金額為 #r"+choice + "#k 臺幣\r\n\r\n#b實際獲得贊助點數為 : #r"+ cm.getRealDonate(choice)+ " #b贊助點\r\n#r注意:付款完成會自動進入帳號，且會在聊天欄通知\r\n如果使用超商條碼附款,將會有兩至三天的工作天, 請斟酌使用\r\n贊助為個人對於本伺服器的支持,無任何利益交換關係\r\n#k請按是繼續");
        } else if (status == 2) {
			cm.openWeb("http://35.220.252.54:80/"+ getDate);
			cm.sendOk("已為您開啟瀏覽器\r\n#r若瀏覽器尚未開啟請至登入器複製網址");
			cm.dispose();
        }
    }
}
