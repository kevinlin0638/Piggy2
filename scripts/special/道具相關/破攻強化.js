var status = 0;
//被選擇的裝備列表
var selectedList = Array();
//筛选后的背包裝備列表
var newItemList = Array();
var itemBorder = "#fUI/UIWindow.img/Item/New/inventory/0#";
var itemMaster = "#fUI/UIWindow.img/Item/activeExpChairIcon#"
var itemIcon = "#fUI/Basic.img/Cursor/16/0#";
var cubeIcon = "#fUI/Basic.img/Cursor/16/0#";
var numArr = Array(
        "#fUI/Basic.img/ItemNo/0#",
        "#fUI/Basic.img/ItemNo/1#",
        "#fUI/Basic.img/ItemNo/2#",
        "#fUI/Basic.img/ItemNo/3#",
        "#fUI/Basic.img/ItemNo/4#",
        "#fUI/Basic.img/ItemNo/5#",
        "#fUI/Basic.img/ItemNo/6#",
        "#fUI/Basic.img/ItemNo/7#",
        "#fUI/Basic.img/ItemNo/8#",
        "#fUI/Basic.img/ItemNo/9#"
        );
var btnOk = "#fUI/Basic.img/BtOK/normal/0#";
var btnOk_disabled = "#fUI/Basic.img/BtOK/disabled/0#";
var startIcon = "";
var downIcon = "";
//裝備槽顺序
var selectedPosition = 0;
//标记位
var step = 0;
//成功率
var successRate = 0;
//费用
var cost = 0;
var haveLuck = false;
var useLuck = false;
var sflag = false;
//裝備等级
var grade = Array(
        "★普通★",
        "★精緻★",
        "★無暇★",
        "★罕見★",
        "★純潔★",
        "★完美★",
        "★史詩★"
        );
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (haveLuck && mode == 0) {
            useLuck = false;
            status = 0;
            mode = 1;
        } else if (haveLuck && mode == 1) {
            useLuck = true;
        }
        if (mode == 0 && status == 0) {
            cm.dispose();
            return;
        }
        if (mode == 0 && status == -1) {
            cm.dispose();
            return;
        }
        //如果拥有黄金鱼，并且点了否

        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
			var ii = cm.getItemInfo();
            if (step == 1) {
                //清除副裝備
                if (selectedPosition == 0)
                    selectedList.splice(0, 4);
                //加入到被选裝備列表
                if (selection != -1)
                    selectedList[selectedPosition] = Array(selection, newItemList[selection]);
                //重置标记
                step = 0;
            }
            var text = "#e┌" + itemIcon + "強化裝備   ┐\t┌  " + cubeIcon + "破攻道具   ┐#n\r\n\r\n";
            for (var i = 0; i < 2; i++) {
                if (selectedList[i] != null)
					if(i == 0)
						text += "     #L" + i + "##v" + selectedList[i][1] + "##l         \t\t\t";
					else
						text += "#L" + i + "##v" + selectedList[i][1] + "##l";
                else
					if (i == 0)
						text += "     #L" + i + "#" + itemMaster + "#l         \t\t\t";
					else
						text += "#L" + i + "#" + itemBorder + "#l";
            }
            text += "#e\r\n\r\n\r\n└\t\t\t\t┘\t └\t\t\t\t\t\t┘#n";
            //显示已经選擇的裝備信息
            if (selectedList.length >= 1) {
                text += "#k\r\n#e┌\t\t     ─ 即將強化的裝備信息 ─   \t\t┐#n\r\n\r\n";
				for (var key in selectedList) {
                    var item;
					var max_star;
					if(key == 0){
						item = cm.getInventory(1).getItem(selectedList[key][0]);

						var itemSeq = "#k#n裝備";
						var itemLevelStr = "";
						itemLevelStr = " (+" + breakstr(item.getBreak_dmg()) + " 頂傷)\r\n#b";

						text += "\t" + itemSeq + ": #r#n Lv." + cm.getReqLevel(item.getItemId()) + " #d#e" + cm.getItemName(item.getItemId()) + "#n#r" + itemLevelStr;

						
					}else{
						item = cm.getInventory(4).getItem(selectedList[key][0]);
						text += "\t成功後 #r破攻 " + (item.getItemId() == 4030006? "100萬" : item.getItemId() == 4030007? "1000萬" : "1億");
						text += "\r\n\t\t\t\t\t"+ downIcon + "\r\n\r\n\t#k使用道具 : #v" + item.getItemId() + "# #b#z" + item.getItemId() + "##k( 剩下 #r" + cm.getItemQuantity(item.getItemId()) + " #k顆 )";
					}
				}
                text += "#e\r\n└\t\t\t\t\t\t\t\t\t\t\t┘#n";
					
            }//显示确定按钮
            var lastBtn = btnOk_disabled;
            if (selectedList.length >= 2) {
                lastBtn = btnOk;
            }
            text += "#k\t\t\t\t#L999##d#e" + lastBtn + "#l\r\n\r\n";
            //操作帮助
            text += "#k\r\n#e┌\t\t\t     ─ 操作幫助 ─   \t\t\t┐#n\r\n";
            text += "\t#b" + numArr[1] + " 合成前，請仔細閱讀合成說明。\r\n\t" + numArr[2] + " 第一個位置為想要衝破攻的裝備。\r\n\t#r" + numArr[3] + " 如果主要裝備變動，俄羅斯方塊也需重新選擇。\r\n\t" + numArr[4] + " 選擇裝備時，裝備的排列順序是依據背包裡的順序。\r\n\t" + numArr[5] + " 選擇結束後，點擊“確認”進行衝破功#k";
            text += "#e\r\n└\t\t\t\t\t\t\t\t\t\t\t┘#n";
            cm.sendSimple(text);
        } else if (status == 1) {
			var ii = cm.getItemInfo();
            //裝備合成逻辑运算
            if (sflag)
                selection = 999;
            if (selection == 999) {
                sflag = true;
                if (selectedList.length < 2) {
                    cm.sendPrev("無法衝破攻，請選擇俄羅斯方塊與裝備");
                } else {
					
					var gitem = cm.getInventory(1).getItem(selectedList[0][0]);

					if(!cm.haveItem(selectedList[1][1], 1)){
							cm.sendOk("您的俄羅斯方塊已用盡");
							cm.dispose();
							return;
						}
					if(gitem.getBreak_dmg() >= 2100000000){
							cm.sendOk("您的裝備已達最高破攻!");
							cm.dispose();
							return;
						}
					var dmg = selectedList[1][1] == 4030006? 1000000 : selectedList[1][1] == 4030007? 10000000 : 100000000;
					gitem.setBreak_dmg(gitem.getBreak_dmg() + dmg);
					gitem.setOwner(breakstr(gitem.getBreak_dmg()));
					cm.getPlayer().forceReAddItem_Flag(gitem);
					cm.gainItem(selectedList[1][1], -1);
                    //主裝備信息
					
					text = "\t\t\t#r點選確定繼續衝或結束對話離開\r\n"
					text += "#k\r\n#e┌\t\t     ─ 強化後的裝備信息 ─   \t\t┐#n\r\n\r\n";
                    var item;
					item = cm.getInventory(1).getItem(selectedList[0][0]);

					var itemSeq = "#k#n裝備";
					var itemLevelStr = "";
					itemLevelStr = " (+" + breakstr(item.getBreak_dmg()) + "頂傷)\r\n#b";

					text += "\t" + itemSeq + ": #r#n Lv." + cm.getReqLevel(item.getItemId()) + " #d#e" + cm.getItemName(item.getItemId()) + "#n#r" + itemLevelStr + "\r\n";
					
					text += "\t成功後 #r破攻 " + (selectedList[1][1] == 4030006? "100萬" : selectedList[1][1] == 4030007? "1000萬" : "1億");
					text += "\r\n\t\t\t\t\t"+ downIcon + "\r\n\r\n\t#k使用道具 : #v" + selectedList[1][1] + "# #b#z" + selectedList[1][1] + "##k( 剩下 #r" + cm.getItemQuantity(selectedList[1][1]) + " #k顆 )";
					text += "#e\r\n└\t\t\t\t\t\t\t\t\t\t\t┘#n";

					text += "#k\r\n\t\t\t\t#L999##d#e" + btnOk + "#l\r\n\r\n";
					status = 0;
					cm.sendSimple(text);
                }
            } else {
                //選擇裝備过程
                selectedPosition = selection;
                if (selectedPosition != 0 && selectedList[0] == null) {
                    cm.sendPrev("請先選擇主裝備！");
                } else {
                    inventoryType = 4;
                    text = "#e經過篩選，以下為所有符合強化合成條件的#r副裝備#n\r\n\r\n#b";
                    if (selectedPosition == 0) {
						inventoryType = 1;
                        text = "#e#d請選擇需要進行強化合成的#r主裝備：#n\r\n\r\n#b";
                    }
					var list = cm.getInventory(inventoryType).list();
					var itemList = list.iterator();
                    var indexof = 1;
                    newItemList = new Array();
                    while (itemList.hasNext()) {
                        var item = itemList.next();
                        //cm.getPlayer().dropMessage(0, item)
						//選擇裝備
						
						if(selectedPosition == 0){
						//过滤现金裝備
							if (cm.isCash(item.getItemId()))
								continue;
//                        // 过滤不能参与合成部位
//                        if (getItemType(item.getItemId()) == -1)
//                            continue;
                        //过滤小于50级的裝備
                        /*var getViceReqLevel = cm.getReqLevel(item.getItemId());
                        if (getViceReqLevel < 50)
                            continue;*/
//                        //过滤等级差裝備
//                        if (selectedPosition != 0) {
//                            var getMasterReqLevel = cm.getReqLevel(selectedList[0][1]);
//                            var getMasterGrade = getGrade(selectedList[0][0]);
//                            var getViceGrade = getGrade(item.getPosition());
//                            if (getViceGrade < getMasterGrade)
//                                continue;
//                            var levelDifference = (getMasterReqLevel - getViceReqLevel);
//                            //过滤等级差
//                            if (levelDifference > 10 || levelDifference < -10)
//                                continue;
//                            var getMasterItemType = getItemType(selectedList[0][1]);
//                            //过滤品级
//                            var getViceItemType = getItemType(item.getItemId());
//                            if (getMasterItemType != getViceItemType)
//                                continue;
//                        }
						
							if(!(getItemType(item.getItemId()) == 7) || Math.floor(item.getItemId() / 10000) == 134 || Math.floor(item.getItemId() / 10000) == 135)
								continue;
                        //过滤已选裝備
							var flag = 0;
							for (var key in selectedList) {
								if (item.getPosition() == selectedList[key][0])
								{
									flag = 1;
									break;
								}
							}
							if (flag == 1)
								continue;
							newItemList[item.getPosition()] = item.getItemId();
						}else{//選擇俄羅斯方塊
							if(!(item.getItemId() >= 4030006 && item.getItemId() <= 4030008))
								continue;
							//过滤已选裝備
							var flag = 0;
							for (var key in selectedList) {
								if (item.getPosition() == selectedList[key][0])
								{
									flag = 1;
									break;
								}
							}
							if (flag == 1)
								continue;
							newItemList[item.getPosition()] = item.getItemId();
						}
                    }
                    var xx = 0;
                    for (var key in newItemList) {
                        xx++;
                        text += "#L" + key + "##v" + newItemList[key] + "#";
                        if (indexof > 1 && indexof % 5 == 0) {
                            text += "\r\n";
                        }
                        indexof++;
                    }
                    if (xx <= 0) {
						if(selectedPosition == 0){
							text = "#r您沒有可以使用裝備強化的道具。#k"
						}else{
							text = "#r您沒有可用的俄羅斯方塊。#k"
						}
						step = 0;
						cm.sendPrev(text);
                    }else{
						status = -1;
						step = 1;
						cm.sendSimple(text);
					}
                    
                }
            }
        }
    }
}
//獲取裝備类型
function getItemType(itemid) {
    var type = Math.floor(itemid / 10000);
    switch (type) {
        case 100:
            return 0;  //帽子
        case 104:
            return 1;  //上衣
        case 105:
            return 2;  //套装
        case 106:
            return 3;  //裤裙
        case 107:
            return 4;  //鞋子
        case 108:
            return 5;  //手套
        case 110:
            return 6;  //披风
        default:
            if (type == 120)
                return -1;
            if (type == 135)
                return -1;
            var type = Math.floor(type / 10);
            if (type == 12 || type == 13 || type == 14 || type == 15 || type == 17) {
                return 7;  //武器
            }
            return -1;
    }
}

//獲取裝備品级
function getGrade(equipPosition) {
    if (equipPosition != null) {
        var item = cm.getInventory(1).getItem(equipPosition);
        var itemGrade = item.getOwner();
        if (itemGrade == null || itemGrade == "")
            return 0;
        for (var k in grade) {
            if (itemGrade == grade[k])
                return k;
        }
    }
    return 0;
}

function breakstr(break_dmg) {
	var rs = "";
    if(break_dmg >= 100000000)
		rs += Math.floor(break_dmg/100000000) + "億";
	break_dmg = break_dmg % 100000000
	if(break_dmg > 10000)
		rs += Math.floor(break_dmg/10000) + "萬";
	
	if(rs == "")
		rs = "0";
    return rs;
}