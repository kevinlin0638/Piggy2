
function start() { 
    cm.sendSimple ("Which shop do you want to open? \r\n#L0#Accessory shop\r\n#L1#Cap shop\r\n#L2#Cape shop\r\n#L3#Shirt shop\r\n#L4#Gloves shop\r\n#L5#Overall shop\r\n#L6#Pants shop\r\n#L7#Shield shop\r\n#L8#Shoe shop\r\n#L10#Mounts shop\r\n#L11#Ring shop\r\n#L12#Pet equip shop\r\n#L13#Misc shop\r\n#L14#Weapon shop 1\r\n#L15#Weapon shop 2\r\n#L16#Weapon shop 3\r\n#L9#Weapon shop 4\r\n#L17#Stars, arrows, bullets, potions, chairs");
}

function action(mode, type, selection) { 
         cm.dispose();
  if (selection == 0) {
         cm.openShop(500);
 } else  if (selection == 1) {
         cm.openShop(501);
 } else  if (selection == 2) {
         cm.openShop(502);
 } else  if (selection == 3) {
         cm.openShop(503);
 } else  if (selection == 4) {
         cm.openShop(504);
 } else  if (selection == 5) {
         cm.openShop(505);
 } else  if (selection == 6) {
	  cm.openShop(506);
 } else  if (selection == 7) {
         cm.openShop(507);
 } else  if (selection == 8) {
         cm.openShop(508);
 } else  if (selection == 9) {
         cm.openShop(509);
 } else  if (selection == 10) {
         cm.openShop(510);
 } else  if (selection == 11) {
         cm.openShop(511);
 } else  if (selection == 12) {
         cm.openShop(512);
 } else  if (selection == 13) {
         cm.openShop(513);
 } else  if (selection == 14) {
         cm.openShop(514);
 } else  if (selection == 15) {
         cm.openShop(515);
 } else  if (selection == 16) {
         cm.openShop(516);
 } else  if (selection == 17) {
         cm.openShop(517);
 } else  if (selection == 18) {
         cm.openShop(518);
}
}