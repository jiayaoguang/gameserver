package com.jyg.processor;

import com.google.protobuf.MessageLite;
import com.jyg.bean.LogicEvent;
import com.jyg.util.FTLLoader;

import io.netty.buffer.ByteBuf;

/**
 * created by jiayaoguang at 2017年12月16日
 */
public interface Processor<T> extends Cloneable {
	void process(LogicEvent<T> event);
	
}

