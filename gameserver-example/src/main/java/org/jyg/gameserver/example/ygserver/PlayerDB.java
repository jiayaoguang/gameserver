package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.db.BaseDBEntity;
import org.jyg.gameserver.db.anno.DBTable;
import org.jyg.gameserver.db.anno.DBTableFieldIgnore;

@DBTable(tableName = "player")
public class PlayerDB extends BaseDBEntity {

    private long id;

    private String name;

    private String password;

    @DBTableFieldIgnore
    private String content;



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
