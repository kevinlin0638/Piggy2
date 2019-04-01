
importPackage(Packages.server);

var status = -1;
var sel, RecName;
var Inventory = ["身上裝", "裝備欄", "消耗欄", "裝飾欄", "其他欄", "特殊欄"];

function start() {
	cm.sendSimple("請選擇項目\r\n#L0#查詢道具擁角色#l\t#L1#查詢角色擁有物品#l\r\n#L2#查詢所有角色#l");
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0 && status == 0) {
			cm.dispose();
			return;
		}
		if (status == -1 && selection > -1)
			sel = selection;
		if (mode == 1)
			status++;
		else
			status--;
	}
	var i = 0;
	switch (sel) {
		case 0://查詢指定物品
			if (status == i++)//1
				cm.sendGetNumber("請輸入要查詢的物品代碼:", 1000000, 1000000, 10000000);
			else if(status == i++) {
				if (!MapleItemInformationProvider.getInstance().itemExists(selection)) {
					cm.sendOk("#r#e輸入失敗\r\n這項代碼無法使用: " + selection);
					status = -1;
				} else {
					var info = SearchItem(selection);
					var text = "獲得【#b#t"+selection+":##k(#r"+selection+"#k)】玩家如下:";
					if (info['itemid'].length == 0)
						text += "\r\n\r\n\t\t\t\t#r此無玩家獲得";
					for (var key in info['itemid']){ 
						if (key%3 == 0)
							text += "\r\n";
						text += "#b" + info['name'][key] + "x " + info['quantity'][key] + "\t"
					}
					cm.sendOk(text);
					status = -1;
				}
			}
			break;
		case 1://查詢角色道具
			if (status == i++)//1
				cm.sendGetText("請輸入要查詢的玩家名稱:");
			else if(status == i++) {
				RecName = RecName==null?cm.getText():RecName;
				var info = SearchPlayer(RecName);
				var text = "獲得【#r"+RecName+"#k】道具欄如下:#b";
				var first = Array();
				var ratio = 0;
				for (var key in info['itemid']){
					inv = info['inventory'][key]>0?info['inventory'][key]:0;
					if (first.indexOf(inv) == -1) {
						first.push(inv);
						text += "\r\n"+Inventory[inv]+"\r\n";
						ratio = 0;
					}
					ratio += 1;
					if (!MapleItemInformationProvider.getInstance().itemExists(info['itemid'][key]))
						text += "#r#fUI/UIWindow.img/QuestIcon/5/0##k";
					else { 
						text += "#i" + info['itemid'][key] + ":#" + (inv > 1 ? info['quantity'][key] : "");
					}
					if (ratio%5 == 0 && inv > 1 || ratio%9 == 0 && inv < 2)
						text += "\r\n";
				}
				cm.sendOk(text);
				status = -1;
			}
			break;
		case 2://查詢所有角色
			if (status == i++) {
				var text = "請選擇玩家項目:";
				var info = SearchAllId();
				for (var key in info){
					if (key%3 == 0)
						text += "\r\n";
					text += "#L" + key + "#" + info[key] + "#l\t";
				}
				cm.sendOk(text);
			} else if(status == i++) {
				var info = SearchAllId();
				cm.sendOk(SearchAllId()[selection]);
				RecName = SearchAllId()[selection];
				status = 0;
				sel = 1;
			}
			break;
		default:
			cm.sendOk("無法取得該選項 sel: " + sel);
			cm.dispose();
			break;
	}

    
}


function SearchItem(item) {
	var conn = cm.getConnection();
	var ps;
	ps = conn.prepareStatement("SELECT * FROM inventoryitems i, characters c WHERE i.characterid = c.id and itemid = "+item);
	var rs = ps.executeQuery();
	var itemid = Array(), name = Array(), quantity = Array();
	while (rs.next()) {
		itemid.push(rs.getString("itemid"));
		name.push(rs.getString("name"));
		quantity.push(rs.getString("quantity"));
	}
	rs.close();
	ps.close();
	//conn.close();
	var info = new Array();
	info['itemid'] = itemid;
	info['name'] = name;
	info['quantity'] = quantity;
	return info;
}

function SearchPlayer(name) {
	var conn = cm.getConnection();
	var ps;
	ps = conn.prepareStatement("SELECT * FROM inventoryitems i, characters c WHERE i.characterid = c.id and name = '"+name+"'");
	var rs = ps.executeQuery();
	var itemid = Array(), name = Array(), inventory = Array(), quantity = Array();
	while (rs.next()) {
		itemid.push(rs.getString("itemid"));
		inventory.push(rs.getInt("inventorytype"));
		if (name.indexOf(rs.getString("name")) == -1)
			name.push(rs.getString("name"));
		quantity.push(rs.getString("quantity"));
	}
	rs.close();
	ps.close();
	//conn.close();
	var info = new Array();
	info['itemid'] = itemid;
	info['inventory'] = inventory;
	info['name'] = name;
	info['quantity'] = quantity;
	return info;
}

function SearchAllId() {
	var conn = cm.getConnection();
	var ps;
	ps = conn.prepareStatement("SELECT * FROM characters");
	var rs = ps.executeQuery();
	var name = Array()
	while (rs.next()) {
		name.push(rs.getString("name"));
	}
	rs.close();
	ps.close();
	//conn.close();
	return name;
}