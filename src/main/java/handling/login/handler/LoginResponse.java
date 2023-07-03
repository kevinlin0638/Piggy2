package handling.login.handler;

/**
 * Created by Weber on 2017/9/9.
 */
public enum LoginResponse {

    LOGIN_SUCCESS(0x0),
    NOP(0x1),
    ACCOUNT_BLOCKED(0x3),
    WRONG_PASSWORD(0x4),
    NOT_REGISTERED(0x5),
    ALREADY_LOGGED_IN(0x7),
    IN_TRANSMISSION(0x7),
    SYSTEM_ERROR(0x8),
    SYSTEM_ERROR2(0x9),
    SYSTEM_OVERLOADED(0xA),
    IP_NOT_ALLOWED(0x22),
    LOGIN_DELAY(0x7),;
    private final int value;

    private LoginResponse(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}