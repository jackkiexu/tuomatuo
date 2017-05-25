package com.lami.tuomatuo.mq.zookeeper.server.quorum;

/**
 * Created by xujiankang on 2017/3/19.
 */
public class QuorumStats {

    private final Provider provider;

    public interface Provider {
        static public final String UNKNOWN_STATE = "unknown";
        static public final String LOOKING_STATE = "leaderelection";
        static public final String LEADING_STATE = "leading";
        static public final String FOLLOWING_STATE = "following";
        static public final String OBSERVING_STATE = "observing";
        public String[] getQuorumPeers();
        public String getServerState();
    }

    public QuorumStats(Provider provider) {
        this.provider = provider;
    }

    public String getServerState(){
        return provider.getServerState();
    }

    public String[] getQuorumPeers() {
        return provider.getQuorumPeers();
    }

    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder(super.toString());
        String state=getServerState();
        if(state.equals(Provider.LEADING_STATE)){
            sb.append("Followers:");
            for(String f: getQuorumPeers()){
                sb.append(" ").append(f);
            }
            sb.append("\n");
        }else if(state.equals(Provider.FOLLOWING_STATE)
                || state.equals(Provider.OBSERVING_STATE)){
            sb.append("Leader: ");
            String[] ldr=getQuorumPeers();
            if(ldr.length>0)
                sb.append(ldr[0]);
            else
                sb.append("not connected");
            sb.append("\n");
        }
        return sb.toString();
    }
}
