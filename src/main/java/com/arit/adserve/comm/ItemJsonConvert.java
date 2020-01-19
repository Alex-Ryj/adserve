package com.arit.adserve.comm;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.arit.adserve.entity.Item;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ItemJsonConvert {

	public Item getEbayItem(String strJson) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonObj = mapper.readTree(strJson);
		String itemId = jsonObj.get("itemId").get(0).asText();
		String title = jsonObj.get("title").get(0).asText();
		String galleryURL = jsonObj.get("galleryURL").get(0).asText();
		String viewItemURL = jsonObj.get("viewItemURL").get(0).asText();
		String priceFormatted = jsonObj.get("sellingStatus").get(0).get("currentPrice").get(0).get("__value__")
				.asText();
		int price = Integer.parseInt(jsonObj.get("sellingStatus").get(0).get("currentPrice").get(0).get("__value__")
				.asText().replaceAll("[,\\.\\s+]", ""));
		String currency = jsonObj.get("sellingStatus").get(0).get("currentPrice").get(0).get("@currencyId")
				.asText();
		String condition = jsonObj.get("condition").get(0).get("conditionDisplayName").get(0).asText();
		Item item = new Item();
		item.setItemId(itemId);
		item.setTitle(title);
		item.setSource("ebay");
		item.setTitle(title);
		item.setPrice(price);
		item.setCurrency(currency);
		item.setPriceForamtted(priceFormatted);
		item.setCondition(condition);
		item.setGaleryURL(galleryURL);
		item.setViewItemURL(viewItemURL);
		return item;
	}
}
