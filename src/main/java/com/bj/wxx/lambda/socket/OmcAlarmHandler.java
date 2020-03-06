package com.bj.wxx.lambda.socket;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Slf4j
public class OmcAlarmHandler {
    private Socket socket;
    private static String SUCCESS = "succ";
    private static String FAIL = "fail";
    private Map<String, Object> countMap = new HashMap<>();
    private boolean realTime = false;
    private String[] stopAlarmReq = new String[1];

    //    public void run() {
//        try {
//            // 读取客户端数据
//            DataInputStream input = new DataInputStream(socket.getInputStream());
//            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
//            String hostAddress = socket.getInetAddress().getHostAddress();
//            int port = socket.getLocalPort();
//            String socketKey = hostAddress + ":" + port;
//            log.info("create connect by [{}]", socketKey);
//            //存储socket信息的缓存
//            Object socketObj = countMap.get(socketKey);
//
//            byte[] bytes = new byte[2048];
//            input.read(bytes);
//            SocketAlarmMsgDTO dto = SocketMsgUtil.parserMsg(bytes);
//            if (dto == null) {
//                log.error("socket client message error");
//                return;
//            }
//            log.info("accept client msg [{}]", dto.toString());
//            // 处理客户端数据
//            String clientInputStr = dto.getBody();
//            Byte clientInputMsgType = dto.getMsgType();
//            StringBuffer body = new StringBuffer();
//            byte serverMsgType = 0;
//            Map<String, String> paramMap = new HashMap<>();
//            if (StringUtils.isNotEmpty(clientInputStr)) {
//                String[] splitRes = clientInputStr.split(";");
//                String msgType = splitRes[0];
//                for (int i = 1; i < splitRes.length; i++) {
//                    String param = splitRes[i];
//                    String[] splitParam = param.split("=");
//                    String paramName = splitParam[0];
//                    String paramValue = splitParam[1];
//                    paramMap.put(paramName, paramValue);
//                }
//                AlarmMsgTypeEnum alarmMsgTypeEnum = EnumUtils.getEnumByType(clientInputMsgType);
//                if (alarmMsgTypeEnum == null) {
//                    body.append("msg type [").append(clientInputMsgType).append("] can not find");
//
//                } else {
//                    switch (alarmMsgTypeEnum) {
//                        case reqLoginAlarm:
//                            body.append("ackLoginAlarm;");
//                            serverMsgType = AlarmMsgTypeEnum.ackLoginAlarm.getMsgType();
//                            String user = paramMap.get("user");
//                            String key = paramMap.get("key");
//                            String type = paramMap.get("type");
//                            if (StringUtils.isEmpty(user) || "null".equals(user)) {
//                                body.append("result=").append(FAIL).append(";resDesc=user is null");
//                                sendResult(body, serverMsgType, out);
//                                break;
//                            }
//                            if (StringUtils.isEmpty(key) || "null".equals(key)) {
//                                body.append("result=").append(FAIL).append(";resDesc=key is null");
//                                sendResult(body, serverMsgType, out);
//                                break;
//                            }
//                            if (StringUtils.isEmpty(type) || "null".equals(type)) {
//                                body.append("result=").append(FAIL).append(";resDesc=msgType is null");
//                                sendResult(body, serverMsgType, out);
//                            }
//                            String checkUserLogin = checkUserLogin(user, key);
//                            if (!"success".equals(checkUserLogin)) {
//                                checkUserLoginTimes(socketKey);
//                                body.append("result=").append(FAIL).append(";resDesc=").append(checkUserLogin);
//                                sendResult(body, serverMsgType, out);
//                                break;
//                            }
//                            body.append("result=").append(SUCCESS);
//                            sendResult(body, serverMsgType, out);
//                            if ("msg".equals(type)) {
//                                //在登录成功后立刻以消息方式进行告警上报
//                                realTime = true;
//                                sendRealTimeAlarm(out);
//                            } else if ("ftp".equals(type)) {
//                                //ftp为文件方式告警同步，不进行任何操作
//                            }
//                            break;
//                        case reqSyncAlarmMsg:
//                            //实时消息不连续时，只能发起一次消息方式告警同步请求
//                            body.append("ackSyncAlarmMsg;");
//                            serverMsgType = AlarmMsgTypeEnum.ackSyncAlarmMsg.getMsgType();
//                            //操作序号，用于区分同一连接的多次请求
//                            String reqId = paramMap.get("reqId");
//                            //同步告警的起始告警消息序号。丢失多条告警时，为最小告警消息序列号。同一个起始告警消息序号的消息方式同步请求NMS只能发送一次
//                            String alarmSeq = paramMap.get("alarmSeq");
//                            if (StringUtils.isEmpty(reqId) || "null".equals(reqId)) {
//                                body.append("result=").append(FAIL).append(";resDesc=操作序号不能为空");
//                                sendResult(body, serverMsgType, out);
//                                break;
//                            }
//                            if (!checkAlarmReq(alarmSeq)) {
//                                body.append("result=").append(FAIL).append(";resDesc=告警消息序号已发起过消息方式告警同步请求");
//                                sendResult(body, serverMsgType, out);
//                                break;
//                            }
//                            //暂停实时告警上报
//                            realTime = false;
//                            sendRealTimeAlarm(out);
//                            log.info("stop send realTime alarm,max alarmSeq={}", stopAlarmReq[0]);
//                            syncAlarmMsg(alarmSeq, out);
//                            //补发告警之后恢复实时告警
//                            realTime = true;
//                            sendRealTimeAlarm(out);
//                            break;
//                        case reqSyncAlarmFile:
//                            body.append("ackSyncAlarmFile;");
//                            serverMsgType = AlarmMsgTypeEnum.ackSyncAlarmFile.getMsgType();
//                            //操作序号，用于区分同一连接的多次请求
//                            String reqId2 = paramMap.get("reqId");
//                            String startTime = paramMap.get("startTime");
//                            String endTime = paramMap.get("endTime");
//                            String alarmSeq2 = paramMap.get("alarmSeq");
//                            String syncSource = paramMap.get("syncSource");
//                            //告警的开始时间和结束时间为空，则时间条件无效，按起始告警消息序号同步
//                            if (("null".equals(startTime) || StringUtils.isEmpty(startTime)) && ("null".equals(endTime) && StringUtils.isEmpty
//                            (endTime))) {
//                                if ((!"null".equals(syncSource) && StringUtils.isNotEmpty(syncSource)) && Integer.parseInt(syncSource) == 1) {
//                                    body.append("reqId=").append(reqId2).append(";result=").append(SUCCESS).append(";resDesc=null");
//                                    sendResult(body, serverMsgType, out);
//                                    String path = buildAlarmFileByAlarmSeq(alarmSeq2, out);
//                                } else {
//                                    log.error("Unexpected param");
//                                    body.append("reqId=").append(reqId2).append(";result=").append(FAIL).append(";resDesc=").append("Unexpected  " +
//                                            "param");
//                                    sendResult(body, serverMsgType, out);
//                                    break;
//                                }
//                            } else {
//                                //按告警时间同步
//                                if ((!"null".equals(syncSource) && StringUtils.isNotEmpty(syncSource)) && (Integer.parseInt(syncSource) == 1) ||
//                                Integer.parseInt(syncSource) == 0) {
//                                    body.append("reqId=").append(reqId2).append(";result=").append(SUCCESS).append(";resDesc=null");
//                                    sendResult(body, serverMsgType, out);
//                                    String path = buildAlarmFileByTime(startTime, endTime, Integer.parseInt(syncSource), out);
//                                } else {
//                                    log.error("Unexpected param");
//                                    body.append("reqId=").append(reqId2).append(";result=").append(FAIL).append(";resDesc=Unexpected param");
//                                    sendResult(body, serverMsgType, out);
//                                    break;
//                                }
//                            }
//                            syncAlarmFile();
//                            socket.close();
//                            break;
//                        case reqHeartBeat:
//                            body.append("ackHeartBeat;");
//                            serverMsgType = AlarmMsgTypeEnum.ackHeartBeat.getMsgType();
//                            String reqId3 = paramMap.get("reqId");
//                            body.append("ackHeartBeat;reqId=").append(reqId3);
//                            sendResult(body, serverMsgType, out);
//                            break;
//                        case closeConnAlarm:
//                            realTime = false;
//                            sendRealTimeAlarm(out);
//                            release();
//                            break;
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            release();
//            log.error("服务器 run 异常: " + e.getMessage());
//        }
//    }
    /*private void sendRealTimeAlarm(DataOutputStream out) throws Exception {
        while (realTime) {
            new Thread(() -> {
                List<Object> alarmList = queryRealTimeAlarm();
                //todo 需要记录最后的告警序号
                long alarmReq = System.currentTimeMillis();
                stopAlarmReq[0] = String.valueOf(alarmReq);
                byte[] bytes1 = SocketMsgUtil.buildMsg(alarmReq + "send alarm msg", AlarmMsgTypeEnum.realTimeAlarm.getMsgType());
                try {
                    out.write(bytes1);
                    out.flush();
                    Thread.sleep(2000);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            });

        }
    }*/
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

    private void release() {
        log.error("release");
        if (socket != null) {
            try {
                socket.close();
            } catch (Exception e) {
                socket = null;
                log.error("服务端 finally 异常:" + e.getMessage());
            }
        }
    }


    public boolean read(byte[] bytes, SocketChannel out) throws Exception {
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
                        socket.close();
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
                        release();
                        break;
                }
                return realTime;
            }
        }
        return false;
    }

    private void sendRealTimeAlarm2(SocketChannel out) throws Exception {
        for (; ; ) {
            while (realTime) {
                List<Object> alarmList = queryRealTimeAlarm();
                //todo 需要记录最后的告警序号
                long alarmReq = System.currentTimeMillis();
                stopAlarmReq[0] = String.valueOf(alarmReq);
                byte[] bytes1 = SocketMsgUtil.buildMsg(alarmReq + "send alarm msg", AlarmMsgTypeEnum.realTimeAlarm.getMsgType());
                try {
                    out.write(ByteBuffer.wrap(bytes1));
                    Thread.sleep(2000);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void reqLoginAlarm(StringBuffer body, byte serverMsgType, Map<String, String> paramMap, String socketKey, SocketChannel out) throws Exception {
        body.append("ackLoginAlarm;");
        serverMsgType = AlarmMsgTypeEnum.ackLoginAlarm.getMsgType();
        String user = paramMap.get("user");
        String key = paramMap.get("key");
        String type = paramMap.get("type");
        if (StringUtils.isEmpty(user) || "null".equals(user)) {
            body.append("result=").append(FAIL).append(";resDesc=user is null");
            sendResult(body, serverMsgType, out);
            return;
        }
        if (StringUtils.isEmpty(key) || "null".equals(key)) {
            body.append("result=").append(FAIL).append(";resDesc=key is null");
            sendResult(body, serverMsgType, out);
            return;
        }
        if (StringUtils.isEmpty(type) || "null".equals(type)) {
            body.append("result=").append(FAIL).append(";resDesc=msgType is null");
            sendResult(body, serverMsgType, out);
            return;
        }
        String checkUserLogin = checkUserLogin(user, key);
        if (!"success".equals(checkUserLogin)) {
            checkUserLoginTimes(socketKey);
            body.append("result=").append(FAIL).append(";resDesc=").append(checkUserLogin);
            sendResult(body, serverMsgType, out);
            return;
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
    }
}
