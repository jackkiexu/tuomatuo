package com.lami.tuomatuo.search.base.curator.crud;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.log4j.Logger;
import org.apache.zookeeper.data.Stat;

/**
 * Created by xjk on 2016/4/15.
 */
public class CrudExample {

    private static final Logger logger = Logger.getLogger(CrudExample.class);

    private static CuratorFramework client =  CuratorFrameworkFactory.newClient("192.168.1.28:2181", new ExponentialBackoffRetry(1000, 3));
    private static String PATH = "/curd2";

    public static void main(String[] args) {
        client.start();
        try {
//            client.create().forPath(PATH, "I Love messi".getBytes());

            byte[] bs = client.getData().forPath(PATH);
            logger.info("新建的节点, data为:" + new String(bs));
            client.setData().forPath(PATH, "I Love football".getBytes());

            // 由于是在 background 模式下获取的 data, 此时的bs可能为 null
            byte[] bs2 = client.getData().forPath(PATH);
            logger.info("修改后的数据 data 为:" + new String(bs2));

            client.delete().forPath(PATH);
            Stat stat = client.checkExists().forPath(PATH);

            // Stat 就是对 zonde 所有属性的一个映射, stat=null 表示节点不存在
            logger.info("stat:"+stat);



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseableUtils.closeQuietly(client);
        }
    }

}
