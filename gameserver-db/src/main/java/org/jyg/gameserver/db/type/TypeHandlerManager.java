package org.jyg.gameserver.db.type;


import java.util.HashMap;
import java.util.Map;

/**
 * create by jiayaoguang at 2021/5/17
 */
@Deprecated
public class TypeHandlerManager {

    private final Map<Class<?>, TypeHandler<?>> typeHandlerMap = new HashMap<>();

    public void register(TypeHandler<?> typeHandler){
        if(typeHandlerMap.containsKey(typeHandler.getBindClassType())){
            throw new RuntimeException("containsKey");
        }

        typeHandlerMap.put(typeHandler.getBindClassType() , typeHandler);
    }

}
