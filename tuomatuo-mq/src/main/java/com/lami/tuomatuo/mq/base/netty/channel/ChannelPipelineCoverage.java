package com.lami.tuomatuo.mq.base.netty.channel;

import java.lang.annotation.*;

/**
 * Created by xujiankang on 2016/9/22.
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ChannelPipelineCoverage {

    public static final String ALL = "all";
    public static final String ONE = "one";

    String value();
}
