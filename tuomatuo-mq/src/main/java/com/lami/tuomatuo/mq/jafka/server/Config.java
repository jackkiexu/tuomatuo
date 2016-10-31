package com.lami.tuomatuo.mq.jafka.server;

import com.lami.tuomatuo.mq.jafka.message.Message;
import com.lami.tuomatuo.mq.jafka.utils.Utils;
import com.lami.tuomatuo.mq.jafka.utils.ZkConfig;

import java.util.Map;
import java.util.Properties;

/**
 * Configuration setting for the jafka server
 *
 * Created by xjk on 2016/10/31.
 */
public class Config extends ZkConfig {

    public Config(Properties props) {
        super(props);
    }

    /** the port to listen and accept connections on (default 6667) */
    public int getPort(){
        return Utils.getInt(props, "port", 6667);
    }

    /**
     * hostname of broker. If not set, will pick up from the value returned from getLocalHost. If there are multiplate
     * interfaces getLocalHost may not be what you want
     */
    public String getHostName(){
        return Utils.getString(props, "hostname", null);
    }

    /**
     * the broker id for this server
     */
    public int getBrokerId(){
        return Utils.getInt(props, "brokerid");
    }

    /**
     * the SO _SNDBUFF buffer of the socket server sockets
     */
    public int getSocketSendBuffer(){
        return Utils.getInt(props, "socket.send.buffer", 100 * 1024);
    }

    /**
     * the SO_RCVBUFF buffer of the socket server sockets
     */
    public int getSocketReceiveBuffer(){
        return Utils.getInt(props, "socket.receive.buffer", 100 * 1024);
    }

    /**
     * the maximum number of bytes in a socket request
     */
    public int getMaxSocketRequestSize(){
        return Utils.getIntInRange(props, "max.socket.request.bytes", 100 * 1024 * 1024, 1, Integer.MAX_VALUE);
    }

    /**
     * the number of worker threads that the server uses for handling all client requests
     */
    public int getNumThreads(){
        return Utils.getIntInRange(props, "num.threads", Runtime.getRuntime().availableProcessors(), 1, Integer.MAX_VALUE);
    }

    /**
     * the interpublic String get in which to measure performance statistics
     */
    public int getMonitoringPeriodSecs(){
        return Utils.getIntInRange(props, "monitoring.period.secs", 600, 1, Integer.MAX_VALUE);
    }

    /**
     * the default number of log partitions per topic
     * @return
     */
    public int getNumPartitions(){
        return Utils.getIntInRange(props, "num.partitions", 1, 1, Integer.MAX_VALUE);
    }

    /**
     * the directory in which the log data is kept
     * @return
     */
    public String getLogDir(){
        return Utils.getString(props, "log.dir");
    }

    /**
     * the maximum size of a single log file
     */
    public int getLogFileSize(){
        return Utils.getIntInRange(props, "log.file.size", 1 * 1024 * 1024 * 1024, Message.MinHeaderSize, Integer.MAX_VALUE);
    }

    /**
     * the number of messages accumulated on a log partition before messages are flushed to disk
     */
    public int getFlushInterval(){
        return Utils.getIntInRange(props, "log.flush.interval", 500, 1, Integer.MAX_VALUE);
    }

    /**
     * the number of hours to keep a log file before deleteing it
     */
    public int getLogRetentionHours(){
        return Utils.getIntInRange(props, "log.retention.hours", 24 * 7, 1, Integer.MAX_VALUE);
    }

    /**
     * the maximum size of the log before deleting it
     */
    public int getLogRetentionSize(){
        return Utils.getInt(props, "log.retention.size", -1);
    }


    /**
     * the number of hours to keep a log file before deleting it for some specific topic
     */
    public Map<String, Integer> getLogRetentionHoursMap(){
        return Utils.getTopicRentionHours(Utils.getString(props, "topic.log.retention.hours", ""));
    }

    /**
     * the frequency in minutes that the log cleaner checks whether any log is eligible for deletion
     */
    public int getLogCleanupIntervalMinutes(){
        return Utils.getIntInRange(props, "log.cleanup.interval.mins", 10, 1, Integer.MAX_VALUE);
    }

    /**
     * enable zookeeper registration in the server
     */
    public boolean getEnableZookeeper(){
        return Utils.getBoolean(props, "enable.zookeeper", true);
    }

    /**
     * the maximum time in ms that a message in selected topics is kept in memory before flushed to disk, e.g, topic1:3000, topic2:6000
     */
    public Map<String, Integer> getFlushIntervalMap(){
        return Utils.getTopicFlushIntervals(Utils.getString(props, "topic.flush.interval.ms", ""));
    }

    /**
     * the frequency in ms that the log flusher checks whether any log needs to be flushed to disk
     */
    public int getFlushSchedulerThreadRate(){
        return Utils.getInt(props, "log.default.flush.scheduler.inetrval.ms", 3000);
    }

    /**
     * the maximum time in ms that a message in any topic is kept in memory before flushed to disk
     */
    public int getDefaultFlushIntervalMs(){
        return Utils.getInt(props, "log.default.flush.interval.ms", getFlushSchedulerThreadRate());
    }

    /**
     * the number of partitions for selected topic, e.g topic1:8,topic2:16
     */

    public Map<String, Integer> getTopicPartitionsMap(){
        return Utils.getTopicPartitions(Utils.getString(props, "topic.partition.count.map", ""));
    }

}
