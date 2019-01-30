package client.messages.commands;

import client.MapleClient;
import constants.ServerConstants;

import java.util.List;

public abstract class AbstractsCommandExecute {

    public abstract boolean execute(MapleClient c, List<String> args);

    public abstract String getHelpMessage();

    public ServerConstants.CommandType getType() {
        return ServerConstants.CommandType.NORMAL;
    }

    public static abstract class TradeExecute extends AbstractsCommandExecute {

        @Override
        public ServerConstants.CommandType getType() {
            return ServerConstants.CommandType.TRADE;
        }
    }

    public static abstract class MerchExecute extends AbstractsCommandExecute {

        @Override
        public ServerConstants.CommandType getType() {
            return ServerConstants.CommandType.MERCH;
        }
    }

}
