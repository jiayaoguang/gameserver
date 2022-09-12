package org.jyg.gameserver.example.ygserver;

import cn.hutool.core.util.RandomUtil;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.example.ygserver.msg.Vector2Msg;
import org.jyg.gameserver.example.ygserver.msg.WallMsg;
import org.jyg.gameserver.example.ygserver.msg.data.MotionMsg;

import java.util.*;

public class Room {

    private long roomId;


    private long endTime = 0;

    private List<WallMsg> wallMsgList = new ArrayList<>();

    private Map<Long,RoomPlayer> roomPlayerMap = new LinkedHashMap<>();

    private long roomObjUidInc;


    private int state;

    public Room(long roomId) {
        this.roomId = roomId;
    }


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


    public long getRoomId() {
        return roomId;
    }


    public Motion createMotion(RoomPlayer roomPlayer , Vector2Msg posi, int type){

        Motion motion = new Motion();
        motion.setId(allocUid());
        motion.setType(type);
        motion.setPosi(posi);
        motion.setHp(100);
        motion.setMaxHp(100);

        motion.setScale(new Vector2Msg(RandomUtil.randomInt( 5,30) , RandomUtil.randomInt( 5,30)));

        roomPlayer.getMotionMap().put(motion.getId() , motion);

        return motion;
    }

    public MotionMsg createMotionMsg(RoomPlayer roomPlayer , Motion motion){
        MotionMsg motionMsg = new MotionMsg();
        motionMsg.setPosi(motion.getPosi());
        motionMsg.setScale(motion.getScale());
        motionMsg.setHp(motion.getHp());
        motionMsg.setMaxHp(motion.getMaxHp());
        motionMsg.setOwnPlayerId(roomPlayer.getPlayer().getPlayerDB().getId());
        return motionMsg;
    }



    private long allocUid(){
        roomObjUidInc ++;
        return roomObjUidInc;
    }

}
