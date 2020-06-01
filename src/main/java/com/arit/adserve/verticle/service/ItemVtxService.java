package com.arit.adserve.verticle.service;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;
import io.vertx.ext.web.api.generator.WebApiServiceGen;

@WebApiServiceGen
public interface ItemVtxService {
	
	void getItem(String providerName, String providerItemId,  OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler);

	void getItems(String providerName, List<String> providerItemIds,  OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler);

	void getItemsByPage(String providerName, String sortedField, boolean sortedDesc, int pageNum, int itemsPerPage, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler);

	/**
	 * Factory method to instantiate the implementation
	 * @param vertx
	 * @return
	 */
	static ItemVtxService create(Vertx vertx) {
	    return new ItemVtxServiceImpl(vertx);
	  }

}
