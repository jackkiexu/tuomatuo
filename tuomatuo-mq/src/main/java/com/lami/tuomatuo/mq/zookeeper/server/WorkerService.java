package com.lami.tuomatuo.mq.zookeeper.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WorkerService is a worker thread pool for running tasks and is implemented
 * using one or more ExecutorServices. A WorkerService can support assignable
 * threads, which it does by creating N separate single thread ExecutorServices,
 * or non-assignable threads, which it does by creatinga single N-thread
 * ExecutorService.
 *  - NIOServerCnxnFactory uses a non-assignable WorkerService because the
 *      socket IO requests are order independent and allowing the
 *      ExecutorService to handle thread assignment gives optimal performance.
 *  - CommitProcessor uses an assignable WorkerService because requests for
 *      a given session must be processed in order.
 * ExecutorService provides queue management and thread restarting, so it's
 * useful even with a single thread
 *
 * Created by xjk on 3/18/17.
 */
public class WorkerService {

    private static final Logger LOG = LoggerFactory.getLogger(WorkerService.class);

}
