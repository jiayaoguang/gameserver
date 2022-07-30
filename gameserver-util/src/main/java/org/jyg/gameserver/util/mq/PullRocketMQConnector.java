package org.jyg.gameserver.util.mq;

import cn.hutool.core.collection.CollectionUtil;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.jyg.gameserver.core.net.PullMQConnector;
import org.jyg.gameserver.core.util.MQConst;

import java.util.List;

/**
 * 主动从mq拉去消息
 */
public class PullRocketMQConnector extends PullMQConnector {

    public static final String CONSUMER_GROUP = "please_rename_unique_group_name_4";
    public static final String DEFAULT_NAMESRVADDR = "127.0.0.1:9876";
//    public static final String TOPIC = "TopicTest";

    private final DefaultLitePullConsumer consumer;



    public PullRocketMQConnector(org.jyg.gameserver.core.util.Context context , int mqPushConsumerId,String topic) {
        super(context , mqPushConsumerId);

        /*
         * Instantiate with specified consumer group name.
         */

        consumer = new DefaultLitePullConsumer(CONSUMER_GROUP);

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
            e.printStackTrace();
            throw new RuntimeException();
        }


    }


    @Override
    public void start(){
        /*
         *  Launch the consumer instance.
         */
        try {
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        super.start();
    }

    @Override
    protected int pollMQMsg() {

        List<MessageExt> messageExts = consumer.poll();
        if(CollectionUtil.isEmpty(messageExts)){
            return 0;
        }

        for(MessageExt messageExt : messageExts){
            publicToDefault(messageExt.getTopic(), messageExt.getMsgId(), messageExt.getBody());
        }

        return messageExts.size();
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
