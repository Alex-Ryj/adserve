package com.arit.adserve;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

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

}