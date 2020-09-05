package org.jyg.gameserver.core.handle;

import org.jyg.gameserver.core.util.Context;

/**
 * create by jiayaoguang on 2020/9/5
 */
public class NettyHandlerFactory {

    private  final ProtoMergeHandler protoMergeHandler;

    private final MyProtobufEncoder myProtobufEncoder;


    public NettyHandlerFactory(Context context) {
        this.protoMergeHandler = new ProtoMergeHandler(context);
        this.myProtobufEncoder = new MyProtobufEncoder(context);
    }


    public ProtoMergeHandler getProtoMergeHandler() {
        return protoMergeHandler;
    }

    public MyProtobufEncoder getMyProtobufEncoder() {
        return myProtobufEncoder;
    }
}
