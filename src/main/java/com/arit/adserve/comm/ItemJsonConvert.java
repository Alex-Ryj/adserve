package com.arit.adserve.comm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.arit.adserve.entity.mongo.ItemMongo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Converting {@link com.arit.adserve.entity.Item} Item from json responses of
 * various providers
 * 
 * @author Alex Ryjoukhine
 * @since May 11, 2020
 * 
 */
@Service
@Slf4j
public class ItemJsonConvert {
	
	/**
	 * @param jsonStr (eBay response from find request by item. See <a
	 *                href="https://developer.ebay.com/DevZone/finding/Concepts/FindingAPIGuide.html>Ebay
	 *                finding API</a>)
	 * @return ItemMongo
	 * @throws IOException
	 */
	public ItemMongo getEbayItemMongo(String jsonStr) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonObj = mapper.readTree(jsonStr);
		String itemId = jsonObj.get("itemId").get(0).asText();
		String title = jsonObj.get("title").get(0).asText();
		String galleryURL = jsonObj.get("galleryURL").get(0).asText();
		String viewItemURL = jsonObj.get("viewItemURL").get(0).asText();
		String priceFormatted = jsonObj.get("sellingStatus").get(0).get("currentPrice").get(0).get("__value__")
				.asText();						
		if(priceFormatted.matches(".*[\\.,]\\d$")) priceFormatted +="0";  //make 2 decimals at the end
		int price = Integer.parseInt(priceFormatted.replaceAll("[\\.,]", ""));
		String currency = jsonObj.get("sellingStatus").get(0).get("currentPrice").get(0).get("@currencyId").asText();
		String condition = jsonObj.get("condition").get(0).get("conditionDisplayName").get(0).asText();
		ItemMongo item = new ItemMongo();
		item.setProviderItemId(itemId);
		item.setProviderName(Constants.EBAY);
		item.setTitle(title);
		item.setSource(Constants.EBAY);
		item.setTitle(title);
		item.setPrice(price);
		item.setCurrency(currency);
		item.setPriceFormatted(priceFormatted);
		item.setCondition(condition);
		item.setGalleryURL(galleryURL);
		item.setViewItemURL(viewItemURL);
		return item;
	}
	
	/**
	 * @param jsonStr (eBay response from find request by item. See <a
	 *                href="https://developer.ebay.com/DevZone/shopping/docs/CallRef/GetMultipleItems.html>Ebay
	 *                shopping API GetMultipleItems</a>)
	 * @param items to be updated
	 * @return Iterable<Item> a List of updated items
	 * @throws IOException
	 */
	public Iterable<ItemMongo> updateEbayItemsMongo(String jsonStr, Iterable<ItemMongo> items) throws IOException {
		List<ItemMongo> result = new ArrayList<>();
		log.debug("updated items json: {}", jsonStr);
		JsonNode jsonArrObj = new ObjectMapper().readTree(jsonStr).get("Item");
		for (final JsonNode jsonObj : jsonArrObj) {
			String itemId = jsonObj.get("ItemID").asText();
			String title = jsonObj.get("Title").asText();
			String galleryURL = jsonObj.get("GalleryURL").asText();
			String viewItemURL = jsonObj.get("ViewItemURLForNaturalSearch").asText();
			String priceStr = jsonObj.get("ConvertedCurrentPrice").get("Value").asText();
			int price = Integer.parseInt(priceStr.replaceAll("[,\\.\\s+]", ""));
			String currency = jsonObj.get("ConvertedCurrentPrice").get("CurrencyID").asText();
			for (ItemMongo item : items) {
				if (item.getProviderItemId().equals(itemId) && item.getProviderName().equals(Constants.EBAY)) {
					item.setProviderName(Constants.EBAY);
					item.setTitle(title);
					item.setSource(Constants.EBAY);
					item.setTitle(title);
					item.setPrice(price);
					item.setCurrency(currency);
					item.setPriceFormatted(priceStr);
					item.setGalleryURL(galleryURL);
					item.setViewItemURL(viewItemURL);
					result.add(item);
				}
			}
		}
		return result;
	}

}
