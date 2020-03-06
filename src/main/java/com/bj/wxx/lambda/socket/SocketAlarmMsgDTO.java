package com.bj.wxx.lambda.socket;

/**
 * @author wangL
 * 13:34 2020/2/24.
 */
public class SocketAlarmMsgDTO {
    private short startSign;
    private byte msgType;
    private int timeStamp;
    private short lenOfBody;
    private String body;

    public SocketAlarmMsgDTO(short startSign, byte msgType, int timeStamp, short lenOfBody, String body) {
        this.startSign = startSign;
        this.msgType = msgType;
        this.timeStamp = timeStamp;
        this.lenOfBody = lenOfBody;
        this.body = body;
    }

    public short getStartSign() {
        return startSign;
    }

    public void setStartSign(short startSign) {
        this.startSign = startSign;
    }

    public byte getMsgType() {
        return msgType;
    }

    public void setMsgType(byte msgType) {
        this.msgType = msgType;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(byte timeStamp) {
        this.timeStamp = timeStamp;
    }

    public short getLenOfBody() {
        return lenOfBody;
    }

    public void setLenOfBody(short lenOfBody) {
        this.lenOfBody = lenOfBody;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "DTO:startSign[" + startSign + "],msgType[" + msgType + "],timeStamp[" + timeStamp + "],lenOfBody[" + lenOfBody + "],body[" + body + "]";
    }
}
