package com.beefarm.DoomPet;

import com.cyntaks.sgf.core.Behavior;
import com.cyntaks.sgf.core.Entity;
import com.cyntaks.sgf.core.SGFGame;

public class ProjectileBehavior extends Behavior
{
	public static final int RIGHT = -1;
	public static final int LEFT = 1;
	
	private static final float X_ACCELERATION = 0;
	private static final float Y_ACCELERATION = 60;
	
	private final float ANGLE;
	private EnvironmentObject object;	
	private float initialSpeed;
	private float minInitialSpeed;
	private float maxInitialSpeed;
	
	//
	// Constructors 
	//
	
	public ProjectileBehavior(float initialVelocity, int direction)
	{
		this.initialSpeed = initialVelocity;
		if(direction == RIGHT)
		{
			ANGLE = 60;
		}
		else if(direction == LEFT)
		{
			ANGLE = 120;
		}
		else
		{
			ANGLE = 0;
		}
	}
	
	public ProjectileBehavior(float minInitialSpeed, float maxInitialSpeed, int direction)
	{
		this.initialSpeed = 0;
		this.minInitialSpeed = minInitialSpeed;
		this.maxInitialSpeed = maxInitialSpeed;
		if(direction == RIGHT)
		{
			ANGLE = 60;
		}
		else if(direction == LEFT)
		{
			ANGLE = 120;
		}
		else
		{
			ANGLE = 0;
		}
	}
	
	//
	// Methods for Behavior
	//
	
	public void update(float delta) 
	{
//		This is to try to keep the objects from falling very far below the ground line
//		if(object.getYPosition() >= Environment.GROUND - object.getCurrentHeight() && object.getYPosition() <= Environment.GROUND && object.getYVelocity() > 0 && object.getYAcceleration() > 0)
//		{
//			object.setAcceleration(object.getXAcceleration(), object.getYAcceleration()-10);
//		}
		
		if(object.getYVelocity() > 0 && object.getYPosition() >= object.getEnvironment().getGround()) //(Cori) This could  be a little bit risky... That is, if environment in object could ever have a null value when y velocity is greater than 0 
		{
			object.setAcceleration(0, 0);
			object.setVelocity(0, 0);
			object.setPosition(object.getXPosition(), object.getEnvironment().getGround()); //(Cori) Make sure this doesn't make the landing of the pellets look choppy
		}
		
	}

	public void handleCollision(Entity collider) 
	{
		
	}

	public void init() 
	{
		initialSpeed += (float)(Math.random()*(maxInitialSpeed - minInitialSpeed) + minInitialSpeed);
		object.setAcceleration(X_ACCELERATION, Y_ACCELERATION);
		object.setVelocity(initialSpeed, extractNormalizedXComponent(ANGLE), -extractNormalizedYComponent(ANGLE));
	}
	
	public void setOwner(Entity entity)
	{
		if(entity instanceof EnvironmentObject)
		{
			this.object = (EnvironmentObject)entity;
		}
		else
		{
			throw new IllegalArgumentException();
		}
	}

	//
	// Helper Methods
	//
	
	private float extractNormalizedXComponent(float angle)
	{
		angle = (float) Math.toRadians(angle);
		return (float) Math.cos(angle);
	}
	
	private float extractNormalizedYComponent(float angle)
	{
		angle = (float) Math.toRadians(angle);
		return (float) Math.sin(angle);
	}
}
