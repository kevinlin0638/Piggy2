/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.netty;

/**
 * @author o黯淡o
 */

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ServerConnection {

    private final int port;
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1); //The initial connection thread where all the new connections go to
    private final EventLoopGroup workerGroup = new NioEventLoopGroup(); //Once the connection thread has finished it will be moved over to this group where the thread will be managed
    private int world = -1;
    private int channels = -1;
    private ServerBootstrap boot;
    private Channel channel;

    public ServerConnection(int port) {
        this.port = port;
    }

    public ServerConnection(int port, int world, int channels) {
        this.port = port;
        this.world = world;
        this.channels = channels;
    }

    public void run() throws InterruptedException , IOException {
        boot = new ServerBootstrap().group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 2000)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception, IOException {
                        ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(10, 0, 0, TimeUnit.MINUTES));
                        ch.pipeline().addLast(new ServerInitializer(world, channels));
                    }
                });
        channel = boot.bind(port).sync().channel().closeFuture().channel();
    }

    public void close() {
        if (channel != null) {
            channel.close();
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
