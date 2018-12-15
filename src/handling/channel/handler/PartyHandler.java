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
import constants.GameConstants;
import handling.channel.ChannelServer;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.World;
import handling.world.exped.ExpeditionType;
import handling.world.exped.MapleExpedition;
import handling.world.exped.PartySearch;
import handling.world.exped.PartySearchType;
import server.maps.Event_DojoAgent;
import server.maps.FieldLimitType;
import server.quest.MapleQuest;
import tools.data.LittleEndianAccessor;
import tools.packet.CWvsContext.ExpeditionPacket;
import tools.packet.CWvsContext.PartyPacket;

import java.util.ArrayList;
import java.util.List;

public class PartyHandler {

    public static void DenyPartyRequest(final LittleEndianAccessor slea, final MapleClient c) {
        final int action = slea.readByte();
        if ((action == 0x3B) || (action == 0x34) || (action == 0x38) || (action == 0x3A)) { // Party Searching
            final MapleCharacter chr = c.getPlayer().getMap().getCharacterById(slea.readInt());
            // DENY_PARTY_REQUEST Data: [34] [28 00 00] 00 - Apply
            // DENY_PARTY_REQUEST Data: [38] [28 00 00] 00 - Already Applied
            // DENY_PARTY_REQUEST Data: [3B] [28 00 00] 00 - Accepted
            // DENY_PARTY_REQUEST Data: [3A] [28 00 00] 00 - Declined
            switch (action) {
                case 0x34:
                    chr.announce(tools.packet.CWvsContext.broadcastMsg(5, "You have applied for a party.")); // gms-like notice pls
                    chr.announce(tools.packet.CWvsContext.enableActions());
                    break;
                case 0x38:
                    chr.announce(tools.packet.CWvsContext.broadcastMsg(5, "You have already applied for this party.")); // gms-like notice pls
                    chr.announce(tools.packet.CWvsContext.enableActions());
                    break;
                case 0x3B:
                    if (chr != null && chr.getParty() == null && c.getPlayer().getParty() != null && c.getPlayer().getParty().getLeader().getId() == c.getPlayer().getId() && c.getPlayer().getParty().getMembers().size() < 6 && c.getPlayer().getParty().getExpeditionId() <= 0 && chr.getQuestNoAdd(MapleQuest.getInstance(GameConstants.PARTY_INVITE)) == null && c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(GameConstants.PARTY_REQUEST)) == null) {
                        chr.setParty(c.getPlayer().getParty());
                        World.Party.updateParty(c.getPlayer().getParty().getId(), PartyOperation.JOIN, new MaplePartyCharacter(chr));
                        chr.receivePartyMemberHP();
                        chr.updatePartyMemberHP();
                    }
                    break;
                case 0x3A:
                    chr.announce(tools.packet.CWvsContext.broadcastMsg(5, c.getPlayer().getName() + " has denied your request.")); // gms-like notice pls
                    chr.announce(tools.packet.CWvsContext.enableActions());
                    break;
            }
            return;
        }
        final int partyid = slea.readInt();
        if (c.getPlayer().getParty() == null && c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(GameConstants.PARTY_INVITE)) == null) {
            MapleParty party = World.Party.getParty(partyid);
            if (party != null) {
                if (action == (GameConstants.GMS ? 0x1F : 0x1B)) { //accept
                    if (party.getMembers().size() < 6) {
                        c.getPlayer().setParty(party);
                        World.Party.updateParty(partyid, PartyOperation.JOIN, new MaplePartyCharacter(c.getPlayer()));
                        c.getPlayer().receivePartyMemberHP();
                        c.getPlayer().updatePartyMemberHP();
                    } else {
                        c.sendPacket(PartyPacket.partyStatusMessage(22, null));
                    }
                } else if (action != (GameConstants.GMS ? 0x1E : 0x16)) {
                    final MapleCharacter cfrom = c.getChannelServer().getPlayerStorage().getCharacterById(party.getLeader().getId());
                    if (cfrom != null) { // %s has denied the party request.
                        cfrom.getClient().sendPacket(PartyPacket.partyStatusMessage(23, c.getPlayer().getName()));
                    }
                }
            } else {
                c.getPlayer().dropMessage(5, "The party you are trying to join does not exist");
            }
        } else {
            c.getPlayer().dropMessage(5, "You can't join the party as you are already in one");
        }

    }

    public static void PartyOperation(final LittleEndianAccessor slea, final MapleClient c) {
        final int operation = slea.readByte();
        MapleParty party = c.getPlayer().getParty();
        MaplePartyCharacter partyplayer = new MaplePartyCharacter(c.getPlayer());

        switch (operation) {
            case 1: // create
                if (party == null) {
                    party = World.Party.createParty(partyplayer);
                    c.getPlayer().setParty(party);
                    c.sendPacket(PartyPacket.partyCreated(party.getId()));

                } else {
                    //  if (party.getExpeditionId() > 0) {
                    //      c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    //        return;
                    //     }
                    if (partyplayer.equals(party.getLeader()) && party.getMembers().size() == 1) { //only one, reupdate
                        c.sendPacket(PartyPacket.partyCreated(party.getId()));
                    } else {
                        System.out.println("[DEBUG]: Operation 1 : Create");
                        c.getPlayer().dropMessage(5, "You can't create a party as you are already in one");
                    }
                }
                break;
            case 2: // leave
                if (party != null) { //are we in a party? o.O"
                    //if (party.getExpeditionId() > 0) {
                    //   c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    //    return;
                    //  }
                    if (partyplayer.equals(party.getLeader())) { // disband
                        if (GameConstants.isDojo(c.getPlayer().getMapId())) {
                            Event_DojoAgent.failed(c.getPlayer());
                        }
                        if (c.getPlayer().getPyramidSubway() != null) {
                            c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                        }
                        World.Party.updateParty(party.getId(), PartyOperation.DISBAND, partyplayer);
                        if (c.getPlayer().getEventInstance() != null) {
                            c.getPlayer().getEventInstance().disbandParty();
                        }
                    } else {
                        if (GameConstants.isDojo(c.getPlayer().getMapId())) {
                            Event_DojoAgent.failed(c.getPlayer());
                        }
                        if (c.getPlayer().getPyramidSubway() != null) {
                            c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                        }
                        World.Party.updateParty(party.getId(), PartyOperation.LEAVE, partyplayer);
                        if (c.getPlayer().getEventInstance() != null) {
                            c.getPlayer().getEventInstance().leftParty(c.getPlayer());
                        }
                    }
                    c.getPlayer().setParty(null);
                }
                break;
            case 3: // accept invitation
                final int partyid = slea.readInt();
                if (party == null) {
                    party = World.Party.getParty(partyid);
                    if (party != null) {
                        //   if (party.getExpeditionId() > 0) {
                        //     c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                        //      return;
                        //  }
                        if (party.getMembers().size() < 6 && c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(GameConstants.PARTY_INVITE)) == null) {
                            c.getPlayer().setParty(party);
                            World.Party.updateParty(party.getId(), PartyOperation.JOIN, partyplayer);
                            c.getPlayer().receivePartyMemberHP();
                            c.getPlayer().updatePartyMemberHP();
                        } else {
                            c.sendPacket(PartyPacket.partyStatusMessage(22, null));
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "The party you are trying to join does not exist");
                    }
                } else {
                    System.out.println("[DEBUG]: Operation 3 : Accept");
                    c.getPlayer().dropMessage(5, "You can't join the party as you are already in one");
                }
                break;
            case 4: // invite
                if (party == null) {
                    party = World.Party.createParty(partyplayer);
                    c.getPlayer().setParty(party);
                    c.sendPacket(PartyPacket.partyCreated(party.getId()));
                }
                // TODO store pending invitations and check against them
                final String theName = slea.readMapleAsciiString();
                final int theCh = World.Find.findChannel(theName);
                final int theWl = World.Find.findWorld(theName);
                if (theCh > 0) {
                    final MapleCharacter invited = ChannelServer.getInstance(theWl, theCh).getPlayerStorage().getCharacterByName(theName);
                    if (invited != null && invited.getParty() == null && invited.getQuestNoAdd(MapleQuest.getInstance(GameConstants.PARTY_INVITE)) == null) {
                        // if (party.getExpeditionId() > 0) {
                        //    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                        //    return;
                        // }
                        if (party.getMembers().size() < 6) {
                            c.sendPacket(PartyPacket.partyStatusMessage(26, invited.getName()));
                            invited.getClient().sendPacket(PartyPacket.partyInvite(c.getPlayer()));
                        } else {
                            c.sendPacket(PartyPacket.partyStatusMessage(22, null));
                        }
                    } else {
                        c.sendPacket(PartyPacket.partyStatusMessage(21, null));
                    }
                } else {
                    c.sendPacket(PartyPacket.partyStatusMessage(17, null));
                }
                break;
            case 5: // expel
                if (party != null && partyplayer != null && partyplayer.equals(party.getLeader())) {
                    //if (party.getExpeditionId() > 0) {
                    //    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    //     return;
                    //  }
                    final MaplePartyCharacter expelled = party.getMemberById(slea.readInt());
                    if (expelled != null) {
                        if (GameConstants.isDojo(c.getPlayer().getMapId()) && expelled.isOnline()) {
                            Event_DojoAgent.failed(c.getPlayer());
                        }
                        if (c.getPlayer().getPyramidSubway() != null && expelled.isOnline()) {
                            c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                        }
                        World.Party.updateParty(party.getId(), PartyOperation.EXPEL, expelled);
                        if (c.getPlayer().getEventInstance() != null) {
                            /*if leader wants to boot someone, then the whole party gets expelled
                            TODO: Find an easier way to get the character behind a MaplePartyCharacter
                            possibly remove just the expellee.*/
                            if (expelled.isOnline()) {
                                c.getPlayer().getEventInstance().disbandParty();
                            }
                        }
                    }
                }
                break;
            case 6: // change leader
                if (party != null) {
                    // if (party.getExpeditionId() > 0) {
                    //     c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    //     return;
                    //  }
                    final MaplePartyCharacter newleader = party.getMemberById(slea.readInt());
                    if (newleader != null && partyplayer.equals(party.getLeader())) {
                        World.Party.updateParty(party.getId(), PartyOperation.CHANGE_LEADER, newleader);
                    }
                }
                break;
            case 7: //request to  join a party
                if (party != null) {
                    //  if (c.getPlayer().getEventInstance() != null || c.getPlayer().getPyramidSubway() != null || party.getExpeditionId() > 0 || GameConstants.isDojo(c.getPlayer().getMapId())) {
                    //      c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    //       return;
                    //   }
                    if (partyplayer.equals(party.getLeader())) { // disband
                        World.Party.updateParty(party.getId(), PartyOperation.DISBAND, partyplayer);
                    } else {
                        World.Party.updateParty(party.getId(), PartyOperation.LEAVE, partyplayer);
                    }
                    c.getPlayer().setParty(null);
                }
                final int partyid_ = slea.readInt();
                if (GameConstants.GMS) {
                    //TODO JUMP
                    party = World.Party.getParty(partyid_);
                    if (party != null && party.getMembers().size() < 6) {
                        //   if (party.getExpeditionId() > 0) {
                        //       c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                        //       return;
                        //   }
                        final MapleCharacter cfrom = c.getPlayer().getMap().getCharacterById(party.getLeader().getId());
                        if (cfrom != null && cfrom.getQuestNoAdd(MapleQuest.getInstance(GameConstants.PARTY_REQUEST)) == null) {
                            c.sendPacket(PartyPacket.partyStatusMessage(50, c.getPlayer().getName()));
                            cfrom.getClient().sendPacket(PartyPacket.partyRequestInvite(c.getPlayer()));
                        } else {
                            c.getPlayer().dropMessage(5, "Player was not found or player is not accepting party requests.");
                        }
                    }
                }
                break;
            case 8: //allow party requests
                if (slea.readByte() > 0) {
                    c.getPlayer().getQuestRemove(MapleQuest.getInstance(GameConstants.PARTY_REQUEST));
                } else {
                    c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.PARTY_REQUEST));
                }
                break;
            default:
                System.out.println("Unhandled Party function." + operation);
                break;
        }
    }

    public static void AllowPartyInvite(final LittleEndianAccessor slea, final MapleClient c) {
        if (slea.readByte() > 0) {
            c.getPlayer().getQuestRemove(MapleQuest.getInstance(GameConstants.PARTY_INVITE));
        } else {
            c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.PARTY_INVITE));
        }
    }

    public static void MemberSearch(final LittleEndianAccessor slea, final MapleClient c) {
        if (!c.getPlayer().isGM() && /*(c.getPlayer().isInBlockedMap() ||*/ FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit())) {
            c.getPlayer().dropMessage(5, "You may not do party search here."); // is party search blocked anywhere in gms?
            return;
        }
        if (c.getPlayer().getParty() != null && c.getPlayer().getParty().getLeader().getName().equalsIgnoreCase(c.getPlayer().getName())) {
            List<MapleCharacter> chars = new ArrayList<>();
            for (MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
                if (chr != c.getPlayer() /*&& !chr.isGM()*/) {
                    if(chr.getParty() != null && chr.getParty().getId() == c.getPlayer().getParty().getId())
                        continue;
                    chars.add(chr);
                }
            }
            c.sendPacket(PartyPacket.showMemberSearch(chars));
        }
    }

    public static void PartySearch(final LittleEndianAccessor slea, final MapleClient c) {
        if ((c.getPlayer().isInBlockedMap() || FieldLimitType.VipRock.check(c.getPlayer().getMap().getFieldLimit()))) {
            c.getPlayer().dropMessage(5, "You may not do party search here.");
            return;
        }
        List<MapleParty> parties = new ArrayList<>();
        for (MapleCharacter chr : c.getPlayer().getMap().getCharactersThreadsafe()) {
            if (chr != null || parties != null) {
                if (chr.getParty() != null  && !parties.contains(chr.getParty())) {
                    if (c.getPlayer().getParty() != null){
                        if (chr.getParty().getId() != c.getPlayer().getParty().getId())
                            parties.add(chr.getParty());
                    }else {
                        parties.add(chr.getParty());
                    }
                }
            }
        }
        c.sendPacket(PartyPacket.showPartySearch(parties));
    }

    public static void PartyListing(final LittleEndianAccessor slea, final MapleClient c) {
        final int mode = slea.readByte();
        PartySearchType pst;
        MapleParty party;
        switch (mode) {
            case 81: //make
            case 0x9F:
            case -97:
            case -105:
                pst = PartySearchType.getById(slea.readInt());
                if (pst == null || c.getPlayer().getLevel() > pst.maxLevel || c.getPlayer().getLevel() < pst.minLevel) {
                    return;
                }
                if (c.getPlayer().getParty() == null && World.Party.searchParty(pst).size() < 10) {
                    party = World.Party.createParty(new MaplePartyCharacter(c.getPlayer()), pst.id);
                    c.getPlayer().setParty(party);
                    c.sendPacket(PartyPacket.partyCreated(party.getId()));
                    final PartySearch ps = new PartySearch(slea.readMapleAsciiString(), pst.exped ? party.getExpeditionId() : party.getId(), pst);
                    World.Party.addSearch(ps);
                    if (pst.exped) {
                        c.sendPacket(ExpeditionPacket.expeditionStatus(World.Party.getExped(party.getExpeditionId()), true));
                    }
                    c.sendPacket(PartyPacket.partyListingAdded(ps));
                } else {
                    c.getPlayer().dropMessage(1, "Unable to create. Please leave the party.");
                }
                break;
            case 83: //display
            case 0xA1:
            case -95:
            case -103:
                pst = PartySearchType.getById(slea.readInt());
                if (pst == null || c.getPlayer().getLevel() > pst.maxLevel || c.getPlayer().getLevel() < pst.minLevel) {
                    return;
                }
                c.sendPacket(PartyPacket.getPartyListing(pst));
                break;
            case 84: //close
            case 0xA2:
            case -94:
            case -102:
                break;
            case 85: //join
            case 0xA3:
            case -93:
            case -101:
                party = c.getPlayer().getParty();
                final MaplePartyCharacter partyplayer = new MaplePartyCharacter(c.getPlayer());
                if (party == null) { //are we in a party? o.O"
                    final int theId = slea.readInt();
                    party = World.Party.getParty(theId);
                    if (party != null) {
                        PartySearch ps = World.Party.getSearchByParty(party.getId());
                        if (ps != null && c.getPlayer().getLevel() <= ps.getType().maxLevel && c.getPlayer().getLevel() >= ps.getType().minLevel && party.getMembers().size() < 6) {
                            c.getPlayer().setParty(party);
                            World.Party.updateParty(party.getId(), PartyOperation.JOIN, partyplayer);
                            c.getPlayer().receivePartyMemberHP();
                            c.getPlayer().updatePartyMemberHP();
                        } else {
                            c.sendPacket(PartyPacket.partyStatusMessage(21, null));
                        }
                    } else {
                        MapleExpedition exped = World.Party.getExped(theId);
                        if (exped != null) {
                            PartySearch ps = World.Party.getSearchByExped(exped.getId());
                            if (ps != null && c.getPlayer().getLevel() <= ps.getType().maxLevel && c.getPlayer().getLevel() >= ps.getType().minLevel && exped.getAllMembers() < exped.getType().maxMembers) {
                                int partyId = exped.getFreeParty();
                                if (partyId < 0) {
                                    c.sendPacket(PartyPacket.partyStatusMessage(21, null));
                                } else if (partyId == 0) { //signal to make a new party
                                    party = World.Party.createPartyAndAdd(partyplayer, exped.getId());
                                    c.getPlayer().setParty(party);
                                    c.sendPacket(PartyPacket.partyCreated(party.getId()));
                                    c.sendPacket(ExpeditionPacket.expeditionStatus(exped, true));
                                    World.Party.expedPacket(exped.getId(), ExpeditionPacket.expeditionJoined(c.getPlayer().getName()), null);
                                    World.Party.expedPacket(exped.getId(), ExpeditionPacket.expeditionUpdate(exped.getIndex(party.getId()), party), null);
                                } else {
                                    c.getPlayer().setParty(World.Party.getParty(partyId));
                                    World.Party.updateParty(partyId, PartyOperation.JOIN, partyplayer);
                                    c.getPlayer().receivePartyMemberHP();
                                    c.getPlayer().updatePartyMemberHP();
                                    c.sendPacket(ExpeditionPacket.expeditionStatus(exped, true));
                                    World.Party.expedPacket(exped.getId(), ExpeditionPacket.expeditionJoined(c.getPlayer().getName()), null);
                                }
                            } else {
                                c.sendPacket(ExpeditionPacket.expeditionError(0, c.getPlayer().getName()));
                            }
                        }
                    }
                }
                break;
            case 104: // party leader has accepted the "APPLY" from user
                int partyid = slea.readInt(); // 0D 00 00
                int charidToJoin = slea.readInt(); // 28 00 00
                MapleCharacter player = c.getChannelServer().getPlayerStorage().getCharacterById(charidToJoin); // when the leader accepts invite, this user will be joined into the party
                party = player.getParty();
                final MaplePartyCharacter partyChar = new MaplePartyCharacter(player);
                if (party == null) { //are we in a party? o.O"
                    party = World.Party.getParty(partyid);
                    if (party != null) {
                        PartySearch ps = World.Party.getSearchByParty(party.getId());
                        if (ps != null && player.getLevel() <= ps.getType().maxLevel && player.getLevel() >= ps.getType().minLevel && party.getMembers().size() < 6) {
                            player.setParty(party);
                            World.Party.updateParty(party.getId(), PartyOperation.JOIN, partyChar);
                            player.receivePartyMemberHP();
                            player.updatePartyMemberHP();
                        } else {
                            c.sendPacket(PartyPacket.partyStatusMessage(21, null));
                        }
                    }
                }
                c.getPlayer().dropMessage(mode + " : " + slea.toString());
                break;
            default:
                if (c.getPlayer().isGM()) {
                    System.out.println("Unknown PartyListing : " + mode + "\n" + slea);
                }
                break;
        }
    }

    public static void Expedition(final LittleEndianAccessor slea, final MapleClient c) {
        if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
            return;
        }
        final int mode = slea.readByte();
        MapleParty part, party;
        String name;
        switch (mode) {
            case 63: //create [PartySearchID]
            case 134:
                //case 119:
                /*final ExpeditionType et = ExpeditionType.getById(slea.readInt());
                if (et != null && c.getPlayer().getParty() == null && c.getPlayer().getLevel() <= et.maxLevel && c.getPlayer().getLevel() >= et.minLevel) {
                    party = World.Party.createParty(new MaplePartyCharacter(c.getPlayer()), et.exped);
                    c.getPlayer().setParty(party);
                    c.sendPacket(PartyPacket.partyCreated(party.getId()));
                    c.sendPacket(ExpeditionPacket.expeditionStatus(World.Party.getExped(party.getExpeditionId()), true));
                } else {
                    c.sendPacket(ExpeditionPacket.expeditionError(0, ""));
                }*/
                c.getPlayer().dropMessage(1, "目前不支援遠征隊搜尋");
                break;
            case 65: //invite [name]
            case 135:
                //case 120:
                name = slea.readMapleAsciiString();
                final int theCh = World.Find.findChannel(name);
                final int theWl = World.Find.findWorld(name);
                if (theCh > 0) {
                    final MapleCharacter invited = ChannelServer.getInstance(theWl, theCh).getPlayerStorage().getCharacterByName(name);
                    party = c.getPlayer().getParty();
                    if (invited != null && invited.getParty() == null && party != null && party.getExpeditionId() > 0) {
                        MapleExpedition me = World.Party.getExped(party.getExpeditionId());
                        if (me != null && me.getAllMembers() < me.getType().maxMembers && invited.getLevel() <= me.getType().maxLevel && invited.getLevel() >= me.getType().minLevel) {
                            c.sendPacket(ExpeditionPacket.expeditionError(7, invited.getName()));
                            invited.getClient().sendPacket(ExpeditionPacket.expeditionInvite(c.getPlayer(), me.getType().exped));
                        } else {
                            c.sendPacket(ExpeditionPacket.expeditionError(3, invited.getName()));
                        }
                    } else {
                        c.sendPacket(ExpeditionPacket.expeditionError(2, name));
                    }
                } else {
                    c.sendPacket(ExpeditionPacket.expeditionError(0, name));
                }
                break;
            case 66: //accept invite [name] [int - 7, then int 8? lol.]
            case 136:
                // case 121:
                name = slea.readMapleAsciiString();
                final int action = slea.readInt();
                final int theChh = World.Find.findChannel(name);
                final int theWll = World.Find.findWorld(name);
                if (theChh > 0) {
                    final MapleCharacter cfrom = ChannelServer.getInstance(theWll, theChh).getPlayerStorage().getCharacterByName(name);
                    if (cfrom != null && cfrom.getParty() != null && cfrom.getParty().getExpeditionId() > 0) {
                        party = cfrom.getParty();
                        MapleExpedition exped = World.Party.getExped(party.getExpeditionId());
                        if (exped != null && action == 8) {
                            if (c.getPlayer().getLevel() <= exped.getType().maxLevel && c.getPlayer().getLevel() >= exped.getType().minLevel && exped.getAllMembers() < exped.getType().maxMembers) {
                                int partyId = exped.getFreeParty();
                                if (partyId < 0) {
                                    c.sendPacket(PartyPacket.partyStatusMessage(21, null));
                                } else if (partyId == 0) { //signal to make a new party
                                    party = World.Party.createPartyAndAdd(new MaplePartyCharacter(c.getPlayer()), exped.getId());
                                    c.getPlayer().setParty(party);
                                    c.sendPacket(PartyPacket.partyCreated(party.getId()));
                                    c.sendPacket(ExpeditionPacket.expeditionStatus(exped, true));
                                    World.Party.expedPacket(exped.getId(), ExpeditionPacket.expeditionJoined(c.getPlayer().getName()), null);
                                    World.Party.expedPacket(exped.getId(), ExpeditionPacket.expeditionUpdate(exped.getIndex(party.getId()), party), null);
                                } else {
                                    c.getPlayer().setParty(World.Party.getParty(partyId));
                                    World.Party.updateParty(partyId, PartyOperation.JOIN, new MaplePartyCharacter(c.getPlayer()));
                                    c.getPlayer().receivePartyMemberHP();
                                    c.getPlayer().updatePartyMemberHP();
                                    c.sendPacket(ExpeditionPacket.expeditionStatus(exped, true));
                                    World.Party.expedPacket(exped.getId(), ExpeditionPacket.expeditionJoined(c.getPlayer().getName()), null);
                                }
                            } else {
                                c.sendPacket(ExpeditionPacket.expeditionError(3, cfrom.getName()));
                            }
                        } else if (action == 9) {
                            cfrom.getClient().sendPacket(PartyPacket.partyStatusMessage(23, c.getPlayer().getName()));
                        }
                    }
                }
                break;
            case 67: //leaving
            case 137:
                //case 122:
                part = c.getPlayer().getParty();
                if (part != null && part.getExpeditionId() > 0) {
                    final MapleExpedition exped = World.Party.getExped(part.getExpeditionId());
                    if (exped != null) {
                        if (GameConstants.isDojo(c.getPlayer().getMapId())) {
                            Event_DojoAgent.failed(c.getPlayer());
                        }
                        if (exped.getLeader() == c.getPlayer().getId()) { // disband
                            World.Party.disbandExped(exped.getId()); //should take care of the rest
                            if (c.getPlayer().getEventInstance() != null) {
                                c.getPlayer().getEventInstance().disbandParty();
                            }
                        } else if (part.getLeader().getId() == c.getPlayer().getId()) {
                            World.Party.updateParty(part.getId(), PartyOperation.DISBAND, new MaplePartyCharacter(c.getPlayer()));
                            if (c.getPlayer().getEventInstance() != null) {
                                c.getPlayer().getEventInstance().disbandParty();
                            }
                            World.Party.expedPacket(exped.getId(), ExpeditionPacket.expeditionLeft(c.getPlayer().getName()), null);
                        } else {
                            World.Party.updateParty(part.getId(), PartyOperation.LEAVE, new MaplePartyCharacter(c.getPlayer()));
                            if (c.getPlayer().getEventInstance() != null) {
                                c.getPlayer().getEventInstance().leftParty(c.getPlayer());
                            }
                            World.Party.expedPacket(exped.getId(), ExpeditionPacket.expeditionLeft(c.getPlayer().getName()), null);
                        }
                        if (c.getPlayer().getPyramidSubway() != null) {
                            c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                        }
                        c.getPlayer().setParty(null);
                    }
                }
                break;
            case 68: //kick [cid]
            case 138:
                //case 123:
                part = c.getPlayer().getParty();
                if (part != null && part.getExpeditionId() > 0) {
                    final MapleExpedition exped = World.Party.getExped(part.getExpeditionId());
                    if (exped != null && exped.getLeader() == c.getPlayer().getId()) {
                        final int cid = slea.readInt();
                        for (int i : exped.getParties()) {
                            final MapleParty par = World.Party.getParty(i);
                            if (par != null) {
                                final MaplePartyCharacter expelled = par.getMemberById(cid);
                                if (expelled != null) {
                                    if (expelled.isOnline() && GameConstants.isDojo(c.getPlayer().getMapId())) {
                                        Event_DojoAgent.failed(c.getPlayer());
                                    }
                                    World.Party.updateParty(i, PartyOperation.EXPEL, expelled);
                                    if (c.getPlayer().getEventInstance() != null) {
                                        if (expelled.isOnline()) {
                                            c.getPlayer().getEventInstance().disbandParty();
                                        }
                                    }
                                    if (c.getPlayer().getPyramidSubway() != null && expelled.isOnline()) {
                                        c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                                    }
                                    World.Party.expedPacket(exped.getId(), ExpeditionPacket.expeditionLeft(expelled.getName()), null);
                                    break;
                                }
                            }
                        }
                    }
                }
                break;
            case 69: //give exped leader [cid]
                //case 124:
            case 139:
                part = c.getPlayer().getParty();
                if (part != null && part.getExpeditionId() > 0) {
                    final MapleExpedition exped = World.Party.getExped(part.getExpeditionId());
                    if (exped != null && exped.getLeader() == c.getPlayer().getId()) {
                        final MaplePartyCharacter newleader = part.getMemberById(slea.readInt());
                        if (newleader != null) {
                            World.Party.updateParty(part.getId(), PartyOperation.CHANGE_LEADER, newleader);
                            exped.setLeader(newleader.getId());
                            World.Party.expedPacket(exped.getId(), ExpeditionPacket.expeditionLeaderChanged(0), null);
                        }
                    }
                }
                break;
            case 70: //give party leader [cid]
                //case 125:
            case 140:
                part = c.getPlayer().getParty();
                if (part != null && part.getExpeditionId() > 0) {
                    final MapleExpedition exped = World.Party.getExped(part.getExpeditionId());
                    if (exped != null && exped.getLeader() == c.getPlayer().getId()) {
                        final int cid = slea.readInt();
                        for (int i : exped.getParties()) {
                            final MapleParty par = World.Party.getParty(i);
                            if (par != null) {
                                final MaplePartyCharacter newleader = par.getMemberById(cid);
                                if (newleader != null && par.getId() != part.getId()) {
                                    World.Party.updateParty(par.getId(), PartyOperation.CHANGE_LEADER, newleader);
                                }
                            }
                        }
                    }
                }
                break;
            case 71: //change party of diff player [partyIndexTo] [cid]
                //case 126:
            case 141:
                part = c.getPlayer().getParty();
                if (part != null && part.getExpeditionId() > 0) {
                    final MapleExpedition exped = World.Party.getExped(part.getExpeditionId());
                    if (exped != null && exped.getLeader() == c.getPlayer().getId()) {
                        final int partyIndexTo = slea.readInt();
                        if (partyIndexTo < exped.getType().maxParty && partyIndexTo <= exped.getParties().size()) {
                            final int cid = slea.readInt();
                            for (int i : exped.getParties()) {
                                final MapleParty par = World.Party.getParty(i);
                                if (par != null) {
                                    final MaplePartyCharacter expelled = par.getMemberById(cid);
                                    if (expelled != null && expelled.isOnline()) {
                                        final MapleCharacter chr = World.getStorage(expelled.getWorld(), expelled.getChannel()).getCharacterById(expelled.getId());
                                        if (chr == null) {
                                            break;
                                        }
                                        if (partyIndexTo < exped.getParties().size()) { //already exists
                                            party = World.Party.getParty(exped.getParties().get(partyIndexTo));
                                            if (party == null || party.getMembers().size() >= 6) {
                                                c.getPlayer().dropMessage(5, "Invalid party.");
                                                break;
                                            }
                                        }
                                        if (GameConstants.isDojo(c.getPlayer().getMapId())) {
                                            Event_DojoAgent.failed(c.getPlayer());
                                        }
                                        World.Party.updateParty(i, PartyOperation.EXPEL, expelled);
                                        if (partyIndexTo < exped.getParties().size()) { //already exists
                                            party = World.Party.getParty(exped.getParties().get(partyIndexTo));
                                            if (party != null && party.getMembers().size() < 6) {
                                                World.Party.updateParty(party.getId(), PartyOperation.JOIN, expelled);
                                                chr.receivePartyMemberHP();
                                                chr.updatePartyMemberHP();
                                                chr.getClient().sendPacket(ExpeditionPacket.expeditionStatus(exped, true));
                                            }
                                        } else {
                                            party = World.Party.createPartyAndAdd(expelled, exped.getId());
                                            chr.setParty(party);
                                            chr.getClient().sendPacket(PartyPacket.partyCreated(party.getId()));
                                            chr.getClient().sendPacket(ExpeditionPacket.expeditionStatus(exped, true));
                                            World.Party.expedPacket(exped.getId(), ExpeditionPacket.expeditionUpdate(exped.getIndex(party.getId()), party), null);
                                        }
                                        if (c.getPlayer().getEventInstance() != null) {
                                            if (expelled.isOnline()) {
                                                c.getPlayer().getEventInstance().disbandParty();
                                            }
                                        }
                                        if (c.getPlayer().getPyramidSubway() != null) {
                                            c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            default:
                if (c.getPlayer().isGM()) {
                    System.out.println("Unknown Expedition : " + mode + "\n" + slea);
                }
                break;
        }
    }
}
