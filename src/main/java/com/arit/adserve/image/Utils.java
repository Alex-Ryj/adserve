package com.arit.adserve.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.data.util.Pair;

public class Utils {
	
	private Utils() {}

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

	
	/**
	 * @param img1
	 * @param img2
	 * @return image of vertically attached images, img1 on top
	 * @throws IOException 
	 */
	public static BufferedImage joinBufferedImageTopDown(BufferedImage img1, BufferedImage img2) throws IOException {
		// do some calculate first
		int offset = 0;
		int wid = img1.getWidth();
		if(wid != img2.getWidth()) throw new IllegalArgumentException ("widths not equal");
		int height = img1.getHeight() + img2.getHeight() + offset;
		// create a new buffer and draw two image into the new image
		BufferedImage newImage = new BufferedImage(wid, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr1 = newImage.createGraphics();
		boolean image1Drawn = gr1.drawImage(img1, 0, 0, null);
		if(!image1Drawn) System.out.println("Problems drawing first image"); //where we are placing image1 in final image
	
		boolean image2Drawn = 	gr1.drawImage(img2, 0, img1.getHeight() + offset, null);
		if(!image1Drawn) System.out.println("Problems drawing first image");
		gr1.dispose();
		return newImage;
	}
	
	/**
	 * @param img1
	 * @param img2
	 * @return image of side by side attached images, img1 on the left side
	 */
	public static BufferedImage joinBufferedImageSideBySide(BufferedImage img1, BufferedImage img2) {
		// do some calculate first
		int offset = 0;
		int wid = img1.getWidth() + img2.getWidth() + offset;
		int height = Math.max(img1.getHeight(), img2.getHeight()) + offset;
		if(height != img2.getHeight()) throw new IllegalArgumentException ("heights not equal");
		// create a new buffer and draw two image into the new image
		BufferedImage newImage = new BufferedImage(wid, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr1 = newImage.createGraphics();
		gr1.drawImage(img1, null, 0, 0);
		gr1.dispose();
		Graphics2D gr2 = newImage.createGraphics();
		gr2.drawImage(img2, null, img1.getWidth() + offset, 0);
		gr2.dispose();
		return newImage;
	}

}
