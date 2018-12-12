package constants;

public enum WorldFlag {

    None((byte) 0),
    Event((byte) 1),
    New((byte) 2),
    Hot((byte) 3);

    final byte id;

    WorldFlag(byte flagId) {
        id = flagId;
    }

    public byte getId() {
        return id;
    }
}