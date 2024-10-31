package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.core.net.Request;
import org.jyg.gameserver.core.net.Response;
import org.jyg.gameserver.core.processor.HttpProcessor;
import org.jyg.gameserver.core.util.Logs;
import org.jyg.gameserver.db.util.CreateTableUtil;
import org.jyg.gameserver.example.ygserver.PlayerDB;

/**
 * create by jiayaoguang on 2021/9/5
 */
public class CreateTableProcessor extends HttpProcessor {

    public CreateTableProcessor() {
        super("/c");
    }

    @Override
    public void service(Request request, Response response) {

        try {
            CreateTableUtil.createTable(PlayerDB.class);
            response.writeAndFlush("ok");
        } catch (Exception e) {
            Logs.DEFAULT_LOGGER.error("make exception : " ,e);
            response.writeAndFlush("error");
        }

    }
}
