/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package constants;

import tools.types.Pair;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Eric
 *         <p>
 *         Global World Properties.
 */
public class WorldConstants {

    // Global Constants : handle world count, channel count, max user per world, max char per account, and event scripts
    // Scripts TODO: Amoria,CWKPQ,BossBalrog_EASY,BossBalrog_NORMAL,ZakumPQ,ProtectTylus,GuildQuest,Ravana_EASY,Ravna_MED,Ravana_HARD (untested or not working)
    public static String Events = "" // event scripts, programmed per world but i'll keep them the same
            + "elevator,AriantPQ1,Aswan,automsg,autoSave,MonsterPark,Trains,Boats,Flight,PVP,Visitor,cpq2,cpq,Rex,AirPlane,CygnusBattle,ScarTarBattle,VonLeonBattle,Ghost,"
            + "Prison,HillaBattle,AswanOffSeason,ArkariumBattle,OrbisPQ,HenesysPQ,Juliet,Dragonica,Pirate,BossQuestEASY,BossQuestMED,BossQuestHARD,BossQuestHELL,Ellin,"
            + "HorntailBattle,LudiPQ,KerningPQ,ZakumBattle,MV,MVBattle,DollHouse,Amoria,CWKPQ,BossBalrog_EASY,BossBalrog_NORMAL,PinkBeanBattle,ZakumPQ,ProtectTylus,ChaosHorntail,"
            + "ChaosZakum,Ravana_EASY,Ravana_HARD,Ravana_MED,GuildQuest";

    public static int GLOBAL_EXP_RATE = 1;
    public static int GLOBAL_MESO_RATE = 1;
    public static int GLOBAL_DROP_RATE = 1; // Default: 2

    public static boolean GLOBAL_RATES = true; // When true, all worlds use the above rates

    public static List<Pair<Integer, String>> eventMessages = new LinkedList<>();


    public static void init() {

        for (WorldConfig worldConfig : WorldConfig.values()) {
            worldConfig.setFlag(WorldFlag.New);
        }

        // 經驗調整
        WorldConfig.雪吉拉.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 5));
        WorldConfig.菇菇寶貝.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));
        WorldConfig.星光精靈.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));
        WorldConfig.緞帶肥肥.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));
        WorldConfig.藍寶.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));
        WorldConfig.綠水靈.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));
        WorldConfig.三眼章魚.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));
        WorldConfig.木妖.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));
        WorldConfig.火毒眼獸.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));
        WorldConfig.蝴蝶精.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));
        WorldConfig.海怒斯.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));
        WorldConfig.電擊象.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));
        WorldConfig.鯨魚號.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));
        WorldConfig.皮卡啾.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));
        WorldConfig.神獸.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));
        WorldConfig.泰勒熊.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));
        WorldConfig.寒霜冰龍.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));
        WorldConfig.九尾妖狐.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));
        WorldConfig.葛雷金剛.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));
        WorldConfig.喵怪仙人.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));
        WorldConfig.雪吉拉.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));
        WorldConfig.雪吉拉.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));
        WorldConfig.雪吉拉.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));
        WorldConfig.雪吉拉.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));
        WorldConfig.雪吉拉.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));
        WorldConfig.雪吉拉.setExpRate((GLOBAL_RATES ? GLOBAL_EXP_RATE : 1));

        // 掉落倍率

        // Exp rates
        WorldConfig.雪吉拉.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 2));
        WorldConfig.菇菇寶貝.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));
        WorldConfig.星光精靈.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));
        WorldConfig.緞帶肥肥.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));
        WorldConfig.藍寶.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));
        WorldConfig.綠水靈.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));
        WorldConfig.三眼章魚.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));
        WorldConfig.木妖.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));
        WorldConfig.火毒眼獸.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));
        WorldConfig.蝴蝶精.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));
        WorldConfig.海怒斯.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));
        WorldConfig.電擊象.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));
        WorldConfig.鯨魚號.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));
        WorldConfig.皮卡啾.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));
        WorldConfig.神獸.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));
        WorldConfig.泰勒熊.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));
        WorldConfig.寒霜冰龍.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));
        WorldConfig.九尾妖狐.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));
        WorldConfig.葛雷金剛.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));
        WorldConfig.喵怪仙人.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));
        WorldConfig.雪吉拉.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));
        WorldConfig.雪吉拉.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));
        WorldConfig.雪吉拉.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));
        WorldConfig.雪吉拉.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));
        WorldConfig.雪吉拉.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));
        WorldConfig.雪吉拉.setDropRate((GLOBAL_RATES ? GLOBAL_DROP_RATE : 1));


        // Event messages
        for (WorldConfig worldConfig : WorldConfig.values()) {
            worldConfig.setEventMessage("歡迎來到" + worldConfig.name() + "伺服器");
        }

        WorldConfig.雪吉拉.setWorldSwitch(true);
        WorldConfig.三眼章魚.setWorldSwitch(true);

        WorldConfig.雪吉拉.setMaxCharacters(15);
        WorldConfig.雪吉拉.setUserLimit(1500);
        WorldConfig.雪吉拉.setChnnaelCount(2);

        WorldConfig.三眼章魚.setMaxCharacters(15);
        WorldConfig.三眼章魚.setUserLimit(1500);
        WorldConfig.三眼章魚.setChnnaelCount(1);

    }
}
