package com.lami.tuomatuo.mq.netty.bootstrap;

import com.lami.tuomatuo.mq.netty.channel.*;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by xjk on 2016/9/29.
 */
public class Bootstrap {

    private static final Logger logger = Logger.getLogger(Bootstrap.class);

    public volatile ChannelFactory factory;
    public volatile ChannelPipeline pipeline = Channels.pipeline();
    public volatile ChannelPipelineFactory pipelineFactory = Channels.pipelineFactory(pipeline);
    public volatile Map<String, Object> options = new HashMap<String, Object>();

    public Bootstrap() {
        super();
    }

    public Bootstrap(ChannelFactory factory) {
        setFactory(factory);
    }

    public ChannelFactory getFactory(){
        ChannelFactory factory = this.factory;
        if(factory == null){
            throw new IllegalStateException("factory is not set yet");
        }
        return factory;
    }

    public void setFactory(ChannelFactory factory){
        if(this.factory != null){
            throw new IllegalStateException("factory can't change once set");
        }
        if(factory == null){
            throw new NullPointerException("factory");
        }
        this.factory = factory;
    }

    public static ChannelPipeline pipeline() {
        return new DefaultChannelPipeline();
    }

    public ChannelPipeline getPipeline(){
        return pipeline;
    }

    public void setPipeline(ChannelPipeline pipeline){
        if(pipeline == null){
            throw new NullPointerException("pipeLine");
        }
        pipeline = this.pipeline;
        pipelineFactory = Channels.pipelineFactory(pipeline);
    }

    public Map<String, ChannelHandler> getPipeLineMap(){
        ChannelPipeline pipeline = this.pipeline;
        if(pipeline == null){
            throw new IllegalStateException("pipelineFactory is use");
        }
        return pipeline.toMap();
    }

    public Map<String, ChannelHandler> getPipelineAsMap() {
        ChannelPipeline pipeline = this.pipeline;
        if (pipeline == null) {
            throw new IllegalStateException("pipelineFactory in use");
        }
        return pipeline.toMap();
    }

    public void setPipelineAsMap(Map<String, ChannelHandler> pipelineMap) {
        if (pipelineMap == null) {
            throw new NullPointerException("pipelineMap");
        }

        if (!isOrderedMap(pipelineMap)) {
            throw new IllegalArgumentException(
                    "pipelineMap is not an ordered map. " +
                            "Please use " +
                            LinkedHashMap.class.getName() + ".");
        }

        ChannelPipeline pipeline = Channels.pipeline();
        for(Map.Entry<String, ChannelHandler> e: pipelineMap.entrySet()) {
            pipeline.addLast(e.getKey(), e.getValue());
        }

        setPipeline(pipeline);
    }

    public ChannelPipelineFactory getPipelineFactory() {
        return pipelineFactory;
    }

    public void setPipelineFactory(ChannelPipelineFactory pipelineFactory) {
        if (pipelineFactory == null) {
            throw new NullPointerException("pipelineFactory");
        }
        pipeline = null;
        this.pipelineFactory = pipelineFactory;
    }

    public Map<String, Object> getOptions() {
        return new TreeMap<String, Object>(options);
    }

    public void setOptions(Map<String, Object> options) {
        if (options == null) {
            throw new NullPointerException("options");
        }
        this.options = new HashMap<String, Object>(options);
    }

    public Object getOption(String key){
        if(key == null){
            throw new NullPointerException("key");
        }
        return options.get(key);
    }

    public void setOption(String key, Object value){
        if(key == null){
            throw new NullPointerException("key");
        }
        if(value == null){
            options.remove(key);
        }else{
            options.put(key, value);
        }

    }

    private static boolean isOrderedMap(Map<String, ChannelHandler> map) {
        Class<Map<String, ChannelHandler>> mapType = getMapClass(map);
        if (LinkedHashMap.class.isAssignableFrom(mapType)) {
            if (logger.isDebugEnabled()) {
                logger.debug(mapType.getSimpleName() + " is an ordered map.");
            }
            return true;
        }

        if (logger.isDebugEnabled()) {
            logger.debug(
                    mapType.getName() + " is not a " +
                            LinkedHashMap.class.getSimpleName());
        }

        // Detect Apache Commons Collections OrderedMap implementations.
        Class<?> type = mapType;
        while (type != null) {
            for (Class<?> i: type.getInterfaces()) {
                if (i.getName().endsWith("OrderedMap")) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(
                                mapType.getSimpleName() +
                                        " is an ordered map (guessed from that it " +
                                        " implements OrderedMap interface.)");
                    }
                    return true;
                }
            }
            type = type.getSuperclass();
        }

        if (logger.isDebugEnabled()) {
            logger.debug(
                    mapType.getName() +
                            " doesn't implement OrderedMap interface.");
        }

        // Last resort: try to create a new instance and test if it maintains
        // the insertion order.
        logger.debug(
                "Last resort; trying to create a new map instance with a " +
                        "default constructor and test if insertion order is " +
                        "maintained.");

        Map<String, ChannelHandler> newMap;
        try {
            newMap = mapType.newInstance();
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                        "Failed to create a new map instance of '" +
                                mapType.getName() +"'.", e);
            }
            return false;
        }

        Random rand = new Random();
        List<String> expectedNames = new ArrayList<String>();
        ChannelHandler dummyHandler = new SimpleChannelHandler();
        for (int i = 0; i < 65536; i ++) {
            String filterName;
            do {
                filterName = String.valueOf(rand.nextInt());
            } while (newMap.containsKey(filterName));

            newMap.put(filterName, dummyHandler);
            expectedNames.add(filterName);

            Iterator<String> it = expectedNames.iterator();
            for (Object key: newMap.keySet()) {
                if (!it.next().equals(key)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(
                                "The specified map didn't pass the insertion " +
                                        "order test after " + (i + 1) + " tries.");
                    }
                    return false;
                }
            }
        }

        logger.debug("The specified map passed the insertion order test.");
        return true;
    }

    private static Class<Map<String, ChannelHandler>> getMapClass(Map<String, ChannelHandler> map){
        return (Class<Map<String, ChannelHandler>>) map.getClass();
    }
}
