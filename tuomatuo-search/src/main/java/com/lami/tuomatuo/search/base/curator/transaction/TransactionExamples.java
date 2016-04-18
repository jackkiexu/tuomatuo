package com.lami.tuomatuo.search.base.curator.transaction;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.log4j.Logger;

import java.util.Collection;

/**
 * Created by xujiankang on 2016/4/15.
 */
public class TransactionExamples {

    private static final Logger logger = Logger.getLogger(TransactionExamples.class);
    private static CuratorFramework client =  CuratorFrameworkFactory.newClient("192.168.1.28:2181", new ExponentialBackoffRetry(1000, 3));

    public static void main(String[] args) {
        try {
            client.start();
            // 开启事务
            CuratorTransaction transaction = client.inTransaction();
            Collection<CuratorTransactionResult> results = transaction.create()
                    .forPath("/a/path", "some data".getBytes()).and().setData()
                    .forPath("/another/path", "other data".getBytes()).and().delete().forPath("/yet/another/path").and().commit();

            for(CuratorTransactionResult result : results){
                logger.info(result.getForPath() + "-" + result.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
        }
    }
}
