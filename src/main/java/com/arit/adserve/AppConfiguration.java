package com.arit.adserve;

import org.apache.camel.CamelContext;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.vertx.VertxComponent;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import com.arit.adserve.comm.SpringUtil;
import com.arit.adserve.providers.ebay.EbayCamelService;
import com.arit.adserve.verticle.ItemOpenApiVerticle;
import com.arit.adserve.verticle.ItemVerticle;

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
@EnableTransactionManagement
public class AppConfiguration {

	private Vertx vertx = Vertx.vertx();

	@Autowired
	private SpringUtil springUtil;
	
	@Autowired
	private EbayCamelService ebayCamelService;

	public void deployVerticles() {
		DeploymentOptions optionsWorker = new DeploymentOptions().setWorker(true);		
		vertx.deployVerticle(SpringUtil.getBean(ItemOpenApiVerticle.class));
		vertx.deployVerticle(SpringUtil.getBean(ItemVerticle.class), optionsWorker);
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
					camelContext.addRoutes(configureGeneralRoutes());
					camelContext.addRoutes(ebayCamelService.configureRoutes());
				} catch (Exception e) {
					log.error("camel failed to start", e);
					System.exit(1);
				}
				deployVerticles();
			}
		};
	}	

	@Bean(name="transactionReadUncommitted")	
	public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
		transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
		return transactionTemplate;
	}
	
	
	private RouteBuilder configureGeneralRoutes() {
		return new RouteBuilder() {
			@SuppressWarnings("unchecked")
			public void configure() throws Exception {
				// camel general exception handling
				onException(RuntimeCamelException.class).log("${body}");
			}
		};
	}

}
