package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.manager.Lifecycle;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager implements Lifecycle {

    private Map<Class<? extends Event> , List<GameEventListener<? extends Event>>> eventListMap = new HashMap<>(128 , 0.5f);


    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }






    public void publishEvent(Event event){

        List<GameEventListener<? extends Event>> eventListeners = eventListMap.get(event.getClass());
        if(eventListeners == null || eventListeners.isEmpty()){
            return;
        }

        if(eventListeners.size() == 1){
            try {
                GameEventListener eventListener = eventListeners.get(0);
                eventListener.onEvent(event);
            }catch (Exception e){
                e.printStackTrace();
            }
            return;
        }


        for(GameEventListener eventListener : eventListeners){
            try {
                eventListener.onEvent(event);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }





    public void addEventListener(GameEventListener<? extends Event> eventListener){

        Type superClass = eventListener.getClass().getGenericInterfaces()[0];
        if (superClass instanceof Class<?>) { // sanity check, should never happen
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        }

        Type _type = ((ParameterizedType) superClass).getActualTypeArguments()[0];

        Class<? extends Event> eventClazz = (Class<? extends Event>) _type;

        addEventListener(eventClazz , eventListener);
    }


    public void addEventListener(Class<? extends Event> eventClazz , GameEventListener<? extends Event> eventListener){

        List<GameEventListener<? extends Event>> eventListenerList = eventListMap.get(eventClazz);
        if(eventListenerList == null){
            eventListenerList = new ArrayList<>();
            Class<? extends Event> eventClass = eventClazz;
            eventListMap.put(eventClass , eventListenerList);
        }
        eventListenerList.add(eventListener);
    }


    public void addHeadEventListener(Class<? extends Event> eventClazz , GameEventListener<? extends Event> eventListener){

        List<GameEventListener<? extends Event>> newEventList = new ArrayList<>();
        newEventList.add(eventListener);

        List<GameEventListener<? extends Event>> eventList = eventListMap.get(eventClazz);
        if(eventList != null){
            newEventList.addAll(eventList);
        }

        eventListMap.put(eventClazz , newEventList);
    }

}
