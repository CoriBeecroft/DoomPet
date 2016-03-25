package com.cyntaks.sgf.core;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Stack;
import com.cyntaks.sgf.core.UpdateListener;
import com.cyntaks.sgf.core.GameState;
import com.cyntaks.sgf.input.InputHandler;

public final class GameStateManager implements UpdateListener
{
  private static GameStateManager INSTANCE; //single available instance of this class
  private HashMap<String, GameState> gameStates; //map of all created GameStates
  private Stack<GameState> activeStates; //stack of active states
    
  ////
  // constructors
  ////
  private GameStateManager()
  {
    gameStates = new HashMap<String, GameState>();
    activeStates = new Stack<GameState>();
  }
  
  public static GameStateManager getInstance()
  {
    if(INSTANCE == null)
      INSTANCE = new GameStateManager();
    return INSTANCE;
  }
  
  ////
  // public interface
  ////
  
  /**
   * returns the GameState with the given name
   */
  public GameState getGameState(String name)
  {
    return (GameState)gameStates.get(name);
  }
  
  /**
   * sets the GameState with the name specified as the top (active) game state
   * and initializes it.
   */
  public void pushGameState(String name)
  {
    GameState gameState = (GameState)gameStates.get(name);
    activeStates.push(gameState);
    gameState.activate();
  }
  
  /**
   * updates all the GameStates in the activeStates stack if they are initialized,
   * updates their loadings screens if not.
   * @param delta the amount of time that passed during the last frame.
   */
  public final void updateOccurred(float delta)
  {
    for(int i = 0; i < activeStates.size(); i++)
    {
      GameState state = (GameState)activeStates.get(i);
      if(state.isInitialized())
      {
        state.auxUpdate(delta);
      }
      else
        state.updateLoadingScreen(delta); 
    }
  }
  
  public final void preRender(Graphics2D g2)
  {
    for(int i = 0; i < activeStates.size(); i++)
    {
      GameState state = (GameState)activeStates.get(i);
      if(state.isInitialized())
        state.preRender(g2);
    }
  }
  
  /**
   * renders all the GameStates in the activeStates stack if they are initialized,
   * renders their loading screens if not.
   */
  public final void render(Graphics2D g2)
  {
    for(int i = 0; i < activeStates.size(); i++)
    {
      GameState state = (GameState)activeStates.get(i);
      if(state.isInitialized())
      {
        state.auxRender(g2);
      }
      else
        state.renderLoadingScreen(g2);
    }
  }
  
  /**
   * removes and returns the GameState at the top of the stack
   */
  public GameState popGameState()
  {
    GameState state = (GameState)activeStates.pop();
    state.cleanup();
    state.setInitialized(false);
    state.getProgressBar().reset();
    InputHandler last = SGFGame.getInstance().getLastInputHandler();
    if(last != null)
    {
    	last.clearPressedButtons();
    }
    return state;
  }
  
  /**
   * returns the GameState at the top of the stack without removing it
   */
  public GameState getTopGameState()
  {
    if(!activeStates.isEmpty())
      return (GameState)activeStates.peek();
    else
      return null;
  }
  
  /**
   * removes all GameStates from the active states stack
   */
  public void clearActiveStates()
  {
    while(!activeStates.isEmpty())
    {
      popGameState();
    }
  }
  
  public void destroy()
  {
    clear();
    
    INSTANCE = null;
  }
  
  public void clear()
  {
    clearActiveStates();
    gameStates.clear();
  }
  
  /**
   * removes the GameState with the given name, this method does not remove the
   * GameState from the stack of active states.
   * @param name
   */
  public final GameState removeGameState(String name)
  {
    return (GameState)gameStates.remove(name);
  }
  
  /**
   * Adds a GameState with it's name as the key, each game state must be added
   * to the GameStateManager through this method before it can be pushed onto
   * the stack.
   */
  public final void addGameState(GameState state)
  {
    gameStates.put(state.getName(), state);
  }
}