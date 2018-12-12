/* 
 * This file is part of the OdinMS MapleStory Private Server 
 * Copyright (C) 2012 Patrick Huy and Matthias Butz 
 * 
 * This program is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License 
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package client;

import scripting.LieDetectorScript;
import server.Timer.EtcTimer;
import server.maps.MapleMap;
import tools.HexTool;
import tools.packet.CWvsContext;
import tools.types.Pair;

/**
 * @author AuroX
 */
public class MapleLieDetector {

    public MapleCharacter chr;
    public byte type; // 0 = Normal, 1 = Admin Macro (Manager Skill) 
    public int attempt;
    public String tester, answer;
    public boolean inProgress, passed;

    public MapleLieDetector(final MapleCharacter c) {
        this.chr = c;
        reset();
    }

    public final boolean startLieDetector(final String tester, final boolean isItem, final boolean anotherAttempt) {
        if (!anotherAttempt && ((isPassed() && isItem) || inProgress() || attempt == 2/* || answer != null || tester != null*/)) {
            return false;
        }
        final Pair<String, String> captcha = LieDetectorScript.getImageBytes();
        if (captcha == null) {
            return false;
        }
        final byte[] image = HexTool.getByteArrayFromHexString(captcha.getLeft());
        this.answer = captcha.getRight();
        this.tester = tester;
        this.inProgress = true;
        this.type = (byte) (isItem ? 0 : 1);
        this.attempt++;

        chr.getClient().sendPacket(CWvsContext.sendLieDetector(image));
        EtcTimer.getInstance().schedule(new Runnable() {

            @Override
            public void run() {
                if (!isPassed() && chr != null) {
                    if (attempt >= 2) {
                        final MapleCharacter search_chr = chr.getMap().getCharacterByName(tester);
                        if (search_chr != null && search_chr.getId() != chr.getId()) {
                            search_chr.dropMessage(5, "The user has failed the Lie Detector Test. You'll be rewarded 7000 mesos from the user.");
                            search_chr.gainMeso(7000, true);
                        }
                        end();
                        // chr.getClient().sendPacket(CWvsContext.LieDetectorResponse((byte) 7, (byte) 4));
                        final MapleMap to = chr.getMap().getReturnMap();
                        chr.changeMap(to, to.getPortal(0));
                    } else { // can have another attempt 
                        startLieDetector(tester, isItem, true);
                    }
                }
            }
        }, 60000); // 60 secs 
        return true;
    }

    public final int getAttempt() {
        return attempt;
    }

    public final byte getLastType() {
        return type;
    }

    public final String getTester() {
        return tester;
    }

    public final String getAnswer() {
        return answer;
    }

    public final boolean inProgress() {
        return inProgress;
    }

    public final boolean isPassed() {
        return passed;
    }

    public final void end() {
        this.inProgress = false;
        this.passed = true;
        this.attempt = 0;
    }

    public final void reset() { // called when change map, cc, reenter cs, or login 
        this.tester = "";
        this.answer = "";
        this.attempt = 0;
        this.inProgress = false;
        this.passed = false;
    }
}  