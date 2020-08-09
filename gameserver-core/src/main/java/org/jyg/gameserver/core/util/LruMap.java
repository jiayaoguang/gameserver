package org.jyg.gameserver.core.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * created by jiayaoguang at 2018年5月17日
 */
public class LruMap<K,V> extends LinkedHashMap<K,V> {
    private final int maxElementNum;

    public LruMap(int maxSize) {
        super(maxSize, 0.75f, true);
        this.maxElementNum = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return this.size() > this.maxElementNum;
    }

}

