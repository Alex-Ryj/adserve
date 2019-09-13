package com.arit.adserve.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemController {
	
	Logger logger = LoggerFactory.getLogger(ItemController.class);
	
	 @RequestMapping("/")
	    public String index() {
		 	logger.info("rest call!");
	        return "Greetings from Spring Boot!";
	    }
}
