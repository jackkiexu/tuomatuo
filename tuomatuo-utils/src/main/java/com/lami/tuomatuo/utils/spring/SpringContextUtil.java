package com.lami.tuomatuo.utils.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * springcontext utils
 * @author xjk
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

	@Autowired
    private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;

	}

	public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
	
	/**
	 * get Bean by beanName
	 * @param beanName
	 * @return bean
	 * @throws BeansException
	 */
	public Object getBean(String beanName) throws BeansException {
        return applicationContext.getBean(beanName);
    }
	
	/**
	 * get Bean by Class
	 * @param requiredType 如： aaa.class
	 * @return bean
	 * @throws BeansException
	 */
	public  <T> Object getBean(Class<T> requiredType) throws BeansException {
        return applicationContext.getBean(requiredType);
    }
	
	/**
	 * get Bean by type
	 * @param type
	 * @return
	 */
	public  Map<String, ? extends Object> getBeanOfType(Class<? extends Object> type){
		return applicationContext.getBeansOfType(type);
	}
}
