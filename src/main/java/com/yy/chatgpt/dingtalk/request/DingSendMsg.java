package com.yy.chatgpt.dingtalk.request;

import com.alibaba.fastjson.JSON;
import com.yy.chatgpt.common.CommonConstant;
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
public class DingSendMsg {

    private String msgtype;

    private Text text;

    private AtText at;


    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public static DingSendMsg error() {
        return buildText(CommonConstant.ERROR_RESPONSE_TEXT);
    }

    public static DingSendMsg buildText(String content) {
        DingSendMsg dingSendMsg = new DingSendMsg();
        dingSendMsg.setMsgtype("text");
        Text text = new Text();
        text.setContent(content);
        dingSendMsg.setText(text);
        return dingSendMsg;
    }


}
