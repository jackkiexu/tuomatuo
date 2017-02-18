package com.lami.tuomatuo.mq.nio3.mina;

import com.lami.mina.mina.domain.MinaMsgHead;
import org.apache.mina.core.buffer.IoBuffer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;


/**
 * ��Ϣ�¼�����
 */
public class HandlerEvent {
    private static HandlerEvent handlerEvent;
    public static HandlerEvent getInstance() {
        if (handlerEvent == null) {
            handlerEvent = new HandlerEvent();
        }
        return handlerEvent;
    }
    public void handle(IoBuffer buf) throws IOException, InterruptedException, UnsupportedEncodingException, SQLException {
        //������ͷ
        MinaMsgHead msgHead = new MinaMsgHead();
        msgHead.bodyLen = buf.getInt();//���峤��
        msgHead.event = buf.getShort();//�¼���

        switch (msgHead.event){//�����¼��Ž�����Ϣ������
            case Event.EV_S_C_TEST:
                byte[] by = new byte[msgHead.bodyLen];
                buf.get(by, 0, by.length);
                String json = new String(by, "UTF-8").trim();
                //����������ҵ����....
                break;
        }
    }
}
