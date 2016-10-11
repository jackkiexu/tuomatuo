package com.lami.tuomatuo.interceptor;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xjk on 2015/9/16.
 */
public class ControllerLogAspect {

    private static final Logger logger = Logger.getLogger(ControllerLogAspect.class);

    public void doBefore(JoinPoint jp){}

    public Object doAround(ProceedingJoinPoint pjp) throws Throwable{
        long time = System.currentTimeMillis();
        Map<String, Map<String, String>> logMap = gatherParams(pjp);
        Object retVal = pjp.proceed();
        logMap.get("methodTrack").put("process time", (System.currentTimeMillis() - time) + "");
        logMap.get("methodTrack").put("retVal", (retVal != null)?retVal.toString():"null");
        logger.info(logMap);
        return retVal;
    }

    public void doAfter(JoinPoint jp){}

    public void doThrowing(JoinPoint jp, Throwable ex){
        logger.info(gatherParams(jp) + " throw exception");
        ex.printStackTrace();
    }

    private Map<String, Map<String, String>> gatherParams(JoinPoint jp){
        HttpServletRequest request = ((org.springframework.web.context.request.ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Map<String, Map<String, String>> logMap = new HashMap<String, Map<String, String>>();
        logMap.put("header", parseRequestHeader(request));
        logMap.put("requestParams", requestParams(request));
        try {
            logMap.put("methodTrack", getMethodTrack(jp));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logMap;
    }

    /**
     * 获取请求参数
     * @param request
     * @return
     */
    private Map<String, String> requestParams(HttpServletRequest request){
        Map<String, String> requestParamMap = new HashMap<String, String>();
        @SuppressWarnings("unchecked")
        Map<String, String[]> paramMap = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : paramMap.entrySet()){
            String key = entry.getKey();
            String value = "";
            Object valueObj = entry.getValue();
            if (valueObj == null){
                value = "";
            }else if (valueObj instanceof  String[]){
                String[] values = (String[])valueObj;
                for(int i = 0; i < values.length; i++){
                    value += values[i] + ",";
                }
                value = value.substring(0, value.length()-1);
            }else{
                value = valueObj.toString();
            }
            requestParamMap.put(key, value);
        }
        return requestParamMap;
    }

    private Map<String, String> parseRequestHeader(HttpServletRequest request){
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("ip", request.getRemoteAddr());
        @SuppressWarnings("unchecked")
        Enumeration<String> headerEnum = request.getHeaderNames();
        if (headerEnum == null || headerEnum.hasMoreElements() == false) return headerMap;
        String headerName = "";
        for(; headerEnum.hasMoreElements();headerName = headerEnum.nextElement()){
            @SuppressWarnings("unchecked")
            Enumeration<String> enumeration = request.getHeaders(headerName);
            StringBuilder values = new StringBuilder();
            if (enumeration != null && enumeration.hasMoreElements()){
                for(;enumeration.hasMoreElements();values.append(enumeration.nextElement()+" ")){}
            }
            if(!StringUtils.isEmpty(values.toString())) headerMap.put(headerName, values.toString());
        }
        return headerMap;
    }

    private Map<String, String> getMethodTrack(JoinPoint joinPoint) throws Exception{
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();

        @SuppressWarnings("rawtypes")
        Class targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        for(Method m : methods){
            if(m.getName().equals(methodName)){
                RequestMapping requestMapping = m.getAnnotation(RequestMapping.class);
                if(requestMapping != null){
                    Map<String, String> methodTrackMap = new HashMap<String, String>();
                    methodTrackMap.put("className", targetName);
                    methodTrackMap.put("methodName", methodName);
                    if (arguments != null) methodTrackMap.put("arguments", Arrays.asList(arguments).toString());
                    return methodTrackMap;
                }
            }
        }
        return new HashMap<String, String>();
    }
}
