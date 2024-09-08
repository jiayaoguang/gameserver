##   <font color=#f1986d size=3>gameserver</font>
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

## Introduction

* This is a universal game server framework
* The server runs on a consumer basis, which can be run and called locally or remotely
* Consumers can run on a separate thread, or multiple consumers can run on a single thread
* Different consumer communication supports remote method calls and asynchronous returns, as well as communication through methods that publish events


## Features

* The TCP communication format with the client is int (length of message body byte array+4) int (message ID) byte [] (message body byte array)
* Message support for protobuf, JSON, custom data types, compression, encryption operations
* support HTTP http tcp udp websocket protocol
* Lightweight remote invocation, dependency injection
* Do not restart updates
* MySQL Object Relationship Mapping and Asynchronous Storage
*Timed event
* Message forwarding proxy, adding a message forwarding proxy server does not require modifying the code, just changing the client to connect to the forwarding proxy server is enough
* Message passing supports MQ, socket, and websocket
* Remote method call , [example](https://github.com/jiayaoguang/gameserver/blob/main/gameserver-test/src/main/java/org/jyg/gameserver/test/invoke/InvokeMethodHttpServerDemo01.java) , Synchronous non blocking call
[example](https://github.com/jiayaoguang/gameserver/blob/main/gameserver-test/src/main/java/org/jyg/gameserver/test/invoke/SyncInvokeMethodServerDemo01.java)




[server example](https://github.com/jiayaoguang/gameserver/tree/main/gameserver-example) |
[unity client example](https://github.com/jiayaoguang/gameclient)



deal protoBuf example:

    public class PingServer {
        public static void main(String[] args) throws Exception {
            GameServerBootstrap bootstarp = new GameServerBootstrap();
            bootstarp.registerSocketEvent(101, new PingProcessor());
            bootstarp.registerSendEventIdByProto(102, p_test.p_scene_sm_response_pong.class);
            //add tcp port
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

http example:

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

more example in [test](https://github.com/jiayaoguang/gameserver/tree/master/gameserver-test/src/main/java/org/jyg/gameserver/test)

----------
## License
[Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) Copyright (C) Apache Software Foundation
	


