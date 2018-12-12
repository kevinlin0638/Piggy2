package scripting;

public enum NPCTalkType {

    NEXT_PREV(0x0),
    IMAGE(0x1),
    YES_NO(0x2),
    INPUT_TEXT(0x3),
    INPUT_NUMBER(0x4),
    SELECTION(0x5),
    SPEED_QUIZE_TEXT(0x6),
    SPEED_QUIZE_ID(0x7),
    UNK_8(0x8),
    AVATAR(0x9),
    ANDROID(0xA),
    PET(0xB),
    PET_ALL(0xC),
    ACCEPT_DECLINE(0xE),
    MULTI_TEXT(0xF),
    UNK_10(0x10),
    DIRECTION_SCRIPT_ACTION(0x14),
    UNK_15(0x15);

    private final byte type;

    private NPCTalkType(int type) {
        this.type = (byte) type;
    }

    public static NPCTalkType getNPCTalkType(int type) {
        for (NPCTalkType dt : values())
        {
            if (dt.getType() == type) {
                return dt;
            }
        }
        return null;
    }

    public byte getType() {
        return type;
    }
}