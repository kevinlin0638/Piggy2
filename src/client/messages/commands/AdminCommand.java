package client.messages.commands;

import client.MapleCharacter;
import client.MapleClient;
import constants.ServerConstants;
import handling.channel.ChannelServer;

import java.util.List;

public class AdminCommand {

    public static ServerConstants.PlayerGMRank getPlayerLevelRequired() {
        return ServerConstants.PlayerGMRank.ADMIN;
    }

    public static class ExpRate extends AbstractsCommandExecute {

        @Override
        public boolean execute(MapleClient c, List<String> args) {
                return false;
        }

        @Override
        public String getHelpMessage() {
            return "@expRate <世界> <頻道> <倍率> - 設定經驗值倍率";
        }
    }

}
