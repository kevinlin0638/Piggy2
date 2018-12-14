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
package handling.netty;

import client.MapleClient;
import constants.ServerConstants;
import handling.RecvPacketOpcode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.AttributeKey;
import tools.HexTool;
import tools.MapleAESOFB;
import tools.data.ByteArrayByteStream;
import tools.data.LittleEndianAccessor;

import java.util.List;

public class MaplePacketDecoder extends ByteToMessageDecoder {

    public static final AttributeKey<DecoderState> DECODER_STATE_KEY = AttributeKey.valueOf(MaplePacketDecoder.class.getName() + ".STATE");

    @Override
    protected void decode(ChannelHandlerContext chc, ByteBuf in, List<Object> message) throws Exception {
        final MapleClient client = chc.channel().attr(MapleClient.CLIENT_KEY).get();
        final DecoderState decoderState = chc.channel().attr(DECODER_STATE_KEY).get();
        if (in.readableBytes() >= 4 && decoderState.packetLength == -1) {
            int packetHeader = in.readInt();
            if (!client.getReceiveCrypto().checkPacket(packetHeader)) {
                chc.channel().close();
                return;
            }
            decoderState.packetLength = MapleAESOFB.getPacketLength(packetHeader);            
        } else if (in.readableBytes() < 4 && decoderState.packetLength == -1) {
            return;
        }
        if (in.readableBytes() >= decoderState.packetLength) {
            byte decryptedPacket[] = new byte[decoderState.packetLength];
            in.readBytes(decryptedPacket);
            decoderState.packetLength = -1;
            client.getReceiveCrypto().crypt(decryptedPacket);
            message.add(decryptedPacket);

            if (ServerConstants.DEBUG) {
                int packetLen = decryptedPacket.length;
                short pHeader = new LittleEndianAccessor(new ByteArrayByteStream(decryptedPacket)).readShort();
                String op = RecvPacketOpcode.nameOf(pHeader);
                if (!RecvPacketOpcode.isSkipLog(RecvPacketOpcode.valueOf(op))) {
                    String tab = "";
                    for (int i = 4; i > op.length() / 8; i--) {
                        tab += "\t";
                    }
                    String t = packetLen >= 10 ? packetLen >= 100 ? packetLen >= 1000 ? "" : " " : "  " : "   ";
                    final StringBuilder sb = new StringBuilder("[Debug] Recv: \t" + op + tab + "\tOpcode:" + HexTool.getOpcodeToString(pHeader) + t + "[" + packetLen + "]");
                    sb.append("\n        Hex : \t").append(HexTool.toString(decryptedPacket));
                    sb.append("\n        Ascii: \t").append(HexTool.toPaddedStringFromAscii(decryptedPacket));
                    System.out.println(sb.toString());
                }
            }
        }
    }

    public static class DecoderState {
        int packetLength = -1;
    }
}
