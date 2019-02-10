/* 阿杜比斯
 * 
 * 地點: 殘暴炎魔門口 (211042300)
 * 
 * 殘暴炎魔 任務 NPC
 * 
 * 任務 100200 = 是否能挑戰殘暴炎魔
 * 任務 100201 = Collecting Gold Teeth <- indicates it's been started
 * 任務 100203 = Collecting Gold Teeth <- indicates it's finished
 * Quest 7000 - Indicates if you've cleared first stage / fail
 * 4031061 = Piece of Fire Ore - stage 1 reward
 * 4031062 = Breath of Fire    - stage 2 reward
 * 4001017 = Eye of Fire       - stage 3 reward
 * 4000082 = Zombie's Gold Tooth (stage 3 req)
 */

var status;
var mapId = 211042300;
var stage;
var teethmode;
var High = 500000;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 && status == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        if (status == 3) {
            cm.sendNext("看來不想這樣啊？如果想明白了可以來找我！");
            cm.dispose();
        }

        status--;
        cm.removeAll(4001015);
        cm.removeAll(4001016);
        cm.removeAll(4001018);
    }
    if (status == 0) {
        if (cm.getPlayerStat("LVL") >= 50) {
            if (cm.getQuestStatus(100200) != 2 && cm.getQuestStatus(100200) != 1) {
                cm.startQuest(100200);
                cm.sendOk("你想被允許挑戰殘暴炎魔大怪物的任務嗎？  嗯……。我#b阿杜比斯#k覺得你有能力承擔這項任務。但是再這之前，你必須完成我交給你的任務。  但是你要小心點。");
                cm.dispose();
                return;
            } else if (cm.getQuestStatus(100201) == 1) {
                // if they have gold teeth and the other items, they are good to go
                teethmode = 1;
                cm.sendNext("你們沒有我需要的物品嗎？這可不是慈善事業！");
            } else {
                if (cm.haveItem(4001109)) {
                    cm.sendSimple("好。。。我看你們有充分的資格，你想挑戰那一階段？ #b\r\n#L0#廢礦調查 (第一階段)#l\r\n#L1#殘暴炎魔迷宮調查 (第二階段)#l\r\n#L2#治煉邀請 (第三階段)#l\r\n#L3#進去打殘暴炎魔#l\r\n#L4#跳過任務 (需要花錢)#l");
                } else {
                    cm.sendSimple("好。。。我看你們有充分的資格，你想挑戰那一階段？ #b\r\n#L0#廢礦調查 (第一階段)#l\r\n#L1#殘暴炎魔迷宮調查 (第二階段)#l\r\n#L2#治煉邀請 (第三階段)#l\r\n#L4#跳過任務 (需要花錢)#l\r\n#L5#購買火焰之眼");
                }
            }
            if (cm.getQuestStatus(100201) == 2) { // They're done the quests
                teethmode = 2;
            }
        } else {
            cm.sendOk("按照你目前的情況，你還不能滿足進行這項任務的能力，當你變的強大的時候，再來找我吧！");
            cm.dispose();
        }
    } else if (status == 1) {
        //quest is good to go.
        // if they're working on this quest, he checks for items.
        if (teethmode == 1) {
            // check for items
            if (cm.haveItem(4000082, 30)) { // take away items, give eyes of fire, complete quest
                if (cm.canHold(4001017)) {
                    cm.removeAll(4031061);
                    cm.removeAll(4031062);
                    cm.gainItem(4000082, -30);
                    cm.gainItem(4001017, 5);
                    cm.sendNext("冶煉好了。 看到左邊的門了嗎？它就是通往殘暴炎魔祭台的門。 不過你需要 #b#t4001017##k 才能進入裡面。讓我看看有多少人能進入到那個恐怖的地方？");
                    cm.completeQuest(100201);
                    cm.completeQuest(100200);
                } else {
                    cm.sendNext("嗯？你確定你有足夠的背包空間嗎？請再檢查一下。");
                }
                cm.dispose();
            } else { // go get more
                cm.sendNext("你還沒有帶來我需要的東西嗎？");
                cm.dispose();
            }
            return;
        }
        if (selection == 0) { //ZPQ
            if (cm.getParty() == null) { //no party
                cm.sendNext("你現在還沒有一個組隊，請組隊後再和我談話。");
                cm.safeDispose();
                return;
            } else if (!cm.isLeader()) { //not party leader
                cm.sendNext("你不是組隊長，請讓你的組隊長和我談話。");
                cm.safeDispose();
                return;
            } else {
                //check each party member, make sure they're above 50 and still in the door map
                //TODO: add zakum variable to characters, check that instead; less hassle
                var party = cm.getParty().getMembers();
                mapId = cm.getMapId();
                var next = true;
                for (var i = 0; i < party.size(); i++) {
                    if ((party.get(i).getLevel() < 50) || (party.get(i).getMapid() != mapId)) {
                        next = false;
                    }
                }
                if (next) {
                    //all requirements met, make an instance and start it up
                    var em = cm.getEventManager("ZakumPQ");
                    if (em == null) {
                        cm.sendOk("我不能讓你進入這個未知的世界，因為管理員還沒有準備好開放。");
                    } else {
                        var prop = em.getProperty("started");
                        if (prop == null || prop.equals("false")) {
                            em.startInstance(cm.getParty(), cm.getMap());
                        } else {
                            cm.sendOk("另一個組隊已經開始了調查任務，請稍後再來。");
                        }
                    }
                    cm.dispose();
                } else {
                    cm.sendNext("請確保你所有組隊員都達到50級以上。");
                    cm.dispose();
                }
            }
        } else if (selection == 1) { //Zakum Jump Quest
            stage = 1;
            if (cm.haveItem(4031061) && !cm.haveItem(4031062)) {
                // good to go
                cm.sendYesNo("你已經成功通過了第一階段。你還有很長的路才能到達殘暴炎魔的祭台。所以，你想好挑戰下一個階段了嗎？");
            } else {
                if (cm.haveItem(4031062)) {
                    cm.sendNext("你已經得到了#t4031062#，所以你不用再挑戰此階段了。");
                } else {
                    cm.sendNext("請完成上一階段的任務再來挑戰此階段。");
                }
                cm.dispose();
            }
        } else if (selection == 2) { //Golden Tooth Collection
            stage = 2;
            if (teethmode == 2 && cm.haveItem(4031061) && cm.haveItem(4031062)) {
                // Already done it once, they want more
                cm.sendYesNo("如果你想得到更多的#b火焰的眼#k， 你需要給我 #b30 個殭屍丟失的金齒#k。 你有更多的金牙要給我嗎？");
            } else if (cm.haveItem(4031061) && cm.haveItem(4031062)) {
                // check if quest is complete, if so reset it (NOT COMPLETE)
                cm.sendYesNo("好吧， 你已經完成了早期的階段。  現在， 努力一點我可以幫你得到進入殘暴炎魔祭台所需要的 火焰的眼。 但是， 我的牙齒最近有點疼。  你見過一個牙醫在冒險島世界的故事嗎？  哦，我聽說殭屍們有幾顆金牙。我需要你找到 #b30 個殭屍丟失的金齒#k 。這樣我就可以自己製造一些假牙。然後我可以幫你拿到你想要的物品\r\n任務要求：\r\n#i4000082##b x 30 個");
            } else {
                cm.sendNext("請完成上一階段的任務再來挑戰此階段。");
                cm.dispose();
            }
        } else if (selection == 3) { // Enter the center of Lava, quest
            var dd = cm.getEventManager("FireDemon");
            if (dd != null && cm.haveItem(4001109)) {
                dd.startInstance(cm.getPlayer());
            } else {
                cm.sendOk("暫時不能進入。");
            }
            cm.dispose();
        } else if (selection == 4) {
            if (cm.getQuestStatus(100200) == 2) {
                cm.sendOk("你已經完成了這個任務無法進行此操作。");
                cm.dispose();
            } else {
                cm.sendYesNo("你想收買我？哈哈，可以啊！但你必須給我 #e300,000,000#n 楓幣，我就可以讓你直接跳過任務。");
                status = 3;
            }
        } else if (selection == 5) {
			cm.sendGetNumber("請問您要買多少個#i4001017##t4001017#呢??\r\n1顆:50萬", 1, 1, 1000);
			status = 5;
		}
    } else if (status == 2) {
        if (stage == 1) {
            cm.warp(280020000, 0); // Breath of Lava I
            cm.dispose();
        } else if (stage == 2) {
            if (teethmode == 2) {
                if (cm.haveItem(4031061, 1) && cm.haveItem(4031062, 1) && cm.haveItem(4000082, 30)) { // take away items, give eyes of fire, complete quest
                    if (cm.canHold(4001017)) {
                        cm.gainItem(4031061, -1);
                        cm.gainItem(4031062, -1);
                        cm.gainItem(4000082, -30);
                        cm.gainItem(4001017, 5);
                        cm.sendNext("冶煉好了。 看到左邊的門了嗎？它就是通往殘暴炎魔祭台的門。 不過你需要 #b#t4001017##k 才能進入裡面。讓我看看有多少人能進入到那個恐怖的地方？");
                        cm.completeQuest(100201);
                        cm.completeQuest(100200);
                    } else {
                        cm.sendNext("你好像沒有足夠的背包空間，請檢查一下再來。");
                    }
                    cm.dispose();
                } else {
                    cm.sendNext("我不認為你帶來了30個 殭屍丟失的金牙呢……。請快點找來，我就會給你需要的東西。");
                    cm.dispose();
                }
            } else {
                cm.startQuest(100201);
                cm.dispose();
            }
        }
    } else if (status == 4) { //bribe
        if (cm.getPlayer().getMeso() < 300000000) {
            cm.sendNext("你好像沒有足夠的楓幣來支付，請檢查一下再來。");
        } else if (!cm.canHold(4001017)) {
            cm.sendNext("你好像沒有足夠的背包空間，請檢查一下再來。");
        } else {
            cm.gainItem(4001017, 5);
            cm.completeQuest(100200);
			cm.completeQuest(100201);
            cm.forceCompleteQuest(7000);
            cm.completeQuest(100203);
            cm.sendOk("好了，祝你玩的愉快！");
            cm.gainMeso(-300000000);
        }
        cm.dispose();
	} else if (status == 6) {
		zkfk = selection;
		cm.sendYesNo("這些#i4001017# 花您 " + zkfk * High + " 楓幣, 請問您確定要購買嗎??");
	} else if (status == 7) {
		if (cm.canHold(4001017)) {
			if (cm.getMeso() >= zkfk * High) {
				cm.gainItem(4001017, zkfk);
				cm.gainMeso(-(zkfk * High));
				cm.sendOk("感謝你購買了 #i4001017# x"+zkfk+" 花您 " + zkfk * High + " 楓幣，謝謝惠顧歡迎下次再來~~");
			} else {
				cm.sendOk("您沒有足夠的楓幣!");
			}
		} else {
			cm.sendOk("你的其他欄好像滿了哦0.0");
		}
    } else {
        cm.dispose();
    }
}