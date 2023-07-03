package extensions.temporary;

public enum DirectionType {
    UNK(0),
    EXEC_TIME(1),
    EFFECT(2),
    ACTION(3),
    UNK4(4),
    UNK5(5),
    ;

    private final int value;

    private DirectionType(int value) {
        this.value = value;
    }

    public static DirectionType getType(int type) {
        for (DirectionType dt : values())
        {
            if (dt.getValue() == type) {
                return dt;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }
}