package org.jyg.gameserver.db;

import org.jyg.gameserver.core.consumer.ResultHandler;
import org.jyg.gameserver.core.consumer.Consumer;
import org.jyg.gameserver.core.data.EventExtData;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.manager.Lifecycle;

import java.util.HashMap;
import java.util.Map;

/**
 * create by jiayaoguang at 2021/5/22
 */
public class ConsumerDBManager implements Lifecycle {

    private final Consumer consumer;


    private final int dbConsumerId;
//

    public ConsumerDBManager(Consumer consumer, int dbConsumerId) {
        this.dbConsumerId = dbConsumerId;
        this.consumer = consumer;
    }

    public int getDbConsumerId() {
        return dbConsumerId;
    }


    public void insert(BaseDBEntity dbEntity) {
        consumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, dbEntity, null, BDEventConst.INSERT
                , new EventExtData(0, 0, dbEntity.getClass().hashCode()));
    }

    public void insert(BaseDBEntity dbEntity, long childChooseId) {
        consumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, dbEntity, null, BDEventConst.INSERT
                , new EventExtData(0, 0, childChooseId));
    }

    public void delete(BaseDBEntity dbEntity, long childChooseId) {
        consumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, dbEntity, null, BDEventConst.DELETE
                , new EventExtData(0, 0, childChooseId));

    }

    public void delete(BaseDBEntity dbEntity){
        consumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, dbEntity, null, BDEventConst.DELETE
                , new EventExtData(0, 0, dbEntity.getClass().hashCode()));
    }

    public void update(BaseDBEntity dbEntity, long childChooseId) {
        consumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, dbEntity, null, BDEventConst.UPDATE
                , new EventExtData(0, 0, childChooseId));
    }

    public void update(BaseDBEntity dbEntity) {
        consumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, dbEntity, null, BDEventConst.UPDATE
                , new EventExtData(0, 0, dbEntity.getClass().hashCode()));
    }


    public void select(BaseDBEntity dbEntity, ResultHandler onSelectResult, long childChooseId) {
        int requestId = consumer.registerCallBackMethod(onSelectResult);
        consumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, dbEntity, null, BDEventConst.SELECT
                , new EventExtData(consumer.getId(), requestId, childChooseId));
    }

    public void select(BaseDBEntity dbEntity, ResultHandler onSelectResult) {
        select(dbEntity , onSelectResult , dbEntity.getClass().hashCode());
    }


    public void selectBy(BaseDBEntity dbEntity,String fieldName, ResultHandler onSelectResult, long childChooseId) {
        int requestId = consumer.registerCallBackMethod(onSelectResult);
        Map<String , Object> params = new HashMap<>();
        params.put("field" , fieldName);
        consumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, dbEntity, null, BDEventConst.SELECT_BY_FIELD
                , new EventExtData(consumer.getId(), requestId, childChooseId,params));
    }

    public void selectBy(BaseDBEntity dbEntity,String fieldName, ResultHandler onSelectResult) {
        selectBy(dbEntity,fieldName, onSelectResult,  dbEntity.getClass().hashCode());
    }


    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
