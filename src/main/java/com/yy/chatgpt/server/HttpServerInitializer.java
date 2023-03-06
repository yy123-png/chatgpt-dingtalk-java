package com.yy.chatgpt.server;

import com.yy.chatgpt.common.CustomConfig;
import com.yy.chatgpt.dingtalk.DingTalkOperation;
import com.yy.chatgpt.openai.ChatGPTOperation;
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
        CustomConfig customConfig = new CustomConfig();

        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("httpCodec", new HttpServerCodec())
                .addLast("httpAggregator", new HttpObjectAggregator(2 * 1024 * 1024))
                .addLast("compressor", new HttpContentCompressor())
                .addLast("chatHandler", new HttpRequestHandler(new DingTalkOperation(customConfig), new ChatGPTOperation(customConfig)));

    }
}
