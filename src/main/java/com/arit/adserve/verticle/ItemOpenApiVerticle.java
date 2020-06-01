package com.arit.adserve.verticle;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.collections4.FactoryUtils;
import org.springframework.stereotype.Service;

import com.arit.adserve.verticle.service.ItemVtxService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.api.RequestParameters;
import io.vertx.ext.web.api.contract.RouterFactoryOptions;
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.serviceproxy.ServiceBinder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Alex Ryjoukhine
 * @since May 22, 2020
 */
@Slf4j
@Service
public class ItemOpenApiVerticle extends AbstractVerticle {

	 private HttpServer server;	 
	 private ServiceBinder serviceBinder;

	 private MessageConsumer<JsonObject> consumer;
	 
	 private void startItemVtxService() {
		serviceBinder = new ServiceBinder(vertx);			
		ItemVtxService itemVtxService =  ItemVtxService.create(vertx);
		consumer = serviceBinder.setAddress("items_manager.adserve")
				.register(ItemVtxService.class, itemVtxService);		
		 
	 }

	  @Override
	  public void start(Promise<Void> promise) {
		  
		startItemVtxService();
		
	    OpenAPI3RouterFactory.create(this.vertx, "openapi/items_api.yml", ar -> {
	        if (ar.succeeded()) {
	            OpenAPI3RouterFactory routerFactory = ar.result();
	            Set<HttpMethod> allowedMethods = new LinkedHashSet<>(Arrays.asList( HttpMethod.GET, HttpMethod.POST, 
	            		HttpMethod.PUT, HttpMethod.DELETE));
	            Set<String> allowedHeaders = new LinkedHashSet<>(Arrays.asList("Access-Control-Request-Method",
	            		//"Access-Control-Allow-Credentials",
	            		"Access-Control-Allow-Origin",
	            		"Access-Control-Allow-Headers",
	            		"Content-Type"));
	          
	            //local Angular testing
	            routerFactory.addGlobalHandler(CorsHandler.create("http://localhost:\\d+").allowedMethods(allowedMethods).allowedHeaders(allowedHeaders));
	            routerFactory.mountServicesFromExtensions();

	            routerFactory.addSecurityHandler("api_key", routingContext -> {
	              // Handle security here
	              routingContext.next();
	            });	         
	            
	            Router router = routerFactory.getRouter();  
	            router.errorHandler(404, routingContext -> { 
	              JsonObject errorObject = new JsonObject() 
	                .put("code", 404)
	                .put("message",
	                  (routingContext.failure() != null) ?
	                    routingContext.failure().getMessage() :
	                    "Not Found"
	                );
	              routingContext
	                .response()
	                .setStatusCode(404)
	                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
	                .end(errorObject.encode()); 
	            });
	            router.errorHandler(400, routingContext -> {
	              JsonObject errorObject = new JsonObject()
	                .put("code", 400)
	                .put("message",
	                  (routingContext.failure() != null) ?
	                    routingContext.failure().getMessage() :
	                    "Validation Exception"
	                );
	              routingContext
	                .response()
	                .setStatusCode(400)
	                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
	                .end(errorObject.encode());
	            });

	            server = vertx.createHttpServer(new HttpServerOptions().setPort(8080).setHost("localhost")); 
	            server.exceptionHandler(ex -> log.error("Http server exception handler.", ex));
	            server.requestHandler(router).listen(); 
	            
	            promise.complete(); // Complete the verticle start
	          } else {
	            promise.fail(ar.cause()); // Fail the verticle start
	          }
	        });
	      }

	      @Override
	      public void stop(){
	        this.server.close();
	        this.consumer.unregister();
	      }

	      public static void main(String[] args) {
	        Vertx vertx = Vertx.vertx();
	        vertx.deployVerticle(new ItemOpenApiVerticle());
	      }

}


