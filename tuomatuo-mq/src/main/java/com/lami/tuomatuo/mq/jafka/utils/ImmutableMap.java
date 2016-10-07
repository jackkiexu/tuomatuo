package com.lami.tuomatuo.mq.jafka.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xjk on 10/1/16.
 */
public class ImmutableMap {

    public static <K, V> Map<K, V> of(K k, V v){
        return of0(new KV<K, V>(k, v));
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2){
        return of0(new KV<K, V>(k1, v1),
                new KV<K, V>(k2, v2)
        );
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3){
        return of0(new KV<K, V>(k1, v1),
                new KV<K, V>(k2, v2),
                new KV<K, V>(k3, v3)
        );
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4){
        return of0(new KV<K, V>(k1, v1),
                new KV<K, V>(k2, v2),
                new KV<K, V>(k3, v3),
                new KV<K, V>(k4, v4)
        );
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5){
        return of0(new KV<K, V>(k1, v1),
                new KV<K, V>(k2, v2),
                new KV<K, V>(k3, v3),
                new KV<K, V>(k4, v4),
                new KV<K, V>(k5, v5)
                );
    }

    public static <K, V> Map<K, V> of(KV<K, V> kv){
        Map<K, V> map = new HashMap<K, V>();
        map.put(kv.k, kv.v);
        return map;
    }

    private static <K, V> Map<K, V> of0(KV<K, V>... kvs){
        Map<K, V> map = new HashMap<K, V>();
        for(KV<K, V> kv : kvs){
            map.put(kv.k, kv.v);
        }
        return map;
    }

}
