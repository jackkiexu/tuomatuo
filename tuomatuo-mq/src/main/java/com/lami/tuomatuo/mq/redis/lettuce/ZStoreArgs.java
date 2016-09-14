package com.lami.tuomatuo.mq.redis.lettuce;

import com.lambdaworks.redis.SortArgs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xujiankang on 2016/9/14.
 */
public class ZStoreArgs {
    private static enum Aggregate {SUM, MIN, MAX }

    private List<Long> weights;
    private Aggregate aggregate;

    public static class Builder{
        public static SortArgs by(String pattern) { return new SortArgs().by(pattern);}
//        public static SortArgs limit(long offset, long count) { return }
    }

    public ZStoreArgs weights(long...weights){
        this.weights = new ArrayList<Long>(weights.length);
        for(long weight : weights){
            this.weights.add(weight);
        }
        return this;
    }

    public ZStoreArgs sum(){
        aggregate = Aggregate.SUM;
        return this;
    }

    public ZStoreArgs min(){
        aggregate = Aggregate.MIN;
        return this;
    }

    public ZStoreArgs max(){
        aggregate = Aggregate.MAX;
        return this;
    }



}
