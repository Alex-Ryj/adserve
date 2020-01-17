package com.arit.adserve.ebay;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class EbayJsonConvertTest {
	
	EbayJsonConvert convert = new EbayJsonConvert();

	@Test
	public void test() throws Exception {
		String strJson = FileUtils.readFileToString(new File(EbayJsonConvertTest.class.getClassLoader().getResource("ebayItem.json").getFile()));
		System.out.println(strJson);
		EbayItem item = convert.getItem(strJsonItem);
		assertEquals("303220686589", item.getItemId());	
		assertEquals("Drone X Pro Foldable Quadcopter WIFI FPV 720P Wide-Angle HD Camera 3 Batteries", item.getTitle());
		assertEquals("https://thumbs2.ebaystatic.com/m/mV9DBtlnOOw9Eb5u8CNfgyA/140.jpg", item.getGaleryURL());
		assertEquals(5839, item.getPrice());
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

}