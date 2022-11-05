package org.jyg.gameserver.db;

import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.consumer.ResultHandler;
import org.jyg.gameserver.core.data.EventExtData;
import org.jyg.gameserver.core.enums.EventType;
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
        gameConsumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, dbEntity, null, BDEventConst.INSERT
                , new EventExtData(0, 0L, dbEntity.getClass().hashCode()));
    }

    public void insert(BaseDBEntity dbEntity, long childChooseId) {
        gameConsumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, dbEntity, null, BDEventConst.INSERT
                , new EventExtData(0, 0L, childChooseId));
    }

    public void delete(BaseDBEntity dbEntity, long childChooseId) {
        gameConsumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, dbEntity, null, BDEventConst.DELETE
                , new EventExtData(0, 0L, childChooseId));

    }

    public void delete(BaseDBEntity dbEntity){
        gameConsumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, dbEntity, null, BDEventConst.DELETE
                , new EventExtData(0, 0L, dbEntity.getClass().hashCode()));
    }

    public void update(BaseDBEntity dbEntity, long childChooseId) {
        gameConsumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, dbEntity, null, BDEventConst.UPDATE
                , new EventExtData(0, 0L, childChooseId));
    }

    public void update(BaseDBEntity dbEntity) {
        gameConsumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, dbEntity, null, BDEventConst.UPDATE
                , new EventExtData(0, 0L, dbEntity.getClass().hashCode()));
    }


    public long select(BaseDBEntity dbEntity, ResultHandler onSelectResult, long childChooseId) {
        long requestId = gameConsumer.registerCallBackMethod(onSelectResult);
        gameConsumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, dbEntity, null, BDEventConst.SELECT
                , new EventExtData(gameConsumer.getId(), requestId, childChooseId));
        return requestId;
    }

    public long select(BaseDBEntity dbEntity, ResultHandler onSelectResult) {
        return select(dbEntity , onSelectResult , dbEntity.getClass().hashCode());
    }


    public long selectBy(BaseDBEntity dbEntity,String fieldName, ResultHandler onSelectResult, long childChooseId) {
        long requestId = gameConsumer.registerCallBackMethod(onSelectResult);
        Map<String , Object> params = new HashMap<>();
        params.put("field" , fieldName);
        gameConsumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, dbEntity, null, BDEventConst.SELECT_BY_FIELD
                , new EventExtData(gameConsumer.getId(), requestId, childChooseId,params));
        return requestId;
    }

    public long selectBy(BaseDBEntity dbEntity,String fieldName, ResultHandler onSelectResult) {
        return selectBy(dbEntity,fieldName, onSelectResult,  dbEntity.getClass().hashCode());
    }


    public long execQuerySql(Class<?> dbEntityClazz, String prepareSql , List<Object> paramValues, long childChooseId, ResultHandler onQueryResult) {
        return execSql(dbEntityClazz, prepareSql, paramValues,SqlExecuteType.QUERY_MANY, childChooseId,onQueryResult);
    }

    public long execQuerySql(Class<?> dbEntityClazz, String prepareSql , List<Object> paramValues, ResultHandler onQueryResult) {
        return execQuerySql(dbEntityClazz, prepareSql, paramValues, dbEntityClazz.hashCode(),onQueryResult);
    }


    public long execSql(String prepareSql , List<Object> paramValues, SqlExecuteType executeType, long childChooseId, ResultHandler onQueryResult) {
       return execSql(null, prepareSql, paramValues,executeType, childChooseId,onQueryResult);
    }

    public long execSql(Class<?> dbEntityClazz, String prepareSql , List<Object> paramValues , SqlExecuteType executeType, long childChooseId, ResultHandler onQueryResult) {
        long requestId = gameConsumer.registerCallBackMethod(onQueryResult);
        ExecSqlInfo execSqlInfo = new ExecSqlInfo(prepareSql ,paramValues , dbEntityClazz , executeType);
        gameConsumer.getGameContext().getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, execSqlInfo, null, 0
                , new EventExtData(gameConsumer.getId(), requestId, childChooseId));
        return requestId;
    }


    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
