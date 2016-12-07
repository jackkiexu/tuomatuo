package com.lami.tuomatuo.mq.jafka.console;

import com.lami.tuomatuo.mq.jafka.consumer.SimpleConsumer;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URI;

/**
 * Created by xjk on 2016/12/7.
 */
public class GetOffsetShell {

    private static final Logger logger = Logger.getLogger(GetOffsetShell.class);

    public static void main(String[] args) throws Exception{
        OptionParser parser = new OptionParser();
        ArgumentAcceptingOptionSpec<String> urlOpt = parser.accepts("server", "REQUIRED: the hostname of the server to connect to.")//
                .withRequiredArg().describedAs("jafka://hostname:port").ofType(String.class);
        ArgumentAcceptingOptionSpec<String> topicOpt = parser.accepts("topic", "REQUIRED: The topic to get offset from.")//
                .withRequiredArg().describedAs("topic").ofType(String.class);
        ArgumentAcceptingOptionSpec<Integer> partitionOpt = parser.accepts("partition", "partition id")//
                .withRequiredArg().describedAs("partition_id").ofType(Integer.class).defaultsTo(0);
        ArgumentAcceptingOptionSpec<Long> timeOpt = parser.accepts("time", "timestamp of the offsets before that")//
                .withRequiredArg().describedAs("timestamp/-1(lastest)/-2(earliest)").ofType(Long.class);
        ArgumentAcceptingOptionSpec<Integer> noffsetsOpt = parser.accepts("offsets", "number of offsets returned")//
                .withRequiredArg().describedAs("count").ofType(Integer.class).defaultsTo(1);
        OptionSet options = parser.parse(args);
        checkRequiredArgs(parser, options, urlOpt, topicOpt, timeOpt);

        URI uri = new URI(options.valueOf(urlOpt));
        String topic = options.valueOf(topicOpt);
        int partition = options.valueOf(partitionOpt).intValue();
        long time = options.valueOf(timeOpt).longValue();
        int noffsets = options.valueOf(noffsetsOpt).intValue();
        SimpleConsumer consumer = new SimpleConsumer(uri.getHost(), uri.getPort(), 10000, 100 * 1000);

        try {
            long[] offsets = consumer.getOffsetsBefore(topic, partition, time, noffsets);
            logger.info("get " + offsets.length + " result");
            for(long offset : offsets){
                System.out.println(offset);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }

    static void checkRequiredArgs(OptionParser parser, OptionSet options, OptionSpec<?>... optionSpecs) throws Exception{
        for(OptionSpec<?> arg : optionSpecs){
            if(!options.has(arg)){
                logger.info("Missing required argument " + arg);
                parser.printHelpOn(System.err);
                System.exit(1);
            }
        }
    }
}
