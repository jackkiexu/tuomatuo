package com.lami.tuomatuo.mq.jafka.network;

import com.lami.tuomatuo.mq.jafka.mx.SocketServerStats;
import com.lami.tuomatuo.mq.jafka.utils.Utils;
import org.apache.log4j.Logger;

/**
 * Created by xjk on 2016/10/31.
 */
public class SocketServer {

    private static Logger logger = Logger.getLogger(SocketServer.class);

    private HandlerMappingFactory handlerFactory;

    private int maxRequestSize;

    private Processor[] processors;

    private Acceptor acceptor;

    private SocketServerStats stats;

    public SocketServer(int port,
                        int numProcessorThreads,
                        int monitoringPeriodSecs,
                        HandlerMappingFactory handlerFactory,
                        int sendBufferSize,
                        int receiveBufferSize,
                        int maxRequestSize
                        ){
        super();
        this.handlerFactory = handlerFactory;
        this.maxRequestSize = maxRequestSize;
        this.processors = new Processor[numProcessorThreads];
        this.stats = new SocketServerStats(1000L * 1000L * 1000L * monitoringPeriodSecs);
        this.acceptor = new Acceptor(port, processors, sendBufferSize, receiveBufferSize);
    }

    /**
     * shutdown the socket server
     */
    public void shutdown() throws InterruptedException{
        acceptor.shutdown();
        for(Processor processor : processors){
            processor.shutdown();
        }
    }

    /**
     * Start the socket server
     * @throws InterruptedException
     */
    public void startup() throws InterruptedException{
        logger.info("start " + processors.length + " Processor threads");
        for(int i = 0; i < processors.length; i++){
            processors[i] = new Processor(handlerFactory, stats, maxRequestSize);
            Utils.newThread("jafka-processor-" + i, processors[i], false).start();
        }
        Utils.newThread("jafka-acceptor", acceptor, false).start();
        acceptor.awaitStartup();
    }


    public SocketServerStats getStats(){
        return stats;
    }
}
