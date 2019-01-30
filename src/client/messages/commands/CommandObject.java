package client.messages.commands;

import client.MapleClient;
import constants.ServerConstants;

import java.util.Arrays;

public class CommandObject {

    private final String command;

    private final int gmLevelReq;

    private final AbstractsCommandExecute execute;


    public CommandObject(String command, int gmLevelReq, AbstractsCommandExecute execute) {
        this.command = command;
        this.gmLevelReq = gmLevelReq;
        this.execute = execute;
    }

    public String getCommand() {
        return command;
    }

    public int getGmLevelReq() {
        return gmLevelReq;
    }

    public boolean execute(MapleClient c, String[] args) {
        return execute.execute(c, Arrays.asList(args));
    }

    public String getHelpMessage() {
        return execute.getHelpMessage();
    }

    public ServerConstants.CommandType getType(){return execute.getType();}
}
