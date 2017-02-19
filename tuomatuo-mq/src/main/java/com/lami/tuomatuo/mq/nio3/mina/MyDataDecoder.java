package com.lami.tuomatuo.mq.nio3.mina;

/**
 * http://blog.csdn.net/cao478208248/article/details/41778511
 * http://blog.csdn.net/shiweijstl/article/details/25137543
 * http://www.blogjava.net/landon/archive/2013/12/02/407122.html
 * http://www.cnblogs.com/dagangzi/p/4717377.html
 */
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class MyDataDecoder extends CumulativeProtocolDecoder {
    /**
     * ����ֵ����:
     * 1�������ݸպ�ʱ������false����֪���������һ������
     * 2�����ݲ���ʱ��Ҫ��һ�������������ݣ���ʱ����false���������� CumulativeProtocolDecoder
     *      �Ὣ���ݷŽ�IoSession�У����´������ݺ���Զ�ƴװ�ٽ��������doDecode
     * 3�������ݶ�ʱ������true����Ϊ��Ҫ�ٽ��������ݽ��ж�ȡ������Ὣʣ��������ٴ����ͱ����doDecode����
     */
    @Override
    public boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out)
            throws Exception {
        /**
         * �ٶ���Ϣ��ʽΪ����Ϣͷ��int���ͣ���ʾ��Ϣ��ĳ��ȡ�short���ͣ���ʾ�¼��ţ�+ ��Ϣ��
         */
        if (in.remaining() < 4)// �����������ʱ��ʣ�೤��С��4��ʱ��ı������������׳���
        {
            return false;
        }
        if (in.remaining() > 1) {
            //�Ա��̵�reset�����ָܻ�positionλ��
            in.mark();
            ////ǰ6�ֽ��ǰ�ͷ��һ��int��һ��short��������ȡһ��int
            int len = in.getInt();//�Ȼ�ȡ�������ݳ���ֵ

            //�Ƚ���Ϣ���Ⱥ�ʵ���յ��ĳ����Ƿ���ȣ�����-2����Ϊ���ǵ���Ϣͷ�и�shortֵ��ûȡ
            if (len > in.remaining() - 2) {
                //���ֶϰ��������ûָ�positionλ�õ�����ǰ,������һ��, ���������ݣ���ƴ�ճ���������
                in.reset();
                return false;
            } else {
                //��Ϣ�����㹻
                in.reset();//���ûָ�positionλ�õ�����ǰ
                int sumLen = 6 + len;//�ܳ� = ��ͷ+����
                byte[] packArr = new byte[sumLen];
                in.get(packArr, 0, sumLen);
                IoBuffer buffer = IoBuffer.allocate(sumLen);
                buffer.put(packArr);
                buffer.flip();
                out.write(buffer);
                //�ߵ���������DefaultHandler��messageReceived����

                if (in.remaining() > 0) {//����ճ�������ø����ٵ���һ�Σ�������һ�ν���
                    return true;
                }
            }
        }
        return false;//����ɹ����ø�����н����¸���
    }
}