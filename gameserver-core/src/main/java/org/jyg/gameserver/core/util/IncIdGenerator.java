package org.jyg.gameserver.core.util;

public class IncIdGenerator implements IdGenerator {

    private long incId;


    public IncIdGenerator() {
        this.incId = 0;
    }

    public IncIdGenerator(long incId) {
        this.incId = incId;
    }

    @Override
    public long nextId() {
        ++incId;
        return incId;
    }
}
