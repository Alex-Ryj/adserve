package com.arit.adserve.image;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UtilsTest {

	@Test
	public void testCalculateNiewImagehight() {
		assertEquals(50, Utils.calulateImageSizeFromNewWidth(100, 100, 50).getSecond().intValue());
		assertEquals(25, Utils.calulateImageSizeFromNewWidth(100, 50, 50).getSecond().intValue());
		assertEquals(18, Utils.calulateImageSizeFromNewWidth(100, 50, 37).getSecond().intValue());
	}

}
