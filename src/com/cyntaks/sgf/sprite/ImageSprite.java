package com.cyntaks.sgf.sprite;

import com.cyntaks.sgf.core.Entity;
import com.cyntaks.sgf.core.ResourceManager;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ImageSprite extends AbstractSprite implements Cloneable
{
  public static final long serialVersionUID = 1;
  private transient BufferedImage image;
  private String imageResourceName;
  private int xOffset;
  private int yOffset;
  private int drawWidth;
  private int drawHeight;
  
  public ImageSprite(String imageResourceName)
  {
    this(null, imageResourceName);
    drawWidth = image.getWidth();
    drawHeight = image.getHeight();
  }
  
  public ImageSprite(Entity entity, String imageResourceName)
  {
    super(entity);
    
    this.imageResourceName = imageResourceName;
    image = ResourceManager.getImage(imageResourceName);
    drawWidth = image.getWidth();
    drawHeight = image.getHeight();
    
    if(entity != null)
      getEntity().setOriginalSize(drawWidth, drawHeight); //(Cori) you might need to use scale to get the correct size
  }
  
  public ImageSprite(String imageResourceName, int xOffset, int yOffset, int drawWidth, int drawHeight)
  {
	  this(imageResourceName);
	  this.xOffset = xOffset;
	  this.yOffset = yOffset;
	  this.drawWidth = drawWidth;
	  this.drawHeight = drawHeight;
  }
  
  public ImageSprite(Entity entity, String imageResourceName, int xOffset, int yOffset, int drawWidth, int drawHeight)
  {
	  this(entity, imageResourceName);
	  this.xOffset = xOffset;
	  this.yOffset = yOffset;
	  this.drawWidth = drawWidth;
	  this.drawHeight = drawHeight;
  }
  
  public Object clone()
  {
    return super.clone();
  }
  
  public void setEntity(Entity entity)
  {
    super.setEntity(entity);
    entity.setOriginalSize(image.getWidth(), image.getHeight()); //(Cori) you might need to use scale to get the correct size
  }
  
  public void render(Graphics2D g2)
  {
	 if(super.isVisible() && super.getEntity().isVisible())
	  g2.drawImage(image, 0, 0, drawWidth, drawHeight,
			  xOffset, yOffset, xOffset+drawWidth, yOffset+drawHeight, null);
  }
  
  public int getWidth()
  {
    return image.getWidth();
  }
  
  public int getHeight()
  {
    return image.getHeight(); 
  }
  
  public String toString()
  {
    return "x: " + getX() + ", y: " + getY() + ", width: " + getWidth() + ", height: " + getHeight();
  }
  
  public BufferedImage getImage()
  {
    return image;
  }
}