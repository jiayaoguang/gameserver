package org.jyg.gameserver.core.consumer;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.manager.ChannelManager;
import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.timer.TimerManager;
import org.jyg.gameserver.core.util.CallBackEvent;
import org.jyg.gameserver.core.util.Context;

/**
 * created by jiayaoguang at 2017年12月6日
 */
public class ConsumerHandler {

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


    public final void onReciveEvent(EventData<?> event) {

        // System.out.println(event.getChannel());
        try {
            doEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            update();
        }

    }

    private void doEvent(EventData event) {
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

            case HTTP_MESSAGE_COME:
                ((Request) event.getData()).setRequestid(getAndIncRequestId());
                context.getDefaultConsumer().processHttpEvent(event);
//				event.getChannel().close();
                // 5秒后关闭
//				dispatcher.getTimerManager().addTimer(new DelayCloseTimer(event.getChannel(), 60 * 1000L));
                break;
            case ON_MESSAGE_COME:
//				dispatcher.webSocketProcess(event);
//				break;
            case RROTO_MSG_COME: {
                Session session = null;
                if (isDefaultConsumer()) {
                    session = channelManager.getSession(event.getChannel());
                }
                context.getDefaultConsumer().processEventMsg(session, event);
                break;
            }
            case BYTE_OBJ_MSG_COME:{
                Session session = null;
                if (isDefaultConsumer()) {
                    session = channelManager.getSession(event.getChannel());
                }
                context.getDefaultConsumer().processEventMsg(session, event);
                break;
            }

            case TEXT_MESSAGE_COME: {
                Session session = null;
                if (isDefaultConsumer()) {
                    session = channelManager.getSession(event.getChannel());
                }
                context.getDefaultConsumer().processTextEvent(session, event);
                break;
            }

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
        callBackEvent.execte();
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
    public void update() {
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

    public ChannelManager getChannelManager() {
        return channelManager;
    }
}
