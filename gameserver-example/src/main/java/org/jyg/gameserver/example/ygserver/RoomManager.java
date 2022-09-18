package org.jyg.gameserver.example.ygserver;

import cn.hutool.core.util.RandomUtil;
import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.event.ConnectEvent;
import org.jyg.gameserver.core.event.DisconnectEvent;
import org.jyg.gameserver.core.manager.Lifecycle;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.example.ygserver.msg.SCRoomEndMsg;
import org.jyg.gameserver.example.ygserver.msg.ServerFrameMsg;
import org.jyg.gameserver.example.ygserver.msg.Vector2Msg;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class RoomManager implements Lifecycle {

    private final GameConsumer gameConsumer;


    private Player waitBattlePlayer;

    private Map<Long,RoomPlayer> battleRoomPlayerMap = new LinkedHashMap<>();

    private long roomIdInc = 0L;


    private Map<Long,Room> battleRoomMap = new LinkedHashMap<>();

//    private Map<Long, Room> waitBattleRoomMap = new LinkedHashMap<>(1024, 0.5f);
//    private Map<Long, Room> inBattleRoomMap = new LinkedHashMap<>(1024, 0.5f);



    public RoomManager(GameConsumer gameConsumer) {
        this.gameConsumer = gameConsumer;
        this.roomIdInc = System.currentTimeMillis();

    }





    public Room getRoom(long humanId) {
        RoomPlayer roomPlayer = battleRoomPlayerMap.get(humanId);
        if(roomPlayer == null){
            return null;
        }
        return roomPlayer.getRoom();
    }



    @Override
    public void start() {
//        resetRoom(room);

        gameConsumer.getTimerManager().addUnlimitedTimer(TimeUnit.SECONDS.toMillis(1), this::update);
        gameConsumer.getTimerManager().addUnlimitedTimer(TimeUnit.MILLISECONDS.toMillis(20), this::updateRoomFrame);
        gameConsumer.getTimerManager().addUnlimitedTimer(TimeUnit.SECONDS.toMillis(3), this::updateWaitBattlePlayer);

//        gameConsumer.getEventManager().addEvent(new DisconnectEvent((session , data)->{
//            if(waitBattlePlayer != null && session.getSessionId() == waitBattlePlayer.getSession().getSessionId()){
//                waitBattlePlayer = null;
//            }
//        }));


    }


    public void update(){
        for(Room room : this.battleRoomMap.values()){
            long now = System.currentTimeMillis();

            if(room.getEndTime() < now){
                endRoom(room);
//                resetRoom(room);

                gameConsumer.getInstanceManager().getInstance(FrameManager.class).getPlayerFrameMsgMap().clear();
            }
        }

    }

    public void endRoom(Room room){
        for(RoomPlayer roomPlayer : room.getRoomPlayerMap().values()){
            roomPlayer.getPlayer().getSession().writeMessage(new SCRoomEndMsg());

            battleRoomPlayerMap.remove(roomPlayer.getPlayer().getPlayerDB().getId());

        }
        battleRoomMap.remove(room.getRoomId());
        room.getRoomPlayerMap().clear();
    }



    public Room enterRoom( Player player1 , Player player2){

        Room room = new Room(allocateRoomId());

        this.battleRoomMap.put(room.getRoomId() , room);


        {
            RoomPlayer roomPlayer1 = room.getRoomPlayerMap().get(player1.getPlayerDB().getId());
            if(roomPlayer1 == null){
                roomPlayer1 = new RoomPlayer();
                roomPlayer1.setPlayer(player1);
                roomPlayer1.setPosi(new Vector2Msg());
                roomPlayer1.setRoom(room);
                room.getRoomPlayerMap().put(player1.getPlayerDB().getId(),roomPlayer1);
                this.battleRoomPlayerMap.put(player1.getPlayerDB().getId(),roomPlayer1);
            }
            roomPlayer1.setPlayer(player1);
            Vector2Msg posi = new Vector2Msg( 0 , -100);

            Motion sysMotion = room.createMotion(roomPlayer1 , posi , 1);
        }


        {
            RoomPlayer roomPlayer2 = room.getRoomPlayerMap().get(player2.getPlayerDB().getId());
            if(roomPlayer2 == null){
                roomPlayer2 = new RoomPlayer();
                roomPlayer2.setPlayer(player1);
                roomPlayer2.setPosi(new Vector2Msg());
                roomPlayer2.setRoom(room);
                room.getRoomPlayerMap().put(player2.getPlayerDB().getId(),roomPlayer2);
                this.battleRoomPlayerMap.put(player2.getPlayerDB().getId(),roomPlayer2);
            }
            roomPlayer2.setPlayer(player2);
            Vector2Msg posi = new Vector2Msg( 0 , 100);

            Motion sysMotion = room.createMotion(roomPlayer2 , posi , 1);
        }

        if (waitBattlePlayer != null){
            if(player1.getSession().getSessionId() == waitBattlePlayer.getSession().getSessionId()){
                waitBattlePlayer = null;
            }else if(player2.getSession().getSessionId() == waitBattlePlayer.getSession().getSessionId()){
                waitBattlePlayer = null;
            }
        }


//        for(int i = 0;i< 10;i++){
//            Vector2Msg posi = new Vector2Msg( 0 , RandomUtil.randomInt(-90 , 90));
//            room.createSysMotion( posi , 0);
//        }

        return room;
    }


    @Override
    public void stop() {

    }




    private long allocateRoomId(){
        roomIdInc ++;
        return roomIdInc;
    }


    public Player getWaitBattlePlayer() {
        return waitBattlePlayer;
    }

    public void setWaitBattlePlayer(Player waitBattlePlayer) {
        this.waitBattlePlayer = waitBattlePlayer;
    }



    private void updateRoomFrame(){

        for( Room room : this.battleRoomMap.values() ){

            FrameManager frameManager = gameConsumer.getInstance(FrameManager.class);

            ServerFrameMsg serverFrameMsg = new ServerFrameMsg();

            serverFrameMsg.getPlayerFrameMsgs().addAll(frameManager.getPlayerFrameMsgMap().values());

//                PlayerFrameMsg robotFrameMsg = new PlayerFrameMsg();
//                robotFrameMsg.setPlayerId(100);
//                Vector2Msg vector2Msg = new Vector2Msg();
//                vector2Msg.setX(0);
//                vector2Msg.setY(0);
//                robotFrameMsg.setPosi(vector2Msg);
//
//                serverFrameMsg.getPlayerFrameMsgs().add(robotFrameMsg);

            for(RoomPlayer player:  room.getRoomPlayerMap().values() ){


                Session session = player.getPlayer().getSession();

                if(session == null || !session.isOpen()){
                    continue;
                }
                session.writeMessage(serverFrameMsg);
            }
        }


    }


    private void updateWaitBattlePlayer(){
        if(waitBattlePlayer == null){
            return;
        }

        if(!waitBattlePlayer.getSession().isOpen()){
            return;
        }

        waitBattlePlayer.sendTip("匹配中。。。。。");

    }

}
