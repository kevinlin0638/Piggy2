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
package handling.channel.handler;

import client.MapleCharacter;
import client.MapleClient;
import client.messages.CommandProcessor;
import constants.ServerConstants.CommandType;
import handling.channel.ChannelServer;
import handling.world.MapleMessenger;
import handling.world.MapleMessengerCharacter;
import handling.world.World;
import server.WordFilter;
import tools.data.LittleEndianAccessor;
import tools.packet.CField;
import tools.packet.CWvsContext;

public class ChatHandler {

    public static void GeneralChat(final LittleEndianAccessor slea, final MapleClient client) {

        final MapleCharacter chr = client.getPlayer();

        if (chr == null || chr.inCS()) {
            return;
        }

        int tick = slea.readInt();
        String text = slea.readMapleAsciiString();
        int unk = slea.readByte();

        text = WordFilter.illegalArrayCheck(text, client.getPlayer());

        if (!chr.isGM()) {
            if (chr.isMuted()) {
                chr.dropMessage(5, "[系統訊息] " + "目前被GM禁止說話中。");
                return;
            }
            if (client.getPlayer().getWatcher() != null) {
                client.getPlayer().getWatcher().dropMessage(5, "[" + client.getPlayer().getName() + "] [Chat Type: All] : " + text);
                return;
            }
            if (text.equalsIgnoreCase("我愛小豬谷")) {
                chr.finishAchievement(11);
            }
        }
        //TODO: 處理GM、玩家指令
        if (chr != null && !CommandProcessor.processCommand(client, text, CommandType.NORMAL)) {
            client.getPlayer().getMap().broadcastMessage(CField.getChatText(chr.getId(), text, chr.isSuperGM(), unk), chr.getTruePosition());
        }

    }

    private static String getChatType(int type) {
        switch (type) {
            case 0:
                return "好友";
            case 1:
                return "隊伍";
            case 2:
                return "公會";
            case 3:
                return "聯盟";
            case 4:
                return "遠征隊";
        }
        return "Unknown";
    }

    public static void Spouse_Chat(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        /* 
        02 00 // size of the spouse's name
        3C 33 // spouses name
        05 00 // length of message
        61 62 63 64 65 // message 
         */
        if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
            return;
        }
        final String spouse = slea.readMapleAsciiString();
        final String message = slea.readMapleAsciiString();
        final int channel = World.Find.findChannel(spouse);
        final int world = World.Find.findWorld(spouse);
        if (c.getPlayer().getMarriageId() == 0 || !c.getPlayer().getPartner().equalsIgnoreCase(spouse)) {
            c.getPlayer().dropMessage(5, "You are not married or your spouse is offline.");
            c.sendPacket(CWvsContext.enableActions());
            return;
        }
        if (channel > 0) {
            MapleCharacter spouseChar = ChannelServer.getInstance(world, channel).getPlayerStorage().getCharacterByName(spouse);
            if (spouseChar == null) {
                c.getPlayer().dropMessage(5, "You are not married or your spouse is offline.");
                c.sendPacket(CWvsContext.enableActions());
                return;
            }
            // TODO: code spouse-chat watch system: if (c.getPlayer().getWatcher() != null) { return; }
            spouseChar.getClient().sendPacket(CWvsContext.toSpouse(c.getPlayer().getName(), message, 5));
            c.sendPacket(CWvsContext.toSpouse(c.getPlayer().getName(), message, 5));
        } else {
            c.getPlayer().dropMessage(5, "You are not married or your spouse is offline.");
        }
    }

    public static void Others(final LittleEndianAccessor slea, final MapleClient c, final MapleCharacter chr) {
        final int type = slea.readByte();
        final byte numRecipients = slea.readByte();
        if (numRecipients <= 0) {
            return;
        }
        int recipients[] = new int[numRecipients];

        for (byte i = 0; i < numRecipients; i++) {
            recipients[i] = slea.readInt();
        }
        final String chattext = slea.readMapleAsciiString();
        if (chr == null || !chr.getCanTalk()) {
            c.sendPacket(CWvsContext.broadcastMsg(6, "You have been muted and are therefore unable to talk."));
            return;
        }
        if (c.getPlayer().getMuteLevel() == 1) {
            c.getPlayer().dropMessage(5, "You have been muted, therefore you can't talk.");
            return;
        }

        if (c.getPlayer().getWatcher() != null) {
            c.getPlayer().getWatcher().dropMessage(5, "[" + c.getPlayer().getName() + " - Chat: " + getChatType(type) + "] : " + chattext);
        }
//
//        if (chattext.length() <= 0 || CommandProcessor.processCommand(c, chattext, CommandType.NORMAL)) {
//            return;
//        }
        switch (type) {
            case 0:
                World.Buddy.buddyChat(recipients, chr.getId(), chr.getName(), chattext);
                break;
            case 1:
                if (chr.getParty() == null) {
                    break;
                }
                World.Party.partyChat(chr.getParty().getId(), chattext, chr.getName());
                break;
            case 2:
                if (chr.getGuildId() <= 0) {
                    break;
                }
                World.Guild.guildChat(chr.getGuildId(), chr.getName(), chr.getId(), chattext);
                break;
            case 3:
                if (chr.getGuildId() <= 0) {
                    break;
                }
                World.Alliance.allianceChat(chr.getGuildId(), chr.getName(), chr.getId(), chattext);
                break;
            case 4:
                if (chr.getParty() == null || chr.getParty().getExpeditionId() <= 0) {
                    break;
                }
                World.Party.expedChat(chr.getParty().getExpeditionId(), chattext, chr.getName());
                break;
        }
    }

    public static final void Messenger(final LittleEndianAccessor slea, final MapleClient c) {
        String input;
        MapleMessenger messenger = c.getPlayer().getMessenger();

        switch (slea.readByte()) {
            case 0x00: // open
                if (messenger == null) {
                    int messengerid = slea.readInt();
                    if (messengerid == 0) { // create
                        c.getPlayer().setMessenger(World.Messenger.createMessenger(new MapleMessengerCharacter(c.getPlayer())));
                    } else { // join
                        messenger = World.Messenger.getMessenger(messengerid);
                        if (messenger != null) {
                            final int position = messenger.getLowestPosition();
                            if (position > -1 && position < 4) {
                                c.getPlayer().setMessenger(messenger);
                                World.Messenger.joinMessenger(messenger.getId(), new MapleMessengerCharacter(c.getPlayer()), c.getPlayer().getName(), c.getWorld(), c.getChannel());
                            }
                        }
                    }
                }
                break;
            case 0x02: // exit
                if (messenger != null) {
                    final MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(c.getPlayer());
                    World.Messenger.leaveMessenger(messenger.getId(), messengerplayer);
                    c.getPlayer().setMessenger(null);
                }
                break;
            case 0x03: // invite

                if (messenger != null) {
                    final int position = messenger.getLowestPosition();
                    if (position <= -1 || position >= 4) {
                        return;
                    }
                    input = slea.readMapleAsciiString();
                    final MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(input);

                    if (target != null) {
                        if (target.getMessenger() == null) {
                            if (!target.isIntern() || c.getPlayer().isIntern()) {
                                c.sendPacket(CField.messengerNote(input, 4, 1));
                                target.getClient().sendPacket(CField.messengerInvite(c.getPlayer().getName(), messenger.getId()));
                            } else {
                                c.sendPacket(CField.messengerNote(input, 4, 0));
                            }
                        } else {
                            c.sendPacket(CField.messengerChat(c.getPlayer().getName(), " : " + target.getName() + " is already using Maple Messenger."));
                        }
                    } else {
                        if (World.isConnected(input)) {
                            World.Messenger.messengerInvite(c.getPlayer().getName(), messenger.getId(), input, c.getWorld(), c.getChannel(), c.getPlayer().isIntern());
                        } else {
                            c.sendPacket(CField.messengerNote(input, 4, 0));
                        }
                    }
                }
                break;
            case 0x05: // decline
                final String targeted = slea.readMapleAsciiString();
                final MapleCharacter target = c.getChannelServer().getPlayerStorage().getCharacterByName(targeted);
                if (target != null) { // This channel
                    if (target.getMessenger() != null) {
                        target.getClient().sendPacket(CField.messengerNote(c.getPlayer().getName(), 5, 0));
                    }
                } else { // Other channel
                    if (!c.getPlayer().isIntern()) {
                        World.Messenger.declineChat(targeted, c.getPlayer().getName());
                    }
                }
                break;
            case 0x06: // message
                if (messenger != null) {
                    final String charname = slea.readMapleAsciiString();
                    final String text = "";//v145 不需要
                    final String chattext = charname + "" + text;
                    if (c.getPlayer().getWatcher() != null) {
                        if (text.equals("0") || text.equals("1")) {
                            return;
                        } else {
                            c.getPlayer().getWatcher().dropMessage(5, "[" + c.getPlayer().getName() + " - Chat: Messenger]" + text);
                        }
                    }
                    World.Messenger.messengerChat(messenger.getId(), charname, text, c.getPlayer().getName());

                }
                break;
        }
    }

    public static final void Whisper_Find(final LittleEndianAccessor slea, final MapleClient c) {
        final byte mode = slea.readByte();
        slea.readInt(); //ticks
        switch (mode) {
            case 68: //buddy
            case 5: { // Find

                final String recipient = slea.readMapleAsciiString();
                MapleCharacter player = c.getChannelServer().getPlayerStorage().getCharacterByName(recipient);
                if (player != null) {
                    if (!player.isIntern() || c.getPlayer().isIntern() && player.isIntern()) {

                        c.sendPacket(CField.getFindReplyWithMap(player.getName(), player.getMap().getId(), mode == 68));
                    } else {
                        c.sendPacket(CField.getWhisperReply(recipient, (byte) 0));
                    }
                } else { // Not found
                    int ch = World.Find.findChannel(recipient);
                    int wl = World.Find.findWorld(recipient);
                    if (ch > 0) {
                        player = ChannelServer.getInstance(wl, ch).getPlayerStorage().getCharacterByName(recipient);
                        if (player == null) {
                            break;
                        }
                        if (player != null) {
                            if (!player.isIntern() || (c.getPlayer().isIntern() && player.isIntern())) {
                                c.sendPacket(CField.getFindReply(recipient, (byte) ch, mode == 68));
                            } else {
                                c.sendPacket(CField.getWhisperReply(recipient, (byte) 0));
                            }
                            return;
                        }
                    }
                    if (ch == -10) {
                        c.sendPacket(CField.getFindReplyWithCS(recipient, mode == 68));
                    } else if (ch == -20) {
                        c.getPlayer().dropMessage(5, "'" + recipient + "' is at the MTS which doesnt exist so let's ban him/her."); //idfc
                    } else {
                        c.sendPacket(CField.getWhisperReply(recipient, (byte) 0));
                    }
                }
                break;
            }
            case 6: { // Whisper
                if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
                    return;
                }
                final String recipient = slea.readMapleAsciiString();
                final String text = slea.readMapleAsciiString();
                final int ch = World.Find.findChannel(recipient);
                final int wl = World.Find.findWorld(recipient);
                if (ch > 0) {
                    MapleCharacter player = ChannelServer.getInstance(wl, ch).getPlayerStorage().getCharacterByName(recipient);
                    if (player == null) {
                        break;
                    }
                    if (c.getPlayer().getWatcher() != null) {
                        c.getPlayer().getWatcher().dropMessage(5, "[" + c.getPlayer().getName() + "] Whispered to [" + recipient + "] : " + text);
                    }
                    player.getClient().sendPacket(CField.getWhisper(c.getPlayer().getName(), c.getChannel(), text));
                    if (!c.getPlayer().isIntern() && player.isIntern()) {
                        c.sendPacket(CField.getWhisperReply(recipient, (byte) 0));
                    } else {
                        c.sendPacket(CField.getWhisperReply(recipient, (byte) 1));
                    }
                } else {
                    c.sendPacket(CField.getWhisperReply(recipient, (byte) 0));
                }
            }
            break;
        }
    }
}
