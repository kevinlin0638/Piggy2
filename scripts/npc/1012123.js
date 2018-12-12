/* Lauren
            Android VIP Hair/Hair Color Change.
    */
    var status = -1;
    var beauty = 0;
    var hair_Colo_new;
    var face_new
     
    function start() {
        action(1, 0, 0);
    }
     
    function action(mode, type, selection) {
        if (mode == 0) {
            cm.dispose();
            return;
        } else {
            status++;
        }
     
        if (status == 0) {
            cm.sendSimple("I'm the android hair changer allow me to take care of your android's hairdo. Please choose the one you want.\r\n#L0#Haircut\r\n#L1#Dye android's hair\r\n#L2#Change android's face");
        } else if (status == 1) {
            if (selection == 0) {
                var hair = cm.getAndroidStat("HAIR");
                hair_Colo_new = [];
                beauty = 1;
                   
                    if (cm.getAndroidStat("GENDER") == 0) {
                    hair_Colo_new = [30000, 30020, 30030, 30040, 30050, 30060, 30100, 30110, 30120, 30130, 30140, 30150, 30160, 30170, 30180, 30190, 30200, 30210, 30220, 30230, 30240, 30250, 30260, 30270, 30280, 30290, 30300, 30310, 30320, 30330, 30340, 30350, 30360, 30370, 30400, 30410, 30420, 30440, 30450, 30460, 30470, 30480, 30490, 30510, 30520, 30530, 30540, 30550, 30560, 30570, 30580, 30590, 30600, 30610, 30620, 30630, 30640, 30650, 30660, 30670, 30680, 30690, 30700, 30710, 30720, 30730, 30740, 30750, 30760, 30770, 30780, 30790, 30800, 30810, 30820, 30830, 30840, 30870, 30880, 30890, 30900, 30910, 30920, 30930, 30940, 30950, 30990, 33000, 33040, 33100, 33110, 33120, 33130, 33150, 33160, 33170, 33180, 33190, 33210, 33220, 33240, 33250, 33260, 33270, 33280, 33290, 33330, 33350, 33360, 33370, 33380, 33390, 33400, 33410, 33430, 33440, 33450, 33460, 33470, 33480, 33500, 33510, 33520, 33530, 33540, 33550, 33580, 33590, 33600, 33610, 33620, 33630, 33660, 33670, 33680, 33690, 33800, 36040, 36060];
                    } else {
                    hair_Colo_new = [31000, 31010, 31020, 31030, 31040, 31050, 31060, 31070, 31080, 31090, 31100, 31110, 31120, 31130, 31140, 31150, 31160, 31170, 31180, 31190, 31200, 31210, 31220, 31230, 31240, 31250, 31260, 31270, 31280, 31290, 31300, 31310, 31320, 31330, 31340, 31350, 31360, 31400, 31410, 31420, 31440, 31450, 31460, 31470, 31480, 31490, 31510, 31520, 31530, 31540, 31550, 31560, 31570, 31580, 31590, 31600, 31610, 31620, 31630, 31640, 31650, 31660, 31670, 31680, 31690, 31700, 31710, 31720, 31730, 31740, 31750, 31760, 31770, 31780, 31790, 31800, 31810, 31820, 31830, 31840, 31850, 31860, 31870, 31880, 31890, 31910, 31920, 31930, 31940, 31950, 31990, 34000, 34010, 34020, 34030, 34040, 34050, 34060, 34070, 34080, 34090, 34100, 34110, 34120, 34130, 34140, 34150, 34160, 34170, 34180, 34190, 34210, 34220, 34240, 34250, 34260, 34270, 34310, 34320, 34330, 34340, 34360, 34370, 34380, 34400, 34410, 34420, 34430, 34440, 34450, 34470, 34480, 34490, 34510, 34540, 34590, 34600, 34610, 34620, 34630, 34650, 34660, 34670, 34680, 34690, 34720, 34780, 34790, 36050];
                    }
                for (var i = 0; i < hair_Colo_new.length; i++) {
                    hair_Colo_new[i] = hair_Colo_new[i] + (hair % 10);
                }
                cm.askAndroid("I can totally change up your android's hairstyle and make it look so good. Why don't you change it up a bit? I'll change it for you. Choose the one to your liking~.", hair_Colo_new);
            } else if (selection == 1) {
                var currenthaircolo = Math.floor((cm.getAndroidStat("HAIR") / 10)) * 10;
                hair_Colo_new = [];
                beauty = 2;
     
                for (var i = 0; i < 8; i++) {
                    hair_Colo_new[i] = currenthaircolo + i;
                }
                cm.askAndroid("I can totally change your android's haircolor and make it look so good. Why don't you change it up a bit? I'll change it for you. Choose the one to your liking.", hair_Colo_new);
            } else if (selection == 2) {
            beauty = 3;
            var face = cm.getAndroidStat("FACE");
            if (cm.getAndroidStat("GENDER") == 0){
                            face_new = [20000, 20001, 20002, 20003, 20004, 20005, 20006, 20007, 20008, 20009, 20010, 20011, 20012, 20013, 20014, 20015, 20016, 20017, 20018, 20019, 20020, 20021, 20022, 20023, 20024, 20025, 20026, 20027, 20028, 20029, 20030, 20031, 20032, 20036, 20037, 20040, 20043, 20044, 20045, 20046, 20047, 20048, 20049, 20050, 20052, 20053, 20055, 20056, 20057];
                } else {
                    face_new = [21000, 21001, 21002, 21003, 21004, 21005, 21006, 21007, 21008, 21009, 21010, 21011, 21012, 21013, 21014, 21015, 21016, 21017, 21018, 21019, 21020, 21021, 21022, 21023, 21024, 21025, 21026, 21027, 21028, 21029, 21030, 21031, 21033, 21034, 21035, 21038, 21041, 21042, 21043, 21044, 21045, 21046, 21047, 21048, 21049, 21052, 21053, 21054, 21055, 21058, 21062];
            }
            for (var i = 0; i < face_new.length; i++) {
                face_new[i] = face_new[i] + face % 1000 - (face % 100);
            }
            cm.askAndroid("Let's see... I can totally transform your android's face into something new. Don't you want to try it? You can get the face of your liking. Take your time in choosing the face of your preference.", face_new);
            }
        } else if (status == 2){
            if (beauty == 1){
                if (cm.setAndroid(hair_Colo_new[selection]) == 1) {
                    cm.reloadChar();
                    cm.sendOk("Enjoy android's your new and improved hairstyle!");
                } else {
                    cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry...");
                }
            } else if (beauty == 2) {
                if (cm.setAndroid(hair_Colo_new[selection]) == 1) {
                    cm.reloadChar();
                    cm.sendOk("Enjoy android's your new and improved haircolor!");
                } else {
                    cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...");
                }
            } else {
            if (cm.setAndroid(face_new[selection]) == 1) {
                    cm.reloadChar();
                    cm.sendOk("Enjoy android's your new and improved face!");
                } else {
                    cm.sendOk("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry...");
                }
            }
            cm.dispose();
        }
    }