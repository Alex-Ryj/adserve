package com.arit.adserve.image;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import com.sun.imageio.plugins.png.PNGMetadata;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ImagePngTest {
	

	@Test
	public void test() throws Exception {
		int width = 100, height = 100;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        // Create a graphics which can be used to draw into the buffered image
        Graphics2D g2d = bufferedImage.createGraphics();
 
        // fill all the image with white
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, width, height);
 
        // create a circle with black
        g2d.setColor(Color.black);
        g2d.fillOval(0, 0, width, height);
 
        // create a string with yellow
        g2d.setColor(Color.yellow);
        g2d.drawString("Java Code Geeks", 50, 120);
 
        // Disposes of this graphics context and releases any system resources that it is using. 
        g2d.dispose();
        
        byte[] pngWithMeta = writeCustomData(bufferedImage, "key", "value");
        FileUtils.writeByteArrayToFile(new File("test.png"), pngWithMeta);
        BufferedImage bi2 = ImageIO.read(new File("test.png"));
        byte[] pngWithMeta2 = writeCustomData(bi2, "key2", value);
        assertEquals("value", readCustomData(pngWithMeta, "key"));
        assertEquals("value", readCustomData(pngWithMeta2, "key"));
        assertEquals(value, readCustomData(pngWithMeta2, "key2"));
        FileUtils.writeByteArrayToFile(new File("test1.png"), pngWithMeta2);
	}
	
	public byte[] writeCustomData(BufferedImage buffImg, String key, String value) throws Exception {
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
	
	public String readCustomData(byte[] imageData, String key) throws IOException{
	    ImageReader imageReader = ImageIO.getImageReadersByFormatName("png").next();

	    imageReader.setInput(ImageIO.createImageInputStream(new ByteArrayInputStream(imageData)), true);

	    // read metadata of first image
	    IIOMetadata metadata = imageReader.getImageMetadata(0);

	    //this cast helps getting the contents
	    PNGMetadata pngmeta = (PNGMetadata) metadata; 
	    NodeList childNodes = pngmeta.getStandardTextNode().getChildNodes();

	    for (int i = 0; i < childNodes.getLength(); i++) {
	        Node node = childNodes.item(i);
	        String keyword = node.getAttributes().getNamedItem("keyword").getNodeValue();
	        String value = node.getAttributes().getNamedItem("value").getNodeValue();
	        if(key.equals(keyword)){
	            return value;
	        }
	    }
	    return null;
	}
	
	String value = "vvvvvvvvvvvvvvvvvvaaaaaaaaaaaaaaaaaaaaaaaavvvvvvvvvvvvvvvvvvvvvvvvvvvaaaaaaaaaaaavvvvvvvvvvvvvvvvvvvvvvvvvvvvvaaaaaaaaaaaddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd";

}
