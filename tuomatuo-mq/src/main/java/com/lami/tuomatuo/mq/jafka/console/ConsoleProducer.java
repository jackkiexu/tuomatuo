package com.lami.tuomatuo.mq.jafka.console;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * Created by xjk on 10/31/16.
 */
public class ConsoleProducer {

    private static final Logger logger = Logger.getLogger(ConsoleProducer.class);

    static void checkRequireArgs(OptionParser parser, OptionSet options, OptionSpec<?>... optionSpecs) throws IOException{
        for(OptionSpec<?> arg : optionSpecs){
            if(!options.has(arg)){
                logger.info("Missing required argument " + arg);
                parser.printHelpOn(System.err);
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
    }
}
