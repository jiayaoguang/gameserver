package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.example.ygserver.msg.Vector2Msg;

import java.util.HashMap;
import java.util.Map;

public class RoomPlayer extends BattleObject{

//    private int id;

    private Player player = null;


    private int score;


    private Map<Long,Motion> motionMap = new HashMap<>();

    private Room room;


    public RoomPlayer() {

        this.setHp(100);

    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Map<Long, Motion> getMotionMap() {
        return motionMap;
    }

    public void setMotionMap(Map<Long, Motion> motionMap) {
        this.motionMap = motionMap;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
