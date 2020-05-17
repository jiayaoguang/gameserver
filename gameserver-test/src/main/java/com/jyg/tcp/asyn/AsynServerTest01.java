package com.jyg.tcp.asyn;

import org.jyg.gameserver.core.processor.ProtoProcessor;
import org.jyg.gameserver.proto.p_sm_scene;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.startup.GameServerBootstarp;
import org.jyg.gameserver.core.util.AsynCallEvent;
import org.jyg.gameserver.core.util.CallBackEvent;


/**
 * Hello world!
 *
 */
public class AsynServerTest01
{
    public static void main( String[] args ) throws Exception{
        GameServerBootstarp bootstarp = new GameServerBootstarp();

		PingProcessor pingProcessor = new PingProcessor();
		System.out.println(pingProcessor.getProtoClassName());
        bootstarp.registerSocketEvent(1, pingProcessor);
        
		bootstarp.addTcpService(8080);
        
        bootstarp.start();
    }

	public static class PingProcessor extends ProtoProcessor<p_sm_scene.p_sm_scene_request_ping> {

		public PingProcessor() {
			super(p_sm_scene.p_sm_scene_request_ping.getDefaultInstance());
		}

		@Override
		public void process(Session session, p_sm_scene.p_sm_scene_request_ping msg) {
			System.out.println("step 1 : ok , i see ping , will exec asyn event ,current thread : "+ Thread.currentThread().getName());
			getEventDispatcher().getSingleThreadExecutorManager(session).execute(new TestAsynCallEvent(),
			new TestCallBackEvent());
		}

		class TestAsynCallEvent implements AsynCallEvent<String>{
			@Override
			public String execute() {
				System.out.println("step 2 :asyn send data ,current thread : "+ Thread.currentThread().getName());
				return "asyn call , he say \" HELLO WORLD \"";
			}
		}

		class TestCallBackEvent extends CallBackEvent {

			@Override
			public void execte(Object data) {
				System.out.println("step 3 :receive data : "+ data + " ,event call back exec ,current thread : "+ Thread.currentThread().getName());
			}
		}

	}
}
