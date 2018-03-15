package com.jyg.net;

import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import com.jyg.bean.LogicEvent;

import io.netty.buffer.ByteBuf;

/**
 * created by jiayaoguang at 2017年12月16日
 */
public interface ProtoProcessor extends Processor {
	
	Parser<MessageLite> getProtoParser();
	
}

