package org.jyg.gameserver.test.db;

import org.jyg.gameserver.db.util.GenDBProxy;

import java.io.File;

/**
 * create by jiayaoguang on 2023/4/23
 */
public class CreateMaikDBProxy {

    public static void main(String[] args) {
        File file = new File("");
        GenDBProxy.genDBProxyFile(MaikDB.class , "gameserver-test/src/main/java");
    }

}
