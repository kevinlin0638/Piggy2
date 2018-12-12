/*
 * This file is part of the OdinMS MapleStory Private Server
 * Copyright (C) 2011 Patrick Huy and Matthias Butz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package server;

import client.CardData;
import constants.GameConstants;
import database.DatabaseConnection;
import provider.MapleData;
import provider.MapleDataProvider;
import provider.MapleDataProviderFactory;
import provider.MapleDataTool;
import tools.types.Pair;
import tools.types.Triple;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * @author AlphaEta
 */
public class CharacterCardFactory {

    private final static CharacterCardFactory instance = new CharacterCardFactory();
    private final MapleDataProvider data = MapleDataProviderFactory.getDataProvider("Etc.wz");
    private final Map<Integer, Integer> cardEffects = new HashMap<>(); // cardid, skillid
    private final Map<Integer, List<Integer>> uniqueEffects = new HashMap<>(); // skillid, cardids

    public static CharacterCardFactory getInstance() {
        return instance;
    }

    public void initialize() {
        final MapleData b = data.getData("CharacterCard.img");
        for (MapleData c : b.getChildByPath("Card")) {
            int skillId = MapleDataTool.getIntConvert("skillID", c, 0);
            if (skillId > 0) {
                cardEffects.put(Integer.parseInt(c.getName()), skillId);
            }
        }
        for (MapleData c : b.getChildByPath("Deck")) {
            boolean uniqueEffect = MapleDataTool.getIntConvert("uniqueEffect", c, 0) > 0;
            int skillId = MapleDataTool.getIntConvert("skillID", c, 0);
            if (uniqueEffect) {
                final List<Integer> ids = new LinkedList<>();
                for (MapleData z : c.getChildByPath("reqCardID")) {
                    ids.add(MapleDataTool.getIntConvert(z));
                }
                if (skillId > 0 && !ids.isEmpty()) {
                    uniqueEffects.put(skillId, ids);
                }
            }
        }
    }

    public final Triple<Integer, Integer, Integer> getCardSkill(final int job, final int level) { // cardid, skillid, skilllevel
        final int skillid = cardEffects.get(job / 10);
        if (skillid <= 0) {
            return null;
        }
        return new Triple<>((skillid - 71000000), skillid, GameConstants.getSkillLevel(level));
    }

    public final List<Integer> getUniqueSkills(final List<Integer> special) {
        final List<Integer> uis = uniqueEffects.entrySet().stream().filter(m -> m.getValue().contains(special.get(0)) && m.getValue().contains(special.get(1)) && m.getValue().contains(special.get(2))).map(Entry::getKey).collect(Collectors.toCollection(LinkedList::new));
        return uis;
    }

    public final int getRankSkill(final int level) {
        return (GameConstants.getSkillLevel(level) + 71001099);
    }

    public final boolean canHaveCard(final int level, final int job) {
        if (level < 30) {
            return false;
        }
        if (cardEffects.get(job / 10) == null) {
            return false;
        }
        return true;
    }

    public final Map<Integer, CardData> loadCharacterCards(final Connection con, final int accId, final int serverId) {
        Map<Integer, CardData> cards = new LinkedHashMap<>(); // order
        Map<Integer, Pair<Short, Short>> inf = loadCharactersInfo(con, accId, serverId);
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM `character_cards` WHERE `accountid` = ?");
            ps.setInt(1, accId);
            ResultSet rs = ps.executeQuery();
            int deck1 = 0, deck2 = 3;
            while (rs.next()) {
                final int cid = rs.getInt("characterid");
                final Pair<Short, Short> x = inf.get(cid);
                if (x == null || !canHaveCard(x.getLeft(), x.getRight())) { // we don't need to delete them as it'll be there till the user reupdate the char cards
                    continue;
                }
                final int position = rs.getInt("position");
                if (position < 4) {
                    deck1++;
                    cards.put(deck1, new CardData(cid, x.getLeft(), x.getRight()));
                } else {
                    deck2++;
                    cards.put(deck2, new CardData(cid, x.getLeft(), x.getRight()));
                }
            }
            rs.close();
            ps.close();

        } catch (SQLException sqlE) {
            System.out.println("Failed to Load character cards. Reason: " + sqlE.toString());
        }
        for (int i = 1; i <= 6; i++) {
            cards.putIfAbsent(i, new CardData(0, (short) 0, (short) 0)); // fill it in
        }
        return cards;
    }

    public Map<Integer, Pair<Short, Short>> loadCharactersInfo(Connection con, int accId, int world) {
        Map<Integer, Pair<Short, Short>> chars = new HashMap<>();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT id, level, job FROM characters WHERE accountid = ? AND world = ?");
            ps.setInt(1, accId);
            ps.setInt(2, world);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                chars.put(rs.getInt("id"), new Pair<>(rs.getShort("level"), rs.getShort("job")));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("error loading characters info. reason: " + e.toString());
        }
        return chars;
    }
}
