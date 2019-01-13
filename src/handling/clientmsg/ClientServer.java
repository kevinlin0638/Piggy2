package handling.clientmsg;

import constants.ServerConstants;
import handling.MapleServerHandler;
import handling.channel.PlayerStorage;
import handling.netty.ServerConnection;

public class ClientServer {

    private final static int PORT = 5287;
    private static String ip;
    private static ServerConnection acceptor;
    private static PlayerStorage players, playersMTS;
    private static boolean finishedShutdown = false;

    public static void initiate() {
        ip = ServerConstants.SERVER_IP + ":" + PORT;
        acceptor = new ServerConnection(PORT, -2, MapleServerHandler.CLIENT_SERVER);
        players = new PlayerStorage();
        playersMTS = new PlayerStorage();
        System.out.println("[ClientServer]Binding to port " + PORT);

        try {
            acceptor.run();
        } catch (final InterruptedException e) {
            System.err.println("Binding to port " + PORT + " failed");
            acceptor.close();
        }
    }

    public static String getIP() {
        return ip;
    }

    public static PlayerStorage getPlayerStorage() {
        return players;
    }

    public static PlayerStorage getPlayerStorageMTS() {
        return playersMTS;
    }

    public static void shutdown() {
        if (finishedShutdown) {
            return;
        }
        System.out.println("Saving all connected clients...");
        players.disconnectAll();
        playersMTS.disconnectAll();
        System.out.println("Shutting down CS...");
        acceptor.close();
        finishedShutdown = true;
    }

    public static boolean isShutdown() {
        return finishedShutdown;
    }
}
