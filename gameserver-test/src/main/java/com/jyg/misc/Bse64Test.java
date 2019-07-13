package com.jyg.misc;

import java.util.Base64;

/**
 * Created by jiayaoguang on 2019/7/13.
 */
public class Bse64Test {

    public static void main(String[] args){

        String src = "123";
        String targetStr = decodeBase64(src);

        System.out.println(targetStr);

    }

    private static String encodeBase64(String src){
        String targetStr = Base64.getEncoder().encodeToString(src.getBytes());
        return targetStr;
    }

    private static String decodeBase64(String targetStr){
        byte[] srcBytes = Base64.getDecoder().decode(targetStr);
        return new String(srcBytes);
    }

}
