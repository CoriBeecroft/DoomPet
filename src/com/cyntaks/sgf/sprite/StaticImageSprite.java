package com.cyntaks.sgf.sprite;

import com.cyntaks.sgf.core.Entity;

public class StaticImageSprite extends ImageSprite implements Cloneable
{
  public static final long serialVersionUID = 1;
  public StaticImageSprite(String imageResourceName, float x, float y)
  {
    super(imageResourceName);
    super.setEntity(new Entity());
    super.getEntity().setOriginalSize(getImage().getWidth(), getImage().getHeight());
    super.getEntity().setPosition(x, y);
    SpriteManager.getInstance().requestSpriteAdd(this);
  }
  
  public void setPosition(float x, float y)
  {
    super.getEntity().setPosition(x, y);
  }
  
  public Object clone()
  {
    return super.clone();
  }
}