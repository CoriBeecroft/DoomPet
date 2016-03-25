package com.cyntaks.sgf.sprite;

import com.cyntaks.sgf.core.Entity;

public class StaticAnimatedSprite extends AnimatedSprite implements Cloneable
{
  public static final long serialVersionUID = 1;
  public StaticAnimatedSprite(String imageResourceName, int x, int y,
                        int cellWidth, int cellHeight,
                        int[][] frameDelays, boolean reverseDirection)
  {
    super(imageResourceName, cellWidth, cellHeight, frameDelays, reverseDirection);
    super.setEntity(new Entity());
    super.getEntity().setPosition(x, y);
    super.getEntity().setOriginalSize(cellWidth, cellHeight); //(Cori) you might need to use scale to get the correct size
    SpriteManager.getInstance().requestSpriteAdd(this);
  }
  
  /**
   * constructs a new Animator with uniform cell size and frame delay specified for each frame.
   * 
   */
  public StaticAnimatedSprite(String imageResourceName, int x, int y,
                        int cellWidth, int cellHeight,
                        int frameDelay, boolean reverseDirection)
  {
    this(imageResourceName, x, y, cellWidth, cellHeight,
         new int[][]{new int[]{frameDelay}}, reverseDirection);
  }
  
  public Object clone()
  {
    return super.clone();
  }
  
  public void setPosition(float x, float y)
  {
    super.getEntity().setPosition(x, y);
  }
}