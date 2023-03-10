package com.yy.chatgpt.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yeyu
 * @since 2023-03-03 13:07
 */
@Slf4j
public class HttpServer {

    private final int port;

    private final EventLoopGroup bossGroup;

    private final EventLoopGroup workGroup;

    private final ServerBootstrap serverBootstrap;

    public HttpServer(int port) {
        this.port = port;
        if (Epoll.isAvailable()) {
            bossGroup = new EpollEventLoopGroup();
            workGroup = new EpollEventLoopGroup();
        } else {
            bossGroup = new NioEventLoopGroup();
            workGroup = new NioEventLoopGroup();
        }
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workGroup).
                channel(Epoll.isAvailable() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new HttpServerInitializer())
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true);
    }


    public void start() {
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            log.info("服务器启动成功,端口:{}", port);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("服务器启动失败", e);
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

}
