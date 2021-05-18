package org.jyg.gameserver.db.serialize;

/**
 * create by jiayaoguang on 2021/5/16
 */
public interface DBFieldSerializer<T> {

    Object serialize(String str);

    String unserialize(Object object);

    Class<T> getSerializeClass();

}
