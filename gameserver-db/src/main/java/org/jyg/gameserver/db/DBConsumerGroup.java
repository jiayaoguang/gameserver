package org.jyg.gameserver.db;

import cn.hutool.core.collection.CollectionUtil;
import io.netty.channel.Channel;
import org.jyg.gameserver.core.consumer.*;
import org.jyg.gameserver.core.data.EventExtData;
import org.jyg.gameserver.core.enums.EventType;
import org.jyg.gameserver.db.serialize.DBFieldSerializer;

import java.util.ArrayList;
import java.util.List;

/**
 * create by jiayaoguang on 2021/5/15
 */
public class DBConsumerGroup extends ConsumerGroup<DBConsumer> {


    public DBConsumerGroup(int childConsumerNum) {

        super(DBConsumer::new, childConsumerNum);

    }


    public DBConsumerGroup(List<DBConsumer> childConsumerList) {
        super(childConsumerList);
    }


    public void addTableInfo(Class<?> dbEntity) {
        for (DBConsumer childDBConsumer : getChildConsumerList()) {
            childDBConsumer.addTableInfo(dbEntity);
        }
    }

    @Override
    protected void processDefaultEvent(int eventId, Object dbEntity) {
        for (DBConsumer childDBConsumer : getChildConsumerList()) {
            childDBConsumer.processDefaultEvent(eventId, dbEntity);
        }
    }

    public void addDBFieldSerializer(DBFieldSerializer<?> dbFieldSerializer){
        for (DBConsumer childDBConsumer : getChildConsumerList()) {
            childDBConsumer.addDBFieldSerializer(dbFieldSerializer);
        }
    }


}
