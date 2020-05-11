package com.arit.adserve.rules;

import java.io.IOException;
import java.util.Collection;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * an service to insert an object to a stateful drools session  
 * 
 * @author Alex Ryjoukhine
 * @since May 11, 2020
 * 
 */
@Slf4j
@Service
public class Evaluate {	
	
	@Autowired
	private DroolsConfig droolsConfig;	
    
    /**
     * @param obj
     * @throws IOException
     */
    public void evaluate(Object obj) throws IOException{    
    	log.info("inserting object into rules: {}", obj);
    	KieSession kSession = droolsConfig.getKieSession();
        kSession.insert(obj); 
        kSession.fireAllRules();      
        kSession.dispose();
    }
    
    /**
     * Fires rules for a certain agenda
     * @param obj 
     * @param agendaName
     * @throws IOException
     */
    public void evaluate(Object obj, String agendaName) throws IOException{    
    	log.info("inserting object into rules: {}", obj);
    	KieSession kSession = droolsConfig.getKieSession();
    	kSession.getAgenda().getAgendaGroup(agendaName).setFocus();
        kSession.insert(obj); 
        kSession.fireAllRules();      
        kSession.dispose();
    }
    
    /**
     * Evaluate collection of objects
     * @param objs
     * @param agendaName
     * @throws IOException
     */
    public void evaluate(Collection<Object> objs) throws IOException{
    	KieSession kSession = droolsConfig.getKieSession();    	
    	for (Object obj : objs) {
    		log.info("inserting object into rules: {}", obj);
    		kSession.insert(obj); 
		}        
        kSession.fireAllRules();      
        kSession.dispose();
    }
    
    /**
     * Evaluate collection of objects for a certain agenda
     * @param objs
     * @param agendaName
     * @throws IOException
     */
    public void evaluate(Collection<Object> objs, String agendaName) throws IOException{
    	KieSession kSession = droolsConfig.getKieSession();
    	kSession.getAgenda().getAgendaGroup(agendaName).setFocus();
    	for (Object obj : objs) {
    		log.info("inserting object into rules: {}", obj);
    		kSession.insert(obj); 
		}        
        kSession.fireAllRules();      
        kSession.dispose();
    }
}
