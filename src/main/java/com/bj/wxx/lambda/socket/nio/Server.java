package com.bj.wxx.lambda.socket.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * nio 服务端
 *
 * @author wangxinxin07
 * @date 2020/3/1
 */
@Slf4j
public class Server {
    private static Selector selector;
    private static ExecutorService service = Executors.newFixedThreadPool(5);

    public static void main(String[] args) {
        ServerSocketChannel ssc = null;
        try {
            ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.bind(new InetSocketAddress(9999));
            selector = Selector.open();
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("NioServer started ......");


            while (true) {
                int events = selector.select();
                if (events > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isAcceptable()) {
                            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                            SocketChannel accept = serverSocketChannel.accept();
                            accept.configureBlocking(false);
                            accept.register(selector, SelectionKey.OP_READ);
                            log.info("accept a new client ");
                        } else if (key.isReadable()) {

                            service.submit(() -> {
                                SocketChannel socketChannel = (SocketChannel) key.channel();
                                ByteBuffer buffer = ByteBuffer.allocate(1024);
                                try {
                                    socketChannel.read(buffer);
                                    buffer.flip();
                                    log.info("read msg from client = {}", new String(buffer.array()));

                                    ByteBuffer outBuffer = ByteBuffer.wrap("ok,i know".getBytes());
                                    socketChannel.write(outBuffer);
                                    key.cancel();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });

                        } else if (key.isWritable()) {
                            log.info("this is writable。。。");
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
