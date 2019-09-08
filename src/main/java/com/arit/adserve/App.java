package com.arit.adserve;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class App 
{
	public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

 
    
    @Bean
    public static PropertyPlaceholderConfigurer properties() {
        PropertyPlaceholderConfigurer ppc
          = new PropertyPlaceholderConfigurer();
        Resource[] resources = new FileSystemResource[]
          { new FileSystemResource( "app.properties" ) };
        ppc.setLocations( resources );
        ppc.setIgnoreUnresolvablePlaceholders( true );
        return ppc;
    }

}