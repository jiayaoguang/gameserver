package org.jyg.gameserver.core.consumer;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import org.jyg.gameserver.core.bean.LogicEvent;

/**
 * create by jiayaoguang on 2020/6/27
 */
public class RingBuffConsumerHandler extends ConsumerHandler  implements EventHandler<LogicEvent>, WorkHandler<LogicEvent> {


    @Override
    public final void onEvent(LogicEvent event, long sequence, boolean endOfBatch) {

        this.onReciveEvent(event);

    }


    @Override
    public final void onEvent(LogicEvent event) throws Exception {

        // System.out.println(event.getChannel());
        this.onReciveEvent(event);

    }
}
