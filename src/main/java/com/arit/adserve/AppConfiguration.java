package com.arit.adserve;

import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

	  @Autowired
	  CamelContext camelContext;
	  
}
