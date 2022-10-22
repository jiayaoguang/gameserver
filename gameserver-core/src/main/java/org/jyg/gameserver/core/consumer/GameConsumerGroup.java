package org.jyg.gameserver.core.consumer;

import cn.hutool.core.collection.CollectionUtil;
import io.netty.channel.Channel;
import org.jyg.gameserver.core.data.EventExtData;
import org.jyg.gameserver.core.enums.EventType;

import java.util.ArrayList;
import java.util.List;

/**
 * create by jiayaoguang on 2021/5/15
 */
public class GameConsumerGroup<T extends GameConsumer> extends GameConsumer {

    private volatile boolean isStart = false;

    private final List<T> childConsumerList = new ArrayList<>();

    public GameConsumerGroup(){

    }


    public GameConsumerGroup(ConsumerFactory<T> childConsumerFactory, int childConsumerNum) {

        for (int i = 0; i < childConsumerNum; i++) {
            T consumer = childConsumerFactory.createConsumer();
            addChildConsumer(consumer);
        }

    }


    public GameConsumerGroup(List<T> childConsumerList) {
        this.childConsumerList.addAll(childConsumerList);
    }

    @Override
    public void doStart() {


        if(CollectionUtil.isEmpty(childConsumerList)){
            throw new IllegalArgumentException("isEmpty(childConsumerList)");
        }

        isStart = true;
        int nextId = 1;
        for (GameConsumer childGameConsumer : childConsumerList) {
            if(childGameConsumer.getId() == 0){
                childGameConsumer.setId(nextId);
                nextId++;
            }
            childGameConsumer.setGameContext(getGameContext());
            childGameConsumer.start();

        }

    }

    @Override
    public void doStop() {

        if (CollectionUtil.isEmpty(childConsumerList)) {
            throw new RuntimeException(" isEmpty(childConsumerList) ");
        }

        for (GameConsumer childGameConsumer : childConsumerList) {
            try {
                childGameConsumer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void publicEvent(EventType evenType, Object data, Channel channel, int eventId, EventExtData eventExtData) {

        if (eventExtData == null) {
            throw new RuntimeException("eventExtData == null");
        }

        int childConsumerIndex = (int) (eventExtData.childChooseId % childConsumerList.size());
        GameConsumer childGameConsumer = childConsumerList.get(childConsumerIndex);
        childGameConsumer.publicEvent(evenType, data, channel, eventId, eventExtData);

    }



    public void initProcessors(ProcessorsInitializer processorsInitializer) {
        processorsInitializer.initProcessors(this);
    }

    public synchronized void addChildConsumer(T consumer, ProcessorsInitializer processorsInitializer) {
        processorsInitializer.initProcessors(consumer);
        addChildConsumer(consumer);
    }

    public synchronized void addChildConsumer(T consumer) {
        if (isStart) {
            throw new RuntimeException("already start");
        }
        childConsumerList.add(consumer);
    }

    protected List<T> getChildConsumerList() {
        return childConsumerList;
    }


    //    public Consumer getChildConsumer(int num) {
//        if (childConsumerList.isEmpty()) {
//            return null;
//        }
//        return childConsumerList.get(num % childConsumerList.size());
//    }


    protected boolean isStart() {
        return isStart;
    }
}
