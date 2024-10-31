package org.jyg.gameserver.util.mq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.jyg.gameserver.core.net.MQConnector;
import org.jyg.gameserver.core.util.GameContext;

public class RocketMQConnector extends MQConnector {

    public static final String CONSUMER_GROUP = "please_rename_unique_group_name_4";
    public static final String DEFAULT_NAMESRVADDR = "127.0.0.1:9876";
//    public static final String TOPIC = "TopicTest";

    private final DefaultMQPushConsumer consumer;


    public RocketMQConnector(GameContext gameContext, int mqPushConsumerId, String topic) {
        super(gameContext, mqPushConsumerId);

        /*
         * Instantiate with specified consumer group name.
         */
        consumer = new DefaultMQPushConsumer(CONSUMER_GROUP);

        /*
         * Specify name server addresses.
         * <p/>
         *
         * Alternatively, you may specify name server addresses via exporting environmental variable: NAMESRV_ADDR
         * <pre>
         * {@code
         * consumer.setNamesrvAddr("name-server1-ip:9876;name-server2-ip:9876");
         * }
         * </pre>
         */
        // Uncomment the following line while debugging, namesrvAddr should be set to your local address
        consumer.setNamesrvAddr(DEFAULT_NAMESRVADDR);

        /*
         * Specify where to start in case the specific consumer group is a brand-new one.
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);

        /*
         * Subscribe one more topic to consume.
         */
        try {
            consumer.subscribe(topic, "*");
        } catch (MQClientException e) {
//            e.printStackTrace();
            throw new RuntimeException(e);
        }

        /*
         *  Register callback to execute on arrival of messages fetched from brokers.
         */
        consumer.registerMessageListener((MessageListenerConcurrently) (msg, mqContext) -> {

            for(MessageExt messageExt : msg){
                publicToDefault(messageExt.getTopic(),  messageExt.getMsgId(),messageExt.getBody() );
            }

            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });



    }


    public void start(){
        /*
         *  Launch the consumer instance.
         */
        try {
            consumer.start();
        } catch (MQClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        consumer.shutdown();
    }


//    @Override
//    public void stop(){
//        super.stop();
//
//    }





}
