package com.yy.chatgpt.dingtalk;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.yy.chatgpt.common.CommonConstant;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author yeyu
 * @since 2023-03-03 10:55
 */
@Data
@Slf4j
public class DingTalkOperation {

    private final String appSecret;

    public DingTalkOperation(String appSecret) {
        this.appSecret = appSecret;
    }

    public boolean checkMsgValid(Long timestamp, String checkSign) {
        try {
            String stringToSign = timestamp + "\n" + appSecret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(appSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            String mySign = Base64.encode(signData);
            if (mySign.equals(checkSign)) {
                return Boolean.TRUE;
            }
            log.warn("消息不合法!");
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("验证消息异常", e);
        }
        return Boolean.FALSE;
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

}
