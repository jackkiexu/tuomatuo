package com.apache.catalina.tribes.group;

import com.apache.catalina.tribes.ChannelListener;
import com.apache.catalina.tribes.Member;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by xjk on 3/13/17.
 */
public class RpcChannel implements ChannelListener{

    @Override
    public void messageReceived(Serializable msg, Member sender) {

    }

    @Override
    public boolean accept(Serializable msg, Member sender) {
        return false;
    }


    public static class RpcCollector{

    }


    public static class RpcCollectorKey{
        final byte[] id;

        public RpcCollectorKey(byte[] id) {
            this.id = id;
        }

        @Override
        public int hashCode() {
            return id[0]+id[1]+id[2]+id[3];
        }

        @Override
        public boolean equals(Object o) {
            if ( o instanceof RpcCollectorKey ) {
                RpcCollectorKey r = (RpcCollectorKey)o;
                return Arrays.equals(id,r.id);
            } else return false;
        }

    }
}
