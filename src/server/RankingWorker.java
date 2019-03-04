/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2005 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
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
package server;

import database.DatabaseConnection;
import tools.FileoutputUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class RankingWorker {

    private final static Map<Integer, List<RankingInformation>> rankings = new HashMap<>();
    private final static Map<String, Integer> jobCommands = new HashMap<>();

    public static Integer getJobCommand(final String job) {
        return jobCommands.get(job);
    }

    public static Map<String, Integer> getJobCommands() {
        return jobCommands;
    }

    public static List<RankingInformation> getRankingInfo(final int job) {
        return rankings.get(job);
    }


    public static void run() {
        // System.out.println("Loading Rankings::");
        long startTime = System.currentTimeMillis();
        loadJobCommands();
        try {
            Connection con = DatabaseConnection.getConnection();
            updateRanking(con);
        } catch (Exception ex) {
            FileoutputUtil.outputFileError(FileoutputUtil.ScriptEx_Log, ex);
            System.err.println("Could not update rankings");
        }
        //  System.out.println("Done loading Rankings in " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds :::"); //keep
    }

    private static void updateRanking(Connection con) throws Exception {
        StringBuilder sb = new StringBuilder("SELECT c.id, c.job, c.name, c.jobRank, c.rank, c.level");
        sb.append(" FROM characters AS c LEFT JOIN accounts AS a ON c.accountid = a.id WHERE c.gm <= 3 AND a.banned = 0");
        sb.append(" ORDER BY c.level DESC, c.rank ASC");
        PreparedStatement ps;
        try (PreparedStatement charSelect = con.prepareStatement(sb.toString());
             ResultSet rs = charSelect.executeQuery()) {
            ps = con.prepareStatement("UPDATE characters SET jobRank = ?, jobRankMove = ?, rank = ?, rankMove = ? WHERE id = ?");
            int rank = 0;
            final Map<Integer, Integer> rankMap = new LinkedHashMap<>();
            for (int i : jobCommands.values()) {
                rankMap.put(i, 0); //job to rank
                rankings.put(i, new ArrayList<RankingInformation>());
            }
            while (rs.next()) {
                int job = rs.getInt("job");
                if (!rankMap.containsKey(job / 100)) { //not supported.
                    continue;
                }
                int jobRank = rankMap.get(job / 100) + 1;
                rankMap.put(job / 100, jobRank);
                rank++;
                rankings.get(-1).add(new RankingInformation(rs.getString("name"), job, rs.getInt("level")));
                rankings.get(job / 100).add(new RankingInformation(rs.getString("name"), job, rs.getInt("level")));
                ps.setInt(1, jobRank);
                ps.setInt(2, rs.getInt("jobRank") - jobRank);
                ps.setInt(3, rank);
                ps.setInt(4, rs.getInt("rank") - rank);
                ps.setInt(5, rs.getInt("id"));
                ps.addBatch();
            }
            ps.executeBatch();
        }
        ps.close();
    }


    public static void loadJobCommands() {
        //messy, cleanup
        jobCommands.put("all", -1);
        jobCommands.put("beginner", 0);
        jobCommands.put("warrior", 1);
        jobCommands.put("magician", 2);
        jobCommands.put("bowman", 3);
        jobCommands.put("thief", 4);
        jobCommands.put("pirate", 5);
        jobCommands.put("nobless", 10);
        jobCommands.put("soulmaster", 11);
        jobCommands.put("flamewizard", 12);
        jobCommands.put("windbreaker", 13);
        jobCommands.put("nightwalker", 14);
        jobCommands.put("striker", 15);
        jobCommands.put("legend", 20);
        jobCommands.put("aran", 21);
        jobCommands.put("evan", 22);
        jobCommands.put("mercedes", 23);
        jobCommands.put("phantom", 24);
        jobCommands.put("citizen", 30);
        jobCommands.put("demonslayer", 31);
        jobCommands.put("battlemage", 32);
        jobCommands.put("wildhunter", 33);
        jobCommands.put("mechanic", 35);
    }

    public static class RankingInformation {

        public String toString;
        public int rank;

        public RankingInformation(String name, int job, int reborns) {
            this.rank = rank;
            final StringBuilder builder = new StringBuilder("Rank ");
            builder.append(rank);
            builder.append(" : ");
            builder.append(name);
            builder.append(" - level ");
            builder.append(reborns);
            builder.append(" ");
            builder.append(MapleCarnivalChallenge.getJobNameById(job));
            this.toString = builder.toString(); //Rank 1 : KiDALex - Level 200 Blade Master | 0 EXP, 30000 Fame
        }

        @Override
        public String toString() {
            return toString;
        }
    }


}
