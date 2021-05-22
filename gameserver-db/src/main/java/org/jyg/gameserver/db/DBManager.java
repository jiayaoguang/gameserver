package org.jyg.gameserver.db;

import org.jyg.gameserver.core.data.EventExtData;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.core.util.Context;

/**
 * create by jiayaoguang at 2021/5/22
 */
@Deprecated
public class DBManager {

    private final Context context;

    protected final int dbConsumerId;

    public DBManager(Context context, int dbConsumerId){
        this.context = context;
        this.dbConsumerId = dbConsumerId;
    }

    public void insert(Object object) {
            context.getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, object, null, BDEventConst.INSERT
                    , new EventExtData(0, 0, object.getClass().hashCode()));
    }

    public void insert(Object object, long childChooseId) {
        context.getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, object, null, BDEventConst.INSERT
                , new EventExtData(0, 0, childChooseId));
    }

    public void delete(Object object, long childChooseId) {
        context.getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, object, null, BDEventConst.DELETE
                , new EventExtData(0, 0, childChooseId));

    }

    public void delete(Object object){
        context.getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, object, null, BDEventConst.DELETE
                , new EventExtData(0, 0, object.getClass().hashCode()));
    }

    public void update(Object object, long childChooseId) {
        context.getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, object, null, BDEventConst.UPDATE
                , new EventExtData(0, 0, childChooseId));
    }

    public void update(Object object) {
        context.getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, object, null, BDEventConst.UPDATE
                , new EventExtData(0, 0, object.getClass().hashCode()));
    }

//    public void select(Object object) {
//
////        fromConsumer.registerCallBackMethod(onSelectResult);
//
//        context.getConsumerManager().publicEvent(dbConsumerId, EventType.DEFAULT_EVENT, object, BDEventTypeConst.DB_EVENT_SELECT);
//    }


}
