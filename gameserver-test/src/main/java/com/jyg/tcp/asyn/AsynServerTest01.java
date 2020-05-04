package com.jyg.tcp.asyn;

import com.jyg.processor.ProtoProcessor;
import com.jyg.proto.p_sm_scene;
import com.jyg.proto.p_test.p_scene_sm_chat;
import com.jyg.proto.p_test.p_scene_sm_response_pong;
import com.jyg.proto.p_test.p_sm_scene_chat;
import com.jyg.proto.p_test.p_sm_scene_request_ping;
import com.jyg.session.Session;
import com.jyg.startup.GameServerBootstarp;
import com.jyg.util.AsynCallEvent;
import com.jyg.util.CallBackEvent;


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
			getEventDispatcher().getExecutorManager().execute(new TestAsynCallEvent(),
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
