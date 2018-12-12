package client;

import constants.GameConstants;

public enum MapleJob {

    初心者(0),
    劍士(100),
    狂戰士(110),
    十字軍(111),
    英雄(112),
    見習騎士(120),
    騎士(121),
    聖騎士(122),
    槍騎兵(130),
    嗜血狂騎(131),
    黑騎士(132),
    法師(200),
    巫師_火毒(210),
    魔導士_火毒(211),
    大魔導士_火毒(212),
    巫師_冰雷(220),
    魔導士_冰雷(221),
    大魔導士_冰雷(222),
    僧侶(230),
    祭司(231),
    主教(232),
    弓箭手(300),
    獵人(310),
    遊俠(311),
    箭神(312),
    弩弓手(320),
    狙擊手(321),
    神射手(322),
    盜賊(400),
    刺客(410),
    暗殺者(411),
    夜使者(412),
    俠盜(420),
    神偷(421),
    暗影神偷(422),
    下忍(430),
    中忍(431),
    上忍(432),
    隱忍(433),
    影武者(434),
    海盜(500),
    砲手(501),
    蒼龍俠客1轉(508),
    打手(510),
    格鬥家(511),
    拳霸(512),
    槍手(520),
    神槍手(521),
    槍神(522),
    重砲兵(530),
    重砲兵隊長(531),
    重砲指揮官(532),
    蒼龍俠客2轉(570),
    蒼龍俠客3轉(571),
    蒼龍俠客4轉(572),
    MANAGER(800),
    管理員(900),
    //    SUPER_GM(910),//台服無
    貴族(1000),
    聖魂劍士1轉(1100),
    聖魂劍士2轉(1110),
    聖魂劍士3轉(1111),
    聖魂劍士4轉(1112),
    烈焰巫師1轉(1200),
    烈焰巫師2轉(1210),
    烈焰巫師3轉(1211),
    烈焰巫師4轉(1212),
    破風使者1轉(1300),
    破風使者2轉(1310),
    破風使者3轉(1311),
    破風使者4轉(1312),
    暗夜行者1轉(1400),
    暗夜行者2轉(1410),
    暗夜行者3轉(1411),
    暗夜行者4轉(1412),
    閃雷悍將1轉(1500),
    閃雷悍將2轉(1510),
    閃雷悍將3轉(1511),
    閃雷悍將4轉(1512),
    傳說(2000),
    龍魔導士(2001),
    精靈遊俠(2002),
    幻影俠盜(2003),
    夜光(2004),
    隱月(2005),
    狂狼勇士1轉(2100),
    狂狼勇士2轉(2110),
    狂狼勇士3轉(2111),
    狂狼勇士4轉(2112),
    龍魔導士1轉(2200),
    龍魔導士2轉(2210),
    龍魔導士3轉(2211),
    龍魔導士4轉(2212),
    龍魔導士5轉(2213),
    龍魔導士6轉(2214),
    龍魔導士7轉(2215),
    龍魔導士8轉(2216),
    龍魔導士9轉(2217),
    龍魔導士10轉(2218),
    精靈遊俠1轉(2300),
    精靈遊俠2轉(2310),
    精靈遊俠3轉(2311),
    精靈遊俠4轉(2312),
    幻影俠盜1轉(2400),
    幻影俠盜2轉(2410),
    幻影俠盜3轉(2411),
    幻影俠盜4轉(2412),
    隱月1轉(2500),
    隱月2轉(2510),
    隱月3轉(2511),
    隱月4轉(2512),
    夜光1轉(2700),
    夜光2轉(2710),
    夜光3轉(2711),
    夜光4轉(2712),
    市民(3000),
    惡魔殺手(3001),
    傑諾(3002),
    惡魔殺手1轉(3100),
    惡魔殺手2轉(3110),
    惡魔殺手3轉(3111),
    惡魔殺手4轉(3112),
    惡魔復仇者1轉(3101),
    惡魔復仇者2轉(3120),
    惡魔復仇者3轉(3121),
    惡魔復仇者4轉(3122),
    煉獄巫師1轉(3200),
    煉獄巫師2轉(3210),
    煉獄巫師3轉(3211),
    煉獄巫師4轉(3212),
    狂豹獵人1轉(3300),
    狂豹獵人2轉(3310),
    狂豹獵人3轉(3311),
    狂豹獵人4轉(3312),
    機甲戰神1轉(3500),
    機甲戰神2轉(3510),
    機甲戰神3轉(3511),
    機甲戰神4轉(3512),
    傑諾1轉(3600),
    傑諾2轉(3610),
    傑諾3轉(3611),
    傑諾4轉(3612),
    爆破者1轉(3700),
    爆破者2轉(3710),
    爆破者3轉(3711),
    爆破者4轉(3712),
    劍豪(4001),
    陰陽師(4002),
    劍豪1轉(4100),
    劍豪2轉(4110),
    劍豪3轉(4111),
    劍豪4轉(4112),
    陰陽師1轉(4200),
    陰陽師2轉(4210),
    陰陽師3轉(4211),
    陰陽師4轉(4212),
    米哈逸(5000),
    米哈逸1轉(5100),
    米哈逸2轉(5110),
    米哈逸3轉(5111),
    米哈逸4轉(5112),
    凱撒(6000),
    天使破壞者(6001),
    凱撒1轉(6100),
    凱撒2轉(6110),
    凱撒3轉(6111),
    凱撒4轉(6112),
    天使破壞者1轉(6500),
    天使破壞者2轉(6510),
    天使破壞者3轉(6511),
    天使破壞者4轉(6512),
    ADDITIONAL_SKILLS(9000),
    神之子JR(10000),
    神之子10100(10100),
    神之子10110(10110),
    神之子10111(10111),
    神之子(10112),
    幻獸師(11000),
    幻獸師1轉(11200),
    幻獸師2轉(11210),
    幻獸師3轉(11211),
    幻獸師4轉(11212),
    皮卡啾(13000),
    皮卡啾1轉(13100),
    凱內西斯(14000),
    凱內西斯1轉(14200),
    凱內西斯2轉(14210),
    凱內西斯3轉(14211),
    凱內西斯4轉(14212),
    未知(999999),;
    private final int jobid;

    MapleJob(int id) {
        this.jobid = id;
    }

    public static String getName(MapleJob mapleJob) {
        return mapleJob.name();
    }

    public static MapleJob getById(int id) {
        for (MapleJob l : values()) {
            if (l.getId() == id) {
                return l;
            }
        }
        return 未知;
    }

    public static boolean isExist(int id) {
        for (MapleJob job : values()) {
            if (job.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public static boolean is冒險家(final int job) {
        return job / 1000 == 0;
    }

    public static boolean is英雄(final int job) {
        return job / 10 == 11;
    }

    public static boolean is聖騎士(final int job) {
        return job / 10 == 12;
    }

    public static boolean is黑騎士(final int job) {
        return job / 10 == 13;
    }

    public static boolean is大魔導士_火毒(final int job) {
        return job / 10 == 21;
    }

    public static boolean is大魔導士_冰雷(final int job) {
        return job / 10 == 22;
    }

    public static boolean is主教(final int job) {
        return job / 10 == 23;
    }

    public static boolean is箭神(final int job) {
        return job / 10 == 31;
    }

    public static boolean is神射手(final int job) {
        return job / 10 == 32;
    }

    public static boolean is夜使者(final int job) {
        return job / 10 == 41;
    }

    public static boolean is暗影神偷(final int job) {
        return job / 10 == 42;
    }

    public static boolean is影武者(final int job) {
        return job / 10 == 43; // sub == 1 && job == 400
    }

    public static boolean is拳霸(final int job) {
        return job / 10 == 51;
    }

    public static boolean is槍神(final int job) {
        return job / 10 == 52;
    }

    public static boolean is重砲指揮官(final int job) {
        return job / 10 == 53 || job == 501 || job == 1;
    }

    public static boolean is蒼龍俠客(final int job) {
        return job / 10 == 57 || job == 508;
    }

    public static boolean is管理員(final int job) {
        return job == 800 || job == 900 || job == 910;
    }

    public static boolean is皇家騎士團(final int job) {
        return job / 1000 == 1;
    }

    public static boolean is聖魂劍士(final int job) {
        return job / 100 == 11;
    }

    public static boolean is烈焰巫師(final int job) {
        return job / 100 == 12;
    }

    public static boolean is破風使者(final int job) {
        return job / 100 == 13;
    }

    public static boolean is暗夜行者(final int job) {
        return job / 100 == 14;
    }

    public static boolean is閃雷悍將(final int job) {
        return job / 100 == 15;
    }

    public static boolean is英雄團(final int job) {
        return job / 1000 == 2;
    }

    public static boolean is狂狼勇士(final int job) {
        return job / 100 == 21 || job == 2000;
    }

    public static boolean is龍魔導士(final int job) {
        return job / 100 == 22 || job == 2001;
    }

    public static boolean is精靈遊俠(final int job) {
        return job / 100 == 23 || job == 2002;
    }

    public static boolean is幻影俠盜(final int job) {
        return job / 100 == 24 || job == 2003;
    }

    public static boolean is夜光(final int job) {
        return job / 100 == 27 || job == 2004;
    }

    public static boolean is隱月(int job) {
        return job / 100 == 25 || job == 2005;
    }

    public static boolean is末日反抗軍(final int job) {
        return job / 1000 == 3;
    }

    public static boolean is惡魔(final int job) {
        return is惡魔殺手(job) || is惡魔復仇者(job) || job == 3001;
    }

    public static boolean is惡魔殺手(final int job) {
        return job / 10 == 311 || job == 3100;
    }

    public static boolean is惡魔復仇者(int job) {
        return job / 10 == 312 || job == 3101;
    }

    public static boolean is煉獄巫師(final int job) {
        return job / 100 == 32;
    }

    public static boolean is狂豹獵人(final int job) {
        return job / 100 == 33;
    }

    public static boolean is機甲戰神(final int job) {
        return job / 100 == 35;
    }

    public static boolean is傑諾(final int job) {
        return job / 100 == 36 || job == 3002;
    }

    public static boolean is爆破者(final int job) {
        return job / 100 == 37;
    }

    public static boolean is曉の陣(int job) {
        return job / 1000 == 4;
    }

    public static boolean is劍豪(int job) {
        return job / 100 == 41 || job == 4001;
    }

    public static boolean is陰陽師(int job) {
        return job / 100 == 42 || job == 4002;
    }

    public static boolean is米哈逸(final int job) {
        return job / 100 == 51 || job == 5000;
    }

    public static boolean is超新星(final int job) {
        return job / 1000 == 6;
    }

    public static boolean is凱撒(final int job) {
        return job / 100 == 61 || job == 6000;
    }

    public static boolean is天使破壞者(final int job) {
        return job / 100 == 65 || job == 6001;
    }

    public static boolean is神之子(int job) {
        return job == 10000 || job == 10100 || job == 10110 || job == 10111 || job == 10112;
    }

    public static boolean is幻獸師(final int job) {
        return job / 100 == 112 || job == 11000;
    }

    public static boolean is皮卡啾(final int job) {
        return job / 100 == 131 || job == 13000;
    }

    public static boolean is凱內西斯(final int job) {
        return job / 100 == 142 || job == 14000;
    }

    public static boolean is劍士(final int job) {
        return getJobBranch(job) == 1;
    }

    public static boolean is法師(final int job) {
        return getJobBranch(job) == 2;
    }

    public static boolean is弓箭手(final int job) {
        return getJobBranch(job) == 3;
    }

    public static boolean is盜賊(final int job) {
        return getJobBranch(job) == 4 || getJobBranch(job) == 6;
    }

    public static boolean is海盜(final int job) {
        return getJobBranch(job) == 5 || getJobBranch(job) == 6;
    }

    public static short getBeginner(final short job) {
        if (job % 1000 < 10) {
            return job;
        }
        switch (job / 100) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 8:
            case 9:
                return (short) 初心者.getId();
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
                return (short) 貴族.getId();
            case 20:
                return (short) 傳說.getId();
            case 21:
                return (short) 傳說.getId();
            case 22:
                return (short) 龍魔導士.getId();
            case 23:
                return (short) 精靈遊俠.getId();
            case 24:
                return (short) 幻影俠盜.getId();
            case 25:
                return (short) 隱月.getId();
            case 27:
                return (short) 夜光.getId();
            case 31:
                return (short) 惡魔殺手.getId();
            case 36:
                return (short) 傑諾.getId();
            case 30:
            case 32:
            case 33:
            case 35:
                return (short) 市民.getId();
            case 40:
            case 41:
                return (short) 劍豪.getId();
            case 42:
                return (short) 陰陽師.getId();
            case 50:
            case 51:
                return (short) 米哈逸.getId();
            case 60:
            case 61:
                return (short) 凱撒.getId();
            case 65:
                return (short) 天使破壞者.getId();
            case 100:
            case 101:
                return (short) 神之子JR.getId();
            case 110:
            case 112:
                return (short) 幻獸師.getId();
            case 130:
            case 131:
                return (short) 皮卡啾.getId();
            case 140:
            case 142:
                return (short) 凱內西斯.getId();
        }
        return (short) 初心者.getId();
    }

    public static boolean isNotMpJob(int job) {
        return is惡魔(job) || is天使破壞者(job) || is神之子(job) || is陰陽師(job) || is凱內西斯(job);
    }

    public static boolean is初心者(int jobid) {
        if (jobid <= 5000) {
            if (jobid != 5000 && (jobid < 2001 || jobid > 2005 && (jobid <= 3000 || jobid > 3002 && (jobid <= 4000 || jobid > 4002)))) {
            } else {
                return true;
            }
        } else if (jobid >= 6000 && (jobid <= 6001 || jobid == 13000)) {
            return true;
        }
        boolean result = isJob12000(jobid);
        if (jobid % 1000 == 0 || jobid / 100 == 8000 || jobid == 8001 || result) {
            result = true;
        }
        return result;
    }

    public static boolean isJob12000(int job) {
        boolean result = isJob12000HighLv(job);
        if (isJob12000LowLv(job) || result) {
            result = true;
        }
        return result;
    }

    public static boolean isJob12000HighLv(int job) {
        return job == 12003 || job == 12004;
    }

    public static boolean isJob12000LowLv(int job) {
        return job == 12000 || job == 12001 || job == 12002;
    }

    public static boolean isJob8000(int job) {
        int v1 = GameConstants.getJobBySkill(job);
        return v1 >= 800000 && v1 <= 800099 || v1 == 8001;
    }

    public static boolean isJob9500(int job) {
        boolean result;
        if (job >= 0) {
            result = GameConstants.getJobBySkill(job) == 9500;
        } else {
            result = false;
        }
        return result;
    }

    public static int get轉數(int jobid) {
        int result;
        if (is初心者(jobid) || jobid % 100 == 0 || jobid == 501 || jobid == 3101 || jobid == 508) {
            result = 1;
        } else {
            int v1 = jobid % 10;
            int v2;
            if (jobid / 10 == 43) {
                v2 = v1 / 2 + 2;
            } else {
                v2 = v1 + 2;
            }
            if (v2 >= 2 && (v2 <= 4 || v2 <= 10 && is龍魔導士(jobid))) {
                result = v2;
            } else {
                result = 0;
            }
        }
        return result;
    }

    public static boolean isBeginner(final int job) {
        return getJobGrade(job) == 0;
    }

    public static boolean isSameJob(int job, int job2) {
        int jobNum = getJobGrade(job);
        int job2Num = getJobGrade(job2);
        // 對初心者判斷
        if (jobNum == 0 || job2Num == 0) {
            return getBeginner((short) job) == getBeginner((short) job2);
        }

        // 初心者過濾掉后, 對職業群進行判斷
        if (getJobGroup(job) != getJobGroup(job2)) {
            return false;
        }

        // 代碼特殊的單獨判斷
        if (MapleJob.is管理員(job) || MapleJob.is管理員(job)) {
            return MapleJob.is管理員(job2) && MapleJob.is管理員(job2);
        } else if (MapleJob.is重砲指揮官(job) || MapleJob.is重砲指揮官(job)) {
            return MapleJob.is重砲指揮官(job2) && MapleJob.is重砲指揮官(job2);
        } else if (MapleJob.is蒼龍俠客(job) || MapleJob.is蒼龍俠客(job)) {
            return MapleJob.is蒼龍俠客(job2) && MapleJob.is蒼龍俠客(job2);
        } else if (MapleJob.is惡魔復仇者(job) || MapleJob.is惡魔復仇者(job)) {
            return MapleJob.is惡魔復仇者(job2) && MapleJob.is惡魔復仇者(job2);
        }

        // 對一轉分支判斷(如 劍士 跟 黑騎)
        if (jobNum == 1 || job2Num == 1) {
            return job / 100 == job2 / 100;
        }

        return job / 10 == job2 / 10;
    }

    public static int getJobGroup(int job) {
        return job / 1000;
    }

    public static int getJobBranch(int job) {
        if (job / 100 == 27) {
            return 2;
        } else {
            return job % 1000 / 100;
        }
    }

    public static int getJobBranch2nd(int job) {
        if (job / 100 == 27) {
            return 2;
        } else {
            return job % 1000 / 100;
        }
    }

    public static int getJobGrade(int jobz) {
        int job = (jobz % 1000);
        if (job / 10 == 0) {
            return 0; //beginner
        } else if (job / 10 % 10 == 0) {
            return 1;
        } else {
            return job % 10 + 2;
        }
    }

    public int getId() {
        return this.jobid;
    }
}