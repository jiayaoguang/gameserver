##   <font color=#f1986d size=3>游戏服务器</font>
![Java CI](https://github.com/jiayaoguang/gameserver/workflows/Java%20CI/badge.svg)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
![Maven](https://img.shields.io/maven-central/v/com.github.jiayaoguang/gameserver-core.svg)
------
Maven 

    <dependency>
        <groupId>com.github.jiayaoguang</groupId>
        <artifactId>gameserver-core</artifactId>
        <version>0.0.3</version>
    </dependency>

## 简介
* 这是一个通用游戏服务器框架
* 服务器以consumer 为运行单位,consumer 可以本地运行和调用,也可远程调用
* consumer可以在单独的线程运行，也可以多个consumer运行在一个线程

## Features
* 消息支持 protobuf、json、自定义 的数据类型，支持压缩、加密操作
* 网页http 请求
* 轻量级远程调用、依赖注入
* 不重启更新
* mysql对象关系映射及异步存储
* 定时事件
* 消息转发代理
* ip白名单



[服务器示例项目](https://github.com/jiayaoguang/gameserver/tree/main/gameserver-example) |
[客户端示例项目](https://github.com/jiayaoguang/gameclient)
	


构建处理 protoBuf数据的服务器例子:

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
    
            @Override
            public void process(Session session, p_sm_scene_request_ping msg) {
                System.out.println("ok , i see ping");
                session.writeMessage(p_test.p_scene_sm_response_pong.newBuilder());
            }
        }
    }

http例子代码:

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
	


