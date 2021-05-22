package org.jyg.gameserver.test.db;

import org.jyg.gameserver.db.BaseDBEntity;

/**
 * create by jiayaoguang on 2021/5/15
 */
public class Maik extends BaseDBEntity {

    private long id;
    private String content;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
