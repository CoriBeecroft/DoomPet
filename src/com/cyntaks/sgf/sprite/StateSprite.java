package com.cyntaks.sgf.sprite;

import com.cyntaks.sgf.core.Entity;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class StateSprite extends AbstractSprite implements Cloneable
{	
  public static final long serialVersionUID = 1;
  private ArrayList<AbstractSprite> states;
  private transient AbstractSprite currentSprite;
  private Entity entity;
  private int currentIndex;
  
  public StateSprite(Entity entity, AbstractSprite sprite)
  {
	  super(null);
	  states = new ArrayList<AbstractSprite>();
	  currentSprite = sprite;
	  this.entity = entity;
	  currentSprite.setEntity(entity);
  }
  public StateSprite(Entity entity, AbstractSprite[] states)
  {
    this(entity, states[0]);
    for (int i = 0; i < states.length; i++) 
    {
    	states[i].setVisible(false);
      this.states.add(i, states[i]);
    }
    states[0].setVisible(true);
  }
  
  public Object clone()
  {
    return super.clone();
  }
  
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    in.defaultReadObject();
    
    currentSprite = (AbstractSprite)states.get(0);
  }
  
  public void updateOccurred(float delta)
  {
    currentSprite.update(delta);
  }
  
  public void render(Graphics2D g2)
  {
    currentSprite.render(g2);
  }
  
  public void setCurrent(int index)
  {
	currentSprite.removeEntity();
	currentSprite.setVisible(false);
	
    currentSprite = (AbstractSprite)states.get(index);
    
    currentSprite.setEntity(entity);
    currentSprite.setVisible(true);
  }
  
  public int getCurrentIndex()
  {
	  return currentIndex;
  }
  
  public void incrementIndex()
  {
	  currentIndex = (currentIndex+1)%states.size();
	  setCurrent(currentIndex);
  }
  
  public void addState(AbstractSprite state, int index)
  {
    states.add(index, state);
  }
  
  public AbstractSprite removeState(int index)
  {
    return (AbstractSprite)states.remove(index);
  }
  
  public boolean removeState(AbstractSprite state)
  {
    return states.remove(state);
  }
}