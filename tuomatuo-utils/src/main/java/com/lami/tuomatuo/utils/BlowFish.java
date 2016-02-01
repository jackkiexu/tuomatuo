package com.lami.tuomatuo.utils;

/**
 * Created by xujiankang on 2016/1/29.
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
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception  {
        SecretKey secret_key = new SecretKeySpec(KEY.getBytes(), ALGORITM);

        String src = "hp5U223DaeowVXJHI+4D0PMjmNLK4AkA8IFQNoHr";
        byte[] target = encrypt(secret_key, src);

        System.out.println("密文： " + Base64.encode(target));
        System.out.println("明文： " + new String(decrypt(secret_key, target)));
    }

}