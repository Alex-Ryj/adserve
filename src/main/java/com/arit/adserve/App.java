package com.arit.adserve;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * Hello api world!
 *
 */
@SpringBootApplication
@ComponentScan
public class App {
	
	@Value("${api.path}")
	String contextPath;
	
	
	public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
	
	@Bean
	ServletRegistrationBean servletCamel() {
	    ServletRegistrationBean servlet = new ServletRegistrationBean
	      (new CamelHttpTransportServlet(), contextPath+"/*");
	    servlet.setName("CamelServlet");
	    return servlet;
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