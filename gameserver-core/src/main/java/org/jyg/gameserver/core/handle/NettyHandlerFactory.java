package org.jyg.gameserver.core.handle;

import org.jyg.gameserver.core.util.Context;
import org.jyg.gameserver.core.util.UnknownMsgHandler;

/**
 * create by jiayaoguang on 2020/9/5
 */
public class NettyHandlerFactory {

    private final MyProtobufEncoder myProtobufEncoder;

    private final MyByteMsgObjEncoder myByteMsgObjEncoder;

    private final MsgEncoder msgEncoder;


    private final Context context;

    public NettyHandlerFactory(Context context) {
        this.myProtobufEncoder = new MyProtobufEncoder(context);
        this.myByteMsgObjEncoder = new MyByteMsgObjEncoder(context);
        this.msgEncoder = new MsgEncoder(context);
        this.context = context;
    }


    public ProtoMergeHandler createProtoMergeHandler(Context context) {
        return new ProtoMergeHandler(context);
    }
    public MsgMergeHandler createMsgMergeHandler(Context context) {
        return new MsgMergeHandler(context);
    }


    public MyProtobufEncoder getMyProtobufEncoder() {
        return myProtobufEncoder;
    }

    public MyByteMsgObjEncoder getMyByteMsgObjEncoder() {
        return myByteMsgObjEncoder;
    }


    public MsgEncoder getMsgEncoder() {
        return msgEncoder;
    }

    public MsgDecoder createMsgDecoder() {
        return new MsgDecoder(context);
    }


}
