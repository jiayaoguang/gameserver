package org.jyg.gameserver.core.event;

public interface Event<A,B> {

    void onEvent(A param1,B param2);

}
