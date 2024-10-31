package org.jyg.gameserver.util;

import org.jyg.gameserver.core.msg.encrypt.MsgEncryptor;

import java.io.IOException;

public class SnappyMsgEncryptor implements MsgEncryptor {

    @Override
    public byte[] encrypt(byte[] originBytes) {
        try {
            return org.xerial.snappy.Snappy.compress(originBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] decrypt(byte[] encryptBytes) {
        try {
            return org.xerial.snappy.Snappy.uncompress(encryptBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
