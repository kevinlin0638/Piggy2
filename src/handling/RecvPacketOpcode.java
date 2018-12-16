/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
Matthias Butz <matze@odinms.de>
Jan Christian Meyer <vimes@odinms.de>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3
as published by the Free Software Foundation. You may not use, modify
or distribute this program under any other version of the
GNU Affero General Public License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package handling;

import tools.ExternalCodeShortTableGetter;
import tools.StringUtil;
import tools.WritableShortValueHolder;

import java.io.*;
import java.util.Properties;

public enum RecvPacketOpcode implements WritableShortValueHolder {
    // GENERIC

    PONG(false),
    CLIENT_HELLO(false),
    GET_SERVER(false),
    // LOGIN
    LOGIN_PASSWORD(false),
    SEND_ENCRYPTED(false),
    CLIENT_ERROR(false),
    SET_GENDER(false),
    SERVERLIST_REQUEST,
    TOS,
    FIND_FRIEND,
    YOUR_INFORMATION,
    REDISPLAY_SERVERLIST,
    CHARLIST_REQUEST,
    SERVERSTATUS_REQUEST,
    CHECK_CHAR_NAME,
    UPDATE_ENV,
    CREATE_CHAR,
    DELETE_CHAR,
    STRANGE_DATA,
    CHAR_SELECT,
    AUTH_SECOND_PASSWORD,
    VIEW_ALL_CHAR,
    VIEW_REGISTER_PIC,
    ENABLE_SPECIAL_CREATION,
    CREATE_SPECIAL_CHAR,
    MONSTER_BOOK_DROPS,
    VIEW_SELECT_PIC,
    PICK_ALL_CHAR,
    TWIN_DRAGON_EGG,
    PART_TIME_JOB,
    XMAS_SURPRISE,
    VICIOUS_HAMMER,
    USE_ALIEN_SOCKET,
    MAGIC_WHEEL,
    USE_ALIEN_SOCKET_RESPONSE,
    USE_NEBULITE_FUSION,
    CHAR_SELECT_NO_PIC,
    VIEW_SERVERLIST,
    RSA_KEY(false),
    CLIENT_START(false),
    CLIENT_FAILED(false),
    // CHANNEL
    PLAYER_LOGGEDIN(false),
    CHANGE_MAP,
    CHANGE_CHANNEL,
    CHANGE_ROOM_CHANNEL,
    ENTER_CASH_SHOP,
    MOVE_PLAYER,
    CANCEL_CHAIR,
    USE_TITLE,
    USE_CHAIR,
    CP_UserMeleeAttack,
    CP_UserShootAttack,
    CP_UserMagicAttack,
    CP_UserBodyAttack,
    TAKE_DAMAGE,
    GENERAL_CHAT,
    CLOSE_CHALKBOARD,
    USE_NEBULITE,
    FACE_EXPRESSION,
    USE_ITEMEFFECT,
    WHEEL_OF_FORTUNE,
    NPC_TALK,
    NPC_TALK_MORE,
    NPC_SHOP,
    STORAGE,
    USE_HIRED_MERCHANT,
    MERCH_ITEM_STORE,
    DUEY_ACTION,
    ITEM_SORT,
    ITEM_GATHER,
    ITEM_MOVE,
    ITEM_MAKER,
    USE_ITEM,
    CANCEL_ITEM_EFFECT,
    //USE_FISHING, // Some unknown value sent by client after fishing for 30 sec, ignored
    USE_SUMMON_BAG,
    PET_FOOD,
    USE_MOUNT_FOOD,
    USE_SCRIPTED_NPC_ITEM,
    USE_CASH_ITEM,
    USE_CATCH_ITEM,
    USE_SKILL_BOOK,
    USE_RETURN_SCROLL,
    USE_UPGRADE_SCROLL,
    DISTRIBUTE_AP,
    AUTO_ASSIGN_AP,
    HEAL_OVER_TIME,
    DISTRIBUTE_SP,
    SPECIAL_MOVE,
    CANCEL_BUFF,
    SKILL_EFFECT,
    MESO_DROP,
    GIVE_FAME,
    CHAR_INFO_REQUEST,
    SPAWN_PET,
    CANCEL_DEBUFF,
    CHANGE_MAP_SPECIAL,
    USE_INNER_PORTAL,
    TROCK_ADD_MAP,
    QUEST_ACTION,
    SKILL_MACRO,
    REWARD_ITEM,
    USE_TREASUER_CHEST,
    PARTYCHAT,
    WHISPER,
    SPOUSE_CHAT,
    MESSENGER,
    PLAYER_INTERACTION,
    PARTY_OPERATION,
    DENY_PARTY_REQUEST,
    GUILD_OPERATION,
    DENY_GUILD_REQUEST,
    BUDDYLIST_MODIFY,
    NOTE_ACTION,
    USE_DOOR,
    CHANGE_KEYMAP,
    ENTER_MTS,
    ALLIANCE_OPERATION,
    DENY_ALLIANCE_REQUEST,
    REQUEST_FAMILY,
    OPEN_FAMILY,
    FAMILY_OPERATION,
    DELETE_JUNIOR,
    DELETE_SENIOR,
    ACCEPT_FAMILY,
    USE_FAMILY,
    FAMILY_PRECEPT,
    FAMILY_SUMMON,
    CYGNUS_SUMMON,
    ARAN_COMBO,
    BBS_OPERATION,
    TRANSFORM_PLAYER,
    MOVE_PET,
    PET_CHAT,
    PET_COMMAND,
    PET_LOOT,
    PET_AUTO_POT,
    MOVE_SUMMON,
    CP_SummonedAttack,
    DAMAGE_SUMMON,
    MOVE_LIFE,
    AUTO_AGGRO,
    FRIENDLY_DAMAGE,
    MONSTER_BOMB,
    HYPNOTIZE_DMG,
    NPC_ACTION,
    ITEM_PICKUP,
    DAMAGE_REACTOR,
    SNOWBALL,
    LEFT_KNOCK_BACK,
    COCONUT,
    MONSTER_CARNIVAL,
    SHIP_OBJECT,
    CS_UPDATE,
    BUY_CS_ITEM,
    COUPON_CODE,
    MAPLETV,
    MOVE_DRAGON,
    REPAIR,
    REPAIR_ALL,
    TOUCHING_MTS,
    USE_MAGNIFY_GLASS,
    USE_POTENTIAL_SCROLL,
    USE_EQUIP_SCROLL,
    GAME_POLL,
    OWL,
    OWL_WARP,
    //XMAS_SURPRISE, //header -> uniqueid(long) is entire structure
    USE_OWL_MINERVA,
    RPS_GAME,
    UPDATE_QUEST,
    //QUEST_ITEM, //header -> questid(int) -> 1/0(byte, open or close)
    USE_ITEM_QUEST,
    FOLLOW_REQUEST,
    FOLLOW_REPLY,
    MOB_NODE,
    DISPLAY_NODE,
    TOUCH_REACTOR,
    RING_ACTION,
    SOLOMON,
    GACH_EXP,
    EXPEDITION_OPERATION,
    EXPEDITION_LISTING,
    PARTY_SEARCH_SQUAD,
    PARTY_SEARCH_SQUADCREW,
    PARTY_SEARCH_CREW,
    PARTY_SEARCH_TEAM,
    USE_TELE_ROCK,
    SUB_SUMMON,
    USE_MECH_DOOR,
    MECH_CANCEL,
    REMOVE_SUMMON,
    AUTO_FOLLOW_REPLY,
    REPORT,
    MOB_BOMB,
    CREATE_ULTIMATE,
    PAM_SONG,
    INNER_CIRCULATOR,
    USE_POT,
    CLEAR_POT,
    FEED_POT,
    CURE_POT,
    CRAFT_MAKE,
    CRAFT_DONE,
    CRAFT_EFFECT,
    STOP_HARVEST,
    START_HARVEST,
    MOVE_BAG,
    USE_BAG,
    CHANGE_SET,
    GET_BOOK_INFO,
    MOVE_ANDROID,
    FACE_ANDROID,
    REISSUE_MEDAL,
    CLICK_REACTOR,
    USE_RECIPE,
    USE_FAMILIAR,
    SPAWN_FAMILIAR,
    RENAME_FAMILIAR,
    MOVE_FAMILIAR,
    TOUCH_FAMILIAR,
    ATTACK_FAMILIAR,
    SIDEKICK_OPERATION,
    DENY_SIDEKICK_REQUEST,
    ALLOW_PARTY_INVITE,
    PROFESSION_INFO,
    QUICK_SLOT,
    MAKE_EXTRACTOR,
    USE_COSMETIC,
    USE_FLAG_SCROLL,
    SWITCH_BAG,
    REWARD_POT,
    PVP_INFO,
    ENTER_PVP,
    ENTER_PVP_PARTY,
    LEAVE_PVP,
    PVP_RESPAWN,
    EQUIP_STOLEN_SKILL,
    UPDATE_STOLEN_SKILL,
    SKILL_SWIPE_REQUEST,
    CHANGE_CODEX_SET,
    CHARACTER_CARD,
    PVP_ATTACK,
    PVP_SUMMON,
    PUBLIC_NPC,
    ENTER_AZWAN,
    ENTER_AZWAN_EVENT,
    LEAVE_AZWAN,
    MTS_TAB,
    MCAUGHTEFF,
    UNKNOWN;

    static {
        reloadValues();
    }

    private short code = -2;
    private boolean checkState;

    RecvPacketOpcode() {
        this.checkState = true;
    }

    RecvPacketOpcode(final boolean CheckState) {
        this.checkState = CheckState;
    }

    public static Properties getDefaultProperties() throws FileNotFoundException, IOException {
        Properties props = new Properties();
        FileInputStream fileInputStream = new FileInputStream("recv.properties");
        props.load(fileInputStream);
        fileInputStream.close();
        return props;
    }

    public static final void reloadValues() {
        String fileName = "recv.properties";
        Properties props = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(fileName); BufferedReader br = new BufferedReader(new InputStreamReader(fileInputStream, StringUtil.codeString(fileName)))) {
            props.load(br);
        } catch (IOException ex) {
            InputStream in = RecvPacketOpcode.class.getClassLoader().getResourceAsStream("properties/" + fileName);
            if (in == null) {
                System.err.println("錯誤: 未加載 " + fileName + " 檔案");
                return;
            }
            try {
                props.load(in);
                in.close();
            } catch (IOException e) {
                throw new RuntimeException("加載 " + fileName + " 檔案出錯", e);
            }
        }
        ExternalCodeShortTableGetter.populateValues(props, values());
    }

    public static String nameOf(short value) {
        for (RecvPacketOpcode header : RecvPacketOpcode.values()) {
            if (header.get() == value) {
                return header.name();
            }
        }
        return "UNKNOWN";
    }

    public static boolean isSkipLog(RecvPacketOpcode opcode) {
        switch (opcode.toString()) {
            case "NPC_ACTION"://# NPC動作...OK!(145)
            case "MOVE_PLAYER"://# 玩家移動...OK!(145)
            case "MOVE_SUMMON"://# 召喚獸移動...OK!(145)
            case "MOVE_DRAGON"://# 神龍移動...OK!(145)
            case "MOVE_ANDROID"://# 機器人移動...OK!(145)
            case "MOVE_LIFE"://# 怪物移動...OK!(145)
            case "HEAL_OVER_TIME"://# 自動恢復...OK!(145)
            //case "CP_UserMeleeAttack":
            case "TAKE_DAMAGE"://# 受到傷害...OK!(145)
            case "AUTO_AGGRO":
            //case "SPECIAL_MOVE":
            //case "USE_ITEM":
            //case "CHANGE_MAP":
            case "GENERAL_CHAT":
            case "ARAN_COMBO":
            case "NPC_TALK":
            case "NPC_TALK_MORE":
               
            case "CHANGE_KEYMAP":
                return true;
            default:
                return false;
        }
    }

    @Override
    public final short get() {
        return code;
    }

    public short getValue() {
        return code;
    }

    @Override
    public void set(short code) {
        this.code = code;
    }

    public final boolean NeedsChecking() {
        return checkState;
    }
}
