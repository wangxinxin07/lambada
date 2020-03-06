package com.bj.wxx.lambda.socket.nio;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * nio 客户端
 *
 * @author wangxinxin07
 * @date 2020/3/1
 */
@Slf4j
public class Client {


    private static Selector selector = null;

    public static void main(String[] args) {
        try {
            SocketChannel sc = SocketChannel.open();
            sc.configureBlocking(false);
            selector = Selector.open();
            sc.register(selector, SelectionKey.OP_CONNECT);
            sc.connect(new InetSocketAddress("localhost", 9999));


            while (true) {
                int events = selector.select();
                if (events > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();

                        if (key.isConnectable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            if (channel.isConnectionPending()) {
                                channel.finishConnect();
                            }

                            channel.configureBlocking(false);
                            channel.register(selector, SelectionKey.OP_READ);
                            channel.write(ByteBuffer.wrap(("Hello , i am client ".getBytes())));
                        } else if (key.isReadable()) {
                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            socketChannel.read(buffer);
                            buffer.flip();
                            log.info("read msg from server = {}",new String(buffer.array()));
                        }
                    }

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
