package com.lami.tuomatuo.mq.zookeeper.server.quorum;

/**
 * When the main(0 method of this class is used to start the program, the first
 * argument is used as a path to the config file, which will be used to obtain
 * configuration information. This file is a Properties file, so keys and
 * values are separated by equals(=) and the key/value pairs are separated
 * by new lines The following is a a general summary of keys used in the
 * configuration file. For full details on this see the documentation in
 * docs/index.html
 *
 * dataDir      - The directory where the ZooKeeper data is stored
 * dataLogDir   - The directory where the ZooKeeper transaction log is stored
 * clientPort   - The Port used to communicate with clients
 * tickTime     - The duration of a tick in milliseconds. This is the basic
 * unit of time in ZooKeeper
 * initLimit    - The maximum number of ticks that a follower will wait to
 * initially synchronize with a leader
 * syncLimit    - The maximum number of ticks that a follower will wait for a
 * message (including heartbeats) from the leader
 * server.id    - This is the host:port that the server with the
 * given id will use for the quorum protocol
 * In addition to the config file. There is a file in the data directory called
 * "myid" that contains the server id as an ASCII decimal value
 *
 * Created by xujiankang on 2017/3/19.
 */
public class QuorumPeerMain {
}
