package com.cyntaks.sgf.utils;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class ImageLoader
{
	private static GraphicsConfiguration gc;

	static
	{
		gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
	}
	
	public static BufferedImage loadImage(String path, int transparency)
	{
		BufferedImage image = null;
		try
		{
			InputStream is = ImageLoader.class.getClassLoader().getResourceAsStream(path);
			BufferedInputStream bi = new BufferedInputStream(is);
			BufferedImage src = ImageIO.read(bi);
	  
			if(src == null)
				System.err.println("Image: " + path + " was not found!");
			
			ImageScale2x scaler = new ImageScale2x(src);
			BufferedImage intermediate = scaler.getScaledImage();
			image = gc.createCompatibleImage(src.getWidth(), src.getHeight(), transparency);
			Graphics2D g2 = image.createGraphics();
			g2.drawImage(intermediate, 0, 0, image.getWidth(), image.getHeight(), null);
			g2.dispose();
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
			return null;
		}
    
		return image;
	}
}