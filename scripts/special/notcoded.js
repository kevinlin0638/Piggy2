function start() {
    var info = "對不起，我並沒有被管理員設置可使用，如果您覺得我應該工作的，那就請您回報給管理員.\r\n";
        info += "我的ID編號: #r" + cm.getNpc() + "#k ";
    cm.sendOk(info);
    cm.dispose();
}