## 游戏服务器  <font color=#f1986d size=3>更新中...</font>
![Java CI](https://github.com/jiayaoguang/gameserver/workflows/Java%20CI/badge.svg)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
------
* core模块-主要的网络库
* auth模块-登录服
* center模块-场景服管理
* scene模块-场景服
* proto模块-传送的相关协议
	
服务器主要采用了netty4.1.19.Final框架处理socket连接,
使用protobuf3.5.1作为发送消息序列化格式,
使用disruptor3.3.2框架去做端口消息的多线程同步

能够处理http，protobuf类型的数据

构建处理 protoBuf数据的服务器例子:
>
     public class PingServer {
         public static void main(String[] args) throws Exception {
             GameServerBootstarp bootstarp = new GameServerBootstarp();
             bootstarp.registerSocketEvent(1, new PingProcessor());
             bootstarp.registerSendEventIdByProto(2, p_scene_sm_response_pong.class);
             //开端口服务
             bootstarp.addTcpService(8080);
             bootstarp.start();
         }
     
         public static class PingProcessor extends ProtoProcessor<p_sm_scene_request_ping> {
     
             public PingProcessor() {
                 super(p_sm_scene_request_ping.getDefaultInstance());
             }
     
             @Override
             public void processProtoMessage(p_sm_scene_request_ping msg, ProtoResponse response) {
                 System.out.println("ok , i see ping");
                 response.writeMsg(p_scene_sm_response_pong.newBuilder());
             }
         }
     }

http例子代码:
>
    GameServerBootstarp bootstarp = new GameServerBootstarp();
    
    bootstarp.registerHttpEvent("/index", new HttpProcessor() {
    
        @Override
        public void service(Request request, Response response) {
            response.writeAndFlush( 
                "<html><head></head><body><div align='center'><h1>Hello world!</h1></div><body></html>"
                 ); 
        }
        
    } );
    
    bootstarp.addHttpService(8080);
    bootstarp.start();

更多例子请在[test](https://github.com/jiayaoguang/gameserver/tree/master/gameserver-test/src/main/java/com/jyg/test01)包查看
    
----------
## License
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) Copyright (C) Apache Software Foundation
	


