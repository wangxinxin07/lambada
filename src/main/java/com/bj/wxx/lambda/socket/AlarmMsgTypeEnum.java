package com.bj.wxx.lambda.socket;

/**
 * @author wangL
 * 13:58 2020/2/21.
 */
public enum AlarmMsgTypeEnum {

    //    实时告警上报
    realTimeAlarm(0, "realTimeAlarm"),
    //登陆请求
    reqLoginAlarm(1, "reqLoginAlarm"),
    //登陆响应
    ackLoginAlarm(2, "ackLoginAlarm"),
    //    消息方式同步告警请求
    reqSyncAlarmMsg(3, "reqSyncAlarmMsg"),
    //    消息方式同步告警响应
    ackSyncAlarmMsg(4, "ackSyncAlarmMsg"),
    //    文件方式同步告警请求
    reqSyncAlarmFile(5, "reqSyncAlarmFile"),
    //    文件方式同步告警响应
    ackSyncAlarmFile(6, "ackSyncAlarmFile"),
    //    文件方式同步告警响应
    ackSyncAlarmFileResult(7, "ackSyncAlarmFileResult"),
    //    心跳请求
    reqHeartBeat(8, "reqHeartBeat"),
    //    心跳响应
    ackHeartBeat(9, "ackHeartBeat"),
    //    关闭连接通知
    closeConnAlarm(10, "closeConnAlarm");

    private byte msgType;
    private String typeDesc;

    AlarmMsgTypeEnum(int msgType, String typeDesc) {
        this.msgType = (byte) msgType;
        this.typeDesc = typeDesc;
    }

    public byte getMsgType() {
        return msgType;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

}
