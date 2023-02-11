package org.jyg.gameserver.util;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * create by jiayaoguang on 2023/2/11
 */
public class JDKAes {



    /**
     * 加解密统一编码方式
     */
    private final static String ENCODING = "utf-8";

    /**
     * 加解密方式
     */
    private final static String ALGORITHM  = "AES";

    /**
     *加密模式及填充方式
     */
    private final static String PATTERN = "AES/ECB/pkcs5padding";
//    private final static String PATTERN = "AES/CBC/pkcs5padding";
    private final Cipher encryptCipher;

    private final Cipher decryptCipher;

    public JDKAes(byte[] key) {
        // AES加密采用pkcs5padding填充
        // 判断Key是否为16位
//        if (key.length != 32 && key.length != 16) {
//            throw new UnsupportedOperationException("key.length != 32 && key.length != 16");
//        }
        try {
            SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);

            encryptCipher = Cipher.getInstance(PATTERN);
            //用密匙初始化Cipher对象
            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey);


            IvParameterSpec ivParameterSpec = new IvParameterSpec("2222222222222222".getBytes(StandardCharsets.UTF_8));

            decryptCipher = Cipher.getInstance(PATTERN);
            //用密匙初始化Cipher对象
            decryptCipher.init(Cipher.DECRYPT_MODE, secretKey);


        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException  e) {
            throw new UnsupportedOperationException(e);
        }

    }

    /**
     * AES加密
     * @throws Exception
     */
    public byte[] encrypt(byte[] msgBytes) throws Exception {
//        if (key == null) {
//            System.out.print("Key为空null");
//            return null;
//        }



        //执行加密操作
//        byte[] encryptData = cipher.doFinal(plainText.getBytes(ENCODING));
        return encryptCipher.doFinal(msgBytes);
    }


    /**
     * AES解密
     * @param msgBytes msgBytes
     */
    public byte[] decrypt(byte[] msgBytes) throws Exception {
        return decryptCipher.doFinal(msgBytes);
    }




}
