package com.cyntaks.sgf.core;

import java.io.Serializable;

public class CollisionGroup implements Serializable, Cloneable
{
  public static final long serialVersionUID = 1L;
  
  private int id;
  private int[] collidingGroups;
  
  public CollisionGroup(int id, int[] collidingGroups)
  {
    this.id = id;
    this.collidingGroups = collidingGroups;
  }
  
  public boolean collidesWith(CollisionGroup group)
  {
    for (int i = 0, n = group.collidingGroups.length; i < n; i++) 
    {
      if(group.collidingGroups[i] == id)
        return true;
    }
    for (int i = 0, n = collidingGroups.length; i < n; i++) 
    {
      if(collidingGroups[i] == group.id)
        return true;
    }
    
    return false;
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
  
  public int getID()
  {
    return id;
  }
  
  public int[] getCollidingGroups()
  {
    return collidingGroups;
  }
}