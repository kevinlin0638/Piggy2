package client.messages.commands;

import client.MapleClient;

import java.util.List;

public abstract class AbstractsCommandExecute {

    public abstract boolean execute(MapleClient c, List<String> args);

    public abstract String getHelpMessage();

}
