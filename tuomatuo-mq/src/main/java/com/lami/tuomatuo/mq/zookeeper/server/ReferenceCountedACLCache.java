package com.lami.tuomatuo.mq.zookeeper.server;

import org.apache.zookeeper.data.ACL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class ReferenceCountedACLCache {

    private static final Logger LOG = LoggerFactory.getLogger(ReferenceCountedACLCache.class);

    final Map<Long, List<ACL>> longkeyMap = new HashMap<>();
    final Map<List<ACL>, Long> aclKeyMap = new HashMap<>();

    final Map<Long, AtomicLongWithEquals> referenceCounter = new HashMap<>();
    private static final long OPEN_UNSAFE_ACL_ID = -1L;

    /**
     * these are number of acls that we have in the datatree
     */
    long aclIndex = 0;

    private long incrementIndex(){
        return ++aclIndex;
    }


    public int size(){
        return aclKeyMap.size();
    }

    private void clear(){
        aclKeyMap.clear();
        longkeyMap.clear();
        referenceCounter.clear();
    }


    public synchronized void addUsage(Long acl){
        if(acl == OPEN_UNSAFE_ACL_ID){
            return;
        }
        if(!longkeyMap.containsKey(acl)){
            LOG.info("Ignoring acl " + acl + " as it does not exist in the cahche");
            return;
        }

        AtomicLong count = referenceCounter.get(acl);
        if(count == null){
            referenceCounter.put(acl, new AtomicLongWithEquals(1));
        }else {
            count.incrementAndGet();
        }
    }

    public synchronized void removeUsage(Long acl){
        if(acl == OPEN_UNSAFE_ACL_ID){
            return;
        }

        if(!longkeyMap.containsKey(acl)){
            LOG.info("Ignoring acl " + acl + " as it does exist in the cache");
            return;
        }

        long newCount = referenceCounter.get(acl).decrementAndGet();
        if(newCount <= 0){
            referenceCounter.remove(acl);
            aclKeyMap.remove(longkeyMap.get(acl));
            longkeyMap.remove(acl);
        }
    }

    public synchronized void purgeUnused(){
        Iterator<Map.Entry<Long, AtomicLongWithEquals>> refCountIter = referenceCounter.entrySet().iterator();
        while(refCountIter.hasNext()){
            Map.Entry<Long, AtomicLongWithEquals> entry = refCountIter.next();
            if(entry.getValue().get() <= 0){
                Long acl = entry.getKey();
                aclKeyMap.remove(longkeyMap.get(acl));
                longkeyMap.remove(acl);
                refCountIter.remove();
            }
        }
    }


    public synchronized Long convertAcls(List<ACL> acls){
        if(acls == null){
            return OPEN_UNSAFE_ACL_ID;
        }

        // get the value from the map
        Long ret = aclKeyMap.get(acls);
        if(ret == null){
            ret = incrementIndex();
            longkeyMap.put(ret, acls);
            aclKeyMap.put(acls, ret);
        }

        addUsage(ret);
        return ret;
    }





    private static class AtomicLongWithEquals extends AtomicLong{
        private static final long serialVersionUID = 3355155896813725462L;

        public AtomicLongWithEquals(long initialValue) {
            super(initialValue);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            return equals((AtomicLongWithEquals) o);
        }

        public boolean equals(AtomicLongWithEquals that) {
            return get() == that.get();
        }

        @Override
        public int hashCode() {
            return 31 * Long.valueOf(get()).hashCode();
        }
    }
}
