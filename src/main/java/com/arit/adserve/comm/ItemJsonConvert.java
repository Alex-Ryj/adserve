package com.arit.adserve.comm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.arit.adserve.entity.Item;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Converting {@link com.arit.adserve.entity.Item} Item from json responses of
 * various providers
 * 
 * @author Alex Ryjoukhine
 * @since May 11, 2020
 * 
 */
@Service
public class ItemJsonConvert {

	/**
	 * @param jsonStr (eBay response from find request by item. See <a
	 *                href="https://developer.ebay.com/DevZone/finding/Concepts/FindingAPIGuide.html>Ebay
	 *                finding API</a>)
	 * @return Item
	 * @throws IOException
	 */
	public Item getEbayItem(String jsonStr) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonObj = mapper.readTree(jsonStr);
		String itemId = jsonObj.get("itemId").get(0).asText();
		String title = jsonObj.get("title").get(0).asText();
		String galleryURL = jsonObj.get("galleryURL").get(0).asText();
		String viewItemURL = jsonObj.get("viewItemURL").get(0).asText();
		String priceFormatted = jsonObj.get("sellingStatus").get(0).get("currentPrice").get(0).get("__value__")
				.asText();
		int price = Integer.parseInt(jsonObj.get("sellingStatus").get(0).get("currentPrice").get(0).get("__value__")
				.asText().replaceAll("[,\\.\\s+]", ""));
		String currency = jsonObj.get("sellingStatus").get(0).get("currentPrice").get(0).get("@currencyId").asText();
		String condition = jsonObj.get("condition").get(0).get("conditionDisplayName").get(0).asText();
		Item item = new Item();
		item.setProviderItemId(itemId);
		item.setProviderName(Constants.EBAY);
		item.setTitle(title);
		item.setSource(Constants.EBAY);
		item.setTitle(title);
		item.setPrice(price);
		item.setCurrency(currency);
		item.setPriceForamtted(priceFormatted);
		item.setCondition(condition);
		item.setGaleryURL(galleryURL);
		item.setViewItemURL(viewItemURL);
		return item;
	}

	/**
	 * @param jsonStr (eBay response from find request by item. See <a
	 *                href="https://developer.ebay.com/DevZone/shopping/docs/CallRef/GetMultipleItems.html>Ebay
	 *                shopping API GetMultipleItems</a>)
	 * @param items to be updated
	 * @return a list of updated items
	 * @throws IOException
	 */
	public List<Item> updateEbayItems(String jsonStr, List<Item> items) throws IOException {
		List<Item> result = new ArrayList<>();
		JsonNode jsonArrObj = new ObjectMapper().readTree(jsonStr).get("Item");
		for (final JsonNode jsonObj : jsonArrObj) {
			String itemId = jsonObj.get("ItemID").asText();
			String title = jsonObj.get("Title").asText();
			String galleryURL = jsonObj.get("GalleryURL").asText();
			String viewItemURL = jsonObj.get("ViewItemURLForNaturalSearch").asText();
			String priceStr = jsonObj.get("ConvertedCurrentPrice").get("Value").asText();
			int price = Integer.parseInt(priceStr.replaceAll("[,\\.\\s+]", ""));
			String currency = jsonObj.get("ConvertedCurrentPrice").get("CurrencyID").asText();
			for (Item item : items) {
				if (item.getProviderItemId().equals(itemId) && item.getProviderName().equals(Constants.EBAY)) {
					item.setProviderName(Constants.EBAY);
					item.setTitle(title);
					item.setSource(Constants.EBAY);
					item.setTitle(title);
					item.setPrice(price);
					item.setCurrency(currency);
					item.setPriceForamtted(priceStr);
					item.setGaleryURL(galleryURL);
					item.setViewItemURL(viewItemURL);
					result.add(item);
				}
			}
		}
		return result;
	}
}
