package com.byids.hy.smarttable.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by 123456 on 2016/3/31.
 */
public class AES {
    private static final String AES = "AES/CBC/PKCS5Padding";

    private static final String CRYPT_KEY = "fadekiebos1573udoap30elz79vidwyc";

    /**
     * 加密
     *
     * @param
     * @return
     */
    public static byte[] encrypt(byte[] src, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        SecretKeySpec securekey = new SecretKeySpec(CRYPT_KEY.getBytes(), AES);
        cipher.init(Cipher.ENCRYPT_MODE, securekey,iv);
        return cipher.doFinal(src);
    }
    /**
     * 解密
     *
     * @param
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(byte[] src, IvParameterSpec iv)  throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        SecretKeySpec securekey = new SecretKeySpec(CRYPT_KEY.getBytes(), AES);//设置加密Key
        cipher.init(Cipher.DECRYPT_MODE, securekey,iv);
        return cipher.doFinal(src);
    }

    /**
     * 二行制转十六进制字符串
     *
     * @param b
     * @return
     */
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
        }
        return hs.toUpperCase();
    }

    public static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0)
            throw new IllegalArgumentException("长度不是偶数");
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    public static void byteStringLog(byte[] bs){
        String log = new String();
        for (int i = 0;i<bs.length;i++){
            int bi = (int)bs[i];
            log=log+" "+ String.valueOf(bi);
        }
        System.out.println(log);
    }

    public static byte[] generateIV(){
        byte[] ivs = new byte[16];
        for (int i = 0;i<ivs.length;i++){
            int randInt = (int)(Math.random()*255);
            ivs[i] = (byte)randInt;
        }
        byteStringLog(ivs);
        return  ivs;
    }

    public static byte[] encrpt(String json) {
        if (json == null || "".equals(json)){
            return null;
        }
        byte[] jsonByte = json.getBytes();
        byte[] ivs = generateIV();
        IvParameterSpec piv = new IvParameterSpec(ivs);

        byte[] idEncryptByte = null;
        try {
            idEncryptByte = encrypt(jsonByte, piv);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ByteUtils.byteJoin(idEncryptByte,ivs);
    }

}
