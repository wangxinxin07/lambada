package com.bj.wxx.lambda.socket.common;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

/**
 * socket server端
 *
 * @author wangxinxin07
 * @date 2020/3/1
 */
@Slf4j
public class Server {

    public static  boolean stop = false;

    public static void main(String[] args) {

        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            while (true) {
                Socket accept = serverSocket.accept();

                new Thread(()->{
                    try {
                        OutputStream outputStream = accept.getOutputStream();
                        int i = 1;
                        log.info("server start write info");
                        while (!stop) {
                            String content = "hello client, this is " + i++ + ",from server";
                            outputStream.write(content.getBytes());
                            outputStream.write("\r\n".getBytes());
                        }
                        log.info("server stop write info");
//                        accept.shutdownOutput();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();


                //启动读流
                new Thread(() -> {
                    try {
                        InputStream inputStream = accept.getInputStream();
                        BufferedReader bfr = new BufferedReader(new InputStreamReader(inputStream));
                        String line = null;
                        while ((line = bfr.readLine()) != null) {
                            log.info("read from clinet info = {}", line);
                        }
                        //读取来自客户端的消息时，停止发送
                        stop = true;
//                        accept.shutdownInput();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
