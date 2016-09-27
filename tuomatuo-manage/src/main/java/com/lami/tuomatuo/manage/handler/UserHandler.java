package com.lami.tuomatuo.manage.handler;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

public class UserHandler {
	Logger logger = Logger.getLogger(UserHandler.class);
	public Object RecordLog(ProceedingJoinPoint point) throws Throwable {
		 String targetName = point.getTarget().getClass().getName();  
	        String methodName = point.getSignature().getName();  
	        Object[] arguments = point.getArgs();  
	  
	        Class targetClass = Class.forName(targetName);  
	        Method[] method = targetClass.getMethods();  
	        String methode = "";  
	        /*for (Method m : method) {  
	            if (m.getName().equals(methodName)) {  
	                Class[] tmpCs = m.getParameterTypes();  
	                if (tmpCs.length == arguments.length) {  
	                    MethodLog methodCache = m.getAnnotation(MethodLog.class);  
	                    methode = methodCache.remark();  
	                    break;  
	                }  
	            }  
	        }  */
	        logger.info(targetName+","+methodName+","+arguments);
		return point;
		
	}
}
