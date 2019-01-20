/*
 * This file is part of the OdinMS MapleStory Private Server
 * Copyright (C) 2012 Patrick Huy and Matthias Butz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation= 0, either version 3 of the License= 0, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not= 0, see <http://www.gnu.org/licenses/>.
 */
package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author AlphaEta
 */
public class StructItemOption {

    public static String[] types = {
            "incSTR",
            "incDEX",
            "incINT",
            "incLUK",
            "incACC",
            "incEVA",
            "incPAD",
            "incMAD",
            "incPDD",
            "incMDD",
            "incMHP",
            "incMMP",
            "incSTRr",
            "incDEXr",
            "incINTr",
            "incLUKr",
            "incACCr",
            "incEVAr",
            "incPADr",
            "incMADr",
            "incPDDr",
            "incMDDr",
            "incMHPr",
            "incMMPr",
            "incSTRlv",
            "incDEXlv",
            "incINTlv",
            "incLUKlv",
            "incPADlv",
            "incMADlv",
            "incSpeed",
            "incJump",
            "incCr",
            "incDAMr",
            "incTerR",
            "incAsrR",
            "incEXPr",
            "incMaxDamage",
            "HP",
            "MP",
            "RecoveryHP",
            "RecoveryMP",
            "level",
            "prop",
            "time",
            "ignoreTargetDEF",
            "ignoreDAM",
            "incAllskill",
            "ignoreDAMr",
            "RecoveryUP",
            "incCriticaldamageMin",
            "incCriticaldamageMax",
            "DAMreflect",
            "mpconReduce",
            "reduceCooltime",
            "incMesoProp",
            "incRewardProp",
            "boss",
            "attackType"
    };
    public int optionType, reqLevel, opID; // opID = nebulite Id or potential ID
    public String face; // angry, cheers, love, blaze, glitter
    public Map<String, Integer> data = new HashMap<>();
    public String opString; //potential string

    public int get(final String type) {
        return data.get(type) != null ? data.get(type) : 0;
    }

    public String[] getItemOption() {
        ArrayList<String> ss = new ArrayList<>();
        for (String s : StructItemOption.types) {
            if (get(s) > 0) {
                ss.add(s);
            }
        }
        String[] s = new String[ss.size()];
        for (int i = 0; i < ss.size() ; i++) {
            s[i] = ss.get(i);
        }
        return s;
    }

    @Override
    public final String toString() { // I should read from the "string" value instead.
        return data.toString();
    }
}