package com.lami.tuomatuo.search.base.curator.Elections;

import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by xjk on 2016/4/15.
 */
public class LeaderSelectorExample {

    private static final Logger logger = Logger.getLogger(LeaderSelectorExample.class);

    public static void main(String[] args) {
        List<CuratorFramework> clients = Lists.newArrayList();
        List<LeaderSelectorClient> examples = Lists.newArrayList();

        try {
            for(int i=0; i < 10 ; i++){
                CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.1.28:2181", new ExponentialBackoffRetry(1000, 3));
                LeaderSelectorClient example = new LeaderSelectorClient(client, "Client - "+i);
                clients.add(client);
                examples.add(example);

                client.start();
                example.start();
            }
            logger.info("------------------------ 观察一会的选举结果 -------------------------------");
            Thread.sleep(10000);

            logger.info("------------------ 观察前5个客户端, 在观察选举的结果 --------------------");
            for(int i = 0; i < 5; i++){
                clients.get(i).close();
            }

            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            for(LeaderSelectorClient example: examples){
                CloseableUtils.closeQuietly(example);
            }
            for(CuratorFramework client : clients){
                CloseableUtils.closeQuietly(client);
            }
        }

    }

}
