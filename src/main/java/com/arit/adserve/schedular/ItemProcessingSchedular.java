package com.arit.adserve.schedular;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Scheduling:
 * - ads image update e.g. to reflect the price change
 * - contextual updates of the serving ads depending on the web page context and ads availability
 * - delete expired ads
 * @author Alex Ryjoukhine
 * @since Aug 4, 2020 
 */

@Service
public class ItemProcessingSchedular {	
	
	@Scheduled(fixedDelayString = "${fixedDelay.in.mills}")
	public void updateAdImage() {
		
	}

	/**
	 * periodically checks the pages where the ads are published, analyzes them 
	 * and select the best possible match between the existing ads and the page context  
	 */
	public void siteAdContextualUpdate() {
		
	}
	
	public void deleteExpiredAds() {
		
	}
}
