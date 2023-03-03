package com.yy.chatgpt.common;

import lombok.Getter;

/**
 * @author yeyu
 * @since 2023-03-03 16:43
 */
@Getter
public enum RoleEnum {
    // 用户
    USER("user"),
    // 系统
    SYSTEM("system"),
    // 助手
    ASSISTANT("assistant");

    private final String code;

    RoleEnum(String code) {
        this.code = code;
    }

}
