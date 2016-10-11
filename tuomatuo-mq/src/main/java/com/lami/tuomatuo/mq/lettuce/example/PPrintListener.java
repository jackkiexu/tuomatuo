package com.lami.tuomatuo.mq.lettuce.example;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.Date;

/**
 * Created by xjk on 2016/9/12.
 */
public class PPrintListener extends JedisPubSub{

    private static final Logger logger = Logger.getLogger(PPrintListener.class);

    private String clientId;
    private PSubHandler handler;
    // TODO heartbeat ＋ reconnect
    // TODO pipleline tohandler message
    // TODO factory produce the variety handler

    public PPrintListener(String clientId,Jedis jedis){
        this.clientId = clientId;
        handler = new PSubHandler(jedis);
    }

    @Override
    public void onMessage(String channel, String message) {
        //此处我们可以取消订阅
        if(message.equalsIgnoreCase("quit")){
            this.unsubscribe(channel);
        }
        handler.handle(channel, message);//触发当前订阅者从自己的消息队列中移除消息
    }

    private void message(String channel,String message){
        String time = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        logger.info("message receive:" + message + ",channel:" + channel + "..." + time);
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        logger.info("message receive:" + message + ",pattern channel:" + channel);

    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        handler.subscribe(channel);
        logger.info("subscribe:" + channel + ";total channels : " + subscribedChannels);

    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        handler.unsubscribe(channel);
        logger.info("unsubscribe:" + channel + ";total channels : " + subscribedChannels);

    }

    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        logger.info("unsubscribe pattern:" + pattern + ";total channels : " + subscribedChannels);

    }

    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        logger.info("subscribe pattern:" + pattern + ";total channels : " + subscribedChannels);
    }

    @Override
    public void unsubscribe(String... channels) {
        super.unsubscribe(channels);
        for(String channel : channels){
            handler.unsubscribe(channel);
        }
    }

    class PSubHandler {

        private Jedis jedis;
        PSubHandler(Jedis jedis){
            this.jedis = jedis;
        }
        public void handle(String channel,String message){
            int index = message.indexOf("/");
            if(index < 0){
                return;
            }
            Long txid = Long.valueOf(message.substring(0,index));
            String key = clientId + "/" + channel;
            while(true){

                String lm = jedis.lindex(key, 0);//获取第一个消息
                if(lm == null){
                    break;
                }
                int li = lm.indexOf("/");
                //如果消息不合法，删除并处理
                if(li < 0){
                    String result = jedis.lpop(key);//删除当前message
                    //为空
                    if(result == null){
                        break;
                    }
                    message(channel, lm);
                    continue;
                }
                Long lxid = Long.valueOf(lm.substring(0,li));//获取消息的txid
                //直接消费txid之前的残留消息
                if(txid >= lxid){
                    jedis.lpop(key);//删除当前message
                    message(channel, lm);
                    continue;
                }else{
                    break;
                }
            }
        }

        public void subscribe(String channel){
            String key = clientId + "/" + channel;
            boolean exist = jedis.sismember(Constants.SUBSCRIBE_CENTER,key);
            if(!exist){
                jedis.sadd(Constants.SUBSCRIBE_CENTER, key);
            }
        }

        public void unsubscribe(String channel){
            String key = clientId + "/" + channel;
            jedis.srem(Constants.SUBSCRIBE_CENTER, key);//从“活跃订阅者”集合中删除
            jedis.del(key);//删除“订阅者消息队列”
        }
    }
}
