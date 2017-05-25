package com.lami.tuomatuo.mq.zookeeper.server;

import com.lami.tuomatuo.mq.zookeeper.Login;
import com.lami.tuomatuo.mq.zookeeper.jmx.MBeanRegistry;
import org.apache.zookeeper.server.auth.SaslServerCallbackHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xujiankang on 2017/3/19.
 */
public abstract class ServerCnxnFactory {

    public static final String ZOOKEEPER_SERVER_CNXN_FACTORY = "zookeeper.serverCnxnFactory";

    public interface PacketProcessor{
        public void processPacket(ByteBuffer packet, ServerCnxn src);
    }

    private static final Logger LOG = LoggerFactory.getLogger(ServerCnxnFactory.class);

    // The buffer will cause the connection to be close when we do a send
    static final ByteBuffer closeConn = ByteBuffer.allocate(0);

    public abstract int getLocalPort();

    public abstract Iterable<ServerCnxn> getConnections();

    public int getNumAliveConnections(){
        synchronized (cnxns){
            return cnxns.size();
        }
    }

    public abstract void closeSession(long sessionId);

    public abstract void configure(InetSocketAddress addr, int maxClientCnxns) throws IOException;

    protected SaslServerCallbackHandler saslServerCallbackHandler;

    public Login login;

    // Maximum number of connection allowed from particular host
    public abstract int getMaxClientCnxnsPerHost();
    // Maximum number of connection allowed from particular host
    public abstract void setMaxClientCnxnsPerHost(int max);

    public abstract void startup(ZooKeeperServer zkServer) throws IOException, InterruptedException;

    public abstract void join() throws InterruptedException;

    public abstract void shutdown();

    public abstract void start();

    protected ZooKeeperServer zkServer;

    final public void setZooKeeperServer(ZooKeeperServer zk){
        this.zkServer = zk;
        if(zk != null){
            zk.setServerCnxnFactory(this);
        }
    }

    public abstract void closeAll();

    static public ServerCnxnFactory createFactory() throws IOException{
        String serverCnxnFactoryName = System.getProperty(ZOOKEEPER_SERVER_CNXN_FACTORY);
        if(serverCnxnFactoryName == null){
            serverCnxnFactoryName = NIOServerCnxnFactory.class.getName();
        }

        try{
            return (ServerCnxnFactory)Class.forName(serverCnxnFactoryName).newInstance();
        }catch (Exception e){
            IOException ioe = new IOException("Couldn't instantiate "
                    + serverCnxnFactoryName);
            ioe.initCause(e);
            throw ioe;
        }
    }

    static public ServerCnxnFactory createFactory(int clientPort,
                                                  int maxClientCnxns) throws IOException{
        return createFactory(new InetSocketAddress(clientPort), maxClientCnxns);
    }

    static public ServerCnxnFactory createFactory(InetSocketAddress addr,
                                                  int maxClientCnxns) throws IOException{
        ServerCnxnFactory factory = createFactory();
        factory.configure(addr, maxClientCnxns);
        return factory;
    }

    public abstract InetSocketAddress getlocalAddress();

    private final Map<ServerCnxn, ConnectionBean> connectionBeans = new ConcurrentHashMap<>();

    protected final HashSet<ServerCnxn> cnxns = new HashSet<>();

    public void unregisterConnection(ServerCnxn serverCnxn){
        ConnectionBean jmxConnectionbean = connectionBeans.remove(serverCnxn);
        if(jmxConnectionbean != null){
            MBeanRegistry.getInstance().unregister(jmxConnectionbean);
        }
    }

    public void registerConnection(ServerCnxn serverCnxn){
        if(zkServer != null){
            ConnectionBean jmxConnectionBean = new ConnectionBean(serverCnxn, zkServer);
            try{
                MBeanRegistry.getInstance().register(jmxConnectionBean, zkServer.jmxServerBean);
                connectionBeans.put(serverCnxn, jmxConnectionBean);
            }catch (Exception e){
                LOG.info("Could not register connection", e);
            }
        }
    }
}
