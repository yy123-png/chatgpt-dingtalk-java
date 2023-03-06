package com.yy.chatgpt.dingtalk.request;

import lombok.Data;

import java.util.List;

/**
 * @author yeyu
 * @since 2023-03-06 11:44
 */
@Data
public class AtText {
    private List<String> atMobiles;

    private List<String> atUserIds;

    private Boolean isAtAll = Boolean.FALSE;
}

