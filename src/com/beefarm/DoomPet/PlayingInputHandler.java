package com.beefarm.DoomPet;

import java.awt.event.KeyEvent;

import com.cyntaks.sgf.core.SGFGame;
import com.cyntaks.sgf.input.InputBinding;
import com.cyntaks.sgf.input.InputHandler;

public class PlayingInputHandler extends InputHandler
{
	private PlayingState state;
	
	private static final int EXIT = 0;
	private static final int FEED = 1;
	private static final int POKE = 2;
	private static final int TRAIN = 3;
	private static final int PUT_TO_SLEEP = 4;
	
	public PlayingInputHandler(PlayingState state)
	{
		this.state = state;
		
		addInputBinding(new InputBinding(EXIT, KeyEvent.VK_ESCAPE, InputBinding.KEYBOARD));
		addInputBinding(new InputBinding(FEED, KeyEvent.VK_F, InputBinding.KEYBOARD));
		addInputBinding(new InputBinding(POKE, KeyEvent.VK_P, InputBinding.KEYBOARD));
		addInputBinding(new InputBinding(PUT_TO_SLEEP, KeyEvent.VK_S, InputBinding.KEYBOARD));
	}
	
	public void buttonPressed(int eventID) 
	{
		switch(eventID)
		{
			case EXIT:
			{
				SGFGame.getInstance().exit();
			}
			case FEED:
			{
				state.getPet().getEnvironment().dispenseFood();
			}
			case POKE:
			{
				//deploy poke
				//(optional) deploy poop
			}
			case TRAIN:
			{
				
			}
			case PUT_TO_SLEEP:
			{
				//Dim lights
			}
		}
	}

	public void buttonReleased(int eventID) 
	{
		
	}

}
