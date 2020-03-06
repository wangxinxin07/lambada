package com.bj.wxx.lambda.socket;

import com.google.common.base.Charsets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SocketMsgUtil {

    /**
     * Big-Endian,将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。
     */
    private static byte[] intToBytes2(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    private static int bytesToInt2(byte[] src, int offset) {
        int value;
        value = (int) (((src[offset] & 0xFF) << 24)
                | ((src[offset + 1] & 0xFF) << 16)
                | ((src[offset + 2] & 0xFF) << 8)
                | (src[offset + 3] & 0xFF));
        return value;
    }

    /**
     * Big-Endian,将short数值转换为占二个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。
     */
    private static byte[] shortToBytes2(short value) {
        byte[] src = new byte[2];
        src[0] = (byte) ((value >> 8) & 0xFF);
        src[1] = (byte) (value & 0xFF);
        return src;
    }

    private static short bytesToShort2(byte[] src, int offset) {
        short value;
        value = (short) (
                ((src[offset] & 0xFF) << 8)
                        | (src[offset + 1] & 0xFF));
        return value;
    }

    /**
     * 单字节处理为字节数组
     */
    private static byte[] byteToBytes2(byte value) {
        byte[] src = new byte[1];
        src[0] = value;
        return src;
    }

    private static byte bytesToByte(byte[] src, int offset) {
        byte value;
        value = (src[offset]);
        return value;
    }

    /**
     * 构造消息
     *
     * @param strBody
     * @param msgType
     */
    public static byte[] buildMsg(String strBody, int msgType) {
        byte[] strBodyBytes = strBody.getBytes(Charsets.UTF_8);
        short bodyLen = (short) strBodyBytes.length;
        byte[] allMessage = new byte[9 + bodyLen];
        short startSign = (short) 0xFFFF;
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        System.arraycopy(shortToBytes2(startSign), 0, allMessage, 0, 2);
        System.arraycopy(byteToBytes2((byte) msgType), 0, allMessage, 2, 1);
        System.arraycopy(intToBytes2(timeStamp), 0, allMessage, 3, 4);
        System.arraycopy(shortToBytes2(bodyLen), 0, allMessage, 7, 2);
        System.arraycopy(strBodyBytes, 0, allMessage, 9, bodyLen);
        return allMessage;
    }

    /**
     * 解析消息
     *
     * @param allMessage
     */
    public static SocketAlarmMsgDTO parserMsg(byte[] allMessage) {
        if (allMessage.length < 9) {
            return null;
        }
        short startSign = bytesToShort2(allMessage, 0);
        byte msgType = bytesToByte(allMessage, 2);
        int timeStamp = bytesToInt2(allMessage, 3);
        short bodyLen = bytesToShort2(allMessage, 7);
        byte[] bodyParser = new byte[bodyLen];
        if (bodyLen > allMessage.length) {
            return null;
        }
        System.arraycopy(allMessage, 9, bodyParser, 0, bodyLen);
        String body = new String(bodyParser, Charsets.UTF_8);
        SocketAlarmMsgDTO dto = new SocketAlarmMsgDTO(startSign, msgType, timeStamp, bodyLen, body);
        return dto;
    }

    public static void main(String[] args) throws IOException {

        //拼接
        String strBody = "reqLogin;user=yiy;key=qw#$@;type=msg";
        byte[] allMessage = new byte[9 + strBody.length()];
        short startSign = (short) 0xFFFF;
        byte msgType = 1;
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        short bodyLen = (short) strBody.length();
        System.arraycopy(shortToBytes2(startSign), 0, allMessage, 0, 2);
        System.arraycopy(byteToBytes2(msgType), 0, allMessage, 2, 1);
        System.arraycopy(intToBytes2(timeStamp), 0, allMessage, 3, 4);
        System.arraycopy(shortToBytes2(bodyLen), 0, allMessage, 7, 2);
        System.arraycopy(strBody.getBytes(), 0, allMessage, 9, strBody.length());

        System.out.println("startSign=" + startSign + ",msgType=" + msgType + ",timeStamp=" + timeStamp + ",bodyLen=" + bodyLen + ",strBody=" + strBody);

        //生成文件确认内容
        File f = new File("d:" + File.separator + "test.txt");
        OutputStream out = null;
        out = new FileOutputStream(f);
        out.write(allMessage);
        out.close();

        //解析
        short startSignParser = bytesToShort2(allMessage, 0);
        byte msgTypeParser = bytesToByte(allMessage, 2);
        int timeStampParser = bytesToInt2(allMessage, 3);
        short bodyLenParser = bytesToShort2(allMessage, 7);
        byte[] bodyParser = new byte[bodyLenParser];
        System.arraycopy(allMessage, 9, bodyParser, 0, bodyLenParser);
        String bodyParserStr = new String(bodyParser);

        System.out.println("startSignParser=" + startSignParser + ",msgTypeParser=" + msgTypeParser + ",timeStampParser=" + timeStampParser + "," +
                "bodyLenParser=" + bodyLenParser + ",bodyParserStr=" + bodyParserStr);

    }
}
