package com.lami.tuomatuo.cache.cache;

/**
 * Created by xjk on 2016/12/6.
 */
public class ExceptionStrategies {

    public static <K> ExceptionStrategy<K> alwaysRetain(){
        return new ExceptionStrategy<K>() {
            @Override
            public <T extends Throwable> boolean removeEntry(K key, T throable) {
                return false;
            }
        };
    }

    public static <K> ExceptionStrategy<K> alwaysRemove(){
        return new ExceptionStrategy<K>() {
            @Override
            public <T extends Throwable> boolean removeEntry(K key, T throable) {
                return true;
            }
        };
    }

    public static <K> ExceptionStrategy<K> removeOn(final Class<? extends Throwable> clas){
        return new ExceptionStrategy<K>() {
            @Override
            public <T extends Throwable> boolean removeEntry(K key, T throable) {
                return clas.isAssignableFrom(throable.getClass());
            }
        };
    }

    public static <K> ExceptionStrategy<K> not(final ExceptionStrategy<K> strategy){
        return new ExceptionStrategy<K>() {
            @Override
            public <T extends Throwable> boolean removeEntry(K key, T throable) {
                return !strategy.removeEntry(key, throable);
            }
        };
    }

    public static <K> ExceptionStrategy<K> and(final ExceptionStrategy<K>... strategies){
        return new ExceptionStrategy<K>() {
            @Override
            public <T extends Throwable> boolean removeEntry(K key, T throable) {
                for(ExceptionStrategy<K> strategy : strategies){
                    if(!strategy.removeEntry(key, throable)){
                        return false;
                    }
                }
                return true;
            }
        };
    }

    public static <K> ExceptionStrategy<K> or(final ExceptionStrategy<K>... strategies){
        return new ExceptionStrategy<K>() {
            @Override
            public <T extends Throwable> boolean removeEntry(K key, T throable) {
                for(ExceptionStrategy<K> strategy : strategies){
                    if(strategy.removeEntry(key, throable)){
                        return true;
                    }
                }
                return false;
            }
        };
    }
}
