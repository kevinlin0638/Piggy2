package server;


import server.status.MapleBuffStatus;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import server.life.MobSkill;
import server.life.MobSkillFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MapleCarnivalFactory {

    private final static MapleCarnivalFactory instance = new MapleCarnivalFactory();
    private final Map<Integer, MCSkill> skills = new HashMap<>();
    private final Map<Integer, MCSkill> guardians = new HashMap<>();
    private final MapleDataProvider dataRoot = MapleDataProviderFactory.getDataProvider("Skill.wz");
    private final Collection<MapleBuffStatus> allBuffStatus = new LinkedList<>();

    public MapleCarnivalFactory() {
        //whoosh
        initialize();
    }

    public static MapleCarnivalFactory getInstance() {
        return instance;
    }

    private void initialize() {
        if (!skills.isEmpty()) {
            return;
        }
        for (MapleData z : dataRoot.getData("MCSkill.img")) {
            skills.put(Integer.parseInt(z.getName()), new MCSkill(MapleDataTool.getInt("spendCP", z, 0), MapleDataTool.getInt("mobSkillID", z, 0), MapleDataTool.getInt("level", z, 0), MapleDataTool.getInt("target", z, 1) > 1));
        }
        for (MapleData z : dataRoot.getData("MCGuardian.img")) {
            guardians.put(Integer.parseInt(z.getName()), new MCSkill(MapleDataTool.getInt("spendCP", z, 0), MapleDataTool.getInt("mobSkillID", z, 0), MapleDataTool.getInt("level", z, 0), true));
        }
        allBuffStatus.add(MapleBuffStatus.CURSE);
        allBuffStatus.add(MapleBuffStatus.FREEZE);
        allBuffStatus.add(MapleBuffStatus.BLIND);
        allBuffStatus.add(MapleBuffStatus.SHADOW);
        allBuffStatus.add(MapleBuffStatus.POTION);
        allBuffStatus.add(MapleBuffStatus.WEAKEN);
        allBuffStatus.add(MapleBuffStatus.DARKNESS);
        allBuffStatus.add(MapleBuffStatus.SEAL);
        allBuffStatus.add(MapleBuffStatus.POISON);
        allBuffStatus.add(MapleBuffStatus.STUN);
        allBuffStatus.add(MapleBuffStatus.WEIRD_FLAME);
        allBuffStatus.add(MapleBuffStatus.REVERSE_DIRECTION);
        allBuffStatus.add(MapleBuffStatus.ZOMBIFY);
        allBuffStatus.add(MapleBuffStatus.SEDUCE);
        allBuffStatus.add(MapleBuffStatus.MORPH);
        allBuffStatus.add(MapleBuffStatus.SLOW);
    }

    public MCSkill getSkill(final int id) {
        return skills.get(id);
    }

    public MCSkill getGuardian(final int id) {
        return guardians.get(id);
    }

    public MapleBuffStatus getRandomDiasease() {
        while (true) {
            for (MapleBuffStatus dis : allBuffStatus) {
                if (Randomizer.nextInt(allBuffStatus.size()) == 0) {
                    return dis;
                }
            }
        }
    }

    public static class MCSkill {

        public int cpLoss, skillid, level;
        public boolean targetsAll;

        public MCSkill(int _cpLoss, int _skillid, int _level, boolean _targetsAll) {
            cpLoss = _cpLoss;
            skillid = _skillid;
            level = _level;
            targetsAll = _targetsAll;
        }

        public MobSkill getSkill() {
            return MobSkillFactory.getMobSkill(skillid, 1); //level?
        }

        public MapleBuffStatus getDisease() {
            if (skillid <= 0) {
                return MapleCarnivalFactory.getInstance().getRandomDiasease();
            }
            return MobSkill.getBuffStatus(skillid);
        }
    }
}
