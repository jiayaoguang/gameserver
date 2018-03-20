package com.jyg.net;

import com.google.protobuf.MessageLite;
import com.jyg.bean.LogicEvent;

import io.netty.buffer.ByteBuf;

/**
 * created by jiayaoguang at 2017年12月16日
 */
public interface Processor<T> {
	
	void process(LogicEvent<T> event)throws Exception;
	
}

