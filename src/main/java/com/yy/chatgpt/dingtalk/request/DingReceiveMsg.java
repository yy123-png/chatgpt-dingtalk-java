package com.yy.chatgpt.dingtalk.request;

import lombok.Data;

/**
 * @author yeyu
 * @since 2023-03-03 11:02
 *
 * <p>钉钉机器人接受的消息体格式,说明见:<a href="https://open.dingtalk.com/document/orgapp/receive-message">https://open.dingtalk.com/document/orgapp/receive-message</a>
 */
@Data
public class DingReceiveMsg {

    private String msgtype;

    private String content;

    private String msgId;

    private String createAt;

    private String conversationType;

    private String conversationId;

    private String conversationTitle;

    private String senderId;

    private String senderNick;

    private String senderCorpId;

    private String sessionWebhook;

    private String sessionWebhookExpiredTime;

    private String isAdmin;

    private String chatbotCorpId;

    private String isInAtList;

    private String senderStaffId;

    private String chatbotUserId;

    private String atUsers;

}
