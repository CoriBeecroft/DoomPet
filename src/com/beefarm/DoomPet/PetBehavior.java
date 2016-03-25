package com.beefarm.DoomPet;

import com.cyntaks.sgf.core.Behavior;
import com.cyntaks.sgf.core.Entity;

public abstract class PetBehavior extends Behavior
{
	protected Pet pet;
	
	public void setOwner(Entity entity)
	{
		if(entity instanceof Pet)
		{
			this.pet = (Pet)entity;
		}
		else
		{
			throw new IllegalArgumentException();
		}
	}
}
