package com.lami.tuomatuo.mq.nio3;

import com.lami.mina.mina.Event;
import com.lami.mina.mina.SessionManager;
import com.lami.mina.mina.domain.MinaMsgHead;
import org.apache.mina.core.buffer.IoBuffer;

public class MainActivity  {

    /**
     * 给服务器发送一条消息
     */
    public void sendMsg(){
        /**
         * 假定消息格式为：消息头（一个short类型：表示事件号、一个int类型：表示消息体的长度）+消息体
         */
        MinaMsgHead msgHead = new MinaMsgHead();
        msgHead.event = Event.EV_C_S_TEST;
        msgHead.bodyLen = 0;//因为消息体是空的所以填0，根据消息体的长度而变

        //创建一个缓冲，缓冲大小为:消息头长度(6位)+消息体长度
        IoBuffer buffer = IoBuffer.allocate(6+msgHead.bodyLen);
        //把消息头put进去
        buffer.putInt(msgHead.bodyLen);
        buffer.putShort(msgHead.event);
        //把消息体put进去

        //发送
        SessionManager.getInstance().writeToServer(buffer);
    }

}
