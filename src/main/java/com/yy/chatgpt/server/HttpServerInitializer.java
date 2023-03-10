package com.yy.chatgpt.server;

import com.yy.chatgpt.user.UserContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author yeyu
 * @since 2023-03-03 13:15
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {


        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("httpCodec", new HttpServerCodec())
                .addLast("httpAggregator", new HttpObjectAggregator(2 * 1024 * 1024))
                .addLast("compressor", new HttpContentCompressor())
                .addLast("chatHandler", new HttpRequestHandler(UserContext.getInstance()));

    }
}
