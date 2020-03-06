package com.bj.wxx.lambda.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wangL
 * 09:33 2020/2/26.
 */
@Slf4j
public class AlarmSocketClientTest {
    private static int port = 31232;
    private void start() {
        Socket socket;
        try {
            socket = new Socket("localhost", 31232);
            //读取服务器端数据
            DataInputStream input = new DataInputStream(socket.getInputStream());
            //向服务器端发送数据
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
//            log.info("请输入: \t");
//            String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
//            int msgType = Integer.parseInt(str);
//            sendMsg(msgType, out);


//            byte[] byteI = new byte[1024 * 5];
//            AtomicInteger i = new AtomicInteger();
//            while (true) {
//                sendMsg(3, out);
//                /*if (i.get() > 5) {
//                    //模拟客户端发现实时告警不连续
//                    sendMsg(3, out);
//                } else {
//                    try {
//                        while ((input.read(byteI) != -1)) {
//                            SocketAlarmMsgDTO dto = SocketMsgUtil.parserMsg(byteI);
//                            if (dto != null) {
//                                i.getAndIncrement();
//                                String body = dto.getBody();
//                                log.info("服务器端返回过来的是: " + dto.toString());
//                            }
//                            if (i.get() > 5) {
//                                //模拟客户端发现实时告警不连续
//                                sendMsg(3, out);
//                            }
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }*/
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start2() {
        Selector selector;
        SocketChannel channel;
        try {
            selector = Selector.open();
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_CONNECT);
            channel.connect(new InetSocketAddress("localhost", 31232));
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            while (true) {

                int events = selector.select();
                if (events > 0) {
                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                    while (it.hasNext()){
                        SelectionKey key = it.next();
                        it.remove();

                        if (key.isConnectable()) {
                            SocketChannel sc = (SocketChannel) key.channel();
                            if (sc.isConnectionPending()) {
                                sc.finishConnect();
                            }
                            sc.configureBlocking(false);
                            sc.register(selector, SelectionKey.OP_READ);
                            byte[] bytes = SocketMsgUtil.buildMsg("reqLoginAlarm;user=wl;key=123;type=msg", 1);
                            sc.write(ByteBuffer.wrap(bytes));
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void start3() throws IOException, InterruptedException {
        Selector selector = Selector.open();
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_CONNECT);
        channel.connect(new InetSocketAddress("localhost", 31232));
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (true) {
            while (selector.select() == 0) System.out.println("not connect!");
            if (channel.isOpen()) {
                channel.finishConnect();
                byte[] bytes = SocketMsgUtil.buildMsg("reqLoginAlarm;user=wl;key=123;type=msg", 1);
                channel.write((ByteBuffer.wrap(bytes)));
                int a = 0, b = 0, c = 0;
                while ((c = channel.read(buffer)) != -1) {
                    if (c > 0) {
                        //模拟告警序列丢失
                        if (a == 5 && b == 0) {
                            b = 1;
                            byte[] bytes2 = SocketMsgUtil.buildMsg("reqSyncAlarmMsg;reqId=3;alarmSeq=10", 3);
                            channel.write((ByteBuffer.wrap(bytes2)));
                            log.info("stop " + channel.isOpen());
                            buffer.flip();
                        } else {
                            a++;
                            SocketAlarmMsgDTO dto = SocketMsgUtil.parserMsg(buffer.array());
                            assert dto != null;
                            log.info("accept server msg[{}]", dto.toString());
                            buffer.flip();
                            buffer.clear();
                            log.info(buffer.position() + "==" + c + "-=-=-=-" + a+"---------"+buffer.array().length);
                        }
                    }
                }
            }
            selector.close();
            channel.close();
        }
    }

    private void sendMsg(int msgType, DataOutputStream out) throws IOException, InterruptedException {
        byte[] bytes;
        switch (msgType) {
            case 1:
                bytes = SocketMsgUtil.buildMsg("reqLoginAlarm;user=wl;key=123;type=msg", msgType);
//                      bytes = SocketMsgUtil.buildMsg("reqLoginAlarm;user=wl;key=123;type=ftp", msgType);
                break;
            case 3:
                bytes = SocketMsgUtil.buildMsg("reqSyncAlarmMsg;reqId=3;alarmSeq=10", msgType);
                break;
            case 5:
                bytes = SocketMsgUtil.buildMsg("reqSyncAlarmFile;reqId=5;startTime=2014-11-27 10:00:00;endTime=2014-11-27 10:30:00; " +
                        "syncSource =0", msgType);
//                        bytes = SocketMsgUtil.buildMsg("reqSyncAlarmFile;reqId=5;startTime=2014-11-27 10:00:00;endTime=2014-11-27 10:30:00;
//                        syncSource =1", msgType);
//                        bytes = SocketMsgUtil.buildMsg("reqSyncAlarmFile;reqId=5;alarmSeq=100;syncSource =1", msgType);
                break;
            case 8:
                bytes = SocketMsgUtil.buildMsg("reqHeartBeat;reqId=8", msgType);
                break;
            case 10:
                bytes = SocketMsgUtil.buildMsg("closeConnAlarm;", msgType);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + msgType);
        }
        out.write(bytes);
        out.flush();
        Thread.sleep(1000);
    }

    public static void main(String[] args) throws Exception {
        log.info("client start...........");
        AlarmSocketClientTest s1 = new AlarmSocketClientTest();
        s1.start2();
    }
}