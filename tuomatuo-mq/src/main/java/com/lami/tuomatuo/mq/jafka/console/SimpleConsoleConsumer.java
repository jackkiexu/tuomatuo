package com.lami.tuomatuo.mq.jafka.console;

import com.lami.tuomatuo.mq.jafka.consumer.SimpleConsumer;
import com.lami.tuomatuo.mq.jafka.utils.Closer;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.log4j.Logger;

import java.net.URI;

/**
 * Created by xjk on 2016/12/7.
 */
public class SimpleConsoleConsumer {

    private static final Logger logger = Logger.getLogger(SimpleConsoleConsumer.class);

    public static void main(String[] args) throws Exception{
        OptionParser parser = new OptionParser();
        ArgumentAcceptingOptionSpec<String> topicIdOpt = parser.accepts("topic", "REQUIRED: The topic id to consumer on.")//
                .withRequiredArg().describedAs("topic").ofType(String.class);
        ArgumentAcceptingOptionSpec<String> serverOpt = parser.accepts("server", "REQUIRED: The jafka server connection string.")//
                .withRequiredArg().describedAs("jafka://hostname:port").ofType(String.class);
        ArgumentAcceptingOptionSpec<Long> offsetOpt = parser.accepts("offset", "The offset to start consuming from.")//
                .withRequiredArg().describedAs("offset").ofType(Long.class).defaultsTo(0L);

        OptionSet optionSet = parser.parse(args);
        checkRequireArgs(parser, optionSet, topicIdOpt, serverOpt);

        URI server = new URI(optionSet.valueOf(serverOpt));
        String topic = optionSet.valueOf(topicIdOpt);
        long startingOffset = optionSet.valueOf(offsetOpt).longValue();

        final SimpleConsumer consumer = new SimpleConsumer(server.getHost(), server.getPort(), 10000, 64 * 1024);
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {

            }
        });
    }

    static void checkRequireArgs(OptionParser parser, OptionSet options, OptionSpec<?>... optionSpecs) throws Exception{
        for(OptionSpec<?> arg : optionSpecs){
            if(!options.has(arg)){
                logger.info("Missing required argument " + arg);
                parser.printHelpOn(System.err);
                System.exit(1);
            }
        }
    }

}
