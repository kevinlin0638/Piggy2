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
package handling.world;

import client.*;
import client.MapleTrait.MapleTraitType;
import client.buddy.BuddyListEntry;
import client.buddy.CharacterNameAndId;
import client.inventory.Item;
import client.inventory.MapleImp;
import client.inventory.MapleMount;
import client.inventory.MaplePet;
import client.skill.Skill;
import client.skill.SkillEntry;
import server.quest.MapleQuest;
import tools.types.Pair;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

public class CharacterTransfer implements Externalizable {

    public final Map<MapleTraitType, Integer> traits = new EnumMap<>(MapleTraitType.class);
    public final Map<CharacterNameAndId, Boolean> buddies = new LinkedHashMap<>();
    public final Map<Integer, Object> Quest = new LinkedHashMap<>(); // Questid instead of MapleQuest, as it's huge. Cant be transporting MapleQuest.java
    public final Map<Integer, SkillEntry> Skills = new LinkedHashMap<>(); // Skillid instead of Skill.java, as it's huge. Cant be transporting Skill.java and MapleStatEffect.java
    /*Start of Custom Feature*/
    public final Map<Integer, CardData> cardsInfo = new LinkedHashMap<>();
    public int characterid, accountid, fame, pvpExp, pvpPoints,
            meso, hair, face, demonMarking, mapid, guildid,
            partyid, messengerid, nxcredit, nxprepaid, MaplePoints, gmtext, charToggle,
            mount_itemid, mount_exp, points, vpoints, marriageId, maxhp, maxmp, hp, mp,
            JQLevel, JQExp, pvpKills, pvpDeaths, wantFame, autoAP,
            familyid, seniorid, junior1, junior2, currentrep, totalrep, battleshipHP, gachexp, guildContribution, totalWins, totalLosses, buddysize, remainingAp;
    public byte channel, gender, gmLevel, guildrank, alliancerank,
            fairyExp, world, initialSpawnPoint, skinColor, mount_level, mount_Fatigue, subcategory;
    public long dps, lastfametime, TranferTime;
    public String name, accountname, BlessOfFairy, BlessOfEmpress, chalkboard, tempIP;
    public short level, str, dex, int_, luk, hpApUsed, job, fatigue;
    public Object inventorys, skillmacro, storage, cs, anticheat;
    public int[] savedlocation, wishlist, rocks, remainingSp, regrocks, hyperrocks;
    public byte[] petStore;
    public int exp;
    public MapleImp[] imps;
    public Map<Integer, Integer> mbook;
    public Map<Byte, Integer> reports = new LinkedHashMap<>();
    public Map<Integer, Pair<Byte, Integer>> keymap;
    public Map<Integer, MonsterFamiliar> familiars;
    public List<Integer> famedcharacters = null, extendedSlots = null;
    public List<Item> rebuy = null;
    public Map<Integer, String> InfoQuest;
    public int cardStack;
    /*All custom shit declare here*/
    public int reborns, apstorage, MSIPoints, clanId;
    public boolean muted, autoToken, elf;
    public Calendar unmuteTime = null;
    public int dgm;
    public int honourexp;
    public InnerSkillValueHolder[] innerSkills;
    public int honourlevel;
    public int noacc;
    public int location, birthday, found, todo, redeemhn;
    public int occupationId, occupationExp, occupationLevel;

    /*End of Custom Feature*/
    public CharacterTransfer() {
        famedcharacters = new ArrayList<>();
        extendedSlots = new ArrayList<>();
        rebuy = new ArrayList<>();
        InfoQuest = new LinkedHashMap<>();
        keymap = new LinkedHashMap<>();
        familiars = new LinkedHashMap<>();
        mbook = new LinkedHashMap<>();
        innerSkills = new InnerSkillValueHolder[3];
    }

    public CharacterTransfer(final MapleCharacter chr) {
        this.characterid = chr.getId();
        this.accountid = chr.getAccountID();
        this.accountname = chr.getClient().getAccountName();
        this.channel = (byte) chr.getClient().getChannel();
        this.nxcredit = chr.getCSPoints(1);
        this.MaplePoints = chr.getCSPoints(2);
        this.nxprepaid = chr.getCSPoints(4);

        this.redeemhn = chr.getHN();
        this.vpoints = chr.getVPoints();
        this.name = chr.getName();
        this.fame = chr.getFame();
        this.gender = (byte) chr.getGender();
        this.level = chr.getLevel();
        this.str = chr.getStat().getStr();
        this.dex = chr.getStat().getDex();
        this.int_ = chr.getStat().getInt();
        this.luk = chr.getStat().getLuk();
        this.hp = chr.getStat().getHp();
        this.mp = chr.getStat().getMp();
        this.maxhp = chr.getStat().getMaxHp();
        this.maxmp = chr.getStat().getMaxMp();
        this.exp = chr.getExp();
        this.hpApUsed = chr.getHpApUsed();
        this.remainingAp = chr.getRemainingAp();
        this.remainingSp = chr.getRemainingSps();
        this.meso = chr.getMeso();
        this.pvpExp = chr.getTotalBattleExp();
        this.pvpPoints = chr.getBattlePoints();
        this.pvpKills = chr.getPvpKills();
        this.pvpDeaths = chr.getPvpDeaths();
        /*Start of Custom Feature*/
        this.reborns = chr.getReborns();
        this.apstorage = chr.getAPS();
        this.MSIPoints = chr.getMSIPoints();
        this.noacc = chr.gethiddenGM();
        this.muted = chr.isMuted();
        this.unmuteTime = chr.getUnmuteTime();
        this.dgm = chr.getDGM();
        this.cardStack = chr.getCardStack();
        //  this.occupation = chr.getOccupation();
        this.occupationId = chr.getOccId();
        this.occupationExp = chr.getOccEXP();
        this.occupationLevel = chr.getOccLevel();
        //this.JQId = chr.getJQId();
        this.JQLevel = chr.getJQLevel();
        this.JQExp = chr.getJQExp();
        this.wantFame = chr.wantFame();
        this.gmtext = chr.getGMText();
        this.charToggle = chr.getCharToggle();
        this.dps = chr.getDPS();
        this.autoAP = chr.getAutoAP();
        this.autoToken = chr.getAutoToken();
        this.elf = chr.getElf();
        /*End of Custom Feature*/
        this.skinColor = chr.getSkinColor();
        this.job = chr.getJob();
        this.hair = chr.getHair();
        this.face = chr.getFace();
        this.demonMarking = chr.getDemonMarking();
        this.mapid = chr.getMapId();
        this.initialSpawnPoint = chr.getInitialSpawnPoint();
        this.marriageId = chr.getMarriageId();
        this.world = chr.getWorld();
        this.guildid = chr.getGuildId();
        this.guildrank = (byte) chr.getGuildRank();
        this.guildContribution = chr.getGuildContribution();
        this.alliancerank = (byte) chr.getAllianceRank();
        this.gmLevel = (byte) chr.getGMLevel();
        this.points = chr.getPoints();
        this.fairyExp = chr.getFairyExp();
        this.petStore = chr.getPetStores();
        this.subcategory = chr.getSubcategory();
        this.imps = chr.getImps();
        this.fatigue = (short) chr.getFatigue();
        this.currentrep = chr.getCurrentRep();
        this.totalrep = chr.getTotalRep();
        this.familyid = chr.getFamilyId();
        this.totalWins = chr.getTotalWins();
        this.totalLosses = chr.getTotalLosses();
        this.seniorid = chr.getSeniorId();
        this.birthday = chr.getBIRTHDAY();
        this.location = chr.getLOCATION();
        this.todo = chr.getTODO();
        this.found = chr.getFOUND();
        this.junior1 = chr.getJunior1();
        this.junior2 = chr.getJunior2();
        this.battleshipHP = chr.currentBattleshipHP();
        this.gachexp = chr.getGachExp();
        this.familiars = chr.getFamiliars();
        this.tempIP = chr.getClient().getTempIP();
        this.rebuy = chr.getRebuy();
        boolean uneq = false;
        for (int i = 0; i < this.petStore.length; i++) {
            final MaplePet pet = chr.getPet(i);
            if (this.petStore[i] == 0) {
                this.petStore[i] = (byte) -1;
            }
            if (pet != null) {
                uneq = true;
                this.petStore[i] = (byte) Math.max(this.petStore[i], pet.getInventoryPosition());
            }

        }
        if (uneq) {
            chr.unequipAllPets();
        }

        for (MapleTraitType t : MapleTraitType.values()) {
            this.traits.put(t, chr.getTrait(t).getTotalExp());
        }
        for (final BuddyListEntry qs : chr.getBuddylist().getBuddies()) {
            this.buddies.put(new CharacterNameAndId(qs.getCharacterId(), qs.getName(), qs.getGroup()), qs.isVisible());
        }
        this.buddysize = chr.getBuddyCapacity();

        this.partyid = chr.getParty() == null ? -1 : chr.getParty().getId();

        if (chr.getMessenger() != null) {
            this.messengerid = chr.getMessenger().getId();
        } else {
            this.messengerid = 0;
        }

        this.InfoQuest = chr.getInfoQuest_Map();

        for (final Map.Entry<MapleQuest, MapleQuestStatus> qs : chr.getQuest_Map().entrySet()) {
            this.Quest.put(qs.getKey().getId(), qs.getValue());
        }

        this.mbook = chr.getMonsterBook().getCards();
        this.inventorys = chr.getInventorys();

        for (final Map.Entry<Skill, SkillEntry> qs : chr.getSkills().entrySet()) {
            this.Skills.put(qs.getKey().getId(), qs.getValue());
        }
/* 224 */
        for (Map.Entry<Integer, CardData> ii : chr.getCharacterCard().getCards().entrySet()) {
/* 225 */
            this.cardsInfo.put(ii.getKey(), ii.getValue());
/*     */
        }
        this.BlessOfFairy = chr.getBlessOfFairyOrigin();
        this.BlessOfEmpress = chr.getBlessOfEmpressOrigin();
        this.chalkboard = chr.getChalkboard();
        this.skillmacro = chr.getMacros();
        this.keymap = chr.getKeyLayout().Layout();
        this.savedlocation = chr.getSavedLocations();
        this.wishlist = chr.getWishlist();
        this.rocks = chr.getRocks();
        this.regrocks = chr.getRegRocks();
        this.hyperrocks = chr.getHyperRocks();
        this.famedcharacters = chr.getFamedCharacters();
        this.lastfametime = chr.getLastFameTime();
        this.storage = chr.getStorage();
        this.cs = chr.getCashInventory();
        this.extendedSlots = chr.getExtendedSlots();
        this.honourexp = chr.getHonourExp();
        this.honourlevel = chr.getHonourLevel();
        this.innerSkills = chr.getInnerSkills();
        final MapleMount mount = chr.getMount();
        this.mount_itemid = mount.getItemId();
        this.mount_Fatigue = mount.getFatigue();
        this.mount_level = mount.getLevel();
        this.mount_exp = mount.getExp();
        TranferTime = System.currentTimeMillis();
    }

    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this.characterid = in.readInt();
        this.accountid = in.readInt();
        this.accountname = in.readUTF();
        this.channel = in.readByte();
        this.nxcredit = in.readInt();
        this.MaplePoints = in.readInt();
        this.nxprepaid = in.readInt();
        this.redeemhn = in.readInt();
        this.name = in.readUTF();
        this.fame = in.readInt();
        this.gender = in.readByte();
        this.level = in.readShort();
        this.str = in.readShort();
        this.dex = in.readShort();
        this.int_ = in.readShort();
        this.luk = in.readShort();
        this.hp = in.readInt();
        this.mp = in.readInt();
        this.maxhp = in.readInt();
        this.maxmp = in.readInt();
        this.exp = in.readInt();
        this.hpApUsed = in.readShort();
        this.remainingAp = in.readShort();
        this.remainingSp = new int[in.readByte()];
        for (int i = 0; i < this.remainingSp.length; i++) {
            this.remainingSp[i] = in.readInt();
        }
        this.meso = in.readInt();
        this.skinColor = in.readByte();
        this.job = in.readShort();
        this.hair = in.readInt();
        this.face = in.readInt();
        this.demonMarking = in.readInt();
        this.mapid = in.readInt();
        this.initialSpawnPoint = in.readByte();
        this.world = in.readByte();
        this.guildid = in.readInt();
        this.guildrank = in.readByte();
        this.guildContribution = in.readInt();
        this.alliancerank = in.readByte();
        this.gmLevel = in.readByte();
        this.points = in.readInt();
        this.vpoints = in.readInt();
        if (in.readByte() == 1) {
            this.BlessOfFairy = in.readUTF();
        } else {
            this.BlessOfFairy = null;
        }
        if (in.readByte() == 1) {
            this.BlessOfEmpress = in.readUTF();
        } else {
            this.BlessOfEmpress = null;
        }
        if (in.readByte() == 1) {
            this.chalkboard = in.readUTF();
        } else {
            this.chalkboard = null;
        }
        this.skillmacro = in.readObject();
        this.lastfametime = in.readLong();
        this.storage = in.readObject();
        this.cs = in.readObject();
        this.mount_itemid = in.readInt();
        this.mount_Fatigue = in.readByte();
        this.mount_level = in.readByte();
        this.mount_exp = in.readInt();
        this.partyid = in.readInt();
        this.messengerid = in.readInt();
        this.inventorys = in.readObject();
        this.fairyExp = in.readByte();
        this.subcategory = in.readByte();
        this.fatigue = in.readShort();
        this.marriageId = in.readInt();
        this.familyid = in.readInt();
        this.seniorid = in.readInt();
        this.junior1 = in.readInt();
        this.junior2 = in.readInt();
        this.currentrep = in.readInt();
        this.totalrep = in.readInt();
        this.battleshipHP = in.readInt();
        this.gachexp = in.readInt();
        this.totalWins = in.readInt();
        this.totalLosses = in.readInt();
        this.anticheat = in.readObject();
        this.tempIP = in.readUTF();
        this.pvpExp = in.readInt();
        this.pvpPoints = in.readInt();
        this.pvpKills = in.readInt();
        this.pvpDeaths = in.readInt();
        /*Start of Custom Feature*/
        this.reborns = in.readInt();
        this.apstorage = in.readInt();
        this.MSIPoints = in.readInt();
        this.noacc = in.readInt();
        this.birthday = in.readInt();
        this.location = in.readInt();
        this.todo = in.readInt();
        this.found = in.readInt();
        this.muted = in.readBoolean();
        this.unmuteTime = (Calendar) in.readObject();
        this.dgm = in.readInt();
        this.cardStack = in.readInt();
        //  this.occupation = chr.getOccupation();
        this.occupationId = in.readInt();
        this.occupationExp = in.readInt();
        this.occupationLevel = in.readInt();
        //this.JQId = chr.getJQId();
        this.JQLevel = in.readInt();
        this.JQExp = in.readInt();
        this.wantFame = in.readInt();
        this.gmtext = in.readInt();
        this.charToggle = in.readInt();
        this.dps = in.readLong();
        this.autoAP = in.readInt();
        this.autoToken = in.readBoolean();
        this.elf = in.readBoolean();
        this.clanId = in.readInt();
        this.honourexp = in.readInt();
        this.honourlevel = in.readInt();
        this.innerSkills = (InnerSkillValueHolder[]) in.readObject();
        /*End of Custom Feature*/

        final int mbooksize = in.readShort();
        for (int i = 0; i < mbooksize; i++) {
            this.mbook.put(in.readInt(), in.readInt());
        }

        final int skillsize = in.readShort();
        for (int i = 0; i < skillsize; i++) {
            this.Skills.put(in.readInt(), new SkillEntry(in.readInt(), in.readByte(), in.readLong(), in.readInt(), in.readByte()));
        }

        int cardSize = in.readByte();
        for (int i = 0; i < cardSize; i++) {
            this.cardsInfo.put(in.readInt(), new CardData(in.readInt(), in.readShort(), in.readShort()));
        }

        this.buddysize = in.readInt();
        final short addedbuddysize = in.readShort();
        for (int i = 0; i < addedbuddysize; i++) {
            buddies.put(new CharacterNameAndId(in.readInt(), in.readUTF(), in.readUTF()), in.readBoolean());
        }

        final int questsize = in.readShort();
        for (int i = 0; i < questsize; i++) {
            this.Quest.put(in.readInt(), in.readObject());
        }

        final int rzsize = in.readByte();
        for (int i = 0; i < rzsize; i++) {
            this.reports.put(in.readByte(), in.readInt());
        }

        final int famesize = in.readByte(); //max 31
        for (int i = 0; i < famesize; i++) {
            this.famedcharacters.add(in.readInt());
        }

        final int esize = in.readByte();
        for (int i = 0; i < esize; i++) {
            this.extendedSlots.add(in.readInt());
        }

        final int savesize = in.readByte();
        savedlocation = new int[savesize];
        for (int i = 0; i < savesize; i++) {
            savedlocation[i] = in.readInt();
        }

        final int wsize = in.readByte();
        wishlist = new int[wsize];
        for (int i = 0; i < wsize; i++) {
            wishlist[i] = in.readInt();
        }

        final int rsize = in.readByte();
        rocks = new int[rsize];
        for (int i = 0; i < rsize; i++) {
            rocks[i] = in.readInt();
        }

        final int resize = in.readByte();
        regrocks = new int[resize];
        for (int i = 0; i < resize; i++) {
            regrocks[i] = in.readInt();
        }

        final int hesize = in.readByte();
        hyperrocks = new int[resize];
        for (int i = 0; i < hesize; i++) {
            hyperrocks[i] = in.readInt();
        }

        final int infosize = in.readShort();
        for (int i = 0; i < infosize; i++) {
            this.InfoQuest.put(in.readInt(), in.readUTF());
        }

        final int keysize = in.readInt();
        for (int i = 0; i < keysize; i++) {
            this.keymap.put(in.readInt(), new Pair<Byte, Integer>(in.readByte(), in.readInt()));
        }

        final int fsize = in.readShort();
        for (int i = 0; i < fsize; i++) {
            this.familiars.put(in.readInt(), new MonsterFamiliar(this.characterid, in.readInt(), in.readInt(), in.readLong(), in.readUTF(), in.readInt(), in.readByte()));
        }

        this.petStore = new byte[in.readByte()];
        for (int i = 0; i < this.petStore.length; i++) {
            this.petStore[i] = in.readByte();
        }

        final int rebsize = in.readShort();
        for (int i = 0; i < rebsize; i++) {
            this.rebuy.add((Item) in.readObject());
        }

        this.imps = new MapleImp[in.readByte()];
        for (int x = 0; x < this.imps.length; x++) {
            if (in.readByte() > 0) {
                MapleImp i = new MapleImp(in.readInt());
                i.setFullness(in.readShort());
                i.setCloseness(in.readShort());
                i.setState(in.readByte());
                i.setLevel(in.readByte());

                this.imps[x] = i;
            }
        }

        for (int i = 0; i < MapleTraitType.values().length; i++) {
            this.traits.put(MapleTraitType.values()[in.readByte()], in.readInt());
        }
        TranferTime = System.currentTimeMillis();
    }

    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeInt(this.characterid);
        out.writeInt(this.accountid);
        out.writeUTF(this.accountname);
        out.writeByte(this.channel);
        out.writeInt(this.nxcredit);
        out.writeInt(this.MaplePoints);
        out.writeInt(this.nxprepaid);
        out.writeInt(this.redeemhn);
        out.writeUTF(this.name);
        out.writeInt(this.fame);
        out.writeByte(this.gender);
        out.writeShort(this.level);
        out.writeShort(this.str);
        out.writeShort(this.dex);
        out.writeShort(this.int_);
        out.writeShort(this.luk);
        out.writeInt(this.hp);
        out.writeInt(this.mp);
        out.writeInt(this.maxhp);
        out.writeInt(this.maxmp);
        out.writeInt(this.exp);
        out.writeShort(this.hpApUsed);
        out.writeInt(this.remainingAp);
        out.writeByte(this.remainingSp.length);
        for (int i = 0; i < this.remainingSp.length; i++) {
            out.writeInt(this.remainingSp[i]);
        }
        out.writeInt(this.meso);
        out.writeByte(this.skinColor);
        out.writeShort(this.job);
        out.writeInt(this.hair);
        out.writeInt(this.face);
        out.writeInt(this.demonMarking);
        out.writeInt(this.mapid);
        out.writeByte(this.initialSpawnPoint);
        out.writeByte(this.world);
        out.writeInt(this.guildid);
        out.writeByte(this.guildrank);
        out.writeInt(this.guildContribution);
        out.writeByte(this.alliancerank);
        out.writeByte(this.gmLevel);
        out.writeInt(this.points);
        out.writeInt(this.vpoints);
        out.writeByte(this.BlessOfFairy == null ? 0 : 1);
        if (this.BlessOfFairy != null) {
            out.writeUTF(this.BlessOfFairy);
        }
        out.writeByte(this.BlessOfEmpress == null ? 0 : 1);
        if (this.BlessOfEmpress != null) {
            out.writeUTF(this.BlessOfEmpress);
        }
        out.writeByte(this.chalkboard == null ? 0 : 1);
        if (this.chalkboard != null) {
            out.writeUTF(this.chalkboard);
        }

        out.writeObject(this.skillmacro);
        out.writeLong(this.lastfametime);
        out.writeObject(this.storage);
        out.writeObject(this.cs);
        out.writeInt(this.mount_itemid);
        out.writeByte(this.mount_Fatigue);
        out.writeByte(this.mount_level);
        out.writeInt(this.mount_exp);
        out.writeInt(this.partyid);
        out.writeInt(this.messengerid);
        out.writeObject(this.inventorys);
        out.writeByte(this.fairyExp);
        out.writeByte(this.subcategory);
        out.writeShort(this.fatigue);
        out.writeInt(this.marriageId);
        out.writeInt(this.familyid);
        out.writeInt(this.seniorid);
        out.writeInt(this.junior1);
        out.writeInt(this.junior2);
        out.writeInt(this.currentrep);
        out.writeInt(this.totalrep);
        out.writeInt(this.battleshipHP);
        out.writeInt(this.gachexp);
        out.writeInt(this.totalWins);
        out.writeInt(this.totalLosses);
        out.writeObject(this.anticheat);
        out.writeUTF(this.tempIP);
        out.writeInt(this.pvpExp);
        out.writeInt(this.pvpPoints);
        out.writeInt(this.pvpKills);
        out.writeInt(this.pvpDeaths);
        /*Start of Custom Feature*/
        out.writeInt(this.reborns);
        out.writeInt(this.apstorage);
        out.writeInt(this.MSIPoints);
        out.writeInt(this.noacc);
        out.writeBoolean(this.muted);
        out.writeObject(this.unmuteTime);
        out.writeInt(this.dgm);
        out.writeInt(this.cardStack);
        out.writeInt(this.occupationId);
        out.writeInt(this.occupationExp);
        out.writeInt(this.occupationLevel);
        out.writeInt(this.JQLevel);
        out.writeInt(this.JQExp);
        out.writeInt(this.wantFame);
        out.writeInt(this.gmtext);
        out.writeInt(this.charToggle);
        out.writeLong(this.dps);
        out.writeInt(this.autoAP);
        out.writeBoolean(this.autoToken);
        out.writeBoolean(this.elf);
        out.writeInt(this.clanId);
        out.writeInt(this.honourexp);
        out.writeInt(this.honourlevel);
        out.writeObject(this.innerSkills);
        out.writeInt(this.birthday);
        out.writeInt(this.found);
        out.writeInt(this.location);
        out.writeInt(this.todo);
        /*End of Custom Feature*/

        out.writeShort(this.mbook.size());
        for (Map.Entry<Integer, Integer> ms : this.mbook.entrySet()) {
            out.writeInt(ms.getKey());
            out.writeInt(ms.getValue());
        }

        out.writeShort(this.Skills.size());
        for (final Map.Entry<Integer, SkillEntry> qs : this.Skills.entrySet()) {
            out.writeInt(qs.getKey());
            out.writeInt(qs.getValue().skillLevel);
            out.writeByte(qs.getValue().masterlevel);
            out.writeLong(qs.getValue().expiration);
            out.writeInt(qs.getValue().teachId);
            out.writeByte(qs.getValue().position);
        }
        
        out.writeByte(this.cardsInfo.size());
        for (Map.Entry qs : this.cardsInfo.entrySet()) {
            out.writeInt((Integer) qs.getKey());
            out.writeInt(((CardData) qs.getValue()).cid);
            out.writeShort(((CardData) qs.getValue()).level);
            out.writeShort(((CardData) qs.getValue()).job);
        }

        out.writeByte(this.buddysize);
        out.writeShort(this.buddies.size());
        for (final Map.Entry<CharacterNameAndId, Boolean> qs : this.buddies.entrySet()) {
            out.writeInt(qs.getKey().getId());
            out.writeUTF(qs.getKey().getName());
            out.writeUTF(qs.getKey().getGroup());
            out.writeBoolean(qs.getValue());
        }

        out.writeShort(this.Quest.size());
        for (final Map.Entry<Integer, Object> qs : this.Quest.entrySet()) {
            out.writeInt(qs.getKey()); // Questid instead of MapleQuest, as it's huge :(
            out.writeObject(qs.getValue());
        }

        out.writeByte(this.reports.size());
        for (Entry<Byte, Integer> ss : reports.entrySet()) {
            out.writeByte(ss.getKey());
            out.writeInt(ss.getValue());
        }

        out.writeByte(this.famedcharacters.size());
        for (final Integer zz : famedcharacters) {
            out.writeInt(zz.intValue());
        }

        out.writeByte(this.extendedSlots.size());
        for (final Integer zz : extendedSlots) {
            out.writeInt(zz.intValue());
        }

        out.writeByte(this.savedlocation.length);
        for (int zz : savedlocation) {
            out.writeInt(zz);
        }

        out.writeByte(this.wishlist.length);
        for (int zz : wishlist) {
            out.writeInt(zz);
        }

        out.writeByte(this.rocks.length);
        for (int zz : rocks) {
            out.writeInt(zz);
        }

        out.writeByte(this.regrocks.length);
        for (int zz : regrocks) {
            out.writeInt(zz);
        }

        out.writeByte(this.hyperrocks.length);
        for (int zz : hyperrocks) {
            out.writeInt(zz);
        }

        out.writeShort(this.InfoQuest.size());
        for (final Map.Entry<Integer, String> qs : this.InfoQuest.entrySet()) {
            out.writeInt(qs.getKey());
            out.writeUTF(qs.getValue());
        }

        out.writeInt(this.keymap.size());
        for (final Map.Entry<Integer, Pair<Byte, Integer>> qs : this.keymap.entrySet()) {
            out.writeInt(qs.getKey());
            out.writeByte(qs.getValue().left);
            out.writeInt(qs.getValue().right);
        }

        out.writeShort(this.familiars.size());
        for (final Map.Entry<Integer, MonsterFamiliar> qs : this.familiars.entrySet()) {
            out.writeInt(qs.getKey());
            final MonsterFamiliar f = qs.getValue();
            out.writeInt(f.getId());
            out.writeInt(f.getFamiliar());
            out.writeLong(f.getExpiry());
            out.writeUTF(f.getName());
            out.writeInt(f.getFatigue());
            out.writeByte(f.getVitality());
        }

        out.writeByte(petStore.length);
        for (int i = 0; i < petStore.length; i++) {
            out.writeByte(petStore[i]);
        }


        out.writeShort(rebuy.size());
        for (int i = 0; i < rebuy.size(); i++) {
            out.writeObject(rebuy.get(i));
        }

        out.writeByte(this.imps.length);
        for (int i = 0; i < this.imps.length; i++) {
            if (this.imps[i] != null) {
                out.writeByte(1);
                out.writeInt(this.imps[i].getItemId());
                out.writeShort(this.imps[i].getFullness());
                out.writeShort(this.imps[i].getCloseness());
                out.writeByte(this.imps[i].getState());
                out.writeByte(this.imps[i].getLevel());
            } else {
                out.writeByte(0);
            }
        }

        for (Entry<MapleTraitType, Integer> ts : this.traits.entrySet()) {
            out.writeByte(ts.getKey().ordinal());
            out.writeInt(ts.getValue());
        }
    }
}
