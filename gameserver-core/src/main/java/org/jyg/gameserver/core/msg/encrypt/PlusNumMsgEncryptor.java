package org.jyg.gameserver.core.msg.encrypt;

/**
 * create by jiayaoguang on 2022/9/17
 */
public class PlusNumMsgEncryptor implements MsgEncryptor{



    private final byte plusNum;
    public PlusNumMsgEncryptor() {
        plusNum = 1;
    }
    public PlusNumMsgEncryptor(byte plusNum) {
        this.plusNum = plusNum;
    }


    @Override
    public byte[] encrypt(byte[] originBytes) {

        for(int i=0;i<originBytes.length;i++){
            originBytes[i]+= plusNum;
        }

        return originBytes;
    }

    @Override
    public byte[] decrypt(byte[] encryptBytes) {
        for(int i=0;i<encryptBytes.length;i++){
            encryptBytes[i]-= plusNum;
        }
        return encryptBytes;
    }
}
