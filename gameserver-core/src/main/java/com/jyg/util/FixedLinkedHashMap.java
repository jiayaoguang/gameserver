package com.jyg.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * created by jiayaoguang at 2018年5月17日
 */
public class FixedLinkedHashMap extends LinkedHashMap {
	protected int maxElements;

	public FixedLinkedHashMap(int maxSize) {
		super(maxSize,0.75f,true);
		this.maxElements = maxSize;
	}
	protected boolean removeEldestEntry(Map.Entry eldest) {
		return this.size() > this.maxElements;
	}
	
}

