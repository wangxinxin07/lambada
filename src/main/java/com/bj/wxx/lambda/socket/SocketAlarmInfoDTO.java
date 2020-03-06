package com.bj.wxx.lambda.socket;

/**
 * @author wangL
 * 13:24 2020/2/25.
 */
public class SocketAlarmInfoDTO {
    /**
     * 用户进行了几次校验，如果三次就主动断开连接
     */
    private int userCheckNum;

    /**
     * 以消息方式请求同步告警数据次数
     */
    private int reqSyncAlarmMsgNum;
}
