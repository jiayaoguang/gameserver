package org.jyg.gameserver.db;

/**
 * create by jiayaoguang at 2021/5/14
 */
public class MySQLUpperKey implements SqlKeyWord {

    private static final String SPACE = " ";
    private static final String select = "SELECT";
    private static final String as = "AS";
    private static final String from = "FROM";
    private static final String where = "WHERE";
    private static final String insert = "INSERT";
    private static final String into = "INTO";
    private static final String values = "VALUES";
    private static final String and = "AND";
    private static final String or = "OR";
    private static final String Null = "NULL";
    private static final String isNull = "IS NULL";
    private static final String isNotNull = "IS NOT NULL";
    private static final String update = "UPDATE";
    private static final String set = "SET";
    private static final String delete = "DELETE";
    private static final String orderBy = "ORDER BY";
    private static final String count = "COUNT";
    private static final String asc = "ASC";
    private static final String on = "ON";

    private static final String limit = "LIMIT";
    private static final String offset = "OFFSET";
    private static final String top = "TOP";

    private static final String groupBy = "GROUP BY";
    private static final String having = "HAVING";
    private static final String between = "BETWEEN";
    private static final String notBetween = "NOT BETWEEN";

    private static final String forUpdate = "FOR UPDATE";

    private static final String distinct = "DISTINCT";
    private static final String join = "JOIN";
    private static final String innerJoin = "INNER JOIN";
    private static final String leftJoin = "LEFT JOIN";
    private static final String rightJoin = "RIGHT JOIN";
    private static final String in = "IN";
    private static final String notIn = "NOT IN";
    private static final String exists = "EXISTS";
    private static final String notExists = "NOT EXISTS";

    @Override
    public String select() {
        return select;
    }

    @Override
    public String as() {
        return as;
    }

    @Override
    public String from() {
        return from;
    }

    @Override
    public String where() {
        return where;
    }

    @Override
    public String insert() {
        return insert;
    }

    @Override
    public String into() {
        return into;
    }

    @Override
    public String values() {
        return values;
    }

    @Override
    public String and() {
        return and;
    }

    @Override
    public String or() {
        return or;
    }

    @Override
    public String Null() {
        return Null;
    }

    @Override
    public String isNull() {
        return isNull;
    }

    @Override
    public String isNotNull() {
        return isNotNull;
    }

    @Override
    public String space() {
        return SPACE;
    }

    @Override
    public String update() {
        return update;
    }

    @Override
    public String set() {
        return set;
    }

    @Override
    public String delete() {
        return delete;
    }

    @Override
    public String orderBy() {
        return orderBy;
    }

    @Override
    public String count() {
        return count;
    }

    @Override
    public String asc() {
        return asc;
    }

    @Override
    public String on() {
        return on;
    }

    @Override
    public String forUpdate() {
        return forUpdate;
    }

    @Override
    public String limit() {
        return limit;
    }

    @Override
    public String offset() {
        return offset;
    }

    @Override
    public String top() {
        return top;
    }

    @Override
    public String groupBy() {
        return groupBy;
    }

    @Override
    public String having() {
        return having;
    }

    @Override
    public String between() {
        return between;
    }

    @Override
    public String notBetween() {
        return notBetween;
    }

    @Override
    public String distinct() {
        return distinct;
    }

    @Override
    public String join() {
        return join;
    }

    @Override
    public String innerJoin() {
        return innerJoin;
    }

    @Override
    public String leftJoin() {
        return leftJoin;
    }

    @Override
    public String rightJoin() {
        return rightJoin;
    }

    @Override
    public String in() {
        return in;
    }

    @Override
    public String notIn() {
        return notIn;
    }

    @Override
    public String exists() {
        return exists;
    }

    @Override
    public String notExists() {
        return notExists;
    }

}

