##   <font color=#f1986d size=3>游戏服务器</font>
![Java CI](https://github.com/jiayaoguang/gameserver/workflows/Java%20CI/badge.svg)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
![Maven](https://img.shields.io/maven-central/v/io.github.jiayaoguang/gameserver-core.svg)
------
Maven

    <dependency>
        <groupId>io.github.jiayaoguang</groupId>
        <artifactId>gameserver-core</artifactId>
        <version>0.1.2</version>
    </dependency>

### [english introduction](https://github.com/jiayaoguang/gameserver/blob/main/README_EN.md)

## 简介
* 这是一个通用游戏服务器框架
* 服务器以consumer 为运行单位,consumer 可以本地运行和调用,也可远程调用
* consumer可以在单独的线程运行，也可以多个consumer运行在一个线程
* 不同的consumer通信支持远程方法调用并异步返回，也可以通过发布事件的方法通信

## Features
* 与客户端的tcp通信格式是 int(消息体字节数组长度+4) int(消息id) byte[](消息体字节数组)
* 消息体格式支持json protobuf 自定义格式 protobuf、json、自定义 的数据类型，支持压缩、加密操作
* 支持 http tcp udp websocket 网络协议
* 轻量级远程调用、依赖注入
* 不重启更新
* mysql对象关系映射及异步存储
* 定时事件
* 消息转发代理,添加消息转发代理服务器无需修改代码，只需让客户端改为连接转发代理服务器即可
* 消息传递支持 MQ,socket , websocket
* 远程方法调用  [example](https://github.com/jiayaoguang/gameserver/blob/main/gameserver-test/src/main/java/org/jyg/gameserver/test/invoke/InvokeMethodHttpServerDemo01.java) , 同步非阻塞调用 [example](https://github.com/jiayaoguang/gameserver/blob/main/gameserver-test/src/main/java/org/jyg/gameserver/test/invoke/SyncInvokeMethodServerDemo01.java)



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

远程调用:
    

    //服务器处理逻辑
    public static class PlusManager {
        @RemoteMethod(uname = "plus")
        public int plus(int a, int b) {
            Logs.CONSUMER.info("plus : {}", (a + b));

            return a + b;
        }
    }


    //客户端代理接口
    public static interface PlusManagerProxy {

        @InvokeRemoteMethod(uname = "plus", targetConsumerId = 10086)
        public InvokeRemoteResultFuture<Integer> plus(int a, int b);

    }

    //客户端创建调用代理
    PlusManagerProxy beInvokeConsumerProxy = consumerThreadStartEvent.getGameConsumer().getRemoteMethodInvokeManager().createRemoteMethodProxy(PlusManagerProxy.class);

    //客户端发起远程调用
    InvokeRemoteResultFuture<Integer> resultFuture = beInvokeConsumerProxy.plus(100, 200);


    //客户端设置获取结果后的处理逻辑
    resultFuture.setResultHandler(new ResultHandler<Integer>() {
        @Override
        public void call(int eventId, Integer result) {
            Logs.DEFAULT_LOGGER.info("get plus result : " + result);
        }
    });

[远程调用完整代码](https://github.com/jiayaoguang/gameserver/blob/main/gameserver-test/src/main/java/org/jyg/gameserver/test/invoke/ProxyInvokeMethodFutureServerDemo01.java)

更多例子请在 [test](https://github.com/jiayaoguang/gameserver/tree/main/gameserver-test/src/main/java/org/jyg/gameserver/test) 包查看

----------
## License
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) Copyright (C) Apache Software Foundation
	


