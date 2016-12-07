package com.lami.tuomatuo.mq.jafka.console;

import com.github.zkclient.ZkClient;
import com.github.zkclient.exception.ZkInterruptedException;
import com.lami.tuomatuo.mq.jafka.consumer.Consumer;
import com.lami.tuomatuo.mq.jafka.consumer.ConsumerConfig;
import com.lami.tuomatuo.mq.jafka.consumer.ConsumerConnector;
import com.lami.tuomatuo.mq.jafka.consumer.MessageStream;
import com.lami.tuomatuo.mq.jafka.message.Message;
import com.lami.tuomatuo.mq.jafka.producer.serializer.DefaultDecoder;
import com.lami.tuomatuo.mq.jafka.utils.Closer;
import com.lami.tuomatuo.mq.jafka.utils.ImmutableMap;
import com.lami.tuomatuo.mq.jafka.utils.zookeeper.ZkStringSerializer;
import com.sohu.jafka.console.*;
import joptsimple.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;

/**
 * Created by xjk on 2016/12/7.
 */
public class ConsoleConsumer {

    private static final Logger logger = Logger.getLogger(ConsoleConsumer.class);

    public static void main(String[] args) throws Exception{
        OptionParser parser = new OptionParser();
        ArgumentAcceptingOptionSpec<String> topicIdOpt = parser.accepts("topic", "REQUIRED: The topic id to consumer on.")//
                .withRequiredArg().describedAs("topic").ofType(String.class);
        final ArgumentAcceptingOptionSpec<String> zkConnectOpt = parser
                .accepts("zookeeper",
                        "REQUIRED: The connection string for the zookeeper connection in the form host:port.  Multiple URLS can be given to allow fail-over.")//
                .withRequiredArg().describedAs("urls").ofType(String.class);
        final ArgumentAcceptingOptionSpec<String> groupIdOpt = parser.accepts("group", "The group id to consume on.")//
                .withRequiredArg().describedAs("gid").defaultsTo("console-consumer-" + new Random().nextInt(100000)).ofType(String.class);
        ArgumentAcceptingOptionSpec<Integer> fetchSizeOpt = parser.accepts("fetch-size", "The amount of data to fetch in a single request.")//
                .withRequiredArg().describedAs("size").ofType(Integer.class).defaultsTo(1024 * 1024);
        ArgumentAcceptingOptionSpec<Integer> socketBufferSizeOpt = parser.accepts("socket-buffer-size", "The size of the tcp RECV size.")//
                .withRequiredArg().describedAs("size").ofType(Integer.class).defaultsTo(2 * 1024 * 1024);
        ArgumentAcceptingOptionSpec<Integer> consumerTimeoutMsOpt = parser
                .accepts("consumer-timeout-ms", "consumer throws timeout exception after waiting this much " + "of time without incoming messages")//
                .withRequiredArg().describedAs("prop").ofType(Integer.class).defaultsTo(-1);
        ArgumentAcceptingOptionSpec<String> messageFormatterOpt = parser
                .accepts("formatter", "The name of a class to use for formatting jafka messages for display.").withRequiredArg().describedAs("class")
                .ofType(String.class).defaultsTo(com.sohu.jafka.console.NewlineMessageFormatter.class.getName());
        //ArgumentAcceptingOptionSpec<String> messageFormatterArgOpt = parser.accepts("property")//
        //         .withRequiredArg().describedAs("prop").ofType(String.class);
        OptionSpecBuilder resetBeginningOpt = parser.accepts("from-beginning", "If the consumer does not already have an established offset to consume from, "
                + "start with the earliest message present in the log rather than the latest message.");
        ArgumentAcceptingOptionSpec<Integer> autoCommitIntervalOpt = parser
                .accepts("autocommit.interval.ms", "The time interval at which to save the current offset in ms")//
                .withRequiredArg().describedAs("ms").ofType(Integer.class).defaultsTo(10 * 1000);
        // ArgumentAcceptingOptionSpec<Integer> maxMessagesOpt = parser
        //       .accepts("max-messages", "The maximum number of messages to consume before exiting. If not set, consumption is continual.")//
        //       .withRequiredArg().describedAs("num_messages").ofType(Integer.class);
        OptionSpecBuilder skipMessageOnErrorOpt = parser.accepts("skip-message-on-error", "If there is an error when processing a message, "
                + "skip it instead of halt.");

        final OptionSet options = tryParse(parser, args);
        checkRequiredArgs(parser, options, topicIdOpt, zkConnectOpt);

        Properties props = new Properties();
        props.put("groupId", options.valueOf(groupIdOpt));
        props.put("socket.buffersize", options.valueOf(socketBufferSizeOpt).toString());
        props.put("fetch.size", options.valueOf(fetchSizeOpt).toString());
        props.put("auto.commit", "true");
        props.put("autocommit.interval.ms", options.valueOf(autoCommitIntervalOpt).toString());
        props.put("autooffet.reset", options.has(resetBeginningOpt)? "smallest" : "largest");
        props.put("zk.connect", options.valueOf(zkConnectOpt));
        props.put("consummer.timeout.ms", options.valueOf(consumerTimeoutMsOpt).toString());

        ConsumerConfig config = new ConsumerConfig(props);
        boolean skipMessageOnError = options.has(skipMessageOnErrorOpt);
        String topic = options.valueOf(topicIdOpt);

        final Class<MessageFormatter> messageFormatterClass = (Class<MessageFormatter>)Class.forName(options.valueOf(messageFormatterOpt));

        final ConsumerConnector connector = Consumer.create(config);
        if(options.has(resetBeginningOpt)){
            tryCleanupZookeeper(options.valueOf(zkConnectOpt), options.valueOf(groupIdOpt));
        }

        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                Closer.closeQuietly(connector);
                if(!options.has(groupIdOpt)){
                    tryCleanupZookeeper(options.valueOf(zkConnectOpt), options.valueOf(groupIdOpt));
                }
            }
        });

        MessageStream<Message> stream = connector.createMessageStreams(ImmutableMap.of(topic, 1), new DefaultDecoder()).get(topic).get(0);
        MessageFormatter formatter = messageFormatterClass.newInstance();

        try {
            for(Message message : stream){
                try {
                    formatter.writeTo(message, System.out);
                } catch (Exception e) {
                    e.printStackTrace();
                    if(skipMessageOnError){
                        logger.info(e.getMessage());
                    }else{
                        throw e;
                    }

                    if(System.out.checkError()){
                        logger.info("Unable to write to standard out, closing consumer");
                        formatter.close();
                        connector.close();
                        System.exit(1);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.flush();
            formatter.close();
            connector.close();
        }

    }

    static OptionSet tryParse(OptionParser parser, String[] args){
        try {
            return parser.parse(args);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static void checkRequiredArgs(OptionParser parser, OptionSet options, OptionSpec<?>... optionSpecs) throws IOException{
        for(OptionSpec<?> arg : optionSpecs){
            if(!options.has(arg)){
                logger.info("Missing required argument " + arg);
                parser.printHelpOn(System.err);
                System.exit(1);
            }
        }
    }

    static void tryCleanupZookeeper(String zkConnect, String groupId){
        try {
            String dir = "/consumers/" + groupId;
            ZkClient zk = new ZkClient(zkConnect, 30 * 1000, 30 * 1000, ZkStringSerializer.getInstance());
            zk.deleteRecursive(dir);
            zk.close();
        } catch (ZkInterruptedException e) {
            e.printStackTrace();
        }
    }
}
