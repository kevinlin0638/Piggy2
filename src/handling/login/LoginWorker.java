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
package handling.login;

import client.MapleClient;
import handling.world.World;
import server.Timer.PingTimer;
import tools.packet.CWvsContext;
import tools.packet.LoginPacket;

public class LoginWorker {

    private static long lastUpdate = 0;

    public static void registerClient(final MapleClient client) {
        if (LoginServer.isAdminOnly() && !client.isGM() && !client.isLocalhost()) {
            client.sendPacket(CWvsContext.getPopupMsg("當前伺服器只能管理員登入.\\r\\n我們目前正在測試一些問題\\r\\n請稍後在嘗試。"));
            client.sendPacket(LoginPacket.getLoginFailed(7));
            return;
        }
        if (!client.isGM() && (client.hasBannedMac() || client.hasBannedIP())) {
            client.sendPacket(LoginPacket.getLoginFailed(3)); //
            return;
        }
        if (client.finishLogin() == 0) {
            client.sendPacket(LoginPacket.getAuthSuccessRequest(client));
            for (World iWorld : LoginServer.getWorlds()) {
                client.sendPacket(LoginPacket.getServerList(iWorld.getWorldId(),
                        iWorld.getWorldName(),
                        iWorld.getFlag(),
                        iWorld.getEventMessage(),
                        iWorld.getChannels()));
            }
            client.sendPacket(LoginPacket.getEndOfServerList());
            client.sendPacket(LoginPacket.selectWorld(1));
            String eventMessage = LoginServer.getWorld(0).getEventMessage();
            eventMessage = eventMessage.replaceAll("#b", "");
            eventMessage = eventMessage.replaceAll("#r", "");
            eventMessage = eventMessage.replaceAll("#k", "");
            //client.sendPacket(LoginPacket.sendRecommended(WorldConfig.雪吉拉.getWorldId(), eventMessage));
            client.setIdleTask(PingTimer.getInstance().schedule(() -> client.getSession().close(), 10 * 60 * 10000));
        } else {
            client.sendPacket(LoginPacket.getLoginFailed(7));
        }
    }
}
