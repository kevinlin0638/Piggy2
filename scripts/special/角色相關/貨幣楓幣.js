var status = -1; 
var selected = 0; 
var ttt = "#fUI/UIWindow/Quest/icon2/7#"; 
var ico = "#fEffect/CharacterEff/1112905/0/1#";  //ICO美化圖标
var nxtofy = 100;  //兌換1個金幣需要的贊助點數量 
var itemtonx = 1;  //兌換100贊助點需要的金幣數量 
var tonx2 = 100;  //1個金幣兌換來的贊助點數量 
var item = 4310005;  //金幣 
 
 
 
function start () { 
  action(1,0,0); 
} 
function action (mode,type,selection) { 
  if (mode == -1 || mode == 0 && status == 0) { 
    cm.dispose(); 
    return; 
  } else { 
    if (mode == 1) { 
      status++; 
    } else { 
      status--; 
    } 
  } 
   
  if (status == 0) { 
    var I = ico; 
    text = (I+I+I+I+I+I+I+I+I+I+I+"#e  金融中心  #n"+I+I+I+I+I+I+I+I+I+I+I); 
    text +=("\r\n\r\n\t\t\t\t#r請選擇您要使用兌換的方案#k\r\n"); 
    text +=("\r\n#n#b#L0##v4310005##z4310005# >>> 贊助點(1 : #r100#b) \t #r[兌換]"); 
    text +=("\r\n#n#b#L1#贊助點 >>> #v4310005##z4310005#(#r100#b : 1) \t#r[兌換]#l\r\n\r\n"); 
    text +=(I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I); 
    text +=("\r\n\t\t\t\t#L3##b" + ttt + " 返回上一頁#k#l\r\n\r\n"); 
    cm.sendNextPrev(text); 
  } else if (status == 1){ 
    if (selection == 1) { 
      if (cm.getPlayer().getPoints() < nxtofy) { 
	  cm.sendOk("您的贊助點不足，無法進行兌換！"); 
        cm.dispose(); 
        return; 
      } else { 
        selected = 2; 
        text = "\r\n#d剩餘贊助點：#r" + cm.getPlayer().getPoints() + " 點\t\t#b可兌換"+Math.floor(cm.getPlayer().getPoints()/nxtofy)+"金幣"; 
        text+= "\r\n#d剩餘金幣：#r" + cm.getItemQuantity(item) + " 個"; 
        text+= "\r\n#d兌換比例：#r100 贊助點 = 1 個 #v4310005##z4310005#"; 
        text+= "\r\n\r\n#r請輸入你要得到的#e金幣數量：" 
        cm.sendGetNumber(text , 1, 1, cm.getPlayer().getPoints()/nxtofy); 
      } 
    //金幣換贊助點 
    }else if (selection == 0) { 
      if (cm.getItemQuantity(item) < itemtonx) { 
        cm.sendOk("您的#v"+item+"#不足，無法進行兌換！"); 
        cm.dispose(); 
        return; 
      } else { 
        selected = 3; 
        text = "\r\n#d剩餘贊助點：#r" + cm.getPlayer().getPoints() + " 點"; 
        text+= "\r\n#d剩餘金幣：#r" + cm.getItemQuantity(item) + " 個\t\t#b可兌換"+cm.getItemQuantity(item)*tonx2+"贊助點"; 
        text+= "\r\n#d兌換比例：#r1 個 #v4310005##z4310005# = 100 贊助點"; 
        text+= "\r\n\r\n#r請輸入你要兌換成贊助點的#e金幣數量：" 
        cm.sendGetNumber(text , 1, 1, cm.getItemQuantity(item)); 
      } 
    }else if (selection == 3) { 
      cm.dispose(); 
      cm.openNpc(9330003, "貨幣兌換"); 
    } 
    //贊助點成功兌換贊助點
  } else if (status == 2){ 
    if (selected == 2){ 
	  if(cm.canHold(4310005, 1)){
		  cm.getPlayer().gainPoints(-selection * nxtofy); 
		  cm.gainItem(item,selection); 
		  cm.sendOk("#b您成功用#r "+ (selection*nxtofy) +" #b贊助點兌換了#r "+selection+" #b個 #v4310005##z4310005#"); 
		  cm.dispose(); 
	  }else{
		  cm.sendOk("#b您的背包空間不足!"); 
		  cm.dispose(); 
	  }
    //金幣成功兌換贊助點 
    } else if (selected == 3){ 
      cm.gainItem(item,-selection); 
      cm.getPlayer().gainPoints(selection * tonx2); 
      cm.sendOk("#b您成功用#r "+selection+" #b個#v4310005##z4310005#兌換了#r "+(selection*tonx2)+" #b贊助點"); 
      cm.dispose(); 
	  } else { 
      cm.sendOk("未知錯誤"); 
      cm.dispose(); 
      return; 
    } 
  } 
 
}