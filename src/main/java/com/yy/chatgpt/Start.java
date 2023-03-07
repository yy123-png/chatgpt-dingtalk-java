package com.yy.chatgpt;

import com.yy.chatgpt.server.HttpServer;

/**
 * @author yeyu
 * @since 2023-03-03 14:04
 */
public class Start {
    public static void main(String[] args) {

        HttpServer httpServer = new HttpServer(8090);
        httpServer.start();
    }
}
