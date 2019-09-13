package com.arit.adserve.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import org.junit.Test;

public class MultiLineTextTest {

	public static void main(String[] args) {
		int width = 400;
		int height = 500;
		BufferedImage bufferedImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d =  bufferedImage.createGraphics();

		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, width, height);
		g2d.setBackground(Color.WHITE);
		g2d.setColor(Color.RED);

		Hashtable<TextAttribute,Object> map = new Hashtable<TextAttribute,Object>();
		map.put(TextAttribute.FAMILY, "Arial");
		map.put(TextAttribute.SIZE,new Float(12.0));		
		map.put(TextAttribute.JUSTIFICATION, TextAttribute.JUSTIFICATION_FULL);
		AttributedString vanGogh = new AttributedString(
		"Many people believe that Vincent van Gogh painted his best works " +
		"during the two-year period he spent in Provence. Here is where he " +
		"painted The Starry Night--which some consider to be his greatest " +
		"work of all. However, as his artistic brilliance reached new " +
		"heights in Provence, his physical and mental health plummeted. ",
		map);

		AttributedCharacterIterator paragraph = vanGogh.getIterator();
		int paragraphStart   = paragraph.getBeginIndex();
		int paragraphEnd = paragraph.getEndIndex();
		FontRenderContext frc = g2d.getFontRenderContext();
		LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, frc);
		float breakWidth = 390f;
		float drawPosY = 10f;
		float drawPosx = 10f;
		lineMeasurer.setPosition(paragraphStart);
		while(lineMeasurer.getPosition()< paragraphEnd ){
		    TextLayout layout = lineMeasurer.nextLayout(breakWidth);
//		    drawPosx = layout.isLeftToRight()?0:breakWidth-layout.getAdvance();
		    drawPosY += layout.getAscent();
		    layout.draw(g2d,drawPosx,drawPosY);
		    drawPosY += layout.getDescent() + layout.getLeading();
		}


		g2d.dispose();

		File file = new File("myimage.png");
		try {
			ImageIO.write(bufferedImage,"png",file);
		

		file = new File("myimage.jpg");
		ImageIO.write(bufferedImage,"jpg",file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Test
	public void testCreateImage() throws Exception {
		MultiLineText mlt = new MultiLineText.Builder().text(text).width(100).breakWidth(80f).fontSize(10f).build();
		File file = new File("testimage.jpg");
		ImageIO.write(mlt.getTextImage(), "jpg", file);
	}
	
	String text = "Many people believe that Vincent van Gogh painted his best works "
			+ "during the two-year period he spent in Provence. Here is where he "
			+ "painted The Starry Night--which some consider to be his greatest "
			+ "work of all. However, as his artistic brilliance reached new "
			+ "heights in Provence, his physical and mental health plummeted. ";

}
