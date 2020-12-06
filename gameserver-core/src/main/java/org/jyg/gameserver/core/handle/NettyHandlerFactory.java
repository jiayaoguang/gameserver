package org.jyg.gameserver.core.handle;

import org.jyg.gameserver.core.util.Context;

/**
 * create by jiayaoguang on 2020/9/5
 */
public class NettyHandlerFactory {

    private final MyProtobufEncoder myProtobufEncoder;

    private final MyByteMsgObjEncoder myByteMsgObjEncoder;


    public NettyHandlerFactory(Context context) {
        this.myProtobufEncoder = new MyProtobufEncoder(context);
        this.myByteMsgObjEncoder = new MyByteMsgObjEncoder(context);
    }


    public ProtoMergeHandler createProtoMergeHandler(Context context) {
        return new ProtoMergeHandler(context);
    }

    public MyProtobufEncoder getMyProtobufEncoder() {
        return myProtobufEncoder;
    }

    public MyByteMsgObjEncoder getMyByteMsgObjEncoder() {
        return myByteMsgObjEncoder;
    }
}
