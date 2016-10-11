package com.lami.tuomatuo.utils;

/**
 * Created by xjk on 2016/1/29.
 */
import org.apache.axis.encoding.Base64;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class BlowFish {

    private final static String ALGORITM = "Blowfish";
    private final static String KEY = "huoqiu_manyi_key";
    private final static String transformation = ALGORITM + "/ECB/PKCS5Padding";

    /**
     * @return
     * @throws Exception
     */
    public static Key keyGenerator() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("Blowfish");
        keyGenerator.init(128);
        return keyGenerator.generateKey();
    }

    /**
     * 加密
     *
     * @param key
     * @param text
     * @return
     * @throws Exception
     */
    public static byte[] encrypt(Key key,String text) throws Exception  {
        Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(text.getBytes());
    }

    /**
     * 解密
     *
     * @param key
     * @param text
     * @return
     * @throws Exception
     */
    public static byte[] decrypt(Key key,byte[] bt) throws Exception  {
        Cipher cipher = Cipher.getInstance("Blowfish/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(bt);
    }

    /**
     * 将两个ASCII字符合成一个字节； 如："EF"--> 0xEF
     *
     * @param src0 byte
     * @param src1 byte
     * @return byte
     */
    private static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    /**
     * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" --> byte[]{0x2B, 0x44, 0xEF,
     * 0xD9}
     *
     * @param src String
     * @return byte[]
     */
    private static byte[] hexString2Bytes(String src) {
        byte[] tmp = src.getBytes();
        byte[] ret = new byte[tmp.length / 2];
        for(int i = 0; i < ret.length; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }


    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception  {
        SecretKey secret_key = new SecretKeySpec(KEY.getBytes(), ALGORITM);

        String src = "MT0VT5EN1FAP7SGA840OBW4DUFJUAB";
        byte[] target = encrypt(secret_key, src);

        System.out.println("密文： " + Base64.encode(target));
        System.out.println("明文： " + new String(decrypt(secret_key, target)));
    }

}