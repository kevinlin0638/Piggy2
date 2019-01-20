var status = -1; 
var selected = 0; 
var ttt = "#fUI/UIWindow/Quest/icon2/7#"; 
var ico = "#fEffect/CharacterEff/1112905/0/1#";  //ICO美化圖标 
var nxtofy = 1100000;  //兌換1個金牌需要的楓點數量 
var itemtonx = 1;  //兌換1000萬楓點需要的金牌數量 
var tonx2 = 1000000;  //1個金牌兌換來的楓點數量 
var item = 4310003;  //金牌
 
 
 
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
    text +=("\r\n#n#b#L0##v4310003##z4310003# >>> 楓點(1 : #r100萬#b) \t #r[兌換]"); 
    text +=("\r\n#n#b#L1#楓點 >>> #v4310003##z4310003#(#r110萬#b : 1) \t#r[兌換]#l\r\n\r\n"); 
    text +=(I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I); 
	text +=("\r\n\t\t\t\t#L3##b" + ttt + " 返回上一頁#k#l\r\n\r\n"); 
    cm.sendNextPrev(text); 
  } else if (status == 1){ 
    if (selection == 1) { 
      if (cm.getNX(2) < nxtofy) { 
        cm.sendOk("您的楓點不足，無法進行兌換！"); 
        cm.dispose(); 
        return; 
      } else { 
        selected = 2; 
        text = "\r\n#d剩餘楓點：#r" + cm.getNX(2) + " 點\t\t#b可兌換"+Math.floor(cm.getNX(2)/nxtofy)+"金牌"; 
        text+= "\r\n#d剩餘金牌：#r" + cm.getItemQuantity(item) + " 個"; 
        text+= "\r\n#d兌換比例：#r110萬 楓點 = 1 個 #v4310003##z4310003#"; 
        text+= "\r\n\r\n#r請輸入你要得到的#e金牌數量：" 
        cm.sendGetNumber(text , 1, 1, cm.getNX(2)/nxtofy); 
      } 
    //金牌換楓點 
    }else if (selection == 0) { 
      if (cm.getItemQuantity(item) < itemtonx) { 
        cm.sendOk("您的#v"+item+"#不足，無法進行兌換！"); 
        cm.dispose(); 
        return; 
      } else { 
        selected = 3; 
        text = "\r\n#d剩餘楓點：#r" + cm.getNX(2) + " 點"; 
        text+= "\r\n#d剩餘金牌：#r" + cm.getItemQuantity(item) + " 個\t\t#b可兌換"+cm.getItemQuantity(item)*tonx2+"楓點"; 
        text+= "\r\n#d兌換比例：#r1 個 #v4310003##z4310003# = 100萬 楓點"; 
        text+= "\r\n\r\n#r請輸入你要兌換成楓點的#e金牌數量：" 
        cm.sendGetNumber(text , 1, 1, cm.getItemQuantity(item)); 
      } 
    }else if (selection == 3) { 
      cm.dispose(); 
      cm.openNpc(9330003, "貨幣兌換"); 
    } 
    //贊助點成功兌換楓點 
  } else if (status == 2){ 
    if (selected == 2){ 
	  if(cm.canHold(4310003, 1)){
		  cm.gainNX(2,-selection * nxtofy); 
		  cm.gainItem(item,selection); 
		  cm.sendOk("#b您成功用#r "+ (selection*nxtofy) +" #b楓點兌換了#r "+selection+" #b個 #v4310003##z4310003#"); 
		  cm.dispose(); 
	  }else{
		  cm.sendOk("#b您的背包空間不足!"); 
		  cm.dispose(); 
	  }
    //金牌成功兌換楓點 
	} else if (selected == 3){ 
      cm.gainItem(item,-selection); 
      cm.gainNX(2,selection * tonx2); 
      cm.sendOk("#b您成功用#r "+selection+" #b個#v4310003##z4310003#兌換了#r "+(selection*tonx2)+" #b楓點"); 
      cm.dispose(); 
    } else { 
      cm.sendOk("未知錯誤"); 
      cm.dispose(); 
      return; 
    } 
  } 
 
}