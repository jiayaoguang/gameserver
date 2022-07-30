package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.example.ygserver.msg.WallMsg;

import java.util.*;

public class Room {


    private long endTime = 0;

    private List<WallMsg> wallMsgList = new ArrayList<>();

    private Map<Long,RoomPlayer> roomPlayerMap = new LinkedHashMap<>();


    private int state;



    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }


    public List<WallMsg> getWallMsgList() {
        return wallMsgList;
    }

    public void setWallMsgList(List<WallMsg> wallMsgList) {
        this.wallMsgList = wallMsgList;
    }


    public Map<Long, RoomPlayer> getRoomPlayerMap() {
        return roomPlayerMap;
    }

    public void setRoomPlayerMap(Map<Long, RoomPlayer> roomPlayerMap) {
        this.roomPlayerMap = roomPlayerMap;
    }

    public void broadcast(ByteMsgObj byteMsgObj){
        for(RoomPlayer roomPlayer : roomPlayerMap.values() ){
            if(roomPlayer.getPlayer().getSession().isOpen()){
                roomPlayer.getPlayer().getSession().writeMessage(byteMsgObj);
            }
        }
    }
}
