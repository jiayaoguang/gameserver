package com.jyg.util;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import reactor.util.annotation.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * create on 2019/8/23 by jiayaoguang
 * 前缀名线程工厂
 */
public class PrefixNameThreadFactory implements ThreadFactory {
	private final AtomicInteger threadIndex = new AtomicInteger(0);
	//线程名前缀
	private String threadNamePrefix;

	public PrefixNameThreadFactory(String threadNamePrefix) {
		this.threadNamePrefix = threadNamePrefix;
	}

	@Override
	public Thread newThread(@NonNull Runnable runnable) {
		return new Thread(runnable, threadNamePrefix + threadIndex.incrementAndGet());
	}
}