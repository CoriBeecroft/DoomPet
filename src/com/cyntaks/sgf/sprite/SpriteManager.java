package com.cyntaks.sgf.sprite;

import com.cyntaks.sgf.core.UpdateListener;
import com.cyntaks.sgf.event.Event;
import com.cyntaks.sgf.event.EventListener;
import com.cyntaks.sgf.utils.RadixSort;
import java.awt.Graphics2D;
import java.util.ArrayList;

public final class SpriteManager implements UpdateListener, EventListener
{
  private static SpriteManager INSTANCE = new SpriteManager();
  private ArrayList<AbstractSprite> sprites;
  private ArrayList<AbstractSprite> spritesToAdd;
  private ArrayList<AbstractSprite> spritesToRemove;
  
  private RadixSort sort;
  
  private int sortType;
  public static final int NO_POSITION_SORT = -1;
  public static final int HIGHEST_TO_LOWEST_Y = 0;
  public static final int LOWEST_TO_HIGHEST_Y = 1;
  public static final int HIGHEST_TO_LOWEST_X = 2;
  public static final int LOWEST_TO_HIGHEST_X = 3;
  
  private Event sortEvent;
  private boolean needSort;
  private boolean paused;
    
  private SpriteManager()
  {
    needSort = true;
    paused = false;
    sprites = new ArrayList<AbstractSprite>();
    spritesToAdd = new ArrayList<AbstractSprite>();
    spritesToRemove = new ArrayList<AbstractSprite>();
    
    sortType = NO_POSITION_SORT;
    sort = new RadixSort();
    sortEvent = new Event(0, Event.INFINITE, Event.EVERY_FRAME, true, this);
  }
  
  public static SpriteManager getInstance()
  {  
    if(INSTANCE == null)
      INSTANCE = new SpriteManager();
    return INSTANCE;
  }
  
  /**
   * called when the Event used to trigger sprite sorting, is finished
   * @param id
   */
  public void finish(int id){}
  
  /**
   * called by an internal Event object at the given sort interval, sorts all of the sprites
   * @param id the Event's id
   * @param value isn't used
   */
  public void update(int id, float value)
  {
    sort();
  }
  
  private void sort()
  {
    int size = sprites.size();
    
    int[] sortLayer = new int[size];
    int[] sortXorY = new int[size];
    
    for (int i = 0; i < size; i++) 
    {
      AbstractSprite sprite = sprites.get(i);
      sortLayer[i] = sprite.getLayer();
      
      switch (sortType) 
      {
        case HIGHEST_TO_LOWEST_Y:
          sortXorY[i] = (int)-sprite.getY();
        break;
        case HIGHEST_TO_LOWEST_X:
          sortXorY[i] = (int)-sprite.getX();
        break;
        case LOWEST_TO_HIGHEST_Y:
          sortXorY[i] = (int)sprite.getY();
        break;
        case LOWEST_TO_HIGHEST_X:
          sortXorY[i] = (int)sprite.getX();
        break;
        default:
          sortXorY[i] = 0;
        break;
      }
    }
    
    sort.resetIndices().sort(sortXorY, size).sort(sortLayer, size);
    needSort = false;
  }
  
  public void updateOccurred(float delta)
  {
    if(!paused)
    {
      for (int i = 0; i < sprites.size(); i++) 
      {
        AbstractSprite sprite = sprites.get(i);
        if(!sprite.isAlive())
          removeSprite(sprite);
        else
          sprite.update(delta);
      }
    }
  }
  
  public void requestSort()
  {
    needSort = true;
  }
  
  public void render(Graphics2D g2)
  {
	  //this prevents synchronization issues
	  for (int i = 0; i < spritesToRemove.size(); i++) {
		removeSprite(spritesToRemove.get(i));
	  }
	  spritesToRemove.clear();
	  for (int i = 0; i < spritesToAdd.size(); i++) {
			addSprite(spritesToAdd.get(i));
	  }
	  spritesToAdd.clear();
	  
    if(needSort)
      sort();
    
    int[] indices = sort.getIndices();
      
    if(indices != null && !sprites.isEmpty())
    {
      for (int i = 0; i < sprites.size(); i++) 
      {
        AbstractSprite sprite = sprites.get(indices[i]);
          
        if(sprite.isAlive())
          sprite.renderAux(g2);
      }
    }
  }
  
  public void requestSpriteAdd(AbstractSprite sprite) {
	  spritesToAdd.add(sprite);
  }
  
  public void requestSpriteRemove(AbstractSprite sprite) {
	  spritesToRemove.add(sprite);
  }
  
  private void addSprite(AbstractSprite sprite)
  {
    needSort = true;
    sprites.add(sprite);
  }
  
  private boolean removeSprite(AbstractSprite sprite)
  {
    needSort = true;
    return sprites.remove(sprite);
  }
  
  public void setSortInterval(int sortInterval)
  {
    sortEvent.setUpdateFrequency(sortInterval);
  }
  
  public void destroy()
  {
    clear();
    INSTANCE = null;
  }
  
  public synchronized void clear()
  {
    sprites.clear();
    needSort = true;
    sort.resetIndices();
  }

  public void setSortType(int sortType)
  {
    this.sortType = sortType;
  }

  public boolean isPaused()
  {
    return paused;
  }

  public void setPaused(boolean paused)
  {
    this.paused = paused;
  }

  public ArrayList getSprites()
  {
    return sprites;
  }
}