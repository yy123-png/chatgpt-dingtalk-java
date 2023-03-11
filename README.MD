# ChatGPT 3.5 接入钉钉机器人

## 1.使用方式

1. docker 运行

   ```shell
   docker run -itd --name=chatgpt -v 你的配置文件所在位置:/app/config.json --net=host yeyuabc/chatgpt-dingtalk-java
   ```

2. Java编译运行

   下载源码后，使用maven package命令编译，生成Jar包，使用Java编译运行

   ```shell
   java -jar chatgpt-java.jar
   ```

## 2.配置文件说明

 ```json
{
  "apiKey": "", 				   // chatGPT API KEY 
  "maxTokens": 2048,			   // 最大令牌数
  "model": "gpt-3.5-turbo",		   // 模型
  "temperature": 0.7,			   // 温度
  "clearToken": "清空会话",		   // 清空缓存，向机器人发送这个指令会清空上下文	
  "systemToken": "设定角色：",	   // 以这个语句开头的问题，会以system的角色发送信息
  "appSecret": "",				   // 钉钉机器人 appSecret 	 
  "sessionTimeOut": 60,			   // 上下文时效时间
  "useHttpProxy" : true            // 是否使用http代理
  "httpProxyHost": "127.0.0.1",	   // http代理地址
  "httpProxyPort": 7890            // http代理端口
}
 ```



