package org.jyg.gameserver.example.ygserver;

import cn.hutool.core.util.RandomUtil;
import org.jyg.gameserver.core.consumer.GameConsumer;
import org.jyg.gameserver.core.event.ConnectEvent;
import org.jyg.gameserver.core.event.DisconnectEvent;
import org.jyg.gameserver.core.manager.Lifecycle;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.Logs;
import org.jyg.gameserver.example.ygserver.msg.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class RoomManager implements Lifecycle {
    private static final int ROOM_MAX_PAYER_NUM = 10;

    private static final int ROOM_MAX_SCORE_MOTION_NUM = 30;

    public static final float ROOM_PLAYER_MIN_SIZE = 10.0f;
    public static final float ROOM_PLAYER_MAX_SIZE = 30.0f;
    private final GameConsumer gameConsumer;



    private final Map<Long,RoomPlayer> battleRoomPlayerMap = new LinkedHashMap<>(1024, 0.5f);

    private long roomIdInc;
    /** key palyerDB id */
    private Map<Long,Player> waitBattlePlayerMap = new LinkedHashMap<>(1024, 0.5f);


    private final Map<Long,Room> battleRoomMap = new LinkedHashMap<>(1024, 0.5f);

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
        gameConsumer.getTimerManager().addUnlimitedTimer(TimeUnit.SECONDS.toMillis(3), this::updateWaitPlayers);

        gameConsumer.getTimerManager().addUnlimitedTimer(TimeUnit.SECONDS.toMillis(3), this::updateRoomMotion);

//        gameConsumer.getEventManager().addEvent(new DisconnectEvent((session , data)->{
//            if(waitBattlePlayer != null && session.getSessionId() == waitBattlePlayer.getSession().getSessionId()){
//                waitBattlePlayer = null;
//            }
//        }));


    }


    public void update(){


        Iterator<Map.Entry<Long,Room>> battleRoomMapIter = this.battleRoomMap.entrySet().iterator();

        for(Room room ;battleRoomMapIter.hasNext();){
            room = battleRoomMapIter.next().getValue();
            long now = System.currentTimeMillis();


            if(room.getEndTime() < now){
                for(RoomPlayer roomPlayer : room.getRoomPlayerMap().values()){
                    roomPlayer.getPlayer().getSession().writeMessage(new SCRoomEndMsg());

                    battleRoomPlayerMap.remove(roomPlayer.getPlayer().getPlayerDB().getId());

                }
                room.getRoomPlayerMap().clear();
//                resetRoom(room);

                room.getPlayerFrameMsgMap().clear();
                battleRoomMapIter.remove();
            }
        }

    }

    @Deprecated
    public void endRoom(Room room){
        for(RoomPlayer roomPlayer : room.getRoomPlayerMap().values()){
            roomPlayer.getPlayer().getSession().writeMessage(new SCRoomEndMsg());

            battleRoomPlayerMap.remove(roomPlayer.getPlayer().getPlayerDB().getId());

        }
        battleRoomMap.remove(room.getRoomId());
        room.getRoomPlayerMap().clear();
    }


    public Room tryEnterRom( Player player){

        Room findRoom = null;


        if(this.battleRoomPlayerMap.isEmpty()){
            return null;
        }

        for (Room room : this.battleRoomMap.values()){
            if( room.getRoomPlayerMap().size() < ROOM_MAX_PAYER_NUM){
                findRoom = room;
            }
        }

        if(findRoom == null){
            return null;
        }


        RoomPlayer roomPlayer1 = createOrGetRoomPlayer(findRoom , player);


        return findRoom;
    }



    private void enterRoom( Room room , Player player ){



        this.battleRoomMap.put(room.getRoomId() , room);


        {
            RoomPlayer roomPlayer = createOrGetRoomPlayer(room , player);
            Vector2Msg posi = new Vector2Msg( RandomUtil.randomInt(-100 , 100) , RandomUtil.randomInt(-100 , 100));
            roomPlayer.setPosi(posi);

            room.getRoomPlayerMap().put(player.getPlayerDB().getId() , roomPlayer);
//            Motion sysMotion = room.createMotion(roomPlayer1 , posi , 1);
        }



        waitBattlePlayerMap.remove(player.getPlayerDB().getId());




//        for(int i = 0;i< 10;i++){
//            Vector2Msg posi = new Vector2Msg( 0 , RandomUtil.randomInt(-90 , 90));
//            room.createSysMotion( posi , 0);
//        }

    }


    private void enterRoom(Room room ,  List<Player> players ){


        this.battleRoomMap.put(room.getRoomId() , room);



        for(Player player : players ){
            enterRoom(room , player);

        }


    }



    private RoomPlayer createOrGetRoomPlayer(Room room , Player player){
        RoomPlayer roomPlayer = room.getRoomPlayerMap().get(player.getPlayerDB().getId());
        if(roomPlayer == null){
            roomPlayer = new RoomPlayer();
            roomPlayer.setPlayer(player);
            roomPlayer.setPosi(new Vector2Msg());
            roomPlayer.setRoom(room);
            roomPlayer.setPlayerSize(ROOM_PLAYER_MIN_SIZE);
            room.getRoomPlayerMap().put(player.getPlayerDB().getId(),roomPlayer);
            this.battleRoomPlayerMap.put(player.getPlayerDB().getId(),roomPlayer);
        }
        roomPlayer.setPlayer(player);

        return roomPlayer;
    }


    @Override
    public void stop() {

    }




    private long allocateRoomId(){
        roomIdInc ++;
        return roomIdInc;
    }





    private void updateRoomFrame(){

        long start = System.currentTimeMillis();

        for( Room room : this.battleRoomMap.values() ){


            ServerFrameMsg serverFrameMsg = new ServerFrameMsg();

            serverFrameMsg.getPlayerFrameMsgs().addAll(room.getPlayerFrameMsgMap().values());

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

        long cost = System.currentTimeMillis() - start;

        if(cost > 10){
            Logs.DEFAULT_LOGGER.error("updateRoomFrame cost {}" , cost);
        }


    }


//    private void updateWaitBattlePlayer(){
//        if(waitBattlePlayer == null){
//            return;
//        }
//
//        if(!waitBattlePlayer.getSession().isOpen()){
//            return;
//        }
//
//        waitBattlePlayer.sendTip("匹配中。。。。。");
//
//    }


    public Map<Long, Player> getWaitBattlePlayerMap() {
        return waitBattlePlayerMap;
    }

    public void setWaitBattlePlayerMap(Map<Long, Player> waitBattlePlayerMap) {
        this.waitBattlePlayerMap = waitBattlePlayerMap;
    }




    public void sendRoomInfoMsg(  RoomPlayer roomPlayer) {
        Player player = roomPlayer.getPlayer();
        Session session = roomPlayer.getPlayer().getSession();

        SCEnterRoomMsg enterRoomMsg = new SCEnterRoomMsg();
        enterRoomMsg.setScore(roomPlayer.getScore());


        Room room = roomPlayer.getRoom();

        enterRoomMsg.wallMsgs.addAll(room.getWallMsgList());


        SCPlayerJoinMsg playerJoinMsg = new SCPlayerJoinMsg();

        PlayerInfoMsg myPlayerInfoMsg = new PlayerInfoMsg();
        myPlayerInfoMsg.setPlayerId(roomPlayer.getPlayer().getPlayerDB().getId());
        myPlayerInfoMsg.setPosi(roomPlayer.getPosi());
        myPlayerInfoMsg.setDir(roomPlayer.getDir());
        myPlayerInfoMsg.setName(roomPlayer.getPlayer().getPlayerDB().getName());
        myPlayerInfoMsg.setHp(roomPlayer.getHp());
        myPlayerInfoMsg.setPlayerSize(roomPlayer.getPlayerSize());
        playerJoinMsg.setPlayerInfoMsg(myPlayerInfoMsg);



        for (RoomPlayer other : room.getRoomPlayerMap().values()) {
            PlayerInfoMsg playerInfoMsg = new PlayerInfoMsg();
            playerInfoMsg.setPlayerId(other.getPlayer().getPlayerDB().getId());
            playerInfoMsg.setPosi(other.getPosi());
            playerInfoMsg.setDir(other.getDir());
            playerInfoMsg.setName(other.getPlayer().getPlayerDB().getName());
            playerInfoMsg.setHp(other.getHp());
            playerInfoMsg.setState(other.getState());
            playerInfoMsg.setPlayerSize(other.getPlayerSize());

            enterRoomMsg.getPlayerInfoMsgs().add(playerInfoMsg);

            if (player.getPlayerDB().getId() != other.getPlayer().getPlayerDB().getId()) {
                other.getPlayer().getSession().writeMessage(playerJoinMsg);
            }

            for (Motion motion : other.getMotionMap().values()) {
                enterRoomMsg.getMotionMsgs().add(room.createMotionMsg(other, motion));
            }

        }

        for (Motion motion : room.getSysMotionMap().values()) {
            enterRoomMsg.getMotionMsgs().add(room.createMotionMsg(0, motion));
        }


        session.writeMessage(enterRoomMsg);
    }


    public void updateWaitPlayers(){
        if(waitBattlePlayerMap.isEmpty()){
            return;
        }
        List<Player> waitPlayers = new ArrayList<>(waitBattlePlayerMap.values());

        waitPlayers.sort(new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                return Long.compare(o1.getPlayerDB().getLastMatchTime() , o2.getPlayerDB().getLastMatchTime());
            }
        });


        long now = System.currentTimeMillis();




        for( int i=0;i<waitPlayers.size();i++ ){
            Player player = waitPlayers.get(i);

            if(now >= player.getPlayerDB().getLastMatchTime()+ 8000L   ){

                Room waitRoom = new Room(allocateRoomId());
                for(;waitRoom.getRoomPlayerMap().size() < ROOM_MAX_PAYER_NUM && i < waitPlayers.size();i++){

                    player = waitPlayers.get(i);
                    waitBattlePlayerMap.remove(player.getPlayerDB().getId());
                    enterRoom(waitRoom , player);

                }

                for(RoomPlayer roomPlayer :waitRoom.getRoomPlayerMap().values()){
                    sendRoomInfoMsg(roomPlayer);
                }

//                endRoom(roz);


            }else {
                player.sendTip("正在匹配");
            }
        }

    }



    public void revive(RoomPlayer roomPlayer){
        Vector2Msg vector2Msg = new Vector2Msg(RandomUtil.randomInt(-100,100) , RandomUtil.randomInt(-100,100));
        roomPlayer.setPosi(vector2Msg);

//        roomPlayer.setDir(new Vector2Msg());
        roomPlayer.setState(0);
        PlayerFrameMsg playerFrameMsg = roomPlayer.getRoom().getPlayerFrameMsgMap().get(roomPlayer.getPlayer().getPlayerDB().getId());
        if(playerFrameMsg != null){
            playerFrameMsg.setPosi(vector2Msg);
            playerFrameMsg.setState(0);
        }
        roomPlayer.setPlayerSize(ROOM_PLAYER_MIN_SIZE);

        SCUpdatePlayerScoreMsg sendScoreMsg = new SCUpdatePlayerScoreMsg();
        sendScoreMsg.setPlayerId(roomPlayer.getPlayer().getPlayerDB().getId());
        sendScoreMsg.setScore(roomPlayer.getScore());
        sendScoreMsg.setPlayerSize(roomPlayer.getPlayerSize());
//        session.writeMessage(sendScoreMsg);
        roomPlayer.getRoom().broadcast(sendScoreMsg);

    }


    public int getPlayerSize(RoomManager roomManager){


        return 0;

    }


    public void updateRoomMotion(){
//        long now = System.currentTimeMillis();
        for(Room room : battleRoomMap.values()){


            if(room.getSysMotionMap().size() < ROOM_MAX_SCORE_MOTION_NUM){
                Motion motion = room.createScoreMotion();
                SCCreateMotionMsg sendMsg = new SCCreateMotionMsg();
                sendMsg.setMotionMsg(room.createMotionMsg(0,motion));
                room.broadcast(sendMsg );
            }
        }
    }


    @Deprecated
    public void checkEatRoomMotion(){
        long now = System.currentTimeMillis();

        for(Room room : battleRoomMap.values()){

            for(RoomPlayer roomPlayer : room.getRoomPlayerMap().values() ){

                for(Motion motion : room.getSysMotionMap().values() ){
                    if(motion.getType() != 3){
                        continue;
                    }




                }

            }

        }
    }


}
