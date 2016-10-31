package com.lami.tuomatuo.mq.jafka.consumer;

import com.lami.tuomatuo.mq.jafka.api.OffsetRequest;
import com.lami.tuomatuo.mq.jafka.utils.Utils;
import com.lami.tuomatuo.mq.jafka.utils.ZkConfig;

import java.util.Properties;

/**
 * Created by xjk on 2016/10/31.
 */
public class ConsumerConfig extends ZkConfig{

    private String groupId;

    private String consumerId;

    private int socketTimeoutMs;

    private int socketBufferSize;

    private int fetchSize;

    private long fetchBackOffMs;

    private boolean autoCommit;

    private int autoCommitIntervalMs;

    private int maxQueuedChunks;

    private int maxRebalanceRetries;

    private int rebalanceBackoffMs;

    private String autoOffsetReset;

    private int consumerTimeoutMs;

    private String mirrorTopicsWhiteList;

    private String mirrorTopicsBlackList;

    private int mirrorConsumerNumThreads;

    public ConsumerConfig(Properties props) {
        super(props);
        this.groupId = Utils.getString(props, "groupid");
        this.consumerId = Utils.getString(props, "consumerid", null);
        this.socketTimeoutMs = get("socket.timeout.ms", 30 * 1000);
        this.socketBufferSize = get("socket.buffersize", 64 * 1024); // 64KB
        this.fetchSize = get("fetch.size", 1024 * 1024); // 1MB
        this.fetchBackOffMs = get("fetch.backoff.ms", 1000);
        this.autoCommit = Utils.getBoolean(props, "autocommit.enable", true);
        this.autoCommitIntervalMs = get("automiccommit.interval.ms", 10 * 1000); // 10 seconds
        this.maxQueuedChunks = get("queuedchunks.max", 10);
        this.maxRebalanceRetries = get("rebalance.retries.max", 4);
        this.rebalanceBackoffMs = get("rebalance.backoff.ms", getZkSyncTimeMs());
        this.autoOffsetReset = get("autooffset.reset", OffsetRequest.SmallestTimeString);
        this.consumerTimeoutMs = get("consumer.timeout.ms", -1);
        this.mirrorTopicsWhiteList = get("mirror.topics.whitelist", "");
        this.mirrorTopicsBlackList = get("mirror.topics.blacklist", "");
        this.mirrorConsumerNumThreads = get("mirror.consumer.numthreads", 1);
    }

    /**
     * a string that uniquely automatically if not set. Set this explicity
     * for only testing purpose
     */
    public String getGroupId(){
        return groupId;
    }

    /**
     * consumer id: generated automatically if not set. Set this explicity
     * for only testing purpose
     */
    public String getConsumerId(){
        return consumerId;
    }

    /** the socket timeout for network request */
    public int getSocketTimeoutMs(){
        return socketTimeoutMs;
    }

    /** this socket timeout for network requests */
    public int getSocketBufferSize() {
        return socketBufferSize;
    }

    /** the number of byes of messages to attempt to fetch */
    public int getFetchSize() {
        return fetchSize;
    }

    /**
     * to avoid repeatedly polling a broker node which has no new data we
     * will backoff every time we get an empty set from the broker
     * @return
     */
    public long getFetchBackOffMs() {
        return fetchBackOffMs;
    }

    /**
     * if true, periodically commit to zookeeper the ffset of messages
     * already fetched by the consumer
     * @return
     */
    public boolean isAutoCommit() {
        return autoCommit;
    }

    /**
     * max number of messages buffered for consumption
     * @return
     */
    public int getAutoCommitIntervalMs() {
        return autoCommitIntervalMs;
    }

    /**
     * max number of messages buffered for consumption
     * @return
     */
    public int getMaxQueuedChunks() {
        return maxQueuedChunks;
    }

    /**
     * max number of retries during rebalance
     * @return
     */
    public int getMaxRebalanceRetries() {
        return maxRebalanceRetries;
    }

    /**
     * backoff time between retries during rebalance
     * @return
     */
    public int getRebalanceBackoffMs() {
        return rebalanceBackoffMs;
    }

    /**
     * what to do if an offset is out of range
     * <pre>
     *      smallest: automatically reset the offset to the smallest offset
     *      largest: automatically reset the offset to the largest offset
     *      anything else: throw exception to the consumer
     * </pre>
     *
     * @return
     */
    public String getAutoOffsetReset() {
        return autoOffsetReset;
    }

    /**
     * throw a timeout exception to the consumer if no message is available
     * for consumption after the specified interval
     * @return
     */
    public int getConsumerTimeoutMs() {
        return consumerTimeoutMs;
    }

    /**
     * Whitelist of topic for this mirror's embedded consumer to consume
     * At most one of whitelist/blacklist may be specified.
     * @return
     */
    public String getMirrorTopicsWhiteList() {
        return mirrorTopicsWhiteList;
    }

    /**
     * Topics to skip mirroring. At most one of whitelist/blacklist may be
     * specified
     * @return
     */
    public String getMirrorTopicsBlackList() {
        return mirrorTopicsBlackList;
    }

    public int getMirrorConsumerNumThreads() {
        return mirrorConsumerNumThreads;
    }
}
