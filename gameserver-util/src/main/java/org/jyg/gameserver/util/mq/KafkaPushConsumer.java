package org.jyg.gameserver.util.mq;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.jyg.gameserver.core.consumer.MQPushConsumer;
import org.jyg.gameserver.core.util.MQConst;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Future;

public class KafkaPushConsumer extends MQPushConsumer {


    private final KafkaProducer<String, byte[]> kafkaProducer;

    public KafkaPushConsumer() {

        // 1. 创建用于连接Kafka的Properties配置
        Properties props = new Properties();
        props.put("bootstrap.servers", "127.0.0.1:9092");
//        props.put("acks", "all");

        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());


        kafkaProducer = new KafkaProducer<>(props);

    }



    public void doStart(){
        super.doStart();

    }


    public void pushMsg(byte[] bytes){
        ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(MQConst.MQ_TOPIC_SEND, "0", bytes);
        Future<RecordMetadata> send = kafkaProducer.send(record);
    }

}
