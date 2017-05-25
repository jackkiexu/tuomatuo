package com.lami.tuomatuo.mq.zookeeper.server.admin;

import java.util.Set;

/**
 * Created by xujiankang on 2017/3/19.
 */
public interface Command {

    Set<String> getNames();

    String getPrimaryname();

    String getDoc();


}
