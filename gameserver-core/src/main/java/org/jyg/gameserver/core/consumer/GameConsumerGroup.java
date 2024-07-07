package org.jyg.gameserver.core.consumer;

import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.apache.commons.lang3.StringUtils;
import org.jyg.gameserver.core.consumer.choose.ChildChooser;
import org.jyg.gameserver.core.consumer.choose.ModChildChooser;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.event.ResultReturnEvent;
import org.jyg.gameserver.core.util.Logs;

import java.util.ArrayList;
import java.util.List;

/**
 * create by jiayaoguang on 2021/5/15
 */
public class GameConsumerGroup<T extends GameConsumer> extends MpscQueueGameConsumer {

    private final List<T> childConsumerList = new ArrayList<>();


    private ChildChooser childChooser = new ModChildChooser();


    private final IntSet childIdSet = new IntLinkedOpenHashSet(32);


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
        super.doStart();

        if(childConsumerList.isEmpty()){
            throw new IllegalArgumentException("isEmpty(childConsumerList)");
        }

        int nextId = 1;
        for (GameConsumer childGameConsumer : childConsumerList) {
            if(childGameConsumer.getId() == 0){
                childGameConsumer.setId(getId() * 10000 + nextId);
                nextId++;
            }
            childGameConsumer.setGameContext(getGameContext());
            childIdSet.add(childGameConsumer.getId());
        }

        for (GameConsumer childGameConsumer : childConsumerList) {
            childGameConsumer.start();
        }

    }

    @Override
    public void doStop() {

        if ( childConsumerList.isEmpty()) {
            Logs.CONSUMER.info(" isEmpty(childConsumerList) ");
            return;
        }

        for (GameConsumer childGameConsumer : childConsumerList) {
            try {
                childGameConsumer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



//    @Override
//    public void publicEvent(EventData<?> eventData) {
//
//
////        int childConsumerIndex = 0;
//
//        String childChooseKey = eventData.getChildChooseId();
//        if(StringUtils.isEmpty(childChooseKey)){
//            childChooseKey = String.valueOf(eventData.getEventId());
//            Logs.DEFAULT_LOGGER.warn("Suggest assigning values to field childChooseKey");
//        }
//
//        GameConsumer childGameConsumer = childChooser.choose(childChooseKey , childConsumerList);
//        childGameConsumer.publicEvent(eventData);
//    }



    @Override
    protected void onReciveEvent(EventData<?> eventData) {

        if(eventData.getEvent() instanceof ResultReturnEvent || childIdSet.contains(eventData.getEvent().getFromConsumerId())){

            super.onReciveEvent(eventData);
            return;
        }

        String childChooseKey = eventData.getChildChooseId();
        if(StringUtils.isEmpty(childChooseKey)){
            childChooseKey = String.valueOf(eventData.getEventId());
            Logs.DEFAULT_LOGGER.warn("Suggest assigning values to field childChooseKey");
        }

        if(eventData.getEvent().getRequestId() != 0){
            long originRequestId = eventData.getEvent().getRequestId();
            int fromConsumerId = eventData.getEvent().getFromConsumerId();

            eventData.getEvent().setFromConsumerId(getId());

            long requestId = registerCallBackMethod((eventId, data) -> eventReturn(fromConsumerId , data , originRequestId));

            eventData.getEvent().setRequestId(requestId);

        }

        GameConsumer childGameConsumer = childChooser.choose(childChooseKey , childConsumerList);
        childGameConsumer.publishEvent(eventData);
    }


    public synchronized void addChildConsumer(T consumer, ProcessorsInitializer processorsInitializer) {
        processorsInitializer.initProcessors(consumer);
        addChildConsumer(consumer);
    }

    public synchronized void addChildConsumer(T consumer) {
        if (isStart()) {
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




    public ChildChooser getChildChooser() {
        return childChooser;
    }

    public void setChildChooser(ChildChooser childChooser) {
        this.childChooser = childChooser;
    }
}
