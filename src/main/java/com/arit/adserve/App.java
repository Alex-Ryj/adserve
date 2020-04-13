package com.arit.adserve;

import com.arit.adserve.controller.ServerVerticle;
import com.arit.adserve.ebay.EbayApi;
import io.vertx.core.Vertx;
import org.apache.camel.CamelContext;
import org.apache.camel.component.vertx.VertxComponent;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;

/**
 * Hello api world!
 *
 */
@SpringBootApplication
@ComponentScan
public class App {

    @Autowired
    private ApplicationContext applicationContext;

    private Vertx vertx = Vertx.vertx();
	
	public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    public void deployVerticle() {
        vertx.deployVerticle(applicationContext.getBean(ServerVerticle.class));
        vertx.deployVerticle(applicationContext.getBean(EbayApi.class));
    }

    @Bean
    public CamelContextConfiguration contextConfiguration() {
        return new CamelContextConfiguration() {
            @Override
            public void beforeApplicationStart(CamelContext context) {
                System.out.println("camel context before start: " + context);
                VertxComponent vertxComponent = new VertxComponent();
                vertxComponent.setVertx(vertx);
                context.addComponent("vertx", vertxComponent);
            }

            @Override
            public void afterApplicationStart(CamelContext camelContext) {
                System.out.println("camel context after start: " + camelContext);
                deployVerticle();
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

}