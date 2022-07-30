package org.jyg.gameserver.util;

import org.jyg.gameserver.core.compress.BytesCodec;

import java.io.IOException;

public class SnappyBytesCodec implements BytesCodec {

    @Override
    public byte[] decode(byte[] originBytes) {
        try {
            return org.xerial.snappy.Snappy.compress(originBytes);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Override
    public byte[] encode(byte[] originBytes) {
        try {
            return org.xerial.snappy.Snappy.uncompress(originBytes);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
