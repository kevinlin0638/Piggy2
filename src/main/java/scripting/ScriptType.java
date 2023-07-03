package scripting;

public enum ScriptType {

    NPC(-1),
    QUEST_START(0),
    QUEST_END(1),
    ITEM(-1),
    ON_FIRST_USER_ENTER(-1),
    ON_USER_ENTER(-1),
    PORTAL(-2),
    REACTOR(-2),
    EVENT(-2);
    private final byte code;

    private ScriptType(int value) {
        code = (byte) value;
    }

    public byte getValue() {
        return code;
    }
}