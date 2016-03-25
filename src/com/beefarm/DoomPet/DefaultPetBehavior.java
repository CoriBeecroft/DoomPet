package com.beefarm.DoomPet;

import com.cyntaks.sgf.core.Entity;
import com.cyntaks.sgf.core.SGFGame;

public class DefaultPetBehavior extends PetBehavior
{	
	private final int DEFAULT_PET_VELOCITY;
	
	private boolean environmentHasFood; //(Cori) come to think of it, this should probably actually be in environment. ...Maybe
	private boolean spriteHasBeenSet;
	
	//
	// Constructors
	//
	
	public DefaultPetBehavior()
	{
		this.DEFAULT_PET_VELOCITY = 64;
	}
	
	public DefaultPetBehavior(int defaultPetVelocity)
	{
		this.DEFAULT_PET_VELOCITY = defaultPetVelocity;
	}
	
	//
	// Methods for PetBehavior
	//
	
	public void update(float delta) 
	{
		//System.out.println("Default PB");
		evaluateEnvironment();
		ensureCorrectPetValues();
		keepPetInBounds();
	}

	public void handleCollision(Entity collider) 
	{
		
	}

	public void init() 
	{
		pet.setVelocity(DEFAULT_PET_VELOCITY, 0);
	}
	
	//
	// Helper methods 
	//
	
	private void evaluateEnvironment()
	{
		environmentHasFood = false; //(Cori) This will probably have to be changed later
		for(int k=0; k<pet.getEnvironment().getObjects().size(); k++)
		{
			switch(pet.getEnvironment().getObjects().get(k).getType())
			{
				case EnvironmentObject.ENEMY:
				{
					//switch to fight behavior
				}
				case EnvironmentObject.FOOD:
				{
					//maybe don't let the pet react until the food is on the ground or until it is halfway to the ground... maybe some sort of waiting behavior could be put into the EatPetBehavior class
					environmentHasFood = true;
					pet.setBehavior(new EatPetBehavior(pet.getEnvironment().getObjects().get(k), this));
					spriteHasBeenSet = false;
					break;
				}
			}
		}
	}
	
	private void ensureCorrectPetValues()
	{
		if(!environmentHasFood)
		{
			if(!spriteHasBeenSet)
			{
				pet.getSprite().setCurrent(EnvironmentObject.SIDEWAYS);
				spriteHasBeenSet = true;
			}
			if(Math.abs(pet.getXVelocity()) != DEFAULT_PET_VELOCITY)
			{
				pet.setVelocity(DEFAULT_PET_VELOCITY, pet.getYVelocity());
			}
		}
	}
	
	private void keepPetInBounds()
	{
		if(pet.getXPosition() < 0)
		{
			if(!environmentHasFood)
			{
				pet.setAcceleration(0, 0);
				pet.setVelocity(-1*pet.getXVelocity(), pet.getYVelocity());
				pet.setPosition(0, pet.getYPosition());
			}
		}
		if((pet.getXPosition() + pet.getCurrentWidth()) > SGFGame.getInstance().getWidth())
		{
			if(!environmentHasFood)
			{
				pet.setAcceleration(0, 0);
				pet.setVelocity(-1*pet.getXVelocity(), pet.getYVelocity());
				pet.setPosition(SGFGame.getInstance().getWidth()-pet.getCurrentWidth(), pet.getYPosition());
			}
		}
	}
}
