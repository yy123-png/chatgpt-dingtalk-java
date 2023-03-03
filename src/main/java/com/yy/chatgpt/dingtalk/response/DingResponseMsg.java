package com.yy.chatgpt.dingtalk.response;

import com.alibaba.fastjson.JSON;
import com.yy.chatgpt.common.CommonConstant;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author yeyu
 * @since 2023-03-03 11:16
 *
 * <p>响应信息,文本形式</p>
 */
@Getter
@Setter
public class DingResponseMsg {

    private String msgtype;

    private Text text;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public static DingResponseMsg error() {
        return buildText(CommonConstant.ERROR_RESPONSE_TEXT);
    }

    public static DingResponseMsg buildText(String content) {
        DingResponseMsg dingResponseMsg = new DingResponseMsg();
        dingResponseMsg.setMsgtype("text");
        Text text = new Text();
        text.setContent(content);
        dingResponseMsg.setText(text);
        return dingResponseMsg;
    }

    @Data
    public static class Text {
        private String content;
    }

}
