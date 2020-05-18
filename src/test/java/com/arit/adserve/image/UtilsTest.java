package com.arit.adserve.image;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
public class UtilsTest {

	@Test
	public void testCalculateNiewImagehight() {
		assertEquals(50, Utils.calulateImageSizeFromNewWidth(100, 100, 50).getSecond().intValue());
		assertEquals(25, Utils.calulateImageSizeFromNewWidth(100, 50, 50).getSecond().intValue());
		assertEquals(18, Utils.calulateImageSizeFromNewWidth(100, 50, 37).getSecond().intValue());
	}
	
	@Test
	public void testVertivalImageMerge() throws Exception {
		String textOntop = text; //"text1";
		String textBelow = text; //"text2";
		MultiLineText mlt = new MultiLineText.Builder().text(textOntop).fontColor(Color.GREEN).fontBackround(Color.WHITE).width(580).breakWidth(560f).fontSize(50f)
				.build();
		MultiLineText mlt1 = new MultiLineText.Builder().text(textBelow).fontColor(Color.BLACK).fontBackround(Color.WHITE).width(580).breakWidth(560f).fontSize(50f)
				.build();
		File file1 = new File("image1.jpg");
		File file2 = new File("image2.jpg");
		ImageIO.write(mlt.getTextImage(), "jpg", file1);
		ImageIO.write(mlt1.getTextImage(), "jpg", file2);
		BufferedImage result = Utils.joinBufferedImageTopDown(mlt.getTextImage(), mlt1.getTextImage());
		File file = new File("testmergedimage.jpg");
		ImageIO.write(result, "jpg", file);
		assertTrue(file.length()>1);
	}	

	
	
	
	static String text = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit."
			+ " Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus "
			+ "et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, "
			+ "pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, "
			+ "fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, "
			+ "venenatis vitae, justo. "
			+ "Lorem ipsum dolor sit amet, consectetuer adipiscing elit ";

}
