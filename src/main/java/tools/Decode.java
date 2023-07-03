package tools;

import client.MapleClient;
import constants.ServerConstants;
import handling.RecvPacketOpcode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import server.Randomizer;
import tools.data.ByteArrayByteStream;
import tools.data.LittleEndianAccessor;

import java.io.*;

public class Decode {
    public static void main(String[] args) {

        try (BufferedReader br = new BufferedReader(new FileReader("./done.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                ByteBuf in = Unpooled.copiedBuffer(new byte[]{});
                System.out.println(dec(in));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String dec(ByteBuf in){
        final byte ivRecv[] = new byte[]{(byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255), (byte) Randomizer.nextInt(255)};
        final MapleAESOFB aesofb = new MapleAESOFB(ivRecv, ServerConstants.MAPLE_VERSION);
        final DecoderStates decoderState = new DecoderStates();

        if (in.readableBytes() >= 4 && decoderState.packetLength == -1) {
            int packetHeader = in.readInt();
            if (!aesofb.checkPacket(packetHeader)) {
                return "";
            }
            decoderState.packetLength = MapleAESOFB.getPacketLength(packetHeader);
        } else if (in.readableBytes() < 4 && decoderState.packetLength == -1) {
            return "";
        }
        if (in.readableBytes() >= decoderState.packetLength) {
            byte decryptedPacket[] = new byte[decoderState.packetLength];
            in.readBytes(decryptedPacket);
            decoderState.packetLength = -1;

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
                return sb.toString();
            }

        }
        return "";
    }

    public static class DecoderStates {
        int packetLength = -1;
    }
}
