package com.arit.adserve.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

/**
 * Call Builder to create an instance
 * some default values are used
 * private String fontFamily = "Arial";
 * private Float fontSize = new Float(12.0);	
 * private float breakWidth = 390f;
 * private float drawPosY = 10f;
 * private float drawPosx = 10f;
 *
 */
public class MultiLineText {
	
	private String fontFamily;
	private Float fontSize;	
	private float breakWidth;
	private float drawPosY;
	private float drawPosX;
	private Color fontColor;
	private Color fontBackroundColor;
	private int width;
	private String text;

	
	
	public static class Builder {
		private String fontFamily = "Arial";
		private Float fontSize = new Float(12.0);	
		private float breakWidth = 390f;
		private float drawPosY = 10f;
		private float drawPosX = 10f;
		private Color fontColor = Color.BLACK;
		private Color fontBackroundColor = Color.GREEN;
		private int width;
		private String text;

		
		public Builder fontFamily(String fontFamily) {
			this.fontFamily = fontFamily;
			return this;
		}
		public Builder fontSize(Float fontSize) {
			this.fontSize = fontSize;
			return this;
		}
		public Builder breakWidth(Float breakWidth) {
			this.breakWidth = breakWidth;
			return this;
		}
		public Builder drawPosY(float drawPosY) {
			this.drawPosY = drawPosY;
			return this;
		}
		public Builder drawPosX(float drawPosX) {
			this.drawPosX = drawPosX;
			return this;
		}
		public Builder width(int width) {
			this.width = width;
			return this;
		}
		public Builder text(String text) {
			this.text = text;
			return this;
		}
		public Builder fontColor(Color fontColor) {
			this.fontColor = fontColor;
			return this;
		}
		public Builder fontBackround(Color fontBackroundColor) {
			this.fontBackroundColor = fontBackroundColor;
			return this;
		}
		
		public MultiLineText build() {
			return new MultiLineText(this);
		}
		
	}
	
	private MultiLineText(Builder b) {
		assert !StringUtils.isEmpty(b.text) && b.width > (b.drawPosX + b.breakWidth) : "check submitted params";
		this.fontFamily = b.fontFamily;
		this.fontSize = b.fontSize;
		this.breakWidth = b.breakWidth;
		this.drawPosX = b.drawPosX;
		this.drawPosY = b.drawPosY;
		this.width = b.width;
		this.text = b.text;
		this.fontColor = b.fontColor;
		this.fontBackroundColor = b.fontBackroundColor;
	}
	

	public BufferedImage getTextImage() {
		Map<TextAttribute,Object> map = new HashMap<>();
		map.put(TextAttribute.FAMILY, fontFamily);
		map.put(TextAttribute.SIZE, fontSize);		
		map.put(TextAttribute.JUSTIFICATION, TextAttribute.JUSTIFICATION_FULL);
		AttributedString attrString = new AttributedString(text, map);
		// a naive calculation of an approximate image height to fit the text 
		int calculatedHeight = (int) (text.length() * fontSize/breakWidth * fontSize + drawPosY) ;
		BufferedImage bufferedImage = new BufferedImage(width, calculatedHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d =  bufferedImage.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);		
		g2d.setColor(fontBackroundColor);
		g2d.fillRect(0, 0, width, calculatedHeight);
		g2d.setBackground(fontBackroundColor);
		g2d.setColor(fontColor);
		AttributedCharacterIterator paragraph = attrString.getIterator();
		int paragraphStart   = paragraph.getBeginIndex();
		int paragraphEnd = paragraph.getEndIndex();
		FontRenderContext frc = g2d.getFontRenderContext();
		LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, frc);		
		lineMeasurer.setPosition(paragraphStart);
		while(lineMeasurer.getPosition()< paragraphEnd ){
		    TextLayout layout = lineMeasurer.nextLayout(breakWidth);
//		    drawPosx = layout.isLeftToRight()?0:breakWidth-layout.getAdvance();
		    drawPosY += layout.getAscent();
		    layout.draw(g2d, drawPosX, drawPosY);
		    drawPosY += layout.getDescent() + layout.getLeading();
		}
		g2d.dispose();
		return bufferedImage.getSubimage(0, 0, width, (int) drawPosY + 5);
	}
}
