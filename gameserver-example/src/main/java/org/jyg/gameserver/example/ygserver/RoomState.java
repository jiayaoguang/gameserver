package org.jyg.gameserver.example.ygserver;

/**
 * create by jiayaoguang on 2022/10/1
 */
public enum RoomState {

    WAIT_BATTLE(1),
    BATTLE(2),

    END(3),





    ;
    int state;


    RoomState(int state) {
        this.state = state;
    }
}
