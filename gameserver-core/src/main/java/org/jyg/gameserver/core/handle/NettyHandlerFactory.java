package org.jyg.gameserver.core.handle;

import org.jyg.gameserver.core.util.GameContext;

/**
 * create by jiayaoguang on 2020/9/5
 */
public class NettyHandlerFactory {

    private final MyProtobufEncoder myProtobufEncoder;

    private final MyByteMsgObjEncoder myByteMsgObjEncoder;

    private final MsgEncoder msgEncoder;


    private final GameContext gameContext;

    public NettyHandlerFactory(GameContext gameContext) {
        this.myProtobufEncoder = new MyProtobufEncoder(gameContext);
        this.myByteMsgObjEncoder = new MyByteMsgObjEncoder(gameContext);
        this.msgEncoder = new MsgEncoder(gameContext);
        this.gameContext = gameContext;
    }


    public MsgMergeHandler createMsgMergeHandler(GameContext gameContext) {
        return new MsgMergeHandler(gameContext);
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
        return new MsgDecoder(gameContext);
    }


}
