package com.lami.tuomatuo.mq.jafka.consumer;

import com.github.zkclient.ZkClient;
import com.lami.tuomatuo.mq.jafka.common.annotations.ClientSide;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xjk on 10/31/16.
 */
@ClientSide
public class Fetcher {

    private static final Logger logger = Logger.getLogger(Fetcher.class);

    private ConsumerConfig config;
    private ZkClient zkClient;

    private volatile List<FetcherRunnable> fetcherThreads = new ArrayList<FetcherRunnable>(0);

    public Fetcher(ConsumerConfig config, ZkClient zkClient) {
        super();
        this.config = config;
        this.zkClient = zkClient;
    }

    public void stopConnectionsToAllBrokers(){
        // shutdown the old fetcher threads, if any
        List<FetcherRunnable> threads = this.fetcherThreads;
        for(FetcherRunnable fetcherThread : threads){
        }
    }
}
