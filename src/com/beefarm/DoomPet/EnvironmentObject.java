package com.beefarm.DoomPet;

import com.cyntaks.sgf.core.CollisionBounds;
import com.cyntaks.sgf.core.CollisionGroup;
import com.cyntaks.sgf.core.Entity;
import com.cyntaks.sgf.sprite.AbstractSprite;

public class EnvironmentObject extends Entity implements Comparable<EnvironmentObject>
{
	public static final int ENEMY = 1;
	public static final int FOOD = 2;
	public static final int PET = 42;

	public static final int BACKWARDS = 0;
	public static final int SIDEWAYS = 2;
	
	protected int type;
	protected Environment environment;
	protected AbstractSprite sprite;
	protected boolean horizontallySymmetrical; 
	
	//
	// Constructors
	//
	
	public EnvironmentObject()
	{
		super();
	}
	
//	public EnvironmentObject(int type)
//	{
//		super();
//		this.type = type;
//	}
	
	public EnvironmentObject(CollisionBounds collisionBounds, int type, Environment environment)
	{
		super(collisionBounds);
		this.type = type;
		this.environment = environment;
	}
	
	public EnvironmentObject(CollisionBounds collisionBounds, CollisionGroup collisionGroup, int type, Environment environment)
	{
		super(collisionBounds, collisionGroup);
		this.type = type;
		this.environment = environment;
	}
	
	//
	// Accessors and mutators
	//
	
	public void setType(int type)
	{
		this.type = type;
	}
	
	public int getType()
	{
		return type;
	}

	public Environment getEnvironment() 
	{
		return environment;
	}

	public void setEnvironment(Environment environment) 
	{
		this.environment = environment;
	}

	public AbstractSprite getSprite() 
	{
		return sprite;
	}

	public void setSprite(AbstractSprite sprite)
	{
		this.sprite = sprite;
	}
	
	public void setHorizontallySymmetrical(boolean symmetrical)
	{
		this.horizontallySymmetrical = symmetrical;
	}
	
	public boolean isHorizontallySymmetrical() 
	{
		return horizontallySymmetrical;
	}

	public void setVelocity(float xVol, float yVol)
	{
		if(horizontallySymmetrical)
		{
			if((xVol > 0 && !isXFlipped()) || (xVol < 0) && isXFlipped())
			{
				flip(true, false);
			}
		}
		super.setVelocity(xVol, yVol); 
	}
	
	//
	// Miscellaneous 
	//
	
	public int compareTo(EnvironmentObject other) 
	{
		return (this.type - other.type)/Math.abs(this.type - other.type);	
	}

}
