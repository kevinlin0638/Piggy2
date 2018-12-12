package client;

public enum MapleStat {

    SKIN(0x1), // byte
    FACE(0x2), // int
    HAIR(0x4), // int
    LEVEL(0x10), // byte
    JOB(0x20), // short
    STR(0x40), // short
    DEX(0x80), // short
    INT(0x100), // short
    LUK(0x200), // short
    HP(0x400), // int
    MAX_HP(0x800), // int
    MP(0x1000), // int
    MAX_MP(0x2000), // int
    AVAILABLE_AP(0x4000), // short
    AVAILABLE_SP(0x8000), // short (depends)
    EXP(0x10000), // int
    FAME(0x20000), // int
    MESO(0x40000), // int

    PET(0x180008), // Pets: 0x8 + 0x80000 + 0x100000  [3 longs]

    FATIGUE(0x80000), // byte
    TRAIT_CHARISMA(0x100000), // int
    TRAIT_INSIGHT(0x200000), // int
    TRAIT_WILL(0x400000), // int
    TRAIT_CRAFT(0x800000), // int
    TRAIT_SENSE(0x1000000), //  int
    TRAIT_CHARM(0x2000000), // int

    TRAIT_LIMIT(0x4000000), // 12 bytes

    BATTLE_EXP(0x8000000), // int
    BATTLE_RANK(0x10000000), // byte
    BATTLE_POINTS(0x20000000),
    ICE_GAGE(0x40000000), // byte byte
    VIRTUE(0x80000000), // int
    GACHAPON_EXP(0x100000000L), // 4 byte
    GENDER(0x200000000L); // int

    private final long i;

    private MapleStat(long i) {
        this.i = i;
    }

    public static MapleStat getByValue(final long value) {
        for (final MapleStat stat : MapleStat.values()) {
            if (stat.i == value) {
                return stat;
            }
        }
        return null;
    }

    public long getValue() {
        return i;
    }

    public static enum Temp {

        STR(0x1),
        DEX(0x2),
        INT(0x4),
        LUK(0x8),
        WATK(0x10),
        WDEF(0x20),
        MATK(0x40),
        MDEF(0x80),
        ACC(0x100),
        AVOID(0x200),
        SPEED(0x400), // byte
        JUMP(0x800), // byte
        UNKNOWN(0x1000); // byte

        private final int i;

        private Temp(int i) {
            this.i = i;
        }

        public int getValue() {
            return i;
        }
    }
}
