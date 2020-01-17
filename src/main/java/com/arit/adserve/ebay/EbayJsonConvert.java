package com.arit.adserve.ebay;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EbayJsonConvert {

	public EbayItem getItem(String strJson) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonObj = mapper.readTree(strJson);
		String itemId = jsonObj.get("itemId").get(0).asText();
		String title = jsonObj.get("title").get(0).asText();
		String name = title;
		String galleryURL = jsonObj.get("galleryURL").get(0).asText();
		String viewItemURL = jsonObj.get("viewItemURL").get(0).asText();
		String priceFormatted = jsonObj.get("sellingStatus").get(0).get("currentPrice").get(0).get("__value__")
				.asText();
		int price = Integer.parseInt(jsonObj.get("sellingStatus").get(0).get("currentPrice").get(0).get("__value__")
				.asText().replaceAll("[,\\.\\s+]", ""));
		String currency = jsonObj.get("sellingStatus").get(0).get("currentPrice").get(0).get("@currencyId")
				.asText();
		String condition = jsonObj.get("condition").get(0).get("conditionDisplayName").get(0).asText();
		EbayItem item = new EbayItem(itemId, name, title, price, currency);	
		item.setPriceForamtted(priceFormatted);
		item.setCondition(condition);
		item.setGaleryURL(galleryURL);
		item.setViewItemURL(viewItemURL);
		return item;
	}
}
