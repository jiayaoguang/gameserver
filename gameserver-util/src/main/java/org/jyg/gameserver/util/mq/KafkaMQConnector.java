package org.jyg.gameserver.util.mq;

import cn.hutool.core.collection.CollectionUtil;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.jyg.gameserver.core.net.PullMQConnector;
import org.jyg.gameserver.core.util.Context;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class KafkaMQConnector extends PullMQConnector {


    private final KafkaConsumer<String, byte[]> kafkaConsumer;

    private final Duration pollDuration = Duration.ofMillis(100);


    public KafkaMQConnector(Context context , int mqPushConsumerId, List<String> topics) {
        super(context , mqPushConsumerId);

        // 1. 创建用于连接Kafka的Properties配置

        Properties props = new Properties();
        props.put("bootstrap.servers", "127.0.0.1:9092");
//        props.put("acks", "all");
        props.put("key.serializer", StringDeserializer.class.getName());
        props.put("value.serializer", org.apache.kafka.common.serialization.ByteArraySerializer.class.getName());

        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", org.apache.kafka.common.serialization.ByteArrayDeserializer.class.getName());

        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 10);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);

        props.put("num.partitions", 1);


        //消费组，kafka根据分组名称判断是不是同一组消费者，同一组消费者去消费一个主题的数据时，数据将在这一组消费者上面轮询
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test");

        kafkaConsumer = new KafkaConsumer<>(props);
        kafkaConsumer.subscribe(topics);


    }




//    @Override
//    public void stop(){
//        super.stop();
//
//    }



    @Override
    protected int pollMQMsg() {

        ConsumerRecords<String, byte[]> records = kafkaConsumer.poll(pollDuration);
        if (CollectionUtil.isEmpty(records)) {
            return 0;
        }
        for (ConsumerRecord<String, byte[]> record : records) {
            byte[] msg = record.value();

            publicToDefault(record.topic() , record.key() , msg);
        }

        kafkaConsumer.commitAsync();

        return records.count();
    }





}
