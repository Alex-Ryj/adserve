package com.arit.adserve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;


/**
 * @author Alex Ryjoukhine
 * @since May 12, 2020
 * 
 */
@Slf4j
@SpringBootApplication
@ComponentScan  
public class App { 
	
	public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    } 
	

    @Bean public ConversionService conversionService() {
        return new DefaultConversionService();
    }

}