//package org.jyg.gameserver.test;
//
//import cn.hutool.http.server.HttpServerRequest;
//import io.vertx.core.AbstractVerticle;
//import io.vertx.core.Handler;
//import io.vertx.core.Verticle;
//
///**
// * create by jiayaoguang on 2022/10/23
// */
//import io.vertx.core.Vertx;
//import io.vertx.core.json.Json;
//import io.vertx.core.net.NetSocket;
//import io.vertx.ext.web.Router;
//import io.vertx.ext.web.RoutingContext;
//
//public class VertxEcho {
//
//    private static int numberOfConnections = 0;
//
//    public static void main(String[] args) {
//        Vertx vertx = Vertx.vertx();
//
//        vertx.createNetServer()
//                .connectHandler(VertxEcho::handleNewClient)
//                .listen(3000);
//
//        vertx.setPeriodic(5000, id -> System.out.println(howMany()));
//
//
//        Router router = Router.router(vertx);
//
//        router.get("/*").handler((Handler<RoutingContext>) o -> {
//                    o.response()
//                            .putHeader("content-type", "application/json; charset=utf-8")
//                            .end("404 not found : " + Thread.currentThread().getName());
//                }
//
//        );
//
//        router.get("/index").handler((Handler<RoutingContext>) o -> {
//                    o.response()
//                            .putHeader("content-type", "application/json; charset=utf-8")
//                            .end("hello world");
//                }
//
//        );
//
//        vertx.createHttpServer()
//                .requestHandler(router)
//                .listen(8080);
//    }
//
//    private static void handleNewClient(NetSocket socket) {
//        numberOfConnections++;
//        socket.handler(buffer -> {
//            socket.write(buffer);
//            if (buffer.toString().endsWith("/quit\n")) {
//                socket.close();
//            }
//        });
//        socket.closeHandler(v -> numberOfConnections--);
//    }
//
//    private static String howMany() {
//        return "We now have " + numberOfConnections + " connections";
//    }
//}
//
