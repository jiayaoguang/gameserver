package org.jyg.gameserver.db;

/**
 * create by jiayaoguang at 2021/5/14
 */
public enum TableFieldType {

    AUTO("", 20),

    INT("INT", 10),
    VARCHAR("VARCHAR", 128),
    CHAR("CHAR", 20),

    SMALLINT("SMALLINT", 6),
//    MEDIUMINT("MEDIUMINT"),
    INTEGER("INTEGER", 10),
    BIGINT("BIGINT", 20),

    DECIMAL("DECIMAL", 20),
    NUMERIC("NUMERIC", 20),

    TINYINT("TINYINT", 4),


    FLOAT("FLOAT", 10),
    DOUBLE("DOUBLE", 20),
    ;

    public final String name;
    public final int defaultLength;

//    TableFieldType(String name) {
//        this.name = name;
//    }

    TableFieldType(String name, int defaultLength) {
        this.name = name;
        this.defaultLength = defaultLength;
    }


    public String getName() {
        return name;
    }

    public int getDefaultLength() {
        return defaultLength;
    }
}
