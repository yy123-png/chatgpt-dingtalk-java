package com.yy.chatgpt.server;

import com.alibaba.fastjson.JSON;
import com.yy.chatgpt.common.CommonConstant;
import com.yy.chatgpt.common.RoleEnum;
import com.yy.chatgpt.dingtalk.DingTalkOperation;
import com.yy.chatgpt.dingtalk.request.DingReceiveMsg;
import com.yy.chatgpt.dingtalk.request.DingSendMsg;
import com.yy.chatgpt.openai.ChatGPTOperation;
import com.yy.chatgpt.user.UserContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yeyu
 * @since 2023-03-03 13:18
 */
@Slf4j
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final DingTalkOperation dingTalkOperation;

    private final ChatGPTOperation chatGPTOperation;

    private final UserContext userContext;

    public HttpRequestHandler(UserContext userContext) {
        this.userContext = userContext;
        this.dingTalkOperation = new DingTalkOperation(userContext);
        this.chatGPTOperation = new ChatGPTOperation(userContext);
    }


    private DingReceiveMsg getDingTalkMsg(FullHttpRequest request) {
        ByteBuf content = request.content();
        byte[] buf = new byte[content.readableBytes()];
        content.readBytes(buf);
        String msg = new String(buf);
        return JSON.parseObject(msg, DingReceiveMsg.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

        boolean hasError = Boolean.FALSE;

        if (msg.headers().isEmpty() || msg.content().readableBytes() == 0) {
            log.warn("请求为空,不进行处理");
            DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            HttpHeaders headers = httpResponse.headers();
            headers.add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
            ctx.writeAndFlush(httpResponse);
            return;
        }

        DingReceiveMsg dingReceiveMsg = this.getDingTalkMsg(msg);
        try {
            // 校验消息是否合法
            Long timestamp = Long.parseLong(msg.headers().get("timestamp"));
            String checkSign = msg.headers().get("sign");
            dingTalkOperation.checkMsgValid(timestamp, checkSign);

            String content = dingReceiveMsg.getText().getContent();
            // 是否清空会话
            if (chatGPTOperation.clearSession(dingReceiveMsg.getSenderStaffId(), content)) {
                dingTalkOperation.sendResponse(DingSendMsg.buildText(CommonConstant.CLEAR_SESSION_TEXT).toString(), dingReceiveMsg.getSessionWebhook());
                return;
            }
            // 获取对话角色
            RoleEnum role = chatGPTOperation.getRole(content);
            if (RoleEnum.SYSTEM.equals(role)) {
                content = chatGPTOperation.removeSystemToken(content);
            }
            // 获取OpenAI响应内容
            String reply = chatGPTOperation.makeReply(dingReceiveMsg.getSenderStaffId(), content, role);
            // 设置at人
            DingSendMsg dingSendMsg = DingSendMsg.buildText(reply);
            dingTalkOperation.setAtUser(dingSendMsg, dingReceiveMsg.getSenderStaffId());
            dingTalkOperation.sendResponse(dingSendMsg.toString(), dingReceiveMsg.getSessionWebhook());

        } catch (Exception e) {
            log.error("处理请求发生异常", e);
            hasError = Boolean.TRUE;
            dingTalkOperation.sendResponse(DingSendMsg.error().toString(), dingReceiveMsg.getSessionWebhook());
            throw e;
        } finally {
            DefaultFullHttpResponse httpResponse;
            if (hasError) {
                // 响应错误请求 并发送钉钉错误信息
                httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
                HttpHeaders headers = httpResponse.headers();
                headers.add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
                ctx.writeAndFlush(httpResponse);
            } else {
                // 响应Http请求
                httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                HttpHeaders headers = httpResponse.headers();
                headers.add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
                ctx.writeAndFlush(httpResponse);
            }
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
