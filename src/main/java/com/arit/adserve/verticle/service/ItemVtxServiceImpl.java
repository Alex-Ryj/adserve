package com.arit.adserve.verticle.service;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.arit.adserve.comm.Constants;
import com.arit.adserve.comm.SpringUtil;
import com.arit.adserve.entity.Item;
import com.arit.adserve.entity.ItemId;
import com.arit.adserve.entity.mongo.ItemMongo;
import com.arit.adserve.entity.mongo.service.ItemMongoService;
import com.arit.adserve.entity.service.ItemServiceImpl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Alex Ryjoukhine
 * @since May 29, 2020
 */
@Slf4j
public class ItemVtxServiceImpl implements ItemVtxService {

	private Vertx vertx;
	private ApplicationContext ctx;
	private ItemMongoService itemService;

	public ItemVtxServiceImpl(Vertx vertx) {
		this.vertx = vertx;
		this.itemService = SpringUtil.getBean(ItemMongoService.class);
	}

	@Override
	public void getItem(String providerName, String providerItemId, OperationRequest context,
			Handler<AsyncResult<OperationResponse>> resultHandler) {
		vertx.executeBlocking(future -> {
			log.info("recieved {} - {}", providerName, providerItemId);
			ItemMongo item = itemService.findByProviderId(providerItemId, providerName);
			resultHandler.handle(Future.succeededFuture(OperationResponse.completedWithJson(JsonObject.mapFrom(item))));
			future.complete();
		}, ar -> log.info("result handler"));
	}

	@Override
	public void getItems(String providerName, List<String> providerItemIds, OperationRequest context,
			Handler<AsyncResult<OperationResponse>> resultHandler) {
		vertx.executeBlocking(future -> {
			log.info("recieved {} - {}", providerName, providerItemIds);
			Iterable<ItemMongo> items = itemService.findAllByProviderIds(providerItemIds, providerName);
			JsonArray array = new JsonArray();
			for (ItemMongo item : items) {
				array.add(JsonObject.mapFrom(item));
			}
			resultHandler.handle(Future.succeededFuture(OperationResponse.completedWithJson(array)));
			future.complete();
		}, ar -> log.info("result handler"));

	}

	@Override
	public void getItemsByPage(String providerName, int pageNum, int itemsPerPage, String sortedField, OperationRequest context,
			Handler<AsyncResult<OperationResponse>> resultHandler) {
		vertx.executeBlocking(future -> {
			log.info("recieved {} - page: {}, items per page {}", providerName, pageNum, itemsPerPage);
			PageRequest reqSorted = PageRequest.of(pageNum, itemsPerPage,Sort.by(sortedField).descending());
			Iterable<ItemMongo> items = itemService.findAllByProvider(providerName, reqSorted);
			resultHandler.handle(Future.succeededFuture(OperationResponse.completedWithJson(JsonObject.mapFrom(items))));
			future.complete();
		}, ar -> log.info("result handler"));
		
	}
}
