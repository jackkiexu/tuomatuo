package com.lami.tuomatuo.mq.apns;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.netty.buffer.ChannelBuffer;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Push notification
 *
 * Created by xujiankang on 2016/9/26.
 */
public class PushNotification {

    public long id;
    public byte[] token;

    private Object alert;
    private Integer badge;
    private Date expiry;
    private String sound;
    private Map<String, Object> extra;

    public PushNotification(long id, byte[] token) {
        this.id = id;
        this.token = token;
    }

    public PushNotification alert(String message){
        this.alert = message;
        return this;
    }

    public Alert alert(){
        Alert alert = new Alert();
        this.alert = alert;
        return alert;
    }

    public PushNotification badge(int numbet){
        this.badge = numbet;
        return this;
    }

    public PushNotification expiry(Date date){
        this.expiry = date;
        return this;
    }

    public Map<String, Object> extra(){
        this.extra = new HashMap<String, Object>();
        return this.extra;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class Alert{
        @JsonProperty("body")               private String body;
        @JsonInclude(JsonInclude.Include.ALWAYS)
        @JsonProperty("action-loc-key")     private String actionLocKey;
        @JsonProperty("loc-key")            private String locKey;
        @JsonProperty("loc-args")           private String[] locArgs;
        @JsonProperty("launch-image")       private String launchImage;

        public Alert body(String body){
            this.body = body;
            return this;
        }

        public Alert actionLocKey(String body){
            this.body = body;
            return this;
        }

        public Alert locKey(String key){
            this.locKey = key;
            return this;
        }

        public Alert locArgs(String...args){
            this.locArgs = args;
            return this;
        }

        public Alert launchImage(String filename){
            this.launchImage = filename;
            return this;
        }
    }

    public void encode(ObjectMapper mapper, ChannelBuffer buf) throws IOException{
        buf.writeByte(1);
        buf.writeInt((int) id);
        buf.writeInt((int) (expiry != null ? (int) expiry.getTime() / 1000 : 0));
        buf.writeShort(token.length);
        buf.writeBytes(token);

        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> aps = new HashMap<String, Object>();

        if(alert != null) aps.put("alert", alert);
        if(badge != null) aps.put("badge", badge);
        if(sound != null) aps.put("sound", sound);
        if(extra != null) map.putAll(extra);

        map.put("aps", aps);
        byte[] bytes = mapper.writeValueAsBytes(map);
        buf.writeShort(bytes.length);
        buf.writeBytes(bytes);
    }

}
