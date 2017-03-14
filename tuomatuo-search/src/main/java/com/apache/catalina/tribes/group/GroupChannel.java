package com.apache.catalina.tribes.group;

import com.apache.catalina.tribes.ChannelInterceptor;
import com.apache.catalina.tribes.ManagedChannel;
import com.apache.juli.logging.Log;
import com.apache.juli.logging.LogFactory;
import com.apache.tomcat.util.res.StringManager;

import java.util.Iterator;

/**
 * The default implementation of a channel
 * The GroupChannel manages the replication channel. It coordinates
 * message being sent and received with membership announcements.
 * The channel has an chain of interceptors that can modify the message or perform other logic
 * It manages a complete group, both membership and replication
 *
 * Created by xjk on 3/14/17.
 */
public class GroupChannel extends ChannelInterceptorBase implements ManagedChannel{

    private static final Log log = LogFactory.getLog(GroupChannel.class);
    protected static final StringManager sm =
            StringManager.getManager(GroupChannel.class.getPackage().getName());

    /**
     * Flag the determine if the channel manages its own heartbeat
     * If set to true, the channel will start a local thread for the heartbeat
     */
    protected boolean heartbeat = true;

    protected long heartbeatSleeptime = 5 * 1000; // every 5 seconds


    /**
     * If <code>Channel.getHeartbeat() == true</code> then a thread of this class
     */
    public static class HeartbeatThread extends Thread {
        private static final Log log = LogFactory.getLog(HeartbeatThread.class);
        protected static int counter = 1;
        protected static synchronized int inc(){
            return counter++;
        }

        protected volatile boolean doRun = true;
        protected final GroupChannel channel;
        protected final long sleepTime;

        public HeartbeatThread(GroupChannel channel, long sleepTime) {
            super();
            this.setPriority(MIN_PRIORITY);
            String channelName = "";
            if(channel.getName() != null) channelName = "[" + channel.getName() + "]";
            setName("GroupChannel-Heartbeat " + channelName + " - " + inc());
            setDaemon(true);

            this.channel = channel;
            this.sleepTime = sleepTime;
        }

        public void stopHeartbeat(){
            doRun = false;
            interrupt();
        }

        @Override
        public void run() {
            while(doRun){
                try{
                    Thread.sleep(sleepTime);
                    channel.heartbeat();
                }catch (Exception x){
                    log.error(sm.getString("groupChannel.unable.sendHeartbeat"),x);
                }
            }
        }
    }



    /**
     * An iterator to loop through the interceptors in a channel
     */
    public static class InterceptorIterator implements Iterator<ChannelInterceptor>{

        private final ChannelInterceptor end;
        private ChannelInterceptor start;

        public InterceptorIterator(ChannelInterceptor start, ChannelInterceptor end) {
            this.end = end;
            this.start = start;
        }

        @Override
        public boolean hasNext() {
            return start != null && start != end;
        }

        @Override
        public ChannelInterceptor next() {
            ChannelInterceptor result = null;
            if(hasNext()){
                result = start;
                start = start.getNext();
            }
            return result;
        }
    }

}
