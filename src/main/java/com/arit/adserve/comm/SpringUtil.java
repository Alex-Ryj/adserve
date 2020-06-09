package com.arit.adserve.comm;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

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
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.context = applicationContext;		
	}
	
	/**
	 * to get a spring bean outside of spring context 
	 * @param <T> 
	 * @param beanClass
	 * @return
	 */
	public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }
	
	 /**
	 * java bean validation service 
	 * @param <T> java bean type to validate
	 * @param input
	 */
	public static <T> void validateInput (T input) {
		    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		    Validator validator = factory.getValidator();
		    Set<ConstraintViolation<T>> violations = validator.validate(input);
		    if (!violations.isEmpty()) {
		      throw new ConstraintViolationException(violations);
		    }
		  }

}
