package com.cyntaks.sgf.sprite;

import com.cyntaks.sgf.core.Entity;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.Serializable;

public abstract class AbstractSprite implements Serializable, Cloneable
{
  public static final long serialVersionUID = 1L;
  
  private Entity entity;
  private int layer;
  private boolean visible;
  private static final Entity DUMMY = new Entity();
  
  public AbstractSprite(Entity entity)
  {
	  visible = true;
    if(entity != null)
    {
      this.entity = entity;
      SpriteManager.getInstance().requestSpriteAdd(this);
     }
    else
    	this.entity = DUMMY;
  }
  
  public AbstractSprite()
  {
    
  }
  
  public void update(float delta)
  {
    
  }
  
  public Object clone()
  {
    try 
    {
      return super.clone();
    } catch (CloneNotSupportedException ex) 
    {
      ex.printStackTrace();
      return null;
    }
  }
  
  final void renderAux(Graphics2D g2)
  {
    if(isVisible() && entity.isVisible())
    {
      AffineTransform oldTransform = g2.getTransform();
      
      g2.translate(entity.getXPosition() + entity.getXScaleOffset(), entity.getYPosition() + entity.getYScaleOffset());
      g2.scale(entity.getXScale(), entity.getYScale());
      g2.rotate(Math.toRadians(entity.getRotation()), 
                entity.getCurrentWidth()/2/entity.getXScale(), 
                entity.getCurrentHeight()/2/entity.getYScale());
      
      render(g2);
      
      g2.setTransform(oldTransform);
    }
  }
  
  public abstract void render(Graphics2D g2);
  
  public float getX()
  {
    return entity.getXPosition();
  }
  public float getY()
  {
    return entity.getYPosition();
  }
  
  public int getWidth()
  {
    return entity.getCurrentWidth();
  }
  public int getHeight()
  {
    return entity.getCurrentHeight();
  }
  
  public boolean isAlive()
  {
    return entity.isAlive();
  }
  
  public boolean isVisible()
  {
    return visible;
  }
  
  public Entity getEntity()
  {
    return entity;
  }

  public int getLayer()
  {
    return layer;
  }

  public void setLayer(int layer)
  {
    this.layer = layer;
  }

  public void setEntity(Entity entity)
  {
    this.entity = entity;
    SpriteManager.getInstance().requestSpriteRemove(this);
    SpriteManager.getInstance().requestSpriteAdd(this);
  }
  
  public void removeEntity()
  {
	  this.entity = DUMMY;
  }

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}