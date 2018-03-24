package com.jyg.gameserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;

import org.junit.Test;

/**
 * created by jiayaoguang at 2017年12月13日
 */
public class ByteBufTest {

	@Test
	public void test(){
		
		
	}
	
	public static void main(String[] args) {
		ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(10); //
		System.out.println(buf.refCnt());
		System.out.println(buf.refCnt());
//		buf.retain();
//		buf.release();
//		buf.release();
//		buf.retain();
		buf.writeChar('A');
		System.out.println(buf);
		ByteBuf buf2 = buf.duplicate();
		int c = buf2.capacity();
		System.out.println(buf2.duplicate()+":"+c);
		
	}
	
	
}

