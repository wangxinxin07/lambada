package com.bj.wxx.lambda.socket.common;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;

/**
 * socket 客户端
 *
 * @author wangxinxin07
 * @date 2020/3/1
 */
@Slf4j
public class Client {

    public static  Socket socket = null;

    public static void main(String[] args) {

        try {
            socket = new Socket("localhost", 8888);

            //读流
            new Thread(() -> {
                try {
                    InputStream inputStream = socket.getInputStream();
                    BufferedReader bfr = new BufferedReader(new InputStreamReader(inputStream));
                    String line = null;
                    while ((line = bfr.readLine()) != null) {
                        log.info("read line info = {}", line);
                    }
//                    socket.shutdownInput();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            //写流
            new Thread(() -> {
                try {
                    //休眠3s后通知服务端停止
                    Thread.sleep(3000);

                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write("okok ,i know ,please stop it".getBytes());
//                    socket.shutdownOutput();
                    outputStream.flush();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            while (true) {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
