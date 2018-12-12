package handling.channel.handler;

import server.status.MapleBuffStatus;
import client.MapleCharacter;
import client.MapleClient;
import client.skill.SkillFactory;
import handling.world.MaplePartyCharacter;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.life.MobSkillFactory;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext;
import tools.packet.MonsterCarnivalPacket;
import tools.types.Pair;

import java.util.List;

/**
 * @author: Eric
 * @rev: 3.7
 * @function: Up-to-date Monster Carnival Handler
 * @update: This will support the 4 buttons in big-bang now that it has been packet updated.
 */

public class MonsterCarnivalHandler {

    public static final void MonsterCarnival(LittleEndianAccessor slea, MapleClient c) {
        if (c.getPlayer().getCarnivalParty() == null) {
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        int tab = slea.readByte();

        if (tab == 0) { // 100 CP
            List mobs = c.getPlayer().getMap().getMobsToSpawn();
            int num = MapleCharacter.rand(1, 4); // size should be (int)5
            if ((num >= mobs.size()) || (c.getPlayer().getAvailableCP() < 100)) {
                c.getPlayer().dropMessage(5, "You do not have the CP.");
                c.sendPacket(CWvsContext.enableActions());
                return;
            }
            MapleMonster mons = MapleLifeFactory.getMonster(((Integer) ((Pair) mobs.get(num)).left).intValue());
            if ((mons != null) && (c.getPlayer().getMap().makeCarnivalSpawn(c.getPlayer().getCarnivalParty().getTeam(), mons, num))) {
                c.getPlayer().getCarnivalParty().useCP(c.getPlayer(), 100);
                c.getPlayer().CPUpdate(false, c.getPlayer().getAvailableCP(), c.getPlayer().getTotalCP(), 0);
                for (MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
                    chr.CPUpdate(true, c.getPlayer().getCarnivalParty().getAvailableCP(), c.getPlayer().getCarnivalParty().getTotalCP(), c.getPlayer().getCarnivalParty().getTeam());
                }
                c.getPlayer().getMap().broadcastMessage(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, num));
                c.sendPacket(CWvsContext.enableActions());
            } else {
                c.getPlayer().dropMessage(5, "You may no longer summon the monster.");
                c.sendPacket(CWvsContext.enableActions());
            }
        } else if (tab == 1) { // 200 CP
            if (c.getPlayer().getAvailableCP() < 200) {
                c.getPlayer().dropMessage(5, "You do not have the CP.");
                c.sendPacket(CWvsContext.enableActions());
                return;
            }
            int rand = MapleCharacter.rand(1, 20);
            if (rand < 10) {
                SkillFactory.getSkill(80001079).getEffect(SkillFactory.getSkill(80001079).getMaxLevel()).applyTo(c.getPlayer());
                c.sendPacket(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, 0));
                c.getPlayer().getParty().getMembers().stream().filter(mpc -> mpc.getId() != c.getPlayer().getId() && mpc.getChannel() == c.getChannel() && mpc.getMapid() == c.getPlayer().getMapId() && mpc.isOnline()).forEach(mpc -> {
                    MapleCharacter mc = c.getPlayer().getMap().getCharacterById(mpc.getId());
                    if (mc != null) {
                        SkillFactory.getSkill(80001079).getEffect(SkillFactory.getSkill(80001079).getMaxLevel()).applyTo(mc);
                        mc.getClient().sendPacket(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, 0));
                    }
                });
            } else {
                // should check for null partys but whatever
                c.getPlayer().getMap().getCharactersThreadsafe().stream().filter(chr -> chr.getParty() != c.getPlayer().getParty()).forEach(chr -> {
                    chr.getDiseaseBuff(MapleBuffStatus.BLIND, MobSkillFactory.getMobSkill(136, 1));
                    c.getPlayer().getMap().broadcastMessage(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, 1));
                });
            }
            c.getPlayer().getCarnivalParty().useCP(c.getPlayer(), 200);
            c.getPlayer().CPUpdate(false, c.getPlayer().getAvailableCP(), c.getPlayer().getTotalCP(), 0);
            for (MaplePartyCharacter mpc : c.getPlayer().getParty().getMembers()) {
                MapleCharacter mc = c.getPlayer().getMap().getCharacterById(mpc.getId());
                mc.CPUpdate(true, c.getPlayer().getCarnivalParty().getAvailableCP(), c.getPlayer().getCarnivalParty().getTotalCP(), c.getPlayer().getCarnivalParty().getTeam());
            }
        } else if (tab == 2) { // 300 CP
            if (c.getPlayer().getAvailableCP() < 300) {
                c.getPlayer().dropMessage(5, "You do not have the CP.");
                c.sendPacket(CWvsContext.enableActions());
                return;
            }
            int rand = MapleCharacter.rand(1, 20);
            if (rand < 10) {
                SkillFactory.getSkill(80001080).getEffect(SkillFactory.getSkill(80001080).getMaxLevel()).applyTo(c.getPlayer());
                c.sendPacket(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, 2));
                for (MaplePartyCharacter mpc : c.getPlayer().getParty().getMembers()) {
                    if (mpc.getId() != c.getPlayer().getId() && mpc.getChannel() == c.getChannel() && mpc.getMapid() == c.getPlayer().getMapId() && mpc.isOnline()) {
                        MapleCharacter mc = c.getPlayer().getMap().getCharacterById(mpc.getId());
                        if (mc != null) {
                            SkillFactory.getSkill(80001080).getEffect(SkillFactory.getSkill(80001080).getMaxLevel()).applyTo(mc);
                            mc.getClient().sendPacket(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, 2));
                        }
                    }
                }
            } else {
                c.getPlayer().getMap().getCharactersThreadsafe().stream().filter(chr -> chr.getParty() != c.getPlayer().getParty()).forEach(chr -> {
                    chr.getDiseaseBuff(MapleBuffStatus.SLOW, MobSkillFactory.getMobSkill(126, 10));
                    c.getPlayer().getMap().broadcastMessage(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, 3));
                });
            }
            c.getPlayer().getCarnivalParty().useCP(c.getPlayer(), 300);
            c.getPlayer().CPUpdate(false, c.getPlayer().getAvailableCP(), c.getPlayer().getTotalCP(), 0);
            for (MaplePartyCharacter mpc : c.getPlayer().getParty().getMembers()) {
                MapleCharacter mc = c.getPlayer().getMap().getCharacterById(mpc.getId());
                mc.CPUpdate(true, c.getPlayer().getCarnivalParty().getAvailableCP(), c.getPlayer().getCarnivalParty().getTotalCP(), c.getPlayer().getCarnivalParty().getTeam());
            }
        } else if (tab == 3) { // 400 CP
            if (c.getPlayer().getAvailableCP() < 400) {
                c.getPlayer().dropMessage(5, "You do not have the CP.");
                c.sendPacket(CWvsContext.enableActions());
                return;
            }
            int rand = MapleCharacter.rand(1, 20);
            if (rand < 10) {
                SkillFactory.getSkill(80001081).getEffect(SkillFactory.getSkill(80001081).getMaxLevel()).applyTo(c.getPlayer());
                c.sendPacket(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, 4));
                for (MaplePartyCharacter mpc : c.getPlayer().getParty().getMembers()) {
                    if (mpc.getId() != c.getPlayer().getId() && mpc.getChannel() == c.getChannel() && mpc.getMapid() == c.getPlayer().getMapId() && mpc.isOnline()) {
                        MapleCharacter mc = c.getPlayer().getMap().getCharacterById(mpc.getId());
                        if (mc != null) {
                            SkillFactory.getSkill(80001081).getEffect(SkillFactory.getSkill(80001081).getMaxLevel()).applyTo(mc);
                            mc.getClient().sendPacket(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, 4));
                        }
                    }
                }
            } else {
                c.getPlayer().getMap().getCharactersThreadsafe().stream().filter(chr -> chr.getParty() != c.getPlayer().getParty()).forEach(chr -> {
                    chr.getDiseaseBuff(MapleBuffStatus.SEAL, MobSkillFactory.getMobSkill(120, 10));
                    c.getPlayer().getMap().broadcastMessage(MonsterCarnivalPacket.playerSummoned(c.getPlayer().getName(), tab, 5));
                });
            }
            c.getPlayer().getCarnivalParty().useCP(c.getPlayer(), 400);
            c.getPlayer().CPUpdate(false, c.getPlayer().getAvailableCP(), c.getPlayer().getTotalCP(), 0);
            for (MaplePartyCharacter mpc : c.getPlayer().getParty().getMembers()) {
                MapleCharacter mc = c.getPlayer().getMap().getCharacterById(mpc.getId());
                mc.CPUpdate(true, c.getPlayer().getCarnivalParty().getAvailableCP(), c.getPlayer().getCarnivalParty().getTotalCP(), c.getPlayer().getCarnivalParty().getTeam());
            }
        }
        c.sendPacket(CWvsContext.enableActions());
    }
}