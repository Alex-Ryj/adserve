package com.arit.adserve.rules;

import java.io.IOException;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import lombok.extern.slf4j.Slf4j;


/**
 * @author Alex Ryjoukhine
 * @since May 15, 2020
 * 
 */
@Slf4j
@Configuration
public class DroolsConfig {

	private static final String RULES_PATH = "rules/";
	private static KieServices kieServices = KieServices.Factory.get();
	private static KieContainer kContainer;
	private static Logger rulesLogger = LoggerFactory.getLogger("RulesLogger");
	
	static {
		try {
			kContainer = getKContiner();
		} catch (IOException e) {
			log.error("kie inizialization", e);
		}
	}

	private static KieFileSystem getKieFileSystem() throws IOException {
		KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
		for (Resource file : getRuleFiles()) {
			kieFileSystem.write(ResourceFactory.newClassPathResource(RULES_PATH + file.getFilename(), "UTF-8"));
		}
		return kieFileSystem;

	}

	private static Resource[] getRuleFiles() throws IOException {
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		return resourcePatternResolver.getResources("classpath*:" + RULES_PATH + "**/*.*");
	}

	public static KieContainer getKieContainer() throws IOException {
		getKieRepository();
		KieBuilder kb = kieServices.newKieBuilder(getKieFileSystem());
		kb.buildAll();
		KieModule kieModule = kb.getKieModule();
		return kieServices.newKieContainer(kieModule.getReleaseId());
	}

	private static void getKieRepository() {
		final KieRepository kieRepository = kieServices.getRepository();
		kieRepository.addKieModule(kieRepository::getDefaultReleaseId);
	}
	
	private static KieContainer getKContiner() throws IOException {
		getKieRepository();
		KieBuilder kb = kieServices.newKieBuilder(getKieFileSystem());
		kb.buildAll();
		KieModule kieModule = kb.getKieModule();
		return kieServices.newKieContainer(kieModule.getReleaseId());
	}

	public KieSession getKieSession() throws IOException {				
		KieSession kieSession = kContainer.newKieSession();
		kieSession.setGlobal("logger", rulesLogger);
		return kieSession;
	}
	
	public StatelessKieSession getKieStatlessSession() throws IOException {			
		StatelessKieSession kieSession = kContainer.newStatelessKieSession();
		kieSession.setGlobal("logger", rulesLogger);
		return kieSession;
	}

}
