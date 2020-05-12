package com.arit.adserve;

import org.apache.camel.CamelContext;
import org.apache.camel.component.vertx.VertxComponent;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.arit.adserve.verticle.EbayApiVerticle;
import com.arit.adserve.verticle.ItemVerticle;
import com.arit.adserve.verticle.WebServerVerticle;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Alex Ryjoukhine
 * @since May 12, 2020
 * 
 */
@Slf4j
@Configuration
public class AppConfiguration {
	
    private Vertx vertx = Vertx.vertx();
	
	   @Autowired
	    private ApplicationContext applicationContext;	

	  @Autowired
	  CamelContext camelContext;
	  
	   public void deployVerticles() {
	        DeploymentOptions optionsWorker = new DeploymentOptions().setWorker(true);
	        vertx.deployVerticle(applicationContext.getBean(WebServerVerticle.class));
	        vertx.deployVerticle(applicationContext.getBean(EbayApiVerticle.class), optionsWorker);
	        vertx.deployVerticle(applicationContext.getBean(ItemVerticle.class), optionsWorker);
	    }

	    @Bean
	    public CamelContextConfiguration contextConfiguration() {
	        return new CamelContextConfiguration() {
	            @Override
	            public void beforeApplicationStart(CamelContext context) {
	                log.info("camel context before start: " + context);
	                VertxComponent vertxComponent = new VertxComponent();
	                vertxComponent.setVertx(vertx);
	                context.addComponent("vertx", vertxComponent);
	            }

	            @Override
	            public void afterApplicationStart(CamelContext camelContext) {
	                log.info("camel context after start: " + camelContext);
	                try {
						camelContext.start();
					} catch (Exception e) {
						log.error("camel failed to start", e);
						System.exit(1);					
					}
	                deployVerticles();
	            }
	        };
	    }
		

	    @Bean
	    public static PropertyPlaceholderConfigurer propertiesExtenal() {
	        PropertyPlaceholderConfigurer ppc
	          = new PropertyPlaceholderConfigurer();
	        Resource[] resources = new FileSystemResource[]
	          { new FileSystemResource( "app.properties" ), new FileSystemResource( "app.yml" ) };
	        ppc.setLocations( resources );
	        ppc.setIgnoreUnresolvablePlaceholders( true );
	        return ppc;
	    }

	    @Bean public ConversionService conversionService() {
	        return new DefaultConversionService();
	    }
	  
}
