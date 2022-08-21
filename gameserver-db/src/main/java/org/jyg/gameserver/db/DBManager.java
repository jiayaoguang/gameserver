package org.jyg.gameserver.db;

import org.jyg.gameserver.core.data.EventExtData;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.util.GameContext;

/**
 * create by jiayaoguang at 2021/5/22
 */
@Deprecated
public class DBManager {

    private final GameContext gameContext;

    protected final int dbConsumerId;

    public DBManager(GameContext gameContext, int dbConsumerId){
        this.gameContext = gameContext;
        this.dbConsumerId = dbConsumerId;
    }

    public void insert(Object object) {
            gameContext.getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, object, null, BDEventConst.INSERT
                    , new EventExtData(0, 0, object.getClass().hashCode()));
    }

    public void insert(Object object, long childChooseId) {
        gameContext.getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, object, null, BDEventConst.INSERT
                , new EventExtData(0, 0, childChooseId));
    }

    public void delete(Object object, long childChooseId) {
        gameContext.getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, object, null, BDEventConst.DELETE
                , new EventExtData(0, 0, childChooseId));

    }

    public void delete(Object object){
        gameContext.getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, object, null, BDEventConst.DELETE
                , new EventExtData(0, 0, object.getClass().hashCode()));
    }

    public void update(Object object, long childChooseId) {
        gameContext.getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, object, null, BDEventConst.UPDATE
                , new EventExtData(0, 0, childChooseId));
    }

    public void update(Object object) {
        gameContext.getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, object, null, BDEventConst.UPDATE
                , new EventExtData(0, 0, object.getClass().hashCode()));
    }

//    public void select(Object object) {
//
////        fromConsumer.registerCallBackMethod(onSelectResult);
//
//        context.getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, object, BDEventTypeConst.DB_EVENT_SELECT);
//    }


}
