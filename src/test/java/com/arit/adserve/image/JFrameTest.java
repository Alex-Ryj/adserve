package com.arit.adserve.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class JFrameTest {
	
	public static void main(String[] args) {
		
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File("files/Panel1.PNG"));
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		Image dimg = img.getScaledInstance(50, 50,
		        Image.SCALE_SMOOTH);
		
		ImageIcon icon = new ImageIcon(dimg);		
		JLabel picLabel = new JLabel(icon);
		JPanel jPanel = new JPanel();
		
		jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
		jPanel.add(picLabel);
		JTextArea textField = new JTextArea("Left ajfls ajfl\n sajfa af djlsj fjfl jsaf ljsaf ;alsdj fsjf sjfk sjafljsa a;sldjfsljflsj");
		textField.setColumns(20);
		textField.setRows(10);
		textField.setWrapStyleWord(true);
//		textField.setPreferredSize(new Dimension(50, 20));
		jPanel.add(textField);
		JFrame frame = new JFrame();
		frame.setBackground(Color.WHITE);
		frame.setUndecorated(true);
		frame.getContentPane().add(jPanel);
		frame.pack();
		BufferedImage bi = new BufferedImage(jPanel.getWidth(),jPanel .getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = bi.createGraphics();
		jPanel.print(graphics);
		graphics.dispose();
		frame.dispose();
		File file = new File("myimage.png");
		try {
			ImageIO.write(bi,"png",file);		

		file = new File("myimage.jpg");
		ImageIO.write(bi,"jpg",file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
