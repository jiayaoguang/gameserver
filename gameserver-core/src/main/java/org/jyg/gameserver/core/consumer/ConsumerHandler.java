package org.jyg.gameserver.core.consumer;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import org.jyg.gameserver.core.bean.LogicEvent;
import org.jyg.gameserver.core.manager.ChannelManager;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.timer.Timer;
import org.jyg.gameserver.core.timer.TimerManager;
import org.jyg.gameserver.core.util.CallBackEvent;
import org.jyg.gameserver.core.util.Context;

/**
 * created by jiayaoguang at 2017年12月6日
 */
public class ConsumerHandler implements EventHandler<LogicEvent>, WorkHandler<LogicEvent> {

    public static final int DEFAULT_CONSUMER_ID = 0;

    private Context context;

    private int requestId = 1;

    private int consumerId = 0;

    private final ChannelManager channelManager = new ChannelManager();

    private TimerManager timerManager;

    public ConsumerHandler() {

    }

    public ConsumerHandler(int consumerId) {
        this.consumerId = consumerId;
    }


    @Override
    public final void onEvent(LogicEvent event, long sequence, boolean endOfBatch) {

        try {
            this.onEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public final void onEvent(LogicEvent event) throws Exception {

        // System.out.println(event.getChannel());
        try {
            doEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            loop();
        }

    }

    private void doEvent(LogicEvent event) {
        switch (event.getChannelEventType()) {

//			case CLIENT_SOCKET_CONNECT_ACTIVE:
//				dispatcher.as_on_client_active(event);
//				break;
//			case CLIENT_SOCKET_CONNECT_INACTIVE:
//				dispatcher.as_on_client_inactive(event);
//				break;

            case SOCKET_CONNECT_ACTIVE:
//				dispatcher.as_on_inner_server_active(event);
                if (isDefaultConsumer()) {
                    channelManager.doLink(event);
                }
                break;
            case SOCKET_CONNECT_INACTIVE:
                if (isDefaultConsumer()) {
                    channelManager.doUnlink(event);
                }
                break;

            case HTTP_MSG_COME:
                ((Request) event.getData()).setRequestid(getAndIncRequestId());
                context.getGlobalQueue().processHttpEvent(event);
//				event.getChannel().close();
                // 5秒后关闭
//				dispatcher.getTimerManager().addTimer(new DelayCloseTimer(event.getChannel(), 60 * 1000L));
                break;
            case ON_MESSAGE_COME:
//				dispatcher.webSocketProcess(event);
//				break;
            case RPC_MSG_COME:
                Session session = null;
                if(isDefaultConsumer()){
                    session = channelManager.getSession(event.getChannel());
                }
                context.getGlobalQueue().processProtoEvent(session , event);
                break;

            case ON_TEXT_MESSAGE_COME:
                System.out.println(event.getData());
                break;

            case INNER_MSG:
                doInnerMsg(event.getData());
                break;

            default:
                throw new IllegalArgumentException("unknown channelEventType <" + event.getChannelEventType() + ">");
        }
    }

    private void doInnerMsg(Object msg) {
        if (!(msg instanceof CallBackEvent)) {
            return;
        }
        CallBackEvent callBackEvent = (CallBackEvent) msg;
        callBackEvent.execte(callBackEvent.getData());
    }


    private int getAndIncRequestId() {
        if (requestId == Integer.MAX_VALUE) {
            requestId = 0;
        }
        return requestId++;
    }

    protected void init() {

    }

    /**
     *
     */
    protected void loop() {
        timerManager.updateTimer();
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }

    public void setTimerManager(TimerManager timerManager) {
        this.timerManager = timerManager;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean isDefaultConsumer() {
        return consumerId == DEFAULT_CONSUMER_ID;
    }
}
