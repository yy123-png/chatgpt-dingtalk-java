package com.yy.chatgpt.common;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author yeyu
 * @since 2023-03-06 11:02
 */
@Data
@Slf4j
public class CustomConfig {
    private String apiKey = "";

    private Integer maxTokens = 512;

    private String model = "gpt-3.5-turbo";

    private Double temperature = 0.7;

    private String clearToken = "清空会话";

    private String systemToken = "设定角色：";

    private String appSecret = "";

    public CustomConfig() {
        Resource resource = new ClassPathResource("config.json");
        StringBuilder builder = new StringBuilder();

        try (InputStream inputStream = resource.getStream()) {
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String s;
            while ((s = bufferedReader.readLine()) != null) {
                builder.append(s);
            }
        } catch (Exception e) {
            log.error("读取文件异常,使用默认配置", e);
        }

        String jsonConfig = builder.toString();
        if (CharSequenceUtil.isBlank(jsonConfig)) {
            log.warn("配置为空,使用默认配置");
        }
        JSONObject config = JSON.parseObject(jsonConfig);
        this.apiKey = config.getString("api_key") == null ? this.apiKey : config.getString("api_key");
        this.maxTokens = config.getInteger("max_tokens") == null ? this.maxTokens : config.getInteger("max_tokens");
        this.model = config.getString("model") == null ? this.model : config.getString("model");
        this.temperature = config.getDouble("temperature") == null ? this.temperature : config.getDouble("temperature");
        this.clearToken = config.getString("clear_token") == null ? this.clearToken : config.getString("clear_token");
        this.systemToken = config.getString("system_token") == null ? this.systemToken : config.getString("system_token");
        this.appSecret = config.getString("app_secret") == null ? this.appSecret : config.getString("app_secret");
    }
}
