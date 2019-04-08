var status = -1; 
var selected = 0; 
var ttt = "#fUI/UIWindow/Quest/icon2/7#"; 
var ico = "#fEffect/CharacterEff/1112905/0/1#";  //ICO美化圖标 
var nxtofy = 110000000;  //兌換1個金幣袋需要的楓幣數量 
var itemtonx = 1;  //兌換10億楓幣需要的金幣袋數量 
var tonx2 = 100000000;  //1個金幣袋兌換來的楓幣數量 
var item = 4310004;  //金幣袋
 
 
 
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
    text +=("\r\n#n#b#L0##v4310004##z4310004# >>> 楓幣(1 : #r1億#b) \t #r[兌換]"); 
    text +=("\r\n#n#b#L1#楓幣 >>> #v4310004##z4310004#(#r1.1億#b : 1) \t#r[兌換]#l\r\n\r\n"); 
    text +=(I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I+I); 
    text +=("\r\n\t\t\t\t#L3##b" + ttt + " 返回上一頁#k#l\r\n\r\n"); 
    cm.sendNextPrev(text); 
  } else if (status == 1){ 
    if (selection == 1) { 
      if (cm.getMeso() < nxtofy) { 
        cm.sendOk("您的楓幣不足，無法進行兌換！"); 
		cm.dispose(); 
        return; 
      } else { 
        selected = 2; 
        text = "\r\n#d剩餘楓幣：#r" + cm.getMeso() + " 點\t\t#b可兌換"+Math.floor(cm.getMeso()/nxtofy)+"金幣袋"; 
        text+= "\r\n#d剩餘金幣袋：#r" + cm.getItemQuantity(item) + " 個"; 
        text+= "\r\n#d兌換比例：#r11億 楓幣 = 1 個 #v4310004##z4310004#"; 
        text+= "\r\n\r\n#r請輸入你要得到的#e金幣袋數量：" 
        cm.sendGetNumber(text , 1, 1, cm.getMeso()/nxtofy); 
      } 
    //金幣袋換楓幣 
    }else if (selection == 0) { 
      if (cm.getItemQuantity(item) < itemtonx) { 
        cm.sendOk("您的#v"+item+"#不足，無法進行兌換！"); 
        cm.dispose(); 
        return; 
      } else { 
        selected = 3; 
        text = "\r\n#d剩餘楓幣：#r" + cm.getMeso() + " 點"; 
        text+= "\r\n#d剩餘金幣袋：#r" + cm.getItemQuantity(item) + " 個\t\t#b可兌換"+cm.getItemQuantity(item)*tonx2+"楓幣"; 
        text+= "\r\n#d兌換比例：#r1 個 #v4310004##z4310004# = 10億 楓幣"; 
        text+= "\r\n\r\n#r請輸入你要兌換成楓幣的#e金幣袋數量：" 
        cm.sendGetNumber(text , 1, 1, cm.getItemQuantity(item)); 
      } 
    }else if (selection == 3) { 
      cm.dispose(); 
      cm.openNpc(9330003, "貨幣兌換"); 
    } 
    //贊助點成功兌換楓幣
  } else if (status == 2){ 
    if (selected == 2){ 
		  if(cm.canHold(4310004, 1)){
		  cm.gainMeso(-selection * nxtofy); 
		  cm.gainItem(item,selection); 
		  cm.sendOk("#b您成功用#r "+ (selection*nxtofy) +" #b楓幣兌換了#r "+selection+" #b個 #v4310004##z4310004#"); 
		  cm.dispose(); 
	  }else{
		  cm.sendOk("#b您的背包空間不足!"); 
		  cm.dispose(); 
	  }
    //金幣袋成功兌換楓幣 
    } else if (selected == 3){
		if(cm.getMeso() + selection * tonx2 > 2100000000){
			cm.sendOk("#b兌換後會超過21億楓幣 兌換失敗"); 
			cm.dispose(); 
			return;
		}
      cm.gainItem(item,-selection); 
      cm.gainMeso(selection * tonx2); 
      cm.sendOk("#b您成功用#r "+selection+" #b個#v4310004##z4310004#兌換了#r "+(selection*tonx2)+" #b楓幣"); 
      cm.dispose(); 
    } else { 
	cm.sendOk("未知錯誤"); 
      cm.dispose(); 
      return; 
    } 
  } 
 
}