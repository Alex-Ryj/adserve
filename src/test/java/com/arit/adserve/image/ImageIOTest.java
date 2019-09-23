package com.arit.adserve.image;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Test;

public class ImageIOTest {
	
	private static final Font FONT = new Font("Serif", Font.PLAIN, 14);
	private static final float PARAGRAPH_BREAK = 10;
	private static final float MARGIN = 20;

	@Test
	public void testImageCreate() throws Exception {
	    int width = 250;
        int height = 250;
 
        // Constructs a BufferedImage of one of the predefined image types.
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
 
        // Save as PNG
        File file = new File("files/myimage.png");
        ImageIO.write(bufferedImage, "png", file);
 
        // Save as JPEG
        file = new File("files/myimage.jpg");
        ImageIO.write(bufferedImage, "jpg", file);
		/*
		 * BufferedImage source = ImageIO.read(new File("...")); BufferedImage logo =
		 * ImageIO.read(new File("/img/Panel1.PNG"));
		 * 
		 * Graphics g = source.getGraphics(); g.drawImage(logo, 0, 0, null);
		 */
	}
	
	
	@Test
	public void testImageEmbed() throws Exception {
	    int width = 350;
        int height = 350;
 
        // Constructs a BufferedImage of one of the predefined image types.
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
        
        
        BufferedImage logo = ImageIO.read(new File("files/Panel1.PNG"));
        g2d.drawImage(logo, 0, 0, null);
 
        // Disposes of this graphics context and releases any system resources that it is using. 
        g2d.dispose();
 
        // Save as PNG
        File file = new File("files/myimage.png");
        ImageIO.write(bufferedImage, "png", file);
 
        // Save as JPEG
        file = new File("files/myimage.jpg");
        ImageIO.write(bufferedImage, "jpg", file);
		/*
		 * BufferedImage source = ImageIO.read(new File("...")); BufferedImage logo =
		 * ImageIO.read(new File("/img/Panel1.PNG"));
		 * 
		 * Graphics g = source.getGraphics(); g.drawImage(logo, 0, 0, null);
		 */
	}
	

	
	private List<BufferedImage> renderText(String str, int width, int height) {
	    String[] paragraphs = str.split("\n");

	    List<BufferedImage> images = new ArrayList<>();

	    BufferedImage img = new BufferedImage(width, 
	            height, 
	            BufferedImage.TYPE_3BYTE_BGR);
	    images.add(img);
	    Graphics2D g2d = setupGraphics(img);

	    float drawPosY = 0;

	    for (int paragraph=0;paragraph<paragraphs.length;paragraph++) {

	        drawPosY += PARAGRAPH_BREAK;

	        AttributedString attStr = new AttributedString(paragraphs[paragraph]);
	        AttributedCharacterIterator it = attStr.getIterator();
	        LineBreakMeasurer measurer = new LineBreakMeasurer(it, g2d.getFontRenderContext());
	        measurer.setPosition(it.getBeginIndex());

	        while (measurer.getPosition() < it.getEndIndex()) {
	            TextLayout layout = measurer.nextLayout(img.getWidth()-MARGIN*2);

	            if (drawPosY > img.getHeight() - layout.getAscent() - layout.getDescent() - layout.getLeading()) {
	                drawPosY = 0;
	                img = new BufferedImage(
	                        width, 
	                        height, 
	                        BufferedImage.TYPE_3BYTE_BGR);
	                images.add(img);
	                g2d.dispose();
	                g2d = setupGraphics(img);
	            }

	            drawPosY += layout.getAscent();

	            layout.draw(g2d, MARGIN, drawPosY);

	            drawPosY += layout.getDescent()+layout.getLeading();
	        }
	    }
	    g2d.dispose();

	    return images;
	}
	
	private Graphics2D setupGraphics(BufferedImage img) {
	    Graphics2D g2d = img.createGraphics();
	    g2d.setFont(FONT);
	    g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
	    g2d.setColor(Color.BLACK);
	    return g2d;
	}
	
	
	


}
