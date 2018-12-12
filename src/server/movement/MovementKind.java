package server.movement;

/**
 * Created by Weber on 2017/9/12.
 */
public enum MovementKind {

    PLAYER_MOVEMENT(0x01),
    MOB_MOVEMENT(0x02),
    PET_MOVEMENT(0x03),
    SUMMON_MOVEMENT(0x04),
    DRAGON_MOVEMENT(0x05),
    FAMILIAR_MOVMENT(0x06);

    int type;

    MovementKind(int type) {
        this.type = type;
    }

    public int getValue() {
        return this.type;
    }
}
