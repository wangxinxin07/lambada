package com.bj.wxx.lambda.socket;



public class EnumUtils {

    public static AlarmMsgTypeEnum getEnumByType(Byte type) {
        for (AlarmMsgTypeEnum t : AlarmMsgTypeEnum.class.getEnumConstants()) {
            if (t.getMsgType() == type) {
                return t;
            }
        }
        return null;
    }

}