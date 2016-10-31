package com.lami.tuomatuo.mq.jafka.server;

import com.lami.tuomatuo.mq.jafka.log.LogManager;
import com.lami.tuomatuo.mq.jafka.network.SocketServer;
import com.lami.tuomatuo.mq.jafka.utils.Scheduler;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by xjk on 2016/10/31.
 */
public class Server {

    private static final Logger logger = Logger.getLogger(Server.class);

    public String CLEAN_SHUTDOWN_FILE = "";

    public Config config;

    private Scheduler scheduler = new Scheduler(1, "jafka-logcleaner-", false);

    private LogManager logManager;

    private final CountDownLatch shutdownLatch = new CountDownLatch(1);

    public AtomicBoolean isShuttingDown = new AtomicBoolean(false);

    public SocketServer socketServer;

    private File logDir;

    public Server(Config config) {
        this.config = config;
        logDir = new File(config.getLogDir());
        if(!logDir.exists()){
            logDir.mkdirs();
        }
    }


}
