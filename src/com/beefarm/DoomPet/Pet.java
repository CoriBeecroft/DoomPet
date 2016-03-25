package com.beefarm.DoomPet;

import com.cyntaks.sgf.sprite.SpriteManager;
import com.cyntaks.sgf.sprite.StateSprite;

public class Pet extends EnvironmentObject
{	
	private Environment environment;
	private StateSprite sprite; //(Cori) does it make sense to have this here?
	
//	public Pet(int type)
//	{
//		super(type);
//	}
	
	public StateSprite getSprite() 
	{
		return sprite;
	}

	public void setSprite(StateSprite sprite) 
	{
		if(this.sprite != null)
		{
			SpriteManager.getInstance().requestSpriteRemove(this.sprite);
			this.sprite.setEntity(null);
		}
		sprite.setEntity(this);
		SpriteManager.getInstance().requestSpriteAdd(sprite);
		
		this.sprite = sprite;
	}

	public Environment getEnvironment()
	{
		return environment;
	}
	
	public void setEnvironment(Environment environment) 
	{
		this.environment = environment;
	}
}
