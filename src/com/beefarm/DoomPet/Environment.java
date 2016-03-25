package com.beefarm.DoomPet;

import java.util.ArrayList;
import com.cyntaks.sgf.core.Entity;
import com.cyntaks.sgf.core.ResourceManager;
import com.cyntaks.sgf.core.SGFGame;
import com.cyntaks.sgf.sprite.AnimatedSprite;
import com.cyntaks.sgf.sprite.SpriteManager;

public class Environment 
{
	public static final int TOP_LAYER = 4; //(Cori) is this really the best place for this?
	
	private Entity background;
	private ArrayList<EnvironmentObject> objects;
	
	private final int ground;
	
	public Environment(Entity background, int ground)
	{
		this.background = background;
		this.ground = ground;
		this.objects = new ArrayList<EnvironmentObject>();
	}
		
	public Environment(Entity background, int ground, ArrayList<EnvironmentObject> objects)
	{
		this.background = background;
		this.ground = ground;
		this.objects = objects;
	}
	
	public void dispenseFood()
	{
		EnvironmentObject foodEntity = new EnvironmentObject();
		foodEntity.setEnvironment(this);
		foodEntity.setType(EnvironmentObject.FOOD);		
		objects.add(foodEntity);
		foodEntity.setOriginalSize(ResourceManager.getImage("food").getWidth(), ResourceManager.getImage("food").getHeight());
		foodEntity.setScaleSize(SGFGame.getInstance().getHeight()/2, SGFGame.getInstance().getHeight()/8);
		foodEntity.setPosition(0, ground); //SGFGame.getInstance().getWidth(), SGFGame.getInstance().getHeight()); 
		foodEntity.setBehavior(new ProjectileBehavior(800, 1700, ProjectileBehavior.RIGHT));
		AnimatedSprite food = new AnimatedSprite(foodEntity, "food", 25, 25, 300, true);
		food.setLayer(2); //(Cori) Maybe this should be a constant instead of just 2
		foodEntity.setSprite(food);
	}
	
	public void removeFood(EnvironmentObject food)
	{
		objects.remove(food);
		food.setEnvironment(null);
		food.setAlive(false);
		SpriteManager.getInstance().requestSpriteRemove(food.getSprite());
	}
	
	public void dimLight()
	{
		
	}
	
	public void brightenLight()
	{
		
	}
	
	public void deployEnemy()
	{
		//remember to sort objects
	}
	
	public void removeEnemy()
	{
		
	}
	
	//
	//Accessors and Mutators
	//
	
	public Entity getBackground() 
	{
		return background;
	}

	public void setBackground(Entity background) 
	{
		this.background = background;
	}

	public ArrayList<EnvironmentObject> getObjects() 
	{
		return objects;
	}

	public void setObjects(ArrayList<EnvironmentObject> objects) 
	{
		this.objects = objects;
	}

	public int getGround() 
	{
		return ground;
	}
}
