package org.jyg.gameserver.core.manager;

import io.netty.channel.Channel;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.data.EventExtData;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.util.GameContext;
import org.jyg.gameserver.core.util.Logs;

import java.util.ArrayList;
import java.util.List;

/**
 * create by jiayaoguang on 2020/5/23
 */
public class ConsumerManager implements Lifecycle{

    private final Int2ObjectMap<GameConsumer> consumerMap = new Int2ObjectLinkedOpenHashMap<>();
    private final GameContext gameContext;


    public ConsumerManager(GameContext gameContext) {
        this.gameContext = gameContext;
    }


    public synchronized void addConsumer(GameConsumer gameConsumer) {
        if(gameContext.isStart()){
            throw new UnsupportedOperationException(" context.isStart , addConsumer operation fail ");
        }

        if(consumerMap.containsKey(gameConsumer.getId())){
            throw new IllegalArgumentException("dumplicate context id , addConsumer operation fail ");
        }

        gameConsumer.setGameContext(gameContext);
        consumerMap.put(gameConsumer.getId(), gameConsumer);
    }

//    public synchronized void setConsumers(Int2ObjectMap<Consumer> consumerMap){
//        this.consumerMap = Int2ObjectMaps.unmodifiable(consumerMap);
//    }

    private GameConsumer getConsumer(int id){
        return this.consumerMap.get(id);
    }

    public List<GameConsumer> getConsumers(){
        return new ArrayList<>(this.consumerMap.values());
    }


    public void publicEvent(int targetConsumerId, EventType evenType, Object data, Channel channel, int eventId , EventExtData eventExtData ){
        GameConsumer gameConsumer = getConsumer(targetConsumerId);
        if(gameConsumer == null){
            Logs.DEFAULT_LOGGER.error("targetConsumer {} not found" , targetConsumerId);
            return;
        }
        gameConsumer.publicEvent(evenType , data ,channel ,eventId , eventExtData);

    }

    public void publicEvent(int targetConsumerId, EventType evenType, Object data, Channel channel, int eventId ){
        this.publicEvent(targetConsumerId , evenType , data ,channel ,eventId , EventData.EMPTY_EVENT_EXT_DATA);
    }

    public void publicEventToDefault(EventType evenType, Object data, Channel channel, int eventId ){
        this.publicEvent(gameContext.getMainConsumerId() , evenType , data ,channel ,eventId , EventData.EMPTY_EVENT_EXT_DATA);
    }

    public void publicEventToDefault(EventType evenType, Object data, int eventId) {
        this.publicEvent(gameContext.getMainConsumerId(), evenType, data, null, eventId, EventData.EMPTY_EVENT_EXT_DATA);
    }

    public void publicEvent(int targetConsumerId, EventType evenType, Object data, int eventId ){
        this.publicEvent(targetConsumerId , evenType , data ,null ,eventId , EventData.EMPTY_EVENT_EXT_DATA);
    }

    public void publicCallBackEvent(int targetConsumerId, Object data, long requestId ,int eventId){
        if(requestId == 0){
            Logs.DEFAULT_LOGGER.error("publicCallBackEvent requestId == 0");
            return;
        }
        this.publicEvent(targetConsumerId , EventType.RESULT_CALL_BACK, data ,null ,eventId , new EventExtData( 0 , requestId ,0));
    }


    @Override
    public void start() {
        for(GameConsumer gameConsumer : getConsumers()){
            gameConsumer.start();
        }
    }

    @Override
    public void stop() {
        for(GameConsumer gameConsumer : getConsumers()){
            try{
                gameConsumer.stop();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
