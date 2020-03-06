package com.bj.wxx.lambda.socket;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class AlarmServer implements Runnable {
    Selector selector;
    ServerSocketChannel serverSocket;

   static ExecutorService service = Executors.newFixedThreadPool(10);


    public AlarmServer(int port) {
        try {
            selector = Selector.open();
            serverSocket = ServerSocketChannel.open();
            serverSocket.socket().bind(new InetSocketAddress(port));
            serverSocket.configureBlocking(false);
            SelectionKey sk = serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            sk.attach(new Acceptor());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            for (; ; ) {
                int events = selector.select();
                if (events == 0) {
                    continue;
                }
                Set selected = selector.selectedKeys();
                Iterator it = selected.iterator();
                while (it.hasNext()) {
                    dispatch((SelectionKey) (it.next()));
                    it.remove();
                }
//                selected.clear();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public static void main(String[] args) {
        log.error("server start......");
        AlarmServer alarmServer = new AlarmServer(31232);
        alarmServer.run();
    }

    private void dispatch(SelectionKey k) {
        log.info(">>>>>>acceptable=" + k.isAcceptable() +
                ",readable=" + k.isReadable() +
                ",writable=" + k.isWritable());

        Runnable r = (Runnable) (k.attachment());
        if (k.isWritable()) {
            r = new Writer();
        } else if (k.isReadable()) {
            r = new Reader();
        } else if (k.isAcceptable()) {
            r = new Acceptor();
        }
        if (r != null)
//            r.run();
//            new Thread(r).start();
          service.submit(r);
    }



    class Acceptor implements Runnable {
        public void run() {
            try {
                SocketChannel c = serverSocket.accept();
                if (c != null)
                    service.submit(new Handler(selector, c));
//                    new Handler(selector, c);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    class Reader implements Runnable {
        public void run() {
            try {
                SocketChannel c = serverSocket.accept();
                if (c != null)
                    service.submit(new Handler(selector, c));
//                    new Thread(new Handler(selector, c)).start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    class Writer implements Runnable {
        public void run() {
            try {
                SocketChannel c = serverSocket.accept();
                if (c != null){
//                    new Handler(selector, c);
                    service.submit(new Handler(selector, c));
//                    new Thread(new Handler(selector, c)).start();
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    static class Handler implements Runnable {
        private static final int MAXOUT = 1024 * 5;
        private static final int MAXIN = 1024 * 5;
        final SocketChannel socket;
        final SelectionKey sk;
        ByteBuffer input = ByteBuffer.allocate(MAXIN);
        ByteBuffer output = ByteBuffer.allocate(MAXOUT);
        static final int READING = 0, SENDING = 1;
        int state = READING;
        private boolean realTime;
        private String[] stopAlarmReq = new String[1];
        private static String SUCCESS = "succ";
        private static String FAIL = "fail";

        Handler(Selector sel, SocketChannel c) throws IOException {
            socket = c;
            c.configureBlocking(false);
            // 尝试监听读事件
            sk = socket.register(sel, 0);
            sk.attach(this);
            sk.interestOps(SelectionKey.OP_READ);
            sel.wakeup();
        }

        boolean inputIsComplete() {
            return true;
        }

        boolean outputIsComplete() {
            return true;
        }

        void processRead(ByteBuffer bytes, SocketChannel out) {
            try {
                readMsg(bytes.array(), out);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void processWrite(ByteBuffer bytes, SocketChannel out) {
            sendRealTimeAlarm2(socket);
        }

        @Override
        public void run() {
            try {
                if (state == READING) {
                    read();
                } else if (state == SENDING) {
                    send();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

        void read() throws IOException {
            log.info("realtime=" + realTime);
            socket.read(input);
            if (inputIsComplete()) {
                processRead(input, socket);
                input.clear();
                state = SENDING;
                // 读完之后，通常监听写事件
                sk.interestOps(SelectionKey.OP_WRITE);
            }
        }

        void send() {
//            long alarmReq = System.currentTimeMillis();
//            byte[] bytes1 = SocketMsgUtil.buildMsg(alarmReq + "send alarm msg", AlarmMsgTypeEnum.realTimeAlarm.getMsgType());
//            output = ByteBuffer.wrap(bytes1);
            try {
                sendRealTimeAlarm2(socket);
                if (outputIsComplete()) {
                    processWrite(output, socket);
                    // 写完之后，通常监听读事件
                    state = READING;
                    output.clear();
                    sk.interestOps(SelectionKey.OP_READ);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public boolean readMsg(byte[] bytes, SocketChannel out) throws Exception {
            SocketAlarmMsgDTO dto = SocketMsgUtil.parserMsg(bytes);
            if (dto == null) {
                log.error("socket client message error");
                return false;
            }
            log.info("accept client msg [{}]", dto.toString());
            // 处理客户端数据
            String clientInputStr = dto.getBody();
            Byte clientInputMsgType = dto.getMsgType();
            StringBuffer body = new StringBuffer();
            byte serverMsgType = 0;
            Map<String, String> paramMap = new HashMap<>();
            if (StringUtils.isNotEmpty(clientInputStr)) {
                String[] splitRes = clientInputStr.split(";");
                String msgType = splitRes[0];
                for (int i = 1; i < splitRes.length; i++) {
                    String param = splitRes[i];
                    String[] splitParam = param.split("=");
                    String paramName = splitParam[0];
                    String paramValue = splitParam[1];
                    paramMap.put(paramName, paramValue);
                }
                AlarmMsgTypeEnum alarmMsgTypeEnum = EnumUtils.getEnumByType(clientInputMsgType);
                if (alarmMsgTypeEnum == null) {
                    body.append("msg type [").append(clientInputMsgType).append("] can not find");
                    return false;
                } else {
                    switch (alarmMsgTypeEnum) {
                        case reqLoginAlarm:
                            body.append("ackLoginAlarm;");
                            serverMsgType = AlarmMsgTypeEnum.ackLoginAlarm.getMsgType();
                            String user = paramMap.get("user");
                            String key = paramMap.get("key");
                            String type = paramMap.get("type");
                            if (StringUtils.isEmpty(user) || "null".equals(user)) {
                                body.append("result=").append(FAIL).append(";resDesc=user is null");
                                sendResult(body, serverMsgType, out);
                                break;
                            }
                            if (StringUtils.isEmpty(key) || "null".equals(key)) {
                                body.append("result=").append(FAIL).append(";resDesc=key is null");
                                sendResult(body, serverMsgType, out);
                                break;
                            }
                            if (StringUtils.isEmpty(type) || "null".equals(type)) {
                                body.append("result=").append(FAIL).append(";resDesc=msgType is null");
                                sendResult(body, serverMsgType, out);
                            }
                            String checkUserLogin = checkUserLogin(user, key);
                            if (!"success".equals(checkUserLogin)) {
//                            checkUserLoginTimes(socketKey);
                                body.append("result=").append(FAIL).append(";resDesc=").append(checkUserLogin);
                                sendResult(body, serverMsgType, out);
                                break;
                            }
                            body.append("result=").append(SUCCESS);
                            sendResult(body, serverMsgType, out);
                            if ("msg".equals(type)) {
                                //在登录成功后立刻以消息方式进行告警上报
                                realTime = true;
                                sendRealTimeAlarm2(out);
                            } else if ("ftp".equals(type)) {
                                //ftp为文件方式告警同步，不进行任何操作
                            }
                            break;
                        case reqSyncAlarmMsg:
                            //实时消息不连续时，只能发起一次消息方式告警同步请求
                            body.append("ackSyncAlarmMsg;");
                            serverMsgType = AlarmMsgTypeEnum.ackSyncAlarmMsg.getMsgType();
                            //操作序号，用于区分同一连接的多次请求
                            String reqId = paramMap.get("reqId");
                            //同步告警的起始告警消息序号。丢失多条告警时，为最小告警消息序列号。同一个起始告警消息序号的消息方式同步请求NMS只能发送一次
                            String alarmSeq = paramMap.get("alarmSeq");
                            if (StringUtils.isEmpty(reqId) || "null".equals(reqId)) {
                                body.append("result=").append(FAIL).append(";resDesc=操作序号不能为空");
                                sendResult(body, serverMsgType, out);
                                break;
                            }
                            if (!checkAlarmReq(alarmSeq)) {
                                body.append("result=").append(FAIL).append(";resDesc=告警消息序号已发起过消息方式告警同步请求");
                                sendResult(body, serverMsgType, out);
                                break;
                            }
                            //暂停实时告警上报
                            realTime = false;
                            sendRealTimeAlarm2(out);
                            log.info("stop send realTime alarm,max alarmSeq={}", stopAlarmReq[0]);
                            syncAlarmMsg(alarmSeq, out);
                            //补发告警之后恢复实时告警
                            realTime = true;
                            sendRealTimeAlarm2(out);
                            break;
                        case reqSyncAlarmFile:
                            body.append("ackSyncAlarmFile;");
                            serverMsgType = AlarmMsgTypeEnum.ackSyncAlarmFile.getMsgType();
                            //操作序号，用于区分同一连接的多次请求
                            String reqId2 = paramMap.get("reqId");
                            String startTime = paramMap.get("startTime");
                            String endTime = paramMap.get("endTime");
                            String alarmSeq2 = paramMap.get("alarmSeq");
                            String syncSource = paramMap.get("syncSource");
                            //告警的开始时间和结束时间为空，则时间条件无效，按起始告警消息序号同步
                            if (("null".equals(startTime) || StringUtils.isEmpty(startTime)) && ("null".equals(endTime) && StringUtils.isEmpty(endTime))) {
                                if ((!"null".equals(syncSource) && StringUtils.isNotEmpty(syncSource)) && Integer.parseInt(syncSource) == 1) {
                                    body.append("reqId=").append(reqId2).append(";result=").append(SUCCESS).append(";resDesc=null");
                                    sendResult(body, serverMsgType, out);
                                    String path = buildAlarmFileByAlarmSeq(alarmSeq2, out);
                                } else {
                                    log.error("Unexpected param");
                                    body.append("reqId=").append(reqId2).append(";result=").append(FAIL).append(";resDesc=").append("Unexpected  " +
                                            "param");
                                    sendResult(body, serverMsgType, out);
                                    break;
                                }
                            } else {
                                //按告警时间同步
                                if ((!"null".equals(syncSource) && StringUtils.isNotEmpty(syncSource)) && (Integer.parseInt(syncSource) == 1) || Integer.parseInt(syncSource) == 0) {
                                    body.append("reqId=").append(reqId2).append(";result=").append(SUCCESS).append(";resDesc=null");
                                    sendResult(body, serverMsgType, out);
                                    String path = buildAlarmFileByTime(startTime, endTime, Integer.parseInt(syncSource), out);
                                } else {
                                    log.error("Unexpected param");
                                    body.append("reqId=").append(reqId2).append(";result=").append(FAIL).append(";resDesc=Unexpected param");
                                    sendResult(body, serverMsgType, out);
                                    break;
                                }
                            }
                            syncAlarmFile();
                            break;
                        case reqHeartBeat:
                            body.append("ackHeartBeat;");
                            serverMsgType = AlarmMsgTypeEnum.ackHeartBeat.getMsgType();
                            String reqId3 = paramMap.get("reqId");
                            body.append("ackHeartBeat;reqId=").append(reqId3);
                            sendResult(body, serverMsgType, out);
                            break;
                        case closeConnAlarm:
                            realTime = false;
                            sendRealTimeAlarm2(out);
                            break;
                    }
                    return realTime;
                }
            }
            return false;
        }

        private void sendRealTimeAlarm2(SocketChannel out) {
            List<Object> alarmList = queryRealTimeAlarm();
            //todo 需要记录最后的告警序号
            long alarmReq = System.currentTimeMillis();
            stopAlarmReq[0] = String.valueOf(alarmReq);
            byte[] bytes1 = SocketMsgUtil.buildMsg(alarmReq + "send alarm msg", AlarmMsgTypeEnum.realTimeAlarm.getMsgType());
            try {
                out.write(ByteBuffer.wrap(bytes1));
                state = READING;
                sk.interestOps(SelectionKey.OP_READ);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void sendResult(StringBuffer body, byte serverMsgType, SocketChannel out) throws IOException {
            // 向客户端回复信息
            String s = body.toString();
            if (StringUtils.isNotEmpty(s)) {
                byte[] result = SocketMsgUtil.buildMsg(body.toString(), serverMsgType);
                out.write(ByteBuffer.wrap(result));
            }
        }

        /**
         * 用户登录认证超时检查
         */
        private void checkUserLoginTimes(String key) {
            //查询缓存中的数据，判断失败登录是否超过3次
        }

        /**
         * 校验用户是否登录
         */
        private boolean userLogin() {
            return true;
        }

        /**
         * 验证用户
         *
         * @param userName
         * @param pwd
         */
        private String checkUserLogin(String userName, String pwd) {
            String result = "success";

            return result;
        }

        /**
         * 验证该告警序列号是否进行过告警消息方式同步
         *
         * @param alarmSeq
         */
        private boolean checkAlarmReq(String alarmSeq) {
            return true;
        }

        private List<Object> queryRealTimeAlarm() {
            List<Object> alarmList = new ArrayList<>();
            long currentTimeMillis = System.currentTimeMillis();
            return alarmList;
        }

        /**
         * 消息方式同步告警
         */
        private void syncAlarmMsg(String alarmSeq, SocketChannel out) {
            log.info("start sync alarm...");
        }

        /**
         * 文件方式同步告警
         */
        private void syncAlarmFile() {

        }

        private String buildAlarmFileByTime(String startTime, String endTime, int syncSource, SocketChannel out) {
//        body.append("ackSyncAlarmFileResult;reqId=").append(reqId2).append(";result=").append(SUCCESS).append(";resDesc=null;fileName=").append
//        (path);
            return null;
        }

        private String buildAlarmFileByAlarmSeq(String alarmSeq, SocketChannel out) {
//        body.append("ackSyncAlarmFileResult;reqId=").append(reqId2).append(";result=").append(SUCCESS).append(";resDesc=null;fileName=").append
//        ("path");
            return null;
        }
    }

}