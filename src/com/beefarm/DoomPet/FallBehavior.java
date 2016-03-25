package com.beefarm.DoomPet;

import com.cyntaks.sgf.core.Behavior;
import com.cyntaks.sgf.core.Entity;

public class FallBehavior extends Behavior //(Cori) maybe later add an EnvironmentObjectBehavior class
{
	private EnvironmentObject entity;
	
	private int minFinalXPosition;
	private int maxFinalXPosition;
	private int minFinalYPosition; //(Cori)	 the whole y range things is a good idea, but it won't look right unless I can make sure that the object closest to the bottom of the screen is in front. =/ I think there is a reasonably easy way to do this, ask Weston!
	private int maxFinalYPosition;
	private int finalXPosition;
	private int finalYPosition;

	//Use this constructor to get object to fall to exact x and y positions
	public FallBehavior(int finalXPosition, int finalYPosition)
	{
		this.finalXPosition = finalXPosition;
		this.finalYPosition = finalYPosition;
	}
	
	//Use this constructor to get an object to fall within a range of x and y positions
	public FallBehavior(int minFinalXPosition, int maxFinalXPosition, int minFinalYPosition, int maxFinalYPosition)
	{
		this.minFinalXPosition = minFinalXPosition;
		this.maxFinalXPosition = maxFinalXPosition;
		this.minFinalYPosition = minFinalYPosition;
		this.maxFinalYPosition = maxFinalYPosition;		
	}
	
	public void update(float delta) 
	{
		if((entity.getXVelocity() > 0 && entity.getXPosition() >= finalXPosition) || (entity.getXVelocity() < 0 && entity.getXPosition() <= finalXPosition))
		{
			entity.setVelocity(0, entity.getYVelocity());
			entity.setPosition(finalXPosition, entity.getYPosition());
		}
		if(entity.getYVelocity() != 0 && entity.getYPosition() >= finalYPosition)
		{
			entity.setPosition(entity.getXPosition(), finalYPosition);
			entity.setVelocity(0, 0);
			entity.setAcceleration(0, 0);
		}
	}

	public void handleCollision(Entity collider) 
	{
		
	}

	public void init() 
	{
		finalXPosition += (int)(Math.random()*(maxFinalXPosition-minFinalXPosition) + minFinalXPosition); 
		if(finalXPosition == entity.getXPosition()) //(Cori) There's probably a better way to do this
		{
			finalXPosition--;
		}
		finalYPosition += (int)(Math.random()*(maxFinalYPosition-minFinalYPosition) + minFinalYPosition);
		 
		entity.setVelocity(700*((finalXPosition-entity.getXPosition())/Math.abs(finalXPosition-entity.getXPosition())), 20);
		entity.setAcceleration(0, 9);
	}
	
	public void setOwner(Entity entity)
	{
		if(entity instanceof EnvironmentObject)
		{
			this.entity = (EnvironmentObject)entity;
		}
		else
		{
			throw new IllegalArgumentException();
		}
	}
	
}
