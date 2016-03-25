package com.cyntaks.sgf.core;

public abstract class Behavior implements Cloneable
{
	private boolean initialized;
	public abstract void update(float delta);
	public abstract void handleCollision(Entity collider);
	public abstract void init();
  
	public void setGameState(GameState gameState)
	{
  
	}
  
	public void setOwner(Entity entity)
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
  
	boolean isInitialized()
	{
		return initialized;
	}
	
	void setInitialized(boolean initialized) 
	{
		this.initialized = initialized;
	}
}