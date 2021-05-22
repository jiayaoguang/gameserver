package org.jyg.gameserver.test.db;

import com.alibaba.fastjson.JSONObject;
import org.jyg.gameserver.db.BaseDBEntity;
import org.jyg.gameserver.db.anno.DBTable;

/**
 * create by jiayaoguang on 2021/5/15
 */
@DBTable(tableName = "maik")
public class Maik2 extends BaseDBEntity {

    private long id;
    private JSONObject content;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public JSONObject getContent() {
        return content;
    }

    public void setContent(JSONObject content) {
        this.content = content;
    }
}
