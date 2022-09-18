package org.jyg.gameserver.core.msg.encrypt;

/**
 * create by jiayaoguang on 2022/9/17
 */
public interface MsgEncryptor {

    byte[] encrypt(byte[] originBytes);

    byte[] decrypt(byte[] encryptBytes);
}
