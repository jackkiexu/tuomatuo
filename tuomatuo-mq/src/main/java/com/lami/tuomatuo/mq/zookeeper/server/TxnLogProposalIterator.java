package com.lami.tuomatuo.mq.zookeeper.server;

/**
 * This class provides an iterator interface to access Proposal deserialized
 * from on-disk txnlog. The iterator deserializes one proposal at a time
 * to reduce memory footprint. Note that the request part of the proposal
 * is not initialized and set to null since we don't need it during
 * follower sync-up
 *
 * Created by xjk on 3/19/17.
 */
public class TxnLogProposalIterator {
}
