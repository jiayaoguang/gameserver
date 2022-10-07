package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.db.ConsumerDBManager;
import org.jyg.gameserver.example.ygserver.msg.*;

public class EnterRoomProcessor extends ByteMsgObjProcessor<CSEnterRoomMsg> {

    public EnterRoomProcessor() {
        super(CSEnterRoomMsg.class);
    }

    @Override
    public void process(Session session, EventData<CSEnterRoomMsg> event) {


        RoomManager roomManager = this.getGameConsumer().getInstanceManager().getInstance(RoomManager.class);

        Player player = session.getSessionObject();


        if (roomManager.getWaitBattlePlayerMap().containsKey(player.getPlayerDB().getId())) {
//            roomManager.setWaitBattlePlayer(player);
            player.sendTip("正在匹配,请勿重复点击");
            return;
        }


        Room room = roomManager.getRoom(player.getPlayerDB().getId());

        if (room != null) {
            RoomPlayer roomPlayer = room.getRoomPlayerMap().get(player.getPlayerDB().getId());
            roomPlayer.setPlayer(player);
            roomManager.sendRoomInfoMsg( roomPlayer);
            return;
        }

        player.getPlayerDB().setLastMatchTime(System.currentTimeMillis());

        ConsumerDBManager consumerDBManager = getGameConsumer().getInstanceManager().getInstance(ConsumerDBManager.class);
        consumerDBManager.update(player.getPlayerDB());

        room = roomManager.tryEnterRom(player);
        if(room != null){
            RoomPlayer roomPlayer = room.getRoomPlayerMap().get(player.getPlayerDB().getId());
            roomPlayer.setPlayer(player);
            roomManager.sendRoomInfoMsg( roomPlayer);
        }else {
            //没有合适的房间,加入匹配队列
            roomManager.getWaitBattlePlayerMap().put(player.getPlayerDB().getId(), player);
            player.sendTip("正在匹配");
        }

//        roomManager.get





//        Player player2 = roomManager.getWaitBattlePlayer();
//        room = roomManager.enterRoom(player, player2);


//        RoomPlayer roomPlayer1 = room.getRoomPlayerMap().get(player.getPlayerDB().getId());
//        roomManager.sendRoomInfoMsg(roomPlayer1);
//        RoomPlayer roomPlayer2 = room.getRoomPlayerMap().get(player2.getPlayerDB().getId());
//
//        roomManager.sendRoomInfoMsg(roomPlayer2);


    }


}
