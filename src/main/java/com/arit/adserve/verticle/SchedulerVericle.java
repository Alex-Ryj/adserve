package com.arit.adserve.verticle;

import java.util.concurrent.TimeUnit;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.vertx.core.AbstractVerticle;
import lombok.extern.slf4j.Slf4j;

/**
 * scheduling all tasks
 * @author Alex Ryjoukhine
 * @since Jun 8, 2020
 */
@Slf4j
@Service
public class SchedulerVericle extends AbstractVerticle {
	
	@Value("${ebay.api.request.period.seconds}")
	private int ebayRequestPeriod;
	
	@Autowired
	private ProducerTemplate template;
	
	@Override
	public void start() throws Exception {
		// TODO Auto-generated method stub
		super.start();
//		vertx.setPeriodic(TimeUnit.SECONDS.toMillis(ebayRequestPeriod), id -> {
//			log.info("calling ebay api");
//			template.sendBody("direct:processItems", "init");
//		});
		
	}

}
