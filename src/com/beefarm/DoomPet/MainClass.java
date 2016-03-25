package com.beefarm.DoomPet;

import com.cyntaks.sgf.core.*;

public class MainClass 
{
	public static void main(String[] args)
	{
		WindowConfiguration[] windowConfigs = new WindowConfiguration[10];

		windowConfigs[0] = new WindowConfiguration(910, 512, 60, false);
//		windowConfigs[0] = new WindowConfiguration(640, 480, 60, false);
		windowConfigs[1] = new WindowConfiguration(640, 480, 75, false);
	    windowConfigs[2] = new WindowConfiguration(640, 480, 85, false);
	    
	    windowConfigs[3] = new WindowConfiguration(800, 600, 60, false);
	    windowConfigs[4] = new WindowConfiguration(800, 600, 75, false);
	    windowConfigs[5] = new WindowConfiguration(800, 600, 85, false);
	    
	    windowConfigs[6] = new WindowConfiguration(1024, 768, 60, false);
	    windowConfigs[7] = new WindowConfiguration(1024, 768, 75, false);
	    windowConfigs[8] = new WindowConfiguration(1024, 768, 85, false);
	    
	    windowConfigs[9] = new WindowConfiguration(640, 480, false);
	    
	    SGFGame game = SGFGame.getInstance();   
	    game.setDebugEnabled(true);
	    
	    game.setFrameRate(63);
	    game.setTimeBased(false);
	    game.setUseSimpleFrameSyncing(false);
	    game.setUseFrameCap(true);
	    game.initDisplay(windowConfigs, "Doom Pet");
		
	    //this stuff should never be done before the window is set up
	    GameStateManager stateManager = GameStateManager.getInstance();
	    PlayingState playingState = new PlayingState("playing_state");
	    stateManager.addGameState(playingState);
	    stateManager.pushGameState("playing_state");
	    
	    game.start();
	}
}