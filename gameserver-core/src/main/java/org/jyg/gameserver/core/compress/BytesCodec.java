package org.jyg.gameserver.core.compress;

public interface BytesCodec {

    public byte[] decode(byte[] originBytes);

    public byte[] encode(byte[] originBytes);

}
