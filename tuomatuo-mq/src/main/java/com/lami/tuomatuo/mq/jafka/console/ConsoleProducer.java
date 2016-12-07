package com.lami.tuomatuo.mq.jafka.console;

import com.lami.tuomatuo.mq.jafka.producer.Producer;
import com.lami.tuomatuo.mq.jafka.producer.ProducerConfig;
import com.lami.tuomatuo.mq.jafka.producer.ProducerData;
import com.lami.tuomatuo.mq.jafka.producer.serializer.StringEncoder;
import com.lami.tuomatuo.mq.jafka.utils.Closer;
import com.sohu.jafka.console.*;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by xjk on 10/31/16.
 */
public class ConsoleProducer {

    private static final Logger logger = Logger.getLogger(ConsoleProducer.class);


    public static void main(String[] args) throws Exception{
        OptionParser parser = new OptionParser();
        final ArgumentAcceptingOptionSpec<String> topicOpt = parser.accepts("topic", "REQUIRED: The topic id to produce messages to.")//
                .withRequiredArg().describedAs("topic").ofType(String.class);
        final ArgumentAcceptingOptionSpec<String> zkConnectOpt = parser.accepts("zookeeper", "REQUIRED: zokkeper connection string, form: HOST:PORT[/CHROOT]")//
                .withRequiredArg().describedAs("connection_string").ofType(String.class);
        final ArgumentAcceptingOptionSpec<String> brokerListOpt = parser.accepts("broker-list", "REQUIRED: broker list, form: 0:localhost:9092")//
                .withRequiredArg().describedAs("broker_list").ofType(String.class);
        final ArgumentAcceptingOptionSpec<String> messageReaderOpt = parser.accepts("message-encoder", "The class name of the message encoder")//
                .withRequiredArg().describedAs("encoder_class").ofType(String.class).defaultsTo(com.sohu.jafka.console.LineMessageReader.class.getName());

        OptionSet options = parser.parse(args);
        if(options.has(zkConnectOpt) && options.has(brokerListOpt)){
            logger.info("Only broker-list or zookeeper config");
            parser.printHelpOn(System.err);
            return;
        }

        ///////////////////////////////////////////////////////////
        final Properties properties = new Properties();
        if(options.has(zkConnectOpt)){
            checkRequireArgs(parser, options, topicOpt, zkConnectOpt);
            properties.put("zk.connect", options.valueOf(zkConnectOpt));
        }else{
            checkRequireArgs(parser, options, topicOpt, brokerListOpt);
            properties.put("broker.list", options.valueOf(brokerListOpt));
        }

        String topic = options.valueOf(topicOpt);
        properties.put("serializer.class", StringEncoder.class.getName());

        MessageReader reader = (MessageReader) Class.forName(options.valueOf(messageReaderOpt)).newInstance();
        reader.init(System.in, null);
        Producer<String, String> producer = new Producer<String, String>(new ProducerConfig(properties));
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                Closer.closeQuietly(producer);
            }
        });

        String message = null;
        while((message = reader.readMessage()) != null){
            if(message.length() == 0){
                break;
            }
            producer.send(new ProducerData<String, String>(topic, message));
        }
    }

    static void checkRequireArgs(OptionParser parser, OptionSet options, OptionSpec<?>... optionSpecs) throws IOException{
        for(OptionSpec<?> arg : optionSpecs){
            if(!options.has(arg)){
                logger.info("Missing required argument " + arg);
                parser.printHelpOn(System.err);
                System.exit(1);
            }
        }
    }

}
