package com.byids.hy.smarttable.utils;

/**
 * Created by 123456 on 2016/4/6.
 */
public class ByteUtils {

    public static byte[] byteJoin(byte[]... bytes) {
        byte[] returnByte = new byte[0];
        for (byte[] byte1 : bytes) {
            returnByte = byteJoin(returnByte,byte1);
        }
        return returnByte;
    }

    public static byte[] byteJoin(byte[] byte1,byte[] byte2) {
        if (byte1 == null || byte2 == null){
            return null;
        }
        byte[] data2 = new byte[byte1.length+byte2.length];
        System.arraycopy(byte1, 0, data2, 0, byte1.length);
        System.arraycopy(byte2, 0, data2, byte1.length, byte2.length);
        return data2;
    }



    public static byte[] intToByteBigEndian(final int integer){
        byte[] lengthByte = new byte[4];

        for (int n = 0; n < 4; n++) {
            lengthByte[3 - n] = (byte) (integer >>> (n * 8));
        }

//        AES.byteStringLog(lengthByte);
//
//        ByteBuffer bb = ByteBuffer.wrap(lengthByte);
//        bb.order(ByteOrder.BIG_ENDIAN);
//
//        byte[] b1 = new byte[bb.remaining()];
//        bb.get(b1);
//        AES.byteStringLog(b1);
////        Log.i("big", bb.order(ByteOrder.BIG_ENDIAN).toString());
//        Log.i("big", bb.order(ByteOrder.BIG_ENDIAN).order().toString());
        return lengthByte;
    }
}
