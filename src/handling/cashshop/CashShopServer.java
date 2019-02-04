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
package handling.cashshop;

import constants.ServerConstants;
import handling.MapleServerHandler;
import handling.channel.PlayerStorage;
import handling.netty.ServerConnection;
import server.maketshop.MTSStorage;

public class CashShopServer {

    public  static int PORT = 8787;
    private static String ip;
    private static ServerConnection acceptor;
    private static PlayerStorage players, playersMTS;
    private static boolean finishedShutdown = false;

    public static void initiate() {
        ip = ServerConstants.SERVER_IP + ":" + PORT;
        acceptor = new ServerConnection(PORT, -1, MapleServerHandler.CASH_SHOP_SERVER);
        players = new PlayerStorage();
        playersMTS = new PlayerStorage();
        System.out.println("[CashShop]Binding to port " + PORT);

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
        System.out.println("Saving all connected clients (CS)...");
        players.disconnectAll();
        playersMTS.disconnectAll();
//        MTSStorage.getInstance().saveBuyNow(true);
        System.out.println("Shutting down CS...");
        acceptor.close();
        finishedShutdown = true;
    }

    public static boolean isShutdown() {
        return finishedShutdown;
    }
}
