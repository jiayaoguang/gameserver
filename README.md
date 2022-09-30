## 游戏服务器  <font color=#f1986d size=3>更新中...</font>
![Java CI](https://github.com/jiayaoguang/gameserver/workflows/Java%20CI/badge.svg)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
------
Maven 

    <dependency>
        <groupId>com.github.jiayaoguang</groupId>
        <artifactId>gameserver-core</artifactId>
        <version>0.0.2</version>
    </dependency>


* core模块- 主要的网络库
* db模块- sql数据库持久化异步封装
* example模块- 一个完整的例子
* 其他模块- 测试用

[服务器示例项目](https://github.com/jiayaoguang/gameserver/tree/main/gameserver-example)
[客户端示例项目](https://github.com/jiayaoguang/gameclient)
	
服务器主要采用了netty4.1.19.Final框架处理socket连接,
使用protobuf3.5.1作为发送消息序列化格式,
使用disruptor3.3.2框架去做端口消息的多线程同步

能够处理http，protobuf , json/自定义字节数组 类型的数据

构建处理 protoBuf数据的服务器例子:
>
    public class PingServer {
        public static void main(String[] args) throws Exception {
            GameServerBootstrap bootstarp = new GameServerBootstrap();
            bootstarp.registerSocketEvent(101, new PingProcessor());
            bootstarp.registerSendEventIdByProto(102, p_test.p_scene_sm_response_pong.class);
            //开端口服务
            bootstarp.addTcpConnector(8080);
            bootstarp.start();
        }
    
        public static class PingProcessor extends ProtoProcessor<p_sm_scene_request_ping> {
    
            public PingProcessor() {
                super(p_sm_scene_request_ping.getDefaultInstance());
            }
    
            @Override
            public void process(Session session, p_sm_scene_request_ping msg) {
                System.out.println("ok , i see ping");
                session.writeMessage(p_test.p_scene_sm_response_pong.newBuilder());
            }
        }
    }

http例子代码:
>
    public class HttpServerDemo01
    {
        public static void main ( String[] args ) throws Exception 
        {
        	
        	
        	GameServerBootstrap bootstarp = new GameServerBootstrap();
            
            bootstarp.addHttpProcessor(new HttpProcessor("/index") {
    			
    			@Override
    			public void service(Request request, Response response) {
    
    				response.writeAndFlush( "<html><head></head><body><div align='center'><h1>Hello world!</h1></div><body></html>" );
    				
    			}
    		} );
            
            bootstarp.addHttpConnector(8080);
            
            bootstarp.start();
        }
    }
    
更多例子请在[test](https://github.com/jiayaoguang/gameserver/tree/master/gameserver-test/src/main/java/org/jyg/gameserver/test)包查看

----------
## License
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) Copyright (C) Apache Software Foundation
	


