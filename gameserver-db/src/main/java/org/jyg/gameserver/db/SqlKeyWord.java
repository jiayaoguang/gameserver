package org.jyg.gameserver.db;

/**
 * create by jiayaoguang at 2021/5/14
 */
public interface SqlKeyWord {

    String select();
    String as();
    String from();
    String where();

    String insert();
    String into();
    String values();
    String and();
    String or();
    String Null();
    String isNull();
    String isNotNull();

    String update();
    String set();
    String delete();

    String orderBy();
    String count();
    String asc();

    String on();
    String forUpdate();
    String limit();
    String offset();
    String top();

    String groupBy();
    String having();
    String between();
    String notBetween();

    String space();

    String distinct();
    String join();
    String innerJoin();
    String leftJoin();
    String rightJoin();
    String in();
    String notIn();
    String exists();
    String notExists();
}

