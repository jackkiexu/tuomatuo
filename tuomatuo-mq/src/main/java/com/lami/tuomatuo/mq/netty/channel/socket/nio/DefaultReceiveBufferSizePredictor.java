package com.lami.tuomatuo.mq.netty.channel.socket.nio;

/**
 * Created by xjk on 2016/9/27.
 */
public class DefaultReceiveBufferSizePredictor implements ReceiveBufferSizePredictor {

    private static int DEFAULT_MINIMUM = 256;
    private static int DEFAULT_INITIAL = 1024;
    private static int DEFAULT_MAXIMUM = 1048576;

    private int minimum;
    private int maximum;
    private int nextReceiveBufferSize = 1024;
    private boolean shouldHalveNow;

    public DefaultReceiveBufferSizePredictor() {
        this(DEFAULT_MINIMUM, DEFAULT_INITIAL, DEFAULT_MAXIMUM);
    }

    public DefaultReceiveBufferSizePredictor(int minimum, int initial, int maximum) {
        if(minimum <= 0) throw new IllegalArgumentException("minimum : " + minimum);
        if(initial <= minimum) throw new IllegalArgumentException("initial : " + initial);
        if(maximum <= initial) throw new IllegalArgumentException("maximum : " + maximum);

        this.minimum = minimum;
        nextReceiveBufferSize = initial;
        this.maximum = maximum;
    }

    public int nextReceiveBufferSize() {
        return nextReceiveBufferSize;
    }

    public void previousReceiveBufferSize(int previousReceiveBufferSize) {
        if(previousReceiveBufferSize < nextReceiveBufferSize >>> 1){
            if(shouldHalveNow){
                nextReceiveBufferSize = Math.max(minimum, nextReceiveBufferSize >>> 1);
                shouldHalveNow = false;
            }else{
                shouldHalveNow = true;
            }
        }else if(previousReceiveBufferSize == nextReceiveBufferSize){
            nextReceiveBufferSize = Math.min(maximum, nextReceiveBufferSize << 1);
            shouldHalveNow = false;
        }
    }
}
