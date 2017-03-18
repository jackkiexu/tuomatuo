package com.lami.tuomatuo.mq.zookeeper.server;

/**
 * Manages the unknown request (i.e unknown OpCode), by:
 * - sending back the KeeperException.UnimplementedException() error code to the client
 * - closing the connection
 *
 * Created by xjk on 3/18/17.
 */
public class UnimplementedRequestProcessor implements RequestProcessor {
}
