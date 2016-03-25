package com.cyntaks.sgf.event;

import com.cyntaks.sgf.core.UpdateListener;
import java.util.ArrayList;

/**
 * updates all of the games Events each frame and removes them if they are
 * no longer alive.
 * 
 * @author Weston Beecroft
 */

public final class EventManager implements UpdateListener
{
  /**
   * the single available instance of this class
   */
  private static EventManager INSTANCE = new EventManager();
  private ArrayList events;
  
  private EventManager()
  {
    events = new ArrayList();
  }
  
  public static final EventManager getInstance()
  {
    if(INSTANCE == null)
      INSTANCE = new EventManager();
    return INSTANCE;
  }
  
  /**
   * updates each Event and removes them if they are not alive
   * @param delta the amount of time passed during the last frame
   */
  public void updateOccurred(float delta)
  {
    for (int i = 0; i < events.size(); i++) 
    {
      Event event = (Event)events.get(i);
      if(!event.isAlive())
        events.remove(event);
      else
        event.update(delta);
    }
  }
  
  /**
   * adds the given Event to this EventManager
   * @param event the Event to be added
   */
  public void addEvent(Event event)
  {
    events.add(event);
  }
  
  /**
   * removes the given event from this EventManager
   * @param event the Event to be removed
   * @return whether the event was found or not
   */
  public boolean removeEvent(Event event)
  {
    return events.remove(event);
  }
  
  /**
   * removes the Event at the given index from this EventManager
   * @param index index of the Event to be removed
   * @return the Event which was removed
   */
  public Event removeEvent(int index)
  {
    return (Event)events.remove(index);
  }
  
  public void destroy()
  {
    clear();
    INSTANCE = null;
  }
  
  public void clear()
  {
    events.clear();
  }
}