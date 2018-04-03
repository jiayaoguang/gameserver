package org.gameserver.auth;

import com.jyg.net.EventDispatcher;
import com.jyg.net.HttpService;
import com.jyg.net.ProtoProcessor;
import com.jyg.net.ProtoResponse;
import com.jyg.proto.p_sm_scene.p_scene_sm_response_pong;
import com.jyg.proto.p_sm_scene.p_sm_scene_request_ping;
import com.jyg.startup.InnerClient;
import com.jyg.startup.GameServerBootstarp;
import com.jyg.timer.Timer;
import com.jyg.timer.TimerCallBack;

import io.netty.channel.Channel;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main ( String[] args ) throws Exception 
    {
    	
    	GameServerBootstarp bootstarp = new GameServerBootstarp();
        
        bootstarp.registerHttpEvent("/index", new IndexHttpProcessor());
        
        bootstarp.registerHttpEvent("/login", new LoginHttpProcessor());
        
        bootstarp.registerHttpEvent("/loginhtml", new LoginHtmlHttpProcessor());
        
        bootstarp.addService(new HttpService(8080,true));
        
        bootstarp.start();
    }
}
