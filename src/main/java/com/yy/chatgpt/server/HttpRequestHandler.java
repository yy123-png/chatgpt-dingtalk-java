package com.yy.chatgpt.server;

import com.alibaba.fastjson.JSON;
import com.yy.chatgpt.common.RoleEnum;
import com.yy.chatgpt.dingtalk.DingTalkOperation;
import com.yy.chatgpt.dingtalk.request.DingReceiveMsg;
import com.yy.chatgpt.dingtalk.response.DingResponseMsg;
import com.yy.chatgpt.openai.ChatGPTOperation;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author yeyu
 * @since 2023-03-03 13:18
 */
@Slf4j
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final DingTalkOperation dingTalkOperation;

    private final ChatGPTOperation chatGPTOperation;

    public HttpRequestHandler(DingTalkOperation dingTalkOperation,
                              ChatGPTOperation chatGPTOperation) {
        this.dingTalkOperation = dingTalkOperation;
        this.chatGPTOperation = chatGPTOperation;
    }


    private DingReceiveMsg getDingTalkMsg(FullHttpRequest request) {
        Long timestamp = Long.parseLong(request.headers().get("timestamp"));
        String checkSign = request.headers().get("sign");
        boolean isValid = dingTalkOperation.checkMsgValid(timestamp, checkSign);
        ByteBuf content = request.content();
        byte[] buf = new byte[content.readableBytes()];
        content.readBytes(buf);
        String msg = new String(buf);
        DingReceiveMsg dingReceiveMsg = JSON.parseObject(msg, DingReceiveMsg.class);
        if (!isValid) {
            dingTalkOperation.sendResponse(DingResponseMsg.error().toString(), dingReceiveMsg.getSessionWebhook());
            return null;
        }
        return dingReceiveMsg;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

        String content = String.format("Received http request, uri: %s, method: %s, content: %s%n",
                msg.uri(), msg.method(), msg.content().toString(CharsetUtil.UTF_8));
        log.info(content);

        DingReceiveMsg dingReceiveMsg = this.getDingTalkMsg(msg);
        if (!Objects.isNull(dingReceiveMsg)) {
            dingTalkOperation.sendResponse(DingResponseMsg.buildText("gptstring").toString(), dingReceiveMsg.getSessionWebhook());
        }

        DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK);
        HttpHeaders headers = httpResponse.headers();
        headers.add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        headers.add(HttpHeaderNames.CONTENT_LENGTH, httpResponse.content().readableBytes());
        headers.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

        ctx.writeAndFlush(httpResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage());
        ctx.close();
    }
}
