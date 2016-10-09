package com.lami.tuomatuo.mq.lettuce;

import com.lami.tuomatuo.mq.lettuce.protocol.CommandKeyword;
import com.lami.tuomatuo.mq.lettuce.protocol.CommandArgs;
import com.lami.tuomatuo.mq.lettuce.protocol.CommandType;

import java.util.ArrayList;
import java.util.List;

/**
 * Argument list builder for the redis <a href="http://redis.io/commands/sort">SORT</a>
 * command. Static import the menthod from {@link ZStoreArgs.Builder} and chain the method calls;
 * <code>by("weight_*").dec().limit(0, 2)</code>
 * Created by xjk on 9/17/16.
 */
public class SortArgs {

    private String by;
    private Long offset, count;
    private List<String> get;
    private CommandKeyword order;
    private boolean alpha;

    /**
     * Static builder methods.
     */
    public static class Builder {
        public static SortArgs by(String pattern) {
            return new SortArgs().by(pattern);
        }

        public static SortArgs limit(long offset, long count) {
            return new SortArgs().limit(offset, count);
        }

        public static SortArgs get(String pattern) {
            return new SortArgs().get(pattern);
        }

        public static SortArgs asc() {
            return new SortArgs().asc();
        }

        public static SortArgs desc() {
            return new SortArgs().desc();
        }

        public static SortArgs alpha() {
            return new SortArgs().alpha();
        }
    }

    public SortArgs by(String pattern) {
        by = pattern;
        return this;
    }

    public SortArgs limit(long offset, long count) {
        this.offset = offset;
        this.count  = count;
        return this;
    }

    public SortArgs get(String pattern) {
        if (get == null) {
            get = new ArrayList<String>();
        }
        get.add(pattern);
        return this;
    }

    public SortArgs asc() {
        order = CommandKeyword.ASC;
        return this;
    }

    public SortArgs desc() {
        order = CommandKeyword.DESC;
        return this;
    }

    public SortArgs alpha() {
        alpha = true;
        return this;
    }

    <K, V> void build(CommandArgs<K, V> args, K store) {

        if (by != null) {
            args.add(CommandKeyword.BY);
            args.add(by);
        }

        if (get != null) {
            for (String pattern : get) {
                args.add(CommandType.GET);
                args.add(pattern);
            }
        }

        if (offset != null) {
            args.add(CommandKeyword.LIMIT);
            args.add(offset);
            args.add(count);
        }

        if (order != null) {
            args.add(order);
        }

        if (alpha) {
            args.add(CommandKeyword.ALPHA);
        }

        if (store != null) {
            args.add(CommandKeyword.STORE);
            args.addKey(store);
        }
    }
}
