package com.arit.adserve.image;

import org.springframework.data.util.Pair;

public class Utils {

	/**
	 * @param width
	 * @param height
	 * @param requiredWith
	 * @return Pair.of(requiredWidth, requiredHeight) 
	 */
	public static Pair<Integer, Integer> calulateImageSizeFromNewWidth(float width, float height, int requiredWidth) {
		int requiredHeight = (int) (height / (width / requiredWidth));
		return Pair.of(requiredWidth, requiredHeight);
	}

}
