package com.yy.chatgpt.dingtalk;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.HttpUtil;
import com.yy.chatgpt.common.CustomConfig;
import com.yy.chatgpt.dingtalk.request.AtText;
import com.yy.chatgpt.dingtalk.request.DingSendMsg;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * @author yeyu
 * @since 2023-03-03 10:55
 */
@Data
@Slf4j
public class DingTalkOperation {


    private final CustomConfig customConfig;

    public DingTalkOperation(CustomConfig customConfig) {
        this.customConfig = customConfig;
    }

    public void checkMsgValid(Long timestamp, String checkSign) {
        try {
            String stringToSign = timestamp + "\n" + customConfig.getAppSecret();
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(customConfig.getAppSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            String mySign = Base64.encode(signData);
            if (!mySign.equals(checkSign)) {
                throw new IllegalStateException("消息不合法");
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("验证消息异常", e);
        }
    }

    public void sendResponse(String msg, String webHook) {
        log.info("post msg:{}", msg);
        String postResult = HttpUtil.createPost(webHook)
                .body(msg)
                .header("Accept", "*/*")
                .header("Content-Type", "application/json")
                .execute()
                .body();
        log.info("post result:{}", postResult);
    }


    public DingSendMsg setAtUser(DingSendMsg dingSendMsg, String senderId) {
        AtText at = new AtText();
        at.setAtUserIds(Arrays.asList(senderId));
        dingSendMsg.setAt(at);
        return dingSendMsg;
    }

}
