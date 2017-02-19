package com.lami.tuomatuo.mq.nio3;

import com.lami.mina.mina.Event;
import com.lami.mina.mina.SessionManager;
import com.lami.mina.mina.domain.MinaMsgHead;
import org.apache.mina.core.buffer.IoBuffer;

public class MainActivity  {

    /**
     * ������������һ����Ϣ
     */
    public void sendMsg(){
        /**
         * �ٶ���Ϣ��ʽΪ����Ϣͷ��һ��short���ͣ���ʾ�¼��š�һ��int���ͣ���ʾ��Ϣ��ĳ��ȣ�+��Ϣ��
         */
        MinaMsgHead msgHead = new MinaMsgHead();
        msgHead.event = Event.EV_C_S_TEST;
        msgHead.bodyLen = 0;//��Ϊ��Ϣ���ǿյ�������0��������Ϣ��ĳ��ȶ���

        //����һ�����壬�����СΪ:��Ϣͷ����(6λ)+��Ϣ�峤��
        IoBuffer buffer = IoBuffer.allocate(6+msgHead.bodyLen);
        //����Ϣͷput��ȥ
        buffer.putInt(msgHead.bodyLen);
        buffer.putShort(msgHead.event);
        //����Ϣ��put��ȥ

        //����
        SessionManager.getInstance().writeToServer(buffer);
    }

}
