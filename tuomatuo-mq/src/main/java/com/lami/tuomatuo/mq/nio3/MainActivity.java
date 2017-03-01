package com.lami.tuomatuo.mq.nio3;

import com.lami.tuomatuo.mq.nio3.mina.Event;
import com.lami.tuomatuo.mq.nio3.mina.SessionManager;
import com.lami.tuomatuo.mq.nio3.mina.domain.MinaMsgHead;
import org.apache.mina.core.buffer.IoBuffer;

public class MainActivity  {

    /**
     * ������������һ����Ϣ
     */
    public void sendMsg(){
        /**
         */
        MinaMsgHead msgHead = new MinaMsgHead();
        msgHead.event = Event.EV_C_S_TEST;
        msgHead.bodyLen = 0;

        IoBuffer buffer = IoBuffer.allocate(6+msgHead.bodyLen);
        buffer.putInt(msgHead.bodyLen);
        buffer.putShort(msgHead.event);
        SessionManager.getInstance().writeToServer(buffer);
    }

}
