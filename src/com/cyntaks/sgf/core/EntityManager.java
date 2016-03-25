package com.cyntaks.sgf.core;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class EntityManager implements UpdateListener
{
  private static EntityManager INSTANCE;
  private ArrayList<Entity> entities;
  private boolean paused;
  
  private EntityManager()
  {
    entities = new ArrayList<Entity>();
    paused = false;
  }
  
  public static EntityManager getInstance()
  {
    if(INSTANCE == null)
      INSTANCE = new EntityManager();
      
    return INSTANCE;
  }
  
  public final void updateOccurred(float delta)
  {
    updateEntities(delta);
    checkForCollisions();
    removeDeadEntities();
  }
  
  public final void render(Graphics2D g2)
  {
    if(SGFGame.getInstance().isDebugEnabled())
    {
      g2.setColor(Color.GREEN);
      g2.drawString("Entities: " + entities.size(), 10, 35);
    }
    for (int i = 0; i < entities.size(); i++) 
    {
      (entities.get(i)).render(g2);
    }
  }
  
  protected void updateEntities(float delta)
  {
    if(!paused)
    {
      for (int i = 0; i < entities.size(); i++) 
      {
        (entities.get(i)).update(delta);
      }
    }
  }
  
  protected void checkForCollisions()
  {
    if(!paused)
    {
      for (int i = 0; i < entities.size(); i++) 
      {
        Entity entity1 = entities.get(i);
        for (int j = i+1; j < entities.size(); j++) 
        {
          Entity entity2 = entities.get(j);
          if(entity2.isAlive() && entity1.isAlive() &&
             entity1.getCollisionGroup().collidesWith(entity2.getCollisionGroup()))
          {
            if(entity1.getCollisionBounds().collidesWith(entity2.getCollisionBounds()))
            {
              Behavior behavior = entity1.getBehavior();
              if(behavior != null)
                behavior.handleCollision(entity2);
                  
              Behavior behavior2 = entity2.getBehavior();
              if(behavior2 != null)
                behavior2.handleCollision(entity1);
            }
          }
        }
      }
    }
  }
  
  protected void removeDeadEntities()
  {
    for (int i = 0; i < entities.size(); i++) 
    {
      Entity entity = entities.get(i);
      if(!entity.isAlive())
        removeEntity(entity);
    }
  }
  
  public ArrayList<Entity> getEntitiesInCollisionGroup(int groupID)
  {
    ArrayList<Entity> list = new ArrayList<Entity>();
    for (int i = 0; i < entities.size(); i++) 
    {
      Entity entity = entities.get(i);
      if(entity.getCollisionGroup().getID() == groupID) 
        list.add(entity);
    }
    
    return list;
  }
  
  void addEntity(Entity entity)
  {
    entities.add(entity);
  }
  
  boolean removeEntity(Entity entity)
  {
    return entities.remove(entity);
  }
  
  public void destroy()
  {
    clear();
    INSTANCE = null;
  }
  
  public void clear()
  {
    entities.clear();
  }

  public boolean isPaused()
  {
    return paused;
  }

  public void setPaused(boolean paused)
  {
    this.paused = paused;
  }

  public ArrayList<Entity> getEntities()
  {
    return entities;
  }
}