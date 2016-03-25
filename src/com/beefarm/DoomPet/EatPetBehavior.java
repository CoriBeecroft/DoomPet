package com.beefarm.DoomPet;

import com.cyntaks.sgf.core.Behavior;
import com.cyntaks.sgf.core.Entity;
import com.cyntaks.sgf.event.Event;
import com.cyntaks.sgf.event.EventListener;

public class EatPetBehavior extends PetBehavior implements EventListener
{
	private static int eatID = 0; 
	private static final int TIME_TO_EAT = 1000;  //(Cori) in milliseconds?
	
	private EnvironmentObject food;
	private Behavior behaviorToReturnTo;
	private boolean isEating;
	private boolean atObject;
	
	public EatPetBehavior(EnvironmentObject food, PetBehavior behaviorToReturnTo) 
	{
		this.food = food;
		this.behaviorToReturnTo = behaviorToReturnTo;
	}
	
	//
	// Methods for PetBehavior 
	//
	
	public void update(float delta) 
	{
		//System.out.println("Eat PB");
		if(!atObject)
		{
			pet.setBehavior(new GoToObjectBehavior(food, this));
			if(food.getXVelocity() == 0 && food.getYVelocity() == 0)
			{
				atObject = true;
			}
		}
		else if(!isEating && atObject)
		{
			isEating = true;
			Event eat = new Event(eatID, TIME_TO_EAT, Event.EVERY_FRAME, false, this);
			eatID++;
			//eat.addEventListener(this);
			pet.getSprite().setCurrent(3);
		}
	}

	public void handleCollision(Entity collider) 
	{
		
	}

	public void init() 
	{
		
	}

	//
	// Methods for EventListener interface
	//
	
	public void update(int id, float value) 
	{
		
	}

	public void finish(int id)
	{
		pet.getEnvironment().removeFood(food);
		pet.setBehavior(behaviorToReturnTo);
	}
}
