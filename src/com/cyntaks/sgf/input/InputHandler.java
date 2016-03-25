package com.cyntaks.sgf.input;

import com.cyntaks.sgf.core.GameState;
import com.cyntaks.sgf.core.GameStateManager;
import com.cyntaks.sgf.core.SGFGame;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.SwingUtilities;

public abstract class InputHandler implements MouseListener, MouseMotionListener, KeyListener
{
  private ArrayList definedBindings;
  private ArrayList pressedKeys;
  private ArrayList pressedMouseButtons;
  
  ////
  // constructors
  ////
  public InputHandler()
  {
    definedBindings = new ArrayList();
    pressedKeys = new ArrayList();
    pressedMouseButtons = new ArrayList();
  }
  
  ////
  // public interface
  ////
  public final void mousePressed(MouseEvent e)
  {
    GameState state = GameStateManager.getInstance().getTopGameState();
    if(state != null && state.isPaused())
      state.gainedFocus();
      
    for (int i = 0; i < definedBindings.size(); i++) 
    {
      InputBinding binding = (InputBinding)definedBindings.get(i);
      if(binding.getDevice() == com.cyntaks.sgf.input.InputBinding.MOUSE &&
         binding.getInputEventCode() == MouseEvent.BUTTON1 &&
         SwingUtilities.isLeftMouseButton(e) ||
         binding.getInputEventCode() == MouseEvent.BUTTON2 &&
         SwingUtilities.isRightMouseButton(e) ||
         binding.getInputEventCode() == MouseEvent.BUTTON3 &&
         SwingUtilities.isMiddleMouseButton(e))
      {
        if(SGFGame.getInstance().hasFocus())
        {
          buttonPressed(binding.getEventID());
          pressedMouseButtons.add(new Integer(binding.getInputEventCode()));
        }
      }
    }
  }
  public final void mouseReleased(MouseEvent e)
  {
    for (int i = 0; i < definedBindings.size(); i++) 
    {
      InputBinding binding = (InputBinding)definedBindings.get(i);
      if(binding.getDevice() == com.cyntaks.sgf.input.InputBinding.MOUSE &&
         binding.getInputEventCode() == MouseEvent.BUTTON1 &&
         SwingUtilities.isLeftMouseButton(e) ||
         binding.getInputEventCode() == MouseEvent.BUTTON2 &&
         SwingUtilities.isRightMouseButton(e) ||
         binding.getInputEventCode() == MouseEvent.BUTTON3 &&
         SwingUtilities.isMiddleMouseButton(e))
      {
        buttonReleased(binding.getEventID());
        pressedMouseButtons.remove(new Integer(binding.getInputEventCode()));
      }
    }
  }
  
  public final void keyPressed(KeyEvent e)
  {
    for (int i = 0; i < definedBindings.size(); i++) 
    {
      InputBinding binding = (InputBinding)definedBindings.get(i);
      if(binding.getDevice() == com.cyntaks.sgf.input.InputBinding.KEYBOARD &&
         binding.getInputEventCode() == e.getKeyCode())
      {
        if(!isKeyPressed(e.getKeyCode()))
        {
          buttonPressed(binding.getEventID());
          pressedKeys.add(new Integer(e.getKeyCode()));
        }
      }
    }
    
    char theChar = e.getKeyChar();
    if(Character.isDefined(theChar))
      characterTyped(theChar);
  }
  public final void keyReleased(KeyEvent e)
  {
    for (int i = 0; i < definedBindings.size(); i++) 
    {
      InputBinding binding = (InputBinding)definedBindings.get(i);
      if(binding.getDevice() == com.cyntaks.sgf.input.InputBinding.KEYBOARD &&
         binding.getInputEventCode() == e.getKeyCode())
      {
        buttonReleased(binding.getEventID());
        pressedKeys.remove(new Integer(e.getKeyCode()));
      }
    }
    
    if(e.getKeyCode() == KeyEvent.VK_F12)
      SGFGame.getInstance().setUseFrameCap(!SGFGame.getInstance().isUsingFrameCap());
  }
  
  public void characterTyped(char character)
  {
    
  }
  
  public boolean isKeyPressed(int keyCode)
  {
    for (int i = 0; i < pressedKeys.size(); i++) 
    {
      if(((Integer)pressedKeys.get(i)).intValue() == keyCode)
        return true;
    }
    
    return false;
  }
  
  public boolean isMouseButtonPressed(int button)
  {
    for (int i = 0; i < pressedMouseButtons.size(); i++) 
    {
      if(((Integer)pressedMouseButtons.get(i)).intValue() == button)
        return true;
    }
    
    return false;
  }
  
  public void keyTyped(KeyEvent e){}
  public void mouseClicked(MouseEvent e){}
  public void mouseEntered(MouseEvent e){}
  public void mouseExited(MouseEvent e){}
  public void mouseDragged(MouseEvent e){}
  public void mouseMoved(MouseEvent e){}
  
  public void addInputBinding(InputBinding binding)
  {
    definedBindings.add(binding);
  }
  
  public void removeInputBinding(InputBinding binding)
  {
    definedBindings.remove(binding);
  }
  
  public void removeBindingsForEvent(int eventID)
  {
    for (int i = 0; i < definedBindings.size(); i++) 
    {
      InputBinding binding = (InputBinding)definedBindings.get(i);
      if(binding.getEventID() == eventID)
        definedBindings.remove(binding);
    }
  }
  
  public void clearPressedButtons()
  {
    pressedMouseButtons.clear();
    pressedKeys.clear();
  }
  
  ////
  // abstract methods
  ////
  public abstract void buttonPressed(int eventID);
  public abstract void buttonReleased(int eventID);
}