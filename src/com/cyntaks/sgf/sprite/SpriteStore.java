package com.cyntaks.sgf.sprite;

import java.util.HashMap;

public class SpriteStore 
{
  private static SpriteStore INSTANCE;
  private HashMap sprites;
  
  public static SpriteStore getInstance()
  {
    if(INSTANCE == null)
      INSTANCE = new SpriteStore();
    
    return INSTANCE;
  }
  
  private SpriteStore()
  {
    sprites = new HashMap();
  }
  
  public void addSpriteModel(AbstractSprite sprite, String name)
  {
    sprites.put(name, sprite);
  }
  
  public AbstractSprite removeSpriteModel(String name)
  {
    return (AbstractSprite)sprites.remove(name);
  }
  
  public AbstractSprite getInstanceOf(String name)
  {
    AbstractSprite sprite = (AbstractSprite)((AbstractSprite)sprites.get(name)).clone();
    SpriteManager.getInstance().requestSpriteAdd(sprite);
    return sprite;
  }
  
  public void clear()
  {
    sprites.clear();
  }
  
  public void destroy()
  {
    clear();
    INSTANCE = null;
  }
}