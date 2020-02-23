package com.arit.adserve.image;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

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
	
	public byte[] pngWriteCustomData(BufferedImage buffImg, String key, String value) throws IOException {
	    ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();

	    ImageWriteParam writeParam = writer.getDefaultWriteParam();
	    ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);

	    //adding metadata
	    IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);

	    IIOMetadataNode textEntry = new IIOMetadataNode("tEXtEntry");
	    textEntry.setAttribute("keyword", key);
	    textEntry.setAttribute("value", value);

	    IIOMetadataNode text = new IIOMetadataNode("tEXt");
	    text.appendChild(textEntry);

	    IIOMetadataNode root = new IIOMetadataNode("javax_imageio_png_1.0");
	    root.appendChild(text);

	    metadata.mergeTree("javax_imageio_png_1.0", root);

	    //writing the data
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ImageOutputStream stream = ImageIO.createImageOutputStream(baos);
	    writer.setOutput(stream);
	    writer.write(metadata, new IIOImage(buffImg, null, metadata), writeParam);
	    stream.close();

	    return baos.toByteArray();
	}

}
