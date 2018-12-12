/* global ms */
var status = -1;

function action(mode, type, selection) {
    if (mode === 0) {
        status--;
    } else {
        status++;
    }

    var i = -1;
    if (status <= i++) {
        ms.dispose();
    } else if (status === i++) {
        ms.getDirectionStatus(true);
        ms.lockUI(1, 1);
        ms.disableOthers(true);
        ms.playerWaite();
        ms.spawnNPCRequestController(1096000, 2209, -107, 0, 9712075);
        ms.spawnNPCRequestController(1096001, 2046, -62, 0, 9712076);
        ms.playerMoveRight();
    } else if (status === i++) {
        ms.getNPCTalk(["好，離開這地方要前往楓之島的理由是什麼？在這裡要前往楓之島的人幾乎不多見…加上看你的穿著似乎不是單純的要去旅行的樣子？ "], [3, 0, 1096000, 0, 1, 0, 0, 1, 0]);
        ms.showWZEffect("Effect/Direction4.img/cannonshooter/face03");
    } else if (status === i++) {
        ms.getNPCTalk(["想要前往楓之島進行修煉啊，去那邊後，繼續修煉前往維多利亞島的話，聽說就變成帥氣的冒險家。"], [3, 0, 1096000, 0, 3, 0, 1, 1, 0]);
    } else if (status === i++) {
        ms.getNPCTalk(["喔，原來知道啊，為了要變成冒險家的第一步從楓之島開始是非常好的，不但可以看到從其他地方來的冒險家，也可以認識朋友，另外也沒有可怕的怪物，但是冒險的開始就在那之後，維多利亞島或像艾納斯一樣的大陸裡，有無法想像的怪物，雖然這也是冒險的樂趣之一。"], [3, 0, 1096000, 0, 1, 0, 1, 1, 0]);
    } else if (status === i++) {
        ms.getNPCTalk(["強力的怪物！為了成為帥氣的冒險家這不是必須的東西嗎？只有實在的修行，才能變得更強，所以要努力修煉才行，出發之前也已經做了很多功課，我準備變成冒險家，哈哈哈！"], [3, 0, 1096000, 0, 3, 0, 1, 1, 0]);
    } else if (status === i++) {
        ms.getNPCTalk(["喔喔，真的很有自信，這樣的心情是非常重要的，但是也會突然發生某些事情！不管發生怎樣的事情，#b即使進入巴洛古的洞穴，好好振作精神的話，就可活下來的#k俗語，將其放在心上，也一定可以贏的。"], [3, 0, 1096000, 0, 1, 0, 1, 1, 0]);
    } else if (status === i++) {
        ms.getNPCTalk(["但是等一下…？你沒有聽到什麼聲音嗎？有感受到不尋常的氣息嗎…？這裡是怪物不常出現的和平海岸…小心一點！"], [3, 0, 1096000, 0, 1, 0, 1, 1, 0]);
    } else if (status === i++) {
        ms.playerMoveRight();
        ms.getDirectionStatus(true);
    } else if (status === i++) {
        ms.spawnNPCRequestController(1096011, 2000, -20, 0, 9712735);
        ms.getEventEffect("Effect/Summon.img/15", [0, 0, 0, 1, 1, 0, 9712735, 0]);
        ms.showEnvironment(5, "cannonshooter/summon", []);
        ms.exceTime(2000);
    } else if (status === i++) {
        ms.removeNPCRequestController(9712075);
        ms.removeNPCRequestController(9712076);
        ms.spawnNPCRequestController(1096008, 2000, -20, 1, 9712886);
        ms.spawnNPCRequestController(1096002, 2108, -82, 0, 9712887);
        ms.getEventEffect("Effect/Direction4.img/effect/cannonshooter/balog/0", [2000, 0, -200, 1, 1, 0, 9712886, 0]);
        ms.exceTime(500);
    } else if (status === i++) {
        ms.setNPCSpecialAction(9712886, "attack2", 0, false);
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction4.img/effect/cannonshooter/npc/0", [2000, 0, -160, 1, 1, 0, 9712887, 0]);
        ms.setNPCSpecialAction(9712886, "attack1", 0, false);
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.showEnvironment(5, "Party1/Failed", []);
        ms.getEventEffect("Effect/Direction4.img/effect/cannonshooter/User/0", [2000, 0, -100, 1, 0, 0]);
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.playerMoveLeft();
        ms.exceTime(150);
    } else if (status === i++) {
        ms.playerWaite();
        ms.exceTime(1000);
        ms.showWZEffect("Effect/Direction4.img/cannonshooter/face02");
    } else if (status === i++) {
        ms.setNPCSpecialAction(9712886, "attack2", 0, false);
        ms.getEventEffect("Effect/Direction4.img/effect/cannonshooter/npc/1", [2000, 0, -160, 1, 1, 0, 9712887, 0]);
        ms.exceTime(500);
        ms.showWZEffect("Effect/Direction4.img/cannonshooter/face02");
    } else if (status === i++) {
        ms.getEventEffect("Effect/Direction4.img/effect/cannonshooter/User/1", [2000, 0, -100, 1, 0, 0]);
        ms.exceTime(1000);
        ms.showWZEffect("Effect/Direction4.img/cannonshooter/face05");
    } else if (status === i++) {
        ms.playerWaite();
        ms.exceTime(100);
        ms.showWZEffect("Effect/Direction4.img/cannonshooter/face05");
    } else if (status === i++) {
        ms.setNPCSpecialAction(9712886, "attack2", 0, false);
        ms.showEnvironment(5, "cannonshooter/Attack1", []);
        ms.exceTime(100);
        ms.showWZEffect("Effect/Direction4.img/cannonshooter/face05");
    } else if (status === i++) {
        ms.exceTime(1000);
        ms.showWZEffect("Effect/Direction4.img/cannonshooter/face05");
    } else if (status === i++) {
        ms.setNPCSpecialAction(9712886, "attack1", 0, false);
        ms.getEventEffect("Effect/Direction4.img/effect/cannonshooter/balog/0", [2000, 0, -200, 1, 1, 0, 9712886, 0]);
        ms.getEventEffect("Mob/8150000.img/attack2/info/effect", [0, 0, 0, 1, 1, 0, 9712886, 0]);
        ms.showEnvironment(5, "cannonshooter/Attack1", []);
        ms.exceTime(1000);
    } else if (status === i++) {
        ms.getEventEffect("Mob/8150000.img/attack2/info/hit", [0, 0, 0, 1, 0, 0]);
        ms.getEventEffect("Effect/Direction4.img/effect/cannonshooter/User/2", [2000, 0, -100, 1, 0, 0]);
        ms.getDirectionEffect(3, "", [6]);
        ms.exceTime(500);
        ms.showWZEffect("Effect/Direction4.img/cannonshooter/face02");
    } else if (status === i++) {
        ms.playerWaite();
        ms.exceTime(500);
        ms.showWZEffect("Effect/Direction4.img/cannonshooter/face02");
    } else if (status === i++) {
        ms.setNPCSpecialAction(9712886, "attack2", 0, false);
        ms.getEventEffect("Mob/8130100.img/attack1/info/effect", [0, 0, 0, 1, 1, 0, 9712886, 0]);
        ms.exceTime(500);
        ms.showWZEffect("Effect/Direction4.img/cannonshooter/face02");
    } else if (status === i++) {
        ms.getEventEffect("Mob/8130100.img/attack1/info/hit", [0, 0, 0, 1, 0, 0]);
        ms.showEnvironment(5, "cannonshooter/Attack1", []);
        ms.getDirectionEffect(3, "", [6]);
        ms.exceTime(500);
        ms.showWZEffect("Effect/Direction4.img/cannonshooter/face01");
    } else if (status === i++) {
        ms.getEventEffect("Mob/8130100.img/attack1/info/effect", [0, 0, 0, 1, 1, 0, 9712886, 0]);
        ms.playerMoveRight();
        ms.exceTime(200);
    } else if (status === i++) {
        ms.playerMoveLeft();
        ms.exceTime(200);
    } else if (status === i++) {
        ms.playerMoveRight();
        ms.exceTime(200);
    } else if (status === i++) {
        ms.playerMoveLeft();
        ms.exceTime(200);
    } else if (status === i++) {
        ms.playerMoveRight();
        ms.exceTime(200);
    } else if (status === i++) {
        ms.playerMoveLeft();
        ms.exceTime(200);
    } else if (status === i++) {
        ms.playerMoveRight();
        ms.exceTime(150);
    } else if (status === i++) {
        ms.playerWaite();
        ms.exceTime(500);
    } else if (status === i++) {
        ms.setNPCSpecialAction(9712886, "attack", 0, false);
        ms.showEnvironment(5, "cannonshooter/Attack2", []);
        ms.getEventEffect("Effect/Direction4.img/effect/cannonshooter/balog/1", [2000, 0, -200, 1, 1, 0, 9712886, 0]);
        ms.exceTime(300);
    } else if (status === i++) {
        ms.playerMoveRight();
        ms.getEventEffect("Effect/Direction4.img/effect/cannonshooter/User/3", [2000, 0, -100, 1, 0, 0]);
        ms.exceTime(2000);
        ms.showWZEffect("Effect/Direction4.img/cannonshooter/face02");
    } else if (status === i++) {
        ms.setNPCSpecialAction(9712886, "stand", 0, false);
        ms.removeNPCRequestController(9712886);
        ms.removeNPCRequestController(9712887);
        ms.getDirectionStatus(true);
        ms.removeNPCRequestController(9712735);
        ms.dispose();
        ms.warp(912060100, 0);
    } else {
        ms.dispose();
    }
}
