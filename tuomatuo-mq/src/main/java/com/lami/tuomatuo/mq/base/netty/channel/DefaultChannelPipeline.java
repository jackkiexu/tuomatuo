package com.lami.tuomatuo.mq.base.netty.channel;

import org.apache.log4j.Logger;

import java.lang.annotation.AnnotationFormatError;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 */
public class DefaultChannelPipeline implements ChannelPipeline {

    private static final Logger logger = Logger.getLogger(DefaultChannelPipeline.class);

    private ChannelSink discardingSink = new ChannelSink() {
        public void eventSunk(ChannelPipeline pipline, ChannelEvent e) throws Exception {
            logger.info("Not attached yet: discarding:" + e);
        }

        public void exceptionCaught(ChannelPipeline pipeline, ChannelEvent e, ChannelPipelineException cause) throws Exception {
            logger.info(cause);
        }
    };

    private volatile Channel channel;
    private volatile ChannelSink sink;
    private volatile DefaultChannelHandlerContext head;
    private volatile DefaultChannelHandlerContext tail;
    private Map<String, DefaultChannelHandlerContext> name2ctx =
            new HashMap<String, DefaultChannelHandlerContext>();



    public void addFirst(String name, ChannelHandler handler) {

    }

    public void addLast(String name, ChannelHandler handler) {

    }

    public void addBefore(String baseName, String name, ChannelHandler handler) {

    }

    public void addAfter(String baseName, String name, ChannelHandler handler) {

    }

    public void remove(ChannelHandler handler) {

    }

    public ChannelHandler remove(String name) {
        return null;
    }

    public <T extends ChannelHandler> T remove(Class<T> handlerType) {
        return null;
    }

    public ChannelHandler removeFirst() {
        return null;
    }

    public ChannelHandler removeLast() {
        return null;
    }

    public void replace(ChannelHandler oldHandler, String newName, ChannelHandler newHandler) {

    }

    public ChannelHandler replace(String oldName, String newName, ChannelHandler newHandler) {
        return null;
    }

    public <T extends ChannelHandler> T replace(Class<T> oldHandlerType, String newName, ChannelHandler newHandler) {
        return null;
    }

    public ChannelHandler getFirst() {
        return head.getHandler();
    }

    public ChannelHandler getLast() {
        return tail.getHandler();
    }

    public ChannelHandler get(String name) {
        DefaultChannelHandlerContext ctx = name2ctx.get(name);
        if(ctx == null){
            return null;
        }else{
            return ctx.getHandler();
        }
    }

    public <T extends ChannelHandler> T get(Class<T> handlerType) {
        ChannelHandlerContext ctx = getContext(handlerType);
        if(ctx == null){
            return null;
        }else{
            return (T)ctx.getHandler();
        }
    }

    public ChannelHandlerContext getContext(ChannelHandler handler) {
        if(handler == null){
            throw new NullPointerException("handler");
        }
        if(name2ctx.isEmpty()){
            return null;
        }
        DefaultChannelHandlerContext ctx = head;
        for(;;){
            if(ctx.getHandler() == handler){
                return ctx;
            }

            ctx = ctx.next;
            if(ctx == null){
                break;
            }
        }
        return null;
    }

    public ChannelHandlerContext getContext(String name) {
        if(name == null){
            throw new NullPointerException("name");
        }
        return name2ctx.get(name);
    }

    public ChannelHandlerContext getContext(Class<? extends ChannelHandler> handlerType) {
        if(name2ctx.isEmpty()){
            return null;
        }
        DefaultChannelHandlerContext ctx = head;
        for(;;){
            if(handlerType.isAssignableFrom(ctx.getHandler().getClass())){
                return ctx;
            }
            ctx = ctx.next;
            if(ctx == null){
                break;
            }
        }

        return null;
    }

    public void sendUpstream(ChannelEvent e) {

    }

    void sendUpstream(DefaultChannelHandlerContext ctx, ChannelEvent e){
        try {
            ((ChannelUpstreamHandler)ctx.getHandler()).handlerUpstream(ctx, e);
        } catch (Throwable t) {
            notifyException(e, t);
        }
    }

    public void sendDownstream(ChannelEvent e) {

    }

    void sendDownstream(DefaultChannelHandlerContext ctx, ChannelEvent e){
        try {
            ((ChannelDownstreamHandler)ctx.getHandler()).handleDownstream(ctx, e);
        } catch (Throwable t) {
            notifyException(e, t);
        }
    }

    public Channel getChannel() {
        return null;
    }

    public ChannelSink getSink() {
        return null;
    }

    public void attach(Channel channel, ChannelSink sink) {

    }

    public Map<String, ChannelHandler> toMap() {
        Map<String, ChannelHandler> map = new LinkedHashMap<String, ChannelHandler>();
        if(name2ctx.isEmpty()){
            return map;
        }

        DefaultChannelHandlerContext ctx = head;
        for(;;){
            map.put(ctx.getName(), ctx.getHandler());
            ctx = ctx.next;
            if(ctx == null){
                break;
            }
        }
        return map;
    }


    DefaultChannelHandlerContext getActualUpstreamContext(DefaultChannelHandlerContext ctx){
        if(ctx == null){
            return null;
        }

        DefaultChannelHandlerContext realCtx = ctx;
        while(!realCtx.canHandleUpstream()){
            realCtx = realCtx.next;
            if(realCtx == null) return null;
        }
        return realCtx;
    }

    DefaultChannelHandlerContext getActualDownstreamContext(DefaultChannelHandlerContext ctx){
        if(ctx == null){
            return null;
        }

        DefaultChannelHandlerContext realCtx = ctx;
        while(!realCtx.canHandleDownstream()){
            realCtx = realCtx.prev;
            if(realCtx == null){
                return null;
            }
        }

        return realCtx;
    }

    void notifyException(ChannelEvent e, Throwable t){
        ChannelPipelineException pe;
        if(t instanceof ChannelPipelineException){
            pe = (ChannelPipelineException) t;
        }else {
            pe = new ChannelPipelineException(t);
        }

        try {
            sink.exceptionCaught(this, e, pe);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void init(String name, ChannelHandler handler){
        DefaultChannelHandlerContext ctx = new DefaultChannelHandlerContext(null, null, name, handler);
        head = tail = ctx;
        name2ctx.clear();
        name2ctx.put(name, ctx);
    }

    private void checkDuplicatename(String name){
        if(name2ctx.containsKey(name)){
            throw new IllegalArgumentException("Duplicate handler name");
        }
    }

    private DefaultChannelHandlerContext getContextOrDie(String name){
        DefaultChannelHandlerContext ctx = (DefaultChannelHandlerContext)getContext(name);
        if(ctx == null){
            throw new NoSuchElementException(name);
        }else{
            return ctx;
        }
    }

    private DefaultChannelHandlerContext getContextOrDie(ChannelHandler handler){
        DefaultChannelHandlerContext ctx = (DefaultChannelHandlerContext)getContext(handler);
        if(ctx == null){
            throw new NoSuchElementException(handler.getClass().getName());
        }else{
            return ctx;
        }
    }

    private DefaultChannelHandlerContext getContextOrDie(Class<? extends ChannelHandler> handlerType){
        DefaultChannelHandlerContext ctx = (DefaultChannelHandlerContext)getContext(handlerType);
        if(ctx == null){
            throw new NoSuchElementException(handlerType.getName());
        }else{
            return ctx;
        }
    }

    private class DefaultChannelHandlerContext implements ChannelHandlerContext{

        volatile DefaultChannelHandlerContext next;
        volatile DefaultChannelHandlerContext prev;
        private String name;
        private ChannelHandler handler;
        private boolean canHandleUpstream;
        private boolean canHandleDownstream;

        public DefaultChannelHandlerContext(DefaultChannelHandlerContext next, DefaultChannelHandlerContext prev, String name, ChannelHandler handler) {

            if(name == null) throw new NullPointerException("name is null");
            if(handler == null) throw new NullPointerException("handler is null");

            canHandleUpstream = handler instanceof ChannelUpstreamHandler;
            canHandleDownstream = handler instanceof ChannelDownstreamHandler;

            if(!canHandleDownstream && !canHandleUpstream){
                throw new IllegalArgumentException("handler must be either" + ChannelUpstreamHandler.class.getName() + "or"+ ChannelDownstreamHandler.class.getName());
            }

            ChannelPipelineCoverage coverage = handler.getClass().getAnnotation(ChannelPipelineCoverage.class);
            if(coverage == null){
                logger.info("Handler " + handler.getClass().getName() + " doesn't have a " +
                ChannelPipelineCoverage.class.getName() +" annotation with its class declaration. It is recommended to add the annotation to tell if one handler instance can handler more than one pipeline ALL or not ONE");
            }else{
                String coverageValue = coverage.value();
                if(coverageValue == null){
                    throw new AnnotationFormatError(ChannelPipelineCoverage.class.getName() + " annotation value is undefined for type : " + handler.getClass().getName());
                }
                if(!coverageValue.equals(ChannelPipelineCoverage.ALL) && !coverageValue.equals(ChannelPipelineCoverage.ONE)){
                    throw new AnnotationFormatError(ChannelPipelineCoverage.class.getName() + " annotation value is undefined for type : " + handler.getClass().getName());
                }
            }

            this.next = next;
            this.prev = prev;
            this.name = name;
            this.handler = handler;
        }

        public ChannelPipeline getPipeline() {
            return DefaultChannelPipeline.this;
        }

        public String getName() {
            return name;
        }

        public ChannelHandler getHandler() {
            return handler;
        }

        public boolean canHandleUpstream() {
            return canHandleUpstream;
        }

        public boolean canHandleDownstream() {
            return canHandleDownstream;
        }

        public void sendDownstream(ChannelEvent e) {
            DefaultChannelHandlerContext prev = getActualDownstreamContext(this.prev);
            if(prev == null){
                try {
                    getSink().eventSunk(DefaultChannelPipeline.this, e);
                } catch (Throwable t) {
                    notifyException(e, t);
                }
            }else{
                DefaultChannelPipeline.this.sendDownstream(prev, e);
            }
        }

        public void sendUpstream(ChannelEvent e){
            DefaultChannelHandlerContext next = getActualUpstreamContext(this.next);
            if(next != null){
                DefaultChannelPipeline.this.sendUpstream(next, e);
            }
        }
    }
}
