package org.jyg.gameserver.core.session;

/**
 * create by jiayaoguang on 2022/7/24
 */
public enum EnumSessionType {

    NORMAL_CLIENT(0),

    SERVER(1),

    ;

    public final int type;
    EnumSessionType(int type){
        this.type = type;
    }

}
