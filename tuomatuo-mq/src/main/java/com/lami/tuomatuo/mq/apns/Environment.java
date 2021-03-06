package com.lami.tuomatuo.mq.apns;

import java.net.InetSocketAddress;

/**
 * Created by xjk on 2016/9/23.
 */
public enum Environment {

    SANDBOX ("sandbox.push.apple.com"),
    PRODUCTION ("push.apple.com");

    public InetSocketAddress gateway;
    public InetSocketAddress feedback;

    Environment(String domain) {
        this.gateway = new InetSocketAddress("gateway." + domain, 2195);
        this.feedback = new InetSocketAddress("feedback." + domain, 2195);
    }
}
