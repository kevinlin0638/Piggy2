/**
 * @author: Eric
 * @func: Music Changing NPC
*/
var music = Array("BgmKhaos/Khaos0","BgmKhaos/Khaos1", "BgmKhaos/Khaos2", "BgmKhaos/Khaos3", "BgmKhaos/Title", "BgmEvent/WhiteChristmas", "BgmEvent/FM23", "BgmEvent/SleepyWood", "BgmEvent/RestNPeace", "BgmEvent/FloralLife", "BgmEvent/Wall", "BgmEvent/pvp0", "BgmEvent/GoPicnic", "BgmEvent/Nightmare", "BgmEvent/Ghost", "BgmEvent/Fista", "BgmEvent/Donate", "BgmEvent/BlackMagician", "BgmEvent/BadGuys", "BgmEvent/AncientMove", "BgmEvent/Touhou", "BgmEvent/gmmap", "BgmEvent2/Baal", "BgmEvent2/BO1", "BgmEvent2/FuckLeecher", "BgmEvent2/risingStar", "BgmEvent2/risingStar2");
var musicN = Array("Khaos 1", "Khaos 2", "Khaos 3", "Khaos 4", "v83 Login", "FM Music", "FM23 Music", "Mario Music 1", "Mario Music 2", "PvP Music", "Wall? wat", "Some other PvP Music?", "Singer/Guitarist waiting room music", "Singer/Guitarist JQ room music", "White Lady Music", "Fista.. Fiesta music?", "Donor Island music", "Black Mage music!", "I have no clue?", "More Mario Music!", "Whatever Touhou is.. music", "GM Map music? LOL", "Baal! WHOO", "some cool music?", "Mmm.. Universe song.", "Some anime song 1", "Some anime song 2");

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 2 && mode == 0) {
            cm.sendOk("Don't want to change the song?\r\nThen why did you click me?");
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            var jukebox = "#b#eCustom Music Player#k#n";
            jukebox += "\r\nHello! This is the #eWizStory#n Custom Music Changer";
            for (var i = 0; i < music.length; i++) {
                jukebox += "\r\n#L" + i + "# " +musicN[i]+ "#l";
            }
            cm.sendSimple(jukebox);
        } else if (status == 1) {
           if (selection >= 0) {
                cm.sendYesNo("Are you sure you want to change the song to:\r\n#b" + musicN[selection] + "#k?");
                mc = selection;
            } else {
                cm.sendOk("Lolwut, you chose a song that isn't in a selection, report this to the EricMS forums!");
                cm.dispose();
            }
        } else if (status == 2) {
                cm.playMusic(music[mc]);
                cm.dispose();       
        } else {
            cm.dispose();
        }
    }
}