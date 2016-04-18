package com.lami.tuomatuo.search.base.curator.persistentephemeral;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.nodes.PersistentEphemeralNode;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.KillSession;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * Created by xujiankang on 2016/4/15.
 */
public class PersistentEphemeralNodeExample {

    private static final Logger logger = Logger.getLogger(PersistentEphemeralNodeExample.class);

    private static String PATH = "/example/ephemeralNode";
    private static String PATH2 = "/example/node";

    public static void main(String[] args) throws Exception{
        TestingServer server = new TestingServer();
        CuratorFramework client = null;

        PersistentEphemeralNode node = null;

        try {
            client =  CuratorFrameworkFactory.newClient("192.168.1.28:2181", new ExponentialBackoffRetry(1000, 3));
            client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
                public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                    logger.info("client state:" + connectionState.name());
                }
            });
            client.start();

            // http://zookeeper.apache.org/doc/r3.2.2/api/org/apache/zookeeper/CreateMode.html
            node = new PersistentEphemeralNode(client, PersistentEphemeralNode.Mode.EPHEMERAL, PATH, "test".getBytes());
            node.start();
            node.waitForInitialCreate(3, TimeUnit.SECONDS);
            String actualPath = node.getActualPath();
            logger.info("node:" + actualPath + ", value:" + new String(client.getData().forPath(actualPath)));

            client.create().forPath(PATH2, "persisten node".getBytes());
            logger.info("node :" + PATH2 + ", value: " + new String(client.getData().forPath(PATH2)));
            KillSession.kill(client.getZookeeperClient().getZooKeeper(), "192.168.1.28:2181");
            logger.info("node :" + actualPath + " doesn't exist:" + (client.checkExists().forPath(actualPath) == null));
            logger.info("node " + PATH2 + " value : " + new String(client.getData().forPath(PATH2)));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(node);
            CloseableUtils.closeQuietly(client);
        }

    }

}
