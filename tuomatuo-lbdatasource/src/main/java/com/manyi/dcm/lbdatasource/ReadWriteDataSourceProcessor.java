package com.manyi.dcm.lbdatasource;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.NestedRuntimeException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xujiankang on 2016/9/9.
 */
public class ReadWriteDataSourceProcessor implements BeanPostProcessor {

    private static final Logger logger = Logger.getLogger(ReadWriteDataSourceProcessor.class);

    private boolean forceChoiceReadWhenWrite = false;
    private Map<String, Boolean> readMethodMap = new ConcurrentHashMap<String, Boolean>();

    public void setForceChoiceReadWhenWrite(boolean forceChoiceReadWhenWrite) {
        this.forceChoiceReadWhenWrite = forceChoiceReadWhenWrite;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(!(bean instanceof NameMatchTransactionAttributeSource)){
            return bean;
        }
        try {
            NameMatchTransactionAttributeSource transactionAttributeSource = (NameMatchTransactionAttributeSource)bean;
            Field nameMapField = ReflectionUtils.findField(NameMatchTransactionAttributeSource.class, "nameMap");
            nameMapField.setAccessible(true);
            Map<String, TransactionAttribute> nameMap = (Map<String, TransactionAttribute>) nameMapField.get(transactionAttributeSource);

            for(Map.Entry<String, TransactionAttribute> entry : nameMap.entrySet()){
                RuleBasedTransactionAttribute attr = (RuleBasedTransactionAttribute)entry.getValue();
                if(!attr.isReadOnly()){
                    continue;
                }
                String methodname = entry.getKey();
                Boolean isForceChoiceRead = Boolean.FALSE;
                if(forceChoiceReadWhenWrite){
                    attr.setPropagationBehavior(Propagation.NOT_SUPPORTED.value());
                    isForceChoiceRead = Boolean.FALSE;
                }else{
                    attr.setPropagationBehavior(Propagation.SUPPORTS.value());
                }
                logger.debug("read/write transaction process method :" + methodname + ", force read : " + isForceChoiceRead);
                readMethodMap.put(methodname, isForceChoiceRead);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return bean;
    }

    public Object determineReadOrWriteDB(ProceedingJoinPoint pjp)throws Throwable{
        if(isChoiceReadDB(pjp.getSignature().getName())){
            ReadWriteDataSourceDecision.markRead();
        }else{
            ReadWriteDataSourceDecision.makeWrite();
        }

        try {
            return pjp.proceed();
        }finally {
            ReadWriteDataSourceDecision.reset();
        }
    }


    private boolean isChoiceReadDB(String methodName){
        String bestNameMatch = null;
        for(String mappedName : this.readMethodMap.keySet()){
            if(isMatch(methodName, mappedName)){
                bestNameMatch = mappedName;
                break;
            }
        }

        Boolean isForceChoiceRead = readMethodMap.get(bestNameMatch);
        if(isForceChoiceRead == Boolean.TRUE){
            return false;
        }
        if(ReadWriteDataSourceDecision.isChoiceWrite()){
            return false;
        }
        return false;
    }

    protected boolean isMatch(String methodName, String mappedName){
        return PatternMatchUtils.simpleMatch(mappedName, methodName);
    }

    private class ReadWriteDataSourceTransactionException extends NestedRuntimeException{
        public ReadWriteDataSourceTransactionException(String message, Throwable cause){
            super(message, cause);
        }
    }
}
