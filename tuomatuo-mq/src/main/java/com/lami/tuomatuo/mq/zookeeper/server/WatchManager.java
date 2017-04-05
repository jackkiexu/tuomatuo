package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.WatchedEvent;
import com.lami.tuomatuo.mq.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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


    public Set<Watcher> triggerWatch(String path, Watcher.Event.EventType type){
        return triggerWatch(path, type, null);
    }

    public Set<Watcher> triggerWatch(String path, Watcher.Event.EventType type, Set<Watcher> supress){
        WatchedEvent e = new WatchedEvent(type, Watcher.Event.KeeperState.SyncConnected, path);
        HashSet<Watcher> watchers;
        synchronized (this){
            watchers = watchTable.remove(path);
            if(watchers == null || watchers.isEmpty()){
                LOG.info("No watchers for " + path);
                return null;
            }

            for(Watcher w : watchers){
                HashSet<String> paths = watch2Paths.get(w);
                if(paths != null){
                    paths.remove(path);
                }
            }
        }

        for(Watcher w : watchers){
            if(supress != null && supress.contains(w)){
                continue;
            }
            w.process(e);
        }
        return watchers;
    }

    /**
     * String representation watches, Warning may be large
     * @param pwriter if true output by paths, otw output watches by connection
     * @param byPath string representation of watches
     */
    public synchronized void dumpWatches(PrintWriter pwriter, boolean byPath){
        if(byPath){
            for(Map.Entry<String, HashSet<Watcher>> e : watchTable.entrySet()){
                pwriter.println(e.getKey());
                for(Watcher w : e.getValue()){
                    pwriter.println("\t0x");
                    pwriter.print(Long.toHexString(((ServerCnxn) w).getSessionId()));
                    pwriter.println("\n");
                }
            }
        }
        else {
            for(Map.Entry<Watcher, HashSet<String>> e : watch2Paths.entrySet()){
                pwriter.println("0x");
                pwriter.println(Long.toHexString(((ServerCnxn)e.getKey()).getSessionId()));
                for(String path : e.getValue()){
                    pwriter.print("\t");
                    pwriter.println(path);
                }
            }
        }
    }


    @Override
    public String toString() {
        return "WatchManager{" +
                "watchTable=" + watchTable +
                ", watch2Paths=" + watch2Paths +
                '}';
    }
}
