package com.arit.adserve.comm;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @author Alex Ryjoukhine
 * @since May 30, 2020
 */
@Service
public class SpringUtil implements ApplicationContextAware {
	
	private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;		
	}
	
	public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

}
