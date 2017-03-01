package com.lami.tuomatuo.mq.nio3.mina;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;

public class ConnectionManager {

    private static final Logger logger = Logger.getLogger(ConnectionManager.class);

    public static final String TAG = "ConnectionManager";
    private ConnectionConfig mConfig;
    private WeakReference<Object> mContext;
    public NioSocketConnector mConnection;
    private IoSession mSession;
    private InetSocketAddress mAddress;

    public ConnectionManager(ConnectionConfig config){

        this.mConfig = config;
        this.mContext = new WeakReference<Object>("");
        init();
    }

    private void init() {
        mAddress = new InetSocketAddress(mConfig.getIp(), mConfig.getPort());
        mConnection = new NioSocketConnector();
        mConnection.getSessionConfig().setReadBufferSize(mConfig.getReadBufferSize());
        mConnection.getSessionConfig().setReaderIdleTime(60*5);
        mConnection.getSessionConfig().setWriterIdleTime(60*5);
        mConnection.getSessionConfig().setBothIdleTime(60*5);
        mConnection.getFilterChain().addFirst("reconnection", new MyIoFilterAdapter());
        mConnection.getFilterChain().addLast("mycoder", new ProtocolCodecFilter(new MyCodecFactory()));
        mConnection.setHandler(new DefaultHandler(mContext));
        mConnection.setDefaultRemoteAddress(mAddress);
    }

    /**
     */
    public boolean connect(){
        try{
            ConnectFuture future = mConnection.connect();
            future.awaitUninterruptibly();
            mSession = future.getSession();
            if(mSession!=null && mSession.isConnected()) {
                SessionManager.getInstance().setSession(mSession);
            }else {
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * �Ͽ�����
     */
    public void disConnect(){
        mConnection.dispose();
        mConnection=null;
        mSession=null;
        mAddress=null;
        mContext = null;
    }

    private class DefaultHandler extends IoHandlerAdapter {

        private Object mContext;
        private DefaultHandler(Object context){
            this.mContext = context;
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            super.sessionOpened(session);
            logger.info("���Ӵ�");
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            logger.info("�յ����ݣ���������Ҫ��ô�������ݾ����������");
            IoBuffer buf = (IoBuffer) message;
            HandlerEvent.getInstance().handle(buf);
        }

        @Override
        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
            super.sessionIdle(session, status);
            logger.info("-�ͻ������������ӿ���");
            if(session != null){
                session.closeOnFlush();
            }
        }
    }

    private  class MyIoFilterAdapter extends IoFilterAdapter {
        @Override
        public void sessionClosed(NextFilter nextFilter, IoSession session) throws Exception {
            logger.info("���ӹرգ�ÿ��5�������������");
            for(;;){
                if(mConnection==null){
                    break;
                }
                if(ConnectionManager.this.connect()){
                    logger.info("��������[" + mConnection.getDefaultRemoteAddress().getHostName() + ":" +
                            mConnection.getDefaultRemoteAddress().getPort() + "]�ɹ�");
                    break;
                }
                Thread.sleep(5000);
            }
        }

    }
}
