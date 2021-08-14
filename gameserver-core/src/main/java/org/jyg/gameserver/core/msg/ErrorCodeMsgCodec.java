package org.jyg.gameserver.core.msg;

import cn.hutool.core.collection.CollectionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * create by jiayaoguang on 2020/10/29
 */
public class ErrorCodeMsgCodec extends AbstractByteMsgCodec<ErrorCodeByteMsg> {


    public ErrorCodeMsgCodec() {
        super(ErrorCodeByteMsg.class);

    }

    @Override
    public byte[] encode(ErrorCodeByteMsg jsonMsg) {

        int len = 4;


        if(CollectionUtil.isNotEmpty(jsonMsg.getParams())){
            for(String param : jsonMsg.getParams()){
                len += param.length();
                len +=4;
            }
        }


        byte[] bytes = new byte[len];

        bytes[0] = (byte) (jsonMsg.getErrorCode()>>24);
        bytes[1] = (byte) ((jsonMsg.getErrorCode()>>16) & 0x0000ff);
        bytes[2] = (byte) ((jsonMsg.getErrorCode()>>8) & 0x0000ff );
        bytes[3] = (byte) (jsonMsg.getErrorCode() & 0x0000ff);


        if(CollectionUtil.isNotEmpty(jsonMsg.getParams())){

            int byteIndex = 4;

            for(String param : jsonMsg.getParams()){

                int paramLen = param.length();

                if(paramLen > 0x00ffff){
                    throw new IllegalArgumentException("error code param too long : " + paramLen);
                }

//                bytes[byteIndex++] = (byte) (paramLen>>24);
//                bytes[byteIndex++] = (byte) ((paramLen>>16) & 0x0000ff);
                bytes[byteIndex++] = (byte) ((paramLen>>8) & 0x0000ff);
                bytes[byteIndex++] = (byte) (paramLen & 0x0000ff);

                for(byte b : param.getBytes()){
                    bytes[byteIndex++] = b;
//                    byteIndex ++;
                }
            }
        }

        return bytes;
    }

    @Override
    public ErrorCodeByteMsg decode(byte[] bytes) {

        int errorCode;
        errorCode = (bytes[0] & 0xff);
        errorCode <<= 8;
        errorCode |= (bytes[1] & 0xff);
        errorCode <<= 8;
        errorCode |= (bytes[2] & 0xff);
        errorCode <<= 8;
        errorCode |= (bytes[3] & 0xff);

        ErrorCodeByteMsg errorCodeByteMsg = new ErrorCodeByteMsg();
        errorCodeByteMsg.setErrorCode(errorCode);
        if(bytes.length == 4){
            return errorCodeByteMsg;
        }

        List<String> params = new ArrayList<>(3);

        for(int readIndex = 4;readIndex < bytes.length;){

            int len;

//            len = (bytes[readIndex++] & 0xff);
//            len <<= 8;
//            len |= (bytes[readIndex ++] & 0xff);
//            len <<= 8;
            len = (bytes[readIndex ++] & 0xff)<<8;
//            len <<= 8;
            len |= (bytes[readIndex ++] & 0xff);

//            readIndex += 4;



//            String s = new String(bytes , readIndex , len);
            params.add(new String(bytes , readIndex , len));


            readIndex += len;
        }
        errorCodeByteMsg.setParams(params);
        return errorCodeByteMsg;
    }



}
