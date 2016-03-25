package com.beefarm.DoomPet;

import com.cyntaks.sgf.core.Behavior;
import com.cyntaks.sgf.core.Entity;

public class WaitPetBehavior extends PetBehavior
{	
	private int numCyclesToWait;
	private int currentCycles;
	private Behavior behaviorToReturnTo;
	private float initialXVelocity;
	
	public WaitPetBehavior(int numCyclesToWait, Behavior behaviorToReturnTo)
	{
		this.numCyclesToWait = numCyclesToWait;
		this.currentCycles = 0;
		this.behaviorToReturnTo = behaviorToReturnTo;
	}
	
	public void update(float delta)
	{
		if(currentCycles == numCyclesToWait)
		{
			pet.setVelocity(initialXVelocity, pet.getYVelocity());
			pet.setBehavior(behaviorToReturnTo);
		}
		currentCycles++; //(Cori) It's possible this is off by one, but I think it's fine
	}
	
	public void handleCollision(Entity collider)
	{
		
	}
	
	public void init()
	{
		initialXVelocity = pet.getXVelocity();
		pet.setVelocity(0, 0);
	}

}
