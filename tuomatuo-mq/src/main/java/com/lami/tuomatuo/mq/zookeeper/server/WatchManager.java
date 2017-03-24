package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This class manages watches. It allows watches to be associated with a string
 * and remove watchers and their watches in addition to managing triggers
 * Created by xjk on 3/18/17.
 */
public class WatchManager {

    private static final Logger LOG = LoggerFactory.getLogger(WatchManager.class);

    private final HashMap<String, HashSet<Watcher>> watchTable = new HashMap<>();

    private final HashMap<Watcher, HashSet<String>> watch2Paths = new HashMap<>();

    synchronized int size(){
        int result = 0;
        for(Set<Watcher> watches : watchTable.values()){
            result += watches.size();
        }
        return result;
    }

    synchronized void addWatch(String path, Watcher watcher){
        HashSet<Watcher> list = watchTable.get(path);

        if(list == null){
            //
        }
    }


}
