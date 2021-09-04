package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.db.util.CreateTableUtil;

/**
 * create by jiayaoguang at 2021/9/4
 */
public class CreateTable {


    public static void main(String[] args) throws Exception {

        CreateTableUtil.createTable(PlayerDB.class);

    }

}
