package com.arit.adserve.verticle.service;

import com.arit.adserve.comm.Constants;
import com.arit.adserve.entity.Item;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;

public class ItemVtxServiceImpl implements ItemVtxService {
	
	  private Vertx vertx;

	  public ItemVtxServiceImpl(Vertx vertx) {  this.vertx = vertx;  }

	@Override
	public void getItem(String providerItemId, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
		Item item = new Item();
        item.setProviderItemId("id");
        item.setTitle("title");
        item.setProviderName(Constants.EBAY);
        item.setViewItemURL("http://viewItemURL.com"); 
		resultHandler.handle(Future.succeededFuture(OperationResponse.completedWithJson(JsonObject.mapFrom(item))));
		
	}

}
