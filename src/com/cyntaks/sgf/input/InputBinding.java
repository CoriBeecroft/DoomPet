package com.cyntaks.sgf.input;

/**
 * binds an in game event to an input event from a mouse or keyboard.
 */

public class InputBinding
{
  public static final int MOUSE = 0;
  public static final int KEYBOARD = 1;
  
  private int eventID;
  private int inputEventCode;
  private int device;
  
  /**
   * constructs a new InputBinding
   * @param eventID represents an in game event which will occur when the specified input event occurs
   * @param inputEventCode the code for the button (or other input type) that this is bound to
   * @param device the input device which will generate the event
   */
  public InputBinding(int eventID, int inputEventCode, int device)
  {
    this.eventID = eventID;
    this.inputEventCode = inputEventCode;
    this.device = device;
  }

  public int getEventID()
  {
    return eventID;
  }

  public void setEventID(int eventID)
  {
    this.eventID = eventID;
  }
  
  public int getDevice()
  {
    return device;
  }

  public int getInputEventCode()
  {
    return inputEventCode;
  }
}