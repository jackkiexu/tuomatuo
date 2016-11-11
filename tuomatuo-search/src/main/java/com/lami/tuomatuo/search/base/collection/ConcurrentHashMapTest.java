package com.lami.tuomatuo.search.base.collection;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xjk on 2016/11/11.
 */
public class ConcurrentHashMapTest {

    public static void main(String[] args) {
        LearnConcurrentHashMap<String, String> map = new LearnConcurrentHashMap<String, String>();
        map.put("1", "2");
        map.put("2", "3");
        map.get("1");
        map.get("2");
    }
}
