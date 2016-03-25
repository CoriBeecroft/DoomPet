package com.beefarm.DoomPet;

import com.cyntaks.sgf.core.Behavior;
import com.cyntaks.sgf.core.Entity;

public class GoToObjectBehavior extends Behavior
{
	//(Cori) right now all of this is for movement along the x-axis
	
	private EnvironmentObject object;
	private EnvironmentObject destinationObject;
	private Behavior behaviorToReturnTo;
	
	//(Cori) Maybe add in velocity and acceleration variables 
	//(Cori) add in an option that says where the entity will stop in relation to the object, default(and what is implemented now) will be when the center of the entity is at the center of the object(in terms of x position)
	//(Cori) make is possible to specify a x and y positions instead of just an environment object
	
	public GoToObjectBehavior(EnvironmentObject object, Behavior behaviorToReturnTo)
	{
		this.destinationObject = object;
		this.behaviorToReturnTo = behaviorToReturnTo;
	}
	
	public void update(float delta) 
	{
		//System.out.println("Go to object B");
		if((object.getXMidpoint() <= destinationObject.getXMidpoint() && object.getXVelocity() < 0) || (object.getXMidpoint() >= destinationObject.getXMidpoint()) && object.getXVelocity() > 0)
		{
			if(destinationObject.getXVelocity() == 0)
			{
				object.setVelocity(0, 0);
				object.setAcceleration(0, 0);
				object.setBehavior(behaviorToReturnTo);				
			}
			else
			{
				setVelocityAndAcceleration();
			}
		}
	}

	public void handleCollision(Entity collider) 
	{
		
	}

	public void init() 
	{
		setVelocityAndAcceleration();
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

	private void setVelocityAndAcceleration()
	{
		if((destinationObject.getXMidpoint() - object.getXMidpoint()) < 0) //if the object is at a lower x position than the entity
		{
			if(object.getXVelocity() > 0) //if the entity isn't already walking in the direction of the object
			{
				object.setVelocity(-object.getXVelocity(), object.getYVelocity());
			}
			object.setAcceleration(-2, 0); //accelerate towards the object
		}
		else if(destinationObject.getXMidpoint() - object.getXMidpoint() > 0) //if the object is at a higher x position than the entity
		{
			if(object.getXVelocity() < 0) //if the entity isn't already walking in the direction of the object
			{ 
				object.setVelocity(object.getXVelocity(), object.getYVelocity());
			}
			object.setAcceleration(2, 0); //accelerate towards the object
		}
	}
}
