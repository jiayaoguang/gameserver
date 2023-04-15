package org.jyg.gameserver.util.mq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.jyg.gameserver.core.consumer.MQPushGameConsumer;
import org.jyg.gameserver.core.util.AllUtil;
import org.jyg.gameserver.core.util.Logs;

public class RocketMQPushGameConsumer extends MQPushGameConsumer {

    public static final String PRODUCER_GROUP = "please_rename_unique_group_name";
    public static final String DEFAULT_NAMESRVADDR = "127.0.0.1:9876";
//    public static final String TOPIC = "TopicTest";
    public static final String TAG = "TagA";


    private final DefaultMQProducer producer;

//    private

    public final String topic;

    public RocketMQPushGameConsumer(String topic) {
        this.topic = topic;

        /*
         * Instantiate with a producer group name.
         */
        producer = new DefaultMQProducer(PRODUCER_GROUP);

        /*
         * Specify name server addresses.
         *
         * Alternatively, you may specify name server addresses via exporting environmental variable: NAMESRV_ADDR
         * <pre>
         * {@code
         *  producer.setNamesrvAddr("name-server1-ip:9876;name-server2-ip:9876");
         * }
         * </pre>
         */
        // Uncomment the following line while debugging, namesrvAddr should be set to your local address
//        producer.setNamesrvAddr(DEFAULT_NAMESRVADDR);

        /*
         * Launch the instance.
         */

        producer.setNamesrvAddr(DEFAULT_NAMESRVADDR);


    }


    public void doStart() {
        super.doStart();

        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }


    }


    public void pushMsg(byte[] bytes) {
        try {
            /*
             * Create a message instance, specifying topic, tag and message body.
             */
            Message msg = new Message(topic /* Topic */, TAG /* Tag */, bytes /* Message body */);

            /*
             * Call send message to deliver message to one of brokers.
             */
            //考虑改为 sendOneWay
            producer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    Logs.DEFAULT_LOGGER.info("send success , msgId : {} topic {}" , sendResult.getMsgId()  , msg.getTopic());
                }

                @Override
                public void onException(Throwable e) {
                    Logs.DEFAULT_LOGGER.info("pushMsg onException , fail " , e);
                }
            });
        } catch (MQClientException | RemotingException | InterruptedException e) {
            Logs.DEFAULT_LOGGER.error("pushMsg error " , e);
        }
        /*
         * There are different ways to send message, if you don't care about the send result,you can use this way
         * {@code
         * producer.sendOneway(msg);
         * }
         */

        /*
         * if you want to get the send result in a synchronize way, you can use this send method
         * {@code
         * SendResult sendResult = producer.send(msg);
         * System.out.printf("%s%n", sendResult);
         * }
         */

        /*
         * if you want to get the send result in a asynchronize way, you can use this send method
         * {@code
         *
         *  producer.send(msg, new SendCallback() {
         *  @Override
         *  public void onSuccess(SendResult sendResult) {
         *      // do something
         *  }
         *
         *  @Override
         *  public void onException(Throwable e) {
         *      // do something
         *  }
         *});
         *
         *}
         */

    }

}
