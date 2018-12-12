/**
 * @author: Eric
 * @func: TrollMS Gender Changer (Male, Female, Shemale)
 * @notes: Need to add requirements, other then coins. 
*/

var status = 0; 

function start() { 
    status = -1; 
    action(1, 0, 0); 
} 

function action(mode, type, selection) { 
    if (mode == -1) { 
        cm.dispose(); 
    } else { 
        if (mode == 0 && status == 0) { 
            cm.dispose(); 
            return; 
        } 
        if (mode == 1) 
            status++; 
        else 
            status--; 
        if (status == 0) { 
            cm.sendSimple("After I got my first sex change, I noticed how great it was..\r\nWould you like one too?\r\n\r\n#dSex Change : Trasexual (Shemale) | Requirements -\r\n* 50 Wiz Coins\r\n* 50 Rebirths\r\n* 50 Snail Shells #v4000019#\r\n#L0#Become a #eTransexual (Shemale)#n#l#k\r\n\r\n\r\n#rSex Change : Female | Requirements -\r\n* 15 Wiz Coins\r\n* 15 Rebirths\r\n* 15 Red Snail Shells #v4000016#\r\n#L1#Change to a #eFemale#n#l#k\r\n\r\n\r\n#bSex Change : Male | Requirements -\r\n* 15 Wiz Coins\r\n* 15 Rebirths\r\n* 15 Blue Snail Shells #v4000000#\r\n#L2#Change to a #eMale#n#l#k"); 
        } else if (status == 1) { 
	  if (selection == 0) {
		if (cm.getPlayer().getGender() == 2) { 
			cm.sendOk("#dYou're already a #eTransexual (Shemale)#n.#k");
			cm.dispose();
		} else {
          if (cm.haveItem(4007099, 50) && cm.haveItem(4000019, 50) && cm.getPlayer().getReborns() >= 50) { 
            cm.getPlayer().setGender(2); 
            cm.gainItem(4007099, -50); 
            cm.reloadChar(); 
			cm.sendOk("You're now a #dTransexual (Shemale)#k!\r\nNot only do you have #rboobies#k and a #bpenis#k..\r\nBut now you may equip #bMale#k and #rFemale#k clothing!\r\nPlus, don't forget; access both #bKIN#k and #rNimaKIN#k!"); 
            cm.dispose(); 
         } else { 
            cm.sendOk("You either don't have #d50 Wiz Coins#k, #d50 Snail Shells #v4000019##k,\r\nor you don't have #d50 Rebirths#k."); 
            cm.dipose(); 
          }
		}
	  } else if (selection == 1) {
		if (cm.getPlayer().getGender() == 1) { 
			cm.sendOk("#rYou're already a #eFemale#n.#k");
			cm.dispose();
		} else {
          if (cm.haveItem(4007099, 15) && cm.haveItem(4000016, 15) && cm.getPlayer().getReborns() >= 15) { 
             cm.getPlayer().setGender(1); 
             cm.gainItem(4007099, -15); 
             cm.reloadChar(); 
			 cm.sendOk("You're now a #rfemale#k! How do you like your new #rboobies#k?"); 
             cm.dispose(); 
          } else { 
             cm.sendOk("You either don't have #r15 Wiz Coins#k, #r15 Red Snail Shells #v4000016##k,\r\nor you don't have #r15 Rebirths#k."); 
             cm.dipose(); 
          }
		}
	  } else if (selection == 2) {
		 if (cm.getPlayer().getGender() == 0) { 
		    cm.sendOk("#bYou're already a #eMale#n.#k");
			cm.dispose();
          } else {
			if (cm.haveItem(4007099, 15) && cm.haveItem(4000000, 15) && cm.getPlayer().getReborns() >= 15) { 
              cm.getPlayer().setGender(0); 
              cm.gainItem(4007099, -15); 
              cm.reloadChar(); 
              cm.sendOk("You're now a #bmale#k.\r\nSorry if I made your #bpenis#k too #esmall#n, it's a common mistake."); 
              cm.dispose(); 
           } else { 
              cm.sendOk("You either don't have #b15 Wiz Coins#k, #b15 Blue Snail Shells #v4000000##k,\r\nor you don't have #b15 Rebirths#k."); 
              cm.dispose(); 
               } 
	        }
         }
      }
   } 
}  