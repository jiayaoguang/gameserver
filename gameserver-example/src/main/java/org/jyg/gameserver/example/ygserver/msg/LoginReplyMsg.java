package org.jyg.gameserver.example.ygserver.msg;

import org.jyg.gameserver.core.msg.ByteMsgObj;

import java.util.ArrayList;
import java.util.List;

public class LoginReplyMsg implements ByteMsgObj {

    private long id;

    private String name;


    public List<WallMsg> wallMsgs = new ArrayList<>();

    private int errorCode;

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


    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public List<WallMsg> getWallMsgs() {
        return wallMsgs;
    }

    public void setWallMsgs(List<WallMsg> wallMsgs) {
        this.wallMsgs = wallMsgs;
    }
}
