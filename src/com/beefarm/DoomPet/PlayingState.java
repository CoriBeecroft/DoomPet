package com.beefarm.DoomPet;

import java.awt.Graphics2D;
import java.awt.Transparency;
import java.util.ArrayList;

import com.cyntaks.sgf.core.GameState;
import com.cyntaks.sgf.core.ResourceManager;
import com.cyntaks.sgf.core.SGFGame;
import com.cyntaks.sgf.sprite.AbstractSprite;
import com.cyntaks.sgf.sprite.AnimatedSprite;
import com.cyntaks.sgf.sprite.ImageSprite;
import com.cyntaks.sgf.sprite.StateSprite;

public class PlayingState extends GameState
{
	//public static Pet petOddling;
	private Pet pet;
	private Environment environment;
	
	public PlayingState(String name) 
	{
		super(name);
	}

	public void loadResources() 
	{
		ResourceManager.queueImage("images/background.png", Transparency.OPAQUE, "background");
		ResourceManager.queueImage("images/tree.png", Transparency.BITMASK, "tree");
		ResourceManager.queueImage("images/light.png", Transparency.BITMASK, "light");
		ResourceManager.queueImage("images/mancubus.png", Transparency.BITMASK, "mancubus");
		ResourceManager.queueImage("images/food.png", Transparency.BITMASK, "food"); //(Cori) what happens if I put two items in the hashmap with the same key
	}

	public void init() 
	{
		this.setInputHandler(new PlayingInputHandler(this));
		ArrayList<EnvironmentObject> objects = new ArrayList<EnvironmentObject>();
		
		EnvironmentObject backgroundEntity = new EnvironmentObject();
		backgroundEntity.setOriginalSize(ResourceManager.getImage("background").getWidth(), ResourceManager.getImage("background").getHeight());
		backgroundEntity.setScaleSize((SGFGame.getInstance().getWidth()), SGFGame.getInstance().getHeight());
		ImageSprite background = new ImageSprite(backgroundEntity, "background");
		background.setLayer(0);
		
		environment = new Environment(backgroundEntity, SGFGame.getInstance().getHeight() - 15*SGFGame.getInstance().getHeight()/100, objects);
		
		EnvironmentObject treeEntity = new EnvironmentObject();
		objects.add(treeEntity);
		treeEntity.setOriginalSize(ResourceManager.getImage("tree").getWidth(), ResourceManager.getImage("tree").getHeight());
		treeEntity.setScaleSize(SGFGame.getInstance().getWidth()/3, (SGFGame.getInstance().getHeight()/3)*2);
		treeEntity.setPosition(2*SGFGame.getInstance().getWidth()/3 + SGFGame.getInstance().getWidth()/25, SGFGame.getInstance().getHeight()/3);
		ImageSprite tree = new ImageSprite(treeEntity, "tree");
		tree.setLayer(1);
		
		EnvironmentObject lightEntity = new EnvironmentObject();
		objects.add(lightEntity);
		lightEntity.setOriginalSize(ResourceManager.getImage("light").getWidth(), ResourceManager.getImage("light").getHeight());
		lightEntity.setScaleSize(SGFGame.getInstance().getWidth()/6, SGFGame.getInstance().getHeight()/3);
		lightEntity.setPosition(SGFGame.getInstance().getWidth()/4, (SGFGame.getInstance().getHeight()/3)*2-SGFGame.getInstance().getHeight()/60);
		AnimatedSprite light = new AnimatedSprite(lightEntity, "light", 23, 80, 500, true);
		light.setLayer(1);
		
		pet = new Pet();
		//petOddling = pet;
		
		pet.setBehavior(new DefaultPetBehavior());
		objects.add(pet);
		pet.setEnvironment(environment);
		pet.setHorizontallySymmetrical(true);
		pet.setOriginalSize(ResourceManager.getImage("mancubus").getWidth(), ResourceManager.getImage("mancubus").getHeight());
		AbstractSprite[] mancubusSprites = new AbstractSprite[]{new AnimatedSprite(pet, "mancubus", 104, 77, 0, 77, 416, 77, 450, true), new AnimatedSprite(pet, "mancubus", 104, 77, 0, 0, 416, 77, 450, true), new AnimatedSprite(pet, "mancubus", 104, 77, 0, 231, 416, 77, 450, true), new AnimatedSprite(pet, "mancubus", 104, 77, 312, 0, 104, 77, 450, false)}; //(Cori) the last one is a little weird... it probably shouldn't really be an animated sprite... oh well, ask Weston what to do
		pet.setScaleSize(SGFGame.getInstance().getWidth()/3, SGFGame.getInstance().getHeight()/2);
		pet.setPosition(SGFGame.getInstance().getWidth()/4, SGFGame.getInstance().getHeight()/2-SGFGame.getInstance().getHeight()/60);
		pet.setSprite(new StateSprite(pet, mancubusSprites));
		pet.getSprite().setCurrent(2);
		pet.getSprite().setLayer(Environment.TOP_LAYER); 
	}

	public void preRender(Graphics2D g2) 
	{
		
	}
	
	public void render(Graphics2D g2) 
	{
		
	}
	
	public void update(float delta) 
	{
		
	}
	
	public void cleanup() 
	{
		
	}
	
	//
	// Accessor and Mutators
	//
	
	public Pet getPet() 
	{
		return pet;
	}


	public void setPet(Pet pet) 
	{
		this.pet = pet;
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
