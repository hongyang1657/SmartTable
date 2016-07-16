package com.byids.hy.smarttable.utils;

/**
 * Created by byids on 16/6/7.
 */
public class Encrypt {
    public static byte[] encrypt(String string){

        byte[] enByte = AES.encrpt(string);
        if (enByte == null){
            return null;
        }else {
        byte[] lengthByte = ByteUtils.intToByteBigEndian(enByte.length);
        byte[] headByte = new byte[8];
        for (int i = 0;i<headByte.length;i++) {
            headByte[i] = 0x50;
        }
        byte[] tailByte = new byte[4];
        tailByte[0] = 0x0d;
        tailByte[1] = 0x0a;
        tailByte[2] = 0x0d;
        tailByte[3] = 0x0a;
        byte[] sendByte = ByteUtils.byteJoin(headByte,lengthByte,enByte,tailByte);
        return sendByte;
        }
    }
}
