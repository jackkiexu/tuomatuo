package com.lami.tuomatuo.search.base.curator.framework;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.log4j.Logger;

import java.util.Collection;

/**
 * Created by xjk on 2016/4/18.
 */
public class TransactionExample {

    private static final Logger logger = Logger.getLogger(TransactionExample.class);

    public static void main(String[] args) {

    }

    public static Collection<CuratorTransactionResult> transaction(CuratorFramework client) throws Exception{
        // this example show how to Zookeeper's new transactions
        Collection<CuratorTransactionResult> results = client.inTransaction().create().forPath("/a/path", "some data".getBytes())
                .and().setData().forPath("another/path", "other data".getBytes())
                .and().delete().forPath("/yet/another")
                .and().commit(); // important
        for(CuratorTransactionResult result : results){
            logger.info(result.getForPath() + "-" + result.getType());
        }
        return results;
    }

    /**
     * These next four methods show how to use Curator's transaction APIs in a
     * more transactional-one-at-a-time - manner
     */
    public static CuratorTransaction startTransaction(CuratorFramework client){
        // start the transaction builder
        return client.inTransaction();
    }

    public static CuratorTransactionFinal addCreateToTransaction(CuratorTransaction transaction) throws Exception{
        // add a create Exception
        return transaction.create().forPath("/a/path", "some data".getBytes()).and();
    }

    public static void commitTransaction(CuratorTransactionFinal transactionFinal) throws Exception{
        // commit the transaction
        transactionFinal.commit();
    }

}
