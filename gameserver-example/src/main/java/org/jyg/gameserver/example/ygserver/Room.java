package org.jyg.gameserver.example.ygserver;

import cn.hutool.core.util.RandomUtil;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.util.LruMap;
import org.jyg.gameserver.example.ygserver.msg.PlayerFrameMsg;
import org.jyg.gameserver.example.ygserver.msg.Vector2Msg;
import org.jyg.gameserver.example.ygserver.msg.WallMsg;
import org.jyg.gameserver.example.ygserver.msg.data.MotionMsg;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Room {

    private long roomId;


    private long createTime = 0;

    private long startBattleTime = 0;

    private long endTime = 0;

    private List<WallMsg> wallMsgList = new ArrayList<>();

    private Map<Long,RoomPlayer> roomPlayerMap = new LinkedHashMap<>();

    private long roomObjUidInc;


    private Map<Long,Motion> sysMotionMap = new LinkedHashMap<>();

//    private RoomState state;


    private Map<Long,Motion> sysScoreMotionMap = new LinkedHashMap<>();


    private Map<Long , PlayerFrameMsg> playerFrameMsgMap = new LruMap<>(100);


    private long lastCreateScoreMotionTime;


    public Room(long roomId) {
        this.roomId = roomId;
//        this.state = RoomState.WAIT_BATTLE;
        this.createTime = System.currentTimeMillis();

        this.startBattleTime = createTime+ TimeUnit.SECONDS.toMillis(8);

        this.endTime = startBattleTime + TimeUnit.MINUTES.toMillis(4);
        this.roomObjUidInc = System.currentTimeMillis();
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

        Vector2Msg scale;
        if(type == 1){
            scale = new Vector2Msg(30,20);
        }else {
            int radiu = RandomUtil.randomInt( 5,30);
            scale = new Vector2Msg(radiu,radiu);
        }

        motion.setScale(scale);

        roomPlayer.getMotionMap().put(motion.getId() , motion);

        return motion;
    }


//    public Motion createSysMotion( Vector2Msg posi, int type){
//
//        Motion motion = new Motion();
//        motion.setId(allocUid());
//        motion.setType(type);
//        motion.setPosi(posi);
//        motion.setHp(100);
//        motion.setMaxHp(100);
//
//
//        int radiu = RandomUtil.randomInt( 5,30);
//
//        motion.setScale(new Vector2Msg(radiu , radiu));
//
//        sysMotionMap.put(motion.getId() , motion);
//
//        return motion;
//    }


    public MotionMsg createMotionMsg(RoomPlayer roomPlayer , Motion motion){

        return createMotionMsg(roomPlayer.getPlayer().getPlayerDB().getId() , motion);
    }


    public MotionMsg createMotionMsg(long playerId , Motion motion){
        MotionMsg motionMsg = new MotionMsg();
        motionMsg.setUid(motion.getId());
        motionMsg.setPosi(motion.getPosi());
        motionMsg.setScale(motion.getScale());
        motionMsg.setHp(motion.getHp());
        motionMsg.setMaxHp(motion.getMaxHp());
        motionMsg.setOwnPlayerId(playerId);
        motionMsg.setMotionType(motion.getType());
        return motionMsg;
    }



    private long allocUid(){
        roomObjUidInc ++;
        return roomObjUidInc;
    }


    public Map<Long, Motion> getSysMotionMap() {
        return sysMotionMap;
    }

    public void setSysMotionMap(Map<Long, Motion> sysMotionMap) {
        this.sysMotionMap = sysMotionMap;
    }


    public Map<Long, PlayerFrameMsg> getPlayerFrameMsgMap() {
        return playerFrameMsgMap;
    }

    public void setPlayerFrameMsgMap(Map<Long, PlayerFrameMsg> playerFrameMsgMap) {
        this.playerFrameMsgMap = playerFrameMsgMap;
    }


    public long getLastCreateScoreMotionTime() {
        return lastCreateScoreMotionTime;
    }

    public void setLastCreateScoreMotionTime(long lastCreateScoreMotionTime) {
        this.lastCreateScoreMotionTime = lastCreateScoreMotionTime;
    }


    public Motion createScoreMotion(){


        Vector2Msg posi = new Vector2Msg(RandomUtil.randomInt( -100,100) , RandomUtil.randomInt( -100,100));

        Motion motion = new Motion();
        motion.setId(allocUid());
        motion.setType(3);
        motion.setPosi(posi);
        motion.setHp(0);
        motion.setMaxHp(0);

        int radiu = RandomUtil.randomInt( 3,6);
        Vector2Msg scale = new Vector2Msg(radiu,radiu);

        motion.setScale(scale);
        this.lastCreateScoreMotionTime = System.currentTimeMillis();
        sysMotionMap.put(motion.getId() , motion);

        return motion;
    }


}
