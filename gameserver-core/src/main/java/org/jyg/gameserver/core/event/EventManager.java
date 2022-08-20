package org.jyg.gameserver.core.event;

import org.jyg.gameserver.core.manager.Lifecycle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager implements Lifecycle {

    private Map<Class<? extends Event> , List<Event<?,?>>> eventListMap = new HashMap<>(128 , 0.5f);


    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    public void triggerEvent(Class<? extends Event> eventType){
        triggerEvent(eventType,null,null);
    }

    public void triggerEvent(Class<? extends Event> eventType , Object param1){
        triggerEvent(eventType,param1,null);
    }

    public void triggerEvent(Class<? extends Event> eventType , Object param1 , Object param2){

        List<Event<?,?>> eventList = eventListMap.get(eventType);
        if(eventList == null || eventList.isEmpty()){
            return;
        }

        if(eventList.size() == 1){
            try {
                Event event = eventList.get(0);
                event.onEvent(param1 , param2);
            }catch (Exception e){
                e.printStackTrace();
            }
            return;
        }


        for(Event event : eventList){
            try {
                event.onEvent(param1 , param2);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }





    public void addEvent(Event event){
        List<Event<?,?>> eventList = eventListMap.get(event.getClass());
        if(eventList == null){
            eventList = new ArrayList<>();
            Class<? extends Event> eventClass = event.getClass();
            eventListMap.put(eventClass , eventList);
        }
        eventList.add(event);
    }


    public void addHeadEvent(Event event){

        List<Event<?,?>> newEventList = new ArrayList<>();
        newEventList.add(event);
        Class<Event> eventClass = (Class<Event>) event.getClass();

        List<Event<?,?>> eventList = eventListMap.get(eventClass);
        if(eventList != null){
            newEventList.addAll(eventList);
        }

        eventListMap.put(eventClass , newEventList);
    }

}
