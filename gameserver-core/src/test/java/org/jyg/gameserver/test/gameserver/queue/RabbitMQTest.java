//package org.jyg.gameserver.test.gameserver.queue;
//
//import com.rabbitmq.client.*;
//import org.junit.Test;
//
//import java.io.IOException;
//import java.util.concurrent.TimeoutException;
//
///**
// * create by jiayaoguang on 2020/6/7
// */
//public class RabbitMQTest {
//    private static final String QUEUE_NAME="test_queue_02";
//    @Test
//    public void testMQproduct() throws IOException, TimeoutException {
//        //创建连接工厂
//        ConnectionFactory factory = new ConnectionFactory();
//        //设置RabbitMQ相关信息
//        factory.setHost("localhost");
//        //factory.setUsername("lp");
//        //factory.setPassword("");
//        // factory.setPort(2088);
//        //创建一个新的连接
//        Connection connection = factory.newConnection();
//        //创建一个通道
//        Channel channel = connection.createChannel();
//        //  声明一个队列        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
//        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
//        String message = "Hello World!";
//        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
//        System.out.println(" [x] Sent '" + message + "'");
//    }
//
//
//    public void testMQConsumer() throws IOException, TimeoutException {
//        // 创建连接工厂
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("localhost");
//        Connection connection = factory.newConnection();
//        Channel channel = connection.createChannel();
//
//        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
//        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
//        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//            String message = new String(delivery.getBody(), "UTF-8");
//            System.out.println(" [x] Received '" + message + "'");
//        };
//        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
//    }
//
//
//}
