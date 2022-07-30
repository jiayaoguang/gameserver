package org.jyg.gameserver.core.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * create by jiayaoguang on 2022/7/30
 */
public class RabbitMQPushConsumer extends MQPushConsumer {



    private final static String EXCHANGE_TYPE = "topic"; //要使用的exchange的名称
    private final static String EXCHANGE_ROUTING_KEY = "my_routing_key.#"; //exchange使用的routing-key

    private Channel channel;

    private Connection connection;

    private final String queueName;

    private final String exchangeName;




    //发送消息
    private String routing_key = "my_routing_key.key1";  //发送消息使用的routing-key

    public RabbitMQPushConsumer(String queueName, String exchangeName) {
        this.queueName = queueName;
        this.exchangeName = exchangeName;
    }


    @Override
    public void doStart(){
        super.doStart();

        createChannel();

    }


    private void tryCloseChannel(){
        if(connection != null){
            try {
                connection.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        if(channel != null){
            try {
                channel.close();
            } catch (IOException | TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void createChannel(){

        tryCloseChannel();

        //创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1"); //设置rabbitmq-server的地址
        connectionFactory.setPort(5672);  //使用的端口号
        connectionFactory.setVirtualHost("/");  //使用的虚拟主机

        //由连接工厂创建连接
        try {
            connection = connectionFactory.newConnection();
            //通过连接创建信道
            channel = connection.createChannel();

            //通过信道声明一个exchange，若已存在则直接使用，不存在会自动创建
            //参数：name、type、是否支持持久化、此交换机没有绑定一个queue时是否自动删除、是否只在rabbitmq内部使用此交换机、此交换机的其它参数（map）
            channel.exchangeDeclare(exchangeName, EXCHANGE_TYPE, true, false, false, null);

            //通过信道声明一个queue，如果此队列已存在，则直接使用；如果不存在，会自动创建
            //参数：name、是否支持持久化、是否是排它的、是否支持自动删除、其他参数（map）
            channel.queueDeclare(queueName, true, false, false, null);

            //将queue绑定至某个exchange。一个exchange可以绑定多个queue
            channel.queueBind(queueName, exchangeName, EXCHANGE_ROUTING_KEY);


        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doStop() {
        tryCloseChannel();
    }


    @Override
    protected void pushMsg(byte[] bytes) throws IOException, TimeoutException {


        channel.basicPublish(exchangeName,routing_key,null,bytes); //消息是byte[]，可以传递所有类型（转换为byte[]），不局限于字符串
        System.out.println("send rabbitmq message , len："+bytes.length);

    }






}
