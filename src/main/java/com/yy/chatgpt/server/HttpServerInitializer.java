package com.yy.chatgpt.server;

import com.yy.chatgpt.dingtalk.DingTalkOperation;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;

/**
 * @author yeyu
 * @since 2023-03-03 13:15
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec())
                .addLast("httpAggregator", new HttpObjectAggregator(2 * 1024 * 1024))
                .addLast("compressor", new HttpContentCompressor())
                .addLast(new HttpRequestHandler(new DingTalkOperation("5wPMPutS4nJu0uiQpqKp46a-TZlQ5jaYw8cV0_mwOGBqVRF9YGf-OTSME5gdI1t7")));

    }
}
