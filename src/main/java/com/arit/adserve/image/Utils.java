package com.arit.adserve.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

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

	
	public static BufferedImage joinBufferedImageTopDown(BufferedImage img1, BufferedImage img2) {
		// do some calculate first
		int offset = 5;
		int wid = img1.getWidth() + img2.getWidth() + offset;
		int height = Math.max(img1.getHeight(), img2.getHeight()) + offset;
		// create a new buffer and draw two image into the new image
		BufferedImage newImage = new BufferedImage(wid, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = newImage.createGraphics();
		Color oldColor = g2.getColor();
		// fill background
		g2.setPaint(Color.WHITE);
		g2.fillRect(0, 0, wid, height);
		// draw image
		g2.setColor(oldColor);
		g2.drawImage(img1, null, 0, 0);
		g2.drawImage(img2, null, img1.getWidth() + offset, 0);
		g2.dispose();
		return newImage;
	}

}
