package com.lami.tuomatuo.manage.bean;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**  
 * 
 * @author ben 
 * @version 1.0   
 * 文件名称：DataSourceInterceptor.java  
 */
public class DataSourceInterceptor implements BeanPostProcessor {
	private static final Logger logger = Logger.getLogger(DataSourceInterceptor.class);
	
	public void setMasterService(JoinPoint point) {
		logger.info("set master datasource.");
		System.out.println("set master datasource.");
        DbContextHolder.setDbType(DataSourceLookupKey.MASTER_DATASOURCE);
    }
	
	public void setDwhService(JoinPoint point) {
		logger.info("set dwh datasource.");
		System.out.println("set dwh datasource.");
        DbContextHolder.setDbType(DataSourceLookupKey.DWH_DATASOURCE);
    }

	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		logger.info(beanName);
		if(beanName!=null&&beanName.endsWith("DwhService")){
			DbContextHolder.setDbType(DataSourceLookupKey.DWH_DATASOURCE);
		}
        return bean;
	}

	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}
	
}