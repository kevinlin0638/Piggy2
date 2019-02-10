/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.netty;

/**
 * @author o黯淡o
 */

import handling.MapleServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.io.IOException;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {

    private int world;
    private int channels;

    public ServerInitializer(int world, int channels) {
        this.world = world;
        this.channels = channels;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws IOException {
        ChannelPipeline pipe = channel.pipeline();
        if(world == -2) {
            pipe.addLast("handler", new MapleServerHandler(world, channels));
            pipe.addLast("encoder", new MaplePacketEncoder()); //encodes the packet
            return;
        }
        pipe.addLast("decoder", new MaplePacketDecoder()); // decodes the packet
        pipe.addLast("encoder", new MaplePacketEncoder()); //encodes the packet
        pipe.addLast("handler", new MapleServerHandler(world, channels));
    }

}
