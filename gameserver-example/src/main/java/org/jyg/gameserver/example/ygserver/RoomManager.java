package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.core.consumer.Consumer;
import org.jyg.gameserver.core.manager.Lifecycle;
import org.jyg.gameserver.example.ygserver.msg.SCRoomEndMsg;
import org.jyg.gameserver.example.ygserver.msg.Vector2Msg;
import org.jyg.gameserver.example.ygserver.msg.WallMsg;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class RoomManager implements Lifecycle {

    private final Consumer consumer;

    private Room room = new Room();


    public RoomManager(Consumer consumer) {
        this.consumer = consumer;
    }



    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }


    @Override
    public void start() {
        resetRoom(room);

        consumer.getTimerManager().addTimer(-1, TimeUnit.SECONDS.toMillis(1), this::update);

    }


    public void update(){
        long now = System.currentTimeMillis();

        if(room.getEndTime() < now){
            endRoom(room);
            resetRoom(room);

            consumer.getInstanceManager().getInstance(FrameManager.class).getPlayerFrameMsgMap().clear();
        }

    }

    public void endRoom(Room room){
        for(RoomPlayer roomPlayer : room.getRoomPlayerMap().values()){
            roomPlayer.getPlayer().getSession().writeMessage(new SCRoomEndMsg());
        }

        room.getRoomPlayerMap().clear();
    }

    public void resetRoom(Room room){

        room.getWallMsgList().clear();
        this.room.setEndTime(System.currentTimeMillis() +  TimeUnit.MINUTES.toMillis(3));

        Random random = new Random();
        for(int num = 0 ;num < 100;num++){
            WallMsg wallMsg = new WallMsg();
            Vector2Msg vector2Msg = new Vector2Msg();
            vector2Msg.setX(random.nextInt(600) - 300);
            vector2Msg.setY(random.nextInt(600) - 300);
//                                wallMsg.setX(random.nextInt(600) - 300);
//                                wallMsg.setY(random.nextInt(600) - 300);
            wallMsg.setHeight(random.nextInt(100));
            wallMsg.setWidth(random.nextInt(100));
            wallMsg.setPosi(vector2Msg);

            room.getWallMsgList().add(wallMsg);
        }
    }


    public RoomPlayer enterRoom(Player player){
        RoomPlayer roomPlayer = room.getRoomPlayerMap().get(player.getPlayerDB().getId());
        if(roomPlayer == null){
            roomPlayer = new RoomPlayer();
            roomPlayer.setPlayer(player);
            roomPlayer.setPosi(new Vector2Msg());
            room.getRoomPlayerMap().put(player.getPlayerDB().getId(),roomPlayer);
        }
        roomPlayer.setPlayer(player);

        return roomPlayer;
    }


    @Override
    public void stop() {

    }
}