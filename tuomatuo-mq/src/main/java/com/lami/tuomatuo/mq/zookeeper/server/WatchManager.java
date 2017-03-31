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
            // don't waste memory if there are few watches on a node
            // rehash when the 4th entry is added, doubling size thereafter
            // seems like a good compromise
            list = new HashSet<Watcher>(4);
            watchTable.put(path, list);
        }
        list.add(watcher);

        HashSet<String> paths = watch2Paths.get(watcher);
        if(paths == null ){
            // cnxn typically have many watches, so use default cap here
            paths = new HashSet<String>();
            watch2Paths.put(watcher, paths);
        }
        paths.add(path);
    }

    public synchronized void removeWatcher(Watcher watcher){
        HashSet<String> paths = watch2Paths.remove(watcher);
        if(paths == null){
            return;
        }
        for(String p : paths){
            HashSet<Watcher> list = watchTable.get(p);
            if(list != null){
                list.remove(watcher);
                if(list.size() == 0){
                    watchTable.remove(p);
                }
            }
        }
    }


}
