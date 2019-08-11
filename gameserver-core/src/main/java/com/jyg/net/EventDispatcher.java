package com.jyg.net;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.protobuf.GeneratedMessageV3;
import com.jyg.bean.LogicEvent;
import com.jyg.enums.ProtoEnum;
import com.jyg.process.PingProtoProcessor;
import com.jyg.process.PongProtoProcessor;
import com.jyg.proto.p_common.p_common_request_ping;
import com.jyg.proto.p_common.p_common_response_pong;
import com.jyg.session.Session;
import com.jyg.timer.Timer;
import com.jyg.timer.TimerTrigger;
import com.jyg.util.DupilcateEventIdException;

import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

/**
 * created by jiayaoguang at 2017年12月17日
 * 用来分发给各个对应注册事件的的处理器
 */
public class EventDispatcher {

    private static final EventDispatcher dispatcher = new EventDispatcher();

    private final HttpProcessor notFOundProcessor = new NotFoundHttpProcessor();

    private final Map<String, HttpProcessor> httpPathMap = new HashMap<>();
    private final Int2ObjectMap<ProtoProcessor<? extends GeneratedMessageV3>> socketEventMap = new Int2ObjectOpenHashMap<>();

    private final Object2IntMap<String> protoNameToEventidMap = new Object2IntOpenHashMap<>();
    private final Map<Channel, Session> channelMap = new LinkedHashMap<>();

    private final TimerTrigger trigger = new TimerTrigger();
    //20 毫秒一帧
    private static final long FRAME_DURATION_TIMEMILLS = 20L;
    //上一帧时间戳
    private long nextFrameTimeStamp = System.currentTimeMillis();

    private EventDispatcher() {
        this.addTimer(new Timer(Integer.MAX_VALUE, 20 * 1000L, null) {
            public void call() {
                EventDispatcher.getInstance().removeOutOfTimeChannels();
            }
        });

        try {
            this.registerSendEventIdByProto(ProtoEnum.P_COMMON_REQUEST_PING.getEventId(), p_common_request_ping.class);
            //注册pong处理器
            this.registerSocketEvent(ProtoEnum.P_COMMON_RESPONSE_PONG.getEventId(), new PongProtoProcessor());

            this.registerSendEventIdByProto(ProtoEnum.P_COMMON_RESPONSE_PONG.getEventId(), p_common_response_pong.class);
            this.registerSocketEvent(ProtoEnum.P_COMMON_REQUEST_PING.getEventId(), new PingProtoProcessor());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static EventDispatcher getInstance() {
        return dispatcher;
    }

    //================================ rpc start =========================================

    /**
     * 注册普通socket事件
     *
     * @param eventid   事件id
     * @param processor 事件处理器
     */
    public void registerSocketEvent(int eventid, ProtoProcessor<? extends GeneratedMessageV3> processor) throws DupilcateEventIdException {
        if (socketEventMap.containsKey(eventid)) {
            throw new DupilcateEventIdException();
        }
//		eventidToProtoNameMap.put(processor.getProtoClassName(), eventid);
        socketEventMap.put(eventid, processor);
    }

    public ProtoProcessor<? extends GeneratedMessageV3> getSocketProcessor(int id) {
        return socketEventMap.get(id);
    }

    /**
     * 处理普通socket事件
     */
    public void socketProcess(LogicEvent<? extends GeneratedMessageV3> event) {
//		MessageLite msg = event.getData();
        ProtoProcessor processor = socketEventMap.get(event.getEventId());
        if (processor == null) {
            System.out.println("unknown socket eventid :" + event.getEventId());
            return;
        }
        processor.process((LogicEvent<? extends GeneratedMessageV3>) event);
    }


    public Integer getEventIdByProtoName(String protoName) {
        return protoNameToEventidMap.get(protoName);
    }

    /**
     * 绑定事件id到proto类，用于发送protobuf消息时，获得对应的eventId，并一起发送给对方
     *
     * @throws Exception
     */
    public void registerSendEventIdByProto(int eventId, Class<? extends GeneratedMessageV3> protoClazz) throws Exception {
        if (protoNameToEventidMap.containsKey(protoClazz.getName())) {
            throw new Exception("dupilcated protoClazz:" + protoClazz.getName());
        }
        protoNameToEventidMap.put(protoClazz.getName(), eventId);
    }

    //================================ socket end =========================================

    private long uid = 1L;

    public void as_on_client_active(LogicEvent<Object> event) {
        channelMap.put(event.getChannel(), new Session(event.getChannel(), uid++));
        // event.getChannel().writeAndFlush(new TextWebSocketFrame(""));
    }

    public void as_on_client_inactive(LogicEvent<Object> event) {
        channelMap.remove(event.getChannel());
    }

    public void as_on_inner_server_active(LogicEvent<Object> event) {
        InetSocketAddress address = (InetSocketAddress) event.getChannel().remoteAddress();

        System.out.println("name connect:" + address.getHostString());

    }

    public void as_on_inner_server_inactive(LogicEvent<Object> event) {
        InetSocketAddress address = (InetSocketAddress) event.getChannel().remoteAddress();

        System.out.println("name disconnect:" + address.getHostString());

    }

    public Session getSession(Channel channel) {
        return channelMap.get(channel);
    }

    //检测并移除超时的channel
    public void removeOutOfTimeChannels() {
//		System.out.println("检测并移除超时的channel");
        Iterator<Map.Entry<Channel, Session>> it = channelMap.entrySet().iterator();
        for (; it.hasNext(); ) {
            Map.Entry<Channel, Session> entry = it.next();
            Channel channel = entry.getKey();
            Session session = entry.getValue();
            if (!channel.isOpen()) {
                it.remove();
                continue;
            }
            if (session == null) {
                it.remove();
                continue;
            }
            if ((session.getLastContactMill() + 60 * 1000) < System.currentTimeMillis()) {
                channel.close();
                it.remove();
                System.out.println("移除超时的channel" + channel);
            }
        }
    }


    public void webSocketProcess(LogicEvent<? extends GeneratedMessageV3> event) {
        ProtoProcessor processor = socketEventMap.get(event.getEventId());
        if (processor == null) {
            System.out.println("unknown logic eventid");
            return;
        }
        processor.process(event);
    }


    public void addTimer(Timer timer) {
        trigger.addTimer(timer);
    }

    public void loop() {

        trigger.tickTigger();
        updateFrame();
    }
    /**
     * 暂时这么写
     */
    private void updateFrame(){
        long now = System.currentTimeMillis();
        while(now >= nextFrameTimeStamp){
            nextFrameTimeStamp += FRAME_DURATION_TIMEMILLS;

            //TODO logic code
        }
    }

    //============================= http start ===========================================

    /**
     * 注册http事件
     *
     * @param path http url path
     */
    public void registerHttpEvent(String path, HttpProcessor processor) throws Exception {
//		path = "/" + path;
        if (httpPathMap.containsKey(path)) {
            throw new IllegalArgumentException("dupilcated path");
        }
        if (path.charAt(0) != '/' || path.contains(".")) {
            throw new IllegalArgumentException("path cannot contain char:'.' and must start with char:'/' ");
        }

        httpPathMap.put(path, processor);
    }

    HttpProcessor getHttpProcessor(String path) {
        HttpProcessor processor = httpPathMap.get(path);
        if (processor == null) {
            processor = notFOundProcessor;
        }
        return processor;
    }

    public void httpProcess(LogicEvent<Request> event) {
        getHttpProcessor(event.getData().noParamUri()).process(event);
    }


    @Deprecated
    public String getNoParamPath(String uri) {
        int endIndex = uri.indexOf('?');
        if (endIndex == -1) {
            return uri;
        }

        return uri.substring(0, endIndex);
    }
    //============================= http end ===========================================

}
