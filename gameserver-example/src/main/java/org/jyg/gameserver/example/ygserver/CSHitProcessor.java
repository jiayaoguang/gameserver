package org.jyg.gameserver.example.ygserver;

import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.processor.ByteMsgObjProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.example.ygserver.msg.CSHitMsg;
import org.jyg.gameserver.example.ygserver.msg.SCHitMsg;

public class CSHitProcessor extends ByteMsgObjProcessor<CSHitMsg> {

    public CSHitProcessor() {
        super(CSHitMsg.class);
    }

    @Override
    public void process(Session session, EventData<CSHitMsg> event) {


        Player player = session.getSessionObject();

        Room room = getGameConsumer().getInstanceManager().getInstance(RoomManager.class).getRoom(player.getPlayerDB().getId());

        RoomPlayer roomPlayer = room.getRoomPlayerMap().get(player.getPlayerDB().getId());
        if(roomPlayer == null){
            return;
        }
        roomPlayer.setScore(roomPlayer.getScore() + 1);
        roomPlayer.setHp(roomPlayer.getHp() + 1);


        RoomPlayer hitRoomPlayer = room.getRoomPlayerMap().get(event.getData().getHitTargetId());
        SCHitMsg scHitMsg = new SCHitMsg();
        if(hitRoomPlayer != null){
            hitRoomPlayer.setHp(hitRoomPlayer.getHp() - 2);
            scHitMsg.setHitTargetId(hitRoomPlayer.getPlayer().getPlayerDB().getId());
            scHitMsg.setTargetHp(hitRoomPlayer.getHp());
        }


        scHitMsg.setScore(roomPlayer.getScore());
        scHitMsg.setAddScore(1);
        scHitMsg.setAttackPlayerId(player.getPlayerDB().getId());
        scHitMsg.setAttackPlayerHp(roomPlayer.getHp());


        room.broadcast(scHitMsg);

    }
}
