package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.MonsterFamiliar;
import client.skill.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import server.status.MonsterStatus;
import server.status.MonsterStatusEffect;
import constants.GameConstants;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.StructFamiliar;
import server.Timer.MapTimer;
import server.life.*;
import server.maps.MapleMap;
import server.maps.MapleNodes.MapleNodeInfo;
import server.movement.ILifeMovementFragment;
import server.movement.MovementKind;
import tools.FileoutputUtil;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.MobPacket;
import tools.types.Pair;
import tools.types.Triple;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MobHandler {

    public static final void MoveMonster(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (chr.getMap() == null)) {
            return;
        }
        int objectId = slea.readInt();
        MapleMonster monster = chr.getMap().getMonsterByOid(objectId);
        if (monster == null) {
            return;
        }
        if (monster.getLinkCID() > 0) {
            return;
        }
        short moveId = slea.readShort();
        boolean useSkill = slea.readByte() > 0;
        byte skill = slea.readByte();
        int unk = slea.readInt();

        int realSkill = 0;
        int level = 0;

        if (useSkill) {
            byte size = monster.getNoSkills();
            boolean used = false;
            if (size > 0) {
                final Pair<Integer, Integer> skillToUse = monster.getSkills().get((byte) Randomizer.nextInt(size));
                realSkill = skillToUse.getLeft();
                level = skillToUse.getRight();
                MobSkill mobSkill = MobSkillFactory.getMobSkill(realSkill, level);
                if ((mobSkill != null) && (!mobSkill.checkCurrentBuff(chr, monster))) {
                    long now = System.currentTimeMillis();
                    long ls = monster.getLastSkillUsed(realSkill);
                    if ((ls == 0L) || ((now - ls > mobSkill.getCoolTime()) && (!mobSkill.onlyOnce()))) {
                        monster.setLastSkillUsed(realSkill, now, mobSkill.getCoolTime());
                        int reqHp = (int) ((float) monster.getHp() / (float) monster.getMobMaxHp() * 100.0F);
                        if (reqHp <= mobSkill.getHP()) {
                            used = true;
                            mobSkill.applyEffect(chr, monster, true);
                        }
                    }
                }
            }
            if (!used) {
                realSkill = 0;
                level = 0;
            }
        }
        final List<Pair<Integer, Integer>> unk3 = new ArrayList<>();
        byte size1 = slea.readByte();
        for (int i = 0; i < size1; i++) {
            unk3.add(new Pair<>((int) slea.readShort(), (int) slea.readShort()));
        }
        final List<Integer> unk2 = new ArrayList<>();
        byte size = slea.readByte();
        for (int i = 0; i < size; i++) {
            unk2.add((int) slea.readShort());
        }
        slea.read(1);
        int unk4 = slea.readInt();
        slea.read(4);
        slea.read(4);
        slea.read(4);
        if (unk4 == 0x12) {
            slea.readMapleAsciiString();
        }
        slea.read(1);
        final Point startPos = slea.readPos();
        final List<ILifeMovementFragment> res;
        slea.skip(4);
        res = MovementParse.parseMovement(slea, startPos, MovementKind.MOB_MOVEMENT);
        int unk5 = slea.readByte();

        for (int i = 0; ; i += 2) {
            if (i >= unk5) {
                break;
            }
            slea.readByte();
        }

        slea.readShort();
        slea.readShort();
        slea.readShort();
        slea.readShort();

        slea.readByte();
        slea.readInt();
        slea.readInt();
        slea.readInt();
        slea.readInt();
        slea.readInt();

        if (res != null && res.size() > 0) {
            MapleMap map = chr.getMap();
            if (useSkill) {
                c.sendPacket(MobPacket.moveMonsterResponse(monster.getObjectId(), moveId, monster.getMp(), monster.isControllerHasAggro(), realSkill, level));
            } else {
                c.sendPacket(MobPacket.moveMonsterResponse(monster.getObjectId(), moveId, monster.getMp(), monster.isControllerHasAggro()));
            }
            if (slea.available() != 0) {
                FileoutputUtil.log("Log_Packet_Except.rtf", "slea.available != 35 (movement parsing error)\n" + slea.toString(true));
                return;
            }
            MovementParse.updatePosition(res, monster, -1);
            Point endPos = monster.getTruePosition();
            map.moveMonster(monster, endPos);
            map.broadcastMessage(chr, MobPacket.moveMonster(useSkill, skill, unk, monster.getObjectId(), startPos, res, unk2, unk3), endPos);
        }
    }

    public static void FriendlyDamage(final LittleEndianAccessor slea, final MapleCharacter chr) {
        int from = slea.readInt();
        slea.skip(4);
        int to = slea.readInt();
        final MapleMap map = chr.getMap();
        final MapleMonster mobto = map.getMonsterByOid(to);
        if (map.getMonsterByOid(from) != null && map.getMonsterByOid(to) != null) {
            final int damage = (mobto.getStats().getLevel() * Randomizer.nextInt(mobto.getStats().getLevel())) / 2; // Temp for now until I figure out something more effective
            mobto.damage(chr, damage, true);
            chr.getClient().sendPacket(MobPacket.damageFriendlyMob(mobto, damage, true));
            checkShammos(chr, mobto, map);
        }
    }

    public static void MobBomb(final LittleEndianAccessor slea, final MapleCharacter chr) {
        final MapleMap map = chr.getMap();
        if (map == null) {
            return;
        }
        final MapleMonster mobfrom = map.getMonsterByOid(slea.readInt());
        slea.skip(4); // something, 9E 07
        slea.readInt(); //-204?

        if (mobfrom != null && mobfrom.getBuff(MonsterStatus.MONSTER_BOMB) != null) {
            /* not sure
            12D -    0B 3D 42 00 EC 05 00 00 32 FF FF FF 00 00 00 00 00 00 00 00
            <monsterstatus done>
            108 - 07 0B 3D 42 00 EC 05 00 00 32 FF FF FF 01 00 00 00 7B 00 00 00
             */
        }
    }

    public static void checkShammos(final MapleCharacter chr, final MapleMonster mobto, final MapleMap map) {
        if (!mobto.isAlive() && mobto.getStats().isEscort()) { //shammos
            for (MapleCharacter chrz : map.getCharactersThreadsafe()) { //check for 2022698
                if (chrz.getParty() != null && chrz.getParty().getLeader().getId() == chrz.getId()) {
                    //leader
                    if (chrz.haveItem(2022698)) {
                        MapleInventoryManipulator.removeById(chrz.getClient(), MapleInventoryType.USE, 2022698, 1, false, true);
                        mobto.heal((int) mobto.getMobMaxHp(), mobto.getMobMaxMp(), true);
                        return;
                    }
                    break;
                }
            }
            map.broadcastMessage(CWvsContext.broadcastMsg(6, "Your party has failed to protect the monster."));
            final MapleMap mapp = chr.getMap().getForcedReturnMap();
            for (MapleCharacter chrz : map.getCharactersThreadsafe()) {
                chrz.changeMap(mapp, mapp.getPortal(0));
            }
        } else if (mobto.getStats().isEscort() && mobto.getEventInstance() != null) {
            mobto.getEventInstance().setProperty("HP", String.valueOf(mobto.getHp()));
        }
    }

    public static void MonsterBomb(final int oid, final MapleCharacter chr) {
        final MapleMonster monster = chr.getMap().getMonsterByOid(oid);

        if (monster == null || !chr.isAlive() || chr.isHidden() || monster.getLinkCID() > 0) {
            return;
        }
        final byte selfd = monster.getStats().getSelfD();
        if (selfd != -1) {
            chr.getMap().killMonster(monster, chr, false, false, selfd);
        }
    }

    public static void AutoAggro(final int monsteroid, final MapleCharacter chr) {
        if (chr == null || chr.getMap() == null || chr.isHidden()) { //no evidence :)
            return;
        }
        final MapleMonster monster = chr.getMap().getMonsterByOid(monsteroid);

        if (monster != null && chr.getTruePosition().distanceSq(monster.getTruePosition()) < 200000 && monster.getLinkCID() <= 0) {
            if (monster.getController() != null) {
                if (chr.getMap().getCharacterById(monster.getController().getId()) == null) {
                    monster.switchController(chr, true);
                } else {
                    monster.switchController(monster.getController(), true);
                }
            } else {
                monster.switchController(chr, true);
            }
        }
    }

    public static void HypnotizeDmg(final LittleEndianAccessor slea, final MapleCharacter chr) {
        final MapleMonster mob_from = chr.getMap().getMonsterByOid(slea.readInt()); // 39 A1 07 00
        final MapleClient client = chr.getClient().getChannelServer().getPlayerStorage().getCharacterById(slea.readInt()).getClient(); // 1F 00 00 00
        final MapleMonster mob_to = chr.getMap().getMonsterByOid(slea.readInt()); // 54 A1 07 00
        slea.skip(1); // 00
        int damage = slea.readInt(); // FF 56 03 00
        slea.skip(1); // 00
        slea.readInt(); // 00 0C 02 79
        if (mob_from != null && mob_to != null && mob_to.getStats().isFriendly()) {
            if (damage > 30000) {
                double damageRound = damage * 0.001;
                damage = (int) Math.round(damageRound);
                System.out.println("Damage: " + damageRound + " Damage (Rounded): " + damage);
            }
            final int rDamage = (int) Math.max(0, Math.min(damage, mob_to.getHp()));
            System.out.println("Damage To Monster: " + rDamage + " Damage Given: " + (mob_to.getHp() - rDamage));
            mob_to.setHp(mob_to.getHp() - rDamage);
            // mob_to.damage(chr, damage, true);
            client.sendPacket(MobPacket.damageFriendlyMob(mob_to, damage, false));
            checkShammos(chr, mob_to, chr.getMap());
        }
    }

    public static void DisplayNode(final LittleEndianAccessor slea, final MapleCharacter chr) {
        final MapleMonster mob_from = chr.getMap().getMonsterByOid(slea.readInt());
        if (mob_from != null) {
            chr.getClient().sendPacket(MobPacket.getNodeProperties(mob_from, chr.getMap()));
        }
    }

    public static void MobNode(final LittleEndianAccessor slea, final MapleCharacter chr) {
        final MapleMonster mob_from = chr.getMap().getMonsterByOid(slea.readInt()); //eric: it is int(oid), int(node)
        final int newNode = slea.readInt();
        final int nodeSize = chr.getMap().getNodes().size();
        if (mob_from != null && nodeSize > 0) {
            final MapleNodeInfo mni = chr.getMap().getNode(newNode);
            if (mni == null) {
                return;
            }
            if (mni.attr == 2) { // OnMobStopSay
                switch (chr.getMapId() / 100) {
                    case 9211201: // Hoblin King
                        if (mni.key == 16) {
                            chr.getMap().talkMonster("Escort me. If you don't come to my side within 30 seconds of me calling you, you will fail.", 5120035, mob_from.getObjectId(), 5);
                        }
                        break;
                    case 9211202:
                        if (mni.key == 0) {
                            chr.getMap().talkMonster("There is the entrance to the cave where Lex was sealed. Hang in there, you are almost there.", 5120035, mob_from.getObjectId(), 5);
                        } else if (mni.key == 26) {
                            chr.getMap().talkMonster("Don't worry about that poster. Let's go!", 2, mob_from.getObjectId(), 5);
                        }
                        break;
                    case 9211203:
                        if (mni.key == 1) {
                            chr.getMap().talkMonster("Shall we go see if the seal is intact?", 2, mob_from.getObjectId(), 4);
                        } else if (mni.key == 4) {
                            chr.getMap().talkMonster("This lock won't stop me. Hahaha!", 2, mob_from.getObjectId(), 4);
                            MapTimer.getInstance().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    if (chr.getParty() == null || chr.getParty().getLeader().getName().equals(chr.getName())) {
                                        chr.getMap().startMapEffect("You fools. I am glad you have come. The Hoblin King will now destroy all of you.", 5120035);
                                        chr.getMap().setReactorState((byte) 1);
                                        MapleMonster rex = MapleLifeFactory.getMonster(9300281);
                                        OverrideMonsterStats s = new OverrideMonsterStats();
                                        s.setOHp(chr.getLevel() > 80 ? (rex.getMobMaxHp() * chr.getLevel()) : rex.getMobMaxHp());
                                        s.setOMp(chr.getLevel() > 80 ? (rex.getMobMaxMp() * chr.getLevel()) : rex.getMobMaxMp());
                                        rex.changeLevel(chr.getLevel() > 80 ? (chr.getLevel()) : rex.getStats().getLevel());
                                        rex.setOverrideStats(s);
                                        chr.getMap().spawnMonsterOnGroundBelow(rex, new Point(332, 174));
                                    }
                                }
                            }, 1500);
                        } else if (mni.key == 8) {
                            int rand = MapleCharacter.rand(0, 6);
                            String text;
                            switch (rand) {
                                case 1:
                                    text = "Rex! Defeat your enemies!"; // <string name="say" value="Rex! Defeat your enemies!"/>
                                    break;
                                case 2:
                                    text = "Rex! Show them your power!"; // <string name="say" value="Rex! Show them your power!"/>
                                    break;
                                case 3:
                                    text = "Haha! Defeat these fools!"; // <string name="say" value="Haha! Defeat these fools!"/>
                                    break;
                                case 4:
                                    // <string name="say" value="Foolish human. Do you understand now? You&apos;ve only helped me release the seal that had trapped Rex!"/>
                                    text = "Foolish human. Do you understand now? You've only helped me release the seal that had trapped Rex!";
                                    break;
                                case 5:
                                    text = "Soon, this world will belong to me!"; // <string name="say" value="Soon, this world will belong to me!"/>
                                    break;
                                case 6:
                                    text = "Rex! Let us go take our revenge on the elders!"; // <string name="say" value="Rex! Let us go take our revenge on the elders!"/>
                                    break;
                                default: // 0
                                    text = "I won't forgive the elders who trapped me here!"; // <string name="say" value="I won&apos;t forgive the elders who trapped me here!"/>
                                    break;
                            }
                            chr.getMap().talkMonster(text, 2, mob_from.getObjectId(), 10); // <int name="sayTic" value="10"/>
                        }
                        break;
                    case 9320001: // Ice Knight.. big todo
                    case 9320002:
                    case 9320003:
                        chr.getMap().talkMonster("Please escort me.", 5120051, mob_from.getObjectId(), 5);
                        break;
                }
            }
            mob_from.setLastNode(newNode);
            if (chr.getMap().isLastNode(newNode)) { //eric: this is actually not necessary to be seperate.
                switch (chr.getMapId() / 100) {
                    case 9211201: // Hoblin King
                    case 9211202:
                        chr.getMap().removeMonster(mob_from);
                        chr.warpParty(chr.getMapId() + 100);
                        break;
                    case 9211203:
                    case 9211204:
                        chr.getMap().removeMonster(mob_from);
                        break;
                    case 9320001: // Ice Knight
                    case 9320002:
                    case 9320003:
                        chr.getMap().removeMonster(mob_from);
                        chr.getMap().broadcastMessage(CWvsContext.broadcastMsg(5, "Proceed to the next stage."));
                        break;

                }
            }
        }
    }

    public static final void RenameFamiliar(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        MonsterFamiliar mf = (MonsterFamiliar) c.getPlayer().getFamiliars().get(Integer.valueOf(slea.readInt()));
        String newName = slea.readMapleAsciiString();
        if ((mf != null) && (mf.getName().equals(mf.getOriginalName()))) {
            mf.setName(newName);
            c.sendPacket(CField.renameFamiliar(mf));
        } else {
            chr.dropMessage(1, "Name was not eligible.");
        }
        c.sendPacket(CWvsContext.enableActions());
    }

    public static final void SpawnFamiliar(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        slea.readInt();
        int mId = slea.readInt();
        c.sendPacket(CWvsContext.enableActions());
        c.getPlayer().removeFamiliar();
        if ((c.getPlayer().getFamiliars().containsKey(Integer.valueOf(mId))) && (slea.readByte() > 0)) {
            MonsterFamiliar mf = (MonsterFamiliar) c.getPlayer().getFamiliars().get(Integer.valueOf(mId));
            if (mf.getFatigue() > 0) {
                c.getPlayer().dropMessage(1, "Please wait " + mf.getFatigue() + " seconds to summon it.");
            } else {
                c.getPlayer().spawnFamiliar(mf);
            }
        }
    }

    public static final void MoveFamiliar(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        slea.skip(13);
        List res = MovementParse.parseMovement(slea, chr.getTruePosition(), MovementKind.FAMILIAR_MOVMENT);
        if ((chr != null) && (chr.getSummonedFamiliar() != null) && (res.size() > 0)) {
            Point pos = chr.getSummonedFamiliar().getPosition();
            MovementParse.updatePosition(res, chr.getSummonedFamiliar(), 0);
            chr.getSummonedFamiliar().updatePosition(res);
            if (!chr.isHidden()) {
                chr.getMap().broadcastMessage(chr, CField.moveFamiliar(chr.getId(), pos, res), chr.getTruePosition());
            }
        }
    }

    public static final void AttackFamiliar(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if (chr.getSummonedFamiliar() == null) {
            return;
        }
        slea.skip(6);
        int skillid = slea.readInt();

        SkillFactory.FamiliarEntry f = SkillFactory.getFamiliar(skillid);
        if (f == null) {
            return;
        }
        byte unk = slea.readByte();
        byte size = slea.readByte();
        final List<Triple<Integer, Integer, List<Integer>>> attackPair = new ArrayList<Triple<Integer, Integer, List<Integer>>>(size);
        for (int i = 0; i < size; i++) {
            int oid = slea.readInt();
            int type = slea.readInt();
            slea.skip(10);
            byte si = slea.readByte();
            List attack = new ArrayList(si);
            for (int x = 0; x < si; x++) {
                attack.add(Integer.valueOf(slea.readInt()));
            }
            attackPair.add(new Triple(Integer.valueOf(oid), Integer.valueOf(type), attack));
        }
        if ((attackPair.isEmpty()) || (attackPair.size() > f.targetCount)) {
            return;
        }
        MapleMonsterStats oStats = chr.getSummonedFamiliar().getOriginalStats();
        chr.getMap().broadcastMessage(chr, CField.familiarAttack(chr.getId(), unk, attackPair), chr.getTruePosition());
        for (Triple attack : attackPair) {
            MapleMonster mons = chr.getMap().getMonsterByOid(((Integer) attack.left).intValue());
            if ((mons == null) || (!mons.isAlive()) || (mons.getStats().isFriendly()) || (mons.getLinkCID() > 0) || (((List) attack.right).size() > f.attackCount)) {
                continue;
            }
            if ((chr.getTruePosition().distanceSq(mons.getTruePosition()) > 640000.0D) || (chr.getSummonedFamiliar().getTruePosition().distanceSq(mons.getTruePosition()) > GameConstants.getAttackRange(f.lt, f.rb))) {
                return;
            }
            for (Iterator i$ = ((List) attack.right).iterator(); i$.hasNext(); ) {
                int damage = ((Integer) i$.next()).intValue();
                if (damage <= oStats.getPhysicalAttack() * 4) {
                    mons.damage(chr, damage, true);
                }
            }
            if ((f.makeChanceResult()) && (mons.isAlive())) {
                for (MonsterStatus s : f.status) {
                    mons.applyStatus(chr, new MonsterStatusEffect(s, Integer.valueOf(f.speed), MonsterStatusEffect.GetGenericSkill(s), null, false), false, f.time * 1000, false, null);
                }
                if (f.knockback) {
                    mons.switchController(chr, true);
                }
            }
        }
        chr.getSummonedFamiliar().addFatigue(chr, attackPair.size());
    }

    public static final void TouchFamiliar(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if (chr.getSummonedFamiliar() == null) {
            return;
        }
        slea.skip(6);
        byte unk = slea.readByte();

        MapleMonster target = chr.getMap().getMonsterByOid(slea.readInt());
        if (target == null) {
            return;
        }
        int type = slea.readInt();
        slea.skip(4);
        int damage = slea.readInt();
        int maxDamage = chr.getSummonedFamiliar().getOriginalStats().getPhysicalAttack() * 5;
        if (damage < maxDamage) {
            damage = maxDamage;
        }
        if ((!target.getStats().isFriendly())) {
            chr.getMap().broadcastMessage(chr, CField.touchFamiliar(chr.getId(), unk, target.getObjectId(), type, 600, damage), chr.getTruePosition());
            target.damage(chr, damage, true);
            chr.getSummonedFamiliar().addFatigue(chr);
        }
    }

    public static final void UseFamiliar(LittleEndianAccessor slea, MapleClient c, MapleCharacter chr) {
        if ((chr == null) || (!chr.isAlive()) || (chr.getMap() == null) || (chr.hasBlockedInventory())) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }      //AD 00 | F3 CB 66 68  | 10 00  | 0B CC 2B 00 
        slea.readInt();
        short slot = slea.readShort();
        int itemId = slea.readInt();
        Item toUse = chr.getInventory(MapleInventoryType.USE).getItem(slot);

        c.sendPacket(CWvsContext.enableActions());
        if ((toUse == null) || (toUse.getQuantity() < 1) || (toUse.getItemId() != itemId) || (itemId / 10000 != 287)) {
            return;
        }
        StructFamiliar f = MapleItemInformationProvider.getInstance().getFamiliarByItem(itemId);
        MonsterFamiliar mf = (MonsterFamiliar) c.getPlayer().getFamiliars().get(Integer.valueOf(f.familiar));
        if (mf != null) {
            if (mf.getVitality() >= 3) {
                mf.setExpiry(Math.min(System.currentTimeMillis() + 7776000000L, mf.getExpiry() + 2592000000L));
            } else {
                mf.setVitality(mf.getVitality() + 1);
                mf.setExpiry(mf.getExpiry() + 2592000000L);
            }
        } else {
            mf = new MonsterFamiliar(c.getPlayer().getId(), f.familiar, System.currentTimeMillis() + 2592000000L);
            c.getPlayer().getFamiliars().put(Integer.valueOf(f.familiar), mf);
        }
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (byte) 1, false, false);
        c.sendPacket(CField.registerFamiliar(mf));
        return;
    }
}
