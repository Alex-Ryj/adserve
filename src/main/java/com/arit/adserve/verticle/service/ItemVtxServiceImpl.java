package com.arit.adserve.verticle.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.arit.adserve.comm.Constants;
import com.arit.adserve.entity.Item;
import com.arit.adserve.entity.ItemId;
import com.arit.adserve.entity.service.ItemServiceImpl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemVtxServiceImpl implements ItemVtxService, ApplicationContextAware {
	
	  private Vertx vertx;
	  private ApplicationContext ctx;
	  private ItemServiceImpl itemService;

	  public ItemVtxServiceImpl(Vertx vertx) {  this.vertx = vertx;  }

	@Override
	public void getItem(String providerName, String providerItemId, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
		vertx.executeBlocking(future -> {
		log.info("recieved {} - {}", providerName, providerItemId);
		ItemId itemId = new ItemId(providerItemId, providerName);
		Item item = itemService.findById(itemId);
        item.setProviderItemId("id");
        item.setTitle("title");
        item.setProviderName(Constants.EBAY);
        item.setViewItemURL("http://viewItemURL.com"); 
		resultHandler.handle(Future.succeededFuture(OperationResponse.completedWithJson(JsonObject.mapFrom(item))));
		future.complete();
		},  ar -> log.info("result handler"));
		
	}

	@Override
	public void setApplicationContext(ApplicationContext context){
		this.ctx=context;	
		itemService = this.ctx.getBean(ItemServiceImpl.class);
	}
}
