package org.jyg.gameserver.db;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.consumer.ResultHandler;
import org.jyg.gameserver.core.event.ConsumerDefaultEvent;
import org.jyg.gameserver.core.manager.Lifecycle;
import org.jyg.gameserver.db.data.ExecSqlInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by jiayaoguang at 2021/5/22
 */
public class ConsumerDBManager implements Lifecycle {

    private final GameConsumer gameConsumer;


    private final int dbConsumerId;
//

    public ConsumerDBManager(GameConsumer gameConsumer, int dbConsumerId) {
        this.dbConsumerId = dbConsumerId;
        this.gameConsumer = gameConsumer;
    }

    public int getDbConsumerId() {
        return dbConsumerId;
    }


    public void insert(BaseDBEntity dbEntity) {

        ConsumerDefaultEvent consumerDefaultEvent = new ConsumerDefaultEvent(BDEventConst.INSERT, dbEntity);

        gameConsumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, consumerDefaultEvent, dbEntity.getClass().getSimpleName());
    }

    public void insert(BaseDBEntity dbEntity, String childChooseId) {

        ConsumerDefaultEvent consumerDefaultEvent = new ConsumerDefaultEvent(BDEventConst.INSERT, dbEntity);

        gameConsumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, consumerDefaultEvent, childChooseId);
    }

    public void delete(BaseDBEntity dbEntity, String childChooseId) {
        ConsumerDefaultEvent consumerDefaultEvent = new ConsumerDefaultEvent(BDEventConst.DELETE, dbEntity);
        gameConsumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, consumerDefaultEvent, childChooseId);

    }

    public void delete(BaseDBEntity dbEntity){
        ConsumerDefaultEvent consumerDefaultEvent = new ConsumerDefaultEvent(BDEventConst.DELETE, dbEntity);
        gameConsumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, consumerDefaultEvent, dbEntity.getClass().getSimpleName());
    }

    public void update(BaseDBEntity dbEntity, String childChooseId) {

        ConsumerDefaultEvent consumerDefaultEvent = new ConsumerDefaultEvent(BDEventConst.UPDATE, dbEntity);

        gameConsumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, consumerDefaultEvent, childChooseId);
    }

    public void update(BaseDBEntity dbEntity) {
        ConsumerDefaultEvent consumerDefaultEvent = new ConsumerDefaultEvent(BDEventConst.UPDATE, dbEntity);
        gameConsumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, consumerDefaultEvent, dbEntity.getClass().getSimpleName());
    }


    public long select(BaseDBEntity dbEntity, ResultHandler onSelectResult, String childChooseId) {
        long requestId = gameConsumer.registerCallBackMethod(onSelectResult);
        ConsumerDefaultEvent consumerDefaultEvent = new ConsumerDefaultEvent(BDEventConst.SELECT, dbEntity);
        consumerDefaultEvent.setFromConsumerId(gameConsumer.getId());
        consumerDefaultEvent.setEventId(BDEventConst.SELECT);
        consumerDefaultEvent.setData(dbEntity);
        consumerDefaultEvent.setRequestId(requestId);

        gameConsumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, consumerDefaultEvent ,childChooseId );
        return requestId;
    }

    public long select(BaseDBEntity dbEntity, ResultHandler onSelectResult) {
        return select(dbEntity , onSelectResult , dbEntity.getClass().getSimpleName());
    }


    public long selectBy(BaseDBEntity dbEntity,String fieldName, ResultHandler onSelectResult, String childChooseId) {
        long requestId = gameConsumer.registerCallBackMethod(onSelectResult);
        Map<String , Object> params = new HashMap<>();
        params.put("field" , fieldName);

        ConsumerDefaultEvent consumerDefaultEvent = new ConsumerDefaultEvent(BDEventConst.SELECT, dbEntity);
        consumerDefaultEvent.setFromConsumerId(gameConsumer.getId());
        consumerDefaultEvent.setEventId(BDEventConst.SELECT_BY_FIELD);
        consumerDefaultEvent.setData(dbEntity);
        consumerDefaultEvent.setRequestId(requestId);
        consumerDefaultEvent.setParams(params);

        gameConsumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, consumerDefaultEvent, childChooseId );
        return requestId;
    }

    public long selectBy(BaseDBEntity dbEntity,String fieldName, ResultHandler onSelectResult) {
        return selectBy(dbEntity,fieldName, onSelectResult,  dbEntity.getClass().getSimpleName());
    }


    public long execQuerySql(Class<?> dbEntityClazz, String prepareSql , List<Object> paramValues, String childChooseId, ResultHandler onQueryResult) {
        return execSql(dbEntityClazz, prepareSql, paramValues,SqlExecuteType.QUERY_MANY, childChooseId,onQueryResult);
    }

    public long execQuerySql(Class<?> dbEntityClazz, String prepareSql , List<Object> paramValues, ResultHandler onQueryResult) {
        return execQuerySql(dbEntityClazz, prepareSql, paramValues, dbEntityClazz.getSimpleName(),onQueryResult);
    }


    public long execSql(String prepareSql , List<Object> paramValues, SqlExecuteType executeType, String childChooseId, ResultHandler onQueryResult) {
       return execSql(null, prepareSql, paramValues,executeType, childChooseId,onQueryResult);
    }

    public long execSql(Class<?> dbEntityClazz, String prepareSql , List<Object> paramValues , SqlExecuteType executeType, String childChooseId, ResultHandler onQueryResult) {
        long requestId = gameConsumer.registerCallBackMethod(onQueryResult);
        ExecSqlInfo execSqlInfo = new ExecSqlInfo(prepareSql ,paramValues , dbEntityClazz , executeType);

        ConsumerDefaultEvent consumerDefaultEvent = new ConsumerDefaultEvent();
        consumerDefaultEvent.setFromConsumerId(gameConsumer.getId());
        consumerDefaultEvent.setEventId(0);
        consumerDefaultEvent.setData(execSqlInfo);
        consumerDefaultEvent.setRequestId(requestId);

        gameConsumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, consumerDefaultEvent, childChooseId );
        return requestId;
    }


    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
