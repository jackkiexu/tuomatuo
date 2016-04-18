package com.lami.tuomatuo.search.base.curator.Elections;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by xujiankang on 2016/4/15.
 */
public class LeaderSelectorClient extends LeaderSelectorListenerAdapter implements Closeable{

    private static final Logger logger = Logger.getLogger(LeaderSelectorClient.class);

    private String name;
    private LeaderSelector leaderSelector;
    private String PATH = "/tmp/2016041401";

    public LeaderSelectorClient(CuratorFramework client, String name) {
        this.name = name;
        leaderSelector = new LeaderSelector(client, PATH, this);
        leaderSelector.autoRequeue();
    }

    public void start() throws Exception{
        leaderSelector.start();
    }

    public void close() throws IOException {
        leaderSelector.close();
    }

    public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
        int waitSeconds = (int)(5* Math.random()) + 1;
        logger.info(name + "是当前的 leader");
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(waitSeconds));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }finally {
            logger.info(name + "让出领导权\n");
        }
    }
}
