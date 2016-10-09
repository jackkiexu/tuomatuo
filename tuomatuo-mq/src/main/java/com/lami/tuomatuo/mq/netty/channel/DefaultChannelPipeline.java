package com.lami.tuomatuo.mq.netty.channel;

import org.apache.log4j.Logger;

import java.lang.annotation.AnnotationFormatError;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Created by xujiankang on 2016/9/26.
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

    public volatile Channel channel;
    public volatile ChannelSink sink;
    public volatile DefaultChannelHandlerContext head;
    public volatile DefaultChannelHandlerContext tail;
    public Map<String, DefaultChannelHandlerContext> name2ctx =
            new HashMap<String, DefaultChannelHandlerContext>();


    public void addFirst(String name, ChannelHandler handler) {
        if (name2ctx.isEmpty()) {
            init(name, handler);
        } else {
            checkDuplicatename(name);
            DefaultChannelHandlerContext oldHead = head;
            DefaultChannelHandlerContext newHead = new DefaultChannelHandlerContext(null, oldHead, name, handler);
            oldHead.prev = newHead;
            head = newHead;
            name2ctx.put(name, newHead);
        }
    }

    public void addLast(String name, ChannelHandler handler) {
        if(name2ctx.isEmpty()){
            init(name, handler);
        }else{
            checkDuplicatename(name);
            DefaultChannelHandlerContext oldtail = tail;
            DefaultChannelHandlerContext newTail = new DefaultChannelHandlerContext(oldtail, null, name, handler);
            oldtail.next = newTail;
            tail = newTail;
            name2ctx.put(name, newTail);
        }
    }

    public void addBefore(String baseName, String name, ChannelHandler handler) {
        DefaultChannelHandlerContext ctx = getContextOrDie(baseName);
        if(ctx == head){
            addFirst(name, handler);
        }else{
            checkDuplicatename(name);
            DefaultChannelHandlerContext newCtx = new DefaultChannelHandlerContext(ctx.prev, ctx, name, handler);
            ctx.prev.next = newCtx;
            ctx.prev = newCtx;
            name2ctx.put(name, newCtx);
        }
    }

    public void addAfter(String baseName, String name, ChannelHandler handler) {
        DefaultChannelHandlerContext ctx = getContextOrDie(baseName);
        if(ctx == tail){
            addLast(name, handler);
        }else{
            checkDuplicatename(name);
            DefaultChannelHandlerContext newCtx = new DefaultChannelHandlerContext(ctx, ctx.next, name, handler);
            ctx.next.prev = newCtx;
            ctx.next = newCtx;
            name2ctx.put(name, newCtx);
        }
    }

    public void remove(ChannelHandler handler) {
        remove(getContextOrDie(handler));
    }

    public ChannelHandler remove(String name) {
        return remove(getContextOrDie(name)).getHandler();
    }

    public <T extends ChannelHandler> T remove(Class<T> handlerType) {
        return (T)remove(getContextOrDie(handlerType)).getHandler();
    }

    private DefaultChannelHandlerContext remove(DefaultChannelHandlerContext ctx){
        if(head == tail){
            head = tail = null;
            name2ctx.clear();
        }else if(ctx == head){
            removeFirst();
        }else if(ctx == tail){
            removeLast();
        }else{
            DefaultChannelHandlerContext prev = ctx.prev;
            DefaultChannelHandlerContext next = ctx.next;
            prev.next = next;
            next.prev = prev;
            name2ctx.remove(ctx.getName());
        }
        return ctx;
    }

    public ChannelHandler removeFirst() {
        if(name2ctx.isEmpty()){
            throw new NoSuchElementException();
        }

        DefaultChannelHandlerContext oldhead = head;
        oldhead.next.prev = null;
        head = oldhead.next;
        name2ctx.remove(oldhead.getName());
        return oldhead.getHandler();
    }

    public ChannelHandler removeLast() {
        if(name2ctx.isEmpty()){
            throw new NoSuchElementException();
        }
        DefaultChannelHandlerContext oldTail = tail;
        oldTail.prev.next = null;
        tail = oldTail.prev;
        name2ctx.remove(oldTail.getName());
        return oldTail.getHandler();
    }

    public void replace(ChannelHandler oldHandler, String newName, ChannelHandler newHandler) {
        replace(getContextOrDie(oldHandler), newName, newHandler);
    }

    public ChannelHandler replace(String oldName, String newName, ChannelHandler newHandler) {
        return replace(getContextOrDie(oldName), newName, newHandler);
    }

    public <T extends ChannelHandler> T replace(Class<T> oldHandlerType, String newName, ChannelHandler newHandler) {
        return (T) replace(getContextOrDie(oldHandlerType), newName, newHandler);
    }

    private ChannelHandler replace(DefaultChannelHandlerContext ctx, String newName, ChannelHandler newHandler){
        if(ctx == head){
            removeFirst();
            addFirst(newName, newHandler);
        }else if(ctx == tail){
            removeLast();
            addLast(newName, newHandler);
        }else{
            boolean sameName = ctx.getName().equals(newName);
            if(!sameName){
                checkDuplicatename(newName);
            }
            DefaultChannelHandlerContext prev = ctx.prev;
            DefaultChannelHandlerContext next = ctx.next;
            DefaultChannelHandlerContext newCtx = new DefaultChannelHandlerContext(prev, next, newName, newHandler);
            prev.next = newCtx;
            next.prev = newCtx;
            if(!sameName){
                name2ctx.remove(ctx.getName());
                name2ctx.put(newName, newCtx);
            }
        }
        return ctx.getHandler();
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
        DefaultChannelHandlerContext head = getActualUpstreamContext(this.head);
        if(head == null){
            logger.info("The pipeline contains no upstream handlers; discarding :" + e);
            return;
        }
        sendUpstream(head, e);
    }

    void sendUpstream(DefaultChannelHandlerContext ctx, ChannelEvent e){
        try {
            ((ChannelUpstreamHandler)ctx.getHandler()).handlerUpstream(ctx, e);
        } catch (Throwable t) {
            notifyException(e, t);
        }
    }

    public void sendDownstream(ChannelEvent e) {
        DefaultChannelHandlerContext tail = getActualDownstreamContext(this.tail);
        if(tail == null){
            try {
                getSink().eventSunk(this, e);
                return ;
            } catch (Exception e1) {
                e1.printStackTrace();
                notifyException(e, e1);
            }
        }

        sendDownstream(tail, e);
    }

    void sendDownstream(DefaultChannelHandlerContext ctx, ChannelEvent e){
        try {
            ((ChannelDownstreamHandler)ctx.getHandler()).handleDownstream(ctx, e);
        } catch (Throwable t) {
            notifyException(e, t);
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public ChannelSink getSink() {
        ChannelSink sink = this.sink;
        if (sink == null) {
            return discardingSink;
        }
        return sink;
    }

    public void attach(Channel channel, ChannelSink sink) {
        if (channel == null) {
            throw new NullPointerException("channel");
        }
        if (sink == null) {
            throw new NullPointerException("sink");
        }
        if (this.channel != null || this.sink != null) {
            throw new IllegalStateException("attached already");
        }
        this.channel = channel;
        this.sink = sink;
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

            logger.info("next: " + next + ", prev:" + prev + ", name:"+ name + ", handler:"+ handler);
            if(name == null) throw new NullPointerException("name is null");
            if(handler == null) throw new NullPointerException("handler is null");

            // check handler type
            canHandleUpstream = handler instanceof ChannelUpstreamHandler;
            canHandleDownstream = handler instanceof ChannelDownstreamHandler;

            if(!canHandleDownstream && !canHandleUpstream) throw new IllegalArgumentException("handler must be either" + ChannelUpstreamHandler.class.getName() + "or"+ ChannelDownstreamHandler.class.getName());

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
