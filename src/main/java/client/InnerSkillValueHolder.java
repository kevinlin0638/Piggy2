package client;

public class InnerSkillValueHolder {

    private final int skillId;
    private final int skillLevel;
    private final byte position;
    private final byte rank;

    public InnerSkillValueHolder(int skillId, int skillLevel, byte position, byte rank) {
        this.skillId = skillId;
        this.skillLevel = skillLevel;
        this.position = position;
        this.rank = rank;
    }

    public int getSkillId() {
        return skillId;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public byte getPosition() {
        return position;
    }

    public byte getRank() {
        return rank;
    }
}
