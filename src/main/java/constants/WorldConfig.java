package constants;

/**
 * Created by Weber on 2017/9/3.
 */
public enum WorldConfig {
    雪吉拉(0),
    菇菇寶貝(1),
    星光精靈(2),
    緞帶肥肥(3),
    藍寶(4),
    綠水靈(5),
    三眼章魚(6),
    木妖(7),
    火毒眼獸(8),
    蝴蝶精(9),
    巴洛古(10),
    海怒斯(11),
    電擊象(12),
    鯨魚號(13),
    皮卡啾(14),
    神獸(15),
    泰勒熊(16),
    寒霜冰龍(17),
    九尾妖狐(18),
    葛雷金剛(19),
    喵怪仙人(20);

    final int worldId;
    private WorldFlag flag;
    private int expRate;
    private int mesoRate;
    private int dropRate;
    private String eventMessage;
    private boolean worldSwitch;
    private int userLimit;
    private int chnnaelCount;
    private int maxCharacters;

    WorldConfig(int serverId) {
        worldId = serverId;
        this.expRate = 5;
        this.mesoRate = 1;
        this.dropRate = 1;
        this.eventMessage = "";
        this.worldSwitch = false;
    }

    public static WorldConfig getById(int id) {
        for (WorldConfig worldConfig : values()) {
            if (worldConfig.getWorldId() == id) {
                return worldConfig;
            }
        }
        return null;
    }

    public int getWorldId() {
        return worldId;
    }

    public int getExpRate() {
        return expRate;
    }

    public void setExpRate(int expRate) {
        this.expRate = expRate;
    }

    public int getMesoRate() {
        return mesoRate;
    }

    public void setMesoRate(int mesoRate) {
        this.mesoRate = mesoRate;
    }

    public int getDropRate() {
        return dropRate;
    }

    public void setDropRate(int dropRate) {
        this.dropRate = dropRate;
    }

    public String getEventMessage() {
        return eventMessage;
    }

    public void setEventMessage(String eventMessage) {
        this.eventMessage = eventMessage;
    }

    public WorldFlag getFlag() {
        return this.flag;
    }

    public void setFlag(WorldFlag flag) {
        this.flag = flag;
    }

    public boolean isOn() {
        return this.worldSwitch;
    }

    public void setWorldSwitch(boolean value) {
        this.worldSwitch = value;
    }

    public int getUserLimit() {
        return userLimit;
    }

    public void setUserLimit(int userLimit) {
        this.userLimit = userLimit;
    }

    public int getChnnaelCount() {
        return chnnaelCount;
    }

    public void setChnnaelCount(int chnnaelCount) {
        this.chnnaelCount = chnnaelCount;
    }

    public int getMaxCharacters() {
        return maxCharacters;
    }

    public void setMaxCharacters(int maxCharacters) {
        this.maxCharacters = maxCharacters;
    }
}
