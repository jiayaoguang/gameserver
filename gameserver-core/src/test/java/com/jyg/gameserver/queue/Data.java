package com.jyg.gameserver.queue;
/**
 * created by jiayaoguang at 2018年5月26日
 */
class Data {
	private final long squenceId;
	private long timeMills;
	
	private Object content;

	public Data() {
		squenceId = 0L;
		timeMills = System.currentTimeMillis();
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public long getTimeMills() {
		return timeMills;
	}
	
	public void init(Object content) {
		this.content = content;
		this.timeMills = System.currentTimeMillis();
	}
	
	public void clean() {
		this.content = null;
		this.timeMills = -1;
	}

}

