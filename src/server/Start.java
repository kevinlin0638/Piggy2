package server;

import client.inventory.MapleInventoryIdentifier;
import client.messages.CommandProcessor;
import client.skill.SkillFactory;
import constants.ServerConfig;
import constants.ServerConstants;
import constants.WorldConstants;
import database.DatabaseConnection;
import handling.MapleServerHandler;
import handling.RecvPacketOpcode;
import handling.SendPacketOpcode;
import handling.cashshop.CashShopServer;
import handling.channel.MapleGuildRanking;
import handling.login.LoginInformationProvider;
import handling.login.LoginServer;
import handling.world.World;
import handling.world.family.MapleFamily;
import handling.world.guild.MapleGuild;
import server.Timer.*;
import server.cashshop.CashItemFactory;
import server.life.MapleLifeFactory;
import server.life.MapleMonsterInformationProvider;
import server.life.MobSkillFactory;
import server.life.PlayerNPC;
import server.maps.MapleMapFactory;
import server.quest.MapleQuest;
import server.status.MapleBuffStatus;
import server.worldevents.MapleOxQuizFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class Start {

    public static final Start instance = new Start();
    public static long startTime = System.currentTimeMillis();

    public static void main(final String args[]) throws InterruptedException {
        instance.run();
    }

    private void resetAllLoginState() {
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement("UPDATE accounts SET loggedin = 0")) {
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("【錯誤】 請確認資料庫是否正確連接");
        }
    }

    private void initTimers() {
        WorldTimer.getInstance().start();
        PokeTimer.getInstance().start();
        EtcTimer.getInstance().start();
        MapTimer.getInstance().start();
        EventTimer.getInstance().start();
        BuffTimer.getInstance().start();
        PingTimer.getInstance().start();
        CloneTimer.getInstance().start();
    }

    public void run() throws InterruptedException {

        System.out.println("楓之谷v145模擬器 啟動中" + "." + ServerConstants.MAPLE_PATCH + "..");
        ServerProperties.load();
        ServerConstants.SERVER_IP = ServerConfig.WORLD_INTERFACE;

        SendPacketOpcode.reloadValues();
        RecvPacketOpcode.reloadValues();
        MapleBuffStatus.reloadValues();
        CommandProcessor.Initiate();
        this.resetAllLoginState();
        // Worlds
        WorldConstants.init();
        World.init();
        // Timers
        this.initTimers();
        // WorldConfig Handler
        MapleServerHandler.initiate();

        // Information
        MapleItemInformationProvider.getInstance().runEtc();
        MapleMonsterInformationProvider.getInstance().load();
        MapleItemInformationProvider.getInstance().runItems();

        // Servers
        LoginServer.initiate();
        CashShopServer.initiate();

        LoginServer.setOn();
        // Every other instance cache :)
        SkillFactory.load();
        LoginInformationProvider.getInstance();
        MapleGuildRanking.getInstance().load();
        MapleGuild.loadAll();
        MapleFamily.loadAll();
        MapleLifeFactory.loadQuestCounts();
        MapleQuest.InitQuests();
        RandomRewards.Load();
        MapleOxQuizFactory.getInstance();
        MapleCarnivalFactory.getInstance();
        //CharacterCardFactory.getInstance().initialize();
        MobSkillFactory.getInstance();
        SpeedRunner.loadSpeedRuns();
        MapleInventoryIdentifier.getInstance();
        MapleMapFactory.loadCustomLife();
        CashItemFactory.getInstance().initialize();
        PlayerNPC.LoadAll();// touch - so we see database problems early...
        MapleMonsterInformationProvider.getInstance().addExtra();
        RankingWorker.run();
        System.out.printf("Server is Opened - %ds ", (System.currentTimeMillis()-startTime)/1000);
    }
}
