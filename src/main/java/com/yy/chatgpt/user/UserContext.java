package com.yy.chatgpt.user;

import com.yy.chatgpt.common.CustomConfig;
import lombok.Data;

/**
 * @author yeyu
 * @since 2023-03-10 13:14
 */
@Data
public class UserContext {

    private final CustomConfig customConfig;

    private final UserCache userCache;

    private static volatile UserContext context = null;

    private UserContext() {
        this.customConfig = new CustomConfig();
        userCache = new UserCache(customConfig);
    }

    public static UserContext getInstance() {
        if (context == null) {
            synchronized (UserContext.class) {
                if (context == null) {
                    context = new UserContext();
                }
            }
        }
        return context;
    }


}
