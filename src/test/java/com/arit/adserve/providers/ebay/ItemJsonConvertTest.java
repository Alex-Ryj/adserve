package com.arit.adserve.providers.ebay;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

import com.arit.adserve.comm.Constants;
import com.arit.adserve.comm.ItemJsonConvert;
import com.arit.adserve.entity.Item;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ContextConfiguration(classes = {ItemJsonConvert.class})
public class ItemJsonConvertTest {

	@Value("classpath:eBay/eBayFindItemsByKeywordsResponse.json")
	Resource ebayRespFile;

	@Value("classpath:eBay/eBayGetMultipleItemsResponse.json")
	Resource ebayGetMultipleItemsRespFile;
	
	@Autowired
	ItemJsonConvert convert;

	@Test
	public void testItemConvert() throws Exception {
     	Item item = convert.getEbayItem(strJsonItem);
		assertEquals("303220686589", item.getProviderItemId());	
		assertEquals("Drone X Pro Foldable Quadcopter WIFI FPV 720P Wide-Angle HD Camera 3 Batteries", item.getTitle());
		assertEquals("https://thumbs2.ebaystatic.com/m/mV9DBtlnOOw9Eb5u8CNfgyA/140.jpg", item.getGaleryURL());
		assertEquals(5839, item.getPrice());
	}
	
	@Test
	public void testEbayFindResponse() throws Exception {
		String jsonResp = new String(Files.readAllBytes(ebayRespFile.getFile().toPath()));
		JsonNode jsonObj = new ObjectMapper().readTree(jsonResp).get("findItemsByKeywordsResponse").get(0).get("paginationOutput").get(0);		
		assertEquals(Long.valueOf(100), Long.valueOf(jsonObj.get("totalPages").get(0).asText()));
		assertEquals(Long.valueOf(1000), Long.valueOf(jsonObj.get("totalEntries").get(0).asText()));
		assertEquals(Long.valueOf(1), Long.valueOf(jsonObj.get("pageNumber").get(0).asText()));
		assertEquals(Long.valueOf(3), Long.valueOf(jsonObj.get("entriesPerPage").get(0).asText()));		
	}
	
	@Test
	public void testName() throws Exception {
		
	}
	
	@Test
	public void testEbayGetMultipleResponse() throws Exception {
		String jsonStr = new String(Files.readAllBytes(ebayGetMultipleItemsRespFile.getFile().toPath()));
		Item item1 = new Item();
		item1.setProviderItemId("313073812382");
		item1.setProviderName(Constants.EBAY);
		Item item2 = new Item();
		item2.setProviderItemId("1");
		item2.setProviderName(Constants.EBAY);
		List<Item> items = new ArrayList<>();
		items.add(item1);
		items.add(item2);
		var updatedItems = convert.updateEbayItems(jsonStr, items);
		assertEquals(1, ((List) updatedItems).size());
	}
	
	@Test
	public void testFormatPrice() throws Exception {
		String[] prices = {"22.0", "22,12", "11,11,1", "22"};
		for (String price : prices) {
			if(price.matches(".*[\\.,]\\d$")) price +="0";
			System.out.println(price);
		}
	}
	
	String strJsonItem = "{" + 
			"	\"itemId\": [" + 
			"		\"303220686589\"" + 
			"	]," + 
			"	\"title\": [" + 
			"		\"Drone X Pro Foldable Quadcopter WIFI FPV 720P Wide-Angle HD Camera 3 Batteries\"" + 
			"	]," + 
			"	\"globalId\": [" + 
			"		\"EBAY-US\"" + 
			"	]," + 
			"	\"primaryCategory\": [" + 
			"		{" + 
			"			\"categoryId\": [" + 
			"				\"182186\"" + 
			"			]," + 
			"			\"categoryName\": [" + 
			"				\"Other RC Model Vehicles & Kits\"" + 
			"			]" + 
			"		}" + 
			"	]," + 
			"	\"secondaryCategory\": [" + 
			"		{" + 
			"			\"categoryId\": [" + 
			"				\"179697\"" + 
			"			]," + 
			"			\"categoryName\": [" + 
			"				\"Camera Drones\"" + 
			"			]" + 
			"		}" + 
			"	]," + 
			"	\"galleryURL\": [" + 
			"		\"https://thumbs2.ebaystatic.com/m/mV9DBtlnOOw9Eb5u8CNfgyA/140.jpg\"" + 
			"	]," + 
			"	\"viewItemURL\": [" + 
			"		\"https://www.ebay.com/itm/Drone-X-Pro-Foldable-Quadcopter-WIFI-FPV-720P-Wide-Angle-HD-Camera-3-Batteries-/303220686589\"" + 
			"	]," + 
			"	\"paymentMethod\": [" + 
			"		\"PayPal\"" + 
			"	]," + 
			"	\"autoPay\": [" + 
			"		\"true\"" + 
			"	]," + 
			"	\"postalCode\": [" + 
			"		\"088**\"" + 
			"	]," + 
			"	\"location\": [" + 
			"		\"Monroe Township,NJ,USA\"" + 
			"	]," + 
			"	\"country\": [" + 
			"		\"US\"" + 
			"	]," + 
			"	\"shippingInfo\": [" + 
			"		{" + 
			"			\"shippingServiceCost\": [" + 
			"				{" + 
			"					\"@currencyId\": \"USD\"," + 
			"					\"__value__\": \"0.0\"" + 
			"				}" + 
			"			]," + 
			"			\"shippingType\": [" + 
			"				\"Free\"" + 
			"			]," + 
			"			\"shipToLocations\": [" + 
			"				\"Worldwide\"" + 
			"			]," + 
			"			\"expeditedShipping\": [" + 
			"				\"true\"" + 
			"			]," + 
			"			\"oneDayShippingAvailable\": [" + 
			"				\"false\"" + 
			"			]," + 
			"			\"handlingTime\": [" + 
			"				\"1\"" + 
			"			]" + 
			"		}" + 
			"	]," + 
			"	\"sellingStatus\": [" + 
			"		{" + 
			"			\"currentPrice\": [" + 
			"				{" + 
			"					\"@currencyId\": \"USD\"," + 
			"					\"__value__\": \"58.39\"" + 
			"				}" + 
			"			]," + 
			"			\"convertedCurrentPrice\": [" + 
			"				{" + 
			"					\"@currencyId\": \"USD\"," + 
			"					\"__value__\": \"58.39\"" + 
			"				}" + 
			"			]," + 
			"			\"sellingState\": [" + 
			"				\"Active\"" + 
			"			]," + 
			"			\"timeLeft\": [" + 
			"				\"P30DT3H2M42S\"" + 
			"			]" + 
			"		}" + 
			"	]," + 
			"	\"listingInfo\": [" + 
			"		{" + 
			"			\"bestOfferEnabled\": [" + 
			"				\"true\"" + 
			"			]," + 
			"			\"buyItNowAvailable\": [" + 
			"				\"false\"" + 
			"			]," + 
			"			\"startTime\": [" + 
			"				\"2019-07-11T11:31:21.000Z\"" + 
			"			]," + 
			"			\"endTime\": [" + 
			"				\"2020-02-11T11:31:21.000Z\"" + 
			"			]," + 
			"			\"listingType\": [" + 
			"				\"FixedPrice\"" + 
			"			]," + 
			"			\"gift\": [" + 
			"				\"false\"" + 
			"			]," + 
			"			\"watchCount\": [" + 
			"				\"671\"" + 
			"			]" + 
			"		}" + 
			"	]," + 
			"	\"returnsAccepted\": [" + 
			"		\"true\"" + 
			"	]," + 
			"	\"condition\": [" + 
			"		{" + 
			"			\"conditionId\": [" + 
			"				\"1000\"" + 
			"			]," + 
			"			\"conditionDisplayName\": [" + 
			"				\"New\"" + 
			"			]" + 
			"		}" + 
			"	]," + 
			"	\"isMultiVariationListing\": [" + 
			"		\"false\"" + 
			"	]," + 
			"	\"topRatedListing\": [" + 
			"		\"false\"" + 
			"	]" + 
			"}" ;
	
	String strJsonItem1 = "{itemId=[\"202664221684\"], title=[\"Cooligg S169 Drone Selfie WIFI FPV Dual HD Camera Foldable RC Quadcopter Toy\"], globalId=[\"EBAY-US\"], primaryCategory=[{\"categoryId\":[\"179697\"],\"categoryName\":[\"Camera Drones\"]}], galleryURL=[\"https:\\/\\/thumbs1.ebaystatic.com\\/m\\/mBGMY-PrZOZ0TXf9y7RytUQ\\/140.jpg\"], viewItemURL=[\"https:\\/\\/www.ebay.com\\/itm\\/Cooligg-S169-Drone-Selfie-WIFI-FPV-Dual-HD-Camera-Foldable-RC-Quadcopter-Toy-\\/202664221684\"], productId=[{\"@type\":\"ReferenceID\",\"__value__\":\"11030516506\"}], paymentMethod=[\"PayPal\"], autoPay=[\"true\"], postalCode=[\"085**\"], location=[\"Windsor,NJ,USA\"], country=[\"US\"], shippingInfo=[{\"shippingServiceCost\":[{\"@currencyId\":\"USD\",\"__value__\":\"0.0\"}],\"shippingType\":[\"Free\"],\"shipToLocations\":[\"Worldwide\"],\"expeditedShipping\":[\"true\"],\"oneDayShippingAvailable\":[\"false\"],\"handlingTime\":[\"1\"]}], sellingStatus=[{\"currentPrice\":[{\"@currencyId\":\"USD\",\"__value__\":\"58.95\"}],\"convertedCurrentPrice\":[{\"@currencyId\":\"USD\",\"__value__\":\"58.95\"}],\"sellingState\":[\"Active\"],\"timeLeft\":[\"P10DT2H37M37S\"]}], listingInfo=[{\"bestOfferEnabled\":[\"true\"],\"buyItNowAvailable\":[\"false\"],\"startTime\":[\"2019-04-28T07:48:41.000Z\"],\"endTime\":[\"2020-01-28T07:48:41.000Z\"],\"listingType\":[\"FixedPrice\"],\"gift\":[\"false\"],\"watchCount\":[\"1304\"]}], returnsAccepted=[\"true\"], condition=[{\"conditionId\":[\"1000\"],\"conditionDisplayName\":[\"New\"]}], isMultiVariationListing=[\"false\"], discountPriceInfo=[{\"originalRetailPrice\":[{\"@currencyId\":\"USD\",\"__value__\":\"74.99\"}],\"pricingTreatment\":[\"STP\"],\"soldOnEbay\":[\"false\"],\"soldOffEbay\":[\"false\"]}], topRatedListing=[\"true\"]}";

}