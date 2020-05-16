package com.arit.adserve.verticle;

import com.arit.adserve.comm.ErrorCodes;
import com.arit.adserve.entity.Item;
import com.arit.adserve.entity.ItemId;
import com.arit.adserve.entity.service.ItemService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author Alex Ryjoukhine
 * @since May 15, 2020
 * 
 */
@Slf4j
@Service
public class ItemVerticle extends AbstractVerticle {

	public static final String GET_ITEM_VTX = "get_item";
	public static final String GET_ITEMS_VTX = "get_items";
	public static final String GET_ITEMS_PAGES_VTX = "get_items_pages";
	public static final String GET_IMAGE_VTX = "get_image";

	@Autowired
	private ItemService itemService;

	@Override
	public void start() throws Exception {
		super.start();
		vertx.eventBus().consumer(GET_ITEM_VTX, message -> {
			log.debug("itemId and provider = " + message.body());
			ItemId itemId = getItemId(message);
			Item item = itemService.findById(itemId);
			JsonObject jsonObject = new JsonObject(Json.encode(item));
			if (item != null) {
				message.reply(jsonObject);
			} else {
				message.fail(ErrorCodes.NOT_FOUND.getCode(), ErrorCodes.NOT_FOUND.getMessage());
			}
		});

		vertx.eventBus().consumer(GET_ITEMS_VTX, message -> {
			log.debug("itemIds = " + message.body());
			JsonArray jsonArray = (JsonArray) message.body();
			List<ItemId> ids = jsonArray.getList();
			Iterable<Item> items = itemService.findAllById(ids);
			JsonArray jsonArrayResult = new JsonArray();
			for (Item item : items) {
				jsonArrayResult.add(new JsonObject(Json.encode(item)));
			}
			if (items != null) {
				message.reply(jsonArrayResult);
			} else {
				message.fail(ErrorCodes.NOT_FOUND.getCode(), ErrorCodes.NOT_FOUND.getMessage());
			}
		});

		vertx.eventBus().consumer(GET_ITEMS_PAGES_VTX, message -> {
			log.debug("itemIds = " + message.body());
			Iterable<Item> items = itemService.findAll(((JsonObject) message.body()).getInteger("pageNumber"),
					((JsonObject) message.body()).getInteger("rowPerPage"));
			JsonObject jsonObject = new JsonObject(Json.encode(items));
			if (items != null) {
				Long count = itemService.count();
				message.reply(jsonObject.put("count", count));
			} else {
				message.fail(ErrorCodes.NOT_FOUND.getCode(), ErrorCodes.NOT_FOUND.getMessage());
			}
		});

		vertx.eventBus().consumer(GET_IMAGE_VTX, message -> {
			log.debug("itemId = " + message.body());
			Item item = itemService.findById(getItemId(message));
			if (item != null) {
				JsonObject jsonObject = new JsonObject().put("img", item.getModifiedImage64BaseStr());
				message.reply(jsonObject);
			} else {
				message.fail(ErrorCodes.NOT_FOUND.getCode(), ErrorCodes.NOT_FOUND.getMessage());
			}
		});
	}

	@Override
	public void stop() throws Exception {
		super.stop();
	}

	private ItemId getItemId(Message<Object> message) {
		JsonObject jsonItemId = (JsonObject) message.body();
		return new ItemId(jsonItemId.getString("providerItemId"), jsonItemId.getString("providerName"));
	}

}
