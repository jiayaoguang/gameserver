package org.jyg.gameserver.test.db;

import org.jyg.gameserver.db.util.CreateTableUtil;

/**
 * create by jiayaoguang on 2023/4/23
 */
public class CreateMaikTable {

    public static void main(String[] args) {
        CreateTableUtil.createTable(MaikDB.class);
    }

}
