package com.apache.catalina.tribes.membership;

import com.apache.catalina.tribes.MembershipListener;
import com.apache.catalina.tribes.MembershipService;
import com.apache.catalina.tribes.MessageListener;

import java.util.Properties;

/**
 * A membership implementation using simple multicast
 * This is the representation of a multicast membership service
 * This class is responsible for maintaining a list of active cluster nodes in the cluster
 * If a node fails to send out a heartbeat, the node will be dismissed
 *
 * Created by xjk on 3/14/17.
 */
public class McastService implements MembershipService, MembershipListener, MessageListener {

    private static final org.apache.juli.logging.Log log =
            org.apache.juli.logging.LogFactory.getLog( McastService.class );

    /**
     * The string manager for this package.
     */
    protected static final StringManager sm = StringManager.getManager(Constants.Package);

    /**
     * The implementation specific properties
     */
    protected Properties properties = new Properties();

}
