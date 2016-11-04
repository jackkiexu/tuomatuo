package com.lami.tuomatuo.mq.jafka.producer.async;

import com.lami.tuomatuo.mq.jafka.api.ProducerRequest;
import com.lami.tuomatuo.mq.jafka.message.ByteBufferMessageSet;
import com.lami.tuomatuo.mq.jafka.message.CompressionCodec;
import com.lami.tuomatuo.mq.jafka.message.Message;
import com.lami.tuomatuo.mq.jafka.producer.ProducerConfig;
import com.lami.tuomatuo.mq.jafka.producer.SyncProducer;
import com.lami.tuomatuo.mq.jafka.producer.serializer.Encoder;

import java.util.*;

/**
 * Created by xjk on 2016/11/4.
 */
public class DefaultEventHandler<T> implements EventHandler<T> {

    private CallbackHandler<T> callbackHandler;

    private Set<String> compressedTopics;

    private CompressionCodec codec;

    public DefaultEventHandler(ProducerConfig producerConfig, CallbackHandler<T> callbackHandler) {
        this.callbackHandler = callbackHandler;
        this.compressedTopics = new HashSet<String>(producerConfig.getCompressedTopics());
        this.codec = producerConfig.getCompressionCodec();
    }

    public void init(Properties properties) {

    }

    public void handle(List<QueueItem<T>> events, SyncProducer producer, Encoder<T> encoder) {
        List<QueueItem<T>> processedEvents = events;
        if(this.callbackHandler != null){
            processedEvents = this.callbackHandler.beforeSendingData(events);
        }
        send(collate(processedEvents, encoder), producer);
    }

    private void send(List<ProducerRequest> produces, SyncProducer syncProducer){
        if(produces.size() > 0){
            syncProducer.multiSend(produces);
        }
    }

    private List<ProducerRequest> collate(List<QueueItem<T>> events, Encoder<T> encoder){
        // TODO to be continue
        Map<String, Map<Integer, List<Message>>> topicPartitionData = new HashMap<String, Map<Integer, List<Message>>>();
        for(QueueItem<T> event : events){
            Map<Integer, List<Message>> partitionData = topicPartitionData.get(event.topic);
            if(partitionData == null){
                partitionData = new HashMap<Integer, List<Message>>();
                topicPartitionData.put(event.topic, partitionData);
            }
            List<Message> data = partitionData.get(event.partition);
            if(data == null){
                data = new ArrayList<Message>();
                partitionData.put(event.partition, data);
            }
            data.add(encoder.toMessage(event.data));
        }

        List<ProducerRequest> requests = new ArrayList<ProducerRequest>();
        for(Map.Entry<String, Map<Integer, List<Message>>> e : topicPartitionData.entrySet()){
            String topic = e.getKey();
            for(Map.Entry<Integer, List<Message>> pd : e.getValue().entrySet()){
                Integer partition = pd.getKey();
                requests.add(new ProducerRequest(topic, partition, convert(topic, pd.getValue())));
            }
        }

        return requests;
    }

    private ByteBufferMessageSet convert(String topic, List<Message> messages){
        // compress condition
        if(codec != CompressionCodec.NoCompressionCodec
                && (compressedTopics.isEmpty() || compressedTopics.contains(topic))){
            return new ByteBufferMessageSet(codec, messages.toArray(new Message[messages.size()]));
        }
        return new ByteBufferMessageSet(CompressionCodec.NoCompressionCodec, messages.toArray(new Message[messages.size()]));
    }

    public void close() {

    }
}
