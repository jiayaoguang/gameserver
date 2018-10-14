## 分布式游戏服务器框架 <font color=#f1986d size=3>更新中...</font>
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)
------
* core项目-主要的网络库
* auth项目-登录服
* sm项目-场景服管理
* scene项目-场景服
* proto项目-传送的相关协议
	
服务器主要采用了netty4.1.19.Final框架处理socket连接,
使用protobuf3.5.1作为发送消息序列化格式,
使用disruptor3.3.2框架去做端口消息的多线程同步

能够处理http，protobuf类型的数据

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
    
    bootstarp.addService(new HttpService(8080));
    bootstarp.start();

更多例子请在[test](https://github.com/jiayaoguang/gameserver/tree/master/gameserver-test/src/main/java/com/jyg/test01)包查看
    
----------
## License
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) Copyright (C) Apache Software Foundation
	


