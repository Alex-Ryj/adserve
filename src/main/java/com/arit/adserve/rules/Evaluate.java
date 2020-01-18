package com.arit.adserve.rules;

import java.io.IOException;

import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class Evaluate {
	
	private static Logger logger = LoggerFactory.getLogger(Evaluate.class);
	
	private static KieSession kSession;
	
	static {
		try {
			kSession = new DroolsConfig().getKieSession();
		} catch (IOException e) {
			logger.error("no kie session", e);
		}
	}
    
    public void evaluate(Object obj){    
    	logger.info("inserting object into rules: {}", obj);
        kSession.insert(obj); 
        kSession.fireAllRules();       
    }

}
