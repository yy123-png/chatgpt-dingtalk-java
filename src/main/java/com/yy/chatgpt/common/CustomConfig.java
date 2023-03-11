package com.yy.chatgpt.common;

import cn.hutool.core.text.CharSequenceUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * @author yeyu
 * @since 2023-03-06 11:02
 */
@Data
@Slf4j
public class CustomConfig {
    // ChatGPT ApiKey
    private String apiKey = "";
    // 最大令牌数
    private Integer maxTokens = 512;
    // 使用模型
    private String model = "gpt-3.5-turbo";
    // 见chatgpt api说明
    private Double temperature = 0.7;
    // 清空缓存指令
    private String clearToken = "清空会话";
    // 切换系统角色指令
    private String systemToken = "设定角色：";
    // 钉钉机器人 appSecret
    private String appSecret = "";

    // 缓存超时时间 (秒)
    private Integer sessionTimeOut = 60;

    private String httpProxyHost = "127.0.0.1";

    private Integer httpProxyPort = 7890;

    public CustomConfig() {
        String path = System.getProperty("user.dir") + File.separator + "/app/config.json";

        StringBuilder builder = new StringBuilder();
        try (FileInputStream inputStream = new FileInputStream(path);) {
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(reader);

            String s;
            while ((s = bufferedReader.readLine()) != null) {
                builder.append(s);
            }
        } catch (Exception e) {
            log.error("读取文件异常,使用默认配置", e);
            return;
        }

        String jsonConfig = builder.toString();
        if (CharSequenceUtil.isBlank(jsonConfig)) {
            log.warn("配置为空,使用默认配置");
            return;
        }
        try {
            JSONObject config = JSON.parseObject(jsonConfig);
            this.apiKey = config.getString("apiKey") == null ? this.apiKey : config.getString("apiKey");
            this.maxTokens = config.getInteger("maxTokens") == null ? this.maxTokens : config.getInteger("maxTokens");
            this.model = config.getString("model") == null ? this.model : config.getString("model");
            this.temperature = config.getDouble("temperature") == null ? this.temperature : config.getDouble("temperature");
            this.clearToken = config.getString("clearToken") == null ? this.clearToken : config.getString("clearToken");
            this.systemToken = config.getString("systemToken") == null ? this.systemToken : config.getString("systemToken");
            this.appSecret = config.getString("appSecret") == null ? this.appSecret : config.getString("appSecret");
            this.sessionTimeOut = config.getInteger("sessionTimeOut") == null ? this.sessionTimeOut : config.getInteger("sessionTimeOut");
            this.httpProxyHost = config.getString("httpProxyHost") == null ? this.httpProxyHost : config.getString("httpProxyHost");
            this.httpProxyPort = config.getInteger("httpProxyPort") == null ? this.httpProxyPort : config.getInteger("httpProxyPort");
        } catch (Exception e) {
            log.error("配置文件存在错误");
        }

    }
}
