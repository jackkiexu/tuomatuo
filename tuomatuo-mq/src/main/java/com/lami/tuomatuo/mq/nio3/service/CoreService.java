package com.lami.tuomatuo.mq.nio3.service;

import com.lami.mina.mina.ConnectionConfig;
import com.lami.mina.mina.ConnectionManager;
import org.apache.log4j.Logger;


public class CoreService {

    private static final Logger logger = Logger.getLogger(CoreService.class);

    public static final String TAG="CoreService";
    private ConnectionThread thread;
    ConnectionManager mManager;

    public CoreService() {
    }

    public void onCreate() {
        ConnectionConfig config = new ConnectionConfig.Builder()
                .setIp("192.168.0.55")//���ӵ�IP��ַ
                .setPort(8888)//���ӵĶ˿ں�
                .setReadBufferSize(1024)
                .setConnectionTimeout(10000).builder();
        mManager = new ConnectionManager(config);
        thread = new ConnectionThread();
        thread.start();
    }

    class ConnectionThread extends Thread {
        boolean isConnection;
        @Override
        public void run() {
            for (;;){
                isConnection = mManager.connect();
                if (isConnection) {
                    logger.info("���ӳɹ�����ѭ��");
                    break;
                }
                try {
                    logger.info("������������");
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void disConnect() {
        mManager.disConnect();
    }

    public void onDestroy() {
        disConnect();
        thread = null;
    }
}
