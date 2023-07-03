package client.messages;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.commands.AbstractsCommandExecute;
import client.messages.commands.AdminCommand;
import client.messages.commands.CommandObject;
import client.messages.commands.PlayerCommand;
import constants.ServerConstants.CommandType;
import constants.ServerConstants.PlayerGMRank;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class CommandProcessor {

    //TODO: 修復 CommandProcessor
    private final static HashMap<String, CommandObject> commandObjects = new HashMap<>();
    private final static HashMap<Integer, ArrayList<String>> commands = new HashMap<>();

    public static void Initiate() {
        initiateCommands();
    }

    /**
     *
     * @param c MapleClient
     * @param type 0 = NormalCommand
     */
    public static void dropHelp(MapleClient c, CommandType type) {
        final StringBuilder sb = new StringBuilder("指令列表:\r\n");
        HashMap<Integer, ArrayList<String>> commandList = new HashMap<>();
        int check = c.getPlayer().getGMLevel();
        commandList = commands;
        for (int i = 0; i <= check; i++) {
            if (commandList.containsKey(i)) {
                sb.append("權限等級： ").append(i).append("\r\n");
                for (String s : commandList.get(i)) {
                    CommandObject co = commandObjects.get(s);
                    if(co.getType() != type)
                        continue;
                    sb.append(co.getHelpMessage());
                    sb.append(" \r\n");
                }
            }
        }
        c.getPlayer().dropNPC(sb.toString());
    }

    private static void sendDisplayMessage(MapleClient c, String msg, CommandType type) {
        if (c.getPlayer() == null) {
            return;
        }
        switch (type) {
            case NORMAL:
                c.getPlayer().dropMessage( msg);
                break;
            case TRADE:
                c.getPlayer().dropMessage("錯誤 : " + msg);
                break;
            case MERCH:
                c.getPlayer().dropMessage(-2, "錯誤 : " + msg);
                break;
        }

    }

    public static boolean processCommand(MapleClient client, String text, CommandType type) {
        if (checkPrefix(text)) {
            return false;
        }

        final MapleCharacter player = client.getPlayer();
        final char prefix = text.charAt(0);
        // Normal

        String[] args = text.split(" ");
        args[0] = args[0].toLowerCase();

        CommandObject commandObject = commandObjects.get(args[0]);
        if (commandObject == null) {
            if (args[0].equals("@help")) {
                dropHelp(client, type);
                return true;
            }
            if(type == CommandType.NORMAL)
                sendDisplayMessage(client, "沒有這個指令,可以使用 @幫助/@help 來查看指令", type);
            else if(type == CommandType.TRADE)
                sendDisplayMessage(client, "沒有這個指令,可以使用 @helpTrade 來查看指令", type);
            else if(type == CommandType.MERCH)
                sendDisplayMessage(client, "沒有這個指令,可以使用 @helpMerch 來查看指令", type);
            return true;
        }

        if(type != commandObject.getType())
            sendDisplayMessage(client, "使用時機錯誤", type);

        if (commandObject.getGmLevelReq() <= player.getGMLevel()) {

            try {
                boolean ret = commandObject.execute(client, args);
                if (!ret) {
                    player.dropMessage("指令錯誤，用法： " + commandObject.getHelpMessage());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                player.dropMessage("Oops! 指令出錯了!");
            }
            return true;
        }
        return false;
    }

    private static boolean checkPrefix(String text) {
        for (PlayerGMRank prefix : PlayerGMRank.values()) {
            if (text.startsWith(String.valueOf(prefix.getCommandPrefix()))) {
                return false;
            }
        }
        return true;
    }

    private static void initiateCommands() {
        Class<?>[] CommandFiles = {
            PlayerCommand.class, AdminCommand.class
        };
        for (Class<?> _class : CommandFiles) {
            try {
                PlayerGMRank rankNeeded = (PlayerGMRank) _class.getMethod("getPlayerLevelRequired", new Class<?>[]{}).invoke(null, (Object[]) null);
                Class<?>[] commandClasses = _class.getDeclaredClasses();
                ArrayList<String> cL = new ArrayList<>();
                for (Class<?> c : commandClasses) {
                    try {
                        if (!Modifier.isAbstract(c.getModifiers()) && !c.isSynthetic()) {
                            Object o = c.newInstance();
                            boolean enabled;
                            try {
                                enabled = c.getDeclaredField("enabled").getBoolean(c.getDeclaredField("enabled"));
                            } catch (NoSuchFieldException ex) {
                                enabled = true;
                            }
                            if (o instanceof AbstractsCommandExecute && enabled) {
                                cL.add(rankNeeded.getCommandPrefix() + c.getSimpleName().toLowerCase());
                                String cmd = rankNeeded.getCommandPrefix() + c.getSimpleName().toLowerCase();
                                commandObjects.put(cmd,
                                        new CommandObject(rankNeeded.getCommandPrefix() + c.getSimpleName().toLowerCase(),
                                                rankNeeded.getLevel(), (AbstractsCommandExecute) o));
                            }
                        }
                    } catch (InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException ex) {
                        ex.printStackTrace();
                    }
                }
                Collections.sort(cL);
                commands.put(rankNeeded.getLevel(), cL);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                ex.printStackTrace();
            }
        }
    }

}
