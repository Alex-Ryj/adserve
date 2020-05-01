package com.arit.adserve.rules;

import java.io.IOException;

import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class Evaluate {	
	
	@Autowired
	private DroolsConfig droolsConfig;	
    
    public void evaluate(Object obj) throws IOException{    
    	log.info("inserting object into rules: {}", obj);
    	KieSession kSession = droolsConfig.getKieSession();
        kSession.insert(obj); 
        kSession.fireAllRules();      
        kSession.dispose();
    }
}
