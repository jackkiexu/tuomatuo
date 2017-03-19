package com.lami.tuomatuo.mq.zookeeper.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NIOServerCnxnFactory implements a multi-threaded ServerCnxnFactory using
 * NIO non-blocking socket calls. Communication between threads is handled via
 * queues:
 *
 *  -1      accept thread, which accepts new connections and assigns to a
 *          selector thread
 *  -1-N    selector threads, each of which selects on 1/N of the connections
 *          The reason the factory supports more than selector thread is that
 *          with large numbers of connections, select() itself can become a
 *          performance bottleneck
 *  -0-M    socket I/O worker threads, which perform basic socket reads and
 *          writes. If configured with O worker threads, the selector threads
 *          do the socket I/O directly
 *  -1      connection expiration thread, which closes idle connections; This is
 *          necessary to expire connections on which no session is established
 *
 *  Typical (default) thread counts are On a 32 core machine. 1 accept thread,
 *  1 connection expiration thread, 4 selector threads and 64 worker threads
 *
 * Created by xujiankang on 2017/3/19.
 */
public class NIOServerCnxnFactory extends ServerCnxnFactory{

    private static final Logger LOG = LoggerFactory.getLogger(NIOServerCnxnFactory.class);

}
